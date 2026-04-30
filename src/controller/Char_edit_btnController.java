/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import db.MobDefinitionRepository;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import util.AppDialogs;
import model.mobs;
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


    String img_name;
    private final MobDefinitionRepository mobRepository = new MobDefinitionRepository();
    
    
    @FXML
    private TextField txtmobstype;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblaname.setText(LoginController.txtname);
        try {
            fetchAccount();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        WorldScaler scaler = new WorldScaler(view, uiRoot);
        Platform.runLater(scaler::scale);
    }    

    @FXML
    private void handlebtnuploadaction(ActionEvent event) throws IOException {
        String selectedImage = MobEditorSupport.choosePreviewAndCopyMobImage(
            mobimg.getScene().getWindow(),
            mobimg
        );
        if (selectedImage != null) {
            img_name = selectedImage;
        }
    }

    @FXML
    private void handlebtnsaveaction(ActionEvent event) throws SQLException, IOException {
        if(txtmname.getText().isEmpty()){
            AppDialogs.showMessageDialog(null, "Please Enter Mobs Name");
            return;
        }
        if(txtmobstype.getText().isEmpty()){
            AppDialogs.showMessageDialog(null, "Please Enter Mobs Type");
            return;
        }
        if(txtstr.getText().isEmpty()){
            AppDialogs.showMessageDialog(null, "Please Enter STR");
            return;
        }
        if(txtagi.getText().isEmpty()){
            AppDialogs.showMessageDialog(null, "Please Enter AGI");
            return;
        }
        if(txtdex.getText().isEmpty()){
            AppDialogs.showMessageDialog(null, "Please Enter DEX");
            return;
        }
        if(txtvid.getText().isEmpty()){
            AppDialogs.showMessageDialog(null, "Please Enter VIT");
            return;
        }
        if(txthp.getText().isEmpty()){
            AppDialogs.showMessageDialog(null, "Please Enter HP");
            return;
        }
        if(txtstamina.getText().isEmpty()){
            AppDialogs.showMessageDialog(null, "Please Enter Stamina");
            return;
        }
        if(mobimg.getImage()==null){
            AppDialogs.showMessageDialog(null, "Please Upload Image");
            return;
        }

        try {
            mobs mob = new mobs(
                Math.max(0, Edit_characterController.mid),
                txtmname.getText().trim(),
                txtmobstype.getText().trim(),
                MobEditorSupport.parseRequiredInt(txtstr, "STR"),
                MobEditorSupport.parseRequiredInt(txtagi, "AGI"),
                MobEditorSupport.parseRequiredInt(txtvid, "VIT"),
                MobEditorSupport.parseRequiredInt(txtdex, "DEX"),
                MobEditorSupport.parseRequiredInt(txthp, "HP"),
                MobEditorSupport.parseRequiredInt(txtstamina, "Stamina"),
                new ImageView(mobimg.getImage()),
                img_name
            );

            if (mob.getImagePath() == null || mob.getImagePath().isBlank()) {
                AppDialogs.showMessageDialog(null, "Please Upload Image");
                return;
            }

            if (Edit_characterController.mid > 0) {
                mobRepository.update(mob);
                AppDialogs.showMessageDialog(null, "Updated Successfully!");
            } else {
                mobRepository.insert(mob);
                AppDialogs.showMessageDialog(null, "Added Successfully!");
            }

            util.SceneManager.switchScene("/view/edit_character.fxml");
        } catch (ClassNotFoundException ex) {
            throw new SQLException("Database driver not found.", ex);
        } catch (NumberFormatException ex) {
            AppDialogs.showMessageDialog(null, ex.getMessage());
        }
    }
     @FXML
    void handlebtnbackaction(ActionEvent event) throws IOException {
        util.SceneManager.switchScene("/view/edit_character.fxml");
    }
    
    private void fetchAccount() throws SQLException{        
        if (Edit_characterController.mid <= 0) {
            img_name = "mob/Slime/Slime1_Idle_with_shadow.png";
            Image image = MobEditorSupport.loadMobImage(img_name);
            mobimg.setImage(image);
            if (image != null) {
                MobEditorSupport.fitImageToPreviewBox(mobimg);
            }
            return;
        }

        try {
            mobs mob = mobRepository.findById(Edit_characterController.mid);
            if (mob == null) {
                return;
            }

            txtmname.setText(mob.getMobs_name());
            txtmobstype.setText(mob.getMobs_type());
            txtstr.setText(String.valueOf(mob.getStr()));
            txtagi.setText(String.valueOf(mob.getAgi()));
            txtdex.setText(String.valueOf(mob.getDex()));
            txtvid.setText(String.valueOf(mob.getVid()));
            txthp.setText(String.valueOf(mob.getHp()));
            txtstamina.setText(String.valueOf(mob.getStimana()));

            img_name = mob.getImagePath();
            Image image = MobEditorSupport.loadMobImage(img_name);
            mobimg.setImage(image);
            if (image != null) {
                MobEditorSupport.fitImageToPreviewBox(mobimg);
            }
        } catch (ClassNotFoundException ex) {
            throw new SQLException("Database driver not found.", ex);
        }
    }
}
