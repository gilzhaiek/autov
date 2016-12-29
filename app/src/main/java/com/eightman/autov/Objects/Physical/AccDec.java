package com.eightman.autov.Objects.Physical;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Utils.MathUtils;

/**
 * Created by gilzhaiek on 2016-12-26.
 */

public class AccDec {
    final double acceleration[]; // m/s^2
    final double emergencyBreak; // m/s^2
    final double maxSpeed;
    final double comfortableDec;

    public AccDec(double maxSpeed, double startAcc, double emergencyDec) {
        this.maxSpeed = maxSpeed;
        acceleration = new double[(int) maxSpeed + 1];

        int i = 0;
        for (; i < acceleration.length - 1; i++) {
            acceleration[i] = startAcc - startAcc * ((double) i) / maxSpeed;
        }
        acceleration[i] = 0;

        emergencyBreak = emergencyDec;
        if(emergencyDec > SimConfig.COMFORTABLE_DEC) {
            comfortableDec = emergencyDec;
        } else {
            comfortableDec = SimConfig.COMFORTABLE_DEC;
        }
    }

    public static AccDec generateRandom(double maxSpeed) {
        double startAcc = MathUtils.getRandomDouble(SimConfig.MIN_ACC, SimConfig.MAX_ACC);
        double emergencyDec = MathUtils.getRandomDouble(SimConfig.MIN_EMERGENCY_DEC, SimConfig.MAX_EMERGENCY_DEC);
        return new AccDec(maxSpeed, startAcc, emergencyDec);
    }

    public double getAcceleration(double speed) {
        if (speed > maxSpeed) {
            speed = maxSpeed;
        }

        return acceleration[(int) speed];
    }

    public double getStopDistance(double currentSpeed) {
        if (currentSpeed == 0) {
            return 0;
        }

        double distance = currentSpeed;
        while (currentSpeed > 0) {
            currentSpeed += comfortableDec;
            distance += currentSpeed;
        }

        return distance;
    }

    public double getEmergencyBreak() {
        return emergencyBreak;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getComfortableDec() {
        return comfortableDec;
    }
}
