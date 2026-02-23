package model;

import java.util.List;
import javafx.scene.Group;
import javafx.scene.image.ImageView;

public final class FarmPlotRegistrar {

    private FarmPlotRegistrar() {}

    public static void registerPlots(Farm farm, Group farmGroup) {
        List<ImageView> plots =
                NodeCollectors.byTypeAndPrefix(farmGroup, ImageView.class, "farm_img");

        NodeCollectors.sortByIdNumber(plots, "farm_img");

        for (ImageView img : plots) {
            int number = NodeCollectors.extractNumber(img.getId(), "farm_img");
            farm.addPlot("Plot " + number, img);
        }
    }
}