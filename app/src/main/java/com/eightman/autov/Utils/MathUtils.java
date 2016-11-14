package com.eightman.autov.Utils;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Objects.CarPosition;

import java.util.Random;

/**
 * Created by gilzhaiek on 2016-10-29.
 */

public class MathUtils {
    static Random random = new Random();

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
}
