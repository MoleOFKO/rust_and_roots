package controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import game.GameLoopManager;
import game.GameState;
import game.GameTimeManager;
import game.InteractionManager;
import game.SceneController;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import model.CollisionRegistry;
import model.Farm;
import model.FarmPlotRegistrar;
import model.InteractionZone;
import model.Player;
import model.PlayerState;
import ui.InventoryRow;
import ui.InventoryUIManager;
import util.SceneManager;
import util.SpawnTag;
import util.WorldScaler;

/**
 * BaseMapController
 *
 * Shared reusable map systems:
 * - player creation + input + loop
 * - spawn handling
 * - exit registration
 * - optional farm bootstrap
 * - optional day/night background binding
 * - optional hotbar + inventory overlay
 * - optional settings / time debug controls
 *
 * Any map can use the shared UI systems by declaring the same fx:id values
 * in its FXML. Missing nodes are handled safely.
 */
public abstract class BaseMapController implements Initializable, SceneController {

    // ---------- Required common nodes ----------
    @FXML protected StackPane view;
    @FXML protected Group world;

    @FXML protected Group human;
    @FXML protected ImageView human_img;
    @FXML protected Rectangle human_hitbox;

    @FXML protected Group walls;

    // ---------- Spawn markers ----------
    @FXML protected Rectangle spawn_default;
    @FXML protected Rectangle spawn_from_left;
    @FXML protected Rectangle spawn_from_right;
    @FXML protected Rectangle spawn_from_up;
    @FXML protected Rectangle spawn_from_down;
    @FXML protected Rectangle spawn_from_door;

    // ---------- Exit trigger zones ----------
    @FXML protected Rectangle exit_left;
    @FXML protected Rectangle exit_right;
    @FXML protected Rectangle exit_up;
    @FXML protected Rectangle exit_down;
    @FXML protected Rectangle exit_door;

    // ---------- Optional farm nodes ----------
    @FXML protected Group farm1;
    @FXML protected Rectangle farm1_detection;
    @FXML protected Button farm1_button;

    // ---------- Optional shared background / day-night ----------
    @FXML protected ImageView bg;

    // ---------- Optional shared hotbar ----------
    @FXML protected HBox hotbar;
    @FXML protected StackPane Hslot;
    @FXML protected ImageView slotImg;
    @FXML protected Button slotBtn;
    @FXML protected Label slotLb;
    @FXML protected StackPane Hslot1;
    @FXML protected ImageView slotImg1;
    @FXML protected Button slotBtn1;
    @FXML protected Label slotLb1;
    @FXML protected StackPane Hslot2;
    @FXML protected ImageView slotImg2;
    @FXML protected Button slotBtn2;
    @FXML protected Label slotLb2;
    @FXML protected StackPane Hslot3;
    @FXML protected ImageView slotImg3;
    @FXML protected Button slotBtn3;
    @FXML protected Label slotLb3;
    @FXML protected StackPane Hslot4;
    @FXML protected ImageView slotImg4;
    @FXML protected Button slotBtn4;
    @FXML protected Label slotLb4;
    @FXML protected StackPane Hslot5;
    @FXML protected Button slotBtn5;

    // ---------- Optional shared inventory overlay ----------
    @FXML protected AnchorPane inventoryOverlay;
    @FXML protected TableView<InventoryRow> invTable;
    @FXML protected TableColumn<InventoryRow, String> colItem;
    @FXML protected TableColumn<InventoryRow, Number> colQty;
    @FXML protected Label invCoinsLabel;

    // ---------- Optional shared settings / time controls ----------
    @FXML protected Label timeLabel;
    @FXML protected Button pauseTimeBtn;
    @FXML protected Slider timeSpeedSlider;
    @FXML protected Label timeSpeedValueLabel;
    @FXML protected Button btnMorning;
    @FXML protected Button btnNight;
    @FXML protected Button btnAddHour;

    // ---------- Runtime ----------
    protected GameState gameState;
    protected Player player;
    protected PlayerState playerState;
    protected Farm farm;
    protected InteractionManager interactionManager;
    protected GameLoopManager gameLoop;
    protected InventoryUIManager inventoryUIManager;

    protected final List<StackPane> hotbarSlots = new ArrayList<>();
    protected final List<Button> hotbarSlotButtons = new ArrayList<>();
    protected int selectedHotbarIndex = -1;

    protected Image dayBg;
    protected Image nightBg;
    protected ChangeListener<Boolean> nightListener;

    protected abstract String mapKey();
    protected abstract void configureExits();

    protected void configureFarm(Farm farm) {
        // default: do nothing
    }

    /** Override to enable day/night background switching on this map. */
    protected String dayBackgroundPath() {
        return null;
    }

    /** Override to enable day/night background switching on this map. */
    protected String nightBackgroundPath() {
        return null;
    }

    /** Hook for subclasses after shared systems are ready. */
    protected void afterBaseInitialize() {
        // default: do nothing
    }

    @Override
    public void init(GameState gs) {
        this.gameState = gs;
        if (human != null) {
            applySpawn(gs.consumePendingSpawn());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (gameState == null) gameState = GameState.get();

        playerState = gameState.getPlayerState();

        player = new Player(
            playerState,
            human,
            human_img,
            human_hitbox,
            64, 64,
            5,
            150_000_000L
        );

        view.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                player.attachInput(newScene);
            }
        });

        if (walls != null) {
            player.setWalls(CollisionRegistry.collectWalls(walls));
        }

        applySpawn(gameState.consumePendingSpawn());

        interactionManager = new InteractionManager();
        configureExits();

        setupFarmIfPresent();
        setupInventoryUiIfPresent();
        setupHotbarIfPresent();
        setupDayNightBindingIfConfigured();
        setupSettingsTabIfPresent();

        GameTimeManager.get().start();
        startLoop();

        new WorldScaler(view, world);
        player.start();

        afterBaseInitialize();
    }

    protected final void registerExit(Rectangle zone, String targetFxml, SpawnTag targetSpawn) {
        if (zone == null) return;

        InteractionZone iz = new InteractionZone(zone, inside -> {
            if (inside) {
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

    protected void selectHotbarSlot(int idx) {
        if (idx < 0 || idx >= hotbarSlots.size()) return;
        if (idx == selectedHotbarIndex) return;

        if (selectedHotbarIndex >= 0) {
            StackPane prev = hotbarSlots.get(selectedHotbarIndex);
            prev.getStyleClass().remove("hotbar-selected");
        }

        StackPane now = hotbarSlots.get(idx);
        if (!now.getStyleClass().contains("hotbar-selected")) {
            now.getStyleClass().add("hotbar-selected");
        }

        selectedHotbarIndex = idx;
        System.out.println("Selected slot: " + (idx + 1));
    }

    protected void toggleInventory() {
        if (inventoryOverlay == null) return;
        setInventoryOpen(!inventoryOverlay.isVisible());
    }

    protected void setInventoryOpen(boolean open) {
        if (inventoryOverlay == null) return;

        inventoryOverlay.setVisible(open);
        inventoryOverlay.setManaged(open);

        if (view != null && view.getScene() != null) {
            if (open) {
                player.clearInputState();
                view.getScene().setOnKeyPressed(null);
                view.getScene().setOnKeyReleased(null);
            } else {
                player.attachInput(view.getScene());
            }
        }
    }

    private void setupFarmIfPresent() {
        if (farm1 == null || farm1_detection == null || farm1_button == null) return;

        farm = new Farm(
            farm1,
            farm1_detection,
            farm1_button,
            gameState.getFarmState(mapKey())
        );

        FarmPlotRegistrar.registerPlots(farm, farm1);
        configureFarm(farm);
    }

    private void setupInventoryUiIfPresent() {
        if (invTable == null || colItem == null || colQty == null || invCoinsLabel == null) return;

        inventoryUIManager = new InventoryUIManager(
            invTable,
            colItem,
            colQty,
            invCoinsLabel,
            player.getInventory()
        );

        if (inventoryOverlay != null) {
            inventoryOverlay.setVisible(false);
            inventoryOverlay.setManaged(false);
        }
    }

    private void setupHotbarIfPresent() {
        hotbarSlots.clear();
        hotbarSlotButtons.clear();

        addHotbarSlot(Hslot, slotBtn);
        addHotbarSlot(Hslot1, slotBtn1);
        addHotbarSlot(Hslot2, slotBtn2);
        addHotbarSlot(Hslot3, slotBtn3);
        addHotbarSlot(Hslot4, slotBtn4);

        for (int i = 0; i < hotbarSlotButtons.size(); i++) {
            final int idx = i;
            hotbarSlotButtons.get(i).setOnAction(e -> selectHotbarSlot(idx));
        }

        if (slotBtn5 != null) {
            slotBtn5.setOnAction(e -> toggleInventory());
        }

        if (!hotbarSlots.isEmpty()) {
            selectHotbarSlot(0);
        }
    }

    private void addHotbarSlot(StackPane slot, Button button) {
        if (slot != null && button != null) {
            hotbarSlots.add(slot);
            hotbarSlotButtons.add(button);
        }
    }

    private void setupDayNightBindingIfConfigured() {
        String dayPath = dayBackgroundPath();
        String nightPath = nightBackgroundPath();
        if (bg == null || dayPath == null || nightPath == null) return;

        dayBg = loadImageOrNull(dayPath);
        nightBg = loadImageOrNull(nightPath);

        if (dayBg != null && bg.getImage() == null) {
            bg.setImage(dayBg);
        }

        GameTimeManager time = GameTimeManager.get();
        nightListener = (obs, wasNight, isNight) -> {
            if (bg == null) return;
            if (isNight) {
                if (nightBg != null) bg.setImage(nightBg);
            } else {
                if (dayBg != null) bg.setImage(dayBg);
            }
        };

        time.nightProperty().addListener(nightListener);
        nightListener.changed(time.nightProperty(), !time.isNight(), time.isNight());

        bg.sceneProperty().addListener((o, oldScene, newScene) -> {
            if (newScene == null && nightListener != null) {
                time.nightProperty().removeListener(nightListener);
                nightListener = null;
            }
        });
    }

    protected Image loadImageOrNull(String path) {
        var url = getClass().getResource(path);
        if (url == null) {
            System.err.println("[BaseMapController] Missing resource: " + path);
            return null;
        }
        return new Image(url.toExternalForm());
    }

    private void setupSettingsTabIfPresent() {
        GameTimeManager time = GameTimeManager.get();

        if (timeLabel != null) {
            timeLabel.textProperty().bind(time.formattedProperty());
        }

        if (timeSpeedSlider != null) {
            timeSpeedSlider.valueProperty().bindBidirectional(time.timeScaleProperty());
        }

        if (timeSpeedValueLabel != null) {
            timeSpeedValueLabel.textProperty().bind(
                time.timeScaleProperty().asString("x%.1f")
            );
        }

        if (pauseTimeBtn != null) {
            pauseTimeBtn.setOnAction(e -> time.togglePause());
        }
        if (btnMorning != null) {
            btnMorning.setOnAction(e -> time.setTimeOfDay(8, 0));
        }
        if (btnNight != null) {
            btnNight.setOnAction(e -> time.setTimeOfDay(20, 0));
        }
        if (btnAddHour != null) {
            btnAddHour.setOnAction(e -> time.addMinutes(60));
        }
    }

    private void startLoop() {
        gameLoop = new GameLoopManager(() -> {
            Bounds playerBounds = human_hitbox.localToScene(human_hitbox.getBoundsInLocal());

            if (interactionManager != null) {
                interactionManager.updateAll(playerBounds);
            }

            if (farm != null) {
                farm.updatePlayerBounds(playerBounds);
                double nowSeconds = GameTimeManager.get().totalSecondsProperty().get();
                farm.update(nowSeconds);
            }
        });

        gameLoop.start();
    }
}
