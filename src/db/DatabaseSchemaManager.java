package db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseSchemaManager {

    private static final String SCRIPT_RESOURCE = "/db/New.sql";
    private static final String SCRIPT_START_MARKER = "-- codex:schema-bootstrap:start";
    private static final String SCRIPT_END_MARKER = "-- codex:schema-bootstrap:end";

    private static boolean ensured = false;

    private DatabaseSchemaManager() {
    }

    public static void ensureSchema(Connection con) throws SQLException {
        if (con == null) {
            return;
        }

        synchronized (DatabaseSchemaManager.class) {
            if (ensured) {
                return;
            }

            executeBootstrapSection(con);
            ensureSaveGameTutorialColumn(con);
            ensureSaveGameAudioColumns(con);
            ensured = true;
        }
    }

    private static void ensureSaveGameTutorialColumn(Connection con) throws SQLException {
        if (hasColumn(con, "save_game", "home_interior_tutorial_seen")) {
        } else {
            execute(con, """
                ALTER TABLE save_game
                ADD COLUMN home_interior_tutorial_seen TINYINT(1) NOT NULL DEFAULT 0
                """);
        }

        if (!hasColumn(con, "save_game", "tutorial_flags")) {
            execute(con, """
                ALTER TABLE save_game
                ADD COLUMN tutorial_flags VARCHAR(500) NOT NULL DEFAULT ''
                """);
        }
    }

    private static void ensureSaveGameAudioColumns(Connection con) throws SQLException {
        if (!hasColumn(con, "save_game", "bgm_volume")) {
            execute(con, """
                ALTER TABLE save_game
                ADD COLUMN bgm_volume DOUBLE NOT NULL DEFAULT 0.5
                """);
        }

        if (!hasColumn(con, "save_game", "sfx_volume")) {
            execute(con, """
                ALTER TABLE save_game
                ADD COLUMN sfx_volume DOUBLE NOT NULL DEFAULT 1.0
                """);
        }
    }

    private static void executeBootstrapSection(Connection con) throws SQLException {
        String sql = loadBootstrapSection();
        executeScript(con, sql);
    }

    private static String loadBootstrapSection() {
        try (InputStream input = DatabaseSchemaManager.class.getResourceAsStream(SCRIPT_RESOURCE)) {
            if (input == null) {
                throw new IllegalStateException("Schema script resource not found: " + SCRIPT_RESOURCE);
            }

            String script = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            int start = script.indexOf(SCRIPT_START_MARKER);
            int end = script.indexOf(SCRIPT_END_MARKER);
            if (start < 0 || end < 0 || end <= start) {
                System.out.println("Schema bootstrap markers not found in " + SCRIPT_RESOURCE + "; skipping bootstrap section.");
                return "";
            }

            return script.substring(start + SCRIPT_START_MARKER.length(), end);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read schema script: " + SCRIPT_RESOURCE, e);
        }
    }

    private static void executeScript(Connection con, String script) throws SQLException {
        String delimiter = ";";
        StringBuilder statement = new StringBuilder();

        for (String line : script.split("\\R")) {
            String trimmed = line.trim();

            if (trimmed.isEmpty()) {
                continue;
            }

            if (trimmed.startsWith("--")) {
                continue;
            }

            if (trimmed.toUpperCase().startsWith("DELIMITER ")) {
                delimiter = trimmed.substring("DELIMITER ".length()).trim();
                continue;
            }

            statement.append(line).append('\n');
            if (endsWithDelimiter(statement, delimiter)) {
                String sql = stripTrailingDelimiter(statement, delimiter).trim();
                if (!sql.isEmpty()) {
                    execute(con, sql);
                }
                statement.setLength(0);
            }
        }

        String trailing = statement.toString().trim();
        if (!trailing.isEmpty()) {
            execute(con, trailing);
        }
    }

    private static boolean endsWithDelimiter(StringBuilder statement, String delimiter) {
        String trimmed = statement.toString().trim();
        return trimmed.endsWith(delimiter);
    }

    private static String stripTrailingDelimiter(StringBuilder statement, String delimiter) {
        String sql = statement.toString().trim();
        return sql.substring(0, sql.length() - delimiter.length());
    }

    private static void execute(Connection con, String sql) throws SQLException {
        try (Statement stmt = con.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static boolean hasColumn(Connection con, String tableName, String columnName) throws SQLException {
        String sql = """
            SELECT 1
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = ?
              AND COLUMN_NAME = ?
            LIMIT 1
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tableName);
            ps.setString(2, columnName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
