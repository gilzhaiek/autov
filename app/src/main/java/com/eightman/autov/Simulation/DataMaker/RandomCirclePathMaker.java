package com.eightman.autov.Simulation.DataMaker;

import android.util.Pair;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Managers.BoundariesManager;
import com.eightman.autov.Objects.CarCharacteristics;
import com.eightman.autov.Objects.CarPath;
import com.eightman.autov.Objects.CarPosition;
import com.eightman.autov.Objects.Geom.Boundaries;
import com.eightman.autov.Objects.Geom.Circle;
import com.eightman.autov.Objects.Geom.LineSegment;
import com.eightman.autov.Objects.Geom.XY;
import com.eightman.autov.Objects.Physical.AccDec;
import com.eightman.autov.Objects.Physical.Wheels;
import com.eightman.autov.Simulation.Drawings.DrawingUtils;
import com.eightman.autov.Simulation.Interfaces.IRandomPathMaker;
import com.eightman.autov.Utils.MathUtils;
import com.eightman.autov.Utils.TrigUtils;

/**
 * Created by gilzhaiek on 2016-12-27.
 */

public class RandomCirclePathMaker implements IRandomPathMaker {
    @Override
    public boolean generatePath(CarPath carPath, CarCharacteristics carCharacteristics) throws Exception {
        XY center = getRandomPoint();
        double randomAngle = MathUtils.getRandomDouble(0, 360);

        CarPosition carPosition = carPath.getCurrentPosition();

        Boundaries toBoundaries = BoundariesManager.getBoundariesRotated(center,
                carCharacteristics.getWidth(), carCharacteristics.getWidth(), randomAngle,
                Wheels.STRAIGHT_WHEELS, carPosition.getBoundaries().getMaxWheelsAngle());


        //while (!carPosition.getBoundaries().equals(toBoundaries)) {
        for (int i = 0; i < 10; i++) {
            CarPosition newCarPosition = moveCloser(carCharacteristics.getAccDec(), carPosition, toBoundaries);
            carPosition.setNext(newCarPosition, true);
            carPosition = newCarPosition;
        }

        return true;
    }

    private XY getRandomPoint() {
        Pair<XY, XY> playground = DrawingUtils.getCarPlayground();
        double x = MathUtils.getRandomDouble(playground.first.getX() + SimConfig.MAX_CAR_LENGTH,
                playground.second.getX() - SimConfig.MAX_CAR_LENGTH);
        double y = MathUtils.getRandomDouble(playground.first.getY() + SimConfig.MAX_CAR_LENGTH,
                playground.second.getY() - SimConfig.MAX_CAR_LENGTH);

        return new XY(x, y);
    }

    private CarPosition moveCloser(AccDec accDec, CarPosition oldCarPosition, Boundaries toBoundaries) {
        double acceleration;
        double wheelsAngle = oldCarPosition.getWheelsAngle();

        Boundaries newBoundaries = oldCarPosition.generateNextBoundaries(wheelsAngle);
        double newSpeed = oldCarPosition.generateNextSpeed();

        // Make sure we slow down in time
        double distanceToStop = accDec.getStopDistance(oldCarPosition.getSpeed());
        double shortestDistance = BoundariesManager.getShortestDistance(oldCarPosition.getBoundaries(), toBoundaries).first;
        if (shortestDistance < distanceToStop) {
            acceleration = accDec.getComfortableDec();
        } else {
            acceleration = accDec.getAcceleration(newSpeed);
        }

        // Did we hit the place?
        if (newBoundaries.equals(toBoundaries)) {
            if (newSpeed == 0.0 && acceleration == 0.0) {
                return CarPosition.getRestedPosition(newBoundaries);
            } else {
                return CarPosition.getMovingPosition(newBoundaries, newSpeed, acceleration, wheelsAngle, SimConfig.PATH_RESOLUTION_MS);
            }
        }

        // TODO: Check if a point is on the direction of heading

        Circle[] toCircles = toBoundaries.getMaxTurningCircles();
        // Check if I am on the circle and on the same direction
        Circle turningCircle = newBoundaries.getTurningCircle();
        if (turningCircle != null && (turningCircle.equals(toCircles[0]) || turningCircle.equals(toCircles[1]))) {
            return CarPosition.getMovingPosition(newBoundaries, newSpeed, acceleration, wheelsAngle, SimConfig.PATH_RESOLUTION_MS);
        }

        Circle[] myMaxCircles = newBoundaries.getMaxTurningCircles();
        LineSegment segments[] = new LineSegment[4];
        if (myMaxCircles[0].getDirection() == toCircles[0].getDirection()) {
            segments[0] = TrigUtils.findOuterTangents(myMaxCircles[0], toCircles[0]);
            segments[1] = TrigUtils.findOuterTangents(myMaxCircles[1], toCircles[1]);
        } else {
            segments[0] = TrigUtils.findOuterTangents(myMaxCircles[0], toCircles[1]);
            segments[1] = TrigUtils.findOuterTangents(myMaxCircles[1], toCircles[0]);
        }

        // TODO: Add Inner Segments to be [2] and [3]

        if (turningCircle == null) { // We're heading straight
            // Check if my direction is intersecting with any of the two circles and reaching the same direction
            double distanceToTargetCircle = -1.0;
            if (outerTangs[0] != null && outerTangs[0].first.equals(newBoundaries.getCenterFront())) {
                distanceToTargetCircle = MathUtils.getDistance(outerTangs[0].first, newBoundaries.getCenterFront());
            } else if (outerTangs[1] != null && outerTangs[1].first.equals(newBoundaries.getCenterFront())) {

            }


            // Make sure I slow down to be max wheels turn when I hit the circle
        }


        // Get the distance between the two max circles (3 options)
        // if the closer one is the same direction - use that
        // if the closer one is the opposite direction but the circles overlap use the circles where the radius don't overlap
    }
}
