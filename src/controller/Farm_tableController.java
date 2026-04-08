package controller;

import game.GameState;
import game.GameTimeManager;
import game.SceneController;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import model.FarmGrowthRules;
import model.FarmState;
import model.Inventory;
import model.ItemDatabase;
import model.NodeCollectors;
import util.AppDialogs;
import util.SceneManager;
import util.SpawnTag;

public class Farm_tableController implements Initializable, SceneController {
    private static final String TOOL_SLOT_STYLE = "-fx-effect: dropshadow(gaussian, rgba(255,215,0,0.85), 14, 0.55, 0, 0);";

    private static final double FARM_TABLE_TIME_SCALE = 0.25;
    private static final int TOTAL_GROWTH_STAGES = FarmGrowthRules.TOTAL_GROWTH_STAGES;
    private static final int DEFAULT_PLANT_STAMINA_COST = 2;

    private static final String PLOT_IDLE_STYLE = "-fx-background-color: transparent; -fx-background-radius: 6; -fx-border-color: transparent; -fx-border-radius: 6;";
    private static final String PLOT_HIGHLIGHT_STYLE = "-fx-background-color: rgba(255,255,0,0.14); -fx-background-radius: 6; -fx-border-color: rgba(255,255,0,0.92); -fx-border-width: 2; -fx-border-radius: 6;";
    
    private GameState gameState;
    private ToolMode activeMode = ToolMode.NONE;
    private Integer selectedSeedItemId;
    private Integer equippedWateringCanItemId;
    private Integer equippedSickleItemId;
    private double previousTimeScale = 1.0;
    private boolean previousPaused = false;
    private boolean timeOverrideApplied = false;
    private String activeFarmMapKey = "home_map";
    private ChangeListener<Number> timeRefreshListener;
    private long lastRenderedMinuteTick = Long.MIN_VALUE;

    private Image defaultSeedsIcon;
    private Image defaultWaterCanIcon;
    private Image defaultSickleIcon;
    
    private final Map<String, ImageView> plotViews = new LinkedHashMap<>();
    private final Map<String, Region> plotTargets = new LinkedHashMap<>();
    private final Map<String, TempPlotData> tempPlots = new LinkedHashMap<>();
    private final Map<Integer, Integer> pendingHarvests = new LinkedHashMap<>();
    private final Map<Integer, Integer> pendingSeedReturns = new LinkedHashMap<>();
    private int pendingFarmingExp = 0;
    private final List<ImageView> seedSlotImages = new ArrayList<>();
    private final List<Label> seedSlotLabels = new ArrayList<>();
    private final Map<String, Integer> seedSlotMapping = new LinkedHashMap<>();

    @FXML private StackPane view;
    @FXML private Group world;
    @FXML private ImageView SeedsImg;
    @FXML private ImageView WaterCanImg;
    @FXML private ImageView SickleImg;
    @FXML private Group farm;
    @FXML private Pane seedList;
    @FXML private Pane confirmPane;
    @FXML private Button btnConfirm;
    @FXML private Button btnBack;

    @FXML private ImageView farm_img1; @FXML private ImageView farm_img2; @FXML private ImageView farm_img3; @FXML private ImageView farm_img4;
    @FXML private ImageView farm_img5; @FXML private ImageView farm_img6; @FXML private ImageView farm_img7; @FXML private ImageView farm_img8;
    @FXML private ImageView farm_img9; @FXML private ImageView farm_img10; @FXML private ImageView farm_img11; @FXML private ImageView farm_img12;
    @FXML private ImageView farm_img13; @FXML private ImageView farm_img14; @FXML private ImageView farm_img15; @FXML private ImageView farm_img16;
    @FXML private ImageView farm_img17; @FXML private ImageView farm_img18; @FXML private ImageView farm_img19; @FXML private ImageView farm_img20;
    @FXML private ImageView farm_img21; @FXML private ImageView farm_img22; @FXML private ImageView farm_img23; @FXML private ImageView farm_img24;
    @FXML private ImageView farm_img25; @FXML private ImageView farm_img26; @FXML private ImageView farm_img27; @FXML private ImageView farm_img28;

    @FXML private ImageView inyimg1; @FXML private ImageView inyimg2; @FXML private ImageView inyimg3; @FXML private ImageView inyimg4; @FXML private ImageView inyimg5;
    @FXML private ImageView inyimg6; @FXML private ImageView inyimg7; @FXML private ImageView inyimg8; @FXML private ImageView inyimg9; @FXML private ImageView inyimg10;
    @FXML private ImageView inyimg11; @FXML private ImageView inyimg12; @FXML private ImageView inyimg13; @FXML private ImageView inyimg14; @FXML private ImageView inyimg15;
    @FXML private ImageView inyimg16; @FXML private ImageView inyimg17; @FXML private ImageView inyimg18; @FXML private ImageView inyimg19; @FXML private ImageView inyimg20;

    @FXML private Label invlbl1; @FXML private Label invlbl2; @FXML private Label invlbl3; @FXML private Label invlbl4; @FXML private Label invlbl5;
    @FXML private Label invlbl6; @FXML private Label invlbl7; @FXML private Label invlbl8; @FXML private Label invlbl9; @FXML private Label invlbl10;
    @FXML private Label invlbl11; @FXML private Label invlbl12; @FXML private Label invlbl13; @FXML private Label invlbl14; @FXML private Label invlbl15;
    @FXML private Label invlbl16; @FXML private Label invlbl17; @FXML private Label invlbl18; @FXML private Label invlbl19; @FXML private Label invlbl20;
    @FXML
    private ImageView closeseedList;

    @Override
    public void init(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (gameState == null) gameState = GameState.get();
        ItemDatabase.loadItems();

        defaultSeedsIcon = SeedsImg == null ? null : SeedsImg.getImage();
        defaultWaterCanIcon = WaterCanImg == null ? null : WaterCanImg.getImage();
        defaultSickleIcon = SickleImg == null ? null : SickleImg.getImage();

        activeFarmMapKey = resolveActiveFarmMapKey();
        collectPlots();
        collectSeedSlots();
        loadTempFarmState();
        applyFarmTableTimeOverride();
        hideSeedList();
        populateSeedList();
        refreshSelectedToolPreview();
        refreshFarmVisualState();
        refreshToolHighlight();
        bindTimeDrivenPlotRefresh();

        if (view != null) {
            view.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene == null) {
                    unbindTimeDrivenPlotRefresh();
                    restoreTimeOverride();
                } else {
                    refreshFarmVisualState();
                }
            });
        }
    }

    @FXML
    private void handleFarmPlot(MouseEvent event) {
        Object source = event.getSource();
        if (source instanceof ImageView clickedView) {
            String id = clickedView.getId();
            if (id == null || !id.startsWith("farm_img")) return;
            int n = NodeCollectors.extractNumber(id, "farm_img");
            if (n < 0) return;
            handlePlotClick("Plot " + n);
            event.consume();
            return;
        }

        if (source instanceof Region region) {
            Object plotId = region.getProperties().get("plotId");
            if (plotId instanceof String s) {
                handlePlotClick(s);
                event.consume();
            }
        }
    }

    @FXML
    private void handleBtnSeedsList(MouseEvent event) {
        handleToolSlotClick(ToolMode.SEED, hasSeedSlotSelection());
    }

    @FXML
    private void handlebtnWater(MouseEvent event) {
        handleToolSlotClick(ToolMode.WATER, isWateringCanSelectionValid(equippedWateringCanItemId));
    }

    @FXML
    private void handleBtnReap(MouseEvent event) {
        handleToolSlotClick(ToolMode.SICKLE, isSickleSelectionValid(equippedSickleItemId));
    }

    @FXML
    private void handleinybox(MouseEvent event) {
        Object source = event.getSource();
        if (!(source instanceof ImageView clickedView)) return;

        Integer selectedItemId = seedSlotMapping.get(clickedView.getId());
        if (selectedItemId == null || selectedItemId <= 0) return;

        selectedSeedItemId = selectedItemId;
        switch (activeMode) {
            case WATER -> equippedWateringCanItemId = selectedItemId;
            case SICKLE -> equippedSickleItemId = selectedItemId;
            case SEED, NONE -> {
            }
        }

        hideSeedList();
        refreshSelectedToolPreview();
        refreshToolHighlight();
        refreshPlotHighlights();
        populateSeedList();
    }

    @FXML
    private void handleConfirm(ActionEvent event) {
        commitFarmChanges();
    }

    @FXML
    private void handleConfirmPane(MouseEvent event) {
        if (event.getTarget() == btnConfirm) {
            return;
        }
        commitFarmChanges();
        event.consume();
    }

    private boolean commitFarmChanges() {
        if (gameState == null || gameState.getPlayerState() == null) {
            AppDialogs.showMessageDialog(null, "Farm changes could not be confirmed right now.");
            return false;
        }

        Inventory inv = gameState.getPlayerState().getInventory();
        FarmState realFarm = gameState.getFarmState(activeFarmMapKey);
        if (realFarm == null) {
            AppDialogs.showMessageDialog(null, "Farm data is unavailable right now.");
            return false;
        }

        Map<Integer, Integer> seedsToRemove = computeSeedConsumption();
        int staminaCost = computePendingFarmStaminaCost();
        if (gameState.getPlayerState().getStamina() < staminaCost) {
            AppDialogs.showMessageDialog(null, "Not enough stamina to confirm these farm changes.");
            return false;
        }
        if (!moveEquippedFarmToolsToStorage(realFarm)) {
            AppDialogs.showMessageDialog(null, "The selected farm tools are no longer available in your inventory or hotbar.");
            return false;
        }

        for (Map.Entry<Integer, Integer> entry : seedsToRemove.entrySet()) {
            if (!removeSeedFromPlayerStorage(entry.getKey(), entry.getValue())) {
                AppDialogs.showMessageDialog(null, "You do not have enough seeds left to confirm those plantings.");
                return false;
            }
        }
        gameState.getPlayerState().consumeStamina(staminaCost);

        for (Map.Entry<Integer, Integer> entry : pendingHarvests.entrySet()) {
            inv.addItem(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Integer, Integer> entry : pendingSeedReturns.entrySet()) {
            inv.addItem(entry.getKey(), entry.getValue());
        }

        if (pendingFarmingExp > 0) {
            gameState.getPlayerSkills().addFarmingExp(pendingFarmingExp);
        }

        tempPlots.forEach((plotId, temp) -> temp.applyTo(realFarm.getPlot(plotId)));
        returnToPreviousScene();
        return true;
    }

    @FXML
    private void handleBack(ActionEvent event) {
        returnToPreviousScene();
    }

    private void returnToPreviousScene() {
        unbindTimeDrivenPlotRefresh();
        restoreTimeOverride();
        gameState.setActiveFarmMapKey(null);
        String returnFxml = gameState.getReturnFxml();
        SpawnTag returnSpawn = gameState.getReturnSpawn();
        if (returnFxml == null) {
            returnFxml = "/view/home_map.fxml";
            returnSpawn = SpawnTag.FROM_FARM;
        }
        SceneManager.switchScene(returnFxml, returnSpawn);
    }

    private void applyFarmTableTimeOverride() {
        if (timeOverrideApplied) return;
        GameTimeManager time = GameTimeManager.get();
        previousTimeScale = time.timeScaleProperty().get();
        previousPaused = time.pausedProperty().get();
        time.setPaused(false);
        time.timeScaleProperty().set(FARM_TABLE_TIME_SCALE);
        timeOverrideApplied = true;
    }

    private void bindTimeDrivenPlotRefresh() {
        if (view == null) {
            return;
        }

        unbindTimeDrivenPlotRefresh();

        GameTimeManager time = GameTimeManager.get();
        timeRefreshListener = (obs, oldValue, newValue) -> {
            if (view.getScene() == null) {
                return;
            }

            long currentMinuteTick = resolveMinuteTick(
                (newValue == null) ? time.totalSecondsProperty().get() : newValue.doubleValue()
            );
            if (currentMinuteTick == lastRenderedMinuteTick) {
                return;
            }

            refreshFarmVisualState();
        };
        time.totalSecondsProperty().addListener(timeRefreshListener);
    }

    private void unbindTimeDrivenPlotRefresh() {
        if (timeRefreshListener == null) {
            return;
        }

        GameTimeManager.get().totalSecondsProperty().removeListener(timeRefreshListener);
        timeRefreshListener = null;
    }

    private void refreshFarmVisualState() {
        refreshPlots();
        refreshPlotHighlights();
        lastRenderedMinuteTick = resolveMinuteTick(GameTimeManager.get().totalSecondsProperty().get());
    }

    private long resolveMinuteTick(double totalSeconds) {
        return (long) Math.floor(Math.max(0.0, totalSeconds) / 60.0);
    }

    private void restoreTimeOverride() {
        if (!timeOverrideApplied) return;
        GameTimeManager time = GameTimeManager.get();
        time.timeScaleProperty().set(previousTimeScale);
        time.setPaused(previousPaused);
        timeOverrideApplied = false;
    }

    private void showSeedList() {
        if (seedList == null) return;
        seedList.setVisible(true);
        seedList.setManaged(true);
        seedList.setDisable(false);
    }

    private void hideSeedList() {
        if (seedList == null) return;
        seedList.setVisible(false);
        seedList.setManaged(false);
        seedList.setDisable(true);
    }

    private void collectPlots() {
        plotViews.clear();
        plotTargets.clear();
        collectPlotNodesRecursive(farm);
    }

    private void collectPlotNodesRecursive(Node node) {
        if (node == null) return;

        if (node instanceof ImageView img) {
            String id = img.getId();
            if (id != null && id.startsWith("farm_img")) {
                int n = NodeCollectors.extractNumber(id, "farm_img");
                if (n >= 0) {
                    String plotId = "Plot " + n;
                    plotViews.put(plotId, img);
                    Region target = (img.getParent() instanceof Region region) ? region : null;
                    if (target != null) {
                        target.getProperties().put("plotId", plotId);
                        target.setPickOnBounds(true);
                        target.setMouseTransparent(false);
                        target.setOnMouseClicked(this::handleFarmPlot);
                        plotTargets.put(plotId, target);
                    }
                }
            }
        }

        if (node instanceof Pane pane) {
            for (Node child : pane.getChildren()) {
                collectPlotNodesRecursive(child);
            }
        } else if (node instanceof Group group) {
            for (Node child : group.getChildren()) {
                collectPlotNodesRecursive(child);
            }
        }
    }

    private void collectSeedSlots() {
        if (!seedSlotImages.isEmpty()) return;
        seedSlotImages.addAll(List.of(inyimg1, inyimg2, inyimg3, inyimg4, inyimg5, inyimg6, inyimg7, inyimg8, inyimg9, inyimg10,
                inyimg11, inyimg12, inyimg13, inyimg14, inyimg15, inyimg16, inyimg17, inyimg18, inyimg19, inyimg20));
        seedSlotLabels.addAll(List.of(invlbl1, invlbl2, invlbl3, invlbl4, invlbl5, invlbl6, invlbl7, invlbl8, invlbl9, invlbl10,
                invlbl11, invlbl12, invlbl13, invlbl14, invlbl15, invlbl16, invlbl17, invlbl18, invlbl19, invlbl20));
    }

    private void loadTempFarmState() {
        tempPlots.clear();
        FarmState realFarm = gameState.getFarmState(activeFarmMapKey);
        for (String plotId : plotViews.keySet()) {
            TempPlotData tempPlotData = TempPlotData.from(realFarm.getPlot(plotId));
            normalizeCropTiming(tempPlotData);
            tempPlots.put(plotId, tempPlotData);
        }
        equippedWateringCanItemId = normalizeItemId(realFarm.getEquippedWateringCanItemId());
        equippedSickleItemId = normalizeItemId(realFarm.getEquippedSickleItemId());
        pendingHarvests.clear();
        pendingSeedReturns.clear();
        pendingFarmingExp = 0;
    }

    private void handlePlotClick(String plotId) {
        TempPlotData pd = tempPlots.get(plotId);
        if (pd == null) return;

        double now = GameTimeManager.get().totalSecondsProperty().get();

        switch (activeMode) {
            case SEED -> {
                if (!isSeedSelectionValid(selectedSeedItemId)) return;
                if (!pd.isEmpty()) return;

                int available = getSelectableAvailableCount(selectedSeedItemId);
                if (available <= 0) return;

                pd.seedItemId = selectedSeedItemId;
                pd.seedItemName = ItemDatabase.getItemName(selectedSeedItemId);
                pd.cropItemName = ItemDatabase.resolveCropNameForSeed(selectedSeedItemId);
                pd.cropSpriteKey = ItemDatabase.buildCropSpriteKeyForSeed(selectedSeedItemId);
                pd.plantedAt = now;
                pd.lastWateredAt = -1;
                pd.accumulatedWaterReduction = 0;
                applySeedGrowthProfile(pd, selectedSeedItemId);
            }

            case WATER -> {
                if (!isWateringCanSelectionValid(equippedWateringCanItemId)) return;
                if (!pd.canWater(now)) return;
                pd.water(now);
            }

            case SICKLE -> {
                if (!isSickleSelectionValid(equippedSickleItemId)) return;
                if (pd.isEmpty() || !pd.isReady(now)) return;

                int harvestItemId = pd.harvestItemId();
                if (harvestItemId > 0) {
                    pendingHarvests.merge(harvestItemId, computeHarvestOutputCount(pd.seedItemId), Integer::sum);
                }

                int seedReturnCount = computeSeedReturnCount(pd.seedItemId);
                if (seedReturnCount > 0 && pd.seedItemId != null && pd.seedItemId > 0) {
                    pendingSeedReturns.merge(pd.seedItemId, seedReturnCount, Integer::sum);
                }

                pendingFarmingExp++;
                pd.clearPlant();
            }

            case NONE -> {
                return;
            }
        }

        refreshFarmVisualState();
        populateSeedList();
    }

    private void populateSeedList() {
        seedSlotMapping.clear();
        List<Integer> availableItems = availableItemsForActiveMode();

        if (!availableItems.isEmpty() && (selectedSeedItemId == null || !availableItems.contains(selectedSeedItemId))) {
            selectedSeedItemId = availableItems.get(0);
        }
        if (availableItems.isEmpty() && activeMode == ToolMode.SEED) {
            selectedSeedItemId = null;
        }

        for (int i = 0; i < seedSlotImages.size(); i++) {
            ImageView img = seedSlotImages.get(i);
            Label lbl = (i < seedSlotLabels.size()) ? seedSlotLabels.get(i) : null;
            if (img == null) continue;

            if (i >= availableItems.size()) {
                img.setImage(null);
                img.setVisible(false);
                img.setOpacity(0.0);
                img.setStyle("");
                if (lbl != null) lbl.setText("");
                continue;
            }

            int itemId = availableItems.get(i);
            int qty = getSelectableAvailableCount(itemId);
            if (qty < 0) qty = 0;

            img.setImage(ItemDatabase.getImage(itemId));
            img.setVisible(true);
            img.setOpacity(itemId == selectedSeedItemId ? 1.0 : 0.75);
            img.setStyle(itemId == selectedSeedItemId
                    ? "-fx-effect: dropshadow(gaussian, yellow, 12, 0.6, 0, 0);"
                    : "");
            seedSlotMapping.put(img.getId(), itemId);

            if (lbl != null) {
                String itemName = ItemDatabase.getItemName(itemId);
                if (itemName == null || itemName.isBlank()) itemName = "Item " + itemId;
                lbl.setText(itemName + " x" + qty);
            }
        }

        refreshSelectedToolPreview();
    }

    private List<Integer> availableItemsForActiveMode() {
        List<Integer> result = new ArrayList<>();
        if (gameState == null || gameState.getPlayerState() == null) return result;

        switch (activeMode) {
            case SEED -> {
                for (Integer itemId : ItemDatabase.getAllSeedItemIds()) {
                    if (!isSeedSelectionValid(itemId)) continue;
                    if (getSelectableAvailableCount(itemId) > 0) {
                        result.add(itemId);
                    }
                }
            }
            case WATER -> {
                for (Integer itemId : ownedInventoryAndHotbarItemIds()) {
                    if (!isWateringCanSelectionValid(itemId)) continue;
                    if (getSelectableAvailableCount(itemId) > 0) {
                        result.add(itemId);
                    }
                }
            }
            case SICKLE -> {
                for (Integer itemId : ownedInventoryAndHotbarItemIds()) {
                    if (!isSickleSelectionValid(itemId)) continue;
                    if (getSelectableAvailableCount(itemId) > 0) {
                        result.add(itemId);
                    }
                }
            }
            case NONE -> {
            }
        }

        return result;
    }

    private List<Integer> ownedInventoryAndHotbarItemIds() {
        List<Integer> result = new ArrayList<>();
        if (gameState == null || gameState.getPlayerState() == null) return result;

        java.util.LinkedHashSet<Integer> ids = new java.util.LinkedHashSet<>();
        ids.addAll(gameState.getPlayerState().getInventory().itemsProperty().keySet());

        for (int i = 0; i < gameState.getPlayerState().getHotbarSize(); i++) {
            int itemId = gameState.getPlayerState().getHotbarItemId(i);
            int qty = gameState.getPlayerState().getHotbarItemCount(i);
            if (itemId > 0 && qty > 0) {
                ids.add(itemId);
            }
        }

        result.addAll(ids);
        return result;
    }

    private int getCombinedOwnedCount(int itemId) {
        if (gameState == null || gameState.getPlayerState() == null || itemId <= 0) return 0;

        int total = gameState.getPlayerState().getInventory().getCount(itemId);

        for (int i = 0; i < gameState.getPlayerState().getHotbarSize(); i++) {
            if (gameState.getPlayerState().getHotbarItemId(i) == itemId) {
                total += gameState.getPlayerState().getHotbarItemCount(i);
            }
        }
        return total;
    }

    private int getSelectableAvailableCount(int itemId) {
        if (itemId <= 0) return 0;
        if (ItemDatabase.isSeedItem(itemId)) {
            return Math.max(0, getCombinedOwnedCount(itemId) - reservedNewSeedUsage(itemId));
        }
        return Math.max(0, getCombinedOwnedCount(itemId) - reservedToolUsage(itemId));
    }

    private boolean isSeedSelectionValid(Integer itemId) {
        return itemId != null
                && itemId > 0
                && ItemDatabase.isSeedItem(itemId)
                && ItemDatabase.hasCompleteFarmDataForSeed(itemId);
    }

    private boolean isWateringCanSelectionValid(Integer itemId) {
        return ItemDatabase.isEquipmentItem(itemId == null ? -1 : itemId)
                && hasItemName(itemId, "watering can", "watering_can");
    }

    private boolean isSickleSelectionValid(Integer itemId) {
        return ItemDatabase.isEquipmentItem(itemId == null ? -1 : itemId)
                && hasItemName(itemId, "sickle");
    }

    private boolean hasItemName(Integer itemId, String... tokens) {
        if (itemId == null || itemId <= 0) return false;
        String name = ItemDatabase.getItemName(itemId);
        if (name == null || name.isBlank()) return false;

        String normalized = name.trim().toLowerCase(java.util.Locale.ROOT).replace('_', ' ');
        for (String token : tokens) {
            if (token == null || token.isBlank()) continue;
            String t = token.trim().toLowerCase(java.util.Locale.ROOT).replace('_', ' ');
            if (normalized.contains(t)) return true;
        }
        return false;
    }

    private void refreshSelectedToolPreview() {
        if (SeedsImg != null) {
            SeedsImg.setImage(activeMode == ToolMode.SEED && isSeedSelectionValid(selectedSeedItemId)
                    ? ItemDatabase.getImage(selectedSeedItemId)
                    : defaultSeedsIcon);
        }

        if (WaterCanImg != null) {
            Integer previewWaterCan = isWateringCanSelectionValid(equippedWateringCanItemId)
                    ? equippedWateringCanItemId
                    : (activeMode == ToolMode.WATER && isWateringCanSelectionValid(selectedSeedItemId) ? selectedSeedItemId : null);
            WaterCanImg.setImage(previewWaterCan != null
                    ? ItemDatabase.getImage(previewWaterCan)
                    : defaultWaterCanIcon);
            WaterCanImg.setStyle(isWateringCanSelectionValid(equippedWateringCanItemId) ? TOOL_SLOT_STYLE : "");
        }

        if (SickleImg != null) {
            Integer previewSickle = isSickleSelectionValid(equippedSickleItemId)
                    ? equippedSickleItemId
                    : (activeMode == ToolMode.SICKLE && isSickleSelectionValid(selectedSeedItemId) ? selectedSeedItemId : null);
            SickleImg.setImage(previewSickle != null
                    ? ItemDatabase.getImage(previewSickle)
                    : defaultSickleIcon);
            SickleImg.setStyle(isSickleSelectionValid(equippedSickleItemId) ? TOOL_SLOT_STYLE : "");
        }
    }

    private int getCombinedOwnedSeedCount(int seedItemId) {
        if (gameState == null || gameState.getPlayerState() == null || seedItemId <= 0) return 0;

        int total = gameState.getPlayerState().getInventory().getCount(seedItemId);

        for (int i = 0; i < gameState.getPlayerState().getHotbarSize(); i++) {
            if (gameState.getPlayerState().getHotbarItemId(i) == seedItemId) {
                total += gameState.getPlayerState().getHotbarItemCount(i);
            }
        }
        return total;
    }

    private int getAvailableSeedCount(int seedItemId) {
        return Math.max(0, getCombinedOwnedSeedCount(seedItemId) - reservedNewSeedUsage(seedItemId));
    }

    private void refreshSelectedSeedPreview() {
        if (SeedsImg == null) return;
        SeedsImg.setImage(selectedSeedItemId == null ? null : ItemDatabase.getImage(selectedSeedItemId));
    }

    private void refreshPlots() {
        double now = GameTimeManager.get().totalSecondsProperty().get();
        for (Map.Entry<String, ImageView> entry : plotViews.entrySet()) {
            String plotId = entry.getKey();
            TempPlotData pd = tempPlots.get(plotId);
            ImageView view = entry.getValue();
            Region target = plotTargets.get(plotId);
            if (view == null) continue;

            if (pd == null || pd.isEmpty()) {
                view.setImage(null);
                view.setVisible(false);
                view.setMouseTransparent(true);
                if (target != null) {
                    target.setVisible(true);
                    target.setMouseTransparent(false);
                    target.setPickOnBounds(true);
                    target.toFront();
                }
                continue;
            }

            int stage = pd.getStage(now, TOTAL_GROWTH_STAGES);
            String spriteKey = pd.cropSpriteKey == null
                    ? ItemDatabase.buildCropSpriteKeyForSeed(pd.seedItemId == null ? -1 : pd.seedItemId)
                    : pd.cropSpriteKey;

            String path = String.format("/image/crops/%s_plant%d.png", spriteKey, stage);
            URL url = getClass().getResource(path);

            if (url == null) {
                view.setImage(null);
                view.setVisible(false);
                view.setMouseTransparent(true);
                continue;
            }

            view.setImage(new Image(url.toExternalForm()));
            view.setVisible(true);
            view.setOpacity(1.0);
            view.setMouseTransparent(false);
        }
    }

    private void refreshToolHighlight() {
        highlightTool(SeedsImg, activeMode == ToolMode.SEED);
        highlightTool(WaterCanImg, activeMode == ToolMode.WATER);
        highlightTool(SickleImg, activeMode == ToolMode.SICKLE);
    }

    private void highlightTool(ImageView view, boolean selected) {
        if (view == null) return;
        view.setOpacity(selected ? 1.0 : 0.55);
        view.setScaleX(selected ? 1.08 : 1.0);
        view.setScaleY(selected ? 1.08 : 1.0);
    }

    private void refreshPlotHighlights() {
        double now = GameTimeManager.get().totalSecondsProperty().get();
        for (Map.Entry<String, ImageView> entry : plotViews.entrySet()) {
            ImageView view = entry.getValue();
            TempPlotData pd = tempPlots.get(entry.getKey());
            if (view == null) continue;

            boolean highlight = switch (activeMode) {
                case SEED -> isSeedSelectionValid(selectedSeedItemId) && pd != null && pd.isEmpty();
                case WATER -> isWateringCanSelectionValid(equippedWateringCanItemId) && pd != null && pd.canWater(now);
                case SICKLE -> isSickleSelectionValid(equippedSickleItemId) && pd != null && pd.isReady(now);
                case NONE -> false;
            };

            view.setVisible(true);
            view.setMouseTransparent(false);
            view.setPickOnBounds(true);
            view.setStyle(highlight
                    ? "-fx-effect: dropshadow(gaussian, yellow, 12, 0.6, 0, 0);"
                    : "");
        }
    }
    
    private int reservedNewSeedUsage(int seedItemId) {
        int count = 0;
        FarmState realFarm = gameState.getFarmState(activeFarmMapKey);
        for (Map.Entry<String, TempPlotData> entry : tempPlots.entrySet()) {
            TempPlotData pd = entry.getValue();
            if (pd == null || pd.seedItemId == null || pd.seedItemId != seedItemId) continue;
            FarmState.PlotData real = realFarm.getPlot(entry.getKey());
            if (real == null || real.seedItemId == null || real.seedItemId <= 0) count++;
        }
        return count;
    }

    private int reservedToolUsage(int itemId) {
        if (itemId <= 0) return 0;
        int reserved = 0;

        FarmState realFarm = gameState.getFarmState(activeFarmMapKey);
        Integer realWateringCan = normalizeItemId(realFarm.getEquippedWateringCanItemId());
        Integer realSickle = normalizeItemId(realFarm.getEquippedSickleItemId());

        if (java.util.Objects.equals(itemId, normalizeItemId(equippedWateringCanItemId))
                && !java.util.Objects.equals(itemId, realWateringCan)) {
            reserved++;
        }
        if (java.util.Objects.equals(itemId, normalizeItemId(equippedSickleItemId))
                && !java.util.Objects.equals(itemId, realSickle)) {
            reserved++;
        }

        return reserved;
    }

    private Map<Integer, Integer> computeSeedConsumption() {
        Map<Integer, Integer> used = new LinkedHashMap<>();
        FarmState realFarm = gameState.getFarmState(activeFarmMapKey);
        for (Map.Entry<String, TempPlotData> entry : tempPlots.entrySet()) {
            FarmState.PlotData real = realFarm.getPlot(entry.getKey());
            TempPlotData temp = entry.getValue();
            boolean newlyPlanted = (real == null || real.isEmpty()) && temp != null && !temp.isEmpty();
            if (!newlyPlanted || temp.seedItemId == null || temp.seedItemId <= 0) continue;
            used.merge(temp.seedItemId, 1, Integer::sum);
        }
        return used;
    }

    private boolean removeSeedFromPlayerStorage(int seedItemId, int qty) {
        if (seedItemId <= 0 || qty <= 0) return true;

        int remaining = qty;
        Inventory inv = gameState.getPlayerState().getInventory();
        int invCount = inv.getCount(seedItemId);
        int fromInventory = Math.min(invCount, remaining);
        if (fromInventory > 0) {
            if (!inv.removeItem(seedItemId, fromInventory)) return false;
            remaining -= fromInventory;
        }

        if (remaining <= 0) return true;

        for (int i = 0; i < gameState.getPlayerState().getHotbarSize() && remaining > 0; i++) {
            if (gameState.getPlayerState().getHotbarItemId(i) != seedItemId) continue;

            int slotCount = gameState.getPlayerState().getHotbarItemCount(i);
            int fromSlot = Math.min(slotCount, remaining);
            if (fromSlot > 0) {
                if (!gameState.getPlayerState().removeHotbarItems(i, fromSlot)) return false;
                remaining -= fromSlot;
            }
        }

        return remaining <= 0;
    }

    private boolean moveEquippedFarmToolsToStorage(FarmState realFarm) {
        if (realFarm == null) return false;

        Integer targetWateringCan = normalizeItemId(equippedWateringCanItemId);
        Integer targetSickle = normalizeItemId(equippedSickleItemId);
        Integer currentWateringCan = normalizeItemId(realFarm.getEquippedWateringCanItemId());
        Integer currentSickle = normalizeItemId(realFarm.getEquippedSickleItemId());

        if (!syncSingleEquippedTool(currentWateringCan, targetWateringCan)) return false;
        if (!syncSingleEquippedTool(currentSickle, targetSickle)) return false;

        realFarm.setEquippedWateringCanItemId(targetWateringCan);
        realFarm.setEquippedSickleItemId(targetSickle);
        return true;
    }

    private boolean syncSingleEquippedTool(Integer currentItemId, Integer targetItemId) {
        Integer normalizedCurrent = normalizeItemId(currentItemId);
        Integer normalizedTarget = normalizeItemId(targetItemId);

        if (java.util.Objects.equals(normalizedCurrent, normalizedTarget)) {
            return true;
        }

        if (normalizedTarget == null) {
            if (normalizedCurrent != null) {
                gameState.getPlayerState().getInventory().addItem(normalizedCurrent, 1);
            }
            return true;
        }

        if (!removeSingleItemFromPlayerStorage(normalizedTarget)) {
            return false;
        }

        if (normalizedCurrent != null) {
            gameState.getPlayerState().getInventory().addItem(normalizedCurrent, 1);
        }
        return true;
    }

    private boolean removeSingleItemFromPlayerStorage(int itemId) {
        if (itemId <= 0) return true;

        Inventory inv = gameState.getPlayerState().getInventory();
        if (inv.getCount(itemId) > 0) {
            return inv.removeItem(itemId, 1);
        }

        for (int i = 0; i < gameState.getPlayerState().getHotbarSize(); i++) {
            if (gameState.getPlayerState().getHotbarItemId(i) == itemId
                    && gameState.getPlayerState().getHotbarItemCount(i) > 0) {
                return gameState.getPlayerState().removeHotbarItems(i, 1);
            }
        }

        return false;
    }

    private Integer normalizeItemId(Integer itemId) {
        return (itemId == null || itemId <= 0) ? null : itemId;
    }

    private int computeSeedReturnCount(Integer seedItemId) {
        if (seedItemId == null || seedItemId <= 0 || gameState == null || gameState.getPlayerState() == null) {
            return 0;
        }

        if (!ItemDatabase.hasCompleteFarmDataForSeed(seedItemId)) {
            return 0;
        }

        int harvestItemId = ItemDatabase.resolveHarvestItemIdForSeed(seedItemId);
        if (harvestItemId <= 0 || harvestItemId == seedItemId) {
            return 0;
        }

        double chancePercent = computeSeedReturnChancePercent();
        if (chancePercent >= 200.0) {
            return 2;
        }
        if (chancePercent >= 100.0) {
            return 1;
        }

        double roll = ThreadLocalRandom.current().nextDouble(100.0);
        return roll < chancePercent ? 1 : 0;
    }

    private double computeSeedReturnChancePercent() {
        int dex = Math.max(0, gameState.getPlayerState().getDex());
        return (dex * 0.01) + (getEffectiveFarmingLevel() * 1.0);
    }

    private int computeHarvestOutputCount(Integer seedItemId) {
        if (seedItemId == null || seedItemId <= 0) return 0;
        int farmingLevel = getEffectiveFarmingLevel();
        return 1 + (farmingLevel / 5);
    }

    private int getEffectiveFarmingLevel() {
        if (gameState == null) return 0;
        GameState.PlayerSkills skills = gameState.getPlayerSkills();
        return skills == null ? 0 : skills.getLevelFromTotalExp(skills.getFarmingExp() + pendingFarmingExp);
    }

    private void handleToolSlotClick(ToolMode mode, boolean hasAssignedItem) {
        if (mode == ToolMode.NONE) return;

        boolean currentlyActive = activeMode == mode;
        boolean listVisible = seedList != null && seedList.isVisible();

        activeMode = mode;

        if (!hasAssignedItem) {
            populateSeedList();
            showSeedList();
        } else if (!currentlyActive) {
            hideSeedList();
        } else if (!listVisible) {
            populateSeedList();
            showSeedList();
        } else {
            hideSeedList();
        }

        refreshSelectedToolPreview();
        refreshToolHighlight();
        refreshPlotHighlights();
    }

    private boolean hasSeedSlotSelection() {
        return isSeedSelectionValid(selectedSeedItemId);
    }

    private void normalizeCropTiming(TempPlotData plotData) {
        if (plotData == null || plotData.seedItemId == null || plotData.seedItemId <= 0) return;
        applySeedGrowthProfile(plotData, plotData.seedItemId);
    }

    private void applySeedGrowthProfile(TempPlotData plotData, int seedItemId) {
        if (plotData == null || seedItemId <= 0) return;
        plotData.totalGrowthSeconds = FarmGrowthRules.resolveTotalGrowthSeconds(seedItemId);
        plotData.waterCooldownSeconds = FarmGrowthRules.resolveWaterCooldownSeconds(plotData.totalGrowthSeconds);
        plotData.waterReductionSeconds = FarmGrowthRules.resolveWaterReductionSeconds(plotData.totalGrowthSeconds);
        plotData.accumulatedWaterReduction = FarmGrowthRules.clampAccumulatedWaterReduction(
            plotData.accumulatedWaterReduction,
            plotData.totalGrowthSeconds
        );
    }

    private int computePendingFarmStaminaCost() {
        int total = 0;
        FarmState realFarm = gameState.getFarmState(activeFarmMapKey);
        for (Map.Entry<String, TempPlotData> entry : tempPlots.entrySet()) {
            FarmState.PlotData real = realFarm.getPlot(entry.getKey());
            TempPlotData temp = entry.getValue();
            if (real.isEmpty() && !temp.isEmpty()) total += DEFAULT_PLANT_STAMINA_COST;
        }
        return total;
    }

    private String resolveActiveFarmMapKey() {
        if (gameState == null) return "home_map";
        String mapKey = gameState.getActiveFarmMapKey();
        if (mapKey == null || mapKey.isBlank()) return "home_map";
        return mapKey;
    }

    @FXML
    private void handlecloseseedList(MouseEvent event) {
        hideSeedList();
        refreshToolHighlight();
        refreshPlotHighlights();
    }

    private enum ToolMode { NONE, SEED, WATER, SICKLE }

    private static class TempPlotData {
        Integer seedItemId;
        String seedItemName;
        String cropItemName;
        String cropSpriteKey;
        double plantedAt;
        double lastWateredAt;
        double accumulatedWaterReduction;
        double totalGrowthSeconds;
        double waterCooldownSeconds;
        double waterReductionSeconds;

        static TempPlotData from(FarmState.PlotData src) {
            TempPlotData t = new TempPlotData();
            t.seedItemId = src.seedItemId;
            t.seedItemName = src.seedItemName;
            t.cropItemName = src.cropItemName;
            t.cropSpriteKey = src.cropSpriteKey;
            t.plantedAt = src.plantedAt;
            t.lastWateredAt = src.lastWateredAt;
            t.accumulatedWaterReduction = src.accumulatedWaterReduction;
            t.totalGrowthSeconds = src.totalGrowthSeconds;
            t.waterCooldownSeconds = src.waterCooldownSeconds;
            t.waterReductionSeconds = src.waterReductionSeconds;
            return t;
        }

        void applyTo(FarmState.PlotData dst) {
            dst.seedItemId = seedItemId;
            dst.seedItemName = seedItemName;
            dst.cropItemName = cropItemName;
            dst.cropSpriteKey = cropSpriteKey;
            dst.plantedAt = plantedAt;
            dst.lastWateredAt = lastWateredAt;
            dst.accumulatedWaterReduction = accumulatedWaterReduction;
            dst.totalGrowthSeconds = totalGrowthSeconds;
            dst.waterCooldownSeconds = waterCooldownSeconds;
            dst.waterReductionSeconds = waterReductionSeconds;
        }

        boolean isEmpty() { return seedItemId == null || seedItemId <= 0; }
        void clearPlant() {
            seedItemId = null; seedItemName = null; cropItemName = null; cropSpriteKey = null;
            plantedAt = -1; lastWateredAt = -1; accumulatedWaterReduction = 0;
        }
        boolean isReady(double nowSeconds) {
            if (isEmpty()) return false;
            return FarmGrowthRules.isReady(plantedAt, accumulatedWaterReduction, totalGrowthSeconds, nowSeconds);
        }
        boolean canWater(double nowSeconds) {
            if (isEmpty()) return false;
            return FarmGrowthRules.canWater(
                plantedAt,
                lastWateredAt,
                accumulatedWaterReduction,
                totalGrowthSeconds,
                waterReductionSeconds,
                nowSeconds
            );
        }
        void water(double nowSeconds) {
            if (!canWater(nowSeconds)) return;
            lastWateredAt = nowSeconds;
            accumulatedWaterReduction = FarmGrowthRules.clampAccumulatedWaterReduction(
                accumulatedWaterReduction + waterReductionSeconds,
                totalGrowthSeconds
            );
        }
        int getStage(double nowSeconds, int totalStages) {
            if (isEmpty()) return 0;
            if (totalStages <= 1) return 1;
            int stage = FarmGrowthRules.getStage(plantedAt, accumulatedWaterReduction, totalGrowthSeconds, nowSeconds);
            return Math.min(totalStages, stage);
        }
        int harvestItemId() { return seedItemId == null ? -1 : ItemDatabase.resolveHarvestItemIdForSeed(seedItemId); }
    }
}
