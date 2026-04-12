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
            int stages = ItemDatabase.resolveMaxGrowthPhasesForSeed(seedItemId);
            return FarmGrowthRules.canWater(
                plantedAt,
                lastWateredAt,
                accumulatedWaterReduction,
                totalGrowthSeconds,
                waterReductionSeconds,
                nowSeconds,
                stages
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

        public int getStage(double nowSeconds, int totalStages) {
            if (isEmpty()) return 0;
            if (totalStages <= 1) return 1;
            int safeStages = Math.max(1, totalStages);
            return FarmGrowthRules.getStage(plantedAt, accumulatedWaterReduction, totalGrowthSeconds, nowSeconds, safeStages);
        }

        public void clearPlant() {
            seedItemId = null;
            seedItemName = null;
            cropItemName = null;
            cropSpriteKey = null;
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
