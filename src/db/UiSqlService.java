package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Account;

/**
 * Centralized SQL helpers for UI/controllers.
 *
 * Goal: controllers should not keep long-lived DB connections; instead they call
 * these methods which use short-lived connections and try-with-resources.
 */
public final class UiSqlService {

    private UiSqlService() {
    }

    private static Connection open() throws ClassNotFoundException {
        return new dbcon().getConnection();
    }

    // -------- Accounts (admin / management) --------

    public static List<Account> fetchAccountsForAccountView() throws SQLException, ClassNotFoundException {
        String sql = """
            SELECT a.account_id,
                   a.account_name,
                   r.role_name AS user_type,
                   s.status_name AS account_stats,
                   a.created_date
            FROM accounts a
            JOIN user_roles r ON a.role_id = r.role_id
            JOIN account_statuses s ON a.status_id = s.status_id
            ORDER BY a.account_id
            """;

        List<Account> result = new ArrayList<>();
        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new Account(
                    rs.getInt("account_id"),
                    rs.getString("account_name"),
                    rs.getString("user_type"),
                    rs.getString("account_stats"),
                    rs.getString("created_date")
                ));
            }
        }
        return result;
    }

    public static List<Account> fetchAccountsForManagement(String currentAccountName, String namePrefix)
        throws SQLException, ClassNotFoundException {

        String roleLookupSql = """
            SELECT r.role_name AS user_type
            FROM accounts a
            JOIN user_roles r ON a.role_id = r.role_id
            WHERE a.account_name = ?
            LIMIT 1
            """;

        String baseSql = """
            SELECT a.account_name,
                   r.role_name AS user_type,
                   a.email,
                   s.status_name AS account_stats,
                   a.created_date
            FROM accounts a
            JOIN user_roles r ON a.role_id = r.role_id
            JOIN account_statuses s ON a.status_id = s.status_id
            """;

        boolean searching = namePrefix != null && !namePrefix.isBlank();
        String safePrefix = searching ? namePrefix.trim() + "%" : null;

        List<Account> result = new ArrayList<>();

        try (Connection con = open()) {
            String currentRole = null;
            if (currentAccountName != null && !currentAccountName.isBlank()) {
                try (PreparedStatement rolePs = con.prepareStatement(roleLookupSql)) {
                    rolePs.setString(1, currentAccountName.trim());
                    try (ResultSet rs = rolePs.executeQuery()) {
                        if (rs.next()) {
                            currentRole = rs.getString("user_type");
                        }
                    }
                }
            }

            boolean isOwner = "owner".equalsIgnoreCase(currentRole);

            StringBuilder sql = new StringBuilder(baseSql);
            List<Object> params = new ArrayList<>();

            if (!isOwner) {
                sql.append(" WHERE r.role_name = 'user' ");
            }

            if (searching) {
                sql.append(isOwner ? " WHERE " : " AND ");
                sql.append(" a.account_name LIKE ? ");
                params.add(safePrefix);
            }

            sql.append(" ORDER BY a.account_id ");

            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(new Account(
                            rs.getString("account_name"),
                            rs.getString("user_type"),
                            rs.getString("email"),
                            rs.getString("account_stats"),
                            rs.getString("created_date")
                        ));
                    }
                }
            }
        }

        return result;
    }

    public record AccountEditData(String accountName, String roleName, String email, String password) {
    }

    public static AccountEditData fetchAccountEditData(String accountName) throws SQLException, ClassNotFoundException {
        if (accountName == null || accountName.isBlank()) {
            return null;
        }

        String sql = """
            SELECT a.account_name,
                   r.role_name AS user_type,
                   a.email,
                   a.passwords
            FROM accounts a
            JOIN user_roles r ON a.role_id = r.role_id
            WHERE a.account_name = ?
            LIMIT 1
            """;

        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountName.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new AccountEditData(
                    rs.getString("account_name"),
                    rs.getString("user_type"),
                    rs.getString("email"),
                    rs.getString("passwords")
                );
            }
        }
    }

    public static boolean updateAccountStatusByName(String accountName, int statusId) throws SQLException, ClassNotFoundException {
        if (accountName == null || accountName.isBlank() || statusId <= 0) {
            return false;
        }

        String sql = "UPDATE accounts SET status_id = ? WHERE account_name = ?";
        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, statusId);
            ps.setString(2, accountName.trim());
            return ps.executeUpdate() > 0;
        }
    }

    // -------- Auth / signup / password reset --------

    public record AuthRow(int accountId, String accountName, String password, String roleName, String statusName) {
    }

    public static AuthRow fetchAuthRow(String loginInput) throws SQLException, ClassNotFoundException {
        if (loginInput == null || loginInput.isBlank()) {
            return null;
        }

        String sql = """
            SELECT a.account_id,
                   a.account_name,
                   a.passwords,
                   r.role_name,
                   s.status_name
            FROM accounts a
            JOIN user_roles r ON a.role_id = r.role_id
            JOIN account_statuses s ON a.status_id = s.status_id
            WHERE a.account_name = ? OR a.email = ?
            LIMIT 1
            """;

        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, loginInput.trim());
            ps.setString(2, loginInput.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new AuthRow(
                    rs.getInt("account_id"),
                    rs.getString("account_name"),
                    rs.getString("passwords"),
                    rs.getString("role_name"),
                    rs.getString("status_name")
                );
            }
        }
    }

    public static Integer findFirstCharacterIdForAccount(int accountId) throws SQLException, ClassNotFoundException {
        if (accountId <= 0) {
            return null;
        }
        String sql = "SELECT character_id FROM characters WHERE account_id=? ORDER BY character_id LIMIT 1";
        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("character_id") : null;
            }
        }
    }

    public static String findPlayerNameForCharacter(int characterId) throws SQLException, ClassNotFoundException {
        if (characterId <= 0) {
            return null;
        }
        String sql = "SELECT player_name FROM characters WHERE character_id=? LIMIT 1";
        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, characterId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("player_name") : null;
            }
        }
    }

    public static Integer findFirstSaveIdForCharacter(int characterId) throws SQLException, ClassNotFoundException {
        if (characterId <= 0) {
            return null;
        }
        String sql = "SELECT saved_id FROM save_game WHERE character_id=? ORDER BY saved_id LIMIT 1";
        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, characterId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("saved_id") : null;
            }
        }
    }

    public static boolean emailExists(String email) throws SQLException, ClassNotFoundException {
        if (email == null || email.isBlank()) {
            return false;
        }
        String sql = "SELECT 1 FROM accounts WHERE email=? LIMIT 1";
        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static boolean usernameExists(String username) throws SQLException, ClassNotFoundException {
        if (username == null || username.isBlank()) {
            return false;
        }
        String sql = "SELECT 1 FROM accounts WHERE account_name=? LIMIT 1";
        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static int insertAccount(String email, String username, String password, int roleId, int statusId)
        throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO accounts (email, account_name, passwords, role_id, status_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, email == null ? "" : email.trim());
            ps.setString(2, username == null ? "" : username.trim());
            ps.setString(3, password == null ? "" : password);
            ps.setInt(4, roleId);
            ps.setInt(5, statusId);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    public static boolean updatePasswordByEmail(String email, String newPassword) throws SQLException, ClassNotFoundException {
        if (email == null || email.isBlank()) {
            return false;
        }
        String sql = "UPDATE accounts SET passwords=? WHERE email=?";
        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newPassword == null ? "" : newPassword);
            ps.setString(2, email.trim());
            return ps.executeUpdate() > 0;
        }
    }

    // -------- Items (editor screens) --------

    public record ItemRow(
        int itemId,
        String itemName,
        String itemTypeName,
        int price,
        int str,
        int agi,
        int vit,
        int dex,
        int hp,
        int stamina,
        String imageFile
    ) {
    }

    public static List<ItemRow> fetchItems(String namePrefix) throws SQLException, ClassNotFoundException {
        String sqlAll = """
            SELECT i.item_id, i.item_name, i.price,
                   it.item_type_name,
                   s.str_stat AS str,
                   s.agi_stat AS agi,
                   s.vit_stat AS vit,
                   s.dex_stat AS dex,
                   s.hp,
                   s.stamina,
                   i.image
            FROM items i
            JOIN item_stats s ON i.item_stats_id = s.item_stats_id
            LEFT JOIN item_types it ON i.item_type_id = it.item_type_id
            ORDER BY i.item_id
            """;

        String sqlSearch = """
            SELECT i.item_id, i.item_name, i.price,
                   it.item_type_name,
                   s.str_stat AS str,
                   s.agi_stat AS agi,
                   s.vit_stat AS vit,
                   s.dex_stat AS dex,
                   s.hp,
                   s.stamina,
                   i.image
            FROM items i
            JOIN item_stats s ON i.item_stats_id = s.item_stats_id
            LEFT JOIN item_types it ON i.item_type_id = it.item_type_id
            WHERE i.item_name LIKE ?
            ORDER BY i.item_id
            """;

        boolean searching = namePrefix != null && !namePrefix.isBlank();
        List<ItemRow> result = new ArrayList<>();
        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(searching ? sqlSearch : sqlAll)) {
            if (searching) {
                ps.setString(1, namePrefix.trim() + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new ItemRow(
                        rs.getInt("item_id"),
                        rs.getString("item_name"),
                        rs.getString("item_type_name"),
                        rs.getInt("price"),
                        rs.getInt("str"),
                        rs.getInt("agi"),
                        rs.getInt("vit"),
                        rs.getInt("dex"),
                        rs.getInt("hp"),
                        rs.getInt("stamina"),
                        rs.getString("image")
                    ));
                }
            }
        }
        return result;
    }

    public static boolean deleteItemAndStats(int itemId) throws SQLException, ClassNotFoundException {
        if (itemId <= 0) {
            return false;
        }
        String sql = """
            DELETE i, s
            FROM items i
            JOIN item_stats s ON s.item_stats_id = i.item_stats_id
            WHERE i.item_id = ?
            """;
        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            return ps.executeUpdate() > 0;
        }
    }

    public record ItemDetails(
        int itemId,
        String itemName,
        int itemTypeId,
        int price,
        int str,
        int agi,
        int vit,
        int dex,
        int hp,
        int stamina,
        String imageFile
    ) {
    }

    public static ItemDetails fetchItemDetails(int itemId) throws SQLException, ClassNotFoundException {
        if (itemId <= 0) {
            return null;
        }

        String sql = """
            SELECT i.item_id,
                   i.item_name,
                   i.price,
                   i.item_type_id,
                   s.str_stat AS str,
                   s.agi_stat AS agi,
                   s.vit_stat AS vit,
                   s.dex_stat AS dex,
                   s.hp,
                   s.stamina,
                   i.image
            FROM items i
            JOIN item_stats s ON i.item_stats_id = s.item_stats_id
            WHERE i.item_id = ?
            LIMIT 1
            """;

        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new ItemDetails(
                    rs.getInt("item_id"),
                    rs.getString("item_name"),
                    rs.getInt("item_type_id"),
                    rs.getInt("price"),
                    rs.getInt("str"),
                    rs.getInt("agi"),
                    rs.getInt("vit"),
                    rs.getInt("dex"),
                    rs.getInt("hp"),
                    rs.getInt("stamina"),
                    rs.getString("image")
                );
            }
        }
    }

    public static boolean updateItemAndStats(ItemDetails details) throws SQLException, ClassNotFoundException {
        if (details == null || details.itemId() <= 0) {
            return false;
        }

        String sql = """
            UPDATE items i
            JOIN item_stats s ON i.item_stats_id = s.item_stats_id
            SET i.item_name = ?,
                i.price = ?,
                i.item_type_id = ?,
                s.str_stat = ?,
                s.agi_stat = ?,
                s.dex_stat = ?,
                s.vit_stat = ?,
                s.hp = ?,
                s.stamina = ?,
                i.image = ?
            WHERE i.item_id = ?
            """;

        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, details.itemName());
            ps.setInt(2, details.price());
            ps.setInt(3, details.itemTypeId());
            ps.setInt(4, details.str());
            ps.setInt(5, details.agi());
            ps.setInt(6, details.dex());
            ps.setInt(7, details.vit());
            ps.setInt(8, details.hp());
            ps.setInt(9, details.stamina());
            ps.setString(10, details.imageFile());
            ps.setInt(11, details.itemId());
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean insertItemWithStats(
        String itemName,
        int price,
        int itemTypeId,
        int str,
        int agi,
        int dex,
        int vit,
        int hp,
        int stamina,
        String imageFile
    ) throws SQLException, ClassNotFoundException {
        String sqlStats = "INSERT INTO item_stats (str_stat, agi_stat, dex_stat, vit_stat, hp, stamina) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlItem = "INSERT INTO items (item_name, price, item_stats_id, image, item_type_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = open()) {
            con.setAutoCommit(false);
            try {
                int statsId;
                try (PreparedStatement psStats = con.prepareStatement(sqlStats, Statement.RETURN_GENERATED_KEYS)) {
                    psStats.setInt(1, str);
                    psStats.setInt(2, agi);
                    psStats.setInt(3, dex);
                    psStats.setInt(4, vit);
                    psStats.setInt(5, hp);
                    psStats.setInt(6, stamina);
                    psStats.executeUpdate();
                    try (ResultSet keys = psStats.getGeneratedKeys()) {
                        statsId = keys.next() ? keys.getInt(1) : 0;
                    }
                }

                if (statsId <= 0) {
                    throw new SQLException("Failed to create item_stats row.");
                }

                try (PreparedStatement psItem = con.prepareStatement(sqlItem)) {
                    psItem.setString(1, itemName == null ? "" : itemName.trim());
                    psItem.setInt(2, price);
                    psItem.setInt(3, statsId);
                    psItem.setString(4, imageFile);
                    psItem.setInt(5, itemTypeId);
                    psItem.executeUpdate();
                }

                con.commit();
                return true;
            } catch (Exception e) {
                try {
                    con.rollback();
                } catch (Exception ignored) {
                }
                if (e instanceof SQLException se) {
                    throw se;
                }
                throw new SQLException("Insert failed.", e);
            } finally {
                try {
                    con.setAutoCommit(true);
                } catch (Exception ignored) {
                }
            }
        }
    }
}
