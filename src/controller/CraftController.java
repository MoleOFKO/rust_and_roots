package controller;

import game.GameState;
import game.GameTimeManager;
import game.SceneController;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
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

public class CraftController implements Initializable, SceneController {
    private static final int MAX_RECIPE_SLOTS = 20;
        private static final int DEFAULT_WAIT_SECONDS = 40;

    private GameState gameState;
    private Inventory inventory;

    private final List<ImageView> recipeImages = new ArrayList<>();
    private final List<Recipe> unlockedRecipes = new ArrayList<>();
    private Recipe selectedRecipe;
    private AnimationTimer craftUiTimer;

    @FXML private StackPane view;
    @FXML private Group world;
    @FXML private ImageView resultitem;
    @FXML private Group craftrecipe;
    @FXML private ImageView craft;
    @FXML private ImageView craft1;
    @FXML private ImageView craft2;
    @FXML private ImageView craft3;
    @FXML private ImageView craft4;
    @FXML private ImageView craft5;
    @FXML private ImageView craft6;
    @FXML private ImageView craft7;
    @FXML private ImageView craft8;
    @FXML private ImageView craft9;
    @FXML private ImageView craft10;
    @FXML private ImageView craft11;
    @FXML private ImageView craft12;
    @FXML private ImageView craft13;
    @FXML private ImageView craft14;
    @FXML private ImageView craft15;
    @FXML private ImageView craft16;
    @FXML private ImageView craft17;
    @FXML private ImageView craft18;
    @FXML private ImageView craft19;
    @FXML private ImageView ing1;
    @FXML private ImageView ing2;
    @FXML private ImageView ing3;
    @FXML private ImageView ing4;
    @FXML private Label ing1lbl;
    @FXML private Label ing2lbl;
    @FXML private Label ing3lbl;
    @FXML private Label ing4lbl;
    @FXML private Label lblname;
    @FXML private Button btncraftitem;
    @FXML private Label lblwt;
    @FXML private Label lblsr;
    @FXML private Label lbleffect;
    @FXML private Button btnclose;

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
        collectRecipeNodes();
        loadRecipes();
        refreshRecipeSlots();
        setupResultInteraction();
        restoreActiveCraftSelection();
        startUiTimer();
        refreshSelectionDetails();
    }

    private void collectRecipeNodes() {
        if (!recipeImages.isEmpty()) return;
        recipeImages.addAll(Arrays.asList(craft, craft1, craft2, craft3, craft4, craft5, craft6, craft7, craft8, craft9, craft10, craft11, craft12, craft13, craft14, craft15, craft16, craft17, craft18, craft19));
    }

    private void loadRecipes() {
        unlockedRecipes.clear();
        addRecipeIfValid(new RecipeTemplate("rope", "Rope", 35, 88, 3, 6, new IngredientSpec("tree_log", 2), new IngredientSpec("blue_fruit", 1)));
        addRecipeIfValid(new RecipeTemplate("wooden_board", "Wooden Board", 45, 84, 3, 8, new IngredientSpec("tree_log", 3)));
        addRecipeIfValid(new RecipeTemplate("stone_block", "Stone Block", 50, 82, 3, 8, new IngredientSpec("Rock1_5", 2)));
        addRecipeIfValid(new RecipeTemplate("polished_stone", "Polished Stone", 55, 80, 4, 10, new IngredientSpec("Rock2_5", 2), new IngredientSpec("Rock1_5", 1)));
        addRecipeIfValid(new RecipeTemplate("fruit_mix", "Fruit Mix", 30, 90, 2, 5, new IngredientSpec("blue_fruit", 1), new IngredientSpec("red_fruit", 1)));
        if (unlockedRecipes.isEmpty()) {
            addSelfCraftFallback("tree_log", 85, 5);
            addSelfCraftFallback("Rock1_5", 82, 6);
            addSelfCraftFallback("Rock2_5", 80, 7);
            addSelfCraftFallback("blue_fruit", 90, 4);
            addSelfCraftFallback("red_fruit", 90, 4);
        }
        unlockedRecipes.sort(Comparator.comparing(r -> r.name, String.CASE_INSENSITIVE_ORDER));
    }

    private void addRecipeIfValid(RecipeTemplate template) {
        item output = resolveItem(template.outputItemName);
        if (output == null) return;
        List<IngredientRequirement> ingredients = new ArrayList<>();
        for (IngredientSpec spec : template.ingredients) {
            item ingItem = resolveItem(spec.itemName);
            if (ingItem == null) return;
            ingredients.add(new IngredientRequirement(ingItem.getItem_id(), spec.amount));
        }
        unlockedRecipes.add(new Recipe(template.key, output.getItem_id(), output.getItem_name(), template.waitSeconds, template.baseSuccessRate, template.dexScaling, template.staminaCost, ingredients));
    }

    private void addSelfCraftFallback(String itemName, int baseSuccessRate, int staminaCost) {
        item itm = resolveItem(itemName);
        if (itm == null) return;
        unlockedRecipes.add(new Recipe("fallback_" + itm.getItem_id(), itm.getItem_id(), itm.getItem_name(), DEFAULT_WAIT_SECONDS, baseSuccessRate, 2, staminaCost, Collections.singletonList(new IngredientRequirement(itm.getItem_id(), 1))));
    }

    private item resolveItem(String key) {
        if (key == null || key.isBlank()) return null;
        item direct = ItemDatabase.getItemByName(key);
        if (direct != null) return direct;
        String normalized = normalizeKey(key);
        for (item itm : ItemDatabase.getAllItems()) {
            if (itm == null || itm.getItem_name() == null) continue;
            if (normalizeKey(itm.getItem_name()).equals(normalized)) return itm;
            String imageFile = ItemDatabase.getImageFileName(itm.getItem_id());
            if (imageFile != null && normalizeKey(stripExtension(imageFile)).equals(normalized)) return itm;
        }
        return null;
    }

    private String normalizeKey(String value) {
        return value == null ? "" : value.toLowerCase().replace(".png", "").replace("_", "").replace(" ", "").trim();
    }

    private String stripExtension(String value) {
        if (value == null) return "";
        int idx = value.lastIndexOf('.');
        return idx >= 0 ? value.substring(0, idx) : value;
    }

    private int extractItemStat(Object itm, String... methodNames) {
        if (itm == null || methodNames == null) return 0;
        for (String methodName : methodNames) {
            if (methodName == null || methodName.isBlank()) continue;
            try {
                java.lang.reflect.Method method = itm.getClass().getMethod(methodName);
                Object value = method.invoke(itm);
                if (value instanceof Number number) return number.intValue();
            } catch (ReflectiveOperationException ignored) {}
        }
        return 0;
    }

    private int getRecipeHpDelta(Recipe recipe) { return extractItemStat(ItemDatabase.getItemById(recipe.outputItemId), "getHp"); }
    private int getRecipeStaminaDelta(Recipe recipe) { return extractItemStat(ItemDatabase.getItemById(recipe.outputItemId), "getStamana", "getStamina", "getStimana"); }

    private void refreshRecipeSlots() {
        for (int i = 0; i < MAX_RECIPE_SLOTS; i++) {
            ImageView imageView = recipeImages.get(i);
            if (i >= unlockedRecipes.size()) {
                if (imageView != null) {
                    imageView.setImage(null);
                    imageView.setVisible(false);
                    imageView.setOpacity(0.0);
                }
                continue;
            }
            Recipe recipe = unlockedRecipes.get(i);
            if (imageView != null) {
                imageView.setImage(ItemDatabase.getImage(recipe.outputItemId));
                imageView.setVisible(true);
                imageView.setOpacity(0.9);
            }
        }
        refreshRecipeSelectionHighlight();
    }

    private void handleRecipeSelection(int index) {
        if (index < 0 || index >= unlockedRecipes.size()) return;
        selectedRecipe = unlockedRecipes.get(index);
        refreshSelectionDetails();
    }

    private void refreshSelectionDetails() {
        refreshRecipeSelectionHighlight();
        if (selectedRecipe == null) {
            if (resultitem != null) resultitem.setImage(null);
            if (lblname != null) lblname.setText("Name :");
            if (lblwt != null) lblwt.setText("Waiting time :");
            if (lblsr != null) lblsr.setText("Success rate : --");
            if (lbleffect != null) lbleffect.setText("Effect :");
            clearIngredientViews();
            if (btncraftitem != null) btncraftitem.setDisable(true);
            return;
        }
        if (resultitem != null) resultitem.setImage(ItemDatabase.getImage(selectedRecipe.outputItemId));
        if (lblname != null) lblname.setText("Name : " + selectedRecipe.name + " x" + selectedRecipe.baseOutputAmount);
        if (lblwt != null) lblwt.setText("Waiting time : " + getAdjustedWaitSeconds(selectedRecipe) + "s  SP:-" + getAdjustedStaminaCost(selectedRecipe));
        if (lblsr != null) lblsr.setText("Success rate : " + computeSuccessRate(selectedRecipe) + "%");
        if (lbleffect != null) lbleffect.setText("Effect : " + buildEffectText(selectedRecipe));
        updateIngredientViews();
        updateCraftingStateUi();
    }

    private String buildEffectText(Recipe recipe) {
        List<String> parts = new ArrayList<>();
        int hp = getRecipeHpDelta(recipe);
        int sp = getRecipeStaminaDelta(recipe);
        if (hp != 0) parts.add((hp > 0 ? "+" : "") + hp + "HP");
        if (sp != 0) parts.add((sp > 0 ? "+" : "") + sp + "SP");
        return parts.isEmpty() ? "No effect" : String.join(" / ", parts);
    }

    private void clearIngredientViews() {
        setIngredientSlot(ing1, ing1lbl, null, 0, 0);
        setIngredientSlot(ing2, ing2lbl, null, 0, 0);
        setIngredientSlot(ing3, ing3lbl, null, 0, 0);
        setIngredientSlot(ing4, ing4lbl, null, 0, 0);
    }

    private void updateIngredientViews() {
        if (selectedRecipe == null) { clearIngredientViews(); return; }
        List<ImageView> images = Arrays.asList(ing1, ing2, ing3, ing4);
        List<Label> labels = Arrays.asList(ing1lbl, ing2lbl, ing3lbl, ing4lbl);
        for (int i = 0; i < images.size(); i++) {
            IngredientRequirement req = (i < selectedRecipe.ingredients.size()) ? selectedRecipe.ingredients.get(i) : null;
            if (req == null) { setIngredientSlot(images.get(i), labels.get(i), null, 0, 0); continue; }
            item ingredient = ItemDatabase.getItemById(req.itemId);
            setIngredientSlot(images.get(i), labels.get(i), ingredient, inventory.getCount(req.itemId), req.requiredAmount);
        }
    }

    private void setIngredientSlot(ImageView imageView, Label label, item itm, int owned, int required) {
        if (imageView != null) {
            imageView.setImage(itm == null ? null : ItemDatabase.getImage(itm.getItem_id()));
            imageView.setVisible(itm != null);
            imageView.setOpacity(itm == null ? 0.0 : 1.0);
        }
        if (label != null) {
            label.setText(itm == null ? "" : (owned + "/" + required));
            label.setStyle(itm != null && owned < required ? "-fx-text-fill: #ff8080;" : "-fx-text-fill: white;");
        }
    }

    private void setupResultInteraction() {
        if (resultitem != null) resultitem.setOnMouseClicked(this::handleresultitemaction);
        if (btncraftitem != null) btncraftitem.setDisable(true);
    }

    private void restoreActiveCraftSelection() {
        if (!gameState.hasActiveCraftingJob()) {
            if (!unlockedRecipes.isEmpty()) selectedRecipe = unlockedRecipes.get(0);
            return;
        }
        int outputItemId = gameState.getActiveCraftingOutputItemId();
        for (Recipe recipe : unlockedRecipes) {
            if (recipe.outputItemId == outputItemId) { selectedRecipe = recipe; return; }
        }
        if (!unlockedRecipes.isEmpty()) selectedRecipe = unlockedRecipes.get(0);
    }

    private void startUiTimer() {
        stopUiTimer();
        craftUiTimer = new AnimationTimer() {
            @Override public void handle(long now) {
                updateCraftingStateUi();
                updateIngredientViews();
            }
        };
        craftUiTimer.start();
        if (view != null) view.sceneProperty().addListener((obs, oldScene, newScene) -> { if (newScene == null) stopUiTimer(); });
    }

    private void stopUiTimer() {
        if (craftUiTimer != null) { craftUiTimer.stop(); craftUiTimer = null; }
    }

    private void updateCraftingStateUi() {
        double now = GameTimeManager.get().totalSecondsProperty().get();
        boolean hasJob = gameState.hasActiveCraftingJob();
        boolean ready = gameState.isCraftingReady(now);
        boolean currentSelectionMatchesJob = selectedRecipe != null && hasJob && selectedRecipe.outputItemId == gameState.getActiveCraftingOutputItemId();
        if (ready && currentSelectionMatchesJob) {
            if (lblwt != null) lblwt.setText("Ready to collect (" + (gameState.isActiveCraftingSuccess() ? "Success" : "Failure") + ")");
            if (resultitem != null) {
                resultitem.setStyle("-fx-effect: dropshadow(gaussian, yellow, 18, 0.8, 0, 0);");
                resultitem.setOpacity(1.0);
            }
            if (lbleffect != null) {
                List<String> parts = new ArrayList<>();
                if (gameState.getActiveCraftingHpDelta() != 0) parts.add((gameState.getActiveCraftingHpDelta() > 0 ? "+" : "") + gameState.getActiveCraftingHpDelta() + "HP");
                if (gameState.getActiveCraftingStaminaDelta() != 0) parts.add((gameState.getActiveCraftingStaminaDelta() > 0 ? "+" : "") + gameState.getActiveCraftingStaminaDelta() + "SP");
                lbleffect.setText("Effect : " + (parts.isEmpty() ? "No effect" : String.join(" / ", parts)));
            }
            if (btncraftitem != null) btncraftitem.setDisable(true);
            return;
        }
        if (hasJob && currentSelectionMatchesJob) {
            int remaining = (int)Math.ceil(Math.max(0, gameState.getActiveCraftingCompleteAtSeconds() - now));
            if (lblwt != null) lblwt.setText("Waiting time : " + remaining + "s");
            if (resultitem != null) {
                resultitem.setStyle("");
                resultitem.setOpacity(0.9);
            }
            if (lbleffect != null) lbleffect.setText("Effect : Crafting... " + (gameState.isActiveCraftingSuccess() ? "success queued" : "failure queued"));
            if (btncraftitem != null) btncraftitem.setDisable(true);
            return;
        }
        if (selectedRecipe != null) {
            if (lblwt != null) lblwt.setText("Waiting time : " + getAdjustedWaitSeconds(selectedRecipe) + "s  SP:-" + getAdjustedStaminaCost(selectedRecipe));
            if (resultitem != null) {
                resultitem.setStyle("");
                resultitem.setOpacity(canCraft(selectedRecipe) ? 1.0 : 0.65);
            }
            if (lbleffect != null) lbleffect.setText("Effect : " + buildEffectText(selectedRecipe));
            if (btncraftitem != null) btncraftitem.setDisable(!canCraft(selectedRecipe) || hasJob);
        }
    }

    private boolean canCraft(Recipe recipe) {
        if (recipe == null) return false;
        if (gameState.getPlayerState().getStamina() < getAdjustedStaminaCost(recipe)) return false;
        for (IngredientRequirement req : recipe.ingredients) {
            if (inventory.getCount(req.itemId) < req.requiredAmount) return false;
        }
        return true;
    }

    private int computeSuccessRate(Recipe recipe) {
        if (recipe == null || gameState == null) return 0;
        return ProcessingRules.computeAdjustedSuccessRate(recipe.baseSuccessRate, recipe.dexScaling, gameState.getPlayerState().getDex());
    }

    private int getAdjustedWaitSeconds(Recipe recipe) {
        if (recipe == null || gameState == null) return 0;
        return ProcessingRules.computeAdjustedWaitSeconds(recipe.waitSeconds, recipe.dexScaling, gameState.getPlayerState().getDex());
    }

    private int getAdjustedStaminaCost(Recipe recipe) {
        if (recipe == null || gameState == null) return 0;
        return ProcessingRules.computeAdjustedStaminaCost(recipe.staminaCost, recipe.dexScaling, gameState.getPlayerState().getDex());
    }

    @FXML
    private void handleresultitemaction(MouseEvent event) {
        if (selectedRecipe == null) return;
        double now = GameTimeManager.get().totalSecondsProperty().get();
        if (!gameState.hasActiveCraftingJob()) return;
        if (selectedRecipe.outputItemId != gameState.getActiveCraftingOutputItemId()) return;
        if (!gameState.isCraftingReady(now)) return;
        inventory.addItem(selectedRecipe.outputItemId, gameState.getActiveCraftingOutputQuantity());
        if (gameState.isActiveCraftingSuccess()) {
            gameState.queueCookedDishEffect(selectedRecipe.outputItemId, gameState.getActiveCraftingHpDelta(), gameState.getActiveCraftingStaminaDelta(), true);
        }
        gameState.clearCraftingJob();
        updateCraftingStateUi();
        updateIngredientViews();
    }

    @FXML
    private void handlecraftrecipeaction(MouseEvent event) {
        Object source = event.getSource();
        int index = recipeImages.indexOf(source);
        if (index >= 0) handleRecipeSelection(index);
    }

    @FXML
    private void handlecraftitemaction(ActionEvent event) {
        if (selectedRecipe == null || gameState.hasActiveCraftingJob() || !canCraft(selectedRecipe)) return;
        for (IngredientRequirement req : selectedRecipe.ingredients) {
            if (!inventory.removeItem(req.itemId, req.requiredAmount)) return;
        }
        int adjustedStaminaCost = getAdjustedStaminaCost(selectedRecipe);
        gameState.getPlayerState().consumeStamina(adjustedStaminaCost);
        int successRate = computeSuccessRate(selectedRecipe);
        boolean success = ProcessingRules.rollSuccess(successRate);
        int outputQty = success ? ProcessingRules.computeOutputAmount(selectedRecipe.baseOutputAmount, successRate) : 0;
        int sign = success ? 1 : -1;
        double now = GameTimeManager.get().totalSecondsProperty().get();
        gameState.startCraftingJob(selectedRecipe.outputItemId, outputQty, now + getAdjustedWaitSeconds(selectedRecipe), sign * getRecipeHpDelta(selectedRecipe), sign * getRecipeStaminaDelta(selectedRecipe), success);
        updateIngredientViews();
        updateCraftingStateUi();
    }

    @FXML
    private void handlecloseaction(ActionEvent event) {
        stopUiTimer();
        String returnFxml = gameState.getReturnFxml();
        SpawnTag returnSpawn = gameState.getReturnSpawn();
        if (returnFxml == null) {
            returnFxml = "/view/home_interior.fxml";
            returnSpawn = SpawnTag.FROM_CRAFT;
        }
        SceneManager.switchScene(returnFxml, returnSpawn);
    }

    private void refreshRecipeSelectionHighlight() {
        for (int i = 0; i < recipeImages.size(); i++) {
            ImageView img = recipeImages.get(i);
            if (img == null) continue;
            boolean selected = i < unlockedRecipes.size() && unlockedRecipes.get(i) == selectedRecipe;
            img.setStyle(selected ? "-fx-effect: dropshadow(gaussian, yellow, 12, 0.7, 0, 0);" : "");
            img.setOpacity(selected ? 1.0 : 0.9);
            img.setScaleX(selected ? 1.05 : 1.0);
            img.setScaleY(selected ? 1.05 : 1.0);
        }
    }

    private static final class IngredientSpec {
        private final String itemName; private final int amount;
        private IngredientSpec(String itemName, int amount) { this.itemName = itemName; this.amount = amount; }
    }
    private static final class IngredientRequirement {
        private final int itemId; private final int requiredAmount;
        private IngredientRequirement(int itemId, int requiredAmount) { this.itemId = itemId; this.requiredAmount = requiredAmount; }
    }
    private static final class RecipeTemplate {
        private final String key; private final String outputItemName; private final int waitSeconds; private final int baseSuccessRate; private final int dexScaling; private final int staminaCost; private final IngredientSpec[] ingredients;
        private RecipeTemplate(String key, String outputItemName, int waitSeconds, int baseSuccessRate, int dexScaling, int staminaCost, IngredientSpec... ingredients) { this.key = key; this.outputItemName = outputItemName; this.waitSeconds = waitSeconds; this.baseSuccessRate = baseSuccessRate; this.dexScaling = dexScaling; this.staminaCost = staminaCost; this.ingredients = ingredients; }
    }
    private static final class Recipe {
        private final String key; private final int outputItemId; private final String name; private final int waitSeconds; private final int baseSuccessRate; private final int dexScaling; private final int staminaCost; private final int baseOutputAmount; private final List<IngredientRequirement> ingredients;
        private Recipe(String key, int outputItemId, String name, int waitSeconds, int baseSuccessRate, int dexScaling, int staminaCost, List<IngredientRequirement> ingredients) { this(key, outputItemId, name, waitSeconds, baseSuccessRate, dexScaling, staminaCost, 1, ingredients); }
        private Recipe(String key, int outputItemId, String name, int waitSeconds, int baseSuccessRate, int dexScaling, int staminaCost, int baseOutputAmount, List<IngredientRequirement> ingredients) { this.key = key; this.outputItemId = outputItemId; this.name = name; this.waitSeconds = waitSeconds; this.baseSuccessRate = baseSuccessRate; this.dexScaling = dexScaling; this.staminaCost = staminaCost; this.baseOutputAmount = Math.max(1, baseOutputAmount); this.ingredients = ingredients; }
    }
}
