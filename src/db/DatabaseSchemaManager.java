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
        execute(con, "DROP PROCEDURE IF EXISTS ensure_saved_tree_drop_table");
        execute(con, "DROP PROCEDURE IF EXISTS ensure_saved_mob_table");
        execute(con, "DROP PROCEDURE IF EXISTS ensure_saved_mob_drop_table");

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
                DECLARE v_missing_max_hp INT DEFAULT 0;
                DECLARE v_missing_drop_rate_pity INT DEFAULT 0;
                DECLARE v_missing_drops_this_life INT DEFAULT 0;
                DECLARE v_missing_damage_since_last_drop_roll INT DEFAULT 0;
                DECLARE v_missing_last_hp_growth_total_seconds INT DEFAULT 0;
                DECLARE v_tree_type_len INT DEFAULT 0;

                CREATE TABLE IF NOT EXISTS saved_tree_resource (
                    saved_id INT NOT NULL,
                    map_key VARCHAR(50) NOT NULL,
                    tree_id VARCHAR(50) NOT NULL,
                    tree_type VARCHAR(50) NOT NULL,
                    current_stage INT NOT NULL DEFAULT '2',
                    lived_days INT NOT NULL DEFAULT '0',
                    hp INT NOT NULL DEFAULT '0',
                    max_hp INT NOT NULL DEFAULT '0',
                    vit INT NOT NULL DEFAULT '0',
                    drop_rate_pity INT NOT NULL DEFAULT '0',
                    drops_this_life INT NOT NULL DEFAULT '0',
                    damage_since_last_drop_roll INT NOT NULL DEFAULT '0',
                    last_hp_growth_total_seconds DOUBLE NOT NULL DEFAULT '-1',
                    respawn_at_total_seconds DOUBLE NOT NULL DEFAULT '-1',
                    last_updated_day_number INT NOT NULL DEFAULT '1',
                    PRIMARY KEY (saved_id, map_key, tree_id),
                    CONSTRAINT fk_saved_tree_resource_save
                        FOREIGN KEY (saved_id) REFERENCES save_game (saved_id) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

                SELECT COALESCE(CHARACTER_MAXIMUM_LENGTH, 0)
                  INTO v_tree_type_len
                  FROM information_schema.columns
                 WHERE table_schema = DATABASE()
                   AND table_name = 'saved_tree_resource'
                   AND column_name = 'tree_type'
                 LIMIT 1;
                IF v_tree_type_len IS NULL OR v_tree_type_len < 50 THEN
                    ALTER TABLE saved_tree_resource
                        MODIFY COLUMN tree_type VARCHAR(50) NOT NULL;
                END IF;

                SELECT COUNT(*)
                  INTO v_missing_max_hp
                  FROM information_schema.columns
                 WHERE table_schema = DATABASE()
                   AND table_name = 'saved_tree_resource'
                   AND column_name = 'max_hp';
                IF v_missing_max_hp = 0 THEN
                    ALTER TABLE saved_tree_resource
                        ADD COLUMN max_hp INT NOT NULL DEFAULT '0' AFTER hp;
                END IF;

                SELECT COUNT(*)
                  INTO v_missing_drop_rate_pity
                  FROM information_schema.columns
                 WHERE table_schema = DATABASE()
                   AND table_name = 'saved_tree_resource'
                   AND column_name = 'drop_rate_pity';
                IF v_missing_drop_rate_pity = 0 THEN
                    ALTER TABLE saved_tree_resource
                        ADD COLUMN drop_rate_pity INT NOT NULL DEFAULT '0' AFTER vit;
                END IF;

                SELECT COUNT(*)
                  INTO v_missing_drops_this_life
                  FROM information_schema.columns
                 WHERE table_schema = DATABASE()
                   AND table_name = 'saved_tree_resource'
                   AND column_name = 'drops_this_life';
                IF v_missing_drops_this_life = 0 THEN
                    ALTER TABLE saved_tree_resource
                        ADD COLUMN drops_this_life INT NOT NULL DEFAULT '0' AFTER drop_rate_pity;
                END IF;

                SELECT COUNT(*)
                  INTO v_missing_damage_since_last_drop_roll
                  FROM information_schema.columns
                 WHERE table_schema = DATABASE()
                   AND table_name = 'saved_tree_resource'
                   AND column_name = 'damage_since_last_drop_roll';
                IF v_missing_damage_since_last_drop_roll = 0 THEN
                    ALTER TABLE saved_tree_resource
                        ADD COLUMN damage_since_last_drop_roll INT NOT NULL DEFAULT '0' AFTER drops_this_life;
                END IF;

                SELECT COUNT(*)
                  INTO v_missing_last_hp_growth_total_seconds
                  FROM information_schema.columns
                 WHERE table_schema = DATABASE()
                   AND table_name = 'saved_tree_resource'
                   AND column_name = 'last_hp_growth_total_seconds';
                IF v_missing_last_hp_growth_total_seconds = 0 THEN
                    ALTER TABLE saved_tree_resource
                        ADD COLUMN last_hp_growth_total_seconds DOUBLE NOT NULL DEFAULT '-1' AFTER damage_since_last_drop_roll;
                END IF;
            END
            """);

        execute(con, """
            CREATE PROCEDURE ensure_saved_tree_drop_table()
            BEGIN
                DECLARE v_missing_quantity INT DEFAULT 0;
                DECLARE v_missing_spawned_at_total_seconds INT DEFAULT 0;
                DECLARE v_missing_expires_at_total_seconds INT DEFAULT 0;

                CREATE TABLE IF NOT EXISTS saved_tree_drop (
                    saved_id INT NOT NULL,
                    map_key VARCHAR(50) NOT NULL,
                    tree_id VARCHAR(50) NOT NULL,
                    drop_slot INT NOT NULL,
                    item_id INT NOT NULL,
                    quantity INT NOT NULL DEFAULT '1',
                    spawned_at_total_seconds DOUBLE NOT NULL DEFAULT '0',
                    expires_at_total_seconds DOUBLE NOT NULL DEFAULT '0',
                    PRIMARY KEY (saved_id, map_key, tree_id, drop_slot),
                    KEY idx_saved_tree_drop_item_id (item_id),
                    CONSTRAINT fk_saved_tree_drop_save
                        FOREIGN KEY (saved_id) REFERENCES save_game (saved_id) ON DELETE CASCADE,
                    CONSTRAINT fk_saved_tree_drop_item
                        FOREIGN KEY (item_id) REFERENCES items (item_id) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

                SELECT COUNT(*)
                  INTO v_missing_quantity
                  FROM information_schema.columns
                 WHERE table_schema = DATABASE()
                   AND table_name = 'saved_tree_drop'
                   AND column_name = 'quantity';
                IF v_missing_quantity = 0 THEN
                    ALTER TABLE saved_tree_drop
                        ADD COLUMN quantity INT NOT NULL DEFAULT '1' AFTER item_id;
                END IF;

                SELECT COUNT(*)
                  INTO v_missing_spawned_at_total_seconds
                  FROM information_schema.columns
                 WHERE table_schema = DATABASE()
                   AND table_name = 'saved_tree_drop'
                   AND column_name = 'spawned_at_total_seconds';
                IF v_missing_spawned_at_total_seconds = 0 THEN
                    ALTER TABLE saved_tree_drop
                        ADD COLUMN spawned_at_total_seconds DOUBLE NOT NULL DEFAULT '0' AFTER quantity;
                END IF;

                SELECT COUNT(*)
                  INTO v_missing_expires_at_total_seconds
                  FROM information_schema.columns
                 WHERE table_schema = DATABASE()
                   AND table_name = 'saved_tree_drop'
                   AND column_name = 'expires_at_total_seconds';
                IF v_missing_expires_at_total_seconds = 0 THEN
                    ALTER TABLE saved_tree_drop
                        ADD COLUMN expires_at_total_seconds DOUBLE NOT NULL DEFAULT '0' AFTER spawned_at_total_seconds;
                END IF;
            END
            """);

        execute(con, """
            CREATE PROCEDURE ensure_saved_mob_table()
            BEGIN
                DECLARE v_missing_max_hp INT DEFAULT 0;
                DECLARE v_missing_max_sp INT DEFAULT 0;
                DECLARE v_missing_dead INT DEFAULT 0;
                DECLARE v_missing_layout_x INT DEFAULT 0;
                DECLARE v_missing_layout_y INT DEFAULT 0;
                DECLARE v_missing_last_regen_total_seconds INT DEFAULT 0;
                DECLARE v_missing_last_updated_day_number INT DEFAULT 0;
                DECLARE v_mob_type_len INT DEFAULT 0;

                CREATE TABLE IF NOT EXISTS saved_mob (
                    saved_id INT NOT NULL,
                    map_key VARCHAR(50) NOT NULL,
                    mob_id VARCHAR(50) NOT NULL,
                    mob_type VARCHAR(50) NOT NULL,
                    lived_days INT NOT NULL DEFAULT '0',
                    hp INT NOT NULL DEFAULT '0',
                    max_hp INT NOT NULL DEFAULT '0',
                    sp INT NOT NULL DEFAULT '0',
                    max_sp INT NOT NULL DEFAULT '0',
                    str_stat INT NOT NULL DEFAULT '0',
                    agi_stat INT NOT NULL DEFAULT '0',
                    vit_stat INT NOT NULL DEFAULT '0',
                    dex_stat INT NOT NULL DEFAULT '0',
                    dead TINYINT(1) NOT NULL DEFAULT '0',
                    layout_x DOUBLE NOT NULL DEFAULT '0',
                    layout_y DOUBLE NOT NULL DEFAULT '0',
                    last_regen_total_seconds DOUBLE NOT NULL DEFAULT '-1',
                    last_updated_day_number INT NOT NULL DEFAULT '1',
                    PRIMARY KEY (saved_id, map_key, mob_id),
                    CONSTRAINT fk_saved_mob_save
                        FOREIGN KEY (saved_id) REFERENCES save_game (saved_id) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

                SELECT COALESCE(CHARACTER_MAXIMUM_LENGTH, 0)
                  INTO v_mob_type_len
                  FROM information_schema.columns
                 WHERE table_schema = DATABASE()
                   AND table_name = 'saved_mob'
                   AND column_name = 'mob_type'
                 LIMIT 1;
                IF v_mob_type_len IS NULL OR v_mob_type_len < 50 THEN
                    ALTER TABLE saved_mob
                        MODIFY COLUMN mob_type VARCHAR(50) NOT NULL;
                END IF;

                SELECT COUNT(*)
                  INTO v_missing_max_hp
                  FROM information_schema.columns
                 WHERE table_schema = DATABASE()
                   AND table_name = 'saved_mob'
                   AND column_name = 'max_hp';
                IF v_missing_max_hp = 0 THEN
                    ALTER TABLE saved_mob
                        ADD COLUMN max_hp INT NOT NULL DEFAULT '0' AFTER hp;
                END IF;

                SELECT COUNT(*)
                  INTO v_missing_max_sp
                  FROM information_schema.columns
                 WHERE table_schema = DATABASE()
                   AND table_name = 'saved_mob'
                   AND column_name = 'max_sp';
                IF v_missing_max_sp = 0 THEN
                    ALTER TABLE saved_mob
                        ADD COLUMN max_sp INT NOT NULL DEFAULT '0' AFTER sp;
                END IF;

                SELECT COUNT(*)
                  INTO v_missing_dead
                  FROM information_schema.columns
                 WHERE table_schema = DATABASE()
                   AND table_name = 'saved_mob'
                   AND column_name = 'dead';
                IF v_missing_dead = 0 THEN
                    ALTER TABLE saved_mob
                        ADD COLUMN dead TINYINT(1) NOT NULL DEFAULT '0' AFTER dex_stat;
                END IF;

                SELECT COUNT(*)
                  INTO v_missing_layout_x
                  FROM information_schema.columns
                 WHERE table_schema = DATABASE()
                   AND table_name = 'saved_mob'
                   AND column_name = 'layout_x';
                IF v_missing_layout_x = 0 THEN
                    ALTER TABLE saved_mob
                        ADD COLUMN layout_x DOUBLE NOT NULL DEFAULT '0' AFTER dead;
                END IF;

                SELECT COUNT(*)
                  INTO v_missing_layout_y
                  FROM information_schema.columns
                 WHERE table_schema = DATABASE()
                   AND table_name = 'saved_mob'
                   AND column_name = 'layout_y';
                IF v_missing_layout_y = 0 THEN
                    ALTER TABLE saved_mob
                        ADD COLUMN layout_y DOUBLE NOT NULL DEFAULT '0' AFTER layout_x;
                END IF;

                SELECT COUNT(*)
                  INTO v_missing_last_regen_total_seconds
                  FROM information_schema.columns
                 WHERE table_schema = DATABASE()
                   AND table_name = 'saved_mob'
                   AND column_name = 'last_regen_total_seconds';
                IF v_missing_last_regen_total_seconds = 0 THEN
                    ALTER TABLE saved_mob
                        ADD COLUMN last_regen_total_seconds DOUBLE NOT NULL DEFAULT '-1' AFTER layout_y;
                END IF;

                SELECT COUNT(*)
                  INTO v_missing_last_updated_day_number
                  FROM information_schema.columns
                 WHERE table_schema = DATABASE()
                   AND table_name = 'saved_mob'
                   AND column_name = 'last_updated_day_number';
                IF v_missing_last_updated_day_number = 0 THEN
                    ALTER TABLE saved_mob
                        ADD COLUMN last_updated_day_number INT NOT NULL DEFAULT '1' AFTER last_regen_total_seconds;
                END IF;
            END
            """);

        execute(con, """
            CREATE PROCEDURE ensure_saved_mob_drop_table()
            BEGIN
                DECLARE v_missing_quantity INT DEFAULT 0;
                DECLARE v_missing_spawned_at_total_seconds INT DEFAULT 0;
                DECLARE v_missing_expires_at_total_seconds INT DEFAULT 0;

                CREATE TABLE IF NOT EXISTS saved_mob_drop (
                    saved_id INT NOT NULL,
                    map_key VARCHAR(50) NOT NULL,
                    mob_id VARCHAR(50) NOT NULL,
                    drop_slot INT NOT NULL,
                    item_id INT NOT NULL,
                    quantity INT NOT NULL DEFAULT '1',
                    spawned_at_total_seconds DOUBLE NOT NULL DEFAULT '0',
                    expires_at_total_seconds DOUBLE NOT NULL DEFAULT '0',
                    PRIMARY KEY (saved_id, map_key, mob_id, drop_slot),
                    KEY idx_saved_mob_drop_item_id (item_id),
                    CONSTRAINT fk_saved_mob_drop_save
                        FOREIGN KEY (saved_id) REFERENCES save_game (saved_id) ON DELETE CASCADE,
                    CONSTRAINT fk_saved_mob_drop_item
                        FOREIGN KEY (item_id) REFERENCES items (item_id) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

                SELECT COUNT(*)
                  INTO v_missing_quantity
                  FROM information_schema.columns
                 WHERE table_schema = DATABASE()
                   AND table_name = 'saved_mob_drop'
                   AND column_name = 'quantity';
                IF v_missing_quantity = 0 THEN
                    ALTER TABLE saved_mob_drop
                        ADD COLUMN quantity INT NOT NULL DEFAULT '1' AFTER item_id;
                END IF;

                SELECT COUNT(*)
                  INTO v_missing_spawned_at_total_seconds
                  FROM information_schema.columns
                 WHERE table_schema = DATABASE()
                   AND table_name = 'saved_mob_drop'
                   AND column_name = 'spawned_at_total_seconds';
                IF v_missing_spawned_at_total_seconds = 0 THEN
                    ALTER TABLE saved_mob_drop
                        ADD COLUMN spawned_at_total_seconds DOUBLE NOT NULL DEFAULT '0' AFTER quantity;
                END IF;

                SELECT COUNT(*)
                  INTO v_missing_expires_at_total_seconds
                  FROM information_schema.columns
                 WHERE table_schema = DATABASE()
                   AND table_name = 'saved_mob_drop'
                   AND column_name = 'expires_at_total_seconds';
                IF v_missing_expires_at_total_seconds = 0 THEN
                    ALTER TABLE saved_mob_drop
                        ADD COLUMN expires_at_total_seconds DOUBLE NOT NULL DEFAULT '0' AFTER spawned_at_total_seconds;
                END IF;
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
                CALL ensure_saved_tree_drop_table();
                CALL ensure_saved_mob_table();
                CALL ensure_saved_mob_drop_table();
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
