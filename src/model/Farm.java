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

/**
 * Farm:
 * - Has a detection area (Rectangle)
 * - Enables button when player hitbox intersects detection area
 * - Button flow: Plant -> Water -> Water -> Reap (default 3 stages)
 *
 * Usage:
 *   Farm farm = new Farm(farmGroup, farmImg, farmDetectionRect, farmButton);
 *   farm.setAvailableSeeds(SeedType.CARROT, SeedType.CORN, SeedType.TOMATO);
 *   // in animation loop:
 *   farm.updatePlayerBounds(playerHitbox.localToScene(playerHitbox.getBoundsInLocal()));
 */
public class Farm {

    // --- Required UI nodes (from FXML) ---
    private final Group root;
    private final Rectangle detectionArea;
    private final Button actionButton;

    //All plots (each plot uses its own ImageView)
    private final Map<String, Plot> plots=new LinkedHashMap<>();
    
    // --- Config ---
    private List<SeedType> availableSeeds = new ArrayList<>();
    
    private java.util.function.BiConsumer<SeedType, Integer> onHarvest;

   
    private int totalStages = 3; //Default = 3 (plant shows stage1, then 2 waters to reach stage3, then reap)
    private String resourcePattern = "/image/crops/%s_plant%d.png";

    public Farm(Group root, Rectangle detectionArea, Button actionButton) {
        this.root = Objects.requireNonNull(root);
        this.detectionArea = Objects.requireNonNull(detectionArea);
        this.actionButton = Objects.requireNonNull(actionButton);

        actionButton.setDisable(true);

        actionButton.setOnAction(e -> openHud());
    }

    // Public API

    public void addPlot(String plotName, ImageView plotImageView){
        plots.put(plotName,new Plot(plotName, plotImageView));
        //Start empty
        plotImageView.setImage(null);
        plotImageView.setVisible(false);
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

    /**
     * Call this repeatedly (e.g. in AnimationTimer) to enable/disable the button
     * based on player bounds intersecting the detection area.
     */
    public void updatePlayerBounds(Bounds playerBoundsScene) {
        Bounds detectBoundsScene = detectionArea.localToScene(detectionArea.getBoundsInLocal());
        boolean inside = playerBoundsScene.intersects(detectBoundsScene);
        actionButton.setDisable(!inside);
    }

    // HUD flow (simple dialogs for now)
    
    private void openHud(){
        if(plots.isEmpty()) return;
        
        //1)choose plot
        Plot chosenPlot=choosePlotDialog();
        if(chosenPlot==null) return;
        
        //2) decide action based on plot state
        switch(chosenPlot.state){
            case EMPTY -> plantOnPlot(chosenPlot);
            case NEED_WATER -> waterPlot(chosenPlot);
            case READY_TO_REAP -> reapPlot(chosenPlot);
        }
        
    }
    
    private Plot choosePlotDialog(){
        List<Plot> plotList=new ArrayList<>(plots.values());
        ChoiceDialog<Plot> dialog=new ChoiceDialog<>(plotList.get(0), plotList);
        dialog.setTitle("Farm");
        dialog.setHeaderText("Select farmland");
        dialog.setContentText("Plot:");
        
        return dialog.showAndWait().orElse(null);
    }
    
    private void plantOnPlot(Plot plot){
        if(availableSeeds.isEmpty())return;
        
        //Choose seed
        ChoiceDialog<SeedType> dialog=new ChoiceDialog<>(availableSeeds.get(0), availableSeeds);
        dialog.setTitle("Plant");
        dialog.setHeaderText("Choose seed to plant on "+plot.name);
        dialog.setContentText("Seed:");
        dialog.showAndWait().ifPresent(seed -> {
            plot.plantedSeed = seed;
            plot.stageIndex=0;
            plot.state=(totalStages <=1)? PlotState.READY_TO_REAP : PlotState.NEED_WATER;
            showStage(plot, plot.stageIndex +1);
        });
    }
    
    private void waterPlot(Plot plot){
        if(plot.plantedSeed == null) return;
        
        if(plot.stageIndex < totalStages -1){
            plot.stageIndex++;
            showStage(plot, plot.stageIndex +1);
        }
        if(plot.stageIndex>= totalStages-1){
            plot.state= PlotState.READY_TO_REAP;
        }else{
            plot.state=PlotState.NEED_WATER;
        }
    }
    
    private void reapPlot(Plot plot){
        // Reward: 1 crop (adjust amount as you like)
        if (plot.plantedSeed != null && onHarvest != null) {
            onHarvest.accept(plot.plantedSeed, 1);
        }

        plot.plantedSeed = null;
        plot.stageIndex=-1;
        plot.state=PlotState.EMPTY;
        
        plot.view.setImage(null);
        plot.view.setVisible(false);
    }
    
    // ------------------------
    // Image helpers
    // ------------------------

    private void showStage(Plot plot,int stageNumber1Based) {
        String seedId = plot.plantedSeed.id;

        String path = String.format(resourcePattern, seedId, stageNumber1Based);
        
        URL url=getClass().getResource(path);
        if(url==null){
            System.out.println("Image not found: "+path);
            return;
        }
        
        Image img = new Image(getClass().getResource(path).toExternalForm());
        plot.view.setImage(img);
        plot.view.setVisible(true);
    }

    // ------------------------
    // Types
    // ------------------------

    private enum PlotState {
        EMPTY,
        NEED_WATER,
        READY_TO_REAP
    }

    private static class Plot{
        final String name;
        final ImageView view;
        
        PlotState state = PlotState.EMPTY;
        SeedType plantedSeed=null;
        int stageIndex=-1;
        Plot(String name, ImageView view){
            this.name=name;
            this.view=view;
        }

        @Override
        public String toString() {
        //This text appears in the plot ChoiceDialog list
            return name+" ("+state+")";
        }
    }
    
    /**
     * Add your seeds here. "id" should match your image naming.
     * Example: CARROT uses carrot_stage1.png, carrot_stage2.png, carrot_stage3.png
     */
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
