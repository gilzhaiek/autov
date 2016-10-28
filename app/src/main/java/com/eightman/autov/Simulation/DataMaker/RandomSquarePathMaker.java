package com.eightman.autov.Simulation.DataMaker;

import com.eightman.autov.Configurations.SimulationConfiguration;
import com.eightman.autov.Objects.CarLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by gilzhaiek on 2016-10-27.
 */

public class RandomSquarePathMaker implements IRandomPathMaker {
    Random random = new Random();

    @Override
    public List<CarLocation> generatePath(CarLocation carLocation) {
        final List<CarLocation> path = new ArrayList<>();

        int cnt = random.nextInt(
                SimulationConfiguration.MAX_NUM_EDGES-SimulationConfiguration.MIN_NUM_EDGES) +
                SimulationConfiguration.MIN_NUM_EDGES;

        for(int i = 0; i < cnt; i++) {
            carLocation = generateNewCarLocation(carLocation);
            path.add(carLocation);
        }

        return path;
    }

    private CarLocation generateNewCarLocation(CarLocation lastLocation) {
        CarLocation.Final loc = lastLocation.getLocation();
        return new CarLocation(getRandomEdge(loc.x), getRandomEdge(loc.y), 30.0, 0.0);
    }

    private double getRandomEdge(double reference) {
        int range = 2 * (SimulationConfiguration.MAX_EDGE_CM - SimulationConfiguration.MIN_EDGE_CM);
        int edge = random.nextInt(range) - (range / 2);
        if (edge < 0) {
            edge -= SimulationConfiguration.MIN_EDGE_CM;
        } else {
            edge += SimulationConfiguration.MIN_EDGE_CM;
        }

        return reference + (double) edge;
    }
}
