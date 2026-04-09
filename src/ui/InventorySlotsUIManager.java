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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import model.Inventory;
import model.ItemDatabase;
import model.item;

public class InventorySlotsUIManager {

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
        List<Integer> owned = new ArrayList<>(inventory.itemsProperty().keySet());
        owned.removeIf(itemId -> !ItemDatabase.isRuntimeUsableItem(itemId));

        owned.sort(Comparator.comparing(id -> {
            item itm = ItemDatabase.getItemById(id);
            return itm == null ? "" : itm.getItem_name();
        }));

        int slotCount = Math.min(images.size(), labels.size());

        for (int i = 0; i < slotCount; i++) {
            ImageView imageView = images.get(i);
            Label label = labels.get(i);
            Button button = (buttons != null && i < buttons.size()) ? buttons.get(i) : null;

            if (i >= owned.size()) {
                if (imageView != null) imageView.setImage(null);
                if (label != null) label.setText("");
                if (button != null) {
                    button.setText("");
                    button.setDisable(true);
                    button.setOnAction(null);
                }
                continue;
            }

            int itemId = owned.get(i);
            int qty = inventory.getCount(itemId);
            item itm = ItemDatabase.getItemById(itemId);
            String itemName = (itm == null) ? ("Item " + itemId) : itm.getItem_name();

            if (imageView != null) {
                imageView.setImage(ItemDatabase.getImage(itemId));
            }

            if (label != null) {
                label.setText("x" + qty);
            }

            if (button != null) {
                button.setDisable(false);
                button.setText(itemName);
            }
        }
    }
}

