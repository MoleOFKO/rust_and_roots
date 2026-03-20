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
import java.sql.Statement;
import java.util.Map;
import model.Farm;
import model.FarmState;
import model.Inventory;
import model.PlayerState;

/*
    SaveGameService saveService = new SaveGameService();
    saveService.saveGame(GameState.get());

*/

public class SaveGameService {

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
            savePlayerInventory(con, gs);
            saveHotbar(con, gs);
            saveFarmPlots(con, gs);
            saveChestItems(con, gs);
            saveTime(con, gs);

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
            INSERT INTO stats (stats_type, str, agi, dex, vit, hp, stamina)
            VALUES ('player_save', 0, ?, 0, 0, ?, ?)
            """;

        try (PreparedStatement psStats = con.prepareStatement(insertStats, Statement.RETURN_GENERATED_KEYS)) {
            psStats.setInt(1, ps.getAgi());
            psStats.setInt(2, ps.getHp());
            psStats.setInt(3, ps.getStamina());
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
            (character_id, saved_name, current_map_id, stats_id, money,
             saved_location_x, saved_location_y, exp,
             selected_hotbar_index, hotbar_visible)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement psSave = con.prepareStatement(insertSave, Statement.RETURN_GENERATED_KEYS)) {
            psSave.setInt(1, gs.getCurrentCharacterId());
            psSave.setString(2, saveName);
            psSave.setInt(3, mapId);
            psSave.setInt(4, statsId);
            psSave.setInt(5, ps.getInventory().getCoins());
            psSave.setInt(6, (int) Math.round(gs.getPlayerX()));
            psSave.setInt(7, (int) Math.round(gs.getPlayerY()));
            psSave.setInt(8, 0);
            psSave.setInt(9, ps.getselectedHotbarIndex());
            psSave.setBoolean(10, ps.isHotbarVisible());
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
            SET current_map_id = ?,
                money = ?,
                saved_location_x = ?,
                saved_location_y = ?,
                selected_hotbar_index = ?,
                hotbar_visible = ?,
                last_saved_date = NOW()
            WHERE saved_id = ?
            """;

        PlayerState ps = gs.getPlayerState();

        try (PreparedStatement psmt = con.prepareStatement(sql)) {
            psmt.setInt(1, resolveMapId(con, gs.getCurrentMapKey()));
            psmt.setInt(2, ps.getInventory().getCoins());
            psmt.setInt(3, (int) Math.round(gs.getPlayerX()));
            psmt.setInt(4, (int) Math.round(gs.getPlayerY()));
            psmt.setInt(5, ps.getselectedHotbarIndex());
            psmt.setBoolean(6, ps.isHotbarVisible());
            psmt.setInt(7, gs.getCurrentSaveId());
            psmt.executeUpdate();
        }
    }

    private void savePlayerStats(Connection con, GameState gs) throws Exception {
        int statsId = getStatsIdForSave(con, gs.getCurrentSaveId());
        PlayerState ps = gs.getPlayerState();

        String sql = """
            UPDATE stats
            SET agi = ?, hp = ?, stamina = ?
            WHERE stats_id = ?
            """;

        try (PreparedStatement psmt = con.prepareStatement(sql)) {
            psmt.setInt(1, ps.getAgi());
            psmt.setInt(2, ps.getHp());
            psmt.setInt(3, ps.getStamina());
            psmt.setInt(4, statsId);
            psmt.executeUpdate();
        }
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

        String sql = "INSERT INTO saved_hotbar (saved_id, slot_index, item_id) VALUES (?, ?, ?)";
        try (PreparedStatement ins = con.prepareStatement(sql)) {
            for (int i = 0; i < ps.getHotbarSize(); i++) {
                int itemId = ps.getHotbarItemId(i);

                ins.setInt(1, saveId);
                ins.setInt(2, i);
                if (itemId <= 0) {
                    ins.setNull(3, java.sql.Types.INTEGER);
                } else {
                    ins.setInt(3, itemId);
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
            (saved_id, map_key, plot_id, seed_type, planted_at, last_watered_at,
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

    private void saveFarmMap(PreparedStatement ins, int saveId, String mapKey, FarmState farmState) throws Exception {
        if (farmState == null) return;

        for (Map.Entry<String, FarmState.PlotData> e : farmState.getAllPlots().entrySet()) {
            FarmState.PlotData p = e.getValue();

            ins.setInt(1, saveId);
            ins.setString(2, mapKey);
            ins.setString(3, e.getKey());
            ins.setString(4, p.seed == null ? null : p.seed.name());
            ins.setDouble(5, p.plantedAt);
            ins.setDouble(6, p.lastWateredAt);
            ins.setDouble(7, p.accumulatedWaterReduction);
            ins.setDouble(8, p.totalGrowthSeconds);
            ins.setDouble(9, p.waterCooldownSeconds);
            ins.setDouble(10, p.waterReductionSeconds);
            ins.addBatch();
        }
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
        String sql = "SELECT stats_id FROM save_game WHERE saved_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("stats_id");
                }
            }
        }
        throw new IllegalStateException("stats_id not found for save_game.saved_id=" + saveId);
    }
}