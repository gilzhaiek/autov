package com.eightman.autov.Simulation.DataMaker;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Hardware.Boundaries;
import com.eightman.autov.Objects.CarPosition;
import com.eightman.autov.Utils.MathUtils;
import com.eightman.autov.Utils.XY;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by gilzhaiek on 2016-10-27.
 */

public class RandomSquarePathMaker implements IRandomPathMaker {
    static Random random = new Random();

    @Override
    public List<CarPosition> generatePath(CarPosition carPosition) {
        final List<CarPosition> path = new ArrayList<>();

        int cnt = random.nextInt(
                SimConfig.MAX_NUM_EDGES - SimConfig.MIN_NUM_EDGES + 1) +
                SimConfig.MIN_NUM_EDGES;

        path.add(carPosition);
        for(int i = 0; i < cnt; i++) {
            XY from = carPosition.getPosition().getBoundaries().getCenterFront();;
            XY delta = new XY(200, 100);//getRandomEdge(), getRandomEdge());
            XY to = from.add(delta);
            carPosition = rotateCar(carPosition, from, to);
            path.add(carPosition);
            carPosition = new CarPosition(carPosition.getPosition().getBoundaries().addOffset(delta), 30.0, 0.0); // TODO: Replace
            path.add(carPosition);
        }

        return path;
    }

    private CarPosition rotateCar(CarPosition lastLocation, XY from, XY to) {
        CarPosition.Final position = lastLocation.getPosition();

        Boundaries toBoundaries = MathUtils.getBoundaries(
                from, to, position.getBoundaries().getWidth(), position.getBoundaries().getLength(), 0);

        return new CarPosition(toBoundaries, 30.0, 0.0); // TODO: Replace
    }

    private double getRandomEdge() {
        // Edge can be positive or negative
        double range = (SimConfig.MAX_EDGE_METERS - SimConfig.MIN_EDGE_METERS);
        int sign = random.nextInt(2) == 1 ? 1 : -1;
        double edge = sign*((int)SimConfig.MIN_EDGE_METERS + random.nextInt((int)range+1));
        return edge;
    }
}
