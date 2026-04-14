package controller;

import db.SaveGameService;
import game.GameState;
import game.GameTimeManager;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
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
import model.CollisionRegistry;
import model.ItemDatabase;
import model.TreeMapState;
import model.item;
import util.AppDialogs;
import util.SceneManager;
import util.SpawnTag;
/**
 * FXML Controller class
 *
 * @author MITUSER-1
 */
public class Left_mapController extends BaseMapController {

    private static final String MAIN_TREE_ID = "tree";
    // Respawn time while in stage 1: half an in-game day (12 hours).
    private static final double TREE_RESPAWN_SECONDS = 12.0 * 60.0 * 60.0;

    // Growth rules (tunable). Stage 2 = day 0, stage 3 = day >= 1.
    private static final int TREE_STAGE3_AFTER_LIVED_DAYS = 1;
    private static final int TREE_STAGE2_BASE_HP = 6;
    private static final int TREE_STAGE3_BASE_HP = 8;
    private static final int TREE_STAGE2_BASE_VIT = 1;
    private static final int TREE_STAGE3_BASE_VIT = 1;

    private TreeMapState.TreeData mainTreeData;
    private String renderedTreeVisualKey = null;

    private int treeLogItemId = -1;
    private int blueFruitItemId = -1;
    private int redFruitItemId = -1;
    private int yellowFruitItemId = -1;

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
    private Rectangle exit_left;
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
    private Label fishing;
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
    private ImageView btnfishingminus1;
    @FXML
    private ImageView btncookingminus1;
    @FXML
    private ImageView btngatheringplus1;
    @FXML
    private ImageView btncraftingplus1;
    @FXML
    private ImageView btnfishingplus1;
    @FXML
    private ImageView btncookingplus1;
    @FXML
    private Group settingsTab;
    @FXML
    private Slider timescaleslider;
    @FXML
    private Button btnsetday;
    @FXML
    private Button btnsetnight;
    @FXML
    private Button btnplus1h;
    @FXML
    private Button btnsaveandexit;
    @FXML
    private Label sliderscale;
    @FXML
    private Label lbldays;
    @FXML
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
    private Rectangle tree_hitbox;
    @FXML
    private Rectangle tree_hitbox_wall;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        bridgeLeftMapSharedUi();
        bindStatusLabels();
        bindSettingControls();
        refreshStatusAllocationUi();
        refreshMenuState();
        setupTreeSystemIfPresent();
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

        tree_img.setOnMouseClicked(this::handleTreeClicked);

        if (player != null) {
            player.setOnAttackStarted(this::handlePlayerAttackStarted);
        }

        // Ensure the tree wall participates in player collision on this map.
        if (player != null && tree_hitbox_wall != null) {
            List<Rectangle> colliders = new ArrayList<>();
            if (super.walls != null) {
                colliders.addAll(CollisionRegistry.collectWalls(super.walls));
            }
            colliders.add(tree_hitbox_wall);
            player.setWalls(colliders);
        }

        // Render once immediately.
        renderTreeVisual();
    }

    private void initializeTreeDefaults(TreeMapState.TreeData data, int currentDay, double nowSeconds) {
        if (data == null) return;

        if (data.type == null) {
            data.type = randomTreeType();
        }

        if (data.lastUpdatedDayNumber <= 0) {
            data.lastUpdatedDayNumber = Math.max(1, currentDay);
        }

        if (data.livedDays < 0) data.livedDays = 0;

        if (data.currentStage < 1 || data.currentStage > 3) {
            data.currentStage = stageFromLivedDays(data.livedDays);
        }

        if (data.currentStage == 1) {
            data.hp = 0;
            if (data.respawnAtTotalSeconds < 0) {
                data.respawnAtTotalSeconds = nowSeconds + TREE_RESPAWN_SECONDS;
            }
        } else {
            data.currentStage = stageFromLivedDays(data.livedDays);
            data.vit = vitFor(data.currentStage, data.livedDays);
            int maxHp = maxHpFor(data.currentStage, data.livedDays);
            if (data.hp <= 0 || data.hp > maxHp) data.hp = maxHp;
            data.respawnAtTotalSeconds = -1;
        }
    }

    private TreeMapState.TreeType randomTreeType() {
        TreeMapState.TreeType[] types = TreeMapState.TreeType.values();
        return types[ThreadLocalRandom.current().nextInt(types.length)];
    }

    @Override
    protected void onGameLoopUpdate(Bounds playerBounds) {
        syncDirectionalHitboxes();
        updateTreeState();
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
        if (mainTreeData == null || tree_hitbox == null) return;
        if (mainTreeData.currentStage == 1) return; // stage 1 cannot receive damage

        Rectangle attackHitbox = resolveActiveAttackHitbox();
        if (attackHitbox == null) return;

        Bounds attackBounds = attackHitbox.localToScene(attackHitbox.getBoundsInLocal());
        Bounds treeBounds = tree_hitbox.localToScene(tree_hitbox.getBoundsInLocal());
        if (!attackBounds.intersects(treeBounds)) {
            return;
        }

        int damage = calculatePlayerDamageAgainstTree(mainTreeData);
        if (damage <= 0) return;

        mainTreeData.hp = Math.max(0, mainTreeData.hp - damage);
        if (mainTreeData.hp <= 0) {
            chopTree(mainTreeData);
        }

        renderTreeVisual();
    }

    private int calculatePlayerDamageAgainstTree(TreeMapState.TreeData tree) {
        if (playerState == null || tree == null) return 0;

        int playerStr = Math.max(0, playerState.getStr());
        int treeVit = Math.max(0, tree.vit);

        int damage = playerStr - treeVit;
        if (isAxeEquipped()) {
            damage += 1;
        }
        return Math.max(0, damage);
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

    private void chopTree(TreeMapState.TreeData tree) {
        if (tree == null) return;
        if (tree.currentStage != 2 && tree.currentStage != 3) return;

        tree.pendingDrops.clear();

        int logId = resolveTreeLogItemId();
        int fruitId = resolveFruitItemIdForType(tree.type);

        int livedDays = Math.max(0, tree.livedDays);
        int logCount = 1 + (livedDays / 2);
        int fruitCount = (tree.currentStage == 3) ? (1 + (livedDays / 3)) : 0;

        if (logId > 0) {
            for (int i = 0; i < logCount; i++) tree.pendingDrops.addLast(logId);
        }
        if (fruitId > 0) {
            for (int i = 0; i < fruitCount; i++) tree.pendingDrops.addLast(fruitId);
        }

        double nowSeconds = GameTimeManager.get().totalSecondsProperty().get();
        int currentDay = GameTimeManager.get().dayNumberProperty().get();

        tree.currentStage = 1;
        tree.hp = 0;
        tree.respawnAtTotalSeconds = nowSeconds + TREE_RESPAWN_SECONDS;
        tree.livedDays = 0;
        tree.lastUpdatedDayNumber = Math.max(1, currentDay);
    }

    private int resolveTreeLogItemId() {
        if (treeLogItemId > 0) return treeLogItemId;
        treeLogItemId = resolveItemId("tree_log");
        return treeLogItemId;
    }

    private int resolveFruitItemIdForType(TreeMapState.TreeType type) {
        TreeMapState.TreeType safe = (type == null) ? TreeMapState.TreeType.BLUE : type;
        return switch (safe) {
            case BLUE -> resolveBlueFruitItemId();
            case RED -> resolveRedFruitItemId();
            case YELLOW -> resolveYellowFruitItemId();
        };
    }

    private int resolveBlueFruitItemId() {
        if (blueFruitItemId > 0) return blueFruitItemId;
        blueFruitItemId = resolveItemId("blue_fruit");
        return blueFruitItemId;
    }

    private int resolveRedFruitItemId() {
        if (redFruitItemId > 0) return redFruitItemId;
        redFruitItemId = resolveItemId("red_fruit");
        return redFruitItemId;
    }

    private int resolveYellowFruitItemId() {
        if (yellowFruitItemId > 0) return yellowFruitItemId;
        yellowFruitItemId = resolveItemId("yellow_fruit");
        return yellowFruitItemId;
    }

    private int resolveItemId(String itemName) {
        item itm = ItemDatabase.getItemByName(itemName);
        return (itm == null) ? -1 : itm.getItem_id();
    }

    private void handleTreeClicked(MouseEvent event) {
        if (event == null || event.getButton() != MouseButton.PRIMARY) return;
        if (mainTreeData == null || player == null) return;
        if (mainTreeData.currentStage != 1) return;
        if (mainTreeData.pendingDrops.isEmpty()) return;

        int itemId = mainTreeData.pendingDrops.peekFirst();
        if (itemId <= 0) {
            mainTreeData.pendingDrops.pollFirst();
            renderTreeVisual();
            return;
        }

        if (!player.getInventory().addItem(itemId, 1)) {
            AppDialogs.showMessageDialog(null, "Not enough inventory space to collect this drop.");
            return;
        }

        mainTreeData.pendingDrops.pollFirst();
        renderTreeVisual();
    }

    private void updateTreeState() {
        if (mainTreeData == null) return;

        GameTimeManager time = GameTimeManager.get();
        int currentDay = time.dayNumberProperty().get();
        double nowSeconds = time.totalSecondsProperty().get();

        if (mainTreeData.currentStage == 1) {
            if (mainTreeData.respawnAtTotalSeconds >= 0 && nowSeconds >= mainTreeData.respawnAtTotalSeconds) {
                // Respawn and erase any unclaimed drops.
                mainTreeData.pendingDrops.clear();
                mainTreeData.currentStage = 2;
                mainTreeData.livedDays = 0;
                mainTreeData.vit = vitFor(2, 0);
                mainTreeData.hp = maxHpFor(2, 0);
                mainTreeData.respawnAtTotalSeconds = -1;
                mainTreeData.lastUpdatedDayNumber = Math.max(1, currentDay);
            }
            renderTreeVisual();
            return;
        }

        applyTreeDailyGrowthIfNeeded(mainTreeData, currentDay);

        // Defensive: keep stats consistent even when no day change occurred.
        if (mainTreeData.currentStage != 1) {
            mainTreeData.currentStage = stageFromLivedDays(mainTreeData.livedDays);
            int maxHp = maxHpFor(mainTreeData.currentStage, mainTreeData.livedDays);
            mainTreeData.vit = vitFor(mainTreeData.currentStage, mainTreeData.livedDays);
            if (mainTreeData.hp > maxHp) mainTreeData.hp = maxHp;
            if (mainTreeData.hp < 0) mainTreeData.hp = 0;
            if (mainTreeData.hp == 0) {
                chopTree(mainTreeData);
            }
        }

        renderTreeVisual();
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

        int oldStage = stageFromLivedDays(tree.livedDays);
        int oldMaxHp = maxHpFor(oldStage, tree.livedDays);

        int delta = safeDay - last;
        tree.lastUpdatedDayNumber = safeDay;
        tree.livedDays = Math.max(0, tree.livedDays + delta);

        int newStage = stageFromLivedDays(tree.livedDays);
        int newMaxHp = maxHpFor(newStage, tree.livedDays);
        int growth = Math.max(0, newMaxHp - oldMaxHp);

        tree.currentStage = newStage;
        tree.vit = vitFor(newStage, tree.livedDays);

        int currentHp = Math.max(0, tree.hp);
        tree.hp = Math.min(newMaxHp, currentHp + growth);
    }

    private int stageFromLivedDays(int livedDays) {
        return Math.max(0, livedDays) >= TREE_STAGE3_AFTER_LIVED_DAYS ? 3 : 2;
    }

    private int maxHpFor(int stage, int livedDays) {
        int days = Math.max(0, livedDays);
        return switch (stage) {
            case 3 -> TREE_STAGE3_BASE_HP + (days * 2);
            default -> TREE_STAGE2_BASE_HP + days;
        };
    }

    private int vitFor(int stage, int livedDays) {
        int days = Math.max(0, livedDays);
        return switch (stage) {
            case 3 -> TREE_STAGE3_BASE_VIT + (days / 3);
            default -> TREE_STAGE2_BASE_VIT;
        };
    }

    private void renderTreeVisual() {
        if (tree_img == null || mainTreeData == null) return;

        String visualKey;
        if (mainTreeData.currentStage == 1) {
            int drop = mainTreeData.pendingDrops.peekFirst() == null ? -1 : mainTreeData.pendingDrops.peekFirst();
            visualKey = "stage1#drop:" + drop;
        } else {
            visualKey = "stage:" + mainTreeData.currentStage + "#type:" + (mainTreeData.type == null ? "null" : mainTreeData.type.name());
        }

        if (visualKey.equals(renderedTreeVisualKey)) {
            return;
        }
        renderedTreeVisualKey = visualKey;

        if (mainTreeData.currentStage == 1) {
            if (!mainTreeData.pendingDrops.isEmpty()) {
                int itemId = mainTreeData.pendingDrops.peekFirst();
                tree_img.setImage(ItemDatabase.getImage(itemId));
                tree_img.setVisible(true);
                tree_img.setManaged(true);
            } else {
                tree_img.setImage(null);
                tree_img.setVisible(false);
                tree_img.setManaged(false);
            }
            return;
        }

        tree_img.setManaged(true);
        tree_img.setVisible(true);

        String color = (mainTreeData.type == null) ? "blue" : mainTreeData.type.name().toLowerCase();
        String spriteName = color + "_tree" + (mainTreeData.currentStage == 3 ? "2" : "1");
        item spriteItem = ItemDatabase.getItemByName(spriteName);

        if (spriteItem != null) {
            tree_img.setImage(ItemDatabase.getImage(spriteItem.getItem_id()));
        } else {
            // Fallback to loading directly from resources if DB entry is missing.
            tree_img.setImage(loadImageOrNull("/image/items/" + spriteName + ".png"));
        }
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
        if (fishing != null) fishing.setText(appendPendingSkillCost(skills.getFishingDisplay(), pendingFishingSkillExp));
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
            configureImageButton(btnfishingplus1, canIncreaseSkill("fishing"), skillAllocationMode, 1.0);
            configureImageButton(btncookingplus1, canIncreaseSkill("cooking"), skillAllocationMode, 1.0);

            configureImageButton(btnfarmingminus1, skillAllocationMode && pendingFarmingSkillExp > 0, skillAllocationMode, 1.0);
            configureImageButton(btngatheringminus1, skillAllocationMode && pendingGatheringSkillExp > 0, skillAllocationMode, 1.0);
            configureImageButton(btncraftingminus1, skillAllocationMode && pendingCraftingSkillExp > 0, skillAllocationMode, 1.0);
            configureImageButton(btnfishingminus1, skillAllocationMode && pendingFishingSkillExp > 0, skillAllocationMode, 1.0);
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
            configureImageButton(btnfishingplus1, false, false, 1.0);
            configureImageButton(btncookingplus1, false, false, 1.0);
            configureImageButton(btnfarmingminus1, false, false, 1.0);
            configureImageButton(btngatheringminus1, false, false, 1.0);
            configureImageButton(btncraftingminus1, false, false, 1.0);
            configureImageButton(btnfishingminus1, false, false, 1.0);
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
       registerExit(exit_left, "/view/home_map.fxml", SpawnTag.FROM_LEFT);
       registerExit(exit_up, "/view/left_map_noone.fxml", SpawnTag.FROM_DOWN);
    }
}
