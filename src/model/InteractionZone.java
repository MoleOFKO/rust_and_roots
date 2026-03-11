package model;

import java.util.function.Consumer;
import javafx.geometry.Bounds;
import javafx.scene.Node;

public class InteractionZone {

    private final Node zoneShape;
    private final Consumer<Boolean> onStateChanged;
    private final Runnable onTriggered;
    private final double triggerDelayGameSeconds;

    private boolean inside = false;
    private double enteredAtGameSeconds = -1;
    private boolean triggered = false;

    
    public InteractionZone(Node zoneShape, Consumer<Boolean> onStateChanged) {
        this(zoneShape, onStateChanged, null, 0);
    }

    public InteractionZone(
            Node zoneShape,
            Consumer<Boolean> onStateChanged,
            Runnable onTriggered,
            double triggerDelayGameSeconds
    ) {
        this.zoneShape = zoneShape;
        this.onStateChanged = onStateChanged;
        this.onTriggered = onTriggered;
        this.triggerDelayGameSeconds = Math.max(0, triggerDelayGameSeconds);
    }

    public void update(Bounds playerBounds, double nowGameSeconds) {
        Bounds zoneBounds = zoneShape.localToScene(zoneShape.getBoundsInLocal());
        boolean nowInside = playerBounds.intersects(zoneBounds);

        if (nowInside != inside) {
            inside = nowInside;

            if (inside) {
                enteredAtGameSeconds = nowGameSeconds;
                triggered = false;
            } else {
                enteredAtGameSeconds = -1;
                triggered = false;
            }

            if (onStateChanged != null) {
                onStateChanged.accept(inside);
            }
        }

        if (inside && !triggered && onTriggered != null) {
            if (nowGameSeconds - enteredAtGameSeconds >= triggerDelayGameSeconds) {
                triggered = true;
                onTriggered.run();
            }
        }
    }

    public boolean isInside() {
        return inside;
    }
}