package com.github.mikephil.charting.formatter;

import com.github.mikephil.charting.components.AxisBase;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

/**
 * Created by philipp on 02/06/16.
 */
public class DefaultAxisValueFormatter implements IAxisValueFormatter {
    /**
     * decimal format for formatting
     */
    protected DecimalFormat mFormat;

    /**
     * the number of decimal digits this formatter uses
     */
    protected final int digits;

    /**
     * Constructor that specifies to how many digits the value should be
     * formatted.
     */
    public DefaultAxisValueFormatter(int digits) {
        this.digits = digits;

        char[] formatChars = new char[digits + 1];
        formatChars[0] = '.';
        for(int i = 1; i < formatChars.length; i++) {
            formatChars[i] = '0';
        }
        String format = new String(formatChars);

        mFormat = new DecimalFormat("###,###,###,##0" + format);
    }

    @Override
    @NotNull
    public String getFormattedValue(float value, @Nullable AxisBase axis) {
        // avoid memory allocations here (for performance)
        return mFormat.format(value);
    }

    /**
     * Returns the number of decimal digits this formatter uses or -1, if unspecified.
     */
    public int getDecimalDigits() {
        return digits;
    }
}
