package com.eightman.autov.Configurations;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class SimulationConfiguration {
    final public static boolean IS_SIMULATION = true;

    final public static double CM_PER_PIXEL = 100; // 1 Meter for every pixel

    // Random Square Path Maker
    final public static int MIN_EDGE_CM = 10*100; // 10 Meters
    final public static int MAX_EDGE_CM = 100*100; // 100 Meters
    final public static int MIN_NUM_EDGES = 1;
    final public static int MAX_NUM_EDGES = 10;
}
