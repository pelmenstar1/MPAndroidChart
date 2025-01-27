
package com.github.mikephil.charting.data;

import android.graphics.Color;

import androidx.annotation.ColorInt;

import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.Fill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BarDataSet extends BarLineScatterCandleBubbleDataSet<BarEntry> implements IBarDataSet {
    /**
     * the maximum number of bars that are stacked upon each other, this value
     * is calculated from the Entries that are added to the DataSet
     */
    private int mStackSize = 1;

    /**
     * the color used for drawing the bar shadows
     */
    @ColorInt
    private int mBarShadowColor = Color.rgb(215, 215, 215);

    private float mBarBorderWidth = 0.0f;

    @ColorInt
    private int mBarBorderColor = Color.BLACK;

    /**
     * the alpha value used to draw the highlight indicator bar
     */
    private int mHighLightAlpha = 120;

    /**
     * the overall entry count, including counting each stack-value individually
     */
    private int mEntryCountStacks = 0;

    /**
     * array of labels used to describe the different values of the stacked bars
     */
    @NotNull
    private String[] mStackLabels = new String[0];

    @NotNull
    protected List<Fill> mFills = new ArrayList<>();

    public BarDataSet(@NotNull List<BarEntry> values, @Nullable String label) {
        super(values, label);

        mHighLightColor = Color.rgb(0, 0, 0);

        calcStackSize(values);
        calcEntryCountIncludingStacks(values);
    }

    @Override
    @NotNull
    public DataSet<BarEntry> copy() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < mEntries.size(); i++) {
            entries.add(mEntries.get(i).copy());
        }

        BarDataSet copied = new BarDataSet(entries, getLabel());
        copy(copied);
        return copied;
    }

    protected void copy(@NotNull BarDataSet barDataSet) {
        super.copy(barDataSet);
        barDataSet.mStackSize = mStackSize;
        barDataSet.mBarShadowColor = mBarShadowColor;
        barDataSet.mBarBorderWidth = mBarBorderWidth;
        barDataSet.mStackLabels = mStackLabels;
        barDataSet.mHighLightAlpha = mHighLightAlpha;
    }

    @Override
    @NotNull
    public List<Fill> getFills() {
        return mFills;
    }

    @Override
    @NotNull
    public Fill getFill(int index) {
        return mFills.get(index % mFills.size());
    }

    /**
     * This method is deprecated.
     * Use getFills() instead.
     */
    @Deprecated
    public List<Fill> getGradients() {
        return mFills;
    }

    /**
     * This method is deprecated.
     * Use getFill(...) instead.
     */
    @Deprecated
    public Fill getGradient(int index) {
        return getFill(index);
    }

    /**
     * Sets the start and end color for gradient color, ONLY color that should be used for this DataSet.
     */
    public void setGradientColor(@ColorInt int startColor, @ColorInt int endColor) {
        mFills.clear();
        mFills.add(new Fill(startColor, endColor));
    }

    /**
     * This method is deprecated.
     * Use setFills(...) instead.
     */
    @Deprecated
    public void setGradientColors(@NotNull List<Fill> gradientColors) {
        this.mFills = gradientColors;
    }

    /**
     * Sets the fills for the bars in this dataset.
     */
    public void setFills(@NotNull List<Fill> fills) {
        this.mFills = fills;
    }

    /**
     * Calculates the total number of entries this DataSet represents, including
     * stacks. All values belonging to a stack are calculated separately.
     */
    private void calcEntryCountIncludingStacks(@NotNull List<BarEntry> yVals) {
        mEntryCountStacks = 0;

        for (int i = 0; i < yVals.size(); i++) {
            float[] values = yVals.get(i).getYVals();

            if (values == null)
                mEntryCountStacks++;
            else
                mEntryCountStacks += values.length;
        }
    }

    /**
     * calculates the maximum stacksize that occurs in the Entries array of this
     * DataSet
     */
    private void calcStackSize(@NotNull List<BarEntry> yVals) {
        for (int i = 0; i < yVals.size(); i++) {
            float[] values = yVals.get(i).getYVals();

            if (values != null && values.length > mStackSize)
                mStackSize = values.length;
        }
    }

    @Override
    protected void calcMinMax(@NotNull BarEntry e) {
        if (!Float.isNaN(e.getY())) {

            if (e.getYVals() == null) {

                if (e.getY() < mYMin)
                    mYMin = e.getY();

                if (e.getY() > mYMax)
                    mYMax = e.getY();
            } else {

                if (-e.getNegativeSum() < mYMin)
                    mYMin = -e.getNegativeSum();

                if (e.getPositiveSum() > mYMax)
                    mYMax = e.getPositiveSum();
            }

            calcMinMaxX(e);
        }
    }

    @Override
    public int getStackSize() {
        return mStackSize;
    }

    @Override
    public boolean isStacked() {
        return mStackSize > 1;
    }

    /**
     * returns the overall entry count, including counting each stack-value
     * individually
     */
    public int getEntryCountStacks() {
        return mEntryCountStacks;
    }

    /**
     * Sets the color used for drawing the bar-shadows. The bar shadows is a
     * surface behind the bar that indicates the maximum value. Don't for get to
     * use getResources().getColor(...) to set this. Or Color.rgb(...).
     */
    public void setBarShadowColor(@ColorInt int color) {
        mBarShadowColor = color;
    }

    @Override
    @ColorInt
    public int getBarShadowColor() {
        return mBarShadowColor;
    }

    /**
     * Sets the width used for drawing borders around the bars.
     * If borderWidth == 0, no border will be drawn.
     */
    public void setBarBorderWidth(float width) {
        mBarBorderWidth = width;
    }

    /**
     * Returns the width used for drawing borders around the bars.
     * If borderWidth == 0, no border will be drawn.
     */
    @Override
    public float getBarBorderWidth() {
        return mBarBorderWidth;
    }

    /**
     * Sets the color drawing borders around the bars.
     */
    public void setBarBorderColor(@ColorInt int color) {
        mBarBorderColor = color;
    }

    /**
     * Returns the color drawing borders around the bars.
     */
    @Override
    @ColorInt
    public int getBarBorderColor() {
        return mBarBorderColor;
    }

    /**
     * Set the alpha value (transparency) that is used for drawing the highlight
     * indicator bar. min = 0 (fully transparent), max = 255 (fully opaque)
     */
    public void setHighLightAlpha(int alpha) {
        mHighLightAlpha = alpha;
    }

    @Override
    public int getHighLightAlpha() {
        return mHighLightAlpha;
    }

    /**
     * Sets labels for different values of bar-stacks, in case there are one.
     */
    public void setStackLabels(@NotNull String[] labels) {
        mStackLabels = labels;
    }

    @Override
    @NotNull
    public String[] getStackLabels() {
        return mStackLabels;
    }
}
