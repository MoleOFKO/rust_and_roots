package controller;

import db.SaveGameService;
import game.GameState;
import game.GameTimeManager;
import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
import model.Farm;
import model.InteractionZone;
import model.ItemDatabase;
import model.Merchant;
import model.item;
import util.SceneManager;
import util.SpawnTag;

public class home_mapController extends BaseMapController {

    private Merchant merChant;
    private InteractionZone merchantZone;

    private static final String DAY_BG_PATH = "/image/maps/home_map_withoutshop.png";
    private static final String NIGHT_BG_PATH = "/image/homemapuse_night.png";

    @FXML private Rectangle merchantdetect;
    @FXML private ImageView merchantImage;
    @FXML private Button btnmerchant;

    @FXML private Rectangle homedetect;
    @FXML private Button doorbtn;

    @FXML private Rectangle leftmapdetect;
    @FXML private Button leftmsp;

    @FXML private Rectangle rightmapdetect;
    @FXML private Button rightmsp;

    @FXML private Rectangle uppermapdetect;
    @FXML private Button uppermsp;

    @FXML private Label statusName;
    @FXML private Label statusEXP;
    @FXML private Label statusHP;
    @FXML private Label statusSP;
    @FXML private Label statusSTR;
    @FXML private Label statusVIT;
    @FXML private Label statusDEX;
    @FXML private Label statusAGI;

    @FXML private ImageView btnHPminus;
    @FXML private ImageView btnHPplus;
    @FXML private ImageView btnSPminus;
    @FXML private ImageView btnSTRminus;
    @FXML private ImageView btnVITminus;
    @FXML private ImageView btnDEXminus;
    @FXML private ImageView btnAGIminus;
    @FXML private ImageView btnSPplus;
    @FXML private ImageView btnSTRplus;
    @FXML private ImageView btnVITplus;
    @FXML private ImageView btnDEXplus;
    @FXML private ImageView btnAGIplus;
    @FXML private ImageView btnUsePoint;
    @FXML private ImageView btnConfirm;

    @FXML private Slider timescaleslider;
    @FXML private Button btnsetday;
    @FXML private Button btnsetnight;
    @FXML private Button btnplus1h;
    @FXML private Button btnsaveandexit;
    @FXML private Label sliderscale;
    @FXML private Label lbldays;
    @FXML private Label lbltime;
    @FXML private Button btnsave;
    @FXML private Button btnload;

    @FXML private Label farming;
    @FXML private Label gathering;
    @FXML private Label crafting;
    @FXML private Label fishing;
    @FXML private Label cooking;
    @FXML private ImageView btnfarmingminus1;
    @FXML private ImageView btnfarmingplus1;
    @FXML private ImageView btngatheringminus1;
    @FXML private ImageView btncraftingminus1;
    @FXML private ImageView btnfishingminus1;
    @FXML private ImageView btncookingminus1;
    @FXML private ImageView btngatheringplus1;
    @FXML private ImageView btncraftingplus1;
    @FXML private ImageView btnfishingplus1;
    @FXML private ImageView btncookingplus1;
    @FXML
    private StackPane view;
    @FXML
    private Group world;
    @FXML
    private ImageView bg;
    @FXML
    private Rectangle spawn_default;
    @FXML
    private Rectangle spawn_from_left;
    @FXML
    private Rectangle spawn_from_right;
    @FXML
    private Rectangle spawn_from_up;
    @FXML
    private Rectangle spawn_from_farm;
    @FXML
    private Rectangle spawn_from_door;
    @FXML
    private Rectangle spawn_from_shop;
    @FXML
    private Group merchant;
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
    private Label hotbarguide;
    @FXML
    private Group farm1;
    @FXML
    private Rectangle farm1_detection;
    @FXML
    private ImageView farm_img1;
    @FXML
    private ImageView farm_img2;
    @FXML
    private ImageView farm_img3;
    @FXML
    private ImageView farm_img4;
    @FXML
    private ImageView farm_img5;
    @FXML
    private ImageView farm_img6;
    @FXML
    private ImageView farm_img7;
    @FXML
    private ImageView farm_img8;
    @FXML
    private ImageView farm_img9;
    @FXML
    private ImageView farm_img10;
    @FXML
    private ImageView farm_img11;
    @FXML
    private ImageView farm_img12;
    @FXML
    private ImageView farm_img13;
    @FXML
    private ImageView farm_img14;
    @FXML
    private ImageView farm_img15;
    @FXML
    private ImageView farm_img16;
    @FXML
    private ImageView farm_img17;
    @FXML
    private ImageView farm_img18;
    @FXML
    private ImageView farm_img27;
    @FXML
    private ImageView farm_img28;
    @FXML
    private ImageView farm_img19;
    @FXML
    private ImageView farm_img20;
    @FXML
    private ImageView farm_img21;
    @FXML
    private ImageView farm_img22;
    @FXML
    private ImageView farm_img23;
    @FXML
    private ImageView farm_img24;
    @FXML
    private ImageView farm_img25;
    @FXML
    private ImageView farm_img26;
    @FXML
    private Button farm1_button;
    @FXML
    private Group human;
    @FXML
    private Rectangle human_hitbox;
    @FXML
    private ImageView human_img;
    @FXML
    private Group home;
    @FXML
    private Group leftMap;
    @FXML
    private Group rightMap;
    @FXML
    private Group upperMap;
    @FXML
    private HBox hotbar;
    @FXML
    private StackPane Hslot;
    @FXML
    private ImageView slotImg;
    @FXML
    private Label slotLb;
    @FXML
    private StackPane Hslot1;
    @FXML
    private ImageView slotImg1;
    @FXML
    private Label slotLb1;
    @FXML
    private StackPane Hslot2;
    @FXML
    private ImageView slotImg2;
    @FXML
    private Label slotLb2;
    @FXML
    private StackPane Hslot3;
    @FXML
    private ImageView slotImg3;
    @FXML
    private Label slotLb3;
    @FXML
    private StackPane Hslot4;
    @FXML
    private ImageView slotImg4;
    @FXML
    private Label slotLb4;
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
    private Group statusgroup;
    @FXML
    private ImageView btnskill;
    @FXML
    private Group skillsgroup;
    @FXML
    private ImageView btnstatus;
    @FXML
    private Group settingsTab;
    @FXML
    private Button openInventory;
    @FXML
    private ImageView btnclose;
    @FXML
    private Button openstatus;
    @FXML
    private Button opensetting;
    @FXML
    private ToggleGroup selectedhotbar;
    @FXML
    private ToggleButton slotBtn;
    @FXML
    private ToggleButton slotBtn1;
    @FXML
    private ToggleButton slotBtn2;
    @FXML
    private ToggleButton slotBtn3;
    @FXML
    private ToggleButton slotBtn4;
    @FXML
    private ImageView equip_img;
    

    @Override
    protected String mapKey() {
        return "home_map";
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
        registerExit(leftmapdetect, "/view/left_map.fxml", SpawnTag.FROM_RIGHT);
        registerExit(rightmapdetect, "/view/right_map.fxml", SpawnTag.FROM_LEFT);
        registerExit(uppermapdetect, "/view/upper_map.fxml", SpawnTag.FROM_DOWN);
        registerExit(homedetect, "/view/home_interior.fxml", SpawnTag.FROM_DOOR);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        bindPlayerEquipmentView(equip_img);
        bridgeHomeSharedUi();
        setupMerchant();
        setupMerchantZone();
        
        if (farm1_button != null) {
            farm1_button.setOnAction(e -> openFarmTable());
        }
    }
  
    @Override
    protected void openFarmTable() {
        if (farm1_button == null || farm1_button.isDisable()) return;
        gameState.setActiveFarmMapKey(mapKey());
        gameState.setReturnScene("/view/home_map.fxml", SpawnTag.FROM_FARM);
        stopAndSwitchScene("/view/farm_table.fxml", SpawnTag.FROM_FARM);
    }

    private void setupMerchant() {
        merChant = new Merchant(merchantImage, 32, 32, 4, 4, 1, "/image/human/merchant_new2.png");
        if (merchantImage != null) merchantImage.toFront();
        merChant.play();
    }

    private void setupMerchantZone() {
        merchantZone = new InteractionZone(merchantdetect, inside -> {
            if (btnmerchant != null) btnmerchant.setDisable(!inside);
        });
        interactionManager.addZone(merchantZone);
    }

    private void bridgeHomeSharedUi() {
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
            Playerinvimg, Playerinvimg1, Playerinvimg2, Playerinvimg3, Playerinvimg4,
            Playerinvimg5, Playerinvimg6, Playerinvimg7, Playerinvimg8, Playerinvimg9,
            Playerinvimg10, Playerinvimg11, Playerinvimg12, Playerinvimg13, Playerinvimg14,
            Playerinvimg15, Playerinvimg16, Playerinvimg17, Playerinvimg18, Playerinvimg19
        );
        
        List<Label> playerInvLabelNodes = List.of(
            Playerinvlbl, Playerinvlbl1, Playerinvlbl2, Playerinvlbl3, Playerinvlbl4,
            Playerinvlbl5, Playerinvlbl6, Playerinvlbl7, Playerinvlbl8, Playerinvlbl9,
            Playerinvlbl10, Playerinvlbl11, Playerinvlbl12, Playerinvlbl13, Playerinvlbl14,
            Playerinvlbl15, Playerinvlbl16, Playerinvlbl17, Playerinvlbl18, Playerinvlbl19
        );
        
        List<Button> playerInvButtonNodes = List.of(
            Playerinvbtn, Playerinvbtn1, Playerinvbtn2, Playerinvbtn3, Playerinvbtn4,
            Playerinvbtn5, Playerinvbtn6, Playerinvbtn7, Playerinvbtn8, Playerinvbtn9,
            Playerinvbtn10, Playerinvbtn11, Playerinvbtn12, Playerinvbtn13, Playerinvbtn14,
            Playerinvbtn15, Playerinvbtn16, Playerinvbtn17, Playerinvbtn18, Playerinvbtn19
        );
        
        bridgeSharedInventoryOverlayUi(
            inventoryOverlay,
            menuInventory,
            statusTab,
            settingsTab,
            statusgroup,
            skillsgroup,
            player_inv_group,
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
    
    @FXML
    private void onMerchantShopClick(ActionEvent event) {
        if (btnmerchant == null || btnmerchant.isDisable()) return;
        gameState.setReturnScene("/view/home_map.fxml", SpawnTag.FROM_SHOP);
        stopAndSwitchScene("/view/shop.fxml", SpawnTag.FROM_SHOP);
    }
    
    @FXML
    private void handledoorbtnaction(ActionEvent event) throws IOException {
        GameState.get().setReturnScene("/view/home_map.fxml", SpawnTag.FROM_DOOR);
        SceneManager.switchScene("/view/home_interior.fxml", SpawnTag.FROM_DOOR);
    }

    @FXML
    private void handleleftmapaction(ActionEvent event) throws IOException {
        GameState.get().setReturnScene("/view/home_map.fxml", SpawnTag.FROM_LEFT);
        SceneManager.switchScene("/view/left_map.fxml", SpawnTag.FROM_RIGHT);
    }

    @FXML
    private void handlerightmapaction(ActionEvent event) throws IOException {
        GameState.get().setReturnScene("/view/home_map.fxml", SpawnTag.FROM_RIGHT);
        SceneManager.switchScene("/view/right_map.fxml", SpawnTag.FROM_LEFT);
    }

    @FXML
    private void handleuppermapaction(ActionEvent event) throws IOException {
        GameState.get().setReturnScene("/view/home_map.fxml", SpawnTag.FROM_UP);
        SceneManager.switchScene("/view/upper_map.fxml", SpawnTag.FROM_DOWN);
    }
 
}
