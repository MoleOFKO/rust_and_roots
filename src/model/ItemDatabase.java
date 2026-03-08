/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import db.dbcon;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.image.Image;

public final class ItemDatabase {

    private static final List<item> allItems = new ArrayList<>();
    private static final Map<Integer, item> itemById = new LinkedHashMap<>();
    private static final Map<String, item> itemByName = new HashMap<>();
    private static final Map<Integer, String> imageFileById = new HashMap<>();
    private static final Map<Integer, Image> imageCache = new HashMap<>();

    private static boolean loaded = false;

    private ItemDatabase() {
    }

    public static void loadItems() {
        if (!loaded) {
            reloadItems();
        }
    }

    public static void reloadItems() {
        allItems.clear();
        itemById.clear();
        itemByName.clear();
        imageFileById.clear();
        imageCache.clear();

        String sql =
            "SELECT i.item_id, i.item_name, i.price, i.image, " +
            "       COALESCE(s.str, 0) AS str, " +
            "       COALESCE(s.agi, 0) AS agi, " +
            "       COALESCE(s.dex, 0) AS dex, " +
            "       COALESCE(s.vit, 0) AS vit, " +
            "       COALESCE(s.hp, 0) AS hp, " +
            "       COALESCE(s.stamina, 0) AS stamina " +
            "FROM items i " +
            "LEFT JOIN stats s ON i.stats_id = s.stats_id " +
            "ORDER BY i.item_id";

        dbcon db = new dbcon();

        try (
            Connection con = db.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                int itemId = rs.getInt("item_id");
                String itemName = rs.getString("item_name");
                int price = rs.getInt("price");

                item itm = new item(itemId, itemName, price);

                itm.setStr(rs.getInt("str"));
                itm.setAgi(rs.getInt("agi"));
                itm.setDex(rs.getInt("dex"));
                itm.setVid(rs.getInt("vit"));
                itm.setHp(rs.getInt("hp"));
                itm.setStimana(rs.getInt("stamina"));

                allItems.add(itm);
                itemById.put(itemId, itm);
                itemByName.put(itemName.toLowerCase().trim(), itm);

                String imageFile = rs.getString("image");
                imageFileById.put(itemId, imageFile);
            }

            loaded = true;
            System.out.println("Loaded items: " + allItems.size());

        } catch (Exception e) {
            loaded = false;
            e.printStackTrace();
        }
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static List<item> getAllItems() {
        return Collections.unmodifiableList(allItems);
    }

    public static item getItemById(int itemId) {
        return itemById.get(itemId);
    }

    public static item getItemByName(String itemName) {
        if (itemName == null) return null;
        return itemByName.get(itemName.toLowerCase().trim());
    }

    public static boolean containsItem(int itemId) {
        return itemById.containsKey(itemId);
    }

    public static String getImageFileName(int itemId) {
        return imageFileById.get(itemId);
    }

    public static Image getImage(int itemId) {
        if (imageCache.containsKey(itemId)) {
            return imageCache.get(itemId);
        }

        String fileName = imageFileById.get(itemId);
        if (fileName == null || fileName.isBlank()) {
            return null;
        }

        try {
            InputStream is = ItemDatabase.class.getResourceAsStream("/image/items/" + fileName);
            if (is == null) {
                System.out.println("Image not found: " + fileName);
                return null;
            }

            Image img = new Image(is);
            imageCache.put(itemId, img);
            return img;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<item> getFirstItems(int limit) {
        int end = Math.min(limit, allItems.size());
        return new ArrayList<>(allItems.subList(0, end));
    }
}