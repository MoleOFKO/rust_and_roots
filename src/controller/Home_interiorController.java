/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import db.SaveGameService;
import game.GameState;
import game.GameTimeManager;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.collections.MapChangeListener;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import model.Inventory;
import model.ItemDatabase;
import model.item;
import javafx.beans.binding.Bindings;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import model.PlayerState;
import util.SceneManager;
import util.TutorialOverlayHelper;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.geometry.Bounds;
import javafx.animation.FadeTransition;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author larry
 */
public class Home_interiorController extends BaseMapController {

    private static final String DAY_BG_PATH = "/image/inhousedesign.png";
    private static final String NIGHT_BG_PATH = "";
    private static final List<String> HOME_TUTORIAL_IMAGE_PATHS = List.of(
        "/image/Toturial/Move.png",
        "/image/Toturial/Run.png",
        "/image/Toturial/before_goes_to_hotbar.png",
        "/image/Toturial/goes_to_hotbar.png",
        "/image/Toturial/Menu.png",
        "/image/Toturial/Inventory.png",
        "/image/Toturial/Stats.png",
        "/image/Toturial/Upgrade Stats.png",
        "/image/Toturial/Skill.png",
        "/image/Toturial/Upgrade Skill.png",
        "/image/Toturial/Setting.png",
        "/image/Toturial/Skill.png"
    );
    @FXML
    private ImageView bg;
    @FXML
    private Button btntuto;
    @FXML
    private Group minimapicongroup;
    @FXML
    private ImageView miniMapIcon;
    @FXML
    private Group minimapgroup;
    @FXML
    private ImageView minimap;
    @FXML
    private ImageView closeminimapbtn;
    @FXML
    private Rectangle left_map_nofour;
    @FXML
    private Rectangle upper_map_nofour;
    @FXML
    private Rectangle right_map_nofour;
    @FXML
    private Rectangle left_map_nothree_of_one;
    @FXML
    private Rectangle left_map_nothree;
    @FXML
    private Rectangle upper_map_nothree;
    @FXML
    private Rectangle right_map_nothree;
    @FXML
    private Rectangle right_map_nothree_of_one;
    @FXML
    private Rectangle left_map_notwo_of_two;
    @FXML
    private Rectangle left_map_notwo_of_one;
    @FXML
    private Rectangle left_map_notwo;
    @FXML
    private Rectangle upper_map_notwo;
    @FXML
    private Rectangle right_map_notwo;
    @FXML
    private Rectangle right_map_notwo_of_one;
    @FXML
    private Rectangle left_map_noone_of_one;
    @FXML
    private Rectangle left_map_noone;
    @FXML
    private Rectangle upper_map;
    @FXML
    private Rectangle right_map_noone;
    @FXML
    private Rectangle right_map_noone_of_one;
    @FXML
    private Rectangle left_map;
    @FXML
    private Rectangle home_map;
    @FXML
    private Rectangle right_map;
    @FXML
    private Rectangle home_interior;
    @FXML
    private Slider bgmVolumeSlider;
    @FXML
    private Slider sfxVolumeSlider;
    @FXML
    private Button tutoclose;

    @Override
    protected String dayBackgroundPath() {
        return DAY_BG_PATH;
    }

    @Override
    protected String nightBackgroundPath() {
        return NIGHT_BG_PATH;
    }

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
    private StackPane tutorialOverlay;
    @FXML
    private Rectangle tutorialShade;
    @FXML
    private Pane tutorialOverlayContent;
    @FXML
    private ImageView tutorialImageView;
    @FXML
    private Label tutorialTitleLabel;
    @FXML
    private Label tutorialPromptLabel;
    @FXML
    private VBox tutorialPromptBox;
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

    private javafx.animation.AnimationTimer dialogOpacityTimer;
    private boolean cookingDialogDimmed = false;
    private FadeTransition cookingDialogFade;
    private int tutorialIndex = -1;
    private boolean tutorialOverlayActive = false;
    
    private static final int CHEST_SLOT_CAPACITY = 42;;
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
    private Group player_inv_group1;
    @FXML
    private ImageView Playerinvimg21;
    @FXML
    private Button Playerinvbtn21;
    @FXML
    private Label Playerinvlbl21;
    @FXML
    private ImageView Playerinvimg31;
    @FXML
    private Button Playerinvbtn31;
    @FXML
    private Label Playerinvlbl31;
    @FXML
    private ImageView Playerinvimg41;
    @FXML
    private Button Playerinvbtn41;
    @FXML
    private Label Playerinvlbl41;
    @FXML
    private ImageView Playerinvimg51;
    @FXML
    private Button Playerinvbtn51;
    @FXML
    private Label Playerinvlbl51;
    @FXML
    private ImageView Playerinvimg61;
    @FXML
    private Button Playerinvbtn61;
    @FXML
    private Label Playerinvlbl61;
    @FXML
    private ImageView Playerinvimg71;
    @FXML
    private Button Playerinvbtn71;
    @FXML
    private Label Playerinvlbl71;
    @FXML
    private ImageView Playerinvimg81;
    @FXML
    private Button Playerinvbtn81;
    @FXML
    private Label Playerinvlbl81;
    @FXML
    private ImageView Playerinvimg91;
    @FXML
    private Button Playerinvbtn91;
    @FXML
    private Label Playerinvlbl91;
    @FXML
    private ImageView Playerinvimg101;
    @FXML
    private Button Playerinvbtn101;
    @FXML
    private Label Playerinvlbl101;
    @FXML
    private ImageView Playerinvimg111;
    @FXML
    private Button Playerinvbtn111;
    @FXML
    private Label Playerinvlbl111;
    @FXML
    private ImageView Playerinvimg121;
    @FXML
    private Button Playerinvbtn121;
    @FXML
    private Label Playerinvlbl121;
    @FXML
    private ImageView Playerinvimg131;
    @FXML
    private Button Playerinvbtn131;
    @FXML
    private Label Playerinvlbl131;
    @FXML
    private ImageView Playerinvimg141;
    @FXML
    private Button Playerinvbtn141;
    @FXML
    private Label Playerinvlbl141;
    @FXML
    private ImageView Playerinvimg151;
    @FXML
    private Button Playerinvbtn151;
    @FXML
    private Label Playerinvlbl151;
    @FXML
    private ImageView Playerinvimg161;
    @FXML
    private Button Playerinvbtn161;
    @FXML
    private Label Playerinvlbl161;
    @FXML
    private ImageView Playerinvimg171;
    @FXML
    private Button Playerinvbtn171;
    @FXML
    private Label Playerinvlbl171;
    @FXML
    private ImageView Playerinvimg181;
    @FXML
    private Button Playerinvbtn181;
    @FXML
    private Label Playerinvlbl181;
    @FXML
    private ImageView Playerinvimg191;
    @FXML
    private Button Playerinvbtn191;
    @FXML
    private Label Playerinvlbl191;
    @FXML
    private Group statusTab;
    @FXML
    private Label statusName;
    @FXML
    private Label statusEXP;
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
    private ImageView btnUsePoint;
    @FXML
    private ImageView btnConfirm;
    @FXML
    private Group settingsTab;
    private Slider timescaleslider;
    private Button btnsetday;
    private Button btnsetnight;
    private Button btnplus1h;
    @FXML
    private Button btnsaveandexit;
    private Label sliderscale;
    private Label lbldays;
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
    @FXML
    private Label hotbarguide;
    @FXML
    private Group statusgroup;
    @FXML
    private ImageView btnskill;
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
    private ImageView Playerinvimg112;
    @FXML
    private Button Playerinvbtn112;
    @FXML
    private Label Playerinvlbl112;
    @FXML
    private ImageView Playerinvimg110;
    @FXML
    private Button Playerinvbtn110;
    @FXML
    private Label Playerinvlbl110;
    @FXML
    private Group skillsgroup;
    @FXML
    private ToggleGroup selectedhotbar;
    @FXML
    private Group dialogboxes;
    @FXML
    private ImageView cookingdialogimg;
    @FXML
    private ImageView craftingdialogimg;
    @FXML
    private ImageView invdialogimg;
    @FXML
    private ImageView meltdialogimg;
    @FXML
    private ImageView sleepingdialogimg;
    @FXML
    private ImageView equip_img;
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

    private void bindSettingControls() {
        GameTimeManager time = GameTimeManager.get();

        if (timescaleslider != null) {
            timescaleslider.valueProperty().unbindBidirectional(time.timeScaleProperty());
            timescaleslider.valueProperty().bindBidirectional(time.timeScaleProperty());
        }

        if (sliderscale != null) {
            sliderscale.textProperty().unbind();
            sliderscale.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("Current Time Speed : x%.1f", time.timeScaleProperty().get()),
                time.timeScaleProperty()
            ));
        }

        if (lbldays != null) {
            lbldays.textProperty().unbind();
            lbldays.textProperty().bind(Bindings.createStringBinding(
                () -> "Days : " + time.dayNumberProperty().get(),
                time.dayNumberProperty()
            ));
        }

        if (lbltime != null) {
            lbltime.textProperty().unbind();
            lbltime.textProperty().bind(Bindings.createStringBinding(
                () -> "Time : " + time.formattedProperty().get(),
                time.formattedProperty()
            ));
        }

        if (btnsetday != null) btnsetday.setOnAction(e -> time.setTimeOfDay(8, 0));
        if (btnsetnight != null) btnsetnight.setOnAction(e -> time.setTimeOfDay(20, 0));
        if (btnplus1h != null) btnplus1h.setOnAction(e -> time.addMinutes(60));
        if (btnsaveandexit != null) {
            btnsaveandexit.setOnAction(e -> {
                try {
                    capturePlayerSnapshot();
                    new SaveGameService().saveGame(GameState.get());
                    if (gameLoop != null) gameLoop.stop();
                    if (player != null) player.stop();
                    GameState.get().setSaveMenuMode(false);
                    util.SceneManager.switchScene("/view/main_menu.fxml");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    private void bindStatusLabels() {
        if (playerState == null) return;

        playerState.playerNameProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
        playerState.expProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
        playerState.hpProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
        playerState.maxHpProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
        playerState.staminaProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
        playerState.maxStaminaProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
        playerState.strProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
        playerState.vitProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
        playerState.dexProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
        playerState.agiProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
    }

    @Override
    protected void refreshStatusAllocationUi() {
        refreshStatusView();
        refreshSkillsView();
        refreshStatusButtons();
        refreshSleepInteractionState();
    }

    private void refreshStatusView() {
        if (playerState == null) return;

        if (statusName != null) statusName.setText("Name : " + playerState.getPlayerName());
        if (statusEXP != null) {
            statusEXP.setText(formatVisibleExpLabel());
        }
        if (statusHP != null) {
            statusHP.setText("HP : " + playerState.getHp() + "/" + (playerState.getMaxHp() + pendingHpPoints) + formatPending(pendingHpPoints));
        }
        if (statusSP != null) {
            statusSP.setText("SP : " + playerState.getStamina() + "/" + (playerState.getMaxStamina() + pendingSpPoints) + formatPending(pendingSpPoints));
        }
        if (statusSTR != null) statusSTR.setText("STR : " + playerState.getStr() + formatPending(pendingStrPoints));
        if (statusVIT != null) statusVIT.setText("VIT : " + playerState.getVit() + formatPending(pendingVitPoints));
        if (statusDEX != null) statusDEX.setText("DEX : " + playerState.getDex() + formatPending(pendingDexPoints));
        if (statusAGI != null) statusAGI.setText("AGI : " + playerState.getAgi() + formatPending(pendingAgiPoints));
    }

    private String formatPending(int points) {
        return points <= 0 ? "" : "(+" + points + ")";
    }

    private void refreshSkillsView() {
        if (gameState == null) return;
        GameState.PlayerSkills skills = gameState.getPlayerSkills();

        if (farming != null) farming.setText(appendPendingSkillCost(skills.getFarmingDisplay(), pendingFarmingSkillExp));
        if (gathering != null) gathering.setText(appendPendingSkillCost(skills.getGatheringDisplay(), pendingGatheringSkillExp));
        if (crafting != null) crafting.setText(appendPendingSkillCost(skills.getCraftingDisplay(), pendingCraftingSkillExp));
        if (combat != null) combat.setText(appendPendingSkillCost(skills.getCombatDisplay(), pendingCombatSkillExp));
        if (cooking != null) cooking.setText(appendPendingSkillCost(skills.getCookingDisplay(), pendingCookingSkillExp));
    }

    private void refreshStatusButtons() {
        boolean canUsePoint = playerState != null && playerState.getExp() > 0;
        boolean skillsVisible = isSkillsScreenVisible();

        configureImageButton(btnUsePoint, canUsePoint, true, statusAllocationMode || skillAllocationMode ? 1.0 : 0.85);

        if (skillsVisible) {
            configureImageButton(btnConfirm, skillAllocationMode && getPendingSkillExpTotal() > 0, skillAllocationMode, 1.0);

            configureImageButton(btnfarmingplus1, canIncreaseSkill("farming"), skillAllocationMode, 1.0);
            configureImageButton(btngatheringplus1, canIncreaseSkill("gathering"), skillAllocationMode, 1.0);
            configureImageButton(btncraftingplus1, canIncreaseSkill("crafting"), skillAllocationMode, 1.0);
            configureImageButton(btncombatplus1, canIncreaseSkill("combat"), skillAllocationMode, 1.0);
            configureImageButton(btncookingplus1, canIncreaseSkill("cooking"), skillAllocationMode, 1.0);

            configureImageButton(btnfarmingminus1, skillAllocationMode && pendingFarmingSkillExp > 0, skillAllocationMode, 1.0);
            configureImageButton(btngatheringminus1, skillAllocationMode && pendingGatheringSkillExp > 0, skillAllocationMode, 1.0);
            configureImageButton(btncraftingminus1, skillAllocationMode && pendingCraftingSkillExp > 0, skillAllocationMode, 1.0);
            configureImageButton(btncombatminus1, skillAllocationMode && pendingCombatSkillExp > 0, skillAllocationMode, 1.0);
            configureImageButton(btncookingminus1, skillAllocationMode && pendingCookingSkillExp > 0, skillAllocationMode, 1.0);

            configureImageButton(btnHPplus, false, false, 1.0);
            configureImageButton(btnSPplus, false, false, 1.0);
            configureImageButton(btnSTRplus, false, false, 1.0);
            configureImageButton(btnVITplus, false, false, 1.0);
            configureImageButton(btnDEXplus, false, false, 1.0);
            configureImageButton(btnAGIplus, false, false, 1.0);
            configureImageButton(btnHPminus, false, false, 1.0);
            configureImageButton(btnSPminus, false, false, 1.0);
            configureImageButton(btnSTRminus, false, false, 1.0);
            configureImageButton(btnVITminus, false, false, 1.0);
            configureImageButton(btnDEXminus, false, false, 1.0);
            configureImageButton(btnAGIminus, false, false, 1.0);
            return;
        }

        if(isStatusScreenVisible()){
            configureImageButton(btnConfirm, statusAllocationMode && getPendingExpCost() > 0, statusAllocationMode, 1.0);

            configureImageButton(btnHPplus, canIncreaseStat("hp"), statusAllocationMode, 1.0);
            configureImageButton(btnSPplus, canIncreaseStat("sp"), statusAllocationMode, 1.0);
            configureImageButton(btnSTRplus, canIncreaseStat("str"), statusAllocationMode, 1.0);
            configureImageButton(btnVITplus, canIncreaseStat("vit"), statusAllocationMode, 1.0);
            configureImageButton(btnDEXplus, canIncreaseStat("dex"), statusAllocationMode, 1.0);
            configureImageButton(btnAGIplus, canIncreaseStat("agi"), statusAllocationMode, 1.0);

            configureImageButton(btnHPminus, statusAllocationMode && pendingHpPoints > 0, statusAllocationMode, 1.0);
            configureImageButton(btnSPminus, statusAllocationMode && pendingSpPoints > 0, statusAllocationMode, 1.0);
            configureImageButton(btnSTRminus, statusAllocationMode && pendingStrPoints > 0, statusAllocationMode, 1.0);
            configureImageButton(btnVITminus, statusAllocationMode && pendingVitPoints > 0, statusAllocationMode, 1.0);
            configureImageButton(btnDEXminus, statusAllocationMode && pendingDexPoints > 0, statusAllocationMode, 1.0);
            configureImageButton(btnAGIminus, statusAllocationMode && pendingAgiPoints > 0, statusAllocationMode, 1.0);

            configureImageButton(btnfarmingplus1, false, false, 1.0);
            configureImageButton(btngatheringplus1, false, false, 1.0);
            configureImageButton(btncraftingplus1, false, false, 1.0);
            configureImageButton(btncombatplus1, false, false, 1.0);
            configureImageButton(btncookingplus1, false, false, 1.0);
            configureImageButton(btnfarmingminus1, false, false, 1.0);
            configureImageButton(btngatheringminus1, false, false, 1.0);
            configureImageButton(btncraftingminus1, false, false, 1.0);
            configureImageButton(btncombatminus1, false, false, 1.0);
            configureImageButton(btncookingminus1, false, false, 1.0);
        }
    }

    private void configureImageButton(ImageView imageView, boolean enabled, boolean visible, double activeOpacity) {
        if (imageView == null) return;
        imageView.setVisible(visible);
        imageView.setManaged(visible);
        imageView.setDisable(!enabled);
        imageView.setMouseTransparent(!visible || !enabled);
        imageView.setOpacity(!visible ? 0.0 : (enabled ? activeOpacity : 0.45));
    }

    private final List<ImageView> chestImages = new ArrayList<>();
    private final List<Label> chestLabels = new ArrayList<>();
    private final List<Button> chestButtons = new ArrayList<>();

    private Inventory chestInventory;
    private final LinkedHashSet<Integer> selectedChestSlotIndices = new LinkedHashSet<>();
    private final LinkedHashSet<Integer> selectedInteriorPlayerSlotIndices = new LinkedHashSet<>();
    private boolean insideBedZone = false;
    private javafx.animation.AnimationTimer sleepTimer;
    private double sleepPreviousTimeScale = 1.0;
    private boolean sleepTimeScaleOverrideActive = false;

    private static final int SLEEP_MINUTES_PER_STAMINA_POINT = 10;
    private static final int SLEEP_MINUTES_PER_HP_POINT = 10;
    private static final int SLEEP_MAX_ADVANCE_MINUTES = 24 * 60;

    @Override
    protected String mapKey() {
        return "Home_interior";
    }

    @Override
    protected void configureExits() {
        registerExit(exit_door, "/view/home_map.fxml", SpawnTag.FROM_DOOR);
    }

    private final List<ImageView> pinvImages = new ArrayList<>();
    private final List<Label> pinvLabels = new ArrayList<>();
    private final List<Button> pinvButtons = new ArrayList<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        bridgeInteriorSharedUi();
        setupFloatingDialogs();
        setupDialogOpacityCheck();
        ItemDatabase.loadItems();
        chestInventory = gameState.getChestInventory();
        setupChestUi();
        setupInteriorZones();
//        setupMenuTabs();
//        bindStatusLabels();
        

        if (btncook != null) btncook.setOnAction(this::handlecookaction);
        if (btncraft != null) btncraft.setOnAction(this::handlecraftaction);
        if (btnmelt != null) btnmelt.setOnAction(e -> openMelt());

        pinvImages.clear();
        pinvLabels.clear();
        pinvButtons.clear();

        pinvImages.add(Playerinvimg);
        pinvImages.add(Playerinvimg1);
        pinvImages.add(Playerinvimg2);
        pinvImages.add(Playerinvimg3);
        pinvImages.add(Playerinvimg4);
        pinvImages.add(Playerinvimg5);
        pinvImages.add(Playerinvimg6);
        pinvImages.add(Playerinvimg7);
        pinvImages.add(Playerinvimg8);
        pinvImages.add(Playerinvimg9);
        pinvImages.add(Playerinvimg10);
        pinvImages.add(Playerinvimg11);
        pinvImages.add(Playerinvimg12);
        pinvImages.add(Playerinvimg13);
        pinvImages.add(Playerinvimg14);
        pinvImages.add(Playerinvimg15);
        pinvImages.add(Playerinvimg16);
        pinvImages.add(Playerinvimg17);
        pinvImages.add(Playerinvimg18);
        pinvImages.add(Playerinvimg19);

        pinvLabels.add(Playerinvlbl);
        pinvLabels.add(Playerinvlbl1);
        pinvLabels.add(Playerinvlbl2);
        pinvLabels.add(Playerinvlbl3);
        pinvLabels.add(Playerinvlbl4);
        pinvLabels.add(Playerinvlbl5);
        pinvLabels.add(Playerinvlbl6);
        pinvLabels.add(Playerinvlbl7);
        pinvLabels.add(Playerinvlbl8);
        pinvLabels.add(Playerinvlbl9);
        pinvLabels.add(Playerinvlbl10);
        pinvLabels.add(Playerinvlbl11);
        pinvLabels.add(Playerinvlbl12);
        pinvLabels.add(Playerinvlbl13);
        pinvLabels.add(Playerinvlbl14);
        pinvLabels.add(Playerinvlbl15);
        pinvLabels.add(Playerinvlbl16);
        pinvLabels.add(Playerinvlbl17);
        pinvLabels.add(Playerinvlbl18);
        pinvLabels.add(Playerinvlbl19);

        pinvButtons.add(Playerinvbtn);
        pinvButtons.add(Playerinvbtn1);
        pinvButtons.add(Playerinvbtn2);
        pinvButtons.add(Playerinvbtn3);
        pinvButtons.add(Playerinvbtn4);
        pinvButtons.add(Playerinvbtn5);
        pinvButtons.add(Playerinvbtn6);
        pinvButtons.add(Playerinvbtn7);
        pinvButtons.add(Playerinvbtn8);
        pinvButtons.add(Playerinvbtn9);
        pinvButtons.add(Playerinvbtn10);
        pinvButtons.add(Playerinvbtn11);
        pinvButtons.add(Playerinvbtn12);
        pinvButtons.add(Playerinvbtn13);
        pinvButtons.add(Playerinvbtn14);
        pinvButtons.add(Playerinvbtn15);
        pinvButtons.add(Playerinvbtn16);
        pinvButtons.add(Playerinvbtn17);
        pinvButtons.add(Playerinvbtn18);
        pinvButtons.add(Playerinvbtn19);

        wireInteriorPlayerButtons();
        refreshInteriorInventorySlots();

        Inventory inv = player.getInventory();
        inv.itemsProperty().addListener(
            (MapChangeListener<Integer, IntegerProperty>) change -> refreshChestAndInventoryViews()
        );
        chestInventory.itemsProperty().addListener(
            (MapChangeListener<Integer, IntegerProperty>) change -> refreshChestAndInventoryViews()
        );

        Platform.runLater(this::showHomeInteriorTutorialIfNeeded);
    }

    private void showHomeInteriorTutorialIfNeeded() {
        if (gameState == null || gameState.isHomeInteriorTutorialSeen()) {
            hideTutorialOverlay();
            return;
        }
        showHomeInteriorTutorial(true);
    }

    private void showHomeInteriorTutorial(boolean markAsSeen) {
        if (tutorialOverlay == null || tutorialImageView == null || tutorialTitleLabel == null) {
            return;
        }

        if (markAsSeen && gameState != null) {
            gameState.setHomeInteriorTutorialSeen(true);
        }
        tutorialIndex = 0;
        tutorialOverlayActive = true;
        tutorialOverlay.setManaged(true);
        tutorialOverlay.setDisable(false);
        tutorialOverlay.setVisible(true);
        if (tutorialPromptLabel != null) {
            tutorialPromptLabel.setText("Tap anywhere to continue");
        }
        configureTutorialOverlayResponsiveLayout();

        if (player != null) {
            player.stop();
        }

        showTutorialStep(tutorialIndex);
    }

    private void showTutorialStep(int index) {
        if (index < 0 || index >= HOME_TUTORIAL_IMAGE_PATHS.size()) {
            hideTutorialOverlay();
            return;
        }

        String imagePath = HOME_TUTORIAL_IMAGE_PATHS.get(index);
        URL url = getClass().getResource(imagePath);
        if (url == null) {
            System.err.println("Tutorial image missing: " + imagePath);
            hideTutorialOverlay();
            return;
        }
        tutorialImageView.setImage(new Image(url.toExternalForm()));
        tutorialTitleLabel.setText(extractTutorialTitle(imagePath));
    }

    private String extractTutorialTitle(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return "Tutorial";
        }

        int slash = imagePath.lastIndexOf('/');
        int dot = imagePath.lastIndexOf('.');
        int start = slash >= 0 ? slash + 1 : 0;
        int end = dot > start ? dot : imagePath.length();
        return imagePath.substring(start, end);
    }

    private void hideTutorialOverlay() {
        tutorialOverlayActive = false;
        tutorialIndex = -1;

        if (tutorialOverlay != null) {
            tutorialOverlay.setVisible(false);
            tutorialOverlay.setDisable(true);
            tutorialOverlay.setManaged(false);
        }

        if (view != null && view.getScene() != null && player != null) {
            player.attachInput(view.getScene());
            player.start();
        }
    }

    private void configureTutorialOverlayResponsiveLayout() {
        if (view == null || tutorialImageView == null || tutorialTitleLabel == null || tutorialPromptLabel == null) {
            return;
        }

        if (tutorialShade != null) {
            tutorialShade.widthProperty().bind(view.widthProperty());
            tutorialShade.heightProperty().bind(view.heightProperty());
        }
        if (tutorialOverlayContent != null) {
            tutorialOverlayContent.prefWidthProperty().bind(view.widthProperty());
            tutorialOverlayContent.prefHeightProperty().bind(view.heightProperty());
        }

        tutorialImageView.fitWidthProperty().bind(Bindings.createDoubleBinding(
            () -> clampTutorialSize(view.getWidth() * 0.76, 260, 1100),
            view.widthProperty()
        ));
        tutorialImageView.fitHeightProperty().bind(Bindings.createDoubleBinding(
            () -> clampTutorialSize(view.getHeight() * 0.62, 180, 820),
            view.heightProperty()
        ));

        tutorialTitleLabel.layoutXProperty().bind(view.widthProperty().subtract(tutorialTitleLabel.prefWidthProperty()).divide(2.0));
        tutorialTitleLabel.layoutYProperty().bind(view.heightProperty().multiply(0.045));
        tutorialTitleLabel.fontProperty().bind(Bindings.createObjectBinding(
            () -> javafx.scene.text.Font.font("System Bold", clampTutorialSize(view.getWidth() * 0.04, 18, 34)),
            view.widthProperty()
        ));

        tutorialImageView.layoutXProperty().bind(view.widthProperty().subtract(tutorialImageView.fitWidthProperty()).divide(2.0));
        tutorialImageView.layoutYProperty().bind(view.heightProperty().subtract(tutorialImageView.fitHeightProperty()).divide(2.0));

        tutorialPromptLabel.fontProperty().bind(Bindings.createObjectBinding(
            () -> javafx.scene.text.Font.font("System Bold", clampTutorialSize(view.getWidth() * 0.028, 14, 26)),
            view.widthProperty()
        ));
        if (tutorialPromptBox != null) {
            tutorialPromptBox.prefWidthProperty().bind(view.widthProperty().multiply(0.8));
            tutorialPromptBox.layoutXProperty().bind(view.widthProperty().subtract(tutorialPromptBox.prefWidthProperty()).divide(2.0));
            tutorialPromptBox.layoutYProperty().bind(view.heightProperty().multiply(0.84));
        }
    }

    private double clampTutorialSize(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    @FXML
    private void handleTutorialOverlayClick(MouseEvent event) {
        if (!tutorialOverlayActive) {
            return;
        }

        tutorialIndex++;
        if (tutorialIndex >= HOME_TUTORIAL_IMAGE_PATHS.size()) {
            hideTutorialOverlay();
            return;
        }

        showTutorialStep(tutorialIndex);
    }
    
    private void bridgeInteriorSharedUi() {
        List<StackPane> hotbarSlotNodes = List.of(Hslot, Hslot1, Hslot2, Hslot3, Hslot4);
        List<ImageView> hotbarImageNodes = List.of(slotImg, slotImg1, slotImg2, slotImg3, slotImg4);
        List<Label> hotbarLabelNodes = List.of(slotLb, slotLb1, slotLb2, slotLb3, slotLb4);
        List<ToggleButton> hotbarButtonNodes = List.of(slotBtn, slotBtn1, slotBtn2, slotBtn3, slotBtn4);

        bridgeSharedHotbarUi(
            hotbar,
            hotbarguide,
            hotbarSlotNodes,
            hotbarImageNodes,
            hotbarLabelNodes,
            hotbarButtonNodes
        );

        List<ImageView> playerInvImageNodes = List.of(
            Playerinvimg112, Playerinvimg110, Playerinvimg21, Playerinvimg31, Playerinvimg41,
            Playerinvimg51, Playerinvimg61, Playerinvimg71, Playerinvimg81, Playerinvimg91,
            Playerinvimg101, Playerinvimg111, Playerinvimg121, Playerinvimg131, Playerinvimg141,
            Playerinvimg151, Playerinvimg161, Playerinvimg171, Playerinvimg181, Playerinvimg191
        );

        List<Label> playerInvLabelNodes = List.of(
            Playerinvlbl112, Playerinvlbl110, Playerinvlbl21, Playerinvlbl31, Playerinvlbl41,
            Playerinvlbl51, Playerinvlbl61, Playerinvlbl71, Playerinvlbl81, Playerinvlbl91,
            Playerinvlbl101, Playerinvlbl111, Playerinvlbl121, Playerinvlbl131, Playerinvlbl141,
            Playerinvlbl151, Playerinvlbl161, Playerinvlbl171, Playerinvlbl181, Playerinvlbl191
        );

        List<Button> playerInvButtonNodes = List.of(
            Playerinvbtn112, Playerinvbtn110, Playerinvbtn21, Playerinvbtn31, Playerinvbtn41,
            Playerinvbtn51, Playerinvbtn61, Playerinvbtn71, Playerinvbtn81, Playerinvbtn91,
            Playerinvbtn101, Playerinvbtn111, Playerinvbtn121, Playerinvbtn131, Playerinvbtn141,
            Playerinvbtn151, Playerinvbtn161, Playerinvbtn171, Playerinvbtn181, Playerinvbtn191
        );

        bridgeSharedInventoryOverlayUi(
            inventoryOverlay,
            menuInventory,
            statusTab,
            settingsTab,
            statusgroup,
            skillsgroup,
            player_inv_group1,
            openInventory,
            openstatus,
            opensetting,
            btnclose,
            btnskill,
            btnstatus,
            playerInvImageNodes,
            playerInvLabelNodes,
            playerInvButtonNodes
        );

        rebuildSharedUiAfterBridge();
    }
    
    private void wireInteriorPlayerButtons() {
        for (int i = 0; i < pinvButtons.size(); i++) {
            final int index = i;
            Button button = pinvButtons.get(i);
            if (button != null) {
                button.setOnAction(e -> selectInteriorPlayerSlot(index));
            }
        }
    }

    private void refreshInteriorInventorySlots() {
        if (player == null || player.getInventory() == null) return;

        List<SlotData> slots = snapshotSlots(player.getInventory(), playerInventorySlotCapacity());

        for (int i = 0; i < pinvImages.size(); i++) {
            ImageView img = pinvImages.get(i);
            Label lbl = pinvLabels.get(i);
            Button btn = pinvButtons.get(i);
            SlotData slot = (i < slots.size()) ? slots.get(i) : null;

            if (slot == null) {
                if (img != null) {
                    img.setImage(null);
                    img.setVisible(false);
                    img.setOpacity(0.0);
                    img.setScaleX(1.0);
                    img.setScaleY(1.0);
                    img.setStyle("");
                }
                if (lbl != null) lbl.setText("");
                if (btn != null) {
                    btn.setText("");
                    btn.setDisable(true);
                }
                continue;
            }

            int itemId = slot.itemId();
            item itm = ItemDatabase.getItemById(itemId);

            if (img != null) {
                img.setImage(ItemDatabase.getImage(itemId));
                img.setVisible(true);
                img.setOpacity(0.9);
                img.setScaleX(1.0);
                img.setScaleY(1.0);
                img.setStyle("");
            }

            if (lbl != null) {
                lbl.setText("x" + slot.quantity());
            }

            if (btn != null) {
                btn.setDisable(false);
                btn.setText(itm == null ? "" : itm.getItem_name());
            }
        }

        refreshInteriorPlayerSelectionHighlight();
    }

    private void refreshInteriorPlayerSelectionHighlight() {
        List<SlotData> slots = snapshotSlots(player.getInventory(), playerInventorySlotCapacity());

        for (int i = 0; i < pinvImages.size(); i++) {
            ImageView imageView = pinvImages.get(i);
            if (imageView == null) continue;

            SlotData slot = (i < slots.size()) ? slots.get(i) : null;
            boolean selected =
                slot != null &&
                selectedInteriorPlayerSlotIndices.contains(i);

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

    private void selectInteriorPlayerSlot(int index) {
        List<SlotData> slots = snapshotSlots(player.getInventory(), playerInventorySlotCapacity());
        if (index < 0 || index >= slots.size()) return;

        SlotData slot = slots.get(index);
        if (slot == null) return;

        toggleInteriorPlayerSelection(index, slot.itemId());
        refreshChestAndInventoryViews();
    }

    private void toggleInteriorPlayerSelection(int slotIndex, int itemId) {
        if (slotIndex < 0 || itemId <= 0) return;

        if (selectedInteriorPlayerSlotIndices.contains(slotIndex)) {
            selectedInteriorPlayerSlotIndices.remove(slotIndex);
        } else {
            selectedInteriorPlayerSlotIndices.add(slotIndex);
        }

        selectedPlayerInventoryItemId = selectedInteriorPlayerSlotIndices.isEmpty() ? null : itemId;
    }

    private void toggleChestSelection(int slotIndex) {
        if (slotIndex < 0) return;

        if (selectedChestSlotIndices.contains(slotIndex)) {
            selectedChestSlotIndices.remove(slotIndex);
        } else {
            selectedChestSlotIndices.add(slotIndex);
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
        insideBedZone = false;
        refreshSleepInteractionState();
        applyInteractionState(btnOpenInv, false);
        applyInteractionState(btncook, false);
        applyInteractionState(btnmelt, false);
        applyInteractionState(btncraft, false);

        if (bed_detect != null && btntoSleep != null) {
            interactionManager.addZone(new InteractionZone(bed_detect, inside -> {
                insideBedZone = inside;
                refreshSleepInteractionState();
            }));
        }

        if (chest_detect != null && btnOpenInv != null) {
            interactionManager.addZone(new InteractionZone(chest_detect, inside -> {
                applyInteractionState(btnOpenInv, inside);
            }));
        }

        if (cook_detect != null && btncook != null) {
            interactionManager.addZone(new InteractionZone(cook_detect, inside -> {
                applyInteractionState(btncook, inside);
            }));
        }

        if (melt_detect != null && btnmelt != null) {
            interactionManager.addZone(new InteractionZone(melt_detect, inside -> {
                applyInteractionState(btnmelt, inside);
            }));
        }

        if (craft_detect != null && btncraft != null) {
            interactionManager.addZone(new InteractionZone(craft_detect, inside -> {
                applyInteractionState(btncraft, inside);
            }));
        }
    }

    private void applyInteractionState(Button button, boolean active) {
        if (button == null) return;

        button.setDisable(!active);
        button.setOpacity(active ? 0.28 : 0.0);
        button.setStyle(active
            ? "-fx-background-color: rgba(255, 235, 59, 0.45); -fx-border-color: gold; -fx-border-width: 2; -fx-effect: dropshadow(gaussian, yellow, 14, 0.7, 0, 0);"
            : "-fx-background-color: transparent; -fx-border-color: transparent;");
    }
    
    @Override
    protected void onPlayerInventorySlotSelected(int index, SlotData slot) {
        if (slot == null) return;

        selectedPlayerInventoryItemId = slot.itemId();
        selectedPlayerInventorySlotIndex = index;
        refreshChestAndInventoryViews();
    }
    
    private boolean isSleepingActive() {
        return sleepTimer != null;
    }

    private void refreshSleepInteractionState() {
        boolean canUseSleep = insideBedZone
            && playerState != null
            && playerState.canSleep()
            && !isSleepingActive();

        applyInteractionState(btntoSleep, canUseSleep);
    }

    private void applySleepTimeScaleOverride(double multiplier) {
        if (sleepTimeScaleOverrideActive) return;
        GameTimeManager time = GameTimeManager.get();
        sleepPreviousTimeScale = time.timeScaleProperty().get();
        time.timeScaleProperty().set(Math.max(0.1, multiplier));
        sleepTimeScaleOverrideActive = true;
    }

    private void restoreSleepTimeScaleOverride() {
        if (!sleepTimeScaleOverrideActive) return;
        GameTimeManager.get().timeScaleProperty().set(sleepPreviousTimeScale);
        sleepTimeScaleOverrideActive = false;
    }

    private void stopSleepSequence() {
        if (sleepTimer != null) {
            sleepTimer.stop();
            sleepTimer = null;
        }

        restoreSleepTimeScaleOverride();

        if (playerState != null) {
            playerState.stopSleeping();
        }

        if (sleepStackPane != null) {
            sleepStackPane.setVisible(false);
            sleepStackPane.setDisable(true);
            sleepStackPane.setManaged(false);
        }

        refreshSleepInteractionState();
    }

    @FXML
    private void handleSleepAction(ActionEvent event) {
        if (btntoSleep == null || btntoSleep.isDisable() || playerState == null || !playerState.canSleep() || isSleepingActive()) {
            return;
        }

        if (sleepStackPane != null) {
            sleepStackPane.setManaged(true);
            sleepStackPane.setDisable(false);
            sleepStackPane.setVisible(true);
        }

        if (player != null) {
            player.clearInputState();
        }

        playerState.startSleeping();

        // Advance game time based on how much the player needs to rest so that
        // time/day changes correctly after sleeping (even if the day doesn't roll over).
        int advanceMinutes = computeSleepAdvanceMinutes();
        if (advanceMinutes > 0) {
            GameTimeManager.get().addMinutes(advanceMinutes);
        }

        // Fully rest (HP + SP) after the time skip.
        playerState.setHp(playerState.getMaxHp());
        playerState.setStamina(playerState.getMaxStamina());

        sleepTimer = new javafx.animation.AnimationTimer() {
            private long startedAt = 0L;

            @Override
            public void handle(long now) {
                if (startedAt == 0L) {
                    startedAt = now;
                    return;
                }

                double elapsedSeconds = (now - startedAt) / 1_000_000_000.0;
                if (elapsedSeconds >= 0.6) {
                    stopSleepSequence();
                }
            }
        };
        sleepTimer.start();
        refreshSleepInteractionState();
    }

    private int computeSleepAdvanceMinutes() {
        if (playerState == null) return 0;

        int missingHp = Math.max(0, playerState.getMaxHp() - playerState.getHp());
        int missingStamina = Math.max(0, playerState.getMaxStamina() - playerState.getStamina());

        long hpMinutes = (long) missingHp * (long) SLEEP_MINUTES_PER_HP_POINT;
        long staminaMinutes = (long) missingStamina * (long) SLEEP_MINUTES_PER_STAMINA_POINT;

        long minutes = Math.max(hpMinutes, staminaMinutes);
        if (minutes <= 0L) return 0;
        if (minutes > SLEEP_MAX_ADVANCE_MINUTES) minutes = SLEEP_MAX_ADVANCE_MINUTES;
        return (int) minutes;
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

        TutorialOverlayHelper.showIfNeeded(
            view,
            gameState,
            "home_interior_chest_inside",
            List.of("/image/Toturial/Chest Tutorial.png")
        );
    }

    @FXML
    private void handlecraftaction(ActionEvent event) {
        openCraft();
    }

    @FXML
    private void handlecookaction(ActionEvent event) {
        openCook();
    }

    @FXML
    private void sendToChestClick(MouseEvent event) {
        moveSelectedItemsToChest();
    }

    @FXML
    private void sendToInventoryClick(MouseEvent event) {
        moveSelectedItemsToInventory();
    }

    @FXML
    private void backClick(MouseEvent event) {
        chestStackPane.setVisible(false);
        chestStackPane.setDisable(true);
        chestStackPane.setManaged(false);
        clearSelection();

        if (!tutorialOverlayActive && view != null && view.getScene() != null && player != null) {
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

        toggleChestSelection(index);
        refreshChestAndInventoryViews();
    }

    private void moveSelectedItemsToChest() {
        if (player == null || chestInventory == null) return;
        if (!moveSelectedStackBatch(player.getInventory(), chestInventory, selectedInteriorPlayerSlotIndices, playerInventorySlotCapacity())) {
            pruneMissingSelections();
            refreshChestAndInventoryViews();
            return;
        }

        clearChestSelectionsOnly();
        refreshChestAndInventoryViews();
    }

    private void moveSelectedItemsToInventory() {
        if (player == null || chestInventory == null) return;
        if (!moveSelectedStackBatch(chestInventory, player.getInventory(), selectedChestSlotIndices, CHEST_SLOT_CAPACITY)) {
            pruneMissingSelections();
            refreshChestAndInventoryViews();
            return;
        }

        clearChestSelectionsOnly();
        refreshChestAndInventoryViews();
    }

    private boolean moveSelectedStackBatch(Inventory from, Inventory to, Collection<Integer> slotIndices, int fromCapacity) {
        if (from == null || to == null || slotIndices == null || slotIndices.isEmpty()) return false;

        List<SlotData> slots = snapshotSlots(from, fromCapacity);
        if (slots.isEmpty()) return false;

        Map<Integer, Integer> transfer = new LinkedHashMap<>();
        for (Integer index : slotIndices) {
            if (index == null) continue;
            int slotIndex = index;
            if (slotIndex < 0 || slotIndex >= slots.size()) continue;

            SlotData slot = slots.get(slotIndex);
            if (slot == null || slot.itemId() <= 0 || slot.quantity() <= 0) continue;

            transfer.merge(slot.itemId(), slot.quantity(), Integer::sum);
        }

        if (transfer.isEmpty()) return false;
        if (!to.canAddItems(transfer)) return false;

        List<SlotData> removed = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : transfer.entrySet()) {
            int itemId = entry.getKey();
            int qty = entry.getValue();
            if (qty <= 0) continue;

            if (!from.removeItem(itemId, qty)) {
                for (SlotData rollback : removed) {
                    from.addItem(rollback.itemId(), rollback.quantity());
                }
                return false;
            }
            removed.add(new SlotData(itemId, qty));
        }

        if (!to.addItems(transfer)) {
            for (SlotData rollback : removed) {
                from.addItem(rollback.itemId(), rollback.quantity());
            }
            return false;
        }

        return true;
    }

    private void clearSelection() {
        clearChestSelectionsOnly();
        clearPlayerInventorySelection();
        refreshChestAndInventoryViews();
    }

    private void clearChestSelectionsOnly() {
        selectedChestSlotIndices.clear();
        selectedInteriorPlayerSlotIndices.clear();
    }

    private void pruneMissingSelections() {
        if (player != null) {
            List<SlotData> playerSlots = snapshotSlots(player.getInventory(), playerInventorySlotCapacity());
            selectedInteriorPlayerSlotIndices.removeIf(idx -> idx == null || idx < 0 || idx >= playerSlots.size());

            if (!selectedInteriorPlayerSlotIndices.isEmpty()) {
                Integer first = selectedInteriorPlayerSlotIndices.iterator().next();
                selectedPlayerInventoryItemId = (first != null && first >= 0 && first < playerSlots.size())
                        ? playerSlots.get(first).itemId()
                        : null;
            } else {
                selectedPlayerInventoryItemId = null;
            }
        } else {
            selectedInteriorPlayerSlotIndices.clear();
            selectedPlayerInventoryItemId = null;
        }

        if (chestInventory != null) {
            List<SlotData> chestSlots = snapshotSlots(chestInventory, CHEST_SLOT_CAPACITY);
            selectedChestSlotIndices.removeIf(idx -> idx == null || idx < 0 || idx >= chestSlots.size());
        } else {
            selectedChestSlotIndices.clear();
        }
    }

    private void refreshChestAndInventoryViews() {
        refreshChestSlots();
        refreshInteriorInventorySlots();
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
    
    private void refreshSelectionHighlight() {
        highlightChestSelection();
        highlightInteriorPlayerSelection();
    }

    private void highlightChestSelection() {
        List<SlotData> slots = snapshotSlots(chestInventory, CHEST_SLOT_CAPACITY);

        for (int i = 0; i < chestImages.size(); i++) {
            ImageView imageView = chestImages.get(i);
            if (imageView == null) continue;

            SlotData slot = (i < slots.size()) ? slots.get(i) : null;
            boolean selected =
                slot != null &&
                selectedChestSlotIndices.contains(i);

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
 
    private void highlightInteriorPlayerSelection() {
        List<SlotData> slots = snapshotSlots(player.getInventory(), playerInventorySlotCapacity());

        for (int i = 0; i < pinvImages.size(); i++) {
            ImageView imageView = pinvImages.get(i);
            if (imageView == null) continue;

            SlotData slot = (i < slots.size()) ? slots.get(i) : null;
            boolean selected =
                slot != null &&
                selectedInteriorPlayerSlotIndices.contains(i);

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
    
//    animation methods
    private void setupFloatingDialogs() {
    playFloating(cookingdialogimg, 0.0);
    playFloating(craftingdialogimg, 0.2);
    playFloating(invdialogimg, 0.4);
    playFloating(meltdialogimg, 0.6);
    playFloating(sleepingdialogimg, 0.8);
    }
    
//    animation methods
    private void playFloating(ImageView imageView, double delaySeconds) {
        if (imageView == null) return;

        TranslateTransition floatAnim = new TranslateTransition(Duration.seconds(1.2), imageView);
        floatAnim.setFromY(0);
        floatAnim.setToY(-8); // how high it floats
        floatAnim.setAutoReverse(true);
        floatAnim.setCycleCount(Animation.INDEFINITE);
        floatAnim.setInterpolator(Interpolator.EASE_BOTH);
        floatAnim.setDelay(Duration.seconds(delaySeconds));
        floatAnim.play();
    }
    
//    low opacity dialog
    private void setupDialogOpacityCheck() {
        if (human_hitbox == null || cookingdialogimg == null) return;

        cookingdialogimg.setOpacity(1.0);
        cookingDialogDimmed = false;

        dialogOpacityTimer = new javafx.animation.AnimationTimer() {
            @Override
            public void handle(long now) {
                updateCookingDialogOpacity();
            }
        };
        dialogOpacityTimer.start();
    }

    private void updateCookingDialogOpacity() {
        if (human_hitbox == null || cookingdialogimg == null) return;

        Bounds humanBounds = human_hitbox.localToScene(human_hitbox.getBoundsInLocal());
        Bounds dialogBounds = cookingdialogimg.localToScene(cookingdialogimg.getBoundsInLocal());

        boolean overlaps = humanBounds.intersects(dialogBounds);

        if (overlaps && !cookingDialogDimmed) {
            cookingDialogDimmed = true;
            fadeCookingDialogTo(0.35);
        } else if (!overlaps && cookingDialogDimmed) {
            cookingDialogDimmed = false;
            fadeCookingDialogTo(1.0);
        }
    }

    private void fadeCookingDialogTo(double targetOpacity) {
        if (cookingdialogimg == null) return;

        if (cookingDialogFade != null) {
            cookingDialogFade.stop();
        }

        cookingDialogFade = new FadeTransition(Duration.millis(180), cookingdialogimg);
        cookingDialogFade.setFromValue(cookingdialogimg.getOpacity());
        cookingDialogFade.setToValue(targetOpacity);
        cookingDialogFade.play();
    }

    @FXML
    private void handlebtntutoaction(ActionEvent event) {
        // Check if the chest interface is currently open/visible
        if (chestStackPane != null && chestStackPane.isVisible()) {
            // Show only the specific chest tutorial image
            TutorialOverlayHelper.show(view, event, List.of("/image/Toturial/Chest Tutorial.png"));
        } else {
            // Otherwise, show the default global tutorial sequence
            TutorialOverlayHelper.show(view, event, TutorialOverlayHelper.GLOBAL_BUTTON_TUTORIAL_IMAGES);
        }

        if (event != null) {
            event.consume();
        }
    }
}
