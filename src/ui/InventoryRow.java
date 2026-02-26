/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import javafx.beans.property.IntegerProperty;
import model.ItemType;

/**
 *
 * @author ASUS
 */
public class InventoryRow {
    
    private final ItemType type;
    private final IntegerProperty quantity;

    public InventoryRow(ItemType type, IntegerProperty quantity) {
        this.type = type;
        this.quantity = quantity;
    }

    public ItemType getType() {
        return type;
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }
    public int getQuantity() {
        return quantity.get();
    }
    
}
