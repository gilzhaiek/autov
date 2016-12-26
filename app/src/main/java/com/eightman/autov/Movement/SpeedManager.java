package com.eightman.autov.Movement;

import com.eightman.autov.Configurations.Constants;
import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Objects.CarPosition;
import com.eightman.autov.Objects.MyCar;
import com.eightman.autov.Objects.Physical.AccDec;
import com.eightman.autov.Objects.Physical.Speed;
import com.eightman.autov.Objects.Physical.Wheels;

/**
 * Created by gilzhaiek on 2016-12-26.
 */

public class SpeedManager {
    private static double getAccDec(double currentSpeed, double targetSpeed, double maxAcc, long deltaTime) {
        double deltaSpeed = targetSpeed - currentSpeed;
        if (deltaSpeed == 0) {
            return 0.0;
        } else {
            double accDec = deltaSpeed * (Constants.ONE_SECOND / deltaTime);
            if (accDec > 0) {
                return accDec > maxAcc ? maxAcc : accDec;
            } else {
                return accDec < SimConfig.COMFORTABLE_DEC ? SimConfig.COMFORTABLE_DEC : accDec;
            }
        }
    }

    public static CarPosition adjustCarPosition(MyCar car, CarPosition carPosition,
                                                double targetWheelsAngle, long deltaTime) {
        double currentSpeed = carPosition.getSpeed();
        double currentWheelAngle = carPosition.getWheelsAngle();

        Speed speed = car.getCarCharacteristics().getSpeed();
        AccDec accDec = car.getCarCharacteristics().getAccDec();
        Wheels wheels = car.getCarCharacteristics().getWheels();
        double deltaWheelsAngle = targetWheelsAngle - carPosition.getWheelsAngle();

        if(Math.abs(deltaWheelsAngle) > wheels.getMaxWheelsAngleChange()) {
            deltaWheelsAngle = deltaWheelsAngle > 0 ? wheels.getMaxWheelsAngleChange() :
                    (-1 * wheels.getMaxWheelsAngleChange());
        }

        double newWheelsAngle = currentWheelAngle + deltaWheelsAngle;
        double targetSpeed = speed.getComfortableSpeed(newWheelsAngle);
        double acceleration = getAccDec(currentSpeed, targetSpeed,
                accDec.getAcceleration(targetSpeed), deltaTime);
        return CarPosition.getMovingPosition(carPosition.getBoundaries(), currentSpeed,
                acceleration, newWheelsAngle, deltaTime);
    }
}
