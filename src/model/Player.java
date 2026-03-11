package model;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
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

    private final Group node;               // the player group in FXML (human)
    private final ImageView sprite;         // human_img
    private final Rectangle hitbox;         // human_hitbox

    private final int frameWidth;
    private final int frameHeight;

    private int currentFrame = 0;
    private int currentRow = 0;

    private long lastUpdate = 0;
    private long lastFrameTime = 0;
    private final long frameDelayNanos;

    private boolean moving = false;
    private final double speed;
    private final PlayerState state;
    private final Set<KeyCode> pressedKeys = EnumSet.noneOf(KeyCode.class);

    private List<Rectangle> walls = List.of();

    private AnimationTimer timer;

    // Optional: any "detection zone" you want to check (farm1_detection etc.)
    private Rectangle detectionTarget;
    private Runnable onDetection;
    private Runnable onNoDetection;

    private final Image walkSpriteSheet = new Image(
            getClass().getResource("/image/human/human_Walk.png").toExternalForm()
    );

    public Player(PlayerState state,
                  Group node,
                  ImageView sprite,
                  Rectangle hitbox,
                  int frameWidth,
                  int frameHeight,
                  double speed,
                  long frameDelayNanos) {

        this.state = state;
        this.node = node;
        this.sprite = sprite;
        this.hitbox = hitbox;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.speed = speed;
        this.frameDelayNanos = frameDelayNanos;

        this.sprite.setImage(walkSpriteSheet);
        this.sprite.setViewport(new Rectangle2D(0, 0, frameWidth, frameHeight));
        this.sprite.setPreserveRatio(false);
        this.sprite.setSmooth(false);
    }

    public Inventory getInventory() {
        return state.getInventory();
    }

    public void setWalls(List<Rectangle> walls) {
        this.walls = (walls == null) ? List.of() : walls;
    }

    /** Hook for overlap checks (farm detection etc.) */
    public void setDetection(Rectangle target, Runnable onDetection, Runnable onNoDetection) {
        this.detectionTarget = target;
        this.onDetection = onDetection;
        this.onNoDetection = onNoDetection;
    }

    public void attachInput(Scene scene) {
        clearInputState();

        scene.setOnKeyPressed(e -> {
            if (isMovementKey(e.getCode())) {
                pressedKeys.add(e.getCode());
            }
        });

        scene.setOnKeyReleased(e -> {
            if (!isMovementKey(e.getCode())) {
                return;
            }

            pressedKeys.remove(e.getCode());
            if (pressedKeys.isEmpty()) {
                moving = false;
                currentFrame = 2; // idle frame
                updateViewport();
            }
        });
    }

    public void start() {
        stop();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastFrameTime == 0) {
                    lastFrameTime = now;
                    lastUpdate = now;
                    return;
                }

                long dtNanos = now - lastFrameTime;
                lastFrameTime = now;

                updateMovement(dtNanos);

                if (moving && now - lastUpdate > frameDelayNanos) {
                    currentFrame = (currentFrame + 1) % 6; // 6 frames per row
                    updateViewport();
                    lastUpdate = now;
                }

                if (detectionTarget != null) {
                    if (isOverlapping(detectionTarget)) {
                        if (onDetection != null) {
                            onDetection.run();
                        }
                    } else {
                        if (onNoDetection != null) {
                            onNoDetection.run();
                        }
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
        lastFrameTime = 0;
        lastUpdate = 0;
        clearInputState();
    }

    public void clearInputState() {
        pressedKeys.clear();
        moving = false;
        currentFrame = 2;
        updateViewport();
    }

    public boolean move(double dx, double dy) {
        boolean moved = false;

        // Resolve each axis independently so the player can slide along walls.
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
        double inputX = 0;
        double inputY = 0;

        if (pressedKeys.contains(KeyCode.A)) {
            inputX -= 1;
        }
        if (pressedKeys.contains(KeyCode.D)) {
            inputX += 1;
        }
        if (pressedKeys.contains(KeyCode.W)) {
            inputY -= 1;
        }
        if (pressedKeys.contains(KeyCode.S)) {
            inputY += 1;
        }

        if (inputX == 0 && inputY == 0) {
            if (moving) {
                moving = false;
                currentFrame = 2;
                updateViewport();
            }
            return;
        }

        if (inputX < 0) {
            currentRow = 1; // Left
        } else if (inputX > 0) {
            currentRow = 2; // Right
        } else if (inputY < 0) {
            currentRow = 3; // Up
        } else {
            currentRow = 0; // Down
        }

        double frameScale = Math.min(0.15, dtNanos / NANOS_PER_60FPS_FRAME); //character's speed
        double dx = inputX * speed * frameScale;
        double dy = inputY * speed * frameScale;

        if (inputX != 0 && inputY != 0) {
            dx *= INV_SQRT_2;
            dy *= INV_SQRT_2;
        }

        moving = move(dx, dy);

        if (!moving) {
            currentFrame = 2;
        }
        updateViewport();
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

    private void updateViewport() {
        int x = currentFrame * frameWidth;
        int y = currentRow * frameHeight;
        sprite.setViewport(new Rectangle2D(x, y, frameWidth, frameHeight));
    }

    private boolean isOverlapping(Rectangle target) {
        Bounds playerBounds = hitbox.localToScene(hitbox.getBoundsInLocal());
        Bounds targetBounds = target.localToScene(target.getBoundsInLocal());
        return playerBounds.intersects(targetBounds);
    }
}
