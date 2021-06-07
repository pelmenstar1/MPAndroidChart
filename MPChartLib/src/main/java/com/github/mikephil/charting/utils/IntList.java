package com.github.mikephil.charting.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public final class IntList implements Iterable<Integer> {
    private int[] data;
    private int length;

    public IntList() {
        data = EmptyArray.INT;
        length = 0;
    }

    private IntList(@NotNull int[] initialDataMutable) {
        data = initialDataMutable;
        length = data.length;
    }

    public IntList(int capacity) {
        data = new int[capacity];
        length = 0;
    }

    @NotNull
    public static IntList ofArrayCopy(@NotNull int[] initialData) {
        int[] copied = new int[initialData.length];
        System.arraycopy(initialData, 0, copied, 0, initialData.length);

        return new IntList(copied);
    }

    @NotNull
    public static IntList ofArrayMutable(@NotNull int[] initialData) {
        return new IntList(initialData);
    }

    @NotNull
    public static IntList ofIntCollection(@NotNull Collection<? extends Integer> collection) {
        int[] data = new int[collection.size()];
        int index = 0;

        for(Integer boxed: collection) {
            data[index++] = boxed;
        }

        return new IntList(data);
    }

    @NotNull
    public static IntList ofIntList(@NotNull List<? extends Integer> list) {
        int[] data = new int[list.size()];

        for(int i = 0; i < data.length; i++) {
            data[i] = list.get(i);
        }

        return new IntList(data);
    }

    public int size() {
        return length;
    }

    public boolean isEmpty() {
        return length == 0;
    }

    public int get(int index) {
        if(index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("index=" + index + ", length=" + length);
        }

        return data[index];
    }

    public void set(int index, int value) {
        if(index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("index=" + index + ", length=" + length);
        }

        data[index] = value;
    }

    public void add(int value) {
        if(length < data.length) {
            data[length++] = value;
        } else {
            int[] newData = new int[length + 1];
            System.arraycopy(data, 0, newData, 0, length);
            newData[length] = value;

            data = newData;
            length = newData.length;
        }
    }

    public void addAll(@NotNull int[] values) {
        if(length + values.length < data.length) {
            System.arraycopy(values, 0, data, length, values.length);
        } else {
            int[] newData = new int[length + values.length];
            System.arraycopy(data, 0, newData, 0, length);
            System.arraycopy(values, 0, newData, length, values.length);

            data = newData;
            length = newData.length;
        }
    }

    public void addAll(@NotNull Collection<? extends Integer> values) {
        int valuesSize = values.size();
        int index;

        if(length + valuesSize < data.length) {
            index = length;
        } else {
            int[] newData = new int[length + valuesSize];
            System.arraycopy(data, 0, newData, 0, length);

            index = length;

            data = newData;
            length = newData.length;
        }

        for(Integer boxed: values) {
            data[index++] = boxed;
        }
    }

    public boolean contains(int value) {
        return indexOf(value) >= 0;
    }

    public int indexOf(int value) {
        for(int i = 0; i < length; i++) {
            if(data[i] == value) {
                return i;
            }
        }

        return -1;
    }

    public int lastIndexOf(int value) {
        for(int i = length - 1; i >= 0; i--) {
            if(data[i] == value) {
                return i;
            }
        }

        return -1;
    }

    @NotNull
    public int[] toArray() {
        int[] copied = new int[length];
        System.arraycopy(data, 0, copied, 0, length);

        return copied;
    }

    public void copyTo(@NotNull int[] values, int copyIndex) {
        System.arraycopy(data, 0, values, copyIndex, length);
    }

    public void clear() {
        data = EmptyArray.INT;
        length = 0;
    }

    public boolean remove(int value) {
        for(int i = 0; i < length; i++) {
            if(data[i] == value) {
                removeAtInternal(i);
                return true;
            }
        }

        return false;
    }

    public void removeAt(int index) {
        if(index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("index=" + index + ", length=" + length);
        }

        removeAtInternal(index);
    }

    private void removeAtInternal(int index) {
        length--;
        if (index < length)
        {
            System.arraycopy(data, index + 1, data, index, length - index);
        }
    }

    @NotNull
    public IntIterator properIterator() {
        return new IntIterator(data);
    }

    @NotNull
    @Override
    public Iterator<Integer> iterator() {
        return new IntBoxedIterator(data);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        IntList o = (IntList) other;
        if(length != o.length) {
            return false;
        }

        int[] data = this.data;
        int[] oData = o.data;

        for(int i = 0; i < length; i++) {
            if(data[i] != oData[i]) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for(int i = 0; i < length; i++) {
            result = result * 31 + data[i];
        }

        return result;
    }

    @Override
    @NotNull
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ length=");
        sb.append(length);
        sb.append(", data=[");

        if(length > 0) {
            int maxIdx = length - 1;

            for(int i = 0; i < maxIdx; i++) {
                sb.append(data[i]);
                sb.append(',');
                sb.append(' ');
            }

            sb.append(data[maxIdx]);
        }

        sb.append(']');
        sb.append(' ');
        sb.append('}');

        return sb.toString();
    }

    public static final class IntIterator {
        private final int[] values;
        private int index;

        public IntIterator(@NotNull int[] values) {
            this.values = values;
        }

        public boolean hasNext() {
            return index < values.length;
        }

        public int next() {
            return values[index++];
        }
    }

    private static final class IntBoxedIterator implements Iterator<Integer> {
        private final int[] values;
        private int index;

        public IntBoxedIterator(@NotNull int[] values) {
            this.values = values;
        }

        @Override
        public boolean hasNext() {
            return index < values.length;
        }

        @Override
        public Integer next() {
            return values[index++];
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void forEachRemaining(@NotNull Consumer<? super Integer> action) {
            for(int i = index; i < values.length; i++) {
                action.accept(values[i]);
            }
        }
    }
}
