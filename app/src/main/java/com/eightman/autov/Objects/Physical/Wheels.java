package com.eightman.autov.Objects.Physical;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Utils.MathUtils;

/**
 * Created by gilzhaiek on 2016-10-29.
 */

public class Wheels {
    final double maxWheelsAngleChange;

    public Wheels(double maxWheelsAngleChange) {
        this.maxWheelsAngleChange = maxWheelsAngleChange;
    }

    public static Wheels generateRandom() {
        return new Wheels(MathUtils.getRandomDouble(SimConfig.MIN_ANGLE_CHANGE_WHEEL_TURN,
                SimConfig.MAX_ANGLE_CHANGE_WHEEL_TURN));
    }

    public double getMaxWheelsAngleChange() {
        return maxWheelsAngleChange;
    }
}
