
package com.github.mikephil.charting.data;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.ParcelFormatException;
import android.os.Parcelable;

import com.github.mikephil.charting.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class representing one entry in the chart. Might contain multiple values.
 * Might only contain a single value depending on the used constructor.
 * 
 * @author Philipp Jahoda
 */
public class Entry implements Parcelable {
    /** the x value */
    private float x = 0f;

    /** the y value */
    private float y = 0f;

    /** optional spot for additional data this Entry represents */
    @Nullable
    private Object mData = null;

    /** optional icon image */
    @Nullable
    private Drawable mIcon = null;

    public Entry() {
    }

    /**
     * A Entry represents one single entry in the chart.
     *
     * @param x the x value
     * @param y the y value (the actual value of the entry)
     */
    public Entry(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * A Entry represents one single entry in the chart.
     *
     * @param x the x value
     * @param y the y value (the actual value of the entry)
     * @param data Spot for additional data this Entry represents.
     */
    public Entry(float x, float y, @Nullable Object data) {
        this.x = x;
        this.y = y;
        this.mData = data;
    }

    /**
     * A Entry represents one single entry in the chart.
     *
     * @param x the x value
     * @param y the y value (the actual value of the entry)
     * @param icon icon image
     */
    public Entry(float x, float y, @Nullable Drawable icon) {
        this.x = x;
        this.y = y;
        this.mIcon = icon;
    }

    /**
     * A Entry represents one single entry in the chart.
     *
     * @param x the x value
     * @param y the y value (the actual value of the entry)
     * @param icon icon image
     * @param data Spot for additional data this Entry represents.
     */
    public Entry(float x, float y, @Nullable Drawable icon, @Nullable Object data) {
        this.x = x;
        this.y = y;

        this.mIcon = icon;
        this.mData = data;
    }

    protected Entry(@NotNull Parcel in) {
        x = in.readFloat();
        y = in.readFloat();

        if (in.readInt() == 1) {
            mData = in.readParcelable(Object.class.getClassLoader());
        }
    }

    /**
     * Returns the x-value of this Entry object.
     */
    public float getX() {
        return x;
    }

    /**
     * Sets the x-value of this Entry object.
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Returns the y value of this Entry.
     */
    public float getY() {
        return y;
    }

    /**
     * Sets the y-value for the Entry.
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Returns the icon of this Entry.
     */
    @Nullable
    public Drawable getIcon() {
        return mIcon;
    }

    /**
     * Sets the icon drawable
     */
    public void setIcon(@Nullable Drawable icon) {
        this.mIcon = icon;
    }

    /**
     * Returns the data, additional information that this Entry represents, or
     * null, if no data has been specified.
     */
    @Nullable
    public Object getData() {
        return mData;
    }

    /**
     * Sets additional data this Entry should represent.
     */
    public void setData(@Nullable Object data) {
        this.mData = data;
    }

    /**
     * returns an exact copy of the entry
     */
    @NotNull
    public Entry copy() {
        return new Entry(x, y, mData);
    }

    /**
     * Compares value, xIndex and data of the entries. Returns true if entries
     * are equal in those points, false if not. Does not check by hash-code like
     * it's done by the "equals" method.
     */
    public boolean equalTo(@Nullable Entry e) {
        if (e == null)
            return false;

        if (e.getData() != this.getData())
            return false;

        if (Math.abs(e.x - this.x) > Utils.FLOAT_EPSILON)
            return false;

        return !(Math.abs(e.getY() - this.getY()) > Utils.FLOAT_EPSILON);
    }

    /**
     * returns a string representation of the entry containing x-index and value
     */
    @Override
    @NotNull
    public String toString() {
        return "Entry, x: " + x + " y: " + getY();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NotNull Parcel dest, int flags) {
        dest.writeFloat(x);
        dest.writeFloat(y);
        if (mData != null) {
            if (getData() instanceof Parcelable) {
                dest.writeInt(1);
                dest.writeParcelable((Parcelable) mData, flags);
            } else {
                throw new ParcelFormatException("Cannot parcel an Entry with non-parcelable data");
            }
        } else {
            dest.writeInt(0);
        }
    }

    @NotNull
    public static final Parcelable.Creator<Entry> CREATOR = new Parcelable.Creator<Entry>() {
        @NotNull
        public Entry createFromParcel(@NotNull Parcel source) {
            return new Entry(source);
        }

        @NotNull
        public Entry[] newArray(int size) {
            return new Entry[size];
        }
    };
}
