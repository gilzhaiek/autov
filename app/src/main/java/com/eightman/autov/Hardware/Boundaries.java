package com.eightman.autov.Hardware;

import com.eightman.autov.Utils.MathUtils;
import com.eightman.autov.Utils.XY;

/**
 * Created by gilzhaiek on 2016-10-29.
 */

public class Boundaries {
    Double width = null;
    Double length = null;
    final XY rightFront;
    final XY rightBack;
    final XY leftBack;
    final XY leftFront;
    final long timestamp;

    public Boundaries(double rFrontX, double rFrontY, double rBackX, double rBackY,
                      double lBackX, double lBackY, double lFrontX, double lFrontY,
                      long timestamp) {
        this.rightFront = new XY(rFrontX, rFrontY);
        this.rightBack = new XY(rBackX, rBackY);
        this.leftBack = new XY(lBackX, lBackY);
        this.leftFront = new XY(lFrontX, lFrontY);
        this.timestamp = timestamp;
    }

    private double getCenter(double val1, double val2) {
        return val1 + (val1 - val2) / 2.0;
    }

    public XY getCenterFront() {
        return new XY(getCenter(rightFront.getX(), leftFront.getX()),
                getCenter(rightFront.getY(), leftFront.getY()));
    }

    public XY getCenterBack() {
        return new XY(getCenter(rightBack.getX(), leftBack.getX()),
                getCenter(rightBack.getY(), leftBack.getY()));
    }

    /*
    TODO: Shortest distance between two non parallel lines
     */
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

    public long getTimestamp() {
        return timestamp;
    }

    public double getWidth() {
        if(width == null) {
            width = new Double(MathUtils.getDistance(leftFront, rightFront));
        }
        return width.doubleValue();
    }

    public double getLength() {
        if(length == null) {
            length = new Double(MathUtils.getDistance(leftFront, leftBack));
        }
        return length.doubleValue();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Boundaries)) {
            return false;
        }

        Boundaries other = (Boundaries)obj;
        return other.rightFront.equals(rightFront) &&
                other.rightBack.equals(rightBack) &&
                other.leftBack.equals(leftBack) &&
                other.leftFront.equals(leftFront) &&
                other.getTimestamp() == timestamp;
    }

    @Override
    public String toString() {
        return "rf=" + rightFront.toString() +
                ",rb=" + rightBack.toString() +
                ",lb=" + leftBack.toString() +
                ",lf=" + leftFront.toString() +
                ",ts=" + timestamp;
    }
}
