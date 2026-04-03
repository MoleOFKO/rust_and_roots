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
public class Left_map_noone_of_oneController extends BaseMapController {

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
    private Rectangle exit_up;
    @FXML
    private Rectangle exit_right;
    @FXML
    private Rectangle spawn_default;
    @FXML
    private Rectangle spawn_from_up;
    @FXML
    private Rectangle spawn_from_right;
    @FXML
    private Group human;
    @FXML
    private Rectangle human_hitbox;
    @FXML
    private ImageView human_img;


    @Override
    protected String mapKey() {
        return "left_map_noone_of_one";
    }

    @Override
    protected void configureExits() {
        registerExit(exit_right, "/view/left_map_noone.fxml", SpawnTag.FROM_LEFT);
        registerExit(exit_up, "/view/left_map_notwo_of_one.fxml", SpawnTag.FROM_DOWN);
    }
    
}
