
package com.github.mikephil.charting.utils;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;

import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Transformer class that contains all matrices and is responsible for
 * transforming values into pixels on the screen and backwards.
 *
 * @author Philipp Jahoda
 */
public class Transformer {
    /**
     * matrix to map the values to the screen pixels
     */
    protected Matrix mMatrixValueToPx = new Matrix();

    /**
     * matrix for handling the different offsets of the chart
     */
    protected Matrix mMatrixOffset = new Matrix();

    protected ViewPortHandler mViewPortHandler;

    public Transformer(@NotNull ViewPortHandler viewPortHandler) {
        this.mViewPortHandler = viewPortHandler;
    }

    /**
     * Prepares the matrix that transforms values to pixels. Calculates the
     * scale factors from the charts size and offsets.
     */
    public void prepareMatrixValuePx(float xChartMin, float deltaX, float deltaY, float yChartMin) {
        float scaleX = (mViewPortHandler.contentWidth()) / deltaX;
        float scaleY = (mViewPortHandler.contentHeight()) / deltaY;

        if (Float.isInfinite(scaleX)) {
            scaleX = 0;
        }
        if (Float.isInfinite(scaleY)) {
            scaleY = 0;
        }

        // setup all matrices
        mMatrixValueToPx.reset();
        mMatrixValueToPx.postTranslate(-xChartMin, -yChartMin);
        mMatrixValueToPx.postScale(scaleX, -scaleY);
    }

    /**
     * Prepares the matrix that contains all offsets.
     */
    public void prepareMatrixOffset(boolean inverted) {
        mMatrixOffset.reset();

        if (!inverted) {
            mMatrixOffset.postTranslate(mViewPortHandler.offsetLeft(),
                    mViewPortHandler.getChartHeight() - mViewPortHandler.offsetBottom());
        } else {
            mMatrixOffset.setTranslate(mViewPortHandler.offsetLeft(), -mViewPortHandler.offsetTop());
            mMatrixOffset.postScale(1.0f, -1.0f);
        }
    }

    protected float[] valuePointsForGenerateTransformedValuesScatter = new float[1];

    /**
     * Transforms an List of Entry into a float array containing the x and
     * y values transformed with all matrices for the SCATTERCHART.
     */
    public float[] generateTransformedValuesScatter(IScatterDataSet data, float phaseX,
                                                    float phaseY, int from, int to) {
        final int count = (int) ((to - from) * phaseX + 1) * 2;

        if (valuePointsForGenerateTransformedValuesScatter.length != count) {
            valuePointsForGenerateTransformedValuesScatter = new float[count];
        }
        float[] valuePoints = valuePointsForGenerateTransformedValuesScatter;

        for (int j = 0; j < count; j += 2) {

            Entry e = data.getEntryForIndex(j / 2 + from);

            if (e != null) {
                valuePoints[j] = e.getX();
                valuePoints[j + 1] = e.getY() * phaseY;
            } else {
                valuePoints[j] = 0;
                valuePoints[j + 1] = 0;
            }
        }

        getValueToPixelMatrix().mapPoints(valuePoints);

        return valuePoints;
    }

    protected float[] valuePointsForGenerateTransformedValuesBubble = new float[1];

    /**
     * Transforms an List of Entry into a float array containing the x and
     * y values transformed with all matrices for the BUBBLECHART.
     */
    public float[] generateTransformedValuesBubble(IBubbleDataSet data, float phaseY, int from, int to) {

        final int count = (to - from + 1) * 2; // (int) Math.ceil((to - from) * phaseX) * 2;

        if (valuePointsForGenerateTransformedValuesBubble.length != count) {
            valuePointsForGenerateTransformedValuesBubble = new float[count];
        }
        float[] valuePoints = valuePointsForGenerateTransformedValuesBubble;

        for (int j = 0; j < count; j += 2) {
            Entry e = data.getEntryForIndex(j / 2 + from);

            valuePoints[j] = e.getX();
            valuePoints[j + 1] = e.getY() * phaseY;
        }

        getValueToPixelMatrix().mapPoints(valuePoints);

        return valuePoints;
    }

    protected float[] valuePointsForGenerateTransformedValuesLine = new float[1];

    /**
     * Transforms an List of Entry into a float array containing the x and
     * y values transformed with all matrices for the line chart.
     */
    public float[] generateTransformedValuesLine(ILineDataSet data,
                                                 float phaseX, float phaseY,
                                                 int min, int max) {

        final int count = ((int) ((max - min) * phaseX) + 1) * 2;

        if (valuePointsForGenerateTransformedValuesLine.length != count) {
            valuePointsForGenerateTransformedValuesLine = new float[count];
        }
        float[] valuePoints = valuePointsForGenerateTransformedValuesLine;

        for (int j = 0; j < count; j += 2) {

            Entry e = data.getEntryForIndex(j / 2 + min);

            valuePoints[j] = e.getX();
            valuePoints[j + 1] = e.getY() * phaseY;
        }

        getValueToPixelMatrix().mapPoints(valuePoints);

        return valuePoints;
    }

    protected float[] valuePointsForGenerateTransformedValuesCandle = new float[1];

    /**
     * Transforms an List of Entry into a float array containing the x and
     * y values transformed with all matrices for the CANDLESTICKCHART.
     *
     */
    public float[] generateTransformedValuesCandle(ICandleDataSet data,
                                                   float phaseX, float phaseY, int from, int to) {

        final int count = (int) ((to - from) * phaseX + 1) * 2;

        if (valuePointsForGenerateTransformedValuesCandle.length != count) {
            valuePointsForGenerateTransformedValuesCandle = new float[count];
        }
        float[] valuePoints = valuePointsForGenerateTransformedValuesCandle;

        for (int j = 0; j < count; j += 2) {
            CandleEntry e = data.getEntryForIndex(j / 2 + from);

            valuePoints[j] = e.getX();
            valuePoints[j + 1] = e.getHigh() * phaseY;
        }

        getValueToPixelMatrix().mapPoints(valuePoints);

        return valuePoints;
    }

    /**
     * transform a path with all the given matrices VERY IMPORTANT: keep order
     * to value-touch-offset
     */
    public void pathValueToPixel(Path path) {

        path.transform(mMatrixValueToPx);
        path.transform(mViewPortHandler.getMatrixTouch());
        path.transform(mMatrixOffset);
    }

    /**
     * Transforms multiple paths will all matrices.
     */
    public void pathValuesToPixel(List<Path> paths) {
        for (int i = 0; i < paths.size(); i++) {
            pathValueToPixel(paths.get(i));
        }
    }

    /**
     * Transform an array of points with all matrices. VERY IMPORTANT: Keep
     * matrix order "value-touch-offset" when transforming.
     */
    public void pointValuesToPixel(@NotNull float[] pts) {

        mMatrixValueToPx.mapPoints(pts);
        mViewPortHandler.getMatrixTouch().mapPoints(pts);
        mMatrixOffset.mapPoints(pts);
    }

    /**
     * Transform a rectangle with all matrices.
     */
    public void rectValueToPixel(@NotNull RectF r) {

        mMatrixValueToPx.mapRect(r);
        mViewPortHandler.getMatrixTouch().mapRect(r);
        mMatrixOffset.mapRect(r);
    }

    /**
     * Transform a rectangle with all matrices with potential animation phases.
     */
    public void rectToPixelPhase(@NotNull RectF r, float phaseY) {

        // multiply the height of the rect with the phase
        r.top *= phaseY;
        r.bottom *= phaseY;

        mMatrixValueToPx.mapRect(r);
        mViewPortHandler.getMatrixTouch().mapRect(r);
        mMatrixOffset.mapRect(r);
    }

    public void rectToPixelPhaseHorizontal(@NotNull RectF r, float phaseY) {
        // multiply the height of the rect with the phase
        r.left *= phaseY;
        r.right *= phaseY;

        mMatrixValueToPx.mapRect(r);
        mViewPortHandler.getMatrixTouch().mapRect(r);
        mMatrixOffset.mapRect(r);
    }

    /**
     * Transform a rectangle with all matrices with potential animation phases.
     */
    public void rectValueToPixelHorizontal(RectF r) {

        mMatrixValueToPx.mapRect(r);
        mViewPortHandler.getMatrixTouch().mapRect(r);
        mMatrixOffset.mapRect(r);
    }

    /**
     * Transform a rectangle with all matrices with potential animation phases.
     */
    public void rectValueToPixelHorizontal(@NotNull RectF r, float phaseY) {

        // multiply the height of the rect with the phase
        r.left *= phaseY;
        r.right *= phaseY;

        mMatrixValueToPx.mapRect(r);
        mViewPortHandler.getMatrixTouch().mapRect(r);
        mMatrixOffset.mapRect(r);
    }

    /**
     * transforms multiple values with all matrices
     */
    public void rectValuesToPixel(@NotNull List<RectF> values) {

        Matrix m = getValueToPixelMatrix();

        for (int i = 0; i < values.size(); i++)
            m.mapRect(values.get(i));
    }

    protected Matrix mPixelToValueMatrixBuffer = new Matrix();

    /**
     * Transforms the given array of touch positions (pixels) (x, y, x, y, ...)
     * into values on the chart.
     */
    public void pixelsToValue(@NotNull float[] pixels) {
        Matrix tmp = mPixelToValueMatrixBuffer;
        tmp.reset();

        // invert all matrixes to convert back to the original value
        mMatrixOffset.invert(tmp);
        tmp.mapPoints(pixels);

        mViewPortHandler.getMatrixTouch().invert(tmp);
        tmp.mapPoints(pixels);

        mMatrixValueToPx.invert(tmp);
        tmp.mapPoints(pixels);
    }

    /**
     * buffer for performance
     */
    float[] ptsBuffer = new float[2];

    /**
     * Returns a recyclable MPPointD instance.
     * returns the x and y values in the chart at the given touch point
     * (encapsulated in a MPPointD). This method transforms pixel coordinates to
     * coordinates / values in the chart. This is the opposite method to
     * getPixelForValues(...).
     */
    @NotNull
    public MPPointF getValuesByTouchPoint(float x, float y) {
        MPPointF result = MPPointF.getInstance(0, 0);
        getValuesByTouchPoint(x, y, result);

        return result;
    }

    public void getValuesByTouchPoint(float x, float y, @NotNull MPPointF outputPoint) {
        ptsBuffer[0] = x;
        ptsBuffer[1] = y;

        pixelsToValue(ptsBuffer);

        outputPoint.x = ptsBuffer[0];
        outputPoint.y = ptsBuffer[1];
    }

    /**
     * Returns a recyclable MPPointD instance.
     * Returns the x and y coordinates (pixels) for a given x and y value in the chart.
     */
    @NotNull
    public MPPointF getPixelForValues(float x, float y) {
        ptsBuffer[0] = x;
        ptsBuffer[1] = y;

        pointValuesToPixel(ptsBuffer);

        float xPx = ptsBuffer[0];
        float yPx = ptsBuffer[1];

        return MPPointF.getInstance(xPx, yPx);
    }

    @NotNull
    public Matrix getValueMatrix() {
        return mMatrixValueToPx;
    }

    @NotNull
    public Matrix getOffsetMatrix() {
        return mMatrixOffset;
    }

    private final Matrix getValuesToPixelMatrixCached = new Matrix();

    @NotNull
    public Matrix getValueToPixelMatrix() {
        getValuesToPixelMatrixCached.set(mMatrixValueToPx);
        getValuesToPixelMatrixCached.postConcat(mViewPortHandler.mMatrixTouch);
        getValuesToPixelMatrixCached.postConcat(mMatrixOffset);
        return getValuesToPixelMatrixCached;
    }

    private Matrix getPixelToValueMatrixCached = new Matrix();

    @NotNull
    public Matrix getPixelToValueMatrix() {
        getValueToPixelMatrix().invert(getPixelToValueMatrixCached);
        return getPixelToValueMatrixCached;
    }
}
