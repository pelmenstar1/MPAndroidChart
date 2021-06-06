package com.github.mikephil.charting.components;

import android.graphics.Color;
import android.graphics.Paint;

import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;

import com.github.mikephil.charting.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class representing the y-axis labels settings and its entries. Only use the setter methods to
 * modify it. Do not
 * access public variables directly. Be aware that not all features the YLabels class provides
 * are suitable for the
 * RadarChart. Customizations that affect the value range of the axis need to be applied before
 * setting data for the
 * chart.
 *
 * @author Philipp Jahoda
 */
public class YAxis extends AxisBase {
    /**
     * indicates if the top y-label entry is drawn or not
     */
    private boolean mDrawTopYLabelEntry = true;

    /**
     * flag that indicates if the axis is inverted or not
     */
    protected boolean mInverted = false;

    /**
     * flag that indicates if the zero-line should be drawn regardless of other grid lines
     */
    protected boolean mDrawZeroLine = false;

    /**
     * flag indicating that auto scale min restriction should be used
     */
    private boolean mUseAutoScaleRestrictionMin = false;

    /**
     * flag indicating that auto scale max restriction should be used
     */
    private boolean mUseAutoScaleRestrictionMax = false;

    /**
     * Color of the zero line
     */
    protected int mZeroLineColor = Color.GRAY;

    /**
     * Width of the zero line in pixels
     */
    protected float mZeroLineWidth = 1f;

    /**
     * axis space from the largest value to the top in percent of the total axis range
     */
    protected float mSpacePercentTop = 10f;

    /**
     * axis space from the smallest value to the bottom in percent of the total axis range
     */
    protected float mSpacePercentBottom = 10f;

    /**
     * the position of the y-labels relative to the chart
     */
    @YAxisLabelPosition
    private int mPosition = LABEL_POSITION_OUTSIDE_CHART;

    /**
     * the horizontal offset of the y-label
     */
    private float mXLabelOffset = 0.0f;

    /**
     * the side this axis object represents
     */
    @AxisDependency
    private final int mAxisDependency;

    /**
     * the minimum width that the axis should take (in dp).
     * <p/>
     * default: 0.0
     */
    protected float mMinWidth = 0.f;

    /**
     * the maximum width that the axis can take (in dp).
     * use Inifinity for disabling the maximum
     * default: Float.POSITIVE_INFINITY (no maximum specified)
     */
    protected float mMaxWidth = Float.POSITIVE_INFINITY;

    public static final int LABEL_POSITION_OUTSIDE_CHART = 0;
    public static final int LABEL_POSITION_INSIDE_CHART = 1;

    @IntDef({ LABEL_POSITION_OUTSIDE_CHART, LABEL_POSITION_INSIDE_CHART })
    @Retention(RetentionPolicy.SOURCE)
    @Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
    public @interface YAxisLabelPosition {
    }

    public static final int DEPENDENCY_LEFT = 0;
    public static final int DEPENDENCY_RIGHT = 1;

    @IntDef({ DEPENDENCY_LEFT, DEPENDENCY_RIGHT })
    @Retention(RetentionPolicy.SOURCE)
    @Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
    public @interface AxisDependency {
    }

    public YAxis() {
        // default left
        this.mAxisDependency = DEPENDENCY_LEFT;
        this.mYOffset = 0f;
    }

    public YAxis(@AxisDependency int position) {
        this.mAxisDependency = position;
        this.mYOffset = 0f;
    }

    @AxisDependency
    public int getAxisDependency() {
        return mAxisDependency;
    }

    /**
     * @return the minimum width that the axis should take (in dp).
     */
    public float getMinWidth() {
        return mMinWidth;
    }

    /**
     * Sets the minimum width that the axis should take (in dp).
     */
    public void setMinWidth(float minWidth) {
        mMinWidth = minWidth;
    }

    /**
     * @return the maximum width that the axis can take (in dp).
     */
    public float getMaxWidth() {
        return mMaxWidth;
    }

    /**
     * Sets the maximum width that the axis can take (in dp).
     */
    public void setMaxWidth(float maxWidth) {
        mMaxWidth = maxWidth;
    }

    /**
     * returns the position of the y-labels
     */
    @YAxisLabelPosition
    public int getLabelPosition() {
        return mPosition;
    }

    /**
     * sets the position of the y-labels
     */
    public void setPosition(@YAxisLabelPosition int pos) {
        mPosition = pos;
    }

    /**
     * returns the horizontal offset of the y-label
     */
    public float getLabelXOffset() {
        return mXLabelOffset;
    }

    /**
     * sets the horizontal offset of the y-label
     */
    public void setLabelXOffset(float xOffset) {
        mXLabelOffset = xOffset;
    }

    /**
     * returns true if drawing the top y-axis label entry is enabled
     */
    public boolean isDrawTopYLabelEntryEnabled() {
        return mDrawTopYLabelEntry;
    }

    /**
     * returns true if drawing the bottom y-axis label entry is enabled
     */
    public boolean isDrawBottomYLabelEntryEnabled() {
        return true;
    }

    /**
     * set this to true to enable drawing the top y-label entry. Disabling this can be helpful
     * when the top y-label and
     * left x-label interfere with each other. default: true
     */
    public void setDrawTopYLabelEntry(boolean enabled) {
        mDrawTopYLabelEntry = enabled;
    }

    /**
     * If this is set to true, the y-axis is inverted which means that low values are on top of
     * the chart, high values
     * on bottom.
     */
    public void setInverted(boolean enabled) {
        mInverted = enabled;
    }

    /**
     * If this returns true, the y-axis is inverted.
     */
    public boolean isInverted() {
        return mInverted;
    }

    /**
     * This method is deprecated.
     * Use setAxisMinimum(...) / setAxisMaximum(...) instead.
     */
    @Deprecated
    public void setStartAtZero(boolean startAtZero) {
        if (startAtZero)
            setAxisMinimum(0f);
        else
            resetAxisMinimum();
    }

    /**
     * Sets the top axis space in percent of the full range. Default 10f
     */
    public void setSpaceTop(float percent) {
        mSpacePercentTop = percent;
    }

    /**
     * Returns the top axis space in percent of the full range. Default 10f
     */
    public float getSpaceTop() {
        return mSpacePercentTop;
    }

    /**
     * Sets the bottom axis space in percent of the full range. Default 10f
     */
    public void setSpaceBottom(float percent) {
        mSpacePercentBottom = percent;
    }

    /**
     * Returns the bottom axis space in percent of the full range. Default 10f
     */
    public float getSpaceBottom() {
        return mSpacePercentBottom;
    }

    public boolean isDrawZeroLineEnabled() {
        return mDrawZeroLine;
    }

    /**
     * Set this to true to draw the zero-line regardless of weather other
     * grid-lines are enabled or not. Default: false
     */
    public void setDrawZeroLine(boolean mDrawZeroLine) {
        this.mDrawZeroLine = mDrawZeroLine;
    }

    @ColorInt
    public int getZeroLineColor() {
        return mZeroLineColor;
    }

    /**
     * Sets the color of the zero line
     */
    public void setZeroLineColor(@ColorInt int color) {
        mZeroLineColor = color;
    }

    public float getZeroLineWidth() {
        return mZeroLineWidth;
    }

    /**
     * Sets the width of the zero line in dp
     */
    public void setZeroLineWidth(float width) {
        this.mZeroLineWidth = Utils.convertDpToPixel(width);
    }

    /**
     * This is for normal (not horizontal) charts horizontal spacing.
     */
    public float getRequiredWidthSpace(@NotNull Paint p) {
        p.setTextSize(mTextSize);

        String label = getLongestLabel();
        float width = (float) Utils.calcTextWidth(p, label) + getXOffset() * 2f;

        float minWidth = getMinWidth();
        float maxWidth = getMaxWidth();

        if (minWidth > 0.f)
            minWidth = Utils.convertDpToPixel(minWidth);

        if (maxWidth > 0.f && maxWidth != Float.POSITIVE_INFINITY)
            maxWidth = Utils.convertDpToPixel(maxWidth);

        width = Math.max(minWidth, Math.min(width, maxWidth > 0.0 ? maxWidth : width));

        return width;
    }

    /**
     * This is for HorizontalBarChart vertical spacing.
     */
    public float getRequiredHeightSpace(@NotNull Paint p) {
        p.setTextSize(mTextSize);

        String label = getLongestLabel();
        return (float) Utils.calcTextHeight(p, label) + getYOffset() * 2f;
    }

    /**
     * Returns true if this axis needs horizontal offset, false if no offset is needed.
     */
    public boolean needsOffset() {
        return isEnabled() && isDrawLabelsEnabled() && mPosition == LABEL_POSITION_OUTSIDE_CHART;
    }

    /**
     * Returns true if autoscale restriction for axis min value is enabled
     */
    @Deprecated
    public boolean isUseAutoScaleMinRestriction( ) {
        return mUseAutoScaleRestrictionMin;
    }

    /**
     * Sets autoscale restriction for axis min value as enabled/disabled
     */
    @Deprecated
    public void setUseAutoScaleMinRestriction( boolean isEnabled ) {
        mUseAutoScaleRestrictionMin = isEnabled;
    }

    /**
     * Returns true if autoscale restriction for axis max value is enabled
     */
    @Deprecated
    public boolean isUseAutoScaleMaxRestriction() {
        return mUseAutoScaleRestrictionMax;
    }

    /**
     * Sets autoscale restriction for axis max value as enabled/disabled
     */
    @Deprecated
    public void setUseAutoScaleMaxRestriction( boolean isEnabled ) {
        mUseAutoScaleRestrictionMax = isEnabled;
    }

    @Override
    public void calculate(float dataMin, float dataMax) {
        float min = dataMin;
        float max = dataMax;

        // Make sure max is greater than min
        // Discussion: https://github.com/danielgindi/Charts/pull/3650#discussion_r221409991
        if (min > max) {
            if (mCustomAxisMax && mCustomAxisMin) {
                float t = min;
                min = max;
                max = t;
            } else if (mCustomAxisMax) {
                min = max < 0f ? max * 1.5f : max * 0.5f;
            } else if (mCustomAxisMin) {
                max = min < 0f ? min * 0.5f : min * 1.5f;
            }
        }

        float range = Math.abs(max - min);

        // in case all values are equal
        if (range == 0f) {
            max = max + 1f;
            min = min - 1f;
        }

        // recalculate
        range = Math.abs(max - min);

        // calc extra spacing
        this.mAxisMinimum = mCustomAxisMin ? this.mAxisMinimum : min - (range / 100f) * getSpaceBottom();
        this.mAxisMaximum = mCustomAxisMax ? this.mAxisMaximum : max + (range / 100f) * getSpaceTop();

        this.mAxisRange = Math.abs(this.mAxisMinimum - this.mAxisMaximum);
    }
}
