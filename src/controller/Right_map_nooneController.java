/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
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
public class Right_map_nooneController extends BaseMapController {

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
    private Group human;
    @FXML
    private ImageView human_img;
    @FXML
    private Rectangle human_hitbox;
    @FXML
    private Rectangle exit_left;
    @FXML
    private Rectangle exit_right;
    @FXML
    private Rectangle spawn_default;
    @FXML
    private Rectangle spawn_from_left;
    @FXML
    private Rectangle spawn_from_right;    
    @FXML
    private Rectangle exit_down;
    @FXML
    private Rectangle spawn_from_down;

    @Override
    protected String mapKey() {
        return "right_map_noone";
    }

    @Override
    protected void configureExits() {
         // Coming from home_map into right_map should spawn FROM_LEFT on right_map,
        // and going back to home_map should spawn FROM_RIGHT on home_map.
        registerExit(exit_left, "/view/upper_map.fxml", SpawnTag.FROM_RIGHT);
        registerExit(exit_right, "/view/right_map_noone_of_one.fxml", SpawnTag.FROM_LEFT);

        // Example: right_map -> upper_map
        registerExit(exit_down, "/view/right_map.fxml", SpawnTag.FROM_UP);
        
    }
    
}
