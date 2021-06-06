
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.RectF;

import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class YAxisRendererHorizontalBarChart extends YAxisRenderer {
    public YAxisRendererHorizontalBarChart(
            @NotNull ViewPortHandler viewPortHandler,
            @NotNull YAxis yAxis,
            @NotNull Transformer trans
    ) {
        super(viewPortHandler, yAxis, trans);

        mLimitLinePaint.setTextAlign(Align.LEFT);
    }

    /**
     * Computes the axis values.
     *
     * @param yMin - the minimum y-value in the data object for this axis
     * @param yMax - the maximum y-value in the data object for this axis
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    public void computeAxis(float yMin, float yMax, boolean inverted) {
        // calculate the starting and entry point of the y-labels (depending on
        // zoom / content rect bounds)

        if (mViewPortHandler.contentHeight() > 10 && !mViewPortHandler.isFullyZoomedOutX()) {
            MPPointF p1 = mTrans.getValuesByTouchPoint(mViewPortHandler.contentLeft(),
                    mViewPortHandler.contentTop());
            MPPointF p2 = mTrans.getValuesByTouchPoint(mViewPortHandler.contentRight(),
                    mViewPortHandler.contentTop());

            if (!inverted) {
                yMin = p1.x;
                yMax = p2.x;
            } else {
                yMin = p2.x;
                yMax = p1.x;
            }

            MPPointF.recycleInstance(p1);
            MPPointF.recycleInstance(p2);
        }

        computeAxisValues(yMin, yMax);
    }

    /**
     * draws the y-axis labels to the screen
     */
    @Override
    public void renderAxisLabels(@NotNull Canvas c) {
        if (!mYAxis.isEnabled() || !mYAxis.isDrawLabelsEnabled())
            return;

        float[] positions = getTransformedPositions();

        mAxisLabelPaint.setTypeface(mYAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mYAxis.getTextSize());
        mAxisLabelPaint.setColor(mYAxis.getTextColor());
        mAxisLabelPaint.setTextAlign(Align.CENTER);

        float baseYOffset = Utils.convertDpToPixel(2.5f);
        float textHeight = Utils.calcTextHeight(mAxisLabelPaint, "Q");

        AxisDependency dependency = mYAxis.getAxisDependency();

        float yPos;

        if (dependency == AxisDependency.LEFT) {
            yPos = mViewPortHandler.contentTop() - baseYOffset;
        } else {
            yPos = mViewPortHandler.contentBottom() + textHeight + baseYOffset;
        }

        drawYLabels(c, yPos, positions, mYAxis.getYOffset());
    }

    @Override
    public void renderAxisLine(@NotNull Canvas c) {
        if (!mYAxis.isEnabled() || !mYAxis.isDrawAxisLineEnabled())
            return;

        mAxisLinePaint.setColor(mYAxis.getAxisLineColor());
        mAxisLinePaint.setStrokeWidth(mYAxis.getAxisLineWidth());

        if (mYAxis.getAxisDependency() == AxisDependency.LEFT) {
            float contentTop = mViewPortHandler.contentTop();

            c.drawLine(
                    mViewPortHandler.contentLeft(),
                    contentTop,
                    mViewPortHandler.contentRight(),
                    contentTop,
                    mAxisLinePaint
            );
        } else {
            float contentBottom = mViewPortHandler.contentBottom();

            c.drawLine(
                    mViewPortHandler.contentLeft(),
                    contentBottom,
                    mViewPortHandler.contentRight(),
                    contentBottom,
                    mAxisLinePaint
            );
        }
    }

    /**
     * draws the y-labels on the specified x-position
     */
    @Override
    protected void drawYLabels(
            @NotNull Canvas c,
            float fixedPosition,
            @NotNull float[] positions,
            float offset
    ) {
        mAxisLabelPaint.setTypeface(mYAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mYAxis.getTextSize());
        mAxisLabelPaint.setColor(mYAxis.getTextColor());

        int from = mYAxis.isDrawBottomYLabelEntryEnabled() ? 0 : 1;
        int to = mYAxis.isDrawTopYLabelEntryEnabled()
                ? mYAxis.mEntryCount
                : (mYAxis.mEntryCount - 1);

        float xOffset = mYAxis.getLabelXOffset();

        for (int i = from; i < to; i++) {
            String text = mYAxis.getFormattedLabel(i);

            c.drawText(text,
                    positions[i * 2],
                    fixedPosition - offset + xOffset,
                    mAxisLabelPaint);
        }
    }

    @Override
    @NotNull
    protected float[] getTransformedPositions() {
        if(mGetTransformedPositionsBuffer.length != mYAxis.mEntryCount * 2) {
            mGetTransformedPositionsBuffer = new float[mYAxis.mEntryCount * 2];
        }

        float[] positions = mGetTransformedPositionsBuffer;

        for (int i = 0; i < positions.length; i += 2) {
            // only fill x values, y values are not needed for x-labels
            positions[i] = mYAxis.mEntries[i / 2];
        }

        //noinspection ConstantConditions
        mTrans.pointValuesToPixel(positions);

        return positions;
    }

    @Override
    @NotNull
    public RectF getGridClippingRect() {
        mGridClippingRect.set(mViewPortHandler.getContentRect());
        mGridClippingRect.inset(-mAxis.getGridLineWidth(), 0f);
        return mGridClippingRect;
    }

    @Override
    @NotNull
    protected Path linePath(@NotNull Path p, int i, @NotNull float[] positions) {
        p.moveTo(positions[i], mViewPortHandler.contentTop());
        p.lineTo(positions[i], mViewPortHandler.contentBottom());

        return p;
    }

    protected Path mDrawZeroLinePathBuffer = new Path();

    @Override
    protected void drawZeroLine(Canvas c) {
        int clipRestoreCount = c.save();
        mZeroLineClippingRect.set(mViewPortHandler.getContentRect());
        mZeroLineClippingRect.inset(-mYAxis.getZeroLineWidth(), 0f);
        c.clipRect(mLimitLineClippingRect);

        // draw zero line
        //noinspection ConstantConditions
        MPPointF pos = mTrans.getPixelForValues(0f, 0f);

        mZeroLinePaint.setColor(mYAxis.getZeroLineColor());
        mZeroLinePaint.setStrokeWidth(mYAxis.getZeroLineWidth());

        Path zeroLinePath = mDrawZeroLinePathBuffer;
        zeroLinePath.reset();

        float x = pos.x - 1f;

        zeroLinePath.moveTo(x, mViewPortHandler.contentTop());
        zeroLinePath.lineTo(x, mViewPortHandler.contentBottom());

        // draw a path because lines don't support dashing on lower android versions
        c.drawPath(zeroLinePath, mZeroLinePaint);

        c.restoreToCount(clipRestoreCount);
    }

    protected Path mRenderLimitLinesPathBuffer = new Path();
    protected float[] mRenderLimitLinesBuffer = new float[4];

    /**
     * Draws the LimitLines associated with this axis to the screen.
     * This is the standard XAxis renderer using the YAxis limit lines.
     */
    @Override
    public void renderLimitLines(@NotNull Canvas c) {
        List<LimitLine> limitLines = mYAxis.getLimitLines();

        if (limitLines.isEmpty())
            return;

        float[] pts = mRenderLimitLinesBuffer;
        pts[0] = 0;
        pts[1] = 0;
        pts[2] = 0;
        pts[3] = 0;
        Path limitLinePath = mRenderLimitLinesPathBuffer;
        limitLinePath.reset();

        for (int i = 0; i < limitLines.size(); i++) {
            LimitLine l = limitLines.get(i);

            if (!l.isEnabled())
                continue;

            int clipRestoreCount = c.save();
            mLimitLineClippingRect.set(mViewPortHandler.getContentRect());
            mLimitLineClippingRect.inset(-l.getLineWidth(), 0.f);
            c.clipRect(mLimitLineClippingRect);

            pts[0] = l.getLimit();
            pts[2] = l.getLimit();

            //noinspection ConstantConditions
            mTrans.pointValuesToPixel(pts);

            pts[1] = mViewPortHandler.contentTop();
            pts[3] = mViewPortHandler.contentBottom();

            limitLinePath.moveTo(pts[0], pts[1]);
            limitLinePath.lineTo(pts[2], pts[3]);

            mLimitLinePaint.setStyle(Paint.Style.STROKE);
            mLimitLinePaint.setColor(l.getLineColor());
            mLimitLinePaint.setPathEffect(l.getDashPathEffect());
            mLimitLinePaint.setStrokeWidth(l.getLineWidth());

            c.drawPath(limitLinePath, mLimitLinePaint);
            limitLinePath.reset();

            String label = l.getLabel();

            // if drawing the limit-value label is enabled
            if (label.length() > 0) {
                mLimitLinePaint.setStyle(l.getTextStyle());
                mLimitLinePaint.setPathEffect(null);
                mLimitLinePaint.setColor(l.getTextColor());
                mLimitLinePaint.setTypeface(l.getTypeface());
                mLimitLinePaint.setStrokeWidth(0.5f);
                mLimitLinePaint.setTextSize(l.getTextSize());

                float xOffset = l.getLineWidth() + l.getXOffset();
                float yOffset = Utils.convertDpToPixel(2f) + l.getYOffset();

                int position = l.getLabelPosition();

                switch (position) {
                    case LimitLine.LABEL_POSITION_RIGHT_TOP: {
                        float labelLineHeight = Utils.calcTextHeight(mLimitLinePaint, label);
                        mLimitLinePaint.setTextAlign(Align.LEFT);
                        c.drawText(
                                label,
                                pts[0] + xOffset,
                                mViewPortHandler.contentTop() + yOffset + labelLineHeight,
                                mLimitLinePaint
                        );
                        break;
                    }
                    case LimitLine.LABEL_POSITION_RIGHT_BOTTOM: {
                        mLimitLinePaint.setTextAlign(Align.LEFT);
                        c.drawText(
                                label,
                                pts[0] + xOffset,
                                mViewPortHandler.contentBottom() - yOffset,
                                mLimitLinePaint
                        );

                        break;
                    }
                    case LimitLine.LABEL_POSITION_LEFT_TOP: {
                        mLimitLinePaint.setTextAlign(Align.RIGHT);
                        float labelLineHeight = Utils.calcTextHeight(mLimitLinePaint, label);
                        c.drawText(
                                label,
                                pts[0] - xOffset,
                                mViewPortHandler.contentTop() + yOffset + labelLineHeight,
                                mLimitLinePaint
                        );
                        break;
                    }
                    case LimitLine.LABEL_POSITION_LEFT_BOTTOM: {
                        mLimitLinePaint.setTextAlign(Align.RIGHT);
                        c.drawText(
                                label,
                                pts[0] - xOffset,
                                mViewPortHandler.contentBottom() - yOffset,
                                mLimitLinePaint
                        );
                    }
                }
            }

            c.restoreToCount(clipRestoreCount);
        }
    }
}
