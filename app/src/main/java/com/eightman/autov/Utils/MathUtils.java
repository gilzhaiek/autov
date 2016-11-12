package com.eightman.autov.Utils;

import java.util.Random;

/**
 * Created by gilzhaiek on 2016-10-29.
 */

public class MathUtils {
    static Random random = new Random();

    public static XY getDelta(XY xyA, XY xyB) {
        return new XY(xyA.getX() - xyB.getX(), xyA.getY() - xyB.getY());
    }

    public static double getSpeed(XY xyA, long timeStampA, XY xyB, long timeStampB) {
        // TODO: finish
        return 0;
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
