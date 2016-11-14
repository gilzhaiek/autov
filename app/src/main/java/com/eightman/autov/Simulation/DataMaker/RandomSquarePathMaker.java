package com.eightman.autov.Simulation.DataMaker;

import com.eightman.autov.Configurations.Constants;
import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Hardware.Boundaries;
import com.eightman.autov.Objects.CarPath;
import com.eightman.autov.Objects.CarPosition;
import com.eightman.autov.Simulation.Drawings.DrawingUtils;
import com.eightman.autov.Simulation.Interfaces.IRandomPathMaker;
import com.eightman.autov.Utils.MathUtils;
import com.eightman.autov.Utils.TrigUtils;
import com.eightman.autov.Utils.XY;
import com.eightman.autov.Utils.CollisionUtils;

import java.util.Random;

/**
 * Created by gilzhaiek on 2016-10-27.
 */

public class RandomSquarePathMaker implements IRandomPathMaker {
    static Random random = new Random();

    @Override
    public boolean generatePath(CarPath carPath) {
        int cnt = random.nextInt(
                SimConfig.MAX_NUM_EDGES - SimConfig.MIN_NUM_EDGES + 1) +
                SimConfig.MIN_NUM_EDGES;

        return generatePositions(carPath.getLastPosition(), cnt);
    }

    private boolean generatePositions(CarPosition lastPosition, int cnt) {
        XY from = lastPosition.getBoundaries().getCenterFront();
        XY delta = null;
        XY to = null;
        do {
            delta = new XY(getRandomEdge(), getRandomEdge());
            to = from.add(delta);
        } while ((!DrawingUtils.inOnScreen(to) && SimConfig.LIMIT_CARS_TO_SCREEN) ||
                DrawingUtils.enteredLaunchArea(from, to));

        if (delta.getX() == delta.getY() && delta.getX() == 0) {
            return generatePositions(lastPosition, cnt);
        }

        double speed = MathUtils.getRandomDouble(SimConfig.MIN_SPEED, SimConfig.MAX_SPEED);
        CarPosition fromRotated = rotateCar(lastPosition, from, to, lastPosition.getTimeToNextPosition(), speed);
        CarPosition carPosition = fromRotated;
        double totalSecondsDouble = delta.getVector() / speed;
        int totalSeconds = (int) totalSecondsDouble;
        long leftOver = (long) (totalSecondsDouble * 1000 - totalSeconds * 1000);
        XY deltaPerSecond = new XY(delta.getX() / totalSeconds, delta.getY() / totalSeconds);
        for (int sec = 0; sec < totalSeconds; ++sec) {
            long timeOffset = (sec == totalSeconds) ? leftOver : Constants.ONE_SECOND;
            Boundaries boundaries = carPosition.getBoundaries().addOffset(deltaPerSecond);
            carPosition = CarPosition.getMovingPosition(
                    boundaries,
                    speed,
                    timeOffset,
                    CollisionUtils.getHeadingBoundaries(
                            boundaries,
                            speed,
                            timeOffset,
                            CollisionUtils.Zone.COLLISION_ZONE),
                    CollisionUtils.getHeadingBoundaries(
                            boundaries,
                            speed,
                            timeOffset,
                            CollisionUtils.Zone.SAFE_ZONE));

            if (!lastPosition.setNext(carPosition, true)) { // In case anyone altered th
                return false;
            }
            lastPosition = carPosition;
        }


        if (leftOver > 0) {
            carPosition = CarPosition.getRestedPosition(fromRotated.getBoundaries().addOffset(delta));

            if (!lastPosition.setNext(carPosition, true)) { // In case anyone altered th
                return false;
            }
            lastPosition = carPosition;
        }

        if (--cnt <= 0) {
            return true;
        }

        return generatePositions(lastPosition, cnt);
    }

    private CarPosition rotateCar(CarPosition position, XY from, XY to, long timeOffset, double speed) {
        Boundaries toBoundaries = TrigUtils.getBoundaries(
                from, to, position.getBoundaries().getWidth(), position.getBoundaries().getLength());

        return CarPosition.getMovingPosition(
                toBoundaries,
                speed,
                timeOffset,
                position.getCollisionZone(),
                position.getSafeZone());
    }

    private double getRandomEdge() {
        // Edge can be positive or negative
        //return (random.nextInt(3)-1)*SimConfig.EDGE_METERS; // <-- Uncomment this for 8 directions
        return (random.nextInt(2) == 1 ? 1 : -1) * SimConfig.EDGE_METERS;
        /*double range = (SimConfig.MAX_EDGE_METERS - SimConfig.MIN_EDGE_METERS);
        int sign = random.nextInt(2) == 1 ? 1 : -1;
        double edge = sign * ((int) SimConfig.MIN_EDGE_METERS + random.nextInt((int) range + 1));
        return edge;*/
    }
}
