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


}
