
package com.github.mikephil.charting.utils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Point encapsulating two double values.
 *
 * @author Philipp Jahoda
 */
public class MPPointD extends ObjectPool.Poolable {
    private static final ObjectPool<MPPointD> pool;

    static {
        pool = ObjectPool.create(64, MPPointD::new);
        pool.setReplenishPercentage(0.5f);
    }

    public double x;
    public double y;

    public MPPointD() {
    }

    public MPPointD(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @NotNull
    public static MPPointD getInstance(double x, double y){
        MPPointD result = pool.get();
        result.x = x;
        result.y = y;
        return result;
    }

    public static void recycleInstance(@NotNull MPPointD instance){
        pool.recycle(instance);
    }

    public static void recycleInstances(@NotNull List<MPPointD> instances){
        pool.recycle(instances);
    }

    @Override
    @NotNull
    public String toString() {
        return "MPPointD, x: " + x + ", y: " + y;
    }
}