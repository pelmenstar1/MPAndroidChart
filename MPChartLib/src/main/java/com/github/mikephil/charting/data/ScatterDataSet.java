
package com.github.mikephil.charting.data;

import androidx.annotation.ColorInt;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.renderer.scatter.ChevronDownShapeRenderer;
import com.github.mikephil.charting.renderer.scatter.ChevronUpShapeRenderer;
import com.github.mikephil.charting.renderer.scatter.CircleShapeRenderer;
import com.github.mikephil.charting.renderer.scatter.CrossShapeRenderer;
import com.github.mikephil.charting.renderer.scatter.IShapeRenderer;
import com.github.mikephil.charting.renderer.scatter.SquareShapeRenderer;
import com.github.mikephil.charting.renderer.scatter.TriangleShapeRenderer;
import com.github.mikephil.charting.renderer.scatter.XShapeRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ScatterDataSet extends LineScatterCandleRadarDataSet<Entry> implements IScatterDataSet {
    /**
     * the size the scattershape will have, in density pixels
     */
    private float mShapeSize = 15f;

    /**
     * Renderer responsible for rendering this DataSet, default: square
     */
    @NotNull
    protected IShapeRenderer mShapeRenderer = new SquareShapeRenderer();

    /**
     * The radius of the hole in the shape (applies to Square, Circle and Triangle)
     * - default: 0.0
     */
    private float mScatterShapeHoleRadius = 0f;

    /**
     * Color for the hole in the shape.
     * Setting to `ColorTemplate.COLOR_NONE` will behave as transparent.
     * - default: ColorTemplate.COLOR_NONE
     */
    private int mScatterShapeHoleColor = ColorTemplate.COLOR_NONE;

    public ScatterDataSet(@NotNull List<Entry> entries, @Nullable String label) {
        super(entries, label);
    }

    @Override
    @NotNull
    public DataSet<Entry> copy() {
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < mEntries.size(); i++) {
            entries.add(mEntries.get(i).copy());
        }

        ScatterDataSet copied = new ScatterDataSet(entries, getLabel());
        copy(copied);
        return copied;
    }

    protected void copy(@NotNull ScatterDataSet scatterDataSet) {
        super.copy(scatterDataSet);
        scatterDataSet.mShapeSize = mShapeSize;
        scatterDataSet.mShapeRenderer = mShapeRenderer;
        scatterDataSet.mScatterShapeHoleRadius = mScatterShapeHoleRadius;
        scatterDataSet.mScatterShapeHoleColor = mScatterShapeHoleColor;
    }

    /**
     * Sets the size in density pixels the drawn scattershape will have. This
     * only applies for non custom shapes.
     */
    public void setScatterShapeSize(float size) {
        mShapeSize = size;
    }

    @Override
    public float getScatterShapeSize() {
        return mShapeSize;
    }

    /**
     * Sets the ScatterShape this DataSet should be drawn with. This will search for an available IShapeRenderer and set this
     * renderer for the DataSet.
     */
    public void setScatterShape(@NotNull ScatterChart.ScatterShape shape) {
        mShapeRenderer = getRendererForShape(shape);
    }

    /**
     * Sets a new IShapeRenderer responsible for drawing this DataSet.
     * This can also be used to set a custom IShapeRenderer aside from the default ones.
     *
     */
    public void setShapeRenderer(@NotNull IShapeRenderer shapeRenderer) {
        mShapeRenderer = shapeRenderer;
    }

    @Override
    @NotNull
    public IShapeRenderer getShapeRenderer() {
        return mShapeRenderer;
    }

    /**
     * Sets the radius of the hole in the shape (applies to Square, Circle and Triangle)
     * Set this to <= 0 to remove holes.
     */
    public void setScatterShapeHoleRadius(float holeRadius) {
        mScatterShapeHoleRadius = holeRadius;
    }

    @Override
    public float getScatterShapeHoleRadius() {
        return mScatterShapeHoleRadius;
    }

    /**
     * Sets the color for the hole in the shape.
     */
    public void setScatterShapeHoleColor(@ColorInt int holeColor) {
        mScatterShapeHoleColor = holeColor;
    }

    @Override
    public int getScatterShapeHoleColor() {
        return mScatterShapeHoleColor;
    }

    @NotNull
    public static IShapeRenderer getRendererForShape(@NotNull ScatterChart.ScatterShape shape) {
        switch (shape) {
            case SQUARE:
                return new SquareShapeRenderer();
            case CIRCLE:
                return new CircleShapeRenderer();
            case TRIANGLE:
                return new TriangleShapeRenderer();
            case CROSS:
                return new CrossShapeRenderer();
            case X:
                return new XShapeRenderer();
            case CHEVRON_UP:
                return new ChevronUpShapeRenderer();
            case CHEVRON_DOWN:
                return new ChevronDownShapeRenderer();
            default:
                throw new IllegalArgumentException("shape");
        }
    }
}
