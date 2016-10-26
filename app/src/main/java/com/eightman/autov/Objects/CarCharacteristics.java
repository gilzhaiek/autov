package com.eightman.autov.Objects;

import android.graphics.Color;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class CarCharacteristics {
    double width; // in meters
    double length; // in meters
    Color color;
    double maxSpeed; // meters per second

    public CarCharacteristics(double width, double length, Color color, double maxSpeed) {
        this.width = width;
        this.length = length;
        this.color = color;
        this.maxSpeed = maxSpeed;
    }
}
