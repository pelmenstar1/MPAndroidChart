
package com.github.mikephil.charting.formatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

/**
 * This IValueFormatter is just for convenience and simply puts a "%" sign after
 * each value. (Recommeded for PieChart)
 *
 * @author Philipp Jahoda
 */
public class PercentFormatter implements IValueFormatter, IAxisValueFormatter {
    protected DecimalFormat mFormat;

    public PercentFormatter() {
        mFormat = new DecimalFormat("###,###,##0.0");
    }

    public PercentFormatter(@NotNull DecimalFormat format) {
        this.mFormat = format;
    }

    // IValueFormatter
    @Override
    @NotNull
    public String getFormattedValue(
            float value,
            @NotNull Entry entry,
            int dataSetIndex,
            @NotNull ViewPortHandler viewPortHandler
    ) {
        return mFormat.format(value) + " %";
    }

    // IAxisValueFormatter
    @Override
    public @NotNull String getFormattedValue(float value, @Nullable AxisBase axis) {
        return mFormat.format(value) + " %";
    }
}
