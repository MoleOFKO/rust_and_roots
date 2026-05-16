package db;

import game.GameState;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class HighScoreService {
    private static final int DAY_SCORE_WEIGHT = 100;
    private static final int STATUS_SCORE_WEIGHT = 10;
    private static final int SKILL_LEVEL_SCORE_WEIGHT = 50;

    private HighScoreService() {
    }

    public static int computeHighScore(Connection con, int saveId) throws Exception {
        if (con == null || saveId <= 0) {
            return 0;
        }

        int daysSurvived = readDaysSurvived(con, saveId);
        int totalStatusPoints = readTotalStatusPoints(con, saveId);
        int totalSkillLevels = readTotalSkillLevels(con, saveId);

        return (daysSurvived * DAY_SCORE_WEIGHT)
                + (totalStatusPoints * STATUS_SCORE_WEIGHT)
                + (totalSkillLevels * SKILL_LEVEL_SCORE_WEIGHT);
    }

    public static void updateHighScore(Connection con, int saveId) throws Exception {
        if (con == null || saveId <= 0 || !hasColumn(con, "save_game", "high_score")) {
            return;
        }

        int highScore = computeHighScore(con, saveId);
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE save_game SET high_score = ? WHERE saved_id = ?")) {
            ps.setInt(1, highScore);
            ps.setInt(2, saveId);
            ps.executeUpdate();
        }
    }

    private static int readDaysSurvived(Connection con, int saveId) throws Exception {
        String sql = "SELECT day_number FROM saved_time WHERE saved_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Math.max(1, rs.getInt("day_number"));
                }
            }
        }
        return 1;
    }

    private static int readTotalStatusPoints(Connection con, int saveId) throws Exception {
        String sql = """
            SELECT ps.str_stat,
                   ps.agi_stat,
                   ps.dex_stat,
                   ps.vit_stat,
                   ps.hp,
                   ps.stamina
            FROM save_game sg
            JOIN player_stats ps ON sg.player_stats_id = ps.player_stats_id
            WHERE sg.saved_id = ?
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Math.max(0, rs.getInt("str_stat"))
                            + Math.max(0, rs.getInt("agi_stat"))
                            + Math.max(0, rs.getInt("dex_stat"))
                            + Math.max(0, rs.getInt("vit_stat"))
                            + Math.max(0, rs.getInt("hp"))
                            + Math.max(0, rs.getInt("stamina"));
                }
            }
        }
        return 0;
    }

    private static int readTotalSkillLevels(Connection con, int saveId) throws Exception {
        String sql = "SELECT skill_level FROM player_skills WHERE saved_id = ?";
        GameState.PlayerSkills levelCalculator = new GameState.PlayerSkills();
        int totalSkillLevels = 0;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saveId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    totalSkillLevels += levelCalculator.getLevelFromTotalExp(rs.getInt("skill_level"));
                }
            }
        }

        return totalSkillLevels;
    }

    private static boolean hasColumn(Connection con, String tableName, String columnName) throws Exception {
        DatabaseMetaData metaData = con.getMetaData();
        try (ResultSet rs = metaData.getColumns(con.getCatalog(), null, tableName, columnName)) {
            if (rs.next()) {
                return true;
            }
        }
        try (ResultSet rs = metaData.getColumns(con.getCatalog(), null, tableName.toUpperCase(), columnName.toUpperCase())) {
            return rs.next();
        }
    }
}
