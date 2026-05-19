package controller;

import game.GameState;
import game.GameTimeManager;
import game.SceneController;
import java.util.HashMap;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import model.Inventory;
import model.ItemDatabase;
import model.RecipeCatalog;
import model.item;
import util.AppDialogs;
import util.SceneManager;
import util.SkillModifiers;
import util.SpawnTag;
import util.TutorialOverlayHelper;
import util.WorldScaler;
import javafx.util.Duration;

public class CraftController implements Initializable, SceneController {
    private static final int MAX_RECIPE_SLOTS = 20;
    private static final int DEFAULT_WAIT_SECONDS = 40;
    private static final String UNKNOWN_RECIPE_IMAGE_PATH = "/image/tools/unknown_recipe.png";
    private static final List<String> CRAFT_TUTORIAL_IMAGES = List.of(
        "/image/Toturial/Craft Tutorial.png"
    );

    private GameState gameState;
    private Inventory inventory;

    private final List<ImageView> recipeImages = new ArrayList<>();
    private final List<Recipe> unlockedRecipes = new ArrayList<>();
    private Recipe selectedRecipe;
    private AnimationTimer craftUiTimer;
    private Image unknownRecipeImage;
    private final Map<ImageView, TranslateTransition> lockedRecipeFloatAnimations = new HashMap<>();

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
    @FXML
    private Button btntuto;

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
        unknownRecipeImage = loadImage(UNKNOWN_RECIPE_IMAGE_PATH);
        new WorldScaler(view, world);
        collectRecipeNodes();
        loadRecipes();
        refreshRecipeSlots();
        setupResultInteraction();
        restoreActiveCraftSelection();
        startUiTimer();
        refreshSelectionDetails();
        TutorialOverlayHelper.showIfNeeded(
            view,
            gameState,
            "craft_inside",
            CRAFT_TUTORIAL_IMAGES
        );
    }

    private void collectRecipeNodes() {
        if (!recipeImages.isEmpty()) return;
        recipeImages.addAll(Arrays.asList(craft, craft1, craft2, craft3, craft4, craft5, craft6, craft7, craft8, craft9, craft10, craft11, craft12, craft13, craft14, craft15, craft16, craft17, craft18, craft19));
    }

    private void loadRecipes() {
        unlockedRecipes.clear();
        for (RecipeCatalog.RecipeDefinition definition : RecipeCatalog.getRecipes(RecipeCatalog.Domain.CRAFTING)) {
            addRecipeIfValid(definition);
        }
        unlockedRecipes.sort(Comparator.comparing(r -> r.name, String.CASE_INSENSITIVE_ORDER));
    }

    private void addRecipeIfValid(RecipeCatalog.RecipeDefinition definition) {
        if (definition == null) {
            return;
        }

        item output = resolveItemByAliases(definition.getOutputAliases());
        if (output == null || !ItemDatabase.isDisplayableItem(output.getItem_id())) return;
        List<IngredientRequirement> ingredients = new ArrayList<>();
        for (RecipeCatalog.IngredientSpec spec : definition.getIngredients()) {
            item ingItem = resolveItemByAliases(spec.getItemAliases());
            if (ingItem == null || !ItemDatabase.isDisplayableItem(ingItem.getItem_id())) return;
            ingredients.add(new IngredientRequirement(ingItem.getItem_id(), spec.getAmount()));
        }
        CraftRecipeBalance balance = craftBalanceFor(definition);
        boolean unlocked = gameState.canAccessRecipe(definition.getKey());
        unlockedRecipes.add(new Recipe(
            definition.getKey(),
            output.getItem_id(),
            definition.getDisplayName(),
            balance.waitSeconds,
            balance.baseSuccessRate,
            balance.dexScaling,
            balance.staminaCost,
            ingredients,
            unlocked
        ));
    }

    private CraftRecipeBalance craftBalanceFor(RecipeCatalog.RecipeDefinition definition) {
        if (definition == null) {
            return new CraftRecipeBalance(DEFAULT_WAIT_SECONDS, 85, 2, 6);
        }

        return switch (definition.getKey()) {
            case "rope" -> new CraftRecipeBalance(35, 88, 3, 6);
            case "wooden_board" -> new CraftRecipeBalance(45, 84, 3, 8);
            case "stone_block" -> new CraftRecipeBalance(50, 82, 3, 8);
            case "polished_stone" -> new CraftRecipeBalance(55, 80, 4, 10);
            case "fruit_mix" -> new CraftRecipeBalance(30, 90, 2, 5);
            default -> new CraftRecipeBalance(DEFAULT_WAIT_SECONDS, 85, 2, 6);
        };
    }

    private item resolveItemByAliases(String... aliases) {
        return RecipeCatalog.resolveItemByAliases(aliases);
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
                    stopLockedRecipeFloat(imageView);
                    imageView.setViewport(null);
                    imageView.setTranslateY(0.0);
                    imageView.setImage(null);
                    imageView.setVisible(false);
                    imageView.setOpacity(0.0);
                }
                continue;
            }
            Recipe recipe = unlockedRecipes.get(i);
            if (imageView != null) {
                imageView.setImage(recipe.unlocked ? ItemDatabase.getImage(recipe.outputItemId) : unknownRecipeImage);
                imageView.setVisible(true);
                imageView.setOpacity(recipe.unlocked ? 0.9 : 0.75);
                applyLockedRecipeVisual(imageView, !recipe.unlocked, i);
            }
        }
        refreshRecipeSelectionHighlight();
    }

    private void handleRecipeSelection(int index) {
        if (index < 0 || index >= unlockedRecipes.size()) return;
        Recipe recipe = unlockedRecipes.get(index);
        if (recipe == null || !recipe.unlocked) return;
        selectedRecipe = recipe;
        refreshSelectionDetails();
    }

    private void refreshSelectionDetails() {
        refreshRecipeSelectionHighlight();
        if (selectedRecipe == null) {
            if (resultitem != null) {
                resultitem.setImage(null);
                resultitem.setStyle("");
                resultitem.setOpacity(0.0);
            }
            if (lblname != null) lblname.setText("Name :");
            if (lblwt != null) lblwt.setText("Waiting time :");
            if (lblsr != null) lblsr.setText("Success rate : --");
            if (lbleffect != null) lbleffect.setText("Effect :");
            clearIngredientViews();
            if (btncraftitem != null) btncraftitem.setDisable(true);
            return;
        }
        if (lblname != null) lblname.setText("Name : " + selectedRecipe.name + " x" + selectedRecipe.baseOutputAmount);
        if (lblwt != null) lblwt.setText("Waiting time : " + getAdjustedWaitSeconds(selectedRecipe) + "s");
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
            Recipe firstUnlocked = firstUnlockedRecipe();
            if (firstUnlocked != null) selectedRecipe = firstUnlocked;
            return;
        }
        int outputItemId = gameState.getActiveCraftingOutputItemId();
        for (Recipe recipe : unlockedRecipes) {
            if (recipe.unlocked && recipe.outputItemId == outputItemId) { selectedRecipe = recipe; return; }
        }
        Recipe firstUnlocked = firstUnlockedRecipe();
        if (firstUnlocked != null) selectedRecipe = firstUnlocked;
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
        stopAllLockedRecipeFloats();
    }

    private void updateCraftingStateUi() {
        double now = GameTimeManager.get().totalSecondsProperty().get();
        boolean hasJob = gameState.hasActiveCraftingJob();
        boolean ready = gameState.isCraftingReady(now);
        int jobOutputItemId = hasJob ? gameState.getActiveCraftingOutputItemId() : -1;

        if (hasJob && ready) {
            if (lblwt != null) lblwt.setText("Ready to collect (" + (gameState.isActiveCraftingSuccess() ? "Success" : "Failure") + ")");
            if (resultitem != null) {
                resultitem.setImage(jobOutputItemId <= 0 ? null : ItemDatabase.getImage(jobOutputItemId));
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
        if (hasJob) {
            int remaining = (int)Math.ceil(Math.max(0, gameState.getActiveCraftingCompleteAtSeconds() - now));
            if (lblwt != null) lblwt.setText("Waiting time : " + remaining + "s");
            if (resultitem != null) {
                resultitem.setImage(null);
                resultitem.setStyle("");
                resultitem.setOpacity(0.0);
            }
            if (lbleffect != null) lbleffect.setText("Effect : Crafting... " + (gameState.isActiveCraftingSuccess() ? "success queued" : "failure queued"));
            if (btncraftitem != null) btncraftitem.setDisable(true);
            return;
        }
        if (selectedRecipe != null) {
            if (lblwt != null) lblwt.setText("Waiting time : " + getAdjustedWaitSeconds(selectedRecipe) + "s");
            if (resultitem != null) {
                // Spec: only show the crafted result once the process is done.
                resultitem.setImage(null);
                resultitem.setStyle("");
                resultitem.setOpacity(0.0);
            }
            if (lbleffect != null) lbleffect.setText("Effect : " + buildEffectText(selectedRecipe));
            if (btncraftitem != null) btncraftitem.setDisable(!canCraft(selectedRecipe) || hasJob);
        }
    }

    private boolean canCraft(Recipe recipe) {
        if (recipe == null) return false;
        for (IngredientRequirement req : recipe.ingredients) {
            if (inventory.getCount(req.itemId) < req.requiredAmount) return false;
        }
        return true;
    }

    private int computeSuccessRate(Recipe recipe) {
        if (recipe == null || gameState == null) return 0;
        int base = ProcessingRules.computeAdjustedSuccessRate(recipe.baseSuccessRate, recipe.dexScaling, gameState.getPlayerState().getDex());
        GameState.PlayerSkills skills = gameState.getPlayerSkills();
        int craftingLevel = (skills == null) ? 0 : Math.max(0, skills.getCraftingLevel());
        int comboSize = (recipe.ingredients == null) ? 1 : recipe.ingredients.size();
        int bonus = SkillModifiers.craftingSuccessBonusPercent(craftingLevel, comboSize);
        return Math.max(0, base + bonus);
    }

    private int getAdjustedWaitSeconds(Recipe recipe) {
        if (recipe == null || gameState == null) return 0;
        return ProcessingRules.computeAdjustedWaitSeconds(recipe.waitSeconds, recipe.dexScaling, gameState.getPlayerState().getDex());
    }

    @FXML
    private void handleresultitemaction(MouseEvent event) {
        double now = GameTimeManager.get().totalSecondsProperty().get();
        if (!gameState.hasActiveCraftingJob()) return;
        if (!gameState.isCraftingReady(now)) return;

        int outputItemId = gameState.getActiveCraftingOutputItemId();
        Recipe jobRecipe = findUnlockedRecipeByOutputItemId(outputItemId);
        if (jobRecipe != null) {
            selectedRecipe = jobRecipe;
        }

        int qty = gameState.getActiveCraftingOutputQuantity();
        if (gameState.isActiveCraftingSuccess()) {
            if (qty > 0 && !inventory.addItem(outputItemId, qty)) {
                AppDialogs.showMessageDialog(null, "Not enough inventory space to collect the crafted item.");
                return;
            }
        } else {
            AppDialogs.showMessageDialog(null, "Crafting failed.");
        }
        if (gameState.isActiveCraftingSuccess()) {
            int hpDelta = gameState.getActiveCraftingHpDelta();
            int staminaDelta = gameState.getActiveCraftingStaminaDelta();
            if (qty > 0 && (hpDelta != 0 || staminaDelta != 0)) {
                for (int i = 0; i < qty; i++) {
                    gameState.queueCookedDishEffect(outputItemId, hpDelta, staminaDelta, true);
                }
            }
        }
        gameState.clearCraftingJob();
        updateCraftingStateUi();
        updateIngredientViews();
        refreshRecipeSelectionHighlight();
    }

    private Recipe findUnlockedRecipeByOutputItemId(int outputItemId) {
        if (outputItemId <= 0) return null;
        for (Recipe recipe : unlockedRecipes) {
            if (recipe != null && recipe.unlocked && recipe.outputItemId == outputItemId) {
                return recipe;
            }
        }
        return null;
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
        int successRate = computeSuccessRate(selectedRecipe);
        boolean success = ProcessingRules.rollSuccess(successRate);
        int outputQty = success ? ProcessingRules.computeOutputAmount(selectedRecipe.baseOutputAmount, successRate) : 0;
        int sign = success ? 1 : -1;
        double now = GameTimeManager.get().totalSecondsProperty().get();
        gameState.startCraftingJob(selectedRecipe.outputItemId, outputQty, now + getAdjustedWaitSeconds(selectedRecipe), sign * getRecipeHpDelta(selectedRecipe), sign * getRecipeStaminaDelta(selectedRecipe), success);
        if (outputQty > 0) {
            gameState.addCraftingSkillExp(outputQty);
        }
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
            boolean locked = i < unlockedRecipes.size() && !unlockedRecipes.get(i).unlocked;
            img.setOpacity(selected ? 1.0 : (locked ? 0.75 : 0.9));
            img.setScaleX(selected ? 1.05 : 1.0);
            img.setScaleY(selected ? 1.05 : 1.0);
        }
    }

    private void applyLockedRecipeVisual(ImageView imageView, boolean locked, int index) {
        if (imageView == null) return;
        if (!locked) {
            stopLockedRecipeFloat(imageView);
            imageView.setViewport(null);
            imageView.setPreserveRatio(true);
            imageView.setTranslateY(0.0);
            return;
        }

        imageView.setViewport(new Rectangle2D(328, 34, 720, 720));
        imageView.setPreserveRatio(false);
        startLockedRecipeFloat(imageView, index);
    }

    private void startLockedRecipeFloat(ImageView imageView, int index) {
        if (imageView == null || lockedRecipeFloatAnimations.containsKey(imageView)) return;
        TranslateTransition floatAnim = new TranslateTransition(Duration.seconds(1.2), imageView);
        floatAnim.setFromY(0.0);
        floatAnim.setToY(-6.0);
        floatAnim.setAutoReverse(true);
        floatAnim.setCycleCount(Animation.INDEFINITE);
        floatAnim.setInterpolator(Interpolator.EASE_BOTH);
        floatAnim.setDelay(Duration.seconds((index % 4) * 0.12));
        floatAnim.play();
        lockedRecipeFloatAnimations.put(imageView, floatAnim);
    }

    private void stopLockedRecipeFloat(ImageView imageView) {
        TranslateTransition floatAnim = lockedRecipeFloatAnimations.remove(imageView);
        if (floatAnim != null) {
            floatAnim.stop();
        }
    }

    private void stopAllLockedRecipeFloats() {
        for (TranslateTransition floatAnim : new ArrayList<>(lockedRecipeFloatAnimations.values())) {
            if (floatAnim != null) {
                floatAnim.stop();
            }
        }
        lockedRecipeFloatAnimations.clear();
    }

    private Recipe firstUnlockedRecipe() {
        for (Recipe recipe : unlockedRecipes) {
            if (recipe != null && recipe.unlocked) return recipe;
        }
        return null;
    }

    private Image loadImage(String path) {
        try {
            var url = getClass().getResource(path);
            return url == null ? null : new Image(url.toExternalForm());
        } catch (Exception ignored) {
            return null;
        }
    }

    @FXML
    private void handlebtntutoaction(ActionEvent event) {
        TutorialOverlayHelper.show(view, event, List.of("/image/Toturial/Craft Tutorial.png"));
        if (event != null) event.consume();
    }

    private static final class IngredientRequirement {
        private final int itemId; private final int requiredAmount;
        private IngredientRequirement(int itemId, int requiredAmount) { this.itemId = itemId; this.requiredAmount = requiredAmount; }
    }
    private static final class CraftRecipeBalance {
        private final int waitSeconds;
        private final int baseSuccessRate;
        private final int dexScaling;
        private final int staminaCost;

        private CraftRecipeBalance(int waitSeconds, int baseSuccessRate, int dexScaling, int staminaCost) {
            this.waitSeconds = waitSeconds;
            this.baseSuccessRate = baseSuccessRate;
            this.dexScaling = dexScaling;
            this.staminaCost = staminaCost;
        }
    }
    private static final class Recipe {
        private final String key; private final int outputItemId; private final String name; private final int waitSeconds; private final int baseSuccessRate; private final int dexScaling; private final int staminaCost; private final int baseOutputAmount; private final List<IngredientRequirement> ingredients; private final boolean unlocked;
        private Recipe(String key, int outputItemId, String name, int waitSeconds, int baseSuccessRate, int dexScaling, int staminaCost, List<IngredientRequirement> ingredients, boolean unlocked) { this(key, outputItemId, name, waitSeconds, baseSuccessRate, dexScaling, staminaCost, 1, ingredients, unlocked); }
        private Recipe(String key, int outputItemId, String name, int waitSeconds, int baseSuccessRate, int dexScaling, int staminaCost, int baseOutputAmount, List<IngredientRequirement> ingredients, boolean unlocked) { this.key = key; this.outputItemId = outputItemId; this.name = name; this.waitSeconds = waitSeconds; this.baseSuccessRate = baseSuccessRate; this.dexScaling = dexScaling; this.staminaCost = staminaCost; this.baseOutputAmount = Math.max(1, baseOutputAmount); this.ingredients = Collections.unmodifiableList(new ArrayList<>(ingredients == null ? List.of() : ingredients)); this.unlocked = unlocked; }
    }
}
