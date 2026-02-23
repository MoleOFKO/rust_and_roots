package model;

import java.util.List;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;

public final class CollisionRegistry {

    private CollisionRegistry() {}

    public static List<Rectangle> collectWalls(Group wallsGroup) {
        return NodeCollectors.byTypeAndPrefix(wallsGroup, Rectangle.class, "wall");
    }
}