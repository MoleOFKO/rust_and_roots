/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import db.LoadGameService;
import game.GameState;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import static rust_and_roots.Rust_and_roots.stage;
import util.SceneManager;

/**
 * FXML Controller class
 *
 * @author larry
 */
public class SaveloadsController implements Initializable {

    @FXML
    private Button btnLoad;
    @FXML
    private Button btnback;

    Parent root;
    @FXML
    private Label save1;
    @FXML
    private Button btnsave1;
    @FXML
    private Label save2;
    @FXML
    private Button btnsave2;
    @FXML
    private Label save3;
    @FXML
    private Button btnsave3;
    @FXML
    private Label save4;
    @FXML
    private Button btnsave4;
    @FXML
    private ImageView cimg;
    @FXML
    private Label cname;
    @FXML
    private Label wname;
    @FXML
    private Label day;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void handlebtnLoadaction(ActionEvent event) {
        try {
            int saveId = GameState.get().getCurrentSaveId();

            if (saveId <= 0) {
                System.out.println("No save found for current account.");
                return;
            }

            LoadGameService loadService = new LoadGameService();
            boolean ok = loadService.loadGame(saveId);

            if (!ok) {
                System.out.println("Load failed.");
                return;
            }

            String mapKey = GameState.get().getCurrentMapKey();
            SceneManager.switchScene("/view/" + mapKey + ".fxml");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlebackaction(ActionEvent event) throws IOException {
        util.SceneManager.switchScene("/view/main_menu.fxml");
    }

    @FXML
    private void handlebtnsave1action(ActionEvent event) {
    }

    @FXML
    private void handlebtnsave2action(ActionEvent event) {
    }

    @FXML
    private void handlebtnsave3action(ActionEvent event) {
    }

    @FXML
    private void handlebtnsave4action(ActionEvent event) {
    }
    
}
