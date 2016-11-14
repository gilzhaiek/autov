package com.eightman.autov.Utils;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Hardware.Boundaries;

/**
 * Created by gilzhaiek on 2016-11-10.
 */

public class CollisionUtils {
    public enum Zone {
        SAFE_ZONE,
        COLLISION_ZONE
    }

    public static Boundaries getParkingBoundaries(Boundaries carBoundaries) {
        return TrigUtils.boundariesAddition(carBoundaries, SimConfig.PARKING_SAFE_DISTANCE);
    }

    public static Boundaries getHeadingBoundaries(
            Boundaries carBoundaries,
            double speed,
            long timeToNextPosition,
            Zone zone) {
        if (speed < SimConfig.MIN_MOVING_SPEED) {
            return getParkingBoundaries(carBoundaries);
        }

        double frontDistanceMeters = (speed * timeToNextPosition / 1000.0) * SimConfig.COLLISION_ZONE_ERROR_ADDITION;
        double backDistanceMeters = 0;

        if (zone == Zone.SAFE_ZONE) {
            frontDistanceMeters += (carBoundaries.getLength() * SimConfig.SAFE_ZONE_ERROR_ADDITION);
            backDistanceMeters = (carBoundaries.getLength() * SimConfig.SAFE_ZONE_ERROR_ADDITION);
        }

        if (frontDistanceMeters < SimConfig.PARKING_SAFE_DISTANCE) {
            frontDistanceMeters = SimConfig.PARKING_SAFE_DISTANCE;
        }

        return TrigUtils.boundariesAddition(
                carBoundaries,
                frontDistanceMeters,
                backDistanceMeters,
                carBoundaries.getWidth()/2.0);
    }

    /*public static Collision getFirstCollision(CarPath carPathActive, long lastPoppedTimestampActive,
                                       CarPath carPathPassive, long lastPoppedTimestampPassive) {
        int idxActive = 0;
        int idxPassive = 0;

        CarPosition currentCarPositionActive = null;
        CarPosition previousCarPositionActive = null;
        CarPosition currentCarPositionPassive = null;
        CarPosition previousCarPositionPassive = null;
        while (true) {
            currentCarPositionActive = carPathActive.getPosition(idxActive);
            if (currentCarPositionActive == null) {
                break;
            }

            currentCarPositionPassive = carPathPassive.getPosition(idxPassive);
            if (currentCarPositionPassive == null) {
                break;
            }


        }

        return null;
    }*/
}
