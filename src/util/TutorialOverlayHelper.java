package util;

import game.GameState;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
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

    private static final double TUTORIAL_BUTTON_SIZE = 28.0;

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
        createAndShowOverlay(root, null, imagePaths);
    }

    public static void show(StackPane root, ActionEvent event, List<String> imagePaths) {
        if (root == null || imagePaths == null || imagePaths.isEmpty()) {
            return;
        }
        Node anchor = event != null && event.getSource() instanceof Node node ? node : null;
        createAndShowOverlay(root, anchor, imagePaths);
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
        createAndShowOverlay(root, null, imagePaths);
    }

    private static void createAndShowOverlay(StackPane root, Node anchor, List<String> imagePaths) {
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
        StackPane tutoclosebtnPane = createTutorialCloseButtonPane(root, anchor);
        overlay.getChildren().add(tutoclosebtnPane);

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

    private static StackPane createTutorialCloseButtonPane(StackPane root, Node anchor) {
        StackPane closePane = new StackPane();
        closePane.setId("tutoclosebtnPane");
        closePane.setManaged(false);
        closePane.setPickOnBounds(true);
        closePane.setMinSize(TUTORIAL_BUTTON_SIZE, TUTORIAL_BUTTON_SIZE);
        closePane.setPrefSize(TUTORIAL_BUTTON_SIZE, TUTORIAL_BUTTON_SIZE);
        closePane.setMaxSize(TUTORIAL_BUTTON_SIZE, TUTORIAL_BUTTON_SIZE);

        Button closeButton = new Button("X");
        closeButton.setId("tutoclosebtn");
        closeButton.setFocusTraversable(false);
        closeButton.setPickOnBounds(true);
        closeButton.setMinSize(0, 0);
        closeButton.prefWidthProperty().bind(closePane.widthProperty());
        closeButton.prefHeightProperty().bind(closePane.heightProperty());
        closeButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        closeButton.setStyle("""
            -fx-background-radius: 100%;
            -fx-background-color: rgba(0, 0, 0, 0.55);
            -fx-border-color: yellow;
            -fx-border-radius: 100%;
            -fx-border-width: 1;
            -fx-padding: 0;
            -fx-text-fill: yellow;
            -fx-font-weight: bold;
            -fx-font-size: 12px;
            """);
        closeButton.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            event.consume();
            closeTutorialOverlay(closePane);
        });
        closeButton.setOnAction(event -> {
            event.consume();
            closeTutorialOverlay(closePane);
        });

        closePane.getChildren().add(closeButton);
        positionTutorialCloseButtonPane(root, anchor, closePane, closeButton);
        Platform.runLater(() -> positionTutorialCloseButtonPane(root, anchor, closePane, closeButton));
        root.widthProperty().addListener((obs, oldValue, newValue) -> positionTutorialCloseButtonPane(root, anchor, closePane, closeButton));
        root.heightProperty().addListener((obs, oldValue, newValue) -> positionTutorialCloseButtonPane(root, anchor, closePane, closeButton));
        return closePane;
    }

    private static void closeTutorialOverlay(Node closeButton) {
        Node overlay = closeButton == null ? null : closeButton.getParent();
        if (overlay != null && overlay.getParent() instanceof StackPane parent) {
            parent.getChildren().remove(overlay);
        }
    }

    private static void positionTutorialCloseButtonPane(StackPane root, Node anchor, StackPane closePane, Button closeButton) {
        if (root == null || closePane == null || closeButton == null) return;

        Node tutorialButton = anchor != null ? anchor : root.lookup("#btntuto");
        if (tutorialButton != null && tutorialButton.getScene() != null) {
            Bounds buttonBounds = tutorialButton.localToScene(tutorialButton.getBoundsInLocal());
            Bounds rootBounds = root.sceneToLocal(buttonBounds);
            double width = rootBounds.getWidth() > 0.0 ? rootBounds.getWidth() : TUTORIAL_BUTTON_SIZE;
            double height = rootBounds.getHeight() > 0.0 ? rootBounds.getHeight() : TUTORIAL_BUTTON_SIZE;
            closePane.setMinSize(width, height);
            closePane.setPrefSize(width, height);
            closePane.setMaxSize(width, height);
            closeButton.setFont(Font.font("System Bold", Math.max(12.0, Math.min(width, height) * 0.43)));
            closePane.resizeRelocate(
                rootBounds.getMinX(),
                rootBounds.getMinY(),
                width,
                height
            );
            return;
        }

        closePane.setMinSize(TUTORIAL_BUTTON_SIZE, TUTORIAL_BUTTON_SIZE);
        closePane.setPrefSize(TUTORIAL_BUTTON_SIZE, TUTORIAL_BUTTON_SIZE);
        closePane.setMaxSize(TUTORIAL_BUTTON_SIZE, TUTORIAL_BUTTON_SIZE);
        closeButton.setFont(Font.font("System Bold", 12.0));
        closePane.resizeRelocate(
            Math.max(0.0, root.getWidth() - TUTORIAL_BUTTON_SIZE),
            0.0,
            TUTORIAL_BUTTON_SIZE,
            TUTORIAL_BUTTON_SIZE
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
