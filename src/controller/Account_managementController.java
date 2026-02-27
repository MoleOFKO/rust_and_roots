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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import model.AccMg;
import model.Account;
import model.item;
import rust_and_roots.Rust_and_roots;
import static rust_and_roots.Rust_and_roots.stage;
import util.WorldScaler;

/**
 * FXML Controller class
 *
 * @author Shane Khant Ko
 */
public class Account_managementController implements Initializable {
    
    @FXML
    private Button btnactive;
    @FXML
    private Label txtaname;
    @FXML
    private TableView<Account> account_view;
    @FXML
    private TableColumn<?, ?> colaccname;
    @FXML
    private TableColumn<?, ?> colemail;
    @FXML
    private TableColumn<?, ?> colstatus;
    @FXML
    private TableColumn<?, ?> colcreateddate;
    @FXML
    private TextField txtsearch;
     @FXML
    private Button btnedit;
    @FXML
    private Button btnban;
    @FXML
    private Button btnsearch;
    
    Connection con;
    ResultSet rs;
    PreparedStatement pst;
    Statement st;
    Parent root;
    public static ObservableList<Account> accountList;
    String created_date;
    public static String accname;
    
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
    private TableColumn<?, ?> user_type;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        WorldScaler scaler = new WorldScaler(view, uiRoot);
        Platform.runLater(scaler::scale);
        dbcon db = new dbcon();
        try {
        con = db.getConnection();
        txtaname.setText(LoginController.txtname);

        initColumn();
        initaccountList();

        // ✅ Set initial state
        btnactive.setVisible(false);
        btnban.setVisible(false);

        // ✅ Update buttons when selecting a row
        account_view.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel == null) {
                btnactive.setVisible(false);
                btnban.setVisible(false);
                return;
            }

            String status = newSel.getAccount_stats();

            if ("active".equalsIgnoreCase(status)) {
                btnactive.setVisible(false); 
                btnban.setVisible(true);     
            } else if ("ban".equalsIgnoreCase(status)) {
                btnactive.setVisible(true);  
                btnban.setVisible(false);    
            } else {
                btnactive.setVisible(false);
                btnban.setVisible(false);
            }
        });

    } catch (ClassNotFoundException | SQLException e) {
        e.printStackTrace();
    }
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
            root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
    }
    
    
    private void initaccountList(   ) throws SQLException{       
    accountList = FXCollections.observableArrayList();

    String sql = "select user_type from accounts where account_name=?";
    pst = con.prepareStatement(sql);
    pst.setString(1, LoginController.txtname);
    rs = pst.executeQuery();

    if (rs.next()) {   // ❗ VERY IMPORTANT
        String owner = "owner";

        if(rs.getString("user_type").equals(owner)) {

            String sql2="select account_name,email,user_type,account_stats,created_date from accounts";
            st = con.createStatement();
            rs = st.executeQuery(sql2);

            while(rs.next()){
                accountList.add(new Account(rs.getString("account_name"),rs.getString("user_type"),rs.getString("email"),rs.getString("account_stats"),rs.getString("created_date")));
            }

        } else {

            String sql1="select account_name,email,user_type,account_stats,created_date from accounts where user_type='user'";
            st = con.createStatement();
            rs = st.executeQuery(sql1);

            while(rs.next()){
                accountList.add(new Account(
                    rs.getString("account_name"),rs.getString("user_type"),rs.getString("email"),rs.getString("account_stats"),rs.getString("created_date")
                ));
            }
        }
    }

    account_view.setItems(accountList);
}
    
    public void initColumn(){
        
        
        colaccname.setCellValueFactory(new PropertyValueFactory("account_name"));
        colemail.setCellValueFactory(new PropertyValueFactory("email"));
        user_type.setCellValueFactory(new PropertyValueFactory("user_type"));
        colstatus.setCellValueFactory(new PropertyValueFactory("account_stats"));
        colcreateddate.setCellValueFactory(new PropertyValueFactory("created_date"));
        
        
        
    }

    @FXML
    private void handlesearchaction(ActionEvent event) throws SQLException {
        if(txtsearch.getText().isEmpty())
            JOptionPane.showMessageDialog(null, "Please Enter search name");
        else{
            accountList.clear();
            String sname=txtsearch.getText();
            String sql = "select account_id,account_name,email,account_stats,created_date from accounts where account_name like ?;";
            pst=con.prepareStatement(sql);
            pst.setString(1, txtsearch.getText()+"%");
            rs=pst.executeQuery();
           
            while(rs.next()){
                created_date = rs.getString("created_date").toString();
            
            
            accountList.add(new Account(rs.getInt("account_id"), rs.getString("account_name"), rs.getString("email"), rs.getString("account_stats"), created_date ));
         }
            account_view.setItems(accountList);
        }
    }
    @FXML
    void handlebtneditaction(ActionEvent event) throws IOException {
        Account a = account_view.getSelectionModel().getSelectedItem();
        if(a==null)
            JOptionPane.showMessageDialog(null, "Please Select one Product You Want to Edit!");
        else{
            accname = a.getAccount_name();
            Stage stage = Rust_and_roots.stage;
            root = FXMLLoader.load(getClass().getResource("/view/account_edit.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }    
    }
   

    
    private void updateButtonsFromDb(int accountId) throws SQLException {
        String sql = "SELECT account_stats FROM accounts WHERE account_id=?";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, accountId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("account_stats");

                    if ("active".equalsIgnoreCase(status)) {
                        btnactive.setVisible(false);
                        btnban.setVisible(true);
                    } else if ("ban".equalsIgnoreCase(status)) {
                        btnactive.setVisible(true);
                        btnban.setVisible(false);
                    } else {
                        btnactive.setVisible(false);
                        btnban.setVisible(false);
                    }
                }
            }
        }
    }

    @FXML
    private void handlebanaction(ActionEvent event) throws SQLException, IOException {
        Account a= account_view.getSelectionModel().getSelectedItem();
        int num = JOptionPane.showConfirmDialog(null, "Are you Sure?");
            if (num == JOptionPane.YES_OPTION) {
                String sql1 = "Update accounts SET account_stats = 'ban' where account_name=?;";
                pst = con.prepareStatement(sql1);
                pst.setString(1, a.getAccount_name());  
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Baned Successfully!");
                root = FXMLLoader.load(getClass().getResource("/view/account_management.fxml"));
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
    }

    @FXML
    private void handleactiveaction(ActionEvent event) throws SQLException, IOException {
        Account a= account_view.getSelectionModel().getSelectedItem();
        int num = JOptionPane.showConfirmDialog(null, "Are you Sure?");
            if (num == JOptionPane.YES_OPTION) {
                String sql1 = "Update accounts SET account_stats = 'active' where account_name =?;";
                pst = con.prepareStatement(sql1);
                pst.setString(1, a.getAccount_name());  
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Actived Successfully!");
                root = FXMLLoader.load(getClass().getResource("/view/account_management.fxml"));
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
    }

}
