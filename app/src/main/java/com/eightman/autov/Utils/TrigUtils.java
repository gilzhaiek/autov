package com.eightman.autov.Utils;

import android.util.Pair;

import com.eightman.autov.Objects.Geom.Boundaries;
import com.eightman.autov.Objects.Geom.Circle;
import com.eightman.autov.Objects.Geom.LineSegment;
import com.eightman.autov.Objects.Geom.XY;
import com.eightman.autov.Objects.Physical.Wheels;

/**
 * Created by gilzhaiek on 2016-11-03.
 */

public class TrigUtils {
    // a/sin(A) = b/sin(B) = c/sin(C)
    // For a triangle, B = wheelsAngle, A=C=(180-wheelsAngle)/2
    // b is the wheelsLengthBase and a=c=equals
    public static double getRadius(double wheelsAngle, double wheelsLengthBase) {
        double A = (180 - Math.abs(wheelsAngle)) / 2.0;
        return wheelsLengthBase * Math.sin(A) / Math.sin(Math.abs(wheelsAngle));
    }

    public static XY getPoint(XY from, XY pointOutsideLine, double addition) {
        double distance = MathUtils.getDistance(from, pointOutsideLine);
        double addX = from.getX() - addition * pointOutsideLine.getX() - from.getX() / distance;
        double addY = from.getY() - addition * pointOutsideLine.getY() - from.getY() / distance;
        return new XY(addX, addY);
    }

    public static XY rotateAroundCenter(final XY center, XY xy, double theta) {
        // cx, cy - center of square coordinates
        // x, y - coordinates of a corner point of the square
        // theta is the angle of rotation

        // translate point to origin
        double tempX = xy.getX() - center.getX();
        double tempY = xy.getY() - center.getY();

        // now apply rotation
        double rotatedX = tempX * Math.cos(theta) - tempY * Math.sin(theta);
        double rotatedY = tempX * Math.sin(theta) + tempY * Math.cos(theta);

        // translate back
        return new XY(rotatedX + center.getX(), rotatedY + center.getY());
    }

    public static double determinant(XY xyA, XY xyB) {
        return xyA.getX() * xyB.getY() - xyA.getY() * xyB.getX();
    }

    public static XY edgeIntersection(LineSegment edgeA, LineSegment edgeB) {
        double det = determinant(
                MathUtils.getDelta(edgeA.getPointB(), edgeA.getPointA()),
                MathUtils.getDelta(edgeB.getPointA(), edgeB.getPointB()));
        double t = determinant(
                MathUtils.getDelta(edgeB.getPointA(), edgeA.getPointA()),
                MathUtils.getDelta(edgeB.getPointA(), edgeB.getPointB())) / det;
        double u = determinant(
                MathUtils.getDelta(edgeA.getPointB(), edgeA.getPointA()),
                MathUtils.getDelta(edgeB.getPointA(), edgeA.getPointA())) / det;

        if ((t < 0) || (u < 0) || (t > 1) || (u > 1)) {
            return null;
        } else {
            return new XY(
                    edgeA.getPointA().getX() * (1 - t) + t * edgeA.getPointB().getX(),
                    edgeA.getPointA().getY() * (1 - t) + t * edgeA.getPointB().getY());
        }
    }

    public static Pair<Double, LineSegment> min(Pair<Double, LineSegment> pair1, Pair<Double, LineSegment> pair2) {
        if (pair1.first < pair2.first) {
            return pair1;
        }
        return pair2;
    }

    public static Circle[] getMaxTurningCircles(Boundaries boundaries, Wheels wheels) {
        double radius = TrigUtils.getRadius(wheels.getMaxWheelsAngle(), boundaries.getLeftSegment().Length());
        Circle circles[] = new Circle[2];
        circles[0] = Circle.getCircle(boundaries.getLeftSegment(), boundaries.getRightSegment(),
                radius, Circle.Direction.COUNTER_CLOCK_WISE);
        circles[1] = Circle.getCircle(boundaries.getRightSegment(), boundaries.getLeftSegment(),
                radius, Circle.Direction.CLOCK_WISE);
        return circles;
    }

    public static Pair<Double, LineSegment> getShortestDistance(Boundaries boundariesA, Boundaries boundariesB) {
        Pair<Double, LineSegment> shortest = MathUtils.getShortestDistance(
                boundariesA.getFrontSegment(), boundariesB.getFrontSegment());
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = min(shortest, MathUtils.getShortestDistance(
                boundariesA.getFrontSegment(), boundariesB.getRightSegment()));
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = min(shortest, MathUtils.getShortestDistance(
                boundariesA.getFrontSegment(), boundariesB.getBackSegment()));
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = min(shortest, MathUtils.getShortestDistance(
                boundariesA.getFrontSegment(), boundariesB.getLeftSegment()));
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = min(shortest, MathUtils.getShortestDistance(
                boundariesA.getRightSegment(), boundariesB.getFrontSegment()));
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = min(shortest, MathUtils.getShortestDistance(
                boundariesA.getRightSegment(), boundariesB.getRightSegment()));
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = min(shortest, MathUtils.getShortestDistance(
                boundariesA.getRightSegment(), boundariesB.getBackSegment()));
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = min(shortest, MathUtils.getShortestDistance(
                boundariesA.getRightSegment(), boundariesB.getLeftSegment()));
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = min(shortest, MathUtils.getShortestDistance(
                boundariesA.getBackSegment(), boundariesB.getFrontSegment()));
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = min(shortest, MathUtils.getShortestDistance(
                boundariesA.getBackSegment(), boundariesB.getRightSegment()));
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = min(shortest, MathUtils.getShortestDistance(
                boundariesA.getBackSegment(), boundariesB.getBackSegment()));
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = min(shortest, MathUtils.getShortestDistance(
                boundariesA.getBackSegment(), boundariesB.getLeftSegment()));
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = min(shortest, MathUtils.getShortestDistance(
                boundariesA.getLeftSegment(), boundariesB.getFrontSegment()));
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = min(shortest, MathUtils.getShortestDistance(
                boundariesA.getLeftSegment(), boundariesB.getRightSegment()));
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = min(shortest, MathUtils.getShortestDistance(
                boundariesA.getLeftSegment(), boundariesB.getBackSegment()));
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = min(shortest, MathUtils.getShortestDistance(
                boundariesA.getLeftSegment(), boundariesB.getLeftSegment()));

        return shortest;
    }
}
