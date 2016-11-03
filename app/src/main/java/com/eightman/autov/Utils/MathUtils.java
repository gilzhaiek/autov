package com.eightman.autov.Utils;

import com.eightman.autov.Hardware.Boundaries;

/**
 * Created by gilzhaiek on 2016-10-29.
 */

public class MathUtils {
    public static double getSpeed(XY xy1, long timeStamp1, XY xy2, long timeStamp2) {
        // TODO: finish
        return 0;
    }

    public static double getDistance(XY xy1, XY xy2) {
        return Math.sqrt(Math.pow(xy1.getX() - xy2.getX(), 2) + Math.pow(xy1.getY() - xy2.getY(), 2));
    }

    public static Boundaries getBoundariesLookingNorth(XY centerXY, double width, double length, long timeStamp) {
        double x = centerXY.getX()+width/2;
        double y = centerXY.getY()+length/2;

        return new Boundaries(centerXY.getX()+x, centerXY.getY()+y,
                centerXY.getX()+x, centerXY.getY()-y,
                centerXY.getX()-x, centerXY.getY()-y,
                centerXY.getX()-x, centerXY.getY()+y, timeStamp);
    }

    public static Boundaries getBoundaries(XY yourXY, XY lookingAtXY, double width, double length, long timeStamp) {
        // https://goo.gl/photos/kT3nq51TM7fqiLjQ6
        double dx = lookingAtXY.getX()-yourXY.getX();
        double dy = lookingAtXY.getY()-yourXY.getY();
        double alpha = Math.atan(dx/dy);
        double beta = Math.PI-alpha;
        double adjAlpha = (width/2)*Math.sin(alpha);
        double oppAlpha = (width/2)*Math.cos(alpha);
        double adjBeta = (length)*Math.sin(beta);
        double oppBeta = (length)*Math.cos(beta);

        double rFrontX, rFrontY, lFrontX, lFrontY;
        double rBackX, rBackY, lBackX, lBackY;
        boolean lookingUp = (lookingAtXY.getY() > yourXY.getY());
        boolean lookingRight = (lookingAtXY.getX() > yourXY.getX());

        if(lookingRight) {
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


        return new Boundaries(rFrontX, rFrontY, rBackX, rBackY, lBackX, lBackY, lFrontX, lFrontY, timeStamp);
    }
}
