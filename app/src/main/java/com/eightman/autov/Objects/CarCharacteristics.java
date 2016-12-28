package com.eightman.autov.Objects;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Objects.Physical.AccDec;
import com.eightman.autov.Objects.Physical.Speed;
import com.eightman.autov.Objects.Physical.Wheels;
import com.eightman.autov.Simulation.Drawings.DrawingUtils;
import com.eightman.autov.Utils.MathUtils;

import java.util.Random;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class CarCharacteristics {
    static Random random = new Random();

    final double width; // in meters
    final double length; // in meters
    final int color;
    final Speed speed;
    final AccDec accDec;
    final Wheels wheels;

    public CarCharacteristics(double width, double length, int color, Speed speed, Wheels wheels, AccDec accDec) {
        this.width = width;
        this.length = length;
        this.color = color;
        this.speed = speed;
        this.wheels = wheels;
        this.accDec = accDec;
    }

    public static CarCharacteristics generateRandom() {
        double length = MathUtils.getRandomDouble(SimConfig.MIN_CAR_LENGTH, SimConfig.MAX_CAR_LENGTH);
        double width = MathUtils.getRandomDouble(SimConfig.MIN_CAR_WIDTH, SimConfig.MAX_CAR_WIDTH);
        Wheels wheels = Wheels.generateRandom(length, width);
        Speed speed = Speed.generateRandom(wheels);
        return new CarCharacteristics(length, width, DrawingUtils.randomColor(), speed, wheels,
                AccDec.generateRandom(speed.getTopSpeed()));
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
