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

    // Player-like regen baseline (per real second).
    private static final double BASE_REGEN_PER_REAL_SECOND = 0.65;
    private static final double REGEN_PER_VIT = 0.07;
    // GameTimeManager uses ~24 in-game seconds per real second at timeScale=1.
    private static final double GAME_SECONDS_PER_REAL_SECOND = 24.0;

    private static final long IDLE_FRAME_DELAY_NANOS = 150_000_000L;
    private static final long RUN_FRAME_DELAY_NANOS = 90_000_000L;
    private static final long BASE_ATTACK_FRAME_DELAY_NANOS = 110_000_000L;
    private static final long HURT_FRAME_DELAY_NANOS = 90_000_000L;
    private static final long DEATH_FRAME_DELAY_NANOS = 90_000_000L;

    private static final long ATTACK_COOLDOWN_NANOS = 250_000_000L;

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

        setAttackHitboxesEnabled(false);
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
            updateViewport();
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
            animState = AnimState.HURT;
            updateAnimation(nowNanos);
            setAttackHitboxesEnabled(false);
            return;
        }
        if (hurtActive) {
            hurtActive = false;
            hurtEndsAtNanos = 0L;
        }

        if (isAttackActive(nowNanos)) {
            moving = false;
            animState = AnimState.ATTACK;
            updateAnimation(nowNanos);
            return;
        }

        // Attack finished.
        if (animState == AnimState.ATTACK) {
            setAttackHitboxesEnabled(false);
            attackDamageApplied = false;
            animState = AnimState.IDLE;
            frameIndex = 0;
            updateViewport();
        }

        boolean canChase = shouldChase(playerBoundsScene, playerState);

        if (canChase) {
            moveTowardTarget(dtNanos, playerBoundsScene);
            animState = moving ? AnimState.RUN : AnimState.IDLE;

            if (shouldStartAttack(nowNanos, playerBoundsScene, playerState)) {
                startAttack(nowNanos);
                tryApplyAttackDamage(playerBoundsScene, playerState);
            }
        } else {
            moving = false;
            setAttackHitboxesEnabled(false);
            animState = AnimState.IDLE;
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
        animState = AnimState.HURT;
        frameIndex = 0;
        lastAnimFrameNanos = nowNanos;
        updateViewport();

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

        // Weights sum to 100 (percent-like).
        // Slime: HP 24%, SP 21%, STR 24%, AGI 20%, VIT 10%, DEX 1%
        if (kind == Kind.SLIME) {
            if (r < 24) return KillBonusStat.HP;
            if (r < 24 + 21) return KillBonusStat.SP;
            if (r < 24 + 21 + 24) return KillBonusStat.STR;
            if (r < 24 + 21 + 24 + 20) return KillBonusStat.AGI;
            if (r < 24 + 21 + 24 + 20 + 10) return KillBonusStat.VIT;
            return KillBonusStat.DEX;
        }

        // Orc: HP 23%, SP 23%, STR 22%, AGI 15%, VIT 15%, DEX 2%
        if (r < 23) return KillBonusStat.HP;
        if (r < 23 + 23) return KillBonusStat.SP;
        if (r < 23 + 23 + 22) return KillBonusStat.STR;
        if (r < 23 + 23 + 22 + 15) return KillBonusStat.AGI;
        if (r < 23 + 23 + 22 + 15 + 15) return KillBonusStat.VIT;
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

        double regenPerGameSecond = (BASE_REGEN_PER_REAL_SECOND + (Math.max(0, data.vit) * REGEN_PER_VIT)) / GAME_SECONDS_PER_REAL_SECOND;
        if (regenPerGameSecond <= 0.0) {
            data.lastRegenTotalSeconds = nowSeconds;
            return;
        }

        double hpGain = regenPerGameSecond * elapsed;
        double spGain = regenPerGameSecond * elapsed;

        hpRegenCarry += hpGain;
        spRegenCarry += spGain;

        int maxHp = Math.max(0, data.maxHp);
        int maxSp = Math.max(0, data.maxSp);

        while (hpRegenCarry >= 1.0 && data.hp < maxHp) {
            data.hp += 1;
            hpRegenCarry -= 1.0;
        }
        while (spRegenCarry >= 1.0 && data.sp < maxSp) {
            data.sp += 1;
            spRegenCarry -= 1.0;
        }

        if (data.hp > maxHp) data.hp = maxHp;
        if (data.sp > maxSp) data.sp = maxSp;
        if (data.hp < 0) data.hp = 0;
        if (data.sp < 0) data.sp = 0;

        data.lastRegenTotalSeconds = nowSeconds;
    }

    private boolean shouldChase(Bounds playerBoundsScene, PlayerState playerState) {
        if (detectRange == null || playerBoundsScene == null || playerState == null) return false;

        Bounds detectBounds = detectRange.localToScene(detectRange.getBoundsInLocal());
        if (!detectBounds.intersects(playerBoundsScene)) {
            return false;
        }

        int targetVit = Math.max(0, playerState.getVit());
        int myStr = Math.max(0, data.str);
        return targetVit < myStr;
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
        double frameScale = Math.min(0.15, dtNanos / NANOS_PER_60FPS_FRAME);
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
        int agi = Math.max(0, data.agi);
        double base = kind == Kind.SLIME ? 2.2 : 2.6;
        double bonus = Math.min(2.0, agi * 0.35);
        return Math.max(0.5, base + bonus);
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
        if (active == null) return false;

        Bounds attackBounds = active.localToScene(active.getBoundsInLocal());
        return attackBounds.intersects(playerBoundsScene);
    }

    private void startAttack(long nowNanos) {
        long duration = (long) attackColumns * getAttackFrameDelayNanos();
        attackEndsAtNanos = nowNanos + duration;
        nextAttackAvailableAtNanos = attackEndsAtNanos + ATTACK_COOLDOWN_NANOS;
        attackDamageApplied = false;

        data.sp = Math.max(0, data.sp - 1);

        setAttackHitboxesEnabled(true);
        animState = AnimState.ATTACK;
        frameIndex = 0;
        lastAnimFrameNanos = nowNanos;
        updateViewport();
    }

    private void tryApplyAttackDamage(Bounds playerBoundsScene, PlayerState playerState) {
        if (playerBoundsScene == null || playerState == null) return;
        if (attackDamageApplied) return;
        Rectangle active = resolveActiveAttackHitbox();
        if (active == null) return;

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
        int myStr = Math.max(0, data.str);
        int targetVit = Math.max(0, target.getVit());
        // The chase rule ensures this is generally positive; still clamp for safety.
        return Math.max(1, myStr - targetVit);
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
        // Speed up slightly with AGI (similar feel to the player).
        double agiBonusScale = 1.0 + (Math.max(0.0, data.agi - 2) * 0.05);
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
        int frameSteps = (int) (steps % Math.max(1, frameCount));

        switch (animState) {
            case IDLE -> frameIndex = (frameIndex + frameSteps) % frameCount;
            case RUN -> {
                if (moving) {
                    frameIndex = (frameIndex + frameSteps) % frameCount;
                } else {
                    frameIndex = 0;
                }
            }
            case ATTACK -> frameIndex = (frameIndex + frameSteps) % frameCount;
            case HURT -> frameIndex = (frameIndex + frameSteps) % frameCount;
            case DEATH -> {
            }
        }

        updateViewport();
    }

    private void updateDeathAnimation(long nowNanos) {
        if (!deathStarted) {
            deathStarted = true;
            animState = AnimState.DEATH;
            frameIndex = 0;
            lastDeathFrameNanos = nowNanos;
            setAttackHitboxesEnabled(false);
            updateViewport();
            return;
        }

        if (nowNanos - lastDeathFrameNanos < DEATH_FRAME_DELAY_NANOS) {
            return;
        }

        lastDeathFrameNanos = nowNanos;
        frameIndex++;

        if (frameIndex >= deathColumns) {
            deathFinished = true;
            spriteView.setVisible(false);
            spriteView.setManaged(false);
            bodyHitbox.setDisable(true);
            return;
        }

        updateViewport();
    }

    private long getFrameDelay(AnimState state) {
        return switch (state) {
            case IDLE -> IDLE_FRAME_DELAY_NANOS;
            case RUN -> RUN_FRAME_DELAY_NANOS;
            case ATTACK -> getAttackFrameDelayNanos();
            case HURT -> HURT_FRAME_DELAY_NANOS;
            case DEATH -> DEATH_FRAME_DELAY_NANOS;
        };
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

    private void setAttackHitboxesEnabled(boolean enabled) {
        Rectangle active = resolveActiveAttackHitbox();
        setHitboxEnabled(attackLeft, enabled && active == attackLeft);
        setHitboxEnabled(attackRight, enabled && active == attackRight);
        setHitboxEnabled(attackUp, enabled && active == attackUp);
        setHitboxEnabled(attackDown, enabled && active == attackDown);
    }

    private void setHitboxEnabled(Rectangle hitbox, boolean enabled) {
        if (hitbox == null) return;
        hitbox.setDisable(!enabled);
        hitbox.setVisible(enabled);
        hitbox.setManaged(enabled);
        hitbox.setMouseTransparent(true);
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

