
package com.github.mikephil.charting.data;

import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Data object that encapsulates all data associated with a LineChart.
 * 
 * @author Philipp Jahoda
 */
public class LineData extends BarLineScatterCandleBubbleData<ILineDataSet, Entry> {
    public LineData() {
        super();
    }

    public LineData(@NotNull ILineDataSet... dataSets) {
        super(dataSets);
    }

    public LineData(@NotNull List<ILineDataSet> dataSets) {
        super(dataSets);
    }
}
