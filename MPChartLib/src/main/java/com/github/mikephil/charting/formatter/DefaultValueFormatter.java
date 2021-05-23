
package com.github.mikephil.charting.formatter;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

/**
 * Default formatter used for formatting values inside the chart. Uses a DecimalFormat with
 * pre-calculated number of digits (depending on max and min value).
 *
 * @author Philipp Jahoda
 */
public class DefaultValueFormatter implements IValueFormatter {
    /**
     * DecimalFormat for formatting
     */
    protected DecimalFormat mFormat;

    protected int mDecimalDigits;

    /**
     * Constructor that specifies to how many digits the value should be
     * formatted.
     */
    public DefaultValueFormatter(int digits) {
        setup(digits);
    }

    /**
     * Sets up the formatter with a given number of decimal digits.
     */
    public void setup(int digits) {
        this.mDecimalDigits = digits;

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
    public String getFormattedValue(
            float value,
            @NotNull Entry entry,
            int dataSetIndex,
            @NotNull ViewPortHandler viewPortHandler
    ) {
        return mFormat.format(value);
    }

    /**
     * Returns the number of decimal digits this formatter uses.
     */
    public int getDecimalDigits() {
        return mDecimalDigits;
    }
}
