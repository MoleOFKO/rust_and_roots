package controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
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
import java.util.Objects;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
    @FXML protected Group menuInventory;
    @FXML protected Group statusTab;
    @FXML protected Group settingsTab;

    @FXML protected Button openInventory;
    @FXML protected Button openstatus;
    @FXML protected Button opensetting;

    @FXML protected ImageView btnclose;
    @FXML protected Label hotbarguide;

    // ---------- Optional shared inventory overlay ----------
    @FXML protected Group statusgroup;
    @FXML protected Group skillsgroup;
    
    @FXML protected ImageView btnskill;
    @FXML protected ImageView btnstatus;
    
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
    
    // ---------- Shared status allocation state ----------
    protected boolean statusAllocationMode = false;
    protected int pendingHpPoints = 0;
    protected int pendingSpPoints = 0;
    protected int pendingStrPoints = 0;
    protected int pendingVitPoints = 0;
    protected int pendingDexPoints = 0;
    protected int pendingAgiPoints = 0;
    
    protected boolean skillAllocationMode = false;
    protected int pendingFarmingSkillExp = 0;
    protected int pendingGatheringSkillExp = 0;
    protected int pendingCraftingSkillExp = 0;
    protected int pendingFishingSkillExp = 0;
    protected int pendingCookingSkillExp = 0;
    
    protected enum MenuTab { INVENTORY, STATUS, SETTINGS }
    protected MenuTab activeMenuTab = MenuTab.INVENTORY;
    
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
            if (human == null) return;

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
    
    protected final void bridgeSharedHotbarUi(
            HBox hotbarNode,
            Label hotbarGuideNode,
            List<StackPane> slotNodes,
            List<ImageView> slotImageNodes,
            List<Label> slotLabelNodes,
            List<Button> slotButtonNodes
    ) {
        this.hotbar = hotbarNode;
        this.hotbarguide = hotbarGuideNode;

        hotbarSlots.clear();
        hotbarImages.clear();
        hotbarLabels.clear();
        hotbarSlotButtons.clear();

        if (slotNodes != null) hotbarSlots.addAll(slotNodes);
        if (slotImageNodes != null) hotbarImages.addAll(slotImageNodes);
        if (slotLabelNodes != null) hotbarLabels.addAll(slotLabelNodes);
        if (slotButtonNodes != null) hotbarSlotButtons.addAll(slotButtonNodes);
    }

    protected final void bridgeSharedInventoryOverlayUi(
            Pane inventoryOverlayNode,
            Group menuInventoryNode,
            Group statusTabNode,
            Group settingsTabNode,
            Group statusGroupNode,
            Group skillsGroupNode,
            Group playerInventoryGroupNode,
            Button openInventoryNode,
            Button openStatusNode,
            Button openSettingNode,
            ImageView closeButtonNode,
            ImageView btnSkillNode,
            ImageView btnStatusNode,
            List<ImageView> playerInventoryImageNodes,
            List<Label> playerInventoryLabelNodes,
            List<Button> playerInventoryButtonNodes
    ) {
        this.inventoryOverlay = inventoryOverlayNode;
        this.menuInventory = menuInventoryNode;
        this.statusTab = statusTabNode;
        this.settingsTab = settingsTabNode;
        this.statusgroup = statusGroupNode;
        this.skillsgroup = skillsGroupNode;
        this.player_inv_group = playerInventoryGroupNode;

        this.openInventory = openInventoryNode;
        this.openstatus = openStatusNode;
        this.opensetting = openSettingNode;
        this.btnclose = closeButtonNode;
        this.btnskill = btnSkillNode;
        this.btnstatus = btnStatusNode;

        playerInventoryImages.clear();
        playerInventoryLabels.clear();
        playerInventoryButtons.clear();

        if (playerInventoryImageNodes != null) playerInventoryImages.addAll(playerInventoryImageNodes);
        if (playerInventoryLabelNodes != null) playerInventoryLabels.addAll(playerInventoryLabelNodes);
        if (playerInventoryButtonNodes != null) playerInventoryButtons.addAll(playerInventoryButtonNodes);
    }

    protected final void rebuildSharedUiAfterBridge() {
        setupInventoryUiIfPresent();
        setupHotbarIfPresent();
        setupMenuTabs();
        refreshPlayerInventoryView();
        refreshHotbarView();
        refreshMenuState();

        if (playerState != null) {
            setHotbarVisible(playerState.isHotbarVisible());
            int selected = playerState.getselectedHotbarIndex();
            if (selected >= 0 && selected < hotbarSlots.size()) {
                selectHotbarSlot(selected);
            }
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

        if (hotbarguide != null) {
            hotbarguide.setVisible(!visible);
            hotbarguide.setManaged(!visible);
            hotbarguide.setDisable(visible);
        }
        
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
        
        if (!open) {
            statusAllocationMode = false;
            skillAllocationMode = false;
            resetPendingStatusChanges();
            resetPendingSkillChanges();
        }
        
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

    protected void setStatusOpen(boolean open){
        if (statusgroup == null) return;
        if (btnstatus == null) return;

        statusgroup.setVisible(open);
        statusgroup.setManaged(open);
        statusgroup.setDisable(!open);
        btnskill.setVisible(open);
        btnskill.setManaged(open);
        btnskill.setDisable(!open);
    }
    
    protected void setSkillsOpen(boolean open){
        if (skillsgroup == null) return;
        if (btnskill == null) return;

        skillsgroup.setVisible(open);
        skillsgroup.setManaged(open);
        skillsgroup.setDisable(!open);
        btnstatus.setVisible(open);
        btnstatus.setManaged(open);
        btnstatus.setDisable(!open);
    }
    
    protected void setupMenuTabs() {
        setActiveMenuTab(MenuTab.INVENTORY);
    }

    protected void setActiveMenuTab(MenuTab tab) {
        activeMenuTab = (tab == null) ? MenuTab.INVENTORY : tab;

        if (menuInventory != null) {
            boolean show = activeMenuTab == MenuTab.INVENTORY;
            menuInventory.setVisible(show);
            menuInventory.setManaged(show);
            menuInventory.setDisable(!show);
        }

        if (statusTab != null) {
            boolean show = activeMenuTab == MenuTab.STATUS;
            statusTab.setVisible(show);
            statusTab.setManaged(show);
            statusTab.setDisable(!show);
        }

        if (settingsTab != null) {
            boolean show = activeMenuTab == MenuTab.SETTINGS;
            settingsTab.setVisible(show);
            settingsTab.setManaged(show);
            settingsTab.setDisable(!show);
        }

        if (activeMenuTab == MenuTab.INVENTORY) {
            refreshPlayerInventoryView();
        }

        refreshMenuButtons();
    }

    protected void refreshMenuButtons() {
        styleMenuButton(openInventory, activeMenuTab == MenuTab.INVENTORY);
        styleMenuButton(openstatus, activeMenuTab == MenuTab.STATUS);
        styleMenuButton(opensetting, activeMenuTab == MenuTab.SETTINGS);
    }

    protected void styleMenuButton(Button button, boolean active) {
        if (button == null) return;
        button.setOpacity(active ? 0.18 : 0.0);
    }

    protected void refreshMenuState() {
        refreshMenuButtons();
    }
    
    // ---------- Shared status allocation logic ----------
    protected void refreshStatusAllocationUi() {
        // subclasses override if they have status labels/buttons to refresh
    }

    protected void resetPendingStatusChanges() {
        pendingHpPoints = 0;
        pendingSpPoints = 0;
        pendingStrPoints = 0;
        pendingVitPoints = 0;
        pendingDexPoints = 0;
        pendingAgiPoints = 0;
    }

    protected int getPendingPointCount(String statKey) {
        return switch (Objects.requireNonNullElse(statKey, "")) {
            case "hp" -> pendingHpPoints;
            case "sp" -> pendingSpPoints;
            case "str" -> pendingStrPoints;
            case "vit" -> pendingVitPoints;
            case "dex" -> pendingDexPoints;
            case "agi" -> pendingAgiPoints;
            default -> 0;
        };
    }

    protected void setPendingPointCount(String statKey, int value) {
        int nextValue = Math.max(0, value);
        switch (Objects.requireNonNullElse(statKey, "")) {
            case "hp" -> pendingHpPoints = nextValue;
            case "sp" -> pendingSpPoints = nextValue;
            case "str" -> pendingStrPoints = nextValue;
            case "vit" -> pendingVitPoints = nextValue;
            case "dex" -> pendingDexPoints = nextValue;
            case "agi" -> pendingAgiPoints = nextValue;
            default -> {
            }
        }
    }

    protected int getCommittedStatIncreases(String statKey) {
        if (playerState == null) return 0;

        return switch (Objects.requireNonNullElse(statKey, "")) {
            case "hp" -> Math.max(0, playerState.getMaxHp() - 45);
            case "sp" -> Math.max(0, playerState.getMaxStamina() - 50);
            case "str" -> Math.max(0, playerState.getStr() - 1);
            case "vit" -> Math.max(0, playerState.getVit() - 1);
            case "dex" -> Math.max(0, playerState.getDex() - 1);
            case "agi" -> Math.max(0, playerState.getAgi() - 1);
            default -> 0;
        };
    }

    protected int getNextPointCost(String statKey) {
        int nextPointIndex = getCommittedStatIncreases(statKey) + getPendingPointCount(statKey);
        return 1 + Math.max(0, nextPointIndex / 10);
    }

    protected int getPendingExpCostForStat(String statKey) {
        int committed = getCommittedStatIncreases(statKey);
        int pending = getPendingPointCount(statKey);
        int total = 0;

        for (int i = 0; i < pending; i++) {
            total += 1 + ((committed + i) / 10);
        }

        return total;
    }

    protected int getPendingExpCost() {
        return getPendingExpCostForStat("hp")
            + getPendingExpCostForStat("sp")
            + getPendingExpCostForStat("str")
            + getPendingExpCostForStat("vit")
            + getPendingExpCostForStat("dex")
            + getPendingExpCostForStat("agi");
    }

    protected int getPreviewRemainingExp() {
        if (playerState == null) return 0;
        return Math.max(0, playerState.getExp() - getPendingExpCost());
    }

    protected int getCurrentCoreStatCap() {
        int dayNumber = GameTimeManager.get().dayNumberProperty().get();
        return 5 + Math.max(0, (dayNumber - 1) / 2);
    }

    protected int getCurrentStatValueWithPending(String statKey) {
        if (playerState == null) return 0;

        return switch (Objects.requireNonNullElse(statKey, "")) {
            case "hp" -> playerState.getMaxHp() + pendingHpPoints;
            case "sp" -> playerState.getMaxStamina() + pendingSpPoints;
            case "str" -> playerState.getStr() + pendingStrPoints;
            case "vit" -> playerState.getVit() + pendingVitPoints;
            case "dex" -> playerState.getDex() + pendingDexPoints;
            case "agi" -> playerState.getAgi() + pendingAgiPoints;
            default -> 0;
        };
    }

    protected int getAverageCoreStatWithPending() {
        if (playerState == null) return 0;

        int total =
            (playerState.getStr() + pendingStrPoints) +
            (playerState.getVit() + pendingVitPoints) +
            (playerState.getDex() + pendingDexPoints) +
            (playerState.getAgi() + pendingAgiPoints);

        return Math.max(0, total / 4);
    }

    protected int getHpSpPointCap() {
        return getAverageCoreStatWithPending() + getCurrentCoreStatCap();
    }

    protected int getHpSpInvestedPointsWithPending(String statKey) {
        if (playerState == null) return 0;

        return switch (Objects.requireNonNullElse(statKey, "")) {
            case "hp" -> Math.max(0, (playerState.getMaxHp() - 45) + pendingHpPoints);
            case "sp" -> Math.max(0, (playerState.getMaxStamina() - 50) + pendingSpPoints);
            default -> 0;
        };
    }

    protected boolean isStatAtCurrentCap(String statKey) {
        int coreCap = getCurrentCoreStatCap();
        int hpSpCap = getHpSpPointCap();

        return switch (Objects.requireNonNullElse(statKey, "")) {
            case "hp" -> getHpSpInvestedPointsWithPending("hp") >= hpSpCap;
            case "sp" -> getHpSpInvestedPointsWithPending("sp") >= hpSpCap;
            case "str" -> getCurrentStatValueWithPending("str") >= coreCap;
            case "vit" -> getCurrentStatValueWithPending("vit") >= coreCap;
            case "dex" -> getCurrentStatValueWithPending("dex") >= coreCap;
            case "agi" -> getCurrentStatValueWithPending("agi") >= coreCap;
            default -> false;
        };
    }

    protected boolean canIncreaseStat(String statKey) {
        return statusAllocationMode
            && playerState != null
            && !isStatAtCurrentCap(statKey)
            && getPreviewRemainingExp() >= getNextPointCost(statKey);
    }

    protected void changePendingStat(String statKey, int delta) {
        if (playerState == null || delta == 0) return;

        int currentPending = getPendingPointCount(statKey);

        if (delta < 0) {
            if (currentPending <= 0) return;
            setPendingPointCount(statKey, currentPending - 1);
            refreshStatusAllocationUi();
            return;
        }

        if (!canIncreaseStat(statKey)) return;

        setPendingPointCount(statKey, currentPending + 1);
        refreshStatusAllocationUi();
    }

    protected void applyPendingStatusChanges() {
        if (playerState == null) return;

        int spentExp = getPendingExpCost();
        if (spentExp <= 0) return;
        if (playerState.getExp() < spentExp) {
            refreshStatusAllocationUi();
            return;
        }

        playerState.setExp(playerState.getExp() - spentExp);

        if (pendingHpPoints > 0) {
            playerState.setMaxHp(playerState.getMaxHp() + pendingHpPoints);
            playerState.setHp(playerState.getMaxHp());
        }
        if (pendingSpPoints > 0) {
            playerState.setMaxStamina(playerState.getMaxStamina() + pendingSpPoints);
            playerState.setStamina(playerState.getMaxStamina());
        }
        if (pendingStrPoints > 0) playerState.setStr(playerState.getStr() + pendingStrPoints);
        if (pendingVitPoints > 0) playerState.setVit(playerState.getVit() + pendingVitPoints);
        if (pendingDexPoints > 0) playerState.setDex(playerState.getDex() + pendingDexPoints);
        if (pendingAgiPoints > 0) playerState.setAgi(playerState.getAgi() + pendingAgiPoints);

        statusAllocationMode = false;
        resetPendingStatusChanges();
        refreshStatusAllocationUi();
    }

    protected void toggleStatusAllocationMode() {
        if (playerState == null || playerState.getExp() <= 0) {
            refreshStatusAllocationUi();
            return;
        }

        if (statusAllocationMode) {
            statusAllocationMode = false;
            resetPendingStatusChanges();
        } else {
            statusAllocationMode = true;
        }

        refreshStatusAllocationUi();
    }

    
    // ---------- Shared skills allocation logic ----------
    protected void resetPendingSkillChanges() {
        pendingCookingSkillExp = 0;
        pendingCraftingSkillExp = 0;
        pendingFarmingSkillExp = 0;
        pendingFishingSkillExp = 0;
        pendingGatheringSkillExp = 0;
    }

    protected int getPendingSkillExp(String skillKey) {
        return switch (Objects.requireNonNullElse(skillKey, "")) {
            case "farming" -> pendingFarmingSkillExp;
            case "gathering" -> pendingGatheringSkillExp;
            case "crafting" -> pendingCraftingSkillExp;
            case "fishing" -> pendingFishingSkillExp;
            case "cooking" -> pendingCookingSkillExp;
            default -> 0;
        };
    }

    protected void setPendingSkillExp(String skillKey, int value) {
        int nextValue = Math.max(0, value);
        switch (Objects.requireNonNullElse(skillKey, "")) {
            case "farming" -> pendingFarmingSkillExp = nextValue;
            case "gathering" -> pendingGatheringSkillExp = nextValue;
            case "crafting" -> pendingCraftingSkillExp = nextValue;
            case "fishing" -> pendingFishingSkillExp = nextValue;
            case "cooking" -> pendingCookingSkillExp = nextValue;
            default -> {
            }
        }
    }

    protected int getPendingSkillExpTotal() {
        return pendingFarmingSkillExp
            + pendingGatheringSkillExp
            + pendingCraftingSkillExp
            + pendingFishingSkillExp
            + pendingCookingSkillExp;
    }

    protected boolean isSkillsScreenVisible() {
        return skillsgroup != null && skillsgroup.isVisible();
    }
    
    protected boolean isStatusScreenVisible() {
        return statusgroup != null && statusgroup.isVisible();
    }

    protected boolean canIncreaseSkill(String skillKey) {
        if (playerState == null || gameState == null || !skillAllocationMode) return false;

        GameState.PlayerSkills skills = gameState.getPlayerSkills();
        int currentTotalExp = switch (Objects.requireNonNullElse(skillKey, "")) {
            case "farming" -> skills.getFarmingExp();
            case "gathering" -> skills.getGatheringExp();
            case "crafting" -> skills.getCraftingExp();
            case "fishing" -> skills.getFishingExp();
            case "cooking" -> skills.getCookingExp();
            default -> -1;
        };

        if (currentTotalExp < 0) return false;
        if (playerState.getExp() < getPendingSkillExpTotal() + 1) return false;

        int previewTotal = currentTotalExp + getPendingSkillExp(skillKey);
        int previewLevel = skills.getLevelFromTotalExp(previewTotal);
        return previewLevel < GameState.PlayerSkills.MAX_LEVEL;
    }

    protected void changePendingSkill(String skillKey, int delta) {
        if (playerState == null || delta == 0 || !skillAllocationMode) return;

        int currentPending = getPendingSkillExp(skillKey);

        if (delta < 0) {
            if (currentPending <= 0) return;
            setPendingSkillExp(skillKey, currentPending - 1);
            refreshStatusAllocationUi();
            return;
        }

        if (!canIncreaseSkill(skillKey)) return;

        setPendingSkillExp(skillKey, currentPending + 1);
        refreshStatusAllocationUi();
    }

    protected void applyPendingSkillChanges() {
        if (playerState == null || gameState == null) return;

        int spentExp = getPendingSkillExpTotal();
        if (spentExp <= 0) return;
        if (playerState.getExp() < spentExp) {
            refreshStatusAllocationUi();
            return;
        }

        playerState.setExp(playerState.getExp() - spentExp);

        if (pendingFarmingSkillExp > 0) gameState.getPlayerSkills().addFarmingExp(pendingFarmingSkillExp);
        if (pendingGatheringSkillExp > 0) gameState.getPlayerSkills().addGatheringExp(pendingGatheringSkillExp);
        if (pendingCraftingSkillExp > 0) gameState.getPlayerSkills().addCraftingExp(pendingCraftingSkillExp);
        if (pendingFishingSkillExp > 0) gameState.getPlayerSkills().addFishingExp(pendingFishingSkillExp);
        if (pendingCookingSkillExp > 0) gameState.getPlayerSkills().addCookingExp(pendingCookingSkillExp);

        skillAllocationMode = false;
        resetPendingSkillChanges();
        refreshStatusAllocationUi();
    }

    protected void toggleSkillAllocationMode() {
        if (playerState == null || playerState.getExp() <= 0) {
            refreshStatusAllocationUi();
            return;
        }

        if (skillAllocationMode) {
            skillAllocationMode = false;
            resetPendingSkillChanges();
        } else {
            skillAllocationMode = true;
        }

        refreshStatusAllocationUi();
    }
    
    // ---------- Shared status action delegates ----------
    protected void onStatusHpMinus() { changePendingStat("hp", -1); }
    protected void onStatusHpPlus() { changePendingStat("hp", 1); }
    protected void onStatusSpMinus() { changePendingStat("sp", -1); }
    protected void onStatusSpPlus() { changePendingStat("sp", 1); }
    protected void onStatusStrMinus() { changePendingStat("str", -1); }
    protected void onStatusStrPlus() { changePendingStat("str", 1); }
    protected void onStatusVitMinus() { changePendingStat("vit", -1); }
    protected void onStatusVitPlus() { changePendingStat("vit", 1); }
    protected void onStatusDexMinus() { changePendingStat("dex", -1); }
    protected void onStatusDexPlus() { changePendingStat("dex", 1); }
    protected void onStatusAgiMinus() { changePendingStat("agi", -1); }
    protected void onStatusAgiPlus() { changePendingStat("agi", 1); }
    protected void onStatusUsePoint() { toggleStatusAllocationMode(); }
    protected void onStatusConfirm() { applyPendingStatusChanges(); }
     // ---------- Shared skill action delegates ----------
    protected void onSkillFarmingMinus() { changePendingSkill("farming", -1); }
    protected void onSkillFarmingPlus() { changePendingSkill("farming", 1); }
    protected void onSkillGatheringMinus() { changePendingSkill("gathering", -1); }
    protected void onSkillGatheringPlus() { changePendingSkill("gathering", 1); }
    protected void onSkillCraftingMinus() { changePendingSkill("crafting", -1); }
    protected void onSkillCraftingPlus() { changePendingSkill("crafting", 1); }
    protected void onSkillFishingMinus() { changePendingSkill("fishing", -1); }
    protected void onSkillFishingPlus() { changePendingSkill("fishing", 1); }
    protected void onSkillCookingMinus() { changePendingSkill("cooking", -1); }
    protected void onSkillCookingPlus() { changePendingSkill("cooking", 1); }

    protected void onSharedUsePoint() {
        if (isSkillsScreenVisible()) {
            toggleSkillAllocationMode();
        } else {
            toggleStatusAllocationMode();
        }
    }

    protected void onSharedConfirm() {
        if (isSkillsScreenVisible()) {
            applyPendingSkillChanges();
        } else {
            applyPendingStatusChanges();
        }
    }

    protected int getVisiblePendingExpCost() {
        return isSkillsScreenVisible() ? getPendingSkillExpTotal() : getPendingExpCost();
    }

    protected String formatVisibleExpLabel() {
        if (playerState == null) return "EXP : 0";
        int pendingExpCost = getVisiblePendingExpCost();
        return "EXP : " + playerState.getExp() + (pendingExpCost > 0 ? " (-" + pendingExpCost + ")" : "");
    }

    protected String formatPendingSkillCost(int value) {
        return value <= 0 ? "" : " (-" + value + ")";
    }

    protected String appendPendingSkillCost(String base, int pendingCost) {
        return base + formatPendingSkillCost(pendingCost);
    }

    protected boolean canAcceptItemBatch(Inventory inventory, Collection<Integer> itemIds, int capacity) {
        if (inventory == null || itemIds == null) return false;

        LinkedHashSet<Integer> uniqueIds = new LinkedHashSet<>();
        for (Integer itemId : itemIds) {
            if (itemId == null || itemId <= 0) continue;
            uniqueIds.add(itemId);
        }

        if (uniqueIds.isEmpty()) return false;

        int neededNewTypes = 0;
        for (Integer itemId : uniqueIds) {
            if (inventory.getCount(itemId) <= 0) {
                neededNewTypes++;
            }
        }

        return inventory.itemsProperty().size() + neededNewTypes <= capacity;
    }

    protected boolean moveSelectedItemBatch(Inventory from, Inventory to, Collection<Integer> itemIds, int targetCapacity) {
        if (from == null || to == null || itemIds == null) return false;

        LinkedHashSet<Integer> uniqueIds = new LinkedHashSet<>();
        for (Integer itemId : itemIds) {
            if (itemId == null || itemId <= 0) continue;
            if (from.getCount(itemId) > 0) {
                uniqueIds.add(itemId);
            }
        }

        if (uniqueIds.isEmpty()) return false;
        if (!canAcceptItemBatch(to, uniqueIds, targetCapacity)) return false;

        List<SlotData> moved = new ArrayList<>();
        for (Integer itemId : uniqueIds) {
            int qty = from.getCount(itemId);
            if (qty <= 0) continue;
            if (!from.removeItem(itemId, qty)) {
                for (SlotData rollback : moved) {
                    from.addItem(rollback.itemId(), rollback.quantity());
                    to.removeItem(rollback.itemId(), rollback.quantity());
                }
                return false;
            }
            to.addItem(itemId, qty);
            moved.add(new SlotData(itemId, qty));
        }

        return !moved.isEmpty();
    }
    
    protected void openInventory(){
        if (inventoryOverlay != null && !inventoryOverlay.isVisible()) {
            setInventoryOpen(true);
        }
        setActiveMenuTab(MenuTab.INVENTORY);
    }

    protected void closeMenuScreen(){
        setInventoryOpen(false);
    }
    
    protected void openStatus(){
        if (inventoryOverlay != null && !inventoryOverlay.isVisible()) {
            setInventoryOpen(true);
        }
        setActiveMenuTab(MenuTab.STATUS);
    }

    protected void openSetting(){
        if (inventoryOverlay != null && !inventoryOverlay.isVisible()) {
            setInventoryOpen(true);
        }
        setActiveMenuTab(MenuTab.SETTINGS);
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
            Button button = hotbarSlotButtons.get(i);
            button.setOnAction(e -> {
                if (idx == selectedHotbarIndex && tryConsumeHotbarItem(idx)) {
                    return;
                }
                selectHotbarSlot(idx);
                hotbarAssignmentIndex = idx;
            });
            button.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.SECONDARY) {
                    moveHotbarSlotToInventory(idx);
                    e.consume();
                }
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
            int quantity = playerState.getHotbarItemCount(i);

            if (itemId <= 0 || quantity <= 0) {
                if (img != null) img.setImage(null);
                if (lbl != null) lbl.setText("");
                continue;
            }

            if (img != null) {
                img.setImage(ItemDatabase.getImage(itemId));
            }

            if (lbl != null) {
                lbl.setText("x" + quantity);
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
                button.setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.SECONDARY) {
                        moveInventorySlotToHotbar(index);
                        e.consume();
                    }
                });
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

    protected boolean moveInventorySlotToHotbar(int index) {
        if (playerState == null || player == null) return false;

        List<SlotData> slots = snapshotSlots(player.getInventory(), playerInventorySlotCapacity());
        if (index < 0 || index >= slots.size()) return false;

        SlotData slot = slots.get(index);
        if (slot == null || slot.itemId() <= 0 || slot.quantity() <= 0) return false;

        int targetIndex = findHotbarTargetSlot(slot.itemId());
        if (targetIndex < 0) {
            return false;
        }

        if (!player.getInventory().removeItem(slot.itemId(), slot.quantity())) {
            return false;
        }

        if (!playerState.addHotbarItems(targetIndex, slot.itemId(), slot.quantity())) {
            player.getInventory().addItem(slot.itemId(), slot.quantity());
            return false;
        }

        selectedPlayerInventoryItemId = null;
        refreshPlayerInventoryView();
        refreshHotbarView();
        return true;
    }

    protected boolean moveHotbarSlotToInventory(int index) {
        if (playerState == null || player == null) return false;
        if (index < 0 || index >= playerState.getHotbarSize()) return false;

        int itemId = playerState.getHotbarItemId(index);
        int quantity = playerState.getHotbarItemCount(index);
        if (itemId <= 0 || quantity <= 0) return false;

        player.getInventory().addItem(itemId, quantity);
        playerState.clearHotbarItem(index);

        refreshPlayerInventoryView();
        refreshHotbarView();
        return true;
    }

    protected int findHotbarTargetSlot(int itemId) {
        if (playerState == null || itemId <= 0) return -1;

        int sameItemSlot = playerState.findHotbarSlotWithItem(itemId);
        if (sameItemSlot >= 0) {
            return sameItemSlot;
        }

        return playerState.findFirstEmptyHotbarSlot();
    }

    protected void clearPlayerInventorySelection() {
        selectedPlayerInventoryItemId = null;
        refreshPlayerInventorySelectionHighlight();
    }

    protected boolean tryConsumeHotbarItem(int index) {
        if (playerState == null || player == null || gameState == null) return false;
        if (index < 0 || index >= playerState.getHotbarSize()) return false;

        int itemId = playerState.getHotbarItemId(index);
        int quantity = playerState.getHotbarItemCount(index);
        if (itemId <= 0 || quantity <= 0) return false;

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

        if (!playerState.removeHotbarItems(index, 1)) {
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
