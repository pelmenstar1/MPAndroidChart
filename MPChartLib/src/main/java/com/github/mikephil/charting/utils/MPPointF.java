package com.github.mikephil.charting.utils;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Tony Patino on 6/24/16.
 */
public class MPPointF extends ObjectPool.Poolable implements Parcelable {
    private static final ObjectPool<MPPointF> pool;

    public float x;
    public float y;

    static {
        pool = ObjectPool.create(32, MPPointF::new);
        pool.setReplenishPercentage(0.5f);
    }

    public MPPointF() {
    }

    public MPPointF(float x, float y) {
        this.x = x;
        this.y = y;
    }

    private MPPointF(@NotNull Parcel in) {
        x = in.readFloat();
        y = in.readFloat();
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    @NotNull
    public static MPPointF getInstance(float x, float y) {
        MPPointF result = pool.get();
        result.x = x;
        result.y = y;

        return result;
    }

    @NotNull
    public static MPPointF getInstance() {
        return pool.get();
    }

    @NotNull
    public static MPPointF getInstance(MPPointF copy) {
        MPPointF result = pool.get();
        result.x = copy.x;
        result.y = copy.y;

        return result;
    }

    public static void recycleInstance(@NotNull MPPointF instance){
        pool.recycle(instance);
    }

    public static void recycleInstances(@NotNull List<MPPointF> instances){
        pool.recycle(instances);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NotNull Parcel dest, int flags) {
        dest.writeFloat(x);
        dest.writeFloat(y);
    }

    public static final Parcelable.Creator<MPPointF> CREATOR = new Parcelable.Creator<MPPointF>() {
        @NotNull
        public MPPointF createFromParcel(@NotNull Parcel in) {
            return new MPPointF(in);
        }

        @NotNull
        public MPPointF[] newArray(int size) {
            return new MPPointF[size];
        }
    };
}