package controller;

import game.GameState;
import game.GameTimeManager;
import game.SceneController;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
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
import model.RecipeCatalog;
import model.item;
import util.AppDialogs;
import util.SceneManager;
import util.SkillModifiers;
import util.SpawnTag;
import util.TutorialOverlayHelper;
import util.WorldScaler;

public class CookController implements Initializable, SceneController {

    private static final int MAX_RECIPE_SLOTS = 20;
    private static final int DEFAULT_DEX = 5;
    private static final List<String> COOK_TUTORIAL_IMAGES = List.of(
        "/image/Toturial/Cooking_inside.png"
    );

    private GameState gameState;
    private Inventory inventory;

    private final List<ImageView> recipeImages = new ArrayList<>();
    private final List<Button> recipeButtons = new ArrayList<>();
    private final List<Label> recipeLabels = new ArrayList<>();
    private final List<Recipe> unlockedRecipes = new ArrayList<>();

    private Recipe selectedRecipe;
    private AnimationTimer cookingUiTimer;

    @FXML private StackPane view;
    @FXML private Group world;
    @FXML private ImageView cookimg;
    @FXML private Button cookbtn;
    private Label cooklbl1;
    @FXML private ImageView cookimg1;
    @FXML private Button cookbtn1;
    private Label cooklbl2;
    @FXML private ImageView cookimg2;
    @FXML private Button cookbtn2;
    private Label cooklbl3;
    @FXML private ImageView cookimg3;
    @FXML private Button cookbtn3;
    private Label cooklbl4;
    @FXML private ImageView cookimg4;
    @FXML private Button cookbtn4;
    private Label cooklbl5;
    @FXML private ImageView cookimg5;
    @FXML private Button cookbtn5;
    private Label cooklbl6;
    @FXML private ImageView cookimg6;
    @FXML private Button cookbtn6;
    private Label cooklbl7;
    @FXML private ImageView cookimg7;
    @FXML private Button cookbtn7;
    private Label cooklbl8;
    @FXML private ImageView cookimg8;
    @FXML private Button cookbtn8;
    private Label cooklbl9;
    @FXML private ImageView cookimg9;
    @FXML private Button cookbtn9;
    private Label cooklbl10;
    @FXML private ImageView cookimg10;
    @FXML private Button cookbtn10;
    private Label cooklbl11;
    @FXML private ImageView cookimg11;
    @FXML private Button cookbtn11;
    private Label cooklbl12;
    @FXML private ImageView cookimg12;
    @FXML private Button cookbtn12;
    private Label cooklbl13;
    @FXML private ImageView cookimg13;
    @FXML private Button cookbtn13;
    private Label cooklbl14;
    @FXML private ImageView cookimg14;
    @FXML private Button cookbtn14;
    private Label cooklbl15;
    @FXML private ImageView cookimg15;
    @FXML private Button cookbtn15;
    private Label cooklbl16;
    @FXML private ImageView cookimg16;
    @FXML private Button cookbtn16;
    private Label cooklbl17;
    @FXML private ImageView cookimg17;
    @FXML private Button cookbtn17;
    private Label cooklbl18;
    @FXML private ImageView cookimg18;
    @FXML private Button cookbtn18;
    private Label cooklbl19;
    @FXML private ImageView cookimg19;
    @FXML private Button cookbtn19;
    private Label cooklbl20;
    @FXML private ImageView Ing1;
    @FXML private ImageView ing2;
    @FXML private ImageView ing3;
    @FXML private ImageView selectedcook;
    @FXML private Button startcooking;
    @FXML private Label inglbl1;
    @FXML private Label inglbl2;
    @FXML private Label inglbl3;
    @FXML private Label cookwaitingtimelbl;
    @FXML private Label cookname;
    @FXML private Label cookeffectlbl;
    @FXML private ImageView closeimgbtn;
    @FXML
    private Label cooksuccessrate;
    @FXML
    private Button btntuto;

    @Override
    public void init(GameState gs) {
        this.gameState = gs;
        this.inventory = gs.getPlayerState().getInventory();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (gameState == null) {
            gameState = GameState.get();
        }
        if (inventory == null) {
            inventory = gameState.getPlayerState().getInventory();
        }

        ItemDatabase.loadItems();
        new WorldScaler(view, world);
        collectRecipeNodes();
        wireRecipeButtons();
        loadRecipes();
        refreshRecipeSlots();
        setupSelectedCookInteraction();
        restoreActiveCookingSelection();
        startUiTimer();
        refreshSelectionDetails();
        TutorialOverlayHelper.showIfNeeded(
            view,
            gameState,
            "cook_inside",
            COOK_TUTORIAL_IMAGES
        );
    }

    private void collectRecipeNodes() {
        if (!recipeImages.isEmpty()) return;

        recipeImages.addAll(Arrays.asList(cookimg, cookimg1, cookimg2, cookimg3, cookimg4, cookimg5, cookimg6, cookimg7, cookimg8, cookimg9, cookimg10, cookimg11, cookimg12, cookimg13, cookimg14, cookimg15, cookimg16, cookimg17, cookimg18, cookimg19));
        recipeButtons.addAll(Arrays.asList(cookbtn, cookbtn1, cookbtn2, cookbtn3, cookbtn4, cookbtn5, cookbtn6, cookbtn7, cookbtn8, cookbtn9, cookbtn10, cookbtn11, cookbtn12, cookbtn13, cookbtn14, cookbtn15, cookbtn16, cookbtn17, cookbtn18, cookbtn19));
        recipeLabels.addAll(Arrays.asList(cooklbl1, cooklbl2, cooklbl3, cooklbl4, cooklbl5, cooklbl6, cooklbl7, cooklbl8, cooklbl9, cooklbl10, cooklbl11, cooklbl12, cooklbl13, cooklbl14, cooklbl15, cooklbl16, cooklbl17, cooklbl18, cooklbl19, cooklbl20));
    }

    private void wireRecipeButtons() {
        for (int i = 0; i < recipeButtons.size(); i++) {
            final int index = i;
            Button button = recipeButtons.get(i);
            if (button != null) {
                button.setOnAction(e -> handleRecipeSelection(index));
            }
        }
    }

    private void loadRecipes() {
        unlockedRecipes.clear();
        List<String> missing = new ArrayList<>();

        for (RecipeCatalog.RecipeDefinition definition : RecipeCatalog.getRecipes(RecipeCatalog.Domain.COOKING)) {
            addRecipeIfValid(definition, missing);
        }

        if (!missing.isEmpty()) {
            System.out.println("[CookController] Missing recipe data/assets:");
            for (String msg : missing) {
                System.out.println("  - " + msg);
            }
        }
    }

    private void addRecipeIfValid(RecipeCatalog.RecipeDefinition definition, List<String> missingReport) {
        if (definition == null) {
            return;
        }
        item output = resolveItemByAliases(definition.getOutputAliases());
        if (output == null) {
            missingReport.add("Output item not found for recipe `" + definition.getKey() + "` aliases=" + Arrays.toString(definition.getOutputAliases()));
            return;
        }
        if (ItemDatabase.getImage(output.getItem_id()) == null) {
            missingReport.add("Output image missing for item `" + output.getItem_name() + "` in recipe `" + definition.getKey() + "`");
            return;
        }

        List<IngredientRequirement> ingredients = new ArrayList<>();
        for (RecipeCatalog.IngredientSpec spec : definition.getIngredients()) {
            item ingItem = resolveItemByAliases(spec.getItemAliases());
            if (ingItem == null) {
                missingReport.add("Ingredient not found for recipe `" + definition.getKey() + "` aliases=" + Arrays.toString(spec.getItemAliases()));
                return;
            }
            if (ItemDatabase.getImage(ingItem.getItem_id()) == null) {
                missingReport.add("Ingredient image missing for `" + ingItem.getItem_name() + "` in recipe `" + definition.getKey() + "`");
                return;
            }
            ingredients.add(new IngredientRequirement(ingItem.getItem_id(), spec.getAmount()));
        }

        if (!gameState.canAccessRecipe(definition.getKey())) {
            return;
        }

        RecipeDifficulty difficulty = difficultyForIngredientCount(ingredients.size());
        unlockedRecipes.add(new Recipe(
            definition.getKey(),
            output.getItem_id(),
            definition.getDisplayName(),
            difficulty.effectText,
            difficulty.waitSeconds,
            difficulty.baseSuccessRate,
            difficulty.dexScaling,
            difficulty.staminaCost,
            1,
            ingredients
        ));
    }

    private RecipeDifficulty difficultyForIngredientCount(int ingredientCount) {
        int count = Math.max(1, ingredientCount);
        return switch (count) {
            case 1 -> new RecipeDifficulty(12, 96, 1, 1, "Easy dish");
            case 2 -> new RecipeDifficulty(24, 88, 2, 3, "Intermediate dish");
            default -> new RecipeDifficulty(36, 78, 3, 5, "Advanced dish");
        };
    }

    private item resolveItemByAliases(String... aliases) {
        return RecipeCatalog.resolveItemByAliases(aliases);
    }

    private void refreshRecipeSlots() {
        for (int i = 0; i < MAX_RECIPE_SLOTS; i++) {
            ImageView imageView = recipeImages.get(i);
            Button button = recipeButtons.get(i);
            Label label = recipeLabels.get(i);

            if (i >= unlockedRecipes.size()) {
                if (imageView != null) imageView.setImage(null);
                if (label != null) label.setText("");
                if (button != null) button.setDisable(true);
                continue;
            }

            Recipe recipe = unlockedRecipes.get(i);
            if (imageView != null) {
                imageView.setImage(ItemDatabase.getImage(recipe.outputItemId));
            }
            if (label != null) {
                label.setText("x" + recipe.baseOutputAmount);
            }
            if (button != null) {
                button.setDisable(false);
                button.setText(recipe.name);
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
            if (selectedcook != null) {
                selectedcook.setImage(null);
                selectedcook.setStyle("");
                selectedcook.setOpacity(0.0);
            }
            if (cookname != null) cookname.setText("Select recipe");
            if (cookeffectlbl != null) cookeffectlbl.setText("Effect:");
            if (cookwaitingtimelbl != null) cookwaitingtimelbl.setText("waiting time:");
            clearIngredientViews();
            if (startcooking != null) startcooking.setDisable(true);
            return;
        }

        if (cookname != null) cookname.setText(selectedRecipe.name);
        if (cookeffectlbl != null) cookeffectlbl.setText("Effect: " + selectedRecipe.effectText);

        updateIngredientViews();
        updateCookingStateUi();
    }

    private void clearIngredientViews() {
        setIngredientSlot(Ing1, inglbl1, null, 0, 0);
        setIngredientSlot(ing2, inglbl2, null, 0, 0);
        setIngredientSlot(ing3, inglbl3, null, 0, 0);
    }

    private void updateIngredientViews() {
        if (selectedRecipe == null) {
            clearIngredientViews();
            return;
        }

        List<ImageView> images = Arrays.asList(Ing1, ing2, ing3);
        List<Label> labels = Arrays.asList(inglbl1, inglbl2, inglbl3);

        for (int i = 0; i < images.size(); i++) {
            IngredientRequirement req = (i < selectedRecipe.ingredients.size()) ? selectedRecipe.ingredients.get(i) : null;
            if (req == null) {
                setIngredientSlot(images.get(i), labels.get(i), null, 0, 0);
                continue;
            }

            item itm = ItemDatabase.getItemById(req.itemId);
            setIngredientSlot(images.get(i), labels.get(i), itm, inventory.getCount(req.itemId), req.requiredAmount);
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

    private void setupSelectedCookInteraction() {
        if (selectedcook != null) {
            selectedcook.setOnMouseClicked(this::handleCollectCookedDish);
        }
        if (startcooking != null) {
            startcooking.setDisable(true);
        }
    }

    private void restoreActiveCookingSelection() {
        if (!gameState.hasActiveCookingJob()) {
            if (!unlockedRecipes.isEmpty()) {
                selectedRecipe = unlockedRecipes.get(0);
            }
            return;
        }

        int outputItemId = gameState.getActiveCookingOutputItemId();
        for (Recipe recipe : unlockedRecipes) {
            if (recipe.outputItemId == outputItemId) {
                selectedRecipe = recipe;
                return;
            }
        }

        if (!unlockedRecipes.isEmpty()) {
            selectedRecipe = unlockedRecipes.get(0);
        }
    }

    private void startUiTimer() {
        stopUiTimer();
        cookingUiTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateCookingStateUi();
                updateIngredientViews();
            }
        };
        cookingUiTimer.start();

        if (view != null) {
            view.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene == null) {
                    stopUiTimer();
                }
            });
        }
    }

    private void stopUiTimer() {
        if (cookingUiTimer != null) {
            cookingUiTimer.stop();
            cookingUiTimer = null;
        }
    }

    private void updateCookingStateUi() {
        double now = GameTimeManager.get().totalSecondsProperty().get();
        boolean hasJob = gameState.hasActiveCookingJob();
        boolean ready = gameState.isCookingReady(now);
        int jobOutputItemId = hasJob ? gameState.getActiveCookingOutputItemId() : -1;

        if (hasJob && ready) {
            if (cookwaitingtimelbl != null) {
                cookwaitingtimelbl.setText("Ready to collect (" + (gameState.isActiveCookingSuccess() ? "Success" : "Failure") + ")");
            }
            if (selectedcook != null) {
                selectedcook.setImage(jobOutputItemId <= 0 ? null : ItemDatabase.getImage(jobOutputItemId));
                selectedcook.setStyle("-fx-effect: dropshadow(gaussian, yellow, 18, 0.8, 0, 0);");
                selectedcook.setOpacity(1.0);
            }
            if (startcooking != null) startcooking.setDisable(true);
            return;
        }

        if (hasJob) {
            int remaining = (int) Math.ceil(Math.max(0, gameState.getActiveCookingCompleteAtSeconds() - now));
            if (cookwaitingtimelbl != null) cookwaitingtimelbl.setText("waiting time: " + remaining + "s");
            if (selectedcook != null) {
                selectedcook.setImage(null);
                selectedcook.setStyle("");
                selectedcook.setOpacity(0.0);
            }
            if (startcooking != null) startcooking.setDisable(true);
            return;
        }

        if (selectedRecipe != null) {
            if (cookwaitingtimelbl != null) {
                cookwaitingtimelbl.setText("waiting time: " + getAdjustedWaitSeconds(selectedRecipe) + "s  SP:-" + getAdjustedStaminaCost(selectedRecipe) + "  SR:" + computeSuccessRate(selectedRecipe) + "%");
            }
            if (selectedcook != null) {
                // Spec: only show the cooked result once the process is done.
                selectedcook.setImage(null);
                selectedcook.setStyle("");
                selectedcook.setOpacity(0.0);
            }
            if (startcooking != null) startcooking.setDisable(!canCook(selectedRecipe) || hasJob);
        }
    }

    private boolean canCook(Recipe recipe) {
        if (recipe == null) return false;
        for (IngredientRequirement req : recipe.ingredients) {
            if (inventory.getCount(req.itemId) < req.requiredAmount) {
                return false;
            }
        }
        return gameState.getPlayerState().getStamina() >= getAdjustedStaminaCost(recipe);
    }

    private int computeSuccessRate(Recipe recipe) {
        if (recipe == null || gameState == null) return 0;
        int base = ProcessingRules.computeAdjustedSuccessRate(recipe.baseSuccessRate, recipe.dexScaling, gameState.getPlayerState().getDex());
        GameState.PlayerSkills skills = gameState.getPlayerSkills();
        int cookingLevel = (skills == null) ? 0 : Math.max(0, skills.getCookingLevel());
        int comboSize = (recipe.ingredients == null) ? 1 : recipe.ingredients.size();
        int bonus = SkillModifiers.cookingSuccessBonusPercent(cookingLevel, comboSize);
        return Math.max(0, base + bonus);
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
    private void handlestartcookingaction(ActionEvent event) {
        if (selectedRecipe == null) return;
        if (gameState.hasActiveCookingJob()) return;
        if (!canCook(selectedRecipe)) return;

        for (IngredientRequirement req : selectedRecipe.ingredients) {
            if (!inventory.removeItem(req.itemId, req.requiredAmount)) {
                return;
            }
        }

        int adjustedStaminaCost = getAdjustedStaminaCost(selectedRecipe);
        gameState.getPlayerState().consumeStamina(adjustedStaminaCost);
        int successRate = computeSuccessRate(selectedRecipe);
        boolean success = ProcessingRules.rollSuccess(successRate);
        int outputQty = success ? ProcessingRules.computeOutputAmount(selectedRecipe.baseOutputAmount, successRate) : 0;

        double now = GameTimeManager.get().totalSecondsProperty().get();
        gameState.startCookingJob(selectedRecipe.outputItemId, outputQty, now + getAdjustedWaitSeconds(selectedRecipe), 0, 0, success);
        if (outputQty > 0) {
            gameState.addCookingSkillExp(outputQty);
        }
        updateIngredientViews();
        updateCookingStateUi();
    }

    private void handleCollectCookedDish(MouseEvent event) {
        double now = GameTimeManager.get().totalSecondsProperty().get();
        if (!gameState.hasActiveCookingJob()) return;
        if (!gameState.isCookingReady(now)) return;

        int outputItemId = gameState.getActiveCookingOutputItemId();
        Recipe jobRecipe = findUnlockedRecipeByOutputItemId(outputItemId);
        if (jobRecipe != null) {
            selectedRecipe = jobRecipe;
        }

        if (gameState.isActiveCookingSuccess()) {
            int qty = gameState.getActiveCookingOutputQuantity();
            if (qty > 0 && !inventory.addItem(outputItemId, qty)) {
                AppDialogs.showMessageDialog(null, "Not enough inventory space to collect the cooked dish.");
                return;
            }
        } else {
            AppDialogs.showMessageDialog(null, "Cooking failed.");
        }
        gameState.clearCookingJob();
        updateCookingStateUi();
        updateIngredientViews();
        refreshRecipeSelectionHighlight();
    }

    private Recipe findUnlockedRecipeByOutputItemId(int outputItemId) {
        if (outputItemId <= 0) return null;
        for (Recipe recipe : unlockedRecipes) {
            if (recipe != null && recipe.outputItemId == outputItemId) {
                return recipe;
            }
        }
        return null;
    }

    @FXML
    private void handlecloseimgbtn(MouseEvent event) {
        stopUiTimer();

        String returnFxml = gameState.getReturnFxml();
        SpawnTag returnSpawn = gameState.getReturnSpawn();

        if (returnFxml == null) {
            returnFxml = "/view/home_interior.fxml";
            returnSpawn = SpawnTag.FROM_COOK;
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

    @FXML
    private void handlerecipes(ActionEvent event) {
        Object source = event.getSource();
        int index = recipeButtons.indexOf(source);
        if (index >= 0) {
            handleRecipeSelection(index);
        }
    }

    @FXML
    private void handlebtntutoaction(ActionEvent event) {
        TutorialOverlayHelper.show(view, TutorialOverlayHelper.GLOBAL_BUTTON_TUTORIAL_IMAGES);
        if (event != null) {
            event.consume();
        }
    }

    private static final class RecipeDifficulty {
        private final int waitSeconds;
        private final int baseSuccessRate;
        private final int dexScaling;
        private final int staminaCost;
        private final String effectText;

        private RecipeDifficulty(int waitSeconds, int baseSuccessRate, int dexScaling, int staminaCost, String effectText) {
            this.waitSeconds = waitSeconds;
            this.baseSuccessRate = baseSuccessRate;
            this.dexScaling = dexScaling;
            this.staminaCost = staminaCost;
            this.effectText = effectText;
        }
    }

    private static final class IngredientRequirement {
        private final int itemId;
        private final int requiredAmount;

        private IngredientRequirement(int itemId, int requiredAmount) {
            this.itemId = itemId;
            this.requiredAmount = requiredAmount;
        }
    }

    private static final class Recipe {
        private final String key;
        private final int outputItemId;
        private final String name;
        private final String effectText;
        private final int waitSeconds;
        private final int baseSuccessRate;
        private final int dexScaling;
        private final int staminaCost;
        private final int baseOutputAmount;
        private final List<IngredientRequirement> ingredients;

        private Recipe(String key, int outputItemId, String name, String effectText, int waitSeconds, int baseSuccessRate, int dexScaling, int staminaCost, int baseOutputAmount, List<IngredientRequirement> ingredients) {
            this.key = key;
            this.outputItemId = outputItemId;
            this.name = name;
            this.effectText = effectText;
            this.waitSeconds = waitSeconds;
            this.baseSuccessRate = baseSuccessRate;
            this.dexScaling = dexScaling;
            this.staminaCost = staminaCost;
            this.baseOutputAmount = Math.max(1, baseOutputAmount);
            this.ingredients = Collections.unmodifiableList(
                new ArrayList<>(ingredients == null ? List.of() : ingredients)
            );
        }
    }
}
