
package com.github.mikephil.charting.buffer;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.jetbrains.annotations.NotNull;

public class HorizontalBarBuffer extends BarBuffer {
    public HorizontalBarBuffer(int size, int dataSetCount, boolean containsStacks) {
        super(size, dataSetCount, containsStacks);
    }

    @Override
    public void feed(@NotNull IBarDataSet data) {
        float size = data.getEntryCount() * phaseX;
        float barWidthHalf = mBarWidth * 0.5f;

        for (int i = 0; i < size; i++) {
            BarEntry e = data.getEntryForIndex(i);

            float x = e.getX();
            float y = e.getY();
            float[] vals = e.getYVals();

            if (!mContainsStacks || vals == null) {
                float bottom = x - barWidthHalf;
                float top = x + barWidthHalf;
                float left, right;
                if (mInverted) {
                    left = y >= 0 ? y : 0;
                    right = y <= 0 ? y : 0;
                } else {
                    right = y >= 0 ? y : 0;
                    left = y <= 0 ? y : 0;
                }

                // multiply the height of the rect with the phase
                if (right > 0)
                    right *= phaseY;
                else
                    left *= phaseY;

                addBar(left, top, right, bottom);

            } else {

                float posY = 0f;
                float negY = -e.getNegativeSum();
                float yStart;

                // fill the stack
                for (float value : vals) {
                    if (value >= 0f) {
                        y = posY;
                        yStart = posY + value;
                        posY = yStart;
                    } else {
                        y = negY;
                        yStart = negY + Math.abs(value);
                        negY += Math.abs(value);
                    }

                    float bottom = x - barWidthHalf;
                    float top = x + barWidthHalf;
                    float left, right;
                    if (mInverted) {
                        left = Math.max(y, yStart);
                        right = Math.min(y, yStart);
                    } else {
                        right = Math.max(y, yStart);
                        left = Math.min(y, yStart);
                    }

                    // multiply the height of the rect with the phase
                    right *= phaseY;
                    left *= phaseY;

                    addBar(left, top, right, bottom);
                }
            }
        }

        reset();
    }
}
