
package com.github.mikephil.charting.components;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Typeface;

import androidx.annotation.IntDef;

import com.github.mikephil.charting.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The limit line is an additional feature for all Line-, Bar- and
 * ScatterCharts. It allows the displaying of an additional line in the chart
 * that marks a certain maximum / limit on the specified axis (x- or y-axis).
 * 
 * @author Philipp Jahoda
 */
public class LimitLine extends ComponentBase {
    /** limit / maximum (the y-value or xIndex) */
    private final float mLimit;

    /** the width of the limit line */
    private float mLineWidth = 2f;

    /** the color of the limit line */
    private int mLineColor = Color.rgb(237, 91, 91);

    /** the style of the label text */
    private Paint.Style mTextStyle = Paint.Style.FILL_AND_STROKE;

    /** label string that is drawn next to the limit line */
    private String mLabel = "";

    /** the path effect of this LimitLine that makes dashed lines possible */
    private DashPathEffect mDashPathEffect = null;

    /** indicates the position of the LimitLine label */
    private int mLabelPosition = LABEL_POSITION_RIGHT_TOP;

    public static final int LABEL_POSITION_LEFT_TOP = 0;
    public static final int LABEL_POSITION_LEFT_BOTTOM = 1;
    public static final int LABEL_POSITION_RIGHT_TOP = 2;
    public static final int LABEL_POSITION_RIGHT_BOTTOM = 3;

    @IntDef({ LABEL_POSITION_LEFT_TOP, LABEL_POSITION_LEFT_BOTTOM, LABEL_POSITION_RIGHT_TOP, LABEL_POSITION_RIGHT_BOTTOM })
    @Retention(RetentionPolicy.SOURCE)
    @Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
    public @interface LimitLabelPosition {
    }

    /**
     * Constructor with limit.
     * 
     * @param limit - the position (the value) on the y-axis (y-value) or x-axis
     *            (xIndex) where this line should appear
     */
    public LimitLine(float limit) {
        mLimit = limit;
    }

    /**
     * Constructor with limit and label.
     * 
     * @param limit - the position (the value) on the y-axis (y-value) or x-axis
     *            (xIndex) where this line should appear
     * @param label - provide "" if no label is required
     */
    public LimitLine(float limit, @NotNull String label) {
        mLimit = limit;
        mLabel = label;
    }

    /**
     * Returns the limit that is set for this line.
     */
    public float getLimit() {
        return mLimit;
    }

    /**
     * set the line width of the chart (min = 0.2f, max = 12f); default 2f NOTE:
     * thinner line == better performance, thicker line == worse performance
     */
    public void setLineWidth(float width) {
        if (width < 0.2f)
            width = 0.2f;
        if (width > 12.0f)
            width = 12.0f;

        mLineWidth = Utils.convertDpToPixel(width);
    }

    /**
     * returns the width of limit line
     */
    public float getLineWidth() {
        return mLineWidth;
    }

    /**
     * Sets the linecolor for this LimitLine. Make sure to use
     * getResources().getColor(...)
     */
    public void setLineColor(int color) {
        mLineColor = color;
    }

    /**
     * Returns the color that is used for this LimitLine
     */
    public int getLineColor() {
        return mLineColor;
    }

    /**
     * Enables the line to be drawn in dashed mode, e.g. like this "- - - - - -"
     * 
     * @param lineLength the length of the line pieces
     * @param spaceLength the length of space inbetween the pieces
     * @param phase offset, in degrees (normally, use 0)
     */
    public void enableDashedLine(float lineLength, float spaceLength, float phase) {
        mDashPathEffect = new DashPathEffect(new float[] {
                lineLength, spaceLength
        }, phase);
    }

    /**
     * Disables the line to be drawn in dashed mode.
     */
    public void disableDashedLine() {
        mDashPathEffect = null;
    }

    /**
     * Returns true if the dashed-line effect is enabled, false if not. Default:
     * disabled
     */
    public boolean isDashedLineEnabled() {
        return mDashPathEffect != null;
    }

    /**
     * returns the DashPathEffect that is set for this LimitLine
     */
    @Nullable
    public DashPathEffect getDashPathEffect() {
        return mDashPathEffect;
    }

    /**
     * Sets the color of the value-text that is drawn next to the LimitLine.
     * Default: Paint.Style.FILL_AND_STROKE
     */
    public void setTextStyle(@NotNull Paint.Style style) {
        this.mTextStyle = style;
    }

    /**
     * Returns the color of the value-text that is drawn next to the LimitLine.
     */
    @NotNull
    public Paint.Style getTextStyle() {
        return mTextStyle;
    }

    /**
     * Sets the position of the LimitLine value label (either on the right or on
     * the left edge of the chart). Not supported for RadarChart.
     */
    public void setLabelPosition(@LimitLabelPosition int pos) {
        mLabelPosition = pos;
    }

    /**
     * Returns the position of the LimitLine label (value).
     */
    @LimitLabelPosition
    public int getLabelPosition() {
        return mLabelPosition;
    }

    /**
     * Sets the label that is drawn next to the limit line. Provide "" if no
     * label is required.
     */
    public void setLabel(@NotNull String label) {
        mLabel = label;
    }

    /**
     * Returns the label that is drawn next to the limit line.
     */
    @NotNull
    public String getLabel() {
        return mLabel;
    }
}
