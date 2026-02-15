package model;

import java.util.List;
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

import model.Inventory;

public class Player {

    private final Group node;              // the player group in FXML (human)
    private final ImageView sprite;         // human_img
    private final Rectangle hitbox;         // human_hitbox

    private final int frameWidth;
    private final int frameHeight;

    private int currentFrame = 0;
    private int currentRow = 0;

    private long lastUpdate = 0;
    private final long frameDelayNanos;

    private boolean moving = false;
    private final double speed;

    private final Inventory inventory = new Inventory(100);//starting coins (change as needs)
    
    private List<Rectangle> walls = List.of(); // set later

    private AnimationTimer timer;

    // Optional: any "detection zone" you want to check (farm1_detection etc.)
    private Rectangle detectionTarget;
    private Runnable onDetection;
    private Runnable onNoDetection;

    
    private Image walk_spriteSheet = new Image(getClass().getResource("/image/human/human_Walk.png").toExternalForm());
    
    public Player(Group node,
                  ImageView sprite,
                  Rectangle hitbox,
                  int frameWidth,
                  int frameHeight,
                  double speed,
                  long frameDelayNanos) {

        this.node = node;
        this.sprite = sprite;
        this.hitbox = hitbox;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.speed = speed;
        this.frameDelayNanos = frameDelayNanos;

        this.sprite.setImage(walk_spriteSheet);
        this.sprite.setViewport(new Rectangle2D(0, 0, frameWidth, frameHeight));
        this.sprite.setPreserveRatio(false);
        this.sprite.setSmooth(false);
    }

    public Inventory getInventory(){
        return inventory;
    }
    
    public void setWalls(List<Rectangle> walls) {
        this.walls = walls;
    }

    /** Hook for overlap checks (farm detection etc.) */
    public void setDetection(Rectangle target, Runnable onDetection, Runnable onNoDetection) {
        this.detectionTarget = target;
        this.onDetection = onDetection;
        this.onNoDetection = onNoDetection;
    }

    public void attachInput(Scene scene) {
        scene.setOnKeyPressed(e -> {
            double dx = 0;
            double dy = 0;

            if (e.getCode() == KeyCode.W) {
                currentRow = 3; // Up
                dy = -speed;
            } else if (e.getCode() == KeyCode.S) {
                currentRow = 0; // Down
                dy = speed;
            } else if (e.getCode() == KeyCode.A) {
                currentRow = 1; // Left
                dx = -speed;
            } else if (e.getCode() == KeyCode.D) {
                currentRow = 2; // Right
                dx = speed;
            } else {
                return;
            }

            // move if no collision
            if (!collides(dx, dy)) {
                move(dx, dy);
                moving = true;
            }
        });

        scene.setOnKeyReleased(e -> {
            moving = false;
            currentFrame = 2; // idle frame (you can replace with idle sprites later)
            updateViewport();
        });
    }

    public void start() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (moving && now - lastUpdate > frameDelayNanos) {
                    currentFrame = (currentFrame + 1) % 6; // 6 frames per row
                    updateViewport();
                    lastUpdate = now;
                }

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
        if (timer != null) timer.stop();
    }

    public void move(double dx, double dy) {
        // X
        if (!collides(dx, 0)) {
            node.setLayoutX(node.getLayoutX() + dx);
        }
        // Y
        if (!collides(0, dy)) {
            node.setLayoutY(node.getLayoutY() + dy);
        }
        
//        updateMerchantInteraction();
    }

    private boolean collides(double dx, double dy) {
        Bounds playerBounds = hitbox.localToScene(hitbox.getBoundsInLocal());

        Bounds movedBounds = new BoundingBox(
                playerBounds.getMinX() + dx,
                playerBounds.getMinY() + dy,
                playerBounds.getWidth(),
                playerBounds.getHeight()
        );

        for (Rectangle wall : walls) {
            Bounds wallBounds = wall.localToScene(wall.getBoundsInLocal());
            if (movedBounds.intersects(wallBounds)) {
                return true;
            }
        }
        return false;
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
