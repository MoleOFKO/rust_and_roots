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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import model.FarmState;
import model.Inventory;
import model.ItemDatabase;
import model.NodeCollectors;
import util.SceneManager;
import util.SpawnTag;

public class Farm_tableController implements Initializable, SceneController {

    private static final double FARM_TABLE_TIME_SCALE = 0.25;
    private static final int TOTAL_GROWTH_STAGES = 3;
    private static final double DEFAULT_GROWTH_SECONDS = 60.0;
    private static final int DEFAULT_PLANT_STAMINA_COST = 2;
    private static final int SEED_RETURN_CHANCE = 15;

    private GameState gameState;
    private ToolMode activeMode = ToolMode.NONE;
    private Integer selectedSeedItemId;
    private double previousTimeScale = 1.0;
    private boolean previousPaused = false;
    private boolean timeOverrideApplied = false;
    private String activeFarmMapKey = "home_map";

    private final Map<String, ImageView> plotViews = new LinkedHashMap<>();
    private final Map<String, TempPlotData> tempPlots = new LinkedHashMap<>();
    private final Map<Integer, Integer> pendingHarvests = new LinkedHashMap<>();
    private final Map<Integer, Integer> pendingSeedReturns = new LinkedHashMap<>();
    private final List<ImageView> seedSlotImages = new ArrayList<>();
    private final List<Label> seedSlotLabels = new ArrayList<>();
    private final Map<String, Integer> seedSlotMapping = new LinkedHashMap<>();

    @FXML private StackPane view;
    @FXML private Pane world;
    @FXML private ImageView SeedsImg;
    @FXML private ImageView WaterCanImg;
    @FXML private ImageView SickleImg;
    @FXML private Group farm;
    @FXML private Pane seedList;
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
        activeFarmMapKey = resolveActiveFarmMapKey();
        collectPlots();
        collectSeedSlots();
        loadTempFarmState();
        applyFarmTableTimeOverride();
        hideSeedList();
        populateSeedList();
        refreshPlots();
        refreshToolHighlight();
        refreshPlotHighlights();
        if (view != null) {
            view.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene == null) restoreTimeOverride();
            });
        }
    }

    @FXML
    private void handleFarmPlot(MouseEvent event) {
        Object source = event.getSource();
        if (!(source instanceof ImageView clickedView)) return;
        String id = clickedView.getId();
        if (id == null || !id.startsWith("farm_img")) return;
        int n = NodeCollectors.extractNumber(id, "farm_img");
        if (n < 0) return;
        handlePlotClick("Plot " + n);
    }

    @FXML
    private void handleBtnSeedsList(MouseEvent event) {
        if (activeMode == ToolMode.SEED && seedList != null && seedList.isVisible()) {
            hideSeedList();
            refreshToolHighlight();
            refreshPlotHighlights();
            return;
        }
        setToolMode(ToolMode.SEED);
    }
    @FXML private void handlebtnWater(MouseEvent event) { setToolMode(ToolMode.WATER); }
    @FXML private void handleBtnReap(MouseEvent event) { setToolMode(ToolMode.SICKLE); }

    @FXML
    private void handleinybox(MouseEvent event) {
        Object source = event.getSource();
        if (!(source instanceof ImageView clickedView)) return;
        Integer seedItemId = seedSlotMapping.get(clickedView.getId());
        if (seedItemId == null || seedItemId <= 0) return;

        selectedSeedItemId = seedItemId;
        activeMode = ToolMode.SEED;

        hideSeedList();
        refreshSelectedSeedPreview();
        refreshToolHighlight();
        refreshPlotHighlights();
        populateSeedList();
    }

    @FXML
    private void handleConfirm(ActionEvent event) {
        Inventory inv = gameState.getPlayerState().getInventory();
        FarmState realFarm = gameState.getFarmState(activeFarmMapKey);
        Map<Integer, Integer> seedsToRemove = computeSeedConsumption();
        int staminaCost = computePendingFarmStaminaCost();
        if (gameState.getPlayerState().getStamina() < staminaCost) return;

        for (Map.Entry<Integer, Integer> entry : seedsToRemove.entrySet()) {
            if (!removeSeedFromPlayerStorage(entry.getKey(), entry.getValue())) return;
        }
        gameState.getPlayerState().consumeStamina(staminaCost);

        for (Map.Entry<Integer, Integer> entry : pendingHarvests.entrySet()) inv.addItem(entry.getKey(), entry.getValue());
        for (Map.Entry<Integer, Integer> entry : pendingSeedReturns.entrySet()) inv.addItem(entry.getKey(), entry.getValue());

        tempPlots.forEach((plotId, temp) -> temp.applyTo(realFarm.getPlot(plotId)));
        returnToPreviousScene();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        returnToPreviousScene();
    }

    private void returnToPreviousScene() {
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

    private void restoreTimeOverride() {
        if (!timeOverrideApplied) return;
        GameTimeManager time = GameTimeManager.get();
        time.timeScaleProperty().set(previousTimeScale);
        time.setPaused(previousPaused);
        timeOverrideApplied = false;
    }

    private void setToolMode(ToolMode mode) {
        activeMode = mode;
        if (mode == ToolMode.SEED) {
            populateSeedList();
            showSeedList();
        } else {
            hideSeedList();
        }
        refreshToolHighlight();
        refreshPlotHighlights();
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
        List<ImageView> plots = NodeCollectors.byTypeAndPrefix(farm, ImageView.class, "farm_img");
        NodeCollectors.sortByIdNumber(plots, "farm_img");
        for (ImageView img : plots) {
            int n = NodeCollectors.extractNumber(img.getId(), "farm_img");
            if (n >= 0) plotViews.put("Plot " + n, img);
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
        for (String plotId : plotViews.keySet()) tempPlots.put(plotId, TempPlotData.from(realFarm.getPlot(plotId)));
        pendingHarvests.clear();
        pendingSeedReturns.clear();
    }

    private void handlePlotClick(String plotId) {
        TempPlotData pd = tempPlots.get(plotId);
        if (pd == null) return;
        double now = GameTimeManager.get().totalSecondsProperty().get();

        switch (activeMode) {
            case SEED -> {
                if (selectedSeedItemId == null || selectedSeedItemId <= 0) return;
                if (!pd.isEmpty()) return;
                int available = getAvailableSeedCount(selectedSeedItemId);
                if (available <= 0) return;

                pd.seedItemId = selectedSeedItemId;
                pd.seedItemName = ItemDatabase.getItemName(selectedSeedItemId);
                pd.cropItemName = ItemDatabase.resolveCropNameForSeed(selectedSeedItemId);
                pd.cropSpriteKey = ItemDatabase.buildCropSpriteKeyForSeed(selectedSeedItemId);
                pd.plantedAt = now;
                pd.lastWateredAt = -1;
                pd.accumulatedWaterReduction = 0;
                pd.totalGrowthSeconds = DEFAULT_GROWTH_SECONDS;
            }
            case WATER -> {
                if (!pd.canWater(now)) return;
                pd.water(now);
            }
            case SICKLE -> {
                if (pd.isEmpty() || !pd.isReady(now)) return;
                int harvestItemId = pd.harvestItemId();
                if (harvestItemId > 0) {
                    pendingHarvests.merge(harvestItemId, 1, Integer::sum);
                }
                if (ThreadLocalRandom.current().nextInt(100) < SEED_RETURN_CHANCE && pd.seedItemId != null && pd.seedItemId > 0) {
                    pendingSeedReturns.merge(pd.seedItemId, 1, Integer::sum);
                }
                gameState.getPlayerSkills().addFarmingExp(1);
                pd.clearPlant();
            }
            case NONE -> { return; }
        }

        refreshPlots();
        refreshPlotHighlights();
        populateSeedList();
    }

    private void populateSeedList() {
        seedSlotMapping.clear();
        List<Integer> seeds = availableSeedsFromInventoryAndHotbar();

        if (!seeds.isEmpty() && (selectedSeedItemId == null || !seeds.contains(selectedSeedItemId))) {
            selectedSeedItemId = seeds.get(0);
        }
        if (seeds.isEmpty()) {
            selectedSeedItemId = null;
        }

        for (int i = 0; i < seedSlotImages.size(); i++) {
            ImageView img = seedSlotImages.get(i);
            Label lbl = (i < seedSlotLabels.size()) ? seedSlotLabels.get(i) : null;
            if (img == null) continue;

            if (i >= seeds.size()) {
                img.setImage(null);
                img.setVisible(false);
                img.setOpacity(0.0);
                img.setStyle("");
                if (lbl != null) lbl.setText("");
                continue;
            }

            int seedItemId = seeds.get(i);
            int qty = getAvailableSeedCount(seedItemId);
            if (qty < 0) qty = 0;

            img.setImage(ItemDatabase.getImage(seedItemId));
            img.setVisible(true);
            img.setOpacity(seedItemId == selectedSeedItemId ? 1.0 : 0.75);
            img.setStyle(seedItemId == selectedSeedItemId
                    ? "-fx-effect: dropshadow(gaussian, yellow, 12, 0.6, 0, 0);"
                    : "");
            seedSlotMapping.put(img.getId(), seedItemId);

            if (lbl != null) {
                String itemName = ItemDatabase.getItemName(seedItemId);
                if (itemName == null || itemName.isBlank()) itemName = "Seed " + seedItemId;
                lbl.setText(itemName + " x" + qty);
            }
        }

        refreshSelectedSeedPreview();
    }

    private List<Integer> availableSeedsFromInventoryAndHotbar() {
        List<Integer> result = new ArrayList<>();
        if (gameState == null || gameState.getPlayerState() == null) return result;

        for (Integer itemId : ItemDatabase.getAllSeedItemIds()) {
            if (itemId == null || itemId <= 0) continue;
            if (!ItemDatabase.hasCompleteFarmDataForSeed(itemId)) continue;

            if (getAvailableSeedCount(itemId) > 0) {
                result.add(itemId);
            }
        }
        return result;
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
            TempPlotData pd = tempPlots.get(entry.getKey());
            ImageView view = entry.getValue();
            if (pd == null || pd.isEmpty()) {
                view.setImage(null);
                view.setVisible(false);
                continue;
            }
            int stage = pd.getStage(now, TOTAL_GROWTH_STAGES);
            String spriteKey = pd.cropSpriteKey == null ? ItemDatabase.buildCropSpriteKeyForSeed(pd.seedItemId == null ? -1 : pd.seedItemId) : pd.cropSpriteKey;
            String path = String.format("/image/crops/%s_plant%d.png", spriteKey, stage);
            URL url = getClass().getResource(path);
            if (url == null) {
                view.setImage(null);
                view.setVisible(false);
                continue;
            }
            view.setImage(new Image(url.toExternalForm()));
            view.setVisible(true);
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
            TempPlotData pd = tempPlots.get(entry.getKey());
            boolean highlight = switch (activeMode) {
                case SEED -> selectedSeedItemId != null && pd != null && pd.isEmpty();
                case WATER -> pd != null && pd.canWater(now);
                case SICKLE -> pd != null && pd.isReady(now);
                case NONE -> false;
            };
            entry.getValue().setStyle(highlight ? "-fx-effect: dropshadow(gaussian, yellow, 12, 0.6, 0, 0);" : "");
        }
    }


    private int reservedNewSeedUsage(int seedItemId) {
        int count = 0;
        FarmState realFarm = gameState.getFarmState(activeFarmMapKey);
        for (Map.Entry<String, TempPlotData> entry : tempPlots.entrySet()) {
            TempPlotData pd = entry.getValue();
            if (pd == null || pd.seedItemId == null || pd.seedItemId != seedItemId) continue;
            FarmState.PlotData real = realFarm.getPlot(entry.getKey());
            boolean newlyPlanted = real.isEmpty() && !pd.isEmpty();
            if (newlyPlanted) count++;
        }
        return count;
    }

    private Map<Integer, Integer> computeSeedConsumption() {
        Map<Integer, Integer> used = new LinkedHashMap<>();
        FarmState realFarm = gameState.getFarmState(activeFarmMapKey);

        for (Map.Entry<String, TempPlotData> entry : tempPlots.entrySet()) {
            TempPlotData temp = entry.getValue();
            FarmState.PlotData real = realFarm.getPlot(entry.getKey());
            boolean newlyPlanted = real.isEmpty() && !temp.isEmpty();
            if (!newlyPlanted || temp.seedItemId == null || temp.seedItemId <= 0) continue;
            used.merge(temp.seedItemId, 1, Integer::sum);
        }

        return used;
    }

    private boolean removeSeedFromPlayerStorage(int seedItemId, int qty) {
        if (gameState == null || gameState.getPlayerState() == null) return false;
        if (seedItemId <= 0 || qty <= 0) return true;

        Inventory inv = gameState.getPlayerState().getInventory();
        int remaining = qty;

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
            double effectiveElapsed = Math.max(0, (nowSeconds - plantedAt) + accumulatedWaterReduction);
            return effectiveElapsed >= totalGrowthSeconds;
        }
        boolean canWater(double nowSeconds) {
            if (isEmpty()) return false;
            if (isReady(nowSeconds)) return false;
            if (lastWateredAt < 0) return true;
            return (nowSeconds - lastWateredAt) >= waterCooldownSeconds;
        }
        void water(double nowSeconds) {
            if (!canWater(nowSeconds)) return;
            lastWateredAt = nowSeconds;
            accumulatedWaterReduction = Math.min(totalGrowthSeconds, accumulatedWaterReduction + waterReductionSeconds);
        }
        int getStage(double nowSeconds, int totalStages) {
            if (isEmpty()) return 0;
            if (totalStages <= 1) return 1;
            double effectiveElapsed = Math.max(0, (nowSeconds - plantedAt) + accumulatedWaterReduction);
            double p = totalGrowthSeconds <= 0 ? 1.0 : Math.min(1.0, effectiveElapsed / totalGrowthSeconds);
            int stage = 1 + (int) Math.floor(p * (totalStages - 1));
            if (stage < 1) stage = 1;
            if (stage > totalStages) stage = totalStages;
            return stage;
        }
        int harvestItemId() { return seedItemId == null ? -1 : ItemDatabase.resolveHarvestItemIdForSeed(seedItemId); }
    }
}
