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
    public static double getCircumference(double radius) {
        return 2 * Math.PI * radius;
    }

    public static double getAnglesInRadians(XY center, XY point) {
        return Math.atan2(point.getY() - center.getY(), point.getX() - center.getX());
    }

    public static double getAnglesInRadians(double radius, double arcLength) {
        return arcLength / getCircumference(radius);
    }

    public static double getAnglesInRadians(double angleInDegrees) {
        return angleInDegrees * Math.PI / 180.0;
    }

    public static XY pointOnCircle(double radius, double angleInDegrees, XY center) {
        // Convert from degrees to radians via multiplication by PI/180
        double anglesInRadians = getAnglesInRadians(angleInDegrees);
        double x = radius * Math.cos(anglesInRadians) + center.getX();
        double y = radius * Math.sin(anglesInRadians) + center.getY();

        return new XY(x, y);
    }

    // a/sin(A) = b/sin(B) = c/sin(C)
    // For a triangle, B = wheelsAngle, A=C=(180-wheelsAngle)/2
    // b is the wheelsLengthBase and a=c=equals
    public static double getRadius(double wheelsAngle, double wheelsLengthBase) {
        double A = (180 - Math.abs(wheelsAngle)) / 2.0;
        return Math.abs(wheelsLengthBase * Math.sin(A) / Math.sin(Math.abs(wheelsAngle)));
    }

    public static XY getPoint(XY from, XY pointOutsideLine, double addition) {
        double distance = MathUtils.getDistance(from, pointOutsideLine);
        double addX = from.getX() + addition * (from.getX() - pointOutsideLine.getX()) / distance;
        double addY = from.getY() + addition * (from.getY() - pointOutsideLine.getY()) / distance;
        return new XY(addX, addY);
    }

    public static XY rotateAroundCenter(final XY center, XY xy, double theta) {
        // cx, cy - center of square coordinates
        // x, y - coordinates of a corner point of the square
        // theta is the angle of rotation

        // translate point to origin
        double tempX = xy.getX() - center.getX();
        double tempY = xy.getY() - center.getY();

        double thetaInRadians = getAnglesInRadians(theta);
        // now apply rotation
        double rotatedX = tempX * Math.cos(thetaInRadians) - tempY * Math.sin(thetaInRadians);
        double rotatedY = tempX * Math.sin(thetaInRadians) + tempY * Math.cos(thetaInRadians);

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
        circles[0] = Circle.getCircle(boundaries.getCenterFront(), boundaries.getRightFront(), radius, Circle.Direction.COUNTER_CLOCK_WISE);
        circles[1] = Circle.getCircle(boundaries.getCenterFront(), boundaries.getLeftFront(), radius, Circle.Direction.CLOCK_WISE);
        return circles;
    }
}
