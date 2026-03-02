package game;

/**
 * Implement this on FXML controllers that need persistent GameState.
 * SceneManager will call init(...) after the FXML loads.
 */
public interface SceneController {
    void init(GameState gameState);
}
