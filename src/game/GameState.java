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

/**
 * Persistent, cross-scene game state (singleton).
 * Keeps player data alive between map loads.
 */
public final class GameState {
    private static final GameState INSTANCE = new GameState();

    public static GameState get() {
        return INSTANCE;
    }

    public static final class FoodEffectOutcome {
        private final int hpDelta;
        private final int staminaDelta;
        private final boolean success;

        public FoodEffectOutcome(int hpDelta, int staminaDelta, boolean success) {
            this.hpDelta = hpDelta;
            this.staminaDelta = staminaDelta;
            this.success = success;
        }

        public int getHpDelta() {
            return hpDelta;
        }

        public int getStaminaDelta() {
            return staminaDelta;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    private final PlayerState playerState = new PlayerState(100); // starting coins
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
    private double activeCookingCompleteAtSeconds = -1;
    private int activeCookingHpDelta = 0;
    private int activeCookingStaminaDelta = 0;
    private boolean activeCookingSuccess = true;

    private final Map<Integer, Deque<FoodEffectOutcome>> cookedDishEffectsByItemId = new HashMap<>();

    private GameState() {
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public Inventory getChestInventory() {
        return chestInventory;
    }

    public void setPendingSpawn(SpawnTag tag) {
        this.pendingSpawn = (tag == null) ? SpawnTag.DEFAULT : tag;
    }

    public SpawnTag consumePendingSpawn() {
        SpawnTag t = pendingSpawn;
        pendingSpawn = SpawnTag.DEFAULT;
        return t;
    }

    public FarmState getFarmState(String mapKey) {
        return farmByMap.computeIfAbsent(mapKey, k -> new FarmState());
    }

    public void setReturnScene(String fxml, SpawnTag spawn) {
        this.returnFxml = fxml;
        this.returnSpawn = (spawn == null) ? SpawnTag.DEFAULT : spawn;
    }

    public String getReturnFxml() {
        return returnFxml;
    }

    public SpawnTag getReturnSpawn() {
        return returnSpawn;
    }

    public void setCurrentMerchantItems(List<model.Merchant.ShopItem> items) {
        this.currentMerchantItems = (items == null) ? List.of() : List.copyOf(items);
    }

    public List<model.Merchant.ShopItem> getCurrentMerchantItems() {
        return currentMerchantItems;
    }

    public int getShopRandomDay() {
        return shopRandomDay;
    }

    public void setShopRandomDay(int day) {
        this.shopRandomDay = day;
    }

    public List<Integer> getDailyRandomShopItemIds() {
        return List.copyOf(dailyRandomShopItemIds);
    }

    public void setDailyRandomShopItemIds(List<Integer> ids) {
        this.dailyRandomShopItemIds = (ids == null)
                ? new ArrayList<>()
                : new ArrayList<>(ids);
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
        if (remainingQty <= 0) {
            dailyRemainingShopStock.put(itemId, 0);
        } else {
            dailyRemainingShopStock.put(itemId, remainingQty);
        }
    }

    public void setDailyRemainingShopStock(Map<Integer, Integer> stockByItemId) {
        dailyRemainingShopStock.clear();

        if (stockByItemId == null) {
            return;
        }

        for (Map.Entry<Integer, Integer> e : stockByItemId.entrySet()) {
            int itemId = e.getKey();
            int qty = (e.getValue() == null) ? 0 : Math.max(0, e.getValue());
            dailyRemainingShopStock.put(itemId, qty);
        }
    }

    public void clearDailyRemainingShopStock() {
        dailyRemainingShopStock.clear();
    }

    public void resetDailyShopStateForNewDay(int day) {
        this.shopRandomDay = day;
        this.dailyRandomShopItemIds.clear();

        this.shopStockDay = day;
        this.dailyRemainingShopStock.clear();
    }

    public void startCookingJob(int outputItemId, double completeAtSeconds, int hpDelta, int staminaDelta, boolean success) {
        this.activeCookingOutputItemId = outputItemId;
        this.activeCookingCompleteAtSeconds = completeAtSeconds;
        this.activeCookingHpDelta = hpDelta;
        this.activeCookingStaminaDelta = staminaDelta;
        this.activeCookingSuccess = success;
    }

    public boolean hasActiveCookingJob() {
        return activeCookingOutputItemId > 0 && activeCookingCompleteAtSeconds >= 0;
    }

    public int getActiveCookingOutputItemId() {
        return activeCookingOutputItemId;
    }

    public double getActiveCookingCompleteAtSeconds() {
        return activeCookingCompleteAtSeconds;
    }

    public int getActiveCookingHpDelta() {
        return activeCookingHpDelta;
    }

    public int getActiveCookingStaminaDelta() {
        return activeCookingStaminaDelta;
    }

    public boolean isActiveCookingSuccess() {
        return activeCookingSuccess;
    }

    public boolean isCookingReady(double nowSeconds) {
        return hasActiveCookingJob() && nowSeconds >= activeCookingCompleteAtSeconds;
    }

    public void queueCookedDishEffect(int itemId, int hpDelta, int staminaDelta, boolean success) {
        if (itemId <= 0) return;
        cookedDishEffectsByItemId
            .computeIfAbsent(itemId, k -> new ArrayDeque<>())
            .addLast(new FoodEffectOutcome(hpDelta, staminaDelta, success));
    }

    public FoodEffectOutcome pollCookedDishEffect(int itemId) {
        Deque<FoodEffectOutcome> q = cookedDishEffectsByItemId.get(itemId);
        if (q == null || q.isEmpty()) return null;

        FoodEffectOutcome outcome = q.removeFirst();
        if (q.isEmpty()) {
            cookedDishEffectsByItemId.remove(itemId);
        }
        return outcome;
    }

    public void clearCookingJob() {
        this.activeCookingOutputItemId = -1;
        this.activeCookingCompleteAtSeconds = -1;
        this.activeCookingHpDelta = 0;
        this.activeCookingStaminaDelta = 0;
        this.activeCookingSuccess = true;
    }
}
