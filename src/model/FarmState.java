package model;

import java.util.HashMap;
import java.util.Map;

public class FarmState {

    public static class PlotData {
        public Farm.SeedType seed = null;

        // total growth time (seconds)
        public double totalGrowthSeconds = 15 * 60;

        // when planted (game seconds from GameTimeManager.totalSecondsProperty)
        public double plantedAt = -1;

        // watering cooldown
        public double waterCooldownSeconds = 5 * 60;
        public double lastWateredAt = -1;

        // each watering reduces remaining by 60 seconds
        public double waterReductionSeconds = 60;

        // accumulated reduction from watering (seconds)
        public double accumulatedWaterReduction = 0;

        public boolean isEmpty() { return seed == null; }

        public boolean isReady(double nowSeconds) {
            if (seed == null) return false;
            double effectiveElapsed = (nowSeconds - plantedAt) + accumulatedWaterReduction;
            return effectiveElapsed >= totalGrowthSeconds;
        }

        public boolean canWater(double nowSeconds) {
            if (seed == null) return false;
            if (isReady(nowSeconds)) return false;
            if (lastWateredAt < 0) return true;
            return (nowSeconds - lastWateredAt) >= waterCooldownSeconds;
        }

        public void water(double nowSeconds) {
            if (!canWater(nowSeconds)) return;
            lastWateredAt = nowSeconds;
            accumulatedWaterReduction += waterReductionSeconds;

            // don’t let reduction exceed total time
            if (accumulatedWaterReduction > totalGrowthSeconds) {
                accumulatedWaterReduction = totalGrowthSeconds;
            }
        }

        public int getStage(double nowSeconds, int totalStages) {
            if (seed == null) return 0;
            if (totalStages <= 1) return 1;

            double effectiveElapsed = Math.max(0, (nowSeconds - plantedAt) + accumulatedWaterReduction);
            double p = Math.min(1.0, effectiveElapsed / totalGrowthSeconds);

            // stage in [1..totalStages]
            int stage = 1 + (int)Math.floor(p * (totalStages - 1));
            if (stage < 1) stage = 1;
            if (stage > totalStages) stage = totalStages;
            return stage;
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