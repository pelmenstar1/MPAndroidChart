
package com.github.mikephil.charting.data;

import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Baseclass for all Line, Bar, Scatter, Candle and Bubble data.
 * 
 * @author Philipp Jahoda
 */
public abstract class BarLineScatterCandleBubbleData<
        TDataSet extends IBarLineScatterCandleBubbleDataSet<TEntry>,
        TEntry extends Entry
> extends ChartData<TDataSet, TEntry> {
    public BarLineScatterCandleBubbleData() {
        super();
    }

    @SafeVarargs
    public BarLineScatterCandleBubbleData(@NotNull TDataSet... sets) {
        super(sets);
    }

    public BarLineScatterCandleBubbleData(@NotNull List<TDataSet> sets) {
        super(sets);
    }
}
