/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import static controller.item_edit_btnController.copyImageToFolder;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import rust_and_roots.Rust_and_roots;
import static rust_and_roots.Rust_and_roots.stage;
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
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dbcon db = new dbcon();
        try{
            con = db.getConnection();
            System.out.println("Ready...");
            lblaname.setText(LoginController.txtname);
            
        }catch (ClassNotFoundException e){
            System.out.println("Not Connected....");
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
            
            //show image to project folder
            img_name = item_edit_btnController.copyImageToFolder(selectedFile);
                                       
        }
    }

    @FXML
    private void handlebtnsaveaction(ActionEvent event) throws SQLException, IOException {
        if(txtmname.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Please Select Category");
        }else if(txtmobstype.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Please Enter Product Name");
        } else if(txtstr.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Please Select Size");
        }else if(txtagi.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Please Enter Price");
        }else if(txtdex.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Please Enter Stock Qty");
        }
        else if(txtvid.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Please Enter Stock Qty");
        }else if(txthp.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Please Enter Stock Qty");
        }else if(txtstamina.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Please Enter Stock Qty");
        }else if(itemimage.getImage()==null){
            JOptionPane.showMessageDialog(null, "Please Upload Image");
        } 
        else{
           try {
                con.setAutoCommit(false);

                // 1) insert stats
                String sqlStats = "INSERT INTO stats (str, agi, dex, vit, hp, stamina) VALUES (?, ?, ?, ?, ?, ?)";
                pst = con.prepareStatement(sqlStats, Statement.RETURN_GENERATED_KEYS);

                pst.setInt(1, Integer.parseInt(txtstr.getText()));
                pst.setInt(2, Integer.parseInt(txtagi.getText()));
                pst.setInt(3, Integer.parseInt(txtdex.getText()));
                pst.setInt(4, Integer.parseInt(txtvid.getText()));
                pst.setInt(5, Integer.parseInt(txthp.getText()));
                pst.setInt(6, Integer.parseInt(txtstamina.getText()));
                pst.executeUpdate();

                ResultSet gen = pst.getGeneratedKeys();
                int statsId = 0;
                if (gen.next()) statsId = gen.getInt(1);

                // 2) insert item using FK statsId
                String sqlItem = "INSERT INTO items (item_name, price, stats_id, image) VALUES (?, ?, ?, ?)";
                pst = con.prepareStatement(sqlItem);

                pst.setString(1, txtmname.getText());
                pst.setInt(2, Integer.parseInt(txtmobstype.getText()));  // price
                pst.setInt(3, statsId);                                  // FK
                pst.setString(4, img_name);                              // filename
                pst.executeUpdate();

                con.commit();
                JOptionPane.showMessageDialog(null, "Inserted Successfully!");

                root = FXMLLoader.load(getClass().getResource("/view/edit_item.fxml"));
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

            } catch (Exception e) {
                con.rollback();
                e.printStackTrace();
            } finally {
                con.setAutoCommit(true);
            }

        }
    }

    @FXML
    private void handlebtnbackaction(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/edit_item.fxml"));      
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
}
