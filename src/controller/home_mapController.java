package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import model.Farm;
import model.Player;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.ItemType;
import model.Merchant;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.CollisionRegistry;
import model.FarmPlotRegistrar;
import game.GameLoopManager;
import game.InteractionManager;
import model.InteractionZone;
import ui.InventoryRow;
import ui.InventoryUIManager;
import util.SceneManager;
import util.WorldScaler;



/**
 * FXML Controller class
 */
public class home_mapController implements Initializable {
    private GameLoopManager gameLoop;
    private Player player;
    private Farm farm;
    private Merchant merChant;
    
    private boolean shop=false,homeInside=false,leftmapInside=false,rightmapInside=false,uppermapInside=false;
    private InteractionZone merchantZone;
    private InteractionZone homeZone;
    private InteractionZone leftMapZone;
    private InteractionZone rightMapZone;
    private InteractionZone upperMapZone;
    private InteractionManager interactionManager;
    private InventoryUIManager inventoryUIManager;
    
    private final List<StackPane> slots = new ArrayList<>();
    private final List<Button> slotButtons = new ArrayList<>();

    private int selectedIndex = -1;
    
    @FXML
    private StackPane view;
    @FXML
    private Group human;
    @FXML
    private ImageView human_img;
    @FXML
    private Rectangle human_hitbox;
    @FXML
    private Group farm1;
    @FXML
    private Rectangle farm1_detection;
    @FXML
    private Button farm1_button;
    @FXML
    private ImageView farm_img1;
    @FXML
    private ImageView farm_img2;
    @FXML
    private ImageView farm_img3;
    @FXML
    private ImageView farm_img4;
    @FXML
    private ImageView farm_img10;
    @FXML
    private ImageView farm_img45;
    @FXML
    private ImageView farm_img44;
    @FXML
    private ImageView farm_img43;
    @FXML
    private ImageView farm_img42;
    @FXML
    private ImageView farm_img41;
    @FXML
    private ImageView farm_img40;
    @FXML
    private ImageView farm_img39;
    @FXML
    private ImageView farm_img38;
    @FXML
    private ImageView farm_img37;
    @FXML
    private ImageView farm_img36;
    @FXML
    private ImageView farm_img35;
    @FXML
    private ImageView farm_img34;
    @FXML
    private ImageView farm_img33;
    @FXML
    private ImageView farm_img32;
    @FXML
    private ImageView farm_img31;
    @FXML
    private ImageView farm_img30;
    @FXML
    private ImageView farm_img29;
    @FXML
    private ImageView farm_img28;
    @FXML
    private ImageView farm_img27;
    @FXML
    private ImageView farm_img26;
    @FXML
    private ImageView farm_img25;
    @FXML
    private ImageView farm_img24;
    @FXML
    private ImageView farm_img23;
    @FXML
    private ImageView farm_img22;
    @FXML
    private ImageView farm_img21;
    @FXML
    private ImageView farm_img19;
    @FXML
    private ImageView farm_img20;
    @FXML
    private ImageView farm_img18;
    @FXML
    private ImageView farm_img17;
    @FXML
    private ImageView farm_img16;
    @FXML
    private ImageView farm_img14;
    @FXML
    private ImageView farm_img15;
    @FXML
    private ImageView farm_img12;
    @FXML
    private ImageView farm_img11;
    @FXML
    private ImageView farm_img9;
    @FXML
    private ImageView farm_img8;
    @FXML
    private ImageView farm_img7;
    @FXML
    private ImageView farm_img6;
    @FXML
    private ImageView farm_img5;
    @FXML
    private ImageView farm_img13;
    @FXML
    private Group merchant;
    @FXML
    private Rectangle merchantdetect;
    @FXML
    private ImageView merchantImage;
    @FXML
    private Button btnmerchant;
    @FXML
    private ImageView bg;
    @FXML
    private Group world;
    @FXML
    private Group walls;
    @FXML
    private HBox hotbar;
    @FXML
    private StackPane Hslot;
    @FXML
    private ImageView slotImg;
    @FXML
    private Button slotBtn;
    @FXML
    private Label slotLb;
    @FXML
    private StackPane Hslot1;
    @FXML
    private ImageView slotImg1;
    @FXML
    private Button slotBtn1;
    @FXML
    private Label slotLb1;
    @FXML
    private StackPane Hslot2;
    @FXML
    private ImageView slotImg2;
    @FXML
    private Button slotBtn2;
    @FXML
    private Label slotLb2;
    @FXML
    private StackPane Hslot3;
    @FXML
    private ImageView slotImg3;
    @FXML
    private Button slotBtn3;
    @FXML
    private Label slotLb3;
    @FXML
    private StackPane Hslot4;
    @FXML
    private ImageView slotImg4;
    @FXML
    private Button slotBtn4;
    @FXML
    private Label slotLb4;
    @FXML
    private StackPane Hslot5;
    @FXML
    private Button slotBtn5;
    @FXML
    private Pane inventoryOverlay;
    @FXML
    private TableView<InventoryRow> invTable;
    @FXML
    private TableColumn<InventoryRow, String> colItem;
    @FXML
    private TableColumn<InventoryRow, Number> colQty;
    @FXML
    private Label invCoinsLabel;
    @FXML
    private Group home;
    @FXML
    private Rectangle homedetect;
    @FXML
    private Button doorbtn;
    @FXML
    private Group leftMap;
    @FXML
    private Rectangle leftmapdetect;
    @FXML
    private Button leftmsp;
    @FXML
    private Group rightMap;
    @FXML
    private Rectangle rightmapdetect;
    @FXML
    private Button rightmsp;
    @FXML
    private Group upperMap;
    @FXML
    private Rectangle uppermapdetect;
    @FXML
    private Button uppermsp;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        player = new Player(
                human,
                human_img,
                human_hitbox,
                64, 64,
                5,
                150_000_000L
        );
        
        inventoryUIManager = new InventoryUIManager(
                invTable,
                colItem,
                colQty,
                invCoinsLabel,
                player.getInventory()
        );
        
        // keep only real item slots selectable (0..4)
        slots.clear();
        slotButtons.clear();
        slots.addAll(List.of(Hslot, Hslot1, Hslot2, Hslot3, Hslot4));
        slotButtons.addAll(List.of(slotBtn, slotBtn1, slotBtn2, slotBtn3, slotBtn4));

        for (int i = 0; i < slotButtons.size(); i++) {
            final int idx = i;
            slotButtons.get(i).setOnAction(e -> selectSlot(idx));
        }

        // menu button opens inventory
        slotBtn5.setOnAction(e -> toggleInventory());

        selectSlot(0);

        
        merChant=new Merchant(
                merchantImage,
                32,32,  //frame width/height
                4,4,    //total frames, columns
                1,      //fps
                "/image/human/merchant_new2.png"
        );
         // Make sure it's on top
        merchantImage.toFront();

        //Start animation
        merChant.play();
        
        registerAllWalls();

        farm = new Farm(farm1, farm1_detection, farm1_button);
        farm.setAvailableSeeds(
                Farm.SeedType.CARROT,
                Farm.SeedType.GRAPE,
                Farm.SeedType.CORN,
                Farm.SeedType.TOMATO
        );

        registerAllPlots();
        
        farm.setOnHarvest((seedType, amount) -> {
            ItemType crop = switch (seedType) {
                case GRAPE -> ItemType.GRAPE;
                case CARROT -> ItemType.CARROT;
                case CORN -> ItemType.CORN;
                case TOMATO -> ItemType.TOMATO;
            };
                
                player.getInventory().addItem(crop, amount);

            // optional: also add coins on harvest
            // player.getInventory().addCoins(5);

            System.out.println("Harvested: " + crop.getDisplayName() + " x" + amount +
                               " | Coins: " + player.getInventory().getCoins());
        });
        
        view.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                player.attachInput(newScene);
            }
        });
        
        interactionManager = new InteractionManager();

        merchantZone = new InteractionZone(merchantdetect, inside -> {
            shop = inside;
            btnmerchant.setDisable(!inside);
        });

        homeZone = new InteractionZone(homedetect, inside -> {
            homeInside = inside;
            doorbtn.setDisable(!inside);
            doorbtn.setVisible(inside);
        });

        leftMapZone = new InteractionZone(leftmapdetect, inside -> {
            leftmapInside = inside;
            leftmsp.setDisable(!inside);
            leftmsp.setVisible(inside);
        });

        rightMapZone = new InteractionZone(rightmapdetect, inside -> {
            rightmapInside = inside;
            rightmsp.setDisable(!inside);
            rightmsp.setVisible(inside);
        });

        upperMapZone = new InteractionZone(uppermapdetect, inside -> {
            uppermapInside = inside;
            uppermsp.setDisable(!inside);
            uppermsp.setVisible(inside);
        });

        interactionManager.addZone(merchantZone);
        interactionManager.addZone(homeZone);
        interactionManager.addZone(leftMapZone);
        interactionManager.addZone(rightMapZone);
        interactionManager.addZone(upperMapZone);
        
        startGameLoop();
        player.start();
        new WorldScaler(view, world);
    }
    
    private void selectSlot(int idx) {
        if (idx < 0 || idx >= slots.size()) return;
        if (idx == selectedIndex) return;

        // un-highlight previous
        if (selectedIndex >= 0) {
            StackPane prev = slots.get(selectedIndex);
            prev.getStyleClass().remove("hotbar-selected");
        }

        // highlight new
        StackPane now = slots.get(idx);
        if (!now.getStyleClass().contains("hotbar-selected")) {
            now.getStyleClass().add("hotbar-selected");
        }

        selectedIndex = idx;

        // (optional) do something with the selected item:
         System.out.println("Selected slot: " + (idx + 1));
    }
    
    @FXML
    private void onMerchantShopClick(ActionEvent event) {
        if (!shop) return; // safety check
        openShopWindow();
    }

    private void startGameLoop(){
        gameLoop = new GameLoopManager(() -> {
            Bounds playerBounds = human_hitbox.localToScene(
                human_hitbox.getBoundsInLocal()
            );

            farm.updatePlayerBounds(playerBounds);
            interactionManager.updateAll(playerBounds);
        });

        gameLoop.start();
    }
    
    private void registerAllWalls(){
        player.setWalls(CollisionRegistry.collectWalls(walls));
    }

    
    private void registerAllPlots(){
        FarmPlotRegistrar.registerPlots(farm, farm1);
    }
    
    private void openShopWindow() {
        ListView<Merchant.ShopItem> listView = new ListView<>();
        listView.getItems().addAll(merChant.getItems());

        Button buyBtn = new Button("Buy Selected");
        buyBtn.setOnAction(e -> {
            Merchant.ShopItem selected = listView.getSelectionModel().getSelectedItem();
            if (selected == null) return;

            int price = selected.getPrice();
            var inv = player.getInventory();
            
            //Map shop item -> actual ItemType
            ItemType itemType = mapShopNameToItemType(selected.getName());
            if (itemType == null) {
                new Alert(Alert.AlertType.ERROR, "Unknown shop item: " + selected.getName(), ButtonType.OK).showAndWait();
                return;
            }

            if (!inv.spendCoins(price)) {
                new Alert(Alert.AlertType.WARNING,
                        "Not enough coins! You have " + inv.getCoins() + "g, need " + price + "g.",
                        ButtonType.OK).showAndWait();
                return;
            }

            inv.addItem(itemType, 1);

            new Alert(Alert.AlertType.INFORMATION,
                    "Purchased " + itemType.getDisplayName() + " for " + price + "g.\n" +
                    "Coins left: " + inv.getCoins() + "g\n" +
                    "You now have: " + inv.getCount(itemType),
                    ButtonType.OK).showAndWait();
        });
        
        VBox root = new VBox(10, listView, buyBtn);
        root.setPadding(new Insets(12));

        Stage shopStage = new Stage();
        shopStage.setTitle("Merchant Shop");
        shopStage.initModality(Modality.APPLICATION_MODAL);
        shopStage.setScene(new Scene(root, 320, 420));
        shopStage.showAndWait();
    }

    
    private ItemType mapShopNameToItemType(String shopName) {
        return switch (shopName) {
            case "Parsnip Seeds" -> ItemType.PARSNIP_SEEDS;
            case "Potato Seeds" -> ItemType.POTATO_SEEDS;
            case "Watering Can Upgrade" -> ItemType.WATERING_CAN_UPGRADE;
            default -> null;
        };
    }

    @FXML
    private void onCloseInventory(ActionEvent event) {
        setInventoryOpen(false);
    }
    
    private void toggleInventory() {
        setInventoryOpen(!inventoryOverlay.isVisible());
    }

    private void setInventoryOpen(boolean open) {
        inventoryOverlay.setVisible(open);
        inventoryOverlay.setManaged(open);

        // Pause input while open (simple approach)
        if (view.getScene() != null) {
            if (open) {
                // disable movement input
                view.getScene().setOnKeyPressed(null);
                view.getScene().setOnKeyReleased(null);
            } else {
                // reattach movement input
                player.attachInput(view.getScene());
            }
        }
    }

    @FXML
    private void handledoorbtnaction(ActionEvent event) throws IOException {
        SceneManager.switchScene("/view/home_interior.fxml");
    }
    
    @FXML
    private void handleleftmapaction(ActionEvent event) throws IOException {
        SceneManager.switchScene("/view/left_map.fxml");
    }
    
    @FXML
    private void handlerightmapaction(ActionEvent event) throws IOException {
        SceneManager.switchScene("/view/right_map.fxml");
    }
    
    @FXML
    private void handleuppermapaction(ActionEvent event) throws IOException {
        SceneManager.switchScene("/view/upper_map.fxml");
    }
    
}
