package model;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private final Map<String, ImageView> plotViews = new LinkedHashMap<>();
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
        state.getPlot(plotId);
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

    public void update(double nowSeconds) {
        for (Map.Entry<String, ImageView> e : plotViews.entrySet()) {
            String plotId = e.getKey();
            ImageView view = e.getValue();
            FarmState.PlotData pd = state.getPlot(plotId);

            if (pd == null || pd.isEmpty()) {
                view.setImage(null);
                view.setVisible(false);
                continue;
            }

            int stage = pd.getStage(nowSeconds, totalStages);
            String spriteKey = resolveSpriteKey(pd);
            showStage(view, spriteKey, stage);
        }
    }

    private String resolveSpriteKey(FarmState.PlotData pd) {
        if (pd == null) return null;
        if (pd.cropSpriteKey != null && !pd.cropSpriteKey.isBlank()) return pd.cropSpriteKey;
        if (pd.seedItemId != null && pd.seedItemId > 0) {
            return ItemDatabase.buildCropSpriteKeyForSeed(pd.seedItemId);
        }
        return pd.seed == null ? null : pd.seed.id;
    }

    private void openHud() {
        if (plotViews.isEmpty()) return;

        String plotId = choosePlotDialog();
        if (plotId == null) return;

        FarmState.PlotData pd = state.getPlot(plotId);
        double now = game.GameTimeManager.get().totalSecondsProperty().get();

        if (pd.isEmpty()) {
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
            pd.seedItemId = ItemDatabase.resolveSeedItemIdByCropName(seed.displayName);
            pd.seedItemName = pd.seedItemId != null && pd.seedItemId > 0 ? ItemDatabase.getItemName(pd.seedItemId) : (seed.displayName + " Seed");
            pd.cropItemName = seed.displayName;
            pd.cropSpriteKey = ItemDatabase.buildCropSpriteKeyForSeed(pd.seedItemId == null ? -1 : pd.seedItemId);
            pd.plantedAt = nowSeconds;
            pd.lastWateredAt = -1;
            pd.accumulatedWaterReduction = 0;

            ImageView view = plotViews.get(plotId);
            if (view != null) {
                showStage(view, resolveSpriteKey(pd), 1);
            }
        });
    }

    private void waterPlot(String plotId, FarmState.PlotData pd, double nowSeconds) {
        if (!pd.canWater(nowSeconds)) return;
        pd.water(nowSeconds);
        ImageView view = plotViews.get(plotId);
        if (view != null) {
            int stage = pd.getStage(nowSeconds, totalStages);
            showStage(view, resolveSpriteKey(pd), stage);
        }
    }

    private void reapPlot(String plotId, FarmState.PlotData pd) {
        if (pd.seed != null && onHarvest != null) {
            onHarvest.accept(pd.seed, 1);
        }
        pd.clearPlant();

        ImageView view = plotViews.get(plotId);
        if (view != null) {
            view.setImage(null);
            view.setVisible(false);
        }
    }

    private void showStage(ImageView view, String spriteKey, int stageNumber1Based) {
        if (spriteKey == null || spriteKey.isBlank()) {
            view.setImage(null);
            view.setVisible(false);
            return;
        }
        String path = String.format(resourcePattern, spriteKey, stageNumber1Based);
        URL url = getClass().getResource(path);
        if (url == null) {
            view.setImage(null);
            view.setVisible(false);
            return;
        }
        view.setImage(new Image(url.toExternalForm()));
        view.setVisible(true);
    }

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
