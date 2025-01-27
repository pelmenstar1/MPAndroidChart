
package com.github.mikephil.charting.data;

import androidx.annotation.IntDef;

import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

public class PieDataSet extends DataSet<PieEntry> implements IPieDataSet {
    /**
     * the space in pixels between the chart-slices, default 0f
     */
    private float mSliceSpace = 0f;
    private boolean mAutomaticallyDisableSliceSpacing;

    /**
     * indicates the selection distance of a pie slice
     */
    private float mShift = 18f;

    @ValuePosition
    private int mXValuePosition = VALUE_POSITION_INSIDE_SLICE;

    @ValuePosition
    private int mYValuePosition = VALUE_POSITION_INSIDE_SLICE;
    private int mValueLineColor = 0xff000000;
    private boolean mUseValueColorForLine = false;
    private float mValueLineWidth = 1.0f;
    private float mValueLinePart1OffsetPercentage = 75.f;
    private float mValueLinePart1Length = 0.3f;
    private float mValueLinePart2Length = 0.4f;
    private boolean mValueLineVariableLength = true;

    @Nullable
    private Integer mHighlightColor = null;

    public PieDataSet(@NotNull List<PieEntry> entries, @Nullable String label) {
        super(entries, label);
//        mShift = Utils.convertDpToPixel(12f);
    }

    @Override
    @NotNull
    public DataSet<PieEntry> copy() {
        List<PieEntry> entries = new ArrayList<>();
        for (int i = 0; i < mEntries.size(); i++) {
            entries.add(mEntries.get(i).copy());
        }

        PieDataSet copied = new PieDataSet(entries, getLabel());
        copy(copied);
        return copied;
    }

    protected void copy(@NotNull PieDataSet pieDataSet) {
        super.copy(pieDataSet);
    }

    @Override
    protected void calcMinMax(@NotNull PieEntry e) {
        calcMinMaxY(e);
    }

    /**
     * Sets the space that is left out between the piechart-slices in dp.
     * Default: 0 --> no space, maximum 20f
     */
    public void setSliceSpace(float spaceDp) {

        if (spaceDp > 20)
            spaceDp = 20f;
        if (spaceDp < 0)
            spaceDp = 0f;

        mSliceSpace = Utils.convertDpToPixel(spaceDp);
    }

    @Override
    public float getSliceSpace() {
        return mSliceSpace;
    }

    /**
     * When enabled, slice spacing will be 0.0 when the smallest value is going to be
     * smaller than the slice spacing itself.
     */
    public void setAutomaticallyDisableSliceSpacing(boolean autoDisable) {
        mAutomaticallyDisableSliceSpacing = autoDisable;
    }

    /**
     * When enabled, slice spacing will be 0.0 when the smallest value is going to be
     * smaller than the slice spacing itself.
     */
    @Override
    public boolean isAutomaticallyDisableSliceSpacingEnabled() {
        return mAutomaticallyDisableSliceSpacing;
    }

    /**
     * sets the distance the highlighted piechart-slice of this DataSet is
     * "shifted" away from the center of the chart, default 12f
     */
    public void setSelectionShift(float shift) {
        mShift = Utils.convertDpToPixel(shift);
    }

    @Override
    public float getSelectionShift() {
        return mShift;
    }

    @Override
    @ValuePosition
    public int getXValuePosition() {
        return mXValuePosition;
    }

    public void setXValuePosition(@ValuePosition int xValuePosition) {
        this.mXValuePosition = xValuePosition;
    }

    @Override
    @ValuePosition
    public int getYValuePosition() {
        return mYValuePosition;
    }

    public void setYValuePosition(@ValuePosition int yValuePosition) {
        this.mYValuePosition = yValuePosition;
    }

    /**
     * This method is deprecated.
     * Use isUseValueColorForLineEnabled() instead.
     */
    @Deprecated
    public boolean isUsingSliceColorAsValueLineColor() {
        return isUseValueColorForLineEnabled();
    }

    /**
     * This method is deprecated.
     * Use setUseValueColorForLine(...) instead.
     */
    @Deprecated
    public void setUsingSliceColorAsValueLineColor(boolean enabled) {
        setUseValueColorForLine(enabled);
    }

    /**
     * When valuePosition is OutsideSlice, indicates line color
     */
    @Override
    public int getValueLineColor() {
        return mValueLineColor;
    }

    public void setValueLineColor(int valueLineColor) {
        this.mValueLineColor = valueLineColor;
    }

    @Override
    public boolean isUseValueColorForLineEnabled()
    {
        return mUseValueColorForLine;
    }

    public void setUseValueColorForLine(boolean enabled)
    {
        mUseValueColorForLine = enabled;
    }

    /**
     * When valuePosition is OutsideSlice, indicates line width
     */
    @Override
    public float getValueLineWidth() {
        return mValueLineWidth;
    }

    public void setValueLineWidth(float valueLineWidth) {
        this.mValueLineWidth = valueLineWidth;
    }

    /**
     * When valuePosition is OutsideSlice, indicates offset as percentage out of the slice size
     */
    @Override
    public float getValueLinePart1OffsetPercentage() {
        return mValueLinePart1OffsetPercentage;
    }

    public void setValueLinePart1OffsetPercentage(float valueLinePart1OffsetPercentage) {
        this.mValueLinePart1OffsetPercentage = valueLinePart1OffsetPercentage;
    }

    /**
     * When valuePosition is OutsideSlice, indicates length of first half of the line
     */
    @Override
    public float getValueLinePart1Length() {
        return mValueLinePart1Length;
    }

    public void setValueLinePart1Length(float valueLinePart1Length) {
        this.mValueLinePart1Length = valueLinePart1Length;
    }

    /**
     * When valuePosition is OutsideSlice, indicates length of second half of the line
     */
    @Override
    public float getValueLinePart2Length() {
        return mValueLinePart2Length;
    }

    public void setValueLinePart2Length(float valueLinePart2Length) {
        this.mValueLinePart2Length = valueLinePart2Length;
    }

    /**
     * When valuePosition is OutsideSlice, this allows variable line length
     */
    @Override
    public boolean isValueLineVariableLength() {
        return mValueLineVariableLength;
    }

    public void setValueLineVariableLength(boolean valueLineVariableLength) {
        this.mValueLineVariableLength = valueLineVariableLength;
    }

    /** Gets the color for the highlighted sector */
    @Override
    @Nullable
    public Integer getHighlightColor()
    {
        return mHighlightColor;
    }

    /** Sets the color for the highlighted sector (null for using entry color) */
    public void setHighlightColor(@Nullable Integer color)
    {
        this.mHighlightColor = color;
    }

    public static final int VALUE_POSITION_INSIDE_SLICE = 0;
    public static final int VALUE_POSITION_OUTSIDE_SLICE = 1;

    @IntDef({ VALUE_POSITION_INSIDE_SLICE, VALUE_POSITION_OUTSIDE_SLICE })
    @Retention(RetentionPolicy.SOURCE)
    @Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
    public @interface ValuePosition {
    }
}
