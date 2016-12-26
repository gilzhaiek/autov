package com.eightman.autov.Objects;

import com.eightman.autov.Objects.Geom.LineSegment;

import java.util.UUID;

/**
 * Created by gilzhaiek on 2016-12-06.
 */

public class ObjectDistanceInfo {
    public enum SegmentType {
        COLLISION_ZONE,
        DECISION_ZONE_ACTIVE,
        DECISION_ZONE_PASSIVE
    }

    final private SegmentType segmentType;
    final private double distance;
    final private LineSegment lineSegment;
    final UUID firstObjUUID;
    final UUID secondObjAUUID;

    public ObjectDistanceInfo(SegmentType segmentType, double distance, LineSegment lineSegment, UUID firstObjUUID, UUID secondObjAUUID) {
        this.segmentType = segmentType;
        this.distance = distance;
        this.lineSegment = lineSegment;
        this.firstObjUUID = firstObjUUID;
        this.secondObjAUUID = secondObjAUUID;
    }

    public SegmentType getSegmentType() {
        return segmentType;
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
