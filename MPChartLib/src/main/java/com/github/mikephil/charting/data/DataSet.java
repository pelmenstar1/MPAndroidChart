
package com.github.mikephil.charting.data;

import androidx.annotation.IntDef;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The DataSet class represents one group or type of entries (Entry) in the
 * Chart that belong together. It is designed to logically separate different
 * groups of values inside the Chart (e.g. the values for a specific line in the
 * LineChart, or the values of a specific group of bars in the BarChart).
 *
 * @author Philipp Jahoda
 */
public abstract class DataSet<T extends Entry> extends BaseDataSet<T> {
    /**
     * the entries that this DataSet represents / holds together
     */
    @NotNull
    protected List<T> mEntries;

    /**
     * maximum y-value in the value array
     */
    protected float mYMax = -Float.MAX_VALUE;

    /**
     * minimum y-value in the value array
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

    /**
     * Creates a new DataSet object with the given values (entries) it represents. Also, a
     * label that describes the DataSet can be specified. The label can also be
     * used to retrieve the DataSet from a ChartData object.
     */
    public DataSet(@NotNull List<T> entries, @Nullable String label) {
        super(label);

        this.mEntries = entries;
        calcMinMax();
    }

    @Override
    public void calcMinMax() {
        mYMax = -Float.MAX_VALUE;
        mYMin = Float.MAX_VALUE;
        mXMax = -Float.MAX_VALUE;
        mXMin = Float.MAX_VALUE;

        if (mEntries.isEmpty()) {
            return;
        }

        for (T e : mEntries) {
            calcMinMax(e);
        }
    }

    @Override
    public void calcMinMaxY(float fromX, float toX) {
        mYMax = -Float.MAX_VALUE;
        mYMin = Float.MAX_VALUE;
        
        if (mEntries.isEmpty())
            return;

        int indexFrom = getEntryIndex(fromX, Float.NaN, ROUNDING_DOWN);
        int indexTo = getEntryIndex(toX, Float.NaN, ROUNDING_UP);

        if (indexTo < indexFrom) return;

        for (int i = indexFrom; i <= indexTo; i++) {
            // only recalculate y
            calcMinMaxY(mEntries.get(i));
        }
    }

    /**
     * Updates the min and max x and y value of this DataSet based on the given Entry.
     */
    protected void calcMinMax(@NotNull T e) {
        calcMinMaxX(e);
        calcMinMaxY(e);
    }

    protected void calcMinMaxX(@NotNull T e) {
        if (e.getX() < mXMin) {
            mXMin = e.getX();
        }

        if (e.getX() > mXMax) {
            mXMax = e.getX();
        }
    }

    protected void calcMinMaxY(@NotNull T e) {
        if (e.getY() < mYMin)
            mYMin = e.getY();

        if (e.getY() > mYMax)
            mYMax = e.getY();
    }

    @Override
    public int getEntryCount() {
        return mEntries.size();
    }

    /**
     * This method is deprecated.
     * Use getEntries() instead.
     */
    @Deprecated
    public List<T> getValues() {
        return mEntries;
    }

    /**
     * Returns the array of entries that this DataSet represents.
     */
    @NotNull
    public List<T> getEntries() {
        return mEntries;
    }

    /**
     * This method is deprecated.
     * Use setEntries(...) instead.
     */
    @Deprecated
    public void setValues(List<T> values) {
        setEntries(values);
    }

    /**
     * Sets the array of entries that this DataSet represents, and calls notifyDataSetChanged()
     */
    public void setEntries(@NotNull List<T> entries) {
        mEntries = entries;
        notifyDataSetChanged();
    }

    /**
     * Provides an exact copy of the DataSet this method is used on.
     */
    @NotNull
    public abstract DataSet<T> copy();
    
    protected void copy(@NotNull DataSet<T> dataSet) {
        super.copy(dataSet);
    }

    @Override
    @NotNull
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(toSimpleString());
        for (int i = 0; i < mEntries.size(); i++) {
            buffer.append(mEntries.get(i).toString()).append(' ');
        }
        return buffer.toString();
    }

    /**
     * Returns a simple string representation of the DataSet with the type and
     * the number of Entries.
     */
    public String toSimpleString() {
        return "DataSet, label: " + (getLabel() == null ? "" : getLabel()) + ", entries: " + mEntries.size() +
                "\n";
    }

    @Override
    public float getYMin() {
        return mYMin;
    }

    @Override
    public float getYMax() {
        return mYMax;
    }

    @Override
    public float getXMin() {
        return mXMin;
    }

    @Override
    public float getXMax() {
        return mXMax;
    }

    @Override
    public void addEntryOrdered(@NotNull T e) {
        calcMinMax(e);

        if (mEntries.size() > 0 && mEntries.get(mEntries.size() - 1).getX() > e.getX()) {
            int closestIndex = getEntryIndex(e.getX(), e.getY(), ROUNDING_UP);
            mEntries.add(closestIndex, e);
        } else {
            mEntries.add(e);
        }
    }

    @Override
    public void clear() {
        mEntries.clear();
        notifyDataSetChanged();
    }

    @Override
    public boolean addEntry(@NotNull T e) {
        List<T> values = getEntries();

        calcMinMax(e);

        // add the entry
        return values.add(e);
    }

    @Override
    public boolean removeEntry(@NotNull T e) {
        // remove the entry
        boolean removed = mEntries.remove(e);

        if (removed) {
            calcMinMax();
        }

        return removed;
    }

    @Override
    public int getEntryIndex(@NotNull Entry e) {
        return mEntries.indexOf(e);
    }

    @Override
    @Nullable
    public T getEntryForXValue(float xValue, float closestToY, @Rounding int rounding) {
        int index = getEntryIndex(xValue, closestToY, rounding);

        if (index > -1)
            return mEntries.get(index);
        return null;
    }

    @Override
    @Nullable
    public T getEntryForXValue(float xValue, float closestToY) {
        return getEntryForXValue(xValue, closestToY, ROUNDING_CLOSEST);
    }

    @Override
    @NotNull
    public T getEntryForIndex(int index) {
        return mEntries.get(index);
    }

    @Override
    public int getEntryIndex(float xValue, float closestToY, @Rounding int rounding) {
        if (mEntries.isEmpty())
            return -1;

        int low = 0;
        int high = mEntries.size() - 1;
        int closest = high;

        while (low < high) {
            int m = (low + high) / 2;

            final float d1 = mEntries.get(m).getX() - xValue,
                    d2 = mEntries.get(m + 1).getX() - xValue,
                    ad1 = Math.abs(d1), ad2 = Math.abs(d2);

            if (ad2 < ad1) {
                // [m + 1] is closer to xValue
                // Search in an higher place
                low = m + 1;
            } else if (ad1 < ad2) {
                // [m] is closer to xValue
                // Search in a lower place
                high = m;
            } else {
                // We have multiple sequential x-value with same distance

                if (d1 >= 0.0) {
                    // Search in a lower place
                    high = m;
                } else if (d1 < 0.0) {
                    // Search in an higher place
                    low = m + 1;
                }
            }

            closest = high;
        }

        if (closest != -1) {
            float closestXValue = mEntries.get(closest).getX();
            if (rounding == ROUNDING_UP) {
                // If rounding up, and found x-value is lower than specified x, and we can go upper...
                if (closestXValue < xValue && closest < mEntries.size() - 1) {
                    ++closest;
                }
            } else if (rounding == ROUNDING_DOWN) {
                // If rounding down, and found x-value is upper than specified x, and we can go lower...
                if (closestXValue > xValue && closest > 0) {
                    --closest;
                }
            }

            // Search by closest to y-value
            if (!Float.isNaN(closestToY)) {
                while (closest > 0 && mEntries.get(closest - 1).getX() == closestXValue)
                    closest -= 1;

                float closestYValue = mEntries.get(closest).getY();
                int closestYIndex = closest;

                while (true) {
                    closest += 1;
                    if (closest >= mEntries.size())
                        break;

                    final Entry value = mEntries.get(closest);

                    if (value.getX() != closestXValue)
                        break;

                    if (Math.abs(value.getY() - closestToY) <= Math.abs(closestYValue - closestToY)) {
                        closestYValue = closestToY;
                        closestYIndex = closest;
                    }
                }

                closest = closestYIndex;
            }
        }

        return closest;
    }

    @Override
    @NotNull
    public List<T> getEntriesForXValue(float xValue) {
        ArrayList<T> entries = new ArrayList<>();

        int low = 0;
        int high = mEntries.size() - 1;

        while (low <= high) {
            int m = (high + low) / 2;
            T entry = mEntries.get(m);

            // if we have a match
            if (xValue == entry.getX()) {
                while (m > 0 && mEntries.get(m - 1).getX() == xValue)
                    m--;

                high = mEntries.size();

                // loop over all "equal" entries
                for (; m < high; m++) {
                    entry = mEntries.get(m);
                    if (entry.getX() == xValue) {
                        entries.add(entry);
                    } else {
                        break;
                    }
                }

                break;
            } else {
                if (xValue > entry.getX())
                    low = m + 1;
                else
                    high = m - 1;
            }
        }

        return entries;
    }

    public static final int ROUNDING_UP = 0;
    public static final int ROUNDING_DOWN = 1;
    public static final int ROUNDING_CLOSEST = 2;

    @IntDef(value = { ROUNDING_UP, ROUNDING_DOWN, ROUNDING_CLOSEST })
    public @interface Rounding {
    }
}