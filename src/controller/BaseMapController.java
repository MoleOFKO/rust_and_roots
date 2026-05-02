package controller;

import db.LoadGameService;
import db.SaveGameService;
import java.net.URL;
import java.lang.reflect.Field;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;

import model.Inventory;
import model.ItemDatabase;
import model.NodeCollectors;
import model.RecipeCatalog;
import game.GameLoopManager;
import game.GameState;
import game.GameTimeManager;
import game.InteractionManager;
import game.SceneController;
import java.util.Objects;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
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
import javafx.scene.paint.Color;
import model.CollisionRegistry;
import model.Farm;
import model.FarmGrowthRules;
import model.FarmPlotRegistrar;
import model.FarmState;
import model.InteractionZone;
import model.MapStateSnapshots;
import model.MobMapState;
import model.Player;
import model.PlayerState;
import model.TreeMapState;
import util.AppDialogs;
import util.ImageCache;
import util.SceneManager;
import util.SpawnTag;
import util.TimeFlow;
import util.TutorialOverlayHelper;
import util.WorldScaler;
import javafx.scene.control.ToggleButton;

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

    private static final double DEFAULT_PLAYER_HITBOX_WIDTH = 14.0;
    private static final double DEFAULT_PLAYER_HITBOX_HEIGHT = 20.0;
    private static final double DEFAULT_PLAYER_HITBOX_TRANSLATE_X = 24.0;
    private static final double DEFAULT_PLAYER_HITBOX_TRANSLATE_Y = 28.0;
    private static final double DEFAULT_PLAYER_HITBOX_LAYOUT_Y_OFFSET_FROM_SPRITE = -5.0;

    protected static final List<String> OVERWORLD_TUTORIAL_IMAGES = List.of(
        "/image/Toturial/Up_bar.png",
        "/image/Toturial/Wp_hold.png",
        "/image/Toturial/After_wp_hold.png",
        "/image/Toturial/before_goes_to_hotbar.png",
        "/image/Toturial/goes_to_hotbar.png",
        "/image/Toturial/Attack.png",
        "/image/Toturial/tt7.png"
    );

    private static final ExecutorService MAP_EVICTION_EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "map-eviction");
        t.setDaemon(true);
        return t;
    });

    private static final ConcurrentHashMap<String, AtomicLong> MAP_EVICTION_VERSION_BY_KEY = new ConcurrentHashMap<>();

    // ---------- Required common nodes ----------
    @FXML protected StackPane view;
    @FXML protected Group world;

    @FXML protected Group human;
    @FXML protected ImageView human_img;
    @FXML protected ImageView equip_img;
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

    // ---------- Optional shared HUD (day/night nav) ----------
    @FXML protected Group daynightnavgroup;
    @FXML protected Label day;
    @FXML protected ImageView daynightcircleimg;
    @FXML protected Label time;
    @FXML protected Label name;
    @FXML protected Label coin;
    @FXML protected ProgressBar hpbar;
    @FXML protected ProgressBar spbar;

    // ---------- Optional shared hotbar ----------
    @FXML protected HBox hotbar;
    @FXML protected StackPane Hslot;
    @FXML protected ImageView slotImg;
    @FXML protected ToggleButton slotBtn;
    @FXML protected Label slotLb;
    @FXML protected StackPane Hslot1;
    @FXML protected ImageView slotImg1;
    @FXML protected ToggleButton slotBtn1;
    @FXML protected Label slotLb1;
    @FXML protected StackPane Hslot2;
    @FXML protected ImageView slotImg2;
    @FXML protected ToggleButton slotBtn2;
    @FXML protected Label slotLb2;
    @FXML protected StackPane Hslot3;
    @FXML protected ImageView slotImg3;
    @FXML protected ToggleButton slotBtn3;
    @FXML protected Label slotLb3;
    @FXML protected StackPane Hslot4;
    @FXML protected ImageView slotImg4;
    @FXML protected ToggleButton slotBtn4;
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
    protected int selectedPlayerInventorySlotIndex = -1;
    
	    // ---------- Runtime ----------
	    protected GameState gameState;
	    protected Player player;
	    protected PlayerState playerState;
	    protected Farm farm;
	    protected InteractionManager interactionManager;
	    protected GameLoopManager gameLoop;

	    // ---------- Shared attack dispatch ----------
	    private final LinkedHashMap<String, Runnable> attackHandlers = new LinkedHashMap<>();
	    private boolean attackDispatcherBound = false;

	    // ---------- Shared collision registry ----------
	    private final List<Rectangle> registeredCollisionRects = new ArrayList<>();
	    private List<Rectangle> cachedStaticWalls = List.of();

	    // ---------- Shared mob system (FXML-driven) ----------
	    private MapMobSystem mobSystem;
	    private NaturalResourceSystem naturalResourceSystem;
	    
	    protected final List<StackPane> hotbarSlots = new ArrayList<>();
	    protected final List<ToggleButton> hotbarSlotButtons = new ArrayList<>();
	    protected int selectedHotbarIndex = -1;

    protected Image dayBg;
    protected Image nightBg;
    protected ChangeListener<Boolean> nightListener;
    protected ChangeListener<Number> farmTimeListener;

    private InvalidationListener sharedHudRefreshListener;
    private final List<Observable> sharedHudRefreshListenerTargets = new ArrayList<>();

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
    protected int pendingCombatSkillExp = 0;
    protected int pendingCookingSkillExp = 0;
    
    protected enum MenuTab { INVENTORY, STATUS, SETTINGS }
    protected MenuTab activeMenuTab = MenuTab.INVENTORY;
    
    protected void configureFarm(Farm farm) {
        if (farm != null) {
            farm.setTotalStages(FarmGrowthRules.TOTAL_GROWTH_STAGES);
        }
    }

    protected String farmTableFxmlPath() {
        return "/view/farm_table.fxml";
    }

    protected void openFarmTable() {
        if (farm1_button == null || farm1_button.isDisable() || gameState == null) return;
        gameState.setActiveFarmMapKey(mapKey());
        gameState.setReturnScene("/view/" + mapKey() + ".fxml", SpawnTag.FROM_FARM);
        stopAndSwitchScene(farmTableFxmlPath(), SpawnTag.FROM_FARM);
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

	    protected void showOverworldTutorialOverlay() {
	        TutorialOverlayHelper.show(view, OVERWORLD_TUTORIAL_IMAGES);
	    }

	    protected final void registerPlayerAttackHandler(String key, Runnable handler) {
	        if (key == null || key.isBlank()) {
	            return;
	        }
	        if (handler == null) {
	            attackHandlers.remove(key);
	            return;
	        }
	        attackHandlers.put(key, handler);
	        bindAttackDispatcherIfNeeded();
	    }

	    private void bindAttackDispatcherIfNeeded() {
	        if (attackDispatcherBound) {
	            return;
	        }
	        if (player == null) {
	            return;
	        }
	        player.setOnAttackStarted(this::dispatchPlayerAttackStarted);
	        attackDispatcherBound = true;
	    }

	    private void dispatchPlayerAttackStarted() {
	        if (attackHandlers.isEmpty()) {
	            return;
	        }

	        for (Runnable handler : new ArrayList<>(attackHandlers.values())) {
	            if (handler == null) continue;
	            try {
	                handler.run();
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	    }

	    protected final void registerPlayerCollisionRectangle(Rectangle rect) {
	        if (rect == null) return;
	        if (!registeredCollisionRects.contains(rect)) {
	            registeredCollisionRects.add(rect);
	        }
	    }

	    protected final void registerPlayerCollisionRectangles(Collection<Rectangle> rects) {
	        if (rects == null || rects.isEmpty()) return;
	        for (Rectangle r : rects) {
	            registerPlayerCollisionRectangle(r);
	        }
	    }

	    protected final void rebuildPlayerCollisionWalls() {
	        if (player == null) return;
	        List<Rectangle> colliders = new ArrayList<>();
	        if (cachedStaticWalls != null && !cachedStaticWalls.isEmpty()) {
	            colliders.addAll(cachedStaticWalls);
	        }
	        if (!registeredCollisionRects.isEmpty()) {
	            colliders.addAll(registeredCollisionRects);
	        }
	        player.setWalls(colliders);
	    }

	    /** Hook for subclasses during the shared game loop tick (called every frame). */
	    protected void onGameLoopUpdate(Bounds playerBounds) {
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

        String currentMap = mapKey();
        if (gameState != null && currentMap != null && !currentMap.isBlank()) {
            // Keep GameState in sync with the currently active map scene.
            gameState.setCurrentMapKey(currentMap);
            // If other maps were evicted to the DB, reload this map's state before any map logic runs.
            new LoadGameService().loadMapState(gameState, currentMap);
        }

        ItemDatabase.loadItems();
        playerState = gameState.getPlayerState();

        normalizePlayerBodyHitbox();

        player = new Player(
            playerState,
            human,
            human_img,
            human_hitbox,
            64, 64,
            5,
            150_000_000L
        );

        ImageView equipView = equip_img;
        if (equipView == null) {
            equipView = controllerField("equip_img", ImageView.class);
        }
        if (equipView != null) {
            bindPlayerEquipmentView(equipView);
        }

        player.setOnToggleHotbarRequest(this::toggleHotbarVisible);
        player.setOnHotbarSlotRequest(this::selectHotbarSlot);

        autoBridgeSharedUiFromControllerFields();
        autoWireSharedUiHandlers();

        view.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                player.attachInput(newScene);
            }
        });

	        cachedStaticWalls = (walls != null) ? CollisionRegistry.collectWalls(walls) : List.of();
	        rebuildPlayerCollisionWalls();

        interactionManager = new InteractionManager();
        configureExits();

        setupFarmIfPresent();
        setupInventoryUiIfPresent();
        setupHotbarIfPresent();
        setupDayNightBindingIfConfigured();
        setupFarmTimeRefreshIfPresent();
	        setupSettingsTabIfPresent();
	        bindSharedStatusAndSettingControlsIfPresent();
	        setupDayNightNavHudIfPresent();

	        mobSystem = MapMobSystem.tryCreate(
	            gameState,
	            mapKey(),
	            world,
	            walls,
	            player,
	            playerState,
	            human_hitbox
	        );
	        if (mobSystem != null) {
	            registerPlayerAttackHandler("mobs", mobSystem::handlePlayerAttackStarted);
	            registerPlayerCollisionRectangles(mobSystem.mobHitboxesForPlayerCollision());
	            rebuildPlayerCollisionWalls();
	        }

	        naturalResourceSystem = NaturalResourceSystem.tryCreate(
	            gameState,
	            mapKey(),
	            world,
	            player,
	            playerState,
	            human_hitbox
	        );
	        if (naturalResourceSystem != null) {
	            registerPlayerAttackHandler("resources", naturalResourceSystem::handlePlayerAttackStarted);
	            registerPlayerCollisionRectangles(naturalResourceSystem.resourceHitboxesForPlayerCollision());
	            rebuildPlayerCollisionWalls();
	        }

	        GameTimeManager.get().start();
	        startLoop();

        new WorldScaler(view, world);
        disableKeyboardActivationRecursive(view);
        player.start();

        afterBaseInitialize();

        // Evict non-current map states in the background so map switches stay smooth.
        Platform.runLater(this::scheduleNonCurrentMapStateEvictionsIfNeeded);
    }

    protected final void stopAndSwitchScene(String targetFxml, SpawnTag targetSpawn) {
        capturePlayerSnapshot();

        cleanupBeforeSceneSwitch();
        if (gameLoop != null) gameLoop.stop();
        if (player != null) player.stop();
        GameTimeManager.get().setWorldDilation(1.0);
        SceneManager.switchScene(targetFxml, targetSpawn);
    }

    private void cleanupBeforeSceneSwitch() {
        detachFarmTimeRefresh();
        detachSharedHudRefreshListener();

        if (nightListener != null) {
            try {
                GameTimeManager.get().nightProperty().removeListener(nightListener);
            } catch (Exception ignored) {
            }
            nightListener = null;
        }

        unbindSharedUiFromSingletons();
        clearCachedUiReferences();

        dayBg = null;
        nightBg = null;
    }

    private void clearCachedUiReferences() {
        hotbarSlots.clear();
        hotbarSlotButtons.clear();
        hotbarImages.clear();
        hotbarLabels.clear();

        playerInventoryImages.clear();
        playerInventoryLabels.clear();
        playerInventoryButtons.clear();
    }

    private void unbindSharedUiFromSingletons() {
        GameTimeManager timeManager = GameTimeManager.get();

        // Settings tab (newer ids)
        if (timeLabel != null) {
            timeLabel.textProperty().unbind();
        }
        if (timeSpeedValueLabel != null) {
            timeSpeedValueLabel.textProperty().unbind();
        }
        if (timeSpeedSlider != null) {
            try {
                timeSpeedSlider.valueProperty().unbindBidirectional(timeManager.timeScaleProperty());
            } catch (Exception ignored) {
            }
        }

        // Settings tab (older ids via reflection)
        Slider legacyTimeScaleSlider = controllerField("timescaleslider", Slider.class);
        if (legacyTimeScaleSlider != null) {
            try {
                legacyTimeScaleSlider.valueProperty().unbindBidirectional(timeManager.timeScaleProperty());
            } catch (Exception ignored) {
            }
        }

        Label legacySliderScale = controllerField("sliderscale", Label.class);
        if (legacySliderScale != null) legacySliderScale.textProperty().unbind();

        Label legacyDays = controllerField("lbldays", Label.class);
        if (legacyDays != null) legacyDays.textProperty().unbind();

        Label legacyTime = controllerField("lbltime", Label.class);
        if (legacyTime != null) legacyTime.textProperty().unbind();

        // Day/night HUD
        Label dayLabel = day != null ? day : controllerField("day", Label.class);
        if (dayLabel != null) dayLabel.textProperty().unbind();

        Label timeLabelHud = this.time != null ? this.time : controllerField("time", Label.class);
        if (timeLabelHud != null) timeLabelHud.textProperty().unbind(); 

        Label nameLabel = name != null ? name : controllerField("name", Label.class);
        if (nameLabel != null) nameLabel.textProperty().unbind();

        Label coinLabel = coin != null ? coin : controllerField("coin", Label.class);
        if (coinLabel != null) coinLabel.textProperty().unbind();

        ImageView circle = daynightcircleimg != null ? daynightcircleimg : controllerField("daynightcircleimg", ImageView.class);
        if (circle != null) circle.rotateProperty().unbind();
    }

    private void scheduleNonCurrentMapStateEvictionsIfNeeded() {
        if (gameState == null) return;
        if (!gameState.hasActiveSave()) return;

        String currentMapKey = mapKey();
        if (currentMapKey == null || currentMapKey.isBlank()) return;

        Set<String> evictionKeys = new LinkedHashSet<>();
        evictionKeys.addAll(gameState.getAllFarmStates().keySet());
        evictionKeys.addAll(gameState.getAllTreeStates().keySet());
        evictionKeys.addAll(gameState.getAllMobStates().keySet());

        for (String mapKeyToEvict : evictionKeys) {
            if (mapKeyToEvict == null || mapKeyToEvict.isBlank()) continue;
            if (mapKeyToEvict.equals(currentMapKey)) continue;
            scheduleSingleMapStateEviction(mapKeyToEvict);
        }
    }

    private void scheduleSingleMapStateEviction(String mapKeyToEvict) {
        if (gameState == null) return;
        if (!gameState.hasActiveSave()) return;
        if (mapKeyToEvict == null || mapKeyToEvict.isBlank()) return;

        int saveId = gameState.getCurrentSaveId();
        if (saveId <= 0) return;

        // Snapshot the per-map states on the FX thread so the background DB write can't race against re-entry.
        FarmState farmState = gameState.getAllFarmStates().get(mapKeyToEvict);
        final FarmState farmSnapshot = (farmState == null) ? null : MapStateSnapshots.copyFarmState(farmState);

        TreeMapState treeState = gameState.getAllTreeStates().get(mapKeyToEvict);
        final TreeMapState treeSnapshot = (treeState == null) ? null : MapStateSnapshots.copyTreeMapState(treeState);

        MobMapState mobState = gameState.getAllMobStates().get(mapKeyToEvict);
        final MobMapState mobSnapshot = (mobState == null) ? null : MapStateSnapshots.copyMobMapState(mobState);

        if (farmSnapshot == null && treeSnapshot == null && mobSnapshot == null) {
            return;
        }

        String evictionKey = saveId + ":" + mapKeyToEvict;
        AtomicLong counter = MAP_EVICTION_VERSION_BY_KEY.computeIfAbsent(evictionKey, k -> new AtomicLong());
        long version = counter.incrementAndGet();

        MAP_EVICTION_EXECUTOR.submit(() -> {
            boolean saved = new SaveGameService().saveMapStateSnapshot(
                saveId,
                mapKeyToEvict,
                farmSnapshot,
                treeSnapshot,
                mobSnapshot
            );

            if (!saved) {
                return;
            }

            Platform.runLater(() -> {
                AtomicLong current = MAP_EVICTION_VERSION_BY_KEY.get(evictionKey);
                if (current == null || current.get() != version) {
                    return; // newer eviction request scheduled
                }

                if (gameState == null) {
                    return;
                }

                // Only evict if the player is no longer on this map.
                if (mapKeyToEvict.equals(gameState.getCurrentMapKey())) {
                    return;
                }

                gameState.getAllFarmStates().remove(mapKeyToEvict);
                gameState.getAllTreeStates().remove(mapKeyToEvict);
                gameState.getAllMobStates().remove(mapKeyToEvict);
            });
        });
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
        List<ToggleButton> slotButtonNodes
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
        refreshEquippedItemVisual();
        if (view != null) {
            view.requestFocus();
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

        if (view != null) {
            view.requestFocus();
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
        refreshGenericStatusView();
        refreshGenericSkillsView();
        refreshGenericStatusButtons();
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
        if (dayNumber <= 0) {
            return 10;
        }
        if (dayNumber <= 5) {
            return 10;
        }
        if (dayNumber <= 10) {
            return 20;
        }
        return 30;
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
        pendingCombatSkillExp = 0;
        pendingGatheringSkillExp = 0;
    }

    protected int getPendingSkillExp(String skillKey) {
        return switch (Objects.requireNonNullElse(skillKey, "")) {
            case "farming" -> pendingFarmingSkillExp;
            case "gathering" -> pendingGatheringSkillExp;
            case "crafting" -> pendingCraftingSkillExp;
            case "combat" -> pendingCombatSkillExp;
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
            case "combat" -> pendingCombatSkillExp = nextValue;
            case "cooking" -> pendingCookingSkillExp = nextValue;
            default -> {
            }
        }
    }

    protected int getPendingSkillExpTotal() {
        return pendingFarmingSkillExp
            + pendingGatheringSkillExp
            + pendingCraftingSkillExp
            + pendingCombatSkillExp
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
            case "combat" -> skills.getCombatExp();
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

        if (pendingFarmingSkillExp > 0) gameState.addFarmingSkillExp(pendingFarmingSkillExp);
        if (pendingGatheringSkillExp > 0) gameState.addGatheringSkillExp(pendingGatheringSkillExp);
        if (pendingCraftingSkillExp > 0) gameState.addCraftingSkillExp(pendingCraftingSkillExp);
        if (pendingCombatSkillExp > 0) gameState.addCombatSkillExp(pendingCombatSkillExp);
        if (pendingCookingSkillExp > 0) gameState.addCookingSkillExp(pendingCookingSkillExp);

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
    protected void onSkillCombatMinus() { changePendingSkill("combat", -1); }
    protected void onSkillCombatPlus() { changePendingSkill("combat", 1); }
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

        Map<Integer, Integer> transfer = new LinkedHashMap<>();
        for (Integer itemId : uniqueIds) {
            if (itemId == null || itemId <= 0) continue;
            int qty = from.getCount(itemId);
            if (qty > 0) {
                transfer.put(itemId, qty);
            }
        }

        if (transfer.isEmpty()) return false;
        if (!to.canAddItems(transfer)) return false;

        List<SlotData> moved = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : transfer.entrySet()) {
            int itemId = entry.getKey();
            int qty = entry.getValue();
            if (qty <= 0) {
                continue;
            }

            if (!from.removeItem(itemId, qty)) {
                for (SlotData rollback : moved) {
                    from.addItem(rollback.itemId(), rollback.quantity());
                    to.removeItem(rollback.itemId(), rollback.quantity());
                }
                return false;
            }
            moved.add(new SlotData(itemId, qty));
        }

        if (!to.addItems(transfer)) {
            for (SlotData rollback : moved) {
                from.addItem(rollback.itemId(), rollback.quantity());
            }
            return false;
        }

        return true;
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
        applyPreferredStatusPanelGroup();
    }

    protected void openSetting(){
        if (inventoryOverlay != null && !inventoryOverlay.isVisible()) {
            setInventoryOpen(true);
        }
        setActiveMenuTab(MenuTab.SETTINGS);
    }

    // ---------- Shared FXML handlers (maps can rely on BaseMapController) ----------
    @FXML
    protected void handleopenInventoryaction(ActionEvent event) {
        openInventory();
    }

    @FXML
    protected void handleopenstatusaction(ActionEvent event) {
        openStatus();
    }

    @FXML
    protected void handleopensettingaction(ActionEvent event) {
        openSetting();
    }

    @FXML
    protected void handlecloseaction(MouseEvent event) {
        closeMenuScreen();
    }

    @FXML
    protected void handlecloseaction(ActionEvent event) {
        closeMenuScreen();
    }

    @FXML
    protected void handleusepointaction(MouseEvent event) {
        onSharedUsePoint();
    }

    @FXML
    protected void handleconfirmaction(MouseEvent event) {
        onSharedConfirm();
    }

    @FXML
    protected void handlebtnskillacion(MouseEvent event) {
        if (gameState != null) gameState.setPreferredStatusPanelGroup(GameState.StatusPanelGroup.SKILLS);
        setSkillsOpen(true);
        setStatusOpen(false);
        refreshStatusAllocationUi();
    }

    @FXML
    protected void handlebtnstatusaction(MouseEvent event) {
        if (gameState != null) gameState.setPreferredStatusPanelGroup(GameState.StatusPanelGroup.STATUS);
        setStatusOpen(true);
        setSkillsOpen(false);
        refreshStatusAllocationUi();
    }

    @FXML
    protected void handleHPminusaction(MouseEvent event) { onStatusHpMinus(); }

    @FXML
    protected void handleHPplusaction(MouseEvent event) { onStatusHpPlus(); }

    @FXML
    protected void handleSPminusaction(MouseEvent event) { onStatusSpMinus(); }

    @FXML
    protected void handleSPplusaction(MouseEvent event) { onStatusSpPlus(); }

    @FXML
    protected void handleSTRminusaction(MouseEvent event) { onStatusStrMinus(); }

    @FXML
    protected void handleSTRplusaction(MouseEvent event) { onStatusStrPlus(); }

    @FXML
    protected void handleVITminusaction(MouseEvent event) { onStatusVitMinus(); }

    @FXML
    protected void handleVITplusaction(MouseEvent event) { onStatusVitPlus(); }

    @FXML
    protected void handleDEXminusaction(MouseEvent event) { onStatusDexMinus(); }

    @FXML
    protected void handleDEXplusaction(MouseEvent event) { onStatusDexPlus(); }

    @FXML
    protected void handleAGIminusaction(MouseEvent event) { onStatusAgiMinus(); }

    @FXML
    protected void handleAGIplusaction(MouseEvent event) { onStatusAgiPlus(); }

    @FXML
    protected void handlefarmingminusaction(MouseEvent event) { onSkillFarmingMinus(); }

    @FXML
    protected void handlefarmingplusaction(MouseEvent event) { onSkillFarmingPlus(); }

    @FXML
    protected void handlegatheringminusaction(MouseEvent event) { onSkillGatheringMinus(); }

    @FXML
    protected void handlegatheringplusaction(MouseEvent event) { onSkillGatheringPlus(); }

    @FXML
    protected void handlecraftingminusaction(MouseEvent event) { onSkillCraftingMinus(); }

    @FXML
    protected void handlecraftingplusaction(MouseEvent event) { onSkillCraftingPlus(); }

    @FXML
    protected void handlecombatminusaction(MouseEvent event) { onSkillCombatMinus(); }

    @FXML
    protected void handlecombatplusaction(MouseEvent event) { onSkillCombatPlus(); }

    @FXML
    protected void handlecookingminusaction(MouseEvent event) { onSkillCookingMinus(); }

    @FXML
    protected void handlecookingplusaction(MouseEvent event) { onSkillCookingPlus(); }

    @FXML
    protected void handlesaveexitaction(ActionEvent event) {
        saveAndExitToMainMenu();
    }

    @FXML
    protected void handlesave(ActionEvent event) {
        openSaveLoadMenu(true);
    }

    @FXML
    protected void handleload(ActionEvent event) {
        openSaveLoadMenu(false);
    }

    protected void openSaveLoadMenu(boolean saveMode) {
        if (gameState == null) return;
        gameState.setSaveMenuMode(saveMode);
        gameState.setReturnScene(resolveCurrentSceneFxmlPath(), SpawnTag.DEFAULT);
        stopAndSwitchScene("/view/saveloads.fxml", SpawnTag.DEFAULT);
    }

    protected void saveAndExitToMainMenu() {
        try {
            capturePlayerSnapshot();
            if (gameState != null) {
                int slot = Math.max(1, gameState.getSelectedSaveSlot());
                new SaveGameService().saveGameToSlot(gameState, slot);
                gameState.setSaveMenuMode(false);
            }
            GameTimeManager.get().setWorldDilation(1.0);
            stopAndSwitchScene("/view/main_menu.fxml", SpawnTag.DEFAULT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected String resolveCurrentSceneFxmlPath() {
        String key = mapKey();
        if (key == null || key.isBlank()) {
            return "/view/home_map.fxml";
        }
        return "/view/" + key.toLowerCase(Locale.ROOT) + ".fxml";
    }

    protected void applyPreferredStatusPanelGroup() {
        if (gameState == null) return;
        switch (gameState.getPreferredStatusPanelGroup()) {
            case SKILLS -> {
                setSkillsOpen(true);
                setStatusOpen(false);
            }
            case STATUS -> {
                setStatusOpen(true);
                setSkillsOpen(false);
            }
        }
        refreshStatusAllocationUi();
    }


    protected void bindPlayerEquipmentView(ImageView equipView) {
        if (player == null) return;
        player.setEquipmentView(equipView);
        refreshEquippedItemVisual();
    }

    private void autoBridgeSharedUiFromControllerFields() {
        HBox hotbarNode = controllerField("hotbar", HBox.class);
        Label hotbarGuideNode = controllerField("hotbarguide", Label.class);
        if (hotbarNode != null || hotbarGuideNode != null) {
            bridgeSharedHotbarUi(
                hotbarNode,
                hotbarGuideNode,
                controllerFieldList(StackPane.class, "Hslot", "Hslot1", "Hslot2", "Hslot3", "Hslot4"),
                controllerFieldList(ImageView.class, "slotImg", "slotImg1", "slotImg2", "slotImg3", "slotImg4"),
                controllerFieldList(Label.class, "slotLb", "slotLb1", "slotLb2", "slotLb3", "slotLb4"),
                controllerFieldList(ToggleButton.class, "slotBtn", "slotBtn1", "slotBtn2", "slotBtn3", "slotBtn4")
            );
        }

        Pane inventoryOverlayNode = controllerField("inventoryOverlay", Pane.class);
        Group menuInventoryNode = controllerField("menuInventory", Group.class);
        Group statusTabNode = controllerField("statusTab", Group.class);
        Group settingsTabNode = controllerField("settingsTab", Group.class);
        Group statusGroupNode = controllerField("statusgroup", Group.class);
        Group skillsGroupNode = controllerField("skillsgroup", Group.class);
        Group playerInventoryGroupNode = controllerField("player_inv_group1", Group.class);
        if (playerInventoryGroupNode == null) {
            playerInventoryGroupNode = controllerField("player_inv_group", Group.class);
        }
        Button openInventoryNode = controllerField("openInventory", Button.class);
        Button openStatusNode = controllerField("openstatus", Button.class);
        Button openSettingNode = controllerField("opensetting", Button.class);
        ImageView closeButtonNode = controllerField("btnclose", ImageView.class);
        ImageView btnSkillNode = controllerField("btnskill", ImageView.class);
        ImageView btnStatusNode = controllerField("btnstatus", ImageView.class);

        if (inventoryOverlayNode != null || menuInventoryNode != null || statusTabNode != null || settingsTabNode != null) {
            boolean hasAltMenuInventorySlots = controllerField("Playerinvimg112", ImageView.class) != null;
            List<ImageView> playerInvImages = hasAltMenuInventorySlots
                ? controllerFieldList(
                    ImageView.class,
                    "Playerinvimg112", "Playerinvimg110", "Playerinvimg21", "Playerinvimg31", "Playerinvimg41",
                    "Playerinvimg51", "Playerinvimg61", "Playerinvimg71", "Playerinvimg81", "Playerinvimg91",
                    "Playerinvimg101", "Playerinvimg111", "Playerinvimg121", "Playerinvimg131", "Playerinvimg141",
                    "Playerinvimg151", "Playerinvimg161", "Playerinvimg171", "Playerinvimg181", "Playerinvimg191"
                )
                : controllerIndexedFieldList(ImageView.class, "Playerinvimg", 20);

            List<Label> playerInvLabels = hasAltMenuInventorySlots
                ? controllerFieldList(
                    Label.class,
                    "Playerinvlbl112", "Playerinvlbl110", "Playerinvlbl21", "Playerinvlbl31", "Playerinvlbl41",
                    "Playerinvlbl51", "Playerinvlbl61", "Playerinvlbl71", "Playerinvlbl81", "Playerinvlbl91",
                    "Playerinvlbl101", "Playerinvlbl111", "Playerinvlbl121", "Playerinvlbl131", "Playerinvlbl141",
                    "Playerinvlbl151", "Playerinvlbl161", "Playerinvlbl171", "Playerinvlbl181", "Playerinvlbl191"
                )
                : controllerIndexedFieldList(Label.class, "Playerinvlbl", 20);

            List<Button> playerInvButtons = hasAltMenuInventorySlots
                ? controllerFieldList(
                    Button.class,
                    "Playerinvbtn112", "Playerinvbtn110", "Playerinvbtn21", "Playerinvbtn31", "Playerinvbtn41",
                    "Playerinvbtn51", "Playerinvbtn61", "Playerinvbtn71", "Playerinvbtn81", "Playerinvbtn91",
                    "Playerinvbtn101", "Playerinvbtn111", "Playerinvbtn121", "Playerinvbtn131", "Playerinvbtn141",
                    "Playerinvbtn151", "Playerinvbtn161", "Playerinvbtn171", "Playerinvbtn181", "Playerinvbtn191"
                )
                : controllerIndexedFieldList(Button.class, "Playerinvbtn", 20);

            bridgeSharedInventoryOverlayUi(
                inventoryOverlayNode,
                menuInventoryNode,
                statusTabNode,
                settingsTabNode,
                statusGroupNode,
                skillsGroupNode,
                playerInventoryGroupNode,
                openInventoryNode,
                openStatusNode,
                openSettingNode,
                closeButtonNode,
                btnSkillNode,
                btnStatusNode,
                playerInvImages,
                playerInvLabels,
                playerInvButtons
            );
        }
    }

    private void autoWireSharedUiHandlers() {
        bindAction("slotBtn5", Button.class, e -> toggleInventory());
        bindAction("openInventory", Button.class, e -> openInventory());
        bindAction("openstatus", Button.class, e -> openStatus());
        bindAction("opensetting", Button.class, e -> openSetting());
        bindMouse("btnclose", ImageView.class, e -> closeMenuScreen());
        bindMouse("btnskill", ImageView.class, e -> {
            if (gameState != null) gameState.setPreferredStatusPanelGroup(GameState.StatusPanelGroup.SKILLS);
            setSkillsOpen(true);
            setStatusOpen(false);
            refreshStatusAllocationUi();
        });
        bindMouse("btnstatus", ImageView.class, e -> {
            if (gameState != null) gameState.setPreferredStatusPanelGroup(GameState.StatusPanelGroup.STATUS);
            setStatusOpen(true);
            setSkillsOpen(false);
            refreshStatusAllocationUi();
        });
        bindMouse("btnUsePoint", ImageView.class, e -> onSharedUsePoint());
        bindMouse("btnConfirm", ImageView.class, e -> onSharedConfirm());
        bindMouse("btnHPminus", ImageView.class, e -> onStatusHpMinus());
        bindMouse("btnHPplus", ImageView.class, e -> onStatusHpPlus());
        bindMouse("btnSPminus", ImageView.class, e -> onStatusSpMinus());
        bindMouse("btnSPplus", ImageView.class, e -> onStatusSpPlus());
        bindMouse("btnSTRminus", ImageView.class, e -> onStatusStrMinus());
        bindMouse("btnSTRplus", ImageView.class, e -> onStatusStrPlus());
        bindMouse("btnVITminus", ImageView.class, e -> onStatusVitMinus());
        bindMouse("btnVITplus", ImageView.class, e -> onStatusVitPlus());
        bindMouse("btnDEXminus", ImageView.class, e -> onStatusDexMinus());
        bindMouse("btnDEXplus", ImageView.class, e -> onStatusDexPlus());
        bindMouse("btnAGIminus", ImageView.class, e -> onStatusAgiMinus());
        bindMouse("btnAGIplus", ImageView.class, e -> onStatusAgiPlus());
        bindMouse("btnfarmingminus1", ImageView.class, e -> onSkillFarmingMinus());
        bindMouse("btnfarmingplus1", ImageView.class, e -> onSkillFarmingPlus());
        bindMouse("btngatheringminus1", ImageView.class, e -> onSkillGatheringMinus());
        bindMouse("btngatheringplus1", ImageView.class, e -> onSkillGatheringPlus());
        bindMouse("btncraftingminus1", ImageView.class, e -> onSkillCraftingMinus());
        bindMouse("btncraftingplus1", ImageView.class, e -> onSkillCraftingPlus());
        bindMouse("btncombatminus1", ImageView.class, e -> onSkillCombatMinus());
        bindMouse("btncombatplus1", ImageView.class, e -> onSkillCombatPlus());
        bindMouse("btncookingminus1", ImageView.class, e -> onSkillCookingMinus());
        bindMouse("btncookingplus1", ImageView.class, e -> onSkillCookingPlus());
    }

    private void bindSharedStatusAndSettingControlsIfPresent() {
        detachSharedHudRefreshListener();

        if (playerState != null) {
            sharedHudRefreshListener = obs -> {
                refreshStatusAllocationUi();
                refreshSharedHudBars();
            };
            registerSharedHudRefreshListener(playerState.playerNameProperty());
            registerSharedHudRefreshListener(playerState.expProperty());
            registerSharedHudRefreshListener(playerState.hpProperty());
            registerSharedHudRefreshListener(playerState.maxHpProperty());
            registerSharedHudRefreshListener(playerState.staminaProperty());
            registerSharedHudRefreshListener(playerState.maxStaminaProperty());
            registerSharedHudRefreshListener(playerState.strProperty());
            registerSharedHudRefreshListener(playerState.vitProperty());
            registerSharedHudRefreshListener(playerState.dexProperty());
            registerSharedHudRefreshListener(playerState.agiProperty());
        }

        GameTimeManager timeManager = GameTimeManager.get();
        Slider timeScaleSlider = controllerField("timescaleslider", Slider.class);
        Label sliderScaleLabel = controllerField("sliderscale", Label.class);
        Label daysLabel = controllerField("lbldays", Label.class);
        Label timeLabelNode = controllerField("lbltime", Label.class);
        Button setDayButton = controllerField("btnsetday", Button.class);
        Button setNightButton = controllerField("btnsetnight", Button.class);
        Button addHourButton = controllerField("btnplus1h", Button.class);
        Button saveExitButton = controllerField("btnsaveandexit", Button.class);

        if (timeScaleSlider != null) {
            timeScaleSlider.valueProperty().unbindBidirectional(timeManager.timeScaleProperty());
            timeScaleSlider.valueProperty().bindBidirectional(timeManager.timeScaleProperty());
        }
        if (sliderScaleLabel != null) {
            sliderScaleLabel.textProperty().unbind();
            sliderScaleLabel.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("Current Time Speed : x%.1f", timeManager.timeScaleProperty().get()),
                timeManager.timeScaleProperty()
            ));
        }
        if (daysLabel != null) {
            daysLabel.textProperty().unbind();
            daysLabel.textProperty().bind(Bindings.createStringBinding(
                () -> "Days : " + timeManager.dayNumberProperty().get(),
                timeManager.dayNumberProperty()
            ));
        }
        if (timeLabelNode != null) {
            timeLabelNode.textProperty().unbind();
            timeLabelNode.textProperty().bind(Bindings.createStringBinding(
                () -> "Time : " + formatClockOnly(timeManager.timeOfDayProperty().get()),
                timeManager.timeOfDayProperty()
            ));
        }
        if (setDayButton != null) setDayButton.setOnAction(e -> timeManager.setTimeOfDay(8, 0));
        if (setNightButton != null) setNightButton.setOnAction(e -> timeManager.setTimeOfDay(20, 0));
        if (addHourButton != null) addHourButton.setOnAction(e -> timeManager.addMinutes(60));
        if (saveExitButton != null) {
            saveExitButton.setOnAction(this::handlesaveexitaction);
        }

        refreshStatusAllocationUi();
        refreshSharedHudBars();
        refreshMenuState();
    }

    private void registerSharedHudRefreshListener(Observable observable) {
        if (observable == null || sharedHudRefreshListener == null) {
            return;
        }
        observable.addListener(sharedHudRefreshListener);
        sharedHudRefreshListenerTargets.add(observable);
    }

    protected final void detachSharedHudRefreshListener() {
        if (sharedHudRefreshListener == null) {
            return;
        }
        for (Observable observable : sharedHudRefreshListenerTargets) {
            if (observable == null) {
                continue;
            }
            observable.removeListener(sharedHudRefreshListener);
        }
        sharedHudRefreshListenerTargets.clear();
        sharedHudRefreshListener = null;
    }

    private void refreshGenericStatusView() {
        Label statusNameNode = controllerField("statusName", Label.class);
        Label statusExpNode = controllerField("statusEXP", Label.class);
        Label statusHpNode = controllerField("statusHP", Label.class);
        Label statusSpNode = controllerField("statusSP", Label.class);
        Label statusStrNode = controllerField("statusSTR", Label.class);
        Label statusVitNode = controllerField("statusVIT", Label.class);
        Label statusDexNode = controllerField("statusDEX", Label.class);
        Label statusAgiNode = controllerField("statusAGI", Label.class);

        if (playerState == null) return;

        if (statusNameNode != null) statusNameNode.setText("Name : " + playerState.getPlayerName());
        if (statusExpNode != null) statusExpNode.setText(formatVisibleExpLabel());

        int shownMaxHp = Math.max(1, playerState.getMaxHp() + pendingHpPoints);
        int shownMaxSp = Math.max(1, playerState.getMaxStamina() + pendingSpPoints);

        if (statusHpNode != null) statusHpNode.setText("HP : " + playerState.getHp() + "/" + shownMaxHp + formatPending(pendingHpPoints));
        if (statusSpNode != null) statusSpNode.setText("SP : " + playerState.getStamina() + "/" + shownMaxSp + formatPending(pendingSpPoints));

        refreshSharedHudBars();

        if (statusStrNode != null) statusStrNode.setText("STR : " + playerState.getStr() + formatPending(pendingStrPoints));
        if (statusVitNode != null) statusVitNode.setText("VIT : " + playerState.getVit() + formatPending(pendingVitPoints));
        if (statusDexNode != null) statusDexNode.setText("DEX : " + playerState.getDex() + formatPending(pendingDexPoints));
        if (statusAgiNode != null) statusAgiNode.setText("AGI : " + playerState.getAgi() + formatPending(pendingAgiPoints));
    }

    private void setupDayNightNavHudIfPresent() {
        Group navGroup = daynightnavgroup != null ? daynightnavgroup : controllerField("daynightnavgroup", Group.class);
        if (navGroup == null) {
            return;
        }

        navGroup.setOpacity(0.45);
        navGroup.setOnMouseEntered(e -> navGroup.setOpacity(1.0));
        navGroup.setOnMouseExited(e -> navGroup.setOpacity(0.45));

        GameTimeManager timeManager = GameTimeManager.get();

        Label dayLabel = day != null ? day : controllerField("day", Label.class);
        if (dayLabel != null) {
            dayLabel.textProperty().unbind();
            dayLabel.textProperty().bind(timeManager.dayNumberProperty().asString("Day: %d"));
        }

        Label timeLabel = time != null ? time : controllerField("time", Label.class);
        if (timeLabel != null) {
            timeLabel.textProperty().unbind();
            timeLabel.textProperty().bind(Bindings.createStringBinding(
                () -> "Time: " + formatClockOnly(timeManager.timeOfDayProperty().get()),
                timeManager.timeOfDayProperty()
            ));
        }

        Label nameLabel = name != null ? name : controllerField("name", Label.class);
        if (nameLabel != null && playerState != null) {
            nameLabel.textProperty().unbind();
            nameLabel.textProperty().bind(Bindings.createStringBinding(
                () -> "Name: " + safePlayerName(playerState.getPlayerName()),
                playerState.playerNameProperty()
            ));
        }

        Label coinLabel = coin != null ? coin : controllerField("coin", Label.class);
        if (coinLabel != null && playerState != null && playerState.getInventory() != null) {
            coinLabel.textProperty().unbind();
            coinLabel.textProperty().bind(playerState.getInventory().coinsProperty().asString(": %d"));
        }

        ImageView circle = daynightcircleimg != null ? daynightcircleimg : controllerField("daynightcircleimg", ImageView.class);
        if (circle != null) {
            circle.rotateProperty().unbind();
            circle.rotateProperty().bind(Bindings.createDoubleBinding(
                () -> computeDayNightCircleAngle(timeManager.totalSecondsProperty().get()),
                timeManager.totalSecondsProperty()
            ));
        }

        refreshSharedHudBars();
    }

    private void refreshSharedHudBars() {
        if (playerState == null) return;

        ProgressBar hpBarNode = hpbar != null ? hpbar : controllerField("hpbar", ProgressBar.class);
        ProgressBar spBarNode = spbar != null ? spbar : controllerField("spbar", ProgressBar.class);
        if (hpBarNode == null && spBarNode == null) {
            return;
        }

        int shownMaxHp = Math.max(1, playerState.getMaxHp() + pendingHpPoints);
        int shownMaxSp = Math.max(1, playerState.getMaxStamina() + pendingSpPoints);

        if (hpBarNode != null && !hpBarNode.progressProperty().isBound()) {
            hpBarNode.setProgress(Math.max(0.0, Math.min(1.0, playerState.getHp() / (double) shownMaxHp)));
        }
        if (spBarNode != null && !spBarNode.progressProperty().isBound()) {
            spBarNode.setProgress(Math.max(0.0, Math.min(1.0, playerState.getStamina() / (double) shownMaxSp)));
        }
    }

    private static String formatClockOnly(LocalTime localTime) {
        LocalTime safeTime = (localTime == null) ? LocalTime.MIDNIGHT : localTime;
        return String.format("%02d:%02d", safeTime.getHour(), safeTime.getMinute());
    }

    private static String safePlayerName(String playerName) {
        if (playerName == null) return "Player";
        String trimmed = playerName.trim();
        return trimmed.isBlank() ? "Player" : trimmed;
    }

    private static double computeDayNightCircleAngle(double totalSeconds) {
        int secondsOfDay = (int) Math.floor(totalSeconds % (24.0 * 60.0 * 60.0));
        if (secondsOfDay < 0) secondsOfDay += 24 * 60 * 60;

        int cycleSeconds = 24 * 60 * 60;
        int anchorAtSix = 6 * 60 * 60;
        int elapsedFromSix = Math.floorMod(secondsOfDay - anchorAtSix, cycleSeconds);
        return -(elapsedFromSix * (360.0 / cycleSeconds));
    }

    private void refreshGenericSkillsView() {
        if (gameState == null) return;
        GameState.PlayerSkills skills = gameState.getPlayerSkills();

        Label farmingNode = controllerField("farming", Label.class);
        Label gatheringNode = controllerField("gathering", Label.class);
        Label craftingNode = controllerField("crafting", Label.class);
        Label combatNode = controllerField("combat", Label.class);
        Label cookingNode = controllerField("cooking", Label.class);

        if (farmingNode != null) farmingNode.setText(appendPendingSkillCost(skills.getFarmingDisplay(), pendingFarmingSkillExp));
        if (gatheringNode != null) gatheringNode.setText(appendPendingSkillCost(skills.getGatheringDisplay(), pendingGatheringSkillExp));
        if (craftingNode != null) craftingNode.setText(appendPendingSkillCost(skills.getCraftingDisplay(), pendingCraftingSkillExp));
        if (combatNode != null) combatNode.setText(appendPendingSkillCost(skills.getCombatDisplay(), pendingCombatSkillExp));
        if (cookingNode != null) cookingNode.setText(appendPendingSkillCost(skills.getCookingDisplay(), pendingCookingSkillExp));
    }

    private void refreshGenericStatusButtons() {
        boolean canUsePoint = playerState != null && playerState.getExp() > 0;
        boolean skillsVisible = isSkillsScreenVisible();

        configureImageButton(controllerField("btnUsePoint", ImageView.class), canUsePoint, true, statusAllocationMode || skillAllocationMode ? 1.0 : 0.85);

        if (skillsVisible) {
            configureImageButton(controllerField("btnConfirm", ImageView.class), skillAllocationMode && getPendingSkillExpTotal() > 0, skillAllocationMode, 1.0);
            configureImageButton(controllerField("btnfarmingplus1", ImageView.class), canIncreaseSkill("farming"), skillAllocationMode, 1.0);
            configureImageButton(controllerField("btngatheringplus1", ImageView.class), canIncreaseSkill("gathering"), skillAllocationMode, 1.0);
            configureImageButton(controllerField("btncraftingplus1", ImageView.class), canIncreaseSkill("crafting"), skillAllocationMode, 1.0);
            configureImageButton(controllerField("btncombatplus1", ImageView.class), canIncreaseSkill("combat"), skillAllocationMode, 1.0);
            configureImageButton(controllerField("btncookingplus1", ImageView.class), canIncreaseSkill("cooking"), skillAllocationMode, 1.0);
            configureImageButton(controllerField("btnfarmingminus1", ImageView.class), skillAllocationMode && pendingFarmingSkillExp > 0, skillAllocationMode, 1.0);
            configureImageButton(controllerField("btngatheringminus1", ImageView.class), skillAllocationMode && pendingGatheringSkillExp > 0, skillAllocationMode, 1.0);
            configureImageButton(controllerField("btncraftingminus1", ImageView.class), skillAllocationMode && pendingCraftingSkillExp > 0, skillAllocationMode, 1.0);
            configureImageButton(controllerField("btncombatminus1", ImageView.class), skillAllocationMode && pendingCombatSkillExp > 0, skillAllocationMode, 1.0);
            configureImageButton(controllerField("btncookingminus1", ImageView.class), skillAllocationMode && pendingCookingSkillExp > 0, skillAllocationMode, 1.0);
            disableStatusButtons();
            return;
        }

        if (isStatusScreenVisible()) {
            configureImageButton(controllerField("btnConfirm", ImageView.class), statusAllocationMode && getPendingExpCost() > 0, statusAllocationMode, 1.0);
            configureImageButton(controllerField("btnHPplus", ImageView.class), canIncreaseStat("hp"), statusAllocationMode, 1.0);
            configureImageButton(controllerField("btnSPplus", ImageView.class), canIncreaseStat("sp"), statusAllocationMode, 1.0);
            configureImageButton(controllerField("btnSTRplus", ImageView.class), canIncreaseStat("str"), statusAllocationMode, 1.0);
            configureImageButton(controllerField("btnVITplus", ImageView.class), canIncreaseStat("vit"), statusAllocationMode, 1.0);
            configureImageButton(controllerField("btnDEXplus", ImageView.class), canIncreaseStat("dex"), statusAllocationMode, 1.0);
            configureImageButton(controllerField("btnAGIplus", ImageView.class), canIncreaseStat("agi"), statusAllocationMode, 1.0);
            configureImageButton(controllerField("btnHPminus", ImageView.class), statusAllocationMode && pendingHpPoints > 0, statusAllocationMode, 1.0);
            configureImageButton(controllerField("btnSPminus", ImageView.class), statusAllocationMode && pendingSpPoints > 0, statusAllocationMode, 1.0);
            configureImageButton(controllerField("btnSTRminus", ImageView.class), statusAllocationMode && pendingStrPoints > 0, statusAllocationMode, 1.0);
            configureImageButton(controllerField("btnVITminus", ImageView.class), statusAllocationMode && pendingVitPoints > 0, statusAllocationMode, 1.0);
            configureImageButton(controllerField("btnDEXminus", ImageView.class), statusAllocationMode && pendingDexPoints > 0, statusAllocationMode, 1.0);
            configureImageButton(controllerField("btnAGIminus", ImageView.class), statusAllocationMode && pendingAgiPoints > 0, statusAllocationMode, 1.0);
            disableSkillButtons();
        }
    }

    private void disableStatusButtons() {
        configureImageButton(controllerField("btnHPplus", ImageView.class), false, false, 1.0);
        configureImageButton(controllerField("btnSPplus", ImageView.class), false, false, 1.0);
        configureImageButton(controllerField("btnSTRplus", ImageView.class), false, false, 1.0);
        configureImageButton(controllerField("btnVITplus", ImageView.class), false, false, 1.0);
        configureImageButton(controllerField("btnDEXplus", ImageView.class), false, false, 1.0);
        configureImageButton(controllerField("btnAGIplus", ImageView.class), false, false, 1.0);
        configureImageButton(controllerField("btnHPminus", ImageView.class), false, false, 1.0);
        configureImageButton(controllerField("btnSPminus", ImageView.class), false, false, 1.0);
        configureImageButton(controllerField("btnSTRminus", ImageView.class), false, false, 1.0);
        configureImageButton(controllerField("btnVITminus", ImageView.class), false, false, 1.0);
        configureImageButton(controllerField("btnDEXminus", ImageView.class), false, false, 1.0);
        configureImageButton(controllerField("btnAGIminus", ImageView.class), false, false, 1.0);
    }

    private void disableSkillButtons() {
        configureImageButton(controllerField("btnfarmingplus1", ImageView.class), false, false, 1.0);
        configureImageButton(controllerField("btngatheringplus1", ImageView.class), false, false, 1.0);
        configureImageButton(controllerField("btncraftingplus1", ImageView.class), false, false, 1.0);
        configureImageButton(controllerField("btncombatplus1", ImageView.class), false, false, 1.0);
        configureImageButton(controllerField("btncookingplus1", ImageView.class), false, false, 1.0);
        configureImageButton(controllerField("btnfarmingminus1", ImageView.class), false, false, 1.0);
        configureImageButton(controllerField("btngatheringminus1", ImageView.class), false, false, 1.0);
        configureImageButton(controllerField("btncraftingminus1", ImageView.class), false, false, 1.0);
        configureImageButton(controllerField("btncombatminus1", ImageView.class), false, false, 1.0);
        configureImageButton(controllerField("btncookingminus1", ImageView.class), false, false, 1.0);
    }

    private void configureImageButton(ImageView imageView, boolean enabled, boolean visible, double activeOpacity) {
        if (imageView == null) return;
        imageView.setVisible(visible);
        imageView.setManaged(visible);
        imageView.setDisable(!enabled);
        imageView.setMouseTransparent(!visible || !enabled);
        imageView.setOpacity(!visible ? 0.0 : (enabled ? activeOpacity : 0.45));
    }

    private String formatPending(int points) {
        return points <= 0 ? "" : "(+" + points + ")";
    }

    private <T> T controllerField(String name, Class<T> type) {
        Field field = findControllerField(name);
        if (field == null) return null;
        try {
            field.setAccessible(true);
            Object value = field.get(this);
            return type.isInstance(value) ? type.cast(value) : null;
        } catch (IllegalAccessException ignored) {
            return null;
        }
    }

    private Field findControllerField(String name) {
        Class<?> current = getClass();
        while (current != null) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    private <T> List<T> controllerFieldList(Class<T> type, String... names) {
        List<T> values = new ArrayList<>();
        if (names == null) return values;
        for (String name : names) {
            T value = controllerField(name, type);
            if (value != null) {
                values.add(value);
            }
        }
        return values;
    }

    private <T> List<T> controllerIndexedFieldList(Class<T> type, String baseName, int count) {
        List<T> values = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String name = i == 0 ? baseName : baseName + i;
            T value = controllerField(name, type);
            if (value != null) {
                values.add(value);
            }
        }
        return values;
    }

    private <T extends javafx.scene.Node> void bindMouse(String fieldName, Class<T> type, javafx.event.EventHandler<? super MouseEvent> handler) {
        T node = controllerField(fieldName, type);
        if (node != null) {
            node.setOnMouseClicked(handler);
        }
    }

    private <T extends ButtonBase> void bindAction(String fieldName, Class<T> type, javafx.event.EventHandler<ActionEvent> handler) {
        T node = controllerField(fieldName, type);
        if (node != null) {
            node.setOnAction(handler);
        }
    }

    protected void refreshEquippedItemVisual() {
        if (player == null || playerState == null) return;

        int itemId = -1;
        if (selectedHotbarIndex >= 0 && selectedHotbarIndex < playerState.getHotbarSize()) {
            itemId = playerState.getHotbarItemId(selectedHotbarIndex);
            if (playerState.getHotbarItemCount(selectedHotbarIndex) <= 0) {
                itemId = -1;
            }
        }
        player.setEquippedItem(itemId);
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
        farm.setOnInteract(this::openFarmTable);
        configureFarm(farm);
    }

    private void setupInventoryUiIfPresent() {
        setupPlayerInventoryGridIfPresent();
        setupMenuTabs();

        if (inventoryOverlay != null) {
            inventoryOverlay.setVisible(false);
            inventoryOverlay.setManaged(false);
            inventoryOverlay.setDisable(true);
        }
    }

    private void setupHotbarIfPresent() {
        // If this controller uses shadowed FXML fields (common in existing maps),
        // autoBridgeSharedUiFromControllerFields() will populate the hotbar lists.
        // Only fall back to the BaseMapController @FXML fields when nothing was bridged.
        if (hotbarSlotButtons.isEmpty() && hotbarSlots.isEmpty()) {
            hotbarSlots.clear();
            hotbarSlotButtons.clear();
            hotbarImages.clear();
            hotbarLabels.clear();

            addHotbarSlot(Hslot, slotBtn, slotImg, slotLb);
            addHotbarSlot(Hslot1, slotBtn1, slotImg1, slotLb1);
            addHotbarSlot(Hslot2, slotBtn2, slotImg2, slotLb2);
            addHotbarSlot(Hslot3, slotBtn3, slotImg3, slotLb3);
            addHotbarSlot(Hslot4, slotBtn4, slotImg4, slotLb4);
        }

        int slotCount = Math.min(hotbarSlotButtons.size(), hotbarSlots.size());
        if (slotCount <= 0) {
            return;
        }

        for (int i = 0; i < slotCount; i++) {
            final int idx = i;
            ToggleButton button = hotbarSlotButtons.get(i);
            if (button == null) {
                continue;
            }
            button.setFocusTraversable(false);
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
            slotBtn5.setFocusTraversable(false);
            slotBtn5.setOnAction(e -> toggleInventory());
        }

        if (playerState != null) {
            setHotbarVisible(playerState.isHotbarVisible());
        }

        if (slotCount > 0) {
            int savedIdx = 0;
            if (playerState != null) {
                savedIdx = Math.min(playerState.getselectedHotbarIndex(), slotCount - 1);
            }
            selectHotbarSlot(savedIdx);
            hotbarAssignmentIndex = savedIdx;
        }

        refreshHotbarView();
    }

    private void addHotbarSlot(StackPane slot, ToggleButton button, ImageView imageView, Label label) {
        if (slot != null && button != null) {
            hotbarSlots.add(slot);
            hotbarSlotButtons.add(button);
            hotbarImages.add(imageView);
            hotbarLabels.add(label);
        }
    }
    
    protected void refreshHotbarView() {
        if (playerState == null) return;

        int slotCount = Math.max(
            hotbarSlotButtons.size(),
            Math.max(hotbarSlots.size(), Math.max(hotbarImages.size(), hotbarLabels.size()))
        );

        for (int i = 0; i < slotCount; i++) {
            ImageView img = (i < hotbarImages.size()) ? hotbarImages.get(i) : null;
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

        refreshEquippedItemVisual();
    }
    
    private void addHotbarSlot(StackPane slot, ToggleButton button) {
        if (slot != null && button != null) {
            hotbarSlots.add(slot);
            hotbarSlotButtons.add(button);
        }
    }

    private void setupDayNightBindingIfConfigured() {
        ImageView background = bg != null ? bg : controllerField("bg", ImageView.class);
        if (background == null) return;

        String dayPath = dayBackgroundPath();
        if (dayPath == null || dayPath.isBlank()) return;

        String nightPath = nightBackgroundPath();

        dayBg = loadImageOrNull(dayPath);
        nightBg = loadImageOrNull(nightPath);
        if (dayBg == null) return;

        GameTimeManager time = GameTimeManager.get();
        nightListener = (obs, wasNight, isNight) -> {
            if (isNight) {
                background.setImage(nightBg != null ? nightBg : dayBg);
                return;
            }
            background.setImage(dayBg);
        };

        time.nightProperty().addListener(nightListener);
        nightListener.changed(time.nightProperty(), !time.isNight(), time.isNight());

        background.sceneProperty().addListener((o, oldScene, newScene) -> {
            if (newScene == null && nightListener != null) {
                time.nightProperty().removeListener(nightListener);
                nightListener = null;
            }
        });
    }

    private void setupFarmTimeRefreshIfPresent() {
        if (farm == null || view == null) {
            return;
        }

        detachFarmTimeRefresh();

        GameTimeManager time = GameTimeManager.get();
        farmTimeListener = (obs, oldValue, newValue) -> {
            if (farm == null || view.getScene() == null) {
                return;
            }
            double nowSeconds = (newValue == null) ? time.totalSecondsProperty().get() : newValue.doubleValue();
            farm.update(nowSeconds);
        };
        time.totalSecondsProperty().addListener(farmTimeListener);

        view.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) {
                detachFarmTimeRefresh();
            } else {
                farm.update(time.totalSecondsProperty().get());
            }
        });
    }

    protected final void detachFarmTimeRefresh() {
        if (farmTimeListener == null) {
            return;
        }

        GameTimeManager.get().totalSecondsProperty().removeListener(farmTimeListener);
        farmTimeListener = null;
    }

    protected Image loadImageOrNull(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        Image image = ImageCache.get(path);
        if (image == null) {
            System.err.println("[BaseMapController] Missing resource: " + path);
        }
        return image;
    }

    private void normalizePlayerBodyHitbox() {
        if (human_hitbox == null || human_img == null) {
            return;
        }

        human_hitbox.setWidth(DEFAULT_PLAYER_HITBOX_WIDTH);
        human_hitbox.setHeight(DEFAULT_PLAYER_HITBOX_HEIGHT);

        // Keep the hitbox anchored to the sprite node so all maps behave consistently even if the
        // FXML layouts differ (some maps position the group, others position the children).
        human_hitbox.setLayoutX(human_img.getLayoutX());
        human_hitbox.setLayoutY(human_img.getLayoutY() + DEFAULT_PLAYER_HITBOX_LAYOUT_Y_OFFSET_FROM_SPRITE);
        human_hitbox.setTranslateX(DEFAULT_PLAYER_HITBOX_TRANSLATE_X);
        human_hitbox.setTranslateY(DEFAULT_PLAYER_HITBOX_TRANSLATE_Y);

        // Make sure styling doesn't inflate collision bounds (stroke can affect boundsInLocal).
        human_hitbox.setFill(Color.TRANSPARENT);
        human_hitbox.setStroke(null);
        human_hitbox.setVisible(false);
        human_hitbox.setManaged(false);
        human_hitbox.setMouseTransparent(true);
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
                GameTimeManager.get().setWorldDilation(1.0);
                return;
            }

            updateWorldTimeDilationFromPlayer();

            Bounds playerBounds = human_hitbox.localToScene(human_hitbox.getBoundsInLocal());

            if (interactionManager != null) {
                interactionManager.updateAll(playerBounds);
            }

	            if (farm != null) {
	                farm.updatePlayerBounds(playerBounds);
	                double nowSeconds = GameTimeManager.get().totalSecondsProperty().get();
	                farm.update(nowSeconds);
	            }

	            if (mobSystem != null) {
	                mobSystem.update(playerBounds);
	            }

	            if (naturalResourceSystem != null) {
	                naturalResourceSystem.update(playerBounds);
	            }

	            onGameLoopUpdate(playerBounds);
	        });

        gameLoop.start();
    }

    private void updateWorldTimeDilationFromPlayer() {
        GameTimeManager time = GameTimeManager.get();
        if (time == null) {
            return;
        }

        double dilation = 1.0;

        if (playerState != null) {
            boolean runActive =
                playerState.isRunToggled()
                    && !playerState.hasStaminaExhaustedLock()
                    && playerState.getStamina() > 0
                    && !playerState.isDead()
                    && !playerState.isSleeping();

            dilation = TimeFlow.worldDilationForPlayerRun(playerState.getAgi(), runActive);
        }

        time.setWorldDilation(dilation);
    }
    
    protected int playerInventorySlotCapacity() {
        if (player != null && player.getInventory() != null) {
            return player.getInventory().getSlotCapacity();
        }
        if (playerState != null && playerState.getInventory() != null) {
            return playerState.getInventory().getSlotCapacity();
        }
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
        selectedPlayerInventorySlotIndex = (slot == null) ? -1 : index;
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
                button.setFocusTraversable(false);
                button.setOnAction(e -> {
                    if (index < snapshotSlots(player.getInventory(), playerInventorySlotCapacity()).size()) {
                        List<SlotData> slots = snapshotSlots(player.getInventory(), playerInventorySlotCapacity());
                        SlotData slot = (index < slots.size()) ? slots.get(index) : null;
                        if (slot != null && index == selectedPlayerInventorySlotIndex
                                && tryConsumeInventoryItem(index)) {
                            return;
                        }
                    }
                    selectPlayerInventorySlot(index);
                });
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
            .filter(e -> e.getKey() != null && e.getKey() > 0 && e.getValue() != null && e.getValue().get() > 0)
            .sorted(Comparator.comparing(
                e -> itemName(e.getKey()),
                String.CASE_INSENSITIVE_ORDER
            ))
            .forEach(e -> {
                if (result.size() >= capacity) {
                    return;
                }

                int itemId = e.getKey();
                int qty = e.getValue().get();
                int maxStack = Math.max(1, ItemDatabase.getMaxStackSize(itemId));

                int remaining = qty;
                while (remaining > 0 && result.size() < capacity) {
                    int stackQty = Math.min(maxStack, remaining);
                    result.add(new SlotData(itemId, stackQty));
                    remaining -= stackQty;
                }
            });

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
                selectedPlayerInventorySlotIndex >= 0 &&
                i == selectedPlayerInventorySlotIndex;

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

        if (!player.getInventory().removeItem(slot.itemId(), slot.quantity())) {
            return false;
        }

        if (!playerState.tryAddHotbarItems(slot.itemId(), slot.quantity())) {
            player.getInventory().addItem(slot.itemId(), slot.quantity());
            AppDialogs.showMessageDialog(null, "Not enough hotbar space.");
            return false;
        }

        selectedPlayerInventoryItemId = null;
        selectedPlayerInventorySlotIndex = -1;
        refreshPlayerInventoryView();
        refreshHotbarView();
        refreshEquippedItemVisual();
        return true;
    }

    protected boolean moveHotbarSlotToInventory(int index) {
        if (playerState == null || player == null) return false;
        if (index < 0 || index >= playerState.getHotbarSize()) return false;

        int itemId = playerState.getHotbarItemId(index);
        int quantity = playerState.getHotbarItemCount(index);
        if (itemId <= 0 || quantity <= 0) return false;

        if (!player.getInventory().addItem(itemId, quantity)) {
            AppDialogs.showMessageDialog(null, "Not enough inventory space.");
            return false;
        }
        playerState.clearHotbarItem(index);

        refreshPlayerInventoryView();
        refreshHotbarView();
        refreshEquippedItemVisual();
        return true;
    }

    protected int findHotbarTargetSlot(int itemId) {
        if (playerState == null || itemId <= 0) return -1;

        int maxStack = Math.max(1, ItemDatabase.getMaxStackSize(itemId));

        for (int i = 0; i < playerState.getHotbarSize(); i++) {
            if (playerState.getHotbarItemId(i) == itemId
                    && playerState.getHotbarItemCount(i) > 0
                    && playerState.getHotbarItemCount(i) < maxStack) {
                return i;
            }
        }

        return playerState.findFirstEmptyHotbarSlot();
    }

    protected void clearPlayerInventorySelection() {
        selectedPlayerInventoryItemId = null;
        selectedPlayerInventorySlotIndex = -1;
        refreshPlayerInventorySelectionHighlight();
    }

    protected boolean tryConsumeHotbarItem(int index) {
        if (playerState == null || player == null || gameState == null) return false;
        if (index < 0 || index >= playerState.getHotbarSize()) return false;

        int itemId = playerState.getHotbarItemId(index);
        int quantity = playerState.getHotbarItemCount(index);
        if (itemId <= 0 || quantity <= 0) return false;

        if (tryUnlockRecipeItem(itemId, () -> playerState.removeHotbarItems(index, 1))) {
            refreshHotbarView();
            refreshPlayerInventoryView();
            refreshEquippedItemVisual();
            return true;
        }

        FoodEffectData effectData = resolveFoodEffectData(itemId);
        if (effectData == null) return false;

        if (!playerState.removeHotbarItems(index, 1)) {
            return false;
        }

        applyFoodEffectData(effectData);

        refreshHotbarView();
        refreshPlayerInventoryView();
        refreshEquippedItemVisual();
        return true;
    }

    protected boolean tryConsumeInventoryItem(int index) {
        if (playerState == null || player == null || gameState == null) return false;

        List<SlotData> slots = snapshotSlots(player.getInventory(), playerInventorySlotCapacity());
        if (index < 0 || index >= slots.size()) return false;

        SlotData slot = slots.get(index);
        if (slot == null || slot.itemId() <= 0 || slot.quantity() <= 0) return false;

        if (tryUnlockRecipeItem(slot.itemId(), () -> player.getInventory().removeItem(slot.itemId(), 1))) {
            selectedPlayerInventoryItemId = null;
            selectedPlayerInventorySlotIndex = -1;
            refreshPlayerInventoryView();
            refreshHotbarView();
            refreshEquippedItemVisual();
            return true;
        }

        FoodEffectData effectData = resolveFoodEffectData(slot.itemId());
        if (effectData == null) return false;
        if (!player.getInventory().removeItem(slot.itemId(), 1)) return false;

        applyFoodEffectData(effectData);
        selectedPlayerInventoryItemId = null;
        selectedPlayerInventorySlotIndex = -1;
        refreshPlayerInventoryView();
        refreshHotbarView();
        refreshEquippedItemVisual();
        return true;
    }

    protected static final class FoodEffectData {
        private final int hpDelta;
        private final int staminaDelta;
        private final int strDelta;
        private final int vitDelta;
        private final int dexDelta;
        private final int agiDelta;

        protected FoodEffectData(int hpDelta, int staminaDelta, int strDelta, int vitDelta, int dexDelta, int agiDelta) {
            this.hpDelta = hpDelta;
            this.staminaDelta = staminaDelta;
            this.strDelta = strDelta;
            this.vitDelta = vitDelta;
            this.dexDelta = dexDelta;
            this.agiDelta = agiDelta;
        }
    }

    protected FoodEffectData resolveFoodEffectData(int itemId) {
        if (itemId <= 0 || gameState == null || playerState == null) return null;

        GameState.FoodEffectOutcome cookedOutcome = gameState.pollCookedDishEffect(itemId);
        if (cookedOutcome != null) {
            return new FoodEffectData(
                cookedOutcome.getHpDelta(),
                cookedOutcome.getStaminaDelta(),
                cookedOutcome.getStrDelta(),
                cookedOutcome.getVitDelta(),
                cookedOutcome.getDexDelta(),
                cookedOutcome.getAgiDelta()
            );
        }

        if (!ItemDatabase.isEdibleItem(itemId)) {
            return null;
        }

        // Spec:
        // - Raw food (not obtainable via cooking): +1% SP.
        // - Single-combo cooking (1 ingredient): +1% HP, +2% SP.
        // - Double-combo cooking (2+ ingredients): +5% HP, +10% SP.
        int comboCount = RecipeCatalog.getMinCookingIngredientCountForOutputItem(itemId);

        double hpPercent;
        double spPercent;
        if (comboCount <= 0) {
            hpPercent = 0.0;
            spPercent = 0.01;
        } else if (comboCount == 1) {
            hpPercent = 0.01;
            spPercent = 0.02;
        } else {
            hpPercent = 0.05;
            spPercent = 0.10;
        }

        int hpDelta = percentOfMaxToDelta(playerState.getMaxHp(), hpPercent);
        int staminaDelta = percentOfMaxToDelta(playerState.getMaxStamina(), spPercent);
        return new FoodEffectData(hpDelta, staminaDelta, 0, 0, 0, 0);
    }

    protected void applyFoodEffectData(FoodEffectData effectData) {
        if (playerState == null || effectData == null) return;

        double nowSeconds = game.GameTimeManager.get().totalSecondsProperty().get();
        playerState.applyFoodEffectWithAbstain(effectData.hpDelta, effectData.staminaDelta, nowSeconds);
        applyPersistentStatDelta("getStr", new String[]{"setStr"}, effectData.strDelta);
        applyPersistentStatDelta("getVit", new String[]{"setVit", "setVid"}, effectData.vitDelta);
        applyPersistentStatDelta("getDex", new String[]{"setDex"}, effectData.dexDelta);
        applyPersistentStatDelta("getAgi", new String[]{"setAgi"}, effectData.agiDelta);
    }

    private int percentOfMaxToDelta(int maxValue, double percent) {
        if (percent <= 0.0 || !Double.isFinite(percent)) {
            return 0;
        }

        int max = Math.max(0, maxValue);
        if (max <= 0) {
            return 0;
        }

        double raw = max * percent;
        int amount = (int) Math.ceil(raw);
        return Math.max(1, amount);
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

    protected boolean tryUnlockRecipeItem(int itemId, BooleanSupplier consumeOneItem) {
        if (itemId <= 0 || gameState == null || consumeOneItem == null || !ItemDatabase.isRecipeItem(itemId)) {
            return false;
        }

        Set<String> matchedKeys = RecipeCatalog.getRecipeKeysUnlockedByItemName(ItemDatabase.getItemName(itemId));
        if (matchedKeys.isEmpty()) {
            return false;
        }

        List<String> matchedDisplayNames = new ArrayList<>();
        for (String recipeKey : matchedKeys) {
            RecipeCatalog.RecipeDefinition definition = RecipeCatalog.findDefinitionByKey(recipeKey);
            matchedDisplayNames.add(
                definition == null
                    ? recipeKey.replace('_', ' ')
                    : definition.getDisplayName()
            );
        }

        List<String> newlyUnlocked = new ArrayList<>();
        for (String recipeKey : matchedKeys) {
            if (recipeKey == null || recipeKey.isBlank() || gameState.isRecipeUnlocked(recipeKey)) {
                continue;
            }
            newlyUnlocked.add(recipeKey);
        }

        if (newlyUnlocked.isEmpty()) {
            if (!consumeOneItem.getAsBoolean()) {
                return false;
            }

            RecipeCatalog.RecipeDefinition bestDefinition = null;
            int bestComboSize = 1;

            for (String recipeKey : matchedKeys) {
                RecipeCatalog.RecipeDefinition definition = RecipeCatalog.findDefinitionByKey(recipeKey);
                if (definition == null || definition.getIngredients() == null) {
                    continue;
                }

                int comboSize = definition.getIngredients().size();
                if (comboSize > bestComboSize) {
                    bestComboSize = comboSize;
                    bestDefinition = definition;
                } else if (comboSize == bestComboSize && bestDefinition == null) {
                    bestDefinition = definition;
                }
            }

            int expGain = Math.min(3, Math.max(1, bestComboSize));
            RecipeCatalog.Domain domain = (bestDefinition == null || bestDefinition.getDomain() == null)
                ? RecipeCatalog.Domain.COOKING
                : bestDefinition.getDomain();

            int rawExpBefore = playerState == null ? 0 : playerState.getExp();
            int skillExpBefore = switch (domain) {
                case COOKING -> gameState.getPlayerSkills().getCookingExp();
                case CRAFTING -> gameState.getPlayerSkills().getCraftingExp();
            };

            switch (domain) {
                case COOKING -> gameState.addCookingSkillExp(expGain);
                case CRAFTING -> gameState.addCraftingSkillExp(expGain);
            }

            String skillName = switch (domain) {
                case CRAFTING -> "Crafting";
                default -> "Cooking";
            };
            int rawExpAfter = playerState == null ? rawExpBefore : playerState.getExp();
            int skillExpAfter = switch (domain) {
                case COOKING -> gameState.getPlayerSkills().getCookingExp();
                case CRAFTING -> gameState.getPlayerSkills().getCraftingExp();
            };

            int rawDelta = Math.max(0, rawExpAfter - rawExpBefore);
            int skillDelta = Math.max(0, skillExpAfter - skillExpBefore);

            String expMessage;
            if (rawDelta > 0 && skillDelta <= 0) {
                expMessage = "Gained +" + rawDelta + " raw EXP (skill is MAX).";
            } else if (rawDelta > 0) {
                expMessage = "Gained +" + skillDelta + " " + skillName + " EXP and +" + rawDelta + " raw EXP.";
            } else {
                expMessage = "Gained +" + skillDelta + " " + skillName + " EXP.";
            }

            AppDialogs.showMessageDialog(
                null,
                "Already unlocked: " + String.join(", ", matchedDisplayNames) + "\n" + expMessage,
                "Recipe Known",
                javax.swing.JOptionPane.INFORMATION_MESSAGE
            );
            return true;
        }

        if (!consumeOneItem.getAsBoolean()) {
            return false;
        }

        for (String recipeKey : newlyUnlocked) {
            gameState.unlockRecipe(recipeKey);
        }

        List<String> displayNames = new ArrayList<>();
        for (String recipeKey : newlyUnlocked) {
            RecipeCatalog.RecipeDefinition definition = RecipeCatalog.findDefinitionByKey(recipeKey);
            displayNames.add(
                definition == null
                    ? recipeKey.replace('_', ' ')
                    : definition.getDisplayName()
            );
        }

        AppDialogs.showMessageDialog(
            null,
            "Unlocked recipe: " + String.join(", ", displayNames),
            "Recipe Unlocked",
            javax.swing.JOptionPane.INFORMATION_MESSAGE
        );
        return true;
    }

    protected String itemName(int itemId) {
        var itm = ItemDatabase.getItemById(itemId);
        return (itm == null) ? ("item-" + itemId) : itm.getItem_name();
    }

    protected void disableKeyboardActivationRecursive(Node node) {
        if (node == null) return;

        if (node instanceof ButtonBase buttonBase) {
            buttonBase.setFocusTraversable(false);
        }

        if (node instanceof Pane pane) {
            for (Node child : pane.getChildren()) {
                disableKeyboardActivationRecursive(child);
            }
            return;
        }

        if (node instanceof Group group) {
            for (Node child : group.getChildren()) {
                disableKeyboardActivationRecursive(child);
            }
        }
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
