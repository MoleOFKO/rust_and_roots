package model;

import java.util.EnumSet;
import java.util.List;
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

public class Player {

    private static final double INV_SQRT_2 = 1.0 / Math.sqrt(2.0);
    private static final double NANOS_PER_60FPS_FRAME = 16_666_666.0;
    private static final double NORMAL_AGI = 5.0;
    private static final double RUN_MULTIPLIER = 1.35;
    private static final double MIN_AGI_SPEED_MULTIPLIER = 0.80;
    private static final double MAX_AGI_SPEED_MULTIPLIER = 1.60;

    private static final int FRAME_SIZE = 64;

    private static final int WALK_COLUMNS = 6;
    private static final int RUN_COLUMNS = 8;
    private static final int IDLE_COLUMNS = 12;
    private static final int DEATH_COLUMNS = 7;

    private static final long WALK_FRAME_DELAY_NANOS = 150_000_000L;
    private static final long RUN_FRAME_DELAY_NANOS = 90_000_000L;
    private static final long IDLE_FRAME_DELAY_NANOS = 120_000_000L;
    private static final long DEATH_FRAME_DELAY_NANOS = 90_000_000L;

    private final Group node;
    private final ImageView sprite;
    private final Rectangle hitbox;

    private final double baseSpeed;
    private final PlayerState state;
    private final Set<KeyCode> pressedKeys = EnumSet.noneOf(KeyCode.class);

    private IntConsumer onHotbarSlotRequest;
    private Runnable onToggleHotbarRequest;

    private double staminaDrainRemainder = 0.0;
    private List<Rectangle> walls = List.of();
    private AnimationTimer timer;

    private Rectangle detectionTarget;
    private Runnable onDetection;
    private Runnable onNoDetection;

    private final Image walkSpriteSheet = new Image(
        getClass().getResource("/image/human/human_Walk.png").toExternalForm()
    );

    private final Image runSpriteSheet = new Image(
        getClass().getResource("/image/human/human_Run.png").toExternalForm()
    );

    private final Image idleSpriteSheet = new Image(
        getClass().getResource("/image/human/human_Idle.png").toExternalForm()
    );

    private final Image deathSpriteSheet = new Image(
        getClass().getResource("/image/human/human_Death.png").toExternalForm()
    );

    private enum AnimState {
        IDLE, WALK, RUN, DEATH
    }

    private AnimState animState = AnimState.IDLE;

    // Row mapping:
    // 0 = down, 1 = left, 2 = right, 3 = up
    private int facingRow = 0;
    private int frameIndex = 0;

    private long lastUpdateTime = 0L;
    private long lastAnimFrameTime = 0L;
    private long lastDeathFrameTime = 0L;

    private boolean moving = false;
    private boolean deathStarted = false;
    private boolean deathFinished = false;

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

    public boolean isRemovedFromWorld() {
        return deathFinished;
    }

    public void attachInput(Scene scene) {
        clearInputState();

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

            if (isMovementKey(code) || isRunKey(code)) {
                pressedKeys.add(code);
            }
        });

        scene.setOnKeyReleased(e -> {
            KeyCode code = e.getCode();

            if (code == KeyCode.E) {
                pressedKeys.remove(code);
                return;
            }

            if (!isMovementKey(code) && !isRunKey(code)) {
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
                    lastAnimFrameTime = now;
                    lastDeathFrameTime = now;
                    updateViewport();
                    return;
                }

                if (deathFinished) {
                    return;
                }

                long dtNanos = now - lastUpdateTime;
                lastUpdateTime = now;

                if (state.isDead()) {
                    updateDeathAnimation(now);
                    return;
                }

                updateMovement(dtNanos);
                updateAnimation(now);

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

        clearInputState();
    }

    public void clearInputState() {
        pressedKeys.clear();
        moving = false;
        state.setStaminaMode(state.isSleeping() ? PlayerState.StaminaMode.SLEEP : PlayerState.StaminaMode.IDLE);

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

    private void updateMovement(long dtNanos) {
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

        double dtSeconds = Math.max(0.0, dtNanos / 1_000_000_000.0);
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
            state.setStaminaMode(PlayerState.StaminaMode.IDLE);
            state.updateStaminaOverTime(dtSeconds, PlayerState.StaminaMode.IDLE);
            setAnimState(AnimState.IDLE, false);
            return;
        }

        boolean wantsRun = pressedKeys.contains(KeyCode.SHIFT);
        boolean isRunning = wantsRun && state.getStamina() > 0;
        double speed = getCurrentMovementSpeed(isRunning);
        double frameScale = Math.min(0.15, dtNanos / NANOS_PER_60FPS_FRAME);

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
                state.updateStaminaOverTime(dtSeconds, PlayerState.StaminaMode.RUN);
                setAnimState(AnimState.RUN, false);
            } else {
                state.setStaminaMode(PlayerState.StaminaMode.WALK);
                state.updateStaminaOverTime(dtSeconds, PlayerState.StaminaMode.WALK);
                setAnimState(AnimState.WALK, false);
            }
        } else {
            state.setStaminaMode(PlayerState.StaminaMode.IDLE);
            state.updateStaminaOverTime(dtSeconds, PlayerState.StaminaMode.IDLE);
            setAnimState(AnimState.IDLE, false);
        }
    }

    private void updateAnimation(long now) {
        if (animState == AnimState.DEATH) {
            return;
        }

        long delay = getFrameDelay(animState);
        if (now - lastAnimFrameTime < delay) {
            return;
        }

        lastAnimFrameTime = now;

        int frameCount = getFrameCount(animState);

        switch (animState) {
            case IDLE -> frameIndex = (frameIndex + 1) % frameCount;
            case WALK, RUN -> {
                if (moving) {
                    frameIndex = (frameIndex + 1) % frameCount;
                } else {
                    frameIndex = 0;
                }
            }
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

    private double getCurrentMovementSpeed(boolean running) {
        double agiRatio = state.getAgi() / NORMAL_AGI;
        double agiModifier = 0.6 + (0.4 * agiRatio);
        agiModifier = Math.max(MIN_AGI_SPEED_MULTIPLIER, Math.min(MAX_AGI_SPEED_MULTIPLIER, agiModifier));

        double currentSpeed = baseSpeed * agiModifier;

        double staminaRatio = state.getMaxStamina() <= 0
            ? 0.0
            : (double) state.getStamina() / state.getMaxStamina();

        if (staminaRatio < 0.30) {
            currentSpeed *= 0.65;
        }

        if (running) {
            currentSpeed *= RUN_MULTIPLIER;
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
    }

    private Image getSheet(AnimState state) {
        return switch (state) {
            case IDLE -> idleSpriteSheet;
            case WALK -> walkSpriteSheet;
            case RUN -> runSpriteSheet;
            case DEATH -> deathSpriteSheet;
        };
    }

    private int getFrameCount(AnimState state) {
        return switch (state) {
            case IDLE -> IDLE_COLUMNS;
            case WALK -> WALK_COLUMNS;
            case RUN -> RUN_COLUMNS;
            case DEATH -> DEATH_COLUMNS;
        };
    }

    private long getFrameDelay(AnimState state) {
        return switch (state) {
            case IDLE -> IDLE_FRAME_DELAY_NANOS;
            case WALK -> WALK_FRAME_DELAY_NANOS;
            case RUN -> RUN_FRAME_DELAY_NANOS;
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

    private boolean isOverlapping(Rectangle target) {
        Bounds playerBounds = hitbox.localToScene(hitbox.getBoundsInLocal());
        Bounds targetBounds = target.localToScene(target.getBoundsInLocal());
        return playerBounds.intersects(targetBounds);
    }
}