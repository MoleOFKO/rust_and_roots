package util;

import game.GameState;
import game.SceneController;
import java.io.IOException;
import java.util.Locale;
import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SceneManager {

    private static MediaPlayer bgmPlayer;
    private static String currentBgmResource;

    public static void switchScene(String fxmlPath) {
        switchScene(fxmlPath, SpawnTag.DEFAULT);
    }

    public static void switchScene(String fxmlPath, SpawnTag spawnTag) {
        try {
            GameState.get().setPendingSpawn(spawnTag);
            updateBgmForScene(fxmlPath);

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

    private enum BgmTrack {
        ACROSS_THE_JADE_ARCHIPELAGO,
        WHERE_THE_TIDE_SLEEPS
    }

    private static void updateBgmForScene(String fxmlPath) {
        if (javafx.application.Platform.isFxApplicationThread()) {
            applyBgmForScene(fxmlPath);
        } else {
            javafx.application.Platform.runLater(() -> applyBgmForScene(fxmlPath));
        }
    }

    private static void applyBgmForScene(String fxmlPath) {
        BgmTrack desiredTrack = resolveBgmTrackForScene(fxmlPath);
        String desiredResource = switch (desiredTrack) {
            case ACROSS_THE_JADE_ARCHIPELAGO -> "/mp3/Across_the_Jade_Archipelago.mp3";
            case WHERE_THE_TIDE_SLEEPS -> "/mp3/Where_the_Tide_Sleeps.mp3";
        };

        if (desiredResource != null
            && desiredResource.equals(currentBgmResource)
            && bgmPlayer != null) {
            // Keep current playback running across same-category scenes.
            if (bgmPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
                bgmPlayer.play();
            }
            return;
        }

        stopAndDisposeBgm();

        currentBgmResource = desiredResource;
        if (desiredResource == null || desiredResource.isBlank()) {
            return;
        }

        var url = SceneManager.class.getResource(desiredResource);
        if (url == null) {
            System.err.println("[SceneManager] Missing BGM resource: " + desiredResource);
            currentBgmResource = null;
            return;
        }

        Media music = new Media(url.toExternalForm());
        bgmPlayer = new MediaPlayer(music);
        bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        bgmPlayer.play();
    }

    private static void stopAndDisposeBgm() {
        if (bgmPlayer == null) {
            return;
        }

        try {
            bgmPlayer.stop();
        } catch (Exception ignored) {
        }
        try {
            bgmPlayer.dispose();
        } catch (Exception ignored) {
        }

        bgmPlayer = null;
        currentBgmResource = null;
    }

    private static BgmTrack resolveBgmTrackForScene(String fxmlPath) {
        String normalized = (fxmlPath == null) ? "" : fxmlPath.trim().toLowerCase(Locale.ROOT);

        // Program start + menus/login/signup/admin screens.
        if (normalized.endsWith("/login.fxml")
            || normalized.endsWith("/main_menu.fxml")
            || normalized.endsWith("/otp.fxml")
            || normalized.contains("/account_")
            || normalized.endsWith("/edit_character.fxml")
            || normalized.endsWith("/char_edit_btn.fxml")
            || normalized.endsWith("/edit_item.fxml")
            || normalized.endsWith("/item_add_btn.fxml")
            || normalized.endsWith("/item_edit_btn.fxml")) {
            return BgmTrack.ACROSS_THE_JADE_ARCHIPELAGO;
        }

        // New game, save/load, and gameplay.
        return BgmTrack.WHERE_THE_TIDE_SLEEPS;
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
