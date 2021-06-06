package com.github.mikephil.charting.interfaces.datasets;

import android.graphics.Paint;

import androidx.annotation.ColorInt;

import com.github.mikephil.charting.data.CandleEntry;

import org.jetbrains.annotations.NotNull;

/**
 * Created by philipp on 21/10/15.
 */
public interface ICandleDataSet extends ILineScatterCandleRadarDataSet<CandleEntry> {
    /**
     * Returns the space that is left out on the left and right side of each
     * candle.
     */
    float getBarSpace();

    /**
     * Returns whether the candle bars should show?
     * When false, only "ticks" will show
     *
     * - default: true
     */
    boolean getShowCandleBar();

    /**
     * Returns the width of the candle-shadow-line in pixels.
     */
    float getShadowWidth();

    /**
     * Returns shadow color for all entries
     */
    @ColorInt
    int getShadowColor();

    /**
     * Returns the neutral color (for open == close)
     */
    @ColorInt
    int getNeutralColor();

    /**
     * Returns the increasing color (for open < close).
     */
    @ColorInt
    int getIncreasingColor();

    /**
     * Returns the decreasing color (for open > close).
     */
    @ColorInt
    int getDecreasingColor();

    /**
     * Returns paint style when open < close
     */
    @NotNull
    Paint.Style getIncreasingPaintStyle();

    /**
     * Returns paint style when open > close
     */
    @NotNull
    Paint.Style getDecreasingPaintStyle();

    /**
     * Is the shadow color same as the candle color?
     */
    boolean getShadowColorSameAsCandle();
}
