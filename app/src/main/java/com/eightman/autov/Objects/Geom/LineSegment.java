package com.eightman.autov.Objects.Geom;

import com.eightman.autov.Utils.MathUtils;

/**
 * Created by gilzhaiek on 2016-11-11.
 */

public class LineSegment {
    XY pointA = null;
    XY pointB = null;
    double length;

    public LineSegment() {
    }

    public LineSegment(XY pointA, XY pointB) {
        this.pointA = pointA;
        this.pointB = pointB;
        calcLength();
    }

    private void calcLength() {
        if (pointA != null && pointB != null) {
            length = MathUtils.getDistance(pointA, pointB);
        }
    }

    public synchronized XY getPointA() {
        return pointA;
    }

    public synchronized XY getPointB() {
        return pointB;
    }

    public synchronized void setPointA(XY pointA) {
        if (this.pointA != pointA) {
            this.pointA = pointA;
            calcLength();
        }
    }

    public synchronized void setPointB(XY pointB) {
        if (this.pointB != pointB) {
            this.pointB = pointB;
            calcLength();
        }
    }

    public synchronized double getSlopeX() {
        return pointB.getX() - pointA.getX();
    }

    public synchronized double getSlopeY() {
        return pointB.getY() - pointA.getY();
    }

    public synchronized XY getCenter() {
        return new XY(pointA.getX() + getSlopeX() / 2.0, pointA.getY() + getSlopeY() / 2.0);
    }

    public synchronized LineSegment addToA(double value) {
        XY newA = new XY(pointA.getX() - value * getSlopeX() / length, pointA.getY() - value * getSlopeY() / length);
        return new LineSegment(newA, pointB);
    }

    public synchronized LineSegment addToB(double value) {
        XY newB = new XY(pointB.getX() + value * getSlopeX() / length, pointB.getY() + value * getSlopeY() / length);
        return new LineSegment(pointA, newB);
    }

    public synchronized LineSegment getSwapped() {
        return new LineSegment(pointB, pointA);
    }

    public double getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "(" + pointA.toString() + "," + pointB + "," + length + ")";
    }
}
