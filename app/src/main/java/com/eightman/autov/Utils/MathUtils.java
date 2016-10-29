package com.eightman.autov.Utils;

import com.eightman.autov.Configurations.SimConfig;
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
        double x = centerXY.getX()+length/2;
        double y = centerXY.getY()+width/2;

        return new Boundaries(centerXY.getX()+x, centerXY.getY()+y,
                centerXY.getX()+x, centerXY.getY()-y,
                centerXY.getX()-x, centerXY.getY()-y,
                centerXY.getX()-x, centerXY.getY()+y, timeStamp);
    }

    public static Boundaries getBoundaries(XY yourXY, XY lookingAtXY, double width, double length, long timeStamp) {
        double dx = yourXY.getX()-lookingAtXY.getX();
        double dy = yourXY.getY()-lookingAtXY.getY();
        double dist = Math.sqrt(dx*dx + dy*dy);
        dx /= dist;
        dy /= dist;

        double rFrontX = yourXY.getX() + (width/2)*dy;
        double rFrontY = yourXY.getY() - (width/2)*dx;
        double lFrontX = yourXY.getX() - (width/2)*dy;
        double lFrontY = yourXY.getX() + (width/2)*dx;

        // FIXME: Also figure out the back of the car
        return new Boundaries(rFrontX, rFrontY,
                rFrontX + SimConfig.MOVE_UNIT_PER_PIXEL, rFrontY + SimConfig.MOVE_UNIT_PER_PIXEL,
                lFrontX + SimConfig.MOVE_UNIT_PER_PIXEL, lFrontY + SimConfig.MOVE_UNIT_PER_PIXEL,
                lFrontX, lFrontY,
                timeStamp);
    }
}
