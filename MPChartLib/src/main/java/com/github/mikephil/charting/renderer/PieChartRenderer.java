
package com.github.mikephil.charting.renderer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.List;

public class PieChartRenderer extends DataRenderer {
    protected PieChart mChart;

    /**
     * paint for the hole in the center of the pie chart and the transparent
     * circle
     */
    protected Paint mHolePaint;
    protected Paint mTransparentCirclePaint;
    protected Paint mValueLinePaint;

    /**
     * paint object for the text that can be displayed in the center of the
     * chart
     */
    private final TextPaint mCenterTextPaint;

    /**
     * paint object used for drwing the slice-text
     */
    private final Paint mEntryLabelsPaint;

    private StaticLayout mCenterTextLayout;
    private CharSequence mCenterTextLastValue;
    private final RectF mCenterTextLastBounds = new RectF();
    private final RectF[] mRectBuffer = { new RectF(), new RectF(), new RectF() };

    /**
     * Bitmap for drawing the center hole
     */
    protected WeakReference<Bitmap> mDrawBitmap;

    protected Canvas mBitmapCanvas;

    public PieChartRenderer(@NotNull PieChart chart,
                            @NotNull ChartAnimator animator,
                            @NotNull ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;

        mHolePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHolePaint.setColor(Color.WHITE);
        mHolePaint.setStyle(Style.FILL);

        mTransparentCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTransparentCirclePaint.setColor(Color.WHITE);
        mTransparentCirclePaint.setStyle(Style.FILL);
        mTransparentCirclePaint.setAlpha(105);

        mCenterTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mCenterTextPaint.setColor(Color.BLACK);
        mCenterTextPaint.setTextSize(Utils.convertDpToPixel(12f));

        mValuePaint.setTextSize(Utils.convertDpToPixel(13f));
        mValuePaint.setColor(Color.WHITE);
        mValuePaint.setTextAlign(Align.CENTER);

        mEntryLabelsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mEntryLabelsPaint.setColor(Color.WHITE);
        mEntryLabelsPaint.setTextAlign(Align.CENTER);
        mEntryLabelsPaint.setTextSize(Utils.convertDpToPixel(13f));

        mValueLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mValueLinePaint.setStyle(Style.STROKE);
    }

    @NotNull
    public Paint getPaintHole() {
        return mHolePaint;
    }

    @NotNull
    public Paint getPaintTransparentCircle() {
        return mTransparentCirclePaint;
    }

    @NotNull
    public TextPaint getPaintCenterText() {
        return mCenterTextPaint;
    }

    @NotNull
    public Paint getPaintEntryLabels() {
        return mEntryLabelsPaint;
    }

    @Override
    public void initBuffers() {
    }

    @Override
    public void drawData(@NotNull Canvas c) {
        int width = (int) mViewPortHandler.getChartWidth();
        int height = (int) mViewPortHandler.getChartHeight();

        Bitmap drawBitmap = mDrawBitmap == null ? null : mDrawBitmap.get();

        if (drawBitmap == null || (drawBitmap.getWidth() != width) || (drawBitmap.getHeight() != height)) {
            if (width > 0 && height > 0) {
                drawBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
                mDrawBitmap = new WeakReference<>(drawBitmap);
                mBitmapCanvas = new Canvas(drawBitmap);
            } else
                return;
        }

        drawBitmap.eraseColor(Color.TRANSPARENT);

        PieData pieData = mChart.getData();

        for (IPieDataSet set : pieData.getDataSets()) {
            if (set.isVisible() && set.getEntryCount() > 0) {
                drawDataSet(set);
            }
        }
    }

    private final Path mPathBuffer = new Path();
    private final RectF mInnerRectBuffer = new RectF();

    protected float calculateMinimumRadiusForSpacedSlice(
            @NotNull MPPointF center,
            float radius,
            float angle,
            float arcStartPointX,
            float arcStartPointY,
            float startAngle,
            float sweepAngle
    ) {
        float angleMiddle = startAngle + sweepAngle * 0.5f;

        // Other point of the arc
        float arcEndPointX = center.x + radius * (float) Math.cos((startAngle + sweepAngle) * Utils.FDEG2RAD);
        float arcEndPointY = center.y + radius * (float) Math.sin((startAngle + sweepAngle) * Utils.FDEG2RAD);

        double angleMiddleRad = angleMiddle * Utils.FDEG2RAD;

        // Middle point on the arc
        float arcMidPointX = center.x + radius * (float) Math.cos(angleMiddleRad);
        float arcMidPointY = center.y + radius * (float) Math.sin(angleMiddleRad);

        float basePointsDistX = arcEndPointX - arcStartPointX;
        float basePointsDistY = arcEndPointY - arcStartPointY;

        // This is the base of the contained triangle
        float basePointsDistance = (float)Math.sqrt(
                basePointsDistX * basePointsDistX + basePointsDistY * basePointsDistY
        );

        // After reducing space from both sides of the "slice",
        //   the angle of the contained triangle should stay the same.
        // So let's find out the height of that triangle.
        float containedTriangleHeight = basePointsDistance * 0.5f *
                (float)Math.tan((180.0 - angle) * 0.5f * Utils.FDEG2RAD);

        // Now we subtract that from the radius
        float spacedRadius = radius - containedTriangleHeight;

        float distX = arcMidPointX - (arcEndPointX + arcStartPointX) * 0.5f;
        float distY = arcMidPointY - (arcEndPointY + arcStartPointY) * 0.5f;

        // And now subtract the height of the arc that's between the triangle and the outer circle
        spacedRadius -= (float)Math.sqrt(distX * distX + distY * distY);

        return spacedRadius;
    }

    /**
     * Calculates the sliceSpace to use based on visible values and their size compared to the set sliceSpace.
     */
    protected float getSliceSpace(@NotNull IPieDataSet dataSet) {
        if (!dataSet.isAutomaticallyDisableSliceSpacingEnabled())
            return dataSet.getSliceSpace();

        float spaceSizeRatio = dataSet.getSliceSpace() / mViewPortHandler.getSmallestContentExtension();
        float minValueRatio = dataSet.getYMin() / mChart.getData().getYValueSum() * 2;

        return spaceSizeRatio > minValueRatio ? 0f : dataSet.getSliceSpace();
    }

    protected void drawDataSet(@NotNull IPieDataSet dataSet) {
        float angle = 0;
        float rotationAngle = mChart.getRotationAngle();

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        RectF circleBox = mChart.getCircleBox();

        int entryCount = dataSet.getEntryCount();
        float[] drawAngles = mChart.getDrawAngles();
        MPPointF center = mChart.getCenterCircleBox();
        float radius = mChart.getRadius();
        boolean drawInnerArc = mChart.isDrawHoleEnabled() && !mChart.isDrawSlicesUnderHoleEnabled();
        float userInnerRadius = drawInnerArc
                ? radius * (mChart.getHoleRadius() / 100.f)
                : 0.f;
        float roundedRadius = (radius - (radius * mChart.getHoleRadius() / 100f)) * 0.5f;
        RectF roundedCircleBox = new RectF();
        boolean drawRoundedSlices = drawInnerArc && mChart.isDrawRoundedSlicesEnabled();

        int visibleAngleCount = 0;
        for (int j = 0; j < entryCount; j++) {
            // draw only if the value is greater than zero
            if ((Math.abs(dataSet.getEntryForIndex(j).getY()) > Utils.FLOAT_EPSILON)) {
                visibleAngleCount++;
            }
        }

        final float sliceSpace = visibleAngleCount <= 1 ? 0.f : getSliceSpace(dataSet);

        for (int j = 0; j < entryCount; j++) {
            float sliceAngle = drawAngles[j];
            float innerRadius = userInnerRadius;

            Entry e = dataSet.getEntryForIndex(j);

            // draw only if the value is greater than zero
            if (!(Math.abs(e.getY()) > Utils.FLOAT_EPSILON)) {
                angle += sliceAngle * phaseX;
                continue;
            }

            // Don't draw if it's highlighted, unless the chart uses rounded slices
            if (dataSet.isHighlightEnabled() && mChart.needsHighlight(j) && !drawRoundedSlices) {
                angle += sliceAngle * phaseX;
                continue;
            }

            final boolean accountForSliceSpacing = sliceSpace > 0f && sliceAngle <= 180f;

            mRenderPaint.setColor(dataSet.getColor(j));

            final float sliceSpaceAngleOuter = visibleAngleCount == 1 ? 0f : sliceSpace / (Utils.FDEG2RAD * radius);
            final float startAngleOuter = rotationAngle + (angle + sliceSpaceAngleOuter * 0.5f) * phaseY;
            float sweepAngleOuter = (sliceAngle - sliceSpaceAngleOuter) * phaseY;
            if (sweepAngleOuter < 0f) {
                sweepAngleOuter = 0f;
            }

            mPathBuffer.reset();

            double startAngleOuterRad = startAngleOuter * Utils.FDEG2RAD;

            float sinStartAngleOuter = (float)Math.sin(startAngleOuterRad);
            float cosStartAngleOuter = (float)Math.cos(startAngleOuterRad);

            if (drawRoundedSlices) {
                float x = center.x + (radius - roundedRadius) * cosStartAngleOuter;
                float y = center.y + (radius - roundedRadius) * sinStartAngleOuter;
                roundedCircleBox.set(x - roundedRadius, y - roundedRadius, x + roundedRadius, y + roundedRadius);
            }

            float arcStartPointX = center.x + radius * cosStartAngleOuter;
            float arcStartPointY = center.y + radius * sinStartAngleOuter;

            if (sweepAngleOuter >= 360.f && sweepAngleOuter % 360f <= Utils.FLOAT_EPSILON) {
                // Android is doing "mod 360"
                mPathBuffer.addCircle(center.x, center.y, radius, Path.Direction.CW);
            } else {
                if (drawRoundedSlices) {
                    mPathBuffer.arcTo(roundedCircleBox, startAngleOuter + 180, -180);
                }

                mPathBuffer.arcTo(
                        circleBox,
                        startAngleOuter,
                        sweepAngleOuter
                );
            }

            // API < 21 does not receive floats in addArc, but a RectF
            mInnerRectBuffer.set(
                    center.x - innerRadius,
                    center.y - innerRadius,
                    center.x + innerRadius,
                    center.y + innerRadius);

            if (drawInnerArc && (innerRadius > 0.f || accountForSliceSpacing)) {
                if (accountForSliceSpacing) {
                    float minSpacedRadius =
                            calculateMinimumRadiusForSpacedSlice(
                                    center, radius,
                                    sliceAngle * phaseY,
                                    arcStartPointX, arcStartPointY,
                                    startAngleOuter,
                                    sweepAngleOuter);

                    if (minSpacedRadius < 0.f)
                        minSpacedRadius = -minSpacedRadius;

                    innerRadius = Math.max(innerRadius, minSpacedRadius);
                }

                final float sliceSpaceAngleInner = visibleAngleCount == 1 || innerRadius == 0.f ?
                        0f :
                        sliceSpace / (Utils.FDEG2RAD * innerRadius);
                final float startAngleInner = rotationAngle + (angle + sliceSpaceAngleInner * 0.5f) * phaseY;
                float sweepAngleInner = (sliceAngle - sliceSpaceAngleInner) * phaseY;
                if (sweepAngleInner < 0f) {
                    sweepAngleInner = 0f;
                }
                final float endAngleInner = startAngleInner + sweepAngleInner;

                if (sweepAngleOuter >= 360f && sweepAngleOuter % 360f <= Utils.FLOAT_EPSILON) {
                    // Android is doing "mod 360"
                    mPathBuffer.addCircle(center.x, center.y, innerRadius, Path.Direction.CCW);
                } else {
                    double endAngleInnerRad = endAngleInner * Utils.FDEG2RAD;

                    float sinEndAngleInner = (float)Math.sin(endAngleInnerRad);
                    float cosEndAngleInner = (float)Math.cos(endAngleInnerRad);

                    if (drawRoundedSlices) {
                        float x = center.x + (radius - roundedRadius) * cosEndAngleInner;
                        float y = center.y + (radius - roundedRadius) * sinEndAngleInner;
                        roundedCircleBox.set(x - roundedRadius, y - roundedRadius, x + roundedRadius, y + roundedRadius);
                        mPathBuffer.arcTo(roundedCircleBox, endAngleInner, 180);
                    } else {
                        mPathBuffer.lineTo(
                                center.x + innerRadius * cosEndAngleInner,
                                center.y + innerRadius * sinEndAngleInner);
                    }

                    mPathBuffer.arcTo(
                            mInnerRectBuffer,
                            endAngleInner,
                            -sweepAngleInner
                    );
                }
            } else {
                if (sweepAngleOuter % 360f > Utils.FLOAT_EPSILON) {
                    if (accountForSliceSpacing) {

                        float angleMiddle = startAngleOuter + sweepAngleOuter * 0.5f;

                        float sliceSpaceOffset =
                                calculateMinimumRadiusForSpacedSlice(
                                        center,
                                        radius,
                                        sliceAngle * phaseY,
                                        arcStartPointX,
                                        arcStartPointY,
                                        startAngleOuter,
                                        sweepAngleOuter);

                        double angleMiddleRad = angleMiddle * Utils.FDEG2RAD;

                        float arcEndPointX = center.x +
                                sliceSpaceOffset * (float) Math.cos(angleMiddleRad);
                        float arcEndPointY = center.y +
                                sliceSpaceOffset * (float) Math.sin(angleMiddleRad);

                        mPathBuffer.lineTo(
                                arcEndPointX,
                                arcEndPointY);

                    } else {
                        mPathBuffer.lineTo(
                                center.x,
                                center.y);
                    }
                }

            }

            mPathBuffer.close();
            mBitmapCanvas.drawPath(mPathBuffer, mRenderPaint);
            angle += sliceAngle * phaseX;
        }

        MPPointF.recycleInstance(center);
    }

    @Override
    public void drawValues(@NotNull Canvas c) {
        MPPointF center = mChart.getCenterCircleBox();

        // get whole the radius
        float radius = mChart.getRadius();
        float rotationAngle = mChart.getRotationAngle();
        float[] drawAngles = mChart.getDrawAngles();
        float[] absoluteAngles = mChart.getAbsoluteAngles();

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        float roundedRadius = (radius - (radius * mChart.getHoleRadius() / 100f)) * 0.5f;
        float holeRadiusPercent = mChart.getHoleRadius() / 100.f;
        float labelRadiusOffset = radius / 10f * 3.6f;

        if (mChart.isDrawHoleEnabled()) {
            labelRadiusOffset = (radius - (radius * holeRadiusPercent)) * 0.5f;

            if (!mChart.isDrawSlicesUnderHoleEnabled() && mChart.isDrawRoundedSlicesEnabled()) {
                // Add curved circle slice and spacing to rotation angle, so that it sits nicely inside
                rotationAngle += roundedRadius * 360 / (Math.PI * 2f * radius);
            }
        }

        float labelRadius = radius - labelRadiusOffset;

        PieData data = mChart.getData();
        List<IPieDataSet> dataSets = data.getDataSets();

        float yValueSum = data.getYValueSum();

        boolean drawEntryLabels = mChart.isDrawEntryLabelsEnabled();

        float angle;
        int xIndex = 0;

        c.save();

        float offset = Utils.convertDpToPixel(5.f);

        for (int i = 0; i < dataSets.size(); i++) {
            IPieDataSet dataSet = dataSets.get(i);

            boolean drawValues = dataSet.isDrawValuesEnabled();

            if (!drawValues && !drawEntryLabels)
                continue;

            int xValuePosition = dataSet.getXValuePosition();
            int yValuePosition = dataSet.getYValuePosition();

            // apply the text-styling defined by the DataSet
            applyValueTextStyle(dataSet);

            float lineHeight = Utils.calcTextHeight(mValuePaint, "Q")
                    + Utils.convertDpToPixel(4f);

            IValueFormatter formatter = dataSet.getValueFormatter();

            int entryCount = dataSet.getEntryCount();

            boolean isUseValueColorForLineEnabled = dataSet.isUseValueColorForLineEnabled();
            int valueLineColor = dataSet.getValueLineColor();

            mValueLinePaint.setStrokeWidth(Utils.convertDpToPixel(dataSet.getValueLineWidth()));

            float sliceSpace = getSliceSpace(dataSet);

            MPPointF iconsOffset = MPPointF.getInstance(dataSet.getIconsOffset());
            iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x);
            iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y);

            for (int j = 0; j < entryCount; j++) {
                PieEntry entry = dataSet.getEntryForIndex(j);

                if (xIndex == 0)
                    angle = 0.f;
                else
                    angle = absoluteAngles[xIndex - 1] * phaseX;

                float sliceAngle = drawAngles[xIndex];
                float sliceSpaceMiddleAngle = sliceSpace / (Utils.FDEG2RAD * labelRadius);

                // offset needed to center the drawn text in the slice
                float angleOffset = (sliceAngle - sliceSpaceMiddleAngle * 0.5f) * 0.5f;

                angle = angle + angleOffset;

                float transformedAngle = rotationAngle + angle * phaseY;

                float value = mChart.isUsePercentValuesEnabled() ? entry.getY()
                        / yValueSum * 100f : entry.getY();
                String entryLabel = entry.getLabel();

                float transformedAngleRad = transformedAngle * Utils.FDEG2RAD;

                float sliceXBase = (float) Math.cos(transformedAngleRad);
                float sliceYBase = (float) Math.sin(transformedAngleRad);

                boolean drawXOutside = drawEntryLabels &&
                        xValuePosition == PieDataSet.VALUE_POSITION_OUTSIDE_SLICE;
                boolean drawYOutside = drawValues &&
                        yValuePosition == PieDataSet.VALUE_POSITION_OUTSIDE_SLICE;
                boolean drawXInside = drawEntryLabels &&
                        xValuePosition == PieDataSet.VALUE_POSITION_INSIDE_SLICE;
                boolean drawYInside = drawValues &&
                        yValuePosition == PieDataSet.VALUE_POSITION_INSIDE_SLICE;

                if (drawXOutside || drawYOutside) {
                    float valueLineLength1 = dataSet.getValueLinePart1Length();
                    float valueLineLength2 = dataSet.getValueLinePart2Length();
                    float valueLinePart1OffsetPercentage = dataSet.getValueLinePart1OffsetPercentage() / 100.f;

                    float pt2x, pt2y;
                    float labelPtx, labelPty;

                    float line1Radius;

                    if (mChart.isDrawHoleEnabled())
                        line1Radius = (radius - (radius * holeRadiusPercent))
                                * valueLinePart1OffsetPercentage
                                + (radius * holeRadiusPercent);
                    else
                        line1Radius = radius * valueLinePart1OffsetPercentage;

                    float polyline2Width = dataSet.isValueLineVariableLength()
                            ? labelRadius * valueLineLength2 * (float) Math.abs(Math.sin(
                            transformedAngle * Utils.FDEG2RAD))
                            : labelRadius * valueLineLength2;

                    float pt0x = line1Radius * sliceXBase + center.x;
                    float pt0y = line1Radius * sliceYBase + center.y;

                    float pt1x = labelRadius * (1 + valueLineLength1) * sliceXBase + center.x;
                    float pt1y = labelRadius * (1 + valueLineLength1) * sliceYBase + center.y;

                    if (transformedAngle % 360.0 >= 90.0 && transformedAngle % 360.0 <= 270.0) {
                        pt2x = pt1x - polyline2Width;
                        pt2y = pt1y;

                        mValuePaint.setTextAlign(Align.RIGHT);

                        if(drawXOutside)
                            mEntryLabelsPaint.setTextAlign(Align.RIGHT);

                        labelPtx = pt2x - offset;
                    } else {
                        pt2x = pt1x + polyline2Width;
                        pt2y = pt1y;
                        mValuePaint.setTextAlign(Align.LEFT);

                        if(drawXOutside)
                            mEntryLabelsPaint.setTextAlign(Align.LEFT);

                        labelPtx = pt2x + offset;
                    }

                    labelPty = pt2y;

                    int lineColor = ColorTemplate.COLOR_NONE;

                    if (isUseValueColorForLineEnabled)
                        lineColor = dataSet.getColor(j);
                    else if (valueLineColor != ColorTemplate.COLOR_NONE)
                        lineColor = valueLineColor;

                    if (lineColor != ColorTemplate.COLOR_NONE) {
                        mValueLinePaint.setColor(lineColor);
                        c.drawLine(pt0x, pt0y, pt1x, pt1y, mValueLinePaint);
                        c.drawLine(pt1x, pt1y, pt2x, pt2y, mValueLinePaint);
                    }

                    // draw everything, depending on settings
                    if (drawXOutside && drawYOutside) {
                        drawValue(c,
                                formatter,
                                value,
                                entry,
                                0,
                                labelPtx,
                                labelPty,
                                dataSet.getValueTextColor(j));

                        if (j < data.getEntryCount() && entryLabel != null) {
                            drawEntryLabel(c, entryLabel, labelPtx, labelPty + lineHeight);
                        }

                    } else if (drawXOutside) {
                        if (j < data.getEntryCount() && entryLabel != null) {
                            drawEntryLabel(c, entryLabel, labelPtx, labelPty + lineHeight * 0.5f);
                        }
                    } else if (drawYOutside) {
                        drawValue(
                                c,
                                formatter,
                                value,
                                entry,
                                0,
                                labelPtx,
                                labelPty + lineHeight * 0.5f,
                                dataSet.getValueTextColor(j));
                    }
                }

                if (drawXInside || drawYInside) {
                    // calculate the text position
                    float x = labelRadius * sliceXBase + center.x;
                    float y = labelRadius * sliceYBase + center.y;

                    mValuePaint.setTextAlign(Align.CENTER);

                    // draw everything, depending on settings
                    if (drawXInside && drawYInside) {
                        drawValue(c, formatter, value, entry, 0, x, y, dataSet.getValueTextColor(j));

                        if (j < data.getEntryCount() && entryLabel != null) {
                            drawEntryLabel(c, entryLabel, x, y + lineHeight);
                        }
                    } else if (drawXInside) {
                        if (j < data.getEntryCount() && entryLabel != null) {
                            drawEntryLabel(c, entryLabel, x, y + lineHeight * 0.5f);
                        }
                    } else {
                        drawValue(c, formatter, value, entry, 0, x, y + lineHeight * 0.5f, dataSet.getValueTextColor(j));
                    }
                }

                if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {
                    Drawable icon = entry.getIcon();

                    float x = (labelRadius + iconsOffset.y) * sliceXBase + center.x;
                    float y = (labelRadius + iconsOffset.y) * sliceYBase + center.y;
                    y += iconsOffset.x;

                    Utils.drawImage(
                            c,
                            icon,
                            (int)x,
                            (int)y,
                            icon.getIntrinsicWidth(),
                            icon.getIntrinsicHeight());
                }

                xIndex++;
            }

            MPPointF.recycleInstance(iconsOffset);
        }
        MPPointF.recycleInstance(center);
        c.restore();
    }

    /**
     * Draws an entry label at the specified position.
     */
    protected void drawEntryLabel(
            @NotNull Canvas c,
            @NotNull String label,
            float x,
            float y
    ) {
        c.drawText(label, x, y, mEntryLabelsPaint);
    }

    @Override
    public void drawExtras(@NotNull Canvas c) {
        drawHole();
        c.drawBitmap(mDrawBitmap.get(), 0, 0, null);
        drawCenterText(c);
    }

    private final Path mHoleCirclePath = new Path();

    /**
     * draws the hole in the center of the chart and the transparent circle /
     * hole
     */
    protected void drawHole() {
        if (mChart.isDrawHoleEnabled() && mBitmapCanvas != null) {
            float radius = mChart.getRadius();
            float holeRadius = radius * (mChart.getHoleRadius() / 100);
            MPPointF center = mChart.getCenterCircleBox();

            if (Color.alpha(mHolePaint.getColor()) > 0) {
                // draw the hole-circle
                mBitmapCanvas.drawCircle(
                        center.x, center.y,
                        holeRadius, mHolePaint);
            }

            // only draw the circle if it can be seen (not covered by the hole)
            if (Color.alpha(mTransparentCirclePaint.getColor()) > 0 &&
                    mChart.getTransparentCircleRadius() > mChart.getHoleRadius()) {

                int alpha = mTransparentCirclePaint.getAlpha();
                float secondHoleRadius = radius * (mChart.getTransparentCircleRadius() / 100);

                mTransparentCirclePaint.setAlpha((int) ((float) alpha * mAnimator.getPhaseX() * mAnimator.getPhaseY()));

                // draw the transparent-circle
                mHoleCirclePath.reset();
                mHoleCirclePath.addCircle(center.x, center.y, secondHoleRadius, Path.Direction.CW);
                mHoleCirclePath.addCircle(center.x, center.y, holeRadius, Path.Direction.CCW);
                mBitmapCanvas.drawPath(mHoleCirclePath, mTransparentCirclePaint);

                // reset alpha
                mTransparentCirclePaint.setAlpha(alpha);
            }
            MPPointF.recycleInstance(center);
        }
    }

    protected Path mDrawCenterTextPathBuffer = new Path();

    /**
     * draws the description text in the center of the pie chart makes most
     * sense when center-hole is enabled
     */
    protected void drawCenterText(Canvas c) {
        CharSequence centerText = mChart.getCenterText();

        if (mChart.isDrawCenterTextEnabled() && centerText != null) {

            MPPointF center = mChart.getCenterCircleBox();
            MPPointF offset = mChart.getCenterTextOffset();

            float x = center.x + offset.x;
            float y = center.y + offset.y;

            float innerRadius = mChart.isDrawHoleEnabled() && !mChart.isDrawSlicesUnderHoleEnabled()
                    ? mChart.getRadius() * (mChart.getHoleRadius() / 100f)
                    : mChart.getRadius();

            RectF holeRect = mRectBuffer[0];
            holeRect.left = x - innerRadius;
            holeRect.top = y - innerRadius;
            holeRect.right = x + innerRadius;
            holeRect.bottom = y + innerRadius;
            RectF boundingRect = mRectBuffer[1];
            boundingRect.set(holeRect);

            float radiusPercent = mChart.getCenterTextRadiusPercent() / 100f;
            if (radiusPercent > 0f) {
                boundingRect.inset(
                        (boundingRect.width() - boundingRect.width() * radiusPercent) * 0.5f,
                        (boundingRect.height() - boundingRect.height() * radiusPercent) * 0.5f
                );
            }

            if (!centerText.equals(mCenterTextLastValue) ||
                    !boundingRect.equals(mCenterTextLastBounds)
            ) {
                // Next time we won't recalculate StaticLayout...
                mCenterTextLastBounds.set(boundingRect);
                mCenterTextLastValue = centerText;

                float width = mCenterTextLastBounds.width();

                // If width is 0, it will crash. Always have a minimum of 1
                mCenterTextLayout = new StaticLayout(centerText, 0, centerText.length(),
                        mCenterTextPaint,
                        (int) Math.max(Math.ceil(width), 1.f),
                        Layout.Alignment.ALIGN_CENTER, 1.f, 0.f, false);
            }

            //float layoutWidth = Utils.getStaticLayoutMaxWidth(mCenterTextLayout);
            float layoutHeight = mCenterTextLayout.getHeight();

            c.save();
            if (Build.VERSION.SDK_INT >= 18) {
                Path path = mDrawCenterTextPathBuffer;
                path.reset();
                path.addOval(holeRect, Path.Direction.CW);
                c.clipPath(path);
            }

            c.translate(boundingRect.left, boundingRect.top + (boundingRect.height() - layoutHeight) / 2.f);
            mCenterTextLayout.draw(c);

            c.restore();

            MPPointF.recycleInstance(center);
            MPPointF.recycleInstance(offset);
        }
    }

    protected RectF mDrawHighlightedRectF = new RectF();

    @Override
    public void drawHighlighted(@NotNull Canvas c, @NotNull Highlight[] indices) {

        /* Skip entirely if using rounded circle slices, because it doesn't make sense to highlight
         * in this way.
         * TODO: add support for changing slice color with highlighting rather than only shifting the slice
         */

        boolean drawInnerArc = mChart.isDrawHoleEnabled() && !mChart.isDrawSlicesUnderHoleEnabled();
        if (drawInnerArc && mChart.isDrawRoundedSlicesEnabled())
            return;

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        float angle;
        float rotationAngle = mChart.getRotationAngle();

        float[] drawAngles = mChart.getDrawAngles();
        float[] absoluteAngles = mChart.getAbsoluteAngles();
        MPPointF center = mChart.getCenterCircleBox();
        float radius = mChart.getRadius();
        float userInnerRadius = drawInnerArc
                ? radius * (mChart.getHoleRadius() / 100.f)
                : 0.f;

        final RectF highlightedCircleBox = mDrawHighlightedRectF;
        highlightedCircleBox.set(0,0,0,0);

        for (Highlight highlight : indices) {
            // get the index to highlight
            int index = (int) highlight.getX();

            if (index >= drawAngles.length)
                continue;

            IPieDataSet set = mChart.getData().getDataSetByIndex(highlight.getDataSetIndex());

            if (!set.isHighlightEnabled())
                continue;

            final int entryCount = set.getEntryCount();
            int visibleAngleCount = 0;

            for (int j = 0; j < entryCount; j++) {
                // draw only if the value is greater than zero
                if ((Math.abs(set.getEntryForIndex(j).getY()) > Utils.FLOAT_EPSILON)) {
                    visibleAngleCount++;
                }
            }

            if (index == 0)
                angle = 0.f;
            else
                angle = absoluteAngles[index - 1] * phaseX;

            float sliceSpace = visibleAngleCount <= 1 ? 0.f : set.getSliceSpace();

            float sliceAngle = drawAngles[index];
            float innerRadius = userInnerRadius;

            float shift = set.getSelectionShift();
            float highlightedRadius = radius + shift;
            highlightedCircleBox.set(mChart.getCircleBox());
            highlightedCircleBox.inset(-shift, -shift);

            boolean accountForSliceSpacing = sliceSpace > 0.f && sliceAngle <= 180.f;

            Integer highlightColor = set.getHighlightColor();
            if (highlightColor == null)
                highlightColor = set.getColor(index);
            mRenderPaint.setColor(highlightColor);

            float sliceSpaceAngleOuter = visibleAngleCount == 1 ?
                    0.f :
                    sliceSpace / (Utils.FDEG2RAD * radius);

            float sliceSpaceAngleShifted = visibleAngleCount == 1 ?
                    0.f :
                    sliceSpace / (Utils.FDEG2RAD * highlightedRadius);

            float startAngleOuter = rotationAngle + (angle + sliceSpaceAngleOuter * 0.5f) * phaseY;
            float sweepAngleOuter = (sliceAngle - sliceSpaceAngleOuter) * phaseY;

            if (sweepAngleOuter < 0f) {
                sweepAngleOuter = 0f;
            }

            float startAngleShifted = rotationAngle + (angle + sliceSpaceAngleShifted * 0.5f) * phaseY;
            float sweepAngleShifted = (sliceAngle - sliceSpaceAngleShifted) * phaseY;
            if (sweepAngleShifted < 0f) {
                sweepAngleShifted = 0f;
            }

            mPathBuffer.reset();

            if (sweepAngleOuter >= 360.f && sweepAngleOuter % 360f <= Utils.FLOAT_EPSILON) {
                // Android is doing "mod 360"
                mPathBuffer.addCircle(center.x, center.y, highlightedRadius, Path.Direction.CW);
            } else {
                double startAngleShiftedRad = startAngleShifted * Utils.FDEG2RAD;

                mPathBuffer.moveTo(
                        center.x + highlightedRadius * (float) Math.cos(startAngleShiftedRad),
                        center.y + highlightedRadius * (float) Math.sin(startAngleShiftedRad));

                mPathBuffer.arcTo(
                        highlightedCircleBox,
                        startAngleShifted,
                        sweepAngleShifted
                );
            }

            float sliceSpaceRadius = 0.f;
            if (accountForSliceSpacing) {
                double startAngleOuterRad = startAngleOuter * Utils.FDEG2RAD;

                sliceSpaceRadius =
                        calculateMinimumRadiusForSpacedSlice(
                                center, radius,
                                sliceAngle * phaseY,
                                center.x + radius * (float) Math.cos(startAngleOuterRad),
                                center.y + radius * (float) Math.sin(startAngleOuterRad),
                                startAngleOuter,
                                sweepAngleOuter);
            }

            // API < 21 does not receive floats in addArc, but a RectF
            mInnerRectBuffer.set(
                    center.x - innerRadius,
                    center.y - innerRadius,
                    center.x + innerRadius,
                    center.y + innerRadius);

            if (drawInnerArc && (innerRadius > 0f || accountForSliceSpacing)) {
                if (accountForSliceSpacing) {
                    float minSpacedRadius = sliceSpaceRadius;

                    if (minSpacedRadius < 0f)
                        minSpacedRadius = -minSpacedRadius;

                    innerRadius = Math.max(innerRadius, minSpacedRadius);
                }

                float sliceSpaceAngleInner = visibleAngleCount == 1 || innerRadius == 0.f ?
                        0.f :
                        sliceSpace / (Utils.FDEG2RAD * innerRadius);
                float startAngleInner = rotationAngle + (angle + sliceSpaceAngleInner * 0.5f) * phaseY;
                float sweepAngleInner = (sliceAngle - sliceSpaceAngleInner) * phaseY;
                if (sweepAngleInner < 0.f) {
                    sweepAngleInner = 0.f;
                }
                float endAngleInner = startAngleInner + sweepAngleInner;

                if (sweepAngleOuter >= 360.f && sweepAngleOuter % 360f <= Utils.FLOAT_EPSILON) {
                    // Android is doing "mod 360"
                    mPathBuffer.addCircle(center.x, center.y, innerRadius, Path.Direction.CCW);
                } else {
                    double endAngleInnerRad = endAngleInner * Utils.FDEG2RAD;

                    mPathBuffer.lineTo(
                            center.x + innerRadius * (float) Math.cos(endAngleInnerRad),
                            center.y + innerRadius * (float) Math.sin(endAngleInnerRad));

                    mPathBuffer.arcTo(
                            mInnerRectBuffer,
                            endAngleInner,
                            -sweepAngleInner
                    );
                }
            } else {
                if (sweepAngleOuter % 360f > Utils.FLOAT_EPSILON) {

                    if (accountForSliceSpacing) {
                        float angleMiddle = startAngleOuter + sweepAngleOuter * 0.5f;
                        double angleMiddleRad = angleMiddle * Utils.FDEG2RAD;

                        float arcEndPointX = center.x +
                                sliceSpaceRadius * (float) Math.cos(angleMiddleRad);
                        float arcEndPointY = center.y +
                                sliceSpaceRadius * (float) Math.sin(angleMiddleRad);

                        mPathBuffer.lineTo(
                                arcEndPointX,
                                arcEndPointY);

                    } else {

                        mPathBuffer.lineTo(
                                center.x,
                                center.y);
                    }

                }

            }

            mPathBuffer.close();

            mBitmapCanvas.drawPath(mPathBuffer, mRenderPaint);
        }

        MPPointF.recycleInstance(center);
    }

    /**
     * This gives all pie-slices a rounded edge.
     */
    protected void drawRoundedSlices(@NotNull Canvas c) {
        if (!mChart.isDrawRoundedSlicesEnabled())
            return;

        IPieDataSet dataSet = mChart.getData().getDataSet();

        if (!dataSet.isVisible())
            return;

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        MPPointF center = mChart.getCenterCircleBox();
        float r = mChart.getRadius();

        // calculate the radius of the "slice-circle"
        float circleRadius = (r - (r * mChart.getHoleRadius() / 100f)) * 0.5f;

        float[] drawAngles = mChart.getDrawAngles();
        float angle = mChart.getRotationAngle();

        for (int j = 0; j < dataSet.getEntryCount(); j++) {
            float sliceAngle = drawAngles[j];

            Entry e = dataSet.getEntryForIndex(j);

            // draw only if the value is greater than zero
            if ((Math.abs(e.getY()) > Utils.FLOAT_EPSILON)) {
                double t = ((angle + sliceAngle) * phaseY) * Utils.FDEG2RAD;

                float x = (r - circleRadius) * (float)Math.cos(t) + center.x;
                float y = (r - circleRadius) * (float)Math.sin(t) + center.y;

                mRenderPaint.setColor(dataSet.getColor(j));
                mBitmapCanvas.drawCircle(x, y, circleRadius, mRenderPaint);
            }

            angle += sliceAngle * phaseX;
        }
        MPPointF.recycleInstance(center);
    }

    /**
     * Releases the drawing bitmap.
     */
    public void releaseBitmap() {
        if (mBitmapCanvas != null) {
            mBitmapCanvas.setBitmap(null);
            mBitmapCanvas = null;
        }

        if (mDrawBitmap != null) {
            Bitmap drawBitmap = mDrawBitmap.get();
            if (drawBitmap != null) {
                drawBitmap.recycle();
            }
            mDrawBitmap.clear();
            mDrawBitmap = null;
        }
    }
}
