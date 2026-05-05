package db;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Deletes a save_game row (and any dependent rows via ON DELETE CASCADE).
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

            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM save_game WHERE saved_id = ?")) {
                ps.setInt(1, saveId);
                ps.executeUpdate();
            }

            con.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

