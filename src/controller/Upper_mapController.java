package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import util.SpawnTag;

/**
 * FXML Controller class
 *
 * @author Kaung Zin
 */
public class Upper_mapController extends BaseMapController {

    @Override
    protected String mapKey() {
        return "upper_map";
    }

    @Override
    protected void configureExits() {
        registerExit(exit_up, "/view/upper_map_notwo.fxml", SpawnTag.FROM_DOWN);
        registerExit(exit_down, "/view/home_map.fxml", SpawnTag.FROM_UP);
        registerExit(exit_right, "/view/right_map_noone.fxml", SpawnTag.FROM_LEFT);
    }
  
    
}
