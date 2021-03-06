package com.eightman.autov.Objects.Physical;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Utils.MathUtils;
import com.eightman.autov.Utils.TrigUtils;

import static com.eightman.autov.Configurations.SimConfig.MAX_WHEEL_BASE;
import static com.eightman.autov.Configurations.SimConfig.MIN_WHEEL_BASE;

/**
 * Created by gilzhaiek on 2016-10-29.
 */

public class Wheels {
    public static final double STRAIGHT_WHEELS = 0.0;
    final double maxWheelsAngle;
    final double wheelsLengthBase;
    final double wheelsWidthBase;

    public Wheels(double wheelBase, double carWidth, double maxWheelsAngle) {
        this.maxWheelsAngle = maxWheelsAngle;
        this.wheelsLengthBase = wheelBase;
        this.wheelsWidthBase = carWidth;
    }

    public static Wheels generateRandom(double carWidth, double carLength) {
        return new Wheels(
                carLength * MathUtils.getRandomDouble(MIN_WHEEL_BASE, MAX_WHEEL_BASE),
                carWidth,
                MathUtils.getRandomDouble(SimConfig.MIN_MAX_WHEELS_ANGEL, SimConfig.MAX_MAX_WHEELS_ANGLE));
    }

    public double getMaxWheelsAngle() {
        return maxWheelsAngle;
    }

    public double getWheelBase() {
        return wheelsLengthBase;
    }

    public double getSmallestRadius() {
        return TrigUtils.getRadius(maxWheelsAngle, wheelsLengthBase);
    }
}
