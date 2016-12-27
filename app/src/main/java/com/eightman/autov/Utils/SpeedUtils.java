package com.eightman.autov.Utils;

import android.util.Pair;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Objects.CarCharacteristics;
import com.eightman.autov.Objects.Physical.Speed;

/**
 * Created by gilzhaiek on 2016-12-27.
 */

public class SpeedUtils {
    public static Pair<Double, Double> getAccWheelAngle(CarCharacteristics carCharacteristics,
                                                        double currentSpeed, double targetSpeed,
                                                        double currentWheelsAngle, double targetWheelsAngle,
                                                        long deltaTime) {
        Speed speed = carCharacteristics.getSpeed();

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

        double targetDec = 0;
        // If we are too fast, we should reduce speed for that wheels angle
        double comfortableSpeed = speed.getComfortableSpeed(wheelsAngle);
        if (currentSpeed > comfortableSpeed) {  // We are too fast
            // 10 - 20 = -10 < -3
            if (comfortableSpeed - currentSpeed < SimConfig.COMFORTABLE_DEC) {
                targetDec = SimConfig.COMFORTABLE_DEC;
            } else {
                targetDec = comfortableSpeed - currentSpeed;
            }
            return new Pair<>(MathUtils.getFactorSec(targetDec, deltaTime), wheelsAngle);
        }

        // We should increase our wheels angle till we are too fast or reached the wheels angle
        while (currentSpeed <= comfortableSpeed && wheelsAngle != targetWheelsAngle) {
            // 45-35 -> 10  45-55 -> -10 45+35 (-35->45) -> 75
            // -45+35 -> -10 -45+55 -> 10  -45-35(35->-45) - -75>
            double deltaWheels = targetWheelsAngle - currentWheelsAngle;
            if (deltaWheels > SimConfig.WHEELS_ANGLE_INCREASE) {
                deltaWheels = SimConfig.WHEELS_ANGLE_INCREASE;
            }

            wheelsAngle = currentWheelsAngle + deltaWheels;
            comfortableSpeed = speed.getComfortableSpeed(wheelsAngle);
        }

        if (comfortableSpeed - currentSpeed < SimConfig.COMFORTABLE_DEC) {
            targetDec = SimConfig.COMFORTABLE_DEC;
        } else {
            targetDec = comfortableSpeed - currentSpeed;
        }
        return new Pair<>(MathUtils.getFactorSec(targetDec, deltaTime), wheelsAngle);

    }
}
