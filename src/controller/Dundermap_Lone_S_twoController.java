/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import controller.BaseMapController;
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
 * @author user
 */
public class Dundermap_Lone_S_twoController extends BaseMapController {
    
    private static final String DAY_BG_PATH = "/image/dungeon/dundermap_L(1)S(2).png";
    private static final String NIGHT_BG_PATH = "";

    @FXML
    private StackPane view;
    @FXML
    private Group world;
    @FXML
    private ImageView bg;
    @FXML
    private Group walls;
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
    private Rectangle exit_up;
    @FXML
    private Rectangle exit_right;
    @FXML
    private Label hotbarguide;
    @FXML
    private Group human;
    @FXML
    private Rectangle human_hitbox;
    @FXML
    private ImageView human_img;
    @FXML
    private ImageView equip_img;
    @FXML
    private Rectangle human_hitbox_left;
    @FXML
    private Rectangle human_hitbox_up;
    @FXML
    private Rectangle human_hitbox_down;
    @FXML
    private Rectangle human_hitbox_right;
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
    private Rectangle spawn_from_up;
    @FXML
    private Rectangle spawn_from_right;
    @FXML
    private Rectangle spawn_default;
    @FXML
    private Group silver;
    @FXML
    private ImageView silver_img;
    @FXML
    private Rectangle silver_hitbox;
    @FXML
    private ImageView drops_img_silver;
    @FXML
    private Group silver1;
    @FXML
    private ImageView silver_img1;
    @FXML
    private Rectangle silver_hitbox1;
    @FXML
    private ImageView drops_img_silver1;
    @FXML
    private Group silver2;
    @FXML
    private ImageView silver_img2;
    @FXML
    private Rectangle silver_hitbox2;
    @FXML
    private ImageView drops_img_silver2;
    @FXML
    private Group silver3;
    @FXML
    private ImageView silver_img3;
    @FXML
    private Rectangle silver_hitbox3;
    @FXML
    private ImageView drops_img_silver3;
    @FXML
    private Group silver4;
    @FXML
    private ImageView silver_img4;
    @FXML
    private Rectangle silver_hitbox4;
    @FXML
    private ImageView drops_img_silver4;
    @FXML
    private Group silver5;
    @FXML
    private ImageView silver_img5;
    @FXML
    private Rectangle silver_hitbox5;
    @FXML
    private ImageView drops_img_silver5;
    @FXML
    private Group gold;
    @FXML
    private ImageView gold_img;
    @FXML
    private Rectangle gold_hitbox;
    @FXML
    private ImageView drops_img_gold;
    @FXML
    private Group gold1;
    @FXML
    private ImageView gold_img1;
    @FXML
    private Rectangle gold_hitbox1;
    @FXML
    private ImageView drops_img_gold1;
    @FXML
    private Group gold2;
    @FXML
    private ImageView gold_img2;
    @FXML
    private Rectangle gold_hitbox2;
    @FXML
    private ImageView drops_img_gold2;
    @FXML
    private Group gold3;
    @FXML
    private ImageView gold_img3;
    @FXML
    private Rectangle gold_hitbox3;
    @FXML
    private ImageView drops_img_gold3;
    @FXML
    private Group gold4;
    @FXML
    private ImageView gold_img4;
    @FXML
    private Rectangle gold_hitbox4;
    @FXML
    private ImageView drops_img_gold4;
    @FXML
    private Group gold5;
    @FXML
    private ImageView gold_img5;
    @FXML
    private Rectangle gold_hitbox5;
    @FXML
    private ImageView drops_img_gold5;
    @FXML
    private Group mithril;
    @FXML
    private ImageView mithril_img;
    @FXML
    private Rectangle mithril_hitbox;
    @FXML
    private ImageView drops_img_mithril;
    @FXML
    private Group mithril1;
    @FXML
    private ImageView mithril_img1;
    @FXML
    private Rectangle mithril_hitbox1;
    @FXML
    private ImageView drops_img_mithril1;
    
    @Override
    protected String dayBackgroundPath() {
        return DAY_BG_PATH;
    }

    @Override
    protected String nightBackgroundPath() {
        return NIGHT_BG_PATH;
    }
    
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
    private Slider timescaleslider;
    @FXML
    private Button btnsetday;
    @FXML
    private Button btnsetnight;
    @FXML
    private Button btnplus1h;
    @FXML
    private Button btnsaveandexit;
    @FXML
    private Label sliderscale;
    @FXML
    private Label lbldays;
    @FXML
    private Label lbltime;
    @FXML
    private Button btnsave;
    @FXML
    private Button btnload;
    @FXML
    private Button openInventory;
    @FXML
    private ImageView btnclose;
    @FXML
    private Button openstatus;
    @FXML
    private Button opensetting;

    /**
     * Initializes the controller class.
     */
    @Override
    protected String mapKey() {
        return "dundermap_Lone_S_two";
    }

    @Override
    protected void configureExits() {
        registerExit(exit_right, "/view/dundermap_right_L_one_S_two.fxml", SpawnTag.FROM_LEFT);
        registerExit(exit_up, "/view/dhome_L_one_S_two.fxml", SpawnTag.FROM_DOWN);
    }
}
