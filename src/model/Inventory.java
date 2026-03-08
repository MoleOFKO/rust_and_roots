package model;

import java.util.Collections;
import java.util.EnumMap;
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

    public Inventory(int startingCoins) {
        coins.set(Math.max(0, startingCoins));
    }

    // --- Coins ---
    public int getCoins() { 
        return coins.get(); 
    }
    
    public IntegerProperty coinsProperty() { 
        return coins; 
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

    public void addItem(int itemId, int amount) {
        if (itemId <= 0 || amount <= 0) return;
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

    // Keep snapshot API for debugging/alerts/etc.
    public Map<Integer, Integer> snapshot() {
        Map<Integer, Integer> snap = new LinkedHashMap<>();
        items.forEach((k, v) -> snap.put(k, v.get()));
        return Collections.unmodifiableMap(snap);
    }
}