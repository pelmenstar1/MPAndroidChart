package com.github.mikephil.charting.interfaces.dataprovider;

import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.Transformer;

public interface BarLineScatterCandleBubbleDataProvider<TData extends BarLineScatterCandleBubbleData<TDataSet, TEntry>, TDataSet extends IBarLineScatterCandleBubbleDataSet<TEntry>, TEntry extends Entry>
        extends ChartInterface<TData, TDataSet, TEntry> {

    Transformer getTransformer(@AxisDependency int axis);
    boolean isInverted(@AxisDependency int axis);
    
    float getLowestVisibleX();
    float getHighestVisibleX();
}
