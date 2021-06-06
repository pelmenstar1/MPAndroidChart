package com.github.mikephil.charting.highlight;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Philipp Jahoda on 21/07/15.
 */
public class ChartHighlighter<T extends BarLineScatterCandleBubbleDataProvider<TData, TDataSet, TEntry>, TData extends BarLineScatterCandleBubbleData<TDataSet, TEntry>, TDataSet extends IBarLineScatterCandleBubbleDataSet<TEntry>, TEntry extends Entry> implements IHighlighter {
    /**
     * instance of the data-provider
     */
    protected T mChart;

    /**
     * buffer for storing previously highlighted values
     */
    protected List<Highlight> mHighlightBuffer = new ArrayList<>();

    public ChartHighlighter(@NotNull T chart) {
        this.mChart = chart;
    }

    @Override
    public Highlight getHighlight(float x, float y) {
        MPPointF pos = getValsForTouch(x, y);
        float xVal = pos.x;
        MPPointF.recycleInstance(pos);

        return getHighlightForX(xVal, x, y);
    }

    /**
     * Returns a recyclable MPPointD instance.
     * Returns the corresponding xPos for a given touch-position in pixels.
     */
    @NotNull
    protected MPPointF getValsForTouch(float x, float y) {
        // take any transformer to determine the x-axis value
        return mChart.getTransformer(YAxis.AxisDependency.LEFT).getValuesByTouchPoint(x, y);
    }

    /**
     * Returns the corresponding Highlight for a given xVal and x- and y-touch position in pixels.
     */
    @Nullable
    protected Highlight getHighlightForX(float xVal, float x, float y) {
        List<Highlight> closestValues = getHighlightsAtXValue(xVal, x, y);

        if(closestValues.isEmpty()) {
            return null;
        }

        float leftAxisMinDist = getMinimumDistance(closestValues, y, YAxis.AxisDependency.LEFT);
        float rightAxisMinDist = getMinimumDistance(closestValues, y, YAxis.AxisDependency.RIGHT);

        YAxis.AxisDependency axis = leftAxisMinDist < rightAxisMinDist ? YAxis.AxisDependency.LEFT : YAxis.AxisDependency.RIGHT;

        return getClosestHighlightByPixel(closestValues, x, y, axis, mChart.getMaxHighlightDistance());
    }

    /**
     * Returns the minimum distance from a touch value (in pixels) to the
     * closest value (in pixels) that is displayed in the chart.
     */
    protected float getMinimumDistance(
            @NotNull List<Highlight> closestValues,
            float pos,
            @NotNull YAxis.AxisDependency axis
    ) {
        float distance = Float.MAX_VALUE;

        for (int i = 0; i < closestValues.size(); i++) {

            Highlight high = closestValues.get(i);

            if (high.getAxis() == axis) {

                float tempDistance = Math.abs(getHighlightPos(high) - pos);
                if (tempDistance < distance) {
                    distance = tempDistance;
                }
            }
        }

        return distance;
    }

    protected float getHighlightPos(@NotNull Highlight h) {
        return h.getYPx();
    }

    /**
     * Returns a list of Highlight objects representing the entries closest to the given xVal.
     * The returned list contains two objects per DataSet (closest rounding up, closest rounding down).
     *
     * @param xVal the transformed x-value of the x-touch position
     * @param x    touch position
     * @param y    touch position
     */
    @NotNull
    protected List<Highlight> getHighlightsAtXValue(float xVal, float x, float y) {
        mHighlightBuffer.clear();

        TData data = getData();

        if (data == null)
            return mHighlightBuffer;

        for (int i = 0, dataSetCount = data.getDataSetCount(); i < dataSetCount; i++) {
            TDataSet dataSet = data.getDataSetByIndex(i);

            // don't include DataSets that cannot be highlighted
            if (!dataSet.isHighlightEnabled())
                continue;

            mHighlightBuffer.addAll(buildHighlights(dataSet, i, xVal, DataSet.ROUNDING_CLOSEST));
        }

        return mHighlightBuffer;
    }

    /**
     * An array of `Highlight` objects corresponding to the selected xValue and dataSetIndex.
     */
    protected List<Highlight> buildHighlights(
            @NotNull TDataSet set,
            int dataSetIndex,
            float xVal,
            @DataSet.Rounding int rounding
    ) {
        ArrayList<Highlight> highlights = new ArrayList<>();

        List<TEntry> entries = set.getEntriesForXValue(xVal);
        if (entries.isEmpty()) {
            // Try to find closest x-value and take all entries for that x-value
            final Entry closest = set.getEntryForXValue(xVal, Float.NaN, rounding);
            if (closest != null)
            {
                entries = set.getEntriesForXValue(closest.getX());
            }
        }

        if (entries.isEmpty())
            return highlights;

        Transformer transformer = mChart.getTransformer(set.getAxisDependency());

        for (TEntry e : entries) {
            MPPointF pixels = transformer.getPixelForValues(e.getX(), e.getY());

            highlights.add(new Highlight(
                    e.getX(), e.getY(),
                    pixels.x, pixels.y,
                    dataSetIndex,
                    set.getAxisDependency())
            );
        }

        return highlights;
    }

    /**
     * Returns the Highlight of the DataSet that contains the closest value on the
     * y-axis.
     *
     * @param closestValues        contains two Highlight objects per DataSet closest to the selected x-position (determined by
     *                             rounding up an down)
     * @param axis                 the closest axis
     */
    @Nullable
    public Highlight getClosestHighlightByPixel(
            @NotNull List<Highlight> closestValues,
            float x, float y,
            @Nullable YAxis.AxisDependency axis,
            float minSelectionDistance) {
        Highlight closest = null;
        float distance = minSelectionDistance;

        for (int i = 0; i < closestValues.size(); i++) {
            Highlight high = closestValues.get(i);

            if (axis == null || high.getAxis() == axis) {
                float cDistance = getDistance(x, y, high.getXPx(), high.getYPx());

                if (cDistance < distance) {
                    closest = high;
                    distance = cDistance;
                }
            }
        }

        return closest;
    }

    /**
     * Calculates the distance between the two given points.
     */
    protected float getDistance(float x1, float y1, float x2, float y2) {
        float xDist = x2 - x1;
        float yDist = y2 - y1;

        return (float)Math.sqrt(xDist * xDist + yDist * yDist);
    }

    @Nullable
    protected TData getData() {
        return mChart.getData();
    }
}
