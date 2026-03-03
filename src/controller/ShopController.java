/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import game.GameState;
import game.SceneController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import model.Inventory;
import model.ItemType;
import model.Merchant;

import java.util.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author larry
 */
public class ShopController implements Initializable, SceneController {

    private GameState gameState;
    private Inventory inv;
    @FXML
    private StackPane root;
    @FXML
    private ImageView inyimg11;
    @FXML
    private Button buybtn;
    @FXML
    private Button sellbtn;

    @Override
    public void init(GameState gs) {
        this.gameState = gs;
        this.inv = gs.getPlayerState().getInventory();
    }

    private enum Mode { BUY, SELL }
    private Mode mode = Mode.BUY;

    private final Map<ItemType, IntegerProperty> buyCart = new LinkedHashMap<>();
    private final Map<ItemType, IntegerProperty> sellCart = new LinkedHashMap<>();

    // Merchant price lookup for BUY mode
    private final Map<ItemType, Integer> buyPriceByType = new EnumMap<>(ItemType.class);

    // rows shown in tableitemdetail
    public static class DetailRow {
        private final ItemType type;
        private final IntegerProperty qty;
        private final IntegerProperty unitPrice;

        public DetailRow(ItemType type, IntegerProperty qty, int unitPrice) {
            this.type = type;
            this.qty = qty;
            this.unitPrice = new SimpleIntegerProperty(unitPrice);
        }
        public ItemType getType() { return type; }
        public IntegerProperty qtyProperty() { return qty; }
        public int getQty() { return qty.get(); }
        public void setQty(int v) { qty.set(v); }
        public IntegerProperty unitPriceProperty() { return unitPrice; }
        public int getUnitPrice() { return unitPrice.get(); }
        public int getLineTotal() { return getQty() * getUnitPrice(); }
    }

    private final ObservableList<DetailRow> detailRows = FXCollections.observableArrayList();
    
    private final List<Button> invBtns = new ArrayList<>();
    private final List<Button> buyBtns = new ArrayList<>();
    private final List<Button> sellBtns = new ArrayList<>();
    
    @FXML
    private Label lblcoin;
    @FXML
    private ImageView inyimg1;
    @FXML
    private Button invbtn1;
    @FXML
    private Label invlbl1;
    @FXML
    private ImageView inyimg2;
    @FXML
    private Button invbtn2;
    @FXML
    private Label invlbl2;
    @FXML
    private ImageView inyimg3;
    @FXML
    private Button invbtn3;
    @FXML
    private Label invlbl3;
    @FXML
    private ImageView inyimg4;
    @FXML
    private Button invbtn4;
    @FXML
    private Label invlbl4;
    @FXML
    private ImageView inyimg5;
    @FXML
    private Button invbtn5;
    @FXML
    private Label invlbl5;
    @FXML
    private ImageView inyimg6;
    @FXML
    private Button invbtn6;
    @FXML
    private Label invlbl6;
    @FXML
    private ImageView inyimg7;
    @FXML
    private Button invbtn7;
    @FXML
    private Label invlbl7;
    @FXML
    private ImageView inyimg8;
    @FXML
    private Button invbtn8;
    @FXML
    private Label invlbl8;
    @FXML
    private ImageView inyimg9;
    @FXML
    private Button invbtn9;
    @FXML
    private Label invlbl9;
    @FXML
    private ImageView inyimg10;
    @FXML
    private Button invbtn10;
    @FXML
    private Label invlbl10;
    @FXML
    private ImageView inyimg12;
    @FXML
    private Button invbtn11;
    @FXML
    private Label invlbl11;
    @FXML
    private ImageView inyimg13;
    @FXML
    private Button invbtn12;
    @FXML
    private Label invlbl12;
    @FXML
    private ImageView inyimg14;
    @FXML
    private Button invbtn13;
    @FXML
    private Label invlbl13;
    @FXML
    private ImageView inyimg15;
    @FXML
    private Button invbtn14;
    @FXML
    private Label invlbl14;
    @FXML
    private Button invbtn15;
    @FXML
    private Label invlbl15;
    @FXML
    private ImageView inyimg16;
    @FXML
    private Button invbtn16;
    @FXML
    private Label invlbl16;
    @FXML
    private ImageView inyimg17;
    @FXML
    private Button invbtn17;
    @FXML
    private Label invlbl17;
    @FXML
    private ImageView inyimg18;
    @FXML
    private Button invbtn18;
    @FXML
    private Label invlbl18;
    @FXML
    private ImageView inyimg19;
    @FXML
    private Button invbtn19;
    @FXML
    private Label invlbl19;
    @FXML
    private ImageView inyimg20;
    @FXML
    private Button invbtn20;
    @FXML
    private Label invlbl20;
    @FXML
    private Pane buypane;
    @FXML
    private ImageView imgbuy1;
    @FXML
    private Button btnbuy1;
    @FXML
    private Label lblbuy1;
    @FXML
    private ImageView imgbuy2;
    @FXML
    private Button btnbuy2;
    @FXML
    private Label lblbuy2;
    @FXML
    private ImageView imgbuy3;
    @FXML
    private Button btnbuy3;
    @FXML
    private Label lblbuy3;
    @FXML
    private ImageView imgbuy4;
    @FXML
    private Button btnbuy4;
    @FXML
    private Label lblbuy4;
    @FXML
    private ImageView imgbuy5;
    @FXML
    private Button btnbuy5;
    @FXML
    private Label lblbuy5;
    @FXML
    private ImageView imgbuy6;
    @FXML
    private Button btnbuy6;
    @FXML
    private Label lblbuy6;
    @FXML
    private ImageView imgbuy7;
    @FXML
    private Button btnbuy7;
    @FXML
    private Label lblbuy7;
    @FXML
    private ImageView imgbuy8;
    @FXML
    private Button btnbuy8;
    @FXML
    private Label lblbuy8;
    @FXML
    private ImageView imgbuy9;
    @FXML
    private Button btnbuy9;
    @FXML
    private Label lblbuy9;
    @FXML
    private ImageView imgbuy10;
    @FXML
    private Button btnbuy10;
    @FXML
    private Label lblbuy10;
    @FXML
    private ImageView imgbuy11;
    @FXML
    private Button btnbuy11;
    @FXML
    private Label lblbuy11;
    @FXML
    private ImageView imgbuy12;
    @FXML
    private Button btnbuy12;
    @FXML
    private Label lblbuy12;
    @FXML
    private Pane sellpane;
    @FXML
    private ImageView imgsell1;
    @FXML
    private Button btnsell1;
    @FXML
    private Label lblsell1;
    @FXML
    private ImageView imgsell2;
    @FXML
    private Button btnsell2;
    @FXML
    private Label lblsell2;
    @FXML
    private ImageView imgsell3;
    @FXML
    private Button btnsell3;
    @FXML
    private Label lblsell3;
    @FXML
    private ImageView imgsell4;
    @FXML
    private Button btnsell4;
    @FXML
    private Label lblsell4;
    @FXML
    private ImageView imgsell5;
    @FXML
    private Button btnsell5;
    @FXML
    private Label lblsell5;
    @FXML
    private ImageView imgsell6;
    @FXML
    private Button btnsell6;
    @FXML
    private Label lblsell6;
    @FXML
    private ImageView imgsell7;
    @FXML
    private Button btnsell7;
    @FXML
    private Label lblsell7;
    @FXML
    private ImageView imgsell8;
    @FXML
    private Button btnsell8;
    @FXML
    private Label lblsell8;
    @FXML
    private ImageView imgsell9;
    @FXML
    private Button btnsell9;
    @FXML
    private Label lblsell9;
    @FXML
    private ImageView imgsell10;
    @FXML
    private Button btnsell10;
    @FXML
    private Label lblsell10;
    @FXML
    private ImageView imgsell11;
    @FXML
    private Button btnsell11;
    @FXML
    private Label lblsell11;
    @FXML
    private ImageView imgsell12;
    @FXML
    private Button btnsell12;
    @FXML
    private Label lblsell12;
    @FXML
    private Button btnback;
    @FXML
    private Label lbltotal;
    @FXML
    private TableView<DetailRow> tableitemdetail;
    @FXML
    private TableColumn<DetailRow, String> tableimg;
    @FXML
    private TableColumn<DetailRow, Integer> tablequantity;
    @FXML
    private TableColumn<DetailRow, Integer> tablecost;
    @FXML
    private Button colbuy;
    @FXML
    private Button colsell;
    

    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (gameState == null) gameState = GameState.get();
        if (inv == null) inv = gameState.getPlayerState().getInventory();

        setupMerchantCatalog();
        setupTable();
        bindCoins();
        setMode(Mode.BUY);     // default
        refreshTableFromActiveCart();
        refreshAllPanels();
        
    }    

    private void setupMerchantCatalog() {
        buyPriceByType.clear();

        for (Merchant.ShopItem si : gameState.getCurrentMerchantItems()) {
            ItemType t = mapShopNameToItemType(si.getName());
            if (t != null) buyPriceByType.put(t, si.getPrice());
        }
    }
    
    private ItemType mapShopNameToItemType(String shopName) {
        return switch (shopName) {
            case "Parsnip Seeds" -> ItemType.PARSNIP_SEEDS;
            case "Potato Seeds" -> ItemType.POTATO_SEEDS;
            case "Watering Can Upgrade" -> ItemType.WATERING_CAN_UPGRADE;
            default -> null;
        };
    }
    
    private void setupTable() {
        tableitemdetail.setEditable(true);
        tableitemdetail.setItems(detailRows);

        tableimg.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getType().getDisplayName())
        );

        tablequantity.setCellValueFactory(cd -> cd.getValue().qtyProperty().asObject());
        tablequantity.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        tablequantity.setOnEditCommit(e -> {
            DetailRow row = e.getRowValue();
            int newQty = (e.getNewValue() == null) ? row.getQty() : e.getNewValue();

            // allow "0 means remove" exactly as you requested
            if (newQty <= 0) {
                removeFromActiveCart(row.getType());
            } else {
                row.setQty(newQty);
            }
            refreshTableFromActiveCart();
        });
        tableitemdetail.setEditable(true);

        tablecost.setCellValueFactory(cd ->
            new SimpleIntegerProperty(cd.getValue().getLineTotal()).asObject()
        );
    }
    
    private Map<ItemType, IntegerProperty> activeCart() {
        return (mode == Mode.BUY) ? buyCart : sellCart;
    }

    private void removeFromActiveCart(ItemType t) {
        activeCart().remove(t);
    }

    private int unitPrice(ItemType t) {
        if (mode == Mode.BUY) {
            return buyPriceByType.getOrDefault(t, 0);
        } else {
            return sellPrice(t);
        }
    }

    // You didn’t specify sell pricing. Pick a rule:
    // here: 50% of buy price if it exists, otherwise 5g default for crops.
    private int sellPrice(ItemType t) {
        int base = buyPriceByType.getOrDefault(t, 0);
        if (base > 0) return Math.max(1, base / 2);

        return switch (t) {
            case GRAPE, CARROT, CORN, TOMATO -> 5;
            default -> 1;
        };
    }

    private void refreshTableFromActiveCart() {
        detailRows.clear();
        for (var e : activeCart().entrySet()) {
            ItemType t = e.getKey();
            IntegerProperty qtyProp = e.getValue();
            int price = unitPrice(t);
            detailRows.add(new DetailRow(t, qtyProp, price));
        }
        refreshTotalLabel();
    }
    
    @FXML
    private void handlebuyaction(ActionEvent event) {
        if (mode != Mode.BUY) return;

        int total = computeActiveTotal();
        if (!inv.canAfford(total)) return;

        // spend
        if (!inv.spendCoins(total)) return;

        // add items
        for (var e : buyCart.entrySet()) {
            inv.addItem(e.getKey(), e.getValue().get());
        }

        buyCart.clear();
        refreshTableFromActiveCart();
        refreshAllPanels();
    }

    @FXML
    private void handlesellaction(ActionEvent event) {
        if (mode != Mode.SELL) return;

        // validate availability
        for (var e : sellCart.entrySet()) {
            ItemType t = e.getKey();
            int qty = e.getValue().get();
            if (inv.getCount(t) < qty) return;
        }

        int totalGain = computeActiveTotal(); // uses sellPrice()

        // remove then pay
        for (var e : sellCart.entrySet()) {
            inv.removeItem(e.getKey(), e.getValue().get());
        }
        inv.addCoins(totalGain);

        sellCart.clear();
        refreshTableFromActiveCart();
        refreshAllPanels();
    }

    private int computeActiveTotal() {
        int sum = 0;
        for (var e : activeCart().entrySet()) {
            sum += unitPrice(e.getKey()) * e.getValue().get();
        }
        return sum;
    }

    private void refreshTotalLabel() {
        int total = computeActiveTotal();
        lbltotal.setText("Total: " + total + "g");
    }
    
    @FXML
    private void handlebackaction(ActionEvent event) {
        String back = gameState.getReturnFxml();
        util.SpawnTag spawn = gameState.getReturnSpawn();
        if (back == null) back = "/view/home_map.fxml"; // fallback
        util.SceneManager.switchScene(back, spawn);
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

        buypane.setDisable(!buyMode);
        sellpane.setDisable(buyMode);

        buybtn.setDisable(!buyMode);
        sellbtn.setDisable(buyMode);

        refreshTableFromActiveCart();
        refreshAllPanels();
    }
    
    private void addToCart(ItemType t, int delta) {
        Map<ItemType, IntegerProperty> cart = activeCart();

        if (mode == Mode.SELL) {
            int have = inv.getCount(t); // available
            int current = cart.containsKey(t) ? cart.get(t).get() : 0;
            int next = Math.min(have, current + delta);
            if (next <= 0) cart.remove(t);
            else cart.computeIfAbsent(t, k -> new SimpleIntegerProperty(0)).set(next);
        } else {
            int current = cart.containsKey(t) ? cart.get(t).get() : 0;
            int next = current + delta;
            if (next <= 0) cart.remove(t);
            else cart.computeIfAbsent(t, k -> new SimpleIntegerProperty(0)).set(next);
        }

        refreshTableFromActiveCart();
        refreshAllPanels();
    }
    
    private void bindCoins() {
        lblcoin.textProperty().unbind();
        lblcoin.textProperty().bind(inv.coinsProperty().asString("Coins: %d"));
    }
    
    private void collectButtonsOnce() {
        if (!invBtns.isEmpty()) return;

        invBtns.addAll(collectByPrefix(root, Button.class, "invbtn"));
        buyBtns.addAll(collectByPrefix(buypane, Button.class, "btnbuy"));
        sellBtns.addAll(collectByPrefix(sellpane, Button.class, "btnsell"));
        
        

        invBtns.sort(Comparator.comparing(Node::getId));
        buyBtns.sort(Comparator.comparing(Node::getId));
        sellBtns.sort(Comparator.comparing(Node::getId));
        
        
}
    

    private <T extends Node> List<T> collectByPrefix(Parent start, Class<T> type, String prefix) {
        List<T> out = new ArrayList<>();
        walk(start, n -> {
            if (type.isInstance(n)) {
                String id = n.getId();
                if (id != null && id.startsWith(prefix)) out.add(type.cast(n));
            }
        });
        return out;
    }

    private void walk(Parent p, java.util.function.Consumer<Node> visitor) {
        for (Node n : p.getChildrenUnmodifiable()) {
            visitor.accept(n);
            if (n instanceof Parent child) walk(child, visitor);
        }
    }

    private void refreshAllPanels() {
        collectButtonsOnce();
        refreshInventoryButtons();
        refreshBuyButtons();
        refreshSellButtons();
        refreshTotalLabel();
    }
    
    private void refreshInventoryButtons() {
        // Create a stable list of what player owns right now
        List<ItemType> owned = new ArrayList<>(inv.itemsProperty().keySet());
        owned.sort(Comparator.comparing(Enum::name));

        for (int i = 0; i < invBtns.size(); i++) {
            Button b = invBtns.get(i);

            if (i >= owned.size()) {
                b.setText("");
                b.setDisable(true);
                b.setOnAction(null);
                continue;
            }

            ItemType t = owned.get(i);
            int count = inv.getCount(t);

            b.setDisable(false);
            b.setText(t.getDisplayName() + " x" + count);

            b.setOnAction(e -> {
                if (mode == Mode.SELL) {
                    addToCart(t, +1);     // adds to sell cart, capped by inv count
                }
            });
        }
    }
    
    private void refreshBuyButtons() {
        List<ItemType> merchantTypes = new ArrayList<>(buyPriceByType.keySet());
        merchantTypes.sort(Comparator.comparing(Enum::name));

        for (int i = 0; i < buyBtns.size(); i++) {
            Button b = buyBtns.get(i);

            if (i >= merchantTypes.size()) {
                b.setText("");
                b.setDisable(true);
                b.setOnAction(null);
                continue;
            }

            ItemType t = merchantTypes.get(i);
            int price = buyPriceByType.getOrDefault(t, 0);

            b.setDisable(false);
            b.setText(t.getDisplayName() + " (" + price + "g)");

            b.setOnAction(e -> {
                if (mode == Mode.BUY) {
                    addToCart(t, +1);
                }
            });
        }
    }
    
    private void refreshSellButtons() {
        List<ItemType> selected = new ArrayList<>(sellCart.keySet());
        selected.sort(Comparator.comparing(Enum::name));

        for (int i = 0; i < sellBtns.size(); i++) {
            Button b = sellBtns.get(i);

            if (i >= selected.size()) {
                b.setText("");
                b.setDisable(true);
                b.setOnAction(null);
                continue;
            }

            ItemType t = selected.get(i);
            int qty = sellCart.get(t).get();

            b.setDisable(false);
            b.setText(t.getDisplayName() + " x" + qty);

            // clicking a sell-pane item should show it in tableitemdetail:
            // (your table already reflects the active cart; so just select it)
            b.setOnAction(e -> {
                for (DetailRow r : detailRows) {
                    if (r.getType() == t) {
                        tableitemdetail.getSelectionModel().select(r);
                        break;
                    }
                }
            });
        }
    }
    
}
