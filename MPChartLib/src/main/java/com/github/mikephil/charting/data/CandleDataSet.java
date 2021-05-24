
package com.github.mikephil.charting.data;

import android.graphics.Paint;

import androidx.annotation.ColorInt;

import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * DataSet for the CandleStickChart.
 *
 * @author Philipp Jahoda
 */
public class CandleDataSet extends LineScatterCandleRadarDataSet<CandleEntry> implements ICandleDataSet {
    /**
     * the width of the shadow of the candle
     */
    private float mShadowWidth = 3f;

    /**
     * should the candle bars show?
     * when false, only "ticks" will show
     * <p/>
     * - default: true
     */
    private boolean mShowCandleBar = true;

    /**
     * the space between the candle entries, default 0.1f (10%)
     */
    private float mBarSpace = 0.1f;

    /**
     * use candle color for the shadow
     */
    private boolean mShadowColorSameAsCandle = false;

    /**
     * paint style when open < close
     * increasing candlesticks are traditionally hollow
     */
    @NotNull
    protected Paint.Style mIncreasingPaintStyle = Paint.Style.STROKE;

    /**
     * paint style when open > close
     * descreasing candlesticks are traditionally filled
     */
    @NotNull
    protected Paint.Style mDecreasingPaintStyle = Paint.Style.FILL;

    /**
     * color for open == close
     */
    @ColorInt
    protected int mNeutralColor = ColorTemplate.COLOR_SKIP;

    /**
     * color for open < close
     */
    @ColorInt
    protected int mIncreasingColor = ColorTemplate.COLOR_SKIP;

    /**
     * color for open > close
     */
    @ColorInt
    protected int mDecreasingColor = ColorTemplate.COLOR_SKIP;

    /**
     * shadow line color, set -1 for backward compatibility and uses default
     * color
     */
    @ColorInt
    protected int mShadowColor = ColorTemplate.COLOR_SKIP;

    public CandleDataSet(@NotNull List<CandleEntry> entries, @Nullable String label) {
        super(entries, label);
    }

    @Override
    @NotNull
    public DataSet<CandleEntry> copy() {
        ArrayList<CandleEntry> entries = new ArrayList<>();
        for (int i = 0; i < mEntries.size(); i++) {
            entries.add(mEntries.get(i).copy());
        }

        CandleDataSet copied = new CandleDataSet(entries, getLabel());
        copy(copied);
        return copied;
    }

    protected void copy(@NotNull CandleDataSet candleDataSet) {
        super.copy(candleDataSet);

        candleDataSet.mShadowWidth = mShadowWidth;
        candleDataSet.mShowCandleBar = mShowCandleBar;
        candleDataSet.mBarSpace = mBarSpace;
        candleDataSet.mShadowColorSameAsCandle = mShadowColorSameAsCandle;
        candleDataSet.mHighLightColor = mHighLightColor;
        candleDataSet.mIncreasingPaintStyle = mIncreasingPaintStyle;
        candleDataSet.mDecreasingPaintStyle = mDecreasingPaintStyle;
        candleDataSet.mNeutralColor = mNeutralColor;
        candleDataSet.mIncreasingColor = mIncreasingColor;
        candleDataSet.mDecreasingColor = mDecreasingColor;
        candleDataSet.mShadowColor = mShadowColor;
    }

    @Override
    protected void calcMinMax(@NotNull CandleEntry e) {
        if (e.getLow() < mYMin)
            mYMin = e.getLow();

        if (e.getHigh() > mYMax)
            mYMax = e.getHigh();

        calcMinMaxX(e);
    }

    @Override
    protected void calcMinMaxY(@NotNull CandleEntry e) {
        if (e.getHigh() < mYMin)
            mYMin = e.getHigh();

        if (e.getHigh() > mYMax)
            mYMax = e.getHigh();

        if (e.getLow() < mYMin)
            mYMin = e.getLow();

        if (e.getLow() > mYMax)
            mYMax = e.getLow();
    }

    /**
     * Sets the space that is left out on the left and right side of each
     * candle, default 0.1f (10%), max 0.45f, min 0f
     */
    public void setBarSpace(float space) {
        if (space < 0f)
            space = 0f;
        if (space > 0.45f)
            space = 0.45f;

        mBarSpace = space;
    }

    @Override
    public float getBarSpace() {
        return mBarSpace;
    }

    /**
     * Sets the width of the candle-shadow-line in pixels. Default 3f.
     */
    public void setShadowWidth(float width) {
        mShadowWidth = Utils.convertDpToPixel(width);
    }

    @Override
    public float getShadowWidth() {
        return mShadowWidth;
    }

    /**
     * Sets whether the candle bars should show?
     */
    public void setShowCandleBar(boolean showCandleBar) {
        mShowCandleBar = showCandleBar;
    }

    @Override
    public boolean getShowCandleBar() {
        return mShowCandleBar;
    }

    // BELOW THIS COLOR HANDLING

    /**
     * Sets the one and ONLY color that should be used for this DataSet when
     * open == close.
     */
    public void setNeutralColor(@ColorInt int color) {
        mNeutralColor = color;
    }

    @Override
    @ColorInt
    public int getNeutralColor() {
        return mNeutralColor;
    }

    /**
     * Sets the one and ONLY color that should be used for this DataSet when
     * open <= close.
     */
    public void setIncreasingColor(@ColorInt int color) {
        mIncreasingColor = color;
    }

    @Override
    @ColorInt
    public int getIncreasingColor() {
        return mIncreasingColor;
    }

    /**
     * Sets the one and ONLY color that should be used for this DataSet when
     * open > close.
     */
    public void setDecreasingColor(@ColorInt int color) {
        mDecreasingColor = color;
    }

    @Override
    @ColorInt
    public int getDecreasingColor() {
        return mDecreasingColor;
    }

    @Override
    @NotNull
    public Paint.Style getIncreasingPaintStyle() {
        return mIncreasingPaintStyle;
    }

    /**
     * Sets paint style when open < close
     */
    public void setIncreasingPaintStyle(@NotNull Paint.Style paintStyle) {
        this.mIncreasingPaintStyle = paintStyle;
    }

    @Override
    @NotNull
    public Paint.Style getDecreasingPaintStyle() {
        return mDecreasingPaintStyle;
    }

    /**
     * Sets paint style when open > close
     */
    public void setDecreasingPaintStyle(@NotNull Paint.Style decreasingPaintStyle) {
        this.mDecreasingPaintStyle = decreasingPaintStyle;
    }

    @Override
    @ColorInt
    public int getShadowColor() {
        return mShadowColor;
    }

    /**
     * Sets shadow color for all entries
     */
    public void setShadowColor(@ColorInt int shadowColor) {
        this.mShadowColor = shadowColor;
    }

    @Override
    public boolean getShadowColorSameAsCandle() {
        return mShadowColorSameAsCandle;
    }

    /**
     * Sets shadow color to be the same color as the candle color
     */
    public void setShadowColorSameAsCandle(boolean state) {
        this.mShadowColorSameAsCandle = state;
    }
}
