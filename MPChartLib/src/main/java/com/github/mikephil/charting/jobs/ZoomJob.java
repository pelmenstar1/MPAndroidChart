
package com.github.mikephil.charting.jobs;

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
public class ZoomJob extends ViewPortJob {
    private static final ObjectPool<ZoomJob> pool;

    static {
        pool = ObjectPool.create(1, ZoomJob::new);
        pool.setReplenishPercentage(0.5f);
    }

    protected float scaleX;
    protected float scaleY;

    @NotNull
    protected YAxis.AxisDependency axisDependency;

    private ZoomJob() {
        //noinspection ConstantConditions
        super(null, 0, 0, null, null);

        axisDependency = YAxis.AxisDependency.LEFT;
    }

    public ZoomJob(
            @NotNull ViewPortHandler viewPortHandler,
            float scaleX, float scaleY,
            float xValue, float yValue,
            @NotNull Transformer trans,
            @NotNull YAxis.AxisDependency axis,
            @NotNull View v) {
        super(viewPortHandler, xValue, yValue, trans, v);

        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.axisDependency = axis;
    }

    @NotNull
    public static ZoomJob getInstance(
            @NotNull ViewPortHandler viewPortHandler,
            float scaleX, float scaleY,
            float xValue, float yValue,
            @NotNull Transformer trans,
            @NotNull YAxis.AxisDependency axis,
            @NotNull View v) {
        ZoomJob result = pool.get();
        result.xValue = xValue;
        result.yValue = yValue;
        result.scaleX = scaleX;
        result.scaleY = scaleY;
        result.mViewPortHandler = viewPortHandler;
        result.mTrans = trans;
        result.axisDependency = axis;
        result.view = v;

        return result;
    }

    public static void recycleInstance(@NotNull ZoomJob instance) {
        pool.recycle(instance);
    }

    protected Matrix mRunMatrixBuffer = new Matrix();

    @Override
    public void run() {
        Matrix save = mRunMatrixBuffer;
        mViewPortHandler.zoom(scaleX, scaleY, save);
        mViewPortHandler.refresh(save, view, false);

        float yValsInView = ((BarLineChartBase) view).getAxis(axisDependency).mAxisRange / mViewPortHandler.getScaleY();
        float xValsInView = ((BarLineChartBase) view).getXAxis().mAxisRange / mViewPortHandler.getScaleX();

        pts[0] = xValue - xValsInView * 0.5f;
        pts[1] = yValue + yValsInView * 0.5f;

        mTrans.pointValuesToPixel(pts);

        mViewPortHandler.translate(pts, save);
        mViewPortHandler.refresh(save, view, false);

        ((BarLineChartBase) view).calculateOffsets();
        view.postInvalidate();

        recycleInstance(this);
    }
}
