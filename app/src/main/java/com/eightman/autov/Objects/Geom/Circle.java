package com.eightman.autov.Objects.Geom;

import com.eightman.autov.Utils.TrigUtils;

/**
 * Created by gilzhaiek on 2016-12-27.
 */

public class Circle {
    XY center;
    double radius;

    public Circle(XY center, double radius) {
        center = new XY(center);
        radius = radius;
    }

    public Circle(double centerX, double centerY, double radius) {
        center = new XY(centerX, centerY);
        radius = radius;
    }

    public static Circle[] getCircles(LineSegment leftSegment, LineSegment rightSegment, double radius) {
        XY centerLeft = leftSegment.getCenter();
        XY centerRight = rightSegment.getCenter();
        Circle[] retCircles = new Circle[2];

        XY center = TrigUtils.getPoint(centerLeft, centerRight, radius);
        retCircles[0] = new Circle(center, radius);

        center = TrigUtils.getPoint(centerRight, centerLeft, radius);
        retCircles[1] = new Circle(center, radius);

        return retCircles;
    }

    public XY getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }
}
