package controller;

import java.util.concurrent.ThreadLocalRandom;

final class ProcessingRules {
    private static final int DEFAULT_DEX = 5;

    private ProcessingRules() {}

    static int computeAdjustedSuccessRate(int baseSuccessRate, int dexScaling, int dex) {
        return Math.max(0, baseSuccessRate + ((dex - DEFAULT_DEX) * dexScaling));
    }

    static int computeAdjustedWaitSeconds(int baseWaitSeconds, int dexScaling, int dex) {
        int adjusted = baseWaitSeconds - ((dex - DEFAULT_DEX) * dexScaling);
        return Math.max(1, adjusted);
    }

    static int computeAdjustedStaminaCost(int baseStaminaCost, int dexScaling, int dex) {
        int adjusted = baseStaminaCost - ((dex - DEFAULT_DEX) * dexScaling);
        return Math.max(0, adjusted);
    }

    static boolean rollSuccess(int successRatePercent) {
        if (successRatePercent >= 100) return true;
        if (successRatePercent <= 0) return false;
        return ThreadLocalRandom.current().nextDouble(100.0) < successRatePercent;
    }

    static int computeOutputAmount(int baseAmount, int successRatePercent) {
        int safeBase = Math.max(0, baseAmount);
        if (safeBase == 0) return 0;
        return successRatePercent >= 200 ? safeBase * 2 : safeBase;
    }
}
