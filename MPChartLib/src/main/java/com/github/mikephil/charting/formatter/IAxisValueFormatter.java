package com.github.mikephil.charting.formatter;

import com.github.mikephil.charting.components.AxisBase;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Philipp Jahoda on 20/09/15.
 * Custom formatter interface that allows formatting of
 * axis labels before they are being drawn.
 */
public interface IAxisValueFormatter {
    /**
     * Called when a value from an axis is to be formatted
     * before being drawn. For performance reasons, avoid excessive calculations
     * and memory allocations inside this method.
     *  @param value the value to be formatted
     * @param axis  the axis the value belongs to
     */
    @NotNull
    String getFormattedValue(float value, @Nullable AxisBase axis);
}
