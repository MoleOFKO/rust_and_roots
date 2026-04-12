package model;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public class Farm {

    private final Group root;
    private final Rectangle detectionArea;
    private final Button actionButton;
    private final Map<String, ImageView> plotViews = new LinkedHashMap<>();
    private final Map<String, String> renderedPlotStages = new LinkedHashMap<>();
    private final FarmState state;

    private Runnable onInteract;

    private int totalStages = FarmGrowthRules.TOTAL_GROWTH_STAGES;
    private String resourcePattern = "/image/crops/%s_plant%d.png";

    public Farm(Group root, Rectangle detectionArea, Button actionButton, FarmState state) {
        this.root = Objects.requireNonNull(root);
        this.detectionArea = Objects.requireNonNull(detectionArea);
        this.actionButton = Objects.requireNonNull(actionButton);
        this.state = Objects.requireNonNull(state);

        actionButton.setDisable(true);
        actionButton.setOnAction(e -> {
            if (actionButton.isDisable()) return;
            if (onInteract != null) onInteract.run();
        });
    }

    public void addPlot(String plotId, ImageView plotImageView) {
        plotViews.put(plotId, plotImageView);
        renderedPlotStages.remove(plotId);
        plotImageView.setImage(null);
        plotImageView.setVisible(false);
        state.getPlot(plotId);
    }

    public void setOnInteract(Runnable onInteract) {
        this.onInteract = onInteract;
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
                renderedPlotStages.remove(plotId);
                view.setImage(null);
                view.setVisible(false);
                continue;
            }

            int stages = ItemDatabase.resolveMaxGrowthPhasesForSeed(pd.seedItemId == null ? -1 : pd.seedItemId);
            int stage = pd.getStage(nowSeconds, stages);
            String spriteKey = resolveSpriteKey(pd);
            showStage(plotId, view, spriteKey, stage);
        }
    }

    private String resolveSpriteKey(FarmState.PlotData pd) {
        if (pd == null) return null;
        if (pd.cropSpriteKey != null && !pd.cropSpriteKey.isBlank()) return pd.cropSpriteKey;
        if (pd.seedItemId != null && pd.seedItemId > 0) {
            return ItemDatabase.buildCropSpriteKeyForSeed(pd.seedItemId);
        }
        return null;
    }

    private void showStage(String plotId, ImageView view, String spriteKey, int stageNumber1Based) {
        if (spriteKey == null || spriteKey.isBlank()) {
            renderedPlotStages.remove(plotId);
            view.setImage(null);
            view.setVisible(false);
            return;
        }

        int imageStageNumber = Math.max(0, stageNumber1Based - 1);

        String stageKey = spriteKey + "#" + stageNumber1Based;
        if (stageKey.equals(renderedPlotStages.get(plotId))) {
            view.setVisible(imageStageNumber > 0);
            return;
        }

        if (imageStageNumber <= 0) {
            view.setImage(null);
            view.setVisible(false);
            renderedPlotStages.put(plotId, stageKey);
            return;
        }

        String path = String.format(resourcePattern, spriteKey, imageStageNumber);
        URL url = getClass().getResource(path);
        if (url == null) {
            renderedPlotStages.remove(plotId);
            view.setImage(null);
            view.setVisible(false);
            return;
        }
        view.setImage(new Image(url.toExternalForm()));
        view.setVisible(true);
        renderedPlotStages.put(plotId, stageKey);
    }
}
