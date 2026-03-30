package model;

import java.util.HashMap;
import java.util.Map;

public class FarmState {

    public static class PlotData {
        /** Legacy field kept for backwards compatibility with older code/saves. */
        public Farm.SeedType seed = null;

        /** DB-driven farm fields. */
        public Integer seedItemId = null;
        public String seedItemName = null;
        public String cropItemName = null;
        public String cropSpriteKey = null;

        public double totalGrowthSeconds = 15 * 60;
        public double plantedAt = -1;
        public double waterCooldownSeconds = 5 * 60;
        public double lastWateredAt = -1;
        public double waterReductionSeconds = 60;
        public double accumulatedWaterReduction = 0;

        public boolean isEmpty() {
            return seed == null && (seedItemId == null || seedItemId <= 0);
        }

        public boolean isReady(double nowSeconds) {
            if (isEmpty()) return false;
            double effectiveElapsed = (nowSeconds - plantedAt) + accumulatedWaterReduction;
            return effectiveElapsed >= totalGrowthSeconds;
        }

        public boolean canWater(double nowSeconds) {
            if (isEmpty()) return false;
            if (isReady(nowSeconds)) return false;
            if (lastWateredAt < 0) return true;
            return (nowSeconds - lastWateredAt) >= waterCooldownSeconds;
        }

        public void water(double nowSeconds) {
            if (!canWater(nowSeconds)) return;
            lastWateredAt = nowSeconds;
            accumulatedWaterReduction += waterReductionSeconds;
            if (accumulatedWaterReduction > totalGrowthSeconds) {
                accumulatedWaterReduction = totalGrowthSeconds;
            }
        }

        public int getStage(double nowSeconds, int totalStages) {
            if (isEmpty()) return 0;
            if (totalStages <= 1) return 1;

            double effectiveElapsed = Math.max(0, (nowSeconds - plantedAt) + accumulatedWaterReduction);
            double p = Math.min(1.0, effectiveElapsed / totalGrowthSeconds);

            int stage = 1 + (int)Math.floor(p * (totalStages - 1));
            if (stage < 1) stage = 1;
            if (stage > totalStages) stage = totalStages;
            return stage;
        }

        public void clearPlant() {
            seed = null;
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
}
