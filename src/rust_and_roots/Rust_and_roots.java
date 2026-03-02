/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML.java to edit this template
 */
package rust_and_roots;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.SceneManager;

/**
 *
 * @author larry
 */
public class Rust_and_roots extends Application {
    public static Stage stage;
    @Override
    public void start(Stage stage) throws Exception {
        
        Rust_and_roots.stage = stage;  // <-- IMPORTANT

        Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
//        Parent root = FXMLLoader.load(getClass().getResource("/view/new_game.fxml"));
//        Parent root = FXMLLoader.load(getClass().getResource("/view/home_map.fxml"));
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();

//        SceneManager.switchScene("/view/home_map.fxml");
//        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
