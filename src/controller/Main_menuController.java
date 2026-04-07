/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import util.WorldScaler;

/**
 * FXML Controller class
 *
 * @author larry
 */
public class Main_menuController implements Initializable {

    @FXML
    private Button btnloadg;
    @FXML
    private Button btnexit;
    @FXML
    private Button btnnewg;
    @FXML 
    private StackPane view;
    @FXML 
    private Group uiRoot;

    Parent root;
    @FXML
    private ImageView bgImage;
    @FXML
    private Button btnlogout;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        new WorldScaler(view, uiRoot);
    }    

    @FXML
    private void handlebtnloadgaction(ActionEvent event) {
        game.GameState.get().setSaveMenuMode(false);
        game.GameState.get().setReturnScene("/view/main_menu.fxml", util.SpawnTag.DEFAULT);
        util.SceneManager.switchScene("/view/saveloads.fxml");
    }

    @FXML
    private void handlebtnexitaction(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void handlebtnnewgaction(ActionEvent event) {
        util.SceneManager.switchScene("/view/new_game.fxml");
    }

    @FXML
    private void handlebtnlogoutaction(ActionEvent event) {
        util.SceneManager.switchScene("/view/login.fxml");
    }
    
}
