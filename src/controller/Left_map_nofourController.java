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
public class Left_map_nofourController extends BaseMapController {

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
    private Rectangle wall4;
    @FXML
    private Rectangle wall5;
    @FXML
    private Rectangle wall6;
    @FXML
    private Rectangle wall7;
    @FXML
    private Rectangle wall8;
    @FXML
    private Rectangle wall9;
    @FXML
    private Rectangle wall10;
    @FXML
    private Rectangle wall11;
    @FXML
    private Rectangle wall12;
    @FXML
    private Rectangle wall13;
    @FXML
    private Rectangle wall14;
    @FXML
    private Rectangle exit_right;
    @FXML
    private Rectangle spawn_default;
    @FXML
    private Rectangle spawn_from_right;
    @FXML
    private Group human;
    @FXML
    private Rectangle human_hitbox;
    @FXML
    private ImageView human_img;
    @FXML
    private Rectangle exit_down;
    @FXML
    private Rectangle spawn_from_down;

    @Override
    protected String mapKey() {
        return "left_map_nofour";
    }

    @Override
    protected void configureExits() {
        registerExit(exit_right, "/view/upper_map_nofour.fxml", SpawnTag.FROM_LEFT);
        registerExit(exit_down, "/view/left_map_nothree.fxml", SpawnTag.FROM_UP);
    }
    
    
}
