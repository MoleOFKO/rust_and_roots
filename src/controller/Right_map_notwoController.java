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
 * @author MITUSER-1
 */
public class Right_map_notwoController extends BaseMapController{

    @FXML
    private StackPane view;
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
    private Rectangle wall51;
    @FXML
    private Rectangle exit_left;
    @FXML
    private Rectangle exit_up;
    @FXML
    private Rectangle exit_right;
    @FXML
    private Rectangle spawn_default;
    @FXML
    private Rectangle spawn_from_left;
    @FXML
    private Rectangle spawn_from_up;
    @FXML
    private Rectangle spawn_from_right;
    @FXML
    private Group human;
    @FXML
    private ImageView human_img;
    @FXML
    private Rectangle human_hitbox;

    @Override
    protected String mapKey() {
        return "right_map_notwo";
    }

    @Override
    protected void configureExits() {
         // Coming from home_map into right_map should spawn FROM_LEFT on right_map,
        // and going back to home_map should spawn FROM_RIGHT on home_map.
        registerExit(exit_left, "/view/upper_map_notwo.fxml", SpawnTag.FROM_RIGHT);
        registerExit(exit_right, "/view/right_map_notwo_of_one.fxml", SpawnTag.FROM_LEFT);

        // Example: right_map -> upper_map
        registerExit(exit_up, "/view/right_map_nothree.fxml", SpawnTag.FROM_DOWN);
    }

    
}
