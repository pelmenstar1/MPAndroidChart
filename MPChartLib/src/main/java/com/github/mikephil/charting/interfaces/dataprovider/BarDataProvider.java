package com.github.mikephil.charting.interfaces.dataprovider;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

public interface BarDataProvider extends BarLineScatterCandleBubbleDataProvider<BarData, IBarDataSet, BarEntry> {
    BarData getBarData();
    boolean isDrawBarShadowEnabled();
    boolean isDrawValueAboveBarEnabled();
    boolean isHighlightFullBarEnabled();
}
