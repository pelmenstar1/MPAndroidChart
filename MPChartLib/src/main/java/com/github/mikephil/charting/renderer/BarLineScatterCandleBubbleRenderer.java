package com.github.mikephil.charting.renderer;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Philipp Jahoda on 09/06/16.
 */
public abstract class BarLineScatterCandleBubbleRenderer<TDataSet extends IBarLineScatterCandleBubbleDataSet<TEntry>, TEntry extends Entry> extends DataRenderer {
    /**
     * buffer for storing the current minimum and maximum visible x
     */
    @NotNull
    protected XBounds mXBounds = new XBounds();

    public BarLineScatterCandleBubbleRenderer(
            @NotNull ChartAnimator animator,
            @NotNull ViewPortHandler viewPortHandler
    ) {
        super(animator, viewPortHandler);
    }

    /**
     * Returns true if the DataSet values should be drawn, false if not.
     */
    protected boolean shouldDrawValues(@NotNull TDataSet set) {
        return set.isVisible() && (set.isDrawValuesEnabled() || set.isDrawIconsEnabled());
    }

    /**
     * Checks if the provided entry object is in bounds for drawing considering the current animation phase.
     */
    protected boolean isInBoundsX(
            @NotNull TEntry e,
            @NotNull TDataSet set
    ) {
        float entryIndex = set.getEntryIndex(e);

        return !(entryIndex >= set.getEntryCount() * mAnimator.getPhaseX());
    }

    /**
     * Class representing the bounds of the current viewport in terms of indices in the values array of a DataSet.
     */
    protected final class XBounds {
        /**
         * minimum visible entry index
         */
        public int min;

        /**
         * maximum visible entry index
         */
        public int max;

        /**
         * range of visible entry indices
         */
        public int range;

        /**
         * Calculates the minimum and maximum x values as well as the range between them.
         *
         */
        public void set(
                BarLineScatterCandleBubbleDataProvider<? extends ChartData<TDataSet, TEntry>, TDataSet, TEntry> chart,
                @NotNull TDataSet dataSet
        ) {
            float phaseX = Math.max(0f, Math.min(1f, mAnimator.getPhaseX()));

            float low = chart.getLowestVisibleX();
            float high = chart.getHighestVisibleX();

            TEntry entryFrom = dataSet.getEntryForXValue(low, Float.NaN, DataSet.ROUNDING_DOWN);
            TEntry entryTo = dataSet.getEntryForXValue(high, Float.NaN, DataSet.ROUNDING_UP);

            min = entryFrom == null ? 0 : dataSet.getEntryIndex(entryFrom);
            max = entryTo == null ? 0 : dataSet.getEntryIndex(entryTo);
            range = (int) ((max - min) * phaseX);
        }
    }
}
