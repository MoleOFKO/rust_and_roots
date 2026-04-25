package util;

/**
 * SkillModifiers
 *
 * Encapsulates per-skill bonuses derived from skill levels.
 *
 * Levels are expected to be 0..10. Values outside this range are clamped.
 */
public final class SkillModifiers {

    private static final int MAX_LEVEL = 10;

    private SkillModifiers() {
    }

    private static int clampLevel(int level) {
        return Math.max(0, Math.min(MAX_LEVEL, level));
    }

    public static int gatheringCommonBonusPercent(int gatheringLevel) {
        int lvl = clampLevel(gatheringLevel);
        return switch (lvl) {
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 3;
            case 4 -> 4;
            case 5 -> 5;
            case 6 -> 10;
            case 7 -> 20;
            case 8 -> 30;
            case 9 -> 40;
            case 10 -> 50;
            default -> 0;
        };
    }

    public static int gatheringUncommonBonusPercent(int gatheringLevel) {
        int lvl = clampLevel(gatheringLevel);
        return switch (lvl) {
            case 5 -> 1;
            case 6 -> 2;
            case 7 -> 3;
            case 8 -> 4;
            case 9 -> 5;
            case 10 -> 5;
            default -> 0;
        };
    }

    public static int gatheringRareBonusPercent(int gatheringLevel) {
        int lvl = clampLevel(gatheringLevel);
        return lvl >= 10 ? 1 : 0;
    }

    public static int craftingSuccessBonusPercent(int craftingLevel, int combinationSize) {
        return recipeSuccessBonusPercent(craftingLevel, combinationSize);
    }

    public static int cookingSuccessBonusPercent(int cookingLevel, int combinationSize) {
        return recipeSuccessBonusPercent(cookingLevel, combinationSize);
    }

    private static int recipeSuccessBonusPercent(int skillLevel, int combinationSize) {
        int lvl = clampLevel(skillLevel);
        int size = Math.max(1, combinationSize);

        if (size <= 1) {
            return switch (lvl) {
                case 1 -> 1;
                case 2 -> 2;
                case 3 -> 3;
                case 4 -> 4;
                case 5 -> 5;
                case 6 -> 10;
                case 7 -> 20;
                case 8 -> 30;
                case 9 -> 40;
                case 10 -> 50;
                default -> 0;
            };
        }

        if (size == 2) {
            return switch (lvl) {
                case 4 -> 1;
                case 5 -> 2;
                case 6 -> 3;
                case 7 -> 4;
                case 8 -> 5;
                case 9 -> 10;
                case 10 -> 20;
                default -> 0;
            };
        }

        return switch (lvl) {
            case 7 -> 1;
            case 8 -> 2;
            case 9 -> 5;
            case 10 -> 10;
            default -> 0;
        };
    }

    public static int farmingDoubleHarvestChancePercent(int farmingLevel) {
        int lvl = clampLevel(farmingLevel);
        return switch (lvl) {
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 3;
            case 4 -> 4;
            case 5 -> 5;
            case 6 -> 10;
            case 7 -> 20;
            case 8 -> 30;
            case 9 -> 40;
            case 10 -> 50;
            default -> 0;
        };
    }

    public static int combatBonusDamage(int combatLevel) {
        int lvl = clampLevel(combatLevel);
        return switch (lvl) {
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 3;
            case 4 -> 3;
            case 5 -> 4;
            case 6 -> 5;
            case 7 -> 6;
            case 8 -> 7;
            case 9 -> 8;
            case 10 -> 9;
            default -> 0;
        };
    }

    public static int combatReceivedDamageReduction(int combatLevel) {
        int lvl = clampLevel(combatLevel);
        return switch (lvl) {
            case 2 -> 1;
            case 3 -> 1;
            case 4 -> 2;
            case 5 -> 3;
            case 6 -> 4;
            case 7 -> 5;
            case 8 -> 6;
            case 9 -> 7;
            case 10 -> 9;
            default -> 0;
        };
    }
}

