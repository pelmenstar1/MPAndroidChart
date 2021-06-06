package com.github.mikephil.charting.highlight;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Philipp Jahoda on 22/07/15.
 */
public class HorizontalBarHighlighter extends BarHighlighter {
	public HorizontalBarHighlighter(@NotNull BarDataProvider chart) {
		super(chart);
	}

	@Override
	public Highlight getHighlight(float x, float y) {
		BarData barData = mChart.getBarData();

		MPPointF pos = getValsForTouch(y, x);

		Highlight high = getHighlightForX(pos.y, y, x);
		if (high == null) {
			return null;
		}

		IBarDataSet set = barData.getDataSetByIndex(high.getDataSetIndex());
		if (set.isStacked()) {
			return getStackedHighlight(high,
					set,
					pos.y,
					pos.x
			);
		}

		MPPointF.recycleInstance(pos);

		return high;
	}

	@Override
	protected List<Highlight> buildHighlights(
			@NotNull IDataSet set,
			int dataSetIndex,
			float xVal,
			@NotNull DataSet.Rounding rounding
	) {
		ArrayList<Highlight> highlights = new ArrayList<>();

		//noinspection unchecked
		List<Entry> entries = set.getEntriesForXValue(xVal);
		if (entries.isEmpty()) {
			// Try to find closest x-value and take all entries for that x-value
			Entry closest = set.getEntryForXValue(xVal, Float.NaN, rounding);
			if (closest != null) {
				//noinspection unchecked
				entries = set.getEntriesForXValue(closest.getX());
			}
		}

		if (entries.isEmpty()) {
			return highlights;
		}

		Transformer transformer = mChart.getTransformer(set.getAxisDependency());

		for (Entry e : entries) {
			MPPointF pixels = transformer.getPixelForValues(e.getY(), e.getX());

			highlights.add(new Highlight(
					e.getX(), e.getY(),
					pixels.x, pixels.y,
					dataSetIndex,
					set.getAxisDependency())
			);
		}

		return highlights;
	}

	@Override
	protected float getDistance(float x1, float y1, float x2, float y2) {
		return Math.abs(y1 - y2);
	}
}
