package controller;

import game.GameState;
import game.GameTimeManager;
import game.SceneController;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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
import model.item;
import util.SceneManager;
import util.SpawnTag;

public class CookController implements Initializable, SceneController {

    private static final int MAX_RECIPE_SLOTS = 20;
    private static final int DEFAULT_DEX = 5;

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
    @FXML private Label cooklbl1;
    @FXML private ImageView cookimg1;
    @FXML private Button cookbtn1;
    @FXML private Label cooklbl2;
    @FXML private ImageView cookimg2;
    @FXML private Button cookbtn2;
    @FXML private Label cooklbl3;
    @FXML private ImageView cookimg3;
    @FXML private Button cookbtn3;
    @FXML private Label cooklbl4;
    @FXML private ImageView cookimg4;
    @FXML private Button cookbtn4;
    @FXML private Label cooklbl5;
    @FXML private ImageView cookimg5;
    @FXML private Button cookbtn5;
    @FXML private Label cooklbl6;
    @FXML private ImageView cookimg6;
    @FXML private Button cookbtn6;
    @FXML private Label cooklbl7;
    @FXML private ImageView cookimg7;
    @FXML private Button cookbtn7;
    @FXML private Label cooklbl8;
    @FXML private ImageView cookimg8;
    @FXML private Button cookbtn8;
    @FXML private Label cooklbl9;
    @FXML private ImageView cookimg9;
    @FXML private Button cookbtn9;
    @FXML private Label cooklbl10;
    @FXML private ImageView cookimg10;
    @FXML private Button cookbtn10;
    @FXML private Label cooklbl11;
    @FXML private ImageView cookimg11;
    @FXML private Button cookbtn11;
    @FXML private Label cooklbl12;
    @FXML private ImageView cookimg12;
    @FXML private Button cookbtn12;
    @FXML private Label cooklbl13;
    @FXML private ImageView cookimg13;
    @FXML private Button cookbtn13;
    @FXML private Label cooklbl14;
    @FXML private ImageView cookimg14;
    @FXML private Button cookbtn14;
    @FXML private Label cooklbl15;
    @FXML private ImageView cookimg15;
    @FXML private Button cookbtn15;
    @FXML private Label cooklbl16;
    @FXML private ImageView cookimg16;
    @FXML private Button cookbtn16;
    @FXML private Label cooklbl17;
    @FXML private ImageView cookimg17;
    @FXML private Button cookbtn17;
    @FXML private Label cooklbl18;
    @FXML private ImageView cookimg18;
    @FXML private Button cookbtn18;
    @FXML private Label cooklbl19;
    @FXML private ImageView cookimg19;
    @FXML private Button cookbtn19;
    @FXML private Label cooklbl20;
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
        collectRecipeNodes();
        wireRecipeButtons();
        loadRecipes();
        refreshRecipeSlots();
        setupSelectedCookInteraction();
        restoreActiveCookingSelection();
        startUiTimer();
        refreshSelectionDetails();
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
        List<RecipeTemplate> templates = buildPlannedRecipes();
        List<String> missing = new ArrayList<>();

        for (RecipeTemplate template : templates) {
            addRecipeIfValid(template, missing);
        }

        if (!missing.isEmpty()) {
            System.out.println("[CookController] Missing recipe data/assets:");
            for (String msg : missing) {
                System.out.println("  - " + msg);
            }
        }
    }

    private void addRecipeIfValid(RecipeTemplate template, List<String> missingReport) {
        item output = resolveItemByAliases(template.outputAliases);
        if (output == null) {
            missingReport.add("Output item not found for recipe `" + template.key + "` aliases=" + Arrays.toString(template.outputAliases));
            return;
        }
        if (ItemDatabase.getImage(output.getItem_id()) == null) {
            missingReport.add("Output image missing for item `" + output.getItem_name() + "` in recipe `" + template.key + "`");
            return;
        }

        List<IngredientRequirement> ingredients = new ArrayList<>();
        for (IngredientSpec spec : template.ingredients) {
            item ingItem = resolveItemByAliases(spec.itemAliases);
            if (ingItem == null) {
                missingReport.add("Ingredient not found for recipe `" + template.key + "` aliases=" + Arrays.toString(spec.itemAliases));
                return;
            }
            if (ItemDatabase.getImage(ingItem.getItem_id()) == null) {
                missingReport.add("Ingredient image missing for `" + ingItem.getItem_name() + "` in recipe `" + template.key + "`");
                return;
            }
            ingredients.add(new IngredientRequirement(ingItem.getItem_id(), spec.amount));
        }

        RecipeDifficulty difficulty = difficultyForIngredientCount(ingredients.size());
        unlockedRecipes.add(new Recipe(
            template.key,
            output.getItem_id(),
            output.getItem_name(),
            template.effectText == null || template.effectText.isBlank() ? difficulty.effectText : template.effectText,
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
        if (aliases == null) return null;
        for (String alias : aliases) {
            if (alias == null || alias.isBlank()) continue;
            item itm = ItemDatabase.getItemByName(alias);
            if (itm != null) return itm;
        }
        return null;
    }

    private List<RecipeTemplate> buildPlannedRecipes() {
        List<RecipeTemplate> recipes = new ArrayList<>();

        recipes.add(new RecipeTemplate("tomato_slices", new String[]{"tomato_slices"}, null,
            new IngredientSpec(new String[]{"tomato"}, 1)));
        recipes.add(new RecipeTemplate("baked_potato", new String[]{"baked_potato"}, null,
            new IngredientSpec(new String[]{"potato"}, 1)));
        recipes.add(new RecipeTemplate("grilled_steak", new String[]{"grilled_steak"}, null,
            new IngredientSpec(new String[]{"steak"}, 1)));
        recipes.add(new RecipeTemplate("salad_leaves", new String[]{"salad_leaves"}, null,
            new IngredientSpec(new String[]{"lettuce"}, 1)));

        recipes.add(new RecipeTemplate("fresh_salad", new String[]{"fresh_salad"}, null,
            new IngredientSpec(new String[]{"tomato"}, 1),
            new IngredientSpec(new String[]{"lettuce"}, 1)));
        recipes.add(new RecipeTemplate("beef_potato_stew", new String[]{"beef_potato_stew"}, null,
            new IngredientSpec(new String[]{"potato"}, 1),
            new IngredientSpec(new String[]{"beef"}, 1)));
        recipes.add(new RecipeTemplate("pepper_steak", new String[]{"pepper_steak"}, null,
            new IngredientSpec(new String[]{"steak"}, 1),
            new IngredientSpec(new String[]{"red_bell_pepper", "red bellpepper"}, 1)));
        recipes.add(new RecipeTemplate("pork_cabbage_stir_fry", new String[]{"pork_cabbage_stir-fry"}, null,
            new IngredientSpec(new String[]{"pork"}, 1),
            new IngredientSpec(new String[]{"cabbage"}, 1)));
        recipes.add(new RecipeTemplate("chicken_corn_soup", new String[]{"chicken_corn_soup"}, null,
            new IngredientSpec(new String[]{"chicken_thigh"}, 1),
            new IngredientSpec(new String[]{"corn"}, 1)));
        recipes.add(new RecipeTemplate("roasted_pumpkin_mash", new String[]{"roasted_pumpkin_mash"}, null,
            new IngredientSpec(new String[]{"pumpkin"}, 1),
            new IngredientSpec(new String[]{"sweet_potato"}, 1)));
        recipes.add(new RecipeTemplate("spicy_eggplant", new String[]{"spicy_eggplant"}, null,
            new IngredientSpec(new String[]{"eggplant"}, 1),
            new IngredientSpec(new String[]{"green_chilli", "green chili"}, 1)));
        recipes.add(new RecipeTemplate("root_mix", new String[]{"root_vegetable_mix", "root_mix"}, null,
            new IngredientSpec(new String[]{"daikon"}, 1),
            new IngredientSpec(new String[]{"carrot"}, 1)));

        recipes.add(new RecipeTemplate("garden_salad", new String[]{"garden_salad"}, null,
            new IngredientSpec(new String[]{"tomato"}, 1),
            new IngredientSpec(new String[]{"lettuce"}, 1),
            new IngredientSpec(new String[]{"red_bell_pepper", "red bellpepper"}, 1)));
        recipes.add(new RecipeTemplate("beef_stew", new String[]{"beef_stew"}, null,
            new IngredientSpec(new String[]{"beef"}, 1),
            new IngredientSpec(new String[]{"potato"}, 1),
            new IngredientSpec(new String[]{"carrot"}, 1)));
        recipes.add(new RecipeTemplate("steak_dinner", new String[]{"steak_dinner"}, null,
            new IngredientSpec(new String[]{"steak"}, 1),
            new IngredientSpec(new String[]{"potato"}, 1),
            new IngredientSpec(new String[]{"broccoli"}, 1)));
        recipes.add(new RecipeTemplate("spicy_pork_stir_fry", new String[]{"spicy_pork_stir-fry"}, null,
            new IngredientSpec(new String[]{"pork"}, 1),
            new IngredientSpec(new String[]{"cabbage"}, 1),
            new IngredientSpec(new String[]{"green_chilli", "green chili"}, 1)));
        recipes.add(new RecipeTemplate("chicken_corn_soup_deluxe", new String[]{"chicken_corn_soup_deluxe"}, "Hearty deluxe soup",
            new IngredientSpec(new String[]{"chicken_thigh"}, 1),
            new IngredientSpec(new String[]{"corn"}, 1),
            new IngredientSpec(new String[]{"carrot"}, 1)));
        recipes.add(new RecipeTemplate("roasted_harvest_plate", new String[]{"roasted_harvest_plate"}, null,
            new IngredientSpec(new String[]{"pumpkin"}, 1),
            new IngredientSpec(new String[]{"sweet_potato"}, 1),
            new IngredientSpec(new String[]{"ham"}, 1)));
        recipes.add(new RecipeTemplate("spicy_eggplant_tomato", new String[]{"spicy_eggplant_tomato"}, null,
            new IngredientSpec(new String[]{"eggplant"}, 1),
            new IngredientSpec(new String[]{"tomato"}, 1),
            new IngredientSpec(new String[]{"green_chilli", "green chili"}, 1)));
        recipes.add(new RecipeTemplate("root_vegetable_beef_soup", new String[]{"root_vegetable_beef_soup"}, null,
            new IngredientSpec(new String[]{"daikon"}, 1),
            new IngredientSpec(new String[]{"carrot"}, 1),
            new IngredientSpec(new String[]{"beef"}, 1)));

        return recipes;
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
            if (selectedcook != null) selectedcook.setImage(null);
            if (cookname != null) cookname.setText("Select recipe");
            if (cookeffectlbl != null) cookeffectlbl.setText("Effect:");
            if (cookwaitingtimelbl != null) cookwaitingtimelbl.setText("waiting time:");
            clearIngredientViews();
            if (startcooking != null) startcooking.setDisable(true);
            return;
        }

        if (selectedcook != null) {
            selectedcook.setImage(ItemDatabase.getImage(selectedRecipe.outputItemId));
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
        boolean currentSelectionMatchesJob = selectedRecipe != null && hasJob && selectedRecipe.outputItemId == gameState.getActiveCookingOutputItemId();

        if (ready && currentSelectionMatchesJob) {
            if (cookwaitingtimelbl != null) cookwaitingtimelbl.setText("Ready to collect");
            if (selectedcook != null) {
                selectedcook.setStyle("-fx-effect: dropshadow(gaussian, yellow, 18, 0.8, 0, 0);");
                selectedcook.setOpacity(1.0);
            }
            if (startcooking != null) startcooking.setDisable(true);
            return;
        }

        if (hasJob && currentSelectionMatchesJob) {
            int remaining = (int) Math.ceil(Math.max(0, gameState.getActiveCookingCompleteAtSeconds() - now));
            if (cookwaitingtimelbl != null) cookwaitingtimelbl.setText("waiting time: " + remaining + "s");
            if (selectedcook != null) {
                selectedcook.setStyle("");
                selectedcook.setOpacity(0.9);
            }
            if (startcooking != null) startcooking.setDisable(true);
            return;
        }

        if (selectedRecipe != null) {
            if (cookwaitingtimelbl != null) {
                cookwaitingtimelbl.setText("waiting time: " + getAdjustedWaitSeconds(selectedRecipe) + "s  SP:-" + getAdjustedStaminaCost(selectedRecipe) + "  SR:" + computeSuccessRate(selectedRecipe) + "%");
            }
            if (selectedcook != null) {
                selectedcook.setStyle("");
                selectedcook.setOpacity(canCook(selectedRecipe) ? 1.0 : 0.65);
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
        updateIngredientViews();
        updateCookingStateUi();
    }

    private void handleCollectCookedDish(MouseEvent event) {
        if (selectedRecipe == null) return;
        double now = GameTimeManager.get().totalSecondsProperty().get();
        if (!gameState.hasActiveCookingJob()) return;
        if (selectedRecipe.outputItemId != gameState.getActiveCookingOutputItemId()) return;
        if (!gameState.isCookingReady(now)) return;

        if (gameState.isActiveCookingSuccess()) {
            inventory.addItem(selectedRecipe.outputItemId, gameState.getActiveCookingOutputQuantity());
        }
        gameState.clearCookingJob();
        updateCookingStateUi();
        updateIngredientViews();
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

    private static final class IngredientSpec {
        private final String[] itemAliases;
        private final int amount;

        private IngredientSpec(String[] itemAliases, int amount) {
            this.itemAliases = itemAliases;
            this.amount = amount;
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

    private static final class RecipeTemplate {
        private final String key;
        private final String[] outputAliases;
        private final String effectText;
        private final IngredientSpec[] ingredients;

        private RecipeTemplate(String key, String[] outputAliases, String effectText, IngredientSpec... ingredients) {
            this.key = key;
            this.outputAliases = outputAliases;
            this.effectText = effectText;
            this.ingredients = ingredients;
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
            this.ingredients = ingredients;
        }
    }
}
