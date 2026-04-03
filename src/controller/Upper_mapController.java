package controller;

import util.SpawnTag;

public class Upper_mapController extends BaseMapController {

    @Override
    protected String mapKey() {
        return "upper_map";
    }

    @Override
    protected void configureExits() {
        registerExit(exit_down, "/view/home_map.fxml", SpawnTag.FROM_UP);
    }
}
