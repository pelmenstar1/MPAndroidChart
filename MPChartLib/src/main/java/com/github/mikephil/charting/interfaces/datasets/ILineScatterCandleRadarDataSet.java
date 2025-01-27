package com.github.mikephil.charting.interfaces.datasets;

import android.graphics.DashPathEffect;

import com.github.mikephil.charting.data.Entry;

import org.jetbrains.annotations.Nullable;

/**
 * Created by Philipp Jahoda on 21/10/15.
 */
public interface ILineScatterCandleRadarDataSet<T extends Entry> extends IBarLineScatterCandleBubbleDataSet<T> {
    /**
     * Returns true if vertical highlight indicator lines are enabled (drawn)
     */
    boolean isVerticalHighlightIndicatorEnabled();

    /**
     * Returns true if vertical highlight indicator lines are enabled (drawn)
     */
    boolean isHorizontalHighlightIndicatorEnabled();

    /**
     * Returns the line-width in which highlight lines are to be drawn.
     */
    float getHighlightLineWidth();

    /**
     * Returns the DashPathEffect that is used for highlighting.
     */
    @Nullable
    DashPathEffect getDashPathEffectHighlight();
}
