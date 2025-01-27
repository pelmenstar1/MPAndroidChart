
package com.github.mikephil.charting.data;

import android.graphics.Color;

import androidx.annotation.ColorInt;

import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Baseclass of all DataSets for Bar-, Line-, Scatter- and CandleStickChart.
 *
 * @author Philipp Jahoda
 */
public abstract class BarLineScatterCandleBubbleDataSet<T extends Entry>
        extends DataSet<T>
        implements IBarLineScatterCandleBubbleDataSet<T> {
    /**
     * default highlight color
     */
    protected int mHighLightColor = Color.rgb(255, 187, 115);

    public BarLineScatterCandleBubbleDataSet(@NotNull List<T> entries, @Nullable String label) {
        super(entries, label);
    }

    /**
     * Sets the color that is used for drawing the highlight indicators. Dont
     * forget to resolve the color using getResources().getColor(...) or
     * Color.rgb(...).
     */
    public void setHighLightColor(@ColorInt int color) {
        mHighLightColor = color;
    }

    @Override
    @ColorInt
    public int getHighLightColor() {
        return mHighLightColor;
    }

    protected void copy(@NotNull BarLineScatterCandleBubbleDataSet<T> dataSet) {
        super.copy(dataSet);
        dataSet.mHighLightColor = mHighLightColor;
    }
}
