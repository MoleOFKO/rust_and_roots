package model;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores the persistent (saveable) state for natural tree resources on a map.
 *
 * Notes:
 * - pendingDrops are runtime-only and should NOT be persisted to DB.
 * - Use lastUpdatedDayNumber to advance livedDays even when the map isn't active.
 */
public class TreeMapState {

    public enum TreeType {
        BLUE,
        RED,
        YELLOW;

        public static TreeType fromDb(String value, TreeType fallback) {
            if (value == null || value.isBlank()) return fallback == null ? BLUE : fallback;
            String normalized = value.trim().toUpperCase();
            for (TreeType t : values()) {
                if (t.name().equals(normalized)) return t;
            }
            return fallback == null ? BLUE : fallback;
        }
    }

    public static class TreeData {
        public TreeType type = null;
        /** 1 = empty/drops, 2 = tree1, 3 = tree2. */
        public int currentStage = 2;
        /** Days survived since last respawn (does not advance while in stage 1). */
        public int livedDays = 0;
        public int hp = 30;
        public int vit = 0;
        /** GameTimeManager.totalSeconds when stage 1 should respawn. -1 means not respawning. */
        public double respawnAtTotalSeconds = -1;
        /** The last GameTimeManager.dayNumber that we applied to livedDays. */
        public int lastUpdatedDayNumber = 1;

        /** Runtime-only, not saved to DB. */
        public final Deque<Integer> pendingDrops = new ArrayDeque<>();

        public boolean isStage1() {
            return currentStage <= 1;
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

