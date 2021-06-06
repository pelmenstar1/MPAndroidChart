
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.RectF;

import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.utils.FSize;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class XAxisRenderer extends AxisRenderer {
    protected XAxis mXAxis;

    public XAxisRenderer(
            @NotNull ViewPortHandler viewPortHandler,
            @NotNull XAxis xAxis,
            @Nullable Transformer trans
    ) {
        super(viewPortHandler, trans, xAxis);

        this.mXAxis = xAxis;

        mAxisLabelPaint.setColor(Color.BLACK);
        mAxisLabelPaint.setTextAlign(Align.CENTER);
        mAxisLabelPaint.setTextSize(Utils.convertDpToPixel(10f));
    }

    protected void setupGridPaint() {
        mGridPaint.setColor(mXAxis.getGridColor());
        mGridPaint.setStrokeWidth(mXAxis.getGridLineWidth());
        mGridPaint.setPathEffect(mXAxis.getGridDashPathEffect());
    }

    @Override
    public void computeAxis(float min, float max, boolean inverted) {
        // calculate the starting and entry point of the y-labels (depending on
        // zoom / contentrect bounds)
        if(mTrans != null) {
            if (mViewPortHandler.contentWidth() > 10 && !mViewPortHandler.isFullyZoomedOutX()) {
                MPPointF p1 = mTrans.getValuesByTouchPoint(mViewPortHandler.contentLeft(), mViewPortHandler.contentTop());
                MPPointF p2 = mTrans.getValuesByTouchPoint(mViewPortHandler.contentRight(), mViewPortHandler.contentTop());

                if (inverted) {
                    min = p2.x;
                    max = p1.x;
                } else {

                    min = p1.x;
                    max = p2.x;
                }

                MPPointF.recycleInstance(p1);
                MPPointF.recycleInstance(p2);
            }

            computeAxisValues(min, max);
        }
    }

    @Override
    protected void computeAxisValues(float min, float max) {
        super.computeAxisValues(min, max);

        computeSize();
    }

    protected void computeSize() {

        String longest = mXAxis.getLongestLabel();

        mAxisLabelPaint.setTypeface(mXAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mXAxis.getTextSize());

        FSize labelSize = Utils.calcTextSize(mAxisLabelPaint, longest);

        float labelWidth = labelSize.width;
        float labelHeight = Utils.calcTextHeight(mAxisLabelPaint, "Q");

        FSize labelRotatedSize = Utils.getSizeOfRotatedRectangleByDegrees(
                labelWidth,
                labelHeight,
                mXAxis.getLabelRotationAngle()
        );

        mXAxis.mLabelWidth = Math.round(labelWidth);
        mXAxis.mLabelHeight = Math.round(labelHeight);
        mXAxis.mLabelRotatedWidth = Math.round(labelRotatedSize.width);
        mXAxis.mLabelRotatedHeight = Math.round(labelRotatedSize.height);

        FSize.recycleInstance(labelRotatedSize);
        FSize.recycleInstance(labelSize);
    }

    @Override
    public void renderAxisLabels(@NotNull Canvas c) {
        if (!mXAxis.isEnabled() || !mXAxis.isDrawLabelsEnabled())
            return;

        float yOffset = mXAxis.getYOffset();

        mAxisLabelPaint.setTypeface(mXAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mXAxis.getTextSize());
        mAxisLabelPaint.setColor(mXAxis.getTextColor());

        MPPointF pointF = MPPointF.getInstance();
        int pos = mXAxis.getPosition();

        switch (pos) {
            case XAxis.POSITION_TOP: {
                pointF.x = 0.5f;
                pointF.y = 1.0f;
                drawLabels(c, mViewPortHandler.contentTop() - yOffset, pointF);

                break;
            }
            case XAxis.POSITION_TOP_INSIDE: {
                pointF.x = 0.5f;
                pointF.y = 1.0f;
                drawLabels(c, mViewPortHandler.contentTop() + yOffset + mXAxis.mLabelRotatedHeight, pointF);

                break;
            }
            case XAxis.POSITION_BOTTOM: {
                pointF.x = 0.5f;
                pointF.y = 0.0f;
                drawLabels(c, mViewPortHandler.contentBottom() + yOffset, pointF);

                break;
            }
            case XAxis.POSITION_BOTTOM_INSIDE: {
                pointF.x = 0.5f;
                pointF.y = 0.0f;
                drawLabels(c, mViewPortHandler.contentBottom() - yOffset - mXAxis.mLabelRotatedHeight, pointF);

                break;
            }
            case XAxis.POSITION_BOTH_SIDED: {
                pointF.x = 0.5f;
                pointF.y = 1.0f;
                drawLabels(c, mViewPortHandler.contentTop() - yOffset, pointF);
                pointF.x = 0.5f;
                pointF.y = 0.0f;
                drawLabels(c, mViewPortHandler.contentBottom() + yOffset, pointF);
            }
        }

        MPPointF.recycleInstance(pointF);
    }

    @Override
    public void renderAxisLine(@NotNull Canvas c) {
        if (!mXAxis.isDrawAxisLineEnabled() || !mXAxis.isEnabled())
            return;

        mAxisLinePaint.setColor(mXAxis.getAxisLineColor());
        mAxisLinePaint.setStrokeWidth(mXAxis.getAxisLineWidth());
        mAxisLinePaint.setPathEffect(mXAxis.getAxisLineDashPathEffect());

        int pos = mXAxis.getPosition();

        if (pos == XAxis.POSITION_TOP
                || pos == XAxis.POSITION_TOP_INSIDE
                || pos == XAxis.POSITION_BOTH_SIDED) {
            c.drawLine(mViewPortHandler.contentLeft(),
                    mViewPortHandler.contentTop(), mViewPortHandler.contentRight(),
                    mViewPortHandler.contentTop(), mAxisLinePaint);
        }

        if (pos == XAxis.POSITION_BOTTOM
                || pos == XAxis.POSITION_BOTTOM_INSIDE
                || pos == XAxis.POSITION_BOTH_SIDED) {
            c.drawLine(mViewPortHandler.contentLeft(),
                    mViewPortHandler.contentBottom(), mViewPortHandler.contentRight(),
                    mViewPortHandler.contentBottom(), mAxisLinePaint);
        }
    }

    /**
     * draws the x-labels on the specified y-position
     */
    protected void drawLabels(
            @NotNull Canvas c,
            float pos,
            @NotNull MPPointF anchor
    ) {
        if(mTrans != null) {
            float labelRotationAngleDegrees = mXAxis.getLabelRotationAngle();
            boolean centeringEnabled = mXAxis.isCenterAxisLabelsEnabled();

            float[] positions = new float[mXAxis.mEntryCount * 2];

            for (int i = 0; i < positions.length; i += 2) {
                // only fill x values
                if (centeringEnabled) {
                    positions[i] = mXAxis.mCenteredEntries[i / 2];
                } else {
                    positions[i] = mXAxis.mEntries[i / 2];
                }
            }

            mTrans.pointValuesToPixel(positions);

            for (int i = 0; i < positions.length; i += 2) {
                float x = positions[i];

                if (mViewPortHandler.isInBoundsX(x)) {

                    String label = mXAxis.getValueFormatter().getFormattedValue(mXAxis.mEntries[i / 2], mXAxis);

                    if (mXAxis.isAvoidFirstLastClippingEnabled()) {

                        // avoid clipping of the last
                        if (i / 2 == mXAxis.mEntryCount - 1 && mXAxis.mEntryCount > 1) {
                            float width = Utils.calcTextWidth(mAxisLabelPaint, label);

                            if (width > mViewPortHandler.offsetRight() * 2
                                    && x + width > mViewPortHandler.getChartWidth())
                                x -= width * 0.5f;

                            // avoid clipping of the first
                        } else if (i == 0) {
                            float width = Utils.calcTextWidth(mAxisLabelPaint, label);

                            x += width * 0.5f;
                        }
                    }

                    drawLabel(c, label, x, pos, anchor, labelRotationAngleDegrees);
                }
            }
        }
    }

    protected void drawLabel(
            @NotNull Canvas c,
            @NotNull String formattedLabel,
            float x, float y,
            @NotNull MPPointF anchor,
            float angleDegrees
    ) {
        Utils.drawXAxisValue(c, formattedLabel, x, y, mAxisLabelPaint, anchor, angleDegrees);
    }

    protected Path mRenderGridLinesPath = new Path();
    protected float[] mRenderGridLinesBuffer = new float[2];

    @Override
    public void renderGridLines(@NotNull Canvas c) {
        if(mTrans != null) {
            if (!mXAxis.isDrawGridLinesEnabled() || !mXAxis.isEnabled())
                return;

            int clipRestoreCount = c.save();
            c.clipRect(getGridClippingRect());

            if (mRenderGridLinesBuffer.length != mAxis.mEntryCount * 2) {
                mRenderGridLinesBuffer = new float[mXAxis.mEntryCount * 2];
            }
            float[] positions = mRenderGridLinesBuffer;

            for (int i = 0; i < positions.length; i += 2) {
                positions[i] = mXAxis.mEntries[i / 2];
                positions[i + 1] = mXAxis.mEntries[i / 2];
            }

            mTrans.pointValuesToPixel(positions);

            setupGridPaint();

            Path gridLinePath = mRenderGridLinesPath;
            gridLinePath.reset();

            for (int i = 0; i < positions.length; i += 2) {
                drawGridLine(c, positions[i], positions[i + 1], gridLinePath);
            }

            c.restoreToCount(clipRestoreCount);
        }
    }

    protected RectF mGridClippingRect = new RectF();

    @NotNull
    public RectF getGridClippingRect() {
        mGridClippingRect.set(mViewPortHandler.getContentRect());
        mGridClippingRect.inset(-mAxis.getGridLineWidth(), 0f);

        return mGridClippingRect;
    }

    /**
     * Draws the grid line at the specified position using the provided path.
     */
    protected void drawGridLine(@NotNull Canvas c, float x, float y, @NotNull Path gridLinePath) {
        gridLinePath.moveTo(x, mViewPortHandler.contentBottom());
        gridLinePath.lineTo(x, mViewPortHandler.contentTop());

        // draw a path because lines don't support dashing on lower android versions
        c.drawPath(gridLinePath, mGridPaint);

        gridLinePath.reset();
    }

    protected float[] mRenderLimitLinesBuffer = new float[2];
    protected RectF mLimitLineClippingRect = new RectF();

    /**
     * Draws the LimitLines associated with this axis to the screen.
     */
    @Override
    public void renderLimitLines(@NotNull Canvas c) {
        if(mTrans != null) {
            List<LimitLine> limitLines = mXAxis.getLimitLines();

            if (limitLines.size() <= 0)
                return;

            float[] position = mRenderLimitLinesBuffer;

            for (int i = 0; i < limitLines.size(); i++) {
                LimitLine l = limitLines.get(i);

                if (!l.isEnabled())
                    continue;

                int clipRestoreCount = c.save();
                mLimitLineClippingRect.set(mViewPortHandler.getContentRect());
                mLimitLineClippingRect.inset(-l.getLineWidth(), 0f);
                c.clipRect(mLimitLineClippingRect);

                position[0] = l.getLimit();
                position[1] = 0.f;

                mTrans.pointValuesToPixel(position);

                renderLimitLineLine(c, l, position);
                renderLimitLineLabel(c, l, position, 2f + l.getYOffset());

                c.restoreToCount(clipRestoreCount);
            }
        }
    }

    float[] mLimitLineSegmentsBuffer = new float[4];
    private final Path mLimitLinePath = new Path();

    public void renderLimitLineLine(
            @NotNull Canvas c,
            @NotNull LimitLine limitLine,
            @NotNull float[] position
    ) {
        mLimitLineSegmentsBuffer[0] = position[0];
        mLimitLineSegmentsBuffer[1] = mViewPortHandler.contentTop();
        mLimitLineSegmentsBuffer[2] = position[0];
        mLimitLineSegmentsBuffer[3] = mViewPortHandler.contentBottom();

        mLimitLinePath.reset();
        mLimitLinePath.moveTo(mLimitLineSegmentsBuffer[0], mLimitLineSegmentsBuffer[1]);
        mLimitLinePath.lineTo(mLimitLineSegmentsBuffer[2], mLimitLineSegmentsBuffer[3]);

        mLimitLinePaint.setStyle(Paint.Style.STROKE);
        mLimitLinePaint.setColor(limitLine.getLineColor());
        mLimitLinePaint.setStrokeWidth(limitLine.getLineWidth());
        mLimitLinePaint.setPathEffect(limitLine.getDashPathEffect());

        c.drawPath(mLimitLinePath, mLimitLinePaint);
    }

    public void renderLimitLineLabel(
            @NotNull Canvas c,
            @NotNull LimitLine limitLine,
            @NotNull float[] position,
            float yOffset
    ) {
        String label = limitLine.getLabel();

        // if drawing the limit-value label is enabled
        if (label.length() > 0) {
            mLimitLinePaint.setStyle(limitLine.getTextStyle());
            mLimitLinePaint.setPathEffect(null);
            mLimitLinePaint.setColor(limitLine.getTextColor());
            mLimitLinePaint.setStrokeWidth(0.5f);
            mLimitLinePaint.setTextSize(limitLine.getTextSize());

            float xOffset = limitLine.getLineWidth() + limitLine.getXOffset();

            int labelPosition = limitLine.getLabelPosition();

            switch (labelPosition) {
                case LimitLine.LABEL_POSITION_RIGHT_TOP: {
                    float labelLineHeight = Utils.calcTextHeight(mLimitLinePaint, label);
                    mLimitLinePaint.setTextAlign(Align.LEFT);
                    c.drawText(label,
                            position[0] + xOffset,
                            mViewPortHandler.contentTop() + yOffset + labelLineHeight,
                            mLimitLinePaint
                    );

                    break;
                }
                case LimitLine.LABEL_POSITION_RIGHT_BOTTOM: {
                    mLimitLinePaint.setTextAlign(Align.LEFT);
                    c.drawText(
                            label,
                            position[0] + xOffset,
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
                            position[0] - xOffset,
                            mViewPortHandler.contentTop() + yOffset + labelLineHeight,
                            mLimitLinePaint
                    );

                    break;
                }
                case LimitLine.LABEL_POSITION_LEFT_BOTTOM: {
                    mLimitLinePaint.setTextAlign(Align.RIGHT);
                    c.drawText(
                            label,
                            position[0] - xOffset,
                            mViewPortHandler.contentBottom() - yOffset,
                            mLimitLinePaint
                    );

                    break;
                }
            }
        }
    }
}
