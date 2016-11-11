package com.eightman.autov.Objects;

import java.util.List;
import java.util.UUID;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class MyCar {
    Object lock = new Object();

    final UUID uuid;
    final CarCharacteristics carCharacteristics;
    final CarPosition carPosition;
    final CarPath carPath;
    double currentSpeed;
    double targetSpeed;
    double acceleration;
    boolean isInAccident = false;

    public MyCar(UUID uuid, CarCharacteristics carChar, CarPosition carPosition) {
        this.uuid = uuid;
        this.carCharacteristics = carChar;
        this.carPosition = carPosition;
        this.carPath = new CarPath(uuid, carPosition.getPosition());
    }

    public CarCharacteristics getCarCharacteristics() {
        return carCharacteristics;
    }

    public CarPosition getCarPosition() {
        return carPosition;
    }

    public void setCarPosition(CarPosition.Final carPosition) {
        this.carPosition.setPosition(carPosition);
    }

    public CarPath getCarPath() {
        return carPath;
    }

    public boolean addPath(List<CarPosition.Final> path, boolean firstPositionIsLast) {
        return this.carPath.add(path, firstPositionIsLast);
    }

    public boolean isInAccident() {
        return isInAccident;
    }

    public void setInAccident(boolean inAccident) {
        isInAccident = inAccident;
    }

    public UUID getUuid() {
        return uuid;
    }

    public double getCurrentSpeed() {
        synchronized (lock) {
            return currentSpeed;
        }
    }

    public void setCurrentSpeed(double currentSpeed) {
        synchronized (lock) {
            this.currentSpeed = currentSpeed;
        }
    }

    public double getAcceleration() {
        synchronized (lock) {
            return acceleration;
        }
    }

    public double getTargetSpeed() {
        return targetSpeed;
    }

    public void setTargetSpeed(double targetSpeed, double acceleration) {
        synchronized (lock) {
            this.targetSpeed = targetSpeed;
            this.acceleration = acceleration;
        }
    }
}
