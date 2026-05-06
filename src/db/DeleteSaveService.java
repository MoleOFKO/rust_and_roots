package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.LinkedHashSet;

/**
 * Deletes a save slot (Save 1..Save 4) for a character (and any dependent rows via ON DELETE CASCADE).
 *
 * Used for permadeath / game-over cleanup so a dead run cannot be resumed.
 */
public final class DeleteSaveService {

    public boolean deleteSaveById(int saveId) {
        if (saveId <= 0) {
            return false;
        }

        dbcon db = new dbcon();

        try (Connection con = db.getConnection()) {
            con.setAutoCommit(false);

            Integer characterId = null;
            String savedName = null;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT character_id, saved_name FROM save_game WHERE saved_id = ?")) {
                ps.setInt(1, saveId);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int cid = rs.getInt("character_id");
                        if (!rs.wasNull() && cid > 0) {
                            characterId = cid;
                        }
                        savedName = rs.getString("saved_name");
                    }
                }
            }

            if (characterId == null || savedName == null || savedName.isBlank()) {
                con.rollback();
                return false;
            }

            // Collect player_stats ids for *all* saves in the same slot so the slot becomes truly empty.
            LinkedHashSet<Integer> playerStatsIds = new LinkedHashSet<>();
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT player_stats_id FROM save_game WHERE character_id = ? AND saved_name = ?")) {
                ps.setInt(1, characterId);
                ps.setString(2, savedName);
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("player_stats_id");
                        if (!rs.wasNull() && id > 0) {
                            playerStatsIds.add(id);
                        }
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM save_game WHERE character_id = ? AND saved_name = ?")) {
                ps.setInt(1, characterId);
                ps.setString(2, savedName);
                ps.executeUpdate();
            }

            // `save_game` references `player_stats` (not ON DELETE CASCADE), so clean it up explicitly.
            if (!playerStatsIds.isEmpty()) {
                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM player_stats WHERE player_stats_id = ?")) {
                    for (int id : playerStatsIds) {
                        ps.setInt(1, id);
                        ps.executeUpdate();
                    }
                }
            }

            con.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
