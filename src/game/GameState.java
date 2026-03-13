package game;

import db.dbcon;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.PlayerState;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.image.Image;

import util.SpawnTag;
import model.FarmState;
import model.Inventory;
import model.item;
/**
 * Persistent, cross-scene game state (singleton).
 * Keeps player data alive between map loads.
 */
public final class GameState {
    private static final GameState INSTANCE = new GameState();
    public static GameState get() { return INSTANCE; }

    private final PlayerState playerState = new PlayerState(100); // starting coins
    private final Inventory chestInventory = new Inventory(0);

    // ---- Scene transition spawn ----
    private SpawnTag pendingSpawn = SpawnTag.DEFAULT;

    // ---------- Shop navigation ----------
    private String returnFxml = null;
    private util.SpawnTag returnSpawn = util.SpawnTag.DEFAULT;

    // ---------- Shop merchant catalog ----------
    private java.util.List<model.Merchant.ShopItem> currentMerchantItems = java.util.List.of();

    
    // ---- Per-map farm persistence ----
    private final Map<String, FarmState> farmByMap = new HashMap<>();
    
    private GameState() {}

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
    
    public void setReturnScene(String fxml, util.SpawnTag spawn) {
        this.returnFxml = fxml;
        this.returnSpawn = (spawn == null) ? util.SpawnTag.DEFAULT : spawn;
    }

    public String getReturnFxml() {
        return returnFxml;
    }

    public util.SpawnTag getReturnSpawn() {
        return returnSpawn;
    }

    public void setCurrentMerchantItems(java.util.List<model.Merchant.ShopItem> items) {
        this.currentMerchantItems = (items == null) ? java.util.List.of() : java.util.List.copyOf(items);
    }

    public java.util.List<model.Merchant.ShopItem> getCurrentMerchantItems() {
        return currentMerchantItems;
    }
    
    
}
