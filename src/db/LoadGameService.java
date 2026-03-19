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

/*
    LoadGameService loadService = new LoadGameService();
    boolean ok = loadService.loadGame(1);

    if (ok) {
        util.SceneManager.switchScene("/view/" + GameState.get().getCurrentMapKey() + ".fxml");
    }

*/

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
            SELECT saved_id, character_id, current_map_id, money,
                   saved_location_x, saved_location_y,
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

                psState.setSelectedHotbarIndex(rs.getInt("selected_hotbar_index"));
                psState.setHotbarVisible(rs.getBoolean("hotbar_visible"));

                replaceCoins(psState.getInventory(), rs.getInt("money"));
                return true;
            }
        }
    }

    private void loadPlayerStats(Connection con, GameState gs, int saveId) throws Exception {
        String sql = """
            SELECT s.agi, s.hp, s.stamina
            FROM save_game sg
            JOIN stats s ON sg.stats_id = s.stats_id
            WHERE sg.saved_id = ?
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return;

                PlayerState psState = gs.getPlayerState();
                psState.setAgi(rs.getInt("agi"));
                psState.setHp(rs.getInt("hp"));
                psState.setStamina(rs.getInt("stamina"));
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

        for (int i = 0; i < psState.getHotbarSize(); i++) {
            psState.clearHotbarItem(i);
        }

        String sql = "SELECT slot_index, item_id FROM saved_hotbar WHERE saved_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int slotIndex = rs.getInt("slot_index");
                    int itemId = rs.getInt("item_id");

                    if (!rs.wasNull()) {
                        psState.setHotbarItemId(slotIndex, itemId);
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
        clearFarmState(gs.getFarmState("home_map"));
        clearFarmState(gs.getFarmState("Home_interior"));

        String sql = """
            SELECT map_key, plot_id, seed_type, planted_at, last_watered_at,
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
                    String seedType = rs.getString("seed_type");

                    FarmState farmState = gs.getFarmState(mapKey);
                    FarmState.PlotData plot = farmState.getPlot(plotId);

                    plot.seed = (seedType == null || seedType.isBlank())
                            ? null
                            : Farm.SeedType.valueOf(seedType);

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