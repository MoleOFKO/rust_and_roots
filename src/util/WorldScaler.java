/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.layout.Pane;

/**
 *
 * @author ASUS
 */
public class WorldScaler {
    private final Pane view;
    private final Group world;

    private final double baseWidth;
    private final double baseHeight;
    
    private boolean queued = false;
    
    public WorldScaler(Pane view, Group world) {
        this.view = view;
        this.world = world;

        // Capture the ORIGINAL unscaled content size once
        Bounds b = world.getLayoutBounds();
        this.baseWidth = b.getWidth();
        this.baseHeight = b.getHeight();
        
        view.widthProperty().addListener((obs, o, n) -> requestScale());
        view.heightProperty().addListener((obs, o, n) -> requestScale());
        
        requestScale();
    }

    public void scale() {
        double paneW = view.getWidth();
        double paneH = view.getHeight();
        if (paneW <= 0 || paneH <= 0) return;

         // Reset first so old transforms do not affect the next calculation
        world.setScaleX(1.0);
        world.setScaleY(1.0);
        world.setLayoutX(0);
        world.setLayoutY(0);
        
        double scale = Math.min(paneW / baseWidth, paneH / baseHeight);

        world.setScaleX(scale);
        world.setScaleY(scale);

        double scaledW = baseWidth * scale;
        double scaledH = baseHeight * scale;

        world.setLayoutX((paneW - scaledW) / 2.0);
        world.setLayoutY((paneH - scaledH) / 2.0);
    }


    private void requestScale() {
        if (queued) return;
        queued = true;

        Platform.runLater(() -> {
            queued = false;
            scale();
        });
    }
}
