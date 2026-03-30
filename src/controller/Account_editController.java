/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import controller.LoginController;
import db.dbcon;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    Connection con;
    ResultSet rs;
    PreparedStatement pst;
    Statement st;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dbcon db = new dbcon();
        try{
            con = db.getConnection();
            System.out.println("Ready...");
            lblaname.setText(LoginController.txtname);
            txtaccname.setText(Account_managementController.accname);
            user_type.getItems().setAll("owner", "admin", "user");
            initaccount();
            
            
        }catch (ClassNotFoundException e){
            System.out.println("Not Connected....");
        } catch (SQLException ex) {
            System.getLogger(Account_editController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        WorldScaler scaler = new WorldScaler(view, uiRoot);
        Platform.runLater(scaler::scale);
    }    

    @FXML
    private void handlebtnsaveaction(ActionEvent event) {
    }

    @FXML
    private void handlebtnbackaction(ActionEvent event) throws IOException {
        util.SceneManager.switchScene("/view/account_edit_main.fxml");
    }

    @FXML
    private void handleaction(ActionEvent event) {
        
    }
    
    private void initaccount() throws SQLException {

    String sql = """
        SELECT a.account_name, r.role_name AS user_type, a.email, a.passwords
        FROM accounts a
        JOIN user_roles r ON a.role_id = r.role_id
        WHERE a.account_name = ?
    """;
    pst = con.prepareStatement(sql);
    pst.setString(1, Account_managementController.accname);
    rs = pst.executeQuery();

    if (rs.next()) {
        txtaccname.setText(rs.getString("account_name"));
        txtemail.setText(rs.getString("email"));
        txtpwd.setText(rs.getString("passwords"));

        // Set ComboBox selected value from DB
        user_type.setValue(rs.getString("user_type"));
    }
}

}
