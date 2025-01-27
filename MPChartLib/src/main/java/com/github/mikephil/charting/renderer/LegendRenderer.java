
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Typeface;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.FSize;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LegendRenderer extends Renderer {
    /**
     * paint for the legend labels
     */
    protected Paint mLegendLabelPaint;

    /**
     * paint used for the legend forms
     */
    protected Paint mLegendFormPaint;

    /**
     * the legend object this renderer renders
     */
    protected Legend mLegend;

    public LegendRenderer(@NotNull ViewPortHandler viewPortHandler, @NotNull Legend legend) {
        super(viewPortHandler);

        this.mLegend = legend;

        mLegendLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLegendLabelPaint.setTextSize(Utils.convertDpToPixel(9f));
        mLegendLabelPaint.setTextAlign(Align.LEFT);

        mLegendFormPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLegendFormPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * Returns the Paint object used for drawing the Legend labels.
     */
    @NotNull
    public Paint getLabelPaint() {
        return mLegendLabelPaint;
    }

    /**
     * Returns the Paint object used for drawing the Legend forms.
     */
    @NotNull
    public Paint getFormPaint() {
        return mLegendFormPaint;
    }

    protected List<LegendEntry> computedEntries = new ArrayList<>(16);

    /**
     * Prepares the legend and calculates all needed forms, labels and colors.
     */
    public void computeLegend(ChartData<?, ?> data) {
        if (!mLegend.isLegendCustom()) {
            computedEntries.clear();

            // loop for building up the colors and labels used in the legend
            for (int i = 0; i < data.getDataSetCount(); i++) {
                IDataSet<?> dataSet = data.getDataSetByIndex(i);

                List<Integer> clrs = dataSet.getColors();
                int entryCount = dataSet.getEntryCount();

                // if we have a barchart with stacked bars
                if (dataSet instanceof IBarDataSet && ((IBarDataSet) dataSet).isStacked()) {
                    IBarDataSet bds = (IBarDataSet) dataSet;
                    String[] sLabels = bds.getStackLabels();

                    int minEntries = Math.min(clrs.size(), bds.getStackSize());

                    for (int j = 0; j < minEntries; j++) {
                        String label;
                        if (sLabels.length > 0) {
                            int labelIndex = j % minEntries;
                            label = labelIndex < sLabels.length ? sLabels[labelIndex] : null;
                        } else {
                            label = null;
                        }

                        computedEntries.add(new LegendEntry(
                                label,
                                dataSet.getForm(),
                                dataSet.getFormSize(),
                                dataSet.getFormLineWidth(),
                                dataSet.getFormLineDashEffect(),
                                clrs.get(j)
                        ));
                    }

                    if (bds.getLabel() != null) {
                        // add the legend description label
                        computedEntries.add(new LegendEntry(
                                dataSet.getLabel(),
                                Legend.FORM_NONE,
                                Float.NaN,
                                Float.NaN,
                                null,
                                ColorTemplate.COLOR_NONE
                        ));
                    }

                } else if (dataSet instanceof IPieDataSet) {
                    IPieDataSet pds = (IPieDataSet) dataSet;

                    for (int j = 0; j < clrs.size() && j < entryCount; j++) {
                        computedEntries.add(new LegendEntry(
                                pds.getEntryForIndex(j).getLabel(),
                                dataSet.getForm(),
                                dataSet.getFormSize(),
                                dataSet.getFormLineWidth(),
                                dataSet.getFormLineDashEffect(),
                                clrs.get(j)
                        ));
                    }

                    if (pds.getLabel() != null) {
                        // add the legend description label
                        computedEntries.add(new LegendEntry(
                                dataSet.getLabel(),
                                Legend.FORM_NONE,
                                Float.NaN,
                                Float.NaN,
                                null,
                                ColorTemplate.COLOR_NONE
                        ));
                    }

                } else if (dataSet instanceof ICandleDataSet && ((ICandleDataSet) dataSet).getDecreasingColor() !=
                        ColorTemplate.COLOR_NONE) {

                    int decreasingColor = ((ICandleDataSet) dataSet).getDecreasingColor();
                    int increasingColor = ((ICandleDataSet) dataSet).getIncreasingColor();

                    computedEntries.add(new LegendEntry(
                            null,
                            dataSet.getForm(),
                            dataSet.getFormSize(),
                            dataSet.getFormLineWidth(),
                            dataSet.getFormLineDashEffect(),
                            decreasingColor
                    ));

                    computedEntries.add(new LegendEntry(
                            dataSet.getLabel(),
                            dataSet.getForm(),
                            dataSet.getFormSize(),
                            dataSet.getFormLineWidth(),
                            dataSet.getFormLineDashEffect(),
                            increasingColor
                    ));

                } else { // all others
                    for (int j = 0; j < clrs.size() && j < entryCount; j++) {
                        String label;

                        // if multiple colors are set for a DataSet, group them
                        if (j < clrs.size() - 1 && j < entryCount - 1) {
                            label = null;
                        } else { // add label to the last entry
                            label = data.getDataSetByIndex(i).getLabel();
                        }

                        computedEntries.add(new LegendEntry(
                                label,
                                dataSet.getForm(),
                                dataSet.getFormSize(),
                                dataSet.getFormLineWidth(),
                                dataSet.getFormLineDashEffect(),
                                clrs.get(j)
                        ));
                    }
                }
            }

            if (mLegend.getExtraEntries() != null) {
                Collections.addAll(computedEntries, mLegend.getExtraEntries());
            }

            mLegend.setEntries(computedEntries);
        }

        Typeface tf = mLegend.getTypeface();

        if (tf != null)
            mLegendLabelPaint.setTypeface(tf);

        mLegendLabelPaint.setTextSize(mLegend.getTextSize());
        mLegendLabelPaint.setColor(mLegend.getTextColor());

        // calculate all dimensions of the mLegend
        mLegend.calculateDimensions(mLegendLabelPaint, mViewPortHandler);
    }

    protected Paint.FontMetrics legendFontMetrics = new Paint.FontMetrics();

    public void renderLegend(Canvas c) {
        if (!mLegend.isEnabled())
            return;

        Typeface tf = mLegend.getTypeface();

        if (tf != null)
            mLegendLabelPaint.setTypeface(tf);

        mLegendLabelPaint.setTextSize(mLegend.getTextSize());
        mLegendLabelPaint.setColor(mLegend.getTextColor());

        float labelLineHeight = Utils.getLineHeight(mLegendLabelPaint, legendFontMetrics);
        float labelLineSpacing = Utils.getLineSpacing(mLegendLabelPaint, legendFontMetrics)
                + Utils.convertDpToPixel(mLegend.getYEntrySpace());
        float formYOffset = labelLineHeight - Utils.calcTextHeight(mLegendLabelPaint, "ABC") * 0.5f;

        LegendEntry[] entries = mLegend.getEntries();

        float formToTextSpace = Utils.convertDpToPixel(mLegend.getFormToTextSpace());
        float xEntrySpace = Utils.convertDpToPixel(mLegend.getXEntrySpace());
        int orientation = mLegend.getOrientation();
        int horizontalAlignment = mLegend.getHorizontalAlignment();
        int verticalAlignment = mLegend.getVerticalAlignment();
        int direction = mLegend.getDirection();
        float defaultFormSize = Utils.convertDpToPixel(mLegend.getFormSize());

        // space between the entries
        float stackSpace = Utils.convertDpToPixel(mLegend.getStackSpace());

        float xOffset = mLegend.getXOffset();
        float yOffset = mLegend.getYOffset();

        float originPosX = 0f;

        switch (horizontalAlignment) {
            case Legend.HALIGN_LEFT:
                if (orientation == Legend.ORIENTATION_VERTICAL) {
                    originPosX = xOffset;
                } else {
                    originPosX = mViewPortHandler.contentLeft() + xOffset;
                }

                if (direction == Legend.DIRECTION_RIGHT_TO_LEFT) {
                    originPosX += mLegend.mNeededWidth;
                }

                break;
            case Legend.HALIGN_RIGHT:
                if (orientation == Legend.ORIENTATION_VERTICAL) {
                    originPosX = mViewPortHandler.getChartWidth() - xOffset;
                } else {
                    originPosX = mViewPortHandler.contentRight() - xOffset;
                }

                if (direction == Legend.DIRECTION_LEFT_TO_RIGHT) {
                    originPosX -= mLegend.mNeededWidth;
                }

                break;
            case Legend.HALIGN_CENTER:
                if (orientation == Legend.ORIENTATION_VERTICAL) {
                    originPosX = mViewPortHandler.getChartWidth() * 0.5f;
                } else {
                    originPosX = mViewPortHandler.contentLeft() + mViewPortHandler.contentWidth() * 0.5f;
                }

                originPosX += (direction == Legend.DIRECTION_LEFT_TO_RIGHT
                        ? +xOffset
                        : -xOffset);

                // Horizontally layed out legends do the center offset on a line basis,
                // So here we offset the vertical ones only.
                if (orientation == Legend.ORIENTATION_VERTICAL) {
                    originPosX += (direction == Legend.DIRECTION_LEFT_TO_RIGHT
                            ? mLegend.mNeededWidth * -0.5f + xOffset
                            : mLegend.mNeededWidth * 0.5f - xOffset);
                }

                break;
        }

        switch (orientation) {
            case Legend.ORIENTATION_HORIZONTAL: {
                List<FSize> calculatedLineSizes = mLegend.getCalculatedLineSizes();
                List<FSize> calculatedLabelSizes = mLegend.getCalculatedLabelSizes();
                List<Boolean> calculatedLabelBreakPoints = mLegend.getCalculatedLabelBreakPoints();

                float posX = originPosX;
                float posY = 0f;

                switch (verticalAlignment) {
                    case Legend.VALIGN_TOP:
                        posY = yOffset;
                        break;

                    case Legend.VALIGN_BOTTOM:
                        posY = mViewPortHandler.getChartHeight() - yOffset - mLegend.mNeededHeight;
                        break;

                    case Legend.VALIGN_CENTER:
                        posY = (mViewPortHandler.getChartHeight() - mLegend.mNeededHeight) * 0.5f + yOffset;
                        break;
                }

                int lineIndex = 0;

                for (int i = 0, count = entries.length; i < count; i++) {
                    LegendEntry e = entries[i];

                    boolean drawingForm = e.form != Legend.FORM_NONE;
                    float formSize = Float.isNaN(e.formSize) ? defaultFormSize : Utils.convertDpToPixel(e.formSize);

                    if (i < calculatedLabelBreakPoints.size() && calculatedLabelBreakPoints.get(i)) {
                        posX = originPosX;
                        posY += labelLineHeight + labelLineSpacing;
                    }

                    if (posX == originPosX &&
                            horizontalAlignment == Legend.HALIGN_CENTER &&
                            lineIndex < calculatedLineSizes.size()) {
                        posX += (direction == Legend.DIRECTION_RIGHT_TO_LEFT
                                ? calculatedLineSizes.get(lineIndex).width
                                : -calculatedLineSizes.get(lineIndex).width) * 0.5f;
                        lineIndex++;
                    }

                    boolean isStacked = e.label == null; // grouped forms have null labels

                    if (drawingForm) {
                        if (direction == Legend.DIRECTION_RIGHT_TO_LEFT)
                            posX -= formSize;

                        drawForm(c, posX, posY + formYOffset, e, mLegend);

                        if (direction == Legend.DIRECTION_LEFT_TO_RIGHT)
                            posX += formSize;
                    }

                    if (!isStacked) {
                        if (drawingForm)
                            posX += direction == Legend.DIRECTION_RIGHT_TO_LEFT ? -formToTextSpace :
                                    formToTextSpace;

                        if (direction == Legend.DIRECTION_RIGHT_TO_LEFT)
                            posX -= calculatedLabelSizes.get(i).width;

                        drawLabel(c, posX, posY + labelLineHeight, e.label);

                        if (direction == Legend.DIRECTION_LEFT_TO_RIGHT)
                            posX += calculatedLabelSizes.get(i).width;

                        posX += direction == Legend.DIRECTION_RIGHT_TO_LEFT ? -xEntrySpace : xEntrySpace;
                    } else
                        posX += direction == Legend.DIRECTION_RIGHT_TO_LEFT ? -stackSpace : stackSpace;
                }

                break;
            }

            case Legend.ORIENTATION_VERTICAL: {
                // contains the stacked legend size in pixels
                float stack = 0f;
                boolean wasStacked = false;
                float posY = 0.f;

                switch (verticalAlignment) {
                    case Legend.VALIGN_TOP:
                        posY = (horizontalAlignment == Legend.HALIGN_CENTER
                                ? 0.f
                                : mViewPortHandler.contentTop());
                        posY += yOffset;
                        break;

                    case Legend.VALIGN_BOTTOM:
                        posY = (horizontalAlignment == Legend.HALIGN_CENTER
                                ? mViewPortHandler.getChartHeight()
                                : mViewPortHandler.contentBottom());
                        posY -= mLegend.mNeededHeight + yOffset;
                        break;

                    case Legend.VALIGN_CENTER:
                        posY = mViewPortHandler.getChartHeight() * 0.5f
                                - mLegend.mNeededHeight * 0.5f
                                + mLegend.getYOffset();
                        break;
                }

                for (LegendEntry e : entries) {
                    boolean drawingForm = e.form != Legend.FORM_NONE;
                    float formSize = Float.isNaN(e.formSize) ? defaultFormSize : Utils.convertDpToPixel(e.formSize);

                    float posX = originPosX;

                    if (drawingForm) {
                        if (direction == Legend.DIRECTION_LEFT_TO_RIGHT)
                            posX += stack;
                        else
                            posX -= formSize - stack;

                        drawForm(c, posX, posY + formYOffset, e, mLegend);

                        if (direction == Legend.DIRECTION_LEFT_TO_RIGHT)
                            posX += formSize;
                    }

                    if (e.label != null) {
                        if (drawingForm && !wasStacked)
                            posX += direction == Legend.DIRECTION_LEFT_TO_RIGHT ? formToTextSpace
                                    : -formToTextSpace;
                        else if (wasStacked)
                            posX = originPosX;

                        if (direction == Legend.DIRECTION_RIGHT_TO_LEFT)
                            posX -= Utils.calcTextWidth(mLegendLabelPaint, e.label);

                        if (wasStacked) {
                            posY += labelLineHeight + labelLineSpacing;
                        }

                        drawLabel(c, posX, posY + labelLineHeight, e.label);

                        // make a step down
                        posY += labelLineHeight + labelLineSpacing;
                        stack = 0f;
                    } else {
                        stack += formSize + stackSpace;
                        wasStacked = true;
                    }
                }

                break;

            }
        }
    }

    private final Path mLineFormPath = new Path();

    /**
     * Draws the Legend-form at the given position with the color at the given
     * index.
     *
     * @param c      canvas to draw with
     * @param x      position
     * @param y      position
     * @param entry  the entry to render
     * @param legend the legend context
     */
    protected void drawForm(
            @NotNull Canvas c,
            float x, float y,
            @NotNull LegendEntry entry,
            @NotNull Legend legend
    ) {
        if (entry.formColor == ColorTemplate.COLOR_SKIP ||
                entry.formColor == ColorTemplate.COLOR_NONE ||
                entry.formColor == 0)
            return;

        int restoreCount = c.save();

        int form = entry.form;
        if (form == Legend.FORM_DEFAULT)
            form = legend.getForm();

        mLegendFormPaint.setColor(entry.formColor);

        float formSize = Utils.convertDpToPixel(
                Float.isNaN(entry.formSize)
                        ? legend.getFormSize()
                        : entry.formSize);
        float half = formSize * 0.5f;

        switch (form) {
            case Legend.FORM_NONE:
                // Do nothing
            case Legend.FORM_EMPTY:
                // Do not draw, but keep space for the form
                break;

            case Legend.FORM_DEFAULT:
            case Legend.FORM_CIRCLE:
                mLegendFormPaint.setStyle(Paint.Style.FILL);
                c.drawCircle(x + half, y, half, mLegendFormPaint);
                break;

            case Legend.FORM_SQUARE:
                mLegendFormPaint.setStyle(Paint.Style.FILL);
                c.drawRect(x, y - half, x + formSize, y + half, mLegendFormPaint);
                break;

            case Legend.FORM_LINE: {
                float formLineWidth = Utils.convertDpToPixel(
                        Float.isNaN(entry.formLineWidth)
                                ? legend.getFormLineWidth()
                                : entry.formLineWidth);
                DashPathEffect formLineDashEffect = entry.formLineDashEffect == null
                        ? legend.getFormLineDashEffect()
                        : entry.formLineDashEffect;
                mLegendFormPaint.setStyle(Paint.Style.STROKE);
                mLegendFormPaint.setStrokeWidth(formLineWidth);
                mLegendFormPaint.setPathEffect(formLineDashEffect);

                mLineFormPath.reset();
                mLineFormPath.moveTo(x, y);
                mLineFormPath.lineTo(x + formSize, y);
                c.drawPath(mLineFormPath, mLegendFormPaint);
                break;
            }
        }

        c.restoreToCount(restoreCount);
    }

    /**
     * Draws the provided label at the given position.
     *
     * @param c     canvas to draw with
     * @param label the label to draw
     */
    protected void drawLabel(@NotNull Canvas c, float x, float y, @NotNull String label) {
        c.drawText(label, x, y, mLegendLabelPaint);
    }
}
