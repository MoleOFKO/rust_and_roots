package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import game.GameLoopManager;
import game.GameState;
import game.GameTimeManager;
import game.InteractionManager;
import game.SceneController;
import model.CollisionRegistry;
import model.Farm;
import model.FarmPlotRegistrar;
import model.Player;
import model.PlayerState;
import model.InteractionZone;
import util.SceneManager;
import util.SpawnTag;
import util.WorldScaler;

/**
 * BaseMapController:
 * - Creates Player VIEW for this map using persistent PlayerState from GameState
 * - Applies spawn based on GameState.consumePendingSpawn()
 * - Updates trigger zones via InteractionManager + InteractionZone
 * - Optional farm support (if farm nodes exist in the FXML)
 *
 * Subclasses only override:
 *  - mapKey()
 *  - configureExits()
 *  - optionally configureFarm(...)
 */
public abstract class BaseMapController implements Initializable, SceneController {

    // ---------- Required common nodes ----------
    @FXML protected StackPane view;
    @FXML protected Group world;

    @FXML protected Group human;
    @FXML protected ImageView human_img;
    @FXML protected Rectangle human_hitbox;

    @FXML protected Group walls;

    // ---------- Spawn markers (optional but recommended) ----------
    @FXML protected Rectangle spawn_default;
    @FXML protected Rectangle spawn_from_left;
    @FXML protected Rectangle spawn_from_right;
    @FXML protected Rectangle spawn_from_up;
    @FXML protected Rectangle spawn_from_down;
    @FXML protected Rectangle spawn_from_door;

    // ---------- Exit trigger zones (optional; define what you need per map) ----------
    @FXML protected Rectangle exit_left;
    @FXML protected Rectangle exit_right;
    @FXML protected Rectangle exit_up;
    @FXML protected Rectangle exit_down;
    @FXML protected Rectangle exit_door;

    // ---------- Optional farm nodes (only present on farm maps) ----------
    @FXML protected Group farm1;
    @FXML protected Rectangle farm1_detection;
    @FXML protected javafx.scene.control.Button farm1_button;

    // ---------- Runtime ----------
    protected GameState gameState;
    protected Player player;         // VIEW/controller for this scene
    protected PlayerState playerState; // persistent DATA
    protected Farm farm;             // optional
    protected InteractionManager interactionManager;
    protected GameLoopManager gameLoop;

    // ---- Subclass provides a stable key used to store per-map state ----
    protected abstract String mapKey();

    // ---- Subclass wires which exits go to which FXML + spawn tags ----
    protected abstract void configureExits();

    // ---- Subclass can customize farm seeds, harvest mapping, images, etc. ----
    protected void configureFarm(Farm farm) {
        // default: do nothing
    }

    @Override
    public void init(GameState gs) {
        this.gameState = gs;
        // If FXML already initialized, apply spawn now.
        if (human != null) {
            applySpawn(gs.consumePendingSpawn());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // If SceneManager injected first, use it; otherwise fallback to singleton
        if (gameState == null) gameState = GameState.get();

        // ---- Persistent player data ----
        playerState = gameState.getPlayerState();

        // ---- Create Player VIEW bound to this map's nodes ----
        // (Use your NEW Player(PlayerState, ...) constructor from step 1/2)
        player = new Player(
            playerState,
            human,
            human_img,
            human_hitbox,
            64, 64,
            5,
            150_000_000L
        );

        // Attach input when scene becomes available (same approach as home_mapController) 
        view.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                player.attachInput(newScene);
            }
        });

        // Walls collision
        if (walls != null) {
            player.setWalls(CollisionRegistry.collectWalls(walls)); // uses NodeCollectors by prefix "wall" 
        }

        // Spawn
        applySpawn(gameState.consumePendingSpawn());

        // Interaction system (trigger zones)
        interactionManager = new InteractionManager();
        configureExits(); // subclasses register exits using helper below

        // Optional farm
        setupFarmIfPresent();

        // Time
        GameTimeManager.get().start(); // reuse your existing time system :contentReference[oaicite:4]{index=4}

        // Loop: update triggers + farm visuals/time-based growth
        startLoop();

        // World scaling (same as home_mapController) 
        new WorldScaler(view, world);

        // Start player animation loop (your Player.start()) 
        player.start();
    }

    // -----------------------------
    // Helpers for subclasses
    // -----------------------------

    protected final void registerExit(Rectangle zone, String targetFxml, SpawnTag targetSpawn) {
        if (zone == null) return;

        InteractionZone iz = new InteractionZone(zone, inside -> {
            if (inside) {
                // stop current animations quickly (optional safety)
                if (gameLoop != null) gameLoop.stop();
                if (player != null) player.stop();

                SceneManager.switchScene(targetFxml, targetSpawn);
            }
        });

        interactionManager.addZone(iz);
    }

    protected final void applySpawn(SpawnTag tag) {
        Rectangle target = switch (tag) {
            case FROM_LEFT -> spawn_from_left;
            case FROM_RIGHT -> spawn_from_right;
            case FROM_UP -> spawn_from_up;
            case FROM_DOWN -> spawn_from_down;
            case FROM_DOOR -> spawn_from_door;
            default -> spawn_default;
        };

        if (target != null && human != null) {
            human.setLayoutX(target.getLayoutX());
            human.setLayoutY(target.getLayoutY());
        }
    }

    private void setupFarmIfPresent() {
        if (farm1 == null || farm1_detection == null || farm1_button == null) return;

        // Uses per-map persistent farm state via GameState.getFarmState(mapKey())
        farm = new Farm(
            farm1,
            farm1_detection,
            farm1_button,
            gameState.getFarmState(mapKey())
        );

        // auto-register plots by farm_img prefix, same as your current helper :contentReference[oaicite:7]{index=7}
        FarmPlotRegistrar.registerPlots(farm, farm1);

        configureFarm(farm);
    }

    private void startLoop() {
        gameLoop = new GameLoopManager(() -> {
            Bounds playerBounds = human_hitbox.localToScene(human_hitbox.getBoundsInLocal());

            // Trigger exits
            if (interactionManager != null) {
                interactionManager.updateAll(playerBounds); // same pattern as home_mapController 
            }

            // Optional farm: detection + growth update
            if (farm != null) {
                farm.updatePlayerBounds(playerBounds);
                double nowSeconds = GameTimeManager.get().totalSecondsProperty().get();
                farm.update(nowSeconds);
            }
        });

        gameLoop.start();
    }
}