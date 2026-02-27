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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import static rust_and_roots.Rust_and_roots.stage;
import util.WorldScaler;

/**
 * FXML Controller class
 *
 * @author larry
 */
public class SaveloadsController implements Initializable {

    @FXML
    private TableView<?> load_slots;
    @FXML
    private TableColumn<?, ?> coloImg;
    @FXML
    private TableColumn<?, ?> colName;
    @FXML
    private TableColumn<?, ?> colDays;
    @FXML
    private Button btnLoad;
    @FXML
    private Button btnback;
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
    private void handlebtnLoadaction(ActionEvent event) {
    }

    @FXML
    private void handlebackaction(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/main_menu.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
}
