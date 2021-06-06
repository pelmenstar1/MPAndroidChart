
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Baseclass of all axis renderers.
 *
 * @author Philipp Jahoda
 */
public abstract class AxisRenderer extends Renderer {
    /** base axis this axis renderer works with */
    protected AxisBase mAxis;

    /** transformer to transform values to screen pixels and return */
    @Nullable
    protected Transformer mTrans;

    /**
     * paint object for the grid lines
     */
    protected Paint mGridPaint;

    /**
     * paint for the x-label values
     */
    protected Paint mAxisLabelPaint;

    /**
     * paint for the line surrounding the chart
     */
    protected Paint mAxisLinePaint;

    /**
     * paint used for the limit lines
     */
    protected Paint mLimitLinePaint;

    public AxisRenderer(
            @NotNull ViewPortHandler viewPortHandler,
            @Nullable Transformer trans,
            @NotNull AxisBase axis
    ) {
        super(viewPortHandler);

        this.mTrans = trans;
        this.mAxis = axis;

        mAxisLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mGridPaint = new Paint();
        mGridPaint.setColor(Color.GRAY);
        mGridPaint.setStrokeWidth(1f);
        mGridPaint.setStyle(Style.STROKE);
        mGridPaint.setAlpha(90);

        mAxisLinePaint = new Paint();
        mAxisLinePaint.setColor(Color.BLACK);
        mAxisLinePaint.setStrokeWidth(1f);
        mAxisLinePaint.setStyle(Style.STROKE);

        mLimitLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLimitLinePaint.setStyle(Style.STROKE);
    }

    /**
     * Returns the Paint object used for drawing the axis (labels).
     */
    public Paint getPaintAxisLabels() {
        return mAxisLabelPaint;
    }

    /**
     * Returns the Paint object that is used for drawing the grid-lines of the
     * axis.
     */
    public Paint getPaintGrid() {
        return mGridPaint;
    }

    /**
     * Returns the Paint object that is used for drawing the axis-line that goes
     * alongside the axis.
     */
    public Paint getPaintAxisLine() {
        return mAxisLinePaint;
    }

    /**
     * Returns the Transformer object used for transforming the axis values.
     */
    public Transformer getTransformer() {
        return mTrans;
    }

    /**
     * Computes the axis values.
     *
     * @param min - the minimum value in the data object for this axis
     * @param max - the maximum value in the data object for this axis
     */
    public void computeAxis(float min, float max, boolean inverted) {
        // calculate the starting and entry point of the y-labels (depending on
        // zoom / contentrect bounds)
        if(mTrans != null) {
            if (mViewPortHandler.contentWidth() > 10 && !mViewPortHandler.isFullyZoomedOutY()) {
                MPPointF p1 = mTrans.getValuesByTouchPoint(mViewPortHandler.contentLeft(), mViewPortHandler.contentTop());
                MPPointF p2 = mTrans.getValuesByTouchPoint(mViewPortHandler.contentLeft(), mViewPortHandler.contentBottom());

                if (!inverted) {
                    min = p2.y;
                    max = p1.y;
                } else {
                    min = p1.y;
                    max = p2.y;
                }

                MPPointF.recycleInstance(p1);
                MPPointF.recycleInstance(p2);
            }

            computeAxisValues(min, max);
        }
    }

    /**
     * Sets up the axis values. Computes the desired number of labels between the two given extremes.
     */
    protected void computeAxisValues(float min, float max) {
        int labelCount = mAxis.getLabelCount();

        float range = Math.abs(max - min);

        if (labelCount == 0 || range <= 0 || Float.isInfinite(range)) {
            mAxis.mEntries = new float[0];
            mAxis.mCenteredEntries = new float[0];
            mAxis.mEntryCount = 0;
            return;
        }

        // Find out how much spacing (in y value space) between axis values
        float rawInterval = range / labelCount;
        float interval = Utils.roundToNextSignificant(rawInterval);

        // If granularity is enabled, then do not allow the interval to go below specified granularity.
        // This is used to avoid repeated values when rounding values for display.
        if (mAxis.isGranularityEnabled())
            interval = Math.max(interval, mAxis.getGranularity());

        // Normalize interval
        float intervalMagnitude = Utils.roundToNextSignificant((float)Math.pow(10, (int) Math.log10(interval)));
        int intervalSigDigit = (int) (interval / intervalMagnitude);
        if (intervalSigDigit > 5) {
            // Use one order of magnitude higher, to avoid intervals like 0.9 or 90
            // if it's 0.0 after floor(), we use the old value
            float intervalMagRaw = (float)Math.floor(10f * intervalMagnitude);

            interval = intervalMagRaw == 0f ? interval : intervalMagRaw;
        }

        int n = mAxis.isCenterAxisLabelsEnabled() ? 1 : 0;

        // force label count
        if (mAxis.isForceLabelsEnabled()) {
            interval = range / (float) (labelCount - 1);
            mAxis.mEntryCount = labelCount;

            if (mAxis.mEntries.length < labelCount) {
                // Ensure stops contains at least numStops elements.
                mAxis.mEntries = new float[labelCount];
            }

            float v = min;

            for (int i = 0; i < labelCount; i++) {
                mAxis.mEntries[i] = v;
                v += interval;
            }

            n = labelCount;

            // no forced count
        } else {
            float first = interval == 0f ? 0f : (float)Math.ceil(min / interval) * interval;
            if(mAxis.isCenterAxisLabelsEnabled()) {
                first -= interval;
            }

            float last = interval == 0f ? 0f : Utils.nextUp((float)Math.floor(max / interval) * interval);

            float f;
            int i;

            if (interval != 0.0 && last != first) {
                for (f = first; f <= last; f += interval) {
                    ++n;
                }
            } else if (last == first && n == 0) {
                n = 1;
            }

            mAxis.mEntryCount = n;

            if (mAxis.mEntries.length < n) {
                // Ensure stops contains at least numStops elements.
                mAxis.mEntries = new float[n];
            }

            for (f = first, i = 0; i < n; f += interval, ++i) {
                if (f == 0f) // Fix for negative zero case (Where value == -0.0, and 0.0 == -0.0)
                    f = 0f;

                mAxis.mEntries[i] = f;
            }
        }

        // set decimals
        if (interval < 1) {
            mAxis.mDecimals = (int) Math.ceil(-Math.log10(interval));
        } else {
            mAxis.mDecimals = 0;
        }

        if (mAxis.isCenterAxisLabelsEnabled()) {
            if (mAxis.mCenteredEntries.length < n) {
                mAxis.mCenteredEntries = new float[n];
            }

            float offset = interval * 0.5f;

            for (int i = 0; i < n; i++) {
                mAxis.mCenteredEntries[i] = mAxis.mEntries[i] + offset;
            }
        }
    }

    /**
     * Draws the axis labels to the screen.
     */
    public abstract void renderAxisLabels(@NotNull Canvas c);

    /**
     * Draws the grid lines belonging to the axis.
     */
    public abstract void renderGridLines(@NotNull Canvas c);

    /**
     * Draws the line that goes alongside the axis.
     */
    public abstract void renderAxisLine(@NotNull Canvas c);

    /**
     * Draws the LimitLines associated with this axis to the screen.
     */
    public abstract void renderLimitLines(@NotNull Canvas c);
}
