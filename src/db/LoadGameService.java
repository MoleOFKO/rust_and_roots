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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.Farm;
import model.FarmGrowthRules;
import model.FarmState;
import model.Inventory;
import model.ItemDatabase;
import model.MobMapState;
import model.PlayerState;
import model.RecipeCatalog;
import model.TreeMapState;
import model.item;

public class LoadGameService {
    private static final String FARM_SLOT_WATERING_CAN = "__farm_slot_watering_can__";
    private static final String FARM_SLOT_SICKLE = "__farm_slot_sickle__";

    /**
     * Loads only the requested map-specific state (farm / trees / mobs) for the currently active save.
     * This is used when non-current maps are evicted from memory to reduce runtime usage.
     */
    public void loadMapState(GameState gs, String mapKey) {
        if (gs == null) return;
        if (!gs.hasActiveSave()) return;
        if (mapKey == null || mapKey.isBlank()) return;

        int saveId = gs.getCurrentSaveId();

        boolean needsFarm = !gs.getAllFarmStates().containsKey(mapKey);
        boolean needsTrees = !gs.getAllTreeStates().containsKey(mapKey);
        boolean needsMobs = !gs.getAllMobStates().containsKey(mapKey);
        if (!needsFarm && !needsTrees && !needsMobs) {
            return;
        }

        dbcon db = new dbcon();
        try (Connection con = db.getConnection()) {
            ItemDatabase.loadItems();

            if (needsFarm) {
                loadFarmPlots(con, gs, saveId, mapKey);
            }
            if (needsTrees) {
                loadTreeResources(con, gs, saveId, mapKey);
                loadTreeDrops(con, gs, saveId, mapKey);
            }
            if (needsMobs) {
                loadMobs(con, gs, saveId, mapKey);
                loadMobDrops(con, gs, saveId, mapKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean loadGame(int saveId) {
        if (saveId <= 0) return false;

        GameState gs = GameState.get();
        dbcon db = new dbcon();

        try (Connection con = db.getConnection()) {
            clearRuntimeState(gs);

            if (!loadMainRow(con, gs, saveId)) {
                return false;
            }

            ItemDatabase.loadItems();

            String currentMapKey = gs.getCurrentMapKey();

            loadPlayerStats(con, gs, saveId);
            loadPlayerSkills(con, gs, saveId);
            loadChestItems(con, gs, saveId);
            loadPlayerInventory(con, gs, saveId);
            loadHotbar(con, gs, saveId);
            loadFarmPlots(con, gs, saveId, currentMapKey);
            loadTime(con, saveId);
            loadShopState(con, gs, saveId);
            loadSmeltState(con, gs, saveId);
            loadSmeltOreHistory(con, gs, saveId);
            loadTreeResources(con, gs, saveId, currentMapKey);
            loadTreeDrops(con, gs, saveId, currentMapKey);
            loadMobs(con, gs, saveId, currentMapKey);
            loadMobDrops(con, gs, saveId, currentMapKey);
            loadUnlockedRecipes(con, gs, saveId);
            gs.ensureAutomaticRecipeUnlocks();
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
            SELECT sg.saved_id,
                   sg.character_id,
                   c.player_name,
                   sg.world_name,
                   sg.current_map_id,
                   sg.money,
                   sg.saved_location_x,
                   sg.saved_location_y,
                   sg.exp,
                   sg.selected_hotbar_index,
                   sg.hotbar_visible
            FROM save_game sg
            LEFT JOIN characters c ON sg.character_id = c.character_id
            WHERE sg.saved_id = ?
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;

                PlayerState psState = gs.getPlayerState();
                String playerName = rs.getString("player_name");

                gs.setCurrentSaveId(rs.getInt("saved_id"));
                gs.setCurrentCharacterId(rs.getInt("character_id"));
                gs.setCurrentMapKey(resolveMapKey(con, rs.getInt("current_map_id")));
                gs.setPlayerX(rs.getDouble("saved_location_x"));
                gs.setPlayerY(rs.getDouble("saved_location_y"));
                gs.setCurrentWorldName(rs.getString("world_name"));
                gs.setCurrentPlayerName(playerName);

                psState.setPlayerName(playerName);
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
            SELECT skill_type_id, skill_level, skill_carry
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
                    double skillCarry = rs.getDouble("skill_carry");

                    switch (skillTypeId) {
                        case 1 -> skills.setFarmingExp(skillLevel);
                        case 2 -> {
                            skills.setGatheringExp(skillLevel);
                            skills.setGatheringExpCarry(skillCarry);
                        }
                        case 3 -> skills.setCraftingExp(skillLevel);
                        case 4 -> skills.setCombatExp(skillLevel);
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

    private void loadFarmPlots(Connection con, GameState gs, int saveId, String mapKeyFilter) throws Exception {
        if (con == null || gs == null || saveId <= 0) return;

        String sql = """
            SELECT map_key, plot_id, seed_item_id, planted_at, last_watered_at,
                   accumulated_water_reduction, total_growth_seconds,
                   water_cooldown_seconds, water_reduction_seconds
            FROM saved_farm_plot
            WHERE saved_id = ?
            """ + ((mapKeyFilter == null || mapKeyFilter.isBlank()) ? "" : " AND map_key = ?");

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);
            if (mapKeyFilter != null && !mapKeyFilter.isBlank()) {
                ps.setString(2, mapKeyFilter);
            }

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

    private void loadShopState(Connection con, GameState gs, int saveId) throws Exception {
        if (con == null || gs == null || saveId <= 0) return;

        gs.setShopRandomDay(-1);
        gs.setDailyRandomShopItemIds(List.of());
        gs.setShopStockDay(-1);
        gs.clearDailyRemainingShopStock();

        if (!hasTable(con, "saved_shop_state")) {
            return;
        }

        String stateSql = "SELECT random_day, stock_day FROM saved_shop_state WHERE saved_id = ?";
        try (PreparedStatement ps = con.prepareStatement(stateSql)) {
            ps.setInt(1, saveId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    gs.setShopRandomDay(rs.getInt("random_day"));
                    gs.setShopStockDay(rs.getInt("stock_day"));
                }
            }
        }

        if (hasTable(con, "saved_shop_random_item")) {
            String sql = """
                SELECT slot_index,
                       item_id
                  FROM saved_shop_random_item
                 WHERE saved_id = ?
                 ORDER BY slot_index
                """;

            ArrayList<Integer> ids = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, saveId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int slotIndex = rs.getInt("slot_index");
                        int itemId = rs.getInt("item_id");
                        if (rs.wasNull()) {
                            continue;
                        }
                        if (slotIndex < 0 || itemId <= 0) {
                            continue;
                        }
                        while (ids.size() <= slotIndex) {
                            ids.add(null);
                        }
                        ids.set(slotIndex, itemId);
                    }
                }
            }
            gs.setDailyRandomShopItemIds(ids);
        }

        if (hasTable(con, "saved_shop_stock")) {
            String sql = """
                SELECT item_id,
                       remaining_qty
                  FROM saved_shop_stock
                 WHERE saved_id = ?
                """;

            Map<Integer, Integer> stock = new LinkedHashMap<>();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, saveId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int itemId = rs.getInt("item_id");
                        if (rs.wasNull() || itemId <= 0) {
                            continue;
                        }
                        int qty = rs.getInt("remaining_qty");
                        stock.put(itemId, Math.max(0, qty));
                    }
                }
            }
            gs.setDailyRemainingShopStock(stock);
        }
    }

    private void loadSmeltState(Connection con, GameState gs, int saveId) throws Exception {
        if (!hasTable(con, "saved_smelt_state")) {
            gs.resetSmeltProgressionState();
            return;
        }

        String sql = """
            SELECT point_cap,
                   points,
                   last_point_update_total_seconds,
                   exp_carry,
                   cap_carry
              FROM saved_smelt_state
             WHERE saved_id = ?
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    gs.resetSmeltProgressionState();
                    return;
                }

                double nowSeconds = GameTimeManager.get().totalSecondsProperty().get();
                gs.loadSmeltProgressionState(
                    rs.getInt("point_cap"),
                    rs.getDouble("points"),
                    rs.getDouble("last_point_update_total_seconds"),
                    rs.getDouble("exp_carry"),
                    rs.getDouble("cap_carry"),
                    nowSeconds
                );
            }
        }
    }

    private void loadSmeltOreHistory(Connection con, GameState gs, int saveId) throws Exception {
        if (!hasTable(con, "saved_smelt_ore_history")) {
            gs.clearSmeltCounts();
            return;
        }

        gs.clearSmeltCounts();

        String sql = """
            SELECT source_item_id,
                   smelt_count
              FROM saved_smelt_ore_history
             WHERE saved_id = ?
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int sourceItemId = rs.getInt("source_item_id");
                    if (rs.wasNull()) {
                        continue;
                    }

                    int count = rs.getInt("smelt_count");
                    if (sourceItemId <= 0 || count <= 0) {
                        continue;
                    }

                    gs.setCompletedSmeltCount(sourceItemId, count);
                }
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

        psState.clearTransientFoodEffects();
        clearInventoryItems(psState.getInventory());
        replaceCoins(psState.getInventory(), 0);

        for (int i = 0; i < psState.getHotbarSize(); i++) {
            psState.clearHotbarItem(i);
        }

        gs.getPlayerSkills().clear();
        gs.clearUnlockedRecipes();
        clearInventoryItems(gs.getChestInventory());
        gs.clearFarmStates();
        gs.clearTreeStates();
        gs.clearMobStates();
        gs.clearActiveSmeltJob();
        gs.resetSmeltProgressionState();

        gs.setShopRandomDay(-1);
        gs.setDailyRandomShopItemIds(List.of());
        gs.setShopStockDay(-1);
        gs.clearDailyRemainingShopStock();
    }

    private void loadTreeResources(Connection con, GameState gs, int saveId, String mapKeyFilter) throws Exception {
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
            """ + ((mapKeyFilter == null || mapKeyFilter.isBlank()) ? "" : " AND map_key = ?");

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);
            if (mapKeyFilter != null && !mapKeyFilter.isBlank()) {
                ps.setString(2, mapKeyFilter);
            }

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

    private void loadTreeDrops(Connection con, GameState gs, int saveId, String mapKeyFilter) throws Exception {
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
            """ + ((mapKeyFilter == null || mapKeyFilter.isBlank()) ? "" : " AND map_key = ?") + """
            ORDER BY map_key, tree_id, drop_slot
            """;

        double nowSeconds = GameTimeManager.get().totalSecondsProperty().get();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);
            if (mapKeyFilter != null && !mapKeyFilter.isBlank()) {
                ps.setString(2, mapKeyFilter);
            }

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

    private void loadMobs(Connection con, GameState gs, int saveId, String mapKeyFilter) throws Exception {
        if (con == null || gs == null || saveId <= 0) return;
        if (!hasTable(con, "saved_mob")) {
            return;
        }

        String sql = """
            SELECT map_key,
                   mob_id,
                   mob_type,
                   lived_days,
                   hp,
                   max_hp,
                   sp,
                   max_sp,
                   str_stat,
                   agi_stat,
                   vit_stat,
                   dex_stat,
                   dead,
                   respawn_at_total_seconds,
                   run_attack_cooldown_until_total_seconds,
                   layout_x,
                   layout_y,
                   last_regen_total_seconds,
                   last_updated_day_number
            FROM saved_mob
            WHERE saved_id = ?
            """ + ((mapKeyFilter == null || mapKeyFilter.isBlank()) ? "" : " AND map_key = ?");

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);
            if (mapKeyFilter != null && !mapKeyFilter.isBlank()) {
                ps.setString(2, mapKeyFilter);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String mapKey = rs.getString("map_key");
                    String mobId = rs.getString("mob_id");
                    if (mapKey == null || mapKey.isBlank() || mobId == null || mobId.isBlank()) {
                        continue;
                    }

                    MobMapState mapState = gs.getMobState(mapKey);
                    MobMapState.MobData mob = mapState.getMob(mobId);

                    mob.mobType = rs.getString("mob_type");
                    mob.livedDays = rs.getInt("lived_days");
                    mob.hp = rs.getInt("hp");
                    mob.maxHp = rs.getInt("max_hp");
                    mob.sp = rs.getInt("sp");
                    mob.maxSp = rs.getInt("max_sp");
                    mob.str = rs.getInt("str_stat");
                    mob.agi = rs.getInt("agi_stat");
                    mob.vit = rs.getInt("vit_stat");
                    mob.dex = rs.getInt("dex_stat");
                    mob.dead = rs.getBoolean("dead");
                    mob.respawnAtTotalSeconds = rs.getDouble("respawn_at_total_seconds");
                    mob.runAttackCooldownUntilTotalSeconds = rs.getDouble("run_attack_cooldown_until_total_seconds");
                    mob.layoutX = rs.getDouble("layout_x");
                    mob.layoutY = rs.getDouble("layout_y");
                    mob.lastRegenTotalSeconds = rs.getDouble("last_regen_total_seconds");
                    mob.lastUpdatedDayNumber = rs.getInt("last_updated_day_number");
                    mob.drops.clear();
                }
            }
        }
    }

    private void loadMobDrops(Connection con, GameState gs, int saveId, String mapKeyFilter) throws Exception {
        if (con == null || gs == null || saveId <= 0) return;
        if (!hasTable(con, "saved_mob_drop")) {
            return;
        }

        String sql = """
            SELECT map_key,
                   mob_id,
                   drop_slot,
                   item_id,
                   quantity,
                   spawned_at_total_seconds,
                   expires_at_total_seconds
            FROM saved_mob_drop
            WHERE saved_id = ?
            """ + ((mapKeyFilter == null || mapKeyFilter.isBlank()) ? "" : " AND map_key = ?") + """
            ORDER BY map_key, mob_id, drop_slot
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);
            if (mapKeyFilter != null && !mapKeyFilter.isBlank()) {
                ps.setString(2, mapKeyFilter);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String mapKey = rs.getString("map_key");
                    String mobId = rs.getString("mob_id");
                    if (mapKey == null || mapKey.isBlank() || mobId == null || mobId.isBlank()) {
                        continue;
                    }

                    int itemId = rs.getInt("item_id");
                    int qty = rs.getInt("quantity");
                    if (itemId <= 0 || qty <= 0) {
                        continue;
                    }

                    MobMapState mapState = gs.getMobState(mapKey);
                    MobMapState.MobData mob = mapState.getMob(mobId);
                    mob.drops.add(new MobMapState.MobDrop(
                        itemId,
                        qty,
                        rs.getDouble("spawned_at_total_seconds"),
                        rs.getDouble("expires_at_total_seconds")
                    ));
                }
            }
        }
    }

    private void loadUnlockedRecipes(Connection con, GameState gs, int saveId) throws Exception {
        gs.clearUnlockedRecipes();
        if (!hasTable(con, "saved_unlocked_recipe")) {
            return;
        }

        ItemDatabase.loadItems();

        String sql = "SELECT recipe_key FROM saved_unlocked_recipe WHERE saved_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String recipeKey = rs.getString("recipe_key");
                    if (recipeKey == null || recipeKey.isBlank()) {
                        continue;
                    }

                    RecipeCatalog.RecipeDefinition definition = RecipeCatalog.findDefinitionByKey(recipeKey);
                    if (definition == null) {
                        System.out.println("Load warning: unknown recipe_key=\"" + recipeKey + "\" skipped.");
                        continue;
                    }

                    if (!RecipeCatalog.hasAllIngredientItems(definition)) {
                        System.out.println("Load warning: recipe_key=\"" + recipeKey + "\" missing ingredient items skipped.");
                        continue;
                    }

                    item output = RecipeCatalog.resolveItemByAliases(definition.getOutputAliases());
                    if (output == null || !ItemDatabase.isDisplayableItem(output.getItem_id())) {
                        System.out.println("Load warning: recipe_key=\"" + recipeKey + "\" missing/invalid output item skipped.");
                        continue;
                    }

                    boolean ingredientsValid = true;
                    for (RecipeCatalog.IngredientSpec spec : definition.getIngredients()) {
                        item ingredient = RecipeCatalog.resolveItemByAliases(spec.getItemAliases());
                        if (ingredient == null || !ItemDatabase.isDisplayableItem(ingredient.getItem_id())) {
                            ingredientsValid = false;
                            break;
                        }
                    }
                    if (!ingredientsValid) {
                        System.out.println("Load warning: recipe_key=\"" + recipeKey + "\" missing/invalid ingredient item skipped.");
                        continue;
                    }

                    gs.unlockRecipe(definition.getKey());
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
