
package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.net.URL;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;


public class Merchant {

    private final ImageView view;

    private final int frameW;
    private final int frameH;
    private final int frames;
    private final int columns;

    private int frameIndex = 0;

    private Timeline timeline;
    
    private final List<ShopItem> items = new ArrayList<>();
    
    public static class ShopItem {
        private final String name;
        private final int price;

        public ShopItem(String name, int price) {
            this.name = name;
            this.price = price;
        }

        public String getName() { return name; }
        public int getPrice() { return price; }

        @Override
        public String toString() {
            return name + " - " + price + "g";
        }
    }

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
        }
    
        private void loadSprite(String spritePath) {
            URL url = getClass().getResource(spritePath);
        if (url == null) {
            throw new IllegalStateException("Missing resource: " + spritePath);
        }

        Image spriteSheet = new Image(url.toExternalForm());
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
        timeline.play();
    }

    public void stop() {
        timeline.stop();
    }

    public List<ShopItem> getItems() {
        return Collections.unmodifiableList(items);
    }

}