package controller;

import util.SpawnTag;

public class Left_mapController extends BaseMapController {

    @Override
    protected String mapKey() {
        return "left_map";
    }

    @Override
    protected void configureExits() {
        registerExit(exit_left, "/view/home_map.fxml", SpawnTag.FROM_LEFT);
    }
}
