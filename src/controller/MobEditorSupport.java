package controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public final class MobEditorSupport {

    private static final String MOB_IMAGE_DIRECTORY = "E:\\PJ\\rust_and_roots\\src\\image\\mob\\";
    private static final String[] MOB_IMAGE_DIRECTORIES = {
        "E:\\PJ\\rust_and_roots\\src\\image\\mob\\",
        "E:\\PJ\\rust_and_roots\\src\\image\\"
    };

    private MobEditorSupport() {
    }

    public static String choosePreviewAndCopyMobImage(Window owner, ImageView preview) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Mob Image");
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

    public static String copyImageToFolder(File sourceFile) throws IOException {
        File imageDir = new File(MOB_IMAGE_DIRECTORY);
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }

        String fileName = sourceFile.getName();
        File destination = new File(imageDir, fileName);
        Files.copy(sourceFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    public static int parseRequiredInt(TextField field, String name) {
        try {
            return Integer.parseInt(field.getText().trim());
        } catch (Exception ex) {
            throw new NumberFormatException(name + " must be a number.");
        }
    }

    public static void fitImageToPreviewBox(ImageView preview) {
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

    public static Image loadMobImage(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return null;
        }

        for (String candidate : buildImageCandidates(fileName)) {
            for (String directory : MOB_IMAGE_DIRECTORIES) {
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

        String normalized = fileName.replace('\\', '/');
        if (!normalized.equals(fileName)) {
            candidates.add(normalized);
        }

        if (normalized.startsWith("mob/")) {
            candidates.add(normalized.substring("mob/".length()));
        }

        return candidates;
    }
}
