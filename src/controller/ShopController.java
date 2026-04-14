package controller;

import game.GameState;
import game.GameTimeManager;
import game.SceneController;
import java.net.URL;
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
import util.AppDialogs;
import util.SpawnTag;

public class ShopController implements Initializable, SceneController {
    private static final int FIXED_BUY_SLOT_COUNT = 8;
    private static final int RANDOM_BUY_SLOT_COUNT = 4;
    private static final int MAX_STOCK_PER_ITEM = 20;
    private static final int RESTOCK_PER_DAY = 10;
    private static final String HOTBAR_SELL_ENTRY_STYLE = "-fx-text-fill: #8fdcff; -fx-font-weight: bold;";

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
    // Per-slot reservation tracking for sell mode (supports split stacks)
    private final Map<SellSlotKey, Integer> reservedForSaleBySlot = new LinkedHashMap<>();
    private final Map<Integer, ArrayDeque<SellSlotKey>> sellReservationOrderByItemId = new LinkedHashMap<>();

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

    private enum SellInventoryEntrySource {
        INVENTORY,
        HOTBAR
    }

    private static final class SellSlotKey {
        private final SellInventoryEntrySource source;
        private final int itemId;
        private final int inventoryStackIndex;
        private final int hotbarIndex;

        private SellSlotKey(SellInventoryEntrySource source, int itemId, int inventoryStackIndex, int hotbarIndex) {
            this.source = (source == null) ? SellInventoryEntrySource.INVENTORY : source;
            this.itemId = itemId;
            this.inventoryStackIndex = inventoryStackIndex;
            this.hotbarIndex = hotbarIndex;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SellSlotKey)) return false;
            SellSlotKey that = (SellSlotKey) o;
            return itemId == that.itemId &&
                inventoryStackIndex == that.inventoryStackIndex &&
                hotbarIndex == that.hotbarIndex &&
                source == that.source;
        }

        @Override
        public int hashCode() {
            return Objects.hash(source, itemId, inventoryStackIndex, hotbarIndex);
        }
    }

    private static final class SellInventoryEntry {
        private final int itemId;
        private final int quantity;
        private final SellInventoryEntrySource source;
        private final int hotbarIndex;
        private final int inventoryStackIndex;

        private SellInventoryEntry(
            int itemId,
            int quantity,
            SellInventoryEntrySource source,
            int hotbarIndex,
            int inventoryStackIndex
        ) {
            this.itemId = itemId;
            this.quantity = quantity;
            this.source = (source == null) ? SellInventoryEntrySource.INVENTORY : source;
            this.hotbarIndex = hotbarIndex;
            this.inventoryStackIndex = inventoryStackIndex;
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
    private final List<SellInventoryEntry> displayedSellInventoryEntries = new ArrayList<>();

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

        loadShopItemsForToday();
        rebuildDailyShopAvailability();
        rebuildSellAvailabilityFromInventory();

        GameTimeManager.get().dayNumberProperty().addListener((obs, oldDay, newDay) -> {
            if (!Objects.equals(oldDay, newDay)) {
                clearBuySelectionCommitted();
                loadShopItemsForToday();
                rebuildDailyShopAvailability();
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
        int selectedCount = computeBuySelectedCount();
        if (selectedCount <= 0 || total <= 0) {
            AppDialogs.showMessageDialog(null, "No item selected to buy.");
            return;
        }

        Map<Integer, Integer> additions = new LinkedHashMap<>();
        for (var e : buyCart.entrySet()) {
            int itemId = e.getKey();
            int qty = e.getValue() == null ? 0 : e.getValue().get();
            if (itemId > 0 && qty > 0) {
                additions.merge(itemId, qty, Integer::sum);
            }
        }

        if (!additions.isEmpty() && !inv.canAddItems(additions)) {
            AppDialogs.showMessageDialog(null, "Not enough inventory space to buy those items.");
            return;
        }
        if (!inv.canAfford(total)) return;

        int coinsBefore = inv.getCoins();
        if (!inv.spendCoins(total)) return;

        if (!additions.isEmpty() && !inv.addItems(additions)) {
            inv.addCoins(total);
            AppDialogs.showMessageDialog(null, "Not enough inventory space to buy those items.");
            return;
        }

        clearBuySelectionCommitted();
        persistCurrentShopRemainingStock();
        rebuildSellAvailabilityFromInventory();
        refreshTableFromBuyCart();
        refreshAllPanels();

        int coinsAfter = inv.getCoins();
        AppDialogs.showMessageDialog(
            null,
            "Bought " + selectedCount + " item(s).\nMoney: -" + total + "g\nCoins: " + coinsBefore + "g -> " + coinsAfter + "g",
            "Purchase Complete",
            javax.swing.JOptionPane.INFORMATION_MESSAGE
        );
    }

    @FXML
    private void handlesellaction(ActionEvent event) {
        if (mode != Mode.SELL) return;
        int selectedCount = computeSellSelectedCount();
        if (selectedCount <= 0) {
            AppDialogs.showMessageDialog(null, "No item selected to sell.");
            return;
        }

        for (var e : sellCart.entrySet()) {
            int itemId = e.getKey();
            int qty = e.getValue().get();
            if (getCombinedOwnedCount(itemId) < qty) return;
        }

        int totalGain = computeSellTotal();
        int coinsBefore = inv.getCoins();

        boolean removed;
        if (!reservedForSaleBySlot.isEmpty()) {
            removed = removeReservedItemsForSale();
        } else {
            removed = true;
            for (var e : sellCart.entrySet()) {
                int itemId = e.getKey();
                int qty = e.getValue().get();
                if (!removeOwnedItemsForSale(itemId, qty)) {
                    removed = false;
                    break;
                }
            }
        }

        if (!removed) {
            return;
        }

        inv.addCoins(totalGain);

        clearSellSelection();
        rebuildSellAvailabilityFromInventory();
        refreshTableFromSellCart();
        refreshAllPanels();

        int coinsAfter = inv.getCoins();
        AppDialogs.showMessageDialog(
            null,
            "Sold " + selectedCount + " item(s).\nMoney: +" + totalGain + "g\nCoins: " + coinsBefore + "g -> " + coinsAfter + "g",
            "Sale Complete",
            javax.swing.JOptionPane.INFORMATION_MESSAGE
        );
    }

    @FXML
    private void handlebackaction(ActionEvent event) {
        releaseBuySelectionReservations();
        persistCurrentShopRemainingStock();
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
        displayedSellInventoryEntries.clear();
        displayedSellInventoryEntries.addAll(buildDisplayedSellInventoryEntries());

        for (int i = 0; i < invBtns.size(); i++) {
            Button b = invBtns.get(i);
            ImageView img = (i < invImages.size()) ? invImages.get(i) : null;
            Label lbl = (i < invLabels.size()) ? invLabels.get(i) : null;

            if (i >= displayedSellInventoryEntries.size()) {
                if (img != null) img.setImage(null);
                if (lbl != null) {
                    lbl.setText("");
                    lbl.setStyle("");
                }
                if (b != null) {
                    b.setText("");
                    b.setOnAction(null);
                    b.setDisable(true);
                }
                continue;
            }

            SellInventoryEntry entry = displayedSellInventoryEntries.get(i);
            int itemId = entry.itemId;
            int availableQty = getSellSlotAvailableCount(entry);

            if (img != null) {
                img.setImage(ItemDatabase.getImage(itemId));
            }
            if (lbl != null) {
                if (entry.source == SellInventoryEntrySource.HOTBAR) {
                    lbl.setText("H" + (entry.hotbarIndex + 1) + " x" + availableQty);
                    lbl.setStyle(HOTBAR_SELL_ENTRY_STYLE);
                } else {
                    lbl.setText(availableQty > 0 ? ("x" + availableQty) : "");
                    lbl.setStyle("");
                }
            }
            if (b != null) {
                b.setText(itemNameSafe(itemId));
                b.setDisable(mode != Mode.SELL || availableQty <= 0);
                b.setOnAction(null);
                b.setOnMouseClicked(e -> {
                    if (mode != Mode.SELL) return;
                    if (e.getButton() != javafx.scene.input.MouseButton.PRIMARY) return;
                    reserveItemForSaleFromSlot(entry, 1);
                });
            }
        }
    }

    private List<SellInventoryEntry> buildDisplayedSellInventoryEntries() {
        List<SellInventoryEntry> displayed = new ArrayList<>();
        int slotCapacity = Math.min(invImages.size(), Math.min(invLabels.size(), invBtns.size()));
        if (slotCapacity <= 0) {
            return displayed;
        }

        List<Integer> inventoryItems = new ArrayList<>(inv.itemsProperty().keySet());
        inventoryItems.removeIf(itemId -> getSellAvailableCount(itemId) <= 0);
        inventoryItems.sort(Comparator.comparing(this::itemNameSafe, String.CASE_INSENSITIVE_ORDER));

        for (Integer itemId : inventoryItems) {
            if (itemId == null || itemId <= 0) {
                continue;
            }

            int remaining = inv.getCount(itemId);
            if (remaining <= 0) continue;

            int maxStack = Math.max(1, ItemDatabase.getMaxStackSize(itemId));
            int stackIndex = 0;
            while (remaining > 0 && displayed.size() < slotCapacity) {
                int stackQty = Math.min(maxStack, remaining);
                SellInventoryEntry entry = new SellInventoryEntry(
                    itemId,
                    stackQty,
                    SellInventoryEntrySource.INVENTORY,
                    -1,
                    stackIndex
                );
                if (getSellSlotAvailableCount(entry) > 0) {
                    displayed.add(entry);
                }
                stackIndex++;
                remaining -= stackQty;
            }

            if (displayed.size() >= slotCapacity) return displayed;
        }

        if (gameState == null || gameState.getPlayerState() == null) {
            return displayed;
        }

        for (int i = 0; i < gameState.getPlayerState().getHotbarSize(); i++) {
            int itemId = gameState.getPlayerState().getHotbarItemId(i);
            int quantity = gameState.getPlayerState().getHotbarItemCount(i);
            if (itemId <= 0 || quantity <= 0) {
                continue;
            }
            SellInventoryEntry entry = new SellInventoryEntry(
                itemId,
                quantity,
                SellInventoryEntrySource.HOTBAR,
                i,
                -1
            );
            if (getSellSlotAvailableCount(entry) > 0) {
                displayed.add(entry);
            }
            if (displayed.size() >= slotCapacity) {
                break;
            }
        }

        return displayed;
    }

    private SellSlotKey getSellSlotKey(SellInventoryEntry entry) {
        if (entry == null) return null;
        if (entry.source == SellInventoryEntrySource.HOTBAR) {
            return new SellSlotKey(SellInventoryEntrySource.HOTBAR, entry.itemId, -1, entry.hotbarIndex);
        }
        return new SellSlotKey(SellInventoryEntrySource.INVENTORY, entry.itemId, entry.inventoryStackIndex, -1);
    }

    private int getReservedForSaleCount(SellSlotKey key) {
        if (key == null) return 0;
        Integer reserved = reservedForSaleBySlot.get(key);
        return reserved == null ? 0 : Math.max(0, reserved);
    }

    private int getSellSlotAvailableCount(SellInventoryEntry entry) {
        if (entry == null) return 0;
        SellSlotKey key = getSellSlotKey(entry);
        int reserved = getReservedForSaleCount(key);
        int base = Math.max(0, entry.quantity);
        if (reserved >= base) return 0;
        return base - reserved;
    }

    private void reserveItemForSaleFromSlot(SellInventoryEntry entry, int amount) {
        if (entry == null || amount <= 0) return;
        if (mode != Mode.SELL) return;

        int reserved = reserveItemForSaleFromSlotInternal(entry, amount);
        if (reserved <= 0) return;

        rebuildSellAvailabilityFromInventory();
        refreshTableFromSellCart();
        refreshInventoryButtons();
        refreshSellPane();
        refreshTotalLabel();
    }

    private int reserveItemForSaleFromSlotInternal(SellInventoryEntry entry, int amount) {
        if (entry == null || amount <= 0) return 0;
        int itemId = entry.itemId;
        if (itemId <= 0) return 0;

        int available = getSellSlotAvailableCount(entry);
        if (available <= 0) return 0;
        int toReserve = Math.min(amount, available);

        SellSlotKey key = getSellSlotKey(entry);
        if (key == null) return 0;

        int currentReserved = getReservedForSaleCount(key);
        reservedForSaleBySlot.put(key, currentReserved + toReserve);

        ArrayDeque<SellSlotKey> order = sellReservationOrderByItemId.computeIfAbsent(itemId, k -> new ArrayDeque<>());
        for (int i = 0; i < toReserve; i++) {
            order.addLast(key);
        }

        int currentSelected = sellCart.containsKey(itemId) ? sellCart.get(itemId).get() : 0;
        int nextSelected = currentSelected + toReserve;
        sellCart.computeIfAbsent(itemId, k -> new SimpleIntegerProperty(0)).set(nextSelected);

        return toReserve;
    }

    private int reserveItemForSaleAuto(int itemId, int amount) {
        if (itemId <= 0 || amount <= 0) return 0;
        int remaining = amount;

        int invCount = inv.getCount(itemId);
        int maxStack = Math.max(1, ItemDatabase.getMaxStackSize(itemId));
        int stackIndex = 0;

        while (invCount > 0 && remaining > 0) {
            int stackQty = Math.min(maxStack, invCount);
            SellInventoryEntry entry = new SellInventoryEntry(
                itemId,
                stackQty,
                SellInventoryEntrySource.INVENTORY,
                -1,
                stackIndex
            );
            int reserved = reserveItemForSaleFromSlotInternal(entry, remaining);
            remaining -= reserved;

            stackIndex++;
            invCount -= stackQty;
        }

        if (remaining > 0 && gameState != null && gameState.getPlayerState() != null) {
            for (int i = 0; i < gameState.getPlayerState().getHotbarSize() && remaining > 0; i++) {
                if (gameState.getPlayerState().getHotbarItemId(i) != itemId) continue;
                int qty = gameState.getPlayerState().getHotbarItemCount(i);
                if (qty <= 0) continue;

                SellInventoryEntry entry = new SellInventoryEntry(
                    itemId,
                    qty,
                    SellInventoryEntrySource.HOTBAR,
                    i,
                    -1
                );
                int reserved = reserveItemForSaleFromSlotInternal(entry, remaining);
                remaining -= reserved;
            }
        }

        return amount - remaining;
    }

    private int releaseReservedForSaleInternal(int itemId, int amount) {
        if (itemId <= 0 || amount <= 0) return 0;
        int currentSelected = sellCart.containsKey(itemId) ? sellCart.get(itemId).get() : 0;
        if (currentSelected <= 0) return 0;
        int toRelease = Math.min(amount, currentSelected);

        int released = 0;
        ArrayDeque<SellSlotKey> order = sellReservationOrderByItemId.get(itemId);

        while (released < toRelease) {
            SellSlotKey key = (order == null) ? null : order.pollLast();
            if (key == null) {
                key = findAnyReservedSlotKeyForItem(itemId);
            }
            if (key == null) {
                break;
            }

            int currentReserved = getReservedForSaleCount(key);
            if (currentReserved <= 1) {
                reservedForSaleBySlot.remove(key);
            } else {
                reservedForSaleBySlot.put(key, currentReserved - 1);
            }
            released++;
        }

        int nextSelected = currentSelected - released;
        if (nextSelected <= 0) {
            sellCart.remove(itemId);
            sellReservationOrderByItemId.remove(itemId);
        } else {
            sellCart.computeIfAbsent(itemId, k -> new SimpleIntegerProperty(0)).set(nextSelected);
        }

        return released;
    }

    private SellSlotKey findAnyReservedSlotKeyForItem(int itemId) {
        for (var e : reservedForSaleBySlot.entrySet()) {
            SellSlotKey key = e.getKey();
            Integer qty = e.getValue();
            if (key != null && key.itemId == itemId && qty != null && qty > 0) {
                return key;
            }
        }
        return null;
    }

    private int getSellAvailableCount(int itemId) {
        IntegerProperty remaining = inventoryAvailableForSell.get(itemId);
        if (remaining != null) {
            return Math.max(0, remaining.get());
        }
        return getCombinedOwnedCount(itemId);
    }

    private int getCombinedOwnedCount(int itemId) {
        if (itemId <= 0 || gameState == null || gameState.getPlayerState() == null) {
            return 0;
        }

        int total = inv.getCount(itemId);
        for (int i = 0; i < gameState.getPlayerState().getHotbarSize(); i++) {
            if (gameState.getPlayerState().getHotbarItemId(i) == itemId) {
                total += gameState.getPlayerState().getHotbarItemCount(i);
            }
        }
        return total;
    }

    private boolean removeOwnedItemsForSale(int itemId, int quantity) {
        if (itemId <= 0 || quantity <= 0) {
            return false;
        }

        int remaining = quantity;
        int fromInventory = Math.min(inv.getCount(itemId), remaining);
        if (fromInventory > 0 && !inv.removeItem(itemId, fromInventory)) {
            return false;
        }
        remaining -= fromInventory;

        if (remaining <= 0 || gameState == null || gameState.getPlayerState() == null) {
            return remaining <= 0;
        }

        for (int i = 0; i < gameState.getPlayerState().getHotbarSize() && remaining > 0; i++) {
            if (gameState.getPlayerState().getHotbarItemId(i) != itemId) {
                continue;
            }
            int slotQty = gameState.getPlayerState().getHotbarItemCount(i);
            int removeFromSlot = Math.min(slotQty, remaining);
            if (removeFromSlot > 0 && !gameState.getPlayerState().removeHotbarItems(i, removeFromSlot)) {
                return false;
            }
            remaining -= removeFromSlot;
        }

        return remaining <= 0;
    }

    private boolean removeReservedItemsForSale() {
        if (gameState == null || gameState.getPlayerState() == null) {
            return false;
        }

        Map<Integer, Integer> removeFromInventory = new LinkedHashMap<>();
        Map<Integer, Integer> removeFromHotbar = new LinkedHashMap<>();
        Map<Integer, Integer> hotbarItemIds = new LinkedHashMap<>();

        for (var e : reservedForSaleBySlot.entrySet()) {
            SellSlotKey key = e.getKey();
            Integer qtyObj = e.getValue();
            int qty = (qtyObj == null) ? 0 : qtyObj;
            if (key == null || qty <= 0) continue;

            if (key.source == SellInventoryEntrySource.HOTBAR) {
                removeFromHotbar.merge(key.hotbarIndex, qty, Integer::sum);
                hotbarItemIds.putIfAbsent(key.hotbarIndex, key.itemId);
            } else {
                removeFromInventory.merge(key.itemId, qty, Integer::sum);
            }
        }

        for (var e : removeFromInventory.entrySet()) {
            int itemId = e.getKey();
            int qty = e.getValue();
            if (qty <= 0) continue;
            if (!inv.removeItem(itemId, qty)) {
                return false;
            }
        }

        for (var e : removeFromHotbar.entrySet()) {
            int index = e.getKey();
            int qty = e.getValue();
            if (qty <= 0) continue;
            int expectedItemId = hotbarItemIds.getOrDefault(index, -1);
            int currentItemId = gameState.getPlayerState().getHotbarItemId(index);
            if (expectedItemId > 0 && currentItemId != expectedItemId) {
                return false;
            }
            if (!gameState.getPlayerState().removeHotbarItems(index, qty)) {
                return false;
            }
        }

        return true;
    }

    private void refreshBuyPane() {
        for (int i = 0; i < buyItemButtons.size(); i++) {
            ImageView img = buyImages.get(i);
            Label stockLbl = buyStockLabels.get(i);
            Label nameLbl = buyNameLabels.get(i);
            Button btn = buyItemButtons.get(i);

            if (i >= shopItems.size()) {
                if (img != null) {
                    img.setImage(null);
                    img.setStyle("");
                    img.setOpacity(1.0);
                    img.setScaleX(1.0);
                    img.setScaleY(1.0);
                }
                if (stockLbl != null) {
                    stockLbl.setText("");
                    stockLbl.setStyle("");
                }
                if (nameLbl != null) nameLbl.setText("");
                if (btn != null) {
                    btn.setDisable(true);
                    btn.setOnMouseClicked(null);
                }
                continue;
            }

            item itm = shopItems.get(i);
            int itemId = itm.getItem_id();
            int selected = buyCart.containsKey(itemId) ? buyCart.get(itemId).get() : 0;

            if (img != null) img.setImage(ItemDatabase.getImage(itemId));
            if (img != null) {
                boolean isSelected = selected > 0;
                img.setStyle(isSelected ? "-fx-effect: dropshadow(gaussian, yellow, 12, 0.8, 0, 0);" : "");
                img.setOpacity(isSelected ? 1.0 : 0.9);
                img.setScaleX(isSelected ? 1.06 : 1.0);
                img.setScaleY(isSelected ? 1.06 : 1.0);
            }
            if (stockLbl != null) {
                stockLbl.setText(selected > 0 ? ("x" + selected) : "");
                stockLbl.setStyle(selected > 0 ? "-fx-text-fill: #f7f3b4; -fx-font-weight: bold;" : "");
            }
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

        List<int[]> stackEntries = new ArrayList<>();
        int maxStacksToShow = sellBtns.size();

        for (Integer itemId : selected) {
            if (itemId == null || itemId <= 0) continue;

            IntegerProperty qtyProp = sellCart.get(itemId);
            int qty = qtyProp == null ? 0 : qtyProp.get();
            if (qty <= 0) continue;

            int maxStack = Math.max(1, ItemDatabase.getMaxStackSize(itemId));
            int remaining = qty;
            while (remaining > 0 && stackEntries.size() < maxStacksToShow) {
                int stackQty = Math.min(maxStack, remaining);
                stackEntries.add(new int[] { itemId, stackQty });
                remaining -= stackQty;
            }

            if (stackEntries.size() >= maxStacksToShow) {
                break;
            }
        }

        for (int i = 0; i < sellBtns.size(); i++) {
            ImageView img = sellImages.get(i);
            Label lbl = sellLabels.get(i);
            Button btn = sellBtns.get(i);

            if (i >= stackEntries.size()) {
                if (img != null) {
                    img.setImage(null);
                    img.setStyle("");
                    img.setOpacity(1.0);
                    img.setScaleX(1.0);
                    img.setScaleY(1.0);
                }
                if (lbl != null) {
                    lbl.setText("");
                    lbl.setStyle("");
                }
                if (btn != null) {
                    btn.setDisable(true);
                    btn.setOnAction(null);
                }
                continue;
            }

            int[] entry = stackEntries.get(i);
            int itemId = entry[0];
            int stackQty = entry[1];
            int totalQty = sellCart.containsKey(itemId) ? sellCart.get(itemId).get() : 0;

            if (img != null) {
                img.setImage(ItemDatabase.getImage(itemId));
                img.setStyle("-fx-effect: dropshadow(gaussian, #9cff6a, 12, 0.8, 0, 0);");
                img.setOpacity(1.0);
                img.setScaleX(1.06);
                img.setScaleY(1.06);
            }

            if (lbl != null) {
                lbl.setText("x" + stackQty);
                lbl.setStyle("-fx-text-fill: #c6ffb0; -fx-font-weight: bold;");
            }

            if (btn != null) {
                btn.setDisable(mode != Mode.SELL);
                btn.setOnAction(e -> setSellSelectionQty(itemId, totalQty - 1));
            }
        }
    }

    private void loadShopItemsForToday() {
        Map<Integer, item> allById = new LinkedHashMap<>();
        for (item itm : ItemDatabase.getAllItems()) {
            if (itm == null) continue;
            int itemId = itm.getItem_id();
            if (!ItemDatabase.isShopDisplayableItem(itemId)) continue;
            allById.put(itemId, itm);
        }

        shopItems.clear();
        dbItemsById.clear();

        int today = GameTimeManager.get().dayNumberProperty().get();
        int shopSlotCapacity = buyItemButtons.isEmpty() ? 12 : buyItemButtons.size();
        int fixedSlotCapacity = Math.min(FIXED_BUY_SLOT_COUNT, Math.max(0, shopSlotCapacity));
        int randomSlotCapacity = Math.min(
            RANDOM_BUY_SLOT_COUNT,
            Math.max(0, shopSlotCapacity - fixedSlotCapacity)
        );
        int fixedVisibleTarget = Math.min(fixedSlotCapacity, allById.size());
        int totalVisibleTarget = Math.min(allById.size(), fixedVisibleTarget + randomSlotCapacity);

        for (Integer itemId : gameState.getFixedShopItemIdsForDay(today)) {
            item itm = allById.get(itemId);
            if (itm != null && !dbItemsById.containsKey(itemId) && shopItems.size() < fixedVisibleTarget) {
                shopItems.add(itm);
                dbItemsById.put(itemId, itm);
            }
        }

        int targetRandomCount = Math.max(0, totalVisibleTarget - shopItems.size());

        List<Integer> savedRandomIds = new ArrayList<>();
        for (Integer itemId : gameState.getDailyRandomShopItemIds()) {
            if (itemId == null || dbItemsById.containsKey(itemId) || !allById.containsKey(itemId)) {
                continue;
            }
            if (!savedRandomIds.contains(itemId)) {
                savedRandomIds.add(itemId);
            }
        }

        boolean needNewRandom =
            gameState.getShopRandomDay() != today ||
            savedRandomIds.size() < targetRandomCount;

        List<Integer> selectedRandomIds = new ArrayList<>();

        if (!needNewRandom) {
            for (Integer itemId : savedRandomIds) {
                if (selectedRandomIds.size() >= targetRandomCount) break;
                selectedRandomIds.add(itemId);
            }
        } else {
            List<item> randomPool = new ArrayList<>(allById.values());
            randomPool.removeIf(itm -> dbItemsById.containsKey(itm.getItem_id()));
            Collections.shuffle(randomPool);

            for (item itm : randomPool) {
                if (selectedRandomIds.size() >= targetRandomCount) break;
                selectedRandomIds.add(itm.getItem_id());
            }
        }

        if (selectedRandomIds.size() < targetRandomCount) {
            List<item> fallbackPool = new ArrayList<>(allById.values());
            fallbackPool.removeIf(itm ->
                dbItemsById.containsKey(itm.getItem_id()) || selectedRandomIds.contains(itm.getItem_id())
            );
            Collections.shuffle(fallbackPool);

            for (item itm : fallbackPool) {
                if (selectedRandomIds.size() >= targetRandomCount) break;
                selectedRandomIds.add(itm.getItem_id());
            }
        }

        gameState.setShopRandomDay(today);
        gameState.setDailyRandomShopItemIds(selectedRandomIds);

        for (Integer itemId : selectedRandomIds) {
            item itm = allById.get(itemId);
            if (itm != null && !dbItemsById.containsKey(itemId) && shopItems.size() < totalVisibleTarget) {
                shopItems.add(itm);
                dbItemsById.put(itemId, itm);
            }
        }

        if (shopItems.size() < totalVisibleTarget) {
            List<item> fallbackPool = new ArrayList<>(allById.values());
            fallbackPool.removeIf(itm -> dbItemsById.containsKey(itm.getItem_id()));
            Collections.shuffle(fallbackPool);

            for (item itm : fallbackPool) {
                if (shopItems.size() >= totalVisibleTarget) break;
                shopItems.add(itm);
                dbItemsById.put(itm.getItem_id(), itm);
            }
        }
    }

    private void rebuildDailyShopAvailability() {
        int today = GameTimeManager.get().dayNumberProperty().get();
        int savedDay = gameState.getShopStockDay();
        int dayDelta = Math.max(0, today - savedDay);
        Map<Integer, Integer> savedRemaining = gameState.getDailyRemainingShopStock();
        boolean hasSavedStock = !savedRemaining.isEmpty();

        shopAvailable.clear();
        dailyTotalShopStock.clear();

        for (item itm : shopItems) {
            int itemId = itm.getItem_id();
            int remaining = hasSavedStock
                ? savedRemaining.getOrDefault(itemId, MAX_STOCK_PER_ITEM)
                : MAX_STOCK_PER_ITEM;

            if (dayDelta > 0) {
                remaining = Math.min(MAX_STOCK_PER_ITEM, remaining + (RESTOCK_PER_DAY * dayDelta));
            }

            dailyTotalShopStock.put(itemId, MAX_STOCK_PER_ITEM);

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
        return MAX_STOCK_PER_ITEM;
    }

    private void rebuildSellAvailabilityFromInventory() {
        inventoryAvailableForSell.clear();

        LinkedHashSet<Integer> ownedItemIds = new LinkedHashSet<>(inv.itemsProperty().keySet());
        if (gameState != null && gameState.getPlayerState() != null) {
            for (int i = 0; i < gameState.getPlayerState().getHotbarSize(); i++) {
                int itemId = gameState.getPlayerState().getHotbarItemId(i);
                if (itemId > 0 && gameState.getPlayerState().getHotbarItemCount(i) > 0) {
                    ownedItemIds.add(itemId);
                }
            }
        }

        for (Integer itemId : ownedItemIds) {
            int owned = getCombinedOwnedCount(itemId);
            int selected = sellCart.containsKey(itemId) ? sellCart.get(itemId).get() : 0;
            int remaining = Math.max(0, owned - selected);
            inventoryAvailableForSell.put(itemId, new SimpleIntegerProperty(remaining));
        }
    }

    // ---------- central synced quantity methods ----------

    private void setBuySelectionQty(int itemId, int newQty) {
        int currentSelected = buyCart.containsKey(itemId) ? buyCart.get(itemId).get() : 0;
        int currentAvailable = shopAvailable.containsKey(itemId) ? shopAvailable.get(itemId).get() : 0;
        int maxSelectable = Math.max(0, currentSelected + currentAvailable);
        int clamped = Math.max(0, Math.min(newQty, maxSelectable));
        int available = Math.max(0, maxSelectable - clamped);

        if (clamped <= 0) {
            buyCart.remove(itemId);
        } else {
            buyCart.computeIfAbsent(itemId, k -> new SimpleIntegerProperty(0)).set(clamped);
        }

        shopAvailable
            .computeIfAbsent(itemId, k -> new SimpleIntegerProperty(0))
            .set(available);

        refreshTableFromBuyCart();
        refreshBuyPane();
        refreshTotalLabel();
    }

    private void setSellSelectionQty(int itemId, int newQty) {
        int owned = getCombinedOwnedCount(itemId);
        int desired = Math.max(0, Math.min(newQty, owned));
        int currentSelected = sellCart.containsKey(itemId) ? sellCart.get(itemId).get() : 0;

        if (desired > currentSelected) {
            reserveItemForSaleAuto(itemId, desired - currentSelected);
        } else if (desired < currentSelected) {
            releaseReservedForSaleInternal(itemId, currentSelected - desired);
        }

        rebuildSellAvailabilityFromInventory();
        refreshTableFromSellCart();
        refreshInventoryButtons();
        refreshSellPane();
        refreshTotalLabel();
    }

    private void releaseBuySelectionReservations() {
        if (!buyCart.isEmpty()) {
            for (var e : buyCart.entrySet()) {
                int itemId = e.getKey();
                int selectedQty = e.getValue().get();
                int availableNow = shopAvailable.containsKey(itemId) ? shopAvailable.get(itemId).get() : 0;
                shopAvailable.computeIfAbsent(itemId, k -> new SimpleIntegerProperty(0))
                             .set(Math.min(MAX_STOCK_PER_ITEM, availableNow + selectedQty));
            }
        }
        buyCart.clear();
    }

    private void clearBuySelectionCommitted() {
        buyCart.clear();
    }

    private void clearSellSelection() {
        sellCart.clear();
        inventoryAvailableForSell.clear();
        reservedForSaleBySlot.clear();
        sellReservationOrderByItemId.clear();
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
        return Math.max(1, (int) Math.round(itm.getPrice() * 0.9));         //Sell price here
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

    private int computeBuySelectedCount() {
        int sum = 0;
        for (var e : buyCart.values()) sum += Math.max(0, e.get());
        return sum;
    }

    private int computeSellSelectedCount() {
        int sum = 0;
        for (var e : sellCart.values()) sum += Math.max(0, e.get());
        return sum;
    }

    private String itemNameSafe(int itemId) {
        item itm = ItemDatabase.getItemById(itemId);
        return (itm == null) ? ("Item " + itemId) : itm.getItem_name();
    }
}
