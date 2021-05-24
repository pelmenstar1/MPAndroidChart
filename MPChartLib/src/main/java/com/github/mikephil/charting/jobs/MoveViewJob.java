
package com.github.mikephil.charting.jobs;

import android.view.View;

import com.github.mikephil.charting.utils.ObjectPool;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Philipp Jahoda on 19/02/16.
 */
public class MoveViewJob extends ViewPortJob {
    private static final ObjectPool<MoveViewJob> pool;

    static {
        pool = ObjectPool.create(2, MoveViewJob::new);
        pool.setReplenishPercentage(0.5f);
    }

    private MoveViewJob() {
        //noinspection ConstantConditions
        super(null, 0,0, null, null);
    }

    public MoveViewJob(
            @NotNull ViewPortHandler viewPortHandler,
            float xValue, float yValue,
            @NotNull Transformer trans,
            @NotNull View v) {
        super(viewPortHandler, xValue, yValue, trans, v);
    }

    @Override
    public void run() {
        pts[0] = xValue;
        pts[1] = yValue;

        mTrans.pointValuesToPixel(pts);
        mViewPortHandler.centerViewPort(pts, view);

        recycleInstance(this);
    }

    @NotNull
    public static MoveViewJob getInstance(
            @NotNull ViewPortHandler viewPortHandler,
            float xValue, float yValue,
            @NotNull Transformer trans,
            @NotNull View v){
        MoveViewJob result = pool.get();
        result.mViewPortHandler = viewPortHandler;
        result.xValue = xValue;
        result.yValue = yValue;
        result.mTrans = trans;
        result.view = v;

        return result;
    }

    public static void recycleInstance(@NotNull MoveViewJob instance){
        pool.recycle(instance);
    }
}
