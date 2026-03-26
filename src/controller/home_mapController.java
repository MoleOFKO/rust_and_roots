package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import game.GameState;
import game.GameTimeManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import model.Farm;
import static model.Farm.SeedType.CARROT;
import static model.Farm.SeedType.CORN;
import static model.Farm.SeedType.GRAPE;
import static model.Farm.SeedType.TOMATO;
import model.ItemDatabase;
import model.item;
import model.Merchant;
import model.InteractionZone;
import util.SceneManager;
import util.SpawnTag;
import java.util.Comparator;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import db.SaveGameService;

public class home_mapController extends BaseMapController {

    private Merchant merChant;
    private boolean shop = false;
    private InteractionZone merchantZone;

    private static final String DAY_BG_PATH = "/image/homemapuse.png";
    private static final String NIGHT_BG_PATH = "/image/homemapuse_night.png";

    @FXML private Group merchant;
    @FXML private Rectangle merchantdetect;
    @FXML private ImageView merchantImage;
    @FXML private Button btnmerchant;

    @FXML private Group home;
    @FXML private Rectangle homedetect;
    @FXML private Button doorbtn;

    @FXML private Group leftMap;
    @FXML private Rectangle leftmapdetect;
    @FXML private Button leftmsp;

    @FXML private Group rightMap;
    @FXML private Rectangle rightmapdetect;
    @FXML private Button rightmsp;

    @FXML private Group upperMap;
    @FXML private Rectangle uppermapdetect;
    @FXML private Button uppermsp;

    @FXML
    private StackPane view;
    @FXML
    private Group world;
    @FXML
    private ImageView bg;
    @FXML
    private Group human;
    @FXML
    private Rectangle human_hitbox;
    @FXML
    private ImageView human_img;
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
    private ImageView farm_img29;
    @FXML
    private ImageView farm_img30;
    @FXML
    private ImageView farm_img31;
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
    private ImageView farm_img32;
    @FXML
    private ImageView farm_img33;
    @FXML
    private ImageView farm_img34;
    @FXML
    private ImageView farm_img35;
    @FXML
    private ImageView farm_img36;
    @FXML
    private ImageView farm_img37;
    @FXML
    private ImageView farm_img38;
    @FXML
    private ImageView farm_img39;
    @FXML
    private ImageView farm_img40;
    @FXML
    private ImageView farm_img41;
    @FXML
    private ImageView farm_img42;
    @FXML
    private ImageView farm_img43;
    @FXML
    private ImageView farm_img44;
    @FXML
    private ImageView farm_img45;
    @FXML
    private Button farm1_button;
    @FXML
    private HBox hotbar;
    @FXML
    private StackPane Hslot;
    @FXML
    private ImageView slotImg;
    @FXML
    private Label slotLb;
    @FXML
    private Button slotBtn;
    @FXML
    private StackPane Hslot1;
    @FXML
    private ImageView slotImg1;
    @FXML
    private Label slotLb1;
    @FXML
    private Button slotBtn1;
    @FXML
    private StackPane Hslot2;
    @FXML
    private ImageView slotImg2;
    @FXML
    private Label slotLb2;
    @FXML
    private Button slotBtn2;
    @FXML
    private StackPane Hslot3;
    @FXML
    private ImageView slotImg3;
    @FXML
    private Label slotLb3;
    @FXML
    private Button slotBtn3;
    @FXML
    private StackPane Hslot4;
    @FXML
    private ImageView slotImg4;
    @FXML
    private Label slotLb4;
    @FXML
    private Button slotBtn4;
    @FXML
    private StackPane Hslot5;
    @FXML
    private Button slotBtn5;
    @FXML
    private Pane inventoryOverlay;
    @FXML
    private Rectangle spawn_default;
    @FXML
    private Rectangle spawn_from_left;
    @FXML
    private Rectangle spawn_from_right;
    @FXML
    private Rectangle spawn_from_up;
    @FXML
    private Rectangle spawn_from_door;
    @FXML
    private Rectangle spawn_from_farm;
    @FXML
    private Rectangle spawn_from_shop;
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
    private ImageView btnclose;
    @FXML
    private Button openInventory;
    @FXML
    private Button openstatus;
    @FXML
    private Button opensetting;
    @FXML
    private Group statusTab;
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
    private Label hotbarguide;
    @FXML
    private Group statusgroup;
    @FXML
    private ImageView btnskill;
    @FXML
    private Label farming;
    @FXML
    private ImageView btnstatus;
    @FXML
    private Label gathering;
    @FXML
    private Label crafting;
    @FXML
    private Label fishing;
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
    private ImageView btnfishingminus1;
    @FXML
    private ImageView btncookingminus1;
    @FXML
    private ImageView btngatheringplus1;
    @FXML
    private ImageView btncraftingplus1;
    @FXML
    private ImageView btnfishingplus1;
    @FXML
    private ImageView btncookingplus1;
    @FXML
    private Rectangle wall30;
    @FXML
    private Group skillsgroup;


    @FXML
    private void handlefarmingminusaction(MouseEvent event) {
    }

    @FXML
    private void handlefarmingplusaction(MouseEvent event) {
    }

    @FXML
    private void handlegatheringminusaction(MouseEvent event) {
    }

    @FXML
    private void handlecraftingminusaction(MouseEvent event) {
    }

    @FXML
    private void handlefishingminusaction(MouseEvent event) {
    }

    @FXML
    private void handlecookingminusaction(MouseEvent event) {
    }

    @FXML
    private void handlegatheringplusaction(MouseEvent event) {
    }

    @FXML
    private void handlecraftingplusaction(MouseEvent event) {
    }

    @FXML
    private void handlefishingplusaction(MouseEvent event) {
    }

    @FXML
    private void handlecookingplusaction(MouseEvent event) {
    }

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
    protected void configureFarm(Farm farm) {
        farm.setAvailableSeeds(
            Farm.SeedType.CARROT,
            Farm.SeedType.GRAPE,
            Farm.SeedType.CORN,
            Farm.SeedType.TOMATO
        );

        farm.setOnHarvest((seedType, amount) -> {
            String cropName = switch (seedType) {
                case GRAPE -> "Grape";
                case CARROT -> "Carrot";
                case CORN -> "Corn";
                case TOMATO -> "Tomato";
            };

            item crop = ItemDatabase.getItemByName(cropName);

            if (crop == null) {
                System.out.println("Unknown crop item in database: " + cropName);
                return;
            }

            player.getInventory().addItem(crop.getItem_id(), amount);

            System.out.println("Harvested: " + crop.getItem_name() + " x" + amount);
        });
    }
    
    private final List<ImageView> playerInvImages = new ArrayList<>();
    private final List<Label> playerInvLabels = new ArrayList<>();
    private final List<Button> playerInvButtons = new ArrayList<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);

        setupMerchant();
        setupMerchantZone();
        bindStatusLabels();
        bindSettingControls();

        playerInvImages.clear();
        playerInvLabels.clear();
        playerInvButtons.clear();

        playerInvImages.add(Playerinvimg);
        playerInvImages.add(Playerinvimg1);
        playerInvImages.add(Playerinvimg2);
        playerInvImages.add(Playerinvimg3);
        playerInvImages.add(Playerinvimg4);
        playerInvImages.add(Playerinvimg5);
        playerInvImages.add(Playerinvimg6);
        playerInvImages.add(Playerinvimg7);
        playerInvImages.add(Playerinvimg8);
        playerInvImages.add(Playerinvimg9);
        playerInvImages.add(Playerinvimg10);
        playerInvImages.add(Playerinvimg11);
        playerInvImages.add(Playerinvimg12);
        playerInvImages.add(Playerinvimg13);
        playerInvImages.add(Playerinvimg14);
        playerInvImages.add(Playerinvimg15);
        playerInvImages.add(Playerinvimg16);
        playerInvImages.add(Playerinvimg17);
        playerInvImages.add(Playerinvimg18);
        playerInvImages.add(Playerinvimg19);

        playerInvLabels.add(Playerinvlbl);
        playerInvLabels.add(Playerinvlbl1);
        playerInvLabels.add(Playerinvlbl2);
        playerInvLabels.add(Playerinvlbl3);
        playerInvLabels.add(Playerinvlbl4);
        playerInvLabels.add(Playerinvlbl5);
        playerInvLabels.add(Playerinvlbl6);
        playerInvLabels.add(Playerinvlbl7);
        playerInvLabels.add(Playerinvlbl8);
        playerInvLabels.add(Playerinvlbl9);
        playerInvLabels.add(Playerinvlbl10);
        playerInvLabels.add(Playerinvlbl11);
        playerInvLabels.add(Playerinvlbl12);
        playerInvLabels.add(Playerinvlbl13);
        playerInvLabels.add(Playerinvlbl14);
        playerInvLabels.add(Playerinvlbl15);
        playerInvLabels.add(Playerinvlbl16);
        playerInvLabels.add(Playerinvlbl17);
        playerInvLabels.add(Playerinvlbl18);
        playerInvLabels.add(Playerinvlbl19);

        playerInvButtons.add(Playerinvbtn);
        playerInvButtons.add(Playerinvbtn1);
        playerInvButtons.add(Playerinvbtn2);
        playerInvButtons.add(Playerinvbtn3);
        playerInvButtons.add(Playerinvbtn4);
        playerInvButtons.add(Playerinvbtn5);
        playerInvButtons.add(Playerinvbtn6);
        playerInvButtons.add(Playerinvbtn7);
        playerInvButtons.add(Playerinvbtn8);
        playerInvButtons.add(Playerinvbtn9);
        playerInvButtons.add(Playerinvbtn10);
        playerInvButtons.add(Playerinvbtn11);
        playerInvButtons.add(Playerinvbtn12);
        playerInvButtons.add(Playerinvbtn13);
        playerInvButtons.add(Playerinvbtn14);
        playerInvButtons.add(Playerinvbtn15);
        playerInvButtons.add(Playerinvbtn16);
        playerInvButtons.add(Playerinvbtn17);
        playerInvButtons.add(Playerinvbtn18);
        playerInvButtons.add(Playerinvbtn19);

        refreshPlayerInventorySlots();

        player.getInventory().itemsProperty().addListener(
            (javafx.collections.MapChangeListener<Integer, javafx.beans.property.IntegerProperty>) change -> {
                refreshPlayerInventorySlots();
            }
        );

        if (farm1_button != null) {
            farm1_button.setOnAction(e -> openFarmTable());
        }

        refreshMenuState();
    }
    
    private void refreshPlayerInventorySlots() {
        if (player == null || player.getInventory() == null) return;

        List<Integer> owned = new ArrayList<>(player.getInventory().itemsProperty().keySet());
        owned.sort(Comparator.comparing(id -> {
            item itm = ItemDatabase.getItemById(id);
            return itm == null ? "" : itm.getItem_name();
        }));

        for (int i = 0; i < playerInvImages.size(); i++) {
            ImageView img = playerInvImages.get(i);
            Label lbl = playerInvLabels.get(i);
            Button btn = playerInvButtons.get(i);

            if (i >= owned.size()) {
                if (img != null) img.setImage(null);
                if (lbl != null) lbl.setText("");
                if (btn != null) {
                    btn.setText("");
                    btn.setDisable(true);
                    btn.setOnAction(null);
                }
                continue;
            }

            int itemId = owned.get(i);
            int qty = player.getInventory().getCount(itemId);
            item itm = ItemDatabase.getItemById(itemId);

            if (img != null) {
                img.setImage(ItemDatabase.getImage(itemId));
            }

            if (lbl != null) {
                lbl.setText("x" + qty);
            }

            if (btn != null) {
                btn.setDisable(false);
                btn.setText(itm == null ? "" : itm.getItem_name());
            }
        }
    }

    private void openFarmTable(){
        if(farm1_button ==null || farm1_button.isDisable()) return;
        
        gameState.setReturnScene("/view/home_map.fxml", SpawnTag.FROM_FARM);
        stopAndSwitchScene("/view/farm_table.fxml", SpawnTag.FROM_FARM);
    }

    private void bindStatusLabels() {
        if (playerState == null) return;

        playerState.playerNameProperty().addListener((obs, o, n) -> refreshStatusView());
        playerState.expProperty().addListener((obs, o, n) -> refreshStatusView());
        playerState.hpProperty().addListener((obs, o, n) -> refreshStatusView());
        playerState.maxHpProperty().addListener((obs, o, n) -> refreshStatusView());
        playerState.staminaProperty().addListener((obs, o, n) -> refreshStatusView());
        playerState.maxStaminaProperty().addListener((obs, o, n) -> refreshStatusView());
        playerState.strProperty().addListener((obs, o, n) -> refreshStatusView());
        playerState.vitProperty().addListener((obs, o, n) -> refreshStatusView());
        playerState.dexProperty().addListener((obs, o, n) -> refreshStatusView());
        playerState.agiProperty().addListener((obs, o, n) -> refreshStatusView());

        refreshStatusView();
    }

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

                    SaveGameService saveService = new SaveGameService();
                    saveService.saveGame(GameState.get());

                    if (gameLoop != null) gameLoop.stop();
                    if (player != null) player.stop();

                    Platform.exit();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    @Override
    protected void refreshStatusAllocationUi() {
        refreshStatusView();
    }
    
    private void refreshStatusView() {
        if (playerState == null) return;

        int pendingExpCost = getPendingExpCost();

        if (statusName != null) statusName.setText("Name : " + playerState.getPlayerName());
        if (statusEXP != null) {
            statusEXP.setText("EXP : " + playerState.getExp() + (pendingExpCost > 0 ? "(-" + pendingExpCost + ")" : ""));
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

        refreshStatusButtons();
    }

    private String formatPending(int points) {
        if (points <= 0) return "";
        return "(+" + points + ")";
    }

    private void refreshStatusButtons() {
        boolean canUsePoint = playerState != null && playerState.getExp() > 0;

        configureImageButton(btnUsePoint, canUsePoint, true, statusAllocationMode ? 1.0 : 0.85);
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
    }

    private void configureImageButton(ImageView imageView, boolean enabled) {
        configureImageButton(imageView, enabled, true, 1.0);
    }

    private void configureImageButton(ImageView imageView, boolean enabled, double activeOpacity) {
        configureImageButton(imageView, enabled, true, activeOpacity);
    }

    private void configureImageButton(ImageView imageView, boolean enabled, boolean visible, double activeOpacity) {
        if (imageView == null) return;
        imageView.setVisible(visible);
        imageView.setManaged(visible);
        imageView.setDisable(!enabled);
        imageView.setMouseTransparent(!visible || !enabled);
        imageView.setOpacity(!visible ? 0.0 : (enabled ? activeOpacity : 0.45));
    }
    
    private void setupMerchant() {
        merChant = new Merchant(merchantImage, 32, 32, 4, 4, 1, "/image/human/merchant_new2.png");

        merchantImage.toFront();
        merChant.play();
    }

    private void setupMerchantZone() {
        merchantZone = new InteractionZone(merchantdetect, inside -> {
            shop = inside;
            btnmerchant.setDisable(!inside);
        });

        interactionManager.addZone(merchantZone);
    }

    @FXML
    private void onMerchantShopClick(ActionEvent event) {
        if (btnmerchant == null || btnmerchant.isDisable()) return;

        gameState.setReturnScene("/view/home_map.fxml", SpawnTag.FROM_SHOP);
        gameState.setCurrentMerchantItems(merChant.getItems());
        stopAndSwitchScene("/view/shop.fxml", SpawnTag.FROM_SHOP);
    }

    @Override
    public void init(GameState gameState) {
        super.init(gameState);
    }

    @Override
    protected void toggleInventory() {
        if (inventoryOverlay == null) return;
        boolean open = !inventoryOverlay.isVisible();
        setInventoryOpen(open);
        if (open) {
            setActiveMenuTab(MenuTab.INVENTORY);
        }
    }

    @Override
    protected void setInventoryOpen(boolean open) {
        if (!open) {
            statusAllocationMode = false;
            resetPendingStatusChanges();
            setActiveMenuTab(MenuTab.INVENTORY);
        }

        super.setInventoryOpen(open);

        if (open) {
            setActiveMenuTab(activeMenuTab == null ? MenuTab.INVENTORY : activeMenuTab);
        }

        refreshMenuState();
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

    @FXML
    private void handleHPminusaction(MouseEvent event) { onStatusHpMinus(); }

    @FXML
    private void handleHPplusaction(MouseEvent event) { onStatusHpPlus(); }

    @FXML
    private void handleSPminusaction(MouseEvent event) { onStatusSpMinus(); }

    @FXML
    private void handleSTRminusaction(MouseEvent event) { onStatusStrMinus(); }

    @FXML
    private void handleVITminusaction(MouseEvent event) { onStatusVitMinus(); }

    @FXML
    private void handleDEXminusaction(MouseEvent event) { onStatusDexMinus(); }

    @FXML
    private void handleAGIminusaction(MouseEvent event) { onStatusAgiMinus(); }

    @FXML
    private void handleSPplusaction(MouseEvent event) { onStatusSpPlus(); }

    @FXML
    private void handleSTRplusaction(MouseEvent event) { onStatusStrPlus(); }

    @FXML
    private void handleVITplusaction(MouseEvent event) { onStatusVitPlus(); }

    @FXML
    private void handleDEXplusaction(MouseEvent event) { onStatusDexPlus(); }

    @FXML
    private void handleAGIplusaction(MouseEvent event) { onStatusAgiPlus(); }

    @FXML
    private void handleusepointaction(MouseEvent event) { onStatusUsePoint(); }

    @FXML
    private void handleconfirmaction(MouseEvent event) { onStatusConfirm(); }
    
    @FXML
    private void handlesaveexitaction(ActionEvent event) {
        try {
            capturePlayerSnapshot();

            SaveGameService saveService = new SaveGameService();
            saveService.saveGameToSlot(GameState.get(), GameState.get().getSelectedSaveSlot());

            if (gameLoop != null) gameLoop.stop();
            if (player != null) player.stop();

            Platform.exit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handlesave(ActionEvent event) {
        capturePlayerSnapshot();
        GameState.get().setSaveMenuMode(true);
        SceneManager.switchScene("/view/saveloads.fxml");
    }
    
    @FXML
    private void handleload(ActionEvent event) {
        capturePlayerSnapshot();
        GameState.get().setSaveMenuMode(false);
        GameState.get().setReturnScene("/view/home_map.fxml", SpawnTag.DEFAULT);
        SceneManager.switchScene("/view/saveloads.fxml");
    }

    @FXML
    private void handlebtnskillacion(MouseEvent event) {
        super.setSkillsOpen(true);
        super.setStatusOpen(false);
    }

    @FXML
    private void handlebtnstatusaction(MouseEvent event) {
        super.setStatusOpen(true);
        super.setSkillsOpen(false);
    }

    @FXML
    private void handleopenInventoryaction(ActionEvent event) {
        super.openInventory();
    }

    @FXML
    private void handlecloseaction(MouseEvent event) {
        super.closeMenuScreen();
    }

    @FXML
    private void handleopenstatusaction(ActionEvent event) {
        super.openStatus();
    }

    @FXML
    private void handleopensettingaction(ActionEvent event) {
        super.openSetting();
    }
}
