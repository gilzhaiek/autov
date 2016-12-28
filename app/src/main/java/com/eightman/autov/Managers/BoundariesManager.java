package com.eightman.autov.Managers;

import com.eightman.autov.Objects.Geom.Boundaries;
import com.eightman.autov.Objects.Geom.XY;
import com.eightman.autov.Utils.TrigUtils;

/**
 * Created by gilzhaiek on 2016-12-27.
 */

public class BoundariesManager {
    public static Boundaries getBoundariesRotated(final XY center, double width, double length, double theta) {
        while (theta >= 360.0) {
            theta -= 360.0;
        }
        Boundaries boundaries = getBoundariesLookingNorth(center, width, length);
        XY rightFront = TrigUtils.rotateAroundCenter(center, boundaries.getRightFront(), theta);
        XY rightBack = TrigUtils.rotateAroundCenter(center, boundaries.getRightBack(), theta);
        XY leftBack = TrigUtils.rotateAroundCenter(center, boundaries.getLeftBack(), theta);
        XY leftFront = TrigUtils.rotateAroundCenter(center, boundaries.getLeftFront(), theta);
        return new Boundaries(rightFront, rightBack, leftBack, leftFront);
    }

    public static Boundaries getBoundariesLookingNorth(final XY center, double width, double length) {
        double dx = width / 2;
        double dy = length / 2;
        return new Boundaries(
                center.getX() + dx, center.getY() + dy,
                center.getX() + dx, center.getY() - dy,
                center.getX() - dx, center.getY() - dy,
                center.getX() - dx, center.getY() + dy);
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
}
