package com.github.mikephil.charting.renderer.scatter;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.jetbrains.annotations.NotNull;

/**
 * Created by wajdic on 15/06/2016.
 * Created at Time 09:08
 */
public final class CircleShapeRenderer implements IShapeRenderer {
    @Override
    public void renderShape(
            @NotNull Canvas c,
            @NotNull IScatterDataSet dataSet,
            @NotNull ViewPortHandler viewPortHandler,
            float posX, float posY,
            @NotNull Paint renderPaint
    ) {
        float shapeSize = dataSet.getScatterShapeSize();
        float shapeHalf = shapeSize * 0.5f;
        float shapeHoleSizeHalf = Utils.convertDpToPixel(dataSet.getScatterShapeHoleRadius());
        float shapeHoleSize = shapeHoleSizeHalf * 2.f;
        float shapeStrokeSize = (shapeSize - shapeHoleSize) * 0.5f;
        float shapeStrokeSizeHalf = shapeStrokeSize * 0.5f;

        int shapeHoleColor = dataSet.getScatterShapeHoleColor();

        if (shapeSize > 0f) {
            renderPaint.setStyle(Paint.Style.STROKE);
            renderPaint.setStrokeWidth(shapeStrokeSize);

            c.drawCircle(
                    posX,
                    posY,
                    shapeHoleSizeHalf + shapeStrokeSizeHalf,
                    renderPaint);

            if (shapeHoleColor != ColorTemplate.COLOR_NONE) {
                renderPaint.setStyle(Paint.Style.FILL);

                renderPaint.setColor(shapeHoleColor);
                c.drawCircle(
                        posX,
                        posY,
                        shapeHoleSizeHalf,
                        renderPaint);
            }
        } else {
            renderPaint.setStyle(Paint.Style.FILL);

            c.drawCircle(
                    posX,
                    posY,
                    shapeHalf,
                    renderPaint);
        }
    }
}
