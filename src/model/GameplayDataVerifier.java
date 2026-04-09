package model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class GameplayDataVerifier {

    private GameplayDataVerifier() {
    }

    public static ValidationSummary prepareForGameplay(String context) {
        ItemDatabase.reloadItems();
        ValidationSummary summary = validateLoadedData();
        summary.printToConsole(context);
        return summary;
    }

    public static ValidationSummary validateLoadedData() {
        List<String> itemIssues = new ArrayList<>();
        List<String> recipeIssues = new ArrayList<>();

        if (!ItemDatabase.isLoaded()) {
            itemIssues.add("Item database could not be loaded.");
            return new ValidationSummary(false, 0, itemIssues, recipeIssues);
        }

        for (item itm : ItemDatabase.getAllItems()) {
            if (itm == null) {
                continue;
            }
            int itemId = itm.getItem_id();
            String failure = ItemDatabase.getVerificationFailure(itemId);
            if (failure == null || failure.isBlank()) {
                continue;
            }
            String itemName = ItemDatabase.getItemName(itemId);
            itemIssues.add(
                "Item " + itemId
                + " [" + ((itemName == null || itemName.isBlank()) ? "Unnamed Item" : itemName) + "]"
                + " failed verification: " + failure
            );
        }

        Set<String> uniqueRecipeIssues = new LinkedHashSet<>();
        for (RecipeCatalog.RecipeDefinition definition : RecipeCatalog.getAllRecipes()) {
            uniqueRecipeIssues.addAll(RecipeCatalog.collectGameplayValidationIssues(definition));
        }
        recipeIssues.addAll(uniqueRecipeIssues);

        return new ValidationSummary(true, ItemDatabase.getAllItems().size(), itemIssues, recipeIssues);
    }

    public static final class ValidationSummary {
        private final boolean databaseReady;
        private final int loadedItemCount;
        private final List<String> itemIssues;
        private final List<String> recipeIssues;

        private ValidationSummary(
            boolean databaseReady,
            int loadedItemCount,
            List<String> itemIssues,
            List<String> recipeIssues
        ) {
            this.databaseReady = databaseReady;
            this.loadedItemCount = Math.max(0, loadedItemCount);
            this.itemIssues = List.copyOf(itemIssues == null ? List.of() : itemIssues);
            this.recipeIssues = List.copyOf(recipeIssues == null ? List.of() : recipeIssues);
        }

        public boolean isDatabaseReady() {
            return databaseReady;
        }

        public int getLoadedItemCount() {
            return loadedItemCount;
        }

        public List<String> getItemIssues() {
            return itemIssues;
        }

        public List<String> getRecipeIssues() {
            return recipeIssues;
        }

        public boolean hasIssues() {
            return !itemIssues.isEmpty() || !recipeIssues.isEmpty();
        }

        public void printToConsole(String context) {
            String normalizedContext = (context == null || context.isBlank()) ? "gameplay bootstrap" : context.trim();
            System.out.println("[GameplayDataVerifier] Verification started for " + normalizedContext + ".");

            if (!databaseReady) {
                for (String issue : itemIssues) {
                    System.out.println("[GameplayDataVerifier] " + issue);
                }
                System.out.println("[GameplayDataVerifier] Verification aborted because item data could not be loaded.");
                return;
            }

            System.out.println("[GameplayDataVerifier] Loaded item records: " + loadedItemCount);

            if (itemIssues.isEmpty()) {
                System.out.println("[GameplayDataVerifier] Item verification passed with no blocked items.");
            } else {
                System.out.println("[GameplayDataVerifier] Invalid item rows skipped for this run:");
                for (String issue : itemIssues) {
                    System.out.println("[GameplayDataVerifier] " + issue);
                }
            }

            if (recipeIssues.isEmpty()) {
                System.out.println("[GameplayDataVerifier] Recipe verification passed with no blocked recipes.");
            } else {
                System.out.println("[GameplayDataVerifier] Invalid recipes skipped for this run:");
                for (String issue : recipeIssues) {
                    System.out.println("[GameplayDataVerifier] " + issue);
                }
            }

            System.out.println("[GameplayDataVerifier] Verification completed.");
        }
    }
}
