/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import db.dbcon;
import java.io.File;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import util.AppDialogs;
import util.WorldScaler;

/**
 * FXML Controller class
 *
 * @author Shane Khant Ko
 */
public class item_edit_btnController implements Initializable {

    @FXML
    private Label lblaname;
    
    @FXML
    private Button btnsave;
    @FXML
    private Button btnback;
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
    private TextField txtmobstype;
    private BorderPane image;
    @FXML
    private ImageView itemimage;
    @FXML 
    private StackPane view;
    @FXML 
    private Group uiRoot;

    /**
     * Initializes the controller class.
     */
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
            inititem();
        }catch (ClassNotFoundException e){
            System.out.println("Not Connected....");
        } catch (SQLException ex) {
            System.getLogger(item_edit_btnController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
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
                int itemTypeId = cbitemtype.getSelectionModel().getSelectedItem().id();
                int price = ItemEditorSupport.parseRequiredInt(txtmobstype, "Price");
                int str = ItemEditorSupport.parseRequiredInt(txtstr, "STR");
                int agi = ItemEditorSupport.parseRequiredInt(txtagi, "AGI");
                int dex = ItemEditorSupport.parseRequiredInt(txtdex, "DEX");
                int vit = ItemEditorSupport.parseRequiredInt(txtvid, "VIT");
                int hp = ItemEditorSupport.parseRequiredInt(txthp, "HP");
                int stamina = ItemEditorSupport.parseRequiredInt(txtstamina, "Stamina");

                String sql = """
                    UPDATE items i
                    JOIN item_stats s ON i.item_stats_id = s.item_stats_id
                    SET i.item_name = ?,
                        i.price = ?,
                        i.item_type_id = ?,
                        s.str_stat = ?,
                        s.agi_stat = ?,
                        s.dex_stat = ?,
                        s.vit_stat = ?,
                        s.hp = ?,
                        s.stamina = ?,
                        i.image = ?
                    WHERE i.item_id = ?
                    """;
                pst = con.prepareStatement(sql);
                pst.setString(1, txtmname.getText().trim());
                pst.setInt(2, price);
                pst.setInt(3, itemTypeId);
                pst.setInt(4, str);
                pst.setInt(5, agi);
                pst.setInt(6, dex);
                pst.setInt(7, vit);
                pst.setInt(8, hp);
                pst.setInt(9, stamina);
                pst.setString(10, img_name);
                pst.setInt(11, Edit_itemController.item_id);
                pst.executeUpdate();
                AppDialogs.showMessageDialog(null, "Updated Successfully!");
                util.SceneManager.switchScene("/view/edit_item.fxml");
            } catch (NumberFormatException ex) {
                AppDialogs.showMessageDialog(null, ex.getMessage());
            }
        } 
    }

    @FXML
    private void handlebtnbackaction(ActionEvent event) throws IOException {
        util.SceneManager.switchScene("/view/edit_item.fxml");
    }
    
    private void handleactionupload(ActionEvent event) throws IOException {
        String selectedImage = ItemEditorSupport.choosePreviewAndCopyItemImage(
            image.getScene().getWindow(),
            itemimage
        );
        if (selectedImage != null) {
            img_name = selectedImage;
        }
    }
    

    private void inititem() throws SQLException {

    String sql = """
        SELECT i.item_name, i.price,
               i.item_type_id,
               s.str_stat AS str,
               s.agi_stat AS agi,
               s.vit_stat AS vit,
               s.dex_stat AS dex,
               s.hp,
               s.stamina,
               i.image
        FROM items i
        JOIN item_stats s ON i.item_stats_id = s.item_stats_id
        WHERE i.item_id = ?
        """;
    pst = con.prepareStatement(sql);
    pst.setInt(1, Edit_itemController.item_id); // whatever your selected item id variable is
    
    rs = pst.executeQuery();

    if (rs.next()) {
        this.img_name = rs.getString("image");
        File file= new File(ItemEditorSupport.itemImagePath(this.img_name));
        Image image = new Image(file.toURI().toString());
        ImageView imgView = new ImageView(image);
        imgView.setFitHeight(50);
        imgView.setFitWidth(50);
        imgView.setPreserveRatio(true);
            txtmname.setText(rs.getString("item_name"));
            txtmobstype.setText(rs.getString("price"));
            txtstr.setText(rs.getString("str"));
            txtagi.setText(rs.getString("agi"));
            txtdex.setText(rs.getString("dex"));
            txtvid.setText(rs.getString("vit"));
            txthp.setText(rs.getString("hp"));
            txtstamina.setText(rs.getString("stamina"));
            File afile= new File(ItemEditorSupport.itemImagePath(this.img_name));
           Image iimage = new Image(afile.toURI().toString());
            
//            ImageView imgView = new ImageView(image);
            Image img = new Image(afile.toURI().toString());
            itemimage.setImage(iimage);
            ItemEditorSupport.fitImageToPreviewBox(itemimage);

            int selectedTypeId = rs.getInt("item_type_id");
            ItemEditorSupport.selectItemTypeById(cbitemtype, selectedTypeId);
        
    }
}

    @FXML
    private void handlecbitemboxaction(ActionEvent event) {
    }
}
