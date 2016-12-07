package com.eightman.autov.Utils;

import android.util.Pair;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Objects.CarPosition;

import java.util.Random;

/**
 * Created by gilzhaiek on 2016-10-29.
 */

public class MathUtils {
    static Random random = new Random(0);

    public static XY getDelta(XY xyA, XY xyB) {
        return new XY(xyA.getX() - xyB.getX(), xyA.getY() - xyB.getY());
    }

    /**
     * @param position
     * @param expectedCompletionTime Millis
     * @return
     */
    public static double getSpeedToNextPosition(CarPosition position, long expectedCompletionTime) {
        // TODO: add front/back direction

        CarPosition nextPosition = position.getNext();
        if (nextPosition == null) {
            return 0;
        }

        XY from = position.getBoundaries().getCenterFront();
        XY to = nextPosition.getBoundaries().getCenterFront();

        double distance = getDistance(from, to); // Meters
        if (distance < SimConfig.MIN_MOVING_DISTANCE) {
            return 0;
        }

        // 3.0 meters in 1000 millis -> 3.0 meters per seconds
        // 3.0 meters in 500 millis -> 6.0 meters per second
        return (distance * 1000) / expectedCompletionTime;
    }

    public static int getRandomInt(int max) {
        return getRandomInt(0, max);
    }

    public static int getRandomInt(int from, int to) {
        if (from == to) {
            return from;
        }

        if (from > to) {
            int tmp = from;
            from = to;
            to = tmp;
        }

        return from + random.nextInt(to - from + 1);
    }

    public static double getRandomDouble(double from, double to) {
        if (from == to) {
            return from;
        }

        if (from > to) {
            double tmp = from;
            from = to;
            to = tmp;
        }

        return from + random.nextDouble() * (to - from);
    }

    public static double getDistance(XY xy1, XY xy2) {
        return Math.sqrt(Math.pow(xy1.getX() - xy2.getX(), 2) + Math.pow(xy1.getY() - xy2.getY(), 2));
    }

    public static Pair<Double, LineSegment> getShortestDistance(XY a1, XY a2, XY b1, XY b2) {
        LineSegment a = new LineSegment(a1, a2);
        LineSegment b = new LineSegment(b1, b2);

        return getShortestDistance(a, b);
    }

    /**
     * Finds the distance between the two line segments
     *
     * @param segmentA Line segment. Not modified.
     * @param segmentB Line segment. Not modified.
     * @return Euclidean distance of the closest point between the two line segments.
     */
    public static Pair<Double, LineSegment> getShortestDistance(LineSegment segmentA, LineSegment segmentB) {
        Pair<Double, LineSegment> closestPair = distanceSq(segmentA, segmentB);
        return new Pair(Math.sqrt(closestPair.first), closestPair.second);
    }

    /**
     * Finds the distance squared between the two line segments
     *
     * @param segmentA Line segment. Not modified.
     * @param segmentB Line segment. Not modified.
     * @return Euclidean distance squared of the closest point between the two line segments.
     */
    public static Pair<Double, LineSegment> distanceSq(LineSegment segmentA, LineSegment segmentB) {
        // intersection of the two lines relative to A
        double slopeAX = segmentA.slopeX();
        double slopeAY = segmentA.slopeY();
        double slopeBX = segmentB.slopeX();
        double slopeBY = segmentB.slopeY();

        double ta = slopeBX * (segmentA.getPointA().getY() - segmentB.getPointA().getY()) -
                slopeBY * (segmentA.getPointA().getX() - segmentB.getPointA().getX());
        double bottom = slopeBY * slopeAX - slopeAY * slopeBX;

        // see they intersect
        if (bottom != 0) {
            // see if the intersection is inside of lineA
            ta /= bottom;
            if (ta >= 0 && ta <= 1.0) {
                // see if the intersection is inside of lineB
                double tb = slopeAX * (segmentB.getPointA().getY() - segmentA.getPointA().getY()) -
                        slopeAY * (segmentB.getPointA().getX() - segmentA.getPointA().getX());
                tb /= slopeAY * slopeBX - slopeBY * slopeAX;
                if (tb >= 0 && tb <= 1.0) {
                    return new Pair(0, null);
                }
            }
        }

        Pair<Double, LineSegment> closestPair, candidatePair;
        closestPair = distanceSq(segmentA, segmentB.getPointA());
        candidatePair = distanceSq(segmentA, segmentB.getPointB());
        if (candidatePair.first < closestPair.first) {
            closestPair = candidatePair;
        }
        candidatePair = distanceSq(segmentB, segmentA.getPointA());
        if (candidatePair.first < closestPair.first) {
            closestPair = candidatePair;
        }
        candidatePair = distanceSq(segmentB, segmentA.getPointB());
        if (candidatePair.first < closestPair.first) {
            closestPair = candidatePair;
        }

        return closestPair;
    }

    /**
     * <p>
     * Returns the Euclidean distance squared of the closest point on a line segment to the specified point.
     * </p>
     *
     * @param line  A line segment. Not modified.
     * @param point The point. Not modified.
     * @return Euclidean distance squared of the closest point on a line is away from a point.
     */
    public static Pair<Double, LineSegment> distanceSq(LineSegment line, XY point) {
        double a = line.getPointB().getX() - line.getPointA().getX();
        double b = line.getPointB().getY() - line.getPointA().getY();

        double t = a * (point.getX() - line.getPointA().getX()) +
                b * (point.getY() - line.getPointA().getY());
        t /= (a * a + b * b);

        // if the point of intersection is past the end points return the distance
        // from the closest end point
        if (t < 0) {
            return distanceSq(
                    line.getPointA().getX(), line.getPointA().getY(),
                    point.getX(), point.getY());
        } else if (t > 1.0) {
            return distanceSq(
                    line.getPointB().getX(), line.getPointB().getY(),
                    point.getX(), point.getY());
        }

        // return the distance of the closest point on the line
        return distanceSq(
                line.getPointA().getX() + t * a, line.getPointA().getY() + t * b,
                point.getX(), point.getY());
    }

    public static Pair<Double, LineSegment> distanceSq(double x0, double y0, double x1, double y1) {
        double dx = x1 - x0;
        double dy = y1 - y0;

        return new Pair(dx * dx + dy * dy, new LineSegment(new XY(x0, y0), new XY(x1, y1)));
    }
}
