package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.RectF;

import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class YAxisRenderer extends AxisRenderer {
    protected YAxis mYAxis;

    protected Paint mZeroLinePaint;

    public YAxisRenderer(
            @NotNull ViewPortHandler viewPortHandler,
            @NotNull YAxis yAxis,
            @Nullable Transformer trans
    ) {
        super(viewPortHandler, trans, yAxis);

        this.mYAxis = yAxis;

        mAxisLabelPaint.setColor(Color.BLACK);
        mAxisLabelPaint.setTextSize(Utils.convertDpToPixel(10f));

        mZeroLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mZeroLinePaint.setColor(Color.GRAY);
        mZeroLinePaint.setStrokeWidth(1f);
        mZeroLinePaint.setStyle(Paint.Style.STROKE);
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

        float xOffset = mYAxis.getXOffset();
        float yOffset = Utils.calcTextHeight(mAxisLabelPaint, "A") / 2.5f + mYAxis.getYOffset();

        int dependency = mYAxis.getAxisDependency();
        int labelPosition = mYAxis.getLabelPosition();

        float xPos;

        if (dependency == YAxis.DEPENDENCY_LEFT) {
            if (labelPosition == YAxis.LABEL_POSITION_OUTSIDE_CHART) {
                mAxisLabelPaint.setTextAlign(Align.RIGHT);
                xPos = mViewPortHandler.offsetLeft() - xOffset;
            } else {
                mAxisLabelPaint.setTextAlign(Align.LEFT);
                xPos = mViewPortHandler.offsetLeft() + xOffset;
            }
        } else {
            if (labelPosition == YAxis.LABEL_POSITION_OUTSIDE_CHART) {
                mAxisLabelPaint.setTextAlign(Align.LEFT);
                xPos = mViewPortHandler.contentRight() + xOffset;
            } else {
                mAxisLabelPaint.setTextAlign(Align.RIGHT);
                xPos = mViewPortHandler.contentRight() - xOffset;
            }
        }

        drawYLabels(c, xPos, positions, yOffset);
    }

    @Override
    public void renderAxisLine(@NotNull Canvas c) {
        if (!mYAxis.isEnabled() || !mYAxis.isDrawAxisLineEnabled())
            return;

        mAxisLinePaint.setColor(mYAxis.getAxisLineColor());
        mAxisLinePaint.setStrokeWidth(mYAxis.getAxisLineWidth());

        if (mYAxis.getAxisDependency() == YAxis.DEPENDENCY_LEFT) {
            c.drawLine(mViewPortHandler.contentLeft(), mViewPortHandler.contentTop(), mViewPortHandler.contentLeft(),
                    mViewPortHandler.contentBottom(), mAxisLinePaint);
        } else {
            c.drawLine(mViewPortHandler.contentRight(), mViewPortHandler.contentTop(), mViewPortHandler.contentRight(),
                    mViewPortHandler.contentBottom(), mAxisLinePaint);
        }
    }

    /**
     * draws the y-labels on the specified x-position
     */
    protected void drawYLabels(
            @NotNull Canvas c,
            float fixedPosition,
            @NotNull float[] positions,
            float offset
    ) {
        int from = mYAxis.isDrawBottomYLabelEntryEnabled() ? 0 : 1;
        int to = mYAxis.isDrawTopYLabelEntryEnabled()
                ? mYAxis.mEntryCount
                : (mYAxis.mEntryCount - 1);

        float xOffset = mYAxis.getLabelXOffset();

        // draw
        for (int i = from; i < to; i++) {
            String text = mYAxis.getFormattedLabel(i);

            c.drawText(text,
                    fixedPosition + xOffset,
                    positions[i * 2 + 1] + offset,
                    mAxisLabelPaint);
        }
    }

    protected Path mRenderGridLinesPath = new Path();

    @Override
    public void renderGridLines(@NotNull Canvas c) {
        if (!mYAxis.isEnabled())
            return;

        if (mYAxis.isDrawGridLinesEnabled()) {
            int clipRestoreCount = c.save();
            c.clipRect(getGridClippingRect());

            float[] positions = getTransformedPositions();

            mGridPaint.setColor(mYAxis.getGridColor());
            mGridPaint.setStrokeWidth(mYAxis.getGridLineWidth());
            mGridPaint.setPathEffect(mYAxis.getGridDashPathEffect());

            Path gridLinePath = mRenderGridLinesPath;
            gridLinePath.reset();

            // draw the grid
            for (int i = 0; i < positions.length; i += 2) {
                // draw a path because lines don't support dashing on lower android versions
                c.drawPath(linePath(gridLinePath, i, positions), mGridPaint);
                gridLinePath.reset();
            }

            c.restoreToCount(clipRestoreCount);
        }

        if (mYAxis.isDrawZeroLineEnabled()) {
            drawZeroLine(c);
        }
    }

    protected RectF mGridClippingRect = new RectF();

    @NotNull
    public RectF getGridClippingRect() {
        mGridClippingRect.set(mViewPortHandler.getContentRect());
        mGridClippingRect.inset(0.f, -mAxis.getGridLineWidth());

        return mGridClippingRect;
    }

    /**
     * Calculates the path for a grid line.
     */
    @NotNull
    protected Path linePath(@NotNull Path p, int i, @NotNull float[] positions) {
        p.moveTo(mViewPortHandler.offsetLeft(), positions[i + 1]);
        p.lineTo(mViewPortHandler.contentRight(), positions[i + 1]);

        return p;
    }

    protected float[] mGetTransformedPositionsBuffer = new float[2];

    /**
     * Transforms the values contained in the axis entries to screen pixels and returns them in form of a float array
     * of x- and y-coordinates.
     */
    protected float[] getTransformedPositions() {
        if(mTrans == null) {
            throw new IllegalStateException("mTrans == null");
        }

        if(mGetTransformedPositionsBuffer.length != mYAxis.mEntryCount * 2){
            mGetTransformedPositionsBuffer = new float[mYAxis.mEntryCount * 2];
        }

        float[] positions = mGetTransformedPositionsBuffer;

        for (int i = 0; i < positions.length; i += 2) {
            // only fill y values, x values are not needed for y-labels
            positions[i + 1] = mYAxis.mEntries[i / 2];
        }

        mTrans.pointValuesToPixel(positions);

        return positions;
    }

    protected Path mDrawZeroLinePath = new Path();
    protected RectF mZeroLineClippingRect = new RectF();

    /**
     * Draws the zero line.
     */
    protected void drawZeroLine(@NotNull Canvas c) {
        if(mTrans != null) {
            int clipRestoreCount = c.save();
            mZeroLineClippingRect.set(mViewPortHandler.getContentRect());
            mZeroLineClippingRect.inset(0.f, -mYAxis.getZeroLineWidth());
            c.clipRect(mZeroLineClippingRect);

            // draw zero line
            MPPointF pos = mTrans.getPixelForValues(0f, 0f);

            mZeroLinePaint.setColor(mYAxis.getZeroLineColor());
            mZeroLinePaint.setStrokeWidth(mYAxis.getZeroLineWidth());

            Path zeroLinePath = mDrawZeroLinePath;
            zeroLinePath.reset();

            zeroLinePath.moveTo(mViewPortHandler.contentLeft(), pos.y);
            zeroLinePath.lineTo(mViewPortHandler.contentRight(), pos.y);

            // draw a path because lines don't support dashing on lower android versions
            c.drawPath(zeroLinePath, mZeroLinePaint);

            c.restoreToCount(clipRestoreCount);
        }
    }

    protected Path mRenderLimitLines = new Path();
    protected float[] mRenderLimitLinesBuffer = new float[2];
    protected RectF mLimitLineClippingRect = new RectF();

    /**
     * Draws the LimitLines associated with this axis to the screen.
     */
    @Override
    public void renderLimitLines(@NotNull Canvas c) {
        List<LimitLine> limitLines = mYAxis.getLimitLines();

        if (limitLines.isEmpty())
            return;

        float[] pts = mRenderLimitLinesBuffer;
        Path limitLinePath = mRenderLimitLines;
        limitLinePath.reset();

        for (int i = 0; i < limitLines.size(); i++) {
            LimitLine l = limitLines.get(i);

            if (!l.isEnabled())
                continue;

            int clipRestoreCount = c.save();
            mLimitLineClippingRect.set(mViewPortHandler.getContentRect());
            mLimitLineClippingRect.inset(0.f, -l.getLineWidth());
            c.clipRect(mLimitLineClippingRect);

            mLimitLinePaint.setStyle(Paint.Style.STROKE);
            mLimitLinePaint.setColor(l.getLineColor());
            mLimitLinePaint.setStrokeWidth(l.getLineWidth());
            mLimitLinePaint.setPathEffect(l.getDashPathEffect());

            pts[1] = l.getLimit();

            mTrans.pointValuesToPixel(pts);

            float y = pts[1];

            limitLinePath.moveTo(mViewPortHandler.contentLeft(), y);
            limitLinePath.lineTo(mViewPortHandler.contentRight(), y);

            c.drawPath(limitLinePath, mLimitLinePaint);
            limitLinePath.reset();
            // c.drawLines(pts, mLimitLinePaint);

            String label = l.getLabel();

            // if drawing the limit-value label is enabled
            if (label.length() > 0) {
                mLimitLinePaint.setStyle(l.getTextStyle());
                mLimitLinePaint.setPathEffect(null);
                mLimitLinePaint.setColor(l.getTextColor());
                mLimitLinePaint.setTypeface(l.getTypeface());
                mLimitLinePaint.setStrokeWidth(0.5f);
                mLimitLinePaint.setTextSize(l.getTextSize());

                float labelLineHeight = Utils.calcTextHeight(mLimitLinePaint, label);
                float xOffset = Utils.convertDpToPixel(4f) + l.getXOffset();
                float yOffset = l.getLineWidth() + labelLineHeight + l.getYOffset();

                int position = l.getLabelPosition();

                switch (position) {
                    case LimitLine.LABEL_POSITION_RIGHT_TOP: {
                        mLimitLinePaint.setTextAlign(Align.RIGHT);
                        c.drawText(label,
                                mViewPortHandler.contentRight() - xOffset,
                                y - yOffset + labelLineHeight, mLimitLinePaint);

                        break;
                    }
                    case LimitLine.LABEL_POSITION_RIGHT_BOTTOM: {
                        mLimitLinePaint.setTextAlign(Align.RIGHT);
                        c.drawText(label,
                                mViewPortHandler.contentRight() - xOffset,
                                y + yOffset, mLimitLinePaint);

                        break;
                    }
                    case LimitLine.LABEL_POSITION_LEFT_TOP: {
                        mLimitLinePaint.setTextAlign(Align.LEFT);
                        c.drawText(label,
                                mViewPortHandler.contentLeft() + xOffset,
                                y - yOffset + labelLineHeight, mLimitLinePaint);
                        break;
                    }
                    case LimitLine.LABEL_POSITION_LEFT_BOTTOM: {
                        mLimitLinePaint.setTextAlign(Align.LEFT);
                        c.drawText(label,
                                mViewPortHandler.offsetLeft() + xOffset,
                                y + yOffset, mLimitLinePaint);
                    }
                }
            }

            c.restoreToCount(clipRestoreCount);
        }
    }
}
