
package com.github.mikephil.charting.data;

import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Baseclass for all Line, Bar, Scatter, Candle and Bubble data.
 * 
 * @author Philipp Jahoda
 */
public abstract class BarLineScatterCandleBubbleData<T extends IBarLineScatterCandleBubbleDataSet<? extends Entry>>
        extends ChartData<T> {
    public BarLineScatterCandleBubbleData() {
        super();
    }

    @SafeVarargs
    public BarLineScatterCandleBubbleData(@NotNull T... sets) {
        super(sets);
    }

    public BarLineScatterCandleBubbleData(@NotNull List<T> sets) {
        super(sets);
    }
}
