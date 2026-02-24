package model;

import java.util.function.Consumer;
import javafx.geometry.Bounds;
import javafx.scene.Node;

public class InteractionZone {

    private final Node zoneShape;
    private final Consumer<Boolean> onStateChanged;
    private boolean inside = false;

    public InteractionZone(Node zoneShape, Consumer<Boolean> onStateChanged) {
        this.zoneShape = zoneShape;
        this.onStateChanged = onStateChanged;
    }

    public void update(Bounds playerBounds) {
        Bounds zoneBounds = zoneShape.localToScene(zoneShape.getBoundsInLocal());
        boolean nowInside = playerBounds.intersects(zoneBounds);

        if (nowInside != inside) {
            inside = nowInside;
            onStateChanged.accept(inside);
        }
    }

    public boolean isInside() {
        return inside;
    }
}