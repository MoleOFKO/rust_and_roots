package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.Arrays;


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
    private boolean runToggled = false;
    private boolean staminaExhaustedLock = false;

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

    public enum StaminaMode {
        SLEEP,
        IDLE,
        WALK,
        RUN
    }

    private static final double DEFAULT_STAMINA_BASE_REGEN_PER_SECOND = 0.65;
    private static final double STAMINA_REGEN_PER_VIT = 0.07;
    private static final double WALK_REGEN_MULTIPLIER = 0.55;
    private static final double RUN_REGEN_MULTIPLIER = 0.20;
    private static final double RUN_BASE_DRAIN_PER_SECOND = 1.35;
    private static final double RUN_DRAIN_SCALING_START = 0.75;
    private static final double RUN_DRAIN_REDUCTION_PER_VIT = 0.05;

    private static final double IDLE_STAMINA_CAP_RATIO = 0.90;
    private static final double WALK_STAMINA_CAP_RATIO = 0.80;
    private static final double RUN_STAMINA_CAP_RATIO = 0.70;
    private static final double SLEEP_STAMINA_CAP_RATIO = 1.00;

    private double staminaPrecise = DEFAULT_STAMINA;
    private StaminaMode staminaMode = StaminaMode.IDLE;
    private boolean sleeping = false;

    // ---- Base regen (world timeline) ----
    // 1 in-game minute (GameTimeManager.totalSeconds is in-game seconds).
    private static final double BASE_REGEN_INTERVAL_SECONDS = 60.0;
    // Base regen amount: 1% of max HP/SP per interval.
    private static final double BASE_REGEN_PERCENT = 0.01;
    // VIT modifiers (kept intentionally mild).
    private static final double REGEN_AMOUNT_MULTIPLIER_PER_VIT = 0.05;
    private static final double REGEN_SPEED_MULTIPLIER_PER_VIT = 0.02;

    private double lastBaseRegenTotalSeconds = -1.0;
    private double baseHpRegenCarry = 0.0;
    private double baseStaminaRegenCarry = 0.0;

    private static final int HOTBAR_SIZE = 5;
    private static final int INVENTORY_SLOT_CAPACITY = 20;
    private final int[] hotbarItemIds = new int[HOTBAR_SIZE];
    private final int[] hotbarItemCounts = new int[HOTBAR_SIZE];

    public int getHotbarSize() {
        return HOTBAR_SIZE;
    }

    public int getHotbarItemId(int index) {
        if (index < 0 || index >= HOTBAR_SIZE) return -1;
        return hotbarItemIds[index];
    }

    public int getHotbarItemCount(int index) {
        if (index < 0 || index >= HOTBAR_SIZE) return 0;
        return hotbarItemCounts[index];
    }

    public boolean isHotbarSlotEmpty(int index) {
        return getHotbarItemId(index) <= 0 || getHotbarItemCount(index) <= 0;
    }

    public void setHotbarItemId(int index, int itemId) {
        if (index < 0 || index >= HOTBAR_SIZE) return;

        int normalized = ItemDatabase.normalizeGameplayItemId(itemId);

        if (normalized <= 0) {
            clearHotbarItem(index);
            return;
        }

        hotbarItemIds[index] = normalized;
        if (hotbarItemCounts[index] <= 0) {
            hotbarItemCounts[index] = 1;
        }
    }

    public void setHotbarSlot(int index, int itemId, int count) {
        if (index < 0 || index >= HOTBAR_SIZE) return;

        int normalized = ItemDatabase.normalizeGameplayItemId(itemId);

        if (normalized <= 0 || count <= 0) {
            clearHotbarItem(index);
            return;
        }

        hotbarItemIds[index] = normalized;
        hotbarItemCounts[index] = clampToStackLimit(normalized, count);
    }

    public boolean addHotbarItems(int index, int itemId, int amount) {
        if (index < 0 || index >= HOTBAR_SIZE) return false;
        if (itemId <= 0 || amount <= 0) return false;

        int normalized = ItemDatabase.normalizeGameplayItemId(itemId);
        if (normalized <= 0) return false;

        if (isHotbarSlotEmpty(index)) {
            int maxStack = getMaxStackSize(normalized);
            if (amount > maxStack) return false;
            setHotbarSlot(index, normalized, amount);
            return amount <= maxStack;
        }

        if (hotbarItemIds[index] != normalized) {
            return false;
        }

        int maxStack = getMaxStackSize(normalized);
        int next = hotbarItemCounts[index] + amount;
        if (next > maxStack) {
            return false;
        }

        hotbarItemCounts[index] = next;
        return hotbarItemCounts[index] > 0;
    }

    public boolean removeHotbarItems(int index, int amount) {
        if (index < 0 || index >= HOTBAR_SIZE) return false;
        if (amount <= 0) return false;
        if (hotbarItemCounts[index] < amount) return false;

        hotbarItemCounts[index] -= amount;
        if (hotbarItemCounts[index] <= 0) {
            clearHotbarItem(index);
        }
        return true;
    }

    public boolean canFitHotbarItems(int itemId, int amount) {
        if (itemId <= 0 || amount <= 0) return false;

        int normalized = ItemDatabase.normalizeGameplayItemId(itemId);
        if (normalized <= 0) return false;

        int remaining = amount;
        int maxStack = getMaxStackSize(normalized);

        for (int i = 0; i < HOTBAR_SIZE && remaining > 0; i++) {
            if (hotbarItemIds[i] == normalized && hotbarItemCounts[i] > 0 && hotbarItemCounts[i] < maxStack) {
                remaining -= Math.min(maxStack - hotbarItemCounts[i], remaining);
            }
        }

        if (remaining <= 0) return true;

        for (int i = 0; i < HOTBAR_SIZE && remaining > 0; i++) {
            if (isHotbarSlotEmpty(i)) {
                remaining -= Math.min(maxStack, remaining);
            }
        }

        return remaining <= 0;
    }

    /**
     * Adds items to the hotbar by filling existing partial stacks first and then
     * using empty slots. This is an all-or-nothing operation.
     */
    public boolean tryAddHotbarItems(int itemId, int amount) {
        if (itemId <= 0 || amount <= 0) return false;

        int normalized = ItemDatabase.normalizeGameplayItemId(itemId);
        if (normalized <= 0) return false;
        if (!canFitHotbarItems(normalized, amount)) return false;

        int remaining = amount;
        int maxStack = getMaxStackSize(normalized);

        // Fill existing partial stacks of the same item.
        for (int i = 0; i < HOTBAR_SIZE && remaining > 0; i++) {
            if (hotbarItemIds[i] != normalized || hotbarItemCounts[i] <= 0) continue;
            int current = hotbarItemCounts[i];
            if (current >= maxStack) continue;

            int add = Math.min(maxStack - current, remaining);
            hotbarItemCounts[i] = current + add;
            remaining -= add;
        }

        // Use empty slots for remaining items.
        for (int i = 0; i < HOTBAR_SIZE && remaining > 0; i++) {
            if (!isHotbarSlotEmpty(i)) continue;
            int put = Math.min(maxStack, remaining);
            hotbarItemIds[i] = normalized;
            hotbarItemCounts[i] = put;
            remaining -= put;
        }

        return remaining <= 0;
    }

    public int findHotbarSlotWithItem(int itemId) {
        if (itemId <= 0) return -1;
        int normalized = ItemDatabase.normalizeGameplayItemId(itemId);
        if (normalized <= 0) return -1;
        for (int i = 0; i < HOTBAR_SIZE; i++) {
            if (hotbarItemIds[i] == normalized && hotbarItemCounts[i] > 0) {
                return i;
            }
        }
        return -1;
    }

    public int findFirstEmptyHotbarSlot() {
        for (int i = 0; i < HOTBAR_SIZE; i++) {
            if (isHotbarSlotEmpty(i)) {
                return i;
            }
        }
        return -1;
    }

    public void clearAllHotbarItems() {
        Arrays.fill(hotbarItemIds, 0);
        Arrays.fill(hotbarItemCounts, 0);
    }

    public void clearHotbarItem(int index) {
        if (index < 0 || index >= HOTBAR_SIZE) return;
        hotbarItemIds[index] = 0;
        hotbarItemCounts[index] = 0;
    }

    private int clampToStackLimit(int itemId, int count) {
        if (count <= 0) return 0;
        int normalized = ItemDatabase.normalizeGameplayItemId(itemId);
        int maxStack = getMaxStackSize(normalized);
        return Math.min(Math.max(1, count), maxStack);
    }

    private int getMaxStackSize(int itemId) {
        int normalized = ItemDatabase.normalizeGameplayItemId(itemId);
        int max = ItemDatabase.getMaxStackSize(normalized);
        return Math.max(1, max);
    }
    
    public PlayerState(int startingCoins) {
        this.inventory = new Inventory(startingCoins, INVENTORY_SLOT_CAPACITY);
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

    public boolean isRunToggled() {
        return runToggled;
    }

    public void setRunToggled(boolean runToggled) {
        this.runToggled = runToggled;
    }

    public boolean hasStaminaExhaustedLock() {
        return staminaExhaustedLock;
    }

    public void setStaminaExhaustedLock(boolean staminaExhaustedLock) {
        this.staminaExhaustedLock = staminaExhaustedLock;
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
        int clamped = clamp(value, 0, getMaxStamina());
        staminaPrecise = clamped;
        stamina.set(clamped);
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
        if (staminaPrecise > getMaxStamina()) {
            staminaPrecise = getMaxStamina();
        }
        setStamina((int) Math.floor(Math.max(0.0, staminaPrecise)));
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

    public void addExp(int delta) {
        if (delta == 0) return;
        setExp(getExp() + delta);
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

    public double getBaseStaminaRegenPerSecond() {
        return DEFAULT_STAMINA_BASE_REGEN_PER_SECOND + (Math.max(0, getVit()) * STAMINA_REGEN_PER_VIT);
    }

    public double getModeRegenMultiplier(StaminaMode mode) {
        if (mode == null) return 1.0;
        return switch (mode) {
            case SLEEP, IDLE -> 1.0;
            case WALK -> WALK_REGEN_MULTIPLIER;
            case RUN -> RUN_REGEN_MULTIPLIER;
        };
    }

    public boolean doesModeDrainStamina(StaminaMode mode) {
        return mode == StaminaMode.RUN;
    }

    public double getStaminaCapRatio(StaminaMode mode) {
        if (mode == null) return IDLE_STAMINA_CAP_RATIO;
        return switch (mode) {
            case SLEEP -> SLEEP_STAMINA_CAP_RATIO;
            case IDLE -> IDLE_STAMINA_CAP_RATIO;
            case WALK -> WALK_STAMINA_CAP_RATIO;
            case RUN -> RUN_STAMINA_CAP_RATIO;
        };
    }

    public int getModeStaminaCap(StaminaMode mode) {
        int cap = (int) Math.floor(getMaxStamina() * getStaminaCapRatio(mode));
        if (mode == StaminaMode.SLEEP) {
            return getMaxStamina();
        }
        return Math.max(0, Math.min(getMaxStamina(), cap));
    }

    public double getRunDrainPerSecond() {
        double reduction = Math.max(0.0, RUN_DRAIN_SCALING_START - (Math.max(0, getVit()) * RUN_DRAIN_REDUCTION_PER_VIT));
        return RUN_BASE_DRAIN_PER_SECOND + reduction;
    }

    public void setStaminaMode(StaminaMode mode) {
        staminaMode = (mode == null) ? StaminaMode.IDLE : mode;
    }

    public StaminaMode getStaminaMode() {
        return staminaMode;
    }

    public boolean canSleep() {
        return !isDead() && (getStamina() < getMaxStamina() || getHp() < getMaxHp());
    }

    public boolean isSleeping() {
        return sleeping;
    }

    public void startSleeping() {
        sleeping = true;
        setStaminaMode(StaminaMode.SLEEP);
    }

    public void stopSleeping() {
        sleeping = false;
        if (staminaMode == StaminaMode.SLEEP) {
            setStaminaMode(StaminaMode.IDLE);
        }
    }

    public void updateStaminaOverTime(double seconds, StaminaMode mode) {
        updateStaminaOverTime(seconds, mode, 1.0);
    }

    public void updateStaminaOverTime(double seconds, StaminaMode mode, double timeFlowMultiplier) {
        if (seconds <= 0.0 || isDead() || getMaxStamina() <= 0) return;

        StaminaMode effectiveMode = (mode == null) ? StaminaMode.IDLE : mode;
        double safeTimeFlow = Math.max(0.0, timeFlowMultiplier);

        // Regen is handled by the world-time based base regen system (see applyBaseRegenIfNeeded).
        double regen = 0.0;
        double drain = doesModeDrainStamina(effectiveMode)
            ? getRunDrainPerSecond() * safeTimeFlow * seconds
            : 0.0;

        double modeRegenLimit = getModeStaminaCap(effectiveMode);

        // Drain is always allowed.
        if (drain > 0.0) {
            staminaPrecise = Math.max(0.0, staminaPrecise - drain);
        }

        // Regen limit is not the same thing as the true stamina cap.
        // If stamina is already above the current state's regen limit,
        // keep it there and just stop regenerating higher in that state.
        if (regen > 0.0 && staminaPrecise < modeRegenLimit) {
            staminaPrecise = Math.min(modeRegenLimit, staminaPrecise + regen);
        }

        if (staminaPrecise > getMaxStamina()) {
            staminaPrecise = getMaxStamina();
        }

        stamina.set(clamp((int) Math.floor(staminaPrecise + 1.0e-9), 0, getMaxStamina()));
    }

    /**
     * Applies passive HP/SP regeneration based on the world timeline (GameTimeManager.totalSeconds).
     *
     * Base:
     * - Regen speed: 1 in-game minute (60 in-game seconds)
     * - Regen amount: 1% of max HP/SP per tick
     *
     * VIT modifiers:
     * - Increases regen amount and increases regen speed slightly.
     */
    public void applyBaseRegenIfNeeded(double nowTotalSeconds) {
        double nowSeconds = Math.max(0.0, nowTotalSeconds);

        if (isDead()) {
            lastBaseRegenTotalSeconds = nowSeconds;
            baseHpRegenCarry = 0.0;
            baseStaminaRegenCarry = 0.0;
            return;
        }

        if (lastBaseRegenTotalSeconds < 0.0 || lastBaseRegenTotalSeconds > nowSeconds) {
            lastBaseRegenTotalSeconds = nowSeconds;
            baseHpRegenCarry = 0.0;
            baseStaminaRegenCarry = 0.0;
            return;
        }

        int vitValue = Math.max(0, getVit());
        int vitBonus = Math.max(0, vitValue - 1);

        double amountMultiplier = 1.0 + (vitBonus * REGEN_AMOUNT_MULTIPLIER_PER_VIT);
        double speedMultiplier = 1.0 + (vitBonus * REGEN_SPEED_MULTIPLIER_PER_VIT);
        double intervalSeconds = BASE_REGEN_INTERVAL_SECONDS / Math.max(1.0e-6, speedMultiplier);

        double elapsed = nowSeconds - lastBaseRegenTotalSeconds;
        if (elapsed < intervalSeconds) {
            return;
        }

        long ticks = (long) Math.floor(elapsed / intervalSeconds);
        if (ticks <= 0L) {
            return;
        }

        int maxHpValue = Math.max(0, getMaxHp());
        int maxSpValue = Math.max(0, getMaxStamina());

        int currentHp = Math.max(0, getHp());
        int currentSp = Math.max(0, getStamina());

        if (maxHpValue > 0 && currentHp < maxHpValue) {
            baseHpRegenCarry += (maxHpValue * BASE_REGEN_PERCENT * amountMultiplier) * ticks;
            while (baseHpRegenCarry >= 1.0 && currentHp < maxHpValue) {
                currentHp += 1;
                baseHpRegenCarry -= 1.0;
            }
            setHp(currentHp);
        } else {
            baseHpRegenCarry = 0.0;
        }

        if (maxSpValue > 0 && currentSp < maxSpValue) {
            baseStaminaRegenCarry += (maxSpValue * BASE_REGEN_PERCENT * amountMultiplier) * ticks;
            while (baseStaminaRegenCarry >= 1.0 && currentSp < maxSpValue) {
                currentSp += 1;
                baseStaminaRegenCarry -= 1.0;
            }
            setStamina(currentSp);
        } else {
            baseStaminaRegenCarry = 0.0;
        }

        lastBaseRegenTotalSeconds += ticks * intervalSeconds;
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
