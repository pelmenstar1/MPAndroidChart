package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.ColorInt;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineRadarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Philipp Jahoda on 25/01/16.
 */
public abstract class LineRadarRenderer<TDataSet extends ILineRadarDataSet<TEntry>, TEntry extends Entry> extends LineScatterCandleRadarRenderer<TDataSet, TEntry> {
    public LineRadarRenderer(
            @NotNull ChartAnimator animator,
            @NotNull ViewPortHandler viewPortHandler
    ) {
        super(animator, viewPortHandler);
    }

    /**
     * Draws the provided path in filled mode with the provided drawable.
     */
    protected void drawFilledPath(
            @NotNull Canvas c,
            @NotNull Path filledPath,
            @NotNull Drawable drawable
    ) {
        if (Build.VERSION.SDK_INT >= 18) {
            int save = c.save();
            c.clipPath(filledPath);

            drawable.setBounds((int) mViewPortHandler.contentLeft(),
                    (int) mViewPortHandler.contentTop(),
                    (int) mViewPortHandler.contentRight(),
                    (int) mViewPortHandler.contentBottom());
            drawable.draw(c);

            c.restoreToCount(save);
        } else {
            throw new RuntimeException("Fill-drawables not (yet) supported below API level 18, " +
                    "this code was run on API level " + Build.VERSION.SDK_INT + ".");
        }
    }

    /**
     * Draws the provided path in filled mode with the provided color and alpha.
     * Special thanks to Angelo Suzuki (https://github.com/tinsukE) for this.
     */
    protected void drawFilledPath(
            @NotNull Canvas c,
            @NotNull Path filledPath,
            @ColorInt int fillColor,
            @ColorInt int fillAlpha) {
        int color = (fillAlpha << 24) | (fillColor & 0xffffff);

        if (Build.VERSION.SDK_INT >= 18) {
            int save = c.save();

            c.clipPath(filledPath);

            c.drawColor(color);
            c.restoreToCount(save);
        } else {

            // save
            Paint.Style previous = mRenderPaint.getStyle();
            int previousColor = mRenderPaint.getColor();

            // set
            mRenderPaint.setStyle(Paint.Style.FILL);
            mRenderPaint.setColor(color);

            c.drawPath(filledPath, mRenderPaint);

            // restore
            mRenderPaint.setColor(previousColor);
            mRenderPaint.setStyle(previous);
        }
    }
}
