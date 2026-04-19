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
import java.sql.Statement;
import java.util.Map;
import model.FarmState;
import model.Inventory;
import model.PlayerState;
import model.TreeMapState;

public class SaveGameService {
    private static final String FARM_SLOT_WATERING_CAN = "__farm_slot_watering_can__";
    private static final String FARM_SLOT_SICKLE = "__farm_slot_sickle__";


    public void saveGame(GameState gs) {
        if (gs == null) return;
        if (gs.getCurrentCharacterId() <= 0) {
            throw new IllegalStateException("GameState currentCharacterId is not set.");
        }

        dbcon db = new dbcon();

        try (Connection con = db.getConnection()) {
            con.setAutoCommit(false);

            if (!gs.hasActiveSave()) {
                createSaveIfMissing(con, gs, "Save 1");
            }

            saveMainRow(con, gs);
            savePlayerStats(con, gs);
            savePlayerSkills(con, gs);
            savePlayerInventory(con, gs);
            saveHotbar(con, gs);
            saveFarmPlots(con, gs);
            saveTreeResources(con, gs);
            saveTreeDrops(con, gs);
            saveChestItems(con, gs);
            saveTime(con, gs);
            saveSmeltState(con, gs);
            saveSmeltOreHistory(con, gs);
            saveUnlockedRecipes(con, gs);

            con.commit();
            System.out.println("Save complete. saved_id=" + gs.getCurrentSaveId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createSaveIfMissing(Connection con, GameState gs, String saveName) throws Exception {
        if (gs.hasActiveSave()) return;

        PlayerState ps = gs.getPlayerState();

        int statsId;
        String insertStats = """
            INSERT INTO player_stats
                (str_stat, agi_stat, dex_stat, vit_stat, hp, stamina)
                VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement psStats = con.prepareStatement(insertStats, Statement.RETURN_GENERATED_KEYS)) {
            psStats.setInt(1, ps.getStr());
            psStats.setInt(2, ps.getAgi());
            psStats.setInt(3, ps.getDex());
            psStats.setInt(4, ps.getVit());
            psStats.setInt(5, ps.getMaxHp());
            psStats.setInt(6, ps.getMaxStamina());
            psStats.executeUpdate();

            try (ResultSet rs = psStats.getGeneratedKeys()) {
                if (!rs.next()) {
                    throw new IllegalStateException("Failed to create stats row.");
                }
                statsId = rs.getInt(1);
            }
        }

        int mapId = resolveMapId(con, gs.getCurrentMapKey());

        String insertSave = """
            INSERT INTO save_game
            (character_id, saved_name, world_name, current_map_id, player_stats_id, money,
             saved_location_x, saved_location_y, exp,
             selected_hotbar_index, hotbar_visible, current_hp, current_stamina)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement psSave = con.prepareStatement(insertSave, Statement.RETURN_GENERATED_KEYS)) {
            psSave.setInt(1, gs.getCurrentCharacterId());
            psSave.setString(2, saveName);
            psSave.setString(3, gs.getCurrentWorldName());
            psSave.setInt(4, mapId);
            psSave.setInt(5, statsId);
            psSave.setInt(6, ps.getInventory().getCoins());
            psSave.setDouble(7, gs.getPlayerX());
            psSave.setDouble(8, gs.getPlayerY());
            psSave.setInt(9, ps.getExp());
            psSave.setInt(10, ps.getselectedHotbarIndex());
            psSave.setBoolean(11, ps.isHotbarVisible());
            psSave.setInt(12, ps.getHp());
            psSave.setInt(13, ps.getStamina());
            psSave.executeUpdate();

            try (ResultSet rs = psSave.getGeneratedKeys()) {
                if (!rs.next()) {
                    throw new IllegalStateException("Failed to create save_game row.");
                }
                gs.setCurrentSaveId(rs.getInt(1));
            }
        }
    }

    private void saveMainRow(Connection con, GameState gs) throws Exception {
        String sql = """
            UPDATE save_game
            SET world_name = ?,
                current_map_id = ?,
                money = ?,
                saved_location_x = ?,
                saved_location_y = ?,
                exp = ?,
                selected_hotbar_index = ?,
                hotbar_visible = ?,
                current_hp = ?,
                current_stamina = ?,
                last_saved_date = NOW()
            WHERE saved_id = ?
            """;

        PlayerState ps = gs.getPlayerState();

        try (PreparedStatement psmt = con.prepareStatement(sql)) {
            psmt.setString(1, gs.getCurrentWorldName());
            psmt.setInt(2, resolveMapId(con, gs.getCurrentMapKey()));
            psmt.setInt(3, ps.getInventory().getCoins());
            psmt.setDouble(4, gs.getPlayerX());
            psmt.setDouble(5, gs.getPlayerY());
            psmt.setInt(6, ps.getExp());
            psmt.setInt(7, ps.getselectedHotbarIndex());
            psmt.setBoolean(8, ps.isHotbarVisible());
            psmt.setInt(9, ps.getHp());
            psmt.setInt(10, ps.getStamina());
            psmt.setInt(11, gs.getCurrentSaveId());
            psmt.executeUpdate();
        }
    }

    private void savePlayerStats(Connection con, GameState gs) throws Exception {
        int statsId = getStatsIdForSave(con, gs.getCurrentSaveId());
        PlayerState ps = gs.getPlayerState();

        String sql = """
            UPDATE player_stats
            SET str_stat = ?, agi_stat = ?, dex_stat = ?, vit_stat = ?, hp = ?, stamina = ?
            WHERE player_stats_id = ?
            """;

        try (PreparedStatement psmt = con.prepareStatement(sql)) {
            psmt.setInt(1, ps.getStr());
            psmt.setInt(2, ps.getAgi());
            psmt.setInt(3, ps.getDex());
            psmt.setInt(4, ps.getVit());
            psmt.setInt(5, ps.getMaxHp());
            psmt.setInt(6, ps.getMaxStamina());
            psmt.setInt(7, statsId);
            psmt.executeUpdate();
        }
    }

    private void savePlayerSkills(Connection con, GameState gs) throws Exception {
        int saveId = gs.getCurrentSaveId();
        GameState.PlayerSkills skills = gs.getPlayerSkills();

        try (PreparedStatement del = con.prepareStatement(
                "DELETE FROM player_skills WHERE saved_id = ?")) {
            del.setInt(1, saveId);
            del.executeUpdate();
        }

        String sql = "INSERT INTO player_skills (saved_id, skill_type_id, skill_level) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            insertSkill(ps, saveId, 1, skills.getFarmingExp());
            insertSkill(ps, saveId, 2, skills.getGatheringExp());
            insertSkill(ps, saveId, 3, skills.getCraftingExp());
            insertSkill(ps, saveId, 4, skills.getFishingExp());
            insertSkill(ps, saveId, 5, skills.getBattlingExp());
            insertSkill(ps, saveId, 6, skills.getCookingExp());
            ps.executeBatch();
        }
    }

    private void insertSkill(PreparedStatement ps, int saveId, int skillTypeId, int skillLevel) throws Exception {
        ps.setInt(1, saveId);
        ps.setInt(2, skillTypeId);
        ps.setInt(3, skillLevel);
        ps.addBatch();
    }
    
    private void savePlayerInventory(Connection con, GameState gs) throws Exception {
        int saveId = gs.getCurrentSaveId();
        Inventory inv = gs.getPlayerState().getInventory();

        try (PreparedStatement del = con.prepareStatement("DELETE FROM saved_item WHERE saved_id = ?")) {
            del.setInt(1, saveId);
            del.executeUpdate();
        }

        String sql = "INSERT INTO saved_item (saved_id, item_id, qty) VALUES (?, ?, ?)";
        try (PreparedStatement ins = con.prepareStatement(sql)) {
            for (Map.Entry<Integer, Integer> e : inv.snapshot().entrySet()) {
                ins.setInt(1, saveId);
                ins.setInt(2, e.getKey());
                ins.setInt(3, e.getValue());
                ins.addBatch();
            }
            ins.executeBatch();
        }
    }

    private void saveHotbar(Connection con, GameState gs) throws Exception {
        int saveId = gs.getCurrentSaveId();
        PlayerState ps = gs.getPlayerState();

        try (PreparedStatement del = con.prepareStatement("DELETE FROM saved_hotbar WHERE saved_id = ?")) {
            del.setInt(1, saveId);
            del.executeUpdate();
        }

        String sql = "INSERT INTO saved_hotbar (saved_id, slot_index, item_id, qty) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ins = con.prepareStatement(sql)) {
            for (int i = 0; i < ps.getHotbarSize(); i++) {
                int itemId = ps.getHotbarItemId(i);
                int qty = ps.getHotbarItemCount(i);

                ins.setInt(1, saveId);
                ins.setInt(2, i);
                if (itemId <= 0 || qty <= 0) {
                    ins.setNull(3, java.sql.Types.INTEGER);
                    ins.setInt(4, 0);
                } else {
                    ins.setInt(3, itemId);
                    ins.setInt(4, qty);
                }
                ins.addBatch();
            }
            ins.executeBatch();
        }
    }

    private void saveFarmPlots(Connection con, GameState gs) throws Exception {
        int saveId = gs.getCurrentSaveId();

        try (PreparedStatement del = con.prepareStatement("DELETE FROM saved_farm_plot WHERE saved_id = ?")) {
            del.setInt(1, saveId);
            del.executeUpdate();
        }

        String sql = """
            INSERT INTO saved_farm_plot
            (saved_id, map_key, plot_id, seed_item_id, planted_at, last_watered_at,
             accumulated_water_reduction, total_growth_seconds,
             water_cooldown_seconds, water_reduction_seconds)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ins = con.prepareStatement(sql)) {
            saveFarmMap(ins, saveId, "home_map", gs.getFarmState("home_map"));
            saveFarmMap(ins, saveId, "Home_interior", gs.getFarmState("Home_interior"));
            ins.executeBatch();
        }
    }

    private void saveTreeResources(Connection con, GameState gs) throws Exception {
        if (con == null || gs == null) return;
        if (!hasTable(con, "saved_tree_resource")) {
            return;
        }

        int saveId = gs.getCurrentSaveId();

        try (PreparedStatement del = con.prepareStatement("DELETE FROM saved_tree_resource WHERE saved_id = ?")) {
            del.setInt(1, saveId);
            del.executeUpdate();
        }

        if (gs.getAllTreeStates().isEmpty()) {
            return;
        }

        String sql = """
            INSERT INTO saved_tree_resource
                (saved_id, map_key, tree_id, tree_type,
                 current_stage, lived_days, hp, max_hp, vit,
                 drop_rate_pity, drops_this_life, damage_since_last_drop_roll, last_hp_growth_total_seconds,
                 respawn_at_total_seconds, last_updated_day_number)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ins = con.prepareStatement(sql)) {
            for (Map.Entry<String, TreeMapState> mapEntry : gs.getAllTreeStates().entrySet()) {
                String mapKey = mapEntry == null ? null : mapEntry.getKey();
                TreeMapState mapState = mapEntry == null ? null : mapEntry.getValue();
                if (mapKey == null || mapKey.isBlank() || mapState == null) {
                    continue;
                }

                for (Map.Entry<String, TreeMapState.TreeData> treeEntry : mapState.getAllTrees().entrySet()) {
                    String treeId = treeEntry == null ? null : treeEntry.getKey();
                    TreeMapState.TreeData tree = treeEntry == null ? null : treeEntry.getValue();
                    if (treeId == null || treeId.isBlank() || tree == null) {
                        continue;
                    }

                    String type = TreeMapState.normalizeTreeTypeKey(tree.typeKey, "blue");
                    int stage = tree.currentStage;
                    if (stage < 1 || stage > 3) stage = 2;

                    ins.setInt(1, saveId);
                    ins.setString(2, mapKey);
                    ins.setString(3, treeId);
                    ins.setString(4, type);
                    ins.setInt(5, stage);
                    ins.setInt(6, Math.max(0, tree.livedDays));
                    ins.setInt(7, Math.max(0, tree.hp));
                    ins.setInt(8, Math.max(0, tree.maxHp));
                    ins.setInt(9, Math.max(0, tree.vit));
                    ins.setInt(10, Math.max(0, tree.dropRatePity));
                    ins.setInt(11, Math.max(0, tree.dropsThisLife));
                    ins.setInt(12, Math.max(0, tree.damageSinceLastDropRoll));
                    ins.setDouble(13, tree.lastHpGrowthTotalSeconds);
                    ins.setDouble(14, tree.respawnAtTotalSeconds);
                    ins.setInt(15, Math.max(1, tree.lastUpdatedDayNumber));
                    ins.addBatch();
                }
            }

            ins.executeBatch();
        }
    }

    private void saveTreeDrops(Connection con, GameState gs) throws Exception {
        if (con == null || gs == null) return;
        if (!hasTable(con, "saved_tree_drop")) {
            return;
        }

        int saveId = gs.getCurrentSaveId();

        try (PreparedStatement del = con.prepareStatement("DELETE FROM saved_tree_drop WHERE saved_id = ?")) {
            del.setInt(1, saveId);
            del.executeUpdate();
        }

        if (gs.getAllTreeStates().isEmpty()) {
            return;
        }

        String sql = """
            INSERT INTO saved_tree_drop
                (saved_id, map_key, tree_id, drop_slot,
                 item_id, quantity, spawned_at_total_seconds, expires_at_total_seconds)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ins = con.prepareStatement(sql)) {
            for (Map.Entry<String, TreeMapState> mapEntry : gs.getAllTreeStates().entrySet()) {
                String mapKey = mapEntry == null ? null : mapEntry.getKey();
                TreeMapState mapState = mapEntry == null ? null : mapEntry.getValue();
                if (mapKey == null || mapKey.isBlank() || mapState == null) {
                    continue;
                }

                for (Map.Entry<String, TreeMapState.TreeData> treeEntry : mapState.getAllTrees().entrySet()) {
                    String treeId = treeEntry == null ? null : treeEntry.getKey();
                    TreeMapState.TreeData tree = treeEntry == null ? null : treeEntry.getValue();
                    if (treeId == null || treeId.isBlank() || tree == null || tree.drops.isEmpty()) {
                        continue;
                    }

                    int slot = 0;
                    for (TreeMapState.TreeDrop drop : tree.drops) {
                        if (drop == null || drop.itemId <= 0 || drop.quantity <= 0) {
                            continue;
                        }

                        ins.setInt(1, saveId);
                        ins.setString(2, mapKey);
                        ins.setString(3, treeId);
                        ins.setInt(4, slot++);
                        ins.setInt(5, drop.itemId);
                        ins.setInt(6, drop.quantity);
                        ins.setDouble(7, drop.spawnedAtTotalSeconds);
                        ins.setDouble(8, drop.expiresAtTotalSeconds);
                        ins.addBatch();
                    }
                }
            }

            ins.executeBatch();
        }
    }

    private void saveFarmMap(PreparedStatement ins, int saveId, String mapKey, FarmState farmState) throws Exception {
        if (farmState == null) return;

        for (Map.Entry<String, FarmState.PlotData> e : farmState.getAllPlots().entrySet()) {
            FarmState.PlotData p = e.getValue();

            ins.setInt(1, saveId);
            ins.setString(2, mapKey);
            ins.setString(3, e.getKey());

            Integer seedItemId = p.seedItemId;
            if (seedItemId == null || seedItemId <= 0) {
                ins.setNull(4, java.sql.Types.INTEGER);
            } else {
                ins.setInt(4, seedItemId);
            }

            ins.setDouble(5, p.plantedAt);
            ins.setDouble(6, p.lastWateredAt);
            ins.setDouble(7, p.accumulatedWaterReduction);
            ins.setDouble(8, p.totalGrowthSeconds);
            ins.setDouble(9, p.waterCooldownSeconds);
            ins.setDouble(10, p.waterReductionSeconds);
            ins.addBatch();
        }

        saveFarmToolSlot(ins, saveId, mapKey, FARM_SLOT_WATERING_CAN, farmState.getEquippedWateringCanItemId());
        saveFarmToolSlot(ins, saveId, mapKey, FARM_SLOT_SICKLE, farmState.getEquippedSickleItemId());
    }

    private void saveFarmToolSlot(PreparedStatement ins, int saveId, String mapKey, String plotId, Integer itemId) throws Exception {
        ins.setInt(1, saveId);
        ins.setString(2, mapKey);
        ins.setString(3, plotId);

        if (itemId == null || itemId <= 0) {
            ins.setNull(4, java.sql.Types.INTEGER);
        } else {
            ins.setInt(4, itemId);
        }

        ins.setDouble(5, -1);
        ins.setDouble(6, -1);
        ins.setDouble(7, 0);
        ins.setDouble(8, 0);
        ins.setDouble(9, 0);
        ins.setDouble(10, 0);
        ins.addBatch();
    }

    private void saveChestItems(Connection con, GameState gs) throws Exception {
        int saveId = gs.getCurrentSaveId();

        try (PreparedStatement del = con.prepareStatement("DELETE FROM saved_chest_item WHERE saved_id = ?")) {
            del.setInt(1, saveId);
            del.executeUpdate();
        }

        String sql = "INSERT INTO saved_chest_item (saved_id, item_id, qty) VALUES (?, ?, ?)";
        try (PreparedStatement ins = con.prepareStatement(sql)) {
            for (Map.Entry<Integer, Integer> e : gs.getChestInventory().snapshot().entrySet()) {
                ins.setInt(1, saveId);
                ins.setInt(2, e.getKey());
                ins.setInt(3, e.getValue());
                ins.addBatch();
            }
            ins.executeBatch();
        }
    }

    private void saveTime(Connection con, GameState gs) throws Exception {
        int saveId = gs.getCurrentSaveId();
        GameTimeManager time = GameTimeManager.get();

        String sql = """
            INSERT INTO saved_time (saved_id, day_number, total_seconds)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE
                day_number = VALUES(day_number),
                total_seconds = VALUES(total_seconds)
            """;

        try (PreparedStatement psmt = con.prepareStatement(sql)) {
            psmt.setInt(1, saveId);
            psmt.setInt(2, time.dayNumberProperty().get());
            psmt.setDouble(3, time.totalSecondsProperty().get());
            psmt.executeUpdate();
        }
    }

    private void saveSmeltState(Connection con, GameState gs) throws Exception {
        if (!hasTable(con, "saved_smelt_state")) {
            return;
        }

        int saveId = gs.getCurrentSaveId();
        double nowSeconds = GameTimeManager.get().totalSecondsProperty().get();

        String sql = """
            INSERT INTO saved_smelt_state
                (saved_id, point_cap, points, last_point_update_total_seconds, exp_carry, cap_carry)
            VALUES (?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                point_cap = VALUES(point_cap),
                points = VALUES(points),
                last_point_update_total_seconds = VALUES(last_point_update_total_seconds),
                exp_carry = VALUES(exp_carry),
                cap_carry = VALUES(cap_carry)
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);
            ps.setInt(2, gs.getSmeltPointCap());
            ps.setDouble(3, gs.getSmeltPointsExact(nowSeconds));
            ps.setDouble(4, gs.getLastSmeltPointUpdateSeconds());
            ps.setDouble(5, gs.getSmeltExpCarry());
            ps.setDouble(6, gs.getSmeltPointCapCarry());
            ps.executeUpdate();
        }
    }

    private void saveSmeltOreHistory(Connection con, GameState gs) throws Exception {
        if (!hasTable(con, "saved_smelt_ore_history")) {
            return;
        }

        int saveId = gs.getCurrentSaveId();

        try (PreparedStatement del = con.prepareStatement("DELETE FROM saved_smelt_ore_history WHERE saved_id = ?")) {
            del.setInt(1, saveId);
            del.executeUpdate();
        }

        Map<Integer, Integer> counts = gs.getCompletedSmeltCountsSnapshot();
        if (counts.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO saved_smelt_ore_history (saved_id, source_item_id, smelt_count) VALUES (?, ?, ?)";
        try (PreparedStatement ins = con.prepareStatement(sql)) {
            for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
                int sourceItemId = entry.getKey() == null ? -1 : entry.getKey();
                int count = entry.getValue() == null ? 0 : entry.getValue();
                if (sourceItemId <= 0 || count <= 0) {
                    continue;
                }
                ins.setInt(1, saveId);
                ins.setInt(2, sourceItemId);
                ins.setInt(3, count);
                ins.addBatch();
            }
            ins.executeBatch();
        }
    }

    private void saveUnlockedRecipes(Connection con, GameState gs) throws Exception {
        if (!hasTable(con, "saved_unlocked_recipe")) {
            return;
        }

        int saveId = gs.getCurrentSaveId();

        try (PreparedStatement del = con.prepareStatement("DELETE FROM saved_unlocked_recipe WHERE saved_id = ?")) {
            del.setInt(1, saveId);
            del.executeUpdate();
        }

        if (gs.getUnlockedRecipeKeys().isEmpty()) {
            return;
        }

        String sql = "INSERT INTO saved_unlocked_recipe (saved_id, recipe_key) VALUES (?, ?)";
        try (PreparedStatement ins = con.prepareStatement(sql)) {
            for (String recipeKey : gs.getUnlockedRecipeKeys()) {
                if (recipeKey == null || recipeKey.isBlank()) {
                    continue;
                }
                ins.setInt(1, saveId);
                ins.setString(2, recipeKey);
                ins.addBatch();
            }
            ins.executeBatch();
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

    private int resolveMapId(Connection con, String mapKey) throws Exception {
        String sql = "SELECT map_id FROM game_map WHERE map_name = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, mapKey);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("map_id");
                }
            }
        }
        throw new IllegalStateException("Map not found in game_map: " + mapKey);
    }

    private int getStatsIdForSave(Connection con, int saveId) throws Exception {
        String sql = "SELECT player_stats_id FROM save_game WHERE saved_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("player_stats_id");
                }
            }
        }
        return -1;
    }
    
    public void saveGameToSlot(GameState gs, int slotNumber) {
        if (gs == null) return;
        if (slotNumber < 1 || slotNumber > 4) {
            throw new IllegalArgumentException("slotNumber must be 1..4");
        }
        if (gs.getCurrentCharacterId() <= 0) {
            throw new IllegalStateException("GameState currentCharacterId is not set.");
        }

        String slotName = "Save " + slotNumber;

        dbcon db = new dbcon();

        try (Connection con = db.getConnection()) {
            con.setAutoCommit(false);

            Integer existingSaveId = findSaveIdByCharacterAndSlot(con, gs.getCurrentCharacterId(), slotName);

            if (existingSaveId != null) {
                gs.setCurrentSaveId(existingSaveId);
            } else {
                createSaveForSlot(con, gs, slotName);
            }

            saveMainRow(con, gs);
            savePlayerStats(con, gs);
            savePlayerSkills(con, gs);
            savePlayerInventory(con, gs);
            saveHotbar(con, gs);
            saveFarmPlots(con, gs);
            saveTreeResources(con, gs);
            saveTreeDrops(con, gs);
            saveChestItems(con, gs);
            saveTime(con, gs);
            saveSmeltState(con, gs);
            saveSmeltOreHistory(con, gs);
            saveUnlockedRecipes(con, gs);

            con.commit();
            System.out.println("Save complete. slot=" + slotName + ", saved_id=" + gs.getCurrentSaveId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private Integer findSaveIdByCharacterAndSlot(Connection con, int characterId, String slotName) throws Exception {
        String sql = "SELECT saved_id FROM save_game WHERE character_id = ? AND saved_name = ? LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, characterId);
            ps.setString(2, slotName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("saved_id");
                }
            }
        }
        return null;
    }

    private void createSaveForSlot(Connection con, GameState gs, String saveName) throws Exception {
        PlayerState ps = gs.getPlayerState();

        int statsId;
        String insertStats = """
            INSERT INTO player_stats (str_stat, agi_stat, dex_stat, vit_stat, hp, stamina)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement psStats = con.prepareStatement(insertStats, Statement.RETURN_GENERATED_KEYS)) {
            psStats.setInt(1, ps.getStr());
            psStats.setInt(2, ps.getAgi());
            psStats.setInt(3, ps.getDex());
            psStats.setInt(4, ps.getVit());
            psStats.setInt(5, ps.getMaxHp());
            psStats.setInt(6, ps.getMaxStamina());
            psStats.executeUpdate();

            try (ResultSet rs = psStats.getGeneratedKeys()) {
                if (!rs.next()) {
                    throw new IllegalStateException("Failed to create stats row.");
                }
                statsId = rs.getInt(1);
            }
        }

        int mapId = resolveMapId(con, gs.getCurrentMapKey());

        String insertSave = """
            INSERT INTO save_game
            (character_id, saved_name, world_name, current_map_id, player_stats_id, money,
             saved_location_x, saved_location_y, exp,
             selected_hotbar_index, hotbar_visible, current_hp, current_stamina)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement psSave = con.prepareStatement(insertSave, Statement.RETURN_GENERATED_KEYS)) {
            psSave.setInt(1, gs.getCurrentCharacterId());
            psSave.setString(2, saveName);
            psSave.setString(3, gs.getCurrentWorldName());
            psSave.setInt(4, mapId);
            psSave.setInt(5, statsId);
            psSave.setInt(6, ps.getInventory().getCoins());
            psSave.setDouble(7, gs.getPlayerX());
            psSave.setDouble(8, gs.getPlayerY());
            psSave.setInt(9, ps.getExp());
            psSave.setInt(10, ps.getselectedHotbarIndex());
            psSave.setBoolean(11, ps.isHotbarVisible());
            psSave.setInt(12, ps.getHp());
            psSave.setInt(13, ps.getStamina());
            psSave.executeUpdate();

            try (ResultSet rs = psSave.getGeneratedKeys()) {
                if (!rs.next()) {
                    throw new IllegalStateException("Failed to create save_game row.");
                }
                gs.setCurrentSaveId(rs.getInt(1));
            }
        }
    }
}
