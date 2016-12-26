package com.eightman.autov.Objects.Geom;

/**
 * Created by gilzhaiek on 2016-11-11.
 */

public class LineSegment {
    XY pointA;
    XY pointB;

    public LineSegment() {
    }

    public LineSegment(XY pointA, XY pointB) {
        this.pointA = pointA;
        this.pointB = pointB;
    }

    public XY getPointA() {
        return pointA;
    }

    public XY getPointB() {
        return pointB;
    }

    public void setPointA(XY pointA) {
        this.pointA = pointA;
    }

    public void setPointB(XY pointB) {
        this.pointB = pointB;
    }

    public double getSlopeX() {
        return pointB.getX() - pointA.getX();
    }

    public double getSlopeY() {
        return pointB.getY() - pointA.getY();
    }
}
