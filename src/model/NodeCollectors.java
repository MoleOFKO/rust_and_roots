package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.Node;

public final class NodeCollectors {

    private NodeCollectors() {}

    // Collect children by type + id prefix
    public static <T extends Node> List<T> byTypeAndPrefix(
            Group group,
            Class<T> type,
            String idPrefix
    ) {
        List<T> result = new ArrayList<>();

        for (Node node : group.getChildren()) {
            if (type.isInstance(node)) {
                String id = node.getId();
                if (id != null && id.startsWith(idPrefix)) {
                    result.add(type.cast(node));
                }
            }
        }
        return result;
    }

    // Sort nodes by numeric suffix in id
    public static <T extends Node> void sortByIdNumber(List<T> nodes, String prefix) {
        nodes.sort(Comparator.comparingInt(n -> extractNumber(n.getId(), prefix)));
    }

    public static int extractNumber(String id, String prefix) {
        try {
            return Integer.parseInt(id.replace(prefix, ""));
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }
}