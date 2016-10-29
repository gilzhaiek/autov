package com.eightman.autov.Objects;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class MyCar {
    CarCharacteristics carCharacteristics;
    CarPosition carPosition;
    CarPath carPath;
    double currentSpeed;
    double targetSpeed;
    double acceleration;

    public MyCar(CarPath carPath, CarPosition carPosition, CarCharacteristics carChar) {
        this.carPath = carPath;
        this.carPosition = carPosition;
        this.carCharacteristics = carChar;
    }

    public void setTargetSpeed(double speed, double acceleration) {

    }
}
