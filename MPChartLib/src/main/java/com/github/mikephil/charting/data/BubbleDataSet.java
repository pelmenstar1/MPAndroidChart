
package com.github.mikephil.charting.data;

import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;
import com.github.mikephil.charting.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BubbleDataSet extends BarLineScatterCandleBubbleDataSet<BubbleEntry> implements IBubbleDataSet {
    protected float mMaxSize;
    protected boolean mNormalizeSize = true;

    private float mHighlightCircleWidth = 2.5f;

    public BubbleDataSet(@NotNull List<BubbleEntry> entries, @Nullable String label) {
        super(entries, label);
    }

    @Override
    public void setHighlightCircleWidth(float width) {
        mHighlightCircleWidth = Utils.convertDpToPixel(width);
    }

    @Override
    public float getHighlightCircleWidth() {
        return mHighlightCircleWidth;
    }

    @Override
    protected void calcMinMax(@NotNull BubbleEntry e) {
        super.calcMinMax(e);

        float size = e.getSize();

        if (size > mMaxSize) {
            mMaxSize = size;
        }
    }

    @Override
    @NotNull
    public DataSet<BubbleEntry> copy() {
        ArrayList<BubbleEntry> entries = new ArrayList<>();
        for (int i = 0; i < mEntries.size(); i++) {
            entries.add(mEntries.get(i).copy());
        }

        BubbleDataSet copied = new BubbleDataSet(entries, getLabel());
        copy(copied);
        return copied;
    }

    protected void copy(@NotNull BubbleDataSet bubbleDataSet) {
        bubbleDataSet.mHighlightCircleWidth = mHighlightCircleWidth;
        bubbleDataSet.mNormalizeSize = mNormalizeSize;
    }

    @Override
    public float getMaxSize() {
        return mMaxSize;
    }

    @Override
    public boolean isNormalizeSizeEnabled() {
        return mNormalizeSize;
    }

    public void setNormalizeSizeEnabled(boolean normalizeSize) {
        mNormalizeSize = normalizeSize;
    }
}
