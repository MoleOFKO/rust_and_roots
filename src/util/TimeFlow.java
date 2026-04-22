package util;

/**
 * Shared time-flow helpers.
 *
 * <p>Concepts:
 * <ul>
 *   <li>World time dilation: slows the whole simulation (day/night, mobs, etc.).</li>
 *   <li>Entity time flow: speeds up/slows down a single entity based on AGI.</li>
 * </ul>
 */
public final class TimeFlow {

    public static final int PLAYER_WALK_AGI_CAP = 15;
    public static final double NORMAL_AGI = 5.0;

    private TimeFlow() {
    }

    /**
     * Converts AGI into an entity-local time-flow multiplier.
     *
     * <p>This intentionally matches the existing movement curve used in the project:
     * {@code 0.6 + 0.4*(agi/NORMAL_AGI)}.
     */
    public static double entityTimeFlowFromAgi(double agi) {
        double safeAgi = Math.max(0.0, agi);
        return 0.6 + (0.4 * (safeAgi / NORMAL_AGI));
    }

    /**
     * Computes the world dilation factor used when the player enters "run" mode.
     *
     * <p>When the player's AGI is above the walk cap, we slow the world so that:
     * <pre>
     * worldDilation * timeFlow(fullAgi) == timeFlow(walkCappedAgi)
     * </pre>
     */
    public static double worldDilationForPlayerRun(int playerAgi, boolean runActive) {
        if (!runActive) {
            return 1.0;
        }

        double agi = Math.max(0.0, playerAgi);
        if (agi <= PLAYER_WALK_AGI_CAP) {
            return 1.0;
        }

        double full = entityTimeFlowFromAgi(agi);
        double capped = entityTimeFlowFromAgi(Math.min(agi, (double) PLAYER_WALK_AGI_CAP));

        if (full <= 0.0) {
            return 1.0;
        }

        return Math.max(0.0, capped / full);
    }
}

