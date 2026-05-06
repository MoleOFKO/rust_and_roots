package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class RecipeCatalog {

    public enum Domain {
        COOKING,
        CRAFTING
    }

    public static final class IngredientSpec {
        private final String[] itemAliases;
        private final int amount;

        public IngredientSpec(String[] itemAliases, int amount) {
            this.itemAliases = (itemAliases == null) ? new String[0] : itemAliases.clone();
            this.amount = Math.max(1, amount);
        }

        public String[] getItemAliases() {
            return itemAliases.clone();
        }

        public int getAmount() {
            return amount;
        }
    }

    public static final class RecipeDefinition {
        private final Domain domain;
        private final String key;
        private final String[] outputAliases;
        private final String displayName;
        private final List<IngredientSpec> ingredients;

        private RecipeDefinition(
            Domain domain,
            String key,
            String[] outputAliases,
            String displayName,
            IngredientSpec... ingredients
        ) {
            this.domain = (domain == null) ? Domain.COOKING : domain;
            this.key = (key == null) ? "" : key;
            this.outputAliases = (outputAliases == null) ? new String[0] : outputAliases.clone();
            this.displayName = (displayName == null || displayName.isBlank())
                ? firstNonBlank(outputAliases, key)
                : displayName;
            this.ingredients = Collections.unmodifiableList(
                Arrays.asList((ingredients == null) ? new IngredientSpec[0] : ingredients.clone())
            );
        }

        public Domain getDomain() {
            return domain;
        }

        public String getKey() {
            return key;
        }

        public String[] getOutputAliases() {
            return outputAliases.clone();
        }

        public String getDisplayName() {
            return displayName;
        }

        public List<IngredientSpec> getIngredients() {
            return ingredients;
        }
    }

    private static final List<RecipeDefinition> COOKING_RECIPES = buildCookingRecipes();
    private static final List<RecipeDefinition> CRAFTING_RECIPES = buildCraftingRecipes();

    private RecipeCatalog() {
    }

    public static List<RecipeDefinition> getRecipes(Domain domain) {
        return switch (domain == null ? Domain.COOKING : domain) {
            case COOKING -> COOKING_RECIPES;
            case CRAFTING -> CRAFTING_RECIPES;
        };
    }

    public static List<RecipeDefinition> getAllRecipes() {
        List<RecipeDefinition> all = new ArrayList<>(COOKING_RECIPES.size() + CRAFTING_RECIPES.size());
        all.addAll(COOKING_RECIPES);
        all.addAll(CRAFTING_RECIPES);
        return Collections.unmodifiableList(all);
    }

    public static List<RecipeDefinition> findDefinitionsUnlockedByItemName(String itemName) {
        if (itemName == null || itemName.isBlank()) {
            return List.of();
        }

        String normalizedItem = normalizeKey(itemName);
        String strippedItem = stripRecipeSuffix(normalizedItem);
        List<RecipeDefinition> matches = new ArrayList<>();

        for (RecipeDefinition definition : getAllRecipes()) {
            if (matchesRecipeItemName(normalizedItem, strippedItem, definition)) {
                matches.add(definition);
            }
        }

        return Collections.unmodifiableList(matches);
    }

    public static Set<String> getRecipeKeysUnlockedByItemName(String itemName) {
        LinkedHashSet<String> keys = new LinkedHashSet<>();

        for (RecipeDefinition definition : findDefinitionsUnlockedByItemName(itemName)) {
            if (definition != null && definition.getKey() != null && !definition.getKey().isBlank()) {
                keys.add(definition.getKey());
            }
        }

        return Collections.unmodifiableSet(keys);
    }

    public static boolean hasAllIngredientItems(RecipeDefinition definition) {
        if (definition == null) {
            return false;
        }

        for (IngredientSpec spec : definition.getIngredients()) {
            if (resolveItemByAliases(spec.getItemAliases()) == null) {
                return false;
            }
        }

        return true;
    }

    public static boolean isCookingOutputItem(int itemId) {
        if (itemId <= 0) {
            return false;
        }

        for (RecipeDefinition definition : COOKING_RECIPES) {
            item output = resolveItemByAliases(definition.getOutputAliases());
            if (output != null && output.getItem_id() == itemId) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the minimum number of ingredient specs required to cook the given output item.
     *
     * <p>Returns 0 if the item is not produced by any cooking recipe.
     */
    public static int getMinCookingIngredientCountForOutputItem(int itemId) {
        if (itemId <= 0) {
            return 0;
        }

        int min = Integer.MAX_VALUE;
        for (RecipeDefinition definition : COOKING_RECIPES) {
            if (definition == null) {
                continue;
            }

            item output = resolveItemByAliases(definition.getOutputAliases());
            if (output == null || output.getItem_id() != itemId) {
                continue;
            }

            int count = definition.getIngredients() == null ? 0 : definition.getIngredients().size();
            if (count > 0) {
                min = Math.min(min, count);
            }
        }

        return (min == Integer.MAX_VALUE) ? 0 : min;
    }

    public static RecipeDefinition findDefinitionByKey(String recipeKey) {
        if (recipeKey == null || recipeKey.isBlank()) {
            return null;
        }

        String normalizedKey = normalizeKey(recipeKey);
        for (RecipeDefinition definition : getAllRecipes()) {
            if (definition == null) {
                continue;
            }
            if (normalizedKey.equals(normalizeKey(definition.getKey()))) {
                return definition;
            }
        }

        return null;
    }

    private static boolean matchesRecipeItemName(
        String normalizedItem,
        String strippedItem,
        RecipeDefinition definition
    ) {
        if (definition == null) {
            return false;
        }

        if (matchesCandidate(normalizedItem, strippedItem, definition.getKey())) {
            return true;
        }

        if (matchesCandidate(normalizedItem, strippedItem, definition.getDisplayName())) {
            return true;
        }

        for (String alias : definition.getOutputAliases()) {
            if (matchesCandidate(normalizedItem, strippedItem, alias)) {
                return true;
            }
        }

        return false;
    }

    private static boolean matchesCandidate(String normalizedItem, String strippedItem, String candidate) {
        String normalizedCandidate = normalizeKey(candidate);
        if (normalizedCandidate.isBlank()) {
            return false;
        }

        return normalizedItem.equals(normalizedCandidate)
            || strippedItem.equals(normalizedCandidate)
            || normalizedItem.equals(normalizedCandidate + "recipe")
            || strippedItem.equals(normalizedCandidate + "recipe")
            || normalizedItem.equals("recipe" + normalizedCandidate)
            || strippedItem.equals("recipe" + normalizedCandidate);
    }

    public static item resolveItemByAliases(String... aliases) {
        if (aliases == null) {
            return null;
        }

        for (String alias : aliases) {
            if (alias == null || alias.isBlank()) {
                continue;
            }

            item direct = ItemDatabase.getItemByName(alias);
            if (direct != null) {
                return direct;
            }
        }

        for (String alias : aliases) {
            String normalizedAlias = normalizeKey(alias);
            if (normalizedAlias.isBlank()) {
                continue;
            }

            for (item itm : ItemDatabase.getAllItems()) {
                if (itm == null || itm.getItem_name() == null) {
                    continue;
                }

                if (normalizeKey(itm.getItem_name()).equals(normalizedAlias)) {
                    return itm;
                }

                String imageFile = ItemDatabase.getImageFileName(itm.getItem_id());
                if (imageFile != null && normalizeKey(stripExtension(imageFile)).equals(normalizedAlias)) {
                    return itm;
                }
            }
        }

        return null;
    }

    private static String stripExtension(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        int idx = value.lastIndexOf('.');
        return (idx >= 0) ? value.substring(0, idx) : value;
    }

    private static String stripRecipeSuffix(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        String normalized = value;
        if (normalized.endsWith("recipe")) {
            normalized = normalized.substring(0, normalized.length() - "recipe".length());
        }
        if (normalized.startsWith("recipe")) {
            normalized = normalized.substring("recipe".length());
        }
        return normalized;
    }

    private static String normalizeKey(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        return value
            .trim()
            .toLowerCase(Locale.ROOT)
            .replace(".png", "")
            .replace("_", "")
            .replace("-", "")
            .replace(" ", "");
    }

    private static String firstNonBlank(String[] values, String fallback) {
        if (values != null) {
            for (String value : values) {
                if (value != null && !value.isBlank()) {
                    return value;
                }
            }
        }
        return (fallback == null) ? "" : fallback;
    }

    private static List<RecipeDefinition> buildCookingRecipes() {
        List<RecipeDefinition> recipes = new ArrayList<>();

        recipes.add(new RecipeDefinition(Domain.COOKING, "tomato_slices", new String[]{"tomato_slices"}, null,
            new IngredientSpec(new String[]{"tomato"}, 1)));
        recipes.add(new RecipeDefinition(Domain.COOKING, "baked_potato", new String[]{"baked_potato"}, null,
            new IngredientSpec(new String[]{"potato"}, 1)));
        recipes.add(new RecipeDefinition(Domain.COOKING, "grilled_steak", new String[]{"grilled_steak"}, null,
            new IngredientSpec(new String[]{"beef", "steak"}, 1)));
        recipes.add(new RecipeDefinition(Domain.COOKING, "salad_leaves", new String[]{"salad_leaves"}, null,
            new IngredientSpec(new String[]{"lettuce"}, 1)));

        recipes.add(new RecipeDefinition(Domain.COOKING, "fresh_salad", new String[]{"fresh_salad"}, null,
            new IngredientSpec(new String[]{"tomato"}, 1),
            new IngredientSpec(new String[]{"lettuce"}, 1)));
        recipes.add(new RecipeDefinition(Domain.COOKING, "beef_potato_stew", new String[]{"beef_potato_stew"}, null,
            new IngredientSpec(new String[]{"potato"}, 1),
            new IngredientSpec(new String[]{"beef"}, 1)));
        recipes.add(new RecipeDefinition(Domain.COOKING, "pepper_steak", new String[]{"pepper_steak"}, null,
            new IngredientSpec(new String[]{"beef", "steak"}, 1),
            new IngredientSpec(new String[]{"red_bell_pepper", "red bellpepper"}, 1)));
        recipes.add(new RecipeDefinition(Domain.COOKING, "pork_cabbage_stir_fry", new String[]{"pork_cabbage_stir-fry"}, null,
            new IngredientSpec(new String[]{"pork"}, 1),
            new IngredientSpec(new String[]{"cabbage"}, 1)));
        recipes.add(new RecipeDefinition(Domain.COOKING, "chicken_corn_soup", new String[]{"chicken_corn_soup"}, null,
            new IngredientSpec(new String[]{"chicken_thigh"}, 1),
            new IngredientSpec(new String[]{"corn"}, 1)));
        recipes.add(new RecipeDefinition(Domain.COOKING, "roasted_pumpkin_mash", new String[]{"roasted_pumpkin_mash"}, null,
            new IngredientSpec(new String[]{"pumpkin"}, 1),
            new IngredientSpec(new String[]{"sweet_potato"}, 1)));
        recipes.add(new RecipeDefinition(Domain.COOKING, "spicy_eggplant", new String[]{"spicy_eggplant"}, null,
            new IngredientSpec(new String[]{"eggplant"}, 1),
            new IngredientSpec(new String[]{"green_chilli", "green chili"}, 1)));
        recipes.add(new RecipeDefinition(Domain.COOKING, "root_mix", new String[]{"root_vegetable_mix", "root_mix"}, null,
            new IngredientSpec(new String[]{"daikon"}, 1),
            new IngredientSpec(new String[]{"carrot"}, 1)));

        recipes.add(new RecipeDefinition(Domain.COOKING, "garden_salad", new String[]{"garden_salad"}, null,
            new IngredientSpec(new String[]{"tomato"}, 1),
            new IngredientSpec(new String[]{"lettuce"}, 1),
            new IngredientSpec(new String[]{"red_bell_pepper", "red bellpepper"}, 1)));
        recipes.add(new RecipeDefinition(Domain.COOKING, "beef_stew", new String[]{"beef_stew"}, null,
            new IngredientSpec(new String[]{"beef"}, 1),
            new IngredientSpec(new String[]{"potato"}, 1),
            new IngredientSpec(new String[]{"carrot"}, 1)));
        recipes.add(new RecipeDefinition(Domain.COOKING, "steak_dinner", new String[]{"steak_dinner"}, null,
            new IngredientSpec(new String[]{"beef", "steak"}, 1),
            new IngredientSpec(new String[]{"potato"}, 1),
            new IngredientSpec(new String[]{"broccoli"}, 1)));
        recipes.add(new RecipeDefinition(Domain.COOKING, "spicy_pork_stir_fry", new String[]{"spicy_pork_stir-fry"}, null,
            new IngredientSpec(new String[]{"pork"}, 1),
            new IngredientSpec(new String[]{"cabbage"}, 1),
            new IngredientSpec(new String[]{"green_chilli", "green chili"}, 1)));
        recipes.add(new RecipeDefinition(Domain.COOKING, "chicken_corn_soup_deluxe", new String[]{"chicken_corn_soup_deluxe", "chicken_corn_soup"}, "Hearty deluxe soup",
            new IngredientSpec(new String[]{"chicken_thigh"}, 1),
            new IngredientSpec(new String[]{"corn"}, 1),
            new IngredientSpec(new String[]{"carrot"}, 1)));
        recipes.add(new RecipeDefinition(Domain.COOKING, "roasted_harvest_plate", new String[]{"roasted_harvest_plate"}, null,
            new IngredientSpec(new String[]{"pumpkin"}, 1),
            new IngredientSpec(new String[]{"sweet_potato"}, 1),
            new IngredientSpec(new String[]{"ham"}, 1)));
        recipes.add(new RecipeDefinition(Domain.COOKING, "spicy_eggplant_tomato", new String[]{"spicy_eggplant_tomato"}, null,
            new IngredientSpec(new String[]{"eggplant"}, 1),
            new IngredientSpec(new String[]{"tomato"}, 1),
            new IngredientSpec(new String[]{"green_chilli", "green chili"}, 1)));
        recipes.add(new RecipeDefinition(Domain.COOKING, "root_vegetable_beef_soup", new String[]{"root_vegetable_beef_soup"}, null,
            new IngredientSpec(new String[]{"daikon"}, 1),
            new IngredientSpec(new String[]{"carrot"}, 1),
            new IngredientSpec(new String[]{"beef"}, 1)));

        return Collections.unmodifiableList(recipes);
    }

    private static List<RecipeDefinition> buildCraftingRecipes() {
        List<RecipeDefinition> recipes = new ArrayList<>();

        recipes.add(new RecipeDefinition(Domain.CRAFTING, "rope", new String[]{"Rope"}, null,
            new IngredientSpec(new String[]{"plant_fiber", "plant fiber"}, 1)));
        recipes.add(new RecipeDefinition(Domain.CRAFTING, "stick", new String[]{"Stick"}, null,
            new IngredientSpec(new String[]{"tree_log"}, 1)));
        recipes.add(new RecipeDefinition(Domain.CRAFTING, "wooden_board", new String[]{"Wooden Board"}, null,
            new IngredientSpec(new String[]{"tree_log"}, 3)));
        recipes.add(new RecipeDefinition(Domain.CRAFTING, "stone_block", new String[]{"Stone Block"}, null,
            new IngredientSpec(new String[]{"stone_drop_item"}, 2)));
        recipes.add(new RecipeDefinition(Domain.CRAFTING, "polished_stone", new String[]{"Polished Stone"}, null,
            new IngredientSpec(new String[]{"stone_drop_item"}, 2),
            new IngredientSpec(new String[]{"sand"}, 1)));
        recipes.add(new RecipeDefinition(Domain.CRAFTING, "fruit_mix", new String[]{"Fruit Mix"}, null,
            new IngredientSpec(new String[]{"blue_fruit"}, 1),
            new IngredientSpec(new String[]{"red_fruit"}, 1)));
        recipes.add(new RecipeDefinition(Domain.CRAFTING, "water_bottle", new String[]{"water_bottle", "Water Bottle"}, null,
            new IngredientSpec(new String[]{"glass"}, 1)));

        return Collections.unmodifiableList(recipes);
    }
}
