package com.eightman.autov.Configurations;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class SimConfig {
    final public static boolean IS_SIMULATION = true;
    final public static int DELAY_MS = 30;

    // Our unit is 1 meter
    final public static double PIXEL_PER_MOVE_UNIT = 0.2; // 1 Meter for every pixel

    // Car
    final public static double MIN_CAR_LENGTH = 100.0;
    final public static double MAX_CAR_LENGTH = 100.0;
    final public static double MIN_CAR_WIDTH = 50.0;
    final public static double MAX_CAR_WIDTH = 50.0;
    final public static double MIN_TOP_SPEED = 1.0; // Meters per second
    final public static double MAX_TOP_SPEED = 3.0; // Meters per second

    // Random Square Path Maker
    final public static double MIN_EDGE_METERS = 10.0; // 10 Meters
    final public static double MAX_EDGE_METERS = 20.0; // 100 Meters
    final public static int MIN_NUM_EDGES = 2;
    final public static int MAX_NUM_EDGES = 20;
    final public static double SPEED = 2.0; // 2 Meters Per Second - 7.2 Km/h

    // Stroke Width
    final public static float STROKE_WIDTH = 3.0f;
    final public static boolean DRAW_WHEEL_PATH = true;

}
