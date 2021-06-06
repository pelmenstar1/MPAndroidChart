package com.github.mikephil.charting.interfaces.dataprovider;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

public interface LineDataProvider extends BarLineScatterCandleBubbleDataProvider<LineData, ILineDataSet, Entry> {
    LineData getLineData();

    YAxis getAxis(YAxis.AxisDependency dependency);
}
