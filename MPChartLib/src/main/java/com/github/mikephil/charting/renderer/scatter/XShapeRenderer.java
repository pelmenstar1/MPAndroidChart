package com.github.mikephil.charting.renderer.scatter;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.jetbrains.annotations.NotNull;

/**
 * Created by wajdic on 15/06/2016.
 * Created at Time 09:08
 */
public class XShapeRenderer implements IShapeRenderer {
    @Override
    public void renderShape(
            @NotNull Canvas c,
            @NotNull IScatterDataSet dataSet,
            @NotNull ViewPortHandler viewPortHandler,
            float posX, float posY,
            @NotNull Paint renderPaint
    ) {
        float shapeHalf = dataSet.getScatterShapeSize() * 0.5f;

        renderPaint.setStyle(Paint.Style.STROKE);
        renderPaint.setStrokeWidth(Utils.convertDpToPixel(1f));

        float leftX = posX - shapeHalf;
        float rightX = posX + shapeHalf;

        float topY = posY + shapeHalf;
        float bottomY = posY - shapeHalf;

        c.drawLine(
                leftX, bottomY,
                rightX, topY,
                renderPaint
        );
        c.drawLine(
                rightX, bottomY,
                leftX, topY,
                renderPaint
        );
    }

}