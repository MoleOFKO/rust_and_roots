package model;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.IntConsumer;
import javafx.animation.AnimationTimer;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Rectangle;
import game.GameTimeManager;
import util.ImageCache;
import util.TimeFlow;

public class Player {

    private static final double INV_SQRT_2 = 1.0 / Math.sqrt(2.0);
    private static final double NANOS_PER_60FPS_FRAME = 16_666_666.0;
    private static final int WALK_AGI_CAP = TimeFlow.PLAYER_WALK_AGI_CAP;
    private static final double RUN_SPEED_MULTIPLIER = 1.5;

    private static final int FRAME_SIZE = 64;

    private static final int WALK_COLUMNS = 6;
    private static final int RUN_COLUMNS = 8;
    private static final int IDLE_COLUMNS = 12;
    private static final int ATTACK_COLUMNS = 4;
    private static final int HURT_COLUMNS = 5;
    private static final int DEATH_COLUMNS = 7;

    private static final long WALK_FRAME_DELAY_NANOS = 150_000_000L;
    private static final long RUN_FRAME_DELAY_NANOS = 90_000_000L;
    private static final long IDLE_FRAME_DELAY_NANOS = 120_000_000L;
    private static final long BASE_ATTACK_FRAME_DELAY_NANOS = 110_000_000L;
    private static final long MIN_ATTACK_FRAME_DELAY_NANOS = (long) NANOS_PER_60FPS_FRAME;
    private static final long HURT_FRAME_DELAY_NANOS = 90_000_000L;
    private static final long DEATH_FRAME_DELAY_NANOS = 90_000_000L;

    private final Group node;
    private final ImageView sprite;
    private ImageView equipmentView;
    private final Rectangle hitbox;

    private final double baseSpeed;
    private final PlayerState state;
    private final Set<KeyCode> pressedKeys = EnumSet.noneOf(KeyCode.class);
    private final Map<EquipmentKind, EquipmentAnimationSet> equipmentAnimations = new EnumMap<>(EquipmentKind.class);

    private IntConsumer onHotbarSlotRequest;
    private Runnable onToggleHotbarRequest;
    private Runnable onAttackStarted;

    private double staminaDrainRemainder = 0.0;
    private List<Rectangle> walls = List.of();
    private AnimationTimer timer;

    private Rectangle detectionTarget;
    private Runnable onDetection;
    private Runnable onNoDetection;

    private int equippedItemId = -1;
    private EquipmentKind equippedKind = EquipmentKind.NONE;

    private final Image walkSpriteSheet = ImageCache.get("/image/human/human_Walk.png");
    private final Image runSpriteSheet = ImageCache.get("/image/human/human_Run.png");
    private final Image idleSpriteSheet = ImageCache.get("/image/human/human_Idle.png");
    private final Image deathSpriteSheet = ImageCache.get("/image/human/human_Death.png");
    private final Image hurtSpriteSheet = ImageCache.get("/image/human/human_Hurt.png");

    private enum AnimState {
        IDLE, WALK, RUN, ATTACK, HURT, DEATH
    }

    private enum EquipmentKind {
        NONE,
        AXE,
        PICKAXE,
        SWORD,
        FISHING_ROD,
        TOOL
    }

    private static final class EquipmentAnimationSet {
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
        private final Image humanAttackSheet;
        private final int humanAttackColumns;

        private EquipmentAnimationSet(
            Image idleSheet,
            int idleColumns,
            Image walkSheet,
            int walkColumns,
            Image runSheet,
            int runColumns,
            Image attackSheet,
            int attackColumns,
            Image hurtSheet,
            int hurtColumns,
            Image humanAttackSheet,
            int humanAttackColumns
        ) {
            this.idleSheet = idleSheet;
            this.idleColumns = Math.max(1, idleColumns);
            this.walkSheet = walkSheet;
            this.walkColumns = Math.max(1, walkColumns);
            this.runSheet = runSheet;
            this.runColumns = Math.max(1, runColumns);
            this.attackSheet = attackSheet;
            this.attackColumns = Math.max(1, attackColumns);
            this.hurtSheet = hurtSheet;
            this.hurtColumns = Math.max(1, hurtColumns);
            this.humanAttackSheet = humanAttackSheet;
            this.humanAttackColumns = Math.max(1, humanAttackColumns);
        }

        private boolean supportsAttack() {
            return idleSheet != null
                || walkSheet != null
                || runSheet != null
                || attackSheet != null
                || hurtSheet != null
                || humanAttackSheet != null;
        }
    }

    private AnimState animState = AnimState.IDLE;

    // Row mapping:
    // 0 = down, 1 = left, 2 = right, 3 = up
    private int facingRow = 0;
    private int frameIndex = 0;

    private long lastUpdateTime = 0L;
    private long lastAnimFrameTime = 0L;
    private long lastDeathFrameTime = 0L;
    private long simNowNanos = 0L;

    private boolean moving = false;
    private boolean deathStarted = false;
    private boolean deathFinished = false;
    private long attackEndsAtNanos = 0L;
    private long nextAttackAvailableAtNanos = 0L;
    private boolean hurtActive = false;
    private long hurtEndsAtNanos = 0L;

    public Player(
            PlayerState state,
            Group node,
            ImageView sprite,
            Rectangle hitbox,
            int frameWidth,
            int frameHeight,
            double speed,
            long frameDelayNanos
    ) {
        this.state = state;
        this.node = node;
        this.sprite = sprite;
        this.hitbox = hitbox;
        this.baseSpeed = speed;

        registerDefaultEquipmentAnimations();

        state.hpProperty().addListener((obs, oldValue, newValue) -> {
            if (oldValue == null || newValue == null) {
                return;
            }
            if (newValue.intValue() < oldValue.intValue() && newValue.intValue() > 0) {
                triggerHurt();
            }
        });

        this.sprite.setImage(idleSpriteSheet);
        this.sprite.setViewport(new Rectangle2D(0, 0, FRAME_SIZE, FRAME_SIZE));
        this.sprite.setPreserveRatio(false);
        this.sprite.setSmooth(false);
        updateViewport();
    }

    public Inventory getInventory() {
        return state.getInventory();
    }

    public void setWalls(List<Rectangle> walls) {
        this.walls = (walls == null) ? List.of() : walls;
    }

    public void setDetection(Rectangle target, Runnable onDetection, Runnable onNoDetection) {
        this.detectionTarget = target;
        this.onDetection = onDetection;
        this.onNoDetection = onNoDetection;
    }

    public void setOnToggleHotbarRequest(Runnable onToggleHotbarRequest) {
        this.onToggleHotbarRequest = onToggleHotbarRequest;
    }

    public void setOnHotbarSlotRequest(IntConsumer onHotbarSlotRequest) {
        this.onHotbarSlotRequest = onHotbarSlotRequest;
    }

    public void setEquipmentView(ImageView equipmentView) {
        this.equipmentView = equipmentView;
        if (this.equipmentView == null) {
            return;
        }

        this.equipmentView.setPreserveRatio(false);
        this.equipmentView.setSmooth(false);
        this.equipmentView.setMouseTransparent(true);
        this.equipmentView.setViewport(new Rectangle2D(0, 0, FRAME_SIZE, FRAME_SIZE));
        this.equipmentView.setVisible(false);
        this.equipmentView.setManaged(false);
        updateEquipmentViewport();
    }

    public void setEquippedItem(int itemId) {
        this.equippedItemId = itemId;
        this.equippedKind = resolveEquipmentKind(itemId);
        updateEquipmentViewport();
    }

    public int getEquippedItemId() {
        return equippedItemId;
    }

    public enum FacingDirection {
        DOWN,
        LEFT,
        RIGHT,
        UP
    }

    public FacingDirection getFacingDirection() {
        return switch (facingRow) {
            case 1 -> FacingDirection.LEFT;
            case 2 -> FacingDirection.RIGHT;
            case 3 -> FacingDirection.UP;
            default -> FacingDirection.DOWN;
        };
    }

    public void setOnAttackStarted(Runnable onAttackStarted) {
        this.onAttackStarted = onAttackStarted;
    }

    public boolean isRemovedFromWorld() {
        return deathFinished;
    }

    public void attachInput(Scene scene) {
        clearTransientInputState();

        scene.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();

            if (deathStarted || deathFinished || state.isDead() || state.isSleeping()) {
                return;
            }

            if (code == KeyCode.DIGIT1) {
                if (onHotbarSlotRequest != null) onHotbarSlotRequest.accept(0);
                return;
            }
            if (code == KeyCode.DIGIT2) {
                if (onHotbarSlotRequest != null) onHotbarSlotRequest.accept(1);
                return;
            }
            if (code == KeyCode.DIGIT3) {
                if (onHotbarSlotRequest != null) onHotbarSlotRequest.accept(2);
                return;
            }
            if (code == KeyCode.DIGIT4) {
                if (onHotbarSlotRequest != null) onHotbarSlotRequest.accept(3);
                return;
            }
            if (code == KeyCode.DIGIT5) {
                if (onHotbarSlotRequest != null) onHotbarSlotRequest.accept(4);
                return;
            }

            if (code == KeyCode.E) {
                if (!pressedKeys.contains(code)) {
                    pressedKeys.add(code);
                    if (onToggleHotbarRequest != null) {
                        onToggleHotbarRequest.run();
                    }
                }
                return;
            }

            if (isRunKey(code)) {
                if (!pressedKeys.contains(code)) {
                    pressedKeys.add(code);
                    if (!state.hasStaminaExhaustedLock() && state.getStamina() > 0) {
                        state.setRunToggled(!state.isRunToggled());
                    }
                }
                return;
            }

            if (isAttackKey(code)) {
                if (!pressedKeys.contains(code)) {
                    pressedKeys.add(code);
                    triggerAttack();
                }
                return;
            }

            if (isMovementKey(code)) {
                pressedKeys.add(code);
            }
        });

        scene.setOnKeyReleased(e -> {
            KeyCode code = e.getCode();

            if (code == KeyCode.E) {
                pressedKeys.remove(code);
                return;
            }

            if (!isMovementKey(code) && !isRunKey(code) && !isAttackKey(code)) {
                return;
            }

            pressedKeys.remove(code);
        });
    }

    public void start() {
        stop();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastUpdateTime == 0L) {
                    lastUpdateTime = now;
                    simNowNanos = 0L;
                    lastAnimFrameTime = 0L;
                    lastDeathFrameTime = 0L;
                    updateViewport();
                    return;
                }

                if (deathFinished) {
                    return;
                }

                long dtNanos = Math.max(0L, now - lastUpdateTime);
                lastUpdateTime = now;

                double dtSeconds = Math.max(0.0, dtNanos / 1_000_000_000.0);

                boolean runActive =
                    state != null
                        && state.isRunToggled()
                        && !state.hasStaminaExhaustedLock()
                        && state.getStamina() > 0
                        && !state.isDead()
                        && !state.isSleeping();

                // Keep the world dilation updated as close to input as possible so the player-run
                // effect applies immediately (BaseMapController also enforces a safe default).
                GameTimeManager time = GameTimeManager.get();
                double worldDilation = TimeFlow.worldDilationForPlayerRun(state == null ? 0 : state.getAgi(), runActive);
                time.setWorldDilation(worldDilation);

                int agiForPlayerFlow = state == null ? 0 : state.getAgi();
                if (!runActive) {
                    agiForPlayerFlow = Math.min(agiForPlayerFlow, WALK_AGI_CAP);
                }
                double playerFlow = TimeFlow.entityTimeFlowFromAgi(agiForPlayerFlow);
                double effective = Math.max(0.0, worldDilation) * Math.max(0.0, playerFlow);

                long dtSimNanos = scaleDeltaNanos(dtNanos, effective);
                simNowNanos = safeAddNanos(simNowNanos, dtSimNanos);

                // Passive regen is based on the world timeline (in-game seconds).
                double nowSeconds = time.totalSecondsProperty().get();
                if (state != null) {
                    state.applyRegenIfNeeded(nowSeconds);
                    state.applyDebuffsIfNeeded(nowSeconds);
                }

                if (state.isDead()) {
                    updateDeathAnimation(simNowNanos);
                    return;
                }

                // Stamina drain is expressed in world time (in-game seconds).
                double dtWorldSeconds = 0.0;
                if (time != null && !time.pausedProperty().get()) {
                    double timeScale = Math.max(0.0, time.timeScaleProperty().get());
                    dtWorldSeconds = dtSeconds * 24.0 * timeScale * Math.max(0.0, worldDilation);
                }

                updateMovement(dtWorldSeconds, dtSimNanos, simNowNanos);
                updateAnimation(simNowNanos);

                if (detectionTarget != null) {
                    if (isOverlapping(detectionTarget)) {
                        if (onDetection != null) onDetection.run();
                    } else {
                        if (onNoDetection != null) onNoDetection.run();
                    }
                }
            }
        };
        timer.start();
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }

        lastUpdateTime = 0L;
        lastAnimFrameTime = 0L;
        lastDeathFrameTime = 0L;
        simNowNanos = 0L;

        clearTransientInputState();
    }

    public void clearInputState() {
        clearTransientInputState();
        state.setRunToggled(false);
        state.setStaminaExhaustedLock(false);
        state.setStaminaMode(state.isSleeping() ? PlayerState.StaminaMode.SLEEP : PlayerState.StaminaMode.IDLE);

        if (!deathStarted && !deathFinished) {
            setAnimState(AnimState.IDLE, true);
        }
    }

    private void clearTransientInputState() {
        pressedKeys.clear();
        moving = false;
        attackEndsAtNanos = 0L;
        nextAttackAvailableAtNanos = 0L;
        hurtActive = false;
        hurtEndsAtNanos = 0L;

        if (!deathStarted && !deathFinished) {
            setAnimState(AnimState.IDLE, true);
        }
    }

    public boolean move(double dx, double dy) {
        boolean moved = false;

        if (dx != 0 && !collides(dx, 0)) {
            node.setLayoutX(node.getLayoutX() + dx);
            moved = true;
        }

        if (dy != 0 && !collides(0, dy)) {
            node.setLayoutY(node.getLayoutY() + dy);
            moved = true;
        }

        return moved;
    }

    private void updateMovement(double dtWorldSeconds, long dtSimNanos, long now) {
        if (state.isDead()) {
            moving = false;
            pressedKeys.clear();
            return;
        }

        if (state.isSleeping()) {
            moving = false;
            pressedKeys.clear();
            state.setStaminaMode(PlayerState.StaminaMode.SLEEP);
            setAnimState(AnimState.IDLE, false);
            return;
        }

        if (hurtActive && now < hurtEndsAtNanos) {
            moving = false;
            state.setStaminaMode(PlayerState.StaminaMode.IDLE);
            state.updateStaminaOverTime(dtWorldSeconds, PlayerState.StaminaMode.IDLE);
            setAnimState(AnimState.HURT, false);
            return;
        }
        if (hurtActive) {
            hurtActive = false;
            hurtEndsAtNanos = 0L;
        }

        if (isAttackActive(now)) {
            moving = false;

            PlayerState.StaminaMode mode = state.isRunToggled()
                ? PlayerState.StaminaMode.RUN
                : PlayerState.StaminaMode.ATTACK;
            state.setStaminaMode(mode);
            state.updateStaminaOverTime(dtWorldSeconds, mode);

            if (mode == PlayerState.StaminaMode.RUN && state.getStamina() <= 0) {
                state.setStaminaExhaustedLock(true);
                state.setRunToggled(false);
            }

            setAnimState(AnimState.ATTACK, false);
            return;
        }

        if (animState == AnimState.ATTACK) {
            setAnimState(AnimState.IDLE, true);
        }

        if (state.hasStaminaExhaustedLock()) {
            moving = false;
            state.setStaminaMode(PlayerState.StaminaMode.IDLE);
            state.updateStaminaOverTime(dtWorldSeconds, PlayerState.StaminaMode.IDLE);
            setAnimState(AnimState.IDLE, false);

            if (state.getStamina() > 0) {
                state.setStaminaExhaustedLock(false);
                state.setRunToggled(false);
            } else {
                return;
            }
        }

        // Spec: if SP (stamina) is 0, the player can't take any actions (move/attack/etc.).
        if (state.getStamina() <= 0) {
            moving = false;
            state.setStaminaMode(PlayerState.StaminaMode.IDLE);
            state.updateStaminaOverTime(dtWorldSeconds, PlayerState.StaminaMode.IDLE);
            setAnimState(AnimState.IDLE, false);
            return;
        }

        double inputX = 0.0;
        double inputY = 0.0;

        if (pressedKeys.contains(KeyCode.A)) inputX -= 1.0;
        if (pressedKeys.contains(KeyCode.D)) inputX += 1.0;
        if (pressedKeys.contains(KeyCode.W)) inputY -= 1.0;
        if (pressedKeys.contains(KeyCode.S)) inputY += 1.0;

        if (inputX != 0.0 || inputY != 0.0) {
            if (Math.abs(inputX) > Math.abs(inputY)) {
                facingRow = (inputX < 0.0) ? 1 : 2;
            } else {
                facingRow = (inputY < 0.0) ? 3 : 0;
            }
        }

        if (inputX == 0.0 && inputY == 0.0) {
            moving = false;

            if (state.isRunToggled()) {
                // Spec: while staying idle, "run" mode does not cost SP unless AGI is > 20.
                if (state.getAgi() > 20) {
                    state.setStaminaMode(PlayerState.StaminaMode.RUN);
                    state.updateStaminaOverTime(dtWorldSeconds, PlayerState.StaminaMode.RUN);
                    if (state.getStamina() <= 0) {
                        state.setStaminaExhaustedLock(true);
                        state.setRunToggled(false);
                    }
                } else {
                    state.setStaminaMode(PlayerState.StaminaMode.IDLE);
                    state.updateStaminaOverTime(dtWorldSeconds, PlayerState.StaminaMode.IDLE);
                }
            } else {
                state.setStaminaMode(PlayerState.StaminaMode.IDLE);
                state.updateStaminaOverTime(dtWorldSeconds, PlayerState.StaminaMode.IDLE);
            }

            setAnimState(AnimState.IDLE, false);
            return;
        }

        boolean wantsRun = state.isRunToggled();
        boolean isRunning = wantsRun && state.getStamina() > 0;
        double speed = getCurrentMovementSpeed(isRunning);
        double frames = dtSimNanos <= 0L ? 0.0 : (dtSimNanos / NANOS_PER_60FPS_FRAME);
        // Spec: cap the player's movement step to 15 frames @ 60fps for human reaction speed.
        frames = Math.min(15.0, frames);
        double frameScale = 0.15 * frames;

        double dx = inputX * speed * frameScale;
        double dy = inputY * speed * frameScale;

        if (inputX != 0.0 && inputY != 0.0) {
            dx *= INV_SQRT_2;
            dy *= INV_SQRT_2;
        }

        moving = move(dx, dy);

        if (moving) {
            if (isRunning) {
                state.setStaminaMode(PlayerState.StaminaMode.RUN);
                state.updateStaminaOverTime(dtWorldSeconds, PlayerState.StaminaMode.RUN);
                if (state.getStamina() <= 0) {
                    state.setStaminaExhaustedLock(true);
                    state.setRunToggled(false);
                    moving = false;
                    setAnimState(AnimState.IDLE, true);
                } else {
                    setAnimState(AnimState.RUN, false);
                }
            } else {
                state.setStaminaMode(PlayerState.StaminaMode.WALK);
                state.updateStaminaOverTime(dtWorldSeconds, PlayerState.StaminaMode.WALK);
                setAnimState(AnimState.WALK, false);
            }
        } else {
            if (state.isRunToggled()) {
                // Spec: if the player isn't actually moving, "run" mode does not cost SP unless AGI is > 20.
                if (state.getAgi() > 20) {
                    state.setStaminaMode(PlayerState.StaminaMode.RUN);
                    state.updateStaminaOverTime(dtWorldSeconds, PlayerState.StaminaMode.RUN);
                    if (state.getStamina() <= 0) {
                        state.setStaminaExhaustedLock(true);
                        state.setRunToggled(false);
                    }
                } else {
                    state.setStaminaMode(PlayerState.StaminaMode.IDLE);
                    state.updateStaminaOverTime(dtWorldSeconds, PlayerState.StaminaMode.IDLE);
                }
            } else {
                state.setStaminaMode(PlayerState.StaminaMode.IDLE);
                state.updateStaminaOverTime(dtWorldSeconds, PlayerState.StaminaMode.IDLE);
            }
            setAnimState(AnimState.IDLE, false);
        }
    }

    private void updateAnimation(long now) {
        if (animState == AnimState.DEATH) {
            return;
        }

        long delay = getFrameDelay(animState);
        long elapsed = now - lastAnimFrameTime;
        if (elapsed < delay) {
            return;
        }

        long steps = elapsed / Math.max(1L, delay);
        if (steps <= 0L) {
            return;
        }
        // Cap catch-up after long stalls so we don't fast-forward entire animations in one frame.
        steps = Math.min(steps, 5L);
        lastAnimFrameTime += steps * delay;

        int frameCount = getFrameCount(animState);
        int frameSteps = (int) (steps % Math.max(1, frameCount));

        switch (animState) {
            case IDLE -> frameIndex = (frameIndex + frameSteps) % frameCount;
            case WALK, RUN -> {
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

    private void updateDeathAnimation(long now) {
        if (!deathStarted) {
            deathStarted = true;
            moving = false;
            pressedKeys.clear();
            animState = AnimState.DEATH;
            frameIndex = 0;
            lastDeathFrameTime = now;
            updateViewport();
            return;
        }

        if (now - lastDeathFrameTime < DEATH_FRAME_DELAY_NANOS) {
            return;
        }

        lastDeathFrameTime = now;
        frameIndex++;

        if (frameIndex >= DEATH_COLUMNS) {
            deathFinished = true;
            node.setVisible(false);
            node.setDisable(true);
            hitbox.setVisible(false);
            if (equipmentView != null) {
                equipmentView.setVisible(false);
            }
            return;
        }

        updateViewport();
    }

    private void setAnimState(AnimState newState, boolean forceReset) {
        if (forceReset || animState != newState) {
            animState = newState;
            frameIndex = 0;
            updateViewport();
        }
    }

    private double getCurrentMovementSpeed(boolean isRunning) {
        double currentSpeed = baseSpeed;
        if (isRunning) {
            currentSpeed *= RUN_SPEED_MULTIPLIER;
        }
        double staminaRatio = state.getMaxStamina() <= 0
            ? 0.0
            : (double) state.getStamina() / state.getMaxStamina();

        if (staminaRatio < 0.30) {
            currentSpeed *= 0.65;
        }
        return currentSpeed;
    }

    private void updateViewport() {
        Image sheet = getSheet(animState);
        int frameCount = getFrameCount(animState);

        int safeFrame = Math.max(0, Math.min(frameIndex, frameCount - 1));
        int x = safeFrame * FRAME_SIZE;
        int y = facingRow * FRAME_SIZE;

        sprite.setImage(sheet);
        sprite.setViewport(new Rectangle2D(x, y, FRAME_SIZE, FRAME_SIZE));
        updateEquipmentViewport();
    }

    private void updateEquipmentViewport() {
        if (equipmentView == null) {
            return;
        }

        EquipmentAnimationSet animationSet = equipmentAnimations.get(equippedKind);
        if (animationSet == null || animState == AnimState.DEATH) {
            equipmentView.setImage(null);
            equipmentView.setVisible(false);
            equipmentView.setManaged(false);
            return;
        }

        Image equipSheet;
        int columns;
        int equipFrame;

        if (animState == AnimState.HURT) {
            if (animationSet.hurtSheet == null) {
                equipmentView.setImage(null);
                equipmentView.setVisible(false);
                equipmentView.setManaged(false);
                return;
            }

            equipSheet = animationSet.hurtSheet;
            columns = animationSet.hurtColumns;
            equipFrame = frameIndex % columns;
        } else
        if (animState == AnimState.ATTACK && animationSet.attackSheet != null) {
            equipSheet = animationSet.attackSheet;
            columns = animationSet.attackColumns;
            equipFrame = frameIndex % columns;
        } else if (animState == AnimState.RUN && animationSet.runSheet != null) {
            equipSheet = animationSet.runSheet;
            columns = animationSet.runColumns;
            equipFrame = frameIndex % columns;
        } else if (animState == AnimState.WALK && animationSet.walkSheet != null) {
            equipSheet = animationSet.walkSheet;
            columns = animationSet.walkColumns;
            equipFrame = frameIndex % columns;
        } else if (animState == AnimState.IDLE && animationSet.idleSheet != null) {
            equipSheet = animationSet.idleSheet;
            columns = animationSet.idleColumns;
            equipFrame = frameIndex % columns;
        } else if (animationSet.walkSheet != null) {
            equipSheet = animationSet.walkSheet;
            columns = animationSet.walkColumns;
            equipFrame = frameIndex % columns;
        } else if (animationSet.idleSheet != null) {
            equipSheet = animationSet.idleSheet;
            columns = animationSet.idleColumns;
            equipFrame = frameIndex % columns;
        } else {
            equipmentView.setImage(null);
            equipmentView.setVisible(false);
            equipmentView.setManaged(false);
            return;
        }

        int x = equipFrame * FRAME_SIZE;
        int y = facingRow * FRAME_SIZE;

        equipmentView.setImage(equipSheet);
        equipmentView.setViewport(new Rectangle2D(x, y, FRAME_SIZE, FRAME_SIZE));
        equipmentView.setVisible(true);
        equipmentView.setManaged(true);
        equipmentView.toFront();
    }

    private Image getSheet(AnimState state) {
        return switch (state) {
            case IDLE -> idleSpriteSheet;
            case WALK -> walkSpriteSheet;
            case RUN -> runSpriteSheet;
            case ATTACK -> getHumanAttackSheet();
            case HURT -> hurtSpriteSheet;
            case DEATH -> deathSpriteSheet;
        };
    }

    private int getFrameCount(AnimState state) {
        return switch (state) {
            case IDLE -> IDLE_COLUMNS;
            case WALK -> WALK_COLUMNS;
            case RUN -> RUN_COLUMNS;
            case ATTACK -> getHumanAttackColumns();
            case HURT -> HURT_COLUMNS;
            case DEATH -> DEATH_COLUMNS;
        };
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

    private boolean collides(double dx, double dy) {
        Bounds playerBounds = toMovementSpace(hitbox.localToScene(hitbox.getBoundsInLocal()));

        Bounds movedBounds = new BoundingBox(
            playerBounds.getMinX() + dx,
            playerBounds.getMinY() + dy,
            playerBounds.getWidth(),
            playerBounds.getHeight()
        );

        for (Rectangle wall : walls) {
            if (wall == null || wall.isDisable()) {
                continue;
            }
            Bounds wallBounds = toMovementSpace(wall.localToScene(wall.getBoundsInLocal()));
            if (movedBounds.intersects(wallBounds)) {
                return true;
            }
        }

        return false;
    }

    private Bounds toMovementSpace(Bounds sceneBounds) {
        if (node.getParent() == null) {
            return sceneBounds;
        }
        return node.getParent().sceneToLocal(sceneBounds);
    }

    private boolean isMovementKey(KeyCode code) {
        return code == KeyCode.W
            || code == KeyCode.A
            || code == KeyCode.S
            || code == KeyCode.D;
    }

    private boolean isRunKey(KeyCode code) {
        return code == KeyCode.SHIFT;
    }

    private boolean isAttackKey(KeyCode code) {
        return code == KeyCode.SPACE;
    }

    private boolean isOverlapping(Rectangle target) {
        Bounds playerBounds = hitbox.localToScene(hitbox.getBoundsInLocal());
        Bounds targetBounds = target.localToScene(target.getBoundsInLocal());
        return playerBounds.intersects(targetBounds);
    }

    private void registerDefaultEquipmentAnimations() {
        registerEquipmentAnimation(
            EquipmentKind.AXE,
            "/image/human/Axe_Idle.png",
            IDLE_COLUMNS,
            "/image/human/axe_walk.png",
            WALK_COLUMNS,
            "/image/human/axe_run.png",
            RUN_COLUMNS,
            "/image/human/wood_cutting_action.png",
            ATTACK_COLUMNS,
            "/image/human/axe_hurt.png",
            HURT_COLUMNS,
            "/image/human/human_wood_cutting_action.png",
            ATTACK_COLUMNS
        );
        registerEquipmentAnimation(
            EquipmentKind.PICKAXE,
            "/image/human/pickaxe_Idle.png",
            IDLE_COLUMNS,
            "/image/human/pickaxe_walk.png",
            WALK_COLUMNS,
            "/image/human/pickaxe_run.png",
            RUN_COLUMNS,
            "/image/human/pickaxe_action.png",
            ATTACK_COLUMNS,
            "/image/human/pickaxe_hurt.png",
            HURT_COLUMNS,
            "/image/human/human_pickaxe_action.png",
            ATTACK_COLUMNS
        );
        registerEquipmentAnimation(
            EquipmentKind.SWORD,
            "/image/human/sword_Idle.png",
            IDLE_COLUMNS,
            "/image/human/sword_walk.png",
            WALK_COLUMNS,
            "/image/human/sword_run.png",
            RUN_COLUMNS,
            "/image/human/sword_attack_action.png",
            8,
            "/image/human/sword_hurt.png",
            HURT_COLUMNS,
            "/image/human/human_attack.png",
            8
        );
    }

    private void registerEquipmentAnimation(
        EquipmentKind kind,
        String idleResourcePath,
        int idleColumns,
        String walkResourcePath,
        int walkColumns,
        String runResourcePath,
        int runColumns,
        String attackResourcePath,
        int attackColumns,
        String hurtResourcePath,
        int hurtColumns,
        String humanAttackResourcePath,
        int humanAttackColumns
    ) {
        if (kind == null || kind == EquipmentKind.NONE) {
            return;
        }

        Image idleSheet = loadOptionalImage(idleResourcePath);
        Image walkSheet = loadOptionalImage(walkResourcePath);
        Image runSheet = loadOptionalImage(runResourcePath);
        Image attackSheet = loadOptionalImage(attackResourcePath);
        Image hurtSheet = loadOptionalImage(hurtResourcePath);
        Image humanAttackSheet = loadOptionalImage(humanAttackResourcePath);

        if (idleSheet == null && walkSheet == null && runSheet == null && attackSheet == null && hurtSheet == null && humanAttackSheet == null) {
            return;
        }

        equipmentAnimations.put(
            kind,
            new EquipmentAnimationSet(
                idleSheet,
                idleColumns,
                walkSheet,
                walkColumns,
                runSheet,
                runColumns,
                attackSheet,
                attackColumns,
                hurtSheet,
                hurtColumns,
                humanAttackSheet,
                humanAttackColumns
            )
        );
    }

    private Image loadOptionalImage(String resourcePath) {
        if (resourcePath == null || resourcePath.isBlank()) {
            return null;
        }
        return ImageCache.get(resourcePath);
    }

    private void triggerAttack() {
        EquipmentAnimationSet animationSet = equipmentAnimations.get(equippedKind);
        long now = simNowNanos;
        if (animationSet == null || !animationSet.supportsAttack() || deathStarted || deathFinished) {
            return;
        }

        int staminaCost = state.getAttackStaminaCost();
        if (state.getStamina() < staminaCost) {
            return;
        }

        if (isAttackActive(now) || now < nextAttackAvailableAtNanos) {
            return;
        }

        state.consumeStamina(staminaCost);
        if (state.getStamina() <= 0) {
            state.setStaminaExhaustedLock(true);
            state.setRunToggled(false);
        }

        moving = false;
        long attackDuration = getAttackDurationNanos(animationSet);
        attackEndsAtNanos = now + attackDuration;
        nextAttackAvailableAtNanos = attackEndsAtNanos;
        setAnimState(AnimState.ATTACK, true);
        if (onAttackStarted != null) {
            onAttackStarted.run();
        }
    }

    private long getAttackDurationNanos(EquipmentAnimationSet animationSet) {
        int columns = getHumanAttackColumns(animationSet);
        return (long) columns * getAttackFrameDelayNanos();
    }

    private boolean isAttackActive(long now) {
        return attackEndsAtNanos > 0L && now < attackEndsAtNanos;
    }

    private Image getHumanAttackSheet() {
        EquipmentAnimationSet animationSet = equipmentAnimations.get(equippedKind);
        if (animationSet != null && animationSet.humanAttackSheet != null) {
            return animationSet.humanAttackSheet;
        }
        return idleSpriteSheet;
    }

    private int getHumanAttackColumns() {
        return getHumanAttackColumns(equipmentAnimations.get(equippedKind));
    }

    private int getHumanAttackColumns(EquipmentAnimationSet animationSet) {
        if (animationSet != null && animationSet.humanAttackSheet != null) {
            return animationSet.humanAttackColumns;
        }
        if (animationSet != null && animationSet.attackSheet != null) {
            return animationSet.attackColumns;
        }
        if (animationSet != null && animationSet.walkSheet != null) {
            return animationSet.walkColumns;
        }
        return ATTACK_COLUMNS;
    }

    private long getAttackFrameDelayNanos() {
        return Math.max(MIN_ATTACK_FRAME_DELAY_NANOS, BASE_ATTACK_FRAME_DELAY_NANOS);
    }

    private void triggerHurt() {
        if (state == null || state.isDead() || deathStarted || deathFinished) {
            return;
        }

        long now = simNowNanos;
        hurtActive = true;
        hurtEndsAtNanos = now + (HURT_COLUMNS * HURT_FRAME_DELAY_NANOS);
        setAnimState(AnimState.HURT, true);
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

    private EquipmentKind resolveEquipmentKind(int itemId) {
        if (itemId <= 0 || !ItemDatabase.isEquipmentItem(itemId)) {
            return EquipmentKind.NONE;
        }

        String type = ItemDatabase.getItemType(itemId);
        String name = ItemDatabase.getItemName(itemId);
        String lookup = ((type == null ? "" : type) + " " + (name == null ? "" : name)).toLowerCase();

        if (lookup.contains("pickaxe") || lookup.contains("pick axe")) {
            return EquipmentKind.PICKAXE;
        }
        if (lookup.contains("axe")) {
            return EquipmentKind.AXE;
        }
        if (lookup.contains("sword")) {
            return EquipmentKind.SWORD;
        }
        if (lookup.contains("fishing rod") || lookup.contains("rod")) {
            return EquipmentKind.FISHING_ROD;
        }
        if (lookup.contains("tool") || lookup.contains("weapon")) {
            return EquipmentKind.TOOL;
        }
        return EquipmentKind.NONE;
    }
}
