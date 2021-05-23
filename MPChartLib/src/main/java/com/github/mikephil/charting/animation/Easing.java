package com.github.mikephil.charting.animation;

import android.animation.TimeInterpolator;
import androidx.annotation.RequiresApi;

/**
 * Easing options.
 *
 * @author Daniel Cohen Gindi
 * @author Mick Ashton
 */
@SuppressWarnings("WeakerAccess")
@RequiresApi(11)
public class Easing {
    public interface EasingFunction extends TimeInterpolator {
        @Override
        float getInterpolation(float input);
    }

    private static final float DOUBLE_PI = 2f * (float) Math.PI;

    public static final EasingFunction Linear = input -> input;

    public static final EasingFunction EaseInQuad = input -> input * input;

    public static final EasingFunction EaseOutQuad = input -> -input * (input - 2f);

    public static final EasingFunction EaseInOutQuad = input -> {
        input *= 2f;

        if (input < 1f) {
            return 0.5f * input * input;
        }

        return -0.5f * ((--input) * (input - 2f) - 1f);
    };

    public static final EasingFunction EaseInCubic = input -> input * input * input;

    public static final EasingFunction EaseOutCubic = input -> {
        float i = input - 1;
        return i * i * i + 1f;
    };

    public static final EasingFunction EaseInOutCubic = input -> {
        input *= 2f;
        if (input < 1f) {
            return 0.5f * (float) Math.pow(input, 3);
        }
        input -= 2f;
        return 0.5f * ((float) Math.pow(input, 3) + 2f);
    };

    public static final EasingFunction EaseInQuart = input -> (float) Math.pow(input, 4);

    public static final EasingFunction EaseOutQuart = input -> {
        input--;
        return -((float) Math.pow(input, 4) - 1f);
    };

    public static final EasingFunction EaseInOutQuart = input -> {
        input *= 2f;
        if (input < 1f) {
            return 0.5f * (float) Math.pow(input, 4);
        }
        input -= 2f;
        return -0.5f * ((float) Math.pow(input, 4) - 2f);
    };

    public static final EasingFunction EaseInSine = input ->
            -(float) Math.cos(input * (Math.PI / 2f)) + 1f;

    public static final EasingFunction EaseOutSine = input ->
            (float) Math.sin(input * (Math.PI / 2f));

    public static final EasingFunction EaseInOutSine = input ->
            -0.5f * ((float) Math.cos(Math.PI * input) - 1f);

    public static final EasingFunction EaseInExpo = input ->
            (input == 0) ? 0f : (float) Math.pow(2f, 10f * (input - 1f));

    public static final EasingFunction EaseOutExpo = input ->
            (input == 1f) ? 1f : (-(float) Math.pow(2f, -10f * (input + 1f)));

    public static final EasingFunction EaseInOutExpo = input -> {
        if (input == 0) {
            return 0f;
        } else if (input == 1f) {
            return 1f;
        }

        input *= 2f;
        if (input < 1f) {
            return 0.5f * (float) Math.pow(2f, 10f * (input - 1f));
        }
        return 0.5f * (-(float) Math.pow(2f, -10f * --input) + 2f);
    };

    public static final EasingFunction EaseInCirc = input -> -((float) Math.sqrt(1f - input * input) - 1f);

    public static final EasingFunction EaseOutCirc = input -> {
        input--;
        return (float) Math.sqrt(1f - input * input);
    };

    public static final EasingFunction EaseInOutCirc = input -> {
        input *= 2f;
        if (input < 1f) {
            return -0.5f * ((float) Math.sqrt(1f - input * input) - 1f);
        }
        return 0.5f * ((float) Math.sqrt(1f - (input -= 2f) * input) + 1f);
    };

    public static final EasingFunction EaseInElastic = input -> {
        if (input == 0) {
            return 0f;
        } else if (input == 1) {
            return 1f;
        }

        float p = 0.3f;
        float s = p / DOUBLE_PI * (float) Math.asin(1f);
        return -((float) Math.pow(2f, 10f * (input -= 1f))
                *(float) Math.sin((input - s) * DOUBLE_PI / p));
    };

    public static final EasingFunction EaseOutElastic = input -> {
        if (input == 0) {
            return 0f;
        } else if (input == 1) {
            return 1f;
        }

        float p = 0.3f;
        float s = p / DOUBLE_PI * (float) Math.asin(1f);
        return 1f
                + (float) Math.pow(2f, -10f * input)
                * (float) Math.sin((input - s) * DOUBLE_PI / p);
    };

    public static final EasingFunction EaseInOutElastic = input -> {
        if (input == 0) {
            return 0f;
        }

        input *= 2f;
        if (input == 2) {
            return 1f;
        }

        float p = 1f / 0.45f;
        float s = 0.45f / DOUBLE_PI * (float) Math.asin(1f);
        if (input < 1f) {
            return -0.5f
                    * ((float) Math.pow(2f, 10f * (input -= 1f))
                    * (float) Math.sin((input * 1f - s) * DOUBLE_PI * p));
        }
        return 1f + 0.5f
                * (float) Math.pow(2f, -10f * (input -= 1f))
                * (float) Math.sin((input * 1f - s) * DOUBLE_PI * p);
    };

    public static final EasingFunction EaseInBack = input -> {
        final float s = 1.70158f;
        return input * input * ((s + 1f) * input - s);
    };

    public static final EasingFunction EaseOutBack = input -> {
        final float s = 1.70158f;
        input--;
        return (input * input * ((s + 1f) * input + s) + 1f);
    };

    public static final EasingFunction EaseInOutBack = input -> {
        float s = 1.70158f;
        input *= 2f;
        if (input < 1f) {
            return 0.5f * (input * input * (((s *= (1.525f)) + 1f) * input - s));
        }
        return 0.5f * ((input -= 2f) * input * (((s *= (1.525f)) + 1f) * input + s) + 2f);
    };

    public static final EasingFunction EaseInBounce = new EasingFunction() {
        public float getInterpolation(float input) {
            return 1f - EaseOutBounce.getInterpolation(1f - input);
        }
    };

    public static final EasingFunction EaseOutBounce = input -> {
        float s = 7.5625f;
        if (input < (1f / 2.75f)) {
            return s * input * input;
        } else if (input < (2f / 2.75f)) {
            return s * (input -= (1.5f / 2.75f)) * input + 0.75f;
        } else if (input < (2.5f / 2.75f)) {
            return s * (input -= (2.25f / 2.75f)) * input + 0.9375f;
        }
        return s * (input -= (2.625f / 2.75f)) * input + 0.984375f;
    };

    public static final EasingFunction EaseInOutBounce = input -> {
        if (input < 0.5f) {
            return EaseInBounce.getInterpolation(input * 2f) * 0.5f;
        }
        return EaseOutBounce.getInterpolation(input * 2f - 1f) * 0.5f + 0.5f;
    };

}
