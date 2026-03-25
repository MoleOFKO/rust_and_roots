package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


/**
 * Player data that must persist across map changes.
 * Keep this class free of JavaFX Nodes so it survives scene reloads.
 */
public final class PlayerState {

    private static final int DEFAULT_HP = 45;
    private static final int DEFAULT_STAMINA = 50;
    private static final int DEFAULT_AGI = 5;
    private static final int DEFAULT_DEX = 5;
    private static final int DEFAULT_STR = 5;
    private static final int DEFAULT_VIT = 5;
    private static final String DEFAULT_NAME = "Player";

    private final Inventory inventory;

    private boolean hotbarVisible = true;
    private int selectedHotbarIndex = 0;

    private final IntegerProperty maxHp = new SimpleIntegerProperty(DEFAULT_HP);
    private final IntegerProperty hp = new SimpleIntegerProperty(DEFAULT_HP);
    private final IntegerProperty maxStamina = new SimpleIntegerProperty(DEFAULT_STAMINA);
    private final IntegerProperty stamina = new SimpleIntegerProperty(DEFAULT_STAMINA);
    private final IntegerProperty agi = new SimpleIntegerProperty(DEFAULT_AGI);
    private final IntegerProperty dex = new SimpleIntegerProperty(DEFAULT_DEX);
    private final IntegerProperty str = new SimpleIntegerProperty(DEFAULT_STR);
    private final IntegerProperty vit = new SimpleIntegerProperty(DEFAULT_VIT);
    private final IntegerProperty exp = new SimpleIntegerProperty(0);
    private final StringProperty playerName = new SimpleStringProperty(DEFAULT_NAME);

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

    public IntegerProperty dexProperty() {
        return dex;
    }

    public int getDex() {
        return dex.get();
    }

    public void setDex(int value) {
        dex.set(Math.max(0, value));
    }

    public int getMaxHp() {
        return maxHp.get();
    }

    public int getMaxStamina() {
        return maxStamina.get();
    }

    public void setMaxHp(int value) {
        maxHp.set(Math.max(1, value));
        if (getHp() > getMaxHp()) {
            setHp(getMaxHp());
        }
    }

    public void setMaxStamina(int value) {
        maxStamina.set(Math.max(1, value));
        if (getStamina() > getMaxStamina()) {
            setStamina(getMaxStamina());
        }
    }

    public IntegerProperty strProperty() {
        return str;
    }

    public int getStr() {
        return str.get();
    }

    public void setStr(int value) {
        str.set(Math.max(0, value));
    }

    public IntegerProperty vitProperty() {
        return vit;
    }

    public int getVit() {
        return vit.get();
    }

    public void setVit(int value) {
        vit.set(Math.max(0, value));
    }

    public IntegerProperty expProperty() {
        return exp;
    }

    public int getExp() {
        return exp.get();
    }

    public void setExp(int value) {
        exp.set(Math.max(0, value));
    }

    public StringProperty playerNameProperty() {
        return playerName;
    }

    public String getPlayerName() {
        return playerName.get();
    }

    public void setPlayerName(String value) {
        if (value == null || value.isBlank()) {
            playerName.set(DEFAULT_NAME);
        } else {
            playerName.set(value);
        }
    }

    public boolean isDead() {
        return getHp() <= 0;
    }

    public void damageHp(int amount) {
        if (amount <= 0) return;
        setHp(getHp() - amount);
    }

    public void healHp(int amount) {
        if (amount <= 0 || isDead()) return;
        setHp(getHp() + amount);
    }

    public void consumeStamina(int amount) {
        if (amount <= 0) return;
        setStamina(getStamina() - amount);
    }

    public void restoreStamina(int amount) {
        if (amount <= 0 || isDead()) return;
        setStamina(getStamina() + amount);
    }

    public void applyFoodEffect(int hpDelta, int staminaDelta) {
        if (hpDelta > 0) {
            healHp(hpDelta);
        } else if (hpDelta < 0) {
            damageHp(-hpDelta);
        }

        if (staminaDelta > 0) {
            restoreStamina(staminaDelta);
        } else if (staminaDelta < 0) {
            consumeStamina(-staminaDelta);
        }
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
