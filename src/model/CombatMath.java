package model;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Shared combat/stat math helpers.
 *
 * Notes on decimals:
 * - For specific stats that may contain decimals, we apply stochastic rounding per-use:
 *   value = floor(value) + (random < fractionalPart ? 1 : 0).
 * - This matches the "yes/no roll" rule described in the project requirements.
 */
public final class CombatMath {

    private CombatMath() {}

    public static final double BASE_ATTACK_DAMAGE = 1.0;
    public static final double BASE_DAMAGE_REDUCTION = 0.5;

    /**
     * STR increases attack damage above a baseline of 1.
     * With STR=1, damage stays at the base.
     */
    public static double computeAttackDamage(int str, int flatBonus) {
        int safeStr = Math.max(0, str);
        int safeBonus = Math.max(0, flatBonus);
        int strBonus = Math.max(0, safeStr - 1);
        return BASE_ATTACK_DAMAGE + strBonus + safeBonus;
    }

    /**
     * VIT increases damage reduction above a baseline of 1.
     * With VIT=1, reduction stays at the base (0.5).
     *
     * Each VIT above 1 adds 0.5 reduction.
     */
    public static double computeDamageReduction(int vit) {
        int safeVit = Math.max(0, vit);
        int vitBonus = Math.max(0, safeVit - 1);
        return BASE_DAMAGE_REDUCTION + (vitBonus * 0.5);
    }

    public static int rollStochastic(double value) {
        double safe = Math.max(0.0, value);
        int whole = (int) Math.floor(safe);
        double fractional = safe - whole;
        if (fractional <= 1.0e-12) {
            return whole;
        }
        if (fractional >= 1.0 - 1.0e-12) {
            return whole + 1;
        }

        double roll = ThreadLocalRandom.current().nextDouble(1.0);
        return whole + (roll < fractional ? 1 : 0);
    }

    public static int computeNetDamage(double attackDamage, double damageReduction) {
        int attackRoll = rollStochastic(attackDamage);
        int reductionRoll = rollStochastic(damageReduction);
        return Math.max(0, attackRoll - reductionRoll);
    }
}

