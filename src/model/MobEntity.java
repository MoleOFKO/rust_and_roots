package model;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

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
        IDLE, RUN, ATTACK, HURT, DEATH
    }

    private static final int FRAME_SIZE = 64;
    private static final double INV_SQRT_2 = 1.0 / Math.sqrt(2.0);
    private static final double NANOS_PER_60FPS_FRAME = 1_000_000_000.0 / 60.0;
    private static final double NORMAL_AGI = 5.0;
    private static final double BASE_MOVEMENT_SPEED = 5.0;
    private static final double RUN_SPEED_BONUS_RATIO = 0.20;
    private static final double MIN_RUN_SPEED_BONUS = 0.75;
    private static final double MAX_RUN_SPEED_BONUS = 1.10;
    private static final double ATTACK_SPEED_PER_AGI = 0.045;

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
    private static final long RUN_FRAME_DELAY_NANOS = 90_000_000L;
    private static final long BASE_ATTACK_FRAME_DELAY_NANOS = 110_000_000L;
    private static final long HURT_FRAME_DELAY_NANOS = 90_000_000L;
    private static final long DEATH_FRAME_DELAY_NANOS = 90_000_000L;

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

    private boolean moving = false;
    private boolean deathStarted = false;
    private boolean deathFinished = false;
    private boolean hurtActive = false;
    private long hurtEndsAtNanos = 0L;
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
        if (deathFinished) {
            updateDropsExpiry(nowSeconds);
            return;
        }

        if (lastUpdateNanos == 0L) {
            lastUpdateNanos = nowNanos;
            lastAnimFrameNanos = nowNanos;
            lastDeathFrameNanos = nowNanos;
            syncNodePositionToStateIfNeeded();
            updateDropsExpiry(nowSeconds);
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

        // Persist position each tick.
        data.layoutX = node.getLayoutX();
        data.layoutY = node.getLayoutY();

        updateDropsExpiry(nowSeconds);

        if (data.dead) {
            updateDeathAnimation(nowNanos);
            return;
        }

        applyDailyGrowthIfNeeded(currentDay);
        applyRegenIfNeeded(nowSeconds);

        if (hurtActive && nowNanos < hurtEndsAtNanos) {
            moving = false;
            syncAttackHitboxesEnabled(false);
            setAnimState(AnimState.HURT, false);
            updateAnimation(nowNanos);
            return;
        }
        if (hurtActive) {
            hurtActive = false;
            hurtEndsAtNanos = 0L;
        }

        if (isAttackActive(nowNanos)) {
            moving = false;
            syncAttackHitboxesEnabled(true);
            setAnimState(AnimState.ATTACK, false);
            tryApplyAttackDamage(playerBoundsScene, playerState);
            updateAnimation(nowNanos);
            return;
        }

        // Attack finished.
        if (animState == AnimState.ATTACK) {
            attackDamageApplied = false;
            setAnimState(AnimState.IDLE, true);
        }

        boolean canChase = shouldChase(playerBoundsScene, playerState);

        if (canChase) {
            moveTowardTarget(dtNanos, playerBoundsScene);
            syncAttackHitboxesEnabled(true);
            setAnimState(moving ? AnimState.RUN : AnimState.IDLE, false);

            if (shouldStartAttack(nowNanos, playerBoundsScene, playerState)) {
                startAttack(nowNanos);
                tryApplyAttackDamage(playerBoundsScene, playerState);
            }
        } else {
            moving = false;
            syncAttackHitboxesEnabled(true);
            setAnimState(AnimState.IDLE, false);
        }

        updateAnimation(nowNanos);
    }

    public boolean applyPlayerDamage(int amount, long nowNanos) {
        int dmg = Math.max(0, amount);
        if (dmg <= 0 || data.dead) return false;

        int hpBefore = Math.max(0, data.hp);
        int hpAfter = Math.max(0, hpBefore - dmg);
        data.hp = hpAfter;

        hurtActive = true;
        hurtEndsAtNanos = nowNanos + (long) hurtColumns * HURT_FRAME_DELAY_NANOS;
        setAnimState(AnimState.HURT, true);
        lastAnimFrameNanos = nowNanos;

        if (hpAfter <= 0) {
            data.dead = true;
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

        if (kind == Kind.SLIME) {
            data.str = Math.max(0, data.str) + delta;
            data.agi = Math.max(0, data.agi) + delta;
            data.maxSp = Math.max(0, data.maxSp) + delta;
            data.sp = Math.min(data.maxSp, Math.max(0, data.sp) + delta);
        } else {
            data.str = Math.max(0, data.str) + delta;
            data.vit = Math.max(0, data.vit) + delta;
            data.maxHp = Math.max(0, data.maxHp) + delta;
            data.hp = Math.min(data.maxHp, Math.max(0, data.hp) + delta);
        }
    }

    private void applyRegenIfNeeded(double nowSeconds) {
        double now = Math.max(0.0, nowSeconds);

        if (data.maxHp <= 0 && data.maxSp <= 0) {
            data.lastRegenTotalSeconds = now;
            hpRegenCarry = 0.0;
            spRegenCarry = 0.0;
            return;
        }

        if (data.lastRegenTotalSeconds < 0 || data.lastRegenTotalSeconds > now) {
            data.lastRegenTotalSeconds = now;
            hpRegenCarry = 0.0;
            spRegenCarry = 0.0;
            return;
        }

        int vitValue = Math.max(0, data.vit);
        int vitBonus = Math.max(0, vitValue - 1);

        double amountMultiplier = 1.0 + (vitBonus * 0.05);
        double speedMultiplier = 1.0 + (vitBonus * 0.02);
        double intervalSeconds = 60.0 / Math.max(1.0e-6, speedMultiplier);

        double elapsed = now - data.lastRegenTotalSeconds;
        if (elapsed < intervalSeconds) {
            return;
        }

        long ticks = (long) Math.floor(elapsed / intervalSeconds);
        if (ticks <= 0L) {
            return;
        }

        int maxHp = Math.max(0, data.maxHp);
        int maxSp = Math.max(0, data.maxSp);

        if (maxHp > 0 && data.hp < maxHp) {
            hpRegenCarry += (maxHp * 0.01 * amountMultiplier) * ticks;
            while (hpRegenCarry >= 1.0 && data.hp < maxHp) {
                data.hp += 1;
                hpRegenCarry -= 1.0;
            }
        } else {
            hpRegenCarry = 0.0;
        }

        if (maxSp > 0 && data.sp < maxSp) {
            spRegenCarry += (maxSp * 0.01 * amountMultiplier) * ticks;
            while (spRegenCarry >= 1.0 && data.sp < maxSp) {
                data.sp += 1;
                spRegenCarry -= 1.0;
            }
        } else {
            spRegenCarry = 0.0;
        }

        if (data.hp > maxHp) data.hp = maxHp;
        if (data.sp > maxSp) data.sp = maxSp;
        if (data.hp < 0) data.hp = 0;
        if (data.sp < 0) data.sp = 0;

        data.lastRegenTotalSeconds += ticks * intervalSeconds;
    }

    private boolean shouldChase(Bounds playerBoundsScene, PlayerState playerState) {
        if (detectRange == null || playerBoundsScene == null || playerState == null) return false;
        if (playerState.isDead()) return false;
        if (data.sp <= 0) return false;

        Bounds detectBounds = detectRange.localToScene(detectRange.getBoundsInLocal());
        return detectBounds.intersects(playerBoundsScene);
    }

    private void moveTowardTarget(long dtNanos, Bounds playerBoundsScene) {
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

        double speed = getMovementSpeed();
        double dtSeconds = Math.min(0.15, Math.max(0.0, dtNanos / 1_000_000_000.0));
        double movementScale = dtSeconds * 9.0;
        double dx = dirX * speed * movementScale;
        double dy = dirY * speed * movementScale;

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
        double agiRatio = Math.max(0.0, data.agi) / NORMAL_AGI;
        double agiModifier = 0.6 + (0.4 * agiRatio);
        double currentSpeed = BASE_MOVEMENT_SPEED * agiModifier;
        return Math.max(0.0, currentSpeed);
    }

    private double getRunSpeedBonus() {
        return Math.max(MIN_RUN_SPEED_BONUS, Math.min(MAX_RUN_SPEED_BONUS, BASE_MOVEMENT_SPEED * RUN_SPEED_BONUS_RATIO));
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

        data.sp = Math.max(0, data.sp - 1);

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
        int dmg = computeDamageTo(playerState);
        playerState.damageHp(dmg);
        attackDamageApplied = true;

        if (wasAlive && playerState.isDead()) {
            applyKillBonus(playerState);
        }
    }

    private int computeDamageTo(PlayerState target) {
        if (target == null) return 0;
        double attackDamage = CombatMath.computeAttackDamage(data.str, 0);
        double reduction = CombatMath.computeDamageReduction(target.getVit());
        return CombatMath.computeNetDamage(attackDamage, reduction);
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
        // Same attack-speed curve as the player (speeds up above NORMAL_AGI).
        double agiBonusScale = 1.0 + (Math.max(0.0, data.agi - NORMAL_AGI) * ATTACK_SPEED_PER_AGI);
        double speedScale = 1.0 / agiBonusScale;
        long computed = Math.max(1L, (long) Math.floor(BASE_ATTACK_FRAME_DELAY_NANOS * speedScale));
        return Math.max((long) NANOS_PER_60FPS_FRAME, computed);
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
            case RUN -> {
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
            case RUN -> getMovementFrameDelayNanos(RUN_FRAME_DELAY_NANOS);
            case ATTACK -> getAttackFrameDelayNanos();
            case HURT -> HURT_FRAME_DELAY_NANOS;
            case DEATH -> DEATH_FRAME_DELAY_NANOS;
        };
    }

    private long getMovementFrameDelayNanos(long baseDelayNanos) {
        double agiRatio = Math.max(0.0, data.agi) / NORMAL_AGI;
        double agiModifier = 0.6 + (0.4 * agiRatio);
        double speedScale = 1.0 / Math.max(0.1, agiModifier);
        long computed = Math.max(1L, (long) Math.floor(baseDelayNanos * speedScale));
        return Math.max((long) NANOS_PER_60FPS_FRAME, computed);
    }

    private int getFrameCount(AnimState state) {
        return switch (state) {
            case IDLE -> idleColumns;
            case RUN -> runColumns;
            case ATTACK -> attackColumns;
            case HURT -> hurtColumns;
            case DEATH -> deathColumns;
        };
    }

    private void updateViewport() {
        Image sheet = switch (animState) {
            case IDLE -> idleSheet;
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
        var url = getClass().getResource(resourcePath);
        if (url == null) {
            throw new IllegalStateException("Missing mob sprite resource: " + resourcePath);
        }
        return new Image(url.toExternalForm());
    }
}
