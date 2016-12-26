package com.eightman.autov.Configurations;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class SimConfig {
    final public static boolean IS_SIMULATION = true;
    // Simulation
    final public static int SIM_NUM_OF_5_CAR_SETS = 10;
    final public static int SIM_DELAY_ADDITION = 100000;

    //final public static int DELAY_MS = 16; // 60fps
    final public static int DELAY_MS = 500; // Every frame is a second
    final public static boolean LIMIT_CARS_TO_SCREEN = true;

    // Our unit is 1 meter
    final public static double PIXEL_PER_MOVE_UNIT = 7.0;

    // Other
    final public static double MIN_MOVING_SPEED = 0.01; // 1 cm per second
    final public static double MIN_MOVING_DISTANCE = 0.01; // 1 cm
    final public static double PARKING_SAFE_DISTANCE = 0.10; // 10 cm
    final public static double COLLISION_ZONE_ERROR_ADDITION = 1.25; // 25%
    final public static double SAFE_ZONE_ERROR_ADDITION = 1.25; // 25%

    // Car
    final public static double MIN_CAR_LENGTH = 3.0;
    final public static double MAX_CAR_LENGTH = 10.0;
    final public static double MIN_CAR_WIDTH = 2.0;
    final public static double MAX_CAR_WIDTH = 4.0;

    // Speed
    final public static double MIN_TOP_SPEED = 2.0; // Meters per second
    final public static double MAX_TOP_SPEED = 8.0; // Meters per second
    final public static double MIN_ACC = 1.0; // m/s^2:  5 m/s after 5 seconds and 10 m/s after 10 seconds.
    final public static double MAX_ACC = 10.0; // m/s^2
    final public static double MIN_EMERGENCY_DEC = -5.0; // m/s^2 Deceleration
    final public static double MAX_EMERGENCY_DEC = -10.0; // m/s^2
    final public static double COMFORTABLE_DEC = -3.0; // m/s^2 http://journaldatabase.info/download/pdf/study_deceleration_behaviour_different

    // Wheels
    final public static double MIN_MAX_WHEELS_ANGEL = 45.0; // DEGREES
    final public static double MAX_MAX_WHEELS_ANGLE = 65.0;  // DEGREES
    final public static double COMFORTABLE_SPEED_FOR_MAX_ANGLE = 0.5; // m/s
    final public static double MIN_ANGLE_CHANGE_WHEEL_TURN = 3.0; // Per Second
    final public static double MAX_ANGLE_CHANGE_WHEEL_TURN = 15.0; // Per Second

    // Random Square Path Maker
    final public static double EDGE_METERS = 20.0;
    final public static int MIN_NUM_EDGES = 2;
    final public static int MAX_NUM_EDGES = 20;
    final public static double MIN_SPEED = 1.5;
    final public static double MAX_SPEED = 4;

    final public static double MAX_COLLISION_VALIDATION = MAX_CAR_LENGTH * 2;

    // Stroke Width
    final public static float STROKE_WIDTH = 5.0f;
    final public static boolean DRAW_WHEEL_PATH = true;

}
