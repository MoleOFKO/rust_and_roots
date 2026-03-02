package model;

/**
 * Player data that must persist across map changes.
 * Keep this class free of JavaFX Nodes so it survives scene reloads.
 */
public final class PlayerState {

    private final Inventory inventory;

    public PlayerState(int startingCoins) {
        this.inventory = new Inventory(startingCoins);
    }

    public Inventory getInventory() {
        return inventory;
    }

    // Add more persistent player data here later (health, stamina, hotbar selection, etc.)
}
