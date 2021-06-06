
package com.github.mikephil.charting.data;

import android.graphics.Typeface;
import android.util.Log;

import androidx.annotation.ColorInt;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class that holds all relevant data that represents the chart. That involves
 * at least one (or more) DataSets, and an array of x-values.
 *
 * @author Philipp Jahoda
 */
public abstract class ChartData<TDataSet extends IDataSet<TEntry>, TEntry extends Entry> {
    /**
     * maximum y-value in the value array across all axes
     */
    protected float mYMax = -Float.MAX_VALUE;

    /**
     * the minimum y-value in the value array across all axes
     */
    protected float mYMin = Float.MAX_VALUE;

    /**
     * maximum x-value in the value array
     */
    protected float mXMax = -Float.MAX_VALUE;

    /**
     * minimum x-value in the value array
     */
    protected float mXMin = Float.MAX_VALUE;

    protected float mLeftAxisMax = -Float.MAX_VALUE;
    protected float mLeftAxisMin = Float.MAX_VALUE;
    protected float mRightAxisMax = -Float.MAX_VALUE;
    protected float mRightAxisMin = Float.MAX_VALUE;

    /**
     * array that holds all DataSets the ChartData object represents
     */
    @NotNull
    protected final List<TDataSet> mDataSets;

    /**
     * Default constructor.
     */
    public ChartData() {
        mDataSets = new ArrayList<>();
    }

    /**
     * Constructor taking single or multiple DataSet objects.
     */
    @SafeVarargs
    public ChartData(@NotNull TDataSet... dataSets) {
        mDataSets = arrayToList(dataSets);
        notifyDataChanged();
    }

    /**
     * Created because Arrays.asList(...) does not support modification.
     */
    @NotNull
    private ArrayList<TDataSet> arrayToList(@NotNull TDataSet[] array) {
        ArrayList<TDataSet> list = new ArrayList<>();
        Collections.addAll(list, array);

        return list;
    }

    /**
     * constructor for chart data
     *
     * @param sets the dataset array
     */
    public ChartData(@NotNull List<TDataSet> sets) {
        this.mDataSets = sets;
        notifyDataChanged();
    }

    /**
     * Call this method to let the ChartData know that the underlying data has
     * changed. Calling this performs all necessary recalculations needed when
     * the contained data has changed.
     */
    public void notifyDataChanged() {
        calcMinMax();
    }

    /**
     * Calc minimum and maximum y-values over all DataSets.
     * Tell DataSets to recalculate their min and max y-values, this is only needed for autoScaleMinMax.
     *
     * @param fromX the x-value to start the calculation from
     * @param toX   the x-value to which the calculation should be performed
     */
    public void calcMinMaxY(float fromX, float toX) {
        for (TDataSet set : mDataSets) {
            set.calcMinMaxY(fromX, toX);
        }

        // apply the new data
        calcMinMax();
    }

    /**
     * Calc minimum and maximum values (both x and y) over all DataSets.
     */
    protected void calcMinMax() {
        mYMax = -Float.MAX_VALUE;
        mYMin = Float.MAX_VALUE;
        mXMax = -Float.MAX_VALUE;
        mXMin = Float.MAX_VALUE;

        for (TDataSet set : mDataSets) {
            calcMinMax(set);
        }

        mLeftAxisMax = -Float.MAX_VALUE;
        mLeftAxisMin = Float.MAX_VALUE;
        mRightAxisMax = -Float.MAX_VALUE;
        mRightAxisMin = Float.MAX_VALUE;

        // left axis
        TDataSet firstLeft = getFirstLeft(mDataSets);

        if (firstLeft != null) {

            mLeftAxisMax = firstLeft.getYMax();
            mLeftAxisMin = firstLeft.getYMin();

            for (TDataSet dataSet : mDataSets) {
                if (dataSet.getAxisDependency() == YAxis.DEPENDENCY_LEFT) {
                    if (dataSet.getYMin() < mLeftAxisMin)
                        mLeftAxisMin = dataSet.getYMin();

                    if (dataSet.getYMax() > mLeftAxisMax)
                        mLeftAxisMax = dataSet.getYMax();
                }
            }
        }

        // right axis
        TDataSet firstRight = getFirstRight(mDataSets);

        if (firstRight != null) {

            mRightAxisMax = firstRight.getYMax();
            mRightAxisMin = firstRight.getYMin();

            for (TDataSet dataSet : mDataSets) {
                if (dataSet.getAxisDependency() == YAxis.DEPENDENCY_RIGHT) {
                    if (dataSet.getYMin() < mRightAxisMin)
                        mRightAxisMin = dataSet.getYMin();

                    if (dataSet.getYMax() > mRightAxisMax)
                        mRightAxisMax = dataSet.getYMax();
                }
            }
        }
    }

    /**
     * returns the number of LineDataSets this object contains
     */
    public int getDataSetCount() {
        return mDataSets.size();
    }

    /**
     * Returns the smallest y-value the data object contains.
     */
    public float getYMin() {
        return mYMin;
    }

    /**
     * Returns the minimum y-value for the specified axis.
     */
    public float getYMin(@AxisDependency int axis) {
        if (axis == YAxis.DEPENDENCY_LEFT) {
            if (mLeftAxisMin == Float.MAX_VALUE) {
                return mRightAxisMin;
            } else
                return mLeftAxisMin;
        } else {
            if (mRightAxisMin == Float.MAX_VALUE) {
                return mLeftAxisMin;
            } else
                return mRightAxisMin;
        }
    }

    /**
     * Returns the greatest y-value the data object contains.
     */
    public float getYMax() {
        return mYMax;
    }

    /**
     * Returns the maximum y-value for the specified axis.
     */
    public float getYMax(@AxisDependency int axis) {
        if (axis == YAxis.DEPENDENCY_LEFT) {
            if (mLeftAxisMax == -Float.MAX_VALUE) {
                return mRightAxisMax;
            } else
                return mLeftAxisMax;
        } else {
            if (mRightAxisMax == -Float.MAX_VALUE) {
                return mLeftAxisMax;
            } else
                return mRightAxisMax;
        }
    }

    /**
     * Returns the minimum x-value this data object contains.
     */
    public float getXMin() {
        return mXMin;
    }

    /**
     * Returns the maximum x-value this data object contains.
     */
    public float getXMax() {
        return mXMax;
    }

    /**
     * Returns all DataSet objects this ChartData object holds.
     */
    @NotNull
    public List<TDataSet> getDataSets() {
        return mDataSets;
    }

    /**
     * Retrieve the index of a DataSet with a specific label from the ChartData.
     * Search can be case sensitive or not. IMPORTANT: This method does
     * calculations at runtime, do not over-use in performance critical
     * situations.
     *
     * @param dataSets   the DataSet array to search
     * @param ignoreCase if true, the search is not case-sensitive
     */
    protected int getDataSetIndexByLabel(@NotNull List<TDataSet> dataSets, @NotNull String label,
                                         boolean ignoreCase) {

        if (ignoreCase) {
            for (int i = 0; i < dataSets.size(); i++)
                if (label.equalsIgnoreCase(dataSets.get(i).getLabel()))
                    return i;
        } else {
            for (int i = 0; i < dataSets.size(); i++)
                if (label.equals(dataSets.get(i).getLabel()))
                    return i;
        }

        return -1;
    }

    /**
     * Returns the labels of all DataSets as a string array.
     */
    @NotNull
    public String[] getDataSetLabels() {
        String[] types = new String[mDataSets.size()];

        for (int i = 0; i < mDataSets.size(); i++) {
            types[i] = mDataSets.get(i).getLabel();
        }

        return types;
    }

    /**
     * Get the Entry for a corresponding highlight object
     *
     * @return the entry that is highlighted
     */
    @Nullable
    public TEntry getEntryForHighlight(@NotNull Highlight highlight) {
        if (highlight.getDataSetIndex() >= mDataSets.size())
            return null;
        else {
            return mDataSets.get(highlight.getDataSetIndex()).getEntryForXValue(highlight.getX(), highlight.getY());
        }
    }

    /**
     * Returns the DataSet object with the given label. Search can be case
     * sensitive or not. IMPORTANT: This method does calculations at runtime.
     * Use with care in performance critical situations.
     */
    @Nullable
    public TDataSet getDataSetByLabel(@NotNull String label, boolean ignoreCase) {
        int index = getDataSetIndexByLabel(mDataSets, label, ignoreCase);

        if (index < 0 || index >= mDataSets.size())
            return null;
        else
            return mDataSets.get(index);
    }

    @NotNull
    public TDataSet getDataSetByIndex(int index) {
        return mDataSets.get(index);
    }

    /**
     * Adds a DataSet dynamically.
     */
    public void addDataSet(@NotNull TDataSet d) {
        calcMinMax(d);
        mDataSets.add(d);
    }

    /**
     * Removes the given DataSet from this data object. Also recalculates all
     * minimum and maximum values. Returns true if a DataSet was removed, false
     * if no DataSet could be removed.
     */
    public boolean removeDataSet(@NotNull TDataSet d) {
        boolean removed = mDataSets.remove(d);

        // if a DataSet was removed
        if (removed) {
            notifyDataChanged();
        }

        return removed;
    }

    /**
     * Removes the DataSet at the given index in the DataSet array from the data
     * object. Also recalculates all minimum and maximum values. Returns true if
     * a DataSet was removed, false if no DataSet could be removed.
     */
    public boolean removeDataSet(int index) {
        TDataSet set = mDataSets.get(index);

        return removeDataSet(set);
    }

    /**
     * Adds an Entry to the DataSet at the specified index.
     * Entries are added to the end of the list.
     */
    public void addEntry(TEntry e, int dataSetIndex) {
        if (mDataSets.size() > dataSetIndex && dataSetIndex >= 0) {
            TDataSet set = mDataSets.get(dataSetIndex);
            // add the entry to the dataset
            if (!set.addEntry(e))
                return;

            calcMinMax(e, set.getAxisDependency());
        } else {
            Log.e("addEntry", "Cannot add Entry because dataSetIndex too high or too low.");
        }
    }

    /**
     * Adjusts the current minimum and maximum values based on the provided Entry object.
     */
    protected void calcMinMax(@NotNull TEntry e, @AxisDependency int axis) {
        if (mYMax < e.getY())
            mYMax = e.getY();
        if (mYMin > e.getY())
            mYMin = e.getY();

        if (mXMax < e.getX())
            mXMax = e.getX();
        if (mXMin > e.getX())
            mXMin = e.getX();

        if (axis == YAxis.DEPENDENCY_LEFT) {
            if (mLeftAxisMax < e.getY())
                mLeftAxisMax = e.getY();
            if (mLeftAxisMin > e.getY())
                mLeftAxisMin = e.getY();
        } else {
            if (mRightAxisMax < e.getY())
                mRightAxisMax = e.getY();
            if (mRightAxisMin > e.getY())
                mRightAxisMin = e.getY();
        }
    }

    /**
     * Adjusts the minimum and maximum values based on the given DataSet.
     */
    protected void calcMinMax(@NotNull TDataSet d) {
        if (mYMax < d.getYMax())
            mYMax = d.getYMax();
        if (mYMin > d.getYMin())
            mYMin = d.getYMin();

        if (mXMax < d.getXMax())
            mXMax = d.getXMax();
        if (mXMin > d.getXMin())
            mXMin = d.getXMin();

        if (d.getAxisDependency() == YAxis.DEPENDENCY_LEFT) {
            if (mLeftAxisMax < d.getYMax())
                mLeftAxisMax = d.getYMax();
            if (mLeftAxisMin > d.getYMin())
                mLeftAxisMin = d.getYMin();
        } else {
            if (mRightAxisMax < d.getYMax())
                mRightAxisMax = d.getYMax();
            if (mRightAxisMin > d.getYMin())
                mRightAxisMin = d.getYMin();
        }
    }

    /**
     * Removes the given Entry object from the DataSet at the specified index.
     */
    public boolean removeEntry(@NotNull TEntry e, int dataSetIndex) {
        if (dataSetIndex >= mDataSets.size())
            return false;

        TDataSet set = mDataSets.get(dataSetIndex);

        if (set != null) {
            // remove the entry from the dataset
            boolean removed = set.removeEntry(e);

            if (removed) {
                notifyDataChanged();
            }

            return removed;
        } else
            return false;
    }

    /**
     * Removes the Entry object closest to the given DataSet at the
     * specified index. Returns true if an Entry was removed, false if no Entry
     * was found that meets the specified requirements.
     *
     */
    public boolean removeEntry(float xValue, int dataSetIndex) {
        if (dataSetIndex >= mDataSets.size())
            return false;

        TDataSet dataSet = mDataSets.get(dataSetIndex);
        TEntry e = dataSet.getEntryForXValue(xValue, Float.NaN);

        if (e == null)
            return false;

        return removeEntry(e, dataSetIndex);
    }

    /**
     * Returns the DataSet that contains the provided Entry, or null, if no
     * DataSet contains this Entry.
     */
    @Nullable
    public TDataSet getDataSetForEntry(@NotNull Entry e) {
        for (int i = 0; i < mDataSets.size(); i++) {
            TDataSet set = mDataSets.get(i);

            for (int j = 0; j < set.getEntryCount(); j++) {
                if (e.equalTo(set.getEntryForXValue(e.getX(), e.getY())))
                    return set;
            }
        }

        return null;
    }

    /**
     * Returns all colors used across all DataSet objects this object
     * represents.
     */
    @NotNull
    public int[] getColors() {
        int colorsCount = 0;

        for (int i = 0; i < mDataSets.size(); i++) {
            colorsCount += mDataSets.get(i).getColors().size();
        }

        int[] colors = new int[colorsCount];
        int index = 0;

        for (int i = 0; i < mDataSets.size(); i++) {
            List<Integer> clrs = mDataSets.get(i).getColors();

            for (Integer clr : clrs) {
                colors[index++] = clr;
            }
        }

        return colors;
    }

    /**
     * Returns the index of the provided DataSet in the DataSet array of this data object, or -1 if it does not exist.
     */
    public int getIndexOfDataSet(@NotNull TDataSet dataSet) {
        return mDataSets.indexOf(dataSet);
    }

    /**
     * Returns the first DataSet from the datasets-array that has it's dependency on the left axis.
     * Returns null if no DataSet with left dependency could be found.
     */
    @Nullable
    protected TDataSet getFirstLeft(@NotNull List<TDataSet> sets) {
        for (TDataSet dataSet : sets) {
            if (dataSet.getAxisDependency() == YAxis.DEPENDENCY_LEFT)
                return dataSet;
        }
        return null;
    }

    /**
     * Returns the first DataSet from the datasets-array that has it's dependency on the right axis.
     * Returns null if no DataSet with right dependency could be found.
     */
    @Nullable
    public TDataSet getFirstRight(@NotNull List<TDataSet> sets) {
        for (TDataSet dataSet : sets) {
            if (dataSet.getAxisDependency() == YAxis.DEPENDENCY_RIGHT)
                return dataSet;
        }
        return null;
    }

    /**
     * Sets a custom IValueFormatter for all DataSets this data object contains.
     *
     */
    public void setValueFormatter(@NotNull IValueFormatter f) {
        for (TDataSet set : mDataSets) {
            set.setValueFormatter(f);
        }
    }

    /**
     * Sets the color of the value-text (color in which the value-labels are
     * drawn) for all DataSets this data object contains.
     */
    public void setValueTextColor(@ColorInt int color) {
        for (TDataSet set : mDataSets) {
            set.setValueTextColor(color);
        }
    }

    /**
     * Sets the same list of value-colors for all DataSets this
     * data object contains.
     */
    public void setValueTextColors(@NotNull List<Integer> colors) {
        for (TDataSet set : mDataSets) {
            set.setValueTextColors(colors);
        }
    }

    /**
     * Sets the Typeface for all value-labels for all DataSets this data object
     * contains.
     */
    public void setValueTypeface(@Nullable Typeface tf) {
        for (TDataSet set : mDataSets) {
            set.setValueTypeface(tf);
        }
    }

    /**
     * Sets the size (in dp) of the value-text for all DataSets this data object
     * contains.
     */
    public void setValueTextSize(float size) {
        for (TDataSet set : mDataSets) {
            set.setValueTextSize(size);
        }
    }

    /**
     * Enables / disables drawing values (value-text) for all DataSets this data
     * object contains.
     */
    public void setDrawValues(boolean enabled) {
        for (TDataSet set : mDataSets) {
            set.setDrawValues(enabled);
        }
    }

    /**
     * Enables / disables highlighting values for all DataSets this data object
     * contains. If set to true, this means that values can
     * be highlighted programmatically or by touch gesture.
     */
    public void setHighlightEnabled(boolean enabled) {
        for (TDataSet set : mDataSets) {
            set.setHighlightEnabled(enabled);
        }
    }

    /**
     * Returns true if highlighting of all underlying values is enabled, false
     * if not.
     */
    public boolean isHighlightEnabled() {
        for (TDataSet set : mDataSets) {
            if (!set.isHighlightEnabled())
                return false;
        }
        return true;
    }

    /**
     * Clears this data object from all DataSets and removes all Entries. Don't
     * forget to invalidate the chart after this.
     */
    public void clearValues() {
        mDataSets.clear();
        notifyDataChanged();
    }

    /**
     * Checks if this data object contains the specified DataSet. Returns true
     * if so, false if not.
     */
    public boolean contains(@NotNull TDataSet dataSet) {
        for (TDataSet set : mDataSets) {
            if (set.equals(dataSet))
                return true;
        }

        return false;
    }

    /**
     * Returns the total entry count across all DataSet objects this data object contains.
     */
    public int getEntryCount() {
        int count = 0;

        for (TDataSet set : mDataSets) {
            count += set.getEntryCount();
        }

        return count;
    }

    /**
     * Returns the DataSet object with the maximum number of entries or null if there are no DataSets.
     */
    @Nullable
    public TDataSet getMaxEntryCountSet() {
        if (mDataSets.isEmpty())
            return null;

        TDataSet max = mDataSets.get(0);

        for (TDataSet set : mDataSets) {
            if (set.getEntryCount() > max.getEntryCount())
                max = set;
        }

        return max;
    }
}
