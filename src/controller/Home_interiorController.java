/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import game.GameTimeManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import model.InteractionZone;
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
        chestStackPane.setDisable(false);
        chestStackPane.setVisible(true);
    }

    @FXML
    private void handlecraftaction(ActionEvent event) {
    }

    @FXML
    private void handlecookaction(ActionEvent event) {
    }

    @FXML
    private void sendToChestClick(MouseEvent event) {
    }

    @FXML
    private void sendToInventoryClick(MouseEvent event) {
    }

    @FXML
    private void backClick(MouseEvent event) {
    }
    
}
