/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import javafx.beans.property.IntegerProperty;

/**
 *
 * @author ASUS
 */
public class InventoryRow {

    private final int itemId;
    private final IntegerProperty quantity;

    public InventoryRow(int itemId, IntegerProperty quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public int getItemId() {
        return itemId;
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public int getQuantity() {
        return quantity.get();
    }
    
}
