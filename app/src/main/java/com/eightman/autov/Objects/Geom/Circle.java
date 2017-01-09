package com.eightman.autov.Objects.Geom;

import com.eightman.autov.Utils.MathUtils;
import com.eightman.autov.Utils.TrigUtils;

/**
 * Created by gilzhaiek on 2016-12-27.
 */

public class Circle {
    public enum Direction {
        CLOCK_WISE,
        COUNTER_CLOCK_WISE,
        STRAIGHT
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

    public double getRatio(XY fromPoint, XY toPoint, XY pointForRatio) {
        double fromAngle = TrigUtils.getAngleRad(center,
                direction == Direction.CLOCK_WISE ? toPoint : fromPoint);
        double toAngle = TrigUtils.getAngleRad(center,
                direction == Direction.CLOCK_WISE ? fromPoint : toPoint);
        double pointForRatioAngle = TrigUtils.getAngleRad(center, pointForRatio);
        if (fromAngle > toAngle) {
            fromAngle -= Math.PI * 2.0;
        }
        if (pointForRatioAngle < fromAngle) {
            pointForRatioAngle += Math.PI * 2.0;
        }
        double deltaFull = toAngle - fromAngle;
        double deltaPart = pointForRatioAngle - fromAngle;

        return deltaPart / deltaFull;
    }

    public boolean isInBetween(XY fromPoint, XY toPoint, XY pointToTest) {
        double fromAngle = TrigUtils.getAngleRad(center,
                direction == Direction.CLOCK_WISE ? toPoint : fromPoint);
        double toAngle = TrigUtils.getAngleRad(center,
                direction == Direction.CLOCK_WISE ? fromPoint : toPoint);
        double pointToTestAngle = TrigUtils.getAngleRad(center, pointToTest);
        if (fromAngle > toAngle) {
            fromAngle -= Math.PI * 2.0;
        }
        //Log.d("SHIT", fromAngle + " < " + pointToTestAngle + " < " + toAngle);
        return fromAngle < pointToTestAngle && pointToTestAngle < toAngle;
    }

    @Override
    public String toString() {
        return "(cx=" + center.getX() + ",cy=" + center.getY() + ",r=" + radius + ",dir=" + direction + ")";
    }
}
