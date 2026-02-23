/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 *
 * @author ASUS
 */
public class SceneManager {
    public static void switchScene(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(
                SceneManager.class.getResource(fxmlPath)
            );

            Scene scene = new Scene(root, 600, 400);
            rust_and_roots.Rust_and_roots.stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
