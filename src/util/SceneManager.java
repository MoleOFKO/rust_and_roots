package util;

import game.GameState;
import game.SceneController;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * Centralized scene switching with controller access + GameState injection.
 */
public class SceneManager {

    /** Backwards-compatible signature. */
    public static void switchScene(String fxmlPath) {
        switchScene(fxmlPath, SpawnTag.DEFAULT);
    }

    public static void switchScene(String fxmlPath, SpawnTag spawnTag) {
        try {
            GameState.get().setPendingSpawn(spawnTag);
            
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof SceneController sc) {
                sc.init(GameState.get());
            }

            // Keep your previous size behavior:
            Scene scene = new Scene(root);
            rust_and_roots.Rust_and_roots.stage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
