package com.eightman.autov.Movement;

import android.util.Pair;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Objects.CarPosition;
import com.eightman.autov.Objects.MyCar;
import com.eightman.autov.Objects.Physical.Wheels;
import com.eightman.autov.Utils.MathUtils;
import com.eightman.autov.Utils.SpeedUtils;

/**
 * Created by gilzhaiek on 2016-12-26.
 */

public class SpeedManager {
    private static double getAccDec(double currentSpeedMS, double targetSpeedMS, double maxAccMSS,
                                    long deltaTime) {
        double accDecMS = targetSpeedMS - currentSpeedMS;
        if (accDecMS == 0) {
            return 0.0;
        } else {
            // If we need to increase in 20m/s - we break it to parts (100ms -> 10
            // 2 m/100ms
            double accDec = MathUtils.getFactorSec(accDecMS, deltaTime);
            if (accDec > 0) {
                double maxAcc = MathUtils.getFactorSec(maxAccMSS, deltaTime);
                return accDec > maxAcc ? maxAcc : accDec;
            } else {
                double comfortableDec = MathUtils.getFactorSec(SimConfig.COMFORTABLE_DEC, deltaTime);
                return accDec < comfortableDec ? comfortableDec : accDec;
            }
        }
    }

    public static CarPosition adjustCarPosition(MyCar car, CarPosition carPosition,
                                                double targetWheelsAngle, long deltaTime) {
        double currentSpeedMS = carPosition.getSpeed();
        double currentWheelAngle = carPosition.getWheelsAngle();

        Wheels wheels = car.getCarCharacteristics().getWheels();

        double deltaWheelsAngle = targetWheelsAngle - carPosition.getWheelsAngle();
        // Make sure you don't go over the max wheels angle
        if (Math.abs(deltaWheelsAngle) > wheels.getMaxWheelsAngle()) {
            targetWheelsAngle = deltaWheelsAngle > 0 ? wheels.getMaxWheelsAngle() :
                    (-1 * wheels.getMaxWheelsAngle());
        }

        Pair<Double, Double> accWheels = SpeedUtils.getAccWheelsAngle(car.getCarCharacteristics(),
                currentSpeedMS,
                car.getCarCharacteristics().getSpeed().getTopSpeed(), // Assume we want to go the fastest
                currentWheelAngle, targetWheelsAngle);

        return CarPosition.getMovingPosition(carPosition.getBoundaries(), currentSpeedMS,
                accWheels.first, accWheels.second, deltaTime);
    }
}
