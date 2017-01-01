package com.eightman.autov.Objects.Geom;

import com.eightman.autov.Utils.MathUtils;
import com.eightman.autov.Utils.TrigUtils;

/**
 * Created by gilzhaiek on 2016-12-27.
 */

public class Circle {
    public enum Direction {
        CLOCK_WISE,
        COUNTER_CLOCK_WISE
    }

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

    public static Circle getCircle(XY pointOnCircumference, XY pointOutside, double radius, Direction direction) {
        XY center = TrigUtils.getPoint(pointOnCircumference, pointOutside, radius);
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

    public double getDistance(Circle circle) {
        return MathUtils.getDistance(center, circle.getCenter());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Circle)) {
            return false;
        }

        Circle other = (Circle) obj;

        // TODO - add some slack
        return other.getCenter().equals(center) &&
                other.getRadius() == radius &&
                other.getDirection() == direction;
    }
}
