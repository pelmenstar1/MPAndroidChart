package com.github.mikephil.charting.data;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Philipp Jahoda
 */
@SuppressLint("ParcelCreator")
public class PieEntry extends Entry {
    @Nullable
    private String label;

    public PieEntry(float value) {
        super(0f, value);
    }

    public PieEntry(float value, @Nullable Object data) {
        super(0f, value, data);
    }

    public PieEntry(float value, @Nullable Drawable icon) {
        super(0f, value, icon);
    }

    public PieEntry(float value, @Nullable Drawable icon, @Nullable Object data) {
        super(0f, value, icon, data);
    }

    public PieEntry(float value, @Nullable String label) {
        super(0f, value);
        this.label = label;
    }

    public PieEntry(float value, @Nullable String label, @Nullable Object data) {
        super(0f, value, data);
        this.label = label;
    }

    public PieEntry(float value, @Nullable String label, @Nullable Drawable icon) {
        super(0f, value, icon);
        this.label = label;
    }

    public PieEntry(float value, @Nullable String label, @Nullable Drawable icon, @Nullable Object data) {
        super(0f, value, icon, data);
        this.label = label;
    }

    /**
     * This is the same as getY(). Returns the value of the PieEntry.
     */
    public float getValue() {
        return getY();
    }

    @Nullable
    public String getLabel() {
        return label;
    }

    public void setLabel(@Nullable String label) {
        this.label = label;
    }

    @Deprecated
    @Override
    public void setX(float x) {
        super.setX(x);
        Log.i("DEPRECATED", "Pie entries do not have x values");
    }

    @Deprecated
    @Override
    public float getX() {
        Log.i("DEPRECATED", "Pie entries do not have x values");
        return super.getX();
    }

    @NotNull
    public PieEntry copy() {
        return new PieEntry(getY(), label, getData());
    }
}
