package com.eightman.autov.Utils;

import com.eightman.autov.Hardware.Boundaries;

/**
 * Created by gilzhaiek on 2016-11-03.
 */

public class TrigUtils {
    public static Boundaries getBoundariesLookingNorth(double x, double y, double width, double length, long timeStamp) {
        double dx = x+width/2;
        double dy = y+length/2;
        return new Boundaries(x+dx, y+dy, x+dx, y-dy, x-dx, y-dy, x-dx, y+dy, timeStamp);
    }

    public static Boundaries getBoundaries(XY yourXY, XY lookingAtXY, double width, double length, long timeStamp) {
        // https://goo.gl/photos/kT3nq51TM7fqiLjQ6
        double dx = lookingAtXY.getX()-yourXY.getX();
        double dy = lookingAtXY.getY()-yourXY.getY();
        double alpha = Math.atan(dy/dx);
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
