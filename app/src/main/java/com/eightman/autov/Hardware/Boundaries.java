package com.eightman.autov.Hardware;

import com.eightman.autov.Utils.Edge;
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
    XY center = null;
    XY centerFront = null;
    XY centerBack = null;
    Edge frontEdge = null;
    Edge rightEdge = null;
    Edge backEdge = null;
    Edge leftEdge = null;

    public Boundaries(double rFrontX, double rFrontY, double rBackX, double rBackY,
                      double lBackX, double lBackY, double lFrontX, double lFrontY) {
        this.rightFront = new XY(rFrontX, rFrontY);
        this.rightBack = new XY(rBackX, rBackY);
        this.leftBack = new XY(lBackX, lBackY);
        this.leftFront = new XY(lFrontX, lFrontY);
    }

    public Boundaries(XY rFront, XY rBack, XY lBack, XY lFront) {
        this.rightFront = rFront;
        this.rightBack = rBack;
        this.leftBack = lBack;
        this.leftFront = lFront;
    }

    public Boundaries addOffset(XY offset) {
        return new Boundaries(
                this.rightFront.add(offset),
                this.rightBack.add(offset),
                this.leftBack.add(offset),
                this.leftFront.add(offset));
    }

    private double getMiddle(double left, double right) {
        return left + (right - left) / 2.0;
    }

    public XY getCenterFront() {
        if (centerFront == null) {
            double centerX = getMiddle(leftFront.getX(), rightFront.getX());
            double centerY = getMiddle(leftFront.getY(), rightFront.getY());
            centerFront = new XY(centerX, centerY);
        }
        return centerFront;
    }

    public XY getCenter() {
        if (center == null) {
            double centerX = getMiddle(leftFront.getX(), rightBack.getX());
            double centerY = getMiddle(leftFront.getY(), rightBack.getY());
            center = new XY(centerX, centerY);
        }
        return center;
    }

    public XY getCenterBack() {
        if (centerBack == null) {
            centerBack = new XY(
                    getMiddle(leftBack.getX(), rightBack.getX()),
                    getMiddle(leftBack.getY(), rightBack.getY()));
        }

        return centerBack;
    }

    public Edge getFrontEdge() {
        if (frontEdge == null) {
            frontEdge = new Edge(leftFront, rightFront);
        }
        return frontEdge;
    }

    public Edge getRightEdge() {
        if (rightEdge == null) {
            rightEdge = new Edge(rightFront, rightBack);
        }
        return rightEdge;
    }

    public Edge getBackEdge() {
        if (backEdge == null) {
            backEdge = new Edge(rightBack, leftBack);
        }
        return backEdge;
    }

    public Edge getLeftEdge() {
        if (leftEdge == null) {
            leftEdge = new Edge(leftBack, leftFront);
        }
        return leftEdge;
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
