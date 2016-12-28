package com.eightman.autov.Utils;

import android.util.Pair;

import com.eightman.autov.Objects.Geom.Boundaries;
import com.eightman.autov.Objects.Geom.LineSegment;
import com.eightman.autov.Objects.Geom.XY;

/**
 * Created by gilzhaiek on 2016-11-03.
 */

public class TrigUtils {
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
