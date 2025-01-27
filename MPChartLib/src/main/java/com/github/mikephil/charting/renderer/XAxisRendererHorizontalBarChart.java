
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.RectF;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.utils.FSize;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class XAxisRendererHorizontalBarChart extends XAxisRenderer {
    protected BarChart mChart;

    public XAxisRendererHorizontalBarChart(
            @NotNull ViewPortHandler viewPortHandler,
            @NotNull XAxis xAxis,
            @NotNull Transformer trans,
            @NotNull BarChart chart) {
        super(viewPortHandler, xAxis, trans);

        this.mChart = chart;
    }

    @Override
    public void computeAxis(float min, float max, boolean inverted) {
        // calculate the starting and entry point of the y-labels (depending on
        // zoom / contentrect bounds)
        if (mViewPortHandler.contentWidth() > 10 && !mViewPortHandler.isFullyZoomedOutY()) {
            MPPointF p1 = mTrans.getValuesByTouchPoint(mViewPortHandler.contentLeft(), mViewPortHandler.contentBottom());
            MPPointF p2 = mTrans.getValuesByTouchPoint(mViewPortHandler.contentLeft(), mViewPortHandler.contentTop());

            if (inverted) {
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
    
    @Override
    protected void computeSize() {
        mAxisLabelPaint.setTypeface(mXAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mXAxis.getTextSize());

        String longest = mXAxis.getLongestLabel();

        FSize labelSize = Utils.calcTextSize(mAxisLabelPaint, longest);

        float labelWidth = (int)(labelSize.width + mXAxis.getXOffset() * 3.5f);
        float labelHeight = labelSize.height;

        FSize labelRotatedSize = Utils.getSizeOfRotatedRectangleByDegrees(
                labelSize.width,
                labelHeight,
                mXAxis.getLabelRotationAngle()
        );

        mXAxis.mLabelWidth = Math.round(labelWidth);
        mXAxis.mLabelHeight = Math.round(labelHeight);
        mXAxis.mLabelRotatedWidth = (int)(labelRotatedSize.width + mXAxis.getXOffset() * 3.5f);
        mXAxis.mLabelRotatedHeight = Math.round(labelRotatedSize.height);

        FSize.recycleInstance(labelRotatedSize);
    }

    @Override
    public void renderAxisLabels(@NotNull Canvas c) {
        if (!mXAxis.isEnabled() || !mXAxis.isDrawLabelsEnabled())
            return;

        float xOffset = mXAxis.getXOffset();

        mAxisLabelPaint.setTypeface(mXAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mXAxis.getTextSize());
        mAxisLabelPaint.setColor(mXAxis.getTextColor());

        MPPointF pointF = MPPointF.getInstance();
        int pos = mXAxis.getPosition();

        switch (pos) {
            case XAxis.POSITION_TOP: {
                pointF.x = 0.0f;
                pointF.y = 0.5f;
                drawLabels(c, mViewPortHandler.contentRight() + xOffset, pointF);

                break;
            }
            case XAxis.POSITION_TOP_INSIDE: {
                pointF.x = 1.0f;
                pointF.y = 0.5f;
                drawLabels(c, mViewPortHandler.contentRight() - xOffset, pointF);

                break;
            }
            case XAxis.POSITION_BOTTOM: {
                pointF.x = 1.0f;
                pointF.y = 0.5f;
                drawLabels(c, mViewPortHandler.contentLeft() - xOffset, pointF);

                break;
            }
            case XAxis.POSITION_BOTTOM_INSIDE: {
                pointF.x = 1.0f;
                pointF.y = 0.5f;
                drawLabels(c, mViewPortHandler.contentLeft() + xOffset, pointF);

                break;
            }
            case XAxis.POSITION_BOTH_SIDED: {
                pointF.x = 0.0f;
                pointF.y = 0.5f;
                drawLabels(c, mViewPortHandler.contentRight() + xOffset, pointF);
                pointF.x = 1.0f;
                pointF.y = 0.5f;
                drawLabels(c, mViewPortHandler.contentLeft() - xOffset, pointF);

                break;
            }
        }


        MPPointF.recycleInstance(pointF);
    }

    @Override
    protected void drawLabels(
            @NotNull Canvas c,
            float pos,
            @NotNull MPPointF anchor
    ) {
        final float labelRotationAngleDegrees = mXAxis.getLabelRotationAngle();
        boolean centeringEnabled = mXAxis.isCenterAxisLabelsEnabled();

        float[] positions = new float[mXAxis.mEntryCount * 2];

        for (int i = 0; i < positions.length; i += 2) {
            // only fill x values
            if (centeringEnabled) {
                positions[i + 1] = mXAxis.mCenteredEntries[i / 2];
            } else {
                positions[i + 1] = mXAxis.mEntries[i / 2];
            }
        }

        mTrans.pointValuesToPixel(positions);

        for (int i = 0; i < positions.length; i += 2) {
            float y = positions[i + 1];

            if (mViewPortHandler.isInBoundsY(y)) {

                String label = mXAxis.getValueFormatter().getFormattedValue(mXAxis.mEntries[i / 2], mXAxis);
                drawLabel(c, label, pos, y, anchor, labelRotationAngleDegrees);
            }
        }
    }

    @Override
    @NotNull
    public RectF getGridClippingRect() {
        mGridClippingRect.set(mViewPortHandler.getContentRect());
        mGridClippingRect.inset(0.f, -mAxis.getGridLineWidth());
        return mGridClippingRect;
    }

    @Override
    protected void drawGridLine(
            @NotNull Canvas c,
            float x, float y,
            @NotNull Path gridLinePath
    ) {
        gridLinePath.moveTo(mViewPortHandler.contentRight(), y);
        gridLinePath.lineTo(mViewPortHandler.contentLeft(), y);

        // draw a path because lines don't support dashing on lower android versions
        c.drawPath(gridLinePath, mGridPaint);

        gridLinePath.reset();
    }

    @Override
    public void renderAxisLine(@NotNull Canvas c) {
        if (!mXAxis.isDrawAxisLineEnabled() || !mXAxis.isEnabled())
            return;

        mAxisLinePaint.setColor(mXAxis.getAxisLineColor());
        mAxisLinePaint.setStrokeWidth(mXAxis.getAxisLineWidth());

        int pos = mXAxis.getPosition();

        if (pos == XAxis.POSITION_TOP
                || pos == XAxis.POSITION_TOP_INSIDE
                || pos == XAxis.POSITION_BOTH_SIDED) {
            c.drawLine(mViewPortHandler.contentRight(),
                    mViewPortHandler.contentTop(), mViewPortHandler.contentRight(),
                    mViewPortHandler.contentBottom(), mAxisLinePaint);
        }

        if (pos == XAxis.POSITION_BOTTOM
                || pos == XAxis.POSITION_BOTTOM_INSIDE
                || pos == XAxis.POSITION_BOTH_SIDED) {
            c.drawLine(mViewPortHandler.contentLeft(),
                    mViewPortHandler.contentTop(), mViewPortHandler.contentLeft(),
                    mViewPortHandler.contentBottom(), mAxisLinePaint);
        }
    }

    protected Path mRenderLimitLinesPathBuffer = new Path();

    /**
	 * Draws the LimitLines associated with this axis to the screen.
	 * This is the standard YAxis renderer using the XAxis limit lines.
	 */
	@Override
	public void renderLimitLines(@NotNull Canvas c) {
		List<LimitLine> limitLines = mXAxis.getLimitLines();

		if (limitLines.size() <= 0)
			return;

		float[] pts = mRenderLimitLinesBuffer;
        pts[0] = 0;
        pts[1] = 0;

		Path limitLinePath = mRenderLimitLinesPathBuffer;
        limitLinePath.reset();

		for (int i = 0; i < limitLines.size(); i++) {

			LimitLine l = limitLines.get(i);

            if(!l.isEnabled())
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

            //noinspection ConstantConditions
            mTrans.pointValuesToPixel(pts);

			float py = pts[1];

			limitLinePath.moveTo(mViewPortHandler.contentLeft(), py);
			limitLinePath.lineTo(mViewPortHandler.contentRight(), py);

			c.drawPath(limitLinePath, mLimitLinePaint);
			limitLinePath.reset();
			// c.drawLines(pts, mLimitLinePaint);

			String label = l.getLabel();

			// if drawing the limit-value label is enabled
			if (label.length() > 0) {
				mLimitLinePaint.setStyle(l.getTextStyle());
				mLimitLinePaint.setPathEffect(null);
				mLimitLinePaint.setColor(l.getTextColor());
				mLimitLinePaint.setStrokeWidth(0.5f);
				mLimitLinePaint.setTextSize(l.getTextSize());

				float labelLineHeight = Utils.calcTextHeight(mLimitLinePaint, label);
                float xOffset = Utils.convertDpToPixel(4f) + l.getXOffset();
                float yOffset = l.getLineWidth() + labelLineHeight + l.getYOffset();

                int position = l.getLabelPosition();

                switch (position) {
                    case LimitLine.LABEL_POSITION_RIGHT_TOP: {
                        mLimitLinePaint.setTextAlign(Align.RIGHT);
                        c.drawText(
                                label,
                                mViewPortHandler.contentRight() - xOffset,
                                py - yOffset + labelLineHeight,
                                mLimitLinePaint
                        );

                        break;
                    }
                    case LimitLine.LABEL_POSITION_RIGHT_BOTTOM: {
                        mLimitLinePaint.setTextAlign(Align.RIGHT);
                        c.drawText(
                                label,
                                mViewPortHandler.contentRight() - xOffset,
                                py + yOffset,
                                mLimitLinePaint
                        );

                        break;
                    }
                    case LimitLine.LABEL_POSITION_LEFT_TOP: {
                        mLimitLinePaint.setTextAlign(Align.LEFT);
                        c.drawText(
                                label,
                                mViewPortHandler.contentLeft() + xOffset,
                                py - yOffset + labelLineHeight,
                                mLimitLinePaint
                        );
                    }
                    case LimitLine.LABEL_POSITION_LEFT_BOTTOM: {
                        mLimitLinePaint.setTextAlign(Align.LEFT);
                        c.drawText(
                                label,
                                mViewPortHandler.offsetLeft() + xOffset,
                                py + yOffset,
                                mLimitLinePaint
                        );

                        break;
                    }
                }
			}

            c.restoreToCount(clipRestoreCount);
		}
	}
}
