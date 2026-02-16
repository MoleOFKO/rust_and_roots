package model;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class Inventory {
    private final IntegerProperty coins= new SimpleIntegerProperty();
     // Each item has its own observable quantity property (best for TableView live-updates)
    private final ObservableMap<ItemType, IntegerProperty> items = FXCollections.observableHashMap();

    public Inventory(int startingCoins) {
        coins.set(Math.max(0, startingCoins));
    }

    // --- Coins ---
    public int getCoins() { return coins.get(); }
    public IntegerProperty coinsProperty() { return coins; }

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
    public ObservableMap<ItemType, IntegerProperty> itemsProperty() {
        return items;
    }
    
    public int getCount(ItemType type) {
        if (type == null) return 0;
        IntegerProperty p = items.get(type);
        return p == null ? 0 : p.get();
    }

    public IntegerProperty countProperty(ItemType type) {
        if (type == null) return new SimpleIntegerProperty(0);
        return items.computeIfAbsent(type, t -> new SimpleIntegerProperty(0));
    }

    public void addItem(ItemType type, int amount) {
        if (type == null || amount <= 0) return;
        IntegerProperty p = items.computeIfAbsent(type, t -> new SimpleIntegerProperty(0));
        p.set(p.get() + amount);
    }

    public boolean removeItem(ItemType type, int amount) {
        if (type == null || amount <= 0) return false;

        IntegerProperty p = items.get(type);
        int have = (p == null) ? 0 : p.get();
        if (have < amount) return false;

        int left = have - amount;
        if (left == 0) items.remove(type);
        else p.set(left);

        return true;
    }

    // Keep snapshot API for debugging/alerts/etc.
    public Map<ItemType, Integer> snapshot() {
        EnumMap<ItemType, Integer> snap = new EnumMap<>(ItemType.class);
        items.forEach((k, v) -> snap.put(k, v.get()));
        return Collections.unmodifiableMap(snap);
    }
}