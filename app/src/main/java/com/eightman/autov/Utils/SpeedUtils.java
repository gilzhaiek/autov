package com.eightman.autov.Utils;

import android.util.Pair;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Objects.CarCharacteristics;
import com.eightman.autov.Objects.Physical.AccDec;
import com.eightman.autov.Objects.Physical.Speed;

/**
 * Created by gilzhaiek on 2016-12-27.
 */

public class SpeedUtils {
    /**
     * Return a Pair of the acc in m/s^2 and the wheels angle
     *
     * @param carCharacteristics
     * @param currentSpeed
     * @param targetSpeed
     * @param currentWheelsAngle
     * @param targetWheelsAngle
     * @return
     */
    public static Pair<Double, Double> getAccWheelsAngle(CarCharacteristics carCharacteristics,
                                                         double currentSpeed, double targetSpeed,
                                                         double currentWheelsAngle, double targetWheelsAngle) {
        Speed speed = carCharacteristics.getSpeed();
        AccDec accDec = carCharacteristics.getAccDec();

        double wheelsAngle = currentWheelsAngle;
        // If the wheels angle is going to decrease, we should decrease the wheels angle
        if (Math.abs(targetWheelsAngle) < Math.abs(currentWheelsAngle)) {
            // Check if we setting the wheels straight
            if ((targetWheelsAngle > 0 && currentSpeed < 0) ||
                    (targetWheelsAngle < 0 && currentSpeed > 0)) {
                wheelsAngle = 0;
            } else {
                wheelsAngle = targetWheelsAngle;
            }
        }

        double fastestSpeed = targetSpeed > currentSpeed ? targetSpeed : currentSpeed;
        double targetAccDec = 0; // in m/s^2
        // If we are too fast, we should reduce speed for that wheels angle
        double comfortableSpeed = speed.getComfortableSpeed(wheelsAngle);
        if (fastestSpeed > comfortableSpeed) {  // We are too fast
            // 10 - 20 = -10 < -3
            if (comfortableSpeed - fastestSpeed < accDec.getComfortableDec(currentSpeed)) {
                targetAccDec = accDec.getComfortableDec(comfortableSpeed);
            } else {
                targetAccDec = comfortableSpeed - fastestSpeed;
            }
            return new Pair<>(targetAccDec, wheelsAngle);
        }

        // We should increase our wheels angle till we are too fast or reached the wheels angle
        while ((fastestSpeed <= comfortableSpeed) && (wheelsAngle != targetWheelsAngle)) {
            // 45-35 -> 10  45-55 -> -10 45+35 (-35->45) -> 75
            // -45+35 -> -10 -45+55 -> 10  -45-35(35->-45) - -75>
            double deltaWheels = targetWheelsAngle - currentWheelsAngle;
            if (deltaWheels > SimConfig.WHEELS_ANGLE_INCREASE) {
                deltaWheels = SimConfig.WHEELS_ANGLE_INCREASE;
            }

            wheelsAngle = currentWheelsAngle + deltaWheels;
            comfortableSpeed = speed.getComfortableSpeed(wheelsAngle);
        }

        // We should set our speed
        if (targetSpeed > comfortableSpeed) {
            targetSpeed = comfortableSpeed;
        }

        targetAccDec = targetSpeed - currentSpeed;
        if (targetAccDec < accDec.getComfortableDec(targetSpeed)) {
            targetAccDec = accDec.getComfortableDec(targetSpeed);
        } else if (targetAccDec > accDec.getAcceleration(currentSpeed)) { // Can't accelerate too quickly
            targetAccDec = accDec.getAcceleration(currentSpeed);
        }
        return new Pair<>(targetAccDec, wheelsAngle);
    }
}
