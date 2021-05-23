package com.github.mikephil.charting.highlight;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by philipp on 12/06/16.
 */
public class RadarHighlighter extends PieRadarHighlighter<RadarChart> {
    public RadarHighlighter(@NotNull RadarChart chart) {
        super(chart);
    }

    @Override
    @Nullable
    protected Highlight getClosestHighlight(int index, float x, float y) {
        List<Highlight> highlights = getHighlightsAtIndex(index);

        float distanceToCenter = mChart.distanceToCenter(x, y) / mChart.getFactor();

        Highlight closest = null;
        float distance = Float.MAX_VALUE;

        for (int i = 0; i < highlights.size(); i++) {
            Highlight high = highlights.get(i);

            float cdistance = Math.abs(high.getY() - distanceToCenter);
            if (cdistance < distance) {
                closest = high;
                distance = cdistance;
            }
        }

        return closest;
    }
    /**
     * Returns an array of Highlight objects for the given index. The Highlight
     * objects give information about the value at the selected index and the
     * DataSet it belongs to. INFORMATION: This method does calculations at
     * runtime. Do not over-use in performance critical situations.
     */
    @NotNull
    protected List<Highlight> getHighlightsAtIndex(int index) {
        mHighlightBuffer.clear();

        float phaseX = mChart.getAnimator().getPhaseX();
        float phaseY = mChart.getAnimator().getPhaseY();
        float sliceAngle = mChart.getSliceAngle();
        float factor = mChart.getFactor();

        float yChartMin = mChart.getYChartMin();
        float rotAngle = mChart.getRotationAngle();

        float factorPhaseY = factor * phaseY;
        float angle = sliceAngle * index * phaseX + rotAngle;

        RadarData data = mChart.getData();
        MPPointF pOut = MPPointF.getInstance(0,0);

        for (int i = 0; i < data.getDataSetCount(); i++) {
            IDataSet<?> dataSet = data.getDataSetByIndex(i);

            Entry entry = dataSet.getEntryForIndex(index);
            float y = entry.getY() - yChartMin;

            float dist = y * factorPhaseY;
            Utils.getPosition(mChart.getCenterOffsets(), dist, angle, pOut);

            mHighlightBuffer.add(new Highlight(index, entry.getY(), pOut.x, pOut.y, i, dataSet.getAxisDependency()));
        }

        return mHighlightBuffer;
    }
}
