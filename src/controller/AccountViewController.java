/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import model.Account;
import static rust_and_roots.Rust_and_roots.stage;
import util.WorldScaler;

/**
 * FXML Controller class
 *
 * @author larry
 */
public class AccountViewController implements Initializable {

    @FXML
    private Label txtaname;
    @FXML
    private Button btnecharacter;
    @FXML
    private Button btneitem;
    @FXML
    private Button btnaccmanagement;
    @FXML
    private Button btnvplayer;
    @FXML
    private Button btnlogout;
    @FXML 
    private StackPane view;
    @FXML 
    private Group uiRoot;
    @FXML
    private TableView<Account> account_view;
    @FXML
    private TableColumn<?, ?> colaccid;
    @FXML
    private TableColumn<?, ?> colaccname;
    @FXML
    private TableColumn<?, ?> colusertype;
    @FXML
    private TableColumn<?, ?> colstatus;
    @FXML
    private TableColumn<?, ?> colcreateddate;
    
    Connection con;
    ResultSet rs;
    PreparedStatement pst;
    Statement st;
    Parent root;
    public static ObservableList<Account> accountList;
    String created_date;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dbcon db = new dbcon();
        try{
            con = db.getConnection();
            System.out.println("Ready...");
            txtaname.setText(LoginController.txtname);
            initColumn();
            initaccountList();
            
            
        }catch (ClassNotFoundException e){
            System.out.println("Not Connected....");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        WorldScaler scaler = new WorldScaler(view, uiRoot);
        Platform.runLater(scaler::scale);
    }    

    @FXML
    private void handlebtnecharacteraction(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/edit_character.fxml"));
        
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handlebtneitemaction(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/edit_item.fxml"));
        
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handlebtnaccmanagementaction(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/account_management.fxml"));
        
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handlebtnvplayeraction(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/account_view.fxml"));
        
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handlebtnlogoutaction(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
        
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }
    
    
    private void initaccountList(   ) throws SQLException{
        accountList = FXCollections.observableArrayList();
        String sql="select account_id,account_name,user_type,account_stats,created_date from accounts";
        st = con.createStatement();
        rs = st.executeQuery(sql);
        while(rs.next()){
            created_date = rs.getString("created_date").toString();
            
            accountList.add(new Account(rs.getInt("account_id"), rs.getString("account_name"), rs.getString("user_type"), rs.getString("account_stats"), created_date ));
         }
        account_view.setItems(accountList);
    }
    
    public void initColumn(){
        
        colaccid.setCellValueFactory(new PropertyValueFactory("account_id"));
        colaccname.setCellValueFactory(new PropertyValueFactory("account_name"));
        colusertype.setCellValueFactory(new PropertyValueFactory("user_type"));
        colstatus.setCellValueFactory(new PropertyValueFactory("account_stats"));
        colcreateddate.setCellValueFactory(new PropertyValueFactory("created_date"));
        
    }

}
