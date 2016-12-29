package com.eightman.autov.Objects.Geom;

import com.eightman.autov.Utils.TrigUtils;

/**
 * Created by gilzhaiek on 2016-12-27.
 */

public class Circle {
    public enum Direction {
        CLOCK_WISE,
        COUNTER_CLOCK_WISE
    };

    final XY center;
    final double radius;
    final Direction direction;

    public Circle(XY center, double radius, Direction direction) {
        this.center = new XY(center);
        this.radius = radius;
        this.direction = direction;
    }

    public Circle(double centerX, double centerY, double radius, Direction direction) {
        this.center = new XY(centerX, centerY);
        this.radius = radius;
        this.direction = direction;
    }

    public static Circle getCircle(LineSegment segment, LineSegment outerSegment, double radius, Direction direction) {
        XY segmentCenter = segment.getCenter();
        XY segmentCenterOuter = outerSegment.getCenter();
        XY center = TrigUtils.getPoint(segmentCenter, segmentCenterOuter, radius);
        return new Circle(center, radius, direction);
    }

    public XY getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }

    public Direction getDirection() {
        return direction;
    }
}
