package controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Comparator;

import model.Inventory;
import model.ItemDatabase;
import model.NodeCollectors;
import game.GameLoopManager;
import game.GameState;
import game.GameTimeManager;
import game.InteractionManager;
import game.SceneController;
import javafx.application.Platform;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.event.ActionEvent;
import javafx.scene.layout.Pane;
import model.CollisionRegistry;
import model.Farm;
import model.FarmPlotRegistrar;
import model.InteractionZone;
import model.Player;
import model.PlayerState;
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
    @FXML protected Rectangle spawn_from_farm;
    @FXML protected Rectangle spawn_from_shop;
    @FXML protected Rectangle spawn_from_bed;
    @FXML protected Rectangle spawn_from_cook;
    @FXML protected Rectangle spawn_from_craft;
    @FXML protected Rectangle spawn_from_inv;
    @FXML protected Rectangle spawn_from_melt;

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
    @FXML protected Pane inventoryOverlay;
    @FXML protected Label invCoinsLabel;

    // ---------- Optional shared settings / time controls ----------
    @FXML protected Label timeLabel;
    @FXML protected Button pauseTimeBtn;
    @FXML protected Slider timeSpeedSlider;
    @FXML protected Label timeSpeedValueLabel;
    @FXML protected Button btnMorning;
    @FXML protected Button btnNight;
    @FXML protected Button btnAddHour;

    @FXML protected Group player_inv_group;

    protected static final int DEFAULT_PLAYER_SLOT_CAPACITY = 20;

    protected record SlotData(int itemId, int quantity) {}

    protected final List<ImageView> playerInventoryImages = new ArrayList<>();
    protected final List<Label> playerInventoryLabels = new ArrayList<>();
    protected final List<Button> playerInventoryButtons = new ArrayList<>();

    protected Integer selectedPlayerInventoryItemId = null;
    
    // ---------- Runtime ----------
    protected GameState gameState;
    protected Player player;
    protected PlayerState playerState;
    protected Farm farm;
    protected InteractionManager interactionManager;
    protected GameLoopManager gameLoop;
    
    protected final List<StackPane> hotbarSlots = new ArrayList<>();
    protected final List<Button> hotbarSlotButtons = new ArrayList<>();
    protected int selectedHotbarIndex = -1;

    protected Image dayBg;
    protected Image nightBg;
    protected ChangeListener<Boolean> nightListener;

    protected abstract String mapKey();
    protected abstract void configureExits();
    
    protected static final double MAP_EXIT_DELAY_GAME_SECONDS =0.5;

    protected final List<ImageView> hotbarImages = new ArrayList<>();
    protected final List<Label> hotbarLabels = new ArrayList<>();
    protected int hotbarAssignmentIndex = 0;
    
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
        SpawnTag spawn = gs.consumePendingSpawn();

        Platform.runLater(() -> {
            boolean shouldUseSavedPosition =
                    gs.getCurrentSaveId() > 0 &&
                    mapKey().equals(gs.getCurrentMapKey()) &&
                    spawn == SpawnTag.DEFAULT;

            if (shouldUseSavedPosition) {
                human.setLayoutX(gs.getPlayerX());
                human.setLayoutY(gs.getPlayerY());
            } else {
                applySpawn(spawn);
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (gameState == null) gameState = GameState.get();

        ItemDatabase.loadItems();
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

        player.setOnToggleHotbarRequest(this::toggleHotbarVisible);
        player.setOnHotbarSlotRequest(this::selectHotbarSlot);

        view.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                player.attachInput(newScene);
            }
        });

        if (walls != null) {
            player.setWalls(CollisionRegistry.collectWalls(walls));
        }

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
        capturePlayerSnapshot();
        applyLoadedPositionIfCurrentMap();
    }

    protected final void stopAndSwitchScene(String targetFxml, SpawnTag targetSpawn) {
        capturePlayerSnapshot();
        
        if (gameLoop != null) gameLoop.stop();
        if (player != null) player.stop();
        SceneManager.switchScene(targetFxml, targetSpawn);
    }

    protected final void registerExit(Rectangle zone, String targetFxml, SpawnTag targetSpawn) {
        if (zone == null) return;

        InteractionZone iz = new InteractionZone(
            zone,
            null,
            () -> stopAndSwitchScene(targetFxml, targetSpawn),
            MAP_EXIT_DELAY_GAME_SECONDS
        );

        interactionManager.addZone(iz);
    }

    protected final void applySpawn(SpawnTag tag) {
        Rectangle target = switch (tag) {
            case FROM_LEFT -> spawn_from_left;
            case FROM_RIGHT -> spawn_from_right;
            case FROM_UP -> spawn_from_up;
            case FROM_DOWN -> spawn_from_down;
            case FROM_DOOR -> spawn_from_door;
            case FROM_FARM -> spawn_from_farm;
            case FROM_SHOP -> spawn_from_shop;
            case FROM_BED -> spawn_from_bed;
            case FROM_COOK -> spawn_from_cook;
            case FROM_CRAFT -> spawn_from_craft;
            case FROM_INV -> spawn_from_inv;
            case FROM_MELT -> spawn_from_melt;
            default -> spawn_default;
        };

        if (target == null || human == null || human_hitbox == null || human.getParent() == null) {
            return;
        }

        // Convert both into scene space
        Bounds targetScene = target.localToScene(target.getBoundsInLocal());
        Bounds hitboxScene = human_hitbox.localToScene(human_hitbox.getBoundsInLocal());

        // Convert both into the player's parent space
        Bounds targetInParent = human.getParent().sceneToLocal(targetScene);
        Bounds hitboxInParent = human.getParent().sceneToLocal(hitboxScene);

        // Align hitbox top-left to spawn marker top-left
        double dx = targetInParent.getMinX() - hitboxInParent.getMinX();
        double dy = targetInParent.getMinY() - hitboxInParent.getMinY();

        human.setLayoutX(human.getLayoutX() + dx);
        human.setLayoutY(human.getLayoutY() + dy);
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
        if(playerState != null){
            playerState.setSelectedHotbarIndex(selectedHotbarIndex);
        }
    }

    @FXML
    protected void onCloseInventory(ActionEvent event){
        setInventoryOpen(false);
    }
    
    protected void toggleHotbarVisible(){
        if(hotbar == null) return;
        setHotbarVisible(!hotbar.isVisible());
    }
    
    protected void setHotbarVisible(boolean visible){
        if(hotbar == null) return;

        hotbar.setVisible(visible);
        hotbar.setManaged(visible);
        hotbar.setDisable(!visible);

        if(playerState != null){
            playerState.setHotbarVisible(visible);
        }
    }
    
    protected void toggleInventory() {
        if (inventoryOverlay == null) return;
        setInventoryOpen(!inventoryOverlay.isVisible());
    }

    protected void setInventoryOpen(boolean open) {
        if (inventoryOverlay == null) return;

        inventoryOverlay.setVisible(open);
        inventoryOverlay.setManaged(open);
        inventoryOverlay.setDisable(!open);

        if (open) {
            refreshPlayerInventoryView();
        } else {
            clearPlayerInventorySelection();
        }

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
        setupPlayerInventoryGridIfPresent();

        if (inventoryOverlay != null) {
            inventoryOverlay.setVisible(false);
            inventoryOverlay.setManaged(false);
            inventoryOverlay.setDisable(true);
        }
    }

    private void setupHotbarIfPresent() {
        hotbarSlots.clear();
        hotbarSlotButtons.clear();
        hotbarImages.clear();
        hotbarLabels.clear();

        addHotbarSlot(Hslot, slotBtn, slotImg, slotLb);
        addHotbarSlot(Hslot1, slotBtn1, slotImg1, slotLb1);
        addHotbarSlot(Hslot2, slotBtn2, slotImg2, slotLb2);
        addHotbarSlot(Hslot3, slotBtn3, slotImg3, slotLb3);
        addHotbarSlot(Hslot4, slotBtn4, slotImg4, slotLb4);

        for (int i = 0; i < hotbarSlotButtons.size(); i++) {
            final int idx = i;
            hotbarSlotButtons.get(i).setOnAction(e -> {
                if (idx == selectedHotbarIndex && tryConsumeHotbarItem(idx)) {
                    return;
                }
                selectHotbarSlot(idx);
                hotbarAssignmentIndex = idx;
            });
        }

        if (slotBtn5 != null) {
            slotBtn5.setOnAction(e -> toggleInventory());
        }

        if (playerState != null) {
            setHotbarVisible(playerState.isHotbarVisible());
        }

        if (!hotbarSlots.isEmpty()) {
            int savedIdx = 0;
            if (playerState != null) {
                savedIdx = Math.min(playerState.getselectedHotbarIndex(), hotbarSlots.size() - 1);
            }
            selectHotbarSlot(savedIdx);
            hotbarAssignmentIndex = savedIdx;
        }

        refreshHotbarView();
    }

    private void addHotbarSlot(StackPane slot, Button button, ImageView imageView, Label label) {
        if (slot != null && button != null) {
            hotbarSlots.add(slot);
            hotbarSlotButtons.add(button);
            hotbarImages.add(imageView);
            hotbarLabels.add(label);
        }
    }
    
    protected void refreshHotbarView() {
        if (playerState == null) return;

        for (int i = 0; i < hotbarImages.size(); i++) {
            ImageView img = hotbarImages.get(i);
            Label lbl = (i < hotbarLabels.size()) ? hotbarLabels.get(i) : null;

            int itemId = playerState.getHotbarItemId(i);

            if (itemId <= 0 || player.getInventory().getCount(itemId) <= 0) {
                if (img != null) img.setImage(null);
                if (lbl != null) lbl.setText("");
                continue;
            }

            if (img != null) {
                img.setImage(ItemDatabase.getImage(itemId));
            }

            if (lbl != null) {
                lbl.setText("x" + player.getInventory().getCount(itemId));
            }
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
            if (player == null || player.isRemovedFromWorld()) {
                return;
            }

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
    
        protected int playerInventorySlotCapacity() {
        return DEFAULT_PLAYER_SLOT_CAPACITY;
    }

    protected String playerInventoryImagePrefix() {
        return "Playerinvimg";
    }

    protected String playerInventoryLabelPrefix() {
        return "Playerinvlbl";
    }

    protected String playerInventoryButtonPrefix() {
        return "Playerinvbtn";
    }

    protected void onPlayerInventorySlotSelected(int index, SlotData slot) {
        selectedPlayerInventoryItemId = (slot == null) ? null : slot.itemId();
        refreshPlayerInventorySelectionHighlight();

        if (slot != null && playerState != null) {
            playerState.setHotbarItemId(hotbarAssignmentIndex, slot.itemId());
            refreshHotbarView();
        }
    }

    protected void setupPlayerInventoryGridIfPresent() {
        if (player_inv_group == null) return;

        collectPlayerInventoryNodes();
        wirePlayerInventoryButtons();

        if (inventoryOverlay != null) {
            inventoryOverlay.setVisible(false);
            inventoryOverlay.setManaged(false);
            inventoryOverlay.setDisable(true);
        }

        refreshPlayerInventoryView();
    }

    protected void collectPlayerInventoryNodes() {
        if (!playerInventoryImages.isEmpty() || !playerInventoryLabels.isEmpty() || !playerInventoryButtons.isEmpty()) {
            return;
        }

        playerInventoryImages.addAll(
            NodeCollectors.byTypeAndPrefix(
                player_inv_group,
                ImageView.class,
                playerInventoryImagePrefix()
            )
        );
        NodeCollectors.sortByIdNumber(playerInventoryImages, playerInventoryImagePrefix());

        playerInventoryLabels.addAll(
            NodeCollectors.byTypeAndPrefix(
                player_inv_group,
                Label.class,
                playerInventoryLabelPrefix()
            )
        );
        NodeCollectors.sortByIdNumber(playerInventoryLabels, playerInventoryLabelPrefix());

        playerInventoryButtons.addAll(
            NodeCollectors.byTypeAndPrefix(
                player_inv_group,
                Button.class,
                playerInventoryButtonPrefix()
            )
        );
        NodeCollectors.sortByIdNumber(playerInventoryButtons, playerInventoryButtonPrefix());
    }

    protected void wirePlayerInventoryButtons() {
        for (int i = 0; i < playerInventoryButtons.size(); i++) {
            final int index = i;
            Button button = playerInventoryButtons.get(i);
            if (button != null) {
                button.setOnAction(e -> selectPlayerInventorySlot(index));
            }
        }
    }

    protected void selectPlayerInventorySlot(int index) {
        List<SlotData> slots = snapshotSlots(player.getInventory(), playerInventorySlotCapacity());
        if (index < 0 || index >= slots.size()) return;

        SlotData slot = slots.get(index);
        if (slot == null) return;

        onPlayerInventorySlotSelected(index, slot);
    }

    protected void refreshPlayerInventoryView() {
        if (player == null) return;
        if (playerInventoryImages.isEmpty() && playerInventoryLabels.isEmpty() && playerInventoryButtons.isEmpty()) return;

        List<SlotData> slots = snapshotSlots(player.getInventory(), playerInventorySlotCapacity());
        refreshSlots(
            slots,
            playerInventoryImages,
            playerInventoryLabels,
            playerInventoryButtons
        );
        refreshHotbarView();
    }

    protected List<SlotData> snapshotSlots(Inventory inventory, int capacity) {
        List<SlotData> result = new ArrayList<>();
        if (inventory == null) return result;

        inventory.itemsProperty().entrySet().stream()
            .filter(e -> e.getValue() != null && e.getValue().get() > 0)
            .sorted(Comparator.comparing(
                e -> itemName(e.getKey()),
                String.CASE_INSENSITIVE_ORDER
            ))
            .limit(capacity)
            .forEach(e -> result.add(new SlotData(e.getKey(), e.getValue().get())));

        return result;
    }

    protected void refreshSlots(
            List<SlotData> slots,
            List<ImageView> images,
            List<Label> labels,
            List<Button> buttons
    ) {
        int max = Math.max(images.size(), Math.max(labels.size(), buttons.size()));

        for (int i = 0; i < max; i++) {
            SlotData slot = (i < slots.size()) ? slots.get(i) : null;

            ImageView imageView = (i < images.size()) ? images.get(i) : null;
            Label label = (i < labels.size()) ? labels.get(i) : null;
            Button button = (i < buttons.size()) ? buttons.get(i) : null;

            if (slot == null) {
                if (imageView != null) {
                    imageView.setImage(null);
                    imageView.setVisible(false);
                    imageView.setOpacity(0.0);
                    imageView.setScaleX(1.0);
                    imageView.setScaleY(1.0);
                    imageView.setStyle("");
                }
                if (label != null) {
                    label.setText("");
                }
                if (button != null) {
                    button.setDisable(true);
                }
                continue;
            }

            if (imageView != null) {
                imageView.setImage(ItemDatabase.getImage(slot.itemId()));
                imageView.setVisible(true);
                imageView.setOpacity(0.9);
                imageView.setScaleX(1.0);
                imageView.setScaleY(1.0);
                imageView.setStyle("");
            }

            if (label != null) {
                label.setText("x" + slot.quantity());
            }

            if (button != null) {
                button.setDisable(false);
            }
        }

        refreshPlayerInventorySelectionHighlight();
    }

    protected void refreshPlayerInventorySelectionHighlight() {
        List<SlotData> slots = snapshotSlots(player.getInventory(), playerInventorySlotCapacity());

        for (int i = 0; i < playerInventoryImages.size(); i++) {
            ImageView imageView = playerInventoryImages.get(i);
            if (imageView == null) continue;

            SlotData slot = (i < slots.size()) ? slots.get(i) : null;
            boolean selected =
                slot != null &&
                selectedPlayerInventoryItemId != null &&
                slot.itemId() == selectedPlayerInventoryItemId;

            imageView.setStyle(selected
                ? "-fx-effect: dropshadow(gaussian, yellow, 14, 0.7, 0, 0);"
                : "");

            if (slot == null) {
                imageView.setOpacity(0.0);
                imageView.setScaleX(1.0);
                imageView.setScaleY(1.0);
            } else {
                imageView.setOpacity(selected ? 1.0 : 0.9);
                imageView.setScaleX(selected ? 1.08 : 1.0);
                imageView.setScaleY(selected ? 1.08 : 1.0);
            }
        }
    }

    protected void clearPlayerInventorySelection() {
        selectedPlayerInventoryItemId = null;
        refreshPlayerInventorySelectionHighlight();
    }

    protected boolean tryConsumeHotbarItem(int index) {
        if (playerState == null || player == null || gameState == null) return false;
        if (index < 0 || index >= playerState.getHotbarSize()) return false;

        int itemId = playerState.getHotbarItemId(index);
        if (itemId <= 0 || player.getInventory().getCount(itemId) <= 0) return false;

        GameState.FoodEffectOutcome cookedOutcome = gameState.pollCookedDishEffect(itemId);

        int hpDelta;
        int staminaDelta;
        int strDelta;
        int vitDelta;
        int dexDelta;
        int agiDelta;

        if (cookedOutcome != null) {
            hpDelta = cookedOutcome.getHpDelta();
            staminaDelta = cookedOutcome.getStaminaDelta();
            strDelta = cookedOutcome.getStrDelta();
            vitDelta = cookedOutcome.getVitDelta();
            dexDelta = cookedOutcome.getDexDelta();
            agiDelta = cookedOutcome.getAgiDelta();
        } else {
            Object itm = ItemDatabase.getItemById(itemId);
            if (itm == null) return false;

            hpDelta = extractItemStat(itm, "getHp");
            staminaDelta = extractItemStat(itm, "getStamina", "getStamana", "getStimana", "getMp");
            strDelta = extractItemStat(itm, "getStr");
            vitDelta = extractItemStat(itm, "getVit", "getVid");
            dexDelta = extractItemStat(itm, "getDex");
            agiDelta = extractItemStat(itm, "getAgi");

            if (hpDelta == 0 && staminaDelta == 0 && strDelta == 0 && vitDelta == 0 && dexDelta == 0 && agiDelta == 0) {
                return false;
            }
        }

        if (!player.getInventory().removeItem(itemId, 1)) {
            return false;
        }

        playerState.applyFoodEffect(hpDelta, staminaDelta);
        applyPersistentStatDelta("getStr", new String[]{"setStr"}, strDelta);
        applyPersistentStatDelta("getVit", new String[]{"setVit", "setVid"}, vitDelta);
        applyPersistentStatDelta("getDex", new String[]{"setDex"}, dexDelta);
        applyPersistentStatDelta("getAgi", new String[]{"setAgi"}, agiDelta);

        refreshHotbarView();
        refreshPlayerInventoryView();
        return true;
    }


    protected void applyPersistentStatDelta(String getterName, String[] setterNames, int delta) {
        if (playerState == null || delta == 0 || setterNames == null) return;

        int currentValue = extractItemStat(playerState, getterName);
        int nextValue = currentValue + delta;

        for (String setterName : setterNames) {
            if (setterName == null || setterName.isBlank()) continue;
            try {
                java.lang.reflect.Method method = playerState.getClass().getMethod(setterName, int.class);
                method.invoke(playerState, nextValue);
                return;
            } catch (ReflectiveOperationException ignored) {
            }
        }
    }

    protected int extractItemStat(Object itm, String... methodNames) {
        if (itm == null || methodNames == null) return 0;

        for (String methodName : methodNames) {
            if (methodName == null || methodName.isBlank()) continue;
            try {
                java.lang.reflect.Method method = itm.getClass().getMethod(methodName);
                Object value = method.invoke(itm);
                if (value instanceof Number number) {
                    return number.intValue();
                }
            } catch (ReflectiveOperationException ignored) {
            }
        }

        return 0;
    }

    protected String itemName(int itemId) {
        var itm = ItemDatabase.getItemById(itemId);
        return (itm == null) ? ("item-" + itemId) : itm.getItem_name();
    }
    
    protected void capturePlayerSnapshot() {
        if (gameState == null || human == null) return;

        gameState.setCurrentMapKey(mapKey());
        gameState.setPlayerX(human.getLayoutX());
        gameState.setPlayerY(human.getLayoutY());
    }
    
    protected void applyLoadedPositionIfCurrentMap() {
        if (gameState == null || human == null) return;

        if (mapKey().equals(gameState.getCurrentMapKey())) {
            human.setLayoutX(gameState.getPlayerX());
            human.setLayoutY(gameState.getPlayerY());
        }
    }
}
