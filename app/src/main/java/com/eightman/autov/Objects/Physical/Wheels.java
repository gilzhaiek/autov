package com.eightman.autov.Objects.Physical;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Utils.MathUtils;

/**
 * Created by gilzhaiek on 2016-10-29.
 */

public class Wheels {
    final double maxWheelsAngle;
    final double wheelsLengthBase;
    final double wheelsWidthBase;

    public Wheels(double wheelBase, double carWidth, double maxWheelsAngle) {
        this.maxWheelsAngle = maxWheelsAngle;
        this.wheelsLengthBase = wheelBase;
        this.wheelsWidthBase = carWidth;
    }

    public static Wheels generateRandom(double carLength, double carWidth) {
        return new Wheels(
                carLength * MathUtils.getRandomDouble(10, 20),
                carWidth,
                MathUtils.getRandomDouble(SimConfig.MIN_MAX_WHEELS_ANGEL, SimConfig.MAX_MAX_WHEELS_ANGLE));
    }

    public double getMaxWheelsAngle() {
        return maxWheelsAngle;
    }

    public double getWheelBase() {
        return wheelsLengthBase;
    }

    // a/sin(A) = b/sin(B) = c/sin(C)
    // For a triangle, B = maxWheelAngle, A=C=(180-maxWheelAngle)/2
    // b is the wheelsLengthBase and a=c=equals
    public double getSmallestRadius() {
        double A = (180 - maxWheelsAngle) / 2.0;
        double a = wheelsLengthBase * Math.sin(A) / Math.sin(maxWheelsAngle);
        return a;
    }
}
