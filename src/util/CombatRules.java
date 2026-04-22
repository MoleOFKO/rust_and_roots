package util;

/**
 * Centralizes combat math so player/mob/resource interactions use the same rules.
 */
public final class CombatRules {

    public static final double BASE_ATTACK_DAMAGE = 1.0;
    public static final double BASE_DAMAGE_REDUCTION = 0.5;

    /**
     * How much 1 VIT contributes to the (decimal) damage reduction stat.
     *
     * <p>The reduction stat is resolved via {@link StatRoller#rollToInt(double)} each hit.
     */
    public static final double DAMAGE_REDUCTION_PER_VIT = 0.1;

    private CombatRules() {
    }

    public static double attackDamageStat(int str, int flatBonus) {
        int safeStr = Math.max(0, str);
        int safeBonus = Math.max(0, flatBonus);
        return BASE_ATTACK_DAMAGE + safeStr + safeBonus;
    }

    public static int rollAttackDamage(int str, int flatBonus) {
        return StatRoller.rollToInt(attackDamageStat(str, flatBonus));
    }

    public static double damageReductionStat(int vit) {
        int safeVit = Math.max(0, vit);
        return BASE_DAMAGE_REDUCTION + (safeVit * DAMAGE_REDUCTION_PER_VIT);
    }

    public static int rollDamageReduction(int vit) {
        return StatRoller.rollToInt(damageReductionStat(vit));
    }

    public static int applyDamageReduction(int rolledAttackDamage, int defenderVit) {
        int raw = Math.max(0, rolledAttackDamage);
        if (raw == 0) {
            return 0;
        }

        int reducedBy = rollDamageReduction(defenderVit);
        return Math.max(0, raw - Math.max(0, reducedBy));
    }
}

