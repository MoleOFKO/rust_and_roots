package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import model.mobs;

public final class MobDefinitionRepository {

    private static final String SELECT_BASE = """
        SELECT mobs_id,
               mobs_name,
               mobs_type,
               str_stat,
               agi_stat,
               vit_stat,
               dex_stat,
               hp,
               stamina,
               image
        FROM mobs
        """;

    public List<mobs> findAll() throws ClassNotFoundException, SQLException {
        try (Connection con = new dbcon().getConnection()) {
            ensureSchema(con);
            try (PreparedStatement ps = con.prepareStatement(SELECT_BASE + " ORDER BY mobs_id");
                 ResultSet rs = ps.executeQuery()) {
                return readMobList(rs);
            }
        }
    }

    public List<mobs> searchByName(String prefix) throws ClassNotFoundException, SQLException {
        try (Connection con = new dbcon().getConnection()) {
            ensureSchema(con);
            try (PreparedStatement ps = con.prepareStatement(SELECT_BASE + " WHERE mobs_name LIKE ? ORDER BY mobs_id")) {
            ps.setString(1, ((prefix == null) ? "" : prefix.trim()) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                return readMobList(rs);
            }
            }
        }
    }

    public mobs findById(int mobsId) throws ClassNotFoundException, SQLException {
        try (Connection con = new dbcon().getConnection()) {
            ensureSchema(con);
            try (PreparedStatement ps = con.prepareStatement(SELECT_BASE + " WHERE mobs_id = ?")) {
            ps.setInt(1, mobsId);
            try (ResultSet rs = ps.executeQuery()) {
                List<mobs> results = readMobList(rs);
                return results.isEmpty() ? null : results.get(0);
            }
            }
        }
    }

    public Map<String, mobs> findAllByTypeKey() throws ClassNotFoundException, SQLException {
        Map<String, mobs> byType = new LinkedHashMap<>();
        for (mobs mob : findAll()) {
            if (mob == null || mob.getMobs_type() == null || mob.getMobs_type().isBlank()) {
                continue;
            }
            byType.put(normalizeType(mob.getMobs_type()), mob);
        }
        return byType;
    }

    public void update(mobs mob) throws ClassNotFoundException, SQLException {
        String sql = """
            UPDATE mobs
            SET mobs_name = ?,
                mobs_type = ?,
                str_stat = ?,
                agi_stat = ?,
                vit_stat = ?,
                dex_stat = ?,
                hp = ?,
                stamina = ?,
                image = ?
            WHERE mobs_id = ?
            """;

        try (Connection con = new dbcon().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ensureSchema(con);
            bindMob(ps, mob, false);
            ps.setInt(10, mob.getMobs_id());
            ps.executeUpdate();
        }
    }

    public void insert(mobs mob) throws ClassNotFoundException, SQLException {
        String sql = """
            INSERT INTO mobs
                (mobs_name, mobs_type, str_stat, agi_stat, vit_stat, dex_stat, hp, stamina, image)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection con = new dbcon().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ensureSchema(con);
            bindMob(ps, mob, true);
            ps.executeUpdate();
        }
    }

    public void delete(int mobsId) throws ClassNotFoundException, SQLException {
        try (Connection con = new dbcon().getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM mobs WHERE mobs_id = ?")) {
            ensureSchema(con);
            ps.setInt(1, mobsId);
            ps.executeUpdate();
        }
    }

    private void ensureSchema(Connection con) throws SQLException {
        if (con == null) {
            return;
        }

        try (PreparedStatement create = con.prepareStatement("""
            CREATE TABLE IF NOT EXISTS mobs (
                mobs_id INT NOT NULL AUTO_INCREMENT,
                mobs_name VARCHAR(100) NOT NULL,
                mobs_type VARCHAR(50) NOT NULL,
                str_stat INT NOT NULL DEFAULT 0,
                agi_stat INT NOT NULL DEFAULT 0,
                vit_stat INT NOT NULL DEFAULT 0,
                dex_stat INT NOT NULL DEFAULT 0,
                hp INT NOT NULL DEFAULT 0,
                stamina INT NOT NULL DEFAULT 0,
                image VARCHAR(255) DEFAULT NULL,
                PRIMARY KEY (mobs_id),
                UNIQUE KEY uq_mobs_mobs_type (mobs_type)
            )
            """)) {
            create.executeUpdate();
        }

        try (PreparedStatement seed = con.prepareStatement("""
            INSERT INTO mobs (mobs_name, mobs_type, str_stat, agi_stat, vit_stat, dex_stat, hp, stamina, image)
            VALUES
                ('Slime', 'slime', 3, 2, 6, 1, 10, 30, 'mob/Slime/Slime1_Idle_with_shadow.png'),
                ('Poison Slime', 'poison_slime', 6, 4, 12, 2, 20, 60, 'mob/Slime/Slime2_Idle_with_shadow.png'),
                ('Lava Slime', 'lava_slime', 6, 4, 12, 2, 20, 60, 'mob/Slime/Slime3_Idle_with_shadow.png'),
                ('Orc', 'orc', 4, 3, 3, 1, 20, 25, 'mob/Orc/orc1_idle_with_shadow.png'),
                ('Orc Berserker', 'orcb', 12, 9, 9, 3, 60, 75, 'mob/Orc/orc3_idle_with_shadow.png')
            ON DUPLICATE KEY UPDATE
                mobs_name = VALUES(mobs_name)
            """)) {
            seed.executeUpdate();
        }
    }

    private List<mobs> readMobList(ResultSet rs) throws SQLException {
        List<mobs> results = new ArrayList<>();
        while (rs.next()) {
            String imagePath = rs.getString("image");
            javafx.scene.image.Image image = controller.MobEditorSupport.loadMobImage(imagePath);
            javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView();
            imageView.setImage(image);
            imageView.setFitHeight(50);
            imageView.setFitWidth(50);
            imageView.setPreserveRatio(true);

            mobs mob = new mobs(
                rs.getInt("mobs_id"),
                rs.getString("mobs_name"),
                rs.getString("mobs_type"),
                rs.getInt("str_stat"),
                rs.getInt("agi_stat"),
                rs.getInt("vit_stat"),
                rs.getInt("dex_stat"),
                rs.getInt("hp"),
                rs.getInt("stamina"),
                imageView,
                imagePath
            );
            results.add(mob);
        }
        return results;
    }

    private void bindMob(PreparedStatement ps, mobs mob, boolean insert) throws SQLException {
        if (mob == null) {
            throw new SQLException("Mob data is missing.");
        }
        ps.setString(1, safeTrim(mob.getMobs_name()));
        ps.setString(2, normalizeType(mob.getMobs_type()));
        ps.setInt(3, Math.max(0, mob.getStr()));
        ps.setInt(4, Math.max(0, mob.getAgi()));
        ps.setInt(5, Math.max(0, mob.getVid()));
        ps.setInt(6, Math.max(0, mob.getDex()));
        ps.setInt(7, Math.max(0, mob.getHp()));
        ps.setInt(8, Math.max(0, mob.getStimana()));
        ps.setString(9, safeTrim(mob.getImagePath()));
    }

    private static String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    public static String normalizeType(String value) {
        return safeTrim(value).toLowerCase(Locale.ROOT);
    }
}
