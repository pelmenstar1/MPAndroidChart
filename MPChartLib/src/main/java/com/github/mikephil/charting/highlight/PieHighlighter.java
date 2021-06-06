package com.github.mikephil.charting.highlight;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by philipp on 12/06/16.
 */
public class PieHighlighter extends PieRadarHighlighter<PieChart, PieData, IPieDataSet, PieEntry> {
    public PieHighlighter(@NotNull PieChart chart) {
        super(chart);
    }

    @Override
    @Nullable
    protected Highlight getClosestHighlight(int index, float x, float y) {
        IPieDataSet set = mChart.getData().getDataSet();

        Entry entry = set.getEntryForIndex(index);

        return new Highlight(index, entry.getY(), x, y, 0, set.getAxisDependency());
    }
}
