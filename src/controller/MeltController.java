package controller;

import game.GameState;
import game.GameTimeManager;
import game.SceneController;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import model.Inventory;
import model.ItemDatabase;
import model.item;
import util.SceneManager;
import util.SpawnTag;

public class MeltController implements Initializable, SceneController {

    private static final int MAX_INV_SLOTS = 20;

    private GameState gameState;
    private Inventory inventory;
    private final List<ImageView> invImages = new ArrayList<>();
    private final List<Button> invButtons = new ArrayList<>();
    private final List<Label> invLabels = new ArrayList<>();
    private final List<Integer> visibleOreItemIds = new ArrayList<>();

    private Integer selectedOreItemId = null;
    private int selectedOreQuantity = 0;
    private SmeltRule selectedRule = null;
    private AnimationTimer uiTimer;

    @FXML private StackPane view;
    @FXML private Group world;
    @FXML private ImageView inyimg1;
    @FXML private Button invbtn1;
    @FXML private Label invlbl1;
    @FXML private ImageView inyimg2;
    @FXML private Button invbtn2;
    @FXML private Label invlbl2;
    @FXML private ImageView inyimg3;
    @FXML private Button invbtn3;
    @FXML private Label invlbl3;
    @FXML private ImageView inyimg4;
    @FXML private Button invbtn4;
    @FXML private Label invlbl4;
    @FXML private ImageView inyimg5;
    @FXML private Button invbtn5;
    @FXML private Label invlbl5;
    @FXML private ImageView inyimg6;
    @FXML private Button invbtn6;
    @FXML private Label invlbl6;
    @FXML private ImageView inyimg7;
    @FXML private Button invbtn7;
    @FXML private Label invlbl7;
    @FXML private ImageView inyimg8;
    @FXML private Button invbtn8;
    @FXML private Label invlbl8;
    @FXML private ImageView inyimg9;
    @FXML private Button invbtn9;
    @FXML private Label invlbl9;
    @FXML private ImageView inyimg10;
    @FXML private Button invbtn10;
    @FXML private Label invlbl10;
    @FXML private ImageView inyimg11;
    @FXML private Button invbtn11;
    @FXML private Label invlbl11;
    @FXML private ImageView inyimg12;
    @FXML private Button invbtn12;
    @FXML private Label invlbl12;
    @FXML private ImageView inyimg13;
    @FXML private Button invbtn13;
    @FXML private Label invlbl13;
    @FXML private ImageView inyimg14;
    @FXML private Button invbtn14;
    @FXML private Label invlbl14;
    @FXML private ImageView inyimg15;
    @FXML private Button invbtn15;
    @FXML private Label invlbl15;
    @FXML private ImageView inyimg16;
    @FXML private Button invbtn16;
    @FXML private Label invlbl16;
    @FXML private ImageView inyimg17;
    @FXML private Button invbtn17;
    @FXML private Label invlbl17;
    @FXML private ImageView inyimg18;
    @FXML private Button invbtn18;
    @FXML private Label invlbl18;
    @FXML private ImageView inyimg19;
    @FXML private Button invbtn19;
    @FXML private Label invlbl19;
    @FXML private ImageView inyimg20;
    @FXML private Button invbtn20;
    @FXML private Label invlbl20;
    @FXML private ImageView selectedoreimg;
    @FXML private ImageView resultoreimg;
    @FXML private Label smeltmp;
    @FXML private ImageView closeimg;
    @FXML private ImageView smeltimg;
    @FXML private ImageView oreqtyminusimg;
    @FXML private ImageView oreqtyplusimg;
    @FXML private Label selectedoreqtylbl;
    @FXML private Label totalwaitingtimelbl;
    @FXML private Label itemreadylbl;
    @FXML private Label guide;

    @Override
    public void init(GameState gs) {
        this.gameState = gs;
        this.inventory = gs.getPlayerState().getInventory();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (gameState == null) gameState = GameState.get();
        if (inventory == null) inventory = gameState.getPlayerState().getInventory();

        ItemDatabase.loadItems();
        collectInventoryNodes();
        wireInventoryButtons();
        wireStandaloneClicks();
        startUiTimer();
        refreshAll();
    }

    private void collectInventoryNodes() {
        if (!invImages.isEmpty()) return;
        invImages.addAll(Arrays.asList(inyimg1, inyimg2, inyimg3, inyimg4, inyimg5, inyimg6, inyimg7, inyimg8, inyimg9, inyimg10, inyimg11, inyimg12, inyimg13, inyimg14, inyimg15, inyimg16, inyimg17, inyimg18, inyimg19, inyimg20));
        invButtons.addAll(Arrays.asList(invbtn1, invbtn2, invbtn3, invbtn4, invbtn5, invbtn6, invbtn7, invbtn8, invbtn9, invbtn10, invbtn11, invbtn12, invbtn13, invbtn14, invbtn15, invbtn16, invbtn17, invbtn18, invbtn19, invbtn20));
        invLabels.addAll(Arrays.asList(invlbl1, invlbl2, invlbl3, invlbl4, invlbl5, invlbl6, invlbl7, invlbl8, invlbl9, invlbl10, invlbl11, invlbl12, invlbl13, invlbl14, invlbl15, invlbl16, invlbl17, invlbl18, invlbl19, invlbl20));
    }

    private void wireInventoryButtons() {
        for (int i = 0; i < invButtons.size(); i++) {
            final int index = i;
            Button b = invButtons.get(i);
            if (b != null) {
                b.setOnAction(e -> handleInventorySelection(index));
            }
        }
    }

    private void wireStandaloneClicks() {
        if (oreqtyminusimg != null) oreqtyminusimg.setOnMouseClicked(e -> changeSelectedQty(-1));
        if (oreqtyplusimg != null) oreqtyplusimg.setOnMouseClicked(e -> changeSelectedQty(1));
        if (selectedoreimg != null) selectedoreimg.setOnMouseClicked(this::handleselectedoreimgremove);
        if (resultoreimg != null) resultoreimg.setOnMouseClicked(this::handleresultoreimgaction);
        if (smeltimg != null) smeltimg.setOnMouseClicked(this::handlesmeltimgaction);
        if (closeimg != null) closeimg.setOnMouseClicked(this::handlecloseimgaction);
    }

    private void startUiTimer() {
        stopUiTimer();
        uiTimer = new AnimationTimer() {
            @Override public void handle(long now) { refreshDynamicUi(); }
        };
        uiTimer.start();

        if (view != null) {
            view.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene == null) stopUiTimer();
            });
        }
    }

    private void stopUiTimer() {
        if (uiTimer != null) {
            uiTimer.stop();
            uiTimer = null;
        }
    }

    private void refreshAll() {
        refreshInventorySlots();
        refreshSelectionUi();
        refreshDynamicUi();
    }

    private void refreshInventorySlots() {
        visibleOreItemIds.clear();
        for (Integer itemId : inventory.itemsProperty().keySet()) {
            int count = inventory.getCount(itemId);
            if (count <= 0) continue;
            if (selectedOreItemId != null && itemId == selectedOreItemId) continue;
            item itm = ItemDatabase.getItemById(itemId);
            SmeltRule rule = resolveRule(itm);
            if (rule != null) visibleOreItemIds.add(itemId);
        }

        visibleOreItemIds.sort(Comparator.comparing(this::itemNameSafe, String.CASE_INSENSITIVE_ORDER));

        for (int i = 0; i < MAX_INV_SLOTS; i++) {
            ImageView img = invImages.get(i);
            Label lbl = invLabels.get(i);
            Button btn = invButtons.get(i);

            if (i >= visibleOreItemIds.size()) {
                if (img != null) img.setImage(null);
                if (lbl != null) lbl.setText("");
                if (btn != null) {
                    btn.setDisable(true);
                    btn.setText("");
                }
                continue;
            }

            int itemId = visibleOreItemIds.get(i);
            int qty = inventory.getCount(itemId);
            item itm = ItemDatabase.getItemById(itemId);

            if (img != null) {
                img.setImage(ItemDatabase.getImage(itemId));
                boolean selected = selectedOreItemId != null && selectedOreItemId == itemId;
                img.setStyle(selected ? "-fx-effect: dropshadow(gaussian, yellow, 12, 0.6, 0, 0);" : "");
            }
            if (lbl != null) lbl.setText("x" + qty);
            if (btn != null) {
                btn.setDisable(hasActiveSmeltJob());
                btn.setText(itm == null ? "" : itm.getItem_name());
            }
        }
    }

    private void handleInventorySelection(int index) {
        if (hasActiveSmeltJob()) return;
        if (index < 0 || index >= visibleOreItemIds.size()) return;

        int itemId = visibleOreItemIds.get(index);
        int available = inventory.getCount(itemId);
        if (available <= 0) return;

        item itm = ItemDatabase.getItemById(itemId);
        SmeltRule rule = resolveRule(itm);
        if (rule == null) return;

        if (!inventory.removeItem(itemId, 1)) return;

        selectedOreItemId = itemId;
        selectedOreQuantity = 1;
        selectedRule = rule;
        refreshAll();
    }

    private void changeSelectedQty(int delta) {
        if (delta == 0 || hasActiveSmeltJob() || selectedOreItemId == null) return;

        if (delta > 0) {
            if (inventory.removeItem(selectedOreItemId, delta)) {
                selectedOreQuantity += delta;
            }
        } else {
            int reduce = Math.min(selectedOreQuantity - 1, -delta);
            if (reduce > 0) {
                selectedOreQuantity -= reduce;
                inventory.addItem(selectedOreItemId, reduce);
            }
        }

        refreshAll();
    }

    @FXML
    private void handleselectedoreimgremove(MouseEvent event) {
        if (hasActiveSmeltJob() || selectedOreItemId == null || selectedOreQuantity <= 0) return;
        gameState.cancelSmeltSelectionBackToInventory(selectedOreItemId, selectedOreQuantity);
        clearSelection();
        refreshAll();
    }

    @FXML
    private void handleresultoreimgaction(MouseEvent event) {
        double now = GameTimeManager.get().totalSecondsProperty().get();
        int ready = gameState.getReadySmeltCount(now);
        if (ready <= 0) return;
        gameState.collectReadySmeltedItems(now);
        refreshAll();
    }

    @FXML
    private void handlecloseimgaction(MouseEvent event) {
        if (selectedOreItemId != null && !hasActiveSmeltJob()) {
            gameState.cancelSmeltSelectionBackToInventory(selectedOreItemId, selectedOreQuantity);
            clearSelection();
        }
        stopUiTimer();
        String back = gameState.getReturnFxml();
        if (back == null) back = "/view/home_interior.fxml";
        SceneManager.switchScene(back, SpawnTag.FROM_MELT);
    }

    @FXML
    private void handlesmeltimgaction(MouseEvent event) {
        if (isSmeltStartBlocked()) return;

        double now = GameTimeManager.get().totalSecondsProperty().get();
        int totalCost = getSelectedTotalCost();
        if (!gameState.spendSmeltPoints(totalCost, now)) {
            refreshDynamicUi();
            return;
        }

        int staminaCost = getSelectedTotalStaminaCost();
        if (gameState.getPlayerState().getStamina() < staminaCost) {
            refreshDynamicUi();
            return;
        }
        gameState.getPlayerState().consumeStamina(staminaCost);

        int successRate = computeSuccessRate(selectedRule);
        boolean success = ProcessingRules.rollSuccess(successRate);
        int outputQty = success ? ProcessingRules.computeOutputAmount(selectedOreQuantity, successRate) : 0;
        if (outputQty > 0) {
            gameState.startSmeltJob(selectedOreItemId, selectedRule.resultItemId, outputQty, selectedRule.costPerOre, getAdjustedSecondsPerOre(selectedRule), now);
        }
        clearSelection();
        refreshAll();
    }

    private void refreshSelectionUi() {
        boolean locked = hasActiveSmeltJob();

        if (selectedoreimg != null) {
            selectedoreimg.setImage(selectedOreItemId == null ? null : ItemDatabase.getImage(selectedOreItemId));
            selectedoreimg.setOpacity(selectedOreItemId == null ? 0.0 : (locked ? 0.9 : 1.0));
            selectedoreimg.setStyle(locked ? "-fx-effect: dropshadow(gaussian, orange, 14, 0.7, 0, 0);" : "");
            selectedoreimg.setDisable(locked || selectedOreItemId == null);
        }

        if (selectedoreqtylbl != null) {
            if (hasActiveSmeltJob()) {
                GameState.ActiveSmeltJob job = gameState.getActiveSmeltJob();
                selectedoreqtylbl.setText("x" + job.getQuantity());
            } else {
                selectedoreqtylbl.setText(selectedOreQuantity > 0 ? ("x" + selectedOreQuantity) : "[ Qty ]");
            }
        }

        boolean canEditQty = !locked && selectedOreItemId != null;
        if (oreqtyminusimg != null) {
            oreqtyminusimg.setDisable(!canEditQty || selectedOreQuantity <= 1);
            oreqtyminusimg.setOpacity((!canEditQty || selectedOreQuantity <= 1) ? 0.45 : 1.0);
        }
        if (oreqtyplusimg != null) {
            boolean noMoreOwned = inventory.getCount(selectedOreItemId == null ? -1 : selectedOreItemId) <= 0;
            oreqtyplusimg.setDisable(!canEditQty || noMoreOwned);
            oreqtyplusimg.setOpacity((!canEditQty || noMoreOwned) ? 0.45 : 1.0);
        }
    }

    private void refreshDynamicUi() {
        double now = GameTimeManager.get().totalSecondsProperty().get();
        GameState.ActiveSmeltJob job = gameState.getActiveSmeltJob();
        int currentP = gameState.getCurrentSmeltPoints(now);

        if (job != null) {
            if (selectedoreimg != null) selectedoreimg.setImage(ItemDatabase.getImage(job.getSourceItemId()));

            int ready = job.getReadyCount(now);
            int remaining = job.getRemainingCount(now);
            double nextReadyAt = job.getNextReadyAtSeconds(now);
            int wait = remaining <= 0 ? 0 : (int) Math.ceil(Math.max(0, nextReadyAt - now));

            if (totalwaitingtimelbl != null) {
                totalwaitingtimelbl.setText("Waiting time: " + wait + "s  [" + ready + "/" + job.getQuantity() + "]");
            }

            if (resultoreimg != null) {
                resultoreimg.setImage(ready > 0 ? ItemDatabase.getImage(job.getResultItemId()) : null);
                resultoreimg.setOpacity(ready > 0 ? 1.0 : 0.0);
                resultoreimg.setStyle(ready > 0 ? "-fx-effect: dropshadow(gaussian, yellow, 16, 0.8, 0, 0);" : "");
                resultoreimg.setDisable(ready <= 0);
            }

            if (itemreadylbl != null) {
                itemreadylbl.setVisible(ready > 0);
                itemreadylbl.setManaged(ready > 0);
                itemreadylbl.setText(ready > 0 ? ("Item Ready... x" + ready) : "Item Ready...");
            }
            if (guide != null) {
                guide.setVisible(ready > 0);
                guide.setManaged(ready > 0);
            }

            if (smeltmp != null) {
                int queuedCost = job.getCostPerOre() * job.getQuantity();
                smeltmp.setText("P : " + currentP + "(-" + queuedCost + ")");
            }
            if (selectedoreqtylbl != null) selectedoreqtylbl.setText("x" + job.getQuantity());
            updateSmeltButtonState(now);
            return;
        }

        int pendingCost = getSelectedTotalCost();
        if (selectedRule != null && selectedOreQuantity > 0) {
            int totalSeconds = (int) Math.ceil(getAdjustedSecondsPerOre(selectedRule) * selectedOreQuantity);
            if (totalwaitingtimelbl != null) totalwaitingtimelbl.setText("Waiting time: " + totalSeconds + "s");
            if (smeltmp != null) smeltmp.setText("P : " + currentP + "(-" + pendingCost + ")  SP:-" + getSelectedTotalStaminaCost() + "  SR:" + computeSuccessRate(selectedRule) + "%");
        } else {
            if (totalwaitingtimelbl != null) totalwaitingtimelbl.setText("Waiting time:");
            if (smeltmp != null) smeltmp.setText("P : " + currentP + "(-0)");
        }

        if (resultoreimg != null) {
            resultoreimg.setImage(null);
            resultoreimg.setOpacity(0.0);
            resultoreimg.setStyle("");
            resultoreimg.setDisable(true);
        }
        if (itemreadylbl != null) { itemreadylbl.setVisible(false); itemreadylbl.setManaged(false); }
        if (guide != null) { guide.setVisible(false); guide.setManaged(false); }

        updateSmeltButtonState(now);
    }

    private void updateSmeltButtonState(double now) {
        boolean blocked = isSmeltStartBlocked(now);
        if (smeltimg != null) {
            smeltimg.setDisable(blocked);
            smeltimg.setOpacity(blocked ? 0.45 : 1.0);
            smeltimg.setStyle(blocked
                ? "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 10, 0.35, 0, 0);"
                : "-fx-effect: dropshadow(gaussian, orange, 14, 0.55, 0, 0);");
        }
    }

    private boolean isSmeltStartBlocked() {
        double now = GameTimeManager.get().totalSecondsProperty().get();
        return isSmeltStartBlocked(now);
    }

    private boolean isSmeltStartBlocked(double now) {
        if (hasActiveSmeltJob()) return true;
        if (selectedOreItemId == null || selectedRule == null || selectedOreQuantity <= 0) return true;
        int totalCost = getSelectedTotalCost();
        return !gameState.canAffordSmeltPoints(totalCost, now)
            || gameState.getPlayerState().getStamina() < getSelectedTotalStaminaCost();
    }

    private int getSelectedTotalCost() {
        return (selectedRule == null || selectedOreQuantity <= 0) ? 0 : selectedRule.costPerOre * selectedOreQuantity;
    }

    private int getSelectedTotalStaminaCost() {
        return (selectedRule == null || selectedOreQuantity <= 0) ? 0 : getAdjustedStaminaCost(selectedRule) * selectedOreQuantity;
    }

    private boolean hasActiveSmeltJob() {
        return gameState != null && gameState.hasActiveSmeltJob();
    }

    private void clearSelection() {
        selectedOreItemId = null;
        selectedOreQuantity = 0;
        selectedRule = null;
    }

    private String itemNameSafe(int itemId) {
        item itm = ItemDatabase.getItemById(itemId);
        return itm == null ? ("Item " + itemId) : itm.getItem_name();
    }

    private int computeSuccessRate(SmeltRule rule) {
        if (rule == null || gameState == null) return 0;
        return ProcessingRules.computeAdjustedSuccessRate(rule.baseSuccessRate, rule.dexScaling, gameState.getPlayerState().getDex());
    }

    private int getAdjustedStaminaCost(SmeltRule rule) {
        if (rule == null || gameState == null) return 0;
        return ProcessingRules.computeAdjustedStaminaCost(rule.staminaCost, rule.dexScaling, gameState.getPlayerState().getDex());
    }

    private double getAdjustedSecondsPerOre(SmeltRule rule) {
        if (rule == null || gameState == null) return 1.0;
        return ProcessingRules.computeAdjustedWaitSeconds((int)Math.ceil(rule.secondsPerOre), rule.dexScaling, gameState.getPlayerState().getDex());
    }

    private SmeltRule resolveRule(item oreItem) {
        if (oreItem == null) return null;
        int sourceItemId = oreItem.getItem_id();

        item copperOre = ItemDatabase.getItemByName("Copper Ore");
        item copperIngot = ItemDatabase.getItemByName("Copper Ingot");

        item ironOre = ItemDatabase.getItemByName("Iron Ore");
        item ironIngot = ItemDatabase.getItemByName("Iron Ingot");

        item silverOre = ItemDatabase.getItemByName("Silver Ore");
        item silverIngot = ItemDatabase.getItemByName("Silver Ingot");

        item goldOre = ItemDatabase.getItemByName("Gold Ore");
        item goldIngot = ItemDatabase.getItemByName("Gold Ingot");

        item coalOre = ItemDatabase.getItemByName("Coal");
        item coalBrick = ItemDatabase.getItemByName("Coal Brick");

        if (copperOre != null && copperIngot != null && sourceItemId == copperOre.getItem_id()) {
            return new SmeltRule(copperOre.getItem_id(), copperIngot.getItem_id(), 3, 10, 90, 2, 2);
        }
        if (ironOre != null && ironIngot != null && sourceItemId == ironOre.getItem_id()) {
            return new SmeltRule(ironOre.getItem_id(), ironIngot.getItem_id(), 4, 12, 88, 3, 3);
        }
        if (silverOre != null && silverIngot != null && sourceItemId == silverOre.getItem_id()) {
            return new SmeltRule(silverOre.getItem_id(), silverIngot.getItem_id(), 6, 16, 82, 3, 4);
        }
        if (goldOre != null && goldIngot != null && sourceItemId == goldOre.getItem_id()) {
            return new SmeltRule(goldOre.getItem_id(), goldIngot.getItem_id(), 8, 20, 76, 4, 5);
        }
        if (coalOre != null && coalBrick != null && sourceItemId == coalOre.getItem_id()) {
            return new SmeltRule(coalOre.getItem_id(), coalBrick.getItem_id(), 1, 6, 96, 2, 1);
        }

        return null;
    }
    
    private static final class SmeltRule {
        final int sourceItemId;
        final int resultItemId;
        final int costPerOre;
        final double secondsPerOre;
        final int baseSuccessRate;
        final int dexScaling;
        final int staminaCost;

        SmeltRule(int sourceItemId, int resultItemId, int costPerOre, double secondsPerOre, int baseSuccessRate, int dexScaling, int staminaCost) {
            this.sourceItemId = sourceItemId;
            this.resultItemId = resultItemId;
            this.costPerOre = Math.max(0, costPerOre);
            this.secondsPerOre = Math.max(1, secondsPerOre);
            this.baseSuccessRate = Math.max(0, baseSuccessRate);
            this.dexScaling = Math.max(0, dexScaling);
            this.staminaCost = Math.max(0, staminaCost);
        }
    }
}
