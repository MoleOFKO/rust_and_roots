package game;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import model.FarmState;
import model.Inventory;
import model.ItemDatabase;
import model.MobMapState;
import model.PlayerState;
import model.RecipeCatalog;
import model.TreeMapState;
import model.item;
import util.SpawnTag;

public final class GameState {
    private static final GameState INSTANCE = new GameState();

    private static final int DEFAULT_SMELT_POINT_CAP = 10;
    private static final double SMELT_POINT_FULL_REGEN_SECONDS = 6000.0; // 1% of cap per in-game minute (100 minutes to full)
    private static final int DEFAULT_FIXED_SHOP_ITEM_COUNT = 8;
    private static final int DEFAULT_RANDOM_SHOP_ITEM_COUNT = 4;
    private static final int CHEST_SLOT_CAPACITY = 42;
    private static final int NEW_GAME_STARTING_COINS = 100;

    private static final int[] RECIPE_UNLOCK_SINGLE_WEIGHT_BY_LEVEL = {100, 90, 80, 70, 60, 50, 40, 30, 20, 10, 10};
    private static final int[] RECIPE_UNLOCK_DOUBLE_WEIGHT_BY_LEVEL = {0, 9, 19, 29, 38, 47, 56, 65, 74, 82, 80};
    private static final int[] RECIPE_UNLOCK_TRIPLE_WEIGHT_BY_LEVEL = {0, 1, 1, 1, 2, 3, 4, 5, 6, 8, 10};

    public enum StatusPanelGroup {
        STATUS,
        SKILLS
    }

    private int smeltPointCap = DEFAULT_SMELT_POINT_CAP;
    private double smeltPoints = DEFAULT_SMELT_POINT_CAP;
    private double lastSmeltPointUpdateSeconds = 0.0;
    private final Map<Integer, Integer> smeltCompletedBySourceItemId = new LinkedHashMap<>();
    private double smeltExpCarry = 0.0;
    private double smeltPointCapCarry = 0.0;
    
    public static GameState get() { return INSTANCE; }

    public static final class FoodEffectOutcome {
        private final int hpDelta;
        private final int staminaDelta;
        private final int strDelta;
        private final int vitDelta;
        private final int dexDelta;
        private final int agiDelta;
        private final boolean success;

        public FoodEffectOutcome(int hpDelta, int staminaDelta, boolean success) {
            this(hpDelta, staminaDelta, 0, 0, 0, 0, success);
        }

        public FoodEffectOutcome(int hpDelta, int staminaDelta, int strDelta, int vitDelta, int dexDelta, int agiDelta, boolean success) {
            this.hpDelta = hpDelta;
            this.staminaDelta = staminaDelta;
            this.strDelta = strDelta;
            this.vitDelta = vitDelta;
            this.dexDelta = dexDelta;
            this.agiDelta = agiDelta;
            this.success = success;
        }

        public int getHpDelta() { return hpDelta; }
        public int getStaminaDelta() { return staminaDelta; }
        public int getStrDelta() { return strDelta; }
        public int getVitDelta() { return vitDelta; }
        public int getDexDelta() { return dexDelta; }
        public int getAgiDelta() { return agiDelta; }
        public boolean isSuccess() { return success; }
    }

    public static final class ActiveSmeltJob {
        private final int sourceItemId;
        private final int resultItemId;
        private final int quantity;
        private final int costPerOre;
        private final double secondsPerOre;
        private final double startedAtSeconds;

        public ActiveSmeltJob(int sourceItemId, int resultItemId, int quantity, int costPerOre, double secondsPerOre, double startedAtSeconds) {
            this.sourceItemId = sourceItemId;
            this.resultItemId = resultItemId;
            this.quantity = quantity;
            this.costPerOre = costPerOre;
            this.secondsPerOre = secondsPerOre;
            this.startedAtSeconds = startedAtSeconds;
        }

        public int getSourceItemId() { return sourceItemId; }
        public int getResultItemId() { return resultItemId; }
        public int getQuantity() { return quantity; }
        public int getCostPerOre() { return costPerOre; }
        public double getSecondsPerOre() { return secondsPerOre; }
        public double getStartedAtSeconds() { return startedAtSeconds; }

        public int getReadyCount(double nowSeconds) {
            if (quantity <= 0 || secondsPerOre <= 0) return 0;
            double elapsed = Math.max(0, nowSeconds - startedAtSeconds);
            int ready = (int) Math.floor(elapsed / secondsPerOre);
            if (ready < 0) ready = 0;
            if (ready > quantity) ready = quantity;
            return ready;
        }

        public int getRemainingCount(double nowSeconds) {
            return Math.max(0, quantity - getReadyCount(nowSeconds));
        }

        public double getNextReadyAtSeconds(double nowSeconds) {
            int ready = getReadyCount(nowSeconds);
            if (ready >= quantity) {
                return startedAtSeconds + (quantity * secondsPerOre);
            }
            return startedAtSeconds + ((ready + 1) * secondsPerOre);
        }
    }

    private final PlayerSkills playerSkills = new PlayerSkills();
    private final int maxSkillTotalExp = computeMaxSkillTotalExp();

    public PlayerSkills getPlayerSkills() {
        return playerSkills;
    }

    private int computeMaxSkillTotalExp() {
        int total = 0;
        for (int level = 0; level < PlayerSkills.MAX_LEVEL; level++) {
            total += playerSkills.getRequiredExpToAdvanceFrom(level);
        }
        return Math.max(0, total);
    }

    public void addFarmingSkillExp(int amount) { playerSkills.addFarmingExp(amount); }
    public void addGatheringSkillExp(int amount) { playerSkills.addGatheringExp(amount); }
    public void addCraftingSkillExp(int amount) { addRecipeSkillExp(RecipeCatalog.Domain.CRAFTING, amount); }
    public void addFishingSkillExp(int amount) { playerSkills.addFishingExp(amount); }
    public void addBattlingSkillExp(int amount) { playerSkills.addBattlingExp(amount); }
    public void addCookingSkillExp(int amount) { addRecipeSkillExp(RecipeCatalog.Domain.COOKING, amount); }

    public void ensureAutomaticRecipeUnlocks() {
        ensureCookingRecipeUnlocksThroughLevel(playerSkills.getCookingLevel());
        ensureCraftingRecipeUnlocksThroughLevel(playerSkills.getCraftingLevel());
    }

    private void ensureBaselineCraftRecipesUnlocked() {
        ItemDatabase.loadItems();
        unlockRecipeIfDefinitionValid("rope");
        unlockRecipeIfDefinitionValid("stick");
    }

    private void unlockRecipeIfDefinitionValid(String recipeKey) {
        if (recipeKey == null || recipeKey.isBlank()) {
            return;
        }

        RecipeCatalog.RecipeDefinition definition = RecipeCatalog.findDefinitionByKey(recipeKey);
        if (definition == null) {
            return;
        }

        if (isRecipeDefinitionValid(definition)) {
            unlockRecipe(definition.getKey());
        }
    }

    private void addRecipeSkillExp(RecipeCatalog.Domain domain, int amount) {
        if (amount <= 0 || playerState == null) {
            return;
        }

        RecipeCatalog.Domain safeDomain = domain == null ? RecipeCatalog.Domain.COOKING : domain;
        int storedTotal = getRecipeSkillTotalExp(safeDomain);
        int currentTotal = Math.min(storedTotal, maxSkillTotalExp);
        if (currentTotal != storedTotal) {
            setRecipeSkillTotalExp(safeDomain, currentTotal);
        }

        int oldLevel = playerSkills.getLevelFromTotalExp(currentTotal);

        long candidate = (long) currentTotal + (long) amount;
        int capped = (int) Math.min(candidate, (long) maxSkillTotalExp);
        int overflow = (int) Math.max(0L, candidate - (long) capped);

        setRecipeSkillTotalExp(safeDomain, capped);

        if (overflow > 0) {
            playerState.addExp(overflow);
        }

        ItemDatabase.loadItems();

        if (countUnlockedValidRecipesForDomain(safeDomain) <= 0) {
            unlockStarterSingleRecipeForDomain(safeDomain);
        }

        int newLevel = playerSkills.getLevelFromTotalExp(capped);
        for (int level = oldLevel + 1; level <= newLevel; level++) {
            unlockRandomRecipeForDomain(safeDomain, level);
        }
    }

    private int getRecipeSkillTotalExp(RecipeCatalog.Domain domain) {
        return switch (domain == null ? RecipeCatalog.Domain.COOKING : domain) {
            case COOKING -> playerSkills.getCookingExp();
            case CRAFTING -> playerSkills.getCraftingExp();
        };
    }

    private void setRecipeSkillTotalExp(RecipeCatalog.Domain domain, int totalExp) {
        int safeTotal = Math.max(0, totalExp);
        switch (domain == null ? RecipeCatalog.Domain.COOKING : domain) {
            case COOKING -> playerSkills.setCookingExp(safeTotal);
            case CRAFTING -> playerSkills.setCraftingExp(safeTotal);
        }
    }

    private void ensureCookingRecipeUnlocksThroughLevel(int cookingLevel) {
        ensureRecipeUnlocksThroughLevel(RecipeCatalog.Domain.COOKING, cookingLevel, true);
    }

    private void ensureCraftingRecipeUnlocksThroughLevel(int craftingLevel) {
        ensureRecipeUnlocksThroughLevel(RecipeCatalog.Domain.CRAFTING, craftingLevel, true);
    }

    private void ensureRecipeUnlocksThroughLevel(RecipeCatalog.Domain domain, int skillLevel, boolean includesLevelZeroStarter) {
        ItemDatabase.loadItems();

        int totalValid = countValidRecipesForDomain(domain);
        if (totalValid <= 0) {
            return;
        }

        int cappedLevel = Math.max(0, Math.min(PlayerSkills.MAX_LEVEL, skillLevel));
        int unlockedCount = countUnlockedValidRecipesForDomain(domain);

        if (includesLevelZeroStarter && unlockedCount <= 0) {
            String starter = unlockStarterSingleRecipeForDomain(domain);
            if (starter != null) {
                unlockedCount++;
            }
        }

        for (int level = 1; level <= cappedLevel && unlockedCount < totalValid; level++) {
            int expectedAtLevel = includesLevelZeroStarter ? (level + 1) : level;
            expectedAtLevel = Math.min(expectedAtLevel, totalValid);
            if (unlockedCount >= expectedAtLevel) {
                continue;
            }

            String unlocked = unlockRandomRecipeForDomain(domain, level);
            if (unlocked == null) {
                break;
            }
            unlockedCount++;
        }
    }

    private int countValidRecipesForDomain(RecipeCatalog.Domain domain) {
        int count = 0;
        for (RecipeCatalog.RecipeDefinition definition : RecipeCatalog.getRecipes(domain)) {
            if (isRecipeDefinitionValid(definition)) {
                count++;
            }
        }
        return count;
    }

    private int countUnlockedValidRecipesForDomain(RecipeCatalog.Domain domain) {
        int count = 0;
        for (RecipeCatalog.RecipeDefinition definition : RecipeCatalog.getRecipes(domain)) {
            if (!isRecipeDefinitionValid(definition)) {
                continue;
            }
            if (isRecipeUnlocked(definition.getKey())) {
                count++;
            }
        }
        return count;
    }

    private String unlockStarterSingleRecipeForDomain(RecipeCatalog.Domain domain) {
        RecipeCatalog.Domain safeDomain = domain == null ? RecipeCatalog.Domain.COOKING : domain;
        List<String> lockedSingles = new ArrayList<>();

        for (RecipeCatalog.RecipeDefinition definition : RecipeCatalog.getRecipes(safeDomain)) {
            if (!isRecipeDefinitionValid(definition)) {
                continue;
            }
            if (recipeCombinationSize(definition) != 1) {
                continue;
            }
            if (isRecipeUnlocked(definition.getKey())) {
                continue;
            }
            lockedSingles.add(definition.getKey());
        }

        if (lockedSingles.isEmpty()) {
            return null;
        }

        return unlockRecipeFromPool(lockedSingles);
    }

    private String unlockRandomRecipeForDomain(RecipeCatalog.Domain domain, int skillLevel) {
        RecipeCatalog.Domain safeDomain = domain == null ? RecipeCatalog.Domain.COOKING : domain;

        List<String> lockedSingles = new ArrayList<>();
        List<String> lockedDoubles = new ArrayList<>();
        List<String> lockedTriples = new ArrayList<>();
        List<String> allLocked = new ArrayList<>();

        for (RecipeCatalog.RecipeDefinition definition : RecipeCatalog.getRecipes(safeDomain)) {
            if (!isRecipeDefinitionValid(definition)) {
                continue;
            }
            if (isRecipeUnlocked(definition.getKey())) {
                continue;
            }

            int comboSize = recipeCombinationSize(definition);
            switch (comboSize) {
                case 1 -> lockedSingles.add(definition.getKey());
                case 2 -> lockedDoubles.add(definition.getKey());
                default -> lockedTriples.add(definition.getKey());
            }
        }

        allLocked.addAll(lockedSingles);
        allLocked.addAll(lockedDoubles);
        allLocked.addAll(lockedTriples);

        if (allLocked.isEmpty()) {
            return null;
        }

        for (int attempt = 0; attempt < 64; attempt++) {
            int targetCombo = rollRecipeCombinationSizeForLevel(skillLevel);
            List<String> pool = switch (targetCombo) {
                case 1 -> lockedSingles;
                case 2 -> lockedDoubles;
                default -> lockedTriples;
            };
            if (pool.isEmpty()) {
                continue;
            }
            return unlockRecipeFromPool(pool);
        }

        return unlockRecipeFromPool(allLocked);
    }

    private String unlockRecipeFromPool(List<String> pool) {
        if (pool == null || pool.isEmpty()) {
            return null;
        }
        String key = pool.get(ThreadLocalRandom.current().nextInt(pool.size()));
        unlockRecipe(key);
        return key;
    }

    private int rollRecipeCombinationSizeForLevel(int level) {
        int idx = Math.max(0, Math.min(RECIPE_UNLOCK_SINGLE_WEIGHT_BY_LEVEL.length - 1, level));
        int single = Math.max(0, RECIPE_UNLOCK_SINGLE_WEIGHT_BY_LEVEL[idx]);
        int doubleW = Math.max(0, RECIPE_UNLOCK_DOUBLE_WEIGHT_BY_LEVEL[idx]);
        int triple = Math.max(0, RECIPE_UNLOCK_TRIPLE_WEIGHT_BY_LEVEL[idx]);

        int total = single + doubleW + triple;
        if (total <= 0) {
            return 1;
        }

        int roll = ThreadLocalRandom.current().nextInt(total);
        if (roll < single) {
            return 1;
        }
        roll -= single;
        if (roll < doubleW) {
            return 2;
        }
        return 3;
    }

    private int recipeCombinationSize(RecipeCatalog.RecipeDefinition definition) {
        if (definition == null || definition.getIngredients() == null) {
            return 1;
        }

        int count = definition.getIngredients().size();
        if (count <= 1) {
            return 1;
        }
        if (count == 2) {
            return 2;
        }
        return 3;
    }

    private boolean isRecipeDefinitionValid(RecipeCatalog.RecipeDefinition definition) {
        if (definition == null) {
            return false;
        }

        String key = definition.getKey();
        if (key == null || key.isBlank()) {
            return false;
        }

        if (definition.getIngredients() == null || definition.getIngredients().isEmpty()) {
            return false;
        }

        if (!RecipeCatalog.hasAllIngredientItems(definition)) {
            return false;
        }

        item output = RecipeCatalog.resolveItemByAliases(definition.getOutputAliases());
        if (output == null || !ItemDatabase.isDisplayableItem(output.getItem_id())) {
            return false;
        }

        for (RecipeCatalog.IngredientSpec spec : definition.getIngredients()) {
            item ingredient = RecipeCatalog.resolveItemByAliases(spec.getItemAliases());
            if (ingredient == null || !ItemDatabase.isDisplayableItem(ingredient.getItem_id())) {
                return false;
            }
        }

        return true;
    }

    public static final class PlayerSkills {
        public static final int MAX_LEVEL = 10;

        private int farmingExp;
        private int gatheringExp;
        private int craftingExp;
        private int fishingExp;
        private int battlingExp;
        private int cookingExp;

        public void clear() {
            farmingExp = 0;
            gatheringExp = 0;
            craftingExp = 0;
            fishingExp = 0;
            battlingExp = 0;
            cookingExp = 0;
        }

        public int getFarmingExp() { return Math.max(0, farmingExp); }
        public void setFarmingExp(int value) { farmingExp = Math.max(0, value); }
        public void addFarmingExp(int value) { if (value > 0) farmingExp += value; }

        public int getGatheringExp() { return Math.max(0, gatheringExp); }
        public void setGatheringExp(int value) { gatheringExp = Math.max(0, value); }
        public void addGatheringExp(int value) { if (value > 0) gatheringExp += value; }

        public int getCraftingExp() { return Math.max(0, craftingExp); }
        public void setCraftingExp(int value) { craftingExp = Math.max(0, value); }
        public void addCraftingExp(int value) { if (value > 0) craftingExp += value; }

        public int getFishingExp() { return Math.max(0, fishingExp); }
        public void setFishingExp(int value) { fishingExp = Math.max(0, value); }
        public void addFishingExp(int value) { if (value > 0) fishingExp += value; }

        public int getBattlingExp() { return Math.max(0, battlingExp); }
        public void setBattlingExp(int value) { battlingExp = Math.max(0, value); }
        public void addBattlingExp(int value) { if (value > 0) battlingExp += value; }

        public int getCookingExp() { return Math.max(0, cookingExp); }
        public void setCookingExp(int value) { cookingExp = Math.max(0, value); }
        public void addCookingExp(int value) { if (value > 0) cookingExp += value; }

        public int getLevelFromTotalExp(int totalExp) {
            int exp = Math.max(0, totalExp);
            int level = 0;

            while (level < MAX_LEVEL) {
                int needed = getRequiredExpToAdvanceFrom(level);
                if (exp < needed) break;
                exp -= needed;
                level++;
            }

            return level;
        }

        public int getProgressFromTotalExp(int totalExp) {
            int exp = Math.max(0, totalExp);
            int level = 0;

            while (level < MAX_LEVEL) {
                int needed = getRequiredExpToAdvanceFrom(level);
                if (exp < needed) break;
                exp -= needed;
                level++;
            }

            return level >= MAX_LEVEL ? 0 : exp;
        }

        public int getRequiredExpToNextLevelFromTotalExp(int totalExp) {
            int level = getLevelFromTotalExp(totalExp);
            if (level >= MAX_LEVEL) return 0;
            return getRequiredExpToAdvanceFrom(level);
        }

        public int getFarmingLevel() { return getLevelFromTotalExp(getFarmingExp()); }
        public int getGatheringLevel() { return getLevelFromTotalExp(getGatheringExp()); }
        public int getCraftingLevel() { return getLevelFromTotalExp(getCraftingExp()); }
        public int getFishingLevel() { return getLevelFromTotalExp(getFishingExp()); }
        public int getBattlingLevel() { return getLevelFromTotalExp(getBattlingExp()); }
        public int getCookingLevel() { return getLevelFromTotalExp(getCookingExp()); }

        public String getFarmingDisplay() { return buildDisplay("Farming", getFarmingExp()); }
        public String getGatheringDisplay() { return buildDisplay("Gathering", getGatheringExp()); }
        public String getCraftingDisplay() { return buildDisplay("Crafting", getCraftingExp()); }
        public String getFishingDisplay() { return buildDisplay("Fishing", getFishingExp()); }
        public String getBattlingDisplay() { return buildDisplay("Battling", getBattlingExp()); }
        public String getCookingDisplay() { return buildDisplay("Cooking", getCookingExp()); }

        private String buildDisplay(String name, int totalExp) {
            int level = getLevelFromTotalExp(totalExp);
            if (level >= MAX_LEVEL) {
                return name + " : " + level + "(MAX)";
            }
            int progress = getProgressFromTotalExp(totalExp);
            int required = getRequiredExpToNextLevelFromTotalExp(totalExp);
            return name + " : " + level + "(" + progress + "/" + required + ")";
        }

        private int getRequiredExpToAdvanceFrom(int currentLevel) {
            if (currentLevel >= MAX_LEVEL) return 0;
            long next = Math.round(Math.pow(4.0 * (currentLevel + 1), 2.1));
            long current = Math.round(Math.pow(4.0 * currentLevel, 2.1));
            return (int) Math.max(1L, next - current);
        }
    }
    
    private final PlayerState playerState = new PlayerState(100);
    private final Inventory chestInventory = new Inventory(0, CHEST_SLOT_CAPACITY);

    private SpawnTag pendingSpawn = SpawnTag.DEFAULT;
    private String returnFxml = null;
    private SpawnTag returnSpawn = SpawnTag.DEFAULT;
    private String activeFarmMapKey = "home_map";
    private final Map<String, FarmState> farmByMap = new HashMap<>();
    private final Map<String, TreeMapState> treesByMap = new HashMap<>();
    private final Map<String, MobMapState> mobsByMap = new HashMap<>();

    private int shopRandomDay = -1;
    private List<Integer> dailyRandomShopItemIds = new ArrayList<>();
    private int shopStockDay = -1;
    private final Map<Integer, Integer> dailyRemainingShopStock = new LinkedHashMap<>();
    private final List<Integer> fixedShopItemIds = new ArrayList<>();
    private int randomShopItemCount = DEFAULT_RANDOM_SHOP_ITEM_COUNT;

    private int activeCookingOutputItemId = -1;
    private int activeCookingOutputQuantity = 1;
    private double activeCookingCompleteAtSeconds = -1;
    private FoodEffectOutcome activeCookingOutcome = null;

    private int activeCraftingOutputItemId = -1;
    private int activeCraftingOutputQuantity = 1;
    private double activeCraftingCompleteAtSeconds = -1;
    private int activeCraftingHpDelta = 0;
    private int activeCraftingStaminaDelta = 0;
    private boolean activeCraftingSuccess = true;

    private final Map<Integer, Deque<FoodEffectOutcome>> cookedDishEffectsByItemId = new HashMap<>();
    private final Set<String> unlockedRecipeKeys = new LinkedHashSet<>();
    private ActiveSmeltJob activeSmeltJob = null;

    private GameState() {
        seedDefaultFixedShopItems();
    }

    public void resetForNewGame() {
        clearInventoryItems(playerState.getInventory());
        replaceCoins(playerState.getInventory(), NEW_GAME_STARTING_COINS);
        playerState.clearAllHotbarItems();

        clearInventoryItems(chestInventory);
        seedStarterChestBonus();

        clearFarmState(getFarmState("home_map"));
        clearFarmState(getFarmState("Home_interior"));
        clearTreeStates();
        clearMobStates();

        playerSkills.clear();
        activeFarmMapKey = "home_map";
        pendingSpawn = SpawnTag.DEFAULT;
        returnFxml = null;
        returnSpawn = SpawnTag.DEFAULT;
        currentMapKey = "home_map";
        playerX = 0;
        playerY = 0;

        activeCookingOutputItemId = -1;
        activeCookingOutputQuantity = 1;
        activeCookingCompleteAtSeconds = -1;
        activeCookingOutcome = null;

        activeCraftingOutputItemId = -1;
        activeCraftingOutputQuantity = 1;
        activeCraftingCompleteAtSeconds = -1;
        activeCraftingHpDelta = 0;
        activeCraftingStaminaDelta = 0;
        activeCraftingSuccess = true;

        cookedDishEffectsByItemId.clear();
        unlockedRecipeKeys.clear();
        ensureAutomaticRecipeUnlocks();
        activeSmeltJob = null;

        resetSmeltProgressionState();

        dailyRandomShopItemIds = new ArrayList<>();
        shopRandomDay = -1;
        shopStockDay = -1;
        dailyRemainingShopStock.clear();
        saveMenuMode = false;
        preferredStatusPanelGroup = StatusPanelGroup.STATUS;
    }

    public void resetSmeltProgressionState() {
        double nowSeconds = GameTimeManager.get().totalSecondsProperty().get();
        smeltCompletedBySourceItemId.clear();
        smeltExpCarry = 0.0;
        smeltPointCapCarry = 0.0;
        smeltPointCap = DEFAULT_SMELT_POINT_CAP;
        smeltPoints = DEFAULT_SMELT_POINT_CAP;
        lastSmeltPointUpdateSeconds = Math.max(0.0, nowSeconds);
    }

    private void seedStarterChestBonus() {
        ItemDatabase.loadItems();
        addStarterChestItem(resolveStarterItemId("watering_can", "watering can", "Watering Can"), 1);
        addStarterChestItem(resolveStarterItemId("sickle", "Sickle"), 1);
    }

    private void clearInventoryItems(Inventory inventory) {
        if (inventory == null) return;
        var itemIds = new ArrayList<>(inventory.itemsProperty().keySet());
        for (Integer itemId : itemIds) {
            int qty = inventory.getCount(itemId);
            if (qty > 0) {
                inventory.removeItem(itemId, qty);
            }
        }
    }

    private void replaceCoins(Inventory inventory, int targetCoins) {
        if (inventory == null) return;
        int currentCoins = inventory.getCoins();
        if (currentCoins > 0) {
            inventory.spendCoins(currentCoins);
        }
        if (targetCoins > 0) {
            inventory.addCoins(targetCoins);
        }
    }

    private void clearFarmState(FarmState farmState) {
        if (farmState == null) return;
        farmState.getAllPlots().clear();
        farmState.clearEquippedTools();
    }

    private int resolveStarterItemId(String... candidateNames) {
        if (candidateNames == null) return -1;

        for (String name : candidateNames) {
            if (name == null || name.isBlank()) continue;
            model.item itm = ItemDatabase.getItemByName(name);
            if (itm != null && itm.getItem_id() > 0) {
                return itm.getItem_id();
            }
        }
        return -1;
    }

    private void addStarterChestItem(int itemId, int qty) {
        if (itemId > 0 && qty > 0 && chestInventory.getCount(itemId) <= 0) {
            chestInventory.addItem(itemId, qty);
        }
    }

    private void seedDefaultFixedShopItems() {
        if (!fixedShopItemIds.isEmpty()) return;

        ItemDatabase.loadItems();

        fixedShopItemIds.clear();

        addPreferredFixedShopItem(fixedShopItemIds, "Fishing Rod");
        addPreferredFixedShopItem(fixedShopItemIds, "Pickaxe");
        addPreferredFixedShopItem(fixedShopItemIds, "Axe");

        addPreferredFixedShopItem(fixedShopItemIds, "Wheat Seed");
        addPreferredFixedShopItem(fixedShopItemIds, "Pineapple Seed");
        addPreferredFixedShopItem(fixedShopItemIds, "Onion Seed");
        addPreferredFixedShopItem(fixedShopItemIds, "Grape Seed");
        addPreferredFixedShopItem(fixedShopItemIds, "Cucumber Seed");

        fillFixedShopItemsByType(fixedShopItemIds, true);
        fillFixedShopItemsByType(fixedShopItemIds, false);
    }

    private int resolveShopItemId(String... candidateNames) {
        return resolveStarterItemId(candidateNames);
    }

    private void addPreferredFixedShopItem(List<Integer> target, String... candidateNames) {
        if (target == null || target.size() >= DEFAULT_FIXED_SHOP_ITEM_COUNT) {
            return;
        }

        int itemId = resolveShopItemId(candidateNames);
        addFixedShopItem(itemId, target);
    }

    private void fillFixedShopItemsByType(List<Integer> target, boolean seedsFirst) {
        if (target == null || target.size() >= DEFAULT_FIXED_SHOP_ITEM_COUNT) {
            return;
        }

        List<Integer> candidates = new ArrayList<>();
        for (model.item itm : ItemDatabase.getAllItems()) {
            if (itm == null) {
                continue;
            }

            int itemId = itm.getItem_id();
            if (!ItemDatabase.isShopDisplayableItem(itemId)) {
                continue;
            }

            boolean isSeed = ItemDatabase.isSeedItem(itemId);
            boolean isEquipment = ItemDatabase.isEquipmentItem(itemId);

            if (seedsFirst && !isSeed) {
                continue;
            }
            if (!seedsFirst && !isEquipment) {
                continue;
            }

            candidates.add(itemId);
        }

        candidates.sort((left, right) -> {
            String leftName = ItemDatabase.getItemName(left);
            String rightName = ItemDatabase.getItemName(right);
            if (leftName == null && rightName == null) return 0;
            if (leftName == null) return 1;
            if (rightName == null) return -1;
            return leftName.compareToIgnoreCase(rightName);
        });

        for (Integer itemId : candidates) {
            if (target.size() >= DEFAULT_FIXED_SHOP_ITEM_COUNT) {
                break;
            }
            addFixedShopItem(itemId, target);
        }
    }

    private void addFixedShopItem(int itemId, List<Integer> target) {
        if (target == null || itemId <= 0 || target.contains(itemId) || !ItemDatabase.isShopDisplayableItem(itemId)) return;
        target.add(itemId);
    }

    public PlayerState getPlayerState() { return playerState; }
    public Inventory getChestInventory() { return chestInventory; }

    public void setPendingSpawn(SpawnTag tag) { 
        this.pendingSpawn = (tag == null) ? SpawnTag.DEFAULT : tag; 
    }
    
    public SpawnTag consumePendingSpawn() { 
        SpawnTag t = pendingSpawn; pendingSpawn = SpawnTag.DEFAULT; return t; 
    }
    
    public FarmState getFarmState(String mapKey) { 
        return farmByMap.computeIfAbsent(mapKey, k -> new FarmState()); 
    }

    public TreeMapState getTreeState(String mapKey) {
        if (mapKey == null || mapKey.isBlank()) {
            mapKey = "home_map";
        }
        return treesByMap.computeIfAbsent(mapKey, k -> new TreeMapState());
    }

    public Map<String, TreeMapState> getAllTreeStates() {
        return treesByMap;
    }

    public void clearTreeStates() {
        treesByMap.clear();
    }

    public MobMapState getMobState(String mapKey) {
        if (mapKey == null || mapKey.isBlank()) {
            mapKey = "home_map";
        }
        return mobsByMap.computeIfAbsent(mapKey, k -> new MobMapState());
    }

    public Map<String, MobMapState> getAllMobStates() {
        return mobsByMap;
    }

    public void clearMobStates() {
        mobsByMap.clear();
    }
    public void setReturnScene(String fxml, SpawnTag spawn) { 
        this.returnFxml = fxml; this.returnSpawn = (spawn == null) ? SpawnTag.DEFAULT : spawn; 
    }
    public String getReturnFxml() { 
        return returnFxml; 
    }
    public SpawnTag getReturnSpawn() { 
        return returnSpawn; 
    }
    public void setActiveFarmMapKey(String mapKey) {
        if (mapKey == null || mapKey.isBlank()) {
            this.activeFarmMapKey = "home_map";
            return;
        }
        this.activeFarmMapKey = mapKey;
    }
    public String getActiveFarmMapKey() {
        return activeFarmMapKey;
    }
    public int getShopRandomDay() { 
        return shopRandomDay; 
    }
    public List<Integer> getFixedShopItemIds() {
        return List.copyOf(fixedShopItemIds);
    }
    public List<Integer> getFixedShopItemIdsForDay(int dayNumber) {
        List<Integer> result = new ArrayList<>();
        int targetCount = fixedShopItemIds.size();
        
        for (Integer itemId : fixedShopItemIds) {
            if (targetCount > 0 && result.size() >= targetCount) break;
            if (itemId != null) {
                addFixedShopItem(itemId, result);
            }
        }

        return List.copyOf(result);
    }
    public void setFixedShopItemIds(List<Integer> itemIds) {
        fixedShopItemIds.clear();
        if (itemIds == null) return;
        for (Integer itemId : itemIds) {
            if (itemId != null) {
                addFixedShopItem(itemId, fixedShopItemIds);
            }
        }
    }
    public boolean canAccessRecipe(String recipeKey) {
        if (recipeKey == null || recipeKey.isBlank()) {
            return false;
        }
        return unlockedRecipeKeys.contains(normalizeRecipeKey(recipeKey));
    }
    public boolean isRecipeUnlocked(String recipeKey) {
        return recipeKey != null && unlockedRecipeKeys.contains(normalizeRecipeKey(recipeKey));
    }
    public void unlockRecipe(String recipeKey) {
        if (recipeKey == null || recipeKey.isBlank()) {
            return;
        }
        unlockedRecipeKeys.add(normalizeRecipeKey(recipeKey));
    }
    public Set<String> getUnlockedRecipeKeys() {
        return Set.copyOf(unlockedRecipeKeys);
    }
    public void setUnlockedRecipeKeys(Iterable<String> recipeKeys) {
        unlockedRecipeKeys.clear();
        if (recipeKeys == null) {
            return;
        }

        for (String recipeKey : recipeKeys) {
            if (recipeKey == null || recipeKey.isBlank()) {
                continue;
            }
            unlockedRecipeKeys.add(normalizeRecipeKey(recipeKey));
        }
    }
    public void clearUnlockedRecipes() {
        unlockedRecipeKeys.clear();
    }
    public int getRandomShopItemCount() {
        return Math.max(0, randomShopItemCount);
    }
    public void setRandomShopItemCount(int count) {
        this.randomShopItemCount = Math.max(0, count);
    }
    public int getTotalShopItemCount() {
        return fixedShopItemIds.size() + getRandomShopItemCount();
    }
    public void setShopRandomDay(int day) { 
        this.shopRandomDay = day; 
    }
    public List<Integer> getDailyRandomShopItemIds() { 
        return List.copyOf(dailyRandomShopItemIds); 
    }
    public void setDailyRandomShopItemIds(List<Integer> ids) { 
        this.dailyRandomShopItemIds = (ids == null) ? new ArrayList<>() : new ArrayList<>(ids); 
    }
    public int getShopStockDay() { 
        return shopStockDay; 
    }
    public void setShopStockDay(int day) { 
        this.shopStockDay = day; 
    }
    public boolean hasDailyRemainingShopStockForDay(int day) { 
        return shopStockDay == day && !dailyRemainingShopStock.isEmpty(); 
    }
    public Map<Integer, Integer> getDailyRemainingShopStock() { 
        return new LinkedHashMap<>(dailyRemainingShopStock); 
    }
    public int getRemainingShopStock(int itemId, int fallbackValue) { 
        return dailyRemainingShopStock.getOrDefault(itemId, fallbackValue); 
    }
    public void setRemainingShopStock(int itemId, int remainingQty) { 
        dailyRemainingShopStock.put(itemId, Math.max(0, remainingQty)); 
    }
    public void setDailyRemainingShopStock(Map<Integer, Integer> stockByItemId) {
        dailyRemainingShopStock.clear();
        if (stockByItemId == null) return;
        for (Map.Entry<Integer, Integer> e : stockByItemId.entrySet()) {
            int qty = (e.getValue() == null) ? 0 : Math.max(0, e.getValue());
            dailyRemainingShopStock.put(e.getKey(), qty);
        }
    }
    public void clearDailyRemainingShopStock() { dailyRemainingShopStock.clear(); }
    public void resetDailyShopStateForNewDay(int day) { this.shopRandomDay = day; this.dailyRandomShopItemIds.clear(); this.shopStockDay = day; this.dailyRemainingShopStock.clear(); }

    public void startCookingJob(int outputItemId, double completeAtSeconds) {
        startCookingJob(outputItemId, 1, completeAtSeconds, 0, 0, true);
    }

    public void startCookingJob(int outputItemId, double completeAtSeconds, int hpDelta, int staminaDelta, boolean success) {
        startCookingJob(outputItemId, 1, completeAtSeconds, hpDelta, staminaDelta, 0, 0, 0, 0, success);
    }

    public void startCookingJob(int outputItemId, int outputQuantity, double completeAtSeconds, int hpDelta, int staminaDelta, boolean success) {
        startCookingJob(outputItemId, outputQuantity, completeAtSeconds, hpDelta, staminaDelta, 0, 0, 0, 0, success);
    }

    public void startCookingJob(int outputItemId, int outputQuantity, double completeAtSeconds, int hpDelta, int staminaDelta, int strDelta, int vitDelta, int dexDelta, int agiDelta, boolean success) {
        this.activeCookingOutputItemId = outputItemId;
        this.activeCookingOutputQuantity = Math.max(0, outputQuantity);
        this.activeCookingCompleteAtSeconds = completeAtSeconds;
        this.activeCookingOutcome = new FoodEffectOutcome(hpDelta, staminaDelta, strDelta, vitDelta, dexDelta, agiDelta, success);
    }

    public boolean hasActiveCookingJob() { return activeCookingOutputItemId > 0 && activeCookingCompleteAtSeconds >= 0; }
    public int getActiveCookingOutputItemId() { return activeCookingOutputItemId; }
    public int getActiveCookingOutputQuantity() { return activeCookingOutputQuantity; }
    public double getActiveCookingCompleteAtSeconds() { return activeCookingCompleteAtSeconds; }
    public int getActiveCookingHpDelta() { return activeCookingOutcome == null ? 0 : activeCookingOutcome.getHpDelta(); }
    public int getActiveCookingStaminaDelta() { return activeCookingOutcome == null ? 0 : activeCookingOutcome.getStaminaDelta(); }
    public int getActiveCookingStrDelta() { return activeCookingOutcome == null ? 0 : activeCookingOutcome.getStrDelta(); }
    public int getActiveCookingVitDelta() { return activeCookingOutcome == null ? 0 : activeCookingOutcome.getVitDelta(); }
    public int getActiveCookingDexDelta() { return activeCookingOutcome == null ? 0 : activeCookingOutcome.getDexDelta(); }
    public int getActiveCookingAgiDelta() { return activeCookingOutcome == null ? 0 : activeCookingOutcome.getAgiDelta(); }
    public boolean isActiveCookingSuccess() { return activeCookingOutcome != null && activeCookingOutcome.isSuccess(); }
    public boolean isCookingReady(double nowSeconds) { return hasActiveCookingJob() && nowSeconds >= activeCookingCompleteAtSeconds; }
    public void clearCookingJob() { activeCookingOutputItemId = -1; activeCookingOutputQuantity = 1; activeCookingCompleteAtSeconds = -1; activeCookingOutcome = null; }

    public void startCraftingJob(int outputItemId, double completeAtSeconds, int hpDelta, int staminaDelta, boolean success) {
        startCraftingJob(outputItemId, 1, completeAtSeconds, hpDelta, staminaDelta, success);
    }

    public void startCraftingJob(int outputItemId, int outputQuantity, double completeAtSeconds, int hpDelta, int staminaDelta, boolean success) {
        this.activeCraftingOutputItemId = outputItemId;
        this.activeCraftingOutputQuantity = Math.max(0, outputQuantity);
        this.activeCraftingCompleteAtSeconds = completeAtSeconds;
        this.activeCraftingHpDelta = hpDelta;
        this.activeCraftingStaminaDelta = staminaDelta;
        this.activeCraftingSuccess = success;
    }
    public boolean hasActiveCraftingJob() { return activeCraftingOutputItemId > 0 && activeCraftingCompleteAtSeconds >= 0; }
    public int getActiveCraftingOutputItemId() { return activeCraftingOutputItemId; }
    public int getActiveCraftingOutputQuantity() { return activeCraftingOutputQuantity; }
    public double getActiveCraftingCompleteAtSeconds() { return activeCraftingCompleteAtSeconds; }
    public int getActiveCraftingHpDelta() { return activeCraftingHpDelta; }
    public int getActiveCraftingStaminaDelta() { return activeCraftingStaminaDelta; }
    public boolean isActiveCraftingSuccess() { return activeCraftingSuccess; }
    public boolean isCraftingReady(double nowSeconds) { return hasActiveCraftingJob() && nowSeconds >= activeCraftingCompleteAtSeconds; }
    public void clearCraftingJob() { activeCraftingOutputItemId = -1; activeCraftingOutputQuantity = 1; activeCraftingCompleteAtSeconds = -1; activeCraftingHpDelta = 0; activeCraftingStaminaDelta = 0; activeCraftingSuccess = true; }

    public void queueCookedDishEffect(int itemId, int hpDelta, int staminaDelta, boolean success) {
        queueCookedDishEffect(itemId, hpDelta, staminaDelta, 0, 0, 0, 0, success);
    }

    public void queueCookedDishEffect(int itemId, int hpDelta, int staminaDelta, int strDelta, int vitDelta, int dexDelta, int agiDelta, boolean success) {
        if (itemId <= 0) return;
        cookedDishEffectsByItemId.computeIfAbsent(itemId, k -> new ArrayDeque<>()).addLast(new FoodEffectOutcome(hpDelta, staminaDelta, strDelta, vitDelta, dexDelta, agiDelta, success));
    }

    public FoodEffectOutcome pollCookedDishEffect(int itemId) {
        Deque<FoodEffectOutcome> q = cookedDishEffectsByItemId.get(itemId);
        if (q == null || q.isEmpty()) return null;
        FoodEffectOutcome outcome = q.removeFirst();
        if (q.isEmpty()) cookedDishEffectsByItemId.remove(itemId);
        return outcome;
    }

    private String normalizeRecipeKey(String key) {
        return key == null ? "" : key.trim().toLowerCase().replace(' ', '_');
    }

    public void startSmeltJob(int sourceItemId, int resultItemId, int quantity, int costPerOre, double secondsPerOre, double startedAtSeconds) {
        if (sourceItemId <= 0 || resultItemId <= 0 || quantity <= 0 || secondsPerOre <= 0) return;
        this.activeSmeltJob = new ActiveSmeltJob(sourceItemId, resultItemId, quantity, costPerOre, secondsPerOre, startedAtSeconds);
    }
    public boolean hasActiveSmeltJob() { return activeSmeltJob != null; }
    public ActiveSmeltJob getActiveSmeltJob() { return activeSmeltJob; }
    public void clearActiveSmeltJob() { activeSmeltJob = null; }
    public int getReadySmeltCount(double nowSeconds) { return activeSmeltJob == null ? 0 : activeSmeltJob.getReadyCount(nowSeconds); }
    public int collectReadySmeltedItems(double nowSeconds) {
        if (activeSmeltJob == null) return 0;
        int sourceItemId = activeSmeltJob.getSourceItemId();
        int ready = activeSmeltJob.getReadyCount(nowSeconds);
        if (ready <= 0) return 0;
        if (!playerState.getInventory().addItem(activeSmeltJob.getResultItemId(), ready)) {
            return 0;
        }

        boolean completesJob = ready >= activeSmeltJob.getQuantity();
        int remaining = activeSmeltJob.getQuantity() - ready;
        if (remaining > 0) {
            double nextStart = activeSmeltJob.getStartedAtSeconds() + (ready * activeSmeltJob.getSecondsPerOre());
            activeSmeltJob = new ActiveSmeltJob(activeSmeltJob.getSourceItemId(), activeSmeltJob.getResultItemId(), remaining, activeSmeltJob.getCostPerOre(), activeSmeltJob.getSecondsPerOre(), nextStart);
        } else {
            activeSmeltJob = null;
        }
        if (completesJob) {
            applySmeltCompletionRewards(sourceItemId, nowSeconds);
        }
        return ready;
    }
    public void cancelSmeltSelectionBackToInventory(int itemId, int quantity) {
        if (itemId > 0 && quantity > 0) playerState.getInventory().addItem(itemId, quantity);
    }
    
    public synchronized int getSmeltPointCap() {
        return smeltPointCap;
    }

    public synchronized void setSmeltPointCap(int newCap, double nowSeconds) {
        refreshSmeltPoints(nowSeconds);
        smeltPointCap = Math.max(0, newCap);
        if (smeltPoints > smeltPointCap) {
            smeltPoints = smeltPointCap;
        }
    }

    public synchronized int getCurrentSmeltPoints(double nowSeconds) {
        refreshSmeltPoints(nowSeconds);
        return (int) Math.floor(smeltPoints);
    }

    public synchronized boolean canAffordSmeltPoints(int cost, double nowSeconds) {
        if (cost <= 0) return true;
        return getCurrentSmeltPoints(nowSeconds) >= cost;
    }

    public synchronized boolean spendSmeltPoints(int cost, double nowSeconds) {
        if (cost < 0) return false;
        refreshSmeltPoints(nowSeconds);
        if (cost > Math.floor(smeltPoints)) {
            return false;
        }
        smeltPoints = Math.max(0.0, smeltPoints - cost);
        lastSmeltPointUpdateSeconds = Math.max(0.0, nowSeconds);
        return true;
    }

    public synchronized void restoreSmeltPoints(int amount, double nowSeconds) {
        if (amount <= 0) return;
        refreshSmeltPoints(nowSeconds);
        smeltPoints = Math.min(smeltPointCap, smeltPoints + amount);
    }

    public synchronized void fillSmeltPoints(double nowSeconds) {
        refreshSmeltPoints(nowSeconds);
        smeltPoints = smeltPointCap;
    }

    private synchronized void refreshSmeltPoints(double nowSeconds) {
        double safeNow = Math.max(0.0, nowSeconds);

        if (safeNow < lastSmeltPointUpdateSeconds) {
            lastSmeltPointUpdateSeconds = safeNow;
            return;
        }

        if (smeltPoints >= smeltPointCap) {
            smeltPoints = smeltPointCap;
            lastSmeltPointUpdateSeconds = safeNow;
            return;
        }

        if (smeltPointCap <= 0) {
            smeltPoints = 0.0;
            lastSmeltPointUpdateSeconds = safeNow;
            return;
        }

        double elapsed = safeNow - lastSmeltPointUpdateSeconds;
        if (elapsed <= 0) return;

        double regen = (elapsed * smeltPointCap) / SMELT_POINT_FULL_REGEN_SECONDS;
        if (regen > 0) {
            smeltPoints = Math.min(smeltPointCap, smeltPoints + regen);
            lastSmeltPointUpdateSeconds = safeNow;
        }
    }

    private double computeSmeltAttemptMultiplier(int attemptNumber) {
        if (attemptNumber <= 1) {
            return 1.0;
        }
        if (attemptNumber < 10) {
            return 0.5;
        }
        int decade = (attemptNumber - 10) / 10;
        int tier = 2 + Math.max(0, decade);
        return Math.pow(0.5, tier);
    }

    public synchronized int getCompletedSmeltCount(int sourceItemId) {
        if (sourceItemId <= 0) return 0;
        return smeltCompletedBySourceItemId.getOrDefault(sourceItemId, 0);
    }

    public synchronized Map<Integer, Integer> getCompletedSmeltCountsSnapshot() {
        return new LinkedHashMap<>(smeltCompletedBySourceItemId);
    }

    public synchronized double getSmeltExpCarry() {
        return smeltExpCarry;
    }

    public synchronized double getSmeltPointCapCarry() {
        return smeltPointCapCarry;
    }

    public synchronized double getSmeltPointsExact(double nowSeconds) {
        refreshSmeltPoints(nowSeconds);
        return smeltPoints;
    }

    public synchronized double getLastSmeltPointUpdateSeconds() {
        return lastSmeltPointUpdateSeconds;
    }

    public synchronized void loadSmeltProgressionState(
        int pointCap,
        double points,
        double lastUpdateSeconds,
        double expCarry,
        double pointCapCarry,
        double nowSeconds
    ) {
        smeltPointCap = Math.max(0, pointCap);
        smeltPoints = Math.max(0.0, Math.min(points, (double) smeltPointCap));
        lastSmeltPointUpdateSeconds = Math.max(0.0, Math.min(lastUpdateSeconds, Math.max(0.0, nowSeconds)));
        smeltExpCarry = Math.max(0.0, expCarry);
        smeltPointCapCarry = Math.max(0.0, pointCapCarry);
    }

    public synchronized void clearSmeltCounts() {
        smeltCompletedBySourceItemId.clear();
    }

    public synchronized void setCompletedSmeltCount(int sourceItemId, int count) {
        if (sourceItemId <= 0) return;
        int safeCount = Math.max(0, count);
        if (safeCount <= 0) {
            smeltCompletedBySourceItemId.remove(sourceItemId);
        } else {
            smeltCompletedBySourceItemId.put(sourceItemId, safeCount);
        }
    }

    public synchronized int computeAdjustedSmeltCostPerOre(int sourceItemId, int baseCostPerOre) {
        int safeBase = Math.max(0, baseCostPerOre);
        if (safeBase <= 0) return 0;

        int attempt = getCompletedSmeltCount(sourceItemId) + 1;
        double multiplier = computeSmeltAttemptMultiplier(attempt);
        int adjusted = (int) Math.ceil(safeBase * multiplier);
        return Math.max(1, adjusted);
    }

    public synchronized void applySmeltCompletionRewards(int sourceItemId, double nowSeconds) {
        if (sourceItemId <= 0 || playerState == null) {
            return;
        }

        int attempt = getCompletedSmeltCount(sourceItemId) + 1;
        double multiplier = computeSmeltAttemptMultiplier(attempt);

        smeltExpCarry += 1.0 * multiplier;
        smeltPointCapCarry += 1.0 * multiplier;

        int expToApply = (int) Math.floor(smeltExpCarry + 1e-9);
        if (expToApply > 0) {
            playerState.addExp(expToApply);
            smeltExpCarry = Math.max(0.0, smeltExpCarry - expToApply);
        }

        int capToApply = (int) Math.floor(smeltPointCapCarry + 1e-9);
        if (capToApply > 0) {
            setSmeltPointCap(smeltPointCap + capToApply, nowSeconds);
            smeltPointCapCarry = Math.max(0.0, smeltPointCapCarry - capToApply);
        }

        setCompletedSmeltCount(sourceItemId, getCompletedSmeltCount(sourceItemId) + 1);
    }

    private int currentAccountId = -1;
    private int currentCharacterId = -1;
    private int currentSaveId = -1;
    private String currentMapKey = "home_map";
    private double playerX = 0;
    private double playerY = 0;


    public int getCurrentAccountId() {
        return currentAccountId;
    }

    public void setCurrentAccountId(int currentAccountId) {
        this.currentAccountId = currentAccountId;
    }

    public int getCurrentCharacterId() {
        return currentCharacterId;
    }

    public void setCurrentCharacterId(int currentCharacterId) {
        this.currentCharacterId = currentCharacterId;
    }

    public int getCurrentSaveId() {
        return currentSaveId;
    }

    public void setCurrentSaveId(int currentSaveId) {
        this.currentSaveId = currentSaveId;
    }

    public boolean hasActiveSave() {
        return currentSaveId > 0;
    }

    public String getCurrentMapKey() {
        return currentMapKey;
    }

    public void setCurrentMapKey(String currentMapKey) {
        this.currentMapKey = currentMapKey;
    }

    public double getPlayerX() {
        return playerX;
    }

    public void setPlayerX(double playerX) {
        this.playerX = playerX;
    }

    public double getPlayerY() {
        return playerY;
    }

    public void setPlayerY(double playerY) {
        this.playerY = playerY;
    }
    
    private int selectedSaveSlot = 1;

    public int getSelectedSaveSlot() {
        return selectedSaveSlot;
    }

    public void setSelectedSaveSlot(int selectedSaveSlot) {
        if (selectedSaveSlot < 1) selectedSaveSlot = 1;
        if (selectedSaveSlot > 4) selectedSaveSlot = 4;
        this.selectedSaveSlot = selectedSaveSlot;
    }
    
    private boolean saveMenuMode = false;
    private StatusPanelGroup preferredStatusPanelGroup = StatusPanelGroup.STATUS;

    public boolean isSaveMenuMode() {
        return saveMenuMode;
    }

    public void setSaveMenuMode(boolean saveMenuMode) {
        this.saveMenuMode = saveMenuMode;
    }

    public StatusPanelGroup getPreferredStatusPanelGroup() {
        return preferredStatusPanelGroup == null ? StatusPanelGroup.STATUS : preferredStatusPanelGroup;
    }

    public void setPreferredStatusPanelGroup(StatusPanelGroup preferredStatusPanelGroup) {
        this.preferredStatusPanelGroup = preferredStatusPanelGroup == null ? StatusPanelGroup.STATUS : preferredStatusPanelGroup;
    }
    
    
    private String currentPlayerName = "";
    private String currentWorldName = "";

    public String getCurrentPlayerName() {
        return currentPlayerName;
    }

    public void setCurrentPlayerName(String currentPlayerName) {
        this.currentPlayerName = currentPlayerName == null ? "" : currentPlayerName.trim();
    }

    public String getCurrentWorldName() {
        return currentWorldName;
    }

    public void setCurrentWorldName(String currentWorldName) {
        this.currentWorldName = currentWorldName == null ? "" : currentWorldName.trim();
    }
    
    private int loadedCurrentHp = 45;
    private int loadedCurrentStamina = 50;

    public int getLoadedCurrentHp() { return loadedCurrentHp; }
    public void setLoadedCurrentHp(int v) { loadedCurrentHp = v; }

    public int getLoadedCurrentStamina() { return loadedCurrentStamina; }
    public void setLoadedCurrentStamina(int v) { loadedCurrentStamina = v; }
}
