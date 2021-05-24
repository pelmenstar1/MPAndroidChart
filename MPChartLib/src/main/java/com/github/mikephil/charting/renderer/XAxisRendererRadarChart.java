
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.PointF;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class XAxisRendererRadarChart extends XAxisRenderer {
    private final RadarChart mChart;

    public XAxisRendererRadarChart(
            @NotNull ViewPortHandler viewPortHandler,
            @NotNull XAxis xAxis,
            @Nullable RadarChart chart
    ) {
        super(viewPortHandler, xAxis, null);

        mChart = chart;
    }

    @Override
    public void renderAxisLabels(@NotNull Canvas c) {
        if (!mXAxis.isEnabled() || !mXAxis.isDrawLabelsEnabled())
            return;

        float labelRotationAngleDegrees = mXAxis.getLabelRotationAngle();
        MPPointF drawLabelAnchor = MPPointF.getInstance(0.5f, 0.25f);

        mAxisLabelPaint.setTypeface(mXAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mXAxis.getTextSize());
        mAxisLabelPaint.setColor(mXAxis.getTextColor());

        float sliceAngle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();

        MPPointF center = mChart.getCenterOffsets();
        MPPointF pOut = MPPointF.getInstance(0,0);

        int entryCount = mChart.getData().getMaxEntryCountSet().getEntryCount();
        IAxisValueFormatter valueFormatter = mXAxis.getValueFormatter();
        float rotAngle = mChart.getRotationAngle();

        float halfRotLabelHeight = mXAxis.mLabelRotatedHeight * 0.5f;

        float dist = mChart.getYRange() * factor + mXAxis.mLabelRotatedWidth * 0.5f;

        for (int i = 0; i < entryCount; i++) {
            String label = valueFormatter.getFormattedValue(i, mXAxis);

            float angle = (sliceAngle * i + rotAngle) % 360f;

            Utils.getPosition(center, dist, angle, pOut);

            drawLabel(c,
                    label,
                    pOut.x,
                    pOut.y - halfRotLabelHeight,
                    drawLabelAnchor,
                    labelRotationAngleDegrees
            );
        }

        MPPointF.recycleInstance(center);
        MPPointF.recycleInstance(pOut);
        MPPointF.recycleInstance(drawLabelAnchor);
    }

	/**
	 * XAxis LimitLines on RadarChart not yet supported.
	 */
	@Override
	public void renderLimitLines(@NotNull Canvas c) {
		// this space intentionally left blank
	}
}
