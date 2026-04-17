package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Stores the persistent (saveable) state for natural tree resources on a map.
 *
 * Notes:
 * - Drops are persisted separately in saved_tree_drop.
 * - Use lastUpdatedDayNumber and lastHpGrowthTotalSeconds to advance growth even when the map isn't active.
 */
public class TreeMapState {

    /**
     * Tree "type" is stored as the base key for sprites/drops, e.g.:
     * - {@code blue} => {@code blue_tree1}, {@code blue_tree2}, {@code blue_fruit}
     */
    public static String normalizeTreeTypeKey(String value, String fallback) {
        if (fallback == null || fallback.isBlank()) {
            fallback = "blue";
        }
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    public static class TreeData {
        /** Base item key used for visuals/drops, e.g. "blue" => "blue_tree1". */
        public String typeKey = null;
        /** 1 = empty/drops, 2 = tree1, 3 = tree2. */
        public int currentStage = 2;
        /** Days survived since last respawn (does not advance while in stage 1). */
        public int livedDays = 0;
        public int hp = 10;
        public int maxHp = 10;
        public int vit = 1;

        /** Accumulates rolled values until a drop triggers at 10. */
        public int dropRatePity = 0;
        /** Number of items dropped since last respawn (capped by map logic). */
        public int dropsThisLife = 0;
        /** HP loss tracked towards the next roll (0..9). */
        public int damageSinceLastDropRoll = 0;
        /** The last GameTimeManager.totalSeconds that we applied HP regen/cap growth. */
        public double lastHpGrowthTotalSeconds = -1;

        /** GameTimeManager.totalSeconds when stage 1 should respawn. -1 means not respawning. */
        public double respawnAtTotalSeconds = -1;
        /** The last GameTimeManager.dayNumber that we applied to livedDays. */
        public int lastUpdatedDayNumber = 1;

        /** Persisted via saved_tree_drop. */
        public final List<TreeDrop> drops = new ArrayList<>();

        public boolean isStage1() {
            return currentStage <= 1;
        }
    }

    public static class TreeDrop {
        public int itemId;
        public int quantity;
        public double spawnedAtTotalSeconds;
        public double expiresAtTotalSeconds;

        public TreeDrop(int itemId, int quantity, double spawnedAtTotalSeconds, double expiresAtTotalSeconds) {
            this.itemId = itemId;
            this.quantity = quantity;
            this.spawnedAtTotalSeconds = spawnedAtTotalSeconds;
            this.expiresAtTotalSeconds = expiresAtTotalSeconds;
        }
    }

    private final Map<String, TreeData> trees = new HashMap<>();

    public TreeData getTree(String treeId) {
        if (treeId == null || treeId.isBlank()) {
            throw new IllegalArgumentException("treeId must be non-empty");
        }
        return trees.computeIfAbsent(treeId, k -> new TreeData());
    }

    public Map<String, TreeData> getAllTrees() {
        return trees;
    }

    public void clear() {
        trees.clear();
    }
}

