package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores the persistent (saveable) state for mobs on a map.
 *
 * Notes:
 * - Drops are persisted separately in saved_mob_drop.
 * - lastUpdatedDayNumber and lastRegenTotalSeconds allow advancing growth/regen when the map isn't active.
 */
public class MobMapState {

    public static class MobData {
        /** Logical mob type key, e.g. "slime" or "orc". */
        public String mobType = null;
        /** Used for respawn/scale rules (incremented by day delta). */
        public int livedDays = 0;

        public int hp = 0;
        public int maxHp = 0;
        public int sp = 0;
        public int maxSp = 0;

        public int str = 0;
        public int agi = 0;
        public int vit = 0;
        public int dex = 0;

        public boolean dead = false;

        /** GameTimeManager.totalSeconds when this mob should respawn. -1 means not respawning. */
        public double respawnAtTotalSeconds = -1.0;

        /** Group layout position (in parent's coordinate space). */
        public double layoutX = 0.0;
        public double layoutY = 0.0;

        /** The last GameTimeManager.totalSeconds that we applied regen. */
        public double lastRegenTotalSeconds = -1.0;
        /** The last GameTimeManager.dayNumber that we applied to livedDays / daily stat growth. */
        public int lastUpdatedDayNumber = 1;

        /** Persisted via saved_mob_drop. */
        public final List<MobDrop> drops = new ArrayList<>();
    }

    public static class MobDrop {
        public int itemId;
        public int quantity;
        public double spawnedAtTotalSeconds;
        public double expiresAtTotalSeconds;

        public MobDrop(int itemId, int quantity, double spawnedAtTotalSeconds, double expiresAtTotalSeconds) {
            this.itemId = itemId;
            this.quantity = quantity;
            this.spawnedAtTotalSeconds = spawnedAtTotalSeconds;
            this.expiresAtTotalSeconds = expiresAtTotalSeconds;
        }
    }

    private final Map<String, MobData> mobs = new HashMap<>();

    public MobData getMob(String mobId) {
        if (mobId == null || mobId.isBlank()) {
            throw new IllegalArgumentException("mobId must be non-empty");
        }
        return mobs.computeIfAbsent(mobId, k -> new MobData());
    }

    public Map<String, MobData> getAllMobs() {
        return mobs;
    }

    public void clear() {
        mobs.clear();
    }
}

