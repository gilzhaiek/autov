package com.eightman.autov.Configurations;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class SimConfig {
    final public static boolean IS_SIMULATION = true;
    final public static int DELAY_MS = 100;
    final public static boolean LIMIT_CARS_TO_SCREEN = true;

    // Our unit is 1 meter
    final public static double PIXEL_PER_MOVE_UNIT = 3.0;

    // Car
    final public static double MIN_CAR_LENGTH = 3.0;
    final public static double MAX_CAR_LENGTH = 10.0;
    final public static double MIN_CAR_WIDTH = 2.0;
    final public static double MAX_CAR_WIDTH = 4.0;
    final public static double MIN_TOP_SPEED = 1.0; // Meters per second
    final public static double MAX_TOP_SPEED = 5.0; // Meters per second

    // Random Square Path Maker
    final public static double EDGE_METERS = 20.0;
    final public static int MIN_NUM_EDGES = 2;
    final public static int MAX_NUM_EDGES = 20;
    final public static double MIN_SPEED = 1.5;
    final public static double MAX_SPEED = 4;

    final public static double MAX_COLLISION_VALIDATION = MAX_CAR_LENGTH*3;

    // Stroke Width
    final public static float STROKE_WIDTH = 3.0f;
    final public static boolean DRAW_WHEEL_PATH = true;

}
