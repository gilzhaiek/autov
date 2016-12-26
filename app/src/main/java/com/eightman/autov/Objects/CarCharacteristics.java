package com.eightman.autov.Objects;

import android.graphics.Color;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Objects.Physical.AccDec;
import com.eightman.autov.Objects.Physical.Speed;
import com.eightman.autov.Objects.Physical.Wheels;
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
    Speed speed;
    AccDec accDec;
    Wheels wheels;

    public CarCharacteristics(double width, double length, int color, Speed speed, AccDec accDec,
                              Wheels wheels) {
        this.width = width;
        this.length = length;
        this.color = color;
        this.speed = speed;
        this.accDec = accDec;
        this.wheels = wheels;
    }

    public static CarCharacteristics generateRandom() {
        Speed speed = Speed.generateRandom();
        return new CarCharacteristics(
                MathUtils.getRandomDouble(SimConfig.MIN_CAR_WIDTH, SimConfig.MAX_CAR_WIDTH),
                MathUtils.getRandomDouble(SimConfig.MIN_CAR_LENGTH, SimConfig.MAX_CAR_LENGTH),
                Color.argb(255, MathUtils.getRandomInt(256), MathUtils.getRandomInt(256), MathUtils.getRandomInt(256)),
                speed,
                AccDec.generateRandom(speed.getTopSpeed()),
                Wheels.generateRandom());
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

    public Speed getSpeed() {
        return speed;
    }

    public AccDec getAccDec() {
        return accDec;
    }

    public Wheels getWheels() {
        return wheels;
    }
}
