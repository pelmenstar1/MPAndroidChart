package com.github.mikephil.charting.listener;

import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.jetbrains.annotations.NotNull;

/**
 * TouchListener for Bar-, Line-, Scatter- and CandleStickChart with handles all
 * touch interaction. Longpress == Zoom out. Double-Tap == Zoom in.
 *
 * @author Philipp Jahoda
 */
public class BarLineChartTouchListener<TData extends BarLineScatterCandleBubbleData<TDataSet, TEntry>, TDataSet extends IBarLineScatterCandleBubbleDataSet<TEntry>, TEntry extends Entry> extends ChartTouchListener<BarLineChartBase<TData, TDataSet, TEntry>, TData, TDataSet, TEntry> {
    /**
     * the original touch-matrix from the chart
     */
    private Matrix mMatrix;

    /**
     * matrix for saving the original matrix state
     */
    private final Matrix mSavedMatrix = new Matrix();

    /**
     * point where the touch action started
     */
    private final MPPointF mTouchStartPoint = MPPointF.getInstance(0,0);

    /**
     * center between two pointers (fingers on the display)
     */
    private final MPPointF mTouchPointCenter = MPPointF.getInstance(0,0);

    private float mSavedXDist = 1f;
    private float mSavedYDist = 1f;
    private float mSavedDist = 1f;

    private TDataSet mClosestDataSetToTouch;

    /**
     * used for tracking velocity of dragging
     */
    private VelocityTracker mVelocityTracker;

    private long mDecelerationLastTime = 0;
    private final MPPointF mDecelerationCurrentPoint = MPPointF.getInstance(0,0);
    private final MPPointF mDecelerationVelocity = MPPointF.getInstance(0,0);

    /**
     * the distance of movement that will be counted as a drag
     */
    private float mDragTriggerDist;

    /**
     * the minimum distance between the pointers that will trigger a zoom gesture
     */
    private final float mMinScalePointerDistance;

    /**
     * Constructor with initialization parameters.
     *
     * @param chart               instance of the chart
     * @param touchMatrix         the touch-matrix of the chart
     * @param dragTriggerDistance the minimum movement distance that will be interpreted as a "drag" gesture in dp (3dp equals
     *                            to about 9 pixels on a 5.5" FHD screen)
     */
    public BarLineChartTouchListener(
            @NotNull BarLineChartBase<TData, TDataSet, TEntry> chart,
            @NotNull Matrix touchMatrix,
            float dragTriggerDistance) {
        super(chart);

        this.mMatrix = touchMatrix;
        this.mDragTriggerDist = Utils.convertDpToPixel(dragTriggerDistance);
        this.mMinScalePointerDistance = Utils.convertDpToPixel(3.5f);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(@NotNull View v, @NotNull MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        if (event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            if (mVelocityTracker != null) {
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            }
        }

        if (mTouchMode == NONE) {
            mGestureDetector.onTouchEvent(event);
        }

        if (!mChart.isDragEnabled() && (!mChart.isScaleXEnabled() && !mChart.isScaleYEnabled()))
            return true;

        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                startAction(event);
                stopDeceleration();
                saveTouchStart(event);

                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() >= 2) {
                    mChart.disableScroll();
                    saveTouchStart(event);

                    float x0 = event.getX();
                    float y0 = event.getY();

                    float x1 = event.getX(1);
                    float y1 = event.getY(1);

                    // get the distance between the pointers on the x-axis
                    mSavedXDist = Math.abs(x1 - x0);

                    // get the distance between the pointers on the y-axis
                    mSavedYDist = Math.abs(y1 - y0);

                    // get the total distance between the pointers
                    mSavedDist = (float)Math.sqrt(mSavedXDist * mSavedXDist + mSavedYDist * mSavedYDist);

                    if (mSavedDist > 10f) {
                        if (mChart.isPinchZoomEnabled()) {
                            mTouchMode = PINCH_ZOOM;
                        } else {
                            if (mChart.isScaleXEnabled() != mChart.isScaleYEnabled()) {
                                mTouchMode = mChart.isScaleXEnabled() ? X_ZOOM : Y_ZOOM;
                            } else {
                                mTouchMode = mSavedXDist > mSavedYDist ? X_ZOOM : Y_ZOOM;
                            }
                        }
                    }

                    // determine the touch-pointer center
                    mTouchPointCenter.x = (x0 + x1) * 0.5f;
                    mTouchPointCenter.y = (y0 + y1) * 0.5f;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mTouchMode == DRAG) {
                    mChart.disableScroll();

                    float x = mChart.isDragXEnabled() ? event.getX() - mTouchStartPoint.x : 0.f;
                    float y = mChart.isDragYEnabled() ? event.getY() - mTouchStartPoint.y : 0.f;

                    performDrag(event, x, y);
                } else if (mTouchMode == X_ZOOM || mTouchMode == Y_ZOOM || mTouchMode == PINCH_ZOOM) {
                    mChart.disableScroll();

                    if (mChart.isScaleXEnabled() || mChart.isScaleYEnabled())
                        performZoom(event);
                } else if (mTouchMode == NONE) {
                    float eSaveDistX = event.getX() - mTouchStartPoint.x;
                    float eSaveDistY = event.getY() - mTouchStartPoint.y;

                    float eSaveDist = (float)Math.sqrt(eSaveDistX * eSaveDistX + eSaveDistY * eSaveDistY);

                    // Math.abs(distance(event.getX(), mTouchStartPoint.x, event.getY(), mTouchStartPoint.y)) > mDragTriggerDist

                    if (eSaveDist >= mDragTriggerDist && mChart.isDragEnabled()) {
                        boolean shouldPan = !mChart.isFullyZoomedOut() ||
                                !mChart.hasNoDragOffset();

                        if (shouldPan) {
                            float distanceX = Math.abs(event.getX() - mTouchStartPoint.x);
                            float distanceY = Math.abs(event.getY() - mTouchStartPoint.y);

                            // Disable dragging in a direction that's disallowed
                            if ((mChart.isDragXEnabled() || distanceY >= distanceX) &&
                                    (mChart.isDragYEnabled() || distanceY <= distanceX)) {

                                mLastGesture = ChartGesture.DRAG;
                                mTouchMode = DRAG;
                            }
                        } else {
                            if (mChart.isHighlightPerDragEnabled()) {
                                mLastGesture = ChartGesture.DRAG;

                                if (mChart.isHighlightPerDragEnabled())
                                    performHighlightDrag(event);
                            }
                        }
                    }

                }
                break;

            case MotionEvent.ACTION_UP:
                VelocityTracker velocityTracker = mVelocityTracker;
                int pointerId = event.getPointerId(0);
                velocityTracker.computeCurrentVelocity(1000, Utils.getMaximumFlingVelocity());
                float velocityY = velocityTracker.getYVelocity(pointerId);
                float velocityX = velocityTracker.getXVelocity(pointerId);

                if (Math.abs(velocityX) > Utils.getMinimumFlingVelocity() ||
                        Math.abs(velocityY) > Utils.getMinimumFlingVelocity()) {

                    if (mTouchMode == DRAG && mChart.isDragDecelerationEnabled()) {
                        stopDeceleration();

                        mDecelerationLastTime = AnimationUtils.currentAnimationTimeMillis();

                        mDecelerationCurrentPoint.x = event.getX();
                        mDecelerationCurrentPoint.y = event.getY();

                        mDecelerationVelocity.x = velocityX;
                        mDecelerationVelocity.y = velocityY;

                        Utils.postInvalidateOnAnimation(mChart); // This causes computeScroll to fire, recommended for this by
                        // Google
                    }
                }

                if (mTouchMode == X_ZOOM ||
                        mTouchMode == Y_ZOOM ||
                        mTouchMode == PINCH_ZOOM ||
                        mTouchMode == POST_ZOOM) {
                    // Range might have changed, which means that Y-axis labels
                    // could have changed in size, affecting Y-axis size.
                    // So we need to recalculate offsets.
                    mChart.calculateOffsets();
                    mChart.postInvalidate();
                }

                mTouchMode = NONE;
                mChart.enableScroll();

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }

                endAction(event);

                break;
            case MotionEvent.ACTION_POINTER_UP:
                Utils.velocityTrackerPointerUpCleanUpIfNecessary(event, mVelocityTracker);

                mTouchMode = POST_ZOOM;
                break;

            case MotionEvent.ACTION_CANCEL:

                mTouchMode = NONE;
                endAction(event);
                break;
        }

        // perform the transformation, update the chart
        mMatrix = mChart.getViewPortHandler().refresh(mMatrix, mChart, true);

        return true; // indicate event was handled
    }

    // BELOW CODE PERFORMS THE ACTUAL TOUCH ACTIONS

    /**
     * Saves the current Matrix state and the touch-start point.
     */
    private void saveTouchStart(@NotNull MotionEvent event) {
        mSavedMatrix.set(mMatrix);
        mTouchStartPoint.x = event.getX();
        mTouchStartPoint.y = event.getY();

        mClosestDataSetToTouch = mChart.getDataSetByTouchPoint(event.getX(), event.getY());
    }

    /**
     * Performs all necessary operations needed for dragging.
     */
    private void performDrag(@NotNull MotionEvent event, float distX, float distY) {
        mLastGesture = ChartGesture.DRAG;

        mMatrix.set(mSavedMatrix);

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        // check if axis is inverted
        if (inverted()) {
            // if there is an inverted horizontalbarchart
            if (mChart instanceof HorizontalBarChart) {
                distX = -distX;
            } else {
                distY = -distY;
            }
        }

        mMatrix.postTranslate(distX, distY);

        if (l != null) {
            l.onChartTranslate(event, distX, distY);
        }
    }

    /**
     * Performs the all operations necessary for pinch and axis zoom.
     */
    private void performZoom(@NotNull MotionEvent event) {
        if (event.getPointerCount() >= 2) { // two finger zoom
            OnChartGestureListener l = mChart.getOnChartGestureListener();

            float x0 = event.getX();
            float y0 = event.getY();

            float x1 = event.getX(1);
            float y1 = event.getY(1);

            float xDist = Math.abs(x1 - x0);
            float yDist = Math.abs(y1 - y0);

            // get the distance between the pointers of the touch event
            float totalDist = (float)Math.sqrt(xDist * xDist + yDist * yDist);

            if (totalDist > mMinScalePointerDistance) {
                // get the translation
                MPPointF t = getTrans(mTouchPointCenter.x, mTouchPointCenter.y);
                ViewPortHandler h = mChart.getViewPortHandler();

                // take actions depending on the activated touch mode
                if (mTouchMode == PINCH_ZOOM) {
                    mLastGesture = ChartGesture.PINCH_ZOOM;

                    float scale = totalDist / mSavedDist; // total scale
                    boolean isZoomingOut = (scale < 1);

                    boolean canZoomMoreX = isZoomingOut ?
                            h.canZoomOutMoreX() :
                            h.canZoomInMoreX();

                    boolean canZoomMoreY = isZoomingOut ?
                            h.canZoomOutMoreY() :
                            h.canZoomInMoreY();

                    float scaleX = (mChart.isScaleXEnabled()) ? scale : 1f;
                    float scaleY = (mChart.isScaleYEnabled()) ? scale : 1f;

                    if (canZoomMoreY || canZoomMoreX) {
                        mMatrix.set(mSavedMatrix);
                        mMatrix.postScale(scaleX, scaleY, t.x, t.y);

                        if (l != null) {
                            l.onChartScale(event, scaleX, scaleY);
                        }
                    }

                } else if (mTouchMode == X_ZOOM && mChart.isScaleXEnabled()) {
                    mLastGesture = ChartGesture.X_ZOOM;

                    float scaleX = xDist / mSavedXDist; // x-axis scale

                    boolean isZoomingOut = (scaleX < 1);
                    boolean canZoomMoreX = isZoomingOut ?
                            h.canZoomOutMoreX() :
                            h.canZoomInMoreX();

                    if (canZoomMoreX) {

                        mMatrix.set(mSavedMatrix);
                        mMatrix.postScale(scaleX, 1f, t.x, t.y);

                        if (l != null)
                            l.onChartScale(event, scaleX, 1f);
                    }

                } else if (mTouchMode == Y_ZOOM && mChart.isScaleYEnabled()) {
                    mLastGesture = ChartGesture.Y_ZOOM;

                    float scaleY = yDist / mSavedYDist; // y-axis scale

                    boolean isZoomingOut = (scaleY < 1);
                    boolean canZoomMoreY = isZoomingOut ?
                            h.canZoomOutMoreY() :
                            h.canZoomInMoreY();

                    if (canZoomMoreY) {
                        mMatrix.set(mSavedMatrix);
                        mMatrix.postScale(1f, scaleY, t.x, t.y);

                        if (l != null) {
                            l.onChartScale(event, 1f, scaleY);
                        }
                    }
                }

                MPPointF.recycleInstance(t);
            }
        }
    }

    /**
     * Highlights upon dragging, generates callbacks for the selection-listener.
     */
    private void performHighlightDrag(@NotNull MotionEvent e) {
        Highlight h = mChart.getHighlightByTouchPoint(e.getX(), e.getY());

        if (h != null && !h.equalTo(mLastHighlighted)) {
            mLastHighlighted = h;
            mChart.highlightValue(h, true);
        }
    }

    /**
     * Returns a recyclable MPPointF instance.
     * returns the correct translation depending on the provided x and y touch
     * points
     */
    @NotNull
    public MPPointF getTrans(float x, float y) {
        ViewPortHandler vph = mChart.getViewPortHandler();

        float xTrans = x - vph.offsetLeft();
        float yTrans;

        // check if axis is inverted
        if (inverted()) {
            yTrans = -(y - vph.offsetTop());
        } else {
            yTrans = -(mChart.getMeasuredHeight() - y - vph.offsetBottom());
        }

        return MPPointF.getInstance(xTrans, yTrans);
    }

    /**
     * Returns true if the current touch situation should be interpreted as inverted, false if not.
     */
    private boolean inverted() {
        return (mClosestDataSetToTouch == null && mChart.isAnyAxisInverted()) || (mClosestDataSetToTouch != null
                && mChart.isInverted(mClosestDataSetToTouch.getAxisDependency()));
    }

    // GETTERS AND GESTURE RECOGNITION BELOW

    /**
     * returns the matrix object the listener holds
     */
    @NotNull
    public Matrix getMatrix() {
        return mMatrix;
    }

    /**
     * Sets the minimum distance that will be interpreted as a "drag" by the chart in dp.
     * Default: 3dp
     */
    public void setDragTriggerDist(float dragTriggerDistance) {
        this.mDragTriggerDist = Utils.convertDpToPixel(dragTriggerDistance);
    }

    @Override
    public boolean onDoubleTap(@NotNull MotionEvent e) {
        mLastGesture = ChartGesture.DOUBLE_TAP;

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {
            l.onChartDoubleTapped(e);
        }

        // check if double-tap zooming is enabled
        if (mChart.isDoubleTapToZoomEnabled() && mChart.getData().getEntryCount() > 0) {
            MPPointF trans = getTrans(e.getX(), e.getY());

            float scaleX = mChart.isScaleXEnabled() ? 1.4f : 1f;
            float scaleY = mChart.isScaleYEnabled() ? 1.4f : 1f;

            mChart.zoom(scaleX, scaleY, trans.x, trans.y);

            if (mChart.isLogEnabled())
                Log.i("BarlineChartTouch", "Double-Tap, Zooming In, x: " + trans.x + ", y: " + trans.y);

            if (l != null) {
                l.onChartScale(e, scaleX, scaleY);
            }

            MPPointF.recycleInstance(trans);
        }

        return super.onDoubleTap(e);
    }

    @Override
    public void onLongPress(@NotNull MotionEvent e) {
        mLastGesture = ChartGesture.LONG_PRESS;

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {
            l.onChartLongPressed(e);
        }
    }

    @Override
    public boolean onSingleTapUp(@NotNull MotionEvent e) {
        mLastGesture = ChartGesture.SINGLE_TAP;

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {
            l.onChartSingleTapped(e);
        }

        if (!mChart.isHighlightPerTapEnabled()) {
            return false;
        }

        Highlight h = mChart.getHighlightByTouchPoint(e.getX(), e.getY());
        performHighlight(h, e);

        return super.onSingleTapUp(e);
    }

    @Override
    public boolean onFling(@NotNull MotionEvent e1, @NotNull MotionEvent e2, float velocityX, float velocityY) {
        mLastGesture = ChartGesture.FLING;

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {
            l.onChartFling(e1, e2, velocityX, velocityY);
        }

        return super.onFling(e1, e2, velocityX, velocityY);
    }

    public void stopDeceleration() {
        mDecelerationVelocity.x = 0;
        mDecelerationVelocity.y = 0;
    }

    public void computeScroll() {
        if (mDecelerationVelocity.x == 0.f && mDecelerationVelocity.y == 0.f)
            return; // There's no deceleration in progress

        long currentTime = AnimationUtils.currentAnimationTimeMillis();

        mDecelerationVelocity.x *= mChart.getDragDecelerationFrictionCoef();
        mDecelerationVelocity.y *= mChart.getDragDecelerationFrictionCoef();

        float timeInterval = (float) (currentTime - mDecelerationLastTime) / 1000.f;

        float distanceX = mDecelerationVelocity.x * timeInterval;
        float distanceY = mDecelerationVelocity.y * timeInterval;

        mDecelerationCurrentPoint.x += distanceX;
        mDecelerationCurrentPoint.y += distanceY;

        MotionEvent event = MotionEvent.obtain(
                currentTime,
                currentTime,
                MotionEvent.ACTION_MOVE,
                mDecelerationCurrentPoint.x,
                mDecelerationCurrentPoint.y,
                0
        );

        float dragDistanceX = mChart.isDragXEnabled() ? mDecelerationCurrentPoint.x - mTouchStartPoint.x : 0f;
        float dragDistanceY = mChart.isDragYEnabled() ? mDecelerationCurrentPoint.y - mTouchStartPoint.y : 0f;

        performDrag(event, dragDistanceX, dragDistanceY);

        event.recycle();
        mMatrix = mChart.getViewPortHandler().refresh(mMatrix, mChart, false);

        mDecelerationLastTime = currentTime;

        if (Math.abs(mDecelerationVelocity.x) >= 0.01 || Math.abs(mDecelerationVelocity.y) >= 0.01)
            Utils.postInvalidateOnAnimation(mChart); // This causes computeScroll to fire, recommended for this by Google
        else {
            // Range might have changed, which means that Y-axis labels
            // could have changed in size, affecting Y-axis size.
            // So we need to recalculate offsets.
            mChart.calculateOffsets();
            mChart.postInvalidate();

            stopDeceleration();
        }
    }
}
