
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.BubbleDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Bubble chart implementation: Copyright 2015 Pierre-Marc Airoldi Licensed
 * under Apache License 2.0 Ported by Daniel Cohen Gindi
 */
public class BubbleChartRenderer extends BarLineScatterCandleBubbleRenderer<IBubbleDataSet, BubbleEntry> {
    protected BubbleDataProvider mChart;

    public BubbleChartRenderer(BubbleDataProvider chart, ChartAnimator animator,
                               ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;

        mRenderPaint.setStyle(Style.FILL);

        mHighlightPaint.setStyle(Style.STROKE);
        mHighlightPaint.setStrokeWidth(Utils.convertDpToPixel(1.5f));
    }

    @Override
    public void initBuffers() {
    }

    @Override
    public void drawData(@NotNull Canvas c) {
        BubbleData bubbleData = mChart.getBubbleData();

        for (IBubbleDataSet set : bubbleData.getDataSets()) {
            if (set.isVisible()) {
                drawDataSet(c, set);
            }
        }
    }

    private final float[] sizeBuffer = new float[4];
    private final float[] pointBuffer = new float[2];

    protected float getShapeSize(float entrySize, float maxSize, float reference, boolean normalizeSize) {
        float factor = normalizeSize ? ((maxSize == 0f) ? 1f : (float) Math.sqrt(entrySize / maxSize)) : entrySize;

        return reference * factor;
    }

    protected void drawDataSet(
            @NotNull Canvas c,
            @NotNull IBubbleDataSet dataSet
    ) {
        if (dataSet.getEntryCount() < 1)
            return;

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        float phaseY = mAnimator.getPhaseY();

        mXBounds.set(mChart, dataSet);

        sizeBuffer[0] = 0f;
        sizeBuffer[2] = 1f;

        trans.pointValuesToPixel(sizeBuffer);

        boolean normalizeSize = dataSet.isNormalizeSizeEnabled();

        float maxBubbleWidth = Math.abs(sizeBuffer[2] - sizeBuffer[0]);
        float maxBubbleHeight = Math.abs(mViewPortHandler.contentBottom() - mViewPortHandler.contentTop());
        float referenceSize = Math.min(maxBubbleHeight, maxBubbleWidth);

        for (int j = mXBounds.min; j <= mXBounds.range + mXBounds.min; j++) {

            final BubbleEntry entry = dataSet.getEntryForIndex(j);

            pointBuffer[0] = entry.getX();
            pointBuffer[1] = (entry.getY()) * phaseY;
            trans.pointValuesToPixel(pointBuffer);

            float px = pointBuffer[0];
            float py = pointBuffer[1];

            float shapeHalf = getShapeSize(entry.getSize(), dataSet.getMaxSize(), referenceSize, normalizeSize) * 0.5f;

            if (!mViewPortHandler.isInBoundsTop(py + shapeHalf)
                    || !mViewPortHandler.isInBoundsBottom(py - shapeHalf))
                continue;

            if (!mViewPortHandler.isInBoundsLeft(px + shapeHalf))
                continue;

            if (!mViewPortHandler.isInBoundsRight(px - shapeHalf))
                break;

            int color = dataSet.getColor(j);

            mRenderPaint.setColor(color);
            c.drawCircle(px, py, shapeHalf, mRenderPaint);
        }
    }

    @Override
    public void drawValues(@NotNull Canvas c) {
        BubbleData bubbleData = mChart.getBubbleData();

        if (bubbleData == null)
            return;

        // if values are drawn
        if (isDrawingValuesAllowed(mChart)) {
            List<IBubbleDataSet> dataSets = bubbleData.getDataSets();

            float lineHeight = Utils.calcTextHeight(mValuePaint, "1");

            for (int i = 0; i < dataSets.size(); i++) {
                IBubbleDataSet dataSet = dataSets.get(i);

                if (!shouldDrawValues(dataSet) || dataSet.getEntryCount() < 1)
                    continue;

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);

                final float phaseX = Math.max(0.f, Math.min(1.f, mAnimator.getPhaseX()));
                final float phaseY = mAnimator.getPhaseY();

                mXBounds.set(mChart, dataSet);

                float[] positions = mChart.getTransformer(dataSet.getAxisDependency())
                        .generateTransformedValuesBubble(dataSet, phaseY, mXBounds.min, mXBounds.max);

                float alpha = phaseX == 1 ? phaseY : phaseX;

                MPPointF iconsOffset = MPPointF.getInstance(dataSet.getIconsOffset());
                iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x);
                iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y);

                for (int j = 0; j < positions.length; j += 2) {
                    int valueTextColor = dataSet.getValueTextColor(j / 2 + mXBounds.min);
                    valueTextColor = Color.argb(Math.round(255.f * alpha), Color.red(valueTextColor),
                            Color.green(valueTextColor), Color.blue(valueTextColor));

                    float x = positions[j];
                    float y = positions[j + 1];

                    if (!mViewPortHandler.isInBoundsRight(x))
                        break;

                    if ((!mViewPortHandler.isInBoundsLeft(x) || !mViewPortHandler.isInBoundsY(y)))
                        continue;

                    BubbleEntry entry = dataSet.getEntryForIndex(j / 2 + mXBounds.min);

                    if (dataSet.isDrawValuesEnabled()) {
                        drawValue(c, dataSet.getValueFormatter(), entry.getSize(), entry, i, x,
                                y + (0.5f * lineHeight), valueTextColor);
                    }

                    Drawable icon = entry.getIcon();
                    if (icon != null && dataSet.isDrawIconsEnabled()) {
                        Utils.drawImage(
                                c,
                                icon,
                                (int)(x + iconsOffset.x),
                                (int)(y + iconsOffset.y),
                                icon.getIntrinsicWidth(),
                                icon.getIntrinsicHeight());
                    }
                }

                MPPointF.recycleInstance(iconsOffset);
            }
        }
    }

    @Override
    public void drawExtras(@NotNull Canvas c) {
    }

    private final float[] hsvBuffer = new float[3];

    @Override
    public void drawHighlighted(@NotNull Canvas c, @NotNull Highlight[] indices) {
        BubbleData bubbleData = mChart.getBubbleData();

        float phaseY = mAnimator.getPhaseY();

        for (Highlight high : indices) {
            IBubbleDataSet set = bubbleData.getDataSetByIndex(high.getDataSetIndex());

            if (!set.isHighlightEnabled())
                continue;

            BubbleEntry entry = set.getEntryForXValue(high.getX(), high.getY());

            if(entry == null) {
                continue;
            }

            if (entry.getY() != high.getY())
                continue;

            if (!isInBoundsX(entry, set))
                continue;

            Transformer trans = mChart.getTransformer(set.getAxisDependency());

            sizeBuffer[0] = 0f;
            sizeBuffer[2] = 1f;

            trans.pointValuesToPixel(sizeBuffer);

            boolean normalizeSize = set.isNormalizeSizeEnabled();

            float maxBubbleWidth = Math.abs(sizeBuffer[2] - sizeBuffer[0]);
            float maxBubbleHeight = Math.abs(
                    mViewPortHandler.contentBottom() - mViewPortHandler.contentTop());
            float referenceSize = Math.min(maxBubbleHeight, maxBubbleWidth);

            pointBuffer[0] = entry.getX();
            pointBuffer[1] = (entry.getY()) * phaseY;
            trans.pointValuesToPixel(pointBuffer);

            float px = pointBuffer[0];
            float py = pointBuffer[1];

            high.setDraw(px, py);

            float shapeHalf = getShapeSize(
                    entry.getSize(),
                    set.getMaxSize(),
                    referenceSize,
                    normalizeSize
            ) * 0.5f;

            if (!mViewPortHandler.isInBoundsTop(py + shapeHalf)
                    || !mViewPortHandler.isInBoundsBottom(py - shapeHalf))
                continue;

            if (!mViewPortHandler.isInBoundsLeft(px + shapeHalf))
                continue;

            if (!mViewPortHandler.isInBoundsRight(px - shapeHalf))
                break;

            final int originalColor = set.getColor((int) entry.getX());

            Color.RGBToHSV(Color.red(originalColor), Color.green(originalColor),
                    Color.blue(originalColor), hsvBuffer);

            hsvBuffer[2] *= 0.5f;
            final int color = Color.HSVToColor(Color.alpha(originalColor), hsvBuffer);

            mHighlightPaint.setColor(color);
            mHighlightPaint.setStrokeWidth(set.getHighlightCircleWidth());
            c.drawCircle(px, py, shapeHalf, mHighlightPaint);
        }
    }
}
