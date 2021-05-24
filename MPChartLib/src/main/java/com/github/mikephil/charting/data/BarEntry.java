package com.github.mikephil.charting.data;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;

import com.github.mikephil.charting.highlight.Range;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Entry class for the BarChart. (especially stacked bars)
 *
 * @author Philipp Jahoda
 */
@SuppressLint("ParcelCreator")
public class BarEntry extends Entry {
    /**
     * the values the stacked barchart holds
     */
    @Nullable
    private float[] mYVals;

    /**
     * the ranges for the individual stack values - automatically calculated
     */
    @NotNull
    private Range[] mRanges;

    /**
     * the sum of all negative values this entry (if stacked) contains
     */
    private float mNegativeSum;

    /**
     * the sum of all positive values this entry (if stacked) contains
     */
    private float mPositiveSum;

    /**
     * Constructor for normal bars (not stacked).
     */
    public BarEntry(float x, float y) {
        super(x, y);
    }

    /**
     * Constructor for normal bars (not stacked).
     *
     * @param data - Spot for additional data this Entry represents.
     */
    public BarEntry(float x, float y, @Nullable Object data) {
        super(x, y, data);
    }

    /**
     * Constructor for normal bars (not stacked).
     *
     * @param icon - icon image
     */
    public BarEntry(float x, float y, @Nullable Drawable icon) {
        super(x, y, icon);
    }

    /**
     * Constructor for normal bars (not stacked).
     *
     * @param icon - icon image
     * @param data - Spot for additional data this Entry represents.
     */
    public BarEntry(float x, float y, @Nullable Drawable icon, @Nullable Object data) {
        super(x, y, icon, data);
    }

    /**
     * Constructor for stacked bar entries. One data object for whole stack
     *
     * @param values - the stack values, use at least 2
     */
    public BarEntry(float x, @NotNull float[] values) {
        super(x, calcSum(values));

        this.mYVals = values;
        calcPosNegSum();
        calcRanges();
    }

    /**
     * Constructor for stacked bar entries. One data object for whole stack
     *
     * @param values - the stack values, use at least 2
     * @param data - Spot for additional data this Entry represents.
     */
    public BarEntry(float x, @NotNull float[] values, @Nullable Object data) {
        super(x, calcSum(values), data);

        this.mYVals = values;
        calcPosNegSum();
        calcRanges();
    }

    /**
     * Constructor for stacked bar entries. One data object for whole stack
     *
     * @param values - the stack values, use at least 2
     * @param icon - icon image
     */
    public BarEntry(float x, @NotNull float[] values, @Nullable Drawable icon) {
        super(x, calcSum(values), icon);

        this.mYVals = values;
        calcPosNegSum();
        calcRanges();
    }

    /**
     * Constructor for stacked bar entries. One data object for whole stack
     *
     * @param values - the stack values, use at least 2
     * @param icon - icon image
     * @param data - Spot for additional data this Entry represents.
     */
    public BarEntry(float x, @NotNull float[] values, @Nullable Drawable icon, @Nullable Object data) {
        super(x, calcSum(values), icon, data);

        this.mYVals = values;
        calcPosNegSum();
        calcRanges();
    }

    /**
     * Returns an exact copy of the BarEntry.
     */
    @NotNull
    public BarEntry copy() {
        BarEntry copied = new BarEntry(getX(), getY(), getData());
        copied.setVals(mYVals);

        return copied;
    }

    /**
     * Returns the stacked values this BarEntry represents, or null, if only a single value is represented (then, use
     * getY()).
     */
    @Nullable
    public float[] getYVals() {
        return mYVals;
    }

    /**
     * Set the array of values this BarEntry should represent.
     */
    public void setVals(@NotNull float[] vals) {
        setY(calcSum(vals));
        mYVals = vals;

        calcPosNegSum();
        calcRanges();
    }

    /**
     * Returns the value of this BarEntry. If the entry is stacked, it returns the positive sum of all values.
     */
    @Override
    public float getY() {
        return super.getY();
    }

    /**
     * Returns the ranges of the individual stack-entries. Will return null if this entry is not stacked.
     */
    @NotNull
    public Range[] getRanges() {
        return mRanges;
    }

    /**
     * Returns true if this BarEntry is stacked (has a values array), false if not.
     */
    public boolean isStacked() {
        return mYVals != null;
    }

    /**
     * Use `getSumBelow(stackIndex)` instead.
     */
    @Deprecated
    public float getBelowSum(int stackIndex) {
        return getSumBelow(stackIndex);
    }

    public float getSumBelow(int stackIndex) {
        if (mYVals == null)
            return 0;

        float remainder = 0f;
        int index = mYVals.length - 1;

        while (index > stackIndex && index >= 0) {
            remainder += mYVals[index];
            index--;
        }

        return remainder;
    }

    /**
     * Returns the sum of all positive values this entry (if stacked) contains.
     */
    public float getPositiveSum() {
        return mPositiveSum;
    }

    /**
     * Returns the sum of all negative values this entry (if stacked) contains. (this is a positive number)
     */
    public float getNegativeSum() {
        return mNegativeSum;
    }

    private void calcPosNegSum() {
        if (mYVals == null) {
            mNegativeSum = 0;
            mPositiveSum = 0;
            return;
        }

        float sumNeg = 0f;
        float sumPos = 0f;

        for (float f : mYVals) {
            if (f <= 0f) {
                sumNeg += Math.abs(f);
            } else {
                sumPos += f;
            }
        }

        mNegativeSum = sumNeg;
        mPositiveSum = sumPos;
    }

    /**
     * Calculates the sum across all values of the given stack.
     *
     */
    private static float calcSum(@NotNull float[] values) {
        float sum = 0f;

        for (float f : values) {
            sum += f;
        }

        return sum;
    }

    protected void calcRanges() {
        float[] values = mYVals;

        if (values == null || values.length == 0) {
            return;
        }

        float negRemain = -getNegativeSum();
        float posRemain = 0f;

        mRanges = new Range[values.length];

        for (int i = 0; i < mRanges.length; i++) {
            float value = values[i];

            if (value < 0) {
                mRanges[i] = new Range(negRemain, negRemain - value);
                negRemain -= value;
            } else {
                mRanges[i] = new Range(posRemain, posRemain + value);
                posRemain += value;
            }
        }
    }
}


