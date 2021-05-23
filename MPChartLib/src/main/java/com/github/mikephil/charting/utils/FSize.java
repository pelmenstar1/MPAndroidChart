
package com.github.mikephil.charting.utils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Class for describing width and height dimensions in some arbitrary
 * unit. Replacement for the android.Util.SizeF which is available only on API >= 21.
 */
public final class FSize extends ObjectPool.Poolable{
    public float width;
    public float height;

    private static final ObjectPool<FSize> pool;

    static {
        pool = ObjectPool.create(256, FSize::new);
        pool.setReplenishPercentage(0.5f);
    }

    @NotNull
    public static FSize getInstance(final float width, final float height){
        FSize result = pool.get();
        result.width = width;
        result.height = height;

        return result;
    }

    public static void recycleInstance(@NotNull FSize instance){
        pool.recycle(instance);
    }

    public static void recycleInstances(@NotNull List<FSize> instances){
        pool.recycle(instances);
    }

    public FSize() {
    }

    public FSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(Object other) {
        if(other == this) return true;
        if(other == null || other.getClass() != getClass()) return false;

        FSize o = (FSize)other;

        return width == o.width && height == o.height;
    }

    @Override
    public String toString() {
        return width + "x" + height;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Float.floatToIntBits(width) ^ Float.floatToIntBits(height);
    }
}
