package com.eightman.autov.Managers;

import android.util.Pair;

import com.eightman.autov.Objects.Geom.Boundaries;
import com.eightman.autov.Objects.Geom.LineSegment;
import com.eightman.autov.Objects.Geom.XY;
import com.eightman.autov.Utils.MathUtils;
import com.eightman.autov.Utils.TrigUtils;

/**
 * Created by gilzhaiek on 2016-12-27.
 */

public class BoundariesManager {
    public static Boundaries rotateBoundaries(Boundaries boundaries, double theta, double newWheelsAngle) {
        while (theta >= 360.0) {
            theta -= 360.0;
        }
        XY center = boundaries.getCenter();
        XY rightFront = TrigUtils.rotateAroundCenter(center, boundaries.getRightFront(), theta);
        XY rightBack = TrigUtils.rotateAroundCenter(center, boundaries.getRightBack(), theta);
        XY leftBack = TrigUtils.rotateAroundCenter(center, boundaries.getLeftBack(), theta);
        XY leftFront = TrigUtils.rotateAroundCenter(center, boundaries.getLeftFront(), theta);
        return new Boundaries(rightFront, rightBack, leftBack, leftFront, newWheelsAngle, boundaries.getMaxWheelsAngle());
    }

    public static Boundaries getBoundariesRotated(final XY center, double width, double length,
                                                  double theta, double wheelsAngle, double maxWheelsAngle) {
        while (theta >= 360.0) {
            theta -= 360.0;
        }
        Boundaries boundaries = getBoundariesLookingNorth(center, width, length, wheelsAngle, maxWheelsAngle);
        XY rightFront = TrigUtils.rotateAroundCenter(center, boundaries.getRightFront(), theta);
        XY rightBack = TrigUtils.rotateAroundCenter(center, boundaries.getRightBack(), theta);
        XY leftBack = TrigUtils.rotateAroundCenter(center, boundaries.getLeftBack(), theta);
        XY leftFront = TrigUtils.rotateAroundCenter(center, boundaries.getLeftFront(), theta);
        return new Boundaries(rightFront, rightBack, leftBack, leftFront, wheelsAngle, maxWheelsAngle);
    }

    public static Boundaries getBoundariesLookingNorth(final XY center, double width, double length,
                                                       double wheelsAngle, double maxWheelsAngle) {
        double dx = width / 2;
        double dy = length / 2;
        return new Boundaries(
                center.getX() + dx, center.getY() + dy,
                center.getX() + dx, center.getY() - dy,
                center.getX() - dx, center.getY() - dy,
                center.getX() - dx, center.getY() + dy,
                wheelsAngle, maxWheelsAngle);
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

        return new Boundaries(rFrontX, rFrontY, rBackX, rBackY, lBackX, lBackY, lFrontX, lFrontY,
                boundaries.getWheelsAngle(), boundaries.getMaxWheelsAngle());
    }

    public static Boundaries genBoundaries(XY frontCenter, XY backCenter, double width, double length,
                                           double wheelsAngle, double maxWheelsAngle) {
        // https://goo.gl/photos/kT3nq51TM7fqiLjQ6
        double dx = frontCenter.getX() - backCenter.getX();
        double dy = frontCenter.getY() - backCenter.getY();
        double alpha = Math.atan(dy / dx);
        double beta = Math.PI - alpha;
        double adjAlpha = (width / 2) * Math.sin(alpha);
        double oppAlpha = (width / 2) * Math.cos(alpha);
        double adjBeta = (length) * Math.sin(beta);
        double oppBeta = (length) * Math.cos(beta);

        double rFrontX, rFrontY, lFrontX, lFrontY;
        double rBackX, rBackY, lBackX, lBackY;
        boolean lookingRight = (frontCenter.getX() > backCenter.getX());

        if (lookingRight) {
            rFrontX = frontCenter.getX() + adjAlpha;
            lFrontX = frontCenter.getX() - adjAlpha;
            rFrontY = frontCenter.getY() - oppAlpha;
            lFrontY = frontCenter.getY() + oppAlpha;
            rBackX = rFrontX + oppBeta;
            lBackX = lFrontX + oppBeta;
            rBackY = rFrontY - adjBeta;
            lBackY = lFrontY - adjBeta;
        } else {
            rFrontX = frontCenter.getX() - adjAlpha;
            lFrontX = frontCenter.getX() + adjAlpha;
            rFrontY = frontCenter.getY() + oppAlpha;
            lFrontY = frontCenter.getY() - oppAlpha;
            rBackX = rFrontX - oppBeta;
            lBackX = lFrontX - oppBeta;
            rBackY = rFrontY + adjBeta;
            lBackY = lFrontY + adjBeta;
        }

        return new Boundaries(rFrontX, rFrontY, rBackX, rBackY, lBackX, lBackY, lFrontX, lFrontY,
                wheelsAngle, maxWheelsAngle);
    }


    public static Boundaries getHeadingBoundaries(XY frontCenter, XY lookingAtXY, double width, double length,
                                                  double wheelsAngle, double maxWheelsAngle) {
        // https://goo.gl/photos/kT3nq51TM7fqiLjQ6
        double dx = lookingAtXY.getX() - frontCenter.getX();
        double dy = lookingAtXY.getY() - frontCenter.getY();
        double alpha = Math.atan(dy / dx);
        double beta = Math.PI - alpha;
        double adjAlpha = (width / 2) * Math.sin(alpha);
        double oppAlpha = (width / 2) * Math.cos(alpha);
        double adjBeta = (length) * Math.sin(beta);
        double oppBeta = (length) * Math.cos(beta);

        double rFrontX, rFrontY, lFrontX, lFrontY;
        double rBackX, rBackY, lBackX, lBackY;
        boolean lookingRight = (lookingAtXY.getX() > frontCenter.getX());

        if (lookingRight) {
            rFrontX = frontCenter.getX() + adjAlpha;
            lFrontX = frontCenter.getX() - adjAlpha;
            rFrontY = frontCenter.getY() - oppAlpha;
            lFrontY = frontCenter.getY() + oppAlpha;
            rBackX = rFrontX + oppBeta;
            lBackX = lFrontX + oppBeta;
            rBackY = rFrontY - adjBeta;
            lBackY = lFrontY - adjBeta;
        } else {
            rFrontX = frontCenter.getX() - adjAlpha;
            lFrontX = frontCenter.getX() + adjAlpha;
            rFrontY = frontCenter.getY() + oppAlpha;
            lFrontY = frontCenter.getY() - oppAlpha;
            rBackX = rFrontX - oppBeta;
            lBackX = lFrontX - oppBeta;
            rBackY = rFrontY + adjBeta;
            lBackY = lFrontY + adjBeta;
        }

        return new Boundaries(rFrontX, rFrontY, rBackX, rBackY, lBackX, lBackY, lFrontX, lFrontY,
                wheelsAngle, maxWheelsAngle);
    }

    public static Pair<Double, LineSegment> getShortestDistance(Boundaries boundariesA, Boundaries boundariesB) {
        Pair<Double, LineSegment> shortest = MathUtils.getShortestDistance(
                boundariesA.getFrontSegment(), boundariesB.getFrontSegment());
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = TrigUtils.min(shortest, MathUtils.getShortestDistance(
                boundariesA.getFrontSegment(), boundariesB.getRightSegment()));
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = TrigUtils.min(shortest, MathUtils.getShortestDistance(
                boundariesA.getFrontSegment(), boundariesB.getBackSegment()));
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = TrigUtils.min(shortest, MathUtils.getShortestDistance(
                boundariesA.getFrontSegment(), boundariesB.getLeftSegment()));
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = TrigUtils.min(shortest, MathUtils.getShortestDistance(
                boundariesA.getRightSegment(), boundariesB.getFrontSegment()));
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = TrigUtils.min(shortest, MathUtils.getShortestDistance(
                boundariesA.getRightSegment(), boundariesB.getRightSegment()));
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = TrigUtils.min(shortest, MathUtils.getShortestDistance(
                boundariesA.getRightSegment(), boundariesB.getBackSegment()));
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = TrigUtils.min(shortest, MathUtils.getShortestDistance(
                boundariesA.getRightSegment(), boundariesB.getLeftSegment()));
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = TrigUtils.min(shortest, MathUtils.getShortestDistance(
                boundariesA.getBackSegment(), boundariesB.getFrontSegment()));
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = TrigUtils.min(shortest, MathUtils.getShortestDistance(
                boundariesA.getBackSegment(), boundariesB.getRightSegment()));
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = TrigUtils.min(shortest, MathUtils.getShortestDistance(
                boundariesA.getBackSegment(), boundariesB.getBackSegment()));
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = TrigUtils.min(shortest, MathUtils.getShortestDistance(
                boundariesA.getBackSegment(), boundariesB.getLeftSegment()));
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = TrigUtils.min(shortest, MathUtils.getShortestDistance(
                boundariesA.getLeftSegment(), boundariesB.getFrontSegment()));
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = TrigUtils.min(shortest, MathUtils.getShortestDistance(
                boundariesA.getLeftSegment(), boundariesB.getRightSegment()));
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = TrigUtils.min(shortest, MathUtils.getShortestDistance(
                boundariesA.getLeftSegment(), boundariesB.getBackSegment()));
        if (shortest.first == 0) {
            return shortest;
        }

        shortest = TrigUtils.min(shortest, MathUtils.getShortestDistance(
                boundariesA.getLeftSegment(), boundariesB.getLeftSegment()));

        return shortest;
    }
}
