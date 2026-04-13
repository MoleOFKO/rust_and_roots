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
    private static final Map<Integer, ItemUseType> itemUseTypeById = new HashMap<>();
    private static final Map<Integer, String> unusableReasonById = new LinkedHashMap<>();
    private static final Map<Integer, Image> imageCache = new HashMap<>();
    private static final Map<Integer, Integer> maxGrowthPhasesBySeedId = new HashMap<>();

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
        itemUseTypeById.clear();
        unusableReasonById.clear();
        imageCache.clear();
        maxGrowthPhasesBySeedId.clear();

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
                String itemTypeName = rs.getString("item_type");

                item itm = new item(itemId, itemName, price);
                itm.setItem_type_name(itemTypeName);
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
            rebuildValidationState();
            loaded = true;
            System.out.println("Loaded items: " + allItems.size());
            logVerificationFindings();
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
    public static ItemUseType getItemUseType(int itemId) { return itemUseTypeById.getOrDefault(itemId, ItemUseType.UNKNOWN); }
    public static String getUnusableReason(int itemId) { return unusableReasonById.get(itemId); }
    public static Map<Integer, String> getUnusableItemsWithReasons() { return Collections.unmodifiableMap(unusableReasonById); }
    public static boolean hasAnyRecipeItems() { return itemUseTypeById.containsValue(ItemUseType.RECIPE); }

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

    public static boolean isUsableItem(int itemId) {
        return itemId > 0
                && containsItem(itemId)
                && !unusableReasonById.containsKey(itemId);
    }

    public static boolean isShopDisplayableItem(int itemId) {
        return itemId > 0
                && isUsableItem(itemId)
                && getItemUseType(itemId) != ItemUseType.CROP
                && isDisplayableItem(itemId);
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
        if (getItemUseType(itemId) == ItemUseType.SEED) return true;
        String type = getItemType(itemId);
        if ("seed".equals(type)) return true;
        String name = getItemName(itemId);
        if (name == null) return false;
        String n = normalizeLookupKey(name);
        return n.endsWith(" seed") || n.endsWith(" seeds");
    }

    public static boolean isEquipmentItem(int itemId) {
        if (itemId <= 0) return false;
        if (getItemUseType(itemId) == ItemUseType.EQUIPMENT) return true;

        String lookup = normalizeLookupKey(getItemName(itemId));
        return lookup.contains("axe")
            || lookup.contains("pickaxe")
            || lookup.contains("pick axe")
            || lookup.contains("sickle")
            || lookup.contains("fishing rod")
            || lookup.contains("watering can");
    }

    public static boolean isEdibleItem(int itemId) {
        if (itemId <= 0) return false;
        return getItemUseType(itemId) == ItemUseType.EDIBLE || RecipeCatalog.isCookingOutputItem(itemId);
    }

    public static boolean isRecipeItem(int itemId) {
        return itemId > 0 && getItemUseType(itemId) == ItemUseType.RECIPE;
    }

    public static boolean isCropItem(int itemId) {
        return itemId > 0 && getItemUseType(itemId) == ItemUseType.CROP;
    }

    /**
     * Returns the maximum number of items that can be stacked in a single slot.
     *
     * Stacking rules:
     * - equipment + recipe: 1 per slot
     * - edible: 32 per slot
     * - seeds: 45 per slot
     * - inedible (and everything else): 64 per slot
     */
    public static int getMaxStackSize(int itemId) {
        if (itemId <= 0) return 0;

        // Ensure item types are available when called early.
        loadItems();

        if (isEquipmentItem(itemId) || isRecipeItem(itemId)) {
            return 1;
        }
        if (isSeedItem(itemId)) {
            return 45;
        }
        if (isEdibleItem(itemId)) {
            return 32;
        }

        // Default to INEDIBLE-like stacks for materials and unknowns.
        return 64;
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
        String spriteKey = guessCropSpriteKeyForSeed(seedItemId);
        item cropStage = findCropStageItemBySpriteKey(spriteKey);
        if (cropStage != null) {
            return cropStage.getItem_id();
        }

        String seedName = getItemName(seedItemId);
        if (seedName == null || seedName.isBlank()) return -1;
        String cropName = normalizeSeedName(seedName);

        item direct = findNonSeedItemByName(cropName);
        if (direct == null) {
            direct = findNonSeedItemByName(normalizeCropAlias(cropName));
        }
        if (direct != null && !isFruitLikeItem(direct.getItem_id())) {
            return direct.getItem_id();
        }

        return -1;
    }

    public static String resolveCropNameForSeed(int seedItemId) {
        String seedName = getItemName(seedItemId);
        return seedName == null ? null : normalizeSeedName(seedName);
    }

    public static int resolveHarvestItemIdForSeed(int seedItemId) {
        String seedName = getItemName(seedItemId);
        if (seedName == null || seedName.isBlank()) return -1;

        String cropName = normalizeSeedName(seedName);
        item harvest = findHarvestItem(cropName);
        if (harvest != null) return harvest.getItem_id();

        harvest = findHarvestItem(normalizeCropAlias(cropName));
        return harvest == null ? -1 : harvest.getItem_id();
    }

    public static boolean hasCompleteFarmDataForSeed(int seedItemId) {
        if (seedItemId <= 0 || !isSeedItem(seedItemId)) return false;
        String seedName = getItemName(seedItemId);
        if (seedName == null || seedName.isBlank()) return false;
        if (!hasImageAsset(seedItemId)) return false;

        int cropItemId = resolveCropItemIdForSeed(seedItemId);
        if (cropItemId <= 0 || !hasImageAsset(cropItemId)) return false;

        int harvestItemId = resolveHarvestItemIdForSeed(seedItemId);
        if (harvestItemId <= 0 || !isDisplayableItem(harvestItemId)) return false;

        String spriteKey = buildCropSpriteKeyForSeed(seedItemId);
        if (spriteKey == null || spriteKey.isBlank()) return false;

        return hasCropStageAsset(spriteKey, 1)
                && hasCropStageAsset(spriteKey, 2)
                && hasCropStageAsset(spriteKey, 3);
    }

    public static int resolveMaxGrowthPhasesForSeed(int seedItemId) {
        loadItems();
        if (seedItemId <= 0 || !isSeedItem(seedItemId)) {
            return FarmGrowthRules.TOTAL_GROWTH_STAGES;
        }

        Integer cached = maxGrowthPhasesBySeedId.get(seedItemId);
        if (cached != null && cached > 0) {
            return cached;
        }

        String spriteKey = buildCropSpriteKeyForSeed(seedItemId);
        int cropImageStages = countCropImageStages(spriteKey);

        int maxGrowthPhases = cropImageStages > 0 ? cropImageStages : FarmGrowthRules.TOTAL_GROWTH_STAGES;
        maxGrowthPhasesBySeedId.put(seedItemId, maxGrowthPhases);
        return maxGrowthPhases;
    }

    private static int countCropImageStages(String spriteKey) {
        if (spriteKey == null || spriteKey.isBlank()) {
            return 0;
        }

        int count = 0;
        for (int stage = 1; stage <= 32; stage++) {
            if (!hasCropStageAsset(spriteKey, stage)) {
                break;
            }
            count++;
        }
        return count;
    }

    public static String buildCropSpriteKeyForSeed(int seedItemId) {
        int cropItemId = resolveCropItemIdForSeed(seedItemId);
        if (cropItemId > 0) {
            String imageFile = getImageFileName(cropItemId);
            if (imageFile != null && !imageFile.isBlank()) {
                String key = stripFileExtension(imageFile);
                key = stripCropSuffixes(key);
                if (key != null && !key.isBlank()) return key;
            }
        }
        return guessCropSpriteKeyForSeed(seedItemId);
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
        out = out.replaceAll("_(plant|tree)\\d+$", "");
        return out;
    }

    public static boolean hasCropStageAsset(String spriteKey, int stageNumber) {
        if (spriteKey == null || spriteKey.isBlank() || stageNumber <= 0) return false;
        String path = String.format("/image/crops/%s_plant%d.png", spriteKey, stageNumber);
        return resourceExists(path);
    }

    private static String normalizeCropAlias(String cropName) {
        if (cropName == null || cropName.isBlank()) return cropName;
        String normalized = cropName.trim();
        if (normalized.equalsIgnoreCase("chilli")) return "chili";
        if (normalized.equalsIgnoreCase("chili")) return "chilli";
        return normalized;
    }

    private static boolean resourceExists(String path) {
        if (path == null || path.isBlank()) return false;
        try (InputStream is = ItemDatabase.class.getResourceAsStream(path)) {
            return is != null;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static void rebuildValidationState() {
        itemUseTypeById.clear();
        unusableReasonById.clear();

        for (item itm : allItems) {
            if (itm == null) continue;
            itemUseTypeById.put(itm.getItem_id(), determineItemUseType(itm));
        }

        for (item itm : allItems) {
            if (itm == null) continue;
            String reason = determineUnusableReason(itm.getItem_id());
            if (reason != null && !reason.isBlank()) {
                unusableReasonById.put(itm.getItem_id(), reason);
            }
        }
    }

    private static void logVerificationFindings() {
        try {
            logUnusableItems();
            logRecipeDefinitionIssues();
        } catch (Exception e) {
            System.out.println("Verification logging failed: " + e.getMessage());
        }
    }

    private static void logUnusableItems() {
        if (unusableReasonById.isEmpty()) {
            System.out.println("Item verification: OK");
            return;
        }

        System.out.println("Item verification: unusable items (" + unusableReasonById.size() + ")");
        for (Map.Entry<Integer, String> entry : unusableReasonById.entrySet()) {
            int itemId = entry.getKey() == null ? -1 : entry.getKey();
            String name = itemId > 0 ? getItemName(itemId) : null;
            String safeName = (name == null || name.isBlank()) ? "<unknown>" : name;
            String reason = entry.getValue();
            System.out.println(" - item_id=" + itemId + " name=\"" + safeName + "\": " + reason);
        }
    }

    private static void logRecipeDefinitionIssues() {
        List<String> issues = new ArrayList<>();

        for (RecipeCatalog.RecipeDefinition definition : RecipeCatalog.getAllRecipes()) {
            if (definition == null) {
                continue;
            }

            item output = RecipeCatalog.resolveItemByAliases(definition.getOutputAliases());
            if (output == null || !isDisplayableItem(output.getItem_id())) {
                issues.add(formatRecipeIssue(definition, "Missing or invalid output item."));
                continue;
            }

            for (RecipeCatalog.IngredientSpec spec : definition.getIngredients()) {
                item ingredient = RecipeCatalog.resolveItemByAliases(spec.getItemAliases());
                if (ingredient == null || !isDisplayableItem(ingredient.getItem_id())) {
                    issues.add(formatRecipeIssue(definition, "Missing or invalid ingredient item."));
                    break;
                }
            }
        }

        if (issues.isEmpty()) {
            System.out.println("Recipe verification: OK");
            return;
        }

        System.out.println("Recipe verification: issues (" + issues.size() + ")");
        for (String issue : issues) {
            System.out.println(" - " + issue);
        }
    }

    private static String formatRecipeIssue(RecipeCatalog.RecipeDefinition definition, String message) {
        if (definition == null) {
            return message;
        }

        String domain = definition.getDomain() == null ? "unknown" : definition.getDomain().name().toLowerCase(Locale.ROOT);
        String key = definition.getKey();
        if (key == null || key.isBlank()) {
            key = definition.getDisplayName();
        }
        if (key == null || key.isBlank()) {
            key = "<unknown>";
        }
        return "[" + domain + "] " + key + ": " + message;
    }

    private static ItemUseType determineItemUseType(item itm) {
        if (itm == null) return ItemUseType.UNKNOWN;

        String rawType = normalizeType(itm.getItem_type_name());
        String itemName = normalizeLookupKey(itm.getItem_name());
        String imageFile = normalizeType(stripFileExtension(getImageFileName(itm.getItem_id())));

        return switch (rawType) {
            case "equipment", "equipments", "tool", "weapon" -> ItemUseType.EQUIPMENT;
            case "edible", "food" -> ItemUseType.EDIBLE;
            case "inedible", "material", "materials" -> ItemUseType.INEDIBLE;
            case "recipe", "recipes" -> ItemUseType.RECIPE;
            case "crop", "crops" -> ItemUseType.CROP;
            case "seed", "seeds" -> ItemUseType.SEED;
            default -> inferItemUseType(itemName, imageFile);
        };
    }

    private static ItemUseType inferItemUseType(String itemName, String imageFile) {
        String normalizedName = normalizeLookupKey(itemName);
        String normalizedImage = normalizeType(imageFile);

        if (normalizedName.endsWith(" seed") || normalizedName.endsWith(" seeds") || normalizedImage.endsWith("_seed")) {
            return ItemUseType.SEED;
        }

        if (normalizedImage.matches(".*_(plant|tree)\\d+$") || normalizedName.matches(".*_(plant|tree)\\d+$")) {
            return ItemUseType.CROP;
        }

        if (normalizedName.contains("axe")
                || normalizedName.contains("pickaxe")
                || normalizedName.contains("pick axe")
                || normalizedName.contains("sickle")
                || normalizedName.contains("fishing rod")
                || normalizedName.contains("watering can")) {
            return ItemUseType.EQUIPMENT;
        }

        return ItemUseType.UNKNOWN;
    }

    private static String determineUnusableReason(int itemId) {
        if (itemId <= 0 || !containsItem(itemId)) {
            return "Missing item record.";
        }

        return switch (getItemUseType(itemId)) {
            case CROP -> "Crop-stage item is visual-only and should not be selectable or sold.";
            case SEED -> hasCompleteFarmDataForSeed(itemId)
                ? null
                : "Seed is missing a complete crop-growth chain or a distinct harvest item.";
            case RECIPE -> isValidRecipeUnlockItem(itemId)
                ? null
                : "Recipe item has no valid recipe definition or is missing ingredient items.";
            case UNKNOWN -> "Item type is unknown, so it cannot be safely used in gameplay.";
            case EQUIPMENT, EDIBLE, INEDIBLE -> null;
        };
    }

    private static boolean isValidRecipeUnlockItem(int itemId) {
        if (itemId <= 0 || !isRecipeItem(itemId)) {
            return false;
        }

        List<RecipeCatalog.RecipeDefinition> definitions =
            RecipeCatalog.findDefinitionsUnlockedByItemName(getItemName(itemId));

        if (definitions.isEmpty()) {
            return false;
        }

        for (RecipeCatalog.RecipeDefinition definition : definitions) {
            if (!RecipeCatalog.hasAllIngredientItems(definition)) {
                return false;
            }

            item output = RecipeCatalog.resolveItemByAliases(definition.getOutputAliases());
            if (output == null || !isDisplayableItem(output.getItem_id())) {
                return false;
            }
        }

        return true;
    }

    private static item findNonSeedItemByName(String itemName) {
        if (itemName == null || itemName.isBlank()) return null;
        item direct = getItemByName(itemName);
        return (direct != null && !isSeedItem(direct.getItem_id())) ? direct : null;
    }

    private static item findHarvestItem(String cropName) {
        if (cropName == null || cropName.isBlank()) {
            return null;
        }

        List<String> candidates = List.of(
            cropName + " Fruit",
            toSpriteKey(cropName) + "_fruit"
        );

        for (String candidate : candidates) {
            item itm = getItemByName(candidate);
            if (itm == null) continue;
            int itemId = itm.getItem_id();
            if (itemId > 0 && !isSeedItem(itemId) && !isCropItem(itemId)) {
                return itm;
            }
        }

        return null;
    }

    private static boolean isFruitLikeItem(int itemId) {
        if (itemId <= 0) return false;
        String name = normalizeLookupKey(getItemName(itemId));
        String imageFile = normalizeType(stripFileExtension(getImageFileName(itemId)));
        return name.endsWith(" fruit") || imageFile.endsWith("_fruit");
    }

    private static String guessCropSpriteKeyForSeed(int seedItemId) {
        if (seedItemId <= 0) return null;

        String imageFile = getImageFileName(seedItemId);
        if (imageFile != null && !imageFile.isBlank()) {
            String key = stripCropSuffixes(stripFileExtension(imageFile));
            if (key != null && !key.isBlank()) {
                return key;
            }
        }

        String seedName = getItemName(seedItemId);
        return seedName == null ? null : stripCropSuffixes(toSpriteKey(normalizeSeedName(seedName)));
    }

    private static item findCropStageItemBySpriteKey(String spriteKey) {
        if (spriteKey == null || spriteKey.isBlank()) {
            return null;
        }

        String normalizedSpriteKey = normalizeType(spriteKey);
        for (item itm : allItems) {
            if (itm == null || getItemUseType(itm.getItem_id()) != ItemUseType.CROP) {
                continue;
            }

            String imageFile = normalizeType(stripFileExtension(getImageFileName(itm.getItem_id())));
            if (imageFile.startsWith(normalizedSpriteKey + "_plant")
                    || imageFile.startsWith(normalizedSpriteKey + "_tree")) {
                return itm;
            }
        }

        return null;
    }

    private static String normalizeType(String type) {
        return type == null ? "" : type.trim().toLowerCase(Locale.ROOT);
    }

    private static String normalizeLookupKey(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT).replace('_', ' ');
    }

    private static String stripFileExtension(String fileName) {
        if (fileName == null || fileName.isBlank()) return "";
        int dot = fileName.lastIndexOf('.');
        return dot > 0 ? fileName.substring(0, dot) : fileName;
    }
}
