
package model;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import util.ImageCache;

public class Merchant {

    private final ImageView view;

    private final int frameW;
    private final int frameH;
    private final int frames;
    private final int columns;

    private int frameIndex = 0;

    private Timeline timeline;

    public Merchant(ImageView view,
                    int frameW, int frameH,
                    int frames, int columns,
                    double fps,
                    String spritePath) {

        this.view = view;
        this.frameW = frameW;
        this.frameH = frameH;
        this.frames = frames;
        this.columns = columns;

        loadSprite(spritePath);
        
        //Setup Animation
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1.0 / fps), e -> nextFrame())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);

        if (this.view != null) {
            this.view.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene == null) {
                    stop();
                    this.view.setImage(null);
                }
            });
        }
        }
    
        private void loadSprite(String spritePath) {
            Image spriteSheet = ImageCache.get(spritePath);
        if (spriteSheet == null) {
            throw new IllegalStateException("Missing resource: " + spritePath);
        }

        view.setImage(spriteSheet);

        view.setViewport(new Rectangle2D(0, 0, frameW, frameH));
        view.setMouseTransparent(true); // ✅ prevents button blocking
        }
        
    private void nextFrame() {
        frameIndex = (frameIndex + 1) % frames;

        int col = frameIndex % columns;
        int row = frameIndex / columns;

        view.setViewport(new Rectangle2D(
                col * frameW,
                row * frameH,
                frameW,
                frameH
        ));
    }

    public void play() {
        if (timeline != null) {
            timeline.play();
        }
    }

    public void stop() {
        if (timeline != null) {
            timeline.stop();
        }
    }
}
