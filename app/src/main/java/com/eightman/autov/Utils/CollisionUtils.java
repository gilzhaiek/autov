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

    public enum Side {
        AFBF, AFBR, AFBB, AFBL,
        ARBF, ARBR, ARBB, ARBL,
        ABBF, ABBR, ABBB, ABBL,
        ALBF, ALBR, ALBB, ALBL,
        NONE
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

    public static Side isColliding(Boundaries boundariesA, Boundaries boundariesB) {
        if (TrigUtils.edgeIntersection(boundariesA.getFrontEdge(), boundariesB.getFrontEdge()) != null) {
            return Side.AFBF;
        }
        if (TrigUtils.edgeIntersection(boundariesA.getFrontEdge(), boundariesB.getRightEdge()) != null) {
            return Side.AFBR;
        }
        if (TrigUtils.edgeIntersection(boundariesA.getFrontEdge(), boundariesB.getBackEdge()) != null) {
            return Side.AFBB;
        }
        if (TrigUtils.edgeIntersection(boundariesA.getFrontEdge(), boundariesB.getLeftEdge()) != null) {
            return Side.AFBL;
        }

        if (TrigUtils.edgeIntersection(boundariesA.getRightEdge(), boundariesB.getFrontEdge()) != null) {
            return Side.ARBF;
        }
        if (TrigUtils.edgeIntersection(boundariesA.getRightEdge(), boundariesB.getRightEdge()) != null) {
            return Side.ARBR;
        }
        if (TrigUtils.edgeIntersection(boundariesA.getRightEdge(), boundariesB.getBackEdge()) != null) {
            return Side.ARBB;
        }
        if (TrigUtils.edgeIntersection(boundariesA.getRightEdge(), boundariesB.getLeftEdge()) != null) {
            return Side.ARBL;
        }

        if (TrigUtils.edgeIntersection(boundariesA.getBackEdge(), boundariesB.getFrontEdge()) != null) {
            return Side.ABBF;
        }
        if (TrigUtils.edgeIntersection(boundariesA.getBackEdge(), boundariesB.getRightEdge()) != null) {
            return Side.ABBR;
        }
        if (TrigUtils.edgeIntersection(boundariesA.getBackEdge(), boundariesB.getBackEdge()) != null) {
            return Side.ABBB;
        }
        if (TrigUtils.edgeIntersection(boundariesA.getBackEdge(), boundariesB.getLeftEdge()) != null) {
            return Side.ABBL;
        }

        if (TrigUtils.edgeIntersection(boundariesA.getLeftEdge(), boundariesB.getFrontEdge()) != null) {
            return Side.ALBF;
        }
        if (TrigUtils.edgeIntersection(boundariesA.getLeftEdge(), boundariesB.getRightEdge()) != null) {
            return Side.ALBR;
        }
        if (TrigUtils.edgeIntersection(boundariesA.getLeftEdge(), boundariesB.getBackEdge()) != null) {
            return Side.ALBB;
        }
        if (TrigUtils.edgeIntersection(boundariesA.getLeftEdge(), boundariesB.getLeftEdge()) != null) {
            return Side.ALBL;
        }

        return Side.NONE;
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
