package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import util.SceneManager;
import util.SpawnTag;
import util.WorldScaler;

public class Game_overController implements Initializable {

    @FXML private StackPane view;
    @FXML private Group uiRoot;
    @FXML private Button btnMainMenu;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (view != null && uiRoot != null) {
            new WorldScaler(view, uiRoot);
        }
    }

    @FXML
    private void handleMainMenuAction(ActionEvent event) {
        SceneManager.switchScene("/view/main_menu.fxml", SpawnTag.DEFAULT);
        if (event != null) {
            event.consume();
        }
    }
}

