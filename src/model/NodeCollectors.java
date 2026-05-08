package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;

public final class NodeCollectors {

    private NodeCollectors() {}

    // Collect children by type + id prefix
    public static <T extends Node> List<T> byTypeAndPrefix(
            Group group,
            Class<T> type,
            String idPrefix
    ) {
        List<T> result = new ArrayList<>();

        collectByTypeAndPrefix(group, type, idPrefix, result);
        return result;
    }

    private static <T extends Node> void collectByTypeAndPrefix(
            Node node,
            Class<T> type,
            String idPrefix,
            List<T> result
    ) {
        if (node == null) {
            return;
        }

        if (type.isInstance(node)) {
            String id = node.getId();
            if (id != null && id.startsWith(idPrefix)) {
                result.add(type.cast(node));
            }
        }

        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                collectByTypeAndPrefix(child, type, idPrefix, result);
            }
        }
    }

    // Sort nodes by numeric suffix in id
    public static <T extends Node> void sortByIdNumber(List<T> nodes, String prefix) {
        nodes.sort(Comparator.comparingInt(n -> extractNumber(n.getId(), prefix)));
    }

    public static int extractNumber(String id, String prefix) {
        try {
            String suffix = id.replace(prefix, "");
            if (suffix.isEmpty()) {
                return 0;
            }
            return Integer.parseInt(suffix);
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }
}
