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
public class RightMapnoone_of_oneController extends BaseMapController {

    @FXML
    private StackPane view;
    @FXML
    private Group world;
    @FXML
    private ImageView bg;
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
    private Rectangle wall19;
    @FXML
    private Rectangle wall13;
    @FXML
    private Rectangle wall14;
    @FXML
    private Rectangle wall15;
    @FXML
    private Rectangle wall16;
    @FXML
    private Rectangle wall17;
    @FXML
    private Rectangle wall18;
    @FXML
    private Rectangle wall20;
    @FXML
    private Rectangle wall21;
    @FXML
    private Rectangle wall22;
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
    private Rectangle spawn_from_up;
    @FXML
    private Rectangle exit_left;
    @FXML
    private Rectangle exit_up;

    @Override
    protected String mapKey() {
     return "right_map_noone_of_one";
    }

    @Override
    protected void configureExits() {
         // Coming from home_map into right_map should spawn FROM_LEFT on right_map,
        // and going back to home_map should spawn FROM_RIGHT on home_map.
        registerExit(exit_left, "/view/right_map_noone.fxml", SpawnTag.FROM_RIGHT);

        // Example: right_map -> upper_map
        registerExit(exit_up, "/view/right_map_notwo_of_one.fxml", SpawnTag.FROM_DOWN);
    }
 
    
}
