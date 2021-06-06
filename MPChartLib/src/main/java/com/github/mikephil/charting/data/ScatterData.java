
package com.github.mikephil.charting.data;

import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ScatterData extends BarLineScatterCandleBubbleData<IScatterDataSet, Entry> {
    public ScatterData() {
        super();
    }

    public ScatterData(@NotNull List<IScatterDataSet> dataSets) {
        super(dataSets);
    }

    public ScatterData(@NotNull IScatterDataSet... dataSets) {
        super(dataSets);
    }

    /**
     * Returns the maximum shape-size across all DataSets.
     */
    public float getGreatestShapeSize() {
        float max = 0f;

        for (IScatterDataSet set : mDataSets) {
            float size = set.getScatterShapeSize();

            if (size > max)
                max = size;
        }

        return max;
    }
}
