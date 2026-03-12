package model;

/**
 * Player data that must persist across map changes.
 * Keep this class free of JavaFX Nodes so it survives scene reloads.
 */
public final class PlayerState {

    private final Inventory inventory;

    private boolean hotbarVisible = true;
    private int selectedHotbarIndex = 0;
    
    public PlayerState(int startingCoins) {
        this.inventory = new Inventory(startingCoins);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public boolean isHotbarVisible(){
        return hotbarVisible;
    }
    
    public void setHotbarVisible(boolean hotbarVisible){
        this.hotbarVisible = hotbarVisible;
    }
    
    public int  getselectedHotbarIndex(){
        return selectedHotbarIndex;
    }
    
    public void setSelectedHotbarIndex(int selectedHotbarIndex){
        this.selectedHotbarIndex = selectedHotbarIndex;
    }
    
}
