package model;

public final class FarmGrowthRules {

    /**
     * Backwards-compatible default crop stage count (number of crop images available: *_plant1..*_plantN).
     *
     * Growth phases are 1-based and always include phase 1 as the initial "sowed/no-image" phase.
     * With the current rules, maxGrowthPhases equals the number of crop image assets for the seed:
     * - phase 1 renders no crop image
     * - phase 2 renders plant1
     * - ...
     * - phase maxGrowthPhases renders plant(maxGrowthPhases-1)
     */
    public static final int TOTAL_GROWTH_STAGES = 4;
    public static final int DEFAULT_CROP_STAGES = TOTAL_GROWTH_STAGES;
    public static final int DEFAULT_MAX_GROWTH_PHASES = DEFAULT_CROP_STAGES;
    public static final double DEFAULT_GROWTH_SECONDS = 60 * 60.0;
    public static final double WATER_REDUCTION_SECONDS = 5 * 60.0;

    private FarmGrowthRules() {
    }

    public static double resolveTotalGrowthSeconds(int seedItemId) {
        return DEFAULT_GROWTH_SECONDS;
    }

    public static double resolvePhaseDurationSeconds(double totalGrowthSeconds) {
        return resolvePhaseDurationSeconds(totalGrowthSeconds, DEFAULT_MAX_GROWTH_PHASES);
    }

    public static double resolvePhaseDurationSeconds(double totalGrowthSeconds, int maxGrowthPhases) {
        double normalized = Math.max(0.0, totalGrowthSeconds);
        if (normalized <= 0.0) {
            return 0.0;
        }
        int phases = Math.max(1, maxGrowthPhases);
        return normalized / phases;
    }

    public static double resolveWaterCooldownSeconds(double totalGrowthSeconds) {
        return resolvePhaseDurationSeconds(totalGrowthSeconds);
    }

    public static double resolveWaterCooldownSeconds(double totalGrowthSeconds, int maxGrowthPhases) {
        return resolvePhaseDurationSeconds(totalGrowthSeconds, maxGrowthPhases);
    }

    public static double resolveWaterReductionSeconds(double totalGrowthSeconds) {
        return WATER_REDUCTION_SECONDS;
    }

    public static double clampAccumulatedWaterReduction(double accumulatedWaterReduction, double totalGrowthSeconds) {
        return Math.max(0.0, Math.min(Math.max(0.0, totalGrowthSeconds), accumulatedWaterReduction));
    }

    public static double computeEffectiveElapsed(double plantedAt, double accumulatedWaterReduction, double nowSeconds) {
        if (plantedAt < 0) {
            return 0.0;
        }
        return Math.max(0.0, (nowSeconds - plantedAt) + Math.max(0.0, accumulatedWaterReduction));
    }

    public static boolean isReady(double plantedAt, double accumulatedWaterReduction, double totalGrowthSeconds, double nowSeconds) {
        if (plantedAt < 0) {
            return false;
        }
        return computeEffectiveElapsed(plantedAt, accumulatedWaterReduction, nowSeconds) >= Math.max(0.0, totalGrowthSeconds);
    }

    /**
     * Returns the current growth phase, 1-based, including the initial sowed/no-image phase.
     * Example: if a crop has plant1..plant4, then maxGrowthPhases should be 4 (phase1 shows no crop image).
     */
    public static int getPhase(
        double plantedAt,
        double accumulatedWaterReduction,
        double totalGrowthSeconds,
        int maxGrowthPhases,
        double nowSeconds
    ) {
        if (plantedAt < 0) {
            return 0;
        }

        int phases = Math.max(1, maxGrowthPhases);
        double phaseDuration = resolvePhaseDurationSeconds(totalGrowthSeconds, phases);
        if (phaseDuration <= 0.0) {
            return phases;
        }

        double effectiveElapsed = computeEffectiveElapsed(plantedAt, accumulatedWaterReduction, nowSeconds);
        int completedPhases = (int) Math.floor(effectiveElapsed / phaseDuration);
        int phase = completedPhases + 1;

        if (phase < 1) {
            phase = 1;
        }
        if (phase > phases) {
            phase = phases;
        }
        return phase;
    }

    public static boolean canWater(
        double plantedAt,
        double lastWateredAt,
        double accumulatedWaterReduction,
        double totalGrowthSeconds,
        double waterReductionSeconds,
        double nowSeconds
    ) {
        return canWater(plantedAt, lastWateredAt, accumulatedWaterReduction, totalGrowthSeconds, DEFAULT_MAX_GROWTH_PHASES, waterReductionSeconds, nowSeconds);
    }

    public static boolean canWater(
        double plantedAt,
        double lastWateredAt,
        double accumulatedWaterReduction,
        double totalGrowthSeconds,
        int maxGrowthPhases,
        double waterReductionSeconds,
        double nowSeconds
    ) {
        if (plantedAt < 0 || isReady(plantedAt, accumulatedWaterReduction, totalGrowthSeconds, nowSeconds)) {
            return false;
        }

        double phaseDuration = resolvePhaseDurationSeconds(totalGrowthSeconds, maxGrowthPhases);
        if (phaseDuration <= 0.0) {
            return false;
        }

        int currentPhase = getPhase(plantedAt, accumulatedWaterReduction, totalGrowthSeconds, maxGrowthPhases, nowSeconds);
        double currentPhaseStartAt = plantedAt + Math.max(
            0.0,
            ((Math.max(1, currentPhase) - 1) * phaseDuration) - Math.max(0.0, accumulatedWaterReduction)
        );

        return lastWateredAt < (currentPhaseStartAt - 1.0e-6);
    }

    public static void applyProfile(FarmState.PlotData plotData, int seedItemId) {
        if (plotData == null || seedItemId <= 0) {
            return;
        }

        // Seed-specific: maxGrowthPhases includes the initial "sowed/no-image" phase (phase 1).
        plotData.maxGrowthPhases = Math.max(1, ItemDatabase.getMaxGrowthPhasesForSeed(seedItemId));
        plotData.totalGrowthSeconds = resolveTotalGrowthSeconds(seedItemId);
        plotData.waterCooldownSeconds = resolveWaterCooldownSeconds(plotData.totalGrowthSeconds, plotData.maxGrowthPhases);
        plotData.waterReductionSeconds = resolveWaterReductionSeconds(plotData.totalGrowthSeconds);
        plotData.accumulatedWaterReduction = clampAccumulatedWaterReduction(
            plotData.accumulatedWaterReduction,
            plotData.totalGrowthSeconds
        );
    }
}
