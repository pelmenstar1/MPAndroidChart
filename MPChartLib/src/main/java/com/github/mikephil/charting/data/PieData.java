
package com.github.mikephil.charting.data;

import android.util.Log;

import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A PieData object can only represent one DataSet. Unlike all other charts, the
 * legend labels of the PieChart are created from the x-values array, and not
 * from the DataSet labels. Each PieData object can only represent one
 * PieDataSet (multiple PieDataSets inside a single PieChart are not possible).
 *
 * @author Philipp Jahoda
 */
public class PieData extends ChartData<IPieDataSet> {
    public PieData() {
        super();
    }

    public PieData(@NotNull IPieDataSet dataSet) {
        super(dataSet);
    }

    /**
     * Sets the PieDataSet this data object should represent.
     */
    public void setDataSet(@NotNull IPieDataSet dataSet) {
        mDataSets.clear();
        mDataSets.add(dataSet);
        notifyDataChanged();
    }

    /**
     * Returns the DataSet this PieData object represents. A PieData object can
     * only contain one DataSet.
     */
    public IPieDataSet getDataSet() {
        return mDataSets.get(0);
    }

    @Override
    @NotNull
    public List<IPieDataSet> getDataSets() {
        List<IPieDataSet> dataSets = super.getDataSets();

        if (dataSets.size() < 1) {
            Log.e("MPAndroidChart",
                    "Found multiple data sets while pie chart only allows one");
        }

        return dataSets;
    }

    /**
     * The PieData object can only have one DataSet. Use getDataSet() method instead.
     *
     */
    @Override
    @NotNull
    public IPieDataSet getDataSetByIndex(int index) {
        if(index == 0) {
            return getDataSet();
        }

        throw new IllegalArgumentException("index>0");
    }

    @Override
    @Nullable
    public IPieDataSet getDataSetByLabel(@NotNull String label, boolean ignoreCase) {
        return ignoreCase ? label.equalsIgnoreCase(mDataSets.get(0).getLabel()) ? mDataSets.get(0)
                : null : label.equals(mDataSets.get(0).getLabel()) ? mDataSets.get(0) : null;
    }

    @Override
    @NotNull
    public Entry getEntryForHighlight(@NotNull Highlight highlight) {
        return getDataSet().getEntryForIndex((int) highlight.getX());
    }

    /**
     * Returns the sum of all values in this PieData object.
     */
    public float getYValueSum() {
        float sum = 0;

        for (int i = 0; i < getDataSet().getEntryCount(); i++) {
            sum += getDataSet().getEntryForIndex(i).getY();
        }

        return sum;
    }
}
