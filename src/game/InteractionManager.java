/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Bounds;
import model.InteractionZone;

/**
 *
 * @author ASUS
 */
public class InteractionManager {
    private final List<InteractionZone> zones = new ArrayList<>();

    public void addZone(InteractionZone zone) {
        zones.add(zone);
    }

    public void updateAll(Bounds playerBounds) {
        double nowGameSeconds = GameTimeManager.get().totalSecondsProperty().get();
        zones.forEach(zone -> zone.update(playerBounds, nowGameSeconds));
    }
}
