package com.eightman.autov.Objects.Physical;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Utils.MathUtils;

/**
 * Created by gilzhaiek on 2016-10-29.
 */

public class Wheels {
    final double maxWheelsAngle;

    public Wheels(double maxWheelsAngle) {
        this.maxWheelsAngle = maxWheelsAngle;
    }

    public static Wheels generateRandom() {
        return new Wheels(MathUtils.getRandomDouble(SimConfig.MIN_MAX_WHEELS_ANGEL,
                SimConfig.MAX_MAX_WHEELS_ANGLE));
    }

    public double getMaxWheelsAngle() {
        return maxWheelsAngle;
    }
}
