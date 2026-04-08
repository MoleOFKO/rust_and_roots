package model;

public final class FarmGrowthRules {

    public static final int TOTAL_GROWTH_STAGES = 4;
    public static final double DEFAULT_GROWTH_SECONDS = 60 * 60.0;
    public static final double WATER_REDUCTION_SECONDS = 5 * 60.0;

    private FarmGrowthRules() {
    }

    public static double resolveTotalGrowthSeconds(int seedItemId) {
        return DEFAULT_GROWTH_SECONDS;
    }

    public static double resolvePhaseDurationSeconds(double totalGrowthSeconds) {
        double normalized = Math.max(0.0, totalGrowthSeconds);
        if (normalized <= 0.0) {
            return 0.0;
        }
        return normalized / TOTAL_GROWTH_STAGES;
    }

    public static double resolveWaterCooldownSeconds(double totalGrowthSeconds) {
        return resolvePhaseDurationSeconds(totalGrowthSeconds);
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

    public static int getStage(double plantedAt, double accumulatedWaterReduction, double totalGrowthSeconds, double nowSeconds) {
        if (plantedAt < 0) {
            return 0;
        }

        double phaseDuration = resolvePhaseDurationSeconds(totalGrowthSeconds);
        if (phaseDuration <= 0.0) {
            return TOTAL_GROWTH_STAGES;
        }

        double effectiveElapsed = computeEffectiveElapsed(plantedAt, accumulatedWaterReduction, nowSeconds);
        int completedPhases = (int) Math.floor(effectiveElapsed / phaseDuration);
        int stage = completedPhases + 1;

        if (stage < 1) {
            stage = 1;
        }
        if (stage > TOTAL_GROWTH_STAGES) {
            stage = TOTAL_GROWTH_STAGES;
        }
        return stage;
    }

    public static boolean canWater(
        double plantedAt,
        double lastWateredAt,
        double accumulatedWaterReduction,
        double totalGrowthSeconds,
        double waterReductionSeconds,
        double nowSeconds
    ) {
        if (plantedAt < 0 || isReady(plantedAt, accumulatedWaterReduction, totalGrowthSeconds, nowSeconds)) {
            return false;
        }

        double phaseDuration = resolvePhaseDurationSeconds(totalGrowthSeconds);
        if (phaseDuration <= 0.0) {
            return false;
        }

        int currentStage = getStage(plantedAt, accumulatedWaterReduction, totalGrowthSeconds, nowSeconds);
        double currentPhaseStartAt = plantedAt + Math.max(
            0.0,
            ((Math.max(1, currentStage) - 1) * phaseDuration) - Math.max(0.0, accumulatedWaterReduction)
        );

        return lastWateredAt < (currentPhaseStartAt - 1.0e-6);
    }

    public static void applyProfile(FarmState.PlotData plotData, int seedItemId) {
        if (plotData == null || seedItemId <= 0) {
            return;
        }

        plotData.totalGrowthSeconds = resolveTotalGrowthSeconds(seedItemId);
        plotData.waterCooldownSeconds = resolveWaterCooldownSeconds(plotData.totalGrowthSeconds);
        plotData.waterReductionSeconds = resolveWaterReductionSeconds(plotData.totalGrowthSeconds);
        plotData.accumulatedWaterReduction = clampAccumulatedWaterReduction(
            plotData.accumulatedWaterReduction,
            plotData.totalGrowthSeconds
        );
    }
}
