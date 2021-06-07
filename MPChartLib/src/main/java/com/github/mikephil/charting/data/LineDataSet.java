
package com.github.mikephil.charting.data;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.util.Log;

import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;

import com.github.mikephil.charting.formatter.DefaultFillFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.IntList;
import com.github.mikephil.charting.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

public class LineDataSet extends LineRadarDataSet<Entry> implements ILineDataSet {
    /**
     * Drawing mode for this line dataset
     **/
    @Mode
    private int mMode = MODE_LINEAR;

    /**
     * List representing all colors that are used for the circles
     */
    @NotNull
    private IntList mCircleColors = new IntList();

    /**
     * the color of the inner circles
     */
    private int mCircleHoleColor = Color.WHITE;

    /**
     * the radius of the circle-shaped value indicators
     */
    private float mCircleRadius = 8f;

    /**
     * the hole radius of the circle-shaped value indicators
     */
    private float mCircleHoleRadius = 4f;

    /**
     * sets the intensity of the cubic lines
     */
    private float mCubicIntensity = 0.2f;

    /**
     * the path effect of this DataSet that makes dashed lines possible
     */
    @Nullable
    private DashPathEffect mDashPathEffect = null;

    /**
     * formatter for customizing the position of the fill-line
     */
    @NotNull
    private IFillFormatter mFillFormatter = new DefaultFillFormatter();

    /**
     * if true, drawing circles is enabled
     */
    private boolean mDrawCircles = true;

    private boolean mDrawCircleHole = true;

    public LineDataSet(@NotNull List<Entry> values, @Nullable String label) {
        super(values, label);

        mCircleColors.add(Color.rgb(140, 234, 255));
    }

    @Override
    @NotNull
    public DataSet<Entry> copy() {
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < mEntries.size(); i++) {
            entries.add(mEntries.get(i).copy());
        }

        LineDataSet copied = new LineDataSet(entries, getLabel());
        copy(copied);
        return copied;
    }

    protected void copy(@NotNull LineDataSet lineDataSet) {
        super.copy(lineDataSet);
        lineDataSet.mCircleColors = mCircleColors;
        lineDataSet.mCircleHoleColor = mCircleHoleColor;
        lineDataSet.mCircleHoleRadius = mCircleHoleRadius;
        lineDataSet.mCircleRadius = mCircleRadius;
        lineDataSet.mCubicIntensity = mCubicIntensity;
        lineDataSet.mDashPathEffect = mDashPathEffect;
        lineDataSet.mDrawCircleHole = mDrawCircleHole;
        lineDataSet.mDrawCircles = mDrawCircleHole;
        lineDataSet.mFillFormatter = mFillFormatter;
        lineDataSet.mMode = mMode;
    }

    /**
     * Returns the drawing mode for this line dataset
     */
    @Override
    @Mode
    public int getMode() {
        return mMode;
    }

    /**
     * Returns the drawing mode for this LineDataSet
     */
    public void setMode(@LineDataSet.Mode int mode) {
        mMode = mode;
    }

    /**
     * Sets the intensity for cubic lines (if enabled). Max = 1f = very cubic,
     * Min = 0.05f = low cubic effect, Default: 0.2f
     */
    public void setCubicIntensity(float intensity) {
        if (intensity > 1f)
            intensity = 1f;
        if (intensity < 0.05f)
            intensity = 0.05f;

        mCubicIntensity = intensity;
    }

    @Override
    public float getCubicIntensity() {
        return mCubicIntensity;
    }

    /**
     * Sets the radius of the drawn circles.
     * Default radius = 4f, Min = 1f
     */
    public void setCircleRadius(float radius) {
        if (radius >= 1f) {
            mCircleRadius = Utils.convertDpToPixel(radius);
        } else {
            Log.e("LineDataSet", "Circle radius cannot be < 1");
        }
    }

    @Override
    public float getCircleRadius() {
        return mCircleRadius;
    }

    /**
     * Sets the hole radius of the drawn circles.
     * Default radius = 2f, Min = 0.5f
     */
    public void setCircleHoleRadius(float holeRadius) {
        if (holeRadius >= 0.5f) {
            mCircleHoleRadius = Utils.convertDpToPixel(holeRadius);
        } else {
            Log.e("LineDataSet", "Circle radius cannot be < 0.5");
        }
    }

    @Override
    public float getCircleHoleRadius() {
        return mCircleHoleRadius;
    }

    /**
     * sets the size (radius) of the circle shpaed value indicators,
     * default size = 4f
     * <p/>
     * This method is deprecated because of unclarity. Use setCircleRadius instead.
     */
    @Deprecated
    public void setCircleSize(float size) {
        setCircleRadius(size);
    }

    /**
     * This function is deprecated because of unclarity. Use getCircleRadius instead.
     */
    @Deprecated
    public float getCircleSize() {
        return getCircleRadius();
    }

    /**
     * Enables the line to be drawn in dashed mode, e.g. like this
     * "- - - - - -". THIS ONLY WORKS IF HARDWARE-ACCELERATION IS TURNED OFF.
     * Keep in mind that hardware acceleration boosts performance.
     *
     * @param lineLength  the length of the line pieces
     * @param spaceLength the length of space in between the pieces
     * @param phase       offset, in degrees (normally, use 0)
     */
    public void enableDashedLine(float lineLength, float spaceLength, float phase) {
        mDashPathEffect = new DashPathEffect(new float[]{
                lineLength, spaceLength
        }, phase);
    }

    /**
     * Disables the line to be drawn in dashed mode.
     */
    public void disableDashedLine() {
        mDashPathEffect = null;
    }

    @Override
    public boolean isDashedLineEnabled() {
        return mDashPathEffect != null;
    }

    @Override
    @Nullable
    public DashPathEffect getDashPathEffect() {
        return mDashPathEffect;
    }

    /**
     * set this to true to enable the drawing of circle indicators for this
     * DataSet, default true
     */
    public void setDrawCircles(boolean enabled) {
        this.mDrawCircles = enabled;
    }

    @Override
    public boolean isDrawCirclesEnabled() {
        return mDrawCircles;
    }

    @Deprecated
    @Override
    public boolean isDrawCubicEnabled() {
        return mMode == MODE_CUBIC_BEZIER;
    }

    @Deprecated
    @Override
    public boolean isDrawSteppedEnabled() {
        return mMode == MODE_STEPPED;
    }

    // ALL CODE BELOW RELATED TO CIRCLE-COLORS

    /**
     * returns all colors specified for the circles
     */
    @NotNull
    public IntList getCircleColors() {
        return mCircleColors;
    }

    @Override
    public int getCircleColor(int index) {
        return mCircleColors.get(index);
    }

    @Override
    public int getCircleColorCount() {
        return mCircleColors.size();
    }

    /**
     * Sets the colors that should be used for the circles of this DataSet.
     * Colors are reused as soon as the number of Entries the DataSet represents
     * is higher than the size of the colors array. Make sure that the colors
     * are already prepared (by calling getResources().getColor(...)) before
     * adding them to the DataSet.
     */
    public void setCircleColors(@NotNull IntList colors) {
        mCircleColors = colors;
    }

    /**
     * Sets the colors that should be used for the circles of this DataSet.
     * Colors are reused as soon as the number of Entries the DataSet represents
     * is higher than the size of the colors array. Make sure that the colors
     * are already prepared (by calling getResources().getColor(...)) before
     * adding them to the DataSet.
     */
    public void setCircleColors(@NotNull int... colors) {
        this.mCircleColors = IntList.ofArrayCopy(colors);
    }

    /**
     * ets the colors that should be used for the circles of this DataSet.
     * Colors are reused as soon as the number of Entries the DataSet represents
     * is higher than the size of the colors array. You can use
     * "new String[] { R.color.red, R.color.green, ... }" to provide colors for
     * this method. Internally, the colors are resolved using
     * getResources().getColor(...)
     */
    public void setCircleColors(@NotNull int[] colors, @NotNull Context c) {
        mCircleColors.clear();

        Resources res = c.getResources();

        for (int color : colors) {
            mCircleColors.add(res.getColor(color));
        }
    }

    /**
     * Sets the one and ONLY color that should be used for this DataSet.
     * Internally, this recreates the colors array and adds the specified color.
     */
    public void setCircleColor(@ColorInt int color) {
        resetCircleColors();
        mCircleColors.add(color);
    }

    /**
     * resets the circle-colors array and creates a new one
     */
    public void resetCircleColors() {
        mCircleColors.clear();
    }

    /**
     * Sets the color of the inner circle of the line-circles.
     */
    public void setCircleHoleColor(@ColorInt int color) {
        mCircleHoleColor = color;
    }

    @Override
    @ColorInt
    public int getCircleHoleColor() {
        return mCircleHoleColor;
    }

    /**
     * Set this to true to allow drawing a hole in each data circle.
     */
    public void setDrawCircleHole(boolean enabled) {
        mDrawCircleHole = enabled;
    }

    @Override
    public boolean isDrawCircleHoleEnabled() {
        return mDrawCircleHole;
    }

    /**
     * Sets a custom IFillFormatter to the chart that handles the position of the
     * filled-line for each DataSet. Set this to null to use the default logic.
     */
    public void setFillFormatter(@NotNull IFillFormatter formatter) {
        mFillFormatter = formatter;
    }

    @Override
    @NotNull
    public IFillFormatter getFillFormatter() {
        return mFillFormatter;
    }

    public static final int MODE_LINEAR = 0;
    public static final int MODE_STEPPED = 1;
    public static final int MODE_CUBIC_BEZIER = 2;
    public static final int MODE_HORIZONTAL_BEZIER = 3;

    @IntDef({ MODE_LINEAR, MODE_STEPPED, MODE_CUBIC_BEZIER, MODE_HORIZONTAL_BEZIER })
    @Retention(RetentionPolicy.SOURCE)
    @Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
    public @interface Mode {
    }
}
