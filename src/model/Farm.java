package model;

import java.net.URL;
import java.util.*;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public class Farm {

    private final Group root;
    private final Rectangle detectionArea;
    private final Button actionButton;

    // UI views only
    private final Map<String, ImageView> plotViews = new LinkedHashMap<>();

    // persistent data
    private final FarmState state;

    private List<SeedType> availableSeeds = new ArrayList<>();
    private java.util.function.BiConsumer<SeedType, Integer> onHarvest;

    private int totalStages = 3;
    private String resourcePattern = "/image/crops/%s_plant%d.png";

    public Farm(Group root, Rectangle detectionArea, Button actionButton, FarmState state) {
        this.root = Objects.requireNonNull(root);
        this.detectionArea = Objects.requireNonNull(detectionArea);
        this.actionButton = Objects.requireNonNull(actionButton);
        this.state = Objects.requireNonNull(state);

        actionButton.setDisable(true);
        actionButton.setOnAction(e -> openHud());
    }

    public void addPlot(String plotId, ImageView plotImageView) {
        plotViews.put(plotId, plotImageView);
        plotImageView.setImage(null);
        plotImageView.setVisible(false);
        state.getPlot(plotId); // ensure exists
    }

    public void setOnHarvest(java.util.function.BiConsumer<SeedType, Integer> onHarvest) {
        this.onHarvest = onHarvest;
    }

    public void setAvailableSeeds(SeedType... seeds) {
        availableSeeds = new ArrayList<>(Arrays.asList(seeds));
    }

    public void setTotalStages(int totalStages) {
        if (totalStages < 1) throw new IllegalArgumentException("totalStages must be >= 1");
        this.totalStages = totalStages;
    }

    public void setResourcePattern(String resourcePattern) {
        this.resourcePattern = Objects.requireNonNull(resourcePattern);
    }

    public void updatePlayerBounds(Bounds playerBoundsScene) {
        Bounds detectBoundsScene = detectionArea.localToScene(detectionArea.getBoundsInLocal());
        boolean inside = playerBoundsScene.intersects(detectBoundsScene);
        actionButton.setDisable(!inside);
    }

    /** Call every frame (or often) to refresh crop visuals based on GameTimeManager seconds */
    public void update(double nowSeconds) {
        for (Map.Entry<String, ImageView> e : plotViews.entrySet()) {
            String plotId = e.getKey();
            ImageView view = e.getValue();
            FarmState.PlotData pd = state.getPlot(plotId);

            if (pd.seed == null) {
                view.setImage(null);
                view.setVisible(false);
                continue;
            }

            int stage = pd.getStage(nowSeconds, totalStages);
            showStage(view, pd.seed.id, stage);
        }
    }

    // ---------------- HUD logic ----------------

    private void openHud() {
        if (plotViews.isEmpty()) return;

        String plotId = choosePlotDialog();
        if (plotId == null) return;

        FarmState.PlotData pd = state.getPlot(plotId);

        double now = game.GameTimeManager.get().totalSecondsProperty().get();

        if (pd.seed == null) {
            plantOnPlot(plotId, pd, now);
        } else if (pd.isReady(now)) {
            reapPlot(plotId, pd);
        } else {
            waterPlot(plotId, pd, now);
        }
    }

    private String choosePlotDialog() {
        List<String> ids = new ArrayList<>(plotViews.keySet());
        ChoiceDialog<String> dialog = new ChoiceDialog<>(ids.get(0), ids);
        dialog.setTitle("Farm");
        dialog.setHeaderText("Select farmland");
        dialog.setContentText("Plot:");
        return dialog.showAndWait().orElse(null);
    }

    private void plantOnPlot(String plotId, FarmState.PlotData pd, double nowSeconds) {
        if (availableSeeds.isEmpty()) return;

        ChoiceDialog<SeedType> dialog = new ChoiceDialog<>(availableSeeds.get(0), availableSeeds);
        dialog.setTitle("Plant");
        dialog.setHeaderText("Choose seed to plant on " + plotId);
        dialog.setContentText("Seed:");

        dialog.showAndWait().ifPresent(seed -> {
            pd.seed = seed;
            pd.plantedAt = nowSeconds;
            pd.lastWateredAt = -1;
            pd.accumulatedWaterReduction = 0;

            // show stage 1 immediately
            ImageView view = plotViews.get(plotId);
            if (view != null) {
                showStage(view, seed.id, 1);
            }
        });
    }

    private void waterPlot(String plotId, FarmState.PlotData pd, double nowSeconds) {
        if (!pd.canWater(nowSeconds)) return;
        pd.water(nowSeconds);

        // update immediate stage after watering
        ImageView view = plotViews.get(plotId);
        if (view != null) {
            int stage = pd.getStage(nowSeconds, totalStages);
            showStage(view, pd.seed.id, stage);
        }
    }

    private void reapPlot(String plotId, FarmState.PlotData pd) {
        if (pd.seed != null && onHarvest != null) {
            onHarvest.accept(pd.seed, 1);
        }
        pd.seed = null;
        pd.plantedAt = -1;
        pd.lastWateredAt = -1;
        pd.accumulatedWaterReduction = 0;

        ImageView view = plotViews.get(plotId);
        if (view != null) {
            view.setImage(null);
            view.setVisible(false);
        }
    }

    // ---------------- Image helper ----------------

    private void showStage(ImageView view, String seedId, int stageNumber1Based) {
        String path = String.format(resourcePattern, seedId, stageNumber1Based);
        URL url = getClass().getResource(path);
        if (url == null) {
            System.out.println("Image not found: " + path);
            return;
        }
        Image img = new Image(url.toExternalForm());
        view.setImage(img);
        view.setVisible(true);
    }

    // ---------------- Seeds ----------------
    public enum SeedType {
        GRAPE("grape", "Grape"),
        CARROT("carrot", "Carrot"),
        CORN("corn", "Corn"),
        TOMATO("tomato", "Tomato");

        public final String id;
        public final String displayName;

        SeedType(String id, String displayName) {
            this.id = id;
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}