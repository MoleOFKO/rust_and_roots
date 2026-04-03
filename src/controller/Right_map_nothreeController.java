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
public class Right_map_nothreeController extends BaseMapController {

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
    private Rectangle exit_left;
    private Rectangle exit_bottom;
    @FXML
    private Rectangle exit_right;
    @FXML
    private Rectangle exit_up;
    @FXML
    private Rectangle wall11;
    @FXML
    private Rectangle wall12;
    @FXML
    private Rectangle wall13;
    @FXML
    private Rectangle wall14;
    @FXML
    private Group world;
    @FXML
    private Rectangle spawn_from_down;
    @FXML
    private Rectangle exit_down;

    @Override
    protected String mapKey() {
        return "right_map_nothree";
    }

    @Override
    protected void configureExits() {
        registerExit(exit_left, "/view/upper_map_nothree.fxml", SpawnTag.FROM_RIGHT);
        registerExit(exit_right, "/view/right_map_nothree_of_one.fxml", SpawnTag.FROM_LEFT);
        registerExit(exit_up, "/view/right_map_nofour.fxml", SpawnTag.FROM_DOWN);
        registerExit(exit_bottom, "/view/right_mapnotwo.fxml", SpawnTag.FROM_UP);
    }
   
    
}
