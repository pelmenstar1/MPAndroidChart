
package com.github.mikephil.charting.data;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;

import com.github.mikephil.charting.interfaces.datasets.ILineRadarDataSet;
import com.github.mikephil.charting.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Base dataset for line and radar DataSets.
 *
 * @author Philipp Jahoda
 */
public abstract class LineRadarDataSet<T extends Entry> extends LineScatterCandleRadarDataSet<T> implements ILineRadarDataSet<T> {
    /**
     * the color that is used for filling the line surface
     */
    @ColorInt
    private int mFillColor = Color.rgb(140, 234, 255);

    /**
     * the drawable to be used for filling the line surface
     */
    @Nullable
    protected Drawable mFillDrawable;

    /**
     * transparency used for filling line surface
     */
    private int mFillAlpha = 85;

    /**
     * the width of the drawn data lines
     */
    private float mLineWidth = 2.5f;

    /**
     * if true, the data will also be drawn filled
     */
    private boolean mDrawFilled = false;

    public LineRadarDataSet(@NotNull List<T> entries, @Nullable String label) {
        super(entries, label);
    }

    @Override
    @ColorInt
    public int getFillColor() {
        return mFillColor;
    }

    /**
     * Sets the color that is used for filling the area below the line.
     * Resets an eventually set "fillDrawable".
     */
    public void setFillColor(@ColorInt int color) {
        mFillColor = color;
        mFillDrawable = null;
    }

    @Override
    @Nullable
    public Drawable getFillDrawable() {
        return mFillDrawable;
    }

    /**
     * Sets the drawable to be used to fill the area below the line.
     */
    @TargetApi(18)
    public void setFillDrawable(@Nullable Drawable drawable) {
        this.mFillDrawable = drawable;
    }

    @Override
    public int getFillAlpha() {
        return mFillAlpha;
    }

    /**
     * sets the alpha value (transparency) that is used for filling the line
     * surface (0-255), default: 85
     */
    public void setFillAlpha(int alpha) {
        mFillAlpha = alpha;
    }

    /**
     * set the line width of the chart (min = 0.2f, max = 10f); default 1f NOTE:
     * thinner line == better performance, thicker line == worse performance
     */
    public void setLineWidth(float width) {
        if (width < 0.0f)
            width = 0.0f;
        if (width > 10.0f)
            width = 10.0f;

        mLineWidth = Utils.convertDpToPixel(width);
    }

    @Override
    public float getLineWidth() {
        return mLineWidth;
    }

    @Override
    public void setDrawFilled(boolean filled) {
        mDrawFilled = filled;
    }

    @Override
    public boolean isDrawFilledEnabled() {
        return mDrawFilled;
    }

    protected void copy(@NotNull LineRadarDataSet<T> lineRadarDataSet) {
        super.copy(lineRadarDataSet);

        lineRadarDataSet.mDrawFilled = mDrawFilled;
        lineRadarDataSet.mFillAlpha = mFillAlpha;
        lineRadarDataSet.mFillColor = mFillColor;
        lineRadarDataSet.mFillDrawable = mFillDrawable;
        lineRadarDataSet.mLineWidth = mLineWidth;
    }
}
