package controller;

import game.GameTimeManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
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
import model.Farm;
import model.ItemDatabase;
import model.MobEntity;
import model.MobMapState;
import model.item;
import util.AppDialogs;
import util.CombatRules;
import util.SkillModifiers;
import util.SpawnTag;

public class RightMapController extends BaseMapController {

    private static final String DAY_BG_PATH = "/image/maps/rightmap.png";
    private static final String NIGHT_BG_PATH = "/image/NightMap/rightmapnight.png";
    @FXML
    private Rectangle spawn_poison_slime;
    @FXML
    private Rectangle spawn_lava_slime;
    @FXML
    private Rectangle spawn_orcb;
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

    @Override
    protected String dayBackgroundPath() {
        return DAY_BG_PATH;
    }

    @Override
    protected String nightBackgroundPath() {
        return NIGHT_BG_PATH;
    }

    @FXML
    private StackPane view;
    @FXML
    private Group world;
    @FXML
    private ImageView bg;
    @FXML
    private Group walls;
    @FXML
    private Rectangle wall1;
    @FXML
    private Rectangle wall2;
    @FXML
    private Rectangle wall3;
    @FXML
    private Rectangle wall4;
    @FXML
    private Group human;
    @FXML
    private ImageView human_img;
    @FXML
    private Rectangle human_hitbox;
    @FXML
    private Rectangle spawn_default;
    @FXML
    private Rectangle spawn_from_left;
    @FXML
    private Rectangle spawn_from_up;
    @FXML
    private Rectangle exit_left;
    @FXML
    private Rectangle exit_up;
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
    private ImageView equip_img;

    // ---- Mobs: Poison Slime (Slime2) ----
    @FXML
    private Group poison_slime;
    @FXML
    private Rectangle poison_slime_hitbox;
    @FXML
    private ImageView poison_slime_img;
    @FXML
    private ImageView poison_slime_drops_img;
    @FXML
    private Rectangle poison_slime_hitbox_left;
    @FXML
    private Rectangle poison_slime_hitbox_right;
    @FXML
    private Rectangle poison_slime_hitbox_up;
    @FXML
    private Rectangle poison_slime_hitbox_down;
    @FXML
    private Rectangle poison_slime_detect;

    // ---- Mobs: Lava Slime (Slime3) ----
    @FXML
    private Group lava_slime;
    @FXML
    private Rectangle lava_slime_hitbox;
    @FXML
    private ImageView lava_slime_img;
    @FXML
    private ImageView lava_slime_drops_img;
    @FXML
    private Rectangle lava_slime_hitbox_left;
    @FXML
    private Rectangle lava_slime_hitbox_right;
    @FXML
    private Rectangle lava_slime_hitbox_up;
    @FXML
    private Rectangle lava_slime_hitbox_down;
    @FXML
    private Rectangle lava_slime_detect;

    // ---- Mobs: Orc B (Orc3) ----
    @FXML
    private Group orcb;
    @FXML
    private Rectangle orcb_hitbox;
    @FXML
    private ImageView orcb_img;
    @FXML
    private ImageView orcb_drops_img;
    @FXML
    private Rectangle orcb_hitbox_left;
    @FXML
    private Rectangle orcb_hitbox_right;
    @FXML
    private Rectangle orcb_hitbox_up;
    @FXML
    private Rectangle orcb_hitbox_down;
    @FXML
    private Rectangle orcb_detect;

    private static final double MOB_DROP_TTL_SECONDS = 12.0 * 60.0 * 60.0;
    private static final double AUTO_GATHER_MARGIN_PIXELS = 2.0;
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

    private MobEntity poisonSlimeMob;
    private MobEntity lavaSlimeMob;
    private MobEntity orcbMob;

    private double poisonSlimeSpawnX;
    private double poisonSlimeSpawnY;
    private double lavaSlimeSpawnX;
    private double lavaSlimeSpawnY;
    private double orcbSpawnX;
    private double orcbSpawnY;

    private String renderedPoisonSlimeDropsKey = null;
    private String renderedLavaSlimeDropsKey = null;
    private String renderedOrcbDropsKey = null;
    private boolean warnedPoisonInventoryFull = false;
    private boolean warnedLavaInventoryFull = false;
    private boolean warnedOrcbInventoryFull = false;
    private int sandItemId = -1;
    private int stringyMeatItemId = -1;
    private int smallMeatChunkItemId = -1;
    private int steakItemId = -1;

	    @Override
	    protected void afterBaseInitialize() {
	        // Mobs/combat are handled by the shared FXML-driven MapMobSystem in BaseMapController.
	    }

    private void setupCombatIfPresent() {
        if (player != null) {
            player.setOnAttackStarted(this::handlePlayerAttackStarted);
        }
    }

    private void setupMobsIfPresent() {
        if (poison_slime == null && lava_slime == null && orcb == null) {
            return;
        }

        if (gameState == null) {
            gameState = game.GameState.get();
        }
        if (gameState == null) {
            return;
        }

        ItemDatabase.loadItems();

        GameTimeManager time = GameTimeManager.get();
        int currentDay = time.dayNumberProperty().get();
        double nowSeconds = time.totalSecondsProperty().get();

        MobMapState mapMobs = gameState.getMobState(mapKey());
        List<Rectangle> staticWalls = (walls != null) ? CollisionRegistry.collectWalls(walls) : List.of();

        if (poison_slime != null && poison_slime_hitbox != null && poison_slime_img != null) {
            poisonSlimeSpawnX = poison_slime.getLayoutX();
            poisonSlimeSpawnY = poison_slime.getLayoutY();

            MobMapState.MobData poisonData = mapMobs.getMob("poison_slime");
            initializeMobDefaultsIfNeeded(
                poisonData,
                "poison_slime",
                poison_slime,
                SLIME_BASE_MAX_HP * 2,
                SLIME_BASE_MAX_SP * 2,
                SLIME_BASE_STR * 2,
                SLIME_BASE_AGI * 2,
                SLIME_BASE_VIT * 2,
                SLIME_BASE_DEX * 2,
                currentDay,
                nowSeconds
            );

            poisonSlimeMob = new MobEntity(
                MobEntity.Kind.SLIME,
                poisonData,
                poison_slime,
                poison_slime_hitbox,
                poison_slime_img,
                poison_slime_detect,
                poison_slime_hitbox_left,
                poison_slime_hitbox_right,
                poison_slime_hitbox_up,
                poison_slime_hitbox_down,
                staticWalls,
                "/image/mob/Slime/Slime2_Idle_with_shadow.png",
                4,
                "/image/mob/Slime/Slime2_Walk_with_shadow.png",
                6,
                "/image/mob/Slime/Slime2_Run_with_shadow.png",
                8,
                "/image/mob/Slime/Slime2_Attack_with_shadow.png",
                10,
                "/image/mob/Slime/Slime2_Hurt_with_shadow.png",
                5,
                "/image/mob/Slime/Slime2_Death_with_shadow.png",
                10
            );
        }

        if (lava_slime != null && lava_slime_hitbox != null && lava_slime_img != null) {
            lavaSlimeSpawnX = lava_slime.getLayoutX();
            lavaSlimeSpawnY = lava_slime.getLayoutY();

            MobMapState.MobData lavaData = mapMobs.getMob("lava_slime");
            initializeMobDefaultsIfNeeded(
                lavaData,
                "lava_slime",
                lava_slime,
                SLIME_BASE_MAX_HP * 2,
                SLIME_BASE_MAX_SP * 2,
                SLIME_BASE_STR * 2,
                SLIME_BASE_AGI * 2,
                SLIME_BASE_VIT * 2,
                SLIME_BASE_DEX * 2,
                currentDay,
                nowSeconds
            );

            lavaSlimeMob = new MobEntity(
                MobEntity.Kind.SLIME,
                lavaData,
                lava_slime,
                lava_slime_hitbox,
                lava_slime_img,
                lava_slime_detect,
                lava_slime_hitbox_left,
                lava_slime_hitbox_right,
                lava_slime_hitbox_up,
                lava_slime_hitbox_down,
                staticWalls,
                "/image/mob/Slime/Slime3_Idle_with_shadow.png",
                4,
                "/image/mob/Slime/Slime3_Walk_with_shadow.png",
                6,
                "/image/mob/Slime/Slime3_Run_with_shadow.png",
                8,
                "/image/mob/Slime/Slime3_Attack_with_shadow.png",
                10,
                "/image/mob/Slime/Slime3_Hurt_with_shadow.png",
                5,
                "/image/mob/Slime/Slime3_Death_with_shadow.png",
                10
            );
        }

        if (orcb != null && orcb_hitbox != null && orcb_img != null) {
            orcbSpawnX = orcb.getLayoutX();
            orcbSpawnY = orcb.getLayoutY();

            MobMapState.MobData orcbData = mapMobs.getMob("orcb");
            initializeMobDefaultsIfNeeded(
                orcbData,
                "orcb",
                orcb,
                ORC_BASE_MAX_HP * 3,
                ORC_BASE_MAX_SP * 3,
                ORC_BASE_STR * 3,
                ORC_BASE_AGI * 3,
                ORC_BASE_VIT * 3,
                ORC_BASE_DEX * 3,
                currentDay,
                nowSeconds
            );

            orcbMob = new MobEntity(
                MobEntity.Kind.ORC,
                orcbData,
                orcb,
                orcb_hitbox,
                orcb_img,
                orcb_detect,
                orcb_hitbox_left,
                orcb_hitbox_right,
                orcb_hitbox_up,
                orcb_hitbox_down,
                staticWalls,
                "/image/mob/Orc/orc3_idle_with_shadow.png",
                4,
                "/image/mob/Orc/orc3_walk_with_shadow.png",
                6,
                "/image/mob/Orc/orc3_run_with_shadow.png",
                8,
                "/image/mob/Orc/orc3_attack_with_shadow.png",
                8,
                "/image/mob/Orc/orc3_hurt_with_shadow.png",
                6,
                "/image/mob/Orc/orc3_death_with_shadow.png",
                8
            );
        }

        // Ensure mobs participate in player collision on this map.
        if (player != null) {
            List<Rectangle> colliders = new ArrayList<>(staticWalls);
            if (poison_slime_hitbox != null) colliders.add(poison_slime_hitbox);
            if (lava_slime_hitbox != null) colliders.add(lava_slime_hitbox);
            if (orcb_hitbox != null) colliders.add(orcb_hitbox);
            player.setWalls(colliders);
        }

        renderPoisonSlimeDrops();
        renderLavaSlimeDrops();
        renderOrcbDrops();
    }

    private boolean initializeMobDefaultsIfNeeded(
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
        if (mob == null || mobNode == null) return false;

        boolean missingType = mob.mobType == null || mob.mobType.isBlank();
        boolean missingStats = mob.maxHp <= 0 || mob.maxSp <= 0;
        if (missingType || missingStats) {
            mob.mobType = mobType;
            mob.dead = false;
            mob.respawnAtTotalSeconds = -1.0;
            mob.runAttackCooldownUntilTotalSeconds = -1.0;
            mob.livedDays = 0;

            mob.maxHp = Math.max(1, maxHp);
            mob.hp = mob.maxHp;
            mob.maxSp = Math.max(1, maxSp);
            mob.sp = mob.maxSp;

            mob.str = Math.max(0, str);
            mob.agi = Math.max(0, agi);
            mob.vit = Math.max(0, vit);
            mob.dex = Math.max(0, dex);

            int safeDay = Math.max(1, currentDay);
            int worldRolls = Math.max(0, safeDay - 1);
            for (int i = 0; i < worldRolls; i++) {
                applySingleMobDailyGrowthRoll(mob);
            }
            mob.hp = mob.maxHp;
            mob.sp = mob.maxSp;

            mob.layoutX = mobNode.getLayoutX();
            mob.layoutY = mobNode.getLayoutY();
            mob.lastRegenTotalSeconds = nowSeconds;
            mob.lastUpdatedDayNumber = safeDay;
            mob.drops.clear();
            return true;
        }

        // Apply saved position.
        mobNode.setLayoutX(mob.layoutX);
        mobNode.setLayoutY(mob.layoutY);

        // Clamp HP/SP.
        mob.maxHp = Math.max(1, mob.maxHp);
        mob.maxSp = Math.max(1, mob.maxSp);
        mob.hp = Math.max(0, Math.min(mob.maxHp, mob.hp));
        mob.sp = Math.max(0, Math.min(mob.maxSp, mob.sp));

        if (Double.isNaN(mob.runAttackCooldownUntilTotalSeconds) || Double.isInfinite(mob.runAttackCooldownUntilTotalSeconds)) {
            mob.runAttackCooldownUntilTotalSeconds = -1.0;
        }
        if (mob.runAttackCooldownUntilTotalSeconds < -1.0) {
            mob.runAttackCooldownUntilTotalSeconds = -1.0;
        }

        return false;
    }

    private void applySingleMobDailyGrowthRoll(MobMapState.MobData mob) {
        if (mob == null) return;

        String type = mob.mobType == null ? "" : mob.mobType.trim().toLowerCase(Locale.ROOT);
        int roll = ThreadLocalRandom.current().nextInt(100);

        if (type.contains("orc")) {
            // Spec (orc): STR 35%, AGI 5%, MAX HP 30%, VIT 30%.
            if (roll < 35) {
                mob.str = Math.max(0, mob.str) + 1;
                return;
            }
            if (roll < 40) {
                mob.agi = Math.max(0, mob.agi) + 1;
                return;
            }
            if (roll < 70) {
                mob.maxHp = Math.max(1, mob.maxHp) + 1;
                mob.hp = Math.min(mob.maxHp, Math.max(0, mob.hp) + 1);
                return;
            }

            mob.vit = Math.max(0, mob.vit) + 1;
            return;
        }

        // Default (slime): STR 10%, AGI 20%, MAX SP 30%, VIT 30%, MAX HP 10%.
        if (roll < 10) {
            mob.str = Math.max(0, mob.str) + 1;
            return;
        }
        if (roll < 30) {
            mob.agi = Math.max(0, mob.agi) + 1;
            return;
        }
        if (roll < 60) {
            mob.maxSp = Math.max(1, mob.maxSp) + 1;
            mob.sp = Math.min(mob.maxSp, Math.max(0, mob.sp) + 1);
            return;
        }
        if (roll < 90) {
            mob.vit = Math.max(0, mob.vit) + 1;
            return;
        }

        mob.maxHp = Math.max(1, mob.maxHp) + 1;
        mob.hp = Math.min(mob.maxHp, Math.max(0, mob.hp) + 1);
    }

	    @Override
	    protected void onGameLoopUpdate(Bounds playerBounds) {
	        // Map-specific tick work (if any) goes here. Mob updates are handled by BaseMapController.
	    }

    private void updateSingleMob(
        MobEntity mob,
        Group mobNode,
        int currentDay,
        double nowSeconds,
        long nowNanos,
        Bounds playerBounds,
        double spawnX,
        double spawnY
    ) {
        if (mob == null || mobNode == null) {
            return;
        }

        boolean wasDead = mob.isDead();
        mob.update(nowNanos, currentDay, nowSeconds, playerBounds, playerState);
        if (wasDead && !mob.isDead()) {
            MobMapState.MobData data = mob.getData();
            mobNode.setLayoutX(spawnX);
            mobNode.setLayoutY(spawnY);
            if (data != null) {
                data.layoutX = spawnX;
                data.layoutY = spawnY;
            }
        }
    }

    private void autoGatherMobDrops(Bounds playerBoundsScene) {
        if (player == null || playerBoundsScene == null) return;

        warnedPoisonInventoryFull = autoGatherSingleMobDrops(poisonSlimeMob, poison_slime_hitbox, playerBoundsScene, warnedPoisonInventoryFull, this::renderPoisonSlimeDrops);
        warnedLavaInventoryFull = autoGatherSingleMobDrops(lavaSlimeMob, lava_slime_hitbox, playerBoundsScene, warnedLavaInventoryFull, this::renderLavaSlimeDrops);
        warnedOrcbInventoryFull = autoGatherSingleMobDrops(orcbMob, orcb_hitbox, playerBoundsScene, warnedOrcbInventoryFull, this::renderOrcbDrops);
    }

    private boolean autoGatherSingleMobDrops(
        MobEntity mob,
        Rectangle mobHitbox,
        Bounds playerBoundsScene,
        boolean warnedInventoryFull,
        Runnable rerender
    ) {
        if (mob == null || mobHitbox == null || player == null || playerBoundsScene == null) {
            return false;
        }

        // Drops are rendered only after the death animation finishes; keep pickup timing consistent.
        if (mob.isDead() && !mob.isDeathAnimationFinished()) {
            return warnedInventoryFull;
        }

        MobMapState.MobData data = mob.getData();
        if (data == null || data.drops == null || data.drops.isEmpty()) {
            return warnedInventoryFull;
        }

        boolean inside = intersectsWithMargin(playerBoundsScene, mobHitbox, AUTO_GATHER_MARGIN_PIXELS);
        if (!inside) {
            return false;
        }

        boolean removedAny = false;
        Iterator<MobMapState.MobDrop> it = data.drops.iterator();
        while (it.hasNext()) {
            MobMapState.MobDrop drop = it.next();
            if (drop == null || drop.itemId <= 0 || drop.quantity <= 0) {
                it.remove();
                removedAny = true;
                continue;
            }

            if (!player.getInventory().addItem(drop.itemId, drop.quantity)) {
                if (!warnedInventoryFull) {
                    AppDialogs.showMessageDialog(null, "Not enough inventory space to collect these drops.");
                    warnedInventoryFull = true;
                }
                break;
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

    private void handlePlayerAttackStarted() {
        if (player == null || playerState == null || human_hitbox == null) return;

        Bounds attackBounds = buildPlayerAttackBounds();
        if (attackBounds == null) return;

        long nowNanos = System.nanoTime();
        GameTimeManager time = GameTimeManager.get();
        double nowSeconds = time.totalSecondsProperty().get();

        int damage = calculatePlayerMobDamage();
        if (damage <= 0) return;

        if (poisonSlimeMob != null && poisonSlimeMob.getBodyHitbox() != null && !poisonSlimeMob.isDead()) {
            Bounds mobBounds = poisonSlimeMob.getBodyHitbox().localToScene(poisonSlimeMob.getBodyHitbox().getBoundsInLocal());
            if (attackBounds.intersects(mobBounds)) {
                boolean killed = poisonSlimeMob.applyPlayerDamage(damage, nowNanos);
                if (killed) {
                    handleMobKilledByPlayer(poisonSlimeMob, nowSeconds);
                    renderPoisonSlimeDrops();
                }
            }
        }

        if (lavaSlimeMob != null && lavaSlimeMob.getBodyHitbox() != null && !lavaSlimeMob.isDead()) {
            Bounds mobBounds = lavaSlimeMob.getBodyHitbox().localToScene(lavaSlimeMob.getBodyHitbox().getBoundsInLocal());
            if (attackBounds.intersects(mobBounds)) {
                boolean killed = lavaSlimeMob.applyPlayerDamage(damage, nowNanos);
                if (killed) {
                    handleMobKilledByPlayer(lavaSlimeMob, nowSeconds);
                    renderLavaSlimeDrops();
                }
            }
        }

        if (orcbMob != null && orcbMob.getBodyHitbox() != null && !orcbMob.isDead()) {
            Bounds mobBounds = orcbMob.getBodyHitbox().localToScene(orcbMob.getBodyHitbox().getBoundsInLocal());
            if (attackBounds.intersects(mobBounds)) {
                boolean killed = orcbMob.applyPlayerDamage(damage, nowNanos);
                if (killed) {
                    handleMobKilledByPlayer(orcbMob, nowSeconds);
                    renderOrcbDrops();
                }
            }
        }
    }

    private Bounds buildPlayerAttackBounds() {
        if (human_hitbox == null || player == null) return null;

        Bounds body = human_hitbox.localToScene(human_hitbox.getBoundsInLocal());
        double attackWidth = 14.0;
        double attackHeight = 20.0;

        return switch (player.getFacingDirection()) {
            case LEFT -> new BoundingBox(
                body.getMinX() - attackWidth,
                body.getMinY() + (body.getHeight() - attackHeight) * 0.5,
                attackWidth,
                attackHeight
            );
            case RIGHT -> new BoundingBox(
                body.getMaxX(),
                body.getMinY() + (body.getHeight() - attackHeight) * 0.5,
                attackWidth,
                attackHeight
            );
            case UP -> new BoundingBox(
                body.getMinX() + (body.getWidth() - attackWidth) * 0.5,
                body.getMinY() - attackHeight,
                attackWidth,
                attackHeight
            );
            case DOWN -> new BoundingBox(
                body.getMinX() + (body.getWidth() - attackWidth) * 0.5,
                body.getMaxY(),
                attackWidth,
                attackHeight
            );
        };
    }

    private void handleMobKilledByPlayer(MobEntity mob, double nowSeconds) {
        if (mob == null || playerState == null) return;

        int expGain = computeExpGain(mob.getData());
        if (expGain > 0) {
            playerState.addExp(expGain);
        }

        if (gameState != null) {
            gameState.addCombatSkillExp(1);
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

        int dropMultiplier = 1;
        MobMapState.MobData data = mob.getData();
        if (data != null && data.mobType != null) {
            String type = data.mobType.trim().toLowerCase(Locale.ROOT);
            if (type.equals("poison_slime") || type.equals("lava_slime")) {
                dropMultiplier = 2;
            } else if (type.equals("orcb")) {
                dropMultiplier = 3;
            }
        }

        if (dropItemId > 0) {
            mob.spawnDrop(dropItemId, dropMultiplier, nowSeconds, MOB_DROP_TTL_SECONDS);
        }
    }

    private int computeExpGain(MobMapState.MobData mob) {
        if (mob == null || playerState == null) return 0;

        // Spec:
        // - Raw EXP uses mob total status (including HP + SP) vs player total status (excluding HP + SP).
        // - Raw EXP = max(1, player_total - mob_total).
        int mobTotal = Math.max(0, mob.maxHp)
            + Math.max(0, mob.maxSp)
            + Math.max(0, mob.str)
            + Math.max(0, mob.agi)
            + Math.max(0, mob.vit)
            + Math.max(0, mob.dex);

        int playerTotal = Math.max(0, playerState.getStr())
            + Math.max(0, playerState.getAgi())
            + Math.max(0, playerState.getVit())
            + Math.max(0, playerState.getDex());

        long raw = (long) playerTotal - (long) mobTotal;
        return (int) Math.max(1L, raw);
    }

    private int calculatePlayerMobDamage() {
        if (playerState == null || player == null) return 0;

        int playerStr = Math.max(0, playerState.getStr());
        int bonus = 0;
        int equippedId = player.getEquippedItemId();
        if (equippedId > 0) {
            String name = ItemDatabase.getItemName(equippedId);
            if (name != null && !name.isBlank()) {
                String normalized = name.trim().toLowerCase(Locale.ROOT);
                if (normalized.contains("sword")) {
                    bonus += 2;
                }
            }
        }

        int rolled = CombatRules.rollAttackDamage(playerStr, bonus);
        int combatBonus = SkillModifiers.combatBonusDamage(getCombatLevel());
        return Math.max(0, rolled + combatBonus);
    }

    private int getCombatLevel() {
        if (gameState == null || gameState.getPlayerSkills() == null) {
            return 0;
        }
        return Math.max(0, gameState.getPlayerSkills().getCombatLevel());
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

    private void renderPoisonSlimeDrops() {
        if (poisonSlimeMob == null) return;
        if (poisonSlimeMob.isDead() && !poisonSlimeMob.isDeathAnimationFinished()) {
            hideMobDropView(poison_slime_drops_img);
            renderedPoisonSlimeDropsKey = null;
            return;
        }
        renderedPoisonSlimeDropsKey = renderMobDrops(poisonSlimeMob.getData(), poison_slime_drops_img, renderedPoisonSlimeDropsKey, this::renderPoisonSlimeDrops);
    }

    private void renderLavaSlimeDrops() {
        if (lavaSlimeMob == null) return;
        if (lavaSlimeMob.isDead() && !lavaSlimeMob.isDeathAnimationFinished()) {
            hideMobDropView(lava_slime_drops_img);
            renderedLavaSlimeDropsKey = null;
            return;
        }
        renderedLavaSlimeDropsKey = renderMobDrops(lavaSlimeMob.getData(), lava_slime_drops_img, renderedLavaSlimeDropsKey, this::renderLavaSlimeDrops);
    }

    private void renderOrcbDrops() {
        if (orcbMob == null) return;
        if (orcbMob.isDead() && !orcbMob.isDeathAnimationFinished()) {
            hideMobDropView(orcb_drops_img);
            renderedOrcbDropsKey = null;
            return;
        }
        renderedOrcbDropsKey = renderMobDrops(orcbMob.getData(), orcb_drops_img, renderedOrcbDropsKey, this::renderOrcbDrops);
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
        dropView.setMouseTransparent(true);
        dropView.setOnMouseClicked(null);

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

    @Override
    protected String mapKey() {
        return "right_map";
    }

    @Override
    protected void configureExits() {
        // Coming from home_map into right_map should spawn FROM_LEFT on right_map,
        // and going back to home_map should spawn FROM_RIGHT on home_map.
        registerExit(exit_left, "/view/home_map.fxml", SpawnTag.FROM_RIGHT);

        // Example: right_map -> upper_map
        registerExit(exit_up, "/view/right_map_noone.fxml", SpawnTag.FROM_DOWN);
    }

   
    @FXML
    private void handlebtntutoaction(ActionEvent event) {
        showOverworldTutorialOverlay();
    }

}
