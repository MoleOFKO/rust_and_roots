package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import game.GameState;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import model.Farm;
import model.ItemType;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.ItemDatabase;
import model.item;
import model.Merchant;
import model.InteractionZone;
import util.SceneManager;
import util.SpawnTag;

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

    @FXML private Label lbName;
    @FXML private Label lbHP;
    @FXML private Label lbSP;
    @FXML private Label lbEXP;
    @FXML private Label lbSTR;
    @FXML private Label lbAGI;
    @FXML private Label lbDEX;
    @FXML private Label lbVIT;
    @FXML private Button btnSave;
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
    private AnchorPane inventoryOverlay;
    @FXML
    private Label invCoinsLabel;
    @FXML
    private TableView<?> invTable;
    @FXML
    private TableColumn<?, ?> colItem;
    @FXML
    private TableColumn<?, ?> colQty;
    @FXML
    private Label timeLabel;
    @FXML
    private Button pauseTimeBtn;
    @FXML
    private Slider timeSpeedSlider;
    @FXML
    private Label timeSpeedValueLabel;
    @FXML
    private Button btnMorning;
    @FXML
    private Button btnNight;
    @FXML
    private Button btnAddHour;
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
        registerExit(merchantdetect, "/view/shop.fxml", SpawnTag.FROM_SHOP);if (farm1_detection != null) {
            interactionManager.addZone(new model.InteractionZone(farm1_detection, inside -> {
                if (inside) {
                    GameState.get().setReturnScene("/view/home_map.fxml", SpawnTag.FROM_FARM);
                    if (gameLoop != null) gameLoop.stop();
                    if (player != null) player.stop();
                    SceneManager.switchScene("/view/farm_table.fxml", SpawnTag.FROM_FARM);
                }
            }));
        }
    }

    @Override
    protected void configureFarm(Farm farm) {
        farm.setAvailableSeeds(
            Farm.SeedType.CARROT,
            Farm.SeedType.GRAPE,
            Farm.SeedType.CORN,
            Farm.SeedType.TOMATO
        );

        if (farm1_button != null) {
            farm1_button.setDisable(true);
            farm1_button.setVisible(false);
            farm1_button.setManaged(false);
        }

        farm.setOnHarvest((seedType, amount) -> {
            ItemType crop = switch (seedType) {
                case GRAPE -> ItemType.GRAPE;
                case CARROT -> ItemType.CARROT;
                case CORN -> ItemType.CORN;
                case TOMATO -> ItemType.TOMATO;
            };

            player.getInventory().addItem(crop, amount);

            System.out.println("Harvested: " + crop.getDisplayName() + " x" + amount);
        });
    }

    @Override
    protected void afterBaseInitialize() {
        setupMerchant();
        setupMerchantZone();
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
        if (!shop) return;

        GameState.get().setReturnScene("/view/home_map.fxml", SpawnTag.FROM_SHOP);
        GameState.get().setCurrentMerchantItems(merChant.getItems());
        SceneManager.switchScene("/view/shop.fxml");
    }

    @FXML
    private void onCloseInventory(ActionEvent event) {
        setInventoryOpen(false);
    }

    @Override
    public void init(GameState gameState) {
        super.init(gameState);
    }

    @FXML
    private void handledoorbtnaction(ActionEvent event) throws IOException {
        GameState.get().setReturnScene("/view/home_map.fxml", SpawnTag.FROM_DOOR);
        SceneManager.switchScene("/view/home_interior.fxml");
    }

    @FXML
    private void handleleftmapaction(ActionEvent event) throws IOException {
        GameState.get().setReturnScene("/view/home_map.fxml", SpawnTag.FROM_LEFT);
        SceneManager.switchScene("/view/left_map.fxml");
    }

    @FXML
    private void handlerightmapaction(ActionEvent event) throws IOException {
        GameState.get().setReturnScene("/view/home_map.fxml", SpawnTag.FROM_RIGHT);
        SceneManager.switchScene("/view/right_map.fxml");
    }

    @FXML
    private void handleuppermapaction(ActionEvent event) throws IOException {
        GameState.get().setReturnScene("/view/home_map.fxml", SpawnTag.FROM_UP);
        SceneManager.switchScene("/view/upper_map.fxml");
    }

    @FXML
    private void handlebtnSave(ActionEvent event) {
    }
}
