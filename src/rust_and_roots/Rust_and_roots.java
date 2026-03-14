/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML.java to edit this template
 */
package rust_and_roots;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author larry
 */
public class Rust_and_roots extends Application {
    public static Stage stage;
    @Override
    public void start(Stage stage) throws Exception {
        Rust_and_roots.stage = stage;

//        stage.setFullScreen(true);

        util.SceneManager.switchScene("/view/new_game.fxml");
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
