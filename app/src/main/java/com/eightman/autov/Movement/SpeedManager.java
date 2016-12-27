package com.eightman.autov.Movement;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Objects.CarPosition;
import com.eightman.autov.Objects.MyCar;
import com.eightman.autov.Objects.Physical.AccDec;
import com.eightman.autov.Objects.Physical.Speed;
import com.eightman.autov.Objects.Physical.Wheels;
import com.eightman.autov.Utils.MathUtils;

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

        Speed speed = car.getCarCharacteristics().getSpeed();
        AccDec accDec = car.getCarCharacteristics().getAccDec();
        Wheels wheels = car.getCarCharacteristics().getWheels();
        double deltaWheelsAngle = targetWheelsAngle - carPosition.getWheelsAngle();

        // Make sure you don't go over the max wheel angle
        if (Math.abs(deltaWheelsAngle) > wheels.getMaxWheelsAngle()) {
            deltaWheelsAngle = deltaWheelsAngle > 0 ? wheels.getMaxWheelsAngle() :
                    (-1 * wheels.getMaxWheelsAngle());
        }

        double newWheelsAngle = currentWheelAngle + deltaWheelsAngle;
        double targetSpeedMS = speed.getComfortableSpeed(newWheelsAngle);
        double acceleration = getAccDec(currentSpeedMS, targetSpeedMS,
                accDec.getAcceleration(targetSpeedMS), deltaTime);
        return CarPosition.getMovingPosition(carPosition.getBoundaries(), currentSpeedMS,
                acceleration, newWheelsAngle, deltaTime);
    }
}
