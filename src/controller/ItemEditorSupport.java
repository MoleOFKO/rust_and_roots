package controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;

final class ItemEditorSupport {

    private static final String ITEM_IMAGE_DIRECTORY = "E:\\PJ\\rust_and_roots\\src\\image\\items\\";
    private static final String[] ITEM_IMAGE_DIRECTORIES = {
        "E:\\PJ\\rust_and_roots\\src\\image\\items\\",
        "E:\\PJ\\rust_and_roots\\src\\image\\crops\\",
        "E:\\PJ\\rust_and_roots\\src\\image\\recipe\\",
        "E:\\PJ\\rust_and_roots\\src\\image\\weapons\\",
        "E:\\PJ\\rust_and_roots\\src\\image\\"
    };

    private ItemEditorSupport() {
    }

    static String choosePreviewAndCopyItemImage(Window owner, ImageView preview) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Product Image");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image File", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(owner);
        if (selectedFile == null) {
            return null;
        }

        preview.setImage(new Image(selectedFile.toURI().toString()));
        fitImageToPreviewBox(preview);
        return copyImageToFolder(selectedFile);
    }

    static String copyImageToFolder(File sourceFile) throws IOException {
        File imageDir = new File(ITEM_IMAGE_DIRECTORY);
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }

        String fileName = sourceFile.getName();
        File destination = new File(imageDir, fileName);
        Files.copy(sourceFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    static void loadItemTypes(Connection con, ComboBox<ItemTypeOption> comboBox) throws SQLException {
        List<ItemTypeOption> options = new ArrayList<>();
        String sql = "SELECT item_type_id, item_type_name FROM item_types ORDER BY item_type_name";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet result = ps.executeQuery()) {
            while (result.next()) {
                options.add(new ItemTypeOption(
                    result.getInt("item_type_id"),
                    result.getString("item_type_name")
                ));
            }
        }
        comboBox.getItems().setAll(options);
        if (!options.isEmpty()) {
            comboBox.getSelectionModel().selectFirst();
        }
    }

    static void selectItemTypeById(ComboBox<ItemTypeOption> comboBox, int itemTypeId) {
        for (ItemTypeOption option : comboBox.getItems()) {
            if (option.id() == itemTypeId) {
                comboBox.getSelectionModel().select(option);
                return;
            }
        }
    }

    static int parseRequiredInt(TextField field, String name) {
        try {
            return Integer.parseInt(field.getText().trim());
        } catch (Exception ex) {
            throw new NumberFormatException(name + " must be a number.");
        }
    }

    static ItemStatPolicy policyFor(ItemTypeOption option) {
        return ItemStatPolicy.forTypeName(option == null ? null : option.name());
    }

    static void applyStatPolicy(
        ComboBox<ItemTypeOption> itemTypeBox,
        TextField str,
        TextField agi,
        TextField dex,
        TextField vit,
        TextField hp,
        TextField stamina
    ) {
        ItemStatPolicy policy = policyFor(itemTypeBox == null ? null : itemTypeBox.getSelectionModel().getSelectedItem());
        applyFieldPolicy(str, policy.allowStr());
        applyFieldPolicy(agi, policy.allowAgi());
        applyFieldPolicy(dex, policy.allowDex());
        applyFieldPolicy(vit, policy.allowVit());
        applyFieldPolicy(hp, policy.allowHp());
        applyFieldPolicy(stamina, policy.allowStamina());
    }

    static int parsePolicyInt(TextField field, String name, boolean allowed) {
        if (!allowed) {
            return 0;
        }
        return parseRequiredInt(field, name);
    }

    static ItemStats normalizeStats(ItemTypeOption option, int str, int agi, int dex, int vit, int hp, int stamina) {
        ItemStatPolicy policy = policyFor(option);
        return new ItemStats(
            policy.allowStr() ? str : 0,
            policy.allowAgi() ? agi : 0,
            policy.allowDex() ? dex : 0,
            policy.allowVit() ? vit : 0,
            policy.allowHp() ? hp : 0,
            policy.allowStamina() ? stamina : 0
        );
    }

    private static void applyFieldPolicy(TextField field, boolean allowed) {
        if (field == null) {
            return;
        }
        field.setDisable(!allowed);
        field.setEditable(allowed);
        if (!allowed) {
            field.setText("0");
        } else if (field.getText() == null || field.getText().isBlank()) {
            field.setText("0");
        }
    }

    static void fitImageToPreviewBox(ImageView preview) {
        if (preview == null) {
            return;
        }
        preview.setPreserveRatio(false);
        if (preview.getFitWidth() <= 0) {
            preview.setFitWidth(preview.getBoundsInLocal().getWidth());
        }
        if (preview.getFitHeight() <= 0) {
            preview.setFitHeight(preview.getBoundsInLocal().getHeight());
        }
    }

    static String itemImagePath(String fileName) {
        return ITEM_IMAGE_DIRECTORY + fileName;
    }

    static Image loadItemImage(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return null;
        }

        for (String candidate : buildImageCandidates(fileName)) {
            for (String directory : ITEM_IMAGE_DIRECTORIES) {
                File imageFile = new File(directory + candidate);
                if (imageFile.exists() && imageFile.isFile()) {
                    return new Image(imageFile.toURI().toString());
                }
            }
        }
        return null;
    }

    private static List<String> buildImageCandidates(String fileName) {
        List<String> candidates = new ArrayList<>();
        candidates.add(fileName);

        if (fileName.endsWith("_fruit.png")) {
            candidates.add(fileName.replace("_fruit.png", ".png"));
        }
        if (fileName.endsWith("_brick.png")) {
            candidates.add(fileName.replace("_brick.png", ".png"));
        }

        return candidates;
    }

    record ItemTypeOption(int id, String name) {
        @Override
        public String toString() {
            return name;
        }
    }

    record ItemStats(int str, int agi, int dex, int vit, int hp, int stamina) {
    }

    record ItemStatPolicy(boolean allowStr, boolean allowAgi, boolean allowDex, boolean allowVit, boolean allowHp, boolean allowStamina) {
        static ItemStatPolicy forTypeName(String typeName) {
            String type = typeName == null ? "" : typeName.trim().toLowerCase();
            return switch (type) {
                case "crop" -> new ItemStatPolicy(false, false, false, true, false, false);
                case "equipment", "equipments", "tool", "weapon" -> new ItemStatPolicy(true, true, true, true, false, false);
                case "edible", "food" -> new ItemStatPolicy(false, false, false, false, true, true);
                case "seed", "seeds", "recipe", "recipes", "inedible", "material", "materials" ->
                    new ItemStatPolicy(false, false, false, false, false, false);
                default -> new ItemStatPolicy(false, false, false, false, false, false);
            };
        }
    }
}
