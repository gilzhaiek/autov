package com.eightman.autov.Objects;

import com.eightman.autov.Utils.LineSegment;

import java.util.UUID;

/**
 * Created by gilzhaiek on 2016-12-06.
 */

public class ObjectDistanceInfo {
    final private double distance;
    final private LineSegment lineSegment;
    final UUID firstObjUUID;
    final UUID secondObjAUUID;

    public ObjectDistanceInfo(double distance, LineSegment lineSegment, UUID firstObjUUID, UUID secondObjAUUID) {
        this.distance = distance;
        this.lineSegment = lineSegment;
        this.firstObjUUID = firstObjUUID;
        this.secondObjAUUID = secondObjAUUID;
    }

    public double getDistance() {
        return distance;
    }

    public LineSegment getLineSegment() {
        return lineSegment;
    }

    public UUID getFirstObjUUID() {
        return firstObjUUID;
    }

    public UUID getSecondObjAUUID() {
        return secondObjAUUID;
    }
}
