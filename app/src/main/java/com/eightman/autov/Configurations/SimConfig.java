package com.eightman.autov.Configurations;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class SimConfig {
    final public static boolean IS_SIMULATION = true;

    // Our unit is 1 meter
    final public static double MOVE_UNIT_PER_PIXEL = 3.0; // 1 Meter for every pixel

    // Car
    final public static double MIN_CAR_LENGTH = 2.0;
    final public static double MAX_CAR_LENGTH = 20.0;
    final public static double MIN_CAR_WIDTH = 1.0;
    final public static double MAX_CAR_WIDTH = 5.0;

    // Random Square Path Maker
    final public static double MIN_EDGE_METERS = 10.0; // 10 Meters
    final public static double MAX_EDGE_METERS = 100.0; // 100 Meters
    final public static int MIN_NUM_EDGES = 10;
    final public static int MAX_NUM_EDGES = 50;

    // Stroke Width
    final public static float STROKE_WIDTH = 3.0f;
}
