
package com.github.mikephil.charting.renderer;

import com.github.mikephil.charting.utils.ViewPortHandler;

import org.jetbrains.annotations.NotNull;

/**
 * Abstract baseclass of all Renderers.
 * 
 * @author Philipp Jahoda
 */
public abstract class Renderer {

    /**
     * the component that handles the drawing area of the chart and it's offsets
     */
    @NotNull
    protected ViewPortHandler mViewPortHandler;

    public Renderer(@NotNull ViewPortHandler viewPortHandler) {
        this.mViewPortHandler = viewPortHandler;
    }
}
