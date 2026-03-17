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
        private final int strDelta;
        private final int vitDelta;
        private final int dexDelta;
        private final int agiDelta;
        private final boolean success;

        public FoodEffectOutcome(
                int hpDelta,
                int staminaDelta,
                int strDelta,
                int vitDelta,
                int dexDelta,
                int agiDelta,
                boolean success
        ) {
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
    private FoodEffectOutcome activeCookingOutcome = null;

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

    public void startCookingJob(
            int outputItemId,
            double completeAtSeconds,
            int hpDelta,
            int staminaDelta,
            int strDelta,
            int vitDelta,
            int dexDelta,
            int agiDelta,
            boolean success
    ) {
        this.activeCookingOutputItemId = outputItemId;
        this.activeCookingCompleteAtSeconds = completeAtSeconds;
        this.activeCookingOutcome = new FoodEffectOutcome(
                hpDelta,
                staminaDelta,
                strDelta,
                vitDelta,
                dexDelta,
                agiDelta,
                success
        );
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

    public boolean isActiveCookingSuccess() {
        return activeCookingOutcome != null && activeCookingOutcome.isSuccess();
    }

    public int getActiveCookingHpDelta() {
        return activeCookingOutcome == null ? 0 : activeCookingOutcome.getHpDelta();
    }

    public int getActiveCookingStaminaDelta() {
        return activeCookingOutcome == null ? 0 : activeCookingOutcome.getStaminaDelta();
    }

    public int getActiveCookingStrDelta() {
        return activeCookingOutcome == null ? 0 : activeCookingOutcome.getStrDelta();
    }

    public int getActiveCookingVitDelta() {
        return activeCookingOutcome == null ? 0 : activeCookingOutcome.getVitDelta();
    }

    public int getActiveCookingDexDelta() {
        return activeCookingOutcome == null ? 0 : activeCookingOutcome.getDexDelta();
    }

    public int getActiveCookingAgiDelta() {
        return activeCookingOutcome == null ? 0 : activeCookingOutcome.getAgiDelta();
    }

    public FoodEffectOutcome getActiveCookingOutcome() {
        return activeCookingOutcome;
    }

    public boolean isCookingReady(double nowSeconds) {
        return hasActiveCookingJob() && nowSeconds >= activeCookingCompleteAtSeconds;
    }

    public void queueCookedDishEffect(
            int itemId,
            int hpDelta,
            int staminaDelta,
            int strDelta,
            int vitDelta,
            int dexDelta,
            int agiDelta,
            boolean success
    ) {
        if (itemId <= 0) return;
        cookedDishEffectsByItemId
                .computeIfAbsent(itemId, ignored -> new ArrayDeque<>())
                .addLast(new FoodEffectOutcome(
                        hpDelta,
                        staminaDelta,
                        strDelta,
                        vitDelta,
                        dexDelta,
                        agiDelta,
                        success
                ));
    }

    public FoodEffectOutcome pollCookedDishEffect(int itemId) {
        Deque<FoodEffectOutcome> q = cookedDishEffectsByItemId.get(itemId);
        if (q == null || q.isEmpty()) {
            return null;
        }
        FoodEffectOutcome outcome = q.pollFirst();
        if (q.isEmpty()) {
            cookedDishEffectsByItemId.remove(itemId);
        }
        return outcome;
    }

    public void clearCookingJob() {
        this.activeCookingOutputItemId = -1;
        this.activeCookingCompleteAtSeconds = -1;
        this.activeCookingOutcome = null;
    }
}
