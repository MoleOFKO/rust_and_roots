package game;

import model.PlayerState;
import java.util.HashMap;
import java.util.Map;

import util.SpawnTag;
import model.FarmState;
/**
 * Persistent, cross-scene game state (singleton).
 * Keeps player data alive between map loads.
 */
public final class GameState {
    private static final GameState INSTANCE = new GameState();
    public static GameState get() { return INSTANCE; }

    private final PlayerState playerState = new PlayerState(100); // starting coins

    // ---- Scene transition spawn ----
    private SpawnTag pendingSpawn = SpawnTag.DEFAULT;

    // ---- Per-map farm persistence ----
    private final Map<String, FarmState> farmByMap = new HashMap<>();
    
    private GameState() {}

    public PlayerState getPlayerState() {
        return playerState;
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
    
}
