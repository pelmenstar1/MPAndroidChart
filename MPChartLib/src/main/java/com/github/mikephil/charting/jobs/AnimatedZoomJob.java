package com.github.mikephil.charting.jobs;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.view.View;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.utils.ObjectPool;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Philipp Jahoda on 19/02/16.
 */
@SuppressLint("NewApi")
public class AnimatedZoomJob extends AnimatedViewPortJob implements Animator.AnimatorListener {
    private static final ObjectPool<AnimatedZoomJob> pool;

    static {
        pool = ObjectPool.create(8, AnimatedZoomJob::new);
    }

    protected float zoomOriginX;
    protected float zoomOriginY;

    protected float zoomCenterX;
    protected float zoomCenterY;

    @NotNull
    protected YAxis yAxis;

    protected float xAxisRange;

    private AnimatedZoomJob() {
        //noinspection ConstantConditions
        super(null, 0, 0, null, null, 0,0,0);

        //noinspection ConstantConditions
        yAxis = null;
    }

    @SuppressLint("NewApi")
    public AnimatedZoomJob(
            @NotNull ViewPortHandler viewPortHandler,
            @NotNull View v,
            @NotNull Transformer trans,
            @NotNull YAxis axis,
            float xAxisRange,
            float scaleX, float scaleY,
            float xOrigin, float yOrigin,
            float zoomCenterX, float zoomCenterY,
            float zoomOriginX, float zoomOriginY,
            long duration) {
        super(viewPortHandler, scaleX, scaleY, trans, v, xOrigin, yOrigin, duration);

        this.zoomCenterX = zoomCenterX;
        this.zoomCenterY = zoomCenterY;
        this.zoomOriginX = zoomOriginX;
        this.zoomOriginY = zoomOriginY;
        this.animator.addListener(this);
        this.yAxis = axis;
        this.xAxisRange = xAxisRange;
    }

    protected Matrix mOnAnimationUpdateMatrixBuffer = new Matrix();

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        float scaleX = xOrigin + (xValue - xOrigin) * phase;
        float scaleY = yOrigin + (yValue - yOrigin) * phase;

        Matrix save = mOnAnimationUpdateMatrixBuffer;
        mViewPortHandler.setZoom(scaleX, scaleY, save);
        mViewPortHandler.refresh(save, view, false);

        float valsInView = yAxis.mAxisRange / mViewPortHandler.getScaleY();
        float xsInView =  xAxisRange / mViewPortHandler.getScaleX();

        pts[0] = zoomOriginX + ((zoomCenterX - xsInView * 0.5f) - zoomOriginX) * phase;
        pts[1] = zoomOriginY + ((zoomCenterY + valsInView * 0.5f) - zoomOriginY) * phase;

        mTrans.pointValuesToPixel(pts);

        mViewPortHandler.translate(pts, save);
        mViewPortHandler.refresh(save, view, true);
    }

    @NotNull
    public static AnimatedZoomJob getInstance(
            @NotNull ViewPortHandler viewPortHandler,
            @NotNull View v,
            @NotNull Transformer trans,
            @NotNull YAxis axis,
            float xAxisRange,
            float scaleX, float scaleY,
            float xOrigin, float yOrigin,
            float zoomCenterX, float zoomCenterY,
            float zoomOriginX, float zoomOriginY,
            long duration) {
        AnimatedZoomJob result = pool.get();
        result.mViewPortHandler = viewPortHandler;
        result.xValue = scaleX;
        result.yValue = scaleY;
        result.mTrans = trans;
        result.view = v;
        result.xOrigin = xOrigin;
        result.yOrigin = yOrigin;
        result.yAxis = axis;
        result.xAxisRange = xAxisRange;
        result.resetAnimator();
        result.animator.setDuration(duration);
        return result;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        ((BarLineChartBase<?, ?, ?>) view).calculateOffsets();
        view.postInvalidate();
    }

    @Override
    public void onAnimationCancel(Animator animation) {
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
    }

    @Override
    public void recycleSelf() {
    }

    @Override
    public void onAnimationStart(Animator animation) {
    }
}
