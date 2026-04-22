package model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class Inventory {
    private final IntegerProperty coins= new SimpleIntegerProperty();
     // Each item has its own observable quantity property (best for TableView live-updates)
    private final ObservableMap<Integer, IntegerProperty> items = FXCollections.observableHashMap();
    private final int slotCapacity;

    public Inventory(int startingCoins, int slotCapacity) {
        this.slotCapacity = Math.max(0, slotCapacity);
        coins.set(Math.max(0, startingCoins));
    }

    public Inventory(int startingCoins) {
        this(startingCoins, Integer.MAX_VALUE);
    }

    // --- Coins ---
    public int getCoins() { 
        return coins.get(); 
    }
    
    public IntegerProperty coinsProperty() { 
        return coins; 
    }

    public int getSlotCapacity() {
        return slotCapacity;
    }

    public void addCoins(int amount) {
        if (amount <= 0) return;
        coins.set(coins.get() + amount);
    }

    public boolean canAfford(int price) {
        return price >= 0 && coins.get() >= price;
    }

    public boolean spendCoins(int price) {
        if (!canAfford(price)) return false;
        coins.set(coins.get() - price);
        return true;
    }

    // --- Items ---
    public ObservableMap<Integer, IntegerProperty> itemsProperty() {
        return items;
    }
    
    public int getCount(int itemId) {
       
        IntegerProperty p = items.get(itemId);
        return p == null ? 0 : p.get();
    }

    public IntegerProperty countProperty(int itemId) {
        return items.computeIfAbsent(itemId, id -> new SimpleIntegerProperty(0));
    }

    public boolean canAddItem(int itemId, int amount) {
        if (itemId <= 0 || amount <= 0) return false;
        return canAddItems(Map.of(itemId, amount));
    }

    public boolean canAddItems(Map<Integer, Integer> additions) {
        if (additions == null || additions.isEmpty()) return false;

        int usedSlots = getUsedSlots();
        long requiredDeltaSlots = 0L;

        for (Map.Entry<Integer, Integer> entry : additions.entrySet()) {
            if (entry == null) continue;
            int itemId = entry.getKey() == null ? -1 : entry.getKey();
            int amount = entry.getValue() == null ? 0 : entry.getValue();
            if (itemId <= 0 || amount <= 0) continue;

            long currentCount = Math.max(0L, (long) getCount(itemId));
            long addCount = (long) amount;
            long nextCount = currentCount + addCount;

            int maxStack = Math.max(1, ItemDatabase.getMaxStackSize(itemId));
            long oldSlots = slotsNeeded(currentCount, maxStack);
            long nextSlots = slotsNeeded(nextCount, maxStack);
            requiredDeltaSlots += (nextSlots - oldSlots);
        }

        if (requiredDeltaSlots <= 0L) {
            // All additions fit into existing partial stacks.
            return usedSlots <= slotCapacity;
        }

        long totalSlots = (long) usedSlots + requiredDeltaSlots;
        return totalSlots <= (long) slotCapacity;
    }

    public boolean addItems(Map<Integer, Integer> additions) {
        if (additions == null || additions.isEmpty()) return false;
        if (!canAddItems(additions)) return false;

        for (Map.Entry<Integer, Integer> entry : additions.entrySet()) {
            if (entry == null) continue;
            int itemId = entry.getKey() == null ? -1 : entry.getKey();
            int amount = entry.getValue() == null ? 0 : entry.getValue();
            if (itemId <= 0 || amount <= 0) continue;
            internalAdd(itemId, amount);
        }

        return true;
    }

    public boolean addItem(int itemId, int amount) {
        if (itemId <= 0 || amount <= 0) return false;
        if (!canAddItem(itemId, amount)) return false;
        internalAdd(itemId, amount);
        return true;
    }

    private void internalAdd(int itemId, int amount) {
        IntegerProperty p = items.computeIfAbsent(itemId, id -> new SimpleIntegerProperty(0));
        p.set(p.get() + amount);
    }

    public boolean removeItem(int itemId, int amount) {
        if (itemId <= 0 || amount <= 0) return false;

        IntegerProperty p = items.get(itemId);
        int have = (p == null) ? 0 : p.get();
        if (have < amount) return false;

        int left = have - amount;
        if (left == 0) {
            items.remove(itemId);
        } else {
            p.set(left);
        }

        return true;
    }

    public int getUsedSlots() {
        int used = 0;
        for (Map.Entry<Integer, IntegerProperty> entry : items.entrySet()) {
            if (entry == null || entry.getValue() == null) continue;
            int itemId = entry.getKey() == null ? -1 : entry.getKey();
            int count = entry.getValue().get();
            if (itemId <= 0 || count <= 0) continue;

            int maxStack = Math.max(1, ItemDatabase.getMaxStackSize(itemId));
            long slots = slotsNeeded(count, maxStack);
            used += (int) Math.min(Integer.MAX_VALUE, slots);
            if (used >= slotCapacity) {
                // No need to count beyond capacity for typical checks.
                // Still safe if capacity is Integer.MAX_VALUE.
                if (slotCapacity != Integer.MAX_VALUE) {
                    return used;
                }
            }
        }
        return used;
    }

    public int getFreeSlots() {
        return Math.max(0, slotCapacity - getUsedSlots());
    }

    private long slotsNeeded(long quantity, int maxStack) {
        if (quantity <= 0) return 0L;
        int safeStack = Math.max(1, maxStack);
        return (quantity + safeStack - 1L) / (long) safeStack;
    }

    // Keep snapshot API for debugging/alerts/etc.
    public Map<Integer, Integer> snapshot() {
        Map<Integer, Integer> snap = new LinkedHashMap<>();
        items.forEach((k, v) -> snap.put(k, v.get()));
        return Collections.unmodifiableMap(snap);
    }
}
