package com.eightman.autov.Objects;

import android.graphics.Color;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Utils.MathUtils;

import java.util.Random;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class CarCharacteristics {
    static Random random = new Random();

    double width; // in meters
    double length; // in meters
    int color;
    double topSpeed; // meters per second

    public CarCharacteristics(double width, double length, int color, double topSpeed) {
        this.width = width;
        this.length = length;
        this.color = color;
        this.topSpeed = topSpeed;
    }

    public static CarCharacteristics generateRandom() {
        return new CarCharacteristics(
                MathUtils.getRandomDouble(SimConfig.MIN_CAR_WIDTH, SimConfig.MAX_CAR_WIDTH),
                MathUtils.getRandomDouble(SimConfig.MIN_CAR_LENGTH, SimConfig.MAX_CAR_LENGTH),
                Color.argb(255, MathUtils.getRandomInt(256), MathUtils.getRandomInt(256), MathUtils.getRandomInt(256)),
                MathUtils.getRandomDouble(SimConfig.MIN_TOP_SPEED, SimConfig.MAX_TOP_SPEED));
    }

    public double getWidth() {
        return width;
    }

    public double getLength() {
        return length;
    }

    public int getColor() {
        return color;
    }

    public double getTopSpeed() {
        return topSpeed;
    }

    public double getSafeRadius() {
        return (length > width) ? length : width;
    }
}
