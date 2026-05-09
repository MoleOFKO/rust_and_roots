package model;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import game.GameState;
import game.GameTimeManager;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import util.CombatRules;
import util.ImageCache;
import util.SkillModifiers;
import util.SoundManager;
import util.TimeFlow;

/**
 * Runtime mob controller (AI + animation) backed by {@link MobMapState.MobData}.
 *
 * This class intentionally owns JavaFX nodes (like {@link Player}) and should not be used as the
 * persisted state container.
 */
public final class MobEntity {

    public enum Kind {
        SLIME,
        ORC
    }

    private enum AnimState {
        IDLE, WALK, RUN, ATTACK, HURT, DEATH
    }

    private static final int FRAME_SIZE = 64;
    private static final double INV_SQRT_2 = 1.0 / Math.sqrt(2.0);
    private static final double NANOS_PER_60FPS_FRAME = 1_000_000_000.0 / 60.0;

    // Player-like regen baseline (per real second).
    private static final double BASE_REGEN_PER_REAL_SECOND = 0.65;
    private static final double REGEN_PER_VIT = 0.07;
    // GameTimeManager uses ~24 in-game seconds per real second at timeScale=1.
    private static final double GAME_SECONDS_PER_REAL_SECOND = 24.0;
    private static final int SLIME_KILL_HP_WEIGHT = 24;
    private static final int SLIME_KILL_SP_WEIGHT = 21;
    private static final int SLIME_KILL_STR_WEIGHT = 24;
    private static final int SLIME_KILL_AGI_WEIGHT = 20;
    private static final int SLIME_KILL_VIT_WEIGHT = 10;
    private static final int ORC_KILL_HP_WEIGHT = 23;
    private static final int ORC_KILL_SP_WEIGHT = 23;
    private static final int ORC_KILL_STR_WEIGHT = 22;
    private static final int ORC_KILL_AGI_WEIGHT = 15;
    private static final int ORC_KILL_VIT_WEIGHT = 15;

    private static final long IDLE_FRAME_DELAY_NANOS = 150_000_000L;
    private static final long WALK_FRAME_DELAY_NANOS = 130_000_000L;
    private static final long RUN_FRAME_DELAY_NANOS = 90_000_000L;
    private static final long BASE_ATTACK_FRAME_DELAY_NANOS = 110_000_000L;
    private static final long HURT_FRAME_DELAY_NANOS = 90_000_000L;
    private static final long DEATH_FRAME_DELAY_NANOS = 90_000_000L;

    // Spec: slime/orc base respawn time is 30 in-game minutes (1,800 seconds).
    private static final double MOB_BASE_RESPAWN_SECONDS = 30.0 * 60.0;

    // Spec: special "run and attack" cooldown is 5 in-game minutes.
    private static final double RUN_ATTACK_COOLDOWN_SECONDS = 5.0 * 60.0;
    private static final double RUN_ATTACK_EDGE_BAND_RATIO = 0.15;
    private static final double RUN_ATTACK_EDGE_BAND_MIN_PIXELS = 8.0;
    private static final double RUN_ATTACK_SPEED_MULTIPLIER = 2.4;

    // Special action SP cost = run + attack.
    private static final int RUN_ATTACK_RUN_SP_COST = 1;
    private static final int ATTACK_SP_COST = 1;
    private static final int RUN_ATTACK_TOTAL_SP_COST = RUN_ATTACK_RUN_SP_COST + ATTACK_SP_COST;

    // Spec base stats (day 1).
    private static final int SLIME_BASE_MAX_HP = 10;
    private static final int SLIME_BASE_MAX_SP = 30;
    private static final int SLIME_BASE_STR = 3;
    private static final int SLIME_BASE_VIT = 6;
    private static final int SLIME_BASE_AGI = 2;
    private static final int SLIME_BASE_DEX = 1;

    private static final int ORC_BASE_MAX_HP = 20;
    private static final int ORC_BASE_MAX_SP = 25;
    private static final int ORC_BASE_STR = 4;
    private static final int ORC_BASE_VIT = 3;
    private static final int ORC_BASE_AGI = 3;
    private static final int ORC_BASE_DEX = 1;

    private int getBaseStatMultiplier() {
        if (data == null || data.mobType == null) return 1;
        String normalized = data.mobType.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "poison_slime", "lava_slime" -> 2;
            case "orcb" -> 3;
            default -> 1;
        };
    }

    private void tryApplyOnHitStatusEffect(PlayerState target) {
        if (target == null || target.isDead() || data == null || data.mobType == null) {
            return;
        }

        String normalized = data.mobType.trim().toLowerCase(Locale.ROOT);
        boolean poison = normalized.equals("poison_slime");
        boolean burn = normalized.equals("lava_slime");
        if (!poison && !burn) {
            return;
        }

        // 10% chance on each successful attack hit.
        if (ThreadLocalRandom.current().nextInt(100) >= 10) {
            return;
        }

        GameTimeManager time = GameTimeManager.get();
        double nowSeconds = (time == null) ? 0.0 : time.totalSecondsProperty().get();
        if (poison) {
            target.applyPoisonDebuff(nowSeconds);
        } else {
            target.applyBurnDebuff(nowSeconds);
        }
    }

    private final Kind kind;
    private final MobMapState.MobData data;

    private final Group node;
    private final Rectangle bodyHitbox;
    private final ImageView spriteView;
    private final Rectangle detectRange;
    private final Rectangle attackLeft;
    private final Rectangle attackRight;
    private final Rectangle attackUp;
    private final Rectangle attackDown;

    private final List<Rectangle> walls;

    private final Image idleSheet;
    private final int idleColumns;
    private final Image walkSheet;
    private final int walkColumns;
    private final Image runSheet;
    private final int runColumns;
    private final Image attackSheet;
    private final int attackColumns;
    private final Image hurtSheet;
    private final int hurtColumns;
    private final Image deathSheet;
    private final int deathColumns;

    private AnimState animState = AnimState.IDLE;
    // Row mapping: 0=down, 1=left, 2=right, 3=up
    private int facingRow = 0;
    private int frameIndex = 0;

    private long lastUpdateNanos = 0L;
    private long lastAnimFrameNanos = 0L;
    private long lastDeathFrameNanos = 0L;
    private long simNowNanos = 0L;

    private boolean moving = false;
    private boolean runAttackActive = false;
    private boolean deathStarted = false;
    private boolean deathFinished = false;
    private boolean hurtActive = false;
    private long hurtEndsAtNanos = 0L;
    private final Effect hurtFlashEffect = createHurtFlashEffect();
    private long attackEndsAtNanos = 0L;
    private long nextAttackAvailableAtNanos = 0L;
    private boolean attackDamageApplied = false;

    private double hpRegenCarry = 0.0;
    private double spRegenCarry = 0.0;

    public MobEntity(
        Kind kind,
        MobMapState.MobData data,
        Group node,
        Rectangle bodyHitbox,
        ImageView spriteView,
        Rectangle detectRange,
        Rectangle attackLeft,
        Rectangle attackRight,
        Rectangle attackUp,
        Rectangle attackDown,
        List<Rectangle> walls,
        String idleSheetPath,
        int idleColumns,
        String walkSheetPath,
        int walkColumns,
        String runSheetPath,
        int runColumns,
        String attackSheetPath,
        int attackColumns,
        String hurtSheetPath,
        int hurtColumns,
        String deathSheetPath,
        int deathColumns
    ) {
        this.kind = Objects.requireNonNullElse(kind, Kind.SLIME);
        this.data = Objects.requireNonNull(data, "data");

        this.node = Objects.requireNonNull(node, "node");
        this.bodyHitbox = Objects.requireNonNull(bodyHitbox, "bodyHitbox");
        this.spriteView = Objects.requireNonNull(spriteView, "spriteView");
        this.detectRange = detectRange;
        this.attackLeft = attackLeft;
        this.attackRight = attackRight;
        this.attackUp = attackUp;
        this.attackDown = attackDown;

        this.walls = (walls == null) ? List.of() : walls;

        this.idleSheet = loadRequiredImage(idleSheetPath);
        this.idleColumns = Math.max(1, idleColumns);
        this.walkSheet = loadRequiredImage(walkSheetPath);
        this.walkColumns = Math.max(1, walkColumns);
        this.runSheet = loadRequiredImage(runSheetPath);
        this.runColumns = Math.max(1, runColumns);
        this.attackSheet = loadRequiredImage(attackSheetPath);
        this.attackColumns = Math.max(1, attackColumns);
        this.hurtSheet = loadRequiredImage(hurtSheetPath);
        this.hurtColumns = Math.max(1, hurtColumns);
        this.deathSheet = loadRequiredImage(deathSheetPath);
        this.deathColumns = Math.max(1, deathColumns);

        this.spriteView.setPreserveRatio(false);
        this.spriteView.setSmooth(false);
        this.spriteView.setMouseTransparent(true);
        this.spriteView.setViewport(new Rectangle2D(0, 0, FRAME_SIZE, FRAME_SIZE));
        this.spriteView.setImage(idleSheet);

        this.bodyHitbox.setMouseTransparent(true);
        if (this.detectRange != null) this.detectRange.setMouseTransparent(true);

        syncAttackHitboxesEnabled(true);
        updateViewport();
    }

    public Kind getKind() {
        return kind;
    }

    public MobMapState.MobData getData() {
        return data;
    }

    public Rectangle getBodyHitbox() {
        return bodyHitbox;
    }

    public boolean isDead() {
        return data.dead;
    }

    public boolean isDeathAnimationFinished() {
        return deathFinished;
    }

    public void update(long nowNanos, int currentDay, double nowSeconds, Bounds playerBoundsScene, PlayerState playerState) {
        updateDropsExpiry(nowSeconds);

        if (data.dead) {
            scheduleRespawnIfNeeded(nowSeconds);

            if (deathFinished) {
                if (tryRespawnIfReady(currentDay, nowSeconds)) {
                    return;
                }
                return;
            }
        }

        if (deathFinished) {
            return;
        }

        if (lastUpdateNanos == 0L) {
            lastUpdateNanos = nowNanos;
            simNowNanos = 0L;
            lastAnimFrameNanos = 0L;
            lastDeathFrameNanos = 0L;
            syncNodePositionToStateIfNeeded();
            if (data.dead) {
                finalizeDeathState();
            } else {
                syncAttackHitboxesEnabled(true);
                updateViewport();
            }
            return;
        }

        long dtNanos = Math.max(0L, nowNanos - lastUpdateNanos);
        lastUpdateNanos = nowNanos;

        double worldDilation = GameTimeManager.get().getWorldDilation();
        double timeFlow = TimeFlow.entityTimeFlowFromAgi(data.agi);
        double effective = Math.max(0.0, worldDilation) * Math.max(0.0, timeFlow);

        long dtSimNanos = scaleDeltaNanos(dtNanos, effective);
        simNowNanos = safeAddNanos(simNowNanos, dtSimNanos);

        // Persist position each tick.
        data.layoutX = node.getLayoutX();
        data.layoutY = node.getLayoutY();

        if (data.dead) {
            updateDeathAnimation(simNowNanos);
            return;
        }

        applyDailyGrowthIfNeeded(currentDay);
        applyRegenIfNeeded(nowSeconds);

        updateHurtFlash(simNowNanos);

        if (isAttackActive(simNowNanos)) {
            moving = false;
            syncAttackHitboxesEnabled(true);
            setAnimState(AnimState.ATTACK, false);
            tryApplyAttackDamage(playerBoundsScene, playerState);
            updateAnimation(simNowNanos);
            return;
        }

        // Attack finished.
        if (animState == AnimState.ATTACK) {
            attackDamageApplied = false;
            setAnimState(AnimState.IDLE, true);
        }

        // Spec: if SP is 0, mobs can't take any actions (move/attack/etc.).
        if (data.sp <= 0) {
            moving = false;
            syncAttackHitboxesEnabled(true);
            setAnimState(AnimState.IDLE, false);
            updateAnimation(simNowNanos);
            return;
        }

        boolean canChase = shouldChase(playerBoundsScene, playerState);

        if (canChase) {
            if (!runAttackActive && shouldStartRunAttack(nowSeconds, playerBoundsScene, playerState)) {
                startRunAttack(nowSeconds);
            }

            double speedMultiplier = runAttackActive ? RUN_ATTACK_SPEED_MULTIPLIER : 1.0;
            moveTowardTarget(dtSimNanos, playerBoundsScene, speedMultiplier);
            syncAttackHitboxesEnabled(true);
            if (moving) {
                setAnimState(runAttackActive ? AnimState.RUN : AnimState.WALK, false);
            } else {
                setAnimState(AnimState.IDLE, false);
            }

            if (shouldStartAttack(simNowNanos, playerBoundsScene, playerState)) {
                startAttack(simNowNanos);
                tryApplyAttackDamage(playerBoundsScene, playerState);
                runAttackActive = false;
            }
        } else {
            moving = false;
            runAttackActive = false;
            syncAttackHitboxesEnabled(true);
            setAnimState(AnimState.IDLE, false);
        }

        updateAnimation(simNowNanos);
    }

    public boolean applyPlayerDamage(int amount, long nowNanos) {
        int raw = Math.max(0, amount);
        int dmg = CombatRules.applyDamageReduction(raw, data.vit);

        // Night scaling: mobs take reduced damage from the player as days progress.
        // Day 1 => -1, Day 2 => -2, ...
        GameTimeManager timeManager = GameTimeManager.get();
        if (dmg > 0 && timeManager != null && timeManager.isNight()) {
            int day = Math.max(1, timeManager.dayNumberProperty().get());
            dmg = Math.max(0, dmg - day);
        }
        if (dmg <= 0 || data.dead) return false;

        int hpBefore = Math.max(0, data.hp);
        int hpAfter = Math.max(0, hpBefore - dmg);
        data.hp = hpAfter;

        long now = simNowNanos;
        hurtActive = true;
        hurtEndsAtNanos = now + (long) hurtColumns * HURT_FRAME_DELAY_NANOS;
        applyHurtFlashEffect();

        if (hpAfter <= 0) {
            data.dead = true;
            bodyHitbox.setDisable(true);
            if (detectRange != null) {
                detectRange.setDisable(true);
            }
            return true;
        }
        return false;
    }

    public void spawnDrop(int itemId, int quantity, double nowSeconds, double ttlSeconds) {
        if (itemId <= 0 || quantity <= 0) return;
        double expires = ttlSeconds <= 0.0 ? -1.0 : (nowSeconds + ttlSeconds);
        data.drops.add(new MobMapState.MobDrop(itemId, quantity, nowSeconds, expires));
    }

    public void applyKillBonus(PlayerState targetState) {
        if (data.dead) return;
        if (targetState == null) return;

        int roll = ThreadLocalRandom.current().nextInt(100);
        KillBonusStat stat = chooseKillBonusStat(roll);
        applyKillBonus(stat);
    }

    private enum KillBonusStat {
        HP,
        SP,
        STR,
        AGI,
        VIT,
        DEX
    }

    private KillBonusStat chooseKillBonusStat(int roll0to99) {
        int r = Math.max(0, Math.min(99, roll0to99));

        if (kind == Kind.SLIME) {
            if (r < SLIME_KILL_HP_WEIGHT) return KillBonusStat.HP;
            if (r < SLIME_KILL_HP_WEIGHT + SLIME_KILL_SP_WEIGHT) return KillBonusStat.SP;
            if (r < SLIME_KILL_HP_WEIGHT + SLIME_KILL_SP_WEIGHT + SLIME_KILL_STR_WEIGHT) return KillBonusStat.STR;
            if (r < SLIME_KILL_HP_WEIGHT + SLIME_KILL_SP_WEIGHT + SLIME_KILL_STR_WEIGHT + SLIME_KILL_AGI_WEIGHT) return KillBonusStat.AGI;
            if (r < SLIME_KILL_HP_WEIGHT + SLIME_KILL_SP_WEIGHT + SLIME_KILL_STR_WEIGHT + SLIME_KILL_AGI_WEIGHT + SLIME_KILL_VIT_WEIGHT) return KillBonusStat.VIT;
            return KillBonusStat.DEX;
        }

        if (r < ORC_KILL_HP_WEIGHT) return KillBonusStat.HP;
        if (r < ORC_KILL_HP_WEIGHT + ORC_KILL_SP_WEIGHT) return KillBonusStat.SP;
        if (r < ORC_KILL_HP_WEIGHT + ORC_KILL_SP_WEIGHT + ORC_KILL_STR_WEIGHT) return KillBonusStat.STR;
        if (r < ORC_KILL_HP_WEIGHT + ORC_KILL_SP_WEIGHT + ORC_KILL_STR_WEIGHT + ORC_KILL_AGI_WEIGHT) return KillBonusStat.AGI;
        if (r < ORC_KILL_HP_WEIGHT + ORC_KILL_SP_WEIGHT + ORC_KILL_STR_WEIGHT + ORC_KILL_AGI_WEIGHT + ORC_KILL_VIT_WEIGHT) return KillBonusStat.VIT;
        return KillBonusStat.DEX;
    }

    private void applyKillBonus(KillBonusStat stat) {
        if (stat == null) return;
        switch (stat) {
            case HP -> {
                data.maxHp = Math.max(0, data.maxHp) + 1;
                data.hp = Math.min(data.maxHp, Math.max(0, data.hp) + 1);
            }
            case SP -> {
                data.maxSp = Math.max(0, data.maxSp) + 1;
                data.sp = Math.min(data.maxSp, Math.max(0, data.sp) + 1);
            }
            case STR -> data.str = Math.max(0, data.str) + 1;
            case AGI -> data.agi = Math.max(0, data.agi) + 1;
            case VIT -> data.vit = Math.max(0, data.vit) + 1;
            case DEX -> data.dex = Math.max(0, data.dex) + 1;
        }
    }

    private void syncNodePositionToStateIfNeeded() {
        // Only apply from state if this mob has been initialized (type set).
        if (data.mobType == null || data.mobType.isBlank()) {
            data.layoutX = node.getLayoutX();
            data.layoutY = node.getLayoutY();
            return;
        }
        node.setLayoutX(data.layoutX);
        node.setLayoutY(data.layoutY);
    }

    private void updateDropsExpiry(double nowSeconds) {
        if (data.drops.isEmpty()) return;
        data.drops.removeIf(drop ->
            drop == null ||
            drop.itemId <= 0 ||
            drop.quantity <= 0 ||
            (drop.expiresAtTotalSeconds > 0 && nowSeconds >= drop.expiresAtTotalSeconds)
        );
    }

    private void applyDailyGrowthIfNeeded(int currentDay) {
        int safeDay = Math.max(1, currentDay);
        int last = data.lastUpdatedDayNumber <= 0 ? safeDay : data.lastUpdatedDayNumber;
        if (safeDay <= last) {
            if (safeDay < last) {
                data.lastUpdatedDayNumber = safeDay;
            }
            return;
        }

        int delta = safeDay - last;
        data.lastUpdatedDayNumber = safeDay;

        data.livedDays = Math.max(0, data.livedDays + delta);

        for (int i = 0; i < delta; i++) {
            applySingleDailyGrowthRoll();
        }

        data.maxHp = Math.max(1, data.maxHp);
        data.maxSp = Math.max(1, data.maxSp);
        data.hp = Math.max(0, Math.min(data.maxHp, data.hp));
        data.sp = Math.max(0, Math.min(data.maxSp, data.sp));
    }

    private void applySingleDailyGrowthRoll() {
        int roll = ThreadLocalRandom.current().nextInt(100);

        if (kind == Kind.SLIME) {
            // Spec (slime): STR 10%, AGI 20%, MAX SP 30%, VIT 30%, MAX HP 10%.
            if (roll < 10) {
                data.str = Math.max(0, data.str) + 1;
                return;
            }
            if (roll < 30) {
                data.agi = Math.max(0, data.agi) + 1;
                return;
            }
            if (roll < 60) {
                data.maxSp = Math.max(1, data.maxSp) + 1;
                data.sp = Math.min(data.maxSp, Math.max(0, data.sp) + 1);
                return;
            }
            if (roll < 90) {
                data.vit = Math.max(0, data.vit) + 1;
                return;
            }

            data.maxHp = Math.max(1, data.maxHp) + 1;
            data.hp = Math.min(data.maxHp, Math.max(0, data.hp) + 1);
            return;
        }

        // Spec (orc): STR 35%, AGI 5%, MAX HP 30%, VIT 30%.
        if (roll < 35) {
            data.str = Math.max(0, data.str) + 1;
            return;
        }
        if (roll < 40) {
            data.agi = Math.max(0, data.agi) + 1;
            return;
        }
        if (roll < 70) {
            data.maxHp = Math.max(1, data.maxHp) + 1;
            data.hp = Math.min(data.maxHp, Math.max(0, data.hp) + 1);
            return;
        }

        data.vit = Math.max(0, data.vit) + 1;
    }

    private void scheduleRespawnIfNeeded(double nowSeconds) {
        if (!data.dead) {
            data.respawnAtTotalSeconds = -1.0;
            return;
        }

        if (data.respawnAtTotalSeconds >= 0.0) {
            return;
        }

        if (Double.isNaN(nowSeconds) || Double.isInfinite(nowSeconds)) {
            data.respawnAtTotalSeconds = -1.0;
            return;
        }

        data.respawnAtTotalSeconds = Math.max(0.0, nowSeconds + computeRespawnSeconds());
    }

    private double computeRespawnSeconds() {
        int totalStatusPoints = 0;
        totalStatusPoints += Math.max(0, data.maxHp);
        totalStatusPoints += Math.max(0, data.maxSp);
        totalStatusPoints += Math.max(0, data.str);
        totalStatusPoints += Math.max(0, data.agi);
        totalStatusPoints += Math.max(0, data.vit);
        totalStatusPoints += Math.max(0, data.dex);
        return Math.max(MOB_BASE_RESPAWN_SECONDS, (double) totalStatusPoints);
    }

    private boolean tryRespawnIfReady(int currentDay, double nowSeconds) {
        if (!data.dead) {
            return false;
        }
        if (data.respawnAtTotalSeconds < 0.0) {
            return false;
        }
        if (nowSeconds < data.respawnAtTotalSeconds) {
            return false;
        }

        respawn(currentDay, nowSeconds);
        return true;
    }

    private void respawn(int currentDay, double nowSeconds) {
        int day = Math.max(1, currentDay);

        data.dead = false;
        data.respawnAtTotalSeconds = -1.0;
        data.runAttackCooldownUntilTotalSeconds = -1.0;
        runAttackActive = false;

        data.livedDays = 0;
        data.lastUpdatedDayNumber = day;
        data.lastRegenTotalSeconds = nowSeconds;

        // Reset stats to base and then apply world-day growth rolls so newly respawned mobs scale with the timeline.
        int baseMultiplier = Math.max(1, getBaseStatMultiplier());
        if (kind == Kind.SLIME) {
            data.maxHp = SLIME_BASE_MAX_HP * baseMultiplier;
            data.maxSp = SLIME_BASE_MAX_SP * baseMultiplier;
            data.str = SLIME_BASE_STR * baseMultiplier;
            data.agi = SLIME_BASE_AGI * baseMultiplier;
            data.vit = SLIME_BASE_VIT * baseMultiplier;
            data.dex = SLIME_BASE_DEX * baseMultiplier;
        } else {
            data.maxHp = ORC_BASE_MAX_HP * baseMultiplier;
            data.maxSp = ORC_BASE_MAX_SP * baseMultiplier;
            data.str = ORC_BASE_STR * baseMultiplier;
            data.agi = ORC_BASE_AGI * baseMultiplier;
            data.vit = ORC_BASE_VIT * baseMultiplier;
            data.dex = ORC_BASE_DEX * baseMultiplier;
        }

        int worldRolls = Math.max(0, day - 1);
        for (int i = 0; i < worldRolls; i++) {
            applySingleDailyGrowthRoll();
        }

        data.maxHp = Math.max(1, data.maxHp);
        data.maxSp = Math.max(1, data.maxSp);
        data.hp = data.maxHp;
        data.sp = data.maxSp;

        hpRegenCarry = 0.0;
        spRegenCarry = 0.0;

        moving = false;
        deathStarted = false;
        deathFinished = false;
        hurtActive = false;
        hurtEndsAtNanos = 0L;
        clearHurtFlashEffect();
        attackEndsAtNanos = 0L;
        nextAttackAvailableAtNanos = 0L;
        attackDamageApplied = false;
        frameIndex = 0;

        bodyHitbox.setDisable(false);
        if (detectRange != null) {
            detectRange.setDisable(false);
        }

        syncAttackHitboxesEnabled(true);
        setAnimState(AnimState.IDLE, true);

        lastUpdateNanos = 0L;
        lastAnimFrameNanos = 0L;
        lastDeathFrameNanos = 0L;
        simNowNanos = 0L;
    }

    private void applyRegenIfNeeded(double nowSeconds) {
        if (data.maxHp <= 0 && data.maxSp <= 0) {
            data.lastRegenTotalSeconds = nowSeconds;
            return;
        }

        if (data.lastRegenTotalSeconds < 0 || data.lastRegenTotalSeconds > nowSeconds) {
            data.lastRegenTotalSeconds = nowSeconds;
            return;
        }

        double elapsed = nowSeconds - data.lastRegenTotalSeconds;
        if (elapsed <= 0.0) {
            return;
        }

        int maxHp = Math.max(0, data.maxHp);
        int maxSp = Math.max(0, data.maxSp);

        int vit = Math.max(0, data.vit);
        double amountMultiplier = 1.0 + (vit * 0.02);
        double speedMultiplier = 1.0 + (vit * 0.01);

        double hpRegenPerGameSecond = (maxHp * 0.01 * amountMultiplier * speedMultiplier) / 60.0;

        // Spec: SP only regens while idle/walking.
        boolean canRegenSp = !runAttackActive
            && (animState == AnimState.IDLE || animState == AnimState.WALK)
            && !isAttackActive(simNowNanos);
        double spRegenPerGameSecond = canRegenSp
            ? (maxSp * 0.01 * amountMultiplier * speedMultiplier) / 60.0
            : 0.0;

        if (hpRegenPerGameSecond <= 0.0 && spRegenPerGameSecond <= 0.0) {
            data.lastRegenTotalSeconds = nowSeconds;
            return;
        }

        double hpGain = hpRegenPerGameSecond * elapsed;
        double spGain = spRegenPerGameSecond * elapsed;

        hpRegenCarry += hpGain;
        spRegenCarry += spGain;

        while (hpRegenCarry >= 1.0 && data.hp < maxHp) {
            data.hp += 1;
            hpRegenCarry -= 1.0;
        }
        while (spRegenCarry >= 1.0 && data.sp < maxSp) {
            data.sp += 1;
            spRegenCarry -= 1.0;
        }

        // Don't stockpile regen while at full.
        if (data.hp >= maxHp) {
            hpRegenCarry = 0.0;
        }
        if (data.sp >= maxSp) {
            spRegenCarry = 0.0;
        }

        if (data.hp > maxHp) data.hp = maxHp;
        if (data.sp > maxSp) data.sp = maxSp;
        if (data.hp < 0) data.hp = 0;
        if (data.sp < 0) data.sp = 0;

        data.lastRegenTotalSeconds = nowSeconds;
    }

    private boolean shouldChase(Bounds playerBoundsScene, PlayerState playerState) {
        if (detectRange == null || playerBoundsScene == null || playerState == null) return false;
        if (playerState.isDead()) return false;

        Bounds detectBounds = detectRange.localToScene(detectRange.getBoundsInLocal());
        return detectBounds.intersects(playerBoundsScene);
    }

    private void moveTowardTarget(long dtNanos, Bounds playerBoundsScene, double speedMultiplier) {
        if (dtNanos <= 0L || playerBoundsScene == null) {
            moving = false;
            return;
        }

        if (node.getParent() == null) {
            moving = false;
            return;
        }

        Bounds mobBoundsScene = bodyHitbox.localToScene(bodyHitbox.getBoundsInLocal());
        Bounds mobBounds = node.getParent().sceneToLocal(mobBoundsScene);
        Bounds playerBounds = node.getParent().sceneToLocal(playerBoundsScene);

        double mobCx = mobBounds.getMinX() + mobBounds.getWidth() / 2.0;
        double mobCy = mobBounds.getMinY() + mobBounds.getHeight() / 2.0;
        double playerCx = playerBounds.getMinX() + playerBounds.getWidth() / 2.0;
        double playerCy = playerBounds.getMinY() + playerBounds.getHeight() / 2.0;

        double vx = playerCx - mobCx;
        double vy = playerCy - mobCy;

        if (Math.abs(vx) > Math.abs(vy)) {
            facingRow = vx < 0 ? 1 : 2;
        } else {
            facingRow = vy < 0 ? 3 : 0;
        }

        double dist = Math.hypot(vx, vy);
        if (dist <= 1.0e-6) {
            moving = false;
            return;
        }

        double dirX = vx / dist;
        double dirY = vy / dist;

        double speed = getMovementSpeed() * Math.max(0.0, speedMultiplier);
        double frames = dtNanos / NANOS_PER_60FPS_FRAME;
        // Cap movement steps to avoid huge jumps after stalls (15 frames @ 60fps).
        frames = Math.min(15.0, Math.max(0.0, frames));
        double frameScale = 0.15 * frames;
        double dx = dirX * speed * frameScale;
        double dy = dirY * speed * frameScale;

        if (dx != 0.0 && dy != 0.0) {
            dx *= INV_SQRT_2;
            dy *= INV_SQRT_2;
        }

        Bounds playerBoundsForCollision = playerBounds;
        moving = move(dx, dy, playerBoundsForCollision);

        data.layoutX = node.getLayoutX();
        data.layoutY = node.getLayoutY();
    }

    private double getMovementSpeed() {
        return 5.0;
    }

    private boolean shouldStartRunAttack(double nowSeconds, Bounds playerBoundsScene, PlayerState playerState) {
        if (detectRange == null || playerBoundsScene == null || playerState == null) return false;
        if (data.dead) return false;
        if (playerState.isDead()) return false;

        if (Double.isNaN(nowSeconds) || Double.isInfinite(nowSeconds)) {
            return false;
        }

        double cooldownUntil = data.runAttackCooldownUntilTotalSeconds;
        if (!Double.isNaN(cooldownUntil)
            && !Double.isInfinite(cooldownUntil)
            && cooldownUntil >= 0.0
            && nowSeconds < cooldownUntil) {
            return false;
        }

        if (data.sp < RUN_ATTACK_TOTAL_SP_COST) {
            return false;
        }

        Bounds detectBounds = detectRange.localToScene(detectRange.getBoundsInLocal());
        if (!detectBounds.intersects(playerBoundsScene)) {
            return false;
        }

        double band = Math.max(
            RUN_ATTACK_EDGE_BAND_MIN_PIXELS,
            Math.min(detectBounds.getWidth(), detectBounds.getHeight()) * RUN_ATTACK_EDGE_BAND_RATIO
        );

        double playerCx = playerBoundsScene.getMinX() + (playerBoundsScene.getWidth() / 2.0);
        double playerCy = playerBoundsScene.getMinY() + (playerBoundsScene.getHeight() / 2.0);

        double distLeft = playerCx - detectBounds.getMinX();
        double distRight = detectBounds.getMaxX() - playerCx;
        double distUp = playerCy - detectBounds.getMinY();
        double distDown = detectBounds.getMaxY() - playerCy;

        double minDist = Math.min(Math.min(distLeft, distRight), Math.min(distUp, distDown));
        return minDist <= band;
    }

    private void startRunAttack(double nowSeconds) {
        if (Double.isNaN(nowSeconds) || Double.isInfinite(nowSeconds)) {
            return;
        }

        runAttackActive = true;
        data.runAttackCooldownUntilTotalSeconds = nowSeconds + RUN_ATTACK_COOLDOWN_SECONDS;
        data.sp = Math.max(0, data.sp - RUN_ATTACK_RUN_SP_COST);
    }

    private boolean move(double dx, double dy, Bounds playerBoundsInParent) {
        boolean moved = false;

        if (dx != 0.0 && !collides(dx, 0.0, playerBoundsInParent)) {
            node.setLayoutX(node.getLayoutX() + dx);
            moved = true;
        }

        if (dy != 0.0 && !collides(0.0, dy, playerBoundsInParent)) {
            node.setLayoutY(node.getLayoutY() + dy);
            moved = true;
        }

        return moved;
    }

    private boolean collides(double dx, double dy, Bounds playerBoundsInParent) {
        if (node.getParent() == null) {
            return false;
        }

        Bounds mobBounds = node.getParent().sceneToLocal(bodyHitbox.localToScene(bodyHitbox.getBoundsInLocal()));
        Bounds movedBounds = new BoundingBox(
            mobBounds.getMinX() + dx,
            mobBounds.getMinY() + dy,
            mobBounds.getWidth(),
            mobBounds.getHeight()
        );

        if (playerBoundsInParent != null && movedBounds.intersects(playerBoundsInParent)) {
            return true;
        }

        for (Rectangle wall : walls) {
            if (wall == null || wall.isDisable()) continue;
            Bounds wallBounds = node.getParent().sceneToLocal(wall.localToScene(wall.getBoundsInLocal()));
            if (movedBounds.intersects(wallBounds)) {
                return true;
            }
        }

        return false;
    }

    private boolean shouldStartAttack(long nowNanos, Bounds playerBoundsScene, PlayerState playerState) {
        if (playerBoundsScene == null || playerState == null) return false;
        if (nowNanos < nextAttackAvailableAtNanos) return false;
        if (data.sp <= 0) return false;

        Rectangle active = resolveActiveAttackHitbox();
        if (active == null || active.isDisable()) return false;

        Bounds attackBounds = active.localToScene(active.getBoundsInLocal());
        return attackBounds.intersects(playerBoundsScene);
    }

    private void startAttack(long nowNanos) {
        long duration = (long) attackColumns * getAttackFrameDelayNanos();
        attackEndsAtNanos = nowNanos + duration;
        nextAttackAvailableAtNanos = attackEndsAtNanos;
        attackDamageApplied = false;

        data.sp = Math.max(0, data.sp - ATTACK_SP_COST);

        moving = false;
        syncAttackHitboxesEnabled(true);
        setAnimState(AnimState.ATTACK, true);
        lastAnimFrameNanos = nowNanos;
    }

    private void tryApplyAttackDamage(Bounds playerBoundsScene, PlayerState playerState) {
        if (playerBoundsScene == null || playerState == null) return;
        if (attackDamageApplied) return;
        Rectangle active = resolveActiveAttackHitbox();
        if (active == null || active.isDisable()) return;

        Bounds attackBounds = active.localToScene(active.getBoundsInLocal());
        if (!attackBounds.intersects(playerBoundsScene)) {
            return;
        }

        boolean wasAlive = !playerState.isDead();
        SoundManager.playSfx(SoundManager.Sfx.HIT);
        int dmg = computeDamageTo(playerState);
        playerState.damageHp(dmg);
        if (wasAlive && !playerState.isDead()) {
            tryApplyOnHitStatusEffect(playerState);
        }
        attackDamageApplied = true;

        if (wasAlive && playerState.isDead()) {
            applyKillBonus(playerState);
        }
    }

    private int computeDamageTo(PlayerState target) {
        if (target == null) return 0;
        int rolled = CombatRules.rollAttackDamage(data.str, 0);
        int baseReduced = CombatRules.applyDamageReduction(rolled, target.getVit());

        // Night scaling:
        // - Scale by day for increasing challenge, but cap it so late-game isn't mathematically impossible.
        // - Apply combat-skill mitigation after the scaling so combat progression stays meaningful.
        int nightBonus = 0;
        GameTimeManager timeManager = GameTimeManager.get();
        if (timeManager != null && timeManager.isNight()) {
            int day = Math.max(1, timeManager.dayNumberProperty().get());
            nightBonus = Math.min(25, day);
        }

        int combatReduction = 0;
        GameState gs = GameState.get();
        if (gs != null && gs.getPlayerSkills() != null) {
            int combatLevel = Math.max(0, gs.getPlayerSkills().getCombatLevel());
            combatReduction = SkillModifiers.combatReceivedDamageReduction(combatLevel);
        }

        long boosted = (long) baseReduced + (long) nightBonus;
        int scaled = (int) Math.min((long) Integer.MAX_VALUE, Math.max(0L, boosted));
        return Math.max(0, scaled - Math.max(0, combatReduction));
    }

    private Rectangle resolveActiveAttackHitbox() {
        return switch (facingRow) {
            case 1 -> attackLeft;
            case 2 -> attackRight;
            case 3 -> attackUp;
            default -> attackDown;
        };
    }

    private boolean isAttackActive(long nowNanos) {
        return attackEndsAtNanos > 0L && nowNanos < attackEndsAtNanos;
    }

    private long getAttackFrameDelayNanos() {
        return Math.max((long) NANOS_PER_60FPS_FRAME, BASE_ATTACK_FRAME_DELAY_NANOS);
    }

    private static long scaleDeltaNanos(long dtNanos, double multiplier) {
        if (dtNanos <= 0L) return 0L;
        if (multiplier <= 0.0) return 0L;
        if (Double.isNaN(multiplier) || Double.isInfinite(multiplier)) return dtNanos;

        double scaled = dtNanos * multiplier;
        if (scaled >= Long.MAX_VALUE) return Long.MAX_VALUE;
        return (long) Math.floor(scaled);
    }

    private static long safeAddNanos(long a, long b) {
        if (b <= 0L) return a;
        if (a >= Long.MAX_VALUE - b) return Long.MAX_VALUE;
        return a + b;
    }

    private void updateAnimation(long nowNanos) {
        if (animState == AnimState.DEATH) {
            updateDeathAnimation(nowNanos);
            return;
        }

        long delay = getFrameDelay(animState);
        long elapsed = nowNanos - lastAnimFrameNanos;
        if (elapsed < delay) {
            if (animState == AnimState.ATTACK) {
                // Apply damage on first frame where we have a chance.
                // (Damage itself is applied from update() when attack starts).
            }
            return;
        }

        long steps = elapsed / Math.max(1L, delay);
        steps = Math.min(steps, 5L);
        lastAnimFrameNanos += steps * delay;

        int frameCount = getFrameCount(animState);
        int loopSteps = (int) (steps % Math.max(1, frameCount));
        int nonLoopSteps = (int) steps;

        switch (animState) {
            case IDLE -> frameIndex = (frameIndex + loopSteps) % frameCount;
            case WALK, RUN -> {
                if (moving) {
                    frameIndex = (frameIndex + loopSteps) % frameCount;
                } else {
                    frameIndex = 0;
                }
            }
            case ATTACK, HURT -> frameIndex = Math.min(frameCount - 1, frameIndex + nonLoopSteps);
            case DEATH -> {
            }
        }

        updateViewport();
    }

    private void updateDeathAnimation(long nowNanos) {
        if (!deathStarted) {
            deathStarted = true;
            hurtActive = false;
            hurtEndsAtNanos = 0L;
            clearHurtFlashEffect();
            setAnimState(AnimState.DEATH, true);
            lastDeathFrameNanos = nowNanos;
            syncAttackHitboxesEnabled(false);
            updateViewport();
            return;
        }

        if (nowNanos - lastDeathFrameNanos < DEATH_FRAME_DELAY_NANOS) {
            return;
        }

        lastDeathFrameNanos = nowNanos;
        frameIndex++;

        if (frameIndex >= deathColumns) {
            finalizeDeathState();
            return;
        }

        updateViewport();
    }

    private void finalizeDeathState() {
        deathFinished = true;
        deathStarted = true;
        setAnimState(AnimState.DEATH, false);
        clearHurtFlashEffect();

        spriteView.setVisible(false);
        spriteView.setManaged(false);

        bodyHitbox.setDisable(true);
        bodyHitbox.setVisible(false);
        bodyHitbox.setManaged(false);

        if (detectRange != null) {
            detectRange.setDisable(true);
            detectRange.setVisible(false);
            detectRange.setManaged(false);
        }

        syncAttackHitboxesEnabled(false);
    }

    private long getFrameDelay(AnimState state) {
        return switch (state) {
            case IDLE -> IDLE_FRAME_DELAY_NANOS;
            case WALK -> WALK_FRAME_DELAY_NANOS;
            case RUN -> RUN_FRAME_DELAY_NANOS;
            case ATTACK -> getAttackFrameDelayNanos();
            case HURT -> HURT_FRAME_DELAY_NANOS;
            case DEATH -> DEATH_FRAME_DELAY_NANOS;
        };
    }

    private int getFrameCount(AnimState state) {
        return switch (state) {
            case IDLE -> idleColumns;
            case WALK -> walkColumns;
            case RUN -> runColumns;
            case ATTACK -> attackColumns;
            case HURT -> hurtColumns;
            case DEATH -> deathColumns;
        };
    }

    private void updateViewport() {
        Image sheet = switch (animState) {
            case IDLE -> idleSheet;
            case WALK -> walkSheet;
            case RUN -> runSheet;
            case ATTACK -> attackSheet;
            case HURT -> hurtSheet;
            case DEATH -> deathSheet;
        };

        int frameCount = getFrameCount(animState);
        int safeFrame = Math.max(0, Math.min(frameIndex, Math.max(0, frameCount - 1)));
        int x = safeFrame * FRAME_SIZE;
        int y = facingRow * FRAME_SIZE;

        spriteView.setImage(sheet);
        spriteView.setViewport(new Rectangle2D(x, y, FRAME_SIZE, FRAME_SIZE));
        spriteView.setVisible(true);
        spriteView.setManaged(true);
    }

    private void syncAttackHitboxesEnabled(boolean enabled) {
        Rectangle active = resolveActiveAttackHitbox();
        setHitboxEnabled(attackLeft, enabled && active == attackLeft);
        setHitboxEnabled(attackRight, enabled && active == attackRight);
        setHitboxEnabled(attackUp, enabled && active == attackUp);
        setHitboxEnabled(attackDown, enabled && active == attackDown);
    }

    private void setHitboxEnabled(Rectangle hitbox, boolean enabled) {
        if (hitbox == null) return;
        hitbox.setDisable(!enabled);
        hitbox.setVisible(false);
        hitbox.setManaged(false);
        hitbox.setMouseTransparent(true);
    }

    private void setAnimState(AnimState newState, boolean forceReset) {
        if (newState == null) return;
        if (forceReset || animState != newState) {
            animState = newState;
            frameIndex = 0;
            updateViewport();
        }
    }

    private Image loadRequiredImage(String resourcePath) {
        if (resourcePath == null || resourcePath.isBlank()) {
            throw new IllegalArgumentException("resourcePath must be non-empty");
        }
        Image image = ImageCache.get(resourcePath);
        if (image == null) {
            throw new IllegalStateException("Missing mob sprite resource: " + resourcePath);
        }
        return image;
    }

    private static Effect createHurtFlashEffect() {
        Blend blend = new Blend();
        blend.setMode(BlendMode.SRC_ATOP);
        blend.setTopInput(new ColorInput(0, 0, FRAME_SIZE, FRAME_SIZE, Color.rgb(255, 40, 40, 0.60)));
        return blend;
    }

    private void updateHurtFlash(long now) {
        if (!hurtActive) {
            clearHurtFlashEffect();
            return;
        }

        if (now < hurtEndsAtNanos) {
            applyHurtFlashEffect();
            return;
        }

        hurtActive = false;
        hurtEndsAtNanos = 0L;
        clearHurtFlashEffect();
    }

    private void applyHurtFlashEffect() {
        if (spriteView != null && spriteView.getEffect() != hurtFlashEffect) {
            spriteView.setEffect(hurtFlashEffect);
        }
    }

    private void clearHurtFlashEffect() {
        if (spriteView != null && spriteView.getEffect() != hurtFlashEffect) {
            spriteView.setEffect(null);
        }
    }
}
