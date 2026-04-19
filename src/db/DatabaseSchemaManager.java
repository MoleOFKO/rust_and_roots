package db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
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
            ensured = true;
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
                throw new IllegalStateException(
                    "Schema bootstrap markers not found in " + SCRIPT_RESOURCE
                );
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
}
