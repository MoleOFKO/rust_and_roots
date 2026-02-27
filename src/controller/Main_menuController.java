/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import static rust_and_roots.Rust_and_roots.stage;
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
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        WorldScaler scaler = new WorldScaler(view, uiRoot);
        Platform.runLater(scaler::scale);
    }    

    @FXML
    private void handlebtnloadgaction(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/saveloads.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handlebtnexitaction(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void handlebtnnewgaction(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/new_game.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }
    
}
