package com.eightman.autov.Simulation.DataMaker;

import com.eightman.autov.Configurations.Constants;
import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Hardware.Boundaries;
import com.eightman.autov.Objects.CarPosition;
import com.eightman.autov.Simulation.Drawings.DrawingUtils;
import com.eightman.autov.Utils.MathUtils;
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
    public List<CarPosition.Final> generatePath(CarPosition.Final fromPosition) {
        final List<CarPosition.Final> path = new LinkedList<>();

        int cnt = random.nextInt(
                SimConfig.MAX_NUM_EDGES - SimConfig.MIN_NUM_EDGES + 1) +
                SimConfig.MIN_NUM_EDGES;

        path.add(fromPosition);
        for (int i = 0; i < cnt; i++) {
            XY from = fromPosition.getBoundaries().getCenterFront();
            XY delta = null;
            XY to = null;
            do {
                delta = new XY(getRandomEdge(), getRandomEdge());
                to = from.add(delta);
            } while ((!DrawingUtils.inOnScreen(to) && SimConfig.LIMIT_CARS_TO_SCREEN) ||
                    DrawingUtils.enteredLaunchArea(from, to));

            if(delta.getX() == delta.getY() && delta.getX() == 0) {
                i--;
                continue;
            }

            double speed = MathUtils.getRandomDouble(SimConfig.MIN_SPEED, SimConfig.MAX_SPEED);
            CarPosition.Final fromRotated = rotateCar(fromPosition, from, to, speed).getPosition();
            CarPosition.Final carPosition = fromRotated;
            double totalSecondsDouble = delta.getVector() / speed;
            int totalSeconds = (int)totalSecondsDouble;
            XY deltaPerSecond = new XY(delta.getX() / totalSeconds, delta.getY() / totalSeconds);
            for (int sec = 0; sec < totalSeconds; sec++) {
                carPosition = (new CarPosition(
                        carPosition.getBoundaries().addOffset(deltaPerSecond),
                        speed,
                        Constants.ONE_SECOND)).getPosition();

                path.add(carPosition);
            }
            long leftOver = (long)(totalSecondsDouble*1000 - totalSeconds*1000);
            if(leftOver > 0) {
                carPosition = (new CarPosition(
                        fromRotated.getBoundaries().addOffset(delta),
                        speed,
                        leftOver)).getPosition();

                path.add(carPosition);
            }
            fromPosition = carPosition;
        }

        return path;
    }

    private CarPosition rotateCar(CarPosition.Final position, XY from, XY to, double speed) {
        Boundaries toBoundaries = TrigUtils.getBoundaries(
                from, to, position.getBoundaries().getWidth(), position.getBoundaries().getLength());

        return new CarPosition(toBoundaries, speed, 0);
    }

    private double getRandomEdge() {
        // Edge can be positive or negative
        //return (random.nextInt(3)-1)*SimConfig.EDGE_METERS; // <-- Uncomment this for 8 directions
        return (random.nextInt(2) == 1 ? 1 : -1)*SimConfig.EDGE_METERS;
        /*double range = (SimConfig.MAX_EDGE_METERS - SimConfig.MIN_EDGE_METERS);
        int sign = random.nextInt(2) == 1 ? 1 : -1;
        double edge = sign * ((int) SimConfig.MIN_EDGE_METERS + random.nextInt((int) range + 1));
        return edge;*/
    }
}
