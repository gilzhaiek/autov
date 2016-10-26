package com.eightman.autov.Objects;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class Car {
    CarCharacteristics carCharacteristics;
    CarLocation carLocation;
    CarPath carPath;
    double currentSpeed;
    double targetSpeed;
    double acceleration;

    public Car(CarPath carPath, CarLocation carLocation, CarCharacteristics carChar) {
        this.carPath = carPath;
        this.carLocation = carLocation;
        this.carCharacteristics = carChar;
    }

    public void setTargetSpeed(double speed, double acceleration) {

    }
}
