/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package ui;
/**
 *
 * @author Shane Khant Ko
 */
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.collections.MapChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import model.Inventory;
import model.ItemDatabase;
import model.item;
import javafx.util.Duration;

public class InventorySlotsUIManager {

    private static final String ITEM_USAGE_TOOLTIP_KEY = "rust_and_roots.itemUsageTooltip";

    private final Inventory inventory;
    private final List<ImageView> images;
    private final List<Label> labels;
    private final List<Button> buttons;

    public InventorySlotsUIManager(
            Inventory inventory,
            List<ImageView> images,
            List<Label> labels
    ) {
        this(inventory, images, labels, null);
    }

    public InventorySlotsUIManager(
            Inventory inventory,
            List<ImageView> images,
            List<Label> labels,
            List<Button> buttons
    ) {
        this.inventory = inventory;
        this.images = (images == null) ? new ArrayList<>() : images;
        this.labels = (labels == null) ? new ArrayList<>() : labels;
        this.buttons = buttons;

        refresh();

        inventory.itemsProperty().addListener(
            (MapChangeListener<Integer, IntegerProperty>) change -> refresh()
        );
    }

    public void refresh() {
        int slotCount = Math.min(images.size(), labels.size());
        List<SlotData> slots = snapshotSlots(slotCount);

        for (int i = 0; i < slotCount; i++) {
            ImageView imageView = images.get(i);
            Label label = labels.get(i);
            Button button = (buttons != null && i < buttons.size()) ? buttons.get(i) : null;

            if (i >= slots.size()) {
                if (imageView != null) imageView.setImage(null);
                installItemUsageTooltip(imageView, -1);
                if (label != null) label.setText("");
                if (button != null) {
                    button.setText("");
                    button.setDisable(true);
                    button.setOnAction(null);
                    installItemUsageTooltip(button, -1);
                }
                continue;
            }

            SlotData slot = slots.get(i);
            int itemId = slot.itemId;
            int qty = slot.quantity;
            item itm = ItemDatabase.getItemById(itemId);
            String itemName = (itm == null) ? ("Item " + itemId) : itm.getItem_name();

            if (imageView != null) {
                imageView.setImage(ItemDatabase.getImage(itemId));
                installItemUsageTooltip(imageView, itemId);
            }

            if (label != null) {
                label.setText("x" + qty);
            }

            if (button != null) {
                button.setDisable(false);
                button.setText(itemName);
                installItemUsageTooltip(button, itemId);
            }
        }
    }

    private static void installItemUsageTooltip(Node node, int itemId) {
        if (node == null) return;

        Object previous = node.getProperties().remove(ITEM_USAGE_TOOLTIP_KEY);
        if (previous instanceof Tooltip oldTooltip) {
            Tooltip.uninstall(node, oldTooltip);
        }

        String text = ItemDatabase.getItemUsageHoverText(itemId);
        if (text == null || text.isBlank()) return;

        Tooltip tooltip = new Tooltip(text);
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setHideDelay(Duration.millis(80));
        Tooltip.install(node, tooltip);
        node.getProperties().put(ITEM_USAGE_TOOLTIP_KEY, tooltip);
    }

    private static final class SlotData {
        private final int itemId;
        private final int quantity;

        private SlotData(int itemId, int quantity) {
            this.itemId = itemId;
            this.quantity = quantity;
        }
    }

    private List<SlotData> snapshotSlots(int capacity) {
        List<SlotData> result = new ArrayList<>();
        if (inventory == null || capacity <= 0) return result;

        List<Integer> owned = new ArrayList<>(inventory.itemsProperty().keySet());
        owned.sort(Comparator.comparing(id -> {
            item itm = ItemDatabase.getItemById(id);
            return itm == null ? "" : itm.getItem_name();
        }));

        for (Integer itemId : owned) {
            if (itemId == null || itemId <= 0) continue;
            int remaining = inventory.getCount(itemId);
            if (remaining <= 0) continue;

            int maxStack = Math.max(1, ItemDatabase.getMaxStackSize(itemId));
            while (remaining > 0 && result.size() < capacity) {
                int stackQty = Math.min(maxStack, remaining);
                result.add(new SlotData(itemId, stackQty));
                remaining -= stackQty;
            }

            if (result.size() >= capacity) {
                break;
            }
        }

        return result;
    }
}

