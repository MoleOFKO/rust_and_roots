/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import static controller.Edit_itemController.itemList;
import static controller.Edit_itemController.item_id;
import db.dbcon;
import game.GameState;
import game.SceneController;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import model.Inventory;
import model.ItemDatabase;
import model.item;
import java.util.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import model.ItemDatabase;
import java.util.Objects;

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
    

    private final Map<Integer, IntegerProperty> buyCart = new LinkedHashMap<>();
    private final Map<Integer, IntegerProperty> sellCart = new LinkedHashMap<>();

    private final List<item> shopItems = new ArrayList<>();
    private final Map<Integer, item> dbItemsById = new LinkedHashMap<>();

    // rows shown in tableitemdetail
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
    private Label buyname1;
    @FXML
    private Label buyname2;
    @FXML
    private Label buyname3;
    @FXML
    private Label buyname4;
    @FXML
    private Label buyname5;
    @FXML
    private Label buyname6;
    @FXML
    private Label buyname7;
    @FXML
    private Label buyname8;
    @FXML
    private Label buyname9;
    @FXML
    private Label buyname10;
    @FXML
    private Label buyname11;
    @FXML
    private Label buyname12;
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
    
    
    

    

    Connection con;
    ResultSet rs;
    PreparedStatement pst;
    Statement st;
    private List<ImageView> buyImages = new ArrayList<>();
    private List<Label> buyLabels = new ArrayList<>();
    private List<Integer> buyStock = new ArrayList<>();
    private List<Label> buyNameLabels = new ArrayList<>();
    private final List<Button> buyItemButtons = new ArrayList<>();
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ItemDatabase.loadItems();
        
        buyImages.add(imgbuy1);
        buyImages.add(imgbuy2);
        buyImages.add(imgbuy3);
        buyImages.add(imgbuy4);
        buyImages.add(imgbuy5);
        buyImages.add(imgbuy6);
        buyImages.add(imgbuy7);
        buyImages.add(imgbuy8);
        buyImages.add(imgbuy9);
        buyImages.add(imgbuy10);
        buyImages.add(imgbuy11);
        buyImages.add(imgbuy12);
        
        buyLabels.add(lblbuy1);
        buyLabels.add(lblbuy2);
        buyLabels.add(lblbuy3);
        buyLabels.add(lblbuy4);
        buyLabels.add(lblbuy5);
        buyLabels.add(lblbuy6);
        buyLabels.add(lblbuy7);
        buyLabels.add(lblbuy8);
        buyLabels.add(lblbuy9);
        buyLabels.add(lblbuy10);
        buyLabels.add(lblbuy11);
        buyLabels.add(lblbuy12);
        
        buyNameLabels.add(buyname1);
        buyNameLabels.add(buyname2);
        buyNameLabels.add(buyname3);
        buyNameLabels.add(buyname4);
        buyNameLabels.add(buyname5);
        buyNameLabels.add(buyname6);
        buyNameLabels.add(buyname7);
        buyNameLabels.add(buyname8);
        buyNameLabels.add(buyname9);
        buyNameLabels.add(buyname10);
        buyNameLabels.add(buyname11);
        buyNameLabels.add(buyname12);
        
        buyItemButtons.add(btnbuy1);
        buyItemButtons.add(btnbuy2);
        buyItemButtons.add(btnbuy3);
        buyItemButtons.add(btnbuy4);
        buyItemButtons.add(btnbuy5);
        buyItemButtons.add(btnbuy6);
        buyItemButtons.add(btnbuy7);
        buyItemButtons.add(btnbuy8);
        buyItemButtons.add(btnbuy9);
        buyItemButtons.add(btnbuy10);
        buyItemButtons.add(btnbuy11);
        buyItemButtons.add(btnbuy12);

        System.out.println("root = " + root);
        System.out.println("buypane = " + buypane);
        System.out.println("sellpane = " + sellpane);

        for (int i = 0; i < buyNameLabels.size(); i++) {
            System.out.println("buyNameLabels[" + i + "] = " + buyNameLabels.get(i));
        }
        loadItemImages();
        
        for(int i = 0; i < 12; i++){
            buyStock.add(10); // default quantity
        }
        refreshBuyStock();
        
        for (int i = 0; i < buyItemButtons.size(); i++) {
            final int index = i;
            buyItemButtons.get(i).setOnAction(e -> handleBuyClick(index));
        }
        
        
        if (gameState == null) gameState = GameState.get();
        if (inv == null) inv = gameState.getPlayerState().getInventory();

        setupMerchantCatalog();
        setupTable();
        bindCoins();
        setMode(Mode.BUY);     // default
        refreshTableFromBuyCart();
        refreshAllPanels();
        
    }    

    private void setupMerchantCatalog() {
//        buyPriceByType.clear();
        // no-op now
    // items come directly from database / ItemDatabase
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
            int newQty = (e.getNewValue() == null) ? row.getQty() : e.getNewValue();

            if (mode == Mode.BUY) {
                if (newQty <= 0) {
                    buyCart.remove(itemId);
                } else {
                    buyCart.computeIfAbsent(itemId, k -> new SimpleIntegerProperty(0)).set(newQty);
                }
                refreshTableFromBuyCart();
            } else {
                int have = inv.getCount(itemId);
                int clamped = Math.max(0, Math.min(newQty, have));

                if (clamped <= 0) {
                    sellCart.remove(itemId);
                } else {
                    sellCart.computeIfAbsent(itemId, k -> new SimpleIntegerProperty(0)).set(clamped);
                }
                refreshTableFromSellCart();
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
            inv.addItem(e.getKey(), e.getValue().get());
        }

        buyCart.clear();
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
            inv.removeItem(e.getKey(), e.getValue().get());
        }

        inv.addCoins(totalGain);
        sellCart.clear();
        refreshTableFromSellCart();
        refreshAllPanels();
    }

    private int sellPrice(int itemId) {
        item itm = ItemDatabase.getItemById(itemId);
        if (itm == null) return 1;
        return Math.max(1, itm.getPrice() / 2);
    }

    private void refreshTotalLabel() {
        int total = (mode == Mode.BUY) ? computeBuyTotal() : computeSellTotal();
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

        if (buyMode) {
            refreshTableFromBuyCart();
        } else {
            refreshTableFromSellCart();
        }

        refreshAllPanels();
    }
    
    private void addToCart(int itemId, int delta) {
        if (mode != Mode.SELL) return;

        int have = inv.getCount(itemId);
        int current = sellCart.containsKey(itemId) ? sellCart.get(itemId).get() : 0;
        int next = Math.min(have, current + delta);

        if (next <= 0) {
            sellCart.remove(itemId);
        } else {
            sellCart.computeIfAbsent(itemId, k -> new SimpleIntegerProperty(0)).set(next);
        }

        refreshTableFromSellCart();
        refreshAllPanels();
    }
    
    private void bindCoins() {
        lblcoin.textProperty().unbind();
        lblcoin.textProperty().bind(inv.coinsProperty().asString("Coins: %d"));
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
        List<Integer> owned = new ArrayList<>(inv.itemsProperty().keySet());
        owned.sort(Comparator.comparing(id -> {
            item itm = ItemDatabase.getItemById(id);
            return itm == null ? "" : itm.getItem_name();
        }));

        for (int i = 0; i < invBtns.size(); i++) {
            Button b = invBtns.get(i);

            if (i >= owned.size()) {
                b.setText("");
                b.setDisable(true);
                b.setOnAction(null);
                continue;
            }

            int itemId = owned.get(i);
            int count = inv.getCount(itemId);
            item itm = ItemDatabase.getItemById(itemId);
            String name = (itm == null) ? ("Item " + itemId) : itm.getItem_name();

            b.setDisable(false);
            b.setText(name + " x" + count);

            b.setOnAction(e -> {
                if (mode == Mode.SELL) {
                    addToCart(itemId, +1);
                }
            });
        }
    }
    
    private void refreshBuyButtons() {
        for (int i = 0; i < buyBtns.size(); i++) {
            Button b = buyBtns.get(i);

            if (i >= shopItems.size()) {
                b.setText("");
                b.setDisable(true);
                b.setOnAction(null);
                continue;
            }

            item itm = shopItems.get(i);
            int price = itm.getPrice();

            b.setDisable(false);
            b.setText(itm.getItem_name() + " (" + price + "g)");

            final int index = i;
            b.setOnAction(e -> {
                if (mode == Mode.BUY) {
                    handleBuyClick(index);
                }
            });
        }
    }
    
    private void refreshSellButtons() {
        List<Integer> selected = new ArrayList<>(sellCart.keySet());
        selected.sort(Comparator.comparing(id -> {
            item itm = ItemDatabase.getItemById(id);
            return itm == null ? "" : itm.getItem_name();
        }));

        for (int i = 0; i < sellBtns.size(); i++) {
            Button b = sellBtns.get(i);

            if (i >= selected.size()) {
                b.setText("");
                b.setDisable(true);
                b.setOnAction(null);
                continue;
            }

            int itemId = selected.get(i);
            int qty = sellCart.get(itemId).get();
            item itm = ItemDatabase.getItemById(itemId);
            String name = (itm == null) ? ("Item " + itemId) : itm.getItem_name();

            b.setDisable(false);
            b.setText(name + " x" + qty);
            b.setOnAction(null);
        }
    }
    
    

    
    
    private void loadItemImages() {
        String sql = "SELECT item_id, item_name, price, image FROM items LIMIT 12";
        dbcon db = new dbcon();

        try (
            Connection con = db.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            int i = 0;
            shopItems.clear();
            dbItemsById.clear();

            while (rs.next() && i < 12) {
                int itemId = rs.getInt("item_id");
                String itemName = rs.getString("item_name");
                int price = rs.getInt("price");
                String imageName = rs.getString("image");

                item itm = new item(itemId, itemName, price);
                shopItems.add(itm);
                dbItemsById.put(itemId, itm);

                if (i < buyImages.size() && buyImages.get(i) != null) {
                    InputStream is = getClass().getResourceAsStream("/image/items/" + imageName);
                    if (is != null) {
                        buyImages.get(i).setImage(new Image(is));
                    } else {
                        System.out.println("Missing image file: " + imageName);
                    }
                }

                if (i < buyNameLabels.size() && buyNameLabels.get(i) != null) {
                    buyNameLabels.get(i).setText(itemName);
                } else {
                    System.out.println("Missing buyname label at index: " + i);
                }

                i++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void refreshBuyStock() {

        for(int i = 0; i < buyLabels.size(); i++){
            buyLabels.get(i).setText("x" + buyStock.get(i));
        }

    }
    
    private void handleBuyClick(int index) {
        if (index < 0 || index >= shopItems.size()) return;

        item selectedItem = shopItems.get(index);

        System.out.println("Clicked: " + selectedItem.getItem_name());

        addDbItemToCart(selectedItem, 1);
    }
    
    private void removeFromBuyCart(int itemId) {
        buyCart.remove(itemId);
    }

    private void addDbItemToCart(item selectedItem, int delta) {
        int itemId = selectedItem.getItem_id();
        int current = buyCart.containsKey(itemId) ? buyCart.get(itemId).get() : 0;
        int next = current + delta;

        if (next <= 0) {
            buyCart.remove(itemId);
        } else {
            buyCart.computeIfAbsent(itemId, k -> new SimpleIntegerProperty(0)).set(next);
        }

        refreshTableFromBuyCart();
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
    

//    private void loadShopItems() {
//        shopItems.clear();
//        shopItems.addAll(ItemDatabase.getFirstItems(12));
//
//        for (int i = 0; i < shopItems.size() && i < buyNameLabels.size(); i++) {
//            item itm = shopItems.get(i);
//
//            buyNameLabels.get(i).setText(itm.getItem_name());
//
//            if (i < buyImages.size()) {
//                buyImages.get(i).setImage(ItemDatabase.getImage(itm.getItem_id()));
//            }
//        }
//    }
}
