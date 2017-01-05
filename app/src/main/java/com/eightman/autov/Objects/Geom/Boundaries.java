package com.eightman.autov.Objects.Geom;

import android.util.Log;

import com.eightman.autov.Utils.MathUtils;
import com.eightman.autov.Utils.TrigUtils;

/**
 * Created by gilzhaiek on 2016-10-29.
 */

public class Boundaries {
    final XY rightFront;
    final XY rightBack;
    final XY leftBack;
    final XY leftFront;
    final double maxWheelsAngle;
    double wheelsAngle;
    Double width = null;
    Double length = null;
    XY center = null;
    XY centerFront = null;
    XY centerBack = null;
    XY centerLeft = null;
    XY centerRight = null;
    LineSegment frontSegment = null;
    LineSegment rightSegment = null;
    LineSegment backSegment = null;
    LineSegment leftSegment = null;
    Circle turningCircle;
    Circle[] maxTurningCircles;

    public Boundaries(double rFrontX, double rFrontY, double rBackX, double rBackY,
                      double lBackX, double lBackY, double lFrontX, double lFrontY,
                      double wheelsAngle, double maxWheelsAngle) {
        this.rightFront = new XY(rFrontX, rFrontY);
        this.rightBack = new XY(rBackX, rBackY);
        this.leftBack = new XY(lBackX, lBackY);
        this.leftFront = new XY(lFrontX, lFrontY);
        this.wheelsAngle = wheelsAngle;
        this.maxWheelsAngle = maxWheelsAngle;
    }

    public Boundaries(XY rFront, XY rBack, XY lBack, XY lFront, double wheelsAngle, double maxWheelsAngle) {
        this.rightFront = rFront;
        this.rightBack = rBack;
        this.leftBack = lBack;
        this.leftFront = lFront;
        this.wheelsAngle = wheelsAngle;
        this.maxWheelsAngle = maxWheelsAngle;
    }

    public Boundaries moveForward(double addition) {
        double length = getLength();
        double addX = addition * (rightFront.getX() - rightBack.getX()) / length;
        double addY = addition * (rightFront.getY() - rightBack.getY()) / length;
        return addOffset(new XY(addX, addY));
    }

    public Boundaries addOffset(XY offset) {
        return new Boundaries(
                this.rightFront.add(offset),
                this.rightBack.add(offset),
                this.leftBack.add(offset),
                this.leftFront.add(offset),
                this.wheelsAngle,
                this.maxWheelsAngle);
    }

    private double getMiddle(double left, double right) {
        return left + (right - left) / 2.0;
    }

    public XY getCenter() {
        if (center == null) {
            double centerX = getMiddle(leftFront.getX(), rightBack.getX());
            double centerY = getMiddle(leftFront.getY(), rightBack.getY());
            center = new XY(centerX, centerY);
        }
        return center;
    }

    public XY getCenterFront() {
        if (centerFront == null) {
            double centerX = getMiddle(leftFront.getX(), rightFront.getX());
            double centerY = getMiddle(leftFront.getY(), rightFront.getY());
            centerFront = new XY(centerX, centerY);
        }
        return centerFront;
    }

    public XY getCenterBack() {
        if (centerBack == null) {
            centerBack = new XY(
                    getMiddle(leftBack.getX(), rightBack.getX()),
                    getMiddle(leftBack.getY(), rightBack.getY()));
        }

        return centerBack;
    }

    public XY getCenterLeft() {
        if (centerLeft == null) {
            double centerX = getMiddle(leftFront.getX(), leftBack.getX());
            double centerY = getMiddle(leftFront.getY(), leftBack.getY());
            centerLeft = new XY(centerX, centerY);
        }
        return centerLeft;
    }

    public XY getCenterRight() {
        if (centerRight == null) {
            double centerX = getMiddle(rightFront.getX(), rightBack.getX());
            double centerY = getMiddle(rightFront.getY(), rightBack.getY());
            centerRight = new XY(centerX, centerY);
        }
        return centerRight;
    }

    public LineSegment getFrontSegment() {
        if (frontSegment == null) {
            frontSegment = new LineSegment(leftFront, rightFront);
        }
        return frontSegment;
    }

    public LineSegment getRightSegment() {
        if (rightSegment == null) {
            rightSegment = new LineSegment(rightFront, rightBack);
        }
        return rightSegment;
    }

    public LineSegment getBackSegment() {
        if (backSegment == null) {
            backSegment = new LineSegment(rightBack, leftBack);
        }
        return backSegment;
    }

    public LineSegment getLeftSegment() {
        if (leftSegment == null) {
            leftSegment = new LineSegment(leftBack, leftFront);
        }
        return leftSegment;
    }

    public synchronized void setWheelsAngle(double wheelsAngle) {
        if (this.wheelsAngle != wheelsAngle) {
            this.wheelsAngle = wheelsAngle;
            if (turningCircle != null) {
                getTurningCircle();
            }
        }
    }

    public synchronized double getWheelsAngle() {
        return wheelsAngle;
    }

    public double getMaxWheelsAngleAbs() {
        return maxWheelsAngle;
    }

    public double getMaxWheelsAngle(Circle.Direction direction) {
        if (direction == Circle.Direction.CLOCK_WISE) {
            return maxWheelsAngle;
        } else {
            return -maxWheelsAngle;
        }
    }

    public synchronized Circle getTurningCircle() {
        if (turningCircle == null) {
            if (wheelsAngle > 0) {
                turningCircle = Circle.getCircle(
                        getCenterFront(),
                        getLeftFront(),
                        TrigUtils.getRadius(wheelsAngle, getLength()),
                        Circle.Direction.CLOCK_WISE);
            } else if (wheelsAngle < 0) {
                turningCircle = Circle.getCircle(
                        getCenterFront(),
                        getRightFront(),
                        TrigUtils.getRadius(wheelsAngle, getLength()),
                        Circle.Direction.COUNTER_CLOCK_WISE);
            } // else null
        }
        return turningCircle;
    }

    public Circle[] getMaxTurningCircles() {
        if (maxTurningCircles == null) {
            maxTurningCircles = new Circle[2];

            double radius = TrigUtils.getRadius(maxWheelsAngle, getLength());
            Log.d("SHIT2", "maxWheels=" + maxWheelsAngle + " R=" + radius + " L=" + getLength());


            maxTurningCircles[0] = Circle.getCircle(getCenterFront(), getLeftFront(), radius, Circle.Direction.CLOCK_WISE);
            maxTurningCircles[1] = Circle.getCircle(getCenterFront(), getRightFront(), radius, Circle.Direction.COUNTER_CLOCK_WISE);

        }
        return maxTurningCircles;
    }

    // TODO: Shortest distance between two non parallel lines
    public double getDistance(Boundaries otherBoundaries) {
        double closestDistance = MathUtils.getDistance(rightFront, otherBoundaries.getRightFront());

        double potentialClosest = MathUtils.getDistance(rightBack, otherBoundaries.getRightBack());
        if (potentialClosest < closestDistance) {
            closestDistance = potentialClosest;
        }

        potentialClosest = MathUtils.getDistance(leftBack, otherBoundaries.getLeftBack());
        if (potentialClosest < closestDistance) {
            closestDistance = potentialClosest;
        }

        potentialClosest = MathUtils.getDistance(leftFront, otherBoundaries.getLeftFront());
        if (potentialClosest < closestDistance) {
            closestDistance = potentialClosest;
        }

        return closestDistance;
    }

    public XY getRightFront() {
        return rightFront;
    }

    public XY getRightBack() {
        return rightBack;
    }

    public XY getLeftBack() {
        return leftBack;
    }

    public XY getLeftFront() {
        return leftFront;
    }

    public double getWidth() {
        if (width == null) {
            width = new Double(MathUtils.getDistance(leftFront, rightFront));
        }
        return width.doubleValue();
    }

    public double getLength() {
        if (length == null) {
            length = new Double(MathUtils.getDistance(leftFront, leftBack));
        }
        return length.doubleValue();
    }

    public double getLargestSide() {
        return (getLength() >= getWidth()) ? getLength() : getWidth();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Boundaries)) {
            return false;
        }

        Boundaries other = (Boundaries) obj;
        return other.rightFront.equals(rightFront) &&
                other.rightBack.equals(rightBack) &&
                other.leftBack.equals(leftBack) &&
                other.leftFront.equals(leftFront);
    }

    @Override
    public String toString() {
        return "{rf=" + rightFront.toString() + "," +
                "rb=" + rightBack.toString() + "," +
                "lb=" + leftBack.toString() + "," +
                "lf=" + leftFront.toString() + "}";
    }
}
