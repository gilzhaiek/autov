package com.eightman.autov.Simulation.DataMaker;

import android.util.Log;

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
    public List<CarPosition> generatePath(CarPosition initialCarPosition) {
        final List<CarPosition> path = new ArrayList<>();

        int cnt = random.nextInt(
                SimConfig.MAX_NUM_EDGES- SimConfig.MIN_NUM_EDGES) +
                SimConfig.MIN_NUM_EDGES;

        for(int i = 0; i < cnt; i++) {
            initialCarPosition = generateNewCarLocation(initialCarPosition);
            path.add(initialCarPosition);
            Log.d("SHIT", initialCarPosition.getPosition().getBoundaries().toString());
        }

        return path;
    }

    private CarPosition generateNewCarLocation(CarPosition lastLocation) {
        CarPosition.Final position = lastLocation.getPosition();
        XY from = position.getBoundaries().getCenterFront();
        XY to = new XY(getRandomEdge(from.getX()), getRandomEdge(from.getY()));

        Boundaries toBoundaries = MathUtils.getBoundaries(
                from, to, position.getBoundaries().getWidth(), position.getBoundaries().getLength(), 0);

        return new CarPosition(toBoundaries, 30.0, 0.0); // TODO: Replace
    }

    private double getRandomEdge(double reference) {
        double range = 2 * (SimConfig.MAX_EDGE_METERS - SimConfig.MIN_EDGE_METERS);
        int edge = random.nextInt((int)range) - ((int)range / 2);
        if (edge < 0) {
            edge -= SimConfig.MIN_EDGE_METERS;
        } else {
            edge += SimConfig.MIN_EDGE_METERS;
        }

        return reference + (double) edge;
    }
}
