package model;

public final class FarmGrowthRules {

    public static final int TOTAL_GROWTH_STAGES = 4;
    public static final double DEFAULT_GROWTH_PHASE_SECONDS = 10 * 60.0;
    public static final double DEFAULT_GROWTH_SECONDS = (TOTAL_GROWTH_STAGES - 1) * DEFAULT_GROWTH_PHASE_SECONDS;
    public static final double WATER_REDUCTION_SECONDS = 5 * 60.0;

    private FarmGrowthRules() {
    }

    public static double resolveTotalGrowthSeconds(int seedItemId) {
        int stages = ItemDatabase.resolveMaxGrowthPhasesForSeed(seedItemId);
        return resolveTotalGrowthSeconds(seedItemId, stages);
    }

    public static double resolveTotalGrowthSeconds(int seedItemId, int totalGrowthStages) {
        int safeStages = Math.max(1, totalGrowthStages);
        int timedPhases = Math.max(0, safeStages - 1);
        if (timedPhases <= 0) {
            return 0.0;
        }
        double baseSeconds = timedPhases * DEFAULT_GROWTH_PHASE_SECONDS;
        int cropVit = ItemDatabase.resolveCropGrowthVitForSeed(seedItemId);
        if (cropVit <= 0) {
            return baseSeconds;
        }

        double speedMultiplier = 1.0 + (Math.min(50, cropVit) * 0.05);
        return Math.max(60.0, baseSeconds / speedMultiplier);
    }

    public static double resolvePhaseDurationSeconds(double totalGrowthSeconds) {
        return resolvePhaseDurationSeconds(totalGrowthSeconds, TOTAL_GROWTH_STAGES);
    }

    public static double resolvePhaseDurationSeconds(double totalGrowthSeconds, int totalGrowthStages) {
        int safeStages = Math.max(1, totalGrowthStages);
        int timedPhases = Math.max(1, safeStages - 1);
        double normalized = Math.max(0.0, totalGrowthSeconds);
        if (normalized <= 0.0) {
            return 0.0;
        }
        return normalized / timedPhases;
    }

    public static double resolveWaterCooldownSeconds(double totalGrowthSeconds) {
        return resolvePhaseDurationSeconds(totalGrowthSeconds);
    }

    public static double resolveWaterCooldownSeconds(double totalGrowthSeconds, int totalGrowthStages) {
        return resolvePhaseDurationSeconds(totalGrowthSeconds, totalGrowthStages);
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
        return getStage(plantedAt, accumulatedWaterReduction, totalGrowthSeconds, nowSeconds, TOTAL_GROWTH_STAGES);
    }

    public static int getStage(
        double plantedAt,
        double accumulatedWaterReduction,
        double totalGrowthSeconds,
        double nowSeconds,
        int totalGrowthStages
    ) {
        if (plantedAt < 0) {
            return 0;
        }

        int safeStages = Math.max(1, totalGrowthStages);
        double phaseDuration = resolvePhaseDurationSeconds(totalGrowthSeconds, safeStages);
        if (phaseDuration <= 0.0) {
            return safeStages;
        }

        double effectiveElapsed = computeEffectiveElapsed(plantedAt, accumulatedWaterReduction, nowSeconds);
        int completedPhases = (int) Math.floor(effectiveElapsed / phaseDuration);
        int stage = completedPhases + 1;

        if (stage < 1) {
            stage = 1;
        }
        if (stage > safeStages) {
            stage = safeStages;
        }
        return stage;
    }

    public static int mapGrowthStageToPlantSpriteStage(int stageNumber1Based, int availableSpriteStages) {
        if (stageNumber1Based <= 0) {
            return 0;
        }

        int mapped = stageNumber1Based;

        // Required sequence:
        // stage 1 -> plant1, stage 2 -> plant2, stage 3 -> plant4, stage 4 -> plant3
        if (stageNumber1Based == 3 && availableSpriteStages >= 4) {
            mapped = 4;
        } else if (stageNumber1Based == 4 && availableSpriteStages >= 3) {
            mapped = 3;
        }

        if (availableSpriteStages > 0) {
            mapped = Math.min(mapped, availableSpriteStages);
        }
        return Math.max(0, mapped);
    }

    public static boolean canWater(
        double plantedAt,
        double lastWateredAt,
        double accumulatedWaterReduction,
        double totalGrowthSeconds,
        double waterReductionSeconds,
        double nowSeconds
    ) {
        return canWater(
            plantedAt,
            lastWateredAt,
            accumulatedWaterReduction,
            totalGrowthSeconds,
            waterReductionSeconds,
            nowSeconds,
            TOTAL_GROWTH_STAGES
        );
    }

    public static boolean canWater(
        double plantedAt,
        double lastWateredAt,
        double accumulatedWaterReduction,
        double totalGrowthSeconds,
        double waterReductionSeconds,
        double nowSeconds,
        int totalGrowthStages
    ) {
        if (plantedAt < 0 || isReady(plantedAt, accumulatedWaterReduction, totalGrowthSeconds, nowSeconds)) {
            return false;
        }

        int safeStages = Math.max(1, totalGrowthStages);
        double phaseDuration = resolvePhaseDurationSeconds(totalGrowthSeconds, safeStages);
        if (phaseDuration <= 0.0) {
            return false;
        }

        int currentStage = getStage(plantedAt, accumulatedWaterReduction, totalGrowthSeconds, nowSeconds, safeStages);
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

        int stages = ItemDatabase.resolveMaxGrowthPhasesForSeed(seedItemId);
        plotData.totalGrowthSeconds = resolveTotalGrowthSeconds(seedItemId, stages);
        plotData.waterCooldownSeconds = resolveWaterCooldownSeconds(plotData.totalGrowthSeconds, stages);
        plotData.waterReductionSeconds = resolveWaterReductionSeconds(plotData.totalGrowthSeconds);
        plotData.accumulatedWaterReduction = clampAccumulatedWaterReduction(
            plotData.accumulatedWaterReduction,
            plotData.totalGrowthSeconds
        );
    }
}
