package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Player data that must persist across map changes.
 * Keep this class free of JavaFX Nodes so it survives scene reloads.
 */
public final class PlayerState {

    private static final int DEFAULT_HP = 100;
    private static final int DEFAULT_STAMINA = 100;
    private static final int DEFAULT_AGI = 5;

    private final Inventory inventory;

    private boolean hotbarVisible = true;
    private int selectedHotbarIndex = 0;

    private final IntegerProperty maxHp = new SimpleIntegerProperty(DEFAULT_HP);
    private final IntegerProperty hp = new SimpleIntegerProperty(DEFAULT_HP);
    private final IntegerProperty maxStamina = new SimpleIntegerProperty(DEFAULT_STAMINA);
    private final IntegerProperty stamina = new SimpleIntegerProperty(DEFAULT_STAMINA);
    private final IntegerProperty agi = new SimpleIntegerProperty(DEFAULT_AGI);

    private static final int HOTBAR_SIZE = 5;
    private final int[] hotbarItemIds = new int[HOTBAR_SIZE];

    public int getHotbarSize() {
        return HOTBAR_SIZE;
    }

    public int getHotbarItemId(int index) {
        if (index < 0 || index >= HOTBAR_SIZE) return -1;
        return hotbarItemIds[index];
    }

    public void setHotbarItemId(int index, int itemId) {
        if (index < 0 || index >= HOTBAR_SIZE) return;
        hotbarItemIds[index] = itemId;
    }

    public void clearHotbarItem(int index) {
        if (index < 0 || index >= HOTBAR_SIZE) return;
        hotbarItemIds[index] = 0;
    }
    
    public PlayerState(int startingCoins) {
        this.inventory = new Inventory(startingCoins);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public boolean isHotbarVisible() {
        return hotbarVisible;
    }

    public void setHotbarVisible(boolean hotbarVisible) {
        this.hotbarVisible = hotbarVisible;
    }

    public int getselectedHotbarIndex() {
        return selectedHotbarIndex;
    }

    public void setSelectedHotbarIndex(int selectedHotbarIndex) {
        this.selectedHotbarIndex = selectedHotbarIndex;
    }

    public IntegerProperty hpProperty() {
        return hp;
    }

    public IntegerProperty maxHpProperty() {
        return maxHp;
    }

    public int getHp() {
        return hp.get();
    }

    public void setHp(int value) {
        hp.set(clamp(value, 0, getMaxHp()));
    }

    public IntegerProperty staminaProperty() {
        return stamina;
    }

    public IntegerProperty maxStaminaProperty() {
        return maxStamina;
    }

    public int getStamina() {
        return stamina.get();
    }

    public void setStamina(int value) {
        stamina.set(clamp(value, 0, getMaxStamina()));
    }

    public IntegerProperty agiProperty() {
        return agi;
    }

    public int getAgi() {
        return agi.get();
    }

    public void setAgi(int value) {
        agi.set(Math.max(0, value));
    }

    public int getMaxHp() {
        return maxHp.get();
    }

    public int getMaxStamina() {
        return maxStamina.get();
    }

    public boolean isDead() {
        return getHp() <= 0;
    }

    public void damageHp(int amount) {
        if (amount <= 0) return;
        setHp(getHp() - amount);
    }

    public void consumeStamina(int amount) {
        if (amount <= 0) return;
        setStamina(getStamina() - amount);
    }

    /**
     * Consumes stamina first. If stamina is exhausted, the remaining cost is paid with HP.
     */
    public void consumeEnergyOrHp(int amount) {
        if (amount <= 0 || isDead()) return;

        int currentStamina = getStamina();
        if (currentStamina >= amount) {
            setStamina(currentStamina - amount);
            return;
        }

        setStamina(0);
        int remainder = amount - currentStamina;
        if (remainder > 0) {
            damageHp(remainder);
        }
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
