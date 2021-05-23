package com.github.mikephil.charting.formatter;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

/**
 * Created by Philipp Jahoda on 28/01/16.
 * <p/>
 * A formatter specifically for stacked BarChart that allows to specify whether the all stack values
 * or just the top value should be drawn.
 */
public class StackedValueFormatter implements IValueFormatter {
    /**
     * if true, all stack values of the stacked bar entry are drawn, else only top
     */
    private final boolean mDrawWholeStack;

    /**
     * a string that should be appended behind the value
     */
    private final String mAppendix;

    private final DecimalFormat mFormat;

    /**
     * Constructor.
     *
     * @param drawWholeStack if true, all stack values of the stacked bar entry are drawn, else only top
     * @param appendix       a string that should be appended behind the value
     * @param decimals       the number of decimal digits to use
     */
    public StackedValueFormatter(boolean drawWholeStack, @NotNull String appendix, int decimals) {
        this.mDrawWholeStack = drawWholeStack;
        this.mAppendix = appendix;

        char[] formatChars = new char[decimals + 1];
        formatChars[0] = '.';
        for(int i = 1; i < formatChars.length; i++) {
            formatChars[i] = '0';
        }
        String format = new String(formatChars);

        this.mFormat = new DecimalFormat("###,###,###,##0" + format);
    }

    @Override
    @NotNull
    public String getFormattedValue(
            float value,
            @NotNull Entry entry,
            int dataSetIndex,
            @NotNull ViewPortHandler viewPortHandler
    ) {
        if (!mDrawWholeStack && entry instanceof BarEntry) {
            BarEntry barEntry = (BarEntry) entry;
            float[] values = barEntry.getYVals();

            if (values != null) {
                // find out if we are on top of the stack
                if (values[values.length - 1] == value) {
                    // return the "sum" across all stack values
                    return mFormat.format(barEntry.getY()) + mAppendix;
                } else {
                    return ""; // return empty
                }
            }
        }

        // return the "proposed" value
        return mFormat.format(value) + mAppendix;
    }
}
