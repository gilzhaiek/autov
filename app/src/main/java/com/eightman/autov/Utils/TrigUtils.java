package com.eightman.autov.Utils;

import android.util.Pair;

import com.eightman.autov.Objects.Geom.Boundaries;
import com.eightman.autov.Objects.Geom.LineSegment;
import com.eightman.autov.Objects.Geom.XY;

/**
 * Created by gilzhaiek on 2016-11-03.
 */

public class TrigUtils {
    // get circle
    // see if a line intersects with a circle

    public static Boundaries getBoundariesLookingNorth(double x, double y, double width, double length) {
        double dx = width / 2;
        double dy = length / 2;
        return new Boundaries(x + dx, y + dy, x + dx, y - dy, x - dx, y - dy, x - dx, y + dy);
    }

    public static Boundaries boundariesAddition(
            Boundaries boundaries, double margin) {
        return boundariesAddition(boundaries, margin, margin, margin);
    }

    public static Boundaries boundariesAddition(
            Boundaries boundaries, double frontLength, double backLength, double width) {
        double dxw = boundaries.getRightFront().getX() - boundaries.getLeftFront().getX();
        double dyw = boundaries.getRightFront().getY() - boundaries.getLeftFront().getY();

        double dxl = boundaries.getCenterFront().getX() - boundaries.getCenterBack().getX();
        double dyl = boundaries.getCenterFront().getY() - boundaries.getCenterBack().getY();

        double xw = (dxw / boundaries.getWidth()) * width;
        double yw = (dyw / boundaries.getWidth()) * width;
        double fyl = (dyl / boundaries.getLength()) * frontLength;
        double byl = (dyl / boundaries.getLength()) * backLength;
        double fxl = (dxl / boundaries.getLength()) * frontLength;
        double bxl = (dxl / boundaries.getLength()) * backLength;

        boolean lookingUp = boundaries.getCenterFront().getY() > boundaries.getCenterBack().getY();
        boolean lookingRight = boundaries.getCenterFront().getX() > boundaries.getCenterBack().getX();

        double rFrontX, rFrontY, lFrontX, lFrontY;
        double rBackX, rBackY, lBackX, lBackY;

        rFrontX = boundaries.getRightFront().getX() + fxl + xw;
        rFrontY = boundaries.getRightFront().getY() + fyl + yw;
        rBackX = boundaries.getRightBack().getX() - bxl + xw;
        rBackY = boundaries.getRightBack().getY() - byl + yw;
        lBackX = boundaries.getLeftBack().getX() - bxl - xw;
        lBackY = boundaries.getLeftBack().getY() - byl - yw;
        lFrontX = boundaries.getLeftFront().getX() + fxl - xw;
        lFrontY = boundaries.getLeftFront().getY() + fyl - yw;

        return new Boundaries(rFrontX, rFrontY, rBackX, rBackY, lBackX, lBackY, lFrontX, lFrontY);

    }

    public static Boundaries getHeadingBoundaries(XY yourXY, XY lookingAtXY, double width, double length) {
        // https://goo.gl/photos/kT3nq51TM7fqiLjQ6
        double dx = lookingAtXY.getX() - yourXY.getX();
        double dy = lookingAtXY.getY() - yourXY.getY();
        double alpha = Math.atan(dy / dx);
        double beta = Math.PI - alpha;
        double adjAlpha = (width / 2) * Math.sin(alpha);
        double oppAlpha = (width / 2) * Math.cos(alpha);
        double adjBeta = (length) * Math.sin(beta);
        double oppBeta = (length) * Math.cos(beta);

        double rFrontX, rFrontY, lFrontX, lFrontY;
        double rBackX, rBackY, lBackX, lBackY;
        boolean lookingUp = (lookingAtXY.getY() > yourXY.getY());
        boolean lookingRight = (lookingAtXY.getX() > yourXY.getX());

        if (lookingRight) {
            rFrontX = yourXY.getX() + adjAlpha;
            lFrontX = yourXY.getX() - adjAlpha;
            rFrontY = yourXY.getY() - oppAlpha;
            lFrontY = yourXY.getY() + oppAlpha;
            rBackX = rFrontX + oppBeta;
            lBackX = lFrontX + oppBeta;
            rBackY = rFrontY - adjBeta;
            lBackY = lFrontY - adjBeta;
        } else {
            rFrontX = yourXY.getX() - adjAlpha;
            lFrontX = yourXY.getX() + adjAlpha;
            rFrontY = yourXY.getY() + oppAlpha;
            lFrontY = yourXY.getY() - oppAlpha;
            rBackX = rFrontX - oppBeta;
            lBackX = lFrontX - oppBeta;
            rBackY = rFrontY + adjBeta;
            lBackY = lFrontY + adjBeta;
        }

        return new Boundaries(rFrontX, rFrontY, rBackX, rBackY, lBackX, lBackY, lFrontX, lFrontY);
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

    private static Pair<Double, LineSegment> min(Pair<Double, LineSegment> pair1, Pair<Double, LineSegment> pair2) {
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
