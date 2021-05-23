package com.github.mikephil.charting.highlight;

import org.jetbrains.annotations.Nullable;

/**
 * Created by philipp on 10/06/16.
 */
public interface IHighlighter {
    /**
     * Returns a Highlight object corresponding to the given x- and y- touch positions in pixels.
     */
    @Nullable
    Highlight getHighlight(float x, float y);
}
