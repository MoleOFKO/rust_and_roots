/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import db.dbcon;
import java.io.InputStream;
import java.lang.reflect.Method;
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

                setItemStat(itm, rs.getInt("str"), "setStr");
                setItemStat(itm, rs.getInt("agi"), "setAgi");
                setItemStat(itm, rs.getInt("dex"), "setDex");
                setItemStat(itm, rs.getInt("vit"), "setVit", "setVid");
                setItemStat(itm, rs.getInt("hp"), "setHp");
                setItemStat(itm, rs.getInt("stamina"), "setStamina", "setStamana", "setStimana", "setMp");

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

    private static void setItemStat(item itm, int value, String... setterNames) {
        if (itm == null || setterNames == null) return;

        for (String setterName : setterNames) {
            if (setterName == null || setterName.isBlank()) continue;
            try {
                Method method = itm.getClass().getMethod(setterName, int.class);
                method.invoke(itm, value);
                return;
            } catch (ReflectiveOperationException ignored) {
            }
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

        String file = imageFileById.get(itemId);
        if (file == null || file.isBlank()) {
            imageCache.put(itemId, null);
            return null;
        }

        String[] attempts = {
            "/image/item/" + file,
            "/image/items/" + file,
            "/image/" + file
        };

        for (String path : attempts) {
            InputStream is = ItemDatabase.class.getResourceAsStream(path);
            if (is == null) continue;
            Image image = new Image(is);
            imageCache.put(itemId, image);
            return image;
        }

        imageCache.put(itemId, null);
        return null;
    }
}