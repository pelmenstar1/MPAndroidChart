
package com.github.mikephil.charting.jobs;

import android.view.View;

import com.github.mikephil.charting.utils.ObjectPool;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.jetbrains.annotations.NotNull;

/**
 * Runnable that is used for viewport modifications since they cannot be
 * executed at any time. This can be used to delay the execution of viewport
 * modifications until the onSizeChanged(...) method of the chart-view is called.
 * This is especially important if viewport modifying methods are called on the chart
 * directly after initialization.
 * 
 * @author Philipp Jahoda
 */
public abstract class ViewPortJob extends ObjectPool.Poolable implements Runnable {
    @NotNull
    protected float[] pts = new float[2];

    @NotNull
    protected ViewPortHandler mViewPortHandler;
    protected float xValue;
    protected float yValue;

    @NotNull
    protected Transformer mTrans;

    @NotNull
    protected View view;

    public ViewPortJob(
            @NotNull ViewPortHandler viewPortHandler,
            float xValue, float yValue,
            @NotNull Transformer trans,
            @NotNull View v
    ) {
        this.mViewPortHandler = viewPortHandler;
        this.xValue = xValue;
        this.yValue = yValue;
        this.mTrans = trans;
        this.view = v;
    }

    public float getXValue() {
        return xValue;
    }

    public float getYValue() {
        return yValue;
    }
}
