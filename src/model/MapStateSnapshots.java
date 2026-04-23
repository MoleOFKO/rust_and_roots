package model;

import java.util.Map;

/**
 * Utility for making deep copies of map state objects so they can be safely persisted
 * from background threads.
 */
public final class MapStateSnapshots {

    private MapStateSnapshots() {
    }

    public static FarmState copyFarmState(FarmState src) {
        if (src == null) return null;

        FarmState dst = new FarmState();
        dst.setEquippedWateringCanItemId(src.getEquippedWateringCanItemId());
        dst.setEquippedSickleItemId(src.getEquippedSickleItemId());

        for (Map.Entry<String, FarmState.PlotData> entry : src.getAllPlots().entrySet()) {
            if (entry == null) continue;
            String plotId = entry.getKey();
            FarmState.PlotData from = entry.getValue();
            if (plotId == null || plotId.isBlank() || from == null) continue;

            FarmState.PlotData to = dst.getPlot(plotId);
            to.seedItemId = from.seedItemId;
            to.seedItemName = from.seedItemName;
            to.cropItemName = from.cropItemName;
            to.cropSpriteKey = from.cropSpriteKey;
            to.totalGrowthSeconds = from.totalGrowthSeconds;
            to.plantedAt = from.plantedAt;
            to.waterCooldownSeconds = from.waterCooldownSeconds;
            to.lastWateredAt = from.lastWateredAt;
            to.waterReductionSeconds = from.waterReductionSeconds;
            to.accumulatedWaterReduction = from.accumulatedWaterReduction;
        }

        return dst;
    }

    public static TreeMapState copyTreeMapState(TreeMapState src) {
        if (src == null) return null;

        TreeMapState dst = new TreeMapState();
        for (Map.Entry<String, TreeMapState.TreeData> entry : src.getAllTrees().entrySet()) {
            if (entry == null) continue;
            String treeId = entry.getKey();
            TreeMapState.TreeData from = entry.getValue();
            if (treeId == null || treeId.isBlank() || from == null) continue;

            TreeMapState.TreeData to = dst.getTree(treeId);
            to.typeKey = from.typeKey;
            to.currentStage = from.currentStage;
            to.livedDays = from.livedDays;
            to.hp = from.hp;
            to.maxHp = from.maxHp;
            to.vit = from.vit;
            to.dropRatePity = from.dropRatePity;
            to.dropsThisLife = from.dropsThisLife;
            to.damageSinceLastDropRoll = from.damageSinceLastDropRoll;
            to.lastHpGrowthTotalSeconds = from.lastHpGrowthTotalSeconds;
            to.respawnAtTotalSeconds = from.respawnAtTotalSeconds;
            to.lastUpdatedDayNumber = from.lastUpdatedDayNumber;

            to.drops.clear();
            for (TreeMapState.TreeDrop drop : from.drops) {
                if (drop == null) continue;
                to.drops.add(new TreeMapState.TreeDrop(
                    drop.itemId,
                    drop.quantity,
                    drop.spawnedAtTotalSeconds,
                    drop.expiresAtTotalSeconds
                ));
            }
        }

        return dst;
    }

    public static MobMapState copyMobMapState(MobMapState src) {
        if (src == null) return null;

        MobMapState dst = new MobMapState();
        for (Map.Entry<String, MobMapState.MobData> entry : src.getAllMobs().entrySet()) {
            if (entry == null) continue;
            String mobId = entry.getKey();
            MobMapState.MobData from = entry.getValue();
            if (mobId == null || mobId.isBlank() || from == null) continue;

            MobMapState.MobData to = dst.getMob(mobId);
            to.mobType = from.mobType;
            to.livedDays = from.livedDays;
            to.hp = from.hp;
            to.maxHp = from.maxHp;
            to.sp = from.sp;
            to.maxSp = from.maxSp;
            to.str = from.str;
            to.agi = from.agi;
            to.vit = from.vit;
            to.dex = from.dex;
            to.dead = from.dead;
            to.respawnAtTotalSeconds = from.respawnAtTotalSeconds;
            to.runAttackCooldownUntilTotalSeconds = from.runAttackCooldownUntilTotalSeconds;
            to.layoutX = from.layoutX;
            to.layoutY = from.layoutY;
            to.lastRegenTotalSeconds = from.lastRegenTotalSeconds;
            to.lastUpdatedDayNumber = from.lastUpdatedDayNumber;

            to.drops.clear();
            for (MobMapState.MobDrop drop : from.drops) {
                if (drop == null) continue;
                to.drops.add(new MobMapState.MobDrop(
                    drop.itemId,
                    drop.quantity,
                    drop.spawnedAtTotalSeconds,
                    drop.expiresAtTotalSeconds
                ));
            }
        }

        return dst;
    }
}

