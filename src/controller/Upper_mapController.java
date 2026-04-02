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
 * @author Kaung Zin
 */
public class Upper_mapController extends BaseMapController {

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
    private Rectangle exit_left;
    @FXML
    private Rectangle exit_up;
    @FXML
    private Rectangle exit_bottom;
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
        return "upper_map";
    }

    @Override
    protected void configureExits() {
        registerExit(exit_up, "/view/upper_map_notwo.fxml", SpawnTag.FROM_DOWN);
        registerExit(exit_down, "/view/home_map.fxml", SpawnTag.FROM_UP);
        registerExit(exit_right, "/view/right_map_noone.fxml", SpawnTag.FROM_LEFT);
    }
  
    
}
