package services;

import controllers.MainContoller;

public class SpeedTestCallback {
    private final MainContoller contoller;

    public SpeedTestCallback(MainContoller contoller) {
        this.contoller = contoller;
    }

    public void updateSpeed(String speed) {
        this.contoller.updateSpeed(speed);
    }
}
