package com.github.mikephil.charting.data;

import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CandleData extends BarLineScatterCandleBubbleData<ICandleDataSet, CandleEntry> {
    public CandleData() {
        super();
    }

    public CandleData(@NotNull List<ICandleDataSet> dataSets) {
        super(dataSets);
    }

    public CandleData(@NotNull ICandleDataSet... dataSets) {
        super(dataSets);
    }
}
