package com.github.mikephil.charting.components;

import android.graphics.DashPathEffect;
import android.graphics.Paint;

import androidx.annotation.IntDef;

import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.EmptyArray;
import com.github.mikephil.charting.utils.FSize;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing the legend of the chart. The legend will contain one entry
 * per color and DataSet. Multiple colors in one DataSet are grouped together.
 * The legend object is NOT available before setting data to the chart.
 *
 * @author Philipp Jahoda
 */
public class Legend extends ComponentBase {
    public static final int FORM_NONE = 0;
    public static final int FORM_EMPTY = 1;
    public static final int FORM_DEFAULT = 2;
    public static final int FORM_SQUARE = 3;
    public static final int FORM_CIRCLE = 4;
    public static final int FORM_LINE = 5;

    @IntDef({ FORM_NONE, FORM_EMPTY, FORM_DEFAULT, FORM_SQUARE, FORM_CIRCLE, FORM_LINE })
    @Retention(RetentionPolicy.SOURCE)
    @Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
    public @interface LegendForm {
    }

    public static final int HALIGN_LEFT = 0;
    public static final int HALIGN_CENTER = 1;
    public static final int HALIGN_RIGHT = 2;

    @IntDef({ HALIGN_LEFT, HALIGN_CENTER, HALIGN_RIGHT })
    @Retention(RetentionPolicy.SOURCE)
    @Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
    public @interface LegendHorizontalAlignment {
    }

    public static final int VALIGN_TOP = 0;
    public static final int VALIGN_CENTER = 1;
    public static final int VALIGN_BOTTOM = 2;

    @IntDef({ VALIGN_TOP, VALIGN_CENTER, VALIGN_BOTTOM })
    @Retention(RetentionPolicy.SOURCE)
    @Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
    public @interface LegendVerticalAlignment {
    }

    public static final int ORIENTATION_HORIZONTAL = 0;
    public static final int ORIENTATION_VERTICAL = 1;

    @IntDef({ ORIENTATION_HORIZONTAL, ORIENTATION_VERTICAL })
    @Retention(RetentionPolicy.SOURCE)
    @Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
    public @interface LegendOrientation {
    }

    public static final int DIRECTION_LEFT_TO_RIGHT = 0;
    public static final int DIRECTION_RIGHT_TO_LEFT = 1;

    @IntDef({ DIRECTION_LEFT_TO_RIGHT, DIRECTION_RIGHT_TO_LEFT })
    @Retention(RetentionPolicy.SOURCE)
    @Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
    public @interface LegendDirection {
    }

    /**
     * The legend entries array
     */
    private LegendEntry[] mEntries = EmptyArray.LEGEND_ENTRY;

    /**
     * Entries that will be appended to the end of the auto calculated entries after calculating the legend.
     * (if the legend has already been calculated, you will need to call notifyDataSetChanged() to let the changes take effect)
     */
    private LegendEntry[] mExtraEntries;

    /**
     * Are the legend labels/colors a custom value or auto calculated? If false,
     * then it's auto, if true, then custom. default false (automatic legend)
     */
    private boolean mIsLegendCustom = false;

    private int mHorizontalAlignment = HALIGN_LEFT;
    private int mVerticalAlignment = VALIGN_BOTTOM;
    private int mOrientation = ORIENTATION_HORIZONTAL;
    private boolean mDrawInside = false;

    /**
     * the text direction for the legend
     */
    private int mDirection = DIRECTION_LEFT_TO_RIGHT;

    /**
     * the shape/form the legend colors are drawn in
     */
    private int mShape = FORM_SQUARE;

    /**
     * the size of the legend forms/shapes
     */
    private float mFormSize = 8f;

    /**
     * the size of the legend forms/shapes
     */
    private float mFormLineWidth = 3f;

    /**
     * Line dash path effect used for shapes that consist of lines.
     */
    private DashPathEffect mFormLineDashEffect = null;

    /**
     * the space between the legend entries on a horizontal axis, default 6f
     */
    private float mXEntrySpace = 6f;

    /**
     * the space between the legend entries on a vertical axis, default 5f
     */
    private float mYEntrySpace = 0f;

    /**
     * the space between the legend entries on a vertical axis, default 2f
     * private float mYEntrySpace = 2f; /** the space between the form and the
     * actual label/text
     */
    private float mFormToTextSpace = 5f;

    /**
     * the space that should be left between stacked forms
     */
    private float mStackSpace = 3f;

    /**
     * the maximum relative size out of the whole chart view in percent
     */
    private float mMaxSizePercent = 0.95f;

    /**
     * default constructor
     */
    public Legend() {
        this.mTextSize = Utils.convertDpToPixel(10f);
        this.mXOffset = Utils.convertDpToPixel(5f);
        this.mYOffset = Utils.convertDpToPixel(3f); // 2
    }

    /**
     * Constructor. Provide entries for the legend.
     */
    public Legend(@Nullable LegendEntry[] entries) {
        this();

        this.mEntries = entries;
    }

    /**
     * This method sets the automatically computed colors for the legend. Use setCustom(...) to set custom colors.
     */
    public void setEntries(@NotNull List<LegendEntry> entries) {
        mEntries = entries.toArray(EmptyArray.LEGEND_ENTRY);
    }

    @NotNull
    public LegendEntry[] getEntries() {
        return mEntries;
    }

    /**
     * returns the maximum length in pixels across all legend labels + formsize
     * + formtotextspace
     *
     * @param p the paint object used for rendering the text
     */
    public float getMaximumEntryWidth(@NotNull Paint p) {
        float max = 0f;
        float maxFormSize = 0f;
        float formToTextSpace = Utils.convertDpToPixel(mFormToTextSpace);

        for (LegendEntry entry : mEntries) {
            final float formSize = Utils.convertDpToPixel(
                    Float.isNaN(entry.formSize)
                    ? mFormSize : entry.formSize);
            if (formSize > maxFormSize)
                maxFormSize = formSize;

            String label = entry.label;
            if (label == null) continue;

            float length = (float) Utils.calcTextWidth(p, label);

            if (length > max)
                max = length;
        }

        return max + maxFormSize + formToTextSpace;
    }

    /**
     * returns the maximum height in pixels across all legend labels
     *
     * @param p the paint object used for rendering the text
     */
    public float getMaximumEntryHeight(@NotNull Paint p) {
        float max = 0f;

        for (LegendEntry entry : mEntries) {
            String label = entry.label;
            if (label == null) continue;

            float length = (float) Utils.calcTextHeight(p, label);

            if (length > max)
                max = length;
        }

        return max;
    }

    @NotNull
    public LegendEntry[] getExtraEntries() {
        return mExtraEntries;
    }

    public void setExtra(@NotNull List<LegendEntry> entries) {
        mExtraEntries = entries.toArray(EmptyArray.LEGEND_ENTRY);
    }

    public void setExtra(@Nullable LegendEntry[] entries) {
        if (entries == null)
            entries = EmptyArray.LEGEND_ENTRY;

        mExtraEntries = entries;
    }

    /**
     * Entries that will be appended to the end of the auto calculated
     *   entries after calculating the legend.
     * (if the legend has already been calculated, you will need to call notifyDataSetChanged()
     *   to let the changes take effect)
     */
    public void setExtra(@NotNull int[] colors, @NotNull String[] labels) {
        List<LegendEntry> entries = new ArrayList<>();

        for (int i = 0; i < Math.min(colors.length, labels.length); i++) {
            final LegendEntry entry = new LegendEntry();
            entry.formColor = colors[i];
            entry.label = labels[i];

            if (entry.formColor == ColorTemplate.COLOR_SKIP ||
                    entry.formColor == 0)
                entry.form = FORM_NONE;
            else if (entry.formColor == ColorTemplate.COLOR_NONE)
                entry.form = FORM_EMPTY;

            entries.add(entry);
        }

        mExtraEntries = entries.toArray(EmptyArray.LEGEND_ENTRY);
    }

    /**
     * Sets a custom legend's entries array.
     * * A null label will start a group.
     * This will disable the feature that automatically calculates the legend
     *   entries from the datasets.
     * Call resetCustom() to re-enable automatic calculation (and then
     *   notifyDataSetChanged() is needed to auto-calculate the legend again)
     */
    public void setCustom(@NotNull LegendEntry[] entries) {
        mEntries = entries;
        mIsLegendCustom = true;
    }

    /**
     * Sets a custom legend's entries array.
     * * A null label will start a group.
     * This will disable the feature that automatically calculates the legend
     *   entries from the datasets.
     * Call resetCustom() to re-enable automatic calculation (and then
     *   notifyDataSetChanged() is needed to auto-calculate the legend again)
     */
    public void setCustom(@NotNull List<LegendEntry> entries) {
        mEntries = entries.toArray(EmptyArray.LEGEND_ENTRY);
        mIsLegendCustom = true;
    }

    /**
     * Calling this will disable the custom legend entries (set by
     * setCustom(...)). Instead, the entries will again be calculated
     * automatically (after notifyDataSetChanged() is called).
     */
    public void resetCustom() {
        mIsLegendCustom = false;
    }

    /**
     * @return true if a custom legend entries has been set default
     * false (automatic legend)
     */
    public boolean isLegendCustom() {
        return mIsLegendCustom;
    }

    /**
     * returns the horizontal alignment of the legend
     */
    @LegendHorizontalAlignment
    public int getHorizontalAlignment() {
        return mHorizontalAlignment;
    }

    /**
     * sets the horizontal alignment of the legend
     */
    public void setHorizontalAlignment(@LegendHorizontalAlignment int value) {
        mHorizontalAlignment = value;
    }

    /**
     * returns the vertical alignment of the legend
     */
    @LegendVerticalAlignment
    public int getVerticalAlignment() {
        return mVerticalAlignment;
    }

    /**
     * sets the vertical alignment of the legend
     */
    public void setVerticalAlignment(@LegendVerticalAlignment int value) {
        mVerticalAlignment = value;
    }

    /**
     * returns the orientation of the legend
     */
    @LegendOrientation
    public int getOrientation() {
        return mOrientation;
    }

    /**
     * sets the orientation of the legend
     */
    public void setOrientation(@LegendOrientation int value) {
        mOrientation = value;
    }

    /**
     * returns whether the legend will draw inside the chart or outside
     */
    public boolean isDrawInsideEnabled() {
        return mDrawInside;
    }

    /**
     * sets whether the legend will draw inside the chart or outside
     */
    public void setDrawInside(boolean value) {
        mDrawInside = value;
    }

    /**
     * returns the text direction of the legend
     */
    @LegendDirection
    public int getDirection() {
        return mDirection;
    }

    /**
     * sets the text direction of the legend
     */
    public void setDirection(@LegendDirection int pos) {
        mDirection = pos;
    }

    /**
     * returns the current form/shape that is set for the legend
     */
    @LegendForm
    public int getForm() {
        return mShape;
    }

    /**
     * sets the form/shape of the legend forms
     */
    public void setForm(@LegendForm int shape) {
        mShape = shape;
    }

    /**
     * sets the size in dp of the legend forms, default 8f
     */
    public void setFormSize(float size) {
        mFormSize = size;
    }

    /**
     * returns the size in dp of the legend forms
     */
    public float getFormSize() {
        return mFormSize;
    }

    /**
     * sets the line width in dp for forms that consist of lines, default 3f
     */
    public void setFormLineWidth(float size) {
        mFormLineWidth = size;
    }

    /**
     * returns the line width in dp for drawing forms that consist of lines
     */
    public float getFormLineWidth() {
        return mFormLineWidth;
    }

    /**
     * Sets the line dash path effect used for shapes that consist of lines.
     */
    public void setFormLineDashEffect(DashPathEffect dashPathEffect) {
        mFormLineDashEffect = dashPathEffect;
    }

    /**
     * @return The line dash path effect used for shapes that consist of lines.
     */
    public DashPathEffect getFormLineDashEffect() {
        return mFormLineDashEffect;
    }

    /**
     * returns the space between the legend entries on a horizontal axis in
     * pixels
     */
    public float getXEntrySpace() {
        return mXEntrySpace;
    }

    /**
     * sets the space between the legend entries on a horizontal axis in pixels,
     * converts to dp internally
     */
    public void setXEntrySpace(float space) {
        mXEntrySpace = space;
    }

    /**
     * returns the space between the legend entries on a vertical axis in pixels
     */
    public float getYEntrySpace() {
        return mYEntrySpace;
    }

    /**
     * sets the space between the legend entries on a vertical axis in pixels,
     * converts to dp internally
     */
    public void setYEntrySpace(float space) {
        mYEntrySpace = space;
    }

    /**
     * returns the space between the form and the actual label/text
     */
    public float getFormToTextSpace() {
        return mFormToTextSpace;
    }

    /**
     * sets the space between the form and the actual label/text, converts to dp
     * internally
     */
    public void setFormToTextSpace(float space) {
        this.mFormToTextSpace = space;
    }

    /**
     * returns the space that is left out between stacked forms (with no label)
     */
    public float getStackSpace() {
        return mStackSpace;
    }

    /**
     * sets the space that is left out between stacked forms (with no label)
     */
    public void setStackSpace(float space) {
        mStackSpace = space;
    }

    /**
     * the total width of the legend (needed width space)
     */
    public float mNeededWidth = 0f;

    /**
     * the total height of the legend (needed height space)
     */
    public float mNeededHeight = 0f;

    public float mTextHeightMax = 0f;

    public float mTextWidthMax = 0f;

    /**
     * flag that indicates if word wrapping is enabled
     */
    private boolean mWordWrapEnabled = false;

    /**
     * Should the legend word wrap? / this is currently supported only for:
     * BelowChartLeft, BelowChartRight, BelowChartCenter. / note that word
     * wrapping a legend takes a toll on performance. / you may want to set
     * maxSizePercent when word wrapping, to set the point where the text wraps.
     * / default: false
     */
    public void setWordWrapEnabled(boolean enabled) {
        mWordWrapEnabled = enabled;
    }

    /**
     * If this is set, then word wrapping the legend is enabled. This means the
     * legend will not be cut off if too long.
     *
     */
    public boolean isWordWrapEnabled() {
        return mWordWrapEnabled;
    }

    /**
     * The maximum relative size out of the whole chart view. / If the legend is
     * to the right/left of the chart, then this affects the width of the
     * legend. / If the legend is to the top/bottom of the chart, then this
     * affects the height of the legend. / If the legend is the center of the
     * piechart, then this defines the size of the rectangular bounds out of the
     * size of the "hole". / default: 0.95f (95%)
     */
    public float getMaxSizePercent() {
        return mMaxSizePercent;
    }

    /**
     * The maximum relative size out of the whole chart view. / If
     * the legend is to the right/left of the chart, then this affects the width
     * of the legend. / If the legend is to the top/bottom of the chart, then
     * this affects the height of the legend. / default: 0.95f (95%)
     */
    public void setMaxSizePercent(float maxSize) {
        mMaxSizePercent = maxSize;
    }

    private final List<FSize> mCalculatedLabelSizes = new ArrayList<>(16);
    private final List<Boolean> mCalculatedLabelBreakPoints = new ArrayList<>(16);
    private final List<FSize> mCalculatedLineSizes = new ArrayList<>(16);

    public List<FSize> getCalculatedLabelSizes() {
        return mCalculatedLabelSizes;
    }

    public List<Boolean> getCalculatedLabelBreakPoints() {
        return mCalculatedLabelBreakPoints;
    }

    public List<FSize> getCalculatedLineSizes() {
        return mCalculatedLineSizes;
    }

    /**
     * Calculates the dimensions of the Legend. This includes the maximum width
     * and height of a single entry, as well as the total width and height of
     * the Legend.
     */
    public void calculateDimensions(@NotNull Paint labelPaint, @NotNull ViewPortHandler viewPortHandler) {
        float defaultFormSize = Utils.convertDpToPixel(mFormSize);
        float stackSpace = Utils.convertDpToPixel(mStackSpace);
        float formToTextSpace = Utils.convertDpToPixel(mFormToTextSpace);
        float xEntrySpace = Utils.convertDpToPixel(mXEntrySpace);
        float yEntrySpace = Utils.convertDpToPixel(mYEntrySpace);
        boolean wordWrapEnabled = mWordWrapEnabled;
        LegendEntry[] entries = mEntries;
        int entryCount = entries.length;

        mTextWidthMax = getMaximumEntryWidth(labelPaint);
        mTextHeightMax = getMaximumEntryHeight(labelPaint);

        switch (mOrientation) {
            case ORIENTATION_VERTICAL: {
                float maxWidth = 0f, maxHeight = 0f, width = 0f;
                float labelLineHeight = Utils.getLineHeight(labelPaint);
                boolean wasStacked = false;

                for (int i = 0; i < entryCount; i++) {

                    LegendEntry e = entries[i];
                    boolean drawingForm = e.form != FORM_NONE;
                    float formSize = Float.isNaN(e.formSize)
                            ? defaultFormSize
                            : Utils.convertDpToPixel(e.formSize);
                    String label = e.label;

                    if (!wasStacked)
                        width = 0.f;

                    if (drawingForm) {
                        if (wasStacked)
                            width += stackSpace;
                        width += formSize;
                    }

                    // grouped forms have null labels
                    if (label != null) {

                        // make a step to the left
                        if (drawingForm && !wasStacked)
                            width += formToTextSpace;
                        else if (wasStacked) {
                            maxWidth = Math.max(maxWidth, width);
                            maxHeight += labelLineHeight + yEntrySpace;
                            width = 0.f;
                            wasStacked = false;
                        }

                        width += Utils.calcTextWidth(labelPaint, label);

                        maxHeight += labelLineHeight + yEntrySpace;
                    } else {
                        wasStacked = true;
                        width += formSize;
                        if (i < entryCount - 1)
                            width += stackSpace;
                    }

                    maxWidth = Math.max(maxWidth, width);
                }

                mNeededWidth = maxWidth;
                mNeededHeight = maxHeight;

                break;
            }
            case ORIENTATION_HORIZONTAL: {
                float labelLineHeight = Utils.getLineHeight(labelPaint);
                float labelLineSpacing = Utils.getLineSpacing(labelPaint) + yEntrySpace;
                float contentWidth = viewPortHandler.contentWidth() * mMaxSizePercent;

                // Start calculating layout
                float maxLineWidth = 0.f;
                float currentLineWidth = 0.f;
                float requiredWidth = 0.f;
                int stackedStartIndex = -1;

                mCalculatedLabelBreakPoints.clear();
                mCalculatedLabelSizes.clear();
                mCalculatedLineSizes.clear();

                for (int i = 0; i < entryCount; i++) {

                    LegendEntry e = entries[i];
                    boolean drawingForm = e.form != FORM_NONE;
                    float formSize = Float.isNaN(e.formSize)
                            ? defaultFormSize
                            : Utils.convertDpToPixel(e.formSize);
                    String label = e.label;

                    mCalculatedLabelBreakPoints.add(false);

                    if (stackedStartIndex == -1) {
                        // we are not stacking, so required width is for this label
                        // only
                        requiredWidth = 0.f;
                    } else {
                        // add the spacing appropriate for stacked labels/forms
                        requiredWidth += stackSpace;
                    }

                    // grouped forms have null labels
                    if (label != null) {

                        mCalculatedLabelSizes.add(Utils.calcTextSize(labelPaint, label));
                        requiredWidth += drawingForm ? formToTextSpace + formSize : 0.f;
                        requiredWidth += mCalculatedLabelSizes.get(i).width;
                    } else {

                        mCalculatedLabelSizes.add(FSize.getInstance(0.f, 0.f));
                        requiredWidth += drawingForm ? formSize : 0.f;

                        if (stackedStartIndex == -1) {
                            // mark this index as we might want to break here later
                            stackedStartIndex = i;
                        }
                    }

                    if (label != null || i == entryCount - 1) {

                        float requiredSpacing = currentLineWidth == 0.f ? 0.f : xEntrySpace;

                        if (!wordWrapEnabled // No word wrapping, it must fit.
                                // The line is empty, it must fit
                                || currentLineWidth == 0.f
                                // It simply fits
                                || (contentWidth - currentLineWidth >=
                                requiredSpacing + requiredWidth)) {
                            // Expand current line
                            currentLineWidth += requiredSpacing + requiredWidth;
                        } else { // It doesn't fit, we need to wrap a line

                            // Add current line size to array
                            mCalculatedLineSizes.add(FSize.getInstance(currentLineWidth, labelLineHeight));
                            maxLineWidth = Math.max(maxLineWidth, currentLineWidth);

                            // Start a new line
                            mCalculatedLabelBreakPoints.set(
                                    stackedStartIndex > -1 ? stackedStartIndex
                                            : i, true);
                            currentLineWidth = requiredWidth;
                        }

                        if (i == entryCount - 1) {
                            // Add last line size to array
                            mCalculatedLineSizes.add(FSize.getInstance(currentLineWidth, labelLineHeight));
                            maxLineWidth = Math.max(maxLineWidth, currentLineWidth);
                        }
                    }

                    stackedStartIndex = label != null ? -1 : stackedStartIndex;
                }

                mNeededWidth = maxLineWidth;
                mNeededHeight = labelLineHeight
                        * (float) (mCalculatedLineSizes.size())
                        + labelLineSpacing *
                        (float) (mCalculatedLineSizes.size() == 0
                                ? 0
                                : (mCalculatedLineSizes.size() - 1));

                break;
            }
        }

        mNeededHeight += mYOffset;
        mNeededWidth += mXOffset;
    }
}
