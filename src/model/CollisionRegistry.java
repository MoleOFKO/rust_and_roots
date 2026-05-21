package model;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.shape.Rectangle;

public final class CollisionRegistry {

    private CollisionRegistry() {}

    public static List<Rectangle> collectWalls(Group wallsGroup) {
        return NodeCollectors.byTypeAndPrefix(wallsGroup, Rectangle.class, "wall");
    }

    public static List<Rectangle> collectMobBlockingObstacles(Group worldGroup, Group wallsGroup) {
        List<Rectangle> result = new ArrayList<>();
        addUnique(result, collectWalls(wallsGroup));
        collectResourceObstacles(worldGroup, result);
        return result;
    }

    private static void collectResourceObstacles(Node node, List<Rectangle> result) {
        if (node == null || result == null) {
            return;
        }

        if (node instanceof Rectangle rect && isResourceObstacleId(rect.getId())) {
            addUnique(result, List.of(rect));
        }

        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                collectResourceObstacles(child, result);
            }
        }
    }

    private static boolean isResourceObstacleId(String id) {
        if (id == null || id.isBlank()) {
            return false;
        }

        String normalized = id.trim().toLowerCase();
        if (normalized.equals("human_hitbox") || normalized.startsWith("human_hitbox_")) {
            return false;
        }
        if (isMobHitboxId(normalized)) {
            return false;
        }

        return normalized.contains("_hitbox") || normalized.endsWith("_wall");
    }

    private static boolean isMobHitboxId(String id) {
        String base = id;
        int hitboxIndex = base.indexOf("_hitbox");
        if (hitboxIndex >= 0) {
            base = base.substring(0, hitboxIndex);
        }
        while (!base.isEmpty()) {
            char c = base.charAt(base.length() - 1);
            if (c < '0' || c > '9') {
                break;
            }
            base = base.substring(0, base.length() - 1);
        }

        return switch (base) {
            case "slime", "poison_slime", "lava_slime", "orc", "orcb" -> true;
            default -> false;
        };
    }

    private static void addUnique(List<Rectangle> target, List<Rectangle> source) {
        if (target == null || source == null) {
            return;
        }
        for (Rectangle rect : source) {
            if (rect != null && !target.contains(rect)) {
                target.add(rect);
            }
        }
    }
}
