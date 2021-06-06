package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Path;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class YAxisRendererRadarChart extends YAxisRenderer {
    private final RadarChart mChart;

    public YAxisRendererRadarChart(
            @NotNull ViewPortHandler viewPortHandler,
            @NotNull YAxis yAxis,
            @NotNull RadarChart chart) {
        super(viewPortHandler, yAxis, null);

        this.mChart = chart;
    }

    @Override
    protected void computeAxisValues(float min, float max) {
        int labelCount = mAxis.getLabelCount();
        float range = Math.abs(max - min);

        if (labelCount == 0 || range <= 0 || Float.isInfinite(range)) {
            mAxis.mEntries = new float[0];
            mAxis.mCenteredEntries = new float[0];
            mAxis.mEntryCount = 0;
            return;
        }

        // Find out how much spacing (in y value space) between axis values
        float rawInterval = range / labelCount;
        float interval = Utils.roundToNextSignificant(rawInterval);

        // If granularity is enabled, then do not allow the interval to go below specified granularity.
        // This is used to avoid repeated values when rounding values for display.
        if (mAxis.isGranularityEnabled())
            interval = Math.max(interval, mAxis.getGranularity());

        // Normalize interval
        float intervalMagnitude = Utils.roundToNextSignificant((float)Math.pow(10, (int) Math.log10(interval)));
        int intervalSigDigit = (int) (interval / intervalMagnitude);
        if (intervalSigDigit > 5) {
            float magRaw = (float)Math.floor(10.0 * intervalMagnitude);

            // Use one order of magnitude higher, to avoid intervals like 0.9 or 90
            // if it's 0.0 after floor(), we use the old value
            interval = magRaw == 0f ? interval : magRaw;
        }

        boolean centeringEnabled = mAxis.isCenterAxisLabelsEnabled();
        int n = centeringEnabled ? 1 : 0;

        // force label count
        if (mAxis.isForceLabelsEnabled()) {
            float step = range / (float) (labelCount - 1);
            mAxis.mEntryCount = labelCount;

            if (mAxis.mEntries.length < labelCount) {
                // Ensure stops contains at least numStops elements.
                mAxis.mEntries = new float[labelCount];
            }

            float v = min;

            for (int i = 0; i < labelCount; i++) {
                mAxis.mEntries[i] = v;
                v += step;
            }

            n = labelCount;

            // no forced count
        } else {
            float first = interval == 0f ? 0f : (float)Math.ceil(min / interval) * interval;
            if (centeringEnabled) {
                first -= interval;
            }

            float last = interval == 0f ? 0f : Utils.nextUp((float)Math.floor(max / interval) * interval);

            float f;
            int i;

            if (interval != 0.0) {
                for (f = first; f <= last; f += interval) {
                    ++n;
                }
            }

            n++;

            mAxis.mEntryCount = n;

            if (mAxis.mEntries.length < n) {
                // Ensure stops contains at least numStops elements.
                mAxis.mEntries = new float[n];
            }

            for (f = first, i = 0; i < n; f += interval, ++i) {
                if (f == 0f) // Fix for negative zero case (Where value == -0.0, and 0.0 == -0.0)
                    f = 0f;

                mAxis.mEntries[i] = f;
            }
        }

        // set decimals
        if (interval < 1) {
            mAxis.mDecimals = (int) Math.ceil(-Math.log10(interval));
        } else {
            mAxis.mDecimals = 0;
        }

        if (centeringEnabled) {
            if (mAxis.mCenteredEntries.length < n) {
                mAxis.mCenteredEntries = new float[n];
            }

            float offset = (mAxis.mEntries[1] - mAxis.mEntries[0]) * 0.5f;

            for (int i = 0; i < n; i++) {
                mAxis.mCenteredEntries[i] = mAxis.mEntries[i] + offset;
            }
        }

        mAxis.mAxisMinimum = mAxis.mEntries[0];
        mAxis.mAxisMaximum = mAxis.mEntries[n-1];
        mAxis.mAxisRange = Math.abs(mAxis.mAxisMaximum - mAxis.mAxisMinimum);
    }

    @Override
    public void renderAxisLabels(@NotNull Canvas c) {
        if (!mYAxis.isEnabled() || !mYAxis.isDrawLabelsEnabled())
            return;

        mAxisLabelPaint.setTypeface(mYAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mYAxis.getTextSize());
        mAxisLabelPaint.setColor(mYAxis.getTextColor());

        MPPointF center = mChart.getCenterOffsets();
        MPPointF pOut = MPPointF.getInstance(0,0);
        float factor = mChart.getFactor();

        int from = mYAxis.isDrawBottomYLabelEntryEnabled() ? 0 : 1;
        int to = mYAxis.isDrawTopYLabelEntryEnabled()
                ? mYAxis.mEntryCount
                : (mYAxis.mEntryCount - 1);

        float xOffset = mYAxis.getLabelXOffset();

        for (int j = from; j < to; j++) {
            float r = (mYAxis.mEntries[j] - mYAxis.mAxisMinimum) * factor;

            Utils.getPosition(center, r, mChart.getRotationAngle(), pOut);

            String label = mYAxis.getFormattedLabel(j);

            c.drawText(label, pOut.x + xOffset, pOut.y, mAxisLabelPaint);
        }

        MPPointF.recycleInstance(center);
        MPPointF.recycleInstance(pOut);
    }

    private final Path mRenderLimitLinesPathBuffer = new Path();

    @Override
    public void renderLimitLines(@NotNull Canvas c) {
        List<LimitLine> limitLines = mYAxis.getLimitLines();

        float sliceAngle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();

        MPPointF center = mChart.getCenterOffsets();
        MPPointF pOut = MPPointF.getInstance(0,0);
        for (int i = 0; i < limitLines.size(); i++) {

            LimitLine l = limitLines.get(i);

            if (!l.isEnabled())
                continue;

            mLimitLinePaint.setColor(l.getLineColor());
            mLimitLinePaint.setPathEffect(l.getDashPathEffect());
            mLimitLinePaint.setStrokeWidth(l.getLineWidth());

            float r = (l.getLimit() - mChart.getYChartMin()) * factor;

            Path limitPath = mRenderLimitLinesPathBuffer;
            limitPath.reset();

            int count = mChart.getData().getMaxEntryCountSet().getEntryCount();

            for (int j = 0; j < count; j++) {
                Utils.getPosition(center, r, sliceAngle * j + mChart.getRotationAngle(), pOut);

                if (j == 0)
                    limitPath.moveTo(pOut.x, pOut.y);
                else
                    limitPath.lineTo(pOut.x, pOut.y);
            }
            limitPath.close();

            c.drawPath(limitPath, mLimitLinePaint);
        }
        MPPointF.recycleInstance(center);
        MPPointF.recycleInstance(pOut);
    }
}
