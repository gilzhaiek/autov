package com.eightman.autov.Objects;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class MyCar {
    CarCharacteristics carCharacteristics;
    CarLocation carLocation;
    CarPath carPath;
    double currentSpeed;
    double targetSpeed;
    double acceleration;

    public MyCar(CarPath carPath, CarLocation carLocation, CarCharacteristics carChar) {
        this.carPath = carPath;
        this.carLocation = carLocation;
        this.carCharacteristics = carChar;
    }

    public void setTargetSpeed(double speed, double acceleration) {

    }
}
