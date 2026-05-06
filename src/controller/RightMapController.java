package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import util.SpawnTag;

public class RightMapController extends BaseMapController {

    private static final String DAY_BG_PATH = "/image/maps/rightmap.png";
    private static final String NIGHT_BG_PATH = "/image/NightMap/rightmapnight.png";

    @Override
    protected String dayBackgroundPath() {
        return DAY_BG_PATH;
    }

    @Override
    protected String nightBackgroundPath() {
        return NIGHT_BG_PATH;
    }

    @Override
    protected String mapKey() {
        return "right_map";
    }

    @Override
    protected void configureExits() {
        // right_map <-> home_map
        registerExit(exit_left, "/view/home_map.fxml", SpawnTag.FROM_RIGHT);

        // right_map -> right_map_noone
        registerExit(exit_up, "/view/right_map_noone.fxml", SpawnTag.FROM_DOWN);
    }

    @FXML
    private void handlebtntutoaction(ActionEvent event) {
        showOverworldTutorialOverlay();
        if (event != null) {
            event.consume();
        }
    }
}

