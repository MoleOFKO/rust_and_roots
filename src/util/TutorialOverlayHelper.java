package util;

import game.GameState;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public final class TutorialOverlayHelper {

    public static final List<String> GLOBAL_BUTTON_TUTORIAL_IMAGES = List.of(
        "/image/Toturial/Move.png",
        "/image/Toturial/Run.png",
        "/image/Toturial/before_goes_to_hotbar.png",
        "/image/Toturial/goes_to_hotbar.png",
        "/image/Toturial/Wp_hold.png",
        "/image/Toturial/After_wp_hold.png",
        "/image/Toturial/Attack.png",
        "/image/Toturial/Menu.png",
        "/image/Toturial/Inventory.png",
        "/image/Toturial/Stats.png",
        "/image/Toturial/Upgrade Stats.png",
        "/image/Toturial/Skill.png",
        "/image/Toturial/Upgrade Skill.png",
        "/image/Toturial/Setting.png",
        "/image/Toturial/Chopping.png",
        "/image/Toturial/Chopping Using Axe.png",
        "/image/Toturial/Mining.png",
        "/image/Toturial/Mining Using Pickaxe.png"     
    );

    private TutorialOverlayHelper() {
    }

    public static void show(StackPane root, List<String> imagePaths) {
        if (root == null || imagePaths == null || imagePaths.isEmpty()) {
            return;
        }
        createAndShowOverlay(root, imagePaths);
    }

    public static void showIfNeeded(StackPane root, GameState gameState, String tutorialKey, List<String> imagePaths) {
        if (root == null || gameState == null || tutorialKey == null || tutorialKey.isBlank()
                || imagePaths == null || imagePaths.isEmpty()) {
            return;
        }
        if (gameState.hasSeenTutorial(tutorialKey)) {
            return;
        }

        gameState.markTutorialSeen(tutorialKey);
        createAndShowOverlay(root, imagePaths);
    }

    private static void createAndShowOverlay(StackPane root, List<String> imagePaths) {
        StackPane overlay = new StackPane();
        overlay.setManaged(true);
        overlay.setVisible(true);
        overlay.setDisable(false);
        overlay.setPickOnBounds(true);

        Rectangle shade = new Rectangle();
        shade.widthProperty().bind(root.widthProperty());
        shade.heightProperty().bind(root.heightProperty());
        shade.setFill(Color.rgb(0, 0, 0, 0.85));

        StackPane content = new StackPane();
        content.setAlignment(Pos.CENTER);

        Label title = new Label();
        title.setAlignment(Pos.CENTER);
        title.setWrapText(true);
        title.setMaxWidth(Double.MAX_VALUE);
        title.setTextFill(Color.WHITE);
        title.fontProperty().bind(Bindings.createObjectBinding(
            () -> Font.font("System Bold", clamp(root.getWidth() * 0.04, 18, 34)),
            root.widthProperty()
        ));

        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(Bindings.createDoubleBinding(
            () -> clamp(root.getWidth() * 0.76, 260, 1100),
            root.widthProperty()
        ));
        imageView.fitHeightProperty().bind(Bindings.createDoubleBinding(
            () -> clamp(root.getHeight() * 0.62, 180, 820),
            root.heightProperty()
        ));

        Label prompt = new Label("Tap anywhere to continue");
        prompt.setWrapText(true);
        prompt.setAlignment(Pos.CENTER);
        prompt.setTextFill(Color.WHITE);
        prompt.fontProperty().bind(Bindings.createObjectBinding(
            () -> Font.font("System Bold", clamp(root.getWidth() * 0.028, 14, 26)),
            root.widthProperty()
        ));

        VBox contentBox = new VBox(18, title, imageView, prompt);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.maxWidthProperty().bind(Bindings.createDoubleBinding(
            () -> clamp(root.getWidth() * 0.88, 320, 1250),
            root.widthProperty()
        ));
        contentBox.maxHeightProperty().bind(Bindings.createDoubleBinding(
            () -> clamp(root.getHeight() * 0.9, 240, 900),
            root.heightProperty()
        ));

        content.getChildren().add(contentBox);
        overlay.getChildren().addAll(shade, content);
        Button tutoclosebtn = createTutorialCloseButton(root);
        overlay.getChildren().add(tutoclosebtn);

        final int[] index = {0};
        Runnable updateStep = () -> {
            String imagePath = imagePaths.get(index[0]);
            title.setText(extractTitle(imagePath));
            imageView.setImage(new Image(TutorialOverlayHelper.class.getResource(imagePath).toExternalForm()));
        };
        updateStep.run();

        overlay.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            event.consume();
            index[0]++;
            if (index[0] >= imagePaths.size()) {
                root.getChildren().remove(overlay);
                return;
            }
            updateStep.run();
        });

        root.getChildren().add(overlay);
    }

    private static Button createTutorialCloseButton(StackPane root) {
        Button closeButton = new Button("X");
        closeButton.setId("tutoclosebtn");
        closeButton.setManaged(false);
        closeButton.setFocusTraversable(false);
        closeButton.setPickOnBounds(true);
        closeButton.setStyle("""
            -fx-background-radius: 100%;
            -fx-background-color: rgba(0, 0, 0, 0.55);
            -fx-border-color: yellow;
            -fx-border-radius: 100%;
            -fx-text-fill: yellow;
            -fx-font-weight: bold;
            """);
        closeButton.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            event.consume();
            closeTutorialOverlay(closeButton);
        });
        closeButton.setOnAction(event -> {
            event.consume();
            closeTutorialOverlay(closeButton);
        });

        positionTutorialCloseButton(root, closeButton);
        Platform.runLater(() -> positionTutorialCloseButton(root, closeButton));
        root.widthProperty().addListener((obs, oldValue, newValue) -> positionTutorialCloseButton(root, closeButton));
        root.heightProperty().addListener((obs, oldValue, newValue) -> positionTutorialCloseButton(root, closeButton));
        return closeButton;
    }

    private static void closeTutorialOverlay(Node closeButton) {
        Node overlay = closeButton == null ? null : closeButton.getParent();
        if (overlay != null && overlay.getParent() instanceof StackPane parent) {
            parent.getChildren().remove(overlay);
        }
    }

    private static void positionTutorialCloseButton(StackPane root, Button closeButton) {
        if (root == null || closeButton == null) return;

        Node tutorialButton = root.lookup("#btntuto");
        if (tutorialButton != null && tutorialButton.getScene() != null) {
            Bounds buttonBounds = tutorialButton.localToScene(tutorialButton.getBoundsInLocal());
            Bounds rootBounds = root.sceneToLocal(buttonBounds);
            closeButton.resizeRelocate(
                rootBounds.getMinX(),
                rootBounds.getMinY(),
                Math.max(28.0, rootBounds.getWidth()),
                Math.max(28.0, rootBounds.getHeight())
            );
            return;
        }

        closeButton.resizeRelocate(
            Math.max(0.0, root.getWidth() - 32.0),
            0.0,
            32.0,
            32.0
        );
    }

    private static String extractTitle(String imagePath) {
        int slash = imagePath.lastIndexOf('/');
        int dot = imagePath.lastIndexOf('.');
        int start = slash >= 0 ? slash + 1 : 0;
        int end = dot > start ? dot : imagePath.length();
        return imagePath.substring(start, end);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
