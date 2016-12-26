package com.eightman.autov.Objects.Physical;

/**
 * Created by gilzhaiek on 2016-10-29.
 */

public class Vehicle {
    Motor motor;
    Speed speed;

    public Vehicle() {
        this.motor = new Motor();
    }

    public Motor getMotor() {
        return motor;
    }

    public void setMotor(Motor motor) {
        this.motor = motor;
    }

    public Speed getSpeed() {
        return speed;
    }

    public void setSpeed(Speed speed) {
        this.speed = speed;
    }
}
