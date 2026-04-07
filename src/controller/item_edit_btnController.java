/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import static controller.Char_edit_btnController.copyImageToFolder;
import db.dbcon;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
    private ComboBox<ItemTypeOption> cbitemtype;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dbcon db = new dbcon();
        try{
            con = db.getConnection();
            System.out.println("Ready...");
            lblaname.setText(LoginController.txtname);
            loadItemTypes();
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
        FileChooser filechooser = new FileChooser();
        filechooser.setTitle("Select Product Image");
        filechooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("Image File", "*.png" , "*.jpg", "*.jpeg", "*.gif")
        );
        Stage stage = (Stage) itemimage.getScene().getWindow();
        File selectedFile = filechooser.showOpenDialog(stage);
        
        if(selectedFile != null){
            
            //show image in ImageView
            Image image = new Image(selectedFile.toURI().toString());
            itemimage.setImage(image);
            fitImageToPreviewBox();
            
            //show image to project folder
            img_name = copyImageToFolder(selectedFile);
                                       
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
                int itemTypeId = cbitemtype.getSelectionModel().getSelectedItem().id;
                int price = parseRequiredInt(txtmobstype, "Price");
                int str = parseRequiredInt(txtstr, "STR");
                int agi = parseRequiredInt(txtagi, "AGI");
                int dex = parseRequiredInt(txtdex, "DEX");
                int vit = parseRequiredInt(txtvid, "VIT");
                int hp = parseRequiredInt(txthp, "HP");
                int stamina = parseRequiredInt(txtstamina, "Stamina");

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
        FileChooser filechooser = new FileChooser();
        filechooser.setTitle("Select Product Image");
        filechooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("Image File", "*.png" , "*.jpg", "*.jpeg", "*.gif")
        );
        Stage stage = (Stage) image.getScene().getWindow();
        File selectedFile = filechooser.showOpenDialog(stage);
        
        if(selectedFile != null){
            
            Image img = new Image(selectedFile.toURI().toString());
            
            itemimage.setImage(img);      // also show in the big image view
            img_name = copyImageToFolder(selectedFile);                         
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
        File file= new File("E:\\PJ\\rust_and_roots\\src\\image\\items\\" + this.img_name);
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
            File afile= new File("E:\\PJ\\rust_and_roots\\src\\image\\items\\" + this.img_name );
           Image iimage = new Image(afile.toURI().toString());
            
//            ImageView imgView = new ImageView(image);
            Image img = new Image(afile.toURI().toString());
            itemimage.setImage(iimage);
            fitImageToPreviewBox();

            int selectedTypeId = rs.getInt("item_type_id");
            selectItemTypeById(selectedTypeId);
        
    }
}
    public static String copyImageToFolder(File sourceFile) throws IOException{
       
        //Create images folder if not exits
        File imageDir  = new File("E:\\PJ\\rust_and_roots\\src\\image\\items\\");
        if(!imageDir.exists()){
            imageDir.mkdirs();
        }
        
        String fileName = sourceFile.getName();
        File destination = new File(imageDir,  fileName);
        
        Files.copy(
                sourceFile.toPath(), 
                destination.toPath(), 
                StandardCopyOption.REPLACE_EXISTING);
        
        return fileName;
    }

    @FXML
    private void handlecbitemboxaction(ActionEvent event) {
    }

    private void loadItemTypes() throws SQLException {
        List<ItemTypeOption> options = new ArrayList<>();
        String sql = "SELECT item_type_id, item_type_name FROM item_types ORDER BY item_type_name";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet result = ps.executeQuery()) {
            while (result.next()) {
                options.add(new ItemTypeOption(
                        result.getInt("item_type_id"),
                        result.getString("item_type_name")
                ));
            }
        }
        cbitemtype.getItems().setAll(options);
        if (!options.isEmpty()) {
            cbitemtype.getSelectionModel().selectFirst();
        }
    }

    private void selectItemTypeById(int itemTypeId) {
        for (ItemTypeOption option : cbitemtype.getItems()) {
            if (option.id == itemTypeId) {
                cbitemtype.getSelectionModel().select(option);
                return;
            }
        }
    }

    private int parseRequiredInt(TextField field, String name) {
        try {
            return Integer.parseInt(field.getText().trim());
        } catch (Exception ex) {
            throw new NumberFormatException(name + " must be a number.");
        }
    }

    private static class ItemTypeOption {
        private final int id;
        private final String name;

        private ItemTypeOption(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private void fitImageToPreviewBox() {
        if (itemimage == null) return;
        itemimage.setPreserveRatio(false);
        itemimage.setFitWidth(itemimage.getBoundsInLocal().getWidth());
        itemimage.setFitHeight(itemimage.getBoundsInLocal().getHeight());
    }
}
