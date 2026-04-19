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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import util.AppDialogs;
import util.WorldScaler;

/**
 * FXML Controller class
 *
 * @author Shane Khant Ko
 */
public class Item_add_btnController implements Initializable {

    @FXML
    private Label lblaname;
    @FXML
    private TextField txtmname;
    @FXML
    private TextField txtstr;
    @FXML
    private TextField txtagi;
    @FXML
    private TextField txtdex;
    @FXML
    private TextField txtvid;
    @FXML
    private TextField txthp;
    @FXML
    private TextField txtstamina;
    @FXML
    private Button btnupload;
    @FXML
    private Button btnsave;
    @FXML
    private Button btnback;
    @FXML
    private TextField txtmobstype;
    @FXML
    private ImageView itemimage;
    @FXML 
    private StackPane view;
    @FXML 
    private Group uiRoot;

    Connection con;
    ResultSet rs;
    PreparedStatement pst;
    Statement st;
    Parent root;
    String img_name;
    @FXML
    private ComboBox<ItemEditorSupport.ItemTypeOption> cbitemtype;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dbcon db = new dbcon();
        try{
            con = db.getConnection();
            System.out.println("Ready...");
            lblaname.setText(LoginController.txtname);
            ItemEditorSupport.loadItemTypes(con, cbitemtype);
            
        }catch (ClassNotFoundException e){
            System.out.println("Not Connected....");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        WorldScaler scaler = new WorldScaler(view, uiRoot);
        Platform.runLater(scaler::scale);
    }    

    @FXML
    private void handlebtnuploadaction(ActionEvent event) throws IOException {
        String selectedImage = ItemEditorSupport.choosePreviewAndCopyItemImage(
            itemimage.getScene().getWindow(),
            itemimage
        );
        if (selectedImage != null) {
            img_name = selectedImage;
        }
    }

    @FXML
    private void handlebtnsaveaction(ActionEvent event) throws SQLException, IOException {
        if(txtmname.getText().isEmpty()){
            AppDialogs.showMessageDialog(null, "Please Enter Item Name");
        }else if(txtmobstype.getText().isEmpty()){
            AppDialogs.showMessageDialog(null, "Please Enter Price");
        } else if(cbitemtype.getSelectionModel().getSelectedItem() == null){
            AppDialogs.showMessageDialog(null, "Please Select Item Type");
        } else if(txtstr.getText().isEmpty()){
            AppDialogs.showMessageDialog(null, "Please Enter STR");
        }else if(txtagi.getText().isEmpty()){
            AppDialogs.showMessageDialog(null, "Please Enter AGI");
        }else if(txtdex.getText().isEmpty()){
            AppDialogs.showMessageDialog(null, "Please Enter DEX");
        }
        else if(txtvid.getText().isEmpty()){
            AppDialogs.showMessageDialog(null, "Please Enter VIT");
        }else if(txthp.getText().isEmpty()){
            AppDialogs.showMessageDialog(null, "Please Enter HP");
        }else if(txtstamina.getText().isEmpty()){
            AppDialogs.showMessageDialog(null, "Please Enter Stamina");
        }else if(itemimage.getImage()==null){
            AppDialogs.showMessageDialog(null, "Please Upload Image");
        } 
        else{
           try {
                con.setAutoCommit(false);
                int itemTypeId = cbitemtype.getSelectionModel().getSelectedItem().id();
                int price = ItemEditorSupport.parseRequiredInt(txtmobstype, "Price");
                int str = ItemEditorSupport.parseRequiredInt(txtstr, "STR");
                int agi = ItemEditorSupport.parseRequiredInt(txtagi, "AGI");
                int dex = ItemEditorSupport.parseRequiredInt(txtdex, "DEX");
                int vit = ItemEditorSupport.parseRequiredInt(txtvid, "VIT");
                int hp = ItemEditorSupport.parseRequiredInt(txthp, "HP");
                int stamina = ItemEditorSupport.parseRequiredInt(txtstamina, "Stamina");

                // 1) insert stats
                String sqlStats = "INSERT INTO item_stats (str_stat, agi_stat, dex_stat, vit_stat, hp, stamina) VALUES (?, ?, ?, ?, ?, ?)";
                pst = con.prepareStatement(sqlStats, Statement.RETURN_GENERATED_KEYS);

                pst.setInt(1, str);
                pst.setInt(2, agi);
                pst.setInt(3, dex);
                pst.setInt(4, vit);
                pst.setInt(5, hp);
                pst.setInt(6, stamina);
                pst.executeUpdate();

                ResultSet gen = pst.getGeneratedKeys();
                int statsId = 0;
                if (gen.next()) {
                    statsId = gen.getInt(1);
                }
                if (statsId <= 0) {
                    throw new SQLException("Failed to create item_stats row.");
                }

                // 2) insert item using FK statsId
                String sqlItem = "INSERT INTO items (item_name, price, item_stats_id, image, item_type_id) VALUES (?, ?, ?, ?, ?)";
                pst = con.prepareStatement(sqlItem);

                pst.setString(1, txtmname.getText());
                pst.setInt(2, price);                                    // price
                pst.setInt(3, statsId);                                  // FK
                pst.setString(4, img_name);                              // filename
                pst.setInt(5, itemTypeId);                               // FK type
                pst.executeUpdate();

                con.commit();
                AppDialogs.showMessageDialog(null, "Inserted Successfully!");
                util.SceneManager.switchScene("/view/edit_item.fxml");
            } catch (NumberFormatException e) {
                con.rollback();
                AppDialogs.showMessageDialog(null, e.getMessage());
            } catch (Exception e) {
                con.rollback();
                e.printStackTrace();
                AppDialogs.showMessageDialog(null, "Insert failed.");
            } finally {
                con.setAutoCommit(true);
            }

        }
    }

    @FXML
    private void handlebtnbackaction(ActionEvent event) throws IOException {
        util.SceneManager.switchScene("/view/edit_item.fxml");
    }

    @FXML
    private void handlecbitemboxaction(ActionEvent event) {
    }
}
