/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

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
public class Char_edit_btnController implements Initializable {

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
    private ImageView mobimg;
    @FXML
    private Button btnupload;
    @FXML
    private Button btnsave;
    @FXML
    private Button btnback;
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
    private TextField txtmobstype;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dbcon db = new dbcon();
        try{
            con = db.getConnection();
            System.out.println("Ready...");
            lblaname.setText(LoginController.txtname);
            fetchAccount();
        }catch (ClassNotFoundException e){
            System.out.println("Not Connected....");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        WorldScaler scaler = new WorldScaler(view, uiRoot);
        Platform.runLater(scaler::scale);
    }    

    @FXML
    private void handlebtnuploadaction(ActionEvent event) throws IOException {
//        FileChooser filechooser = new FileChooser();
//        filechooser.setTitle("Select Product Image");
//        filechooser.getExtensionFilters().add(
//        new FileChooser.ExtensionFilter("Image File", "*.png" , "*.jpg", "*.jpeg", "*.gif")
//        );
//        Stage stage = (Stage) mobimg.getScene().getWindow();
//        File selectedFile = filechooser.showOpenDialog(stage);
//        
//        if(selectedFile != null){
//            
//            //show image in ImageView
//            Image image = new Image(selectedFile.toURI().toString());
//            mobimg.setImage(image);
//            
//            //show image to project folder
//            img_name = copyImageToFolder(selectedFile);
                                       
//        }
    }

    @FXML
    private void handlebtnsaveaction(ActionEvent event) throws SQLException, IOException {
//        if(txtmname.getText().isEmpty()){
//            JOptionPane.showMessageDialog(null, "Please Enter Mobs Name");
//        }else if(txtmobstype.getText().isEmpty()){
//            JOptionPane.showMessageDialog(null, "Please Enter Mobs Type");
//        } else if(txtstr.getText().isEmpty()){
//            JOptionPane.showMessageDialog(null, "Please Enter STR");
//        }else if(txtagi.getText().isEmpty()){
//            JOptionPane.showMessageDialog(null, "Please Enter AGI");
//        }else if(txtdex.getText().isEmpty()){
//            JOptionPane.showMessageDialog(null, "Please Enter DEX");
//        }else if(txtvid.getText().isEmpty()){
//            JOptionPane.showMessageDialog(null, "Please Enter VIT");
//        }else if(txthp.getText().isEmpty()){
//            JOptionPane.showMessageDialog(null, "Please Enter HP");
//        }else if(txtstamina.getText().isEmpty()){
//            JOptionPane.showMessageDialog(null, "Please Enter Stamina");
//        }else if(mobimg.getImage()==null){
//            JOptionPane.showMessageDialog(null, "Please Upload Image");
//        } 
//        else{
//           
//        String sql = "UPDATE mobs m JOIN stats s ON m.stats = s.stats_id SET m.mobs_name = ?,m.mobs_type = ?,s.str = ?,s.agi = ?,s.dex = ?, s.vid = ?,s.hp = ?,s.stimana = ?,m.image=? WHERE m.mobs_id = ?";
//        pst = con.prepareStatement(sql);
//        pst.setString(1, txtmname.getText());
//        pst.setString(2, txtmobstype.getText());
//        pst.setInt(3, Integer.parseInt(txtstr.getText()));
//        pst.setInt(4, Integer.parseInt((txtagi.getText())));
//        pst.setInt(5, Integer.parseInt(txtdex.getText()));
//        pst.setInt(6,Integer.parseInt(txtvid.getText()));
//        pst.setInt(7, Integer.parseInt(txthp.getText()));
//        pst.setInt(8, Integer.parseInt(txtstamina.getText()));
//        pst.setString(9, img_name);
//         pst.setInt(10, Edit_characterController.mid);
//        pst.executeUpdate();
//        JOptionPane.showMessageDialog(null, "Updated Successfully!");
//        Stage stage = Rust_and_roots.stage;
//
//        root = FXMLLoader.load(getClass().getResource("/view/edit_character.fxml"));
//            Scene scene = new Scene(root);
//            stage.setScene(scene);
//            stage.show();
//        }
    }
     @FXML
    void handlebtnbackaction(ActionEvent event) {

    }
    public static String copyImageToFolder(File sourceFile) throws IOException{
       
        //Create images folder if not exits
        File imageDir  = new File("E:\\PJ\\rust_and_roots\\src\\image\\");
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
    
    private void fetchAccount() throws SQLException{        
        String sql = "select m.mobs_name,m.mobs_type,s.str,s.agi,s.vid,s.dex,s.hp,s.stimana,m.image from mobs m JOIN stats s ON m.stats= s.stats_id WHERE m.mobs_id = ?;";
        pst = con.prepareStatement(sql);
        pst.setInt(1, Edit_characterController.mid);
        rs=pst.executeQuery();

       if(rs.next()){
           txtmname.setText(rs.getString("mobs_name"));
           txtmobstype.setText(rs.getString("mobs_type"));
           txtstr.setText(rs.getString("str"));
           txtagi.setText(rs.getString("agi"));
           txtdex.setText(rs.getString("dex"));
           txtvid.setText(rs.getString("vid"));
           txthp.setText(rs.getString("hp"));
           txtstamina.setText(rs.getString("stimana"));
           
           String img_name1 = rs.getString("image");
            File file= new File("E:\\PJ\\rust_and_roots\\src\\image\\"+img_name1 );
            Image image = new Image(file.toURI().toString());
            ImageView imgView = new ImageView(image);
            imgView.setFitHeight(50);
            imgView.setFitWidth(50);
            imgView.setPreserveRatio(true);
            
           mobimg.setImage(image);
       }
    }
}
