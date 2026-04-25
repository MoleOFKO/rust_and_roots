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
}
