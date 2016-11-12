package com.eightman.autov.Utils;

/**
 * Created by gilzhaiek on 2016-11-11.
 */

public class Edge {
    XY pointA;
    XY pointB;

    public Edge(XY pointA, XY pointB) {
        this.pointA = pointA;
        this.pointB = pointB;
    }

    public XY getPointA() {
        return pointA;
    }

    public XY getPointB() {
        return pointB;
    }
}
