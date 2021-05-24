package com.github.mikephil.charting.jobs;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.view.View;

import com.github.mikephil.charting.utils.ObjectPool;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Philipp Jahoda on 19/02/16.
 */
@SuppressLint("NewApi")
public class AnimatedMoveViewJob extends AnimatedViewPortJob {
    private static final ObjectPool<AnimatedMoveViewJob> pool;

    static {
        pool = ObjectPool.create(4, AnimatedMoveViewJob::new);
        pool.setReplenishPercentage(0.5f);
    }

    private AnimatedMoveViewJob() {
        //noinspection ConstantConditions
        super(null, 0,0, null, null, 0, 0, 0);
    }

    public AnimatedMoveViewJob(
            @NotNull ViewPortHandler viewPortHandler,
            float xValue, float yValue,
            @NotNull Transformer trans,
            @NotNull View v,
            float xOrigin, float yOrigin,
            long duration
    ) {
        super(viewPortHandler, xValue, yValue, trans, v, xOrigin, yOrigin, duration);
    }

    @NotNull
    public static AnimatedMoveViewJob getInstance(
            @NotNull ViewPortHandler viewPortHandler,
            float xValue, float yValue,
            @NotNull Transformer trans,
            @NotNull View v,
            float xOrigin, float yOrigin,
            long duration) {
        AnimatedMoveViewJob result = pool.get();
        result.mViewPortHandler = viewPortHandler;
        result.xValue = xValue;
        result.yValue = yValue;
        result.mTrans = trans;
        result.view = v;
        result.xOrigin = xOrigin;
        result.yOrigin = yOrigin;
        result.animator.setDuration(duration);

        return result;
    }

    public static void recycleInstance(@NotNull AnimatedMoveViewJob instance) {
        pool.recycle(instance);
    }

    public void recycleSelf(){
        recycleInstance(this);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        pts[0] = xOrigin + (xValue - xOrigin) * phase;
        pts[1] = yOrigin + (yValue - yOrigin) * phase;

        mTrans.pointValuesToPixel(pts);
        mViewPortHandler.centerViewPort(pts, view);
    }
}
