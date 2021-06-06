package com.github.mikephil.charting.interfaces.dataprovider;

import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;

public interface BubbleDataProvider extends BarLineScatterCandleBubbleDataProvider<BubbleData, IBubbleDataSet, BubbleEntry> {
    BubbleData getBubbleData();
}
