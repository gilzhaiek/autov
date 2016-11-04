package com.eightman.autov.Simulation.DataMaker;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Hardware.Boundaries;
import com.eightman.autov.Objects.CarPosition;
import com.eightman.autov.Utils.TrigUtils;
import com.eightman.autov.Utils.XY;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by gilzhaiek on 2016-10-27.
 */

public class RandomSquarePathMaker implements IRandomPathMaker {
    static Random random = new Random();

    @Override
    public List<CarPosition.Final> generatePath(CarPosition.Final carPosition) {
        final List<CarPosition.Final> path = new LinkedList<>();

        int cnt = random.nextInt(
                SimConfig.MAX_NUM_EDGES - SimConfig.MIN_NUM_EDGES + 1) +
                SimConfig.MIN_NUM_EDGES;

        path.add(carPosition);
        for(int i = 0; i < cnt; i++) {
            XY from = carPosition.getBoundaries().getCenterFront();
            XY delta = new XY(getRandomEdge(), getRandomEdge());
            XY to = from.add(delta);
            carPosition = rotateCar(carPosition, from, to).getPosition();
            double totalSecond = delta.getVector()/SimConfig.SPEED;
            XY deltaPerSecond = new XY(delta.getX()/SimConfig.SPEED, delta.getY()/SimConfig.SPEED);
            for(int sec = 0; sec < totalSecond; sec++) {
                carPosition = (new CarPosition(
                        carPosition.getBoundaries().addOffset(deltaPerSecond),
                        SimConfig.SPEED)).getPosition(); // TODO: Replace

                path.add(carPosition);
            }
        }

        return path;
    }

    private CarPosition rotateCar(CarPosition.Final position, XY from, XY to) {
        Boundaries toBoundaries = TrigUtils.getBoundaries(
                from, to,
                position.getBoundaries().getWidth(), position.getBoundaries().getLength(),
                System.currentTimeMillis());

        return new CarPosition(toBoundaries, SimConfig.SPEED); // TODO: Replace
    }

    private double getRandomEdge() {
        // Edge can be positive or negative
        double range = (SimConfig.MAX_EDGE_METERS - SimConfig.MIN_EDGE_METERS);
        int sign = random.nextInt(2) == 1 ? 1 : -1;
        double edge = sign*((int)SimConfig.MIN_EDGE_METERS + random.nextInt((int)range+1));
        return edge;
    }
}
