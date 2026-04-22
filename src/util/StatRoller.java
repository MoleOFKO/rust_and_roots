package util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Applies the project's "decimal stat" rule:
 *
 * <p>The integer part is always applied. The fractional part is treated as a probability to
 * increase by +1 for that moment.
 *
 * <p>Examples:
 * <ul>
 *   <li>{@code 0.9} => 90% => 1, else 0</li>
 *   <li>{@code 1.2} => 20% => 2, else 1</li>
 * </ul>
 */
public final class StatRoller {

    private StatRoller() {
    }

    public static int rollToInt(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return 0;
        }

        if (value <= 0.0) {
            return 0;
        }

        double floored = Math.floor(value);
        if (floored >= Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }

        int base = (int) floored;
        double fraction = value - base;
        if (fraction <= 0.0) {
            return base;
        }

        // Defensive clamp for weird floating point values.
        fraction = Math.max(0.0, Math.min(1.0, fraction));

        boolean yes = ThreadLocalRandom.current().nextDouble(1.0) < fraction;
        if (!yes) {
            return base;
        }

        if (base >= Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }

        return base + 1;
    }
}

