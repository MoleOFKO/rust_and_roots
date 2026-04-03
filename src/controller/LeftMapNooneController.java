package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import util.SpawnTag;

/**
 * FXML Controller class
 *
 * @author Kaung Zin
 */
public class LeftMapNooneController extends BaseMapController {

    @FXML
    private StackPane view;
    @FXML
    private Group world;
    @FXML
    private Group walls;
    @FXML
    private Rectangle wall;
    @FXML
    private Rectangle wall1;
    @FXML
    private Rectangle wall2;
    @FXML
    private Rectangle wall3;
    @FXML
    private Rectangle exit_bottom;
    @FXML
    private Rectangle exit_up;
    @FXML
    private Rectangle exit_left;
    @FXML
    private Rectangle spawn_default;
    @FXML
    private Rectangle spawn_from_left;
    @FXML
    private Rectangle spawn_from_up;
    @FXML
    private Rectangle spawn_from_bottom;
    @FXML
    private Group human;
    @FXML
    private Rectangle human_hitbox;
    @FXML
    private ImageView human_img;
  

    @Override
    protected String mapKey() {
        return "left_map_noone";
    }

    @Override
    protected void configureExits() {
        registerExit(exit_left, "/view/left_map_noone_of_one.fxml", SpawnTag.FROM_RIGHT);
        registerExit(exit_up, "/view/left_map_notwo.fxml", SpawnTag.FROM_DOWN);
        registerExit(exit_down, "/view/left_map.fxml", SpawnTag.FROM_UP);
    }
    
}
