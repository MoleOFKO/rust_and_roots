/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import model.CollisionRegistry;
import model.ItemDatabase;
import model.TreeMapState;
import util.AppDialogs;
import util.CombatRules;
import util.SkillModifiers;
import util.SpawnTag;

/**
 * FXML Controller class
 *
 * @author MITUSER-1
 */
public class D_homemapController extends BaseMapController {

    private static final String DAY_BG_PATH = "/image/dungeon/d_homemap.png";
    private static final String NIGHT_BG_PATH = "";

    @Override
    protected String dayBackgroundPath() {
        return DAY_BG_PATH;
    }

    @Override
    protected String nightBackgroundPath() {
        return NIGHT_BG_PATH;
    }

    private static final double RESOURCE_RESPAWN_SECONDS = 3.0 * 60.0 * 60.0;
    private static final double DROP_TTL_SECONDS = 12.0 * 60.0 * 60.0;
    private static final int STARTING_MAX_HP = 10;
    private static final int STARTING_VIT = 1;
    private static final int MAX_DROPS_PER_LIFE = 10;
    private static final double AUTO_GATHER_MARGIN_PIXELS = 2.0;

    private static final class OreNode {
        final String nodeId;
        final String dropItemKey;
        final ImageView sprite;
        final Rectangle hitbox;
        final ImageView dropView;

        TreeMapState.TreeData data;
        String renderedDropsKey = null;
        boolean warnedInventoryFull = false;
        int dropItemId = -1;

        OreNode(String nodeId, String dropItemKey, ImageView sprite, Rectangle hitbox, ImageView dropView) {
            this.nodeId = nodeId == null ? "" : nodeId;
            this.dropItemKey = dropItemKey == null ? "" : dropItemKey;
            this.sprite = sprite;
            this.hitbox = hitbox;
            this.dropView = dropView;
        }

        int resolveDropItemId() {
            if (dropItemId > 0) {
                return dropItemId;
            }
            if (dropItemKey.isBlank()) {
                dropItemId = -1;
                return dropItemId;
            }
            ItemDatabase.loadItems();
            model.item itm = ItemDatabase.getItemByName(dropItemKey);
            dropItemId = (itm == null) ? -1 : itm.getItem_id();
            return dropItemId;
        }
    }

    private final List<OreNode> oreNodes = new ArrayList<>();
    private boolean oresInitialized = false;

    @FXML
    private StackPane view;
    @FXML
    private Group world;
    @FXML
    private Rectangle exit_right;
    @FXML
    private Rectangle exit_down;
    @FXML
    private Label hotbarguide;
    @FXML
    private Rectangle spawn_default;
    @FXML
    private Rectangle spawn_from_right;
    @FXML
    private Rectangle spawn_from_down;
    @FXML
    private Group human;
    @FXML
    private Rectangle human_hitbox;
    @FXML
    private ImageView human_img;
    @FXML
    private ImageView equip_img;
    @FXML
    private Rectangle human_hitbox_left;
    @FXML
    private Rectangle human_hitbox_up;
    @FXML
    private Rectangle human_hitbox_down;
    @FXML
    private Rectangle human_hitbox_right;
    @FXML
    private HBox hotbar;
    @FXML
    private StackPane Hslot;
    @FXML
    private ImageView slotImg;
    @FXML
    private Label slotLb;
    @FXML
    private ToggleButton slotBtn;
    @FXML
    private ToggleGroup selectedhotbar;
    @FXML
    private StackPane Hslot1;
    @FXML
    private ImageView slotImg1;
    @FXML
    private Label slotLb1;
    @FXML
    private ToggleButton slotBtn1;
    @FXML
    private StackPane Hslot2;
    @FXML
    private ImageView slotImg2;
    @FXML
    private Label slotLb2;
    @FXML
    private ToggleButton slotBtn2;
    @FXML
    private StackPane Hslot3;
    @FXML
    private ImageView slotImg3;
    @FXML
    private Label slotLb3;
    @FXML
    private ToggleButton slotBtn3;
    @FXML
    private StackPane Hslot4;
    @FXML
    private ImageView slotImg4;
    @FXML
    private Label slotLb4;
    @FXML
    private ToggleButton slotBtn4;
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
    private Group stone_dungeon;
    @FXML
    private ImageView stone_dungeon_img;
    @FXML
    private Rectangle stone_dungeon_hitbox;
    @FXML
    private ImageView drops_img_stone_dungeon;
    @FXML
    private Group adamantite;
    @FXML
    private ImageView adamantite_img;
    @FXML
    private Rectangle adamantite_hitbox;
    @FXML
    private ImageView drops_img_adamantite;
    @FXML
    private Group silver;
    @FXML
    private ImageView silver_img;
    @FXML
    private Rectangle silver_hitbox;
    @FXML
    private ImageView drops_img_silver;
    @FXML
    private Group orichalcum;
    @FXML
    private ImageView orichalcum_img;
    @FXML
    private Rectangle orichalcum_hitbox;
    @FXML
    private ImageView drops_img_orichalcum;
    @FXML
    private Group iron;
    @FXML
    private ImageView iron_img;
    @FXML
    private Rectangle iron_hitbox;
    @FXML
    private ImageView drops_img_iron;
    @FXML
    private Group mithril;
    @FXML
    private ImageView mithril_img;
    @FXML
    private Rectangle mithril_hitbox;
    @FXML
    private ImageView drops_img_mithril;
    @FXML
    private Group copper;
    @FXML
    private ImageView copper_img;
    @FXML
    private Rectangle copper_hitbox;
    @FXML
    private ImageView drops_img_copper;
    @FXML
    private Group gold;
    @FXML
    private ImageView gold_img;
    @FXML
    private Rectangle gold_hitbox;
    @FXML
    private ImageView drops_img_gold;
    @FXML
    private Group hihiogane;
    @FXML
    private ImageView hihiogane_img;
    @FXML
    private Rectangle hihiogane_hitbox;
    @FXML
    private ImageView drops_img_hihiogane;

    @Override
    protected void afterBaseInitialize() {
        initializeOreNodesIfNeeded();
    }

    @Override
    protected void onGameLoopUpdate(Bounds playerBounds) {
        initializeOreNodesIfNeeded();
        syncDirectionalHitboxes();
        updateOreNodes();
        autoGatherOreDrops(playerBounds);
    }

    private void initializeOreNodesIfNeeded() {
        if (oresInitialized) {
            return;
        }

        if (gameState == null) {
            return;
        }

        if (player == null) {
            return;
        }

        ItemDatabase.loadItems();

        oreNodes.clear();
        oreNodes.add(new OreNode("stone_dungeon", "stone_dungeon_drop_item", stone_dungeon_img, stone_dungeon_hitbox, drops_img_stone_dungeon));
        oreNodes.add(new OreNode("iron", "iron_drop_item", iron_img, iron_hitbox, drops_img_iron));
        oreNodes.add(new OreNode("copper", "copper_drop_item", copper_img, copper_hitbox, drops_img_copper));
        oreNodes.add(new OreNode("silver", "silver_drop_item", silver_img, silver_hitbox, drops_img_silver));
        oreNodes.add(new OreNode("gold", "gold_drop_item", gold_img, gold_hitbox, drops_img_gold));
        oreNodes.add(new OreNode("adamantite", "adamantite_drop_item", adamantite_img, adamantite_hitbox, drops_img_adamantite));
        oreNodes.add(new OreNode("orichalcum", "orichalcum_drop_item", orichalcum_img, orichalcum_hitbox, drops_img_orichalcum));
        oreNodes.add(new OreNode("mithril", "mithril_drop_item", mithril_img, mithril_hitbox, drops_img_mithril));
        oreNodes.add(new OreNode("hihiogane", "hihiogane_drop_item", hihiogane_img, hihiogane_hitbox, drops_img_hihiogane));

        TreeMapState treeState = gameState.getTreeState(mapKey());
        int currentDay = game.GameTimeManager.get().dayNumberProperty().get();
        double nowSeconds = game.GameTimeManager.get().totalSecondsProperty().get();

        for (OreNode node : oreNodes) {
            if (node == null || node.nodeId.isBlank()) {
                continue;
            }
            node.data = treeState.getTree(node.nodeId);
            initializeOreDefaults(node.data, currentDay, nowSeconds);

            if (node.hitbox != null) {
                node.hitbox.setMouseTransparent(true);
            }
        }

	        registerPlayerAttackHandler("ores", this::handlePlayerAttackStarted);
	        refreshPlayerCollisionWalls();

        for (OreNode node : oreNodes) {
            renderOreVisual(node);
            renderOreDrops(node);
        }

        oresInitialized = true;
    }

	    private void refreshPlayerCollisionWalls() {
	        List<Rectangle> colliders = new ArrayList<>();
	        for (OreNode node : oreNodes) {
	            if (node != null && node.hitbox != null) {
	                colliders.add(node.hitbox);
	            }
	        }
	        registerPlayerCollisionRectangles(colliders);
	        rebuildPlayerCollisionWalls();
	    }

    private void initializeOreDefaults(TreeMapState.TreeData data, int currentDay, double nowSeconds) {
        if (data == null) {
            return;
        }

        if (data.typeKey == null || data.typeKey.isBlank()) {
            data.typeKey = "ore";
        }

        if (data.lastUpdatedDayNumber <= 0) {
            data.lastUpdatedDayNumber = Math.max(1, currentDay);
        }

        if (data.currentStage < 1 || data.currentStage > 3) {
            data.currentStage = 2;
        }

        if (data.currentStage == 1) {
            data.hp = 0;
            data.maxHp = 0;
            data.vit = 0;
            if (data.respawnAtTotalSeconds < 0) {
                data.respawnAtTotalSeconds = nowSeconds + RESOURCE_RESPAWN_SECONDS;
            }
            return;
        }

        if (data.maxHp <= 0) {
            data.maxHp = STARTING_MAX_HP;
        }
        if (data.hp <= 0) {
            data.hp = data.maxHp;
        }
        if (data.vit <= 0) {
            data.vit = STARTING_VIT;
        }

        if (data.hp > data.maxHp) {
            data.hp = data.maxHp;
        }
        if (data.hp < 0) {
            data.hp = 0;
        }

        if (data.dropsThisLife < 0) {
            data.dropsThisLife = 0;
        }
        if (data.dropRatePity < 0) {
            data.dropRatePity = 0;
        }

        data.respawnAtTotalSeconds = -1;
    }

    private void updateOreNodes() {
        if (gameState == null || oreNodes.isEmpty()) {
            return;
        }

        int currentDay = game.GameTimeManager.get().dayNumberProperty().get();
        double nowSeconds = game.GameTimeManager.get().totalSecondsProperty().get();

        for (OreNode node : oreNodes) {
            if (node == null || node.data == null) {
                continue;
            }

            cleanupExpiredDrops(node.data, nowSeconds);

            if (node.data.currentStage == 1) {
                if (node.data.respawnAtTotalSeconds >= 0 && nowSeconds >= node.data.respawnAtTotalSeconds) {
                    respawnOre(node, currentDay, nowSeconds);
                }
            }

            renderOreVisual(node);
            renderOreDrops(node);
        }
    }

    private void respawnOre(OreNode node, int currentDay, double nowSeconds) {
        if (node == null || node.data == null) {
            return;
        }

        node.data.currentStage = 2;
        node.data.hp = STARTING_MAX_HP;
        node.data.maxHp = STARTING_MAX_HP;
        node.data.vit = STARTING_VIT;
        node.data.respawnAtTotalSeconds = -1;
        node.data.lastUpdatedDayNumber = Math.max(1, currentDay);

        node.data.dropRatePity = 0;
        node.data.dropsThisLife = 0;
        node.data.damageSinceLastDropRoll = 0;

        if (node.hitbox != null) {
            node.hitbox.setDisable(false);
        }
    }

    private void cleanupExpiredDrops(TreeMapState.TreeData resource, double nowSeconds) {
        if (resource == null || resource.drops == null || resource.drops.isEmpty()) {
            return;
        }

        resource.drops.removeIf(drop -> drop == null
            || drop.itemId <= 0
            || drop.quantity <= 0
            || (drop.expiresAtTotalSeconds > 0 && nowSeconds >= drop.expiresAtTotalSeconds)
        );
    }

    private void renderOreVisual(OreNode node) {
        if (node == null || node.data == null) {
            return;
        }

        boolean alive = node.data.currentStage != 1;

        if (node.sprite != null) {
            node.sprite.setVisible(alive);
            node.sprite.setManaged(alive);
        }

        if (node.hitbox != null) {
            node.hitbox.setDisable(!alive);
        }
    }

    private String buildDropsKey(TreeMapState.TreeData data) {
        if (data == null || data.drops == null || data.drops.isEmpty()) {
            return "drops:none";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("drops:").append(data.drops.size());
        for (TreeMapState.TreeDrop d : data.drops) {
            if (d == null) {
                continue;
            }
            sb.append('#').append(d.itemId).append('x').append(d.quantity);
        }
        return sb.toString();
    }

    private void renderOreDrops(OreNode node) {
        if (node == null || node.dropView == null || node.data == null) {
            return;
        }

        String key = buildDropsKey(node.data);
        if (key.equals(node.renderedDropsKey)) {
            return;
        }
        node.renderedDropsKey = key;

        TreeMapState.TreeDrop first = null;
        for (TreeMapState.TreeDrop d : node.data.drops) {
            if (d == null || d.itemId <= 0 || d.quantity <= 0) {
                continue;
            }
            if (ItemDatabase.getImage(d.itemId) == null) {
                continue;
            }
            first = d;
            break;
        }

        if (first == null) {
            node.dropView.setImage(null);
            node.dropView.setVisible(false);
            node.dropView.setManaged(false);
            node.dropView.setOnMouseClicked(null);
            return;
        }

        node.dropView.setImage(ItemDatabase.getImage(first.itemId));
        boolean show = node.dropView.getImage() != null;
        node.dropView.setVisible(show);
        node.dropView.setManaged(show);
        node.dropView.setPickOnBounds(true);
        node.dropView.setMouseTransparent(true);
        node.dropView.setOnMouseClicked(null);
    }

    private void syncDirectionalHitboxes() {
        if (player == null) {
            return;
        }

        model.Player.FacingDirection facing = player.getFacingDirection();
        setDirectionalHitboxActive(human_hitbox_left, facing == model.Player.FacingDirection.LEFT);
        setDirectionalHitboxActive(human_hitbox_right, facing == model.Player.FacingDirection.RIGHT);
        setDirectionalHitboxActive(human_hitbox_up, facing == model.Player.FacingDirection.UP);
        setDirectionalHitboxActive(human_hitbox_down, facing == model.Player.FacingDirection.DOWN);
    }

    private void setDirectionalHitboxActive(Rectangle hitbox, boolean active) {
        if (hitbox == null) {
            return;
        }
        hitbox.setDisable(!active);
        hitbox.setVisible(active);
        hitbox.setManaged(active);
    }

    private Rectangle resolveActiveAttackHitbox() {
        if (player == null) {
            return null;
        }

        return switch (player.getFacingDirection()) {
            case LEFT -> human_hitbox_left;
            case RIGHT -> human_hitbox_right;
            case UP -> human_hitbox_up;
            case DOWN -> human_hitbox_down;
        };
    }

    private void handlePlayerAttackStarted() {
        Rectangle attackHitbox = resolveActiveAttackHitbox();
        if (attackHitbox == null) {
            return;
        }

        Bounds attackBounds = attackHitbox.localToScene(attackHitbox.getBoundsInLocal());
        int currentDay = game.GameTimeManager.get().dayNumberProperty().get();
        double nowSeconds = game.GameTimeManager.get().totalSecondsProperty().get();

        int damage = calculatePlayerOreDamage();
        if (damage <= 0) {
            return;
        }

        for (OreNode node : oreNodes) {
            if (node == null || node.data == null || node.hitbox == null) {
                continue;
            }
            if (node.data.currentStage == 1) {
                continue;
            }

            Bounds nodeBounds = node.hitbox.localToScene(node.hitbox.getBoundsInLocal());
            if (!attackBounds.intersects(nodeBounds)) {
                continue;
            }

            applyPlayerAttackToOre(node, currentDay, nowSeconds, damage);
            renderOreVisual(node);
            renderOreDrops(node);
            break;
        }
    }

    private int calculatePlayerOreDamage() {
        if (playerState == null || player == null) {
            return 0;
        }

        int playerStr = Math.max(0, playerState.getStr());
        int bonus = isPickaxeEquipped() ? 1 : 0;
        return CombatRules.rollAttackDamage(playerStr, bonus);
    }

    private boolean isPickaxeEquipped() {
        if (player == null) {
            return false;
        }
        int equippedId = player.getEquippedItemId();
        if (equippedId <= 0) {
            return false;
        }
        String name = ItemDatabase.getItemName(equippedId);
        if (name == null || name.isBlank()) {
            return false;
        }

        String normalized = name.trim().toLowerCase(Locale.ROOT);
        if (normalized.equals("pickaxe") || normalized.equals("pick axe")) {
            return true;
        }
        return normalized.contains("pickaxe") || normalized.contains("pick axe");
    }

    private void applyPlayerAttackToOre(OreNode node, int currentDay, double nowSeconds, int baseDamage) {
        if (node == null || node.data == null) {
            return;
        }

        int hpBefore = Math.max(0, node.data.hp);
        int vit = Math.max(0, node.data.vit);
        int dealt = CombatRules.applyDamageReduction(Math.max(0, baseDamage), vit);
        if (dealt <= 0) {
            return;
        }

        int hpAfter = Math.max(0, hpBefore - dealt);
        node.data.hp = hpAfter;

        spawnCommonOreDrops(node, nowSeconds);

        if (hpAfter <= 0) {
            killOre(node, currentDay, nowSeconds);
        }
    }

    private void spawnCommonOreDrops(OreNode node, double nowSeconds) {
        if (node == null || node.data == null) {
            return;
        }

        int dropItemId = node.resolveDropItemId();
        if (dropItemId <= 0) {
            return;
        }

        if (node.data.dropsThisLife < 0) {
            node.data.dropsThisLife = 0;
        }
        if (node.data.dropsThisLife >= MAX_DROPS_PER_LIFE) {
            return;
        }

        node.data.drops.add(new TreeMapState.TreeDrop(dropItemId, 1, nowSeconds, nowSeconds + DROP_TTL_SECONDS));
        node.data.dropsThisLife += 1;

        int gatheringLevel = gameState == null || gameState.getPlayerSkills() == null
            ? 0
            : Math.max(0, gameState.getPlayerSkills().getGatheringLevel());
        int bonusPercent = SkillModifiers.gatheringCommonBonusPercent(gatheringLevel);
        int chance = Math.max(0, Math.min(100, bonusPercent));
        if (chance > 0 && node.data.dropsThisLife < MAX_DROPS_PER_LIFE) {
            if (ThreadLocalRandom.current().nextDouble(100.0) < chance) {
                node.data.drops.add(new TreeMapState.TreeDrop(dropItemId, 1, nowSeconds, nowSeconds + DROP_TTL_SECONDS));
                node.data.dropsThisLife += 1;
            }
        }
    }

    private void killOre(OreNode node, int currentDay, double nowSeconds) {
        if (node == null || node.data == null) {
            return;
        }

        node.data.currentStage = 1;
        node.data.hp = 0;
        node.data.maxHp = 0;
        node.data.vit = 0;
        node.data.respawnAtTotalSeconds = nowSeconds + RESOURCE_RESPAWN_SECONDS;
        node.data.livedDays = 0;
        node.data.lastUpdatedDayNumber = Math.max(1, currentDay);

        node.data.dropRatePity = 0;
        node.data.damageSinceLastDropRoll = 0;

        if (node.hitbox != null) {
            node.hitbox.setDisable(true);
        }
    }

    private void autoGatherOreDrops(Bounds playerBoundsScene) {
        if (player == null || playerBoundsScene == null) {
            return;
        }

        for (OreNode node : oreNodes) {
            if (node == null || node.data == null || node.hitbox == null) {
                continue;
            }
            node.warnedInventoryFull = autoGatherResourceDrops(node.data, node.hitbox, playerBoundsScene, node.warnedInventoryFull);
            renderOreDrops(node);
        }
    }

    private boolean autoGatherResourceDrops(
        TreeMapState.TreeData resource,
        Rectangle resourceHitbox,
        Bounds playerBoundsScene,
        boolean warnedInventoryFull
    ) {
        if (resource == null || resourceHitbox == null || player == null || playerBoundsScene == null) {
            return warnedInventoryFull;
        }

        boolean inside = intersectsWithMargin(playerBoundsScene, resourceHitbox, AUTO_GATHER_MARGIN_PIXELS);
        if (!inside) {
            return warnedInventoryFull;
        }

        if (resource.drops == null || resource.drops.isEmpty()) {
            return warnedInventoryFull;
        }

        Iterator<TreeMapState.TreeDrop> it = resource.drops.iterator();
        while (it.hasNext()) {
            TreeMapState.TreeDrop drop = it.next();
            if (drop == null || drop.itemId <= 0 || drop.quantity <= 0) {
                it.remove();
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
        }

        return warnedInventoryFull;
    }

    private boolean intersectsWithMargin(Bounds playerBoundsScene, Rectangle targetHitbox, double marginPixels) {
        if (playerBoundsScene == null || targetHitbox == null) {
            return false;
        }

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
    
    @Override
    protected String mapKey() {
        return "d_homemap";
    }

    @Override
    protected void configureExits() {
        registerExit(exit_down, "/view/d_undermap.fxml", SpawnTag.FROM_UP);
        registerExit(exit_right, "/view/left_map_notwo_of_two.fxml", SpawnTag.FROM_LEFT);
    }
}
