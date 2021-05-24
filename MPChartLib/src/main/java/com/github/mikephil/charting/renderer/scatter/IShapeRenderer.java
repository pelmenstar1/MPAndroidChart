package com.github.mikephil.charting.renderer.scatter;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.jetbrains.annotations.NotNull;

/**
 * Created by wajdic on 15/06/2016.
 * Created at Time 09:07
 */
public interface IShapeRenderer
{
    /**
     * Renders the provided ScatterDataSet with a shape.
     *
     * @param c               Canvas object for drawing the shape
     * @param dataSet         The DataSet to be drawn
     * @param viewPortHandler Contains information about the current state of the view
     * @param posX            Position to draw the shape at
     * @param posY            Position to draw the shape at
     * @param renderPaint     Paint object used for styling and drawing
     */
    void renderShape(
            @NotNull Canvas c,
            @NotNull IScatterDataSet dataSet,
            @NotNull ViewPortHandler viewPortHandler,
            float posX, float posY,
            @NotNull Paint renderPaint
    );
}
