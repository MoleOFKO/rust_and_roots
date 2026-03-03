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

    public WorldScaler(Pane view, Group world) {
        this(view, world, false);
    }   
    public WorldScaler(Pane view, Group world, boolean center) {
        this.view = view;
        this.world = world;

        view.widthProperty().addListener((obs, o, n) -> requestScale());
        view.heightProperty().addListener((obs, o, n) -> requestScale());
        
        requestScale();
    }

    public void scale() {
        double paneW = view.getWidth();
        double paneH = view.getHeight();
        if (paneW <= 0 || paneH <= 0) return;

        Bounds b = world.getLayoutBounds();
        double contentW = b.getWidth();
        double contentH = b.getHeight();
        if (contentW <= 0 || contentH <= 0) return;

        double scale = Math.min(paneW / contentW, paneH / contentH);
        world.setScaleX(scale);
        world.setScaleY(scale);

        double scaledW = contentW * scale;
        double scaledH = contentH * scale;
        world.setLayoutX((paneW - scaledW) / 2.0);
        world.setLayoutY((paneH - scaledH) / 2.0);
    }

    private boolean queued = false;

    private void requestScale() {
        if (queued) return;
        queued = true;

        Platform.runLater(() -> {
            queued = false;
            scale();
        });
    }
}
