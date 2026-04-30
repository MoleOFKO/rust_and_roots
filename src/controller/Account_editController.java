/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import controller.LoginController;
import db.UiSqlService;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import static rust_and_roots.Rust_and_roots.stage;
import util.WorldScaler;

/**
 * FXML Controller class
 *
 * @author Shane Khant Ko
 */
public class Account_editController implements Initializable {

    @FXML
    private Label lblaname;
    @FXML
    private TextField txtaccname;
    @FXML
    private TextField txtemail;
    @FXML
    private TextField txtpwd;
    @FXML
    private Button btnsave;
    @FXML
    private Button btnback;
    @FXML 
    private StackPane view;
    @FXML 
    private Group uiRoot;
    @FXML
    private ComboBox<String> user_type;

    Parent root;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblaname.setText(LoginController.txtname);
        txtaccname.setText(Account_managementController.accname);
        user_type.getItems().setAll("owner", "admin", "user");
        initaccount();
        WorldScaler scaler = new WorldScaler(view, uiRoot);
        Platform.runLater(scaler::scale);
    }    

    @FXML
    private void handlebtnsaveaction(ActionEvent event) {
    }

    @FXML
    private void handlebtnbackaction(ActionEvent event) throws IOException {
        util.SceneManager.switchScene("/view/account_management.fxml");
    }

    @FXML
    private void handleaction(ActionEvent event) {
        
    }
    
    private void initaccount() {
        try {
            UiSqlService.AccountEditData data = UiSqlService.fetchAccountEditData(Account_managementController.accname);
            if (data == null) {
                return;
            }
            txtaccname.setText(data.accountName());
            txtemail.setText(data.email());
            txtpwd.setText(data.password());
            user_type.setValue(data.roleName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
