package game;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.FarmState;
import model.Inventory;
import model.PlayerState;
import util.SpawnTag;

public final class GameState {
    private static final GameState INSTANCE = new GameState();

    private static final int DEFAULT_SMELT_POINT_CAP = 10;
    private static final double DEFAULT_SMELT_POINT_REGEN_SECONDS = 15.0;

    private int smeltPointCap = DEFAULT_SMELT_POINT_CAP;
    private double smeltPoints = DEFAULT_SMELT_POINT_CAP;
    private double lastSmeltPointUpdateSeconds = 0.0;
    private double smeltPointRegenSeconds = DEFAULT_SMELT_POINT_REGEN_SECONDS;
    
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

    private final PlayerState playerState = new PlayerState(100);
    private final Inventory chestInventory = new Inventory(0);

    private SpawnTag pendingSpawn = SpawnTag.DEFAULT;
    private String returnFxml = null;
    private SpawnTag returnSpawn = SpawnTag.DEFAULT;
    private List<model.Merchant.ShopItem> currentMerchantItems = List.of();
    private final Map<String, FarmState> farmByMap = new HashMap<>();

    private int shopRandomDay = -1;
    private List<Integer> dailyRandomShopItemIds = new ArrayList<>();
    private int shopStockDay = -1;
    private final Map<Integer, Integer> dailyRemainingShopStock = new LinkedHashMap<>();

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
    private ActiveSmeltJob activeSmeltJob = null;

    private GameState() {}

    public PlayerState getPlayerState() { return playerState; }
    public Inventory getChestInventory() { return chestInventory; }

    public void setPendingSpawn(SpawnTag tag) { this.pendingSpawn = (tag == null) ? SpawnTag.DEFAULT : tag; }
    public SpawnTag consumePendingSpawn() { SpawnTag t = pendingSpawn; pendingSpawn = SpawnTag.DEFAULT; return t; }
    public FarmState getFarmState(String mapKey) { return farmByMap.computeIfAbsent(mapKey, k -> new FarmState()); }
    public void setReturnScene(String fxml, SpawnTag spawn) { this.returnFxml = fxml; this.returnSpawn = (spawn == null) ? SpawnTag.DEFAULT : spawn; }
    public String getReturnFxml() { return returnFxml; }
    public SpawnTag getReturnSpawn() { return returnSpawn; }
    public void setCurrentMerchantItems(List<model.Merchant.ShopItem> items) { this.currentMerchantItems = (items == null) ? List.of() : List.copyOf(items); }
    public List<model.Merchant.ShopItem> getCurrentMerchantItems() { return currentMerchantItems; }
    public int getShopRandomDay() { return shopRandomDay; }
    public void setShopRandomDay(int day) { this.shopRandomDay = day; }
    public List<Integer> getDailyRandomShopItemIds() { return List.copyOf(dailyRandomShopItemIds); }
    public void setDailyRandomShopItemIds(List<Integer> ids) { this.dailyRandomShopItemIds = (ids == null) ? new ArrayList<>() : new ArrayList<>(ids); }
    public int getShopStockDay() { return shopStockDay; }
    public void setShopStockDay(int day) { this.shopStockDay = day; }
    public boolean hasDailyRemainingShopStockForDay(int day) { return shopStockDay == day && !dailyRemainingShopStock.isEmpty(); }
    public Map<Integer, Integer> getDailyRemainingShopStock() { return new LinkedHashMap<>(dailyRemainingShopStock); }
    public int getRemainingShopStock(int itemId, int fallbackValue) { return dailyRemainingShopStock.getOrDefault(itemId, fallbackValue); }
    public void setRemainingShopStock(int itemId, int remainingQty) { dailyRemainingShopStock.put(itemId, Math.max(0, remainingQty)); }
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

    public void startSmeltJob(int sourceItemId, int resultItemId, int quantity, int costPerOre, double secondsPerOre, double startedAtSeconds) {
        if (sourceItemId <= 0 || resultItemId <= 0 || quantity <= 0 || secondsPerOre <= 0) return;
        this.activeSmeltJob = new ActiveSmeltJob(sourceItemId, resultItemId, quantity, costPerOre, secondsPerOre, startedAtSeconds);
    }
    public boolean hasActiveSmeltJob() { return activeSmeltJob != null; }
    public ActiveSmeltJob getActiveSmeltJob() { return activeSmeltJob; }
    public int getReadySmeltCount(double nowSeconds) { return activeSmeltJob == null ? 0 : activeSmeltJob.getReadyCount(nowSeconds); }
    public int collectReadySmeltedItems(double nowSeconds) {
        if (activeSmeltJob == null) return 0;
        int ready = activeSmeltJob.getReadyCount(nowSeconds);
        if (ready <= 0) return 0;
        playerState.getInventory().addItem(activeSmeltJob.getResultItemId(), ready);
        int remaining = activeSmeltJob.getQuantity() - ready;
        if (remaining > 0) {
            double nextStart = activeSmeltJob.getStartedAtSeconds() + (ready * activeSmeltJob.getSecondsPerOre());
            activeSmeltJob = new ActiveSmeltJob(activeSmeltJob.getSourceItemId(), activeSmeltJob.getResultItemId(), remaining, activeSmeltJob.getCostPerOre(), activeSmeltJob.getSecondsPerOre(), nextStart);
        } else {
            activeSmeltJob = null;
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

    public synchronized double getSmeltPointRegenSeconds() {
        return smeltPointRegenSeconds;
    }

    public synchronized void setSmeltPointRegenSeconds(double regenSeconds, double nowSeconds) {
        refreshSmeltPoints(nowSeconds);
        smeltPointRegenSeconds = regenSeconds <= 0 ? DEFAULT_SMELT_POINT_REGEN_SECONDS : regenSeconds;
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

        if (smeltPointRegenSeconds <= 0) {
            smeltPoints = smeltPointCap;
            lastSmeltPointUpdateSeconds = safeNow;
            return;
        }

        double elapsed = safeNow - lastSmeltPointUpdateSeconds;
        if (elapsed <= 0) return;

        double regen = elapsed / smeltPointRegenSeconds;
        if (regen > 0) {
            smeltPoints = Math.min(smeltPointCap, smeltPoints + regen);
            lastSmeltPointUpdateSeconds = safeNow;
        }
    }

    private int currentCharacterId = -1;
    private int currentSaveId = -1;
    private String currentMapKey = "home_map";
    private double playerX = 0;
    private double playerY = 0;

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

    public boolean isSaveMenuMode() {
        return saveMenuMode;
    }

    public void setSaveMenuMode(boolean saveMenuMode) {
        this.saveMenuMode = saveMenuMode;
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
}
