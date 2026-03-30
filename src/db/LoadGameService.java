/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import game.GameState;
import game.GameTimeManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.Farm;
import model.FarmState;
import model.Inventory;
import model.PlayerState;

public class LoadGameService {

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
            loadPlayerInventory(con, gs, saveId);
            loadHotbar(con, gs, saveId);
            loadChestItems(con, gs, saveId);
            loadFarmPlots(con, gs, saveId);
            loadTime(con, saveId);

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

        String sql = "SELECT item_id, qty FROM saved_item WHERE saved_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int itemId = rs.getInt("item_id");
                    int qty = rs.getInt("qty");
                    if (itemId > 0 && qty > 0) {
                        inv.addItem(itemId, qty);
                    }
                }
            }
        }
    }

    private void loadHotbar(Connection con, GameState gs, int saveId) throws Exception {
        PlayerState psState = gs.getPlayerState();

        psState.clearAllHotbarItems();

        String sql = "SELECT slot_index, item_id, qty FROM saved_hotbar WHERE saved_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int slotIndex = rs.getInt("slot_index");
                    int itemId = rs.getInt("item_id");
                    int qty = rs.getInt("qty");

                    if (!rs.wasNull()) {
                        psState.setHotbarSlot(slotIndex, itemId, Math.max(1, qty));
                    }
                }
            }
        }
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
                        chest.addItem(itemId, qty);
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

                    FarmState.PlotData plot = farm.getPlot(plotId);
                    if (plot == null) continue;

                    Integer seedItemId = (Integer) rs.getObject("seed_item_id");
                    plot.seedItemId = seedItemId;

                    plot.plantedAt = rs.getDouble("planted_at");
                    plot.lastWateredAt = rs.getDouble("last_watered_at");
                    plot.accumulatedWaterReduction = rs.getDouble("accumulated_water_reduction");
                    plot.totalGrowthSeconds = rs.getDouble("total_growth_seconds");
                    plot.waterCooldownSeconds = rs.getDouble("water_cooldown_seconds");
                    plot.waterReductionSeconds = rs.getDouble("water_reduction_seconds");
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
        clearInventoryItems(gs.getChestInventory());
        clearFarmState(gs.getFarmState("home_map"));
        clearFarmState(gs.getFarmState("Home_interior"));
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
    }
}
