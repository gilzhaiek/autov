package com.eightman.autov.Utils;

import android.util.Log;
import android.util.Pair;

import com.eightman.autov.Objects.Geom.Circle;
import com.eightman.autov.Objects.Geom.LineSegment;
import com.eightman.autov.Objects.Geom.XY;

/**
 * Created by gilzhaiek on 2016-11-03.
 */

public class TrigUtils {
    public static XY findTangent(Circle circle, double alphaRadians) {
        double r = circle.getRadius();
        double cx = circle.getCenter().getX();
        double cy = circle.getCenter().getY();
        double x = cx + r * Math.cos(Math.PI / 2.0 - alphaRadians);
        double y = cy + r * Math.sin(Math.PI / 2.0 - alphaRadians);
        return new XY(x, y);
    }

    // http://www.ambrsoft.com/TrigoCalc/Circles2/Circles2Tangent_.htm
    private static LineSegment _findOuterTangents(Circle bigCircle,
                                                  Circle smallCircle,
                                                  boolean oppositeSegment) {
        double a = bigCircle.getCenter().getX();
        double b = bigCircle.getCenter().getY();
        double c = smallCircle.getCenter().getX();
        double d = smallCircle.getCenter().getY();
        double r0 = bigCircle.getRadius();
        double r1 = smallCircle.getRadius();

        double xp = (c * r0 - a * r1) / (r0 - r1);
        double yp = (d * r0 - b * r1) / (r0 - r1);

        XY xy1 = null, xy2 = null;
        double rSqr = r0 * r0;
        double denominator = Math.pow(xp - a, 2) + Math.pow(yp - b, 2);
        double part = Math.sqrt(Math.pow(xp - a, 2) + Math.pow(yp - b, 2) - rSqr);
        if ((bigCircle.getDirection() == Circle.Direction.COUNTER_CLOCK_WISE) == !oppositeSegment) {
            double xt1 = ((rSqr * (xp - a) + r0 * (yp - b) * part) / (denominator)) + a;
            double yt1 = ((rSqr * (yp - b) - r0 * (xp - a) * part) / (denominator)) + b;
            xy1 = new XY(xt1, yt1);
        } else {
            double xt2 = ((rSqr * (xp - a) - r0 * (yp - b) * part) / (denominator)) + a;
            double yt2 = ((rSqr * (yp - b) + r0 * (xp - a) * part) / (denominator)) + b;
            xy2 = new XY(xt2, yt2);
        }

        rSqr = r1 * r1;
        denominator = Math.pow(xp - c, 2) + Math.pow(yp - d, 2);
        part = Math.sqrt(Math.pow(xp - c, 2) + Math.pow(yp - d, 2) - rSqr);
        if (xy1 != null) {
            double xt3 = ((rSqr * (xp - c) + r1 * (yp - d) * part) / (denominator)) + c;
            double yt3 = ((rSqr * (yp - d) - r1 * (xp - c) * part) / (denominator)) + d;
            return new LineSegment(xy1, new XY(xt3, yt3));
        } else {
            double xt4 = ((rSqr * (xp - c) - r1 * (yp - d) * part) / (denominator)) + c;
            double yt4 = ((rSqr * (yp - d) + r1 * (xp - c) * part) / (denominator)) + d;
            return new LineSegment(xy2, new XY(xt4, yt4));
        }
    }

    public static double isBetween(XY edgeA, XY edgeB, XY point) {
        Log.d("SHIT", "isBetween " + MathUtils.getDistance(edgeA, point) +
                " + " + MathUtils.getDistance(point, edgeB) +
                " = " + (MathUtils.getDistance(edgeA, point) + MathUtils.getDistance(point, edgeB)) +
                " == " + MathUtils.getDistance(edgeA, edgeB));
        return Math.abs(MathUtils.getDistance(edgeA, point) + MathUtils.getDistance(point, edgeB) -
                MathUtils.getDistance(edgeA, edgeB));
    }

    // http://www.ambrsoft.com/TrigoCalc/Circles2/Circles2Tangent_.htm
    public static LineSegment findOuterTangents(Circle fromCircle, Circle toCircle) {
        if (fromCircle == null || toCircle == null) {
            return null;
        }

        // Outer tangents only make sense for same direction
        if (fromCircle.getDirection() != toCircle.getDirection()) {
            return null;
        }

        double deltaRadius = Math.abs(fromCircle.getRadius() - toCircle.getRadius());
        if (fromCircle.getRadius() > toCircle.getRadius() && deltaRadius > 0.01) {
            Log.d("SHIT4", "From " + fromCircle.getRadius() + " > To " + toCircle.getRadius());
            return _findOuterTangents(fromCircle, toCircle, false);
        } else if (fromCircle.getRadius() < toCircle.getRadius() && deltaRadius > 0.01) {
            Log.d("SHIT4", "From " + fromCircle.getRadius() + " < To " + toCircle.getRadius());
            return _findOuterTangents(toCircle, fromCircle, true).getSwapped();
        } else {
            Log.d("SHIT4", "To == From = " + fromCircle.getRadius());
            double halfToRadius = toCircle.getRadius() / 2.0;
            Circle smallerToCircle = new Circle(toCircle.getCenter(), halfToRadius, toCircle.getDirection());
            LineSegment toSmallerSegment = _findOuterTangents(fromCircle, smallerToCircle, false);
            LineSegment centerToSmaller = new LineSegment(smallerToCircle.getCenter(), toSmallerSegment.getPointB());
            return new LineSegment(toSmallerSegment.getPointA(), centerToSmaller.addToB(halfToRadius).getPointB());
        }
    }

    // https://en.wikipedia.org/wiki/Tangent_lines_to_circles
    /*public static LineSegment findOuterTangents(Circle circle1, Circle circle2) {
        if (circle1 == null || circle2 == null) {
            return null;
        }

        double lambda = Math.atan(
                (circle1.getCenter().getY() - circle2.getCenter().getY()) /
                        (circle2.getCenter().getX() - circle1.getCenter().getX()));
        double beta = Math.asin((circle2.getRadius() - circle1.getRadius()) /
                (Math.sqrt(Math.pow(circle2.getCenter().getX() - circle1.getCenter().getX(), 2) +
                        Math.pow(circle2.getCenter().getY() - circle1.getCenter().getY(), 2))));
        double alpha = lambda - beta;
        return new LineSegment(findTangent(circle1, alpha), findTangent(circle2, alpha));
    }*/

    public static double getCircumference(double radius) {
        return 2 * Math.PI * radius;
    }

    public static double getAngleRad(XY center, XY point) {
        return Math.atan2(point.getY() - center.getY(), point.getX() - center.getX());
    }

    public static double getAngleRad(double radius, double arcLength) {
        return Math.toRadians(360 * arcLength / getCircumference(radius));
    }

    // a/sin(A) = b/sin(B) = c/sin(C)
    // For a triangle, B = wheelsAngle, A=C=(180-wheelsAngle)/2
    // b is the wheelsLengthBase and a=c=equals
    public static double getRadius(double wheelsAngle, double wheelsLengthBase) {
        if (wheelsAngle == 0) {
            throw new IllegalArgumentException("wheelsAngle can't be 0");
        }
        if (wheelsAngle >= 90.0) {
            throw new IllegalArgumentException("wheelsAngle can't be greater or equal to 90");
        }
        wheelsAngle = Math.abs(wheelsAngle);
        double A = (180 - wheelsAngle * 2.0);
        return Math.abs(wheelsLengthBase *
                Math.sin(Math.toRadians(A)) / Math.sin(Math.toRadians(wheelsAngle)));
    }

    public static XY getPoint(XY from, XY pointOutsideLine, double addition) {
        double distance = MathUtils.getDistance(from, pointOutsideLine);
        double addX = from.getX() + addition * (from.getX() - pointOutsideLine.getX()) / distance;
        double addY = from.getY() + addition * (from.getY() - pointOutsideLine.getY()) / distance;
        return new XY(addX, addY);
    }

    // http://math.stackexchange.com/questions/814950/how-can-i-rotate-a-coordinate-around-a-circle
    public static XY rotateAroundCenterRadians(final XY center, XY xy, double thetaInRadians) {
        // cx, cy - center of square coordinates
        // x, y - coordinates of a corner point of the square
        // theta is the angle of rotation

        // translate point to origin
        double tempX = xy.getX() - center.getX();
        double tempY = xy.getY() - center.getY();

        // now apply rotation
        double rotatedX = tempX * Math.cos(-thetaInRadians) - tempY * Math.sin(-thetaInRadians);
        double rotatedY = tempX * Math.sin(-thetaInRadians) + tempY * Math.cos(-thetaInRadians);

        // translate back
        return new XY(rotatedX + center.getX(), rotatedY + center.getY());
    }


    public static XY rotateAroundCenter(final XY center, final XY xy, double angDeg) {
        return rotateAroundCenterRadians(center, xy, Math.toRadians(angDeg));
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
}
