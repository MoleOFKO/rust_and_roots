/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
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
import model.NodeCollectors;
import util.SceneManager;
import util.SpawnTag;
import model.ItemDatabase;
import model.item;

/**
 * FXML Controller class
 *
 * @author ASUS
 */
public class Farm_tableController implements Initializable, SceneController {

    private GameState gameState;

    private ToolMode activeMode = ToolMode.NONE;
    private Farm.SeedType selectedSeedType;

    private final Map<String, ImageView> plotViews = new LinkedHashMap<>();
    private final Map<String, TempPlotData> tempPlots = new LinkedHashMap<>();
    private final Map<Integer, Integer> pendingHarvests = new LinkedHashMap<>();

    @FXML
    private StackPane view;
    @FXML
    private Pane world;
    @FXML
    private ImageView SeedsImg;
    @FXML
    private ImageView WaterCanImg;
    @FXML
    private ImageView SickleImg;
    @FXML
    private Group farm;
    @FXML
    private ImageView farm_img1;
    @FXML
    private ImageView farm_img2;
    @FXML
    private ImageView farm_img3;
    @FXML
    private ImageView farm_img4;
    @FXML
    private ImageView farm_img5;
    @FXML
    private ImageView farm_img6;
    @FXML
    private ImageView farm_img7;
    @FXML
    private ImageView farm_img8;
    @FXML
    private ImageView farm_img9;
    @FXML
    private ImageView farm_img10;
    @FXML
    private ImageView farm_img11;
    @FXML
    private ImageView farm_img12;
    @FXML
    private ImageView farm_img13;
    @FXML
    private ImageView farm_img14;
    @FXML
    private ImageView farm_img15;
    @FXML
    private ImageView farm_img16;
    @FXML
    private ImageView farm_img17;
    @FXML
    private ImageView farm_img18;
    @FXML
    private ImageView farm_img19;
    @FXML
    private ImageView farm_img20;
    @FXML
    private ImageView farm_img21;
    @FXML
    private ImageView farm_img22;
    @FXML
    private ImageView farm_img23;
    @FXML
    private ImageView farm_img24;
    @FXML
    private ImageView farm_img25;
    @FXML
    private ImageView farm_img26;
    @FXML
    private ImageView farm_img27;
    @FXML
    private ImageView farm_img28;
    @FXML
    private Pane seedList;
    @FXML
    private ImageView inyimg1;
    @FXML
    private Label invlbl1;
    @FXML
    private Button btnConfirm;
    @FXML
    private Button btnBack;
    @FXML
    private ImageView inyimg2;
    @FXML
    private Label invlbl2;
    @FXML
    private ImageView inyimg3;
    @FXML
    private Label invlbl3;
    @FXML
    private ImageView inyimg4;
    @FXML
    private Label invlbl4;
    @FXML
    private ImageView inyimg5;
    @FXML
    private Label invlbl5;
    @FXML
    private ImageView inyimg6;
    @FXML
    private Label invlbl6;
    @FXML
    private ImageView inyimg7;
    @FXML
    private Label invlbl7;
    @FXML
    private ImageView inyimg8;
    @FXML
    private Label invlbl8;
    @FXML
    private ImageView inyimg9;
    @FXML
    private Label invlbl9;
    @FXML
    private ImageView inyimg10;
    @FXML
    private Label invlbl10;
    @FXML
    private ImageView inyimg11;
    @FXML
    private Label invlbl11;
    @FXML
    private ImageView inyimg12;
    @FXML
    private Label invlbl12;
    @FXML
    private ImageView inyimg13;
    @FXML
    private Label invlbl13;
    @FXML
    private ImageView inyimg14;
    @FXML
    private Label invlbl14;
    @FXML
    private ImageView inyimg15;
    @FXML
    private Label invlbl15;
    @FXML
    private ImageView inyimg16;
    @FXML
    private Label invlbl16;
    @FXML
    private ImageView inyimg17;
    @FXML
    private Label invlbl17;
    @FXML
    private ImageView inyimg18;
    @FXML
    private Label invlbl18;
    @FXML
    private ImageView inyimg19;
    @FXML
    private Label invlbl19;
    @FXML
    private ImageView inyimg20;
    @FXML
    private Label invlbl20;

    @Override
    public void init(GameState gameState) {
        this.gameState = gameState;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (gameState == null) gameState = GameState.get();

            ItemDatabase.loadItems();

            collectPlots();
            loadTempFarmState();
            bindPlotClicks();
            populateSeedList();
            seedList.setVisible(false);
            seedList.setManaged(false);
            seedList.setDisable(true);
            refreshPlots();
            refreshToolHighlight();
            refreshPlotHighlights();
    }
    
    private void collectPlots() {
        List<ImageView> plots = NodeCollectors.byTypeAndPrefix(farm, ImageView.class, "farm_img");
        NodeCollectors.sortByIdNumber(plots, "farm_img");

        for (ImageView img : plots) {
            int n = NodeCollectors.extractNumber(img.getId(), "farm_img");
            plotViews.put("Plot " + n, img);
        }
    }
    
    private int toSeedItemId(Farm.SeedType seedType) {
        String itemName = switch (seedType) {
            case CARROT -> "Carrot Seeds";
            case GRAPE -> "Grape Seeds";
            case CORN -> "Corn Seeds";
            case TOMATO -> "Tomato Seeds";
        };

        item itm = ItemDatabase.getItemByName(itemName);
        return (itm == null) ? -1 : itm.getItem_id();
    }
    
    private List<Farm.SeedType> availableSeedsFromInventory() {
        Inventory inv = gameState.getPlayerState().getInventory();
        List<Farm.SeedType> result = new ArrayList<>();

        for (Farm.SeedType seed : Farm.SeedType.values()) {
            int itemId = toSeedItemId(seed);
            if (itemId > 0 && inv.getCount(itemId) > 0) {
                result.add(seed);
            }
        }
        return result;
    }

    @FXML
    private void handleFarmPlot(MouseEvent event) {
    }

    @FXML
    private void handleBtnSeedsList(MouseEvent event) {
    }

    @FXML
    private void handlebtnWater(MouseEvent event) {
    }

    @FXML
    private void handleBtnReap(MouseEvent event) {
    }

    @FXML
    private void handleinybox(MouseEvent event) {
    }
    
    private enum ToolMode {
        NONE, SEED, WATER, SICKLE
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

        boolean isEmpty() { return seed == null; }

        boolean isReady(double nowSeconds) {
            if (seed == null) return false;
            double effectiveElapsed = (nowSeconds - plantedAt) + accumulatedWaterReduction;
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
    
    private void handleBtnSeedsList(ActionEvent event) {
        activeMode = ToolMode.SEED;
        populateSeedList();
        seedList.setVisible(true);
        seedList.setManaged(true);
        seedList.setDisable(false);
        refreshToolHighlight();
        refreshPlotHighlights();
    }

    private void handlebtnWater(ActionEvent event) {
        activeMode = ToolMode.WATER;
        seedList.setVisible(false);
        seedList.setManaged(false);
        seedList.setDisable(true);
        refreshToolHighlight();
        refreshPlotHighlights();
    }

    private void handleBtnReap(ActionEvent event) {
        activeMode = ToolMode.SICKLE;
        seedList.setVisible(false);
        seedList.setManaged(false);
        seedList.setDisable(true);
        refreshToolHighlight();
        refreshPlotHighlights();
    }
    
    private void bindPlotClicks() {
        plotViews.forEach((plotId, view) -> view.setOnMouseClicked(e -> handlePlotClick(plotId)));
    }

    private void handlePlotClick(String plotId) {
        TempPlotData pd = tempPlots.get(plotId);
        double now = GameTimeManager.get().totalSecondsProperty().get();

        switch (activeMode) {
            case SEED -> {
                if (selectedSeedType == null) return;
                if (!pd.isEmpty()) return;

                int seedItemId = toSeedItemId(selectedSeedType);
                if (seedItemId <= 0) return;

                if (reservedSeedUsage(seedItemId) >= gameState.getPlayerState().getInventory().getCount(seedItemId)) {
                    return;
                }

                pd.seed = selectedSeedType;
                pd.plantedAt = now;
                pd.lastWateredAt = -1;
                pd.accumulatedWaterReduction = 0;
            }
            case WATER -> {
                if (pd.canWater(now)) {
                    pd.water(now);
                }
            }
            case SICKLE -> {
                if (pd.seed != null && pd.isReady(now)) {
                    int cropItemId = toCropItemId(pd.seed);
                    if (cropItemId > 0) {
                        pendingHarvests.merge(cropItemId, 1, Integer::sum);
                    }
                    pd.seed = null;
                    pd.plantedAt = -1;
                    pd.lastWateredAt = -1;
                    pd.accumulatedWaterReduction = 0;
                }
            }
            default -> {
                return;
            }
        }

        refreshPlots();
        refreshPlotHighlights();
    }
    
    @FXML
    private void handleConfirm(ActionEvent event) {
        Inventory inv = gameState.getPlayerState().getInventory();
        FarmState realFarm = gameState.getFarmState("home_map");

        Map<Integer, Integer> seedsToRemove = computeSeedConsumption();
        for (Map.Entry<Integer, Integer> entry : seedsToRemove.entrySet()) {
            if (!inv.removeItem(entry.getKey(), entry.getValue())) {
                return;
            }
        }

        for (Map.Entry<Integer, Integer> entry : pendingHarvests.entrySet()) {
            inv.addItem(entry.getKey(), entry.getValue());
        }

        tempPlots.forEach((plotId, temp) -> {
            FarmState.PlotData real = realFarm.getPlot(plotId);
            temp.applyTo(real);
        });

        String returnFxml = gameState.getReturnFxml();
        SpawnTag returnSpawn = gameState.getReturnSpawn();

        if (returnFxml == null) {
            returnFxml = "/view/home_map.fxml";
            returnSpawn = SpawnTag.FROM_FARM;
        }

        SceneManager.switchScene(returnFxml, returnSpawn);
    }
    
    @FXML
    private void handleBack(ActionEvent event) {
        String returnFxml = gameState.getReturnFxml();
        SpawnTag returnSpawn = gameState.getReturnSpawn();

        if (returnFxml == null) {
            returnFxml = "/view/home_map.fxml";
            returnSpawn = SpawnTag.FROM_FARM;
        }

        SceneManager.switchScene(returnFxml, returnSpawn);
    }
    
    private int toCropItemId(Farm.SeedType seedType) {
        String itemName = switch (seedType) {
            case GRAPE -> "Grape";
            case CARROT -> "Carrot";
            case CORN -> "Corn";
            case TOMATO -> "Tomato";
        };

        item itm = ItemDatabase.getItemByName(itemName);
        return (itm == null) ? -1 : itm.getItem_id();
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

        for (Map.Entry<String, TempPlotData> e : tempPlots.entrySet()) {
            String plotId = e.getKey();
            TempPlotData temp = e.getValue();
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

        for (Map.Entry<String, ImageView> e : plotViews.entrySet()) {
            String plotId = e.getKey();
            ImageView view = e.getValue();
            TempPlotData pd = tempPlots.get(plotId);

            boolean highlight = switch (activeMode) {
                case SEED -> selectedSeedType != null && pd != null && pd.isEmpty();
                case WATER -> pd != null && pd.canWater(now);
                case SICKLE -> pd != null && pd.isReady(now);
                default -> false;
            };

            if (highlight) {
                view.setStyle("-fx-effect: dropshadow(gaussian, yellow, 12, 0.6, 0, 0);"
                        + "-fx-border-color: yellow;"
                        + "-fx-border-width: 2;");
            } else {
                view.setStyle("");
            }
        }
    }
    
    private void refreshPlots() {
        double now = GameTimeManager.get().totalSecondsProperty().get();

        for (Map.Entry<String, ImageView> e : plotViews.entrySet()) {
            String plotId = e.getKey();
            ImageView view = e.getValue();
            TempPlotData pd = tempPlots.get(plotId);

            if (pd == null || pd.seed == null) {
                view.setImage(null);
                view.setVisible(false);
                continue;
            }

            int stage = pd.getStage(now, 3);
            String path = String.format("/image/crops/%s_plant%d.png", pd.seed.id, stage);

            URL url = getClass().getResource(path);
            if (url == null) {
                System.out.println("Image not found: " + path);
                view.setImage(null);
                view.setVisible(false);
                continue;
            }

            view.setImage(new Image(url.toExternalForm()));
            view.setVisible(true);
        }
    }
    
    private void loadTempFarmState() {
        tempPlots.clear();

        FarmState realFarm = gameState.getFarmState("home_map");
        for (String plotId : plotViews.keySet()) {
            FarmState.PlotData src = realFarm.getPlot(plotId);
            tempPlots.put(plotId, TempPlotData.from(src));
        }
    }
    
    private void populateSeedList() {
        List<Farm.SeedType> seeds = availableSeedsFromInventory();

        seedList.getChildren().clear();
        if (seeds.isEmpty()) {
            Label empty = new Label("No seeds in inventory");
            empty.setLayoutX(16);
            empty.setLayoutY(16);
            seedList.getChildren().add(empty);
            selectedSeedType = null;
            refreshSelectedSeedPreview();
            return;
        }

        int startX = 12;
        int startY = 12;
        int gapX = 92;
        int gapY = 88;
        int perRow = 5;

        Inventory inv = gameState.getPlayerState().getInventory();

        for (int i = 0; i < seeds.size(); i++) {
            Farm.SeedType seed = seeds.get(i);
            int row = i / perRow;
            int col = i % perRow;
            double x = startX + col * gapX;
            double y = startY + row * gapY;

            ImageView img = new ImageView(loadSeedIcon(seed));
            img.setFitWidth(55);
            img.setFitHeight(55);
            img.setPreserveRatio(true);
            img.setPickOnBounds(true);
            img.setLayoutX(x);
            img.setLayoutY(y);
            img.setOpacity(seed == selectedSeedType ? 1.0 : 0.75);
            img.setOnMouseClicked(e -> {
                selectedSeedType = seed;
                activeMode = ToolMode.SEED;
                refreshSelectedSeedPreview();
                populateSeedList();
                refreshToolHighlight();
                refreshPlotHighlights();
            });

            int seedItemId = toSeedItemId(seed);
            Label qty = new Label("x" + inv.getCount(seedItemId));
            qty.setLayoutX(x);
            qty.setLayoutY(y + 58);

            Label name = new Label(seed.displayName);
            name.setLayoutX(x);
            name.setLayoutY(y + 72);

            seedList.getChildren().addAll(img, qty, name);
        }

        if (selectedSeedType == null || !seeds.contains(selectedSeedType)) {
            selectedSeedType = seeds.get(0);
        }

        refreshSelectedSeedPreview();
    }

    private void refreshSelectedSeedPreview() {
        if (SeedsImg == null) return;
        if (selectedSeedType == null) return;
        SeedsImg.setImage(loadSeedIcon(selectedSeedType));
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
    
}
