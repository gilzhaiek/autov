package com.eightman.autov.Utils;

/**
 * Created by gilzhaiek on 2016-11-11.
 */

public class LineSegment {
    XY pointA;
    XY pointB;

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


    public double slopeX() {
        return pointB.getX() - pointA.getX();
    }

    public double slopeY() {
        return pointB.getY() - pointA.getY();
    }

}
