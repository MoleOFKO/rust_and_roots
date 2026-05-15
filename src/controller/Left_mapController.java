package controller;

import db.SaveGameService;
import game.GameState;
import game.GameTimeManager;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import javafx.scene.control.ProgressBar;
import model.CollisionRegistry;
import model.ItemDatabase;
import model.TreeMapState;
import model.item;
import util.AppDialogs;
import util.CombatRules;
import util.SceneManager;
import util.SkillModifiers;
import util.SpawnTag;
/**
 * FXML Controller class
 *
 * @author MITUSER-1
 */
public class Left_mapController extends BaseMapController {

    private static final String DAY_BG_PATH = "/image/maps/leftmap.png";
    private static final String NIGHT_BG_PATH = "/image/NightMap/leftmapnight.png";
    @FXML
    private ImageView bg;
    @FXML
    private Group daynightnavgroup;
    @FXML
    private Label day;
    @FXML
    private ImageView daynightcircleimg;
    @FXML
    private Label time;
    @FXML
    private Label name;
    @FXML
    private Label coin;
    @FXML
    private ProgressBar hpbar;
    @FXML
    private ProgressBar spbar;
    @FXML
    private Button btntuto;
    @FXML
    private Group tree1;
    @FXML
    private ImageView tree_img1;
    @FXML
    private Rectangle tree_hitbox1;
    @FXML
    private ImageView drops_img_tree1;
    @FXML
    private Group tree2;
    @FXML
    private ImageView tree_img2;
    @FXML
    private Rectangle tree_hitbox2;
    @FXML
    private ImageView drops_img_tree2;
    @FXML
    private Group tree3;
    @FXML
    private ImageView tree_img3;
    @FXML
    private Rectangle tree_hitbox3;
    @FXML
    private ImageView drops_img_tree3;
    @FXML
    private Group treelayorgp1;
    @FXML
    private ImageView treelayorimg1;
    @FXML
    private Group treelayorgp2;
    @FXML
    private ImageView treelayorimg2;
    @FXML
    private Group treelayorgp3;
    @FXML
    private ImageView treelayorimg3;
    @FXML
    private Group stone1;
    @FXML
    private ImageView stone_img1;
    @FXML
    private Rectangle stone_hitbox1;
    @FXML
    private ImageView drops_img_stone1;
    @FXML
    private Group stone2;
    @FXML
    private ImageView stone_img2;
    @FXML
    private Rectangle stone_hitbox2;
    @FXML
    private ImageView drops_img_stone2;
    @FXML
    private Group stone3;
    @FXML
    private ImageView stone_img3;
    @FXML
    private Rectangle stone_hitbox3;
    @FXML
    private ImageView drops_img_stone3;
    @FXML
    private Group minimapicongroup;
    @FXML
    private ImageView miniMapIcon;
    @FXML
    private Group minimapgroup;
    @FXML
    private ImageView minimap;
    @FXML
    private ImageView closeminimapbtn;
    @FXML
    private Rectangle left_map_nofour;
    @FXML
    private Rectangle upper_map_nofour;
    @FXML
    private Rectangle right_map_nofour;
    @FXML
    private Rectangle left_map_nothree_of_one;
    @FXML
    private Rectangle left_map_nothree;
    @FXML
    private Rectangle upper_map_nothree;
    @FXML
    private Rectangle right_map_nothree;
    @FXML
    private Rectangle right_map_nothree_of_one;
    @FXML
    private Rectangle left_map_notwo_of_two;
    @FXML
    private Rectangle left_map_notwo_of_one;
    @FXML
    private Rectangle left_map_notwo;
    @FXML
    private Rectangle upper_map_notwo;
    @FXML
    private Rectangle right_map_notwo;
    @FXML
    private Rectangle right_map_notwo_of_one;
    @FXML
    private Rectangle left_map_noone_of_one;
    @FXML
    private Rectangle left_map_noone;
    @FXML
    private Rectangle upper_map;
    @FXML
    private Rectangle right_map_noone;
    @FXML
    private Rectangle right_map_noone_of_one;
    @FXML
    private Rectangle left_map;
    @FXML
    private Rectangle home_map;
    @FXML
    private Rectangle right_map;
    @FXML
    private Slider bgmVolumeSlider;
    @FXML
    private Slider sfxVolumeSlider;

    @Override
    protected String dayBackgroundPath() {
        return DAY_BG_PATH;
    }

    @Override
    protected String nightBackgroundPath() {
        return NIGHT_BG_PATH;
    }

    private static final String MAIN_TREE_ID = "tree";
    private static final String MAIN_STONE_ID = "stone";
    private static final double AUTO_GATHER_MARGIN_PIXELS = 2.0;
    // Spec: tree/stone respawn base time is 3 in-game hours (10,800 seconds).
    private static final double TREE_RESPAWN_SECONDS = 3.0 * 60.0 * 60.0;

    private static final double TREE_DROP_TTL_SECONDS = 12.0 * 60.0 * 60.0;
    private static final double TREE_DROP_AUTO_COLLECT_DELAY_SECONDS = 0.2 * 60.0;
    private static final int TREE_STARTING_MAX_HP = 10;
    private static final int TREE_MAX_DROPS_PER_LIFE = 10;
    private static final int TREE_STAGE2_MAX_ALIVE_DROPS = 9;
    private static final int TREE_STAGE3_MAX_ALIVE_DROPS = 8;
    private static final int TREE_DROP_ROLL_HP_CHUNK = 10;
    // HP growth uses GameTimeManager.totalSeconds (in-game seconds).
    // GameTimeManager runs at ~24 in-game seconds per real second, so scale values accordingly.
    private static final double TREE_HP_REGEN_SECONDS = 48.0;       // ~2 real seconds
    private static final double TREE_HP_CAP_GROW_SECONDS = 1440.0;  // ~1 real minute

    // Growth rules (tunable). Stage 2 = day 1, stage 3 = day >= 2.
    private static final int TREE_STAGE3_AFTER_LIVED_DAYS = 1;

    private TreeMapState.TreeData mainTreeData;
    private String renderedTreeVisualKey = null;
    private String renderedTreeLayerVisualKey = null;
    private String renderedTreeDropsKey = null;
    private boolean warnedTreeInventoryFull = false;

    private TreeMapState.TreeData mainStoneData;
    private String renderedStoneVisualKey = null;
    private String renderedStoneDropsKey = null;
    private boolean warnedStoneInventoryFull = false;

    private int treeLogItemId = -1;
    private int treeStumpItemId = -1;
    private int plantFiberItemId = -1;

    private int stoneItemId = -1;
    private int sandItemId = -1;

    private List<String> cachedValidTreeTypeKeys = null;

    @FXML
    private void handlebtntutoaction(ActionEvent event) {
        showOverworldTutorialOverlay();
    }
    
    @FunctionalInterface
    private interface ResourceDropSpawner {
        void spawn(TreeMapState.TreeData resource, double nowSeconds);
    }

    @FunctionalInterface
    private interface ResourceLifecycleHandler {
        void handle(TreeMapState.TreeData resource, int currentDay, double nowSeconds);
    }

    @FXML
    private StackPane view;
    @FXML
    private Group world;
    @FXML
    private Group walls;
    @FXML
    private Rectangle wall;
    @FXML
    private Rectangle wall1;
    @FXML
    private Rectangle wall2;
    @FXML
    private Rectangle wall3;
    @FXML
    private Rectangle wall4;
    @FXML
    private Rectangle wall5;
    @FXML
    private Rectangle wall6;
    @FXML
    private Rectangle wall7;
    @FXML
    private Rectangle wall8;
    @FXML
    private Rectangle wall9;
    @FXML
    private Rectangle wall10;
    @FXML
    private Rectangle wall11;
    @FXML
    private Rectangle wall12;
    @FXML
    private Rectangle wall13;
    @FXML
    private Rectangle wall14;
    @FXML
    private Rectangle wall15;
    @FXML
    private Rectangle wall16;
    @FXML
    private Rectangle wall17;
    @FXML
    private Rectangle exit_up;
    @FXML
    private Rectangle spawn_default;
    @FXML
    private Rectangle spawn_from_up;
    @FXML
    private Group human;
    @FXML
    private Rectangle human_hitbox;
    @FXML
    private ImageView human_img;
    @FXML
    private HBox hotbar;
    @FXML
    private Label hotbarguide;
    @FXML
    private StackPane Hslot;
    @FXML
    private StackPane Hslot1;
    @FXML
    private StackPane Hslot2;
    @FXML
    private StackPane Hslot3;
    @FXML
    private StackPane Hslot4;
    @FXML
    private ImageView slotImg;
    @FXML
    private ImageView slotImg1;
    @FXML
    private ImageView slotImg2;
    @FXML
    private ImageView slotImg3;
    @FXML
    private ImageView slotImg4;
    @FXML
    private Label slotLb;
    @FXML
    private Label slotLb1;
    @FXML
    private Label slotLb2;
    @FXML
    private Label slotLb3;
    @FXML
    private Label slotLb4;
    @FXML
    private ToggleButton slotBtn;
    @FXML
    private ToggleButton slotBtn1;
    @FXML
    private ToggleButton slotBtn2;
    @FXML
    private ToggleButton slotBtn3;
    @FXML
    private ToggleButton slotBtn4;
    @FXML
    private ToggleGroup selectedhotbar;
    @FXML
    private StackPane Hslot5;
    @FXML
    private Button slotBtn5;
    @FXML
    private Pane inventoryOverlay;
    @FXML
    private Group menuInventory;
    @FXML
    private Group player_inv_group;
    @FXML
    private ImageView Playerinvimg;
    @FXML
    private Button Playerinvbtn;
    @FXML
    private Label Playerinvlbl;
    @FXML
    private ImageView Playerinvimg1;
    @FXML
    private Button Playerinvbtn1;
    @FXML
    private Label Playerinvlbl1;
    @FXML
    private ImageView Playerinvimg2;
    @FXML
    private Button Playerinvbtn2;
    @FXML
    private Label Playerinvlbl2;
    @FXML
    private ImageView Playerinvimg3;
    @FXML
    private Button Playerinvbtn3;
    @FXML
    private Label Playerinvlbl3;
    @FXML
    private ImageView Playerinvimg4;
    @FXML
    private Button Playerinvbtn4;
    @FXML
    private Label Playerinvlbl4;
    @FXML
    private ImageView Playerinvimg5;
    @FXML
    private Button Playerinvbtn5;
    @FXML
    private Label Playerinvlbl5;
    @FXML
    private ImageView Playerinvimg6;
    @FXML
    private Button Playerinvbtn6;
    @FXML
    private Label Playerinvlbl6;
    @FXML
    private ImageView Playerinvimg7;
    @FXML
    private Button Playerinvbtn7;
    @FXML
    private Label Playerinvlbl7;
    @FXML
    private ImageView Playerinvimg8;
    @FXML
    private Button Playerinvbtn8;
    @FXML
    private Label Playerinvlbl8;
    @FXML
    private ImageView Playerinvimg9;
    @FXML
    private Button Playerinvbtn9;
    @FXML
    private Label Playerinvlbl9;
    @FXML
    private ImageView Playerinvimg10;
    @FXML
    private Button Playerinvbtn10;
    @FXML
    private Label Playerinvlbl10;
    @FXML
    private ImageView Playerinvimg11;
    @FXML
    private Button Playerinvbtn11;
    @FXML
    private Label Playerinvlbl11;
    @FXML
    private ImageView Playerinvimg12;
    @FXML
    private Button Playerinvbtn12;
    @FXML
    private Label Playerinvlbl12;
    @FXML
    private ImageView Playerinvimg13;
    @FXML
    private Button Playerinvbtn13;
    @FXML
    private Label Playerinvlbl13;
    @FXML
    private ImageView Playerinvimg14;
    @FXML
    private Button Playerinvbtn14;
    @FXML
    private Label Playerinvlbl14;
    @FXML
    private ImageView Playerinvimg15;
    @FXML
    private Button Playerinvbtn15;
    @FXML
    private Label Playerinvlbl15;
    @FXML
    private ImageView Playerinvimg16;
    @FXML
    private Button Playerinvbtn16;
    @FXML
    private Label Playerinvlbl16;
    @FXML
    private ImageView Playerinvimg17;
    @FXML
    private Button Playerinvbtn17;
    @FXML
    private Label Playerinvlbl17;
    @FXML
    private ImageView Playerinvimg18;
    @FXML
    private Button Playerinvbtn18;
    @FXML
    private Label Playerinvlbl18;
    @FXML
    private ImageView Playerinvimg19;
    @FXML
    private Button Playerinvbtn19;
    @FXML
    private Label Playerinvlbl19;
    @FXML
    private Group statusTab;
    @FXML
    private Label statusName;
    @FXML
    private Label statusEXP;
    @FXML
    private ImageView btnUsePoint;
    @FXML
    private ImageView btnConfirm;
    @FXML
    private Group statusgroup;
    @FXML
    private Label statusHP;
    @FXML
    private Label statusSP;
    @FXML
    private Label statusSTR;
    @FXML
    private Label statusVIT;
    @FXML
    private Label statusDEX;
    @FXML
    private Label statusAGI;
    @FXML
    private ImageView btnskill;
    @FXML
    private ImageView btnHPminus;
    @FXML
    private ImageView btnHPplus;
    @FXML
    private ImageView btnSPminus;
    @FXML
    private ImageView btnSTRminus;
    @FXML
    private ImageView btnVITminus;
    @FXML
    private ImageView btnDEXminus;
    @FXML
    private ImageView btnAGIminus;
    @FXML
    private ImageView btnSPplus;
    @FXML
    private ImageView btnSTRplus;
    @FXML
    private ImageView btnVITplus;
    @FXML
    private ImageView btnDEXplus;
    @FXML
    private ImageView btnAGIplus;
    @FXML
    private Group skillsgroup;
    @FXML
    private ImageView btnstatus;
    @FXML
    private Label farming;
    @FXML
    private Label gathering;
    @FXML
    private Label crafting;
    @FXML
    private Label combat;
    @FXML
    private Label cooking;
    @FXML
    private ImageView btnfarmingminus1;
    @FXML
    private ImageView btnfarmingplus1;
    @FXML
    private ImageView btngatheringminus1;
    @FXML
    private ImageView btncraftingminus1;
    @FXML
    private ImageView btncombatminus1;
    @FXML
    private ImageView btncookingminus1;
    @FXML
    private ImageView btngatheringplus1;
    @FXML
    private ImageView btncraftingplus1;
    @FXML
    private ImageView btncombatplus1;
    @FXML
    private ImageView btncookingplus1;
    @FXML
    private Group settingsTab;
    private Slider timescaleslider;
    private Button btnsetday;
    private Button btnsetnight;
    private Button btnplus1h;
    @FXML
    private Button btnsaveandexit;
    private Label sliderscale;
    private Label lbldays;
    private Label lbltime;
    @FXML
    private Button btnsave;
    @FXML
    private Button btnload;
    @FXML
    private Button openInventory;
    @FXML
    private ImageView btnclose;
    @FXML
    private Button openstatus;
    @FXML
    private Button opensetting;
    @FXML
    private ImageView equip_img;
    @FXML
    private Rectangle spawn_from_right;
    @FXML
    private Rectangle exit_right;
    @FXML
    private Rectangle human_hitbox_left;
    @FXML
    private Rectangle human_hitbox_up;
    @FXML
    private Rectangle human_hitbox_down;
    @FXML
    private Rectangle human_hitbox_right;
    @FXML
    private Group tree;
    @FXML
    private ImageView tree_img;
    @FXML
    private ImageView drops_img_tree;
    @FXML
    private Rectangle tree_hitbox;
    @FXML
    private Group stone;
    @FXML
    private ImageView stone_img;
    @FXML
    private ImageView drops_img_stone;
    @FXML
    private Rectangle stone_hitbox;
    @FXML
    private Group treelayorgp;
    @FXML
    private ImageView treelayorimg;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        bridgeLeftMapSharedUi();
        refreshStatusAllocationUi();
        refreshMenuState();
    }

    private void setupTreeSystemIfPresent() {
        if (tree_img == null || tree_hitbox == null) {
            return;
        }

        if (gameState == null) {
            gameState = GameState.get();
        }

        TreeMapState mapTrees = gameState.getTreeState(mapKey());
        mainTreeData = mapTrees.getTree(MAIN_TREE_ID);

        int currentDay = GameTimeManager.get().dayNumberProperty().get();
        double nowSeconds = GameTimeManager.get().totalSecondsProperty().get();
	        initializeTreeDefaults(mainTreeData, currentDay, nowSeconds);

	        if (player != null) {
	            registerPlayerAttackHandler("resources", this::handlePlayerAttackStarted);
	        }

        // Tree hitboxes are used for intersection/collision only, not input.
        tree_hitbox.setMouseTransparent(true);
        if (treelayorgp != null) treelayorgp.setMouseTransparent(true);
        if (treelayorimg != null) treelayorimg.setMouseTransparent(true);

        refreshPlayerCollisionWalls();

        // Render once immediately.
        renderTreeVisual();
        renderTreeDrops();
        javafx.application.Platform.runLater(this::syncTreeLayerPosition);
    }

    private void setupStoneSystemIfPresent() {
        if (stone_img == null || stone_hitbox == null) {
            return;
        }

        if (gameState == null) {
            gameState = GameState.get();
        }

        ItemDatabase.loadItems();

        TreeMapState mapTrees = gameState.getTreeState(mapKey());
        mainStoneData = mapTrees.getTree(MAIN_STONE_ID);

        int currentDay = GameTimeManager.get().dayNumberProperty().get();
        double nowSeconds = GameTimeManager.get().totalSecondsProperty().get();
	        initializeStoneDefaults(mainStoneData, currentDay, nowSeconds);

	        if (player != null) {
	            registerPlayerAttackHandler("resources", this::handlePlayerAttackStarted);
	        }

        // Stone hitboxes are used for intersection/collision only, not input.
        stone_hitbox.setMouseTransparent(true);
        refreshPlayerCollisionWalls();

        renderStoneVisual();
        renderStoneDrops();
    }

	    private void refreshPlayerCollisionWalls() {
	        List<Rectangle> colliders = new ArrayList<>();
	        if (tree_hitbox != null) colliders.add(tree_hitbox);
	        if (stone_hitbox != null) colliders.add(stone_hitbox);
	        registerPlayerCollisionRectangles(colliders);
	        rebuildPlayerCollisionWalls();
	    }

    private void initializeTreeDefaults(TreeMapState.TreeData data, int currentDay, double nowSeconds) {
        if (data == null) return;

        if (data.typeKey == null || data.typeKey.isBlank() || !isValidTreeTypeKey(data.typeKey)) {
            data.typeKey = randomTreeTypeKey();
        }

        if (data.lastUpdatedDayNumber <= 0) {
            data.lastUpdatedDayNumber = Math.max(1, currentDay);
        }

        if (data.livedDays < 0) data.livedDays = 0;

        if (data.currentStage < 1 || data.currentStage > 3) {
            data.currentStage = 2;
        }

        if (data.dropRatePity < 0) data.dropRatePity = 0;
        if (data.dropsThisLife < 0) data.dropsThisLife = 0;
        if (data.damageSinceLastDropRoll < 0) data.damageSinceLastDropRoll = 0;
        if (data.damageSinceLastDropRoll >= TREE_DROP_ROLL_HP_CHUNK) {
            data.damageSinceLastDropRoll %= TREE_DROP_ROLL_HP_CHUNK;
        }

        if (data.currentStage == 1) {
            data.hp = 0;
            data.maxHp = 0;
            data.vit = 0;
            data.lastHpGrowthTotalSeconds = -1;
            if (data.respawnAtTotalSeconds < 0) {
                data.respawnAtTotalSeconds = nowSeconds + TREE_RESPAWN_SECONDS;
            }
        } else {
            data.currentStage = stageFromLivedDays(data.livedDays);
            data.vit = vitFor(data.livedDays);

            if (data.maxHp <= 0) {
                data.maxHp = TREE_STARTING_MAX_HP;
            }
            if (data.maxHp < TREE_STARTING_MAX_HP) {
                data.maxHp = TREE_STARTING_MAX_HP;
            }
            if (data.hp <= 0) {
                data.hp = data.maxHp;
            }
            if (data.hp > data.maxHp) data.hp = data.maxHp;
            if (data.hp < 0) data.hp = 0;

            if (data.lastHpGrowthTotalSeconds < 0 || data.lastHpGrowthTotalSeconds > nowSeconds) {
                data.lastHpGrowthTotalSeconds = nowSeconds;
            }
            data.respawnAtTotalSeconds = -1;
        }
    }

    private void initializeStoneDefaults(TreeMapState.TreeData data, int currentDay, double nowSeconds) {
        if (data == null) return;

        if (data.typeKey == null || data.typeKey.isBlank()) {
            data.typeKey = "stone";
        }

        if (data.lastUpdatedDayNumber <= 0) {
            data.lastUpdatedDayNumber = Math.max(1, currentDay);
        }

        if (data.livedDays < 0) data.livedDays = 0;

        if (data.currentStage < 1 || data.currentStage > 3) {
            data.currentStage = 2;
        }

        if (data.dropRatePity < 0) data.dropRatePity = 0;
        if (data.dropsThisLife < 0) data.dropsThisLife = 0;
        if (data.damageSinceLastDropRoll < 0) data.damageSinceLastDropRoll = 0;
        if (data.damageSinceLastDropRoll >= TREE_DROP_ROLL_HP_CHUNK) {
            data.damageSinceLastDropRoll %= TREE_DROP_ROLL_HP_CHUNK;
        }

        if (data.currentStage == 1) {
            data.hp = 0;
            data.maxHp = 0;
            data.vit = 0;
            data.lastHpGrowthTotalSeconds = -1;
            if (data.respawnAtTotalSeconds < 0) {
                data.respawnAtTotalSeconds = nowSeconds + TREE_RESPAWN_SECONDS;
            }
        } else {
            data.currentStage = stageFromLivedDays(data.livedDays);
            data.vit = vitFor(data.livedDays);

            if (data.maxHp <= 0) {
                data.maxHp = TREE_STARTING_MAX_HP;
            }
            if (data.maxHp < TREE_STARTING_MAX_HP) {
                data.maxHp = TREE_STARTING_MAX_HP;
            }
            if (data.hp <= 0) {
                data.hp = data.maxHp;
            }
            if (data.hp > data.maxHp) data.hp = data.maxHp;
            if (data.hp < 0) data.hp = 0;

            if (data.lastHpGrowthTotalSeconds < 0 || data.lastHpGrowthTotalSeconds > nowSeconds) {
                data.lastHpGrowthTotalSeconds = nowSeconds;
            }
            data.respawnAtTotalSeconds = -1;
        }
    }

    private String randomTreeTypeKey() {
        List<String> keys = getValidTreeTypeKeys();
        if (keys.isEmpty()) {
            return "blue";
        }
        return keys.get(ThreadLocalRandom.current().nextInt(keys.size()));
    }

    private List<String> getValidTreeTypeKeys() {
        if (cachedValidTreeTypeKeys != null) {
            return cachedValidTreeTypeKeys;
        }

        ItemDatabase.loadItems();

        Set<String> keys = new HashSet<>();
        for (item itm : ItemDatabase.getAllItems()) {
            if (itm == null) continue;
            String name = itm.getItem_name();
            if (name == null || name.isBlank()) continue;

            String normalized = name.trim().toLowerCase(Locale.ROOT);
            if (!normalized.endsWith("_tree1")) {
                continue;
            }

            String base = normalized.substring(0, normalized.length() - "_tree1".length());
            if (base.isBlank()) continue;
            if (isValidTreeTypeKey(base)) {
                keys.add(base);
            }
        }

        List<String> list = new ArrayList<>(keys);
        Collections.sort(list);
        cachedValidTreeTypeKeys = list;
        return cachedValidTreeTypeKeys;
    }

    private boolean isValidTreeTypeKey(String typeKey) {
        if (typeKey == null || typeKey.isBlank()) return false;
        String base = typeKey.trim().toLowerCase(Locale.ROOT);

        item tree1 = ItemDatabase.getItemByName(base + "_tree1");
        item tree2 = ItemDatabase.getItemByName(base + "_tree2");
        item fruit = ItemDatabase.getItemByName(base + "_fruit");
        if (tree1 == null || tree2 == null || fruit == null) {
            return false;
        }

        if (!ItemDatabase.isDisplayableItem(tree1.getItem_id())
            || !ItemDatabase.isDisplayableItem(tree2.getItem_id())
            || !ItemDatabase.isDisplayableItem(fruit.getItem_id())) {
            return false;
        }

        // Enforce that every tree sprite also has an "upper part" image variant.
        // Spec: <base>_tree<number>.png and <base>_tree<number>up.png live under /image/items/.
        String up1 = "/image/items/" + base + "_tree1up.png";
        String up2 = "/image/items/" + base + "_tree2up.png";
        return getClass().getResource(up1) != null && getClass().getResource(up2) != null;
    }

    @Override
    protected void onGameLoopUpdate(Bounds playerBounds) {
        syncDirectionalHitboxes();
        updateTreeState();
        updateStoneState();
        autoGatherDrops(playerBounds);
    }

    private void syncDirectionalHitboxes() {
        if (player == null) return;
        var facing = player.getFacingDirection();

        setDirectionalHitboxActive(human_hitbox_left, facing == model.Player.FacingDirection.LEFT);
        setDirectionalHitboxActive(human_hitbox_right, facing == model.Player.FacingDirection.RIGHT);
        setDirectionalHitboxActive(human_hitbox_up, facing == model.Player.FacingDirection.UP);
        setDirectionalHitboxActive(human_hitbox_down, facing == model.Player.FacingDirection.DOWN);
    }

    private void setDirectionalHitboxActive(Rectangle hitbox, boolean active) {
        if (hitbox == null) return;
        hitbox.setDisable(!active);
        hitbox.setVisible(active);
        hitbox.setManaged(active);
    }

    private Rectangle resolveActiveAttackHitbox() {
        if (player == null) return null;
        return switch (player.getFacingDirection()) {
            case LEFT -> human_hitbox_left;
            case RIGHT -> human_hitbox_right;
            case UP -> human_hitbox_up;
            case DOWN -> human_hitbox_down;
        };
    }

    private void handlePlayerAttackStarted() {
        Rectangle attackHitbox = resolveActiveAttackHitbox();
        if (attackHitbox == null) return;

        Bounds attackBounds = attackHitbox.localToScene(attackHitbox.getBoundsInLocal());
        GameTimeManager time = GameTimeManager.get();
        int currentDay = time.dayNumberProperty().get();
        double nowSeconds = time.totalSecondsProperty().get();

        if (mainTreeData != null && tree_hitbox != null && mainTreeData.currentStage != 1) {
            Bounds treeBounds = tree_hitbox.localToScene(tree_hitbox.getBoundsInLocal());
            if (attackBounds.intersects(treeBounds)) {
                applyPlayerAttackToResource(
                    "TREE",
                    mainTreeData,
                    currentDay,
                    nowSeconds,
                    calculatePlayerBaseDamage(),
                    resolveTreeLogItemId(),
                    resolvePlantFiberItemId(),
                    49.0,
                    this::spawnAliveTreeDropForStage,
                    this::killTree,
                    () -> {
                        renderTreeVisual();
                        renderTreeDrops();
                    }
                );
            }
        }

        if (mainStoneData != null && stone_hitbox != null && mainStoneData.currentStage != 1) {
            Bounds stoneBounds = stone_hitbox.localToScene(stone_hitbox.getBoundsInLocal());
            if (attackBounds.intersects(stoneBounds)) {
                applyPlayerAttackToResource(
                    "STONE",
                    mainStoneData,
                    currentDay,
                    nowSeconds,
                    calculatePlayerBaseDamageForStone(),
                    resolveStoneItemId(),
                    resolveSandItemId(),
                    49.0,
                    this::spawnAliveStoneDropForStage,
                    this::killStone,
                    () -> {
                        renderStoneVisual();
                        renderStoneDrops();
                    }
                );
            }
        }
    }

    private void applyPlayerAttackToResource(
        String debugTag,
        TreeMapState.TreeData resource,
        int currentDay,
        double nowSeconds,
        int baseDamage,
        int commonDropItemId,
        int uncommonDropItemId,
        double uncommonDropChancePercent,
        ResourceDropSpawner dropSpawner,
        ResourceLifecycleHandler killHandler,
        Runnable render
    ) {
        if (resource == null) return;
        if (resource.currentStage == 1) return;

        // Keep vit and HP growth up-to-date even if this map wasn't ticking.
        applyTreeDailyGrowthIfNeeded(resource, currentDay);
        applyTreeHpGrowthIfNeeded(resource, nowSeconds);

        // Pause natural regeneration while the resource is actively being attacked.
        resource.lastHpGrowthTotalSeconds = nowSeconds;
        resource.currentStage = stageFromLivedDays(resource.livedDays);
        resource.vit = vitFor(resource.livedDays);

        int resourceVit = Math.max(0, resource.vit);
        int receivedDamage = CombatRules.applyDamageReduction(baseDamage, resourceVit);
        int reducedDamage = Math.max(0, baseDamage - receivedDamage);

        int hpBefore = Math.max(0, resource.hp);
        int maxHp = Math.max(0, resource.maxHp);
        int actualDamage = Math.min(receivedDamage, hpBefore);
        int hpAfter = Math.max(0, hpBefore - actualDamage);

        System.out.println(
            "[" + (debugTag == null ? "RESOURCE" : debugTag) + "] HP " + hpBefore + "/" + maxHp +
            ", VIT " + resourceVit +
            " | PlayerDamage " + baseDamage +
            " | Reduced " + reducedDamage +
            " | Received " + actualDamage +
            " | HP -> " + hpAfter + "/" + maxHp
        );

        if (actualDamage <= 0) {
            return;
        }

        resource.hp = hpAfter;
        applyResourceDropRollsForDamage(
            resource,
            actualDamage,
            nowSeconds,
            dropSpawner,
            commonDropItemId,
            uncommonDropItemId,
            uncommonDropChancePercent
        );
        if (resource.hp <= 0 && killHandler != null) {
            killHandler.handle(resource, currentDay, nowSeconds);
        }

        if (render != null) {
            render.run();
        }
    }

    private int calculatePlayerBaseDamage() {
        if (playerState == null) return 0;

        int playerStr = Math.max(0, playerState.getStr());
        int bonus = isAxeEquipped() ? 1 : 0;
        return CombatRules.rollAttackDamage(playerStr, bonus);
    }

    private int calculatePlayerBaseDamageForStone() {
        if (playerState == null) return 0;

        int playerStr = Math.max(0, playerState.getStr());
        int bonus = isPickaxeEquipped() ? 1 : 0;
        return CombatRules.rollAttackDamage(playerStr, bonus);
    }

    private boolean isAxeEquipped() {
        if (player == null) return false;
        int equippedId = player.getEquippedItemId();
        if (equippedId <= 0) return false;
        String name = ItemDatabase.getItemName(equippedId);
        if (name == null || name.isBlank()) return false;

        String normalized = name.trim().toLowerCase(Locale.ROOT);
        if (normalized.equals("axe")) return true;

        // Be defensive for future item naming like "Stone Axe". Avoid matching pickaxes.
        if (normalized.contains("pickaxe") || normalized.contains("pick axe")) return false;
        return normalized.contains("axe");
    }

    private boolean isPickaxeEquipped() {
        if (player == null) return false;
        int equippedId = player.getEquippedItemId();
        if (equippedId <= 0) return false;
        String name = ItemDatabase.getItemName(equippedId);
        if (name == null || name.isBlank()) return false;

        String normalized = name.trim().toLowerCase(Locale.ROOT);
        if (normalized.equals("pickaxe") || normalized.equals("pick axe")) return true;
        return normalized.contains("pickaxe") || normalized.contains("pick axe");
    }

    private void killTree(TreeMapState.TreeData tree, int currentDay, double nowSeconds) {
        if (tree == null) return;
        if (tree.currentStage == 1) return;

        double respawnSeconds = computeResourceRespawnSeconds(tree);
        tree.currentStage = 1;
        tree.hp = 0;
        tree.maxHp = 0;
        tree.vit = 0;
        tree.respawnAtTotalSeconds = nowSeconds + respawnSeconds;
        tree.livedDays = 0;
        tree.lastUpdatedDayNumber = Math.max(1, currentDay);
        tree.lastHpGrowthTotalSeconds = -1;

        tree.dropRatePity = 0;
        tree.damageSinceLastDropRoll = 0;
    }

    private void respawnTree(TreeMapState.TreeData tree, int currentDay, double nowSeconds) {
        if (tree == null) return;

        tree.typeKey = randomTreeTypeKey();
        tree.livedDays = Math.max(0, currentDay - 1);
        tree.currentStage = stageFromLivedDays(tree.livedDays);
        tree.vit = vitFor(tree.livedDays);
        tree.maxHp = TREE_STARTING_MAX_HP;
        tree.hp = TREE_STARTING_MAX_HP;
        tree.respawnAtTotalSeconds = -1;
        tree.lastUpdatedDayNumber = Math.max(1, currentDay);
        tree.lastHpGrowthTotalSeconds = nowSeconds;

        tree.dropRatePity = 0;
        tree.dropsThisLife = 0;
        tree.damageSinceLastDropRoll = 0;
    }

    private void killStone(TreeMapState.TreeData stone, int currentDay, double nowSeconds) {
        if (stone == null) return;
        if (stone.currentStage == 1) return;

        double respawnSeconds = computeResourceRespawnSeconds(stone);
        stone.currentStage = 1;
        stone.hp = 0;
        stone.maxHp = 0;
        stone.vit = 0;
        stone.respawnAtTotalSeconds = nowSeconds + respawnSeconds;
        stone.livedDays = 0;
        stone.lastUpdatedDayNumber = Math.max(1, currentDay);
        stone.lastHpGrowthTotalSeconds = -1;

        stone.dropRatePity = 0;
        stone.damageSinceLastDropRoll = 0;
    }

    private void respawnStone(TreeMapState.TreeData stone, int currentDay, double nowSeconds) {
        if (stone == null) return;

        stone.typeKey = "stone";
        stone.livedDays = Math.max(0, currentDay - 1);
        stone.currentStage = stageFromLivedDays(stone.livedDays);
        stone.vit = vitFor(stone.livedDays);
        stone.maxHp = TREE_STARTING_MAX_HP;
        stone.hp = TREE_STARTING_MAX_HP;
        stone.respawnAtTotalSeconds = -1;
        stone.lastUpdatedDayNumber = Math.max(1, currentDay);
        stone.lastHpGrowthTotalSeconds = nowSeconds;

        stone.dropRatePity = 0;
        stone.dropsThisLife = 0;
        stone.damageSinceLastDropRoll = 0;
    }

    private double computeResourceRespawnSeconds(TreeMapState.TreeData resource) {
        if (resource == null) {
            return TREE_RESPAWN_SECONDS;
        }

        int totalStatusPoints = 0;
        totalStatusPoints += Math.max(0, resource.maxHp);
        totalStatusPoints += Math.max(0, resource.vit);

        return Math.max(TREE_RESPAWN_SECONDS, (double) totalStatusPoints);
    }

    private void applyResourceDropRollsForDamage(
        TreeMapState.TreeData resource,
        int hpLost,
        double nowSeconds,
        ResourceDropSpawner dropSpawner,
        int commonDropItemId,
        int uncommonDropItemId,
        double uncommonDropChancePercent
    ) {
        if (resource == null) return;
        if (resource.currentStage != 2 && resource.currentStage != 3) return;
        if (hpLost <= 0) return;

        int gatheringLevel = 0;
        if (gameState != null && gameState.getPlayerSkills() != null) {
            gatheringLevel = Math.max(0, gameState.getPlayerSkills().getGatheringLevel());
        }
        int commonBonus = SkillModifiers.gatheringCommonBonusPercent(gatheringLevel);
        int uncommonBonus = SkillModifiers.gatheringUncommonBonusPercent(gatheringLevel);
        double adjustedUncommonChancePercent = Math.max(0.0, Math.min(100.0, uncommonDropChancePercent + uncommonBonus));

        int remainderBefore = Math.max(0, resource.damageSinceLastDropRoll);
        int hpAfterCall = Math.max(0, resource.hp);
        int hpBeforeCall = hpAfterCall + hpLost;

        resource.damageSinceLastDropRoll = remainderBefore + hpLost;

        int processedRolls = 0;

        while (resource.damageSinceLastDropRoll >= TREE_DROP_ROLL_HP_CHUNK) {
            resource.damageSinceLastDropRoll -= TREE_DROP_ROLL_HP_CHUNK;
            processedRolls++;

            int chunkDamageFromThisCall = (processedRolls * TREE_DROP_ROLL_HP_CHUNK) - remainderBefore;
            if (chunkDamageFromThisCall < 0) {
                chunkDamageFromThisCall = 0;
            }
            chunkDamageFromThisCall = Math.min(hpLost, chunkDamageFromThisCall);

            int hpAfterChunk = Math.max(0, hpBeforeCall - chunkDamageFromThisCall);

            if (hpAfterChunk < 20) {
                trySpawnTreeDrop(resource, commonDropItemId, nowSeconds);
                trySpawnGatheringCommonBonusDrop(resource, commonDropItemId, nowSeconds, commonBonus);
                if (uncommonDropItemId > 0
                    && adjustedUncommonChancePercent > 0.0
                    && ThreadLocalRandom.current().nextDouble(100.0) < adjustedUncommonChancePercent) {
                    trySpawnTreeDrop(resource, uncommonDropItemId, nowSeconds);
                }
                continue;
            }

            if (uncommonDropItemId > 0
                && adjustedUncommonChancePercent > 0.0
                && ThreadLocalRandom.current().nextDouble(100.0) < adjustedUncommonChancePercent) {
                trySpawnTreeDrop(resource, uncommonDropItemId, nowSeconds);
            }

            if (dropSpawner == null || !canSpawnAliveDrop(resource)) {
                continue;
            }

            int roll = rollTreeDropNumber(resource);
            if (roll == 0) {
                continue;
            }
            if (roll == 10) {
                resource.dropRatePity = 0;
                dropSpawner.spawn(resource, nowSeconds);
                continue;
            }

            resource.dropRatePity = Math.min(10, Math.max(0, resource.dropRatePity) + roll);
            if (resource.dropRatePity >= 10) {
                resource.dropRatePity = 0;
                dropSpawner.spawn(resource, nowSeconds);
            }
        }

        if (resource.damageSinceLastDropRoll < 0) resource.damageSinceLastDropRoll = 0;
        if (resource.dropRatePity < 0) resource.dropRatePity = 0;
        if (resource.dropRatePity > 10) resource.dropRatePity = 10;
    }

    private void spawnAliveTreeDropForStage(TreeMapState.TreeData tree, double nowSeconds) {
        if (tree == null) return;
        if (!canSpawnAliveDrop(tree)) return;

        int logId = resolveTreeLogItemId();
        if (tree.currentStage == 2) {
            trySpawnTreeDrop(tree, logId, nowSeconds);
            trySpawnGatheringCommonBonusDrop(tree, logId, nowSeconds, SkillModifiers.gatheringCommonBonusPercent(getGatheringLevel()));
            return;
        }

        int fruitId = resolveFruitItemIdForTypeKey(tree.typeKey);
        int chosen = chooseValidTreeDropItemId(logId, fruitId);
        trySpawnTreeDrop(tree, chosen, nowSeconds);
        trySpawnGatheringCommonBonusDrop(tree, chosen, nowSeconds, SkillModifiers.gatheringCommonBonusPercent(getGatheringLevel()));
    }

    private void spawnAliveStoneDropForStage(TreeMapState.TreeData stone, double nowSeconds) {
        if (stone == null) return;
        if (!canSpawnAliveDrop(stone)) return;

        int stoneId = resolveStoneItemId();
        trySpawnTreeDrop(stone, stoneId, nowSeconds);
        trySpawnGatheringCommonBonusDrop(stone, stoneId, nowSeconds, SkillModifiers.gatheringCommonBonusPercent(getGatheringLevel()));
    }

    private void applyGuaranteedDeathDrops(TreeMapState.TreeData tree, int stageAtDeath, double nowSeconds) {
        if (tree == null) return;

        int logId = resolveTreeLogItemId();
        int fruitId = resolveFruitItemIdForTypeKey(tree.typeKey);
        int fiberId = resolvePlantFiberItemId();
        int commonBonus = SkillModifiers.gatheringCommonBonusPercent(getGatheringLevel());

        if (stageAtDeath == 2) {
            trySpawnTreeDrop(tree, logId, nowSeconds);
            trySpawnGatheringCommonBonusDrop(tree, logId, nowSeconds, commonBonus);
            trySpawnPlantFiberDrop(tree, fiberId, nowSeconds);
            return;
        }

        if (stageAtDeath == 3) {
            trySpawnTreeDrop(tree, logId, nowSeconds);
            trySpawnGatheringCommonBonusDrop(tree, logId, nowSeconds, commonBonus);
            trySpawnTreeDrop(tree, fruitId, nowSeconds);
            trySpawnGatheringCommonBonusDrop(tree, fruitId, nowSeconds, commonBonus);
            trySpawnPlantFiberDrop(tree, fiberId, nowSeconds);
        }
    }

    private void applyGuaranteedStoneDeathDrops(TreeMapState.TreeData stone, int stageAtDeath, double nowSeconds) {
        if (stone == null) return;

        int stoneId = resolveStoneItemId();
        int sandId = resolveSandItemId();
        trySpawnTreeDrop(stone, stoneId, nowSeconds);
        trySpawnGatheringCommonBonusDrop(stone, stoneId, nowSeconds, SkillModifiers.gatheringCommonBonusPercent(getGatheringLevel()));

        int gatheringLevel = getGatheringLevel();
        double sandChance = 49.0 + SkillModifiers.gatheringUncommonBonusPercent(gatheringLevel);
        if (sandId > 0 && sandChance > 0.0 && ThreadLocalRandom.current().nextDouble(100.0) < Math.min(100.0, sandChance)) {
            trySpawnTreeDrop(stone, sandId, nowSeconds);
        }
    }

    private boolean canSpawnAliveDrop(TreeMapState.TreeData tree) {
        if (tree == null) return false;

        int aliveCap = switch (tree.currentStage) {
            case 2 -> TREE_STAGE2_MAX_ALIVE_DROPS;
            case 3 -> TREE_STAGE3_MAX_ALIVE_DROPS;
            default -> 0;
        };

        return tree.dropsThisLife < aliveCap && tree.dropsThisLife < TREE_MAX_DROPS_PER_LIFE;
    }

    private int rollTreeDropNumber(TreeMapState.TreeData tree) {
        int treeVit = Math.max(0, tree == null ? 0 : tree.vit);
        int playerDex = Math.max(0, playerState == null ? 0 : playerState.getDex());

        int zeroWeight = calculateZeroRollWeight(playerDex, treeVit);
        int positiveWeightEach = 10; // 1..10 all remain equal to each other
        int totalWeight = zeroWeight + (10 * positiveWeightEach);

        int pick = ThreadLocalRandom.current().nextInt(totalWeight);

        if (pick < zeroWeight) {
            return 0;
        }

        return ((pick - zeroWeight) / positiveWeightEach) + 1; // 1..10
    }

    private int calculateZeroRollWeight(int playerDex, int treeVit) {
        int safeDex = Math.max(0, playerDex);
        int safeVit = Math.max(0, treeVit);

        if (safeDex > safeVit * 2) {
            return 0;
        }

        int baseZeroWeight = 10; // equal to each of the positive numbers at baseline

        if (safeDex <= safeVit) {
            return baseZeroWeight;
        }

        double progress = (double) (safeDex - safeVit) / Math.max(1.0, safeVit);
        double clamped = Math.max(0.0, Math.min(1.0, progress / 2.0));

        return (int) Math.round(baseZeroWeight * (1.0 - clamped));
    }
    
    private void trySpawnPlantFiberDrop(TreeMapState.TreeData tree, int plantFiberId, double nowSeconds) {
        if (tree == null) return;
        if (plantFiberId <= 0) return;

        double chancePercent = computePlantFiberDropChancePercent(tree);
        if (chancePercent <= 0.0) return;

        if (ThreadLocalRandom.current().nextDouble(100.0) >= chancePercent) {
            return;
        }

        trySpawnTreeDrop(tree, plantFiberId, nowSeconds);
    }

    private int chooseValidTreeDropItemId(int logId, int fruitId) {
        boolean hasLog = logId > 0;
        boolean hasFruit = fruitId > 0;

        if (hasLog && hasFruit) {
            return ThreadLocalRandom.current().nextBoolean() ? logId : fruitId;
        }
        if (hasLog) return logId;
        return fruitId;
    }

    private int chooseStoneDropItemId(int stoneId, int sandId) {
        boolean hasStone = stoneId > 0;
        boolean hasSand = sandId > 0;

        if (hasStone && hasSand) {
            // 51% stone, 49% sand
            return ThreadLocalRandom.current().nextDouble(100.0) < 51.0 ? stoneId : sandId;
        }
        if (hasStone) return stoneId;
        return sandId;
    }

    private boolean trySpawnTreeDrop(TreeMapState.TreeData tree, int itemId, double nowSeconds) {
        if (tree == null) return false;
        if (tree.dropsThisLife >= TREE_MAX_DROPS_PER_LIFE) return false;
        if (itemId <= 0) return false;

        tree.drops.add(new TreeMapState.TreeDrop(itemId, 1, nowSeconds, nowSeconds + TREE_DROP_TTL_SECONDS));
        tree.dropsThisLife += 1;
        return true;
    }

    private boolean cleanupExpiredTreeDrops(TreeMapState.TreeData tree, double nowSeconds) {
        if (tree == null || tree.drops.isEmpty()) return false;
        int before = tree.drops.size();
        tree.drops.removeIf(drop ->
            drop == null ||
            drop.itemId <= 0 ||
            drop.quantity <= 0 ||
            (drop.expiresAtTotalSeconds > 0 && nowSeconds >= drop.expiresAtTotalSeconds)
        );
        return tree.drops.size() != before;
    }

    private int resolveTreeLogItemId() {
        if (treeLogItemId > 0) return treeLogItemId;
        treeLogItemId = resolveItemId("tree_log");
        return treeLogItemId;
    }

    private int resolvePlantFiberItemId() {
        if (plantFiberItemId > 0) return plantFiberItemId;
        plantFiberItemId = resolveItemId("plant_fiber");
        return plantFiberItemId;
    }

    private int resolveStoneItemId() {
        if (stoneItemId > 0) return stoneItemId;
        stoneItemId = resolveItemId("stone_drop_item");
        return stoneItemId;
    }

    private int resolveSandItemId() {
        if (sandItemId > 0) return sandItemId;
        sandItemId = resolveItemId("sand");
        return sandItemId;
    }

    private int resolveTreeStumpItemId() {
        if (treeStumpItemId > 0) return treeStumpItemId;
        treeStumpItemId = resolveItemId("tree_stumb");
        return treeStumpItemId;
    }

    private double computePlantFiberDropChancePercent(TreeMapState.TreeData tree) {
        int playerDex = Math.max(0, playerState == null ? 0 : playerState.getDex());
        int gatheringLevel = getGatheringLevel();

        int stage = tree == null ? 0 : tree.currentStage;
        double base = stage >= 3 ? 12.0 : 9.0;
        double chance = base
            + (playerDex * 0.20)
            + SkillModifiers.gatheringUncommonBonusPercent(gatheringLevel)
            + SkillModifiers.gatheringRareBonusPercent(gatheringLevel);
        return Math.max(0.0, Math.min(35.0, chance));
    }

    private int getGatheringLevel() {
        if (gameState == null || gameState.getPlayerSkills() == null) {
            return 0;
        }
        return Math.max(0, gameState.getPlayerSkills().getGatheringLevel());
    }

    private void trySpawnGatheringCommonBonusDrop(TreeMapState.TreeData resource, int itemId, double nowSeconds, int bonusPercent) {
        if (resource == null || itemId <= 0) return;
        int chance = Math.max(0, Math.min(100, bonusPercent));
        if (chance <= 0) return;
        if (ThreadLocalRandom.current().nextDouble(100.0) < chance) {
            trySpawnTreeDrop(resource, itemId, nowSeconds);
        }
    }

    private int resolveFruitItemIdForTypeKey(String typeKey) {
        String base = (typeKey == null) ? "" : typeKey.trim().toLowerCase(Locale.ROOT);
        if (base.isBlank()) {
            base = "blue";
        }
        return resolveItemId(base + "_fruit");
    }

    private int resolveItemId(String itemName) {
        item itm = ItemDatabase.getItemByName(itemName);
        return (itm == null) ? -1 : itm.getItem_id();
    }

    private void autoGatherDrops(Bounds playerBoundsScene) {
        if (player == null || playerBoundsScene == null) return;

        double nowSeconds = GameTimeManager.get().totalSecondsProperty().get();
        warnedTreeInventoryFull = autoGatherResourceDrops(mainTreeData, tree_hitbox, playerBoundsScene, nowSeconds, warnedTreeInventoryFull, this::renderTreeDrops);
        warnedStoneInventoryFull = autoGatherResourceDrops(mainStoneData, stone_hitbox, playerBoundsScene, nowSeconds, warnedStoneInventoryFull, this::renderStoneDrops);
    }

    private boolean autoGatherResourceDrops(
        TreeMapState.TreeData resource,
        Rectangle resourceHitbox,
        Bounds playerBoundsScene,
        double nowSeconds,
        boolean warnedInventoryFull,
        Runnable rerender
    ) {
        if (resource == null || resourceHitbox == null || player == null || playerBoundsScene == null) {
            return false;
        }

        boolean inside = intersectsWithMargin(playerBoundsScene, resourceHitbox, AUTO_GATHER_MARGIN_PIXELS);
        if (!inside) {
            return false;
        }

        if (resource.drops == null || resource.drops.isEmpty()) {
            return warnedInventoryFull;
        }

        boolean removedAny = false;
        Iterator<TreeMapState.TreeDrop> it = resource.drops.iterator();
        while (it.hasNext()) {
            TreeMapState.TreeDrop drop = it.next();
            if (drop == null || drop.itemId <= 0 || drop.quantity <= 0) {
                it.remove();
                removedAny = true;
                continue;
            }
            if (nowSeconds < drop.spawnedAtTotalSeconds + TREE_DROP_AUTO_COLLECT_DELAY_SECONDS) {
                continue;
            }

            if (!player.getInventory().addItem(drop.itemId, drop.quantity)) {
                if (!warnedInventoryFull) {
                    AppDialogs.showMessageDialog(null, "Not enough inventory space to collect these drops.");
                    warnedInventoryFull = true;
                }
                break;
            }

            if (gameState != null) {
                gameState.addGatheringExpFromCollectedItems(drop.quantity);
            }

            it.remove();
            removedAny = true;
        }

        if (removedAny && rerender != null) {
            rerender.run();
        }

        return warnedInventoryFull;
    }

    private boolean intersectsWithMargin(Bounds playerBoundsScene, Rectangle targetHitbox, double marginPixels) {
        if (playerBoundsScene == null || targetHitbox == null) return false;
        Bounds targetBoundsScene = targetHitbox.localToScene(targetHitbox.getBoundsInLocal());
        double margin = Math.max(0.0, marginPixels);

        Bounds expanded = new BoundingBox(
            targetBoundsScene.getMinX() - margin,
            targetBoundsScene.getMinY() - margin,
            targetBoundsScene.getWidth() + (margin * 2.0),
            targetBoundsScene.getHeight() + (margin * 2.0)
        );

        return expanded.intersects(playerBoundsScene);
    }

    private void updateResourceState(
        TreeMapState.TreeData resource,
        int currentDay,
        double nowSeconds,
        ResourceLifecycleHandler respawnHandler,
        ResourceLifecycleHandler killHandler
    ) {
        if (resource == null) return;

        cleanupExpiredTreeDrops(resource, nowSeconds);

        if (resource.currentStage == 1) {
            resource.hp = 0;
            resource.maxHp = 0;
            resource.vit = 0;

            if (respawnHandler != null && resource.respawnAtTotalSeconds >= 0 && nowSeconds >= resource.respawnAtTotalSeconds) {
                respawnHandler.handle(resource, currentDay, nowSeconds);
            }
            return;
        }

        applyTreeDailyGrowthIfNeeded(resource, currentDay);
        applyTreeHpGrowthIfNeeded(resource, nowSeconds);

        resource.currentStage = stageFromLivedDays(resource.livedDays);
        resource.vit = vitFor(resource.livedDays);

        if (resource.maxHp < TREE_STARTING_MAX_HP) {
            resource.maxHp = TREE_STARTING_MAX_HP;
        }
        if (resource.hp > resource.maxHp) resource.hp = resource.maxHp;
        if (resource.hp < 0) resource.hp = 0;

        if (killHandler != null && resource.hp == 0) {
            killHandler.handle(resource, currentDay, nowSeconds);
        }
    }

    private void updateTreeState() {
        if (mainTreeData == null) return;

        GameTimeManager time = GameTimeManager.get();
        int currentDay = time.dayNumberProperty().get();
        double nowSeconds = time.totalSecondsProperty().get();

        updateResourceState(mainTreeData, currentDay, nowSeconds, this::respawnTree, this::killTree);

        renderTreeVisual();
        renderTreeDrops();
    }

    private void updateStoneState() {
        if (mainStoneData == null) return;

        GameTimeManager time = GameTimeManager.get();
        int currentDay = time.dayNumberProperty().get();
        double nowSeconds = time.totalSecondsProperty().get();

        updateResourceState(mainStoneData, currentDay, nowSeconds, this::respawnStone, this::killStone);

        renderStoneVisual();
        renderStoneDrops();
    }

    private void applyTreeDailyGrowthIfNeeded(TreeMapState.TreeData tree, int currentDay) {
        if (tree == null) return;
        if (tree.currentStage == 1) return;
        int safeDay = Math.max(1, currentDay);
        int last = tree.lastUpdatedDayNumber <= 0 ? safeDay : tree.lastUpdatedDayNumber;

        if (safeDay == last) {
            return;
        }

        if (safeDay < last) {
            tree.lastUpdatedDayNumber = safeDay;
            return;
        }

        int delta = safeDay - last;
        tree.lastUpdatedDayNumber = safeDay;
        tree.livedDays = Math.max(0, tree.livedDays + delta);
        tree.currentStage = stageFromLivedDays(tree.livedDays);
        tree.vit = vitFor(tree.livedDays);
    }

    private void applyTreeHpGrowthIfNeeded(TreeMapState.TreeData tree, double nowSeconds) {
        if (tree == null) return;
        if (tree.currentStage == 1) return;

        if (tree.maxHp < TREE_STARTING_MAX_HP) {
            tree.maxHp = TREE_STARTING_MAX_HP;
        }
        if (tree.hp > tree.maxHp) tree.hp = tree.maxHp;
        if (tree.hp < 0) tree.hp = 0;

        if (tree.lastHpGrowthTotalSeconds < 0 || tree.lastHpGrowthTotalSeconds > nowSeconds) {
            tree.lastHpGrowthTotalSeconds = nowSeconds;
            return;
        }

        double elapsed = nowSeconds - tree.lastHpGrowthTotalSeconds;
        if (elapsed <= 0) {
            return;
        }

        double remaining = elapsed;
        while (true) {
            if (tree.hp < tree.maxHp) {
                long ticks = (long) Math.floor(remaining / TREE_HP_REGEN_SECONDS);
                int needed = tree.maxHp - tree.hp;
                long apply = Math.min(ticks, (long) Math.max(0, needed));
                if (apply <= 0) {
                    break;
                }

                tree.hp += (int) apply;
                remaining -= apply * TREE_HP_REGEN_SECONDS;
                continue;
            }

            long capTicks = (long) Math.floor(remaining / TREE_HP_CAP_GROW_SECONDS);
            if (capTicks <= 0) {
                break;
            }

            int inc = capTicks > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) capTicks;
            tree.maxHp += inc;
            tree.hp += inc; // stays full while growing
            remaining -= inc * TREE_HP_CAP_GROW_SECONDS;
        }

        tree.lastHpGrowthTotalSeconds = nowSeconds - remaining;
    }

    private int stageFromLivedDays(int livedDays) {
        return Math.max(0, livedDays) >= TREE_STAGE3_AFTER_LIVED_DAYS ? 3 : 2;
    }

    private int vitFor(int livedDays) {
        int days = Math.max(0, livedDays);
        return Math.max(1, days + 1);
    }

    private void renderTreeVisual() {
        if (tree_img == null || mainTreeData == null) return;

        syncTreeLayerPosition();

        String visualKey;
        if (mainTreeData.currentStage == 1) {
            visualKey = "stage:1";
        } else {
            visualKey = "stage:" + mainTreeData.currentStage + "#type:" + (mainTreeData.typeKey == null ? "null" : mainTreeData.typeKey);
        }

        boolean visualUnchanged = visualKey.equals(renderedTreeVisualKey);

        tree_img.setManaged(true);
        tree_img.setVisible(true);
        if (tree_hitbox != null) {
            tree_hitbox.setDisable(mainTreeData.currentStage == 1);
        }

        if (mainTreeData.currentStage == 1) {
            tree_img.setImage(null);
            tree_img.setVisible(false);
            tree_img.setManaged(false);
            hideTreeLayerVisual("stage:1");
            renderedTreeVisualKey = visualKey;
            return;
        }

        if (visualUnchanged) {
            return;
        }
        renderedTreeVisualKey = visualKey;

        String base = (mainTreeData.typeKey == null) ? "blue" : mainTreeData.typeKey.trim().toLowerCase(Locale.ROOT);
        String spriteName = base + "_tree" + (mainTreeData.currentStage == 3 ? "2" : "1");
        item spriteItem = ItemDatabase.getItemByName(spriteName);

        if (spriteItem != null) {
            tree_img.setImage(ItemDatabase.getImage(spriteItem.getItem_id()));
        } else {
            // Fallback to loading directly from resources if DB entry is missing.
            tree_img.setImage(loadImageOrNull("/image/items/" + spriteName + ".png"));
        }

        if (tree_img.getImage() == null) {
            hideTreeLayerVisual("sprite:missing");
        } else {
            renderTreeLayerVisual(spriteName, visualKey);
        }
    }

    private void renderTreeLayerVisual(String spriteName, String visualKey) {
        if (treelayorimg == null || treelayorgp == null) {
            return;
        }

        syncTreeLayerPosition();

        String key = (visualKey == null || visualKey.isBlank())
            ? ("sprite:" + (spriteName == null ? "null" : spriteName))
            : ("layer:" + visualKey);

        if (Objects.equals(key, renderedTreeLayerVisualKey)) {
            return;
        }
        renderedTreeLayerVisualKey = key;

        if (spriteName == null || spriteName.isBlank()) {
            hideTreeLayerVisual("sprite:null");
            return;
        }

        if (tree_img != null) {
            treelayorimg.setFitWidth(tree_img.getFitWidth());
            treelayorimg.setFitHeight(tree_img.getFitHeight());
        }
        treelayorimg.setPreserveRatio(false);
        treelayorimg.setSmooth(false);
        treelayorimg.setPickOnBounds(true);

        var upper = loadImageOrNull("/image/items/" + spriteName + "up.png");
        if (upper == null) {
            hideTreeLayerVisual("sprite:missing_upper");
            return;
        }

        treelayorimg.setImage(upper);
        treelayorimg.setVisible(true);
        treelayorimg.setManaged(true);
        treelayorgp.setVisible(true);
        treelayorgp.setManaged(true);
    }

    private void hideTreeLayerVisual(String key) {
        if (treelayorimg == null || treelayorgp == null) {
            renderedTreeLayerVisualKey = key;
            return;
        }

        if (key != null && !key.isBlank()) {
            renderedTreeLayerVisualKey = key;
        }

        treelayorimg.setImage(null);
        treelayorimg.setVisible(false);
        treelayorimg.setManaged(false);

        treelayorgp.setVisible(false);
        treelayorgp.setManaged(false);
    }

    private void syncTreeLayerPosition() {
        if (treelayorgp == null || tree_img == null) return;
        if (treelayorgp.getParent() == null) return;
        if (tree_img.getScene() == null || treelayorgp.getScene() == null) return;

        Bounds treeBoundsScene = tree_img.localToScene(tree_img.getBoundsInLocal());
        Bounds treeBoundsInParent = treelayorgp.getParent().sceneToLocal(treeBoundsScene);

        treelayorgp.setLayoutX(treeBoundsInParent.getMinX());
        treelayorgp.setLayoutY(treeBoundsInParent.getMinY());
    }

    private String renderResourceDrops(
        TreeMapState.TreeData resource,
        ImageView dropView,
        String renderedKey,
        Runnable rerender
    ) {
        if (dropView == null || resource == null) {
            return renderedKey;
        }

        String key = buildTreeDropsKey(resource);
        if (key.equals(renderedKey)) {
            return renderedKey;
        }

        TreeMapState.TreeDrop first = null;
        for (TreeMapState.TreeDrop d : resource.drops) {
            if (d == null || d.itemId <= 0 || d.quantity <= 0) continue;
            if (ItemDatabase.getImage(d.itemId) == null) continue;
            first = d;
            break;
        }

        if (first == null) {
            dropView.setImage(null);
            dropView.setVisible(false);
            dropView.setManaged(false);
            dropView.setOnMouseClicked(null);
            return key;
        }

        final TreeMapState.TreeDrop displayedDrop = first;
        dropView.setImage(ItemDatabase.getImage(displayedDrop.itemId));
        boolean show = dropView.getImage() != null;
        dropView.setVisible(show);
        dropView.setManaged(show);
        dropView.setPickOnBounds(true);
        dropView.setMouseTransparent(true);
        dropView.setOnMouseClicked(null);

        return key;
    }

    private void renderTreeDrops() {
        renderedTreeDropsKey = renderResourceDrops(mainTreeData, drops_img_tree, renderedTreeDropsKey, this::renderTreeDrops);
    }

    private void renderStoneVisual() {
        if (stone_img == null || mainStoneData == null) return;

        String visualKey = mainStoneData.currentStage == 1
            ? "stage:1"
            : "stage:" + mainStoneData.currentStage;

        if (visualKey.equals(renderedStoneVisualKey)) {
            return;
        }
        renderedStoneVisualKey = visualKey;

        stone_img.setManaged(true);
        stone_img.setVisible(true);
        if (stone_hitbox != null) {
            stone_hitbox.setDisable(mainStoneData.currentStage == 1);
        }

        if (mainStoneData.currentStage == 1) {
            stone_img.setImage(null);
            stone_img.setVisible(false);
            stone_img.setManaged(false);
            return;
        }

        stone_img.setImage(loadImageOrNull("/image/weapons/stone.png"));
    }

    private void renderStoneDrops() {
        renderedStoneDropsKey = renderResourceDrops(mainStoneData, drops_img_stone, renderedStoneDropsKey, this::renderStoneDrops);
    }

    private String buildTreeDropsKey(TreeMapState.TreeData tree) {
        if (tree == null || tree.drops.isEmpty()) {
            return "drops:none";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("drops:").append(tree.drops.size());
        for (TreeMapState.TreeDrop d : tree.drops) {
            if (d == null) continue;
            sb.append('#').append(d.itemId).append('x').append(d.quantity);
        }
        return sb.toString();
    }

    private void bridgeLeftMapSharedUi() {
        List<StackPane> hotbarSlotNodes = List.of(Hslot, Hslot1, Hslot2, Hslot3, Hslot4);
        List<ImageView> hotbarImageNodes = List.of(slotImg, slotImg1, slotImg2, slotImg3, slotImg4);
        List<Label> hotbarLabelNodes = List.of(slotLb, slotLb1, slotLb2, slotLb3, slotLb4);
        List<ToggleButton> hotbarButtonNodes = List.of(slotBtn, slotBtn1, slotBtn2, slotBtn3, slotBtn4);

        bridgeSharedHotbarUi(
            hotbar,
            hotbarguide,
            hotbarSlotNodes,
            hotbarImageNodes,
            hotbarLabelNodes,
            hotbarButtonNodes
        );

        List<ImageView> playerInvImageNodes = List.of(
            Playerinvimg, Playerinvimg1, Playerinvimg2, Playerinvimg3, Playerinvimg4,
            Playerinvimg5, Playerinvimg6, Playerinvimg7, Playerinvimg8, Playerinvimg9,
            Playerinvimg10, Playerinvimg11, Playerinvimg12, Playerinvimg13, Playerinvimg14,
            Playerinvimg15, Playerinvimg16, Playerinvimg17, Playerinvimg18, Playerinvimg19
        );

        List<Label> playerInvLabelNodes = List.of(
            Playerinvlbl, Playerinvlbl1, Playerinvlbl2, Playerinvlbl3, Playerinvlbl4,
            Playerinvlbl5, Playerinvlbl6, Playerinvlbl7, Playerinvlbl8, Playerinvlbl9,
            Playerinvlbl10, Playerinvlbl11, Playerinvlbl12, Playerinvlbl13, Playerinvlbl14,
            Playerinvlbl15, Playerinvlbl16, Playerinvlbl17, Playerinvlbl18, Playerinvlbl19
        );

        List<Button> playerInvButtonNodes = List.of(
            Playerinvbtn, Playerinvbtn1, Playerinvbtn2, Playerinvbtn3, Playerinvbtn4,
            Playerinvbtn5, Playerinvbtn6, Playerinvbtn7, Playerinvbtn8, Playerinvbtn9,
            Playerinvbtn10, Playerinvbtn11, Playerinvbtn12, Playerinvbtn13, Playerinvbtn14,
            Playerinvbtn15, Playerinvbtn16, Playerinvbtn17, Playerinvbtn18, Playerinvbtn19
        );

        bridgeSharedInventoryOverlayUi(
            inventoryOverlay,
            menuInventory,
            statusTab,
            settingsTab,
            statusgroup,
            skillsgroup,
            player_inv_group,
            openInventory,
            openstatus,
            opensetting,
            btnclose,
            btnskill,
            btnstatus,
            playerInvImageNodes,
            playerInvLabelNodes,
            playerInvButtonNodes
        );

        rebuildSharedUiAfterBridge();
    }

    private void bindStatusLabels() {
        if (playerState == null) return;

        playerState.playerNameProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
        playerState.expProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
        playerState.hpProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
        playerState.maxHpProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
        playerState.staminaProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
        playerState.maxStaminaProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
        playerState.strProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
        playerState.vitProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
        playerState.dexProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
        playerState.agiProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
    }

    private void bindSettingControls() {
        GameTimeManager time = GameTimeManager.get();

        if (timescaleslider != null) {
            timescaleslider.valueProperty().unbindBidirectional(time.timeScaleProperty());
            timescaleslider.valueProperty().bindBidirectional(time.timeScaleProperty());
        }

        if (sliderscale != null) {
            sliderscale.textProperty().unbind();
            sliderscale.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("Current Time Speed : x%.1f", time.timeScaleProperty().get()),
                time.timeScaleProperty()
            ));
        }

        if (lbldays != null) {
            lbldays.textProperty().unbind();
            lbldays.textProperty().bind(Bindings.createStringBinding(
                () -> "Days : " + time.dayNumberProperty().get(),
                time.dayNumberProperty()
            ));
        }

        if (lbltime != null) {
            lbltime.textProperty().unbind();
            lbltime.textProperty().bind(Bindings.createStringBinding(
                () -> "Time : " + time.formattedProperty().get(),
                time.formattedProperty()
            ));
        }

        if (btnsetday != null) btnsetday.setOnAction(e -> time.setTimeOfDay(8, 0));
        if (btnsetnight != null) btnsetnight.setOnAction(e -> time.setTimeOfDay(20, 0));
        if (btnplus1h != null) btnplus1h.setOnAction(e -> time.addMinutes(60));
        if (btnsaveandexit != null) {
            btnsaveandexit.setOnAction(e -> {
                try {
                    capturePlayerSnapshot();
                    new SaveGameService().saveGame(GameState.get());
                    if (gameLoop != null) gameLoop.stop();
                    if (player != null) player.stop();
                    SceneManager.switchScene("/view/main_menu.fxml");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    @Override
    protected void refreshStatusAllocationUi() {
        refreshStatusView();
        refreshSkillsView();
        refreshStatusButtons();
    }

    private void refreshStatusView() {
        if (playerState == null) return;

        if (statusName != null) statusName.setText("Name : " + playerState.getPlayerName());
        if (statusEXP != null) statusEXP.setText(formatVisibleExpLabel());
        if (statusHP != null) {
            statusHP.setText("HP : " + playerState.getHp() + "/" + (playerState.getMaxHp() + pendingHpPoints) + formatPending(pendingHpPoints));
        }
        if (statusSP != null) {
            statusSP.setText("SP : " + playerState.getStamina() + "/" + (playerState.getMaxStamina() + pendingSpPoints) + formatPending(pendingSpPoints));
        }
        if (statusSTR != null) statusSTR.setText("STR : " + playerState.getStr() + formatPending(pendingStrPoints));
        if (statusVIT != null) statusVIT.setText("VIT : " + playerState.getVit() + formatPending(pendingVitPoints));
        if (statusDEX != null) statusDEX.setText("DEX : " + playerState.getDex() + formatPending(pendingDexPoints));
        if (statusAGI != null) statusAGI.setText("AGI : " + playerState.getAgi() + formatPending(pendingAgiPoints));
    }

    private String formatPending(int points) {
        return points <= 0 ? "" : "(+" + points + ")";
    }

    private void refreshSkillsView() {
        if (gameState == null) return;
        GameState.PlayerSkills skills = gameState.getPlayerSkills();

        if (farming != null) farming.setText(appendPendingSkillCost(skills.getFarmingDisplay(), pendingFarmingSkillExp));
        if (gathering != null) gathering.setText(appendPendingSkillCost(skills.getGatheringDisplay(), pendingGatheringSkillExp));
        if (crafting != null) crafting.setText(appendPendingSkillCost(skills.getCraftingDisplay(), pendingCraftingSkillExp));
        if (combat != null) combat.setText(appendPendingSkillCost(skills.getCombatDisplay(), pendingCombatSkillExp));
        if (cooking != null) cooking.setText(appendPendingSkillCost(skills.getCookingDisplay(), pendingCookingSkillExp));
    }

    private void refreshStatusButtons() {
        boolean canUsePoint = playerState != null && playerState.getExp() > 0;
        boolean skillsVisible = isSkillsScreenVisible();

        configureImageButton(btnUsePoint, canUsePoint, true, statusAllocationMode || skillAllocationMode ? 1.0 : 0.85);

        if (skillsVisible) {
            configureImageButton(btnConfirm, skillAllocationMode && getPendingSkillExpTotal() > 0, skillAllocationMode, 1.0);

            configureImageButton(btnfarmingplus1, canIncreaseSkill("farming"), skillAllocationMode, 1.0);
            configureImageButton(btngatheringplus1, canIncreaseSkill("gathering"), skillAllocationMode, 1.0);
            configureImageButton(btncraftingplus1, canIncreaseSkill("crafting"), skillAllocationMode, 1.0);
            configureImageButton(btncombatplus1, canIncreaseSkill("combat"), skillAllocationMode, 1.0);
            configureImageButton(btncookingplus1, canIncreaseSkill("cooking"), skillAllocationMode, 1.0);

            configureImageButton(btnfarmingminus1, skillAllocationMode && pendingFarmingSkillExp > 0, skillAllocationMode, 1.0);
            configureImageButton(btngatheringminus1, skillAllocationMode && pendingGatheringSkillExp > 0, skillAllocationMode, 1.0);
            configureImageButton(btncraftingminus1, skillAllocationMode && pendingCraftingSkillExp > 0, skillAllocationMode, 1.0);
            configureImageButton(btncombatminus1, skillAllocationMode && pendingCombatSkillExp > 0, skillAllocationMode, 1.0);
            configureImageButton(btncookingminus1, skillAllocationMode && pendingCookingSkillExp > 0, skillAllocationMode, 1.0);

            configureImageButton(btnHPplus, false, false, 1.0);
            configureImageButton(btnSPplus, false, false, 1.0);
            configureImageButton(btnSTRplus, false, false, 1.0);
            configureImageButton(btnVITplus, false, false, 1.0);
            configureImageButton(btnDEXplus, false, false, 1.0);
            configureImageButton(btnAGIplus, false, false, 1.0);
            configureImageButton(btnHPminus, false, false, 1.0);
            configureImageButton(btnSPminus, false, false, 1.0);
            configureImageButton(btnSTRminus, false, false, 1.0);
            configureImageButton(btnVITminus, false, false, 1.0);
            configureImageButton(btnDEXminus, false, false, 1.0);
            configureImageButton(btnAGIminus, false, false, 1.0);
            return;
        }

        if (isStatusScreenVisible()) {
            configureImageButton(btnConfirm, statusAllocationMode && getPendingExpCost() > 0, statusAllocationMode, 1.0);

            configureImageButton(btnHPplus, canIncreaseStat("hp"), statusAllocationMode, 1.0);
            configureImageButton(btnSPplus, canIncreaseStat("sp"), statusAllocationMode, 1.0);
            configureImageButton(btnSTRplus, canIncreaseStat("str"), statusAllocationMode, 1.0);
            configureImageButton(btnVITplus, canIncreaseStat("vit"), statusAllocationMode, 1.0);
            configureImageButton(btnDEXplus, canIncreaseStat("dex"), statusAllocationMode, 1.0);
            configureImageButton(btnAGIplus, canIncreaseStat("agi"), statusAllocationMode, 1.0);

            configureImageButton(btnHPminus, statusAllocationMode && pendingHpPoints > 0, statusAllocationMode, 1.0);
            configureImageButton(btnSPminus, statusAllocationMode && pendingSpPoints > 0, statusAllocationMode, 1.0);
            configureImageButton(btnSTRminus, statusAllocationMode && pendingStrPoints > 0, statusAllocationMode, 1.0);
            configureImageButton(btnVITminus, statusAllocationMode && pendingVitPoints > 0, statusAllocationMode, 1.0);
            configureImageButton(btnDEXminus, statusAllocationMode && pendingDexPoints > 0, statusAllocationMode, 1.0);
            configureImageButton(btnAGIminus, statusAllocationMode && pendingAgiPoints > 0, statusAllocationMode, 1.0);

            configureImageButton(btnfarmingplus1, false, false, 1.0);
            configureImageButton(btngatheringplus1, false, false, 1.0);
            configureImageButton(btncraftingplus1, false, false, 1.0);
            configureImageButton(btncombatplus1, false, false, 1.0);
            configureImageButton(btncookingplus1, false, false, 1.0);
            configureImageButton(btnfarmingminus1, false, false, 1.0);
            configureImageButton(btngatheringminus1, false, false, 1.0);
            configureImageButton(btncraftingminus1, false, false, 1.0);
            configureImageButton(btncombatminus1, false, false, 1.0);
            configureImageButton(btncookingminus1, false, false, 1.0);
        }
    }

    private void configureImageButton(ImageView imageView, boolean enabled, boolean visible, double activeOpacity) {
        if (imageView == null) return;
        imageView.setVisible(visible);
        imageView.setManaged(visible);
        imageView.setDisable(!enabled);
        imageView.setMouseTransparent(!visible || !enabled);
        imageView.setOpacity(!visible ? 0.0 : (enabled ? activeOpacity : 0.45));
    }

    @Override
    protected String mapKey() {
        return "left_map";
    }

    @Override
    protected void configureExits() {
       registerExit(exit_right, "/view/home_map.fxml", SpawnTag.FROM_LEFT);
       registerExit(exit_up, "/view/left_map_noone.fxml", SpawnTag.FROM_DOWN);
    }
}
