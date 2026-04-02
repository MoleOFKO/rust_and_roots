package util;

import game.GameState;
import game.SceneController;
import java.io.IOException;
import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SceneManager {

    public static void switchScene(String fxmlPath) {
        switchScene(fxmlPath, SpawnTag.DEFAULT);
    }

    public static void switchScene(String fxmlPath, SpawnTag spawnTag) {
        try {
            GameState.get().setPendingSpawn(spawnTag);

            Stage stage = rust_and_roots.Rust_and_roots.stage;
            if (stage == null) throw new IllegalStateException("Primary stage is null");

            Scene currentScene = stage.getScene();

            // first time only
            if (currentScene == null) {
                FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
                Parent root = loader.load();

                Object controller = loader.getController();
                if (controller instanceof SceneController sc) {
                    sc.init(GameState.get());
                }

                Parent responsiveRoot = wrapResponsiveRoot(root);
                currentScene = new Scene(responsiveRoot);
                stage.setScene(currentScene);
                return;
            }

            // loading screen
            StackPane loadingRoot = new StackPane();
            loadingRoot.setStyle("-fx-background-color: black;");

            Label loadingLabel = new Label("Loading...");
            loadingLabel.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;"
            );

            loadingRoot.setAlignment(Pos.CENTER);
            loadingRoot.getChildren().add(loadingLabel);

            currentScene.setRoot(loadingRoot);

            PauseTransition pause = new PauseTransition(Duration.millis(300));
            pause.setOnFinished(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
                    Parent root = loader.load();

                    Object controller = loader.getController();
                    if (controller instanceof SceneController sc) {
                        sc.init(GameState.get());
                    }

                    stage.getScene().setRoot(wrapResponsiveRoot(root));

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            pause.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Parent wrapResponsiveRoot(Parent content) {
        StackPane container = new StackPane(content);
        container.setStyle("-fx-background-color: black;");
        container.setAlignment(Pos.CENTER);

        Runnable updateScale = () -> {
            Bounds bounds = content.getLayoutBounds();
            double baseW = bounds.getWidth();
            double baseH = bounds.getHeight();
            double availableW = container.getWidth();
            double availableH = container.getHeight();

            if (baseW <= 0 || baseH <= 0 || availableW <= 0 || availableH <= 0) return;

            double scale = Math.min(availableW / baseW, availableH / baseH);
            if (scale <= 0) scale = 1.0;

            content.setScaleX(scale);
            content.setScaleY(scale);
        };

        container.widthProperty().addListener((obs, oldV, newV) -> updateScale.run());
        container.heightProperty().addListener((obs, oldV, newV) -> updateScale.run());
        content.layoutBoundsProperty().addListener((obs, oldV, newV) -> updateScale.run());

        javafx.application.Platform.runLater(updateScale);
        return container;
    }
}
