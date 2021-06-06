package com.github.mikephil.charting.data;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;

import androidx.annotation.ColorInt;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Philipp Jahoda on 21/10/15.
 * This is the base dataset of all DataSets. It's purpose is to implement critical methods
 * provided by the IDataSet interface.
 */
public abstract class BaseDataSet<T extends Entry> implements IDataSet<T> {
    /**
     * List representing all colors that are used for this DataSet
     */
    @NotNull
    protected List<Integer> mColors;

    /**
     * List representing all colors that are used for drawing the actual values for this DataSet
     */
    @NotNull
    protected List<Integer> mValueColors;

    /**
     * label that describes the DataSet or the data the DataSet represents
     */
    @Nullable
    private String mLabel = "DataSet";

    /**
     * this specifies which axis this DataSet should be plotted against
     */
    @NotNull
    protected YAxis.AxisDependency mAxisDependency = YAxis.AxisDependency.LEFT;

    /**
     * if true, value highlightning is enabled
     */
    protected boolean mHighlightEnabled = true;

    /**
     * custom formatter that is used instead of the auto-formatter if set
     */
    @Nullable
    protected transient IValueFormatter mValueFormatter;

    /**
     * the typeface used for the value text
     */
    @Nullable
    protected Typeface mValueTypeface;

    @Legend.LegendForm
    private int mForm = Legend.FORM_DEFAULT;
    private float mFormSize = Float.NaN;
    private float mFormLineWidth = Float.NaN;

    @Nullable
    private DashPathEffect mFormLineDashEffect = null;

    /**
     * if true, y-values are drawn on the chart
     */
    protected boolean mDrawValues = true;

    /**
     * if true, y-icons are drawn on the chart
     */
    protected boolean mDrawIcons = true;

    /**
     * the offset for drawing icons (in dp)
     */
    @NotNull
    protected MPPointF mIconsOffset = new MPPointF();

    /**
     * the size of the value-text labels
     */
    protected float mValueTextSize = 17f;

    /**
     * flag that indicates if the DataSet is visible or not
     */
    protected boolean mVisible = true;

    /**
     * Default constructor.
     */
    public BaseDataSet() {
        mColors = new ArrayList<>();
        mValueColors = new ArrayList<>();

        // default color
        mColors.add(Color.rgb(140, 234, 255));
        mValueColors.add(Color.BLACK);
    }

    /**
     * Constructor with label.
     */
    public BaseDataSet(@Nullable String label) {
        this();
        this.mLabel = label;
    }

    /**
     * Use this method to tell the data set that the underlying data has changed.
     */
    public void notifyDataSetChanged() {
        calcMinMax();
    }

    /**
     * ###### ###### COLOR GETTING RELATED METHODS ##### ######
     */

    @Override
    @NotNull
    public List<Integer> getColors() {
        return mColors;
    }

    @NotNull
    public List<Integer> getValueColors() {
        return mValueColors;
    }

    @Override
    @ColorInt
    public int getColor() {
        return mColors.get(0);
    }

    @Override
    @ColorInt
    public int getColor(int index) {
        return mColors.get(index % mColors.size());
    }

     // ###### ###### COLOR SETTING RELATED METHODS ##### ######

    /**
     * Sets the colors that should be used fore this DataSet. Colors are reused
     * as soon as the number of Entries the DataSet represents is higher than
     * the size of the colors array. If you are using colors from the resources,
     * make sure that the colors are already prepared (by calling
     * getResources().getColor(...)) before adding them to the DataSet.

     */
    public void setColors(@NotNull List<Integer> colors) {
        this.mColors = colors;
    }

    /**
     * Sets the colors that should be used fore this DataSet. Colors are reused
     * as soon as the number of Entries the DataSet represents is higher than
     * the size of the colors array. If you are using colors from the resources,
     * make sure that the colors are already prepared (by calling
     * getResources().getColor(...)) before adding them to the DataSet.
     */
    public void setColors(@NotNull int... colors) {
        this.mColors = ColorTemplate.createColors(colors);
    }

    /**
     * Sets the colors that should be used fore this DataSet. Colors are reused
     * as soon as the number of Entries the DataSet represents is higher than
     * the size of the colors array. You can use
     * "new int[] { R.color.red, R.color.green, ... }" to provide colors for
     * this method. Internally, the colors are resolved using
     * getResources().getColor(...)
     */
    public void setColors(@NotNull int[] colors, @NotNull Context c) {
        Resources res = c.getResources();
        mColors.clear();

        for (int color : colors) {
            mColors.add(res.getColor(color));
        }
    }

    /**
     * Adds a new color to the colors array of the DataSet.
     */
    public void addColor(@ColorInt int color) {
        mColors.add(color);
    }

    /**
     * Sets the one and ONLY color that should be used for this DataSet.
     * Internally, this recreates the colors array and adds the specified color.
     */
    public void setColor(@ColorInt int color) {
        resetColors();
        mColors.add(color);
    }

    /**
     * Sets a color with a specific alpha value.
     *
     * @param alpha from 0-255
     */
    public void setColor(@ColorInt int color, int alpha) {
        setColor(Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color)));
    }

    /**
     * Sets colors with a specific alpha value.
     */
    public void setColors(@NotNull int[] colors, int alpha) {
        resetColors();

        for (int color : colors) {
            addColor(Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color)));
        }
    }

    /**
     * Resets all colors of this DataSet and recreates the colors array.
     */
    public void resetColors() {
        mColors.clear();
    }

    /**
     * ###### ###### OTHER STYLING RELATED METHODS ##### ######
     */

    @Override
    public void setLabel(@Nullable String label) {
        mLabel = label;
    }

    @Override
    @Nullable
    public String getLabel() {
        return mLabel;
    }

    @Override
    public void setHighlightEnabled(boolean enabled) {
        mHighlightEnabled = enabled;
    }

    @Override
    public boolean isHighlightEnabled() {
        return mHighlightEnabled;
    }

    @Override
    public void setValueFormatter(@NotNull IValueFormatter f) {
        mValueFormatter = f;
    }

    @Override
    @NotNull
    public IValueFormatter getValueFormatter() {
        if (mValueFormatter == null) {
            return Utils.getDefaultValueFormatter();
        }

        return mValueFormatter;
    }

    @Override
    public boolean needsFormatter() {
        return mValueFormatter == null;
    }

    @Override
    public void setValueTextColor(@ColorInt int color) {
        mValueColors.clear();
        mValueColors.add(color);
    }

    @Override
    public void setValueTextColors(@NotNull List<Integer> colors) {
        mValueColors = colors;
    }

    @Override
    public void setValueTypeface(@Nullable Typeface tf) {
        mValueTypeface = tf;
    }

    @Override
    public void setValueTextSize(float size) {
        mValueTextSize = Utils.convertDpToPixel(size);
    }

    @Override
    @ColorInt
    public int getValueTextColor() {
        return mValueColors.get(0);
    }

    @Override
    @ColorInt
    public int getValueTextColor(int index) {
        return mValueColors.get(index % mValueColors.size());
    }

    @Override
    @Nullable
    public Typeface getValueTypeface() {
        return mValueTypeface;
    }

    @Override
    public float getValueTextSize() {
        return mValueTextSize;
    }

    public void setForm(@Legend.LegendForm int form) {
        mForm = form;
    }

    @Override
    @Legend.LegendForm
    public int getForm() {
        return mForm;
    }

    public void setFormSize(float formSize) {
        mFormSize = formSize;
    }

    @Override
    public float getFormSize() {
        return mFormSize;
    }

    public void setFormLineWidth(float formLineWidth) {
        mFormLineWidth = formLineWidth;
    }

    @Override
    public float getFormLineWidth() {
        return mFormLineWidth;
    }

    public void setFormLineDashEffect(@Nullable DashPathEffect dashPathEffect) {
        mFormLineDashEffect = dashPathEffect;
    }

    @Override
    @Nullable
    public DashPathEffect getFormLineDashEffect() {
        return mFormLineDashEffect;
    }

    @Override
    public void setDrawValues(boolean enabled) {
        this.mDrawValues = enabled;
    }

    @Override
    public boolean isDrawValuesEnabled() {
        return mDrawValues;
    }

    @Override
    public void setDrawIcons(boolean enabled) {
        mDrawIcons = enabled;
    }

    @Override
    public boolean isDrawIconsEnabled() {
        return mDrawIcons;
    }

    @Override
    public void setIconsOffset(@NotNull MPPointF offsetDp) {
        mIconsOffset.x = offsetDp.x;
        mIconsOffset.y = offsetDp.y;
    }

    @Override
    @NotNull
    public MPPointF getIconsOffset() {
        return mIconsOffset;
    }

    @Override
    public void setVisible(boolean visible) {
        mVisible = visible;
    }

    @Override
    public boolean isVisible() {
        return mVisible;
    }

    @Override
    @NotNull
    public YAxis.AxisDependency getAxisDependency() {
        return mAxisDependency;
    }

    @Override
    public void setAxisDependency(@NotNull YAxis.AxisDependency dependency) {
        mAxisDependency = dependency;
    }

    /**
     * ###### ###### DATA RELATED METHODS ###### ######
     */

    @Override
    public int getIndexInEntries(int xIndex) {
        for (int i = 0; i < getEntryCount(); i++) {
            if (xIndex == getEntryForIndex(i).getX()) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public boolean removeFirst() {
        if (getEntryCount() > 0) {
            T entry = getEntryForIndex(0);

            return removeEntry(entry);
        } else {
            return false;
        }
    }

    @Override
    public boolean removeLast() {
        if (getEntryCount() > 0) {
            T e = getEntryForIndex(getEntryCount() - 1);

            return removeEntry(e);
        } else
            return false;
    }

    @Override
    public boolean removeEntryByXValue(float xValue) {
        T e = getEntryForXValue(xValue, Float.NaN);
        if(e == null) {
            return false;
        }

        return removeEntry(e);
    }

    @Override
    public boolean removeEntry(int index) {
        T e = getEntryForIndex(index);

        return removeEntry(e);
    }

    @Override
    public boolean contains(@NotNull T e) {
        for (int i = 0; i < getEntryCount(); i++) {
            if (getEntryForIndex(i).equals(e))
                return true;
        }

        return false;
    }

    protected void copy(@NotNull BaseDataSet<T> baseDataSet) {
        baseDataSet.mAxisDependency = mAxisDependency;
        baseDataSet.mColors = mColors;
        baseDataSet.mDrawIcons = mDrawIcons;
        baseDataSet.mDrawValues = mDrawValues;
        baseDataSet.mForm = mForm;
        baseDataSet.mFormLineDashEffect = mFormLineDashEffect;
        baseDataSet.mFormLineWidth = mFormLineWidth;
        baseDataSet.mFormSize = mFormSize;
        baseDataSet.mHighlightEnabled = mHighlightEnabled;
        baseDataSet.mIconsOffset = mIconsOffset;
        baseDataSet.mValueColors = mValueColors;
        baseDataSet.mValueFormatter = mValueFormatter;
        baseDataSet.mValueTextSize = mValueTextSize;
        baseDataSet.mVisible = mVisible;
    }
}
