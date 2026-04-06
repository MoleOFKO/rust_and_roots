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
import java.util.Locale;
import java.util.Map;
import javafx.scene.image.Image;

public final class ItemDatabase {

    private static final List<item> allItems = new ArrayList<>();
    private static final Map<Integer, item> itemById = new LinkedHashMap<>();
    private static final Map<String, item> itemByName = new HashMap<>();
    private static final Map<Integer, String> imageFileById = new HashMap<>();
    private static final Map<Integer, String> itemTypeById = new HashMap<>();
    private static final Map<Integer, Image> imageCache = new HashMap<>();

    private static boolean loaded = false;

    private ItemDatabase() {}

    public static void loadItems() {
        if (!loaded) reloadItems();
    }

    public static void reloadItems() {
        allItems.clear();
        itemById.clear();
        itemByName.clear();
        imageFileById.clear();
        itemTypeById.clear();
        imageCache.clear();

        String sql = """
            SELECT i.item_id,
                   i.item_name,
                   i.price,
                   i.image,
                   it.item_type_name AS item_type,
                   COALESCE(s.str_stat, 0) AS str,
                   COALESCE(s.agi_stat, 0) AS agi,
                   COALESCE(s.dex_stat, 0) AS dex,
                   COALESCE(s.vit_stat, 0) AS vit,
                   COALESCE(s.hp, 0) AS hp,
                   COALESCE(s.stamina, 0) AS stamina
            FROM items i
            LEFT JOIN item_stats s ON i.item_stats_id = s.item_stats_id
            LEFT JOIN item_types it ON i.item_type_id = it.item_type_id
            ORDER BY i.item_id
            """;

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
                itemByName.put(normalizeLookupKey(itemName), itm);
                imageFileById.put(itemId, rs.getString("image"));
                itemTypeById.put(itemId, normalizeType(rs.getString("item_type")));
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

    public static boolean isLoaded() { return loaded; }
    public static List<item> getAllItems() { return Collections.unmodifiableList(allItems); }
    public static item getItemById(int itemId) { return itemById.get(itemId); }

    public static item getItemByName(String itemName) {
        if (itemName == null) return null;
        return itemByName.get(normalizeLookupKey(itemName));
    }

    public static boolean containsItem(int itemId) { return itemById.containsKey(itemId); }
    public static String getImageFileName(int itemId) { return imageFileById.get(itemId); }
    public static String getItemType(int itemId) { return itemTypeById.getOrDefault(itemId, ""); }

    public static boolean hasImageAsset(int itemId) {
        return itemId > 0 && containsItem(itemId) && getImage(itemId) != null;
    }

    public static boolean isDisplayableItem(int itemId) {
        return itemId > 0
                && containsItem(itemId)
                && getItemName(itemId) != null
                && !getItemName(itemId).isBlank()
                && hasImageAsset(itemId);
    }

    public static String getItemName(int itemId) {
        item itm = getItemById(itemId);
        return itm == null ? null : itm.getItem_name();
    }

    public static Image getImage(int itemId) {
        if (imageCache.containsKey(itemId)) return imageCache.get(itemId);
        String file = imageFileById.get(itemId);
        if (file == null || file.isBlank()) {
            imageCache.put(itemId, null);
            return null;
        }
        String[] attempts = { "/image/item/" + file, "/image/items/" + file, "/image/" + file };
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

    public static boolean isSeedItem(int itemId) {
        String type = getItemType(itemId);
        if ("seed".equals(type)) return true;
        String name = getItemName(itemId);
        if (name == null) return false;
        String n = normalizeLookupKey(name);
        return n.endsWith(" seed") || n.endsWith(" seeds");
    }

    public static List<Integer> getAllSeedItemIds() {
        List<Integer> ids = new ArrayList<>();
        for (item itm : allItems) {
            if (itm != null && isSeedItem(itm.getItem_id())) ids.add(itm.getItem_id());
        }
        return ids;
    }

    public static int resolveSeedItemIdByCropName(String cropName) {
        if (cropName == null || cropName.isBlank()) return -1;
        item direct = getItemByName(cropName + " Seed");
        if (direct != null) return direct.getItem_id();
        direct = getItemByName(cropName + " Seeds");
        if (direct != null) return direct.getItem_id();
        String normalizedCrop = normalizeLookupKey(cropName);
        for (item itm : allItems) {
            if (itm == null || !isSeedItem(itm.getItem_id())) continue;
            if (normalizedCrop.equals(normalizeLookupKey(normalizeSeedName(itm.getItem_name())))) {
                return itm.getItem_id();
            }
        }
        return -1;
    }

    public static int resolveCropItemIdForSeed(int seedItemId) {
        String seedName = getItemName(seedItemId);
        if (seedName == null || seedName.isBlank()) return -1;
        String cropName = normalizeSeedName(seedName);
        item direct = getItemByName(cropName);
        if (direct != null && !isSeedItem(direct.getItem_id())) return direct.getItem_id();
        direct = getItemByName(cropName + " Fruit");
        if (direct != null && !isSeedItem(direct.getItem_id())) return direct.getItem_id();
        direct = getItemByName(toSpriteKey(cropName) + "_fruit");
        if (direct != null && !isSeedItem(direct.getItem_id())) return direct.getItem_id();

        String normalizedTarget = normalizeLookupKey(cropName);
        for (item itm : allItems) {
            if (itm == null || isSeedItem(itm.getItem_id())) continue;
            if (normalizedTarget.equals(normalizeLookupKey(itm.getItem_name()))) {
                return itm.getItem_id();
            }
        }
        return -1;
    }

    public static String resolveCropNameForSeed(int seedItemId) {
        int cropItemId = resolveCropItemIdForSeed(seedItemId);
        if (cropItemId > 0) return getItemName(cropItemId);
        String seedName = getItemName(seedItemId);
        return seedName == null ? null : normalizeSeedName(seedName);
    }

    public static int resolveHarvestItemIdForSeed(int seedItemId) {
        int cropItemId = resolveCropItemIdForSeed(seedItemId);
        if (cropItemId <= 0) return -1;

        String cropName = getItemName(cropItemId);
        if (cropName == null || cropName.isBlank()) return -1;

        item fruitNamed = getItemByName(cropName + " Fruit");
        if (fruitNamed != null && fruitNamed.getItem_id() > 0) return fruitNamed.getItem_id();

        String spriteKey = toSpriteKey(cropName);
        item underscoredFruit = getItemByName(spriteKey + "_fruit");
        if (underscoredFruit != null && underscoredFruit.getItem_id() > 0) return underscoredFruit.getItem_id();

        return cropItemId;
    }

    public static boolean hasCompleteFarmDataForSeed(int seedItemId) {
        if (seedItemId <= 0 || !isSeedItem(seedItemId)) return false;
        String seedName = getItemName(seedItemId);
        if (seedName == null || seedName.isBlank()) return false;
        if (!hasImageAsset(seedItemId)) return false;

        int cropItemId = resolveCropItemIdForSeed(seedItemId);
        if (cropItemId <= 0) return false;
        String cropName = getItemName(cropItemId);
        if (cropName == null || cropName.isBlank()) return false;
        if (!hasImageAsset(cropItemId)) return false;

        int harvestItemId = resolveHarvestItemIdForSeed(seedItemId);
        if (harvestItemId <= 0 || !isDisplayableItem(harvestItemId)) return false;

        String spriteKey = buildCropSpriteKeyForSeed(seedItemId);
        if (spriteKey == null || spriteKey.isBlank()) return false;

        return hasCropStageAsset(spriteKey, 1)
                && hasCropStageAsset(spriteKey, 2)
                && hasCropStageAsset(spriteKey, 3);
    }

    public static String buildCropSpriteKeyForSeed(int seedItemId) {
        int cropItemId = resolveCropItemIdForSeed(seedItemId);
        if (cropItemId > 0) {
            String imageFile = getImageFileName(cropItemId);
            if (imageFile != null && !imageFile.isBlank()) {
                int dot = imageFile.lastIndexOf('.');
                String key = dot > 0 ? imageFile.substring(0, dot) : imageFile;
                key = stripCropSuffixes(key);
                if (key != null && !key.isBlank()) return key;
            }
            String cropName = getItemName(cropItemId);
            if (cropName != null) {
                String key = stripCropSuffixes(toSpriteKey(cropName));
                if (key != null && !key.isBlank()) return key;
            }
        }
        String seedName = getItemName(seedItemId);
        return seedName == null ? null : stripCropSuffixes(toSpriteKey(normalizeSeedName(seedName)));
    }

    public static String normalizeSeedName(String itemName) {
        if (itemName == null) return null;
        String out = itemName.trim();
        if (out.toLowerCase(Locale.ROOT).endsWith(" seeds")) {
            return out.substring(0, out.length() - 6).trim();
        }
        if (out.toLowerCase(Locale.ROOT).endsWith(" seed")) {
            return out.substring(0, out.length() - 5).trim();
        }
        return out;
    }

    public static String toSpriteKey(String name) {
        if (name == null) return null;
        return normalizeLookupKey(name).replace(' ', '_').replace('-', '_');
    }

    private static String stripCropSuffixes(String key) {
        if (key == null || key.isBlank()) return key;
        String out = key.trim().toLowerCase(Locale.ROOT);
        if (out.endsWith("_fruit")) out = out.substring(0, out.length() - 6);
        if (out.endsWith("_seed")) out = out.substring(0, out.length() - 5);
        return out;
    }

    public static boolean hasCropStageAsset(String spriteKey, int stageNumber) {
        if (spriteKey == null || spriteKey.isBlank() || stageNumber <= 0) return false;
        String path = String.format("/image/crops/%s_plant%d.png", spriteKey, stageNumber);
        return resourceExists(path);
    }

    private static boolean resourceExists(String path) {
        if (path == null || path.isBlank()) return false;
        try (InputStream is = ItemDatabase.class.getResourceAsStream(path)) {
            return is != null;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static String normalizeType(String type) {
        return type == null ? "" : type.trim().toLowerCase(Locale.ROOT);
    }

    private static String normalizeLookupKey(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT).replace('_', ' ');
    }
}
