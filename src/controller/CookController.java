package controller;

import game.GameState;
import game.GameTimeManager;
import game.SceneController;
import java.util.HashMap;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
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

public class CookController implements Initializable, SceneController {

    private static final int MAX_RECIPE_SLOTS = 20;
    private static final int DEFAULT_DEX = 5;
    private static final String UNKNOWN_RECIPE_IMAGE_PATH = "/image/tools/unknown_recipe.png";
    private static final List<String> COOK_TUTORIAL_IMAGES = List.of(
        "/image/Toturial/Cooking Tutorial.png"
    );

    private GameState gameState;
    private Inventory inventory;

    private final List<ImageView> recipeImages = new ArrayList<>();
    private final List<Button> recipeButtons = new ArrayList<>();
    private final List<Label> recipeLabels = new ArrayList<>();
    private final List<Recipe> unlockedRecipes = new ArrayList<>();

    private Recipe selectedRecipe;
    private AnimationTimer cookingUiTimer;
    private Image unknownRecipeImage;
    private final Map<ImageView, TranslateTransition> lockedRecipeFloatAnimations = new HashMap<>();

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
    @FXML
    private Button tutoclose;

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
        unknownRecipeImage = loadImage(UNKNOWN_RECIPE_IMAGE_PATH);
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

        boolean unlocked = gameState.canAccessRecipe(definition.getKey());
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
            ingredients,
            unlocked
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
                if (imageView != null) {
                    stopLockedRecipeFloat(imageView);
                    imageView.setViewport(null);
                    imageView.setTranslateY(0.0);
                    imageView.setImage(null);
                }
                if (label != null) label.setText("");
                if (button != null) button.setDisable(true);
                continue;
            }

            Recipe recipe = unlockedRecipes.get(i);
            if (imageView != null) {
                imageView.setImage(recipe.unlocked ? ItemDatabase.getImage(recipe.outputItemId) : unknownRecipeImage);
                imageView.setVisible(true);
                imageView.setOpacity(recipe.unlocked ? 0.9 : 0.75);
                applyLockedRecipeVisual(imageView, !recipe.unlocked, i);
            }
            if (label != null) {
                label.setText(recipe.unlocked ? ("x" + recipe.baseOutputAmount) : "");
            }
            if (button != null) {
                button.setDisable(!recipe.unlocked);
                button.setText(recipe.unlocked ? recipe.name : "");
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
            Recipe firstUnlocked = firstUnlockedRecipe();
            if (firstUnlocked != null) {
                selectedRecipe = firstUnlocked;
            }
            return;
        }

        int outputItemId = gameState.getActiveCookingOutputItemId();
        for (Recipe recipe : unlockedRecipes) {
            if (recipe.unlocked && recipe.outputItemId == outputItemId) {
                selectedRecipe = recipe;
                return;
            }
        }

        Recipe firstUnlocked = firstUnlockedRecipe();
        if (firstUnlocked != null) {
            selectedRecipe = firstUnlocked;
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
        stopAllLockedRecipeFloats();
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
                cookwaitingtimelbl.setText("waiting time: " + getAdjustedWaitSeconds(selectedRecipe) + "s  SR:" + computeSuccessRate(selectedRecipe) + "%");
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
        return true;
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
            if (recipe != null && recipe.unlocked && recipe.outputItemId == outputItemId) {
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
    private void handlerecipes(ActionEvent event) {
        Object source = event.getSource();
        int index = recipeButtons.indexOf(source);
        if (index >= 0) {
            handleRecipeSelection(index);
        }
    }

    @FXML
    private void handlebtntutoaction(ActionEvent event) {
        TutorialOverlayHelper.show(view, event, List.of("/image/Toturial/Cooking Tutorial.png"));
        if (event != null) event.consume();
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
        private final boolean unlocked;

        private Recipe(String key, int outputItemId, String name, String effectText, int waitSeconds, int baseSuccessRate, int dexScaling, int staminaCost, int baseOutputAmount, List<IngredientRequirement> ingredients, boolean unlocked) {
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
            this.unlocked = unlocked;
        }
    }
}
