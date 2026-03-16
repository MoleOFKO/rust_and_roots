package game;

import java.util.ArrayList;
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

    private final PlayerState playerState = new PlayerState(100); // starting coins
    private final Inventory chestInventory = new Inventory(0);

    // ---- Scene transition spawn ----
    private SpawnTag pendingSpawn = SpawnTag.DEFAULT;

    // ---------- Shop navigation ----------
    private String returnFxml = null;
    private SpawnTag returnSpawn = SpawnTag.DEFAULT;

    // ---------- Shop merchant catalog ----------
    private List<model.Merchant.ShopItem> currentMerchantItems = List.of();

    // ---- Per-map farm persistence ----
    private final Map<String, FarmState> farmByMap = new HashMap<>();

    // ---------- Daily shop random catalog ----------
    private int shopRandomDay = -1;
    private List<Integer> dailyRandomShopItemIds = new ArrayList<>();

    // ---------- Daily shop remaining stock ----------
    // Key = itemId, Value = remaining quantity for the current shop day
    private int shopStockDay = -1;
    private final Map<Integer, Integer> dailyRemainingShopStock = new LinkedHashMap<>();

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

    // =========================================================
    // Daily persistent shop stock
    // =========================================================

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

    /**
     * Call this when a new day starts and the shop should regenerate its stock.
     */
    public void resetDailyShopStateForNewDay(int day) {
        this.shopRandomDay = day;
        this.dailyRandomShopItemIds.clear();

        this.shopStockDay = day;
        this.dailyRemainingShopStock.clear();
    }
}