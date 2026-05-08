package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Marks a save as permanently dead without deleting it.
 *
 * This keeps the save slot as a milestone (e.g., survived days) while preventing replay.
 */
public class MarkSaveDeadService {

    public void markSaveDead(int saveId, int dayNumber, double totalSeconds) {
        if (saveId <= 0) {
            return;
        }

        dbcon db = new dbcon();

        try (Connection con = db.getConnection()) {
            con.setAutoCommit(false);

            updateSaveGameDeadFlag(con, saveId);
            updateSavedTime(con, saveId, dayNumber, totalSeconds);

            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateSaveGameDeadFlag(Connection con, int saveId) throws SQLException {
        SQLException last = null;

        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE save_game SET is_dead = 1, current_hp = 0, last_saved_date = NOW() WHERE saved_id = ?")) {
            ps.setInt(1, saveId);
            ps.executeUpdate();
            return;
        } catch (SQLException e) {
            last = e;
        }

        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE save_game SET current_hp = 0, last_saved_date = NOW() WHERE saved_id = ?")) {
            ps.setInt(1, saveId);
            ps.executeUpdate();
            return;
        } catch (SQLException e) {
            if (last != null) {
                e.addSuppressed(last);
            }
            throw e;
        }
    }

    private void updateSavedTime(Connection con, int saveId, int dayNumber, double totalSeconds) throws SQLException {
        if (dayNumber <= 0) {
            dayNumber = 1;
        }
        if (!Double.isFinite(totalSeconds) || totalSeconds < 0.0) {
            totalSeconds = 0.0;
        }

        String sql = """
            INSERT INTO saved_time (saved_id, day_number, total_seconds)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE
                day_number = VALUES(day_number),
                total_seconds = VALUES(total_seconds)
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);
            ps.setInt(2, dayNumber);
            ps.setDouble(3, totalSeconds);
            ps.executeUpdate();
        }
    }
}

