
package com.github.mikephil.charting.data;

import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * Data container for the RadarChart.
 *
 * @author Philipp Jahoda
 */
public class RadarData extends ChartData<IRadarDataSet> {
    @Nullable
    private List<String> mLabels;

    public RadarData() {
        super();
    }

    public RadarData(@NotNull List<IRadarDataSet> dataSets) {
        super(dataSets);
    }

    public RadarData(@NotNull IRadarDataSet... dataSets) {
        super(dataSets);
    }

    /**
     * Sets the labels that should be drawn around the RadarChart at the end of each web line.
     */
    public void setLabels(@NotNull List<String> labels) {
        this.mLabels = labels;
    }

    /**
     * Sets the labels that should be drawn around the RadarChart at the end of each web line.
     */
    public void setLabels(@NotNull String... labels) {
        this.mLabels = Arrays.asList(labels);
    }

    @Nullable
    public List<String> getLabels() {
        return mLabels;
    }

    @Override
    public Entry getEntryForHighlight(Highlight highlight) {
        return getDataSetByIndex(highlight.getDataSetIndex()).getEntryForIndex((int) highlight.getX());
    }
}
