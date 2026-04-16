/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import game.GameState;
import game.GameTimeManager;
import java.sql.DatabaseMetaData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;
import model.Farm;
import model.FarmGrowthRules;
import model.FarmState;
import model.Inventory;
import model.ItemDatabase;
import model.PlayerState;
import model.TreeMapState;

public class LoadGameService {
    private static final String FARM_SLOT_WATERING_CAN = "__farm_slot_watering_can__";
    private static final String FARM_SLOT_SICKLE = "__farm_slot_sickle__";


    public boolean loadGame(int saveId) {
        if (saveId <= 0) return false;

        GameState gs = GameState.get();
        dbcon db = new dbcon();

        try (Connection con = db.getConnection()) {
            clearRuntimeState(gs);

            if (!loadMainRow(con, gs, saveId)) {
                return false;
            }

            loadPlayerStats(con, gs, saveId);
            loadPlayerSkills(con, gs, saveId);
            loadChestItems(con, gs, saveId);
            loadPlayerInventory(con, gs, saveId);
            loadHotbar(con, gs, saveId);
            loadFarmPlots(con, gs, saveId);
            loadTime(con, saveId);
            loadTreeResources(con, gs, saveId);
            loadTreeDrops(con, gs, saveId);
            loadUnlockedRecipes(con, gs, saveId);
            gs.setPreferredStatusPanelGroup(GameState.StatusPanelGroup.STATUS);

            gs.setCurrentSaveId(saveId);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean loadMainRow(Connection con, GameState gs, int saveId) throws Exception {
        String sql = """
            SELECT saved_id, character_id, world_name, current_map_id, money,
                   saved_location_x, saved_location_y, exp,
                   selected_hotbar_index, hotbar_visible
            FROM save_game
            WHERE saved_id = ?
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;

                PlayerState psState = gs.getPlayerState();

                gs.setCurrentSaveId(rs.getInt("saved_id"));
                gs.setCurrentCharacterId(rs.getInt("character_id"));
                gs.setCurrentMapKey(resolveMapKey(con, rs.getInt("current_map_id")));
                gs.setPlayerX(rs.getDouble("saved_location_x"));
                gs.setPlayerY(rs.getDouble("saved_location_y"));
                gs.setCurrentWorldName(rs.getString("world_name"));

                psState.setSelectedHotbarIndex(rs.getInt("selected_hotbar_index"));
                psState.setHotbarVisible(rs.getBoolean("hotbar_visible"));
                psState.setExp(rs.getInt("exp"));

                replaceCoins(psState.getInventory(), rs.getInt("money"));
                return true;
            }
        }
    }

    private void loadPlayerStats(Connection con, GameState gs, int saveId) throws Exception {
        String sql = """
            SELECT ps.str_stat, ps.agi_stat, ps.dex_stat, ps.vit_stat, ps.hp, ps.stamina,
                   sg.current_hp, sg.current_stamina
            FROM save_game sg
            JOIN player_stats ps ON sg.player_stats_id = ps.player_stats_id
            WHERE sg.saved_id = ?
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return;

                PlayerState psState = gs.getPlayerState();
                psState.setStr(rs.getInt("str_stat"));
                psState.setAgi(rs.getInt("agi_stat"));
                psState.setDex(rs.getInt("dex_stat"));
                psState.setVit(rs.getInt("vit_stat"));

                psState.setMaxHp(rs.getInt("hp"));
                psState.setHp(rs.getInt("current_hp"));

                psState.setMaxStamina(rs.getInt("stamina"));
                psState.setStamina(rs.getInt("current_stamina"));
            }
        }
    }

    private void loadPlayerSkills(Connection con, GameState gs, int saveId) throws Exception {
        String sql = """
            SELECT skill_type_id, skill_level
            FROM player_skills
            WHERE saved_id = ?
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);

            try (ResultSet rs = ps.executeQuery()) {
                GameState.PlayerSkills skills = gs.getPlayerSkills();
                skills.clear();

                while (rs.next()) {
                    int skillTypeId = rs.getInt("skill_type_id");
                    int skillLevel = rs.getInt("skill_level");

                    switch (skillTypeId) {
                        case 1 -> skills.setFarmingExp(skillLevel);
                        case 2 -> skills.setGatheringExp(skillLevel);
                        case 3 -> skills.setCraftingExp(skillLevel);
                        case 4 -> skills.setFishingExp(skillLevel);
                        case 5 -> skills.setBattlingExp(skillLevel);
                        case 6 -> skills.setCookingExp(skillLevel);
                    }
                }
            }
        }
    }
    
    private void loadPlayerInventory(Connection con, GameState gs, int saveId) throws Exception {
        Inventory inv = gs.getPlayerState().getInventory();
        clearInventoryItems(inv);
        Inventory chest = gs.getChestInventory();

        String sql = "SELECT item_id, qty FROM saved_item WHERE saved_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int itemId = rs.getInt("item_id");
                    int qty = rs.getInt("qty");
                    if (itemId > 0 && qty > 0) {
                        if (!inv.addItem(itemId, qty)) {
                            if (!chest.addItem(itemId, qty)) {
                                System.out.println("Load warning: inventory overflow dropped item_id=" + itemId + " qty=" + qty);
                            }
                        }
                    }
                }
            }
        }
    }

    private void loadHotbar(Connection con, GameState gs, int saveId) throws Exception {
        PlayerState psState = gs.getPlayerState();
        Inventory inv = psState.getInventory();
        Inventory chest = gs.getChestInventory();

        psState.clearAllHotbarItems();

        int hotbarSize = psState.getHotbarSize();
        int[] slotItemIds = new int[hotbarSize];
        int[] slotQuantities = new int[hotbarSize];
        boolean[] hasSlotValue = new boolean[hotbarSize];
        Map<Integer, Integer> overflowByItemId = new LinkedHashMap<>();

        String sql = "SELECT slot_index, item_id, qty FROM saved_hotbar WHERE saved_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int slotIndex = rs.getInt("slot_index");
                    int itemId = rs.getInt("item_id");
                    boolean itemWasNull = rs.wasNull();
                    int qty = rs.getInt("qty");

                    if (!itemWasNull && itemId > 0 && qty > 0) {
                        if (slotIndex < 0 || slotIndex >= hotbarSize) {
                            overflowByItemId.merge(itemId, qty, Integer::sum);
                            continue;
                        }

                        slotItemIds[slotIndex] = itemId;
                        slotQuantities[slotIndex] = qty;
                        hasSlotValue[slotIndex] = true;
                    }
                }
            }
        }

        for (int i = 0; i < hotbarSize; i++) {
            if (!hasSlotValue[i]) continue;

            int itemId = slotItemIds[i];
            int qty = slotQuantities[i];
            if (itemId <= 0 || qty <= 0) continue;

            int maxStack = Math.max(1, ItemDatabase.getMaxStackSize(itemId));
            int safeQty = Math.min(qty, maxStack);
            psState.setHotbarSlot(i, itemId, safeQty);

            int overflow = qty - safeQty;
            if (overflow > 0) {
                overflowByItemId.merge(itemId, overflow, Integer::sum);
            }
        }

        for (Map.Entry<Integer, Integer> entry : overflowByItemId.entrySet()) {
            int itemId = entry.getKey();
            int qty = entry.getValue();
            if (itemId <= 0 || qty <= 0) continue;

            int remaining = distributeHotbarOverflow(psState, itemId, qty);
            if (remaining <= 0) continue;

            if (!inv.addItem(itemId, remaining)) {
                if (!chest.addItem(itemId, remaining)) {
                    System.out.println("Load warning: hotbar overflow dropped item_id=" + itemId + " qty=" + remaining);
                }
            }
        }
    }

    private int distributeHotbarOverflow(PlayerState psState, int itemId, int qty) {
        if (psState == null || itemId <= 0 || qty <= 0) return qty;

        int remaining = qty;
        int maxStack = Math.max(1, ItemDatabase.getMaxStackSize(itemId));
        int hotbarSize = psState.getHotbarSize();

        for (int i = 0; i < hotbarSize && remaining > 0; i++) {
            if (psState.getHotbarItemId(i) != itemId) continue;
            int current = psState.getHotbarItemCount(i);
            if (current <= 0 || current >= maxStack) continue;

            int add = Math.min(maxStack - current, remaining);
            psState.setHotbarSlot(i, itemId, current + add);
            remaining -= add;
        }

        for (int i = 0; i < hotbarSize && remaining > 0; i++) {
            if (!psState.isHotbarSlotEmpty(i)) continue;

            int put = Math.min(maxStack, remaining);
            psState.setHotbarSlot(i, itemId, put);
            remaining -= put;
        }

        return remaining;
    }

    private void loadChestItems(Connection con, GameState gs, int saveId) throws Exception {
        Inventory chest = gs.getChestInventory();
        clearInventoryItems(chest);

        String sql = "SELECT item_id, qty FROM saved_chest_item WHERE saved_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int itemId = rs.getInt("item_id");
                    int qty = rs.getInt("qty");
                    if (itemId > 0 && qty > 0) {
                        if (!chest.addItem(itemId, qty)) {
                            System.out.println("Load warning: chest overflow dropped item_id=" + itemId + " qty=" + qty);
                        }
                    }
                }
            }
        }
    }

    private void loadFarmPlots(Connection con, GameState gs, int saveId) throws Exception {
        String sql = """
            SELECT map_key, plot_id, seed_item_id, planted_at, last_watered_at,
                   accumulated_water_reduction, total_growth_seconds,
                   water_cooldown_seconds, water_reduction_seconds
            FROM saved_farm_plot
            WHERE saved_id = ?
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String mapKey = rs.getString("map_key");
                    String plotId = rs.getString("plot_id");

                    FarmState farm = gs.getFarmState(mapKey);
                    if (farm == null) continue;

                    Integer seedItemId = (Integer) rs.getObject("seed_item_id");

                    if (FARM_SLOT_WATERING_CAN.equals(plotId)) {
                        farm.setEquippedWateringCanItemId(seedItemId);
                        continue;
                    }

                    if (FARM_SLOT_SICKLE.equals(plotId)) {
                        farm.setEquippedSickleItemId(seedItemId);
                        continue;
                    }

                    FarmState.PlotData plot = farm.getPlot(plotId);
                    if (plot == null) continue;

                    plot.seedItemId = seedItemId;

                    plot.plantedAt = rs.getDouble("planted_at");
                    plot.lastWateredAt = rs.getDouble("last_watered_at");
                    plot.accumulatedWaterReduction = rs.getDouble("accumulated_water_reduction");
                    plot.totalGrowthSeconds = rs.getDouble("total_growth_seconds");
                    plot.waterCooldownSeconds = rs.getDouble("water_cooldown_seconds");
                    plot.waterReductionSeconds = rs.getDouble("water_reduction_seconds");

                    if (seedItemId != null && seedItemId > 0) {
                        plot.seedItemName = ItemDatabase.getItemName(seedItemId);
                        plot.cropItemName = ItemDatabase.resolveCropNameForSeed(seedItemId);
                        plot.cropSpriteKey = ItemDatabase.buildCropSpriteKeyForSeed(seedItemId);
                        FarmGrowthRules.applyProfile(plot, seedItemId);
                    } else {
                        plot.seedItemName = null;
                        plot.cropItemName = null;
                        plot.cropSpriteKey = null;
                    }
                }
            }
        }
    }

    private void loadTime(Connection con, int saveId) throws Exception {
        String sql = "SELECT day_number, total_seconds FROM saved_time WHERE saved_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return;

                GameTimeManager time = GameTimeManager.get();
                time.setDay(rs.getInt("day_number"));
                time.totalSecondsProperty().set(rs.getDouble("total_seconds"));
            }
        }
    }

    private String resolveMapKey(Connection con, int mapId) throws Exception {
        String sql = "SELECT map_name FROM game_map WHERE map_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, mapId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("map_name");
                }
            }
        }

        return "home_map";
    }

    private void clearRuntimeState(GameState gs) {
        PlayerState psState = gs.getPlayerState();

        clearInventoryItems(psState.getInventory());
        replaceCoins(psState.getInventory(), 0);

        for (int i = 0; i < psState.getHotbarSize(); i++) {
            psState.clearHotbarItem(i);
        }

        gs.getPlayerSkills().clear();
        gs.clearUnlockedRecipes();
        clearInventoryItems(gs.getChestInventory());
        clearFarmState(gs.getFarmState("home_map"));
        clearFarmState(gs.getFarmState("Home_interior"));
        gs.clearTreeStates();
    }

    private void loadTreeResources(Connection con, GameState gs, int saveId) throws Exception {
        if (con == null || gs == null || saveId <= 0) return;
        if (!hasTable(con, "saved_tree_resource")) {
            return;
        }

        String sql = """
            SELECT map_key,
                   tree_id,
                   tree_type,
                   current_stage,
                   lived_days,
                   hp,
                   max_hp,
                   vit,
                   drop_rate_pity,
                   drops_this_life,
                   damage_since_last_drop_roll,
                   last_hp_growth_total_seconds,
                   respawn_at_total_seconds,
                   last_updated_day_number
            FROM saved_tree_resource
            WHERE saved_id = ?
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String mapKey = rs.getString("map_key");
                    String treeId = rs.getString("tree_id");
                    if (mapKey == null || mapKey.isBlank() || treeId == null || treeId.isBlank()) {
                        continue;
                    }

                    TreeMapState mapState = gs.getTreeState(mapKey);
                    TreeMapState.TreeData tree = mapState.getTree(treeId);

                    tree.typeKey = TreeMapState.normalizeTreeTypeKey(rs.getString("tree_type"), "blue");
                    tree.currentStage = rs.getInt("current_stage");
                    tree.livedDays = rs.getInt("lived_days");
                    tree.hp = rs.getInt("hp");
                    tree.maxHp = rs.getInt("max_hp");
                    tree.vit = rs.getInt("vit");
                    tree.dropRatePity = rs.getInt("drop_rate_pity");
                    tree.dropsThisLife = rs.getInt("drops_this_life");
                    tree.damageSinceLastDropRoll = rs.getInt("damage_since_last_drop_roll");
                    tree.lastHpGrowthTotalSeconds = rs.getDouble("last_hp_growth_total_seconds");
                    tree.respawnAtTotalSeconds = rs.getDouble("respawn_at_total_seconds");
                    tree.lastUpdatedDayNumber = rs.getInt("last_updated_day_number");
                    tree.drops.clear();
                }
            }
        }
    }

    private void loadTreeDrops(Connection con, GameState gs, int saveId) throws Exception {
        if (con == null || gs == null || saveId <= 0) return;
        if (!hasTable(con, "saved_tree_drop")) {
            return;
        }

        String sql = """
            SELECT map_key,
                   tree_id,
                   drop_slot,
                   item_id,
                   quantity,
                   spawned_at_total_seconds,
                   expires_at_total_seconds
            FROM saved_tree_drop
            WHERE saved_id = ?
            ORDER BY map_key, tree_id, drop_slot
            """;

        double nowSeconds = GameTimeManager.get().totalSecondsProperty().get();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String mapKey = rs.getString("map_key");
                    String treeId = rs.getString("tree_id");
                    if (mapKey == null || mapKey.isBlank() || treeId == null || treeId.isBlank()) {
                        continue;
                    }

                    int itemId = rs.getInt("item_id");
                    int quantity = rs.getInt("quantity");
                    double spawnedAt = rs.getDouble("spawned_at_total_seconds");
                    double expiresAt = rs.getDouble("expires_at_total_seconds");
                    if (itemId <= 0 || quantity <= 0) {
                        continue;
                    }
                    if (expiresAt > 0 && nowSeconds >= expiresAt) {
                        continue;
                    }

                    TreeMapState.TreeData tree = gs.getTreeState(mapKey).getTree(treeId);
                    tree.drops.add(new TreeMapState.TreeDrop(itemId, quantity, spawnedAt, expiresAt));
                }
            }
        }
    }

    private void loadUnlockedRecipes(Connection con, GameState gs, int saveId) throws Exception {
        gs.clearUnlockedRecipes();
        if (!hasTable(con, "saved_unlocked_recipe")) {
            return;
        }

        String sql = "SELECT recipe_key FROM saved_unlocked_recipe WHERE saved_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    gs.unlockRecipe(rs.getString("recipe_key"));
                }
            }
        }
    }

    private boolean hasTable(Connection con, String tableName) throws Exception {
        if (con == null || tableName == null || tableName.isBlank()) {
            return false;
        }

        DatabaseMetaData metaData = con.getMetaData();
        try (ResultSet rs = metaData.getTables(con.getCatalog(), null, tableName, new String[]{"TABLE"})) {
            if (rs.next()) {
                return true;
            }
        }
        try (ResultSet rs = metaData.getTables(con.getCatalog(), null, tableName.toUpperCase(), new String[]{"TABLE"})) {
            return rs.next();
        }
    }

    private void clearInventoryItems(Inventory inv) {
        var ids = new java.util.ArrayList<>(inv.itemsProperty().keySet());
        for (Integer itemId : ids) {
            int qty = inv.getCount(itemId);
            if (qty > 0) {
                inv.removeItem(itemId, qty);
            }
        }
    }

    private void replaceCoins(Inventory inv, int targetCoins) {
        int current = inv.getCoins();
        if (current > 0) {
            inv.spendCoins(current);
        }
        if (targetCoins > 0) {
            inv.addCoins(targetCoins);
        }
    }

    private void clearFarmState(FarmState farmState) {
        farmState.getAllPlots().clear();
        farmState.clearEquippedTools();
    }
}
