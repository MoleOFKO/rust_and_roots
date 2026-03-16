package controller;

import db.dbcon;
import game.GameState;
import game.GameTimeManager;
import game.SceneController;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.converter.IntegerStringConverter;
import model.Inventory;
import model.ItemDatabase;
import model.item;
import java.util.*;
import javafx.scene.image.Image;
import javafx.scene.Group;
import ui.InventorySlotsUIManager;
import util.SpawnTag;

public class ShopController implements Initializable, SceneController {

    private GameState gameState;
    private Inventory inv;

    @Override
    public void init(GameState gs) {
        this.gameState = gs;
        this.inv = gs.getPlayerState().getInventory();
    }

    private enum Mode { BUY, SELL }
    private Mode mode = Mode.BUY;

    // ---------- persistent/current shop data ----------
    private final List<item> shopItems = new ArrayList<>();
    private final Map<Integer, item> dbItemsById = new LinkedHashMap<>();

    // ---------- stock / selection state ----------
    // BUY side:
    // shopAvailable = remaining stock still in shop pane
    // buyCart = selected quantity in table
    private final Map<Integer, IntegerProperty> shopAvailable = new LinkedHashMap<>();
    private final Map<Integer, IntegerProperty> buyCart = new LinkedHashMap<>();
    private final Map<Integer, Integer> dailyTotalShopStock = new LinkedHashMap<>();

    // SELL side:
    // inventoryAvailableForSell = remaining qty still shown as available from inventory
    // sellCart = selected qty in sell pane / table
    private final Map<Integer, IntegerProperty> inventoryAvailableForSell = new LinkedHashMap<>();
    private final Map<Integer, IntegerProperty> sellCart = new LinkedHashMap<>();

    // ---------- rows shown in table ----------
    public static class DetailRow {
        private final item itemData;
        private final IntegerProperty qty;
        private final IntegerProperty unitPrice;

        public DetailRow(item itemData, IntegerProperty qty, int unitPrice) {
            this.itemData = itemData;
            this.qty = qty;
            this.unitPrice = new SimpleIntegerProperty(unitPrice);
        }

        public item getItemData() {
            return itemData;
        }

        public IntegerProperty qtyProperty() {
            return qty;
        }

        public int getQty() {
            return qty.get();
        }

        public void setQty(int v) {
            qty.set(v);
        }

        public IntegerProperty unitPriceProperty() {
            return unitPrice;
        }

        public int getUnitPrice() {
            return unitPrice.get();
        }

        public int getLineTotal() {
            return getQty() * getUnitPrice();
        }
    }

    private final ObservableList<DetailRow> detailRows = FXCollections.observableArrayList();

    // ---------- button collections ----------
    private final List<Button> invBtns = new ArrayList<>();
    private final List<Button> buyBtns = new ArrayList<>();
    private final List<Button> sellBtns = new ArrayList<>();

    // ---------- buy pane collections ----------
    private final List<ImageView> buyImages = new ArrayList<>();
    private final List<Label> buyStockLabels = new ArrayList<>();
    private final List<Label> buyNameLabels = new ArrayList<>();
    private final List<Button> buyItemButtons = new ArrayList<>();

    // ---------- sell pane collections ----------
    private final List<ImageView> sellImages = new ArrayList<>();
    private final List<Label> sellLabels = new ArrayList<>();

    // ---------- inventory ui ----------
    private final List<ImageView> invImages = new ArrayList<>();
    private final List<Label> invLabels = new ArrayList<>();
    private InventorySlotsUIManager inventorySlotsUIManager;

    // ---------- FXML ----------
    @FXML private Button buybtn;
    @FXML private Button sellbtn;
    @FXML private StackPane view;
    @FXML private Group world;
    @FXML private Label lblcoin;

    @FXML private ImageView inyimg1;
    @FXML private Button invbtn1;
    @FXML private Label invlbl1;
    @FXML private ImageView inyimg2;
    @FXML private Button invbtn2;
    @FXML private Label invlbl2;
    @FXML private ImageView inyimg3;
    @FXML private Button invbtn3;
    @FXML private Label invlbl3;
    @FXML private ImageView inyimg4;
    @FXML private Button invbtn4;
    @FXML private Label invlbl4;
    @FXML private ImageView inyimg5;
    @FXML private Button invbtn5;
    @FXML private Label invlbl5;
    @FXML private ImageView inyimg6;
    @FXML private Button invbtn6;
    @FXML private Label invlbl6;
    @FXML private ImageView inyimg7;
    @FXML private Button invbtn7;
    @FXML private Label invlbl7;
    @FXML private ImageView inyimg8;
    @FXML private Button invbtn8;
    @FXML private Label invlbl8;
    @FXML private ImageView inyimg9;
    @FXML private Button invbtn9;
    @FXML private Label invlbl9;
    @FXML private ImageView inyimg10;
    @FXML private Button invbtn10;
    @FXML private Label invlbl10;
    @FXML private ImageView inyimg11;
    @FXML private Button invbtn11;
    @FXML private Label invlbl11;
    @FXML private ImageView inyimg12;
    @FXML private Button invbtn12;
    @FXML private Label invlbl12;
    @FXML private ImageView inyimg13;
    @FXML private Button invbtn13;
    @FXML private Label invlbl13;
    @FXML private ImageView inyimg14;
    @FXML private Button invbtn14;
    @FXML private Label invlbl14;
    @FXML private ImageView inyimg15;
    @FXML private Button invbtn15;
    @FXML private Label invlbl15;
    @FXML private ImageView inyimg16;
    @FXML private Button invbtn16;
    @FXML private Label invlbl16;
    @FXML private ImageView inyimg17;
    @FXML private Button invbtn17;
    @FXML private Label invlbl17;
    @FXML private ImageView inyimg18;
    @FXML private Button invbtn18;
    @FXML private Label invlbl18;
    @FXML private ImageView inyimg19;
    @FXML private Button invbtn19;
    @FXML private Label invlbl19;
    @FXML private ImageView inyimg20;
    @FXML private Button invbtn20;
    @FXML private Label invlbl20;

    @FXML private Pane buypane;
    @FXML private ImageView imgbuy1;
    @FXML private Button btnbuy1;
    @FXML private Label lblbuy1;
    @FXML private ImageView imgbuy2;
    @FXML private Button btnbuy2;
    @FXML private Label lblbuy2;
    @FXML private ImageView imgbuy3;
    @FXML private Button btnbuy3;
    @FXML private Label lblbuy3;
    @FXML private ImageView imgbuy4;
    @FXML private Button btnbuy4;
    @FXML private Label lblbuy4;
    @FXML private ImageView imgbuy5;
    @FXML private Button btnbuy5;
    @FXML private Label lblbuy5;
    @FXML private ImageView imgbuy6;
    @FXML private Button btnbuy6;
    @FXML private Label lblbuy6;
    @FXML private ImageView imgbuy7;
    @FXML private Button btnbuy7;
    @FXML private Label lblbuy7;
    @FXML private ImageView imgbuy8;
    @FXML private Button btnbuy8;
    @FXML private Label lblbuy8;
    @FXML private ImageView imgbuy9;
    @FXML private Button btnbuy9;
    @FXML private Label lblbuy9;
    @FXML private ImageView imgbuy10;
    @FXML private Button btnbuy10;
    @FXML private Label lblbuy10;
    @FXML private ImageView imgbuy11;
    @FXML private Button btnbuy11;
    @FXML private Label lblbuy11;
    @FXML private ImageView imgbuy12;
    @FXML private Button btnbuy12;
    @FXML private Label lblbuy12;

    @FXML private Label buyname1;
    @FXML private Label buyname2;
    @FXML private Label buyname3;
    @FXML private Label buyname4;
    @FXML private Label buyname5;
    @FXML private Label buyname6;
    @FXML private Label buyname7;
    @FXML private Label buyname8;
    @FXML private Label buyname9;
    @FXML private Label buyname10;
    @FXML private Label buyname11;
    @FXML private Label buyname12;

    @FXML private Pane sellpane;
    @FXML private ImageView imgsell1;
    @FXML private Button btnsell1;
    @FXML private Label lblsell1;
    @FXML private ImageView imgsell2;
    @FXML private Button btnsell2;
    @FXML private Label lblsell2;
    @FXML private ImageView imgsell3;
    @FXML private Button btnsell3;
    @FXML private Label lblsell3;
    @FXML private ImageView imgsell4;
    @FXML private Button btnsell4;
    @FXML private Label lblsell4;
    @FXML private ImageView imgsell5;
    @FXML private Button btnsell5;
    @FXML private Label lblsell5;
    @FXML private ImageView imgsell6;
    @FXML private Button btnsell6;
    @FXML private Label lblsell6;
    @FXML private ImageView imgsell7;
    @FXML private Button btnsell7;
    @FXML private Label lblsell7;
    @FXML private ImageView imgsell8;
    @FXML private Button btnsell8;
    @FXML private Label lblsell8;
    @FXML private ImageView imgsell9;
    @FXML private Button btnsell9;
    @FXML private Label lblsell9;
    @FXML private ImageView imgsell10;
    @FXML private Button btnsell10;
    @FXML private Label lblsell10;
    @FXML private ImageView imgsell11;
    @FXML private Button btnsell11;
    @FXML private Label lblsell11;
    @FXML private ImageView imgsell12;
    @FXML private Button btnsell12;
    @FXML private Label lblsell12;

    @FXML private Button btnback;
    @FXML private Label lbltotal;
    @FXML private TableView<DetailRow> tableitemdetail;
    @FXML private TableColumn<DetailRow, String> tableimg;
    @FXML private TableColumn<DetailRow, Integer> tablequantity;
    @FXML private TableColumn<DetailRow, Integer> tablecost;
    @FXML private Button colbuy;
    @FXML private Button colsell;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ItemDatabase.loadItems();

        if (gameState == null) gameState = GameState.get();
        if (inv == null) inv = gameState.getPlayerState().getInventory();

        buildUiCollections();
        collectButtonsOnce();

        inventorySlotsUIManager = new InventorySlotsUIManager(inv, invImages, invLabels, invBtns);

        loadShopItemsForToday();
        rebuildDailyShopAvailability();
        rebuildSellAvailabilityFromInventory();

        GameTimeManager.get().dayNumberProperty().addListener((obs, oldDay, newDay) -> {
            if (!Objects.equals(oldDay, newDay)) {
                gameState.resetDailyShopStateForNewDay(newDay.intValue());
                loadShopItemsForToday();
                rebuildDailyShopAvailability();
                clearBuySelection();
                if (mode == Mode.BUY) {
                    refreshTableFromBuyCart();
                }
                refreshAllPanels();
            }
        });

        setupTable();
        bindCoins();
        setMode(Mode.BUY);
        refreshAllPanels();
    }

    private void buildUiCollections() {
        buyImages.clear();
        buyStockLabels.clear();
        buyNameLabels.clear();
        buyItemButtons.clear();
        invImages.clear();
        invLabels.clear();
        sellImages.clear();
        sellLabels.clear();

        buyImages.addAll(Arrays.asList(
            imgbuy1, imgbuy2, imgbuy3, imgbuy4, imgbuy5, imgbuy6,
            imgbuy7, imgbuy8, imgbuy9, imgbuy10, imgbuy11, imgbuy12
        ));

        buyStockLabels.addAll(Arrays.asList(
            lblbuy1, lblbuy2, lblbuy3, lblbuy4, lblbuy5, lblbuy6,
            lblbuy7, lblbuy8, lblbuy9, lblbuy10, lblbuy11, lblbuy12
        ));

        buyNameLabels.addAll(Arrays.asList(
            buyname1, buyname2, buyname3, buyname4, buyname5, buyname6,
            buyname7, buyname8, buyname9, buyname10, buyname11, buyname12
        ));

        buyItemButtons.addAll(Arrays.asList(
            btnbuy1, btnbuy2, btnbuy3, btnbuy4, btnbuy5, btnbuy6,
            btnbuy7, btnbuy8, btnbuy9, btnbuy10, btnbuy11, btnbuy12
        ));

        invImages.addAll(Arrays.asList(
            inyimg1, inyimg2, inyimg3, inyimg4, inyimg5,
            inyimg6, inyimg7, inyimg8, inyimg9, inyimg10,
            inyimg11, inyimg12, inyimg13, inyimg14, inyimg15,
            inyimg16, inyimg17, inyimg18, inyimg19, inyimg20
        ));

        invLabels.addAll(Arrays.asList(
            invlbl1, invlbl2, invlbl3, invlbl4, invlbl5,
            invlbl6, invlbl7, invlbl8, invlbl9, invlbl10,
            invlbl11, invlbl12, invlbl13, invlbl14, invlbl15,
            invlbl16, invlbl17, invlbl18, invlbl19, invlbl20
        ));

        sellImages.addAll(Arrays.asList(
            imgsell1, imgsell2, imgsell3, imgsell4, imgsell5, imgsell6,
            imgsell7, imgsell8, imgsell9, imgsell10, imgsell11, imgsell12
        ));

        sellLabels.addAll(Arrays.asList(
            lblsell1, lblsell2, lblsell3, lblsell4, lblsell5, lblsell6,
            lblsell7, lblsell8, lblsell9, lblsell10, lblsell11, lblsell12
        ));
    }

    private void setupTable() {
        tableitemdetail.setEditable(true);
        tableitemdetail.setItems(detailRows);

        tableimg.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getItemData().getItem_name())
        );

        tablequantity.setCellValueFactory(cd ->
            cd.getValue().qtyProperty().asObject()
        );

        tablequantity.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        tablequantity.setOnEditCommit(e -> {
            DetailRow row = e.getRowValue();
            int itemId = row.getItemData().getItem_id();
            Integer newValue = e.getNewValue();
            int newQty = (newValue == null) ? row.getQty() : newValue;

            if (mode == Mode.BUY) {
                setBuySelectionQty(itemId, newQty);
            } else {
                setSellSelectionQty(itemId, newQty);
            }
        });

        tablecost.setCellValueFactory(cd ->
            new SimpleIntegerProperty(cd.getValue().getLineTotal()).asObject()
        );
    }

    @FXML
    private void handlebuyaction(ActionEvent event) {
        if (mode != Mode.BUY) return;

        int total = computeBuyTotal();
        if (!inv.canAfford(total)) return;
        if (!inv.spendCoins(total)) return;

        for (var e : buyCart.entrySet()) {
            int itemId = e.getKey();
            int qty = e.getValue().get();
            if (qty > 0) {
                inv.addItem(itemId, qty);
            }
        }

        clearBuySelection();
        persistCurrentShopRemainingStock();
        rebuildSellAvailabilityFromInventory();
        refreshTableFromBuyCart();
        refreshAllPanels();
    }

    @FXML
    private void handlesellaction(ActionEvent event) {
        if (mode != Mode.SELL) return;

        for (var e : sellCart.entrySet()) {
            int itemId = e.getKey();
            int qty = e.getValue().get();
            if (inv.getCount(itemId) < qty) return;
        }

        int totalGain = computeSellTotal();

        for (var e : sellCart.entrySet()) {
            int itemId = e.getKey();
            int qty = e.getValue().get();
            inv.removeItem(itemId, qty);
        }

        inv.addCoins(totalGain);

        clearSellSelection();
        rebuildSellAvailabilityFromInventory();
        refreshTableFromSellCart();
        refreshAllPanels();
    }

    @FXML
    private void handlebackaction(ActionEvent event) {
        String back = gameState.getReturnFxml();
        if (back == null) back = "/view/home_map.fxml";
        util.SceneManager.switchScene(back, SpawnTag.FROM_SHOP);
    }

    @FXML
    private void handlecolbuyaction(ActionEvent event) {
        setMode(Mode.BUY);
    }

    @FXML
    private void handlecolsellaction(ActionEvent event) {
        setMode(Mode.SELL);
    }

    private void setMode(Mode m) {
        this.mode = m;

        boolean buyMode = (m == Mode.BUY);

        buypane.setVisible(buyMode);
        buypane.setManaged(buyMode);
        buypane.setDisable(!buyMode);

        sellpane.setVisible(!buyMode);
        sellpane.setManaged(!buyMode);
        sellpane.setDisable(buyMode);

        buybtn.setDisable(!buyMode);
        sellbtn.setDisable(buyMode);

        if (buyMode) {
            refreshTableFromBuyCart();
        } else {
            rebuildSellAvailabilityFromInventory();
            refreshTableFromSellCart();
        }

        refreshAllPanels();
    }

    private void bindCoins() {
        lblcoin.textProperty().unbind();
        lblcoin.textProperty().bind(inv.coinsProperty().asString(": %d"));
    }

    private void collectButtonsOnce() {
        if (!invBtns.isEmpty()) return;

        invBtns.addAll(Arrays.asList(
            invbtn1, invbtn2, invbtn3, invbtn4, invbtn5,
            invbtn6, invbtn7, invbtn8, invbtn9, invbtn10,
            invbtn11, invbtn12, invbtn13, invbtn14, invbtn15,
            invbtn16, invbtn17, invbtn18, invbtn19, invbtn20
        ));

        buyBtns.addAll(Arrays.asList(
            btnbuy1, btnbuy2, btnbuy3, btnbuy4, btnbuy5, btnbuy6,
            btnbuy7, btnbuy8, btnbuy9, btnbuy10, btnbuy11, btnbuy12
        ));

        sellBtns.addAll(Arrays.asList(
            btnsell1, btnsell2, btnsell3, btnsell4, btnsell5, btnsell6,
            btnsell7, btnsell8, btnsell9, btnsell10, btnsell11, btnsell12
        ));

        invBtns.removeIf(Objects::isNull);
        buyBtns.removeIf(Objects::isNull);
        sellBtns.removeIf(Objects::isNull);
    }

    private void refreshAllPanels() {
        refreshInventoryButtons();
        refreshBuyPane();
        refreshSellPane();
        refreshTotalLabel();
    }

    private void refreshInventoryButtons() {
        List<Integer> available = new ArrayList<>(inv.itemsProperty().keySet());
        available.removeIf(id -> inv.getCount(id) <= 0);

        available.sort(Comparator.comparing(this::itemNameSafe, String.CASE_INSENSITIVE_ORDER));

        for (int i = 0; i < invBtns.size(); i++) {
            Button b = invBtns.get(i);

            if (b == null) continue;

            if (i >= available.size()) {
                b.setOnAction(null);
                b.setDisable(true);
                continue;
            }

            int itemId = available.get(i);
            b.setDisable(false);
            b.setOnAction(e -> {
                if (mode == Mode.SELL) {
                    int current = sellCart.containsKey(itemId) ? sellCart.get(itemId).get() : 0;
                    setSellSelectionQty(itemId, current + 1);
                }
            });
        }

        if (inventorySlotsUIManager != null) {
            inventorySlotsUIManager.refresh();
        }

        // In SELL mode, visually subtract selected qty from inventory labels
        if (mode == Mode.SELL) {
            applySellAvailabilityToInventoryLabels(available);
        }
    }

    private void applySellAvailabilityToInventoryLabels(List<Integer> visibleInventoryIds) {
        for (int i = 0; i < visibleInventoryIds.size() && i < invLabels.size(); i++) {
            int itemId = visibleInventoryIds.get(i);
            IntegerProperty remainingProp = inventoryAvailableForSell.get(itemId);
            int remaining = (remainingProp == null) ? inv.getCount(itemId) : remainingProp.get();
            invLabels.get(i).setText("x" + remaining);
        }
    }

    private void refreshBuyPane() {
        for (int i = 0; i < buyItemButtons.size(); i++) {
            ImageView img = buyImages.get(i);
            Label stockLbl = buyStockLabels.get(i);
            Label nameLbl = buyNameLabels.get(i);
            Button btn = buyItemButtons.get(i);

            if (i >= shopItems.size()) {
                if (img != null) img.setImage(null);
                if (stockLbl != null) stockLbl.setText("");
                if (nameLbl != null) nameLbl.setText("");
                if (btn != null) {
                    btn.setDisable(true);
                    btn.setOnMouseClicked(null);
                }
                continue;
            }

            item itm = shopItems.get(i);
            int itemId = itm.getItem_id();
            int available = shopAvailable.containsKey(itemId) ? shopAvailable.get(itemId).get() : 0;

            if (img != null) img.setImage(ItemDatabase.getImage(itemId));
            if (stockLbl != null) stockLbl.setText("x" + available);
            if (nameLbl != null) nameLbl.setText(itm.getItem_name());

            if (btn != null) {
                btn.setDisable(mode != Mode.BUY);
                final int finalItemId = itemId;
                btn.setOnMouseClicked(e -> {
                    if (mode != Mode.BUY) return;

                    if (e.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                        int current = buyCart.containsKey(finalItemId) ? buyCart.get(finalItemId).get() : 0;
                        setBuySelectionQty(finalItemId, current + 1);
                    } else if (e.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
                        int current = buyCart.containsKey(finalItemId) ? buyCart.get(finalItemId).get() : 0;
                        setBuySelectionQty(finalItemId, current - 1);
                    }
                });
            }
        }
    }

    private void refreshSellPane() {
        List<Integer> selected = new ArrayList<>(sellCart.keySet());
        selected.sort(Comparator.comparing(this::itemNameSafe, String.CASE_INSENSITIVE_ORDER));

        for (int i = 0; i < sellBtns.size(); i++) {
            ImageView img = sellImages.get(i);
            Label lbl = sellLabels.get(i);
            Button btn = sellBtns.get(i);

            if (i >= selected.size()) {
                if (img != null) img.setImage(null);
                if (lbl != null) lbl.setText("");
                if (btn != null) {
                    btn.setDisable(true);
                    btn.setOnAction(null);
                }
                continue;
            }

            int itemId = selected.get(i);
            int qty = sellCart.get(itemId).get();

            if (img != null) {
                img.setImage(ItemDatabase.getImage(itemId));
            }

            if (lbl != null) {
                lbl.setText("x" + qty);
            }

            if (btn != null) {
                btn.setDisable(mode != Mode.SELL);
                btn.setOnAction(e -> setSellSelectionQty(itemId, qty - 1));
            }
        }
    }

    private void loadShopItemsForToday() {
        String sql = "SELECT item_id, item_name, price FROM items";
        dbcon db = new dbcon();

        try (
            Connection con = db.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            List<item> allItems = new ArrayList<>();
            Map<Integer, item> allById = new LinkedHashMap<>();

            while (rs.next()) {
                int itemId = rs.getInt("item_id");
                String itemName = rs.getString("item_name");
                int price = rs.getInt("price");

                item itm = new item(itemId, itemName, price);
                allItems.add(itm);
                allById.put(itemId, itm);
            }

            shopItems.clear();
            dbItemsById.clear();

            for (int i = 0; i < 8 && i < allItems.size(); i++) {
                item itm = allItems.get(i);
                shopItems.add(itm);
                dbItemsById.put(itm.getItem_id(), itm);
            }

            int today = GameTimeManager.get().dayNumberProperty().get();
            List<Integer> savedRandomIds = gameState.getDailyRandomShopItemIds();

            boolean needNewRandom =
                gameState.getShopRandomDay() != today ||
                savedRandomIds == null ||
                savedRandomIds.size() < 4;

            if (needNewRandom) {
                List<item> randomPool = new ArrayList<>(allItems);
                randomPool.removeIf(itm -> dbItemsById.containsKey(itm.getItem_id()));
                Collections.shuffle(randomPool);

                List<Integer> newRandomIds = new ArrayList<>();
                for (int i = 0; i < 4 && i < randomPool.size(); i++) {
                    newRandomIds.add(randomPool.get(i).getItem_id());
                }

                gameState.setShopRandomDay(today);
                gameState.setDailyRandomShopItemIds(newRandomIds);
                savedRandomIds = newRandomIds;
            }

            for (Integer itemId : savedRandomIds) {
                item itm = allById.get(itemId);
                if (itm != null && !dbItemsById.containsKey(itemId) && shopItems.size() < 12) {
                    shopItems.add(itm);
                    dbItemsById.put(itemId, itm);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rebuildDailyShopAvailability() {
        int today = GameTimeManager.get().dayNumberProperty().get();

        shopAvailable.clear();
        dailyTotalShopStock.clear();

        boolean hasSavedStock = gameState.hasDailyRemainingShopStockForDay(today);

        Map<Integer, Integer> savedRemaining = hasSavedStock
            ? gameState.getDailyRemainingShopStock()
            : Map.of();

        for (item itm : shopItems) {
            int itemId = itm.getItem_id();
            int defaultStock = getDefaultShopStock(itemId);

            dailyTotalShopStock.put(itemId, defaultStock);

            int remaining = hasSavedStock
                ? savedRemaining.getOrDefault(itemId, defaultStock)
                : defaultStock;

            shopAvailable.put(itemId, new SimpleIntegerProperty(remaining));
        }

        gameState.setShopStockDay(today);
        persistCurrentShopRemainingStock();
    }

    private void persistCurrentShopRemainingStock() {
        Map<Integer, Integer> stock = new LinkedHashMap<>();

        for (var e : shopAvailable.entrySet()) {
            stock.put(e.getKey(), e.getValue().get());
        }

        gameState.setDailyRemainingShopStock(stock);
    }
    
    private int getDefaultShopStock(int itemId) {
        item itm = dbItemsById.get(itemId);
        if (itm == null) return 10;

        String name = itm.getItem_name().toLowerCase(Locale.ROOT);

        if (name.contains("seed")) return 25;
        if (name.contains("tool")) return 4;
        return 10;
    }

    private void rebuildSellAvailabilityFromInventory() {
        inventoryAvailableForSell.clear();

        for (Integer itemId : inv.itemsProperty().keySet()) {
            int owned = inv.getCount(itemId);
            int selected = sellCart.containsKey(itemId) ? sellCart.get(itemId).get() : 0;
            int remaining = Math.max(0, owned - selected);
            inventoryAvailableForSell.put(itemId, new SimpleIntegerProperty(remaining));
        }
    }

    // ---------- central synced quantity methods ----------

    private void setBuySelectionQty(int itemId, int newQty) {
        int totalStock = dailyTotalShopStock.getOrDefault(itemId, 0);
        int clamped = Math.max(0, Math.min(newQty, totalStock));
        int available = totalStock - clamped;

        if (clamped <= 0) {
            buyCart.remove(itemId);
        } else {
            buyCart.computeIfAbsent(itemId, k -> new SimpleIntegerProperty(0)).set(clamped);
        }

        shopAvailable
            .computeIfAbsent(itemId, k -> new SimpleIntegerProperty(0))
            .set(available);

        persistCurrentShopRemainingStock();
        refreshTableFromBuyCart();
        refreshBuyPane();
        refreshTotalLabel();
    }

    private void setSellSelectionQty(int itemId, int newQty) {
        int owned = inv.getCount(itemId);
        int clamped = Math.max(0, Math.min(newQty, owned));
        int remaining = owned - clamped;

        if (clamped <= 0) {
            sellCart.remove(itemId);
        } else {
            sellCart.computeIfAbsent(itemId, k -> new SimpleIntegerProperty(0)).set(clamped);
        }

        inventoryAvailableForSell
            .computeIfAbsent(itemId, k -> new SimpleIntegerProperty(owned))
            .set(remaining);

        refreshTableFromSellCart();
        refreshInventoryButtons();
        refreshSellPane();
        refreshTotalLabel();
    }

    private void clearBuySelection() {
        buyCart.clear();
        for (var e : dailyTotalShopStock.entrySet()) {
            shopAvailable
                .computeIfAbsent(e.getKey(), k -> new SimpleIntegerProperty(0))
                .set(e.getValue());
        }
    }

    private void clearSellSelection() {
        sellCart.clear();
        inventoryAvailableForSell.clear();
        rebuildSellAvailabilityFromInventory();
    }

    private void refreshTableFromBuyCart() {
        detailRows.clear();

        for (var e : buyCart.entrySet()) {
            int itemId = e.getKey();
            IntegerProperty qtyProp = e.getValue();
            item itm = dbItemsById.get(itemId);

            if (itm != null) {
                detailRows.add(new DetailRow(itm, qtyProp, itm.getPrice()));
            }
        }

        refreshTotalLabel();
    }

    private void refreshTableFromSellCart() {
        detailRows.clear();

        for (var e : sellCart.entrySet()) {
            int itemId = e.getKey();
            IntegerProperty qtyProp = e.getValue();
            item itm = ItemDatabase.getItemById(itemId);

            if (itm != null) {
                detailRows.add(new DetailRow(itm, qtyProp, sellPrice(itemId)));
            }
        }

        refreshTotalLabel();
    }

    private int sellPrice(int itemId) {
        item itm = ItemDatabase.getItemById(itemId);
        if (itm == null) return 1;
        return Math.max(1, itm.getPrice() / 2);
    }

    private int computeBuyTotal() {
        int sum = 0;

        for (var e : buyCart.entrySet()) {
            int itemId = e.getKey();
            int qty = e.getValue().get();
            item itm = dbItemsById.get(itemId);

            if (itm != null) {
                sum += itm.getPrice() * qty;
            }
        }

        return sum;
    }

    private int computeSellTotal() {
        int sum = 0;

        for (var e : sellCart.entrySet()) {
            int itemId = e.getKey();
            int qty = e.getValue().get();
            sum += sellPrice(itemId) * qty;
        }

        return sum;
    }

    private void refreshTotalLabel() {
        int total = (mode == Mode.BUY) ? computeBuyTotal() : computeSellTotal();
        lbltotal.setText(total + "g");
    }

    private String itemNameSafe(int itemId) {
        item itm = ItemDatabase.getItemById(itemId);
        return (itm == null) ? ("Item " + itemId) : itm.getItem_name();
    }
}