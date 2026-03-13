package util;

import game.GameState;
import game.SceneController;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Centralized scene switching with controller access + GameState injection.
 */
public class SceneManager {

    public static void switchScene(String fxmlPath) {
        switchScene(fxmlPath, SpawnTag.DEFAULT);
    }

    public static void switchScene(String fxmlPath, SpawnTag spawnTag) {
        try {
            GameState.get().setPendingSpawn(spawnTag);
            
            Stage stage = rust_and_roots.Rust_and_roots.stage;
            if (stage == null) throw new IllegalStateException("Primary stage is null");

            // ---- Capture current window state BEFORE changing scene ----
//            boolean wasFullScreen = stage.isFullScreen();
//            boolean wasMaximized = stage.isMaximized();
//
//            double prevW = stage.getWidth();
//            double prevH = stage.getHeight();
//            double prevX = stage.getX();
//            double prevY = stage.getY();
            
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof SceneController sc) {
                sc.init(GameState.get());
            }

            
            Scene existingScene = stage.getScene();

            if (existingScene == null) {
                // first time only
                existingScene = new Scene(root);
                stage.setScene(existingScene);
            } else {
                // reuse the same scene, only replace root
                existingScene.setRoot(root);
            }
            
//           Platform.runLater(() -> {
//                if (wasFullScreen) {
//                    stage.setFullScreen(true);
//                    return;
//                }
//
//                if (wasMaximized) {
//                    stage.setMaximized(true);
//                    return;
//                }
//
//                // normal windowed mode
//                stage.setFullScreen(false);
//                stage.setMaximized(false);
//                stage.setWidth(prevW);
//                stage.setHeight(prevH);
//                stage.setX(prevX);
//                stage.setY(prevY);
//            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
