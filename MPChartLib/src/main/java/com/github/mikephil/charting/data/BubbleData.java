
package com.github.mikephil.charting.data;

import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BubbleData extends BarLineScatterCandleBubbleData<IBubbleDataSet> {
    public BubbleData() {
        super();
    }

    public BubbleData(@NotNull IBubbleDataSet... dataSets) {
        super(dataSets);
    }

    public BubbleData(@NotNull List<IBubbleDataSet> dataSets) {
        super(dataSets);
    }

    /**
     * Sets the width of the circle that surrounds the bubble when highlighted
     * for all DataSet objects this data object contains, in dp.
     */
    public void setHighlightCircleWidth(float width) {
        for (IBubbleDataSet set : mDataSets) {
            set.setHighlightCircleWidth(width);
        }
    }
}
