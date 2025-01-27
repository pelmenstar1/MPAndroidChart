
package com.github.mikephil.charting.data.filter;

import android.annotation.TargetApi;
import android.os.Build;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Implemented according to Wiki-Pseudocode {@link}
 * http://en.wikipedia.org/wiki/Ramer�Douglas�Peucker_algorithm
 *
 * @author Philipp Baldauf & Phliipp Jahoda
 */
public class Approximator {

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public float[] reduceWithDouglasPeucker(float[] points, float tolerance) {
        int greatestIndex = 0;
        float greatestDistance = 0f;

        Line line = new Line(points[0], points[1], points[points.length - 2], points[points.length - 1]);

        for (int i = 2; i < points.length - 2; i += 2) {

            float distance = line.distance(points[i], points[i + 1]);

            if (distance > greatestDistance) {
                greatestDistance = distance;
                greatestIndex = i;
            }
        }

        if (greatestDistance > tolerance) {
            float[] reduced1 = reduceWithDouglasPeucker(Arrays.copyOfRange(points, 0, greatestIndex + 2), tolerance);
            float[] reduced2 = reduceWithDouglasPeucker(Arrays.copyOfRange(points, greatestIndex, points.length),
                    tolerance);

            float[] result2 = Arrays.copyOfRange(reduced2, 2, reduced2.length);

            return concat(reduced1, result2);
        } else {
            return line.points;
        }
    }

    /**
     * Combine arrays.
     */
    @NotNull
    float[] concat(@NotNull float[]... arrays) {
        int length = 0;
        for (float[] array : arrays) {
            length += array.length;
        }

        float[] result = new float[length];
        int index = 0;
        for (float[] array : arrays) {
            for (float element : array) {
                result[index++] = element;
            }
        }

        return result;
    }

    private static final class Line {
        public final float[] points;

        private final float sxey;
        private final float exsy;

        private final float dx;
        private final float dy;

        private final float length;

        public Line(float x1, float y1, float x2, float y2) {
            dx = x1 - x2;
            dy = y1 - y2;
            sxey = x1 * y2;
            exsy = x2 * y1;
            length = (float) Math.sqrt(dx * dx + dy * dy);

            points = new float[] { x1, y1, x2, y2 };
        }

        public float distance(float x, float y) {
            return Math.abs(dy * x - dx * y + sxey - exsy) / length;
        }
    }
}
