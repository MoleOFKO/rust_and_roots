package controller;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import model.Farm;
import util.SpawnTag;

public class RightMapController extends BaseMapController {

    @FXML
    private StackPane view;
    @FXML
    private Group world;
    @FXML
    private ImageView bg;
    @FXML
    private Group walls;
    @FXML
    private Rectangle wall1;
    @FXML
    private Rectangle wall2;
    @FXML
    private Rectangle wall3;
    @FXML
    private Rectangle wall4;
    @FXML
    private Group human;
    @FXML
    private ImageView human_img;
    @FXML
    private Rectangle human_hitbox;
    @FXML
    private Rectangle spawn_default;
    @FXML
    private Rectangle spawn_from_left;
    @FXML
    private Rectangle spawn_from_right;
    @FXML
    private Rectangle spawn_from_up;
    @FXML
    private Rectangle spawn_from_down;
    @FXML
    private Rectangle spawn_from_door;
    @FXML
    private Rectangle exit_left;
    @FXML
    private Rectangle exit_right;
    @FXML
    private Rectangle exit_up;
    @FXML
    private Rectangle exit_down;
    @FXML
    private Rectangle exit_door;

    @Override
    protected String mapKey() {
        return "right_map";
    }

    @Override
    protected void configureExits() {
        // Coming from home_map into right_map should spawn FROM_LEFT on right_map,
        // and going back to home_map should spawn FROM_RIGHT on home_map.
        registerExit(exit_left, "/view/home_map.fxml", SpawnTag.FROM_RIGHT);

        // Example: right_map -> upper_map
        registerExit(exit_up, "/view/upper_map.fxml", SpawnTag.FROM_DOWN);
    }

    @Override
    protected void configureFarm(Farm farm) {
        // If right_map has a farm, configure it here (optional)
        // farm.setAvailableSeeds(...);
        // farm.setOnHarvest(...);
    }
}