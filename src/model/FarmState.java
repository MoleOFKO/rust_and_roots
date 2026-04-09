package model;

import java.util.HashMap;
import java.util.Map;

public class FarmState {
    private Integer equippedWateringCanItemId = null;
    private Integer equippedSickleItemId = null;

    public static class PlotData {
        /** DB-driven farm fields. */
        public Integer seedItemId = null;
        public String seedItemName = null;
        public String cropItemName = null;
        public String cropSpriteKey = null;

        /**
         * Max growth phases includes the initial "sowed/no-image" phase.
         * Example: if crop images are plant1..plant4 then maxGrowthPhases is 4 (phase 1 shows no crop image).
         */
        public int maxGrowthPhases = FarmGrowthRules.DEFAULT_MAX_GROWTH_PHASES;

        public double totalGrowthSeconds = FarmGrowthRules.DEFAULT_GROWTH_SECONDS;
        public double plantedAt = -1;
        public double waterCooldownSeconds = FarmGrowthRules.resolveWaterCooldownSeconds(FarmGrowthRules.DEFAULT_GROWTH_SECONDS);
        public double lastWateredAt = -1;
        public double waterReductionSeconds = FarmGrowthRules.resolveWaterReductionSeconds(FarmGrowthRules.DEFAULT_GROWTH_SECONDS);
        public double accumulatedWaterReduction = 0;

        public boolean isEmpty() {
            return seedItemId == null || seedItemId <= 0;
        }

        public boolean isReady(double nowSeconds) {
            if (isEmpty()) return false;
            return FarmGrowthRules.isReady(plantedAt, accumulatedWaterReduction, totalGrowthSeconds, nowSeconds);
        }

        public boolean canWater(double nowSeconds) {
            if (isEmpty()) return false;
            return FarmGrowthRules.canWater(
                plantedAt,
                lastWateredAt,
                accumulatedWaterReduction,
                totalGrowthSeconds,
                maxGrowthPhases,
                waterReductionSeconds,
                nowSeconds
            );
        }

        public void water(double nowSeconds) {
            if (!canWater(nowSeconds)) return;
            lastWateredAt = nowSeconds;
            accumulatedWaterReduction = FarmGrowthRules.clampAccumulatedWaterReduction(
                accumulatedWaterReduction + Math.max(0.0, waterReductionSeconds),
                totalGrowthSeconds
            );
        }

        /**
         * Returns the crop image stage to render, where 0 means "no crop image" (freshly sowed).
         * For example, with maxGrowthPhases=4, this returns 0..3 and maps to plant1..plant3 by using stageNumber as-is.
         */
        public int getImageStage(double nowSeconds) {
            if (isEmpty()) return 0;
            int phases = Math.max(1, maxGrowthPhases);
            int phase = FarmGrowthRules.getPhase(plantedAt, accumulatedWaterReduction, totalGrowthSeconds, phases, nowSeconds);
            int cropStages = Math.max(0, phases - 1);
            int imageStage = Math.max(0, phase - 1);
            return Math.min(cropStages, imageStage);
        }

        public void clearPlant() {
            seedItemId = null;
            seedItemName = null;
            cropItemName = null;
            cropSpriteKey = null;
            maxGrowthPhases = FarmGrowthRules.DEFAULT_MAX_GROWTH_PHASES;
            plantedAt = -1;
            lastWateredAt = -1;
            accumulatedWaterReduction = 0;
        }
    }

    private final Map<String, PlotData> plots = new HashMap<>();

    public PlotData getPlot(String plotId) {
        return plots.computeIfAbsent(plotId, k -> new PlotData());
    }

    public Map<String, PlotData> getAllPlots() {
        return plots;
    }

    public Integer getEquippedWateringCanItemId() {
        return equippedWateringCanItemId;
    }

    public void setEquippedWateringCanItemId(Integer equippedWateringCanItemId) {
        this.equippedWateringCanItemId = normalizeEquippedItemId(equippedWateringCanItemId);
    }

    public Integer getEquippedSickleItemId() {
        return equippedSickleItemId;
    }

    public void setEquippedSickleItemId(Integer equippedSickleItemId) {
        this.equippedSickleItemId = normalizeEquippedItemId(equippedSickleItemId);
    }

    public void clearEquippedTools() {
        equippedWateringCanItemId = null;
        equippedSickleItemId = null;
    }

    private Integer normalizeEquippedItemId(Integer itemId) {
        return (itemId == null || itemId <= 0) ? null : itemId;
    }
}
