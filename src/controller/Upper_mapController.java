package controller;

import game.GameTimeManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import model.CollisionRegistry;
import model.CombatMath;
import model.ItemDatabase;
import model.MobEntity;
import model.MobMapState;
import model.item;
import util.AppDialogs;
import util.SpawnTag;

/**
 * FXML Controller class
 *
 * @author Kaung Zin
 */
public class Upper_mapController extends BaseMapController {

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
    private Rectangle exit_down;
    @FXML
    private Label hotbarguide;
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
    private Rectangle exit_right;
    @FXML
    private Rectangle spawn_from_right;
    @FXML
    private Rectangle spawn_from_down;
    @FXML
    private ImageView equip_img;

    @FXML
    private Rectangle human_hitbox_left;
    @FXML
    private Rectangle human_hitbox_right;
    @FXML
    private Rectangle human_hitbox_up;
    @FXML
    private Rectangle human_hitbox_down;

    // ---- Mobs: Slime ----
    @FXML
    private Group slime;
    @FXML
    private Rectangle slime_hitbox;
    @FXML
    private ImageView slime_img;
    @FXML
    private ImageView slime_drops_img;
    @FXML
    private Rectangle slime_hitbox_left;
    @FXML
    private Rectangle slime_hitbox_right;
    @FXML
    private Rectangle slime_hitbox_up;
    @FXML
    private Rectangle slime_hitbox_down;
    @FXML
    private Rectangle slime_detect;

    // ---- Mobs: Orc ----
    @FXML
    private Group orc;
    @FXML
    private Rectangle orc_hitbox;
    @FXML
    private ImageView orc_img;
    @FXML
    private ImageView orc_drops_img;
    @FXML
    private Rectangle orc_hitbox_left;
    @FXML
    private Rectangle orc_hitbox_right;
    @FXML
    private Rectangle orc_hitbox_up;
    @FXML
    private Rectangle orc_hitbox_down;
    @FXML
    private Rectangle orc_detect;

    private static final double MOB_DROP_TTL_SECONDS = 12.0 * 60.0 * 60.0;
    private static final double ORC_LOOT_COMMON_WEIGHT = 70.0;
    private static final double ORC_LOOT_UNCOMMON_WEIGHT = 25.0;
    private static final double ORC_LOOT_RARE_WEIGHT = 5.0;
    private static final int SLIME_BASE_MAX_HP = 10;
    private static final int SLIME_BASE_MAX_SP = 30;
    private static final int SLIME_BASE_STR = 3;
    private static final int SLIME_BASE_AGI = 2;
    private static final int SLIME_BASE_VIT = 6;
    private static final int SLIME_BASE_DEX = 1;
    private static final int ORC_BASE_MAX_HP = 20;
    private static final int ORC_BASE_MAX_SP = 25;
    private static final int ORC_BASE_STR = 4;
    private static final int ORC_BASE_AGI = 3;
    private static final int ORC_BASE_VIT = 3;
    private static final int ORC_BASE_DEX = 1;

    private MobEntity slimeMob;
    private MobEntity orcMob;

    private String renderedSlimeDropsKey = null;
    private String renderedOrcDropsKey = null;
    private int sandItemId = -1;
    private int stringyMeatItemId = -1;
    private int smallMeatChunkItemId = -1;
    private int steakItemId = -1;

    @Override
    protected void afterBaseInitialize() {
        setupCombatIfPresent();
        setupMobsIfPresent();
    }

    private void setupCombatIfPresent() {
        if (player != null) {
            player.setOnAttackStarted(this::handlePlayerAttackStarted);
        }

        // Player directional hitboxes are used for intersection only, not input.
        if (human_hitbox_left != null) human_hitbox_left.setMouseTransparent(true);
        if (human_hitbox_right != null) human_hitbox_right.setMouseTransparent(true);
        if (human_hitbox_up != null) human_hitbox_up.setMouseTransparent(true);
        if (human_hitbox_down != null) human_hitbox_down.setMouseTransparent(true);

        syncDirectionalHitboxes();
    }

    private void setupMobsIfPresent() {
        if (slime == null || slime_hitbox == null || slime_img == null) {
            return;
        }
        if (orc == null || orc_hitbox == null || orc_img == null) {
            return;
        }

        if (gameState == null) {
            gameState = game.GameState.get();
        }

        ItemDatabase.loadItems();

        GameTimeManager time = GameTimeManager.get();
        int currentDay = time.dayNumberProperty().get();
        double nowSeconds = time.totalSecondsProperty().get();

        MobMapState mapMobs = gameState.getMobState(mapKey());
        MobMapState.MobData slimeData = mapMobs.getMob("slime");
        MobMapState.MobData orcData = mapMobs.getMob("orc");

        initializeMobDefaultsIfNeeded(
            slimeData,
            "slime",
            slime,
            SLIME_BASE_MAX_HP,
            SLIME_BASE_MAX_SP,
            SLIME_BASE_STR,
            SLIME_BASE_AGI,
            SLIME_BASE_VIT,
            SLIME_BASE_DEX,
            currentDay,
            nowSeconds
        );
        initializeMobDefaultsIfNeeded(
            orcData,
            "orc",
            orc,
            ORC_BASE_MAX_HP,
            ORC_BASE_MAX_SP,
            ORC_BASE_STR,
            ORC_BASE_AGI,
            ORC_BASE_VIT,
            ORC_BASE_DEX,
            currentDay,
            nowSeconds
        );

        List<Rectangle> staticWalls = super.walls != null
            ? CollisionRegistry.collectWalls(super.walls)
            : List.of();

        slimeMob = new MobEntity(
            MobEntity.Kind.SLIME,
            slimeData,
            slime,
            slime_hitbox,
            slime_img,
            slime_detect,
            slime_hitbox_left,
            slime_hitbox_right,
            slime_hitbox_up,
            slime_hitbox_down,
            staticWalls,
            "/image/mob/Slime/Slime1_Idle_with_shadow.png",
            6,
            "/image/mob/Slime/Slime1_Walk_with_shadow.png",
            8,
            "/image/mob/Slime/Slime1_Attack_with_shadow.png",
            10,
            "/image/mob/Slime/Slime1_Hurt_with_shadow.png",
            5,
            "/image/mob/Slime/Slime1_Death_with_shadow.png",
            10
        );

        orcMob = new MobEntity(
            MobEntity.Kind.ORC,
            orcData,
            orc,
            orc_hitbox,
            orc_img,
            orc_detect,
            orc_hitbox_left,
            orc_hitbox_right,
            orc_hitbox_up,
            orc_hitbox_down,
            staticWalls,
            "/image/mob/Orc/orc1_idle_with_shadow.png",
            4,
            "/image/mob/Orc/orc1_walk_with_shadow.png",
            6,
            "/image/mob/Orc/orc1_attack_with_shadow.png",
            8,
            "/image/mob/Orc/orc1_hurt_with_shadow.png",
            6,
            "/image/mob/Orc/orc1_death_with_shadow.png",
            8
        );

        // Ensure mobs participate in player collision on this map.
        if (player != null) {
            List<Rectangle> colliders = new ArrayList<>(staticWalls);
            if (slime_hitbox != null) colliders.add(slime_hitbox);
            if (orc_hitbox != null) colliders.add(orc_hitbox);
            player.setWalls(colliders);
        }

        // Initial render of any persisted drops.
        renderSlimeDrops();
        renderOrcDrops();
    }

    private void initializeMobDefaultsIfNeeded(
        MobMapState.MobData mob,
        String mobType,
        Group mobNode,
        int maxHp,
        int maxSp,
        int str,
        int agi,
        int vit,
        int dex,
        int currentDay,
        double nowSeconds
    ) {
        if (mob == null || mobNode == null) return;

        boolean missingType = mob.mobType == null || mob.mobType.isBlank();
        boolean missingStats = mob.maxHp <= 0 || mob.maxSp <= 0;
        if (missingType || missingStats) {
            mob.mobType = mobType;
            mob.dead = false;
            mob.livedDays = 0;

            mob.maxHp = Math.max(1, maxHp);
            mob.hp = mob.maxHp;
            mob.maxSp = Math.max(1, maxSp);
            mob.sp = mob.maxSp;

            mob.str = Math.max(0, str);
            mob.agi = Math.max(0, agi);
            mob.vit = Math.max(0, vit);
            mob.dex = Math.max(0, dex);

            mob.layoutX = mobNode.getLayoutX();
            mob.layoutY = mobNode.getLayoutY();
            mob.lastRegenTotalSeconds = nowSeconds;
            mob.lastUpdatedDayNumber = Math.max(1, currentDay);
            mob.drops.clear();
            return;
        }

        // Apply saved position.
        mobNode.setLayoutX(mob.layoutX);
        mobNode.setLayoutY(mob.layoutY);

        // Clamp HP/SP.
        mob.maxHp = Math.max(1, mob.maxHp);
        mob.maxSp = Math.max(1, mob.maxSp);
        mob.hp = Math.max(0, Math.min(mob.maxHp, mob.hp));
        mob.sp = Math.max(0, Math.min(mob.maxSp, mob.sp));
    }

    @Override
    protected void onGameLoopUpdate(Bounds playerBounds) {
        syncDirectionalHitboxes();

        if (playerBounds == null || playerState == null) {
            return;
        }

        GameTimeManager time = GameTimeManager.get();
        int currentDay = time.dayNumberProperty().get();
        double nowSeconds = time.totalSecondsProperty().get();
        long nowNanos = getWorldSimNowNanos();

        if (slimeMob != null) {
            slimeMob.update(nowNanos, currentDay, nowSeconds, playerBounds, playerState);
        }
        if (orcMob != null) {
            orcMob.update(nowNanos, currentDay, nowSeconds, playerBounds, playerState);
        }

        renderSlimeDrops();
        renderOrcDrops();
    }

    private void syncDirectionalHitboxes() {
        if (player == null) return;
        model.Player.FacingDirection facing = player.getFacingDirection();

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
        long nowNanos = getWorldSimNowNanos();

        GameTimeManager time = GameTimeManager.get();
        double nowSeconds = time.totalSecondsProperty().get();

        if (slimeMob != null && slimeMob.getBodyHitbox() != null && !slimeMob.isDead()) {
            Bounds mobBounds = slimeMob.getBodyHitbox().localToScene(slimeMob.getBodyHitbox().getBoundsInLocal());
            if (attackBounds.intersects(mobBounds)) {
                int damage = calculatePlayerDamageAgainst(slimeMob);
                if (damage > 0) {
                    boolean killed = slimeMob.applyPlayerDamage(damage, nowNanos);
                    if (killed) {
                        handleMobKilledByPlayer(slimeMob, nowSeconds);
                        renderSlimeDrops();
                    }
                }
            }
        }

        if (orcMob != null && orcMob.getBodyHitbox() != null && !orcMob.isDead()) {
            Bounds mobBounds = orcMob.getBodyHitbox().localToScene(orcMob.getBodyHitbox().getBoundsInLocal());
            if (attackBounds.intersects(mobBounds)) {
                int damage = calculatePlayerDamageAgainst(orcMob);
                if (damage > 0) {
                    boolean killed = orcMob.applyPlayerDamage(damage, nowNanos);
                    if (killed) {
                        handleMobKilledByPlayer(orcMob, nowSeconds);
                        renderOrcDrops();
                    }
                }
            }
        }
    }

    private void handleMobKilledByPlayer(MobEntity mob, double nowSeconds) {
        if (mob == null || playerState == null) return;

        int expGain = computeExpGain(mob.getData());
        if (expGain > 0) {
            playerState.addExp(expGain);
        }

        int dropItemId = -1;
        if (mob.getKind() == MobEntity.Kind.ORC) {
            dropItemId = chooseOrcLootItemId();
        } else {
            dropItemId = resolveSandItemId();
        }

        if (dropItemId <= 0) {
            // Fallback to legacy sand drop if items are missing from the DB.
            dropItemId = resolveSandItemId();
        }

        if (dropItemId > 0) {
            mob.spawnDrop(dropItemId, 1, nowSeconds, MOB_DROP_TTL_SECONDS);
        }
    }

    private int computeExpGain(MobMapState.MobData mob) {
        if (mob == null || playerState == null) return 0;

        int mobTotal = Math.max(0, mob.str) + Math.max(0, mob.agi) + Math.max(0, mob.vit) + Math.max(0, mob.dex);
        int playerTotal = Math.max(0, playerState.getStr()) + Math.max(0, playerState.getAgi()) + Math.max(0, playerState.getVit()) + Math.max(0, playerState.getDex());

        long raw = (long) mobTotal - (long) playerTotal + 1L;
        return (int) Math.max(0L, raw);
    }

    private int calculatePlayerDamageAgainst(MobEntity mob) {
        if (playerState == null || mob == null || mob.getData() == null) return 0;

        double attackDamage = CombatMath.computeAttackDamage(playerState.getStr(), 0);
        double reduction = CombatMath.computeDamageReduction(mob.getData().vit);
        return CombatMath.computeNetDamage(attackDamage, reduction);
    }

    private int resolveSandItemId() {
        if (sandItemId > 0) return sandItemId;
        sandItemId = resolveItemId("sand");
        return sandItemId;
    }

    private int resolveStringyMeatItemId() {
        if (stringyMeatItemId > 0) return stringyMeatItemId;
        stringyMeatItemId = resolveItemId("stringy_meat");
        return stringyMeatItemId;
    }

    private int resolveSmallMeatChunkItemId() {
        if (smallMeatChunkItemId > 0) return smallMeatChunkItemId;
        smallMeatChunkItemId = resolveItemId("small_meat_chunk");
        return smallMeatChunkItemId;
    }

    private int resolveSteakItemId() {
        if (steakItemId > 0) return steakItemId;
        steakItemId = resolveItemId("Steak");
        return steakItemId;
    }

    private int resolveItemId(String itemName) {
        ItemDatabase.loadItems();
        item itm = ItemDatabase.getItemByName(itemName);
        return (itm == null) ? -1 : itm.getItem_id();
    }

    private int chooseOrcLootItemId() {
        int stringyId = resolveStringyMeatItemId();
        int chunkId = resolveSmallMeatChunkItemId();
        int steakId = resolveSteakItemId();

        int[] ids = { stringyId, chunkId, steakId };
        double[] weights = { ORC_LOOT_COMMON_WEIGHT, ORC_LOOT_UNCOMMON_WEIGHT, ORC_LOOT_RARE_WEIGHT };

        double total = 0.0;
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] > 0 && weights[i] > 0.0) {
                total += weights[i];
            }
        }
        if (total <= 0.0) {
            return -1;
        }

        double roll = ThreadLocalRandom.current().nextDouble(total);
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] <= 0 || weights[i] <= 0.0) continue;
            roll -= weights[i];
            if (roll < 0.0) return ids[i];
        }

        for (int i = ids.length - 1; i >= 0; i--) {
            if (ids[i] > 0) return ids[i];
        }
        return -1;
    }

    private void renderSlimeDrops() {
        if (slimeMob == null) return;
        if (slimeMob.isDead() && !slimeMob.isDeathAnimationFinished()) {
            hideMobDropView(slime_drops_img);
            renderedSlimeDropsKey = null;
            return;
        }
        renderedSlimeDropsKey = renderMobDrops(slimeMob.getData(), slime_drops_img, renderedSlimeDropsKey, this::renderSlimeDrops);
    }

    private void renderOrcDrops() {
        if (orcMob == null) return;
        if (orcMob.isDead() && !orcMob.isDeathAnimationFinished()) {
            hideMobDropView(orc_drops_img);
            renderedOrcDropsKey = null;
            return;
        }
        renderedOrcDropsKey = renderMobDrops(orcMob.getData(), orc_drops_img, renderedOrcDropsKey, this::renderOrcDrops);
    }

    private void hideMobDropView(ImageView dropView) {
        if (dropView == null) return;
        dropView.setImage(null);
        dropView.setVisible(false);
        dropView.setManaged(false);
        dropView.setOnMouseClicked(null);
    }

    private String renderMobDrops(MobMapState.MobData mob, ImageView dropView, String renderedKey, Runnable rerender) {
        if (dropView == null || mob == null) {
            return renderedKey;
        }

        String key = buildMobDropsKey(mob);
        if (key.equals(renderedKey)) {
            return renderedKey;
        }

        MobMapState.MobDrop first = null;
        for (MobMapState.MobDrop d : mob.drops) {
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

        final MobMapState.MobDrop displayedDrop = first;
        dropView.setImage(ItemDatabase.getImage(displayedDrop.itemId));
        boolean show = dropView.getImage() != null;
        dropView.setVisible(show);
        dropView.setManaged(show);
        dropView.setPickOnBounds(true);

        dropView.setOnMouseClicked(event -> {
            if (event.getButton() != MouseButton.PRIMARY) return;
            event.consume();
            handleMobDropClicked(mob, displayedDrop, rerender);
        });

        return key;
    }

    private String buildMobDropsKey(MobMapState.MobData mob) {
        if (mob == null || mob.drops.isEmpty()) {
            return "drops:none";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("drops:count=").append(mob.drops.size());
        for (MobMapState.MobDrop d : mob.drops) {
            if (d == null) continue;
            sb.append('#').append(d.itemId).append('x').append(d.quantity);
        }
        return sb.toString();
    }

    private void handleMobDropClicked(MobMapState.MobData mob, MobMapState.MobDrop drop, Runnable rerender) {
        if (drop == null || mob == null || player == null) return;
        if (drop.itemId <= 0 || drop.quantity <= 0) return;

        if (!player.getInventory().addItem(drop.itemId, drop.quantity)) {
            AppDialogs.showMessageDialog(null, "Not enough inventory space to collect this drop.");
            return;
        }

        mob.drops.remove(drop);
        if (rerender != null) {
            rerender.run();
        }
    }

    @Override
    protected String mapKey() {
        return "upper_map";
    }

    @Override
    protected void configureExits() {
        registerExit(exit_up, "/view/upper_map_notwo.fxml", SpawnTag.FROM_DOWN);
        registerExit(exit_down, "/view/home_map.fxml", SpawnTag.FROM_UP);
        registerExit(exit_right, "/view/right_map_noone.fxml", SpawnTag.FROM_LEFT);
    }
  
    
}
