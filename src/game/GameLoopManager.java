/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import javafx.animation.AnimationTimer;

/**
 *
 * @author ASUS
 */
public class GameLoopManager {

    private final AnimationTimer timer;

    public GameLoopManager(Runnable onUpdate) {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                onUpdate.run();
            }
        };
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }
}