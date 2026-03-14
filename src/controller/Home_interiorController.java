/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import game.GameTimeManager;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import model.InteractionZone;
import model.Inventory;
import model.ItemDatabase;
import model.item;
import util.SpawnTag;

/**
 * FXML Controller class
 *
 * @author larry
 */
public class Home_interiorController extends BaseMapController {

    @FXML
    private Group walls;
    @FXML
    private Rectangle wall;
    @FXML
    private Rectangle wall1;
    @FXML
    private Rectangle wall2;
    @FXML
    private Rectangle wall3;
    @FXML
    private Rectangle wall4;
    @FXML
    private Rectangle wall5;
    @FXML
    private Rectangle wall6;
    @FXML
    private Rectangle wall7;
    @FXML
    private Rectangle wall8;
    @FXML
    private Rectangle wall9;
    @FXML
    private Rectangle wall10;
    @FXML
    private Rectangle wall11;
    @FXML
    private Rectangle wall12;
    @FXML
    private Rectangle wall13;
    @FXML
    private Rectangle wall14;
    @FXML
    private Rectangle wall15;
    @FXML
    private Rectangle wall16;
    @FXML
    private Rectangle wall17;
    @FXML
    private Rectangle wall18;
    @FXML
    private Rectangle wall19;
    @FXML
    private Rectangle wall20;
    @FXML
    private Rectangle wall21;
    @FXML
    private Rectangle wall22;
    @FXML
    private Rectangle wall23;
    @FXML
    private Rectangle wall24;
    @FXML
    private Rectangle wall25;
    @FXML
    private Rectangle wall26;
    @FXML
    private Rectangle spawn_default;
    @FXML
    private Group spawn;
    @FXML
    private Rectangle spawn_from_bed;
    @FXML
    private Rectangle spawn_from_cook;
    @FXML
    private Rectangle spawn_from_craft;
    @FXML
    private Rectangle spawn_from_inv;
    @FXML
    private Rectangle spawn_from_melt;
    @FXML
    private Rectangle spawn_from_door;
    @FXML
    private Rectangle exit_door;
    @FXML
    private Group human;
    @FXML
    private Rectangle human_hitbox;
    @FXML
    private ImageView human_img;
    @FXML
    private StackPane view;
    @FXML
    private StackPane sleepStackPane;
    @FXML
    private Button btntoSleep;
    @FXML
    private Button btnOpenInv;
    @FXML
    private ImageView inyimg;
    @FXML
    private Label invlbl;
    @FXML
    private Button invbtn;
    @FXML
    private ImageView inyimg1;
    @FXML
    private Label invlbl1;
    @FXML
    private Button invbtn1;
    @FXML
    private ImageView inyimg2;
    @FXML
    private Label invlbl2;
    @FXML
    private Button invbtn2;
    @FXML
    private ImageView inyimg3;
    @FXML
    private Label invlbl3;
    @FXML
    private Button invbtn3;
    @FXML
    private ImageView inyimg4;
    @FXML
    private Label invlbl4;
    @FXML
    private Button invbtn4;
    @FXML
    private ImageView inyimg5;
    @FXML
    private Label invlbl5;
    @FXML
    private Button invbtn5;
    @FXML
    private ImageView inyimg6;
    @FXML
    private Label invlbl6;
    @FXML
    private Button invbtn6;
    @FXML
    private ImageView inyimg7;
    @FXML
    private Label invlbl7;
    @FXML
    private Button invbtn7;
    @FXML
    private ImageView inyimg8;
    @FXML
    private Label invlbl8;
    @FXML
    private Button invbtn8;
    @FXML
    private ImageView inyimg9;
    @FXML
    private Label invlbl9;
    @FXML
    private Button invbtn9;
    @FXML
    private ImageView inyimg10;
    @FXML
    private Label invlbl10;
    @FXML
    private Button invbtn10;
    @FXML
    private ImageView inyimg11;
    @FXML
    private Label invlbl11;
    @FXML
    private Button invbtn11;
    @FXML
    private ImageView inyimg12;
    @FXML
    private Label invlbl12;
    @FXML
    private Button invbtn12;
    @FXML
    private ImageView inyimg13;
    @FXML
    private Label invlbl13;
    @FXML
    private Button invbtn13;
    @FXML
    private ImageView inyimg14;
    @FXML
    private Label invlbl14;
    @FXML
    private Button invbtn14;
    @FXML
    private ImageView inyimg15;
    @FXML
    private Label invlbl15;
    @FXML
    private Button invbtn15;
    @FXML
    private ImageView inyimg16;
    @FXML
    private Label invlbl16;
    @FXML
    private Button invbtn16;
    @FXML
    private ImageView inyimg17;
    @FXML
    private Label invlbl17;
    @FXML
    private Button invbtn17;
    @FXML
    private ImageView inyimg18;
    @FXML
    private Label invlbl18;
    @FXML
    private Button invbtn18;
    @FXML
    private ImageView inyimg19;
    @FXML
    private Label invlbl19;
    @FXML
    private Button invbtn19;
    @FXML
    private ImageView inyimg20;
    @FXML
    private Label invlbl20;
    @FXML
    private Button invbtn20;
    @FXML
    private ImageView inyimg21;
    @FXML
    private Label invlbl21;
    @FXML
    private Button invbtn21;
    @FXML
    private ImageView inyimg22;
    @FXML
    private Label invlbl22;
    @FXML
    private Button invbtn22;
    @FXML
    private ImageView inyimg23;
    @FXML
    private Label invlbl23;
    @FXML
    private Button invbtn23;
    @FXML
    private ImageView inyimg24;
    @FXML
    private Label invlbl24;
    @FXML
    private Button invbtn24;
    @FXML
    private ImageView inyimg25;
    @FXML
    private Label invlbl25;
    @FXML
    private Button invbtn25;
    @FXML
    private ImageView inyimg26;
    @FXML
    private Label invlbl26;
    @FXML
    private Button invbtn26;
    @FXML
    private ImageView inyimg27;
    @FXML
    private Label invlbl27;
    @FXML
    private Button invbtn27;
    @FXML
    private ImageView inyimg28;
    @FXML
    private Label invlbl28;
    @FXML
    private Button invbtn28;
    @FXML
    private ImageView inyimg29;
    @FXML
    private Label invlbl29;
    @FXML
    private Button invbtn29;
    @FXML
    private ImageView inyimg30;
    @FXML
    private Label invlbl30;
    @FXML
    private Button invbtn30;
    @FXML
    private ImageView inyimg31;
    @FXML
    private Label invlbl31;
    @FXML
    private Button invbtn31;
    @FXML
    private ImageView inyimg32;
    @FXML
    private Label invlbl32;
    @FXML
    private Button invbtn32;
    @FXML
    private ImageView inyimg33;
    @FXML
    private Label invlbl33;
    @FXML
    private Button invbtn33;
    @FXML
    private ImageView inyimg34;
    @FXML
    private Label invlbl34;
    @FXML
    private Button invbtn34;
    @FXML
    private ImageView inyimg35;
    @FXML
    private Label invlbl35;
    @FXML
    private Button invbtn35;
    @FXML
    private ImageView inyimg36;
    @FXML
    private Label invlbl36;
    @FXML
    private Button invbtn36;
    @FXML
    private ImageView inyimg37;
    @FXML
    private Label invlbl37;
    @FXML
    private Button invbtn37;
    @FXML
    private ImageView inyimg38;
    @FXML
    private Label invlbl38;
    @FXML
    private Button invbtn38;
    @FXML
    private ImageView inyimg39;
    @FXML
    private Label invlbl39;
    @FXML
    private Button invbtn39;
    @FXML
    private ImageView inyimg40;
    @FXML
    private Label invlbl40;
    @FXML
    private Button invbtn40;
    @FXML
    private ImageView inyimg41;
    @FXML
    private Label invlbl41;
    @FXML
    private Button invbtn41;
    @FXML
    private Group world;
    @FXML
    private StackPane chestStackPane;
    @FXML
    private Rectangle craft_detect;
    @FXML
    private Rectangle bed_detect;
    @FXML
    private Rectangle chest_detect;
    @FXML
    private Rectangle melt_detect;
    @FXML
    private Rectangle cook_detect;
    @FXML
    private Button btnmelt;
    @FXML
    private Button btncraft;
    @FXML
    private Button btncook;
    @FXML
    private Group chest_inv_group;
    @FXML
    private Group player_inv_group;
    @FXML
    private ImageView Playerinvimg;
    @FXML
    private Button Playerinvbtn;
    @FXML
    private Label Playerinvlbl;
    @FXML
    private ImageView Playerinvimg1;
    @FXML
    private Button Playerinvbtn1;
    @FXML
    private Label Playerinvlbl1;
    @FXML
    private ImageView Playerinvimg2;
    @FXML
    private Button Playerinvbtn2;
    @FXML
    private Label Playerinvlbl2;
    @FXML
    private ImageView Playerinvimg3;
    @FXML
    private Button Playerinvbtn3;
    @FXML
    private Label Playerinvlbl3;
    @FXML
    private ImageView Playerinvimg4;
    @FXML
    private Button Playerinvbtn4;
    @FXML
    private Label Playerinvlbl4;
    @FXML
    private ImageView Playerinvimg5;
    @FXML
    private Button Playerinvbtn5;
    @FXML
    private Label Playerinvlbl5;
    @FXML
    private ImageView Playerinvimg6;
    @FXML
    private Button Playerinvbtn6;
    @FXML
    private Label Playerinvlbl6;
    @FXML
    private ImageView Playerinvimg7;
    @FXML
    private Button Playerinvbtn7;
    @FXML
    private Label Playerinvlbl7;
    @FXML
    private ImageView Playerinvimg8;
    @FXML
    private Button Playerinvbtn8;
    @FXML
    private Label Playerinvlbl8;
    @FXML
    private ImageView Playerinvimg9;
    @FXML
    private Button Playerinvbtn9;
    @FXML
    private Label Playerinvlbl9;
    @FXML
    private ImageView Playerinvimg10;
    @FXML
    private Button Playerinvbtn10;
    @FXML
    private Label Playerinvlbl10;
    @FXML
    private ImageView Playerinvimg11;
    @FXML
    private Button Playerinvbtn11;
    @FXML
    private Label Playerinvlbl11;
    @FXML
    private ImageView Playerinvimg12;
    @FXML
    private Button Playerinvbtn12;
    @FXML
    private Label Playerinvlbl12;
    @FXML
    private ImageView Playerinvimg13;
    @FXML
    private Button Playerinvbtn13;
    @FXML
    private Label Playerinvlbl13;
    @FXML
    private ImageView Playerinvimg14;
    @FXML
    private Button Playerinvbtn14;
    @FXML
    private Label Playerinvlbl14;
    @FXML
    private ImageView Playerinvimg15;
    @FXML
    private Button Playerinvbtn15;
    @FXML
    private Label Playerinvlbl15;
    @FXML
    private ImageView Playerinvimg16;
    @FXML
    private Button Playerinvbtn16;
    @FXML
    private Label Playerinvlbl16;
    @FXML
    private ImageView Playerinvimg17;
    @FXML
    private Button Playerinvbtn17;
    @FXML
    private Label Playerinvlbl17;
    @FXML
    private ImageView Playerinvimg18;
    @FXML
    private Button Playerinvbtn18;
    @FXML
    private Label Playerinvlbl18;
    @FXML
    private ImageView Playerinvimg19;
    @FXML
    private Button Playerinvbtn19;
    @FXML
    private Label Playerinvlbl19;
    @FXML
    private ImageView send_to_chest;
    @FXML
    private ImageView send_to_inventory;
    @FXML
    private ImageView Back;

    private static final int CHEST_SLOT_CAPACITY = 42;;

    private enum SelectionSource { NONE, PLAYER, CHEST }

    private final List<ImageView> chestImages = new ArrayList<>();
    private final List<Label> chestLabels = new ArrayList<>();
    private final List<Button> chestButtons = new ArrayList<>();

    private Inventory chestInventory;
    private SelectionSource selectedSource = SelectionSource.NONE;
    private Integer selectedItemId = null;

    @Override
    protected String mapKey() {
        return "Home_interior";
    }

    @Override
    protected void configureExits() {
        registerExit(exit_door, "/view/home_map.fxml", SpawnTag.FROM_DOOR);
        registerExit(cook_detect, "/view/cook.fxml", SpawnTag.FROM_COOK);
        registerExit(melt_detect, "/view/melt.fxml", SpawnTag.FROM_MELT);
        registerExit(craft_detect, "/view/craft.fxml", SpawnTag.FROM_CRAFT);
    }

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);

        ItemDatabase.loadItems();
        chestInventory = gameState.getChestInventory();
        setupChestUi();
        setupInteriorZones();

        if (btntoSleep != null) btntoSleep.setDisable(true);
        if (btnOpenInv != null) btnOpenInv.setDisable(true);
        if (btncook != null) btncook.setDisable(true);
        if (btnmelt != null) btnmelt.setDisable(true);
        if (btncraft != null) btncraft.setDisable(true);

        if(btncook != null){
            btncook.setOnAction(e -> openCook());
        }
        if (btnmelt != null) {
            btnmelt.setOnAction(e -> openMelt());
        }
        if (btncraft != null) {
            btncraft.setOnAction(e -> openCraft());
        }
        
        if (sleepStackPane != null) {
            sleepStackPane.setVisible(false);
            sleepStackPane.setDisable(true);
            sleepStackPane.setManaged(false);
        }

        if (chestStackPane != null) {
            chestStackPane.setVisible(false);
            chestStackPane.setDisable(true);
            chestStackPane.setManaged(false);
        }
    }

    private void openCook() {
        if (btncook.isDisable()) return;
        gameState.setReturnScene("/view/home_interior.fxml", SpawnTag.FROM_COOK);
        stopAndSwitchScene("/view/cook.fxml", SpawnTag.FROM_COOK);
    }

    private void openMelt() {
        if (btnmelt.isDisable()) return;
        gameState.setReturnScene("/view/home_interior.fxml", SpawnTag.FROM_MELT);
        stopAndSwitchScene("/view/melt.fxml", SpawnTag.FROM_MELT);
    }

    private void openCraft() {
        if (btncraft.isDisable()) return;
        gameState.setReturnScene("/view/home_interior.fxml", SpawnTag.FROM_CRAFT);
        stopAndSwitchScene("/view/craft.fxml", SpawnTag.FROM_CRAFT);
    }
    
    private void setupInteriorZones() {
        if (bed_detect != null && btntoSleep != null) {
            interactionManager.addZone(new InteractionZone(bed_detect, inside -> {
                btntoSleep.setDisable(!inside);
            }));
        }

        if (chest_detect != null && btnOpenInv != null) {
            interactionManager.addZone(new InteractionZone(chest_detect, inside -> {
                btnOpenInv.setDisable(!inside);
            }));
        }

        if (cook_detect != null && btncook != null) {
            interactionManager.addZone(new InteractionZone(cook_detect, inside -> {
                btncook.setDisable(!inside);
            }));
        }

        if (melt_detect != null && btnmelt != null) {
            interactionManager.addZone(new InteractionZone(melt_detect, inside -> {
                btnmelt.setDisable(!inside);
            }));
        }

        if (craft_detect != null && btncraft != null) {
            interactionManager.addZone(new InteractionZone(craft_detect, inside -> {
                btncraft.setDisable(!inside);
            }));
        }
    }
    
    @Override
    protected void onPlayerInventorySlotSelected(int index, SlotData slot) {
        if (slot == null) return;

        selectedSource = SelectionSource.PLAYER;
        selectedItemId = slot.itemId();
        selectedPlayerInventoryItemId = slot.itemId();

        refreshChestAndInventoryViews();
    }
    
    @FXML
    private void handleSleepAction(ActionEvent event) {
        if (btntoSleep.isDisable()) return;

        sleepStackPane.setManaged(true);
        sleepStackPane.setDisable(false);
        sleepStackPane.setVisible(true);
        
        if (player != null) {
        player.clearInputState();
        }

        javafx.animation.PauseTransition pause =
            new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2));

        pause.setOnFinished(e -> {
            GameTimeManager time = GameTimeManager.get();

            // move to next morning
            time.setDay(time.dayNumberProperty().get() + 1);
            time.setTimeOfDay(6, 0);

            sleepStackPane.setVisible(false);
            sleepStackPane.setDisable(true);
            sleepStackPane.setManaged(false);
        });

        pause.play();
    }

    @FXML
    private void HandleInventoryAction(ActionEvent event) {
        if (btnOpenInv != null && btnOpenInv.isDisable()) return;

        chestStackPane.setManaged(true);
        chestStackPane.setDisable(false);
        chestStackPane.setVisible(true);
        clearSelection();
        refreshChestAndInventoryViews();

        if (player != null) {
            player.clearInputState();
        }
        if (view != null && view.getScene() != null) {
            view.getScene().setOnKeyPressed(null);
            view.getScene().setOnKeyReleased(null);
        }
    }

    @FXML
    private void handlecraftaction(ActionEvent event) {
    }

    @FXML
    private void handlecookaction(ActionEvent event) {
    }

    @FXML
    private void sendToChestClick(MouseEvent event) {
        moveSelectedItem(SelectionSource.PLAYER);
    }

    @FXML
    private void sendToInventoryClick(MouseEvent event) {
        moveSelectedItem(SelectionSource.CHEST);
    }

    @FXML
    private void backClick(MouseEvent event) {
        chestStackPane.setVisible(false);
        chestStackPane.setDisable(true);
        chestStackPane.setManaged(false);
        clearSelection();

        if (view != null && view.getScene() != null && player != null) {
            player.attachInput(view.getScene());
        }
    }

    private void setupChestUi() {
        collectChestNodes();
        wireChestButtons();
        refreshChestAndInventoryViews();
    }

    private void collectChestNodes() {
        if (!chestImages.isEmpty()) return;
        chestImages.addAll(List.of(
            inyimg, inyimg1, inyimg2, inyimg3, inyimg4, inyimg5, inyimg6, inyimg7, inyimg8, inyimg9,
            inyimg10, inyimg11, inyimg12, inyimg13, inyimg14, inyimg15, inyimg16, inyimg17, inyimg18, inyimg19,
            inyimg20, inyimg21, inyimg22, inyimg23, inyimg24, inyimg25, inyimg26, inyimg27, inyimg28, inyimg29,
            inyimg30, inyimg31, inyimg32, inyimg33, inyimg34, inyimg35, inyimg36, inyimg37, inyimg38, inyimg39,
            inyimg40, inyimg41
        ));
        chestLabels.addAll(List.of(
            invlbl, invlbl1, invlbl2, invlbl3, invlbl4, invlbl5, invlbl6, invlbl7, invlbl8, invlbl9,
            invlbl10, invlbl11, invlbl12, invlbl13, invlbl14, invlbl15, invlbl16, invlbl17, invlbl18, invlbl19,
            invlbl20, invlbl21, invlbl22, invlbl23, invlbl24, invlbl25, invlbl26, invlbl27, invlbl28, invlbl29,
            invlbl30, invlbl31, invlbl32, invlbl33, invlbl34, invlbl35, invlbl36, invlbl37, invlbl38, invlbl39,
            invlbl40, invlbl41
        ));
        chestButtons.addAll(List.of(
            invbtn, invbtn1, invbtn2, invbtn3, invbtn4, invbtn5, invbtn6, invbtn7, invbtn8, invbtn9,
            invbtn10, invbtn11, invbtn12, invbtn13, invbtn14, invbtn15, invbtn16, invbtn17, invbtn18, invbtn19,
            invbtn20, invbtn21, invbtn22, invbtn23, invbtn24, invbtn25, invbtn26, invbtn27, invbtn28, invbtn29,
            invbtn30, invbtn31, invbtn32, invbtn33, invbtn34, invbtn35, invbtn36, invbtn37, invbtn38, invbtn39,
            invbtn40, invbtn41
        ));
    }

    private void wireChestButtons() {
        for (int i = 0; i < chestButtons.size(); i++) {
            final int index = i;
            Button button = chestButtons.get(i);
            if (button != null) {
                button.setOnAction(e -> selectChestSlot(index));
            }
        }
    }

    private void selectChestSlot(int index) {
        List<SlotData> slots = snapshotSlots(chestInventory, CHEST_SLOT_CAPACITY);
        if (index < 0 || index >= slots.size()) return;

        SlotData slot = slots.get(index);
        if (slot == null) return;

        selectedSource = SelectionSource.CHEST;
        selectedItemId = slot.itemId();
        selectedPlayerInventoryItemId = null;

        refreshChestAndInventoryViews();
    }

    private void moveSelectedItem(SelectionSource expectedSource) {
        if (selectedSource != expectedSource || selectedItemId == null) return;

        Inventory from = (expectedSource == SelectionSource.PLAYER) ? player.getInventory() : chestInventory;
        Inventory to = (expectedSource == SelectionSource.PLAYER) ? chestInventory : player.getInventory();
        int targetCapacity = (expectedSource == SelectionSource.PLAYER) ? CHEST_SLOT_CAPACITY : playerInventorySlotCapacity();

        int qty = from.getCount(selectedItemId);
        if (qty <= 0) {
            clearSelection();
            refreshChestAndInventoryViews();
            return;
        }

        if (!canAcceptItemType(to, selectedItemId, targetCapacity)) {
            return;
        }

        if (!from.removeItem(selectedItemId, qty)) {
            return;
        }

        to.addItem(selectedItemId, qty);
        clearSelection();
        refreshChestAndInventoryViews();
    }

    private boolean canAcceptItemType(Inventory inventory, int itemId, int capacity) {
        if (inventory.getCount(itemId) > 0) return true;
        return inventory.itemsProperty().size() < capacity;
    }

    private void clearSelection() {
        selectedSource = SelectionSource.NONE;
        selectedItemId = null;
        clearPlayerInventorySelection();
        refreshChestAndInventoryViews();
    }

    private void refreshChestAndInventoryViews() {
        refreshChestSlots();
        refreshPlayerInventoryView();
        refreshSelectionHighlight();
    }

    private void refreshChestSlots() {
        List<SlotData> slots = snapshotSlots(chestInventory, CHEST_SLOT_CAPACITY);
        int max = Math.max(chestImages.size(), Math.max(chestLabels.size(), chestButtons.size()));

        for (int i = 0; i < max; i++) {
            SlotData slot = (i < slots.size()) ? slots.get(i) : null;

            ImageView imageView = (i < chestImages.size()) ? chestImages.get(i) : null;
            Label label = (i < chestLabels.size()) ? chestLabels.get(i) : null;
            Button button = (i < chestButtons.size()) ? chestButtons.get(i) : null;

            if (slot == null) {
                if (imageView != null) {
                    imageView.setImage(null);
                    imageView.setVisible(false);
                    imageView.setOpacity(0.0);
                    imageView.setScaleX(1.0);
                    imageView.setScaleY(1.0);
                    imageView.setStyle("");
                }
                if (label != null) {
                    label.setText("");
                }
                if (button != null) {
                    button.setDisable(true);
                }
                continue;
            }

            if (imageView != null) {
                imageView.setImage(ItemDatabase.getImage(slot.itemId()));
                imageView.setVisible(true);
                imageView.setOpacity(0.9);
                imageView.setScaleX(1.0);
                imageView.setScaleY(1.0);
                imageView.setStyle("");
            }

            if (label != null) {
                label.setText("x" + slot.quantity());
            }

            if (button != null) {
                button.setDisable(false);
            }
        }
    }
    
//    private void refreshSlots(List<SlotData> slots, List<ImageView> images, List<Label> labels, List<Button> buttons, SelectionSource source) {
//        for (int i = 0; i < images.size(); i++) {
//            SlotData slot = (i < slots.size()) ? slots.get(i) : null;
//            ImageView imageView = images.get(i);
//            Label label = labels.get(i);
//            Button button = buttons.get(i);
//
//            if (slot == null) {
//                if (imageView != null) {
//                    imageView.setImage(null);
//                    imageView.setVisible(false);
//                }
//                if (label != null) {
//                    label.setText("");
//                }
//                if (button != null) {
//                    button.setDisable(true);
//                }
//                continue;
//            }
//
//            if (imageView != null) {
//                imageView.setImage(loadItemImage(slot.itemId()));
//                imageView.setVisible(true);
//            }
//            if (label != null) {
//                label.setText("x" + slot.quantity());
//            }
//            if (button != null) {
//                button.setDisable(false);
//            }
//        }
//
//        refreshSelectionHighlight();
//    }

    private void refreshSelectionHighlight() {
        highlightChestSelection();
        refreshPlayerInventorySelectionHighlight();
    }

    private void highlightChestSelection() {
        List<SlotData> slots = snapshotSlots(chestInventory, CHEST_SLOT_CAPACITY);

        for (int i = 0; i < chestImages.size(); i++) {
            ImageView imageView = chestImages.get(i);
            if (imageView == null) continue;

            SlotData slot = (i < slots.size()) ? slots.get(i) : null;
            boolean selected =
                slot != null &&
                selectedSource == SelectionSource.CHEST &&
                selectedItemId != null &&
                slot.itemId() == selectedItemId;

            imageView.setStyle(selected
                ? "-fx-effect: dropshadow(gaussian, yellow, 14, 0.7, 0, 0);"
                : "");

            if (slot == null) {
                imageView.setOpacity(0.0);
                imageView.setScaleX(1.0);
                imageView.setScaleY(1.0);
            } else {
                imageView.setOpacity(selected ? 1.0 : 0.9);
                imageView.setScaleX(selected ? 1.08 : 1.0);
                imageView.setScaleY(selected ? 1.08 : 1.0);
            }
        }
    }

//    private Image loadItemImage(int itemId) {
//        item itm = ItemDatabase.getItemById(itemId);
//        if (itm == null) return null;
//
//        for (String path : imageCandidates(itm)) {
//            try (InputStream is = getClass().getResourceAsStream(path)) {
//                if (is != null) {
//                    return new Image(is);
//                }
//            } catch (Exception ignored) {
//            }
//        }
//        return null;
//    }

//    private List<String> imageCandidates(item itm) {
//        String name = itm.getItem_name();
//        String normalized = name.toLowerCase()
//            .replace(" ", "_")
//            .replace("-", "_")
//            .replace("'", "")
//            .replace("__", "_");
//
//        List<String> candidates = new ArrayList<>();
//        candidates.add("/image/items/" + normalized + ".png");
//        if (normalized.endsWith("_seeds")) {
//            candidates.add("/image/items/" + normalized.replace("_seeds", "") + "_seeds.png");
//        }
//        if (normalized.endsWith("s") && !normalized.endsWith("ss")) {
//            candidates.add("/image/items/" + normalized.substring(0, normalized.length() - 1) + ".png");
//        }
//        return candidates;
//    }
//
//    private String itemName(int itemId) {
//        item itm = ItemDatabase.getItemById(itemId);
//        return (itm == null) ? ("item-" + itemId) : itm.getItem_name();
//    }
    
}