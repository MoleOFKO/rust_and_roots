/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javax.swing.JOptionPane;
import static rust_and_roots.Rust_and_roots.stage;

/**
 * FXML Controller class
 *
 * @author Shane Khant Ko
 */
public class New_gameController implements Initializable {

    @FXML
    private TextField txtchaname;
    @FXML
    private Label lblstr;
    @FXML
    private Label lblagi;
    @FXML
    private Label lblvit;
    @FXML
    private Button btnStartGame;
    @FXML
    private ImageView char_img;
    @FXML
    private Button btnback;
    @FXML
    private Label lbldex;
    @FXML
    private Label lblhp;
    @FXML
    private Label lblstamina;
    @FXML
    private TextField txtworldname;

    Parent root;
    public static String char_name;
    public static String world_name;
    public static int str;
    public static int agi;
    public static int vit;
    public static int dex;
    public static int hp;
    public static int stamina;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    

    @FXML
    private void handlebtnStartGame(ActionEvent event) throws IOException {
        if(txtchaname.getText().isEmpty())
            JOptionPane.showMessageDialog(null, "Please Enter Character Name!");
        else{
            char_name = txtchaname.getText();
            world_name = txtworldname.getText();
            
            str = Integer.parseInt(lblstr.getText());
            agi = Integer.parseInt(lblagi.getText());
            vit = Integer.parseInt(lblvit.getText());
            dex = Integer.parseInt(lbldex.getText());
            hp = Integer.parseInt(lblhp.getText());
            stamina = Integer.parseInt(lblstamina.getText());

            
            root = FXMLLoader.load(getClass().getResource("/view/home_map.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML
    private void handlebackaction(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/main_menu.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
}
