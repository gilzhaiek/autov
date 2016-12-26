package com.eightman.autov.Objects;

import com.eightman.autov.Configurations.Constants;

import java.util.UUID;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class MyCar {
    Object lock = new Object();

    final UUID uuid;
    final CarCharacteristics carCharacteristics;
    final CarPath carPath;

    CarPosition carPosition;
    boolean isInAccident = false;
    int numOfAccidents = 0;
    final static long timeResolution = Constants.ONE_SECOND;

    public MyCar(UUID uuid, CarCharacteristics carChar, CarPosition carPosition) {
        this.uuid = uuid;
        this.carCharacteristics = carChar;
        this.carPosition = carPosition;
        this.carPath = new CarPath(uuid, carPosition);
    }

    public CarCharacteristics getCarCharacteristics() {
        return carCharacteristics;
    }

    public CarPosition getCarPosition() {
        synchronized (carPosition) {
            return carPosition;
        }
    }

    public void setCarPosition(CarPosition carPosition) {
        if (carPosition == null) {
            return;
        }
        synchronized (carPosition) {
            this.carPosition = carPosition;
        }
    }

    public CarPath getCarPath() {
        return carPath;
    }

    public boolean isInAccident() {
        return isInAccident;
    }

    public void setInAccident(boolean inAccident) {
        if (inAccident == true && isInAccident == false) {
            numOfAccidents++;
        }
        isInAccident = inAccident;
    }

    public int getNumOfAccidents() {
        return numOfAccidents;
    }

    public UUID getUuid() {
        return uuid;
    }

    public static long getTimeResolution() {
        return timeResolution;
    }
}
