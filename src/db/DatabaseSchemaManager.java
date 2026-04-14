package db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseSchemaManager {

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

            installProcedures(con);
            callProcedure(con, "ensure_application_schema");
            ensured = true;
        }
    }

    private static void installProcedures(Connection con) throws SQLException {
        execute(con, "DROP PROCEDURE IF EXISTS ensure_application_schema");
        execute(con, "DROP PROCEDURE IF EXISTS ensure_chicken_corn_soup_deluxe");
        execute(con, "DROP PROCEDURE IF EXISTS ensure_saved_unlocked_recipe_table");
        execute(con, "DROP PROCEDURE IF EXISTS ensure_saved_farm_plot_defaults");
        execute(con, "DROP PROCEDURE IF EXISTS ensure_saved_tree_resource_table");

        execute(con, """
            CREATE PROCEDURE ensure_chicken_corn_soup_deluxe()
            BEGIN
                DECLARE v_target_name VARCHAR(100) DEFAULT 'chicken_corn_soup_deluxe';
                DECLARE v_existing_item_id INT DEFAULT NULL;
                DECLARE v_new_item_stats_id INT DEFAULT NULL;
                DECLARE v_fallback_type_id INT DEFAULT 1;

                START TRANSACTION;

                SELECT item_id
                  INTO v_existing_item_id
                  FROM items
                 WHERE LOWER(REPLACE(item_name, '_', ' ')) = LOWER(REPLACE(v_target_name, '_', ' '))
                 LIMIT 1;

                IF v_existing_item_id IS NULL THEN
                    INSERT INTO item_stats (str_stat, agi_stat, dex_stat, vit_stat, hp, stamina)
                    VALUES (0, 0, 0, 0, 0, 0);

                    SET v_new_item_stats_id = LAST_INSERT_ID();

                    SELECT COALESCE(MIN(item_type_id), 1)
                      INTO v_fallback_type_id
                      FROM item_types;

                    INSERT INTO items (item_name, price, item_stats_id, image, item_type_id)
                    VALUES (v_target_name, 120, v_new_item_stats_id, 'chicken_corn_soup.png', v_fallback_type_id);
                END IF;

                COMMIT;
            END
            """);

        execute(con, """
            CREATE PROCEDURE ensure_saved_unlocked_recipe_table()
            BEGIN
                CREATE TABLE IF NOT EXISTS saved_unlocked_recipe (
                    saved_id INT NOT NULL,
                    recipe_key VARCHAR(100) NOT NULL,
                    PRIMARY KEY (saved_id, recipe_key),
                    CONSTRAINT fk_saved_unlocked_recipe_save
                        FOREIGN KEY (saved_id) REFERENCES save_game (saved_id) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            END
            """);

        execute(con, """
            CREATE PROCEDURE ensure_saved_tree_resource_table()
            BEGIN
                CREATE TABLE IF NOT EXISTS saved_tree_resource (
                    saved_id INT NOT NULL,
                    map_key VARCHAR(50) NOT NULL,
                    tree_id VARCHAR(50) NOT NULL,
                    tree_type VARCHAR(10) NOT NULL,
                    current_stage INT NOT NULL DEFAULT '2',
                    lived_days INT NOT NULL DEFAULT '0',
                    hp INT NOT NULL DEFAULT '0',
                    vit INT NOT NULL DEFAULT '0',
                    respawn_at_total_seconds DOUBLE NOT NULL DEFAULT '-1',
                    last_updated_day_number INT NOT NULL DEFAULT '1',
                    PRIMARY KEY (saved_id, map_key, tree_id),
                    CONSTRAINT fk_saved_tree_resource_save
                        FOREIGN KEY (saved_id) REFERENCES save_game (saved_id) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            END
            """);

        execute(con, """
            CREATE PROCEDURE ensure_saved_farm_plot_defaults()
            BEGIN
                DECLARE v_has_table INT DEFAULT 0;
                DECLARE v_required_columns INT DEFAULT 0;

                SELECT COUNT(*)
                  INTO v_has_table
                  FROM information_schema.tables
                 WHERE table_schema = DATABASE()
                   AND table_name = 'saved_farm_plot';

                IF v_has_table > 0 THEN
                    SELECT COUNT(*)
                      INTO v_required_columns
                      FROM information_schema.columns
                     WHERE table_schema = DATABASE()
                       AND table_name = 'saved_farm_plot'
                       AND column_name IN (
                           'total_growth_seconds',
                           'water_cooldown_seconds',
                           'water_reduction_seconds'
                       );

                    IF v_required_columns = 3 THEN
                        ALTER TABLE saved_farm_plot
                            MODIFY COLUMN total_growth_seconds DOUBLE NOT NULL DEFAULT '3600',
                            MODIFY COLUMN water_cooldown_seconds DOUBLE NOT NULL DEFAULT '900',
                            MODIFY COLUMN water_reduction_seconds DOUBLE NOT NULL DEFAULT '300';
                    END IF;
                END IF;
            END
            """);

        execute(con, """
            CREATE PROCEDURE ensure_application_schema()
            BEGIN
                CALL ensure_saved_unlocked_recipe_table();
                CALL ensure_saved_tree_resource_table();
                CALL ensure_saved_farm_plot_defaults();
                CALL ensure_chicken_corn_soup_deluxe();
            END
            """);
    }

    private static void callProcedure(Connection con, String procedureName) throws SQLException {
        execute(con, "CALL " + procedureName + "()");
    }

    private static void execute(Connection con, String sql) throws SQLException {
        try (Statement stmt = con.createStatement()) {
            stmt.execute(sql);
        }
    }
}
