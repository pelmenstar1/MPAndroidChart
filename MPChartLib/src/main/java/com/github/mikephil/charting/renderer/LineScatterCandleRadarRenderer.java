package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Path;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineScatterCandleRadarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Philipp Jahoda on 11/07/15.
 */
public abstract class LineScatterCandleRadarRenderer<TDataSet extends ILineScatterCandleRadarDataSet<TEntry>, TEntry extends Entry> extends BarLineScatterCandleBubbleRenderer<TDataSet, TEntry> {
    /**
     * path that is used for drawing highlight-lines (drawLines(...) cannot be used because of dashes)
     */
    @NotNull
    private final Path mHighlightLinePath = new Path();

    public LineScatterCandleRadarRenderer(
            @NotNull ChartAnimator animator,
            @NotNull ViewPortHandler viewPortHandler
    ) {
        super(animator, viewPortHandler);
    }

    /**
     * Draws vertical & horizontal highlight-lines if enabled.
     *
     * @param x x-position of the highlight line intersection
     * @param y y-position of the highlight line intersection
     * @param set the currently drawn dataset
     */
    protected void drawHighlightLines(
            @NotNull Canvas c,
            float x, float y,
            @NotNull TDataSet set
    ) {
        // set color and stroke-width
        mHighlightPaint.setColor(set.getHighLightColor());
        mHighlightPaint.setStrokeWidth(set.getHighlightLineWidth());

        // draw highlighted lines (if enabled)
        mHighlightPaint.setPathEffect(set.getDashPathEffectHighlight());

        // draw vertical highlight lines
        if (set.isVerticalHighlightIndicatorEnabled()) {
            // create vertical path
            mHighlightLinePath.reset();
            mHighlightLinePath.moveTo(x, mViewPortHandler.contentTop());
            mHighlightLinePath.lineTo(x, mViewPortHandler.contentBottom());

            c.drawPath(mHighlightLinePath, mHighlightPaint);
        }

        // draw horizontal highlight lines
        if (set.isHorizontalHighlightIndicatorEnabled()) {
            // create horizontal path
            mHighlightLinePath.reset();
            mHighlightLinePath.moveTo(mViewPortHandler.contentLeft(), y);
            mHighlightLinePath.lineTo(mViewPortHandler.contentRight(), y);

            c.drawPath(mHighlightLinePath, mHighlightPaint);
        }
    }
}
