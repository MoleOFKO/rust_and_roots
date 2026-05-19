/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import util.SpawnTag;

/**
 * FXML Controller class
 *
 * @author Kaung Zin
 */
public class FinalbossController extends BaseMapController {
    
    private static final String DAY_BG_PATH = "/image/maps/finalboss.png";
    private static final String NIGHT_BG_PATH = "/image/NightMap/finalboss_night.png";

    @FXML
    private StackPane view;
    @FXML
    private Group world;
    @FXML
    private Group human;
    @FXML
    private ImageView human_img;
    @FXML
    private ImageView equip_img;
    @FXML
    private Rectangle human_hitbox;
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
    private Rectangle wall27;
    @FXML
    private Rectangle wall28;
    @FXML
    private Rectangle wall29;
    @FXML
    private Rectangle wall30;
    @FXML
    private Rectangle wall31;
    @FXML
    private Rectangle exit_down;
    @FXML
    private Rectangle spawn_default;
    @FXML
    private Rectangle spawn_from_down;
    @FXML
    private Group daynightnavgroup;
    @FXML
    private Label day;
    @FXML
    private ImageView daynightcircleimg;
    @FXML
    private Label time;
    @FXML
    private Label name;
    @FXML
    private Label coin;
    @FXML
    private ProgressBar hpbar;
    @FXML
    private ProgressBar spbar;
    @FXML
    private Label hotbarguide;
    @FXML
    private HBox hotbar;
    @FXML
    private StackPane Hslot;
    @FXML
    private ImageView slotImg;
    @FXML
    private Label slotLb;
    @FXML
    private ToggleButton slotBtn;
    @FXML
    private ToggleGroup selectedhotbar;
    @FXML
    private StackPane Hslot1;
    @FXML
    private ImageView slotImg1;
    @FXML
    private Label slotLb1;
    @FXML
    private ToggleButton slotBtn1;
    @FXML
    private StackPane Hslot2;
    @FXML
    private ImageView slotImg2;
    @FXML
    private Label slotLb2;
    @FXML
    private ToggleButton slotBtn2;
    @FXML
    private StackPane Hslot3;
    @FXML
    private ImageView slotImg3;
    @FXML
    private Label slotLb3;
    @FXML
    private ToggleButton slotBtn3;
    @FXML
    private StackPane Hslot4;
    @FXML
    private ImageView slotImg4;
    @FXML
    private Label slotLb4;
    @FXML
    private ToggleButton slotBtn4;
    @FXML
    private StackPane Hslot5;
    @FXML
    private Button slotBtn5;
    @FXML
    private Pane inventoryOverlay;
    @FXML
    private Group menuInventory;
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
    private Group statusTab;
    @FXML
    private Label statusName;
    @FXML
    private Label statusEXP;
    @FXML
    private ImageView btnUsePoint;
    @FXML
    private ImageView btnConfirm;
    @FXML
    private Group statusgroup;
    @FXML
    private Label statusHP;
    @FXML
    private Label statusSP;
    @FXML
    private Label statusSTR;
    @FXML
    private Label statusVIT;
    @FXML
    private Label statusDEX;
    @FXML
    private Label statusAGI;
    @FXML
    private ImageView btnskill;
    @FXML
    private ImageView btnHPminus;
    @FXML
    private ImageView btnHPplus;
    @FXML
    private ImageView btnSPminus;
    @FXML
    private ImageView btnSTRminus;
    @FXML
    private ImageView btnVITminus;
    @FXML
    private ImageView btnDEXminus;
    @FXML
    private ImageView btnAGIminus;
    @FXML
    private ImageView btnSPplus;
    @FXML
    private ImageView btnSTRplus;
    @FXML
    private ImageView btnVITplus;
    @FXML
    private ImageView btnDEXplus;
    @FXML
    private ImageView btnAGIplus;
    @FXML
    private Group skillsgroup;
    @FXML
    private ImageView btnstatus;
    @FXML
    private Label farming;
    @FXML
    private Label gathering;
    @FXML
    private Label crafting;
    @FXML
    private Label combat;
    @FXML
    private Label cooking;
    @FXML
    private ImageView btnfarmingminus1;
    @FXML
    private ImageView btnfarmingplus1;
    @FXML
    private ImageView btngatheringminus1;
    @FXML
    private ImageView btncraftingminus1;
    @FXML
    private ImageView btncombatminus1;
    @FXML
    private ImageView btncookingminus1;
    @FXML
    private ImageView btngatheringplus1;
    @FXML
    private ImageView btncraftingplus1;
    @FXML
    private ImageView btncombatplus1;
    @FXML
    private ImageView btncookingplus1;
    @FXML
    private Group settingsTab;
    @FXML
    private Slider bgmVolumeSlider;
    @FXML
    private Slider sfxVolumeSlider;
    @FXML
    private Button btnsave;
    @FXML
    private Button btnload;
    @FXML
    private Button btnsaveandexit;
    @FXML
    private Button openInventory;
    @FXML
    private ImageView btnclose;
    @FXML
    private Button openstatus;
    @FXML
    private Button opensetting;
    @FXML
    private Button btntuto;
    @FXML
    private Group orcb;
    @FXML
    private Rectangle orcb_hitbox;
    @FXML
    private ImageView orcb_img;
    @FXML
    private ImageView orcb_drops_img;
    @FXML
    private Rectangle orcb_hitbox_left;
    @FXML
    private Rectangle orcb_hitbox_up;
    @FXML
    private Rectangle orcb_hitbox_down;
    @FXML
    private Rectangle orcb_hitbox_right;
    @FXML
    private Rectangle orcb_detect;

//    /**
//     * Initializes the controller class.
//     */
//    @Override
//    public void initialize(URL url, ResourceBundle rb) {
//        // TODO
//    }    
//
//    @FXML
//    private void handleusepointaction(MouseEvent event) {
//    }
//
//    @FXML
//    private void handleconfirmaction(MouseEvent event) {
//    }
//
//    @FXML
//    private void handlebtnskillacion(MouseEvent event) {
//    }
//
//    @FXML
//    private void handleHPminusaction(MouseEvent event) {
//    }
//
//    @FXML
//    private void handleHPplusaction(MouseEvent event) {
//    }
//
//    @FXML
//    private void handleSPminusaction(MouseEvent event) {
//    }
//
//    @FXML
//    private void handleSTRminusaction(MouseEvent event) {
//    }
//
//    @FXML
//    private void handleVITminusaction(MouseEvent event) {
//    }
//
//    @FXML
//    private void handleDEXminusaction(MouseEvent event) {
//    }
//
//    @FXML
//    private void handleAGIminusaction(MouseEvent event) {
//    }
//
//    @FXML
//    private void handleSPplusaction(MouseEvent event) {
//    }
//
//    @FXML
//    private void handleSTRplusaction(MouseEvent event) {
//    }
//
//    @FXML
//    private void handleVITplusaction(MouseEvent event) {
//    }
//
//    @FXML
//    private void handleDEXplusaction(MouseEvent event) {
//    }
//
//    @FXML
//    private void handleAGIplusaction(MouseEvent event) {
//    }
//
//    @FXML
//    private void handlebtnstatusaction(MouseEvent event) {
//    }
//
//    @FXML
//    private void handlefarmingminusaction(MouseEvent event) {
//    }
//
//    @FXML
//    private void handlefarmingplusaction(MouseEvent event) {
//    }
//
//    @FXML
//    private void handlegatheringminusaction(MouseEvent event) {
//    }
//
//    @FXML
//    private void handlecraftingminusaction(MouseEvent event) {
//    }
//
//    @FXML
//    private void handlecombatminusaction(MouseEvent event) {
//    }
//
//    @FXML
//    private void handlecookingminusaction(MouseEvent event) {
//    }
//
//    @FXML
//    private void handlegatheringplusaction(MouseEvent event) {
//    }
//
//    @FXML
//    private void handlecraftingplusaction(MouseEvent event) {
//    }
//
//    @FXML
//    private void handlecombatplusaction(MouseEvent event) {
//    }
//
//    @FXML
//    private void handlecookingplusaction(MouseEvent event) {
//    }
//
//    @FXML
//    private void handlesave(ActionEvent event) {
//    }
//
//    @FXML
//    private void handleload(ActionEvent event) {
//    }
//
//    @FXML
//    private void handlesaveexitaction(ActionEvent event) {
//    }
//
//    @FXML
//    private void handleopenInventoryaction(ActionEvent event) {
//    }
//
//    @FXML
//    private void handlecloseaction(MouseEvent event) {
//    }
//
//    @FXML
//    private void handleopenstatusaction(ActionEvent event) {
//    }
//
//    @FXML
//    private void handleopensettingaction(ActionEvent event) {
//    }
//
    @FXML
    private void handlebtntutoaction(ActionEvent event) {
        showOverworldTutorialOverlay();
    }

    @Override
    protected String mapKey() {
        return "finalboss";
    }
    
    @Override
    protected String dayBackgroundPath() {
        return DAY_BG_PATH;
    }

    @Override
    protected String nightBackgroundPath() {
        return NIGHT_BG_PATH;
    }

    @Override
    protected void configureExits() {
        registerExit(exit_down, "/view/right_map_notwo_of_two.fxml", SpawnTag.FROM_UP);
    }
}
