package com.github.mikephil.charting.highlight;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.MPPointD;

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

		MPPointD pos = getValsForTouch(y, x);

		Highlight high = getHighlightForX((float) pos.y, y, x);
		if (high == null) {
			return null;
		}

		IBarDataSet set = barData.getDataSetByIndex(high.getDataSetIndex());
		if (set.isStacked()) {
			return getStackedHighlight(high,
					set,
					(float) pos.y,
					(float) pos.x);
		}

		MPPointD.recycleInstance(pos);

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
			final Entry closest = set.getEntryForXValue(xVal, Float.NaN, rounding);
			if (closest != null) {
				//noinspection unchecked
				entries = set.getEntriesForXValue(closest.getX());
			}
		}

		if (entries.isEmpty()) {
			return highlights;
		}

		for (Entry e : entries) {
			MPPointD pixels = mChart.getTransformer(
					set.getAxisDependency()).getPixelForValues(e.getY(), e.getX());

			highlights.add(new Highlight(
					e.getX(), e.getY(),
					(float) pixels.x, (float) pixels.y,
					dataSetIndex, set.getAxisDependency()));
		}

		return highlights;
	}

	@Override
	protected float getDistance(float x1, float y1, float x2, float y2) {
		return Math.abs(y1 - y2);
	}
}
