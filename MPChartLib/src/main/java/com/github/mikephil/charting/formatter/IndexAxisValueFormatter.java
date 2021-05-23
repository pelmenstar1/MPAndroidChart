
package com.github.mikephil.charting.formatter;

import com.github.mikephil.charting.components.AxisBase;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * This formatter is used for passing an array of x-axis labels, on whole x steps.
 */
public class IndexAxisValueFormatter implements IAxisValueFormatter {
    private String[] mValues;
    private int mValueCount = 0;

    /**
     * An empty constructor.
     * Use `setValues` to set the axis labels.
     */
    public IndexAxisValueFormatter() {
    }

    /**
     * Constructor that specifies axis labels.
     *
     * @param values The values string array
     */
    public IndexAxisValueFormatter(@NotNull String[] values) {
        setValues(values);
    }

    /**
     * Constructor that specifies axis labels.
     *
     * @param values The values string array
     */
    public IndexAxisValueFormatter(@NotNull Collection<String> values) {
        setValues(values.toArray(new String[0]));
    }

    @NotNull
    public String getFormattedValue(float value, @Nullable AxisBase axis) {
        int index = Math.round(value);

        if (index < 0 || index >= mValueCount || index != (int)value)
            return "";

        return mValues[index];
    }

    @NotNull
    public String[] getValues()
    {
        return mValues;
    }

    public void setValues(@NotNull String[] values) {
        this.mValues = values;
        this.mValueCount = values.length;
    }
}
