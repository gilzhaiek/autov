package com.eightman.autov.Objects.Physical;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Utils.MathUtils;

/**
 * Created by gilzhaiek on 2016-10-29.
 */

public class Speed {
    final double topSpeed; // m/s
    final double maxWheelsAngle;
    final double comfortableSpeedWheelsAngle[];

    public Speed(double topSpeed, double maxWheelsAngle) {
        this.topSpeed = topSpeed;
        this.maxWheelsAngle = maxWheelsAngle;
        comfortableSpeedWheelsAngle = new double[(int) maxWheelsAngle + 1];

        double maxAngleMaxSpeed = SimConfig.COMFORTABLE_SPEED_FOR_MAX_ANGLE;
        if (maxAngleMaxSpeed > this.topSpeed) {
            maxAngleMaxSpeed = this.topSpeed;
        }
        for (int i = 0; i < comfortableSpeedWheelsAngle.length - 1; i++) {
            comfortableSpeedWheelsAngle[i] = this.topSpeed -
                    (this.topSpeed - maxAngleMaxSpeed) * ((double) i) / SimConfig.MAX_MAX_WHEELS_ANGLE;
        }
    }

    public static Speed generateRandom() {
        return new Speed(MathUtils.getRandomDouble(SimConfig.MIN_TOP_SPEED, SimConfig.MAX_TOP_SPEED),
                MathUtils.getRandomDouble(SimConfig.MIN_MAX_WHEELS_ANGEL, SimConfig.MAX_MAX_WHEELS_ANGLE));
    }

    public double getTopSpeed() {
        return topSpeed;
    }

    public double getMaxWheelsAngle() {
        return maxWheelsAngle;
    }

    public double getComfortableSpeed(double wheelsAngle) {
        wheelsAngle = Math.abs(wheelsAngle);
        if (wheelsAngle > maxWheelsAngle) {
            wheelsAngle = maxWheelsAngle;
        }
        return comfortableSpeedWheelsAngle[(int) wheelsAngle];
    }
}
