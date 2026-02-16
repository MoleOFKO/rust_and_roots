package model;

public enum ItemType {

    // Crops
    GRAPE("Grape"),
    CARROT("Carrot"),
    CORN("Corn"),
    TOMATO("Tomato"),

    
    // Seeds (example)
    PARSNIP_SEEDS("Parsnip Seeds"),
    POTATO_SEEDS("Potato Seeds"),

    // Tools/upgrades (example)
    WATERING_CAN_UPGRADE("Watering Can Upgrade");

    private final String displayName;

    ItemType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}