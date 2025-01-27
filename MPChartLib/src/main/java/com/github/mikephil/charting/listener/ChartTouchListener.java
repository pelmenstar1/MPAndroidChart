package com.github.mikephil.charting.listener;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.IntDef;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by philipp on 12/06/15.
 */
public abstract class ChartTouchListener<TChart extends Chart<TData, TDataSet, TEntry>, TData extends ChartData<TDataSet, TEntry>, TDataSet extends IDataSet<TEntry>, TEntry extends Entry> extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {
    public static final int GESTURE_NONE = 0;
    public static final int GESTURE_DRAG = 1;
    public static final int GESTURE_X_ZOOM = 2;
    public static final int GESTURE_Y_ZOOM = 3;
    public static final int GESTURE_PINCH_ZOOM = 4;
    public static final int GESTURE_ROTATE = 5;
    public static final int GESTURE_SINGLE_TAP = 6;
    public static final int GESTURE_DOUBLE_TAP = 7;
    public static final int GESTURE_LONG_PRESS = 8;
    public static final int GESTURE_FLING = 9;

    @IntDef(value = {
            GESTURE_NONE,
            GESTURE_DRAG,
            GESTURE_X_ZOOM, GESTURE_Y_ZOOM,
            GESTURE_PINCH_ZOOM,
            GESTURE_ROTATE,
            GESTURE_SINGLE_TAP, GESTURE_DOUBLE_TAP,
            GESTURE_LONG_PRESS,
            GESTURE_FLING
    })
    public @interface ChartGesture {
    }

    /**
     * the last touch gesture that has been performed
     **/
    @ChartGesture
    protected int mLastGesture = GESTURE_NONE;

    // states
    protected static final int NONE = 0;
    protected static final int DRAG = 1;
    protected static final int X_ZOOM = 2;
    protected static final int Y_ZOOM = 3;
    protected static final int PINCH_ZOOM = 4;
    protected static final int POST_ZOOM = 5;
    protected static final int ROTATE = 6;

    /**
     * integer field that holds the current touch-state
     */
    protected int mTouchMode = NONE;

    /**
     * the last highlighted object (via touch)
     */
    protected Highlight mLastHighlighted;

    /**
     * the gesturedetector used for detecting taps and longpresses, ...
     */
    protected GestureDetector mGestureDetector;

    /**
     * the chart the listener represents
     */
    protected TChart mChart;

    public ChartTouchListener(TChart chart) {
        this.mChart = chart;

        mGestureDetector = new GestureDetector(chart.getContext(), this);
    }

    /**
     * Calls the OnChartGestureListener to do the start callback
     */
    public void startAction(@NotNull MotionEvent me) {
        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {
            l.onChartGestureStart(me, mLastGesture);
        }
    }

    /**
     * Calls the OnChartGestureListener to do the end callback
     */
    public void endAction(@NotNull MotionEvent me) {
        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {
            l.onChartGestureEnd(me, mLastGesture);
        }
    }

    /**
     * Sets the last value that was highlighted via touch.
     */
    public void setLastHighlighted(@Nullable Highlight high) {
        mLastHighlighted = high;
    }

    /**
     * returns the touch mode the listener is currently in
     */
    public int getTouchMode() {
        return mTouchMode;
    }

    /**
     * Returns the last gesture that has been performed on the chart.
     */
    @ChartGesture
    public int getLastGesture() {
        return mLastGesture;
    }

    /**
     * Perform a highlight operation.
     */
    protected void performHighlight(@Nullable Highlight h, @NotNull MotionEvent e) {
        if (h == null || h.equalTo(mLastHighlighted)) {
            mChart.highlightValue(null, true);
            mLastHighlighted = null;
        } else {
            mChart.highlightValue(h, true);
            mLastHighlighted = h;
        }
    }

    /**
     * returns the distance between two points
     */
    protected static float distance(float eventX, float startX, float eventY, float startY) {
        float dx = eventX - startX;
        float dy = eventY - startY;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
