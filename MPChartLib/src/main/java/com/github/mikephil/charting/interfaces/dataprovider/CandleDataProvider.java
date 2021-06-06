package com.github.mikephil.charting.interfaces.dataprovider;

import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;

public interface CandleDataProvider extends BarLineScatterCandleBubbleDataProvider<CandleData, ICandleDataSet, CandleEntry> {
    CandleData getCandleData();
}
