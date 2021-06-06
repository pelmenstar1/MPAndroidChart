package com.github.mikephil.charting.interfaces.dataprovider;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;

public interface ScatterDataProvider extends BarLineScatterCandleBubbleDataProvider<ScatterData, IScatterDataSet, Entry> {
    ScatterData getScatterData();
}
