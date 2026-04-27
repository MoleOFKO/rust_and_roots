/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import db.MobDefinitionRepository;
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
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javax.swing.JOptionPane;
import util.AppDialogs;
import model.mobs;
import util.WorldScaler;

/**
 * FXML Controller class
 *
 * @author Shane Khant Ko
 */
public class Edit_characterController implements Initializable {

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
    private TableView<mobs> mob_view;
    @FXML
    private TableColumn<?, ?> colmobid;
    @FXML
    private TableColumn<?, ?> colmobname;
    @FXML
    private TableColumn<?, ?> colmobtype;
    @FXML
    private TableColumn<?, ?> colstr;
    @FXML
    private TableColumn<?, ?> colagi;
    @FXML
    private TableColumn<?, ?> colvit;
    @FXML
    private TableColumn<?, ?> coldex;
    @FXML
    private TableColumn<?, ?> colhp;
    @FXML
    private TableColumn<?, ?> colstimana;
    @FXML
    private TableColumn<?, ?> mobimage;
    @FXML
    private TextField txtsearch;
    @FXML
    private Button btnsearch;
    @FXML
    private Button btnedit;
    @FXML
    private Button btnadd;
    @FXML
    private Button btndelete;
    @FXML 
    private StackPane view;
    @FXML 
    private Group uiRoot;
    
    
    Connection con;
    ResultSet rs;
    PreparedStatement pst;
    Statement st;
    public static ObservableList<mobs> mobsList;
    public static int mid;
    private final MobDefinitionRepository mobRepository = new MobDefinitionRepository();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dbcon db = new dbcon();
        try{
            con = db.getConnection();
            txtaname.setText(LoginController.txtname);
            initColumn();
            initmobsList();
        }catch (ClassNotFoundException e){
            System.out.println("Not Connected....");
        } catch (SQLException ex) {
            ex.printStackTrace();
            AppDialogs.showMessageDialog(null, "Unable to load mobs from database.");
        }
        WorldScaler scaler = new WorldScaler(view, uiRoot);
        Platform.runLater(scaler::scale);
    }    

    @FXML
    private void handlebtnecharacteraction(ActionEvent event) throws SQLException, IOException {
        util.SceneManager.switchScene("/view/edit_character.fxml");
    }

    @FXML
    private void handlebtneitemaction(ActionEvent event) throws IOException {
        util.SceneManager.switchScene("/view/edit_item.fxml");
    }

    @FXML
    private void handlebtnaccmanagementaction(ActionEvent event) throws IOException {
        util.SceneManager.switchScene("/view/account_management.fxml");
    }

    @FXML
    private void handlebtnvplayeraction(ActionEvent event) throws IOException {
        util.SceneManager.switchScene("/view/account_view.fxml");
    }

    @FXML
    private void handlebtnlogoutaction(ActionEvent event) throws IOException {
        util.SceneManager.switchScene("/view/Login.fxml");
    }

   @FXML
   private void handlesearchaction(ActionEvent event) throws SQLException {
        String keyword = txtsearch.getText() == null ? "" : txtsearch.getText().trim();
        try {
            mobsList.setAll(keyword.isEmpty() ? mobRepository.findAll() : mobRepository.searchByName(keyword));
        } catch (ClassNotFoundException ex) {
            throw new SQLException("Database driver not found.", ex);
        }
   }
     @FXML
   void handlebtnaddaction(ActionEvent event) throws IOException {
        mid = -1;
        util.SceneManager.switchScene("/view/char_edit_btn.fxml");
  }

    @FXML
  void handlebtndeleteaction(ActionEvent event) throws SQLException, IOException {
        mobs m = mob_view.getSelectionModel().getSelectedItem();
        if (m == null) {
            AppDialogs.showMessageDialog(null, "Please select a mob first!");
            return;
        }
        int num = AppDialogs.showConfirmDialog(null, "Are you Sure?");

        if (num == JOptionPane.YES_OPTION) {
            try {
                mobRepository.delete(m.getMobs_id());
                AppDialogs.showMessageDialog(null, "Deleted Successfully!");
                initmobsList();
            } catch (ClassNotFoundException ex) {
                throw new SQLException("Database driver not found.", ex);
            }
        }
   }
    @FXML
  void handlebtneditaction(ActionEvent event) throws IOException {
        mobs m = mob_view.getSelectionModel().getSelectedItem();
        if(m==null) {
            AppDialogs.showMessageDialog(null, "Please Select one Mobs You Want to Edit!");
            return;
        }
        mid = m.getMobs_id();
        util.SceneManager.switchScene("/view/char_edit_btn.fxml");
    }

    private void initmobsList() throws SQLException {
        try {
            mobsList = FXCollections.observableArrayList(mobRepository.findAll());
        } catch (ClassNotFoundException ex) {
            throw new SQLException("Database driver not found.", ex);
        }
        mob_view.setItems(mobsList);
    }
    
    public void initColumn(){
        colmobid.setCellValueFactory(new PropertyValueFactory<>("mobs_id"));
        colmobname.setCellValueFactory(new PropertyValueFactory<>("mobs_name"));
        colmobtype.setCellValueFactory(new PropertyValueFactory<>("mobs_type"));
        colstr.setCellValueFactory(new PropertyValueFactory<>("str"));
        colagi.setCellValueFactory(new PropertyValueFactory<>("agi"));
        colvit.setCellValueFactory(new PropertyValueFactory<>("vid"));
        coldex.setCellValueFactory(new PropertyValueFactory<>("dex"));
        colhp.setCellValueFactory(new PropertyValueFactory<>("hp"));
        colstimana.setCellValueFactory(new PropertyValueFactory<>("stimana"));
        mobimage.setCellValueFactory(new PropertyValueFactory<>("image"));
    }
}
