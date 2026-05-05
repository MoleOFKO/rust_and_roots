package controller;

import static controller.BaseMapController.MAP_EXIT_DELAY_GAME_SECONDS;
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
import javax.swing.JOptionPane;
import model.InteractionZone;
import util.SpawnTag;

/**
 * FXML Controller class
 *
 * @author Kaung Zin
 */
public class Upper_map_nofourController extends BaseMapController{

    private static final String DAY_BG_PATH = "/image/maps/upper_map_no.4.png";
    private static final String NIGHT_BG_PATH = "/image/NightMap/upper_map_no.4night.png";
    @FXML
    private ImageView bg;
    @FXML
    private Group tree;
    @FXML
    private ImageView tree_img;
    @FXML
    private Rectangle tree_hitbox;
    @FXML
    private ImageView drops_img_tree;
    @FXML
    private Group tree1;
    @FXML
    private ImageView tree_img1;
    @FXML
    private Rectangle tree_hitbox1;
    @FXML
    private ImageView drops_img_tree1;
    @FXML
    private Group treelayorgp1;
    @FXML
    private ImageView treelayorimg1;
    @FXML
    private Group treelayorgp;
    @FXML
    private ImageView treelayorimg;
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
    private Button btntuto;
    @FXML
    private Group stone7;
    @FXML
    private ImageView stone_img7;
    @FXML
    private Rectangle stone_hitbox7;
    @FXML
    private ImageView drops_img_stone7;
    @FXML
    private Group stone6;
    @FXML
    private ImageView stone_img6;
    @FXML
    private Rectangle stone_hitbox6;
    @FXML
    private ImageView drops_img_stone6;
    @FXML
    private Group stone5;
    @FXML
    private ImageView stone_img5;
    @FXML
    private Rectangle stone_hitbox5;
    @FXML
    private ImageView drops_img_stone5;
    @FXML
    private Group stone4;
    @FXML
    private ImageView stone_img4;
    @FXML
    private Rectangle stone_hitbox4;
    @FXML
    private ImageView drops_img_stone4;
    @FXML
    private Group stone3;
    @FXML
    private ImageView stone_img3;
    @FXML
    private Rectangle stone_hitbox3;
    @FXML
    private ImageView drops_img_stone3;
    @FXML
    private Group stone2;
    @FXML
    private ImageView stone_img2;
    @FXML
    private Rectangle stone_hitbox2;
    @FXML
    private ImageView drops_img_stone2;
    @FXML
    private Group stone1;
    @FXML
    private ImageView stone_img1;
    @FXML
    private Rectangle stone_hitbox1;
    @FXML
    private ImageView drops_img_stone1;
    @FXML
    private Group stone;
    @FXML
    private ImageView stone_img;
    @FXML
    private Rectangle stone_hitbox;
    @FXML
    private ImageView drops_img_stone;
    @FXML
    private Group slime;
    @FXML
    private Rectangle slime_hitbox;
    @FXML
    private ImageView slime_img;
    @FXML
    private ImageView slime_drops_img;
    @FXML
    private Rectangle slime_hitbox_left;
    @FXML
    private Rectangle slime_hitbox_up;
    @FXML
    private Rectangle slime_hitbox_down;
    @FXML
    private Rectangle slime_hitbox_right;
    @FXML
    private Rectangle slime_detect;
    @FXML
    private Group slime1;
    @FXML
    private Group orc;
    @FXML
    private Rectangle orc_hitbox;
    @FXML
    private ImageView orc_img;
    @FXML
    private ImageView orc_drops_img;
    @FXML
    private Rectangle orc_hitbox_left;
    @FXML
    private Rectangle orc_hitbox_up;
    @FXML
    private Rectangle orc_hitbox_down;
    @FXML
    private Rectangle orc_hitbox_right;
    @FXML
    private Rectangle orc_detect;
    @FXML
    private Rectangle slime1_hitbox;
    @FXML
    private ImageView slime1_img;
    @FXML
    private ImageView slime1_drops_img;
    @FXML
    private Rectangle slime1_hitbox_left;
    @FXML
    private Rectangle slime1_hitbox_up;
    @FXML
    private Rectangle slime1_hitbox_down;
    @FXML
    private Rectangle slime1_hitbox_right;
    @FXML
    private Rectangle slime1_detect;
    
    @Override
    protected String dayBackgroundPath() {
        return DAY_BG_PATH;
    }

    @Override
    protected String nightBackgroundPath() {
        return NIGHT_BG_PATH;
    }

    @FXML
    private StackPane view;
    @FXML
    private Group world;
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
    private Rectangle spawn_default;
    @FXML
    private Rectangle spawn_from_left;
    @FXML
    private Rectangle spawn_from_right;
    @FXML
    private Rectangle exit_right;
    @FXML
    private Rectangle exit_left;
    @FXML
    private Group human;
    @FXML
    private Rectangle human_hitbox;
    @FXML
    private ImageView human_img;
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
    @FXML
    private ImageView equip_img;
   

    @Override
    protected String mapKey() {
        return "upper_map_nofour";
    }

    @Override
    protected void configureExits() {
        
        registerExit(exit_right, "/view/right_map_nofour.fxml", SpawnTag.FROM_LEFT);
        registerExit(exit_left, "/view/left_map_nofour.fxml", SpawnTag.FROM_RIGHT);
//        if (exit_left != null) {
//            interactionManager.addZone(new InteractionZone(
//                exit_left,
//                null,
//                () -> JOptionPane.showMessageDialog(null, "Map unavailable"),
//                MAP_EXIT_DELAY_GAME_SECONDS
//            ));
//        }
    }
    @FXML
    private void handlebtntutoaction(ActionEvent event) {
        showOverworldTutorialOverlay();
    }
}
