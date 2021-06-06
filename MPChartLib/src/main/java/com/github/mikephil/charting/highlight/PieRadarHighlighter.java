package com.github.mikephil.charting.highlight;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.PieRadarChartBase;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by philipp on 12/06/16.
 */
public abstract class PieRadarHighlighter<TChart extends PieRadarChartBase<TData, TDataSet, TEntry>, TData extends ChartData<TDataSet, TEntry>, TDataSet extends IDataSet<TEntry>, TEntry extends Entry> implements IHighlighter {
    protected TChart mChart;

    protected List<Highlight> mHighlightBuffer = new ArrayList<>();

    public PieRadarHighlighter(@NotNull TChart chart) {
        this.mChart = chart;
    }

    @Override
    @Nullable
    public Highlight getHighlight(float x, float y) {
        float touchDistanceToCenter = mChart.distanceToCenter(x, y);

        // check if a slice was touched
        if (touchDistanceToCenter > mChart.getRadius()) {
            // if no slice was touched, highlight nothing
            return null;
        } else {
            float angle = mChart.getAngleForPoint(x, y);

            if (mChart instanceof PieChart) {
                angle /= mChart.getAnimator().getPhaseY();
            }

            int index = mChart.getIndexForAngle(angle);

            // check if the index could be found
            if (index < 0 || index >= mChart.getData().getMaxEntryCountSet().getEntryCount()) {
                return null;
            } else {
                return getClosestHighlight(index, x, y);
            }
        }
    }

    /**
     * Returns the closest Highlight object of the given objects based on the touch position inside the chart.
     */
    @Nullable
    protected abstract Highlight getClosestHighlight(int index, float x, float y);
}
