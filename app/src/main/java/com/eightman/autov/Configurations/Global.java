package com.eightman.autov.Configurations;

/**
 * Created by gilzhaiek on 2016-11-04.
 */

public class Global {
    private static long id = 1;

    public static long generateId() {
        return id++;
    }

    public static double canvasWidth = 100; // 100 pixels
    public static double canvasHeight = 100; // 300 pixels

    public static double offsetX = 0.0;
    public static double offsetY = 0.0;

    // Our unit is 1 meter
    public static double pixelPerMoveUnit = 100.0; // 1 Meter is 100 pixels so 1 pixel is 1/100 meter
}
