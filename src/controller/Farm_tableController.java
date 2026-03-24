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
import model.Farm;
import model.FarmState;
import model.Inventory;
import model.ItemDatabase;
import model.NodeCollectors;
import model.item;
import util.SceneManager;
import util.SpawnTag;

public class Farm_tableController implements Initializable, SceneController {

    private static final double FARM_TABLE_TIME_SCALE = 0.25;
    private static final int TOTAL_GROWTH_STAGES = 3;
    private static final int DEFAULT_DEX = 5;

    private GameState gameState;

    private ToolMode activeMode = ToolMode.NONE;
    private Farm.SeedType selectedSeedType;

    private double previousTimeScale = 1.0;
    private boolean previousPaused = false;
    private boolean timeOverrideApplied = false;

    private final Map<String, ImageView> plotViews = new LinkedHashMap<>();
    private final Map<String, TempPlotData> tempPlots = new LinkedHashMap<>();
    private final Map<Integer, Integer> pendingHarvests = new LinkedHashMap<>();

    private final List<ImageView> seedSlotImages = new ArrayList<>();
    private final List<Label> seedSlotLabels = new ArrayList<>();
    private final Map<String, Farm.SeedType> seedSlotMapping = new LinkedHashMap<>();

    @FXML private StackPane view;
    @FXML private Pane world;

    @FXML private ImageView SeedsImg;
    @FXML private ImageView WaterCanImg;
    @FXML private ImageView SickleImg;

    @FXML private Group farm;
    @FXML private ImageView farm_img1;
    @FXML private ImageView farm_img2;
    @FXML private ImageView farm_img3;
    @FXML private ImageView farm_img4;
    @FXML private ImageView farm_img5;
    @FXML private ImageView farm_img6;
    @FXML private ImageView farm_img7;
    @FXML private ImageView farm_img8;
    @FXML private ImageView farm_img9;
    @FXML private ImageView farm_img10;
    @FXML private ImageView farm_img11;
    @FXML private ImageView farm_img12;
    @FXML private ImageView farm_img13;
    @FXML private ImageView farm_img14;
    @FXML private ImageView farm_img15;
    @FXML private ImageView farm_img16;
    @FXML private ImageView farm_img17;
    @FXML private ImageView farm_img18;
    @FXML private ImageView farm_img19;
    @FXML private ImageView farm_img20;
    @FXML private ImageView farm_img21;
    @FXML private ImageView farm_img22;
    @FXML private ImageView farm_img23;
    @FXML private ImageView farm_img24;
    @FXML private ImageView farm_img25;
    @FXML private ImageView farm_img26;
    @FXML private ImageView farm_img27;
    @FXML private ImageView farm_img28;

    @FXML private Pane seedList;

    @FXML private ImageView inyimg1;
    @FXML private Label invlbl1;
    @FXML private ImageView inyimg2;
    @FXML private Label invlbl2;
    @FXML private ImageView inyimg3;
    @FXML private Label invlbl3;
    @FXML private ImageView inyimg4;
    @FXML private Label invlbl4;
    @FXML private ImageView inyimg5;
    @FXML private Label invlbl5;
    @FXML private ImageView inyimg6;
    @FXML private Label invlbl6;
    @FXML private ImageView inyimg7;
    @FXML private Label invlbl7;
    @FXML private ImageView inyimg8;
    @FXML private Label invlbl8;
    @FXML private ImageView inyimg9;
    @FXML private Label invlbl9;
    @FXML private ImageView inyimg10;
    @FXML private Label invlbl10;
    @FXML private ImageView inyimg11;
    @FXML private Label invlbl11;
    @FXML private ImageView inyimg12;
    @FXML private Label invlbl12;
    @FXML private ImageView inyimg13;
    @FXML private Label invlbl13;
    @FXML private ImageView inyimg14;
    @FXML private Label invlbl14;
    @FXML private ImageView inyimg15;
    @FXML private Label invlbl15;
    @FXML private ImageView inyimg16;
    @FXML private Label invlbl16;
    @FXML private ImageView inyimg17;
    @FXML private Label invlbl17;
    @FXML private ImageView inyimg18;
    @FXML private Label invlbl18;
    @FXML private ImageView inyimg19;
    @FXML private Label invlbl19;
    @FXML private ImageView inyimg20;
    @FXML private Label invlbl20;

    @FXML private Button btnConfirm;
    @FXML private Button btnBack;

    @Override
    public void init(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (gameState == null) {
            gameState = GameState.get();
        }

        ItemDatabase.loadItems();

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
                if (newScene == null) {
                    restoreTimeOverride();
                }
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
        setToolMode(ToolMode.SEED);
    }

    @FXML
    private void handlebtnWater(MouseEvent event) {
        setToolMode(ToolMode.WATER);
    }

    @FXML
    private void handleBtnReap(MouseEvent event) {
        setToolMode(ToolMode.SICKLE);
    }

    @FXML
    private void handleinybox(MouseEvent event) {
        Object source = event.getSource();
        if (!(source instanceof ImageView clickedView)) return;

        String id = clickedView.getId();
        if (id == null) return;

        Farm.SeedType seed = seedSlotMapping.get(id);
        if (seed == null) return;

        selectedSeedType = seed;
        setToolMode(ToolMode.SEED);
        populateSeedList();
    }

    @FXML
    private void handleConfirm(ActionEvent event) {
        Inventory inv = gameState.getPlayerState().getInventory();
        FarmState realFarm = gameState.getFarmState("home_map");

        Map<Integer, Integer> seedsToRemove = computeSeedConsumption();
        int staminaCost = computePendingFarmStaminaCost();
        if (gameState.getPlayerState().getStamina() < staminaCost) {
            return;
        }

        for (Map.Entry<Integer, Integer> entry : seedsToRemove.entrySet()) {
            if (!inv.removeItem(entry.getKey(), entry.getValue())) {
                return;
            }
        }
        gameState.getPlayerState().consumeStamina(staminaCost);

        for (Map.Entry<Integer, Integer> entry : pendingHarvests.entrySet()) {
            inv.addItem(entry.getKey(), entry.getValue());
        }

        tempPlots.forEach((plotId, temp) -> {
            FarmState.PlotData real = realFarm.getPlot(plotId);
            temp.applyTo(real);
        });

        returnToPreviousScene();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        returnToPreviousScene();
    }

    private void returnToPreviousScene() {
        restoreTimeOverride();

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
            if (n >= 0) {
                plotViews.put("Plot " + n, img);
            }
        }
    }

    private void collectSeedSlots() {
        if (!seedSlotImages.isEmpty()) return;

        seedSlotImages.add(inyimg1);
        seedSlotImages.add(inyimg2);
        seedSlotImages.add(inyimg3);
        seedSlotImages.add(inyimg4);
        seedSlotImages.add(inyimg5);
        seedSlotImages.add(inyimg6);
        seedSlotImages.add(inyimg7);
        seedSlotImages.add(inyimg8);
        seedSlotImages.add(inyimg9);
        seedSlotImages.add(inyimg10);
        seedSlotImages.add(inyimg11);
        seedSlotImages.add(inyimg12);
        seedSlotImages.add(inyimg13);
        seedSlotImages.add(inyimg14);
        seedSlotImages.add(inyimg15);
        seedSlotImages.add(inyimg16);
        seedSlotImages.add(inyimg17);
        seedSlotImages.add(inyimg18);
        seedSlotImages.add(inyimg19);
        seedSlotImages.add(inyimg20);

        seedSlotLabels.add(invlbl1);
        seedSlotLabels.add(invlbl2);
        seedSlotLabels.add(invlbl3);
        seedSlotLabels.add(invlbl4);
        seedSlotLabels.add(invlbl5);
        seedSlotLabels.add(invlbl6);
        seedSlotLabels.add(invlbl7);
        seedSlotLabels.add(invlbl8);
        seedSlotLabels.add(invlbl9);
        seedSlotLabels.add(invlbl10);
        seedSlotLabels.add(invlbl11);
        seedSlotLabels.add(invlbl12);
        seedSlotLabels.add(invlbl13);
        seedSlotLabels.add(invlbl14);
        seedSlotLabels.add(invlbl15);
        seedSlotLabels.add(invlbl16);
        seedSlotLabels.add(invlbl17);
        seedSlotLabels.add(invlbl18);
        seedSlotLabels.add(invlbl19);
        seedSlotLabels.add(invlbl20);
    }

    private void loadTempFarmState() {
        tempPlots.clear();

        FarmState realFarm = gameState.getFarmState("home_map");
        for (String plotId : plotViews.keySet()) {
            FarmState.PlotData src = realFarm.getPlot(plotId);
            tempPlots.put(plotId, TempPlotData.from(src));
        }

        pendingHarvests.clear();
    }

    private void handlePlotClick(String plotId) {
        TempPlotData pd = tempPlots.get(plotId);
        if (pd == null) return;

        double now = GameTimeManager.get().totalSecondsProperty().get();

        switch (activeMode) {
            case SEED -> {
                if (selectedSeedType == null) return;
                if (!pd.isEmpty()) return;

                int seedItemId = toSeedItemId(selectedSeedType);
                if (seedItemId <= 0) return;

                int owned = gameState.getPlayerState().getInventory().getCount(seedItemId);
                int alreadyReserved = reservedSeedUsage(seedItemId);

                if (alreadyReserved >= owned) return;

                FarmRecipe recipe = getFarmRecipe(selectedSeedType);
                pd.seed = selectedSeedType;
                pd.plantedAt = now;
                pd.lastWateredAt = -1;
                pd.accumulatedWaterReduction = 0;
                pd.totalGrowthSeconds = recipe == null ? pd.totalGrowthSeconds : recipe.waitSeconds;
            }
            case WATER -> {
                if (!pd.canWater(now)) return;
                pd.water(now);
            }
            case SICKLE -> {
                if (pd.seed == null || !pd.isReady(now)) return;

                FarmRecipe recipe = getFarmRecipe(pd.seed);
                int cropItemId = (recipe == null) ? toCropItemId(pd.seed) : recipe.outputItemId;
                int successRate = (recipe == null) ? 100 : computeSuccessRate(recipe);
                boolean success = (recipe == null) || ProcessingRules.rollSuccess(successRate);
                int outputAmount = success ? ProcessingRules.computeOutputAmount(recipe == null ? 1 : recipe.baseOutputAmount, successRate) : 0;
                if (cropItemId > 0 && outputAmount > 0) {
                    pendingHarvests.merge(cropItemId, outputAmount, Integer::sum);
                }

                pd.seed = null;
                pd.plantedAt = -1;
                pd.lastWateredAt = -1;
                pd.accumulatedWaterReduction = 0;
            }
            case NONE -> {
                return;
            }
        }

        refreshPlots();
        refreshPlotHighlights();
        populateSeedList();
    }

    private void populateSeedList() {
        seedSlotMapping.clear();

        List<Farm.SeedType> seeds = availableSeedsFromInventory();
        Inventory inv = gameState.getPlayerState().getInventory();

        if (!seeds.isEmpty() && (selectedSeedType == null || !seeds.contains(selectedSeedType))) {
            selectedSeedType = seeds.get(0);
        }
        if (seeds.isEmpty()) {
            selectedSeedType = null;
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

            Farm.SeedType seed = seeds.get(i);
            int itemId = toSeedItemId(seed);
            int qty = inv.getCount(itemId) - reservedSeedUsage(itemId);
            if (qty < 0) qty = 0;

            img.setImage(loadSeedIcon(seed));
            img.setVisible(true);
            img.setOpacity(seed == selectedSeedType ? 1.0 : 0.75);
            img.setStyle(seed == selectedSeedType
                    ? "-fx-effect: dropshadow(gaussian, yellow, 12, 0.6, 0, 0);"
                    : "");

            seedSlotMapping.put(img.getId(), seed);

            if (lbl != null) {
                lbl.setText(seed.displayName + " x" + qty);
            }
        }

        refreshSelectedSeedPreview();
    }

    private void refreshSelectedSeedPreview() {
        if (SeedsImg == null) return;
        SeedsImg.setImage(selectedSeedType == null ? null : loadSeedIcon(selectedSeedType));
    }

    private void refreshPlots() {
        double now = GameTimeManager.get().totalSecondsProperty().get();

        for (Map.Entry<String, ImageView> entry : plotViews.entrySet()) {
            String plotId = entry.getKey();
            ImageView view = entry.getValue();
            TempPlotData pd = tempPlots.get(plotId);

            if (pd == null || pd.seed == null) {
                view.setImage(null);
                view.setVisible(false);
                continue;
            }

            int stage = pd.getStage(now, TOTAL_GROWTH_STAGES);
            String path = String.format("/image/crops/%s_plant%d.png", pd.seed.id, stage);

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
            String plotId = entry.getKey();
            ImageView view = entry.getValue();
            TempPlotData pd = tempPlots.get(plotId);

            boolean highlight = switch (activeMode) {
                case SEED -> selectedSeedType != null && pd != null && pd.isEmpty();
                case WATER -> pd != null && pd.canWater(now);
                case SICKLE -> pd != null && pd.isReady(now);
                case NONE -> false;
            };

            view.setStyle(highlight
                    ? "-fx-effect: dropshadow(gaussian, yellow, 12, 0.6, 0, 0);"
                    : "");
        }
    }

    private List<Farm.SeedType> availableSeedsFromInventory() {
        Inventory inv = gameState.getPlayerState().getInventory();
        List<Farm.SeedType> result = new ArrayList<>();

        for (Farm.SeedType seed : Farm.SeedType.values()) {
            int itemId = toSeedItemId(seed);
            if (itemId <= 0) continue;

            int freeCount = inv.getCount(itemId) - reservedSeedUsage(itemId);
            if (freeCount > 0) {
                result.add(seed);
            }
        }

        return result;
    }

    private int reservedSeedUsage(int seedItemId) {
        int count = 0;

        for (TempPlotData pd : tempPlots.values()) {
            if (pd.seed == null) continue;

            int mappedId = toSeedItemId(pd.seed);
            if (mappedId == seedItemId) {
                count++;
            }
        }

        return count;
    }

    private Map<Integer, Integer> computeSeedConsumption() {
        Map<Integer, Integer> used = new LinkedHashMap<>();
        FarmState realFarm = gameState.getFarmState("home_map");

        for (Map.Entry<String, TempPlotData> entry : tempPlots.entrySet()) {
            String plotId = entry.getKey();
            TempPlotData temp = entry.getValue();
            FarmState.PlotData real = realFarm.getPlot(plotId);

            boolean newlyPlanted = real.seed == null && temp.seed != null;
            if (!newlyPlanted) continue;

            int seedItemId = toSeedItemId(temp.seed);
            if (seedItemId > 0) {
                used.merge(seedItemId, 1, Integer::sum);
            }
        }

        return used;
    }


    private int computePendingFarmStaminaCost() {
        int total = 0;
        FarmState realFarm = gameState.getFarmState("home_map");
        for (Map.Entry<String, TempPlotData> entry : tempPlots.entrySet()) {
            String plotId = entry.getKey();
            TempPlotData temp = entry.getValue();
            FarmState.PlotData real = realFarm.getPlot(plotId);
            boolean newlyPlanted = real.seed == null && temp.seed != null;
            if (!newlyPlanted) continue;
            FarmRecipe recipe = getFarmRecipe(temp.seed);
            if (recipe != null) total += getAdjustedStaminaCost(recipe);
        }
        return total;
    }

    private FarmRecipe getFarmRecipe(Farm.SeedType seedType) {
        if (seedType == null) return null;
        int usedItemId = toSeedItemId(seedType);
        int outputItemId = toCropItemId(seedType);
        if (usedItemId <= 0 || outputItemId <= 0) return null;
        return switch (seedType) {
            case CARROT -> new FarmRecipe(seedType, usedItemId, outputItemId, 45, 88, 3, 2, 1);
            case GRAPE -> new FarmRecipe(seedType, usedItemId, outputItemId, 60, 84, 3, 3, 1);
            case CORN -> new FarmRecipe(seedType, usedItemId, outputItemId, 55, 86, 3, 2, 1);
            case TOMATO -> new FarmRecipe(seedType, usedItemId, outputItemId, 50, 90, 4, 2, 1);
        };
    }

    private int computeSuccessRate(FarmRecipe recipe) {
        if (recipe == null || gameState == null) return 0;
        return ProcessingRules.computeAdjustedSuccessRate(recipe.baseSuccessRate, recipe.dexScaling, gameState.getPlayerState().getDex());
    }

//    private int getAdjustedWaitSeconds(FarmRecipe recipe) {
//        if (recipe == null || gameState == null) return 0;
//        return ProcessingRules.computeAdjustedWaitSeconds(recipe.waitSeconds, recipe.dexScaling, gameState.getPlayerState().getDex());
//    } delete this if it's still unused later

    private int getAdjustedStaminaCost(FarmRecipe recipe) {
        if (recipe == null || gameState == null) return 0;
        return ProcessingRules.computeAdjustedStaminaCost(recipe.staminaCost, recipe.dexScaling, gameState.getPlayerState().getDex());
    }

    private int toSeedItemId(Farm.SeedType seedType) {
        String itemName = switch (seedType) {
            case CARROT -> "Carrot Seeds";
            case GRAPE -> "Grape Seeds";
            case CORN -> "Corn Seeds";
            case TOMATO -> "Tomato Seeds";
        };

        item itm = ItemDatabase.getItemByName(itemName);
        return itm == null ? -1 : itm.getItem_id();
    }

    private int toCropItemId(Farm.SeedType seedType) {
        String itemName = switch (seedType) {
            case GRAPE -> "Grape";
            case CARROT -> "Carrot";
            case CORN -> "Corn";
            case TOMATO -> "Tomato";
        };

        item itm = ItemDatabase.getItemByName(itemName);
        return itm == null ? -1 : itm.getItem_id();
    }

    private Image loadSeedIcon(Farm.SeedType seed) {
        String[] candidates = {
            String.format("/image/items/%s.png", seed.id),
            String.format("/image/items/%s_seeds.png", seed.id),
            String.format("/image/crops/%s_plant1.png", seed.id)
        };

        for (String path : candidates) {
            URL url = getClass().getResource(path);
            if (url != null) {
                return new Image(url.toExternalForm());
            }
        }

        return SeedsImg != null ? SeedsImg.getImage() : null;
    }

    private enum ToolMode {
        NONE, SEED, WATER, SICKLE
    }

    private static final class FarmRecipe {
        final Farm.SeedType seedType;
        final int usedItemId;
        final int outputItemId;
        final int waitSeconds;
        final int baseSuccessRate;
        final int dexScaling;
        final int staminaCost;
        final int baseOutputAmount;

        FarmRecipe(Farm.SeedType seedType, int usedItemId, int outputItemId, int waitSeconds, int baseSuccessRate, int dexScaling, int staminaCost, int baseOutputAmount) {
            this.seedType = seedType;
            this.usedItemId = usedItemId;
            this.outputItemId = outputItemId;
            this.waitSeconds = waitSeconds;
            this.baseSuccessRate = baseSuccessRate;
            this.dexScaling = dexScaling;
            this.staminaCost = staminaCost;
            this.baseOutputAmount = Math.max(1, baseOutputAmount);
        }
    }

    private static class TempPlotData {
        Farm.SeedType seed;
        double plantedAt;
        double lastWateredAt;
        double accumulatedWaterReduction;
        double totalGrowthSeconds;
        double waterCooldownSeconds;
        double waterReductionSeconds;

        static TempPlotData from(FarmState.PlotData src) {
            TempPlotData t = new TempPlotData();
            t.seed = src.seed;
            t.plantedAt = src.plantedAt;
            t.lastWateredAt = src.lastWateredAt;
            t.accumulatedWaterReduction = src.accumulatedWaterReduction;
            t.totalGrowthSeconds = src.totalGrowthSeconds;
            t.waterCooldownSeconds = src.waterCooldownSeconds;
            t.waterReductionSeconds = src.waterReductionSeconds;
            return t;
        }

        void applyTo(FarmState.PlotData dst) {
            dst.seed = seed;
            dst.plantedAt = plantedAt;
            dst.lastWateredAt = lastWateredAt;
            dst.accumulatedWaterReduction = accumulatedWaterReduction;
            dst.totalGrowthSeconds = totalGrowthSeconds;
            dst.waterCooldownSeconds = waterCooldownSeconds;
            dst.waterReductionSeconds = waterReductionSeconds;
        }

        boolean isEmpty() {
            return seed == null;
        }

        boolean isReady(double nowSeconds) {
            if (seed == null) return false;
            double effectiveElapsed = Math.max(0, (nowSeconds - plantedAt) + accumulatedWaterReduction);
            return effectiveElapsed >= totalGrowthSeconds;
        }

        boolean canWater(double nowSeconds) {
            if (seed == null) return false;
            if (isReady(nowSeconds)) return false;
            if (lastWateredAt < 0) return true;
            return (nowSeconds - lastWateredAt) >= waterCooldownSeconds;
        }

        void water(double nowSeconds) {
            if (!canWater(nowSeconds)) return;
            lastWateredAt = nowSeconds;
            accumulatedWaterReduction += waterReductionSeconds;
            if (accumulatedWaterReduction > totalGrowthSeconds) {
                accumulatedWaterReduction = totalGrowthSeconds;
            }
        }

        int getStage(double nowSeconds, int totalStages) {
            if (seed == null) return 0;
            if (totalStages <= 1) return 1;

            double effectiveElapsed = Math.max(0, (nowSeconds - plantedAt) + accumulatedWaterReduction);
            double p = totalGrowthSeconds <= 0 ? 1.0 : Math.min(1.0, effectiveElapsed / totalGrowthSeconds);

            int stage = 1 + (int) Math.floor(p * (totalStages - 1));
            if (stage < 1) stage = 1;
            if (stage > totalStages) stage = totalStages;
            return stage;
        }
    }
}