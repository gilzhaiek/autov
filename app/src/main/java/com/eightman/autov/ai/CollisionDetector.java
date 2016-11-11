package com.eightman.autov.ai;

import android.util.Pair;

import com.eightman.autov.Hardware.Boundaries;
import com.eightman.autov.Objects.CarPath;
import com.eightman.autov.Objects.CarPosition;
import com.eightman.autov.Objects.Collision;
import com.eightman.autov.Utils.XY;

/**
 * Created by gilzhaiek on 2016-11-10.
 */

public class CollisionDetector {
    private Boundaries getRealBoundary(CarPosition.Final carPosition, long deltaTime) {
        Boundaries carBoundaries = carPosition.getBoundaries();

        double maxDistanceMeters = carPosition.getSpeed() * deltaTime / 1000.0;
        double largestSide = carBoundaries.getLargestSide();

        // TODO: Fix
        if (maxDistanceMeters >= largestSide) {
            return new Pair<>(maxDistanceMeters, carBoundaries.getCenterFront());
        } else {
            return new Pair<>(largestSide + maxDistanceMeters, carBoundaries.getCenter());
        }
    }

    public Collision getFirstCollision(CarPath carPathActive, long lastPoppedTimestampActive,
                                       CarPath carPathPassive, long lastPoppedTimestampPassive) {
        int idxActive = 0;
        int idxPassive = 0;

        CarPosition.Final currentCarPositionActive = null;
        CarPosition.Final previousCarPositionActive = null;
        CarPosition.Final currentCarPositionPassive = null;
        CarPosition.Final previousCarPositionPassive = null;
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
    }
}
