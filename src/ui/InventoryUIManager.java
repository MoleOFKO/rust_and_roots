/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.Inventory;

/**
 *
 * @author ASUS
 */
public class InventoryUIManager {
    private final TableView<InventoryRow> table;
    private final TableColumn<InventoryRow, String> colItem;
    private final TableColumn<InventoryRow, Number> colQty;
    private final Label coinsLabel;
    private final ObservableList<InventoryRow> rows =
            FXCollections.observableArrayList();

    public InventoryUIManager(
        TableView<InventoryRow> table,
        TableColumn<InventoryRow, String> colItem,
        TableColumn<InventoryRow, Number> colQty,
        Label coinsLabel,
        Inventory inventory
    ) {
        this.table = table;
        this.colItem = colItem;
        this.colQty = colQty;
        this.coinsLabel = coinsLabel;

        setupTable();
        bindInventory(inventory);
    }

    private void setupTable() {
        table.setItems(rows);

        colItem.setCellValueFactory(data ->
            new SimpleStringProperty(
                data.getValue().getType().getDisplayName()
            )
        );

        colQty.setCellValueFactory(data ->
            data.getValue().quantityProperty()
        );
    }

    private void bindInventory(Inventory inv) {
        coinsLabel.textProperty()
                .bind(inv.coinsProperty().asString("Coins: %dg"));
    }
}
