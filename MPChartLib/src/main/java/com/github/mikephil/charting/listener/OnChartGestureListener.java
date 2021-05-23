package com.github.mikephil.charting.listener;

import android.view.MotionEvent;

import org.jetbrains.annotations.NotNull;

/**
 * Listener for callbacks when doing gestures on the chart.
 *
 * @author Philipp Jahoda
 */
public interface OnChartGestureListener {
    /**
     * Callbacks when a touch-gesture has started on the chart (ACTION_DOWN)
     */
    void onChartGestureStart(@NotNull MotionEvent me, @NotNull ChartTouchListener.ChartGesture lastPerformedGesture);

    /**
     * Callbacks when a touch-gesture has ended on the chart (ACTION_UP, ACTION_CANCEL)
     */
    void onChartGestureEnd(@NotNull MotionEvent me, @NotNull ChartTouchListener.ChartGesture lastPerformedGesture);

    /**
     * Callbacks when the chart is longpressed.
     */
    void onChartLongPressed(@NotNull MotionEvent me);

    /**
     * Callbacks when the chart is double-tapped.
     */
    void onChartDoubleTapped(@NotNull MotionEvent me);

    /**
     * Callbacks when the chart is single-tapped.
     */
    void onChartSingleTapped(@NotNull MotionEvent me);

    /**
     * Callbacks then a fling gesture is made on the chart.
     */
    void onChartFling(@NotNull MotionEvent me1, @NotNull MotionEvent me2, float velocityX, float velocityY);

    /**
     * Callbacks when the chart is scaled / zoomed via pinch zoom / double-tap gesture.
     *
     * @param scaleX scalefactor on the x-axis
     * @param scaleY scalefactor on the y-axis
     */
    void onChartScale(@NotNull MotionEvent me, float scaleX, float scaleY);

    /**
     * Callbacks when the chart is moved / translated via drag gesture.
     *
     * @param dX translation distance on the x-axis
     * @param dY translation distance on the y-axis
     */
    void onChartTranslate(@NotNull MotionEvent me, float dX, float dY);
}
