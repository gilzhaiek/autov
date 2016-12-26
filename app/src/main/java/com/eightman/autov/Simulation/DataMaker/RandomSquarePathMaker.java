package com.eightman.autov.Simulation.DataMaker;

import com.eightman.autov.Configurations.Constants;
import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Objects.CarPath;
import com.eightman.autov.Objects.CarPosition;
import com.eightman.autov.Objects.Geom.Boundaries;
import com.eightman.autov.Objects.Geom.LineSegment;
import com.eightman.autov.Objects.Geom.XY;
import com.eightman.autov.Simulation.Drawings.DrawingUtils;
import com.eightman.autov.Simulation.Interfaces.IRandomPathMaker;
import com.eightman.autov.Utils.MathUtils;
import com.eightman.autov.Utils.TrigUtils;

import java.util.Random;

/**
 * Created by gilzhaiek on 2016-10-27.
 */

public class RandomSquarePathMaker implements IRandomPathMaker {
    static Random random = new Random();

    @Override
    public boolean generatePath(CarPath carPath) throws Exception {
//        int cnt = random.nextInt(
//                SimConfig.MAX_NUM_EDGES - SimConfig.MIN_NUM_EDGES + 1) +
//                SimConfig.MIN_NUM_EDGES;

        return generateHardPositions(carPath.getLastPosition(), 1);
    }

    private void genRandomPath(CarPosition lastPosition, LineSegment lineSegment) {
        lineSegment.setPointA(lastPosition.getBoundaries().getCenterFront());
        do {
            lineSegment.setPointB(
                    lineSegment.getPointA().add(new XY(getRandomEdge(), getRandomEdge())));
        }
        while ((!DrawingUtils.inOnScreen(lineSegment.getPointB()) && SimConfig.LIMIT_CARS_TO_SCREEN));

        // Didn't go anywhere - generate again...
        if (lineSegment.getSlopeX() == 0 && lineSegment.getSlopeY() == 0) {
            genRandomPath(lastPosition, lineSegment);
        }
    }

    private CarPosition headTowards(CarPosition fromPosition, Boundaries finalBoundaries,
                                    double finalSpeed, long deltaSpeed) {
        return fromPosition;// TODO: Finish
    }

    private boolean generateSoftPosition(CarPosition fromPosition, int cnt) throws Exception {
        LineSegment lineSegment = new LineSegment();
        genRandomPath(fromPosition, lineSegment);

        Boundaries toBoundaries = TrigUtils.getHeadingBoundaries(
                lineSegment.getPointA(), lineSegment.getPointB(),
                fromPosition.getBoundaries().getWidth(), fromPosition.getBoundaries().getLength());
        toBoundaries = toBoundaries.addOffset(lineSegment.getPointB());

        CarPosition toPosition;
        do {
            toPosition = headTowards(fromPosition, toBoundaries, 0, Constants.ONE_SECOND);
        } while (!toPosition.getBoundaries().equals(toBoundaries));

        return true;
    }

    private boolean generateHardPositions(CarPosition lastPosition, int cnt) throws Exception {
        LineSegment lineSegment = new LineSegment();
        genRandomPath(lastPosition, lineSegment);
        XY delta = new XY(lineSegment.getSlopeX(), lineSegment.getSlopeY());

        double speed = MathUtils.getRandomDouble(SimConfig.MIN_SPEED, SimConfig.MAX_SPEED);
        CarPosition fromRotated = rotateCar(lastPosition,
                lineSegment.getPointA(), lineSegment.getPointB(),
                lastPosition.getTimeToNextPosition(), speed);

        CarPosition carPosition = fromRotated;
        double totalSecondsDouble = delta.getVector() / speed;
        int totalSeconds = (int) totalSecondsDouble;
        long leftOver = (long) (totalSecondsDouble * 1000 - totalSeconds * 1000);
        XY deltaPerSecond = new XY(delta.getX() / totalSeconds, delta.getY() / totalSeconds);
        for (int sec = 0; ; ++sec) {
            long timeOffset = (sec == totalSeconds) ? leftOver : Constants.ONE_SECOND;
            Boundaries boundaries = carPosition.getBoundaries().addOffset(deltaPerSecond);
            carPosition = CarPosition.getMovingPosition(boundaries, speed, 0.0, 0.0, timeOffset);

            if (!lastPosition.setNext(carPosition, true)) { // In case anyone altered the path
                return false;
            }
            lastPosition = carPosition;

            if (sec >= totalSeconds) {
                break;
            }
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

        return generateHardPositions(lastPosition, cnt);
    }

    private CarPosition rotateCar(CarPosition position, XY from, XY to, long timeOffset, double speed) {
        Boundaries toBoundaries = TrigUtils.getHeadingBoundaries(
                from, to, position.getBoundaries().getWidth(), position.getBoundaries().getLength());

        return CarPosition.getMovingPosition(toBoundaries, speed, 0.0, 0.0, timeOffset);
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
