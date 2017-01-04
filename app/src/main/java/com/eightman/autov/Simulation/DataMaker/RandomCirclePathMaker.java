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
import com.eightman.autov.Objects.Physical.Speed;
import com.eightman.autov.Objects.Physical.Wheels;
import com.eightman.autov.Simulation.Drawings.DrawingUtils;
import com.eightman.autov.Simulation.Interfaces.IRandomPathMaker;
import com.eightman.autov.Utils.MathUtils;
import com.eightman.autov.Utils.TrigUtils;

import static com.eightman.autov.Objects.Physical.Wheels.STRAIGHT_WHEELS;

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
                STRAIGHT_WHEELS, carPosition.getBoundaries().getMaxWheelsAngleAbs());
        toBoundaries = toBoundaries.addOffset(new XY(
                MathUtils.getRandomDouble(SimConfig.MIN_EDGE_METERS, SimConfig.MAX_EDGE_METERS),
                MathUtils.getRandomDouble(SimConfig.MIN_EDGE_METERS, SimConfig.MAX_EDGE_METERS)));

        //while (!carPosition.getBoundaries().equals(toBoundaries)) {
        for (int i = 0; i < 100; i++) {
            CarPosition newCarPosition = moveCloser(carCharacteristics.getAccDec(), carCharacteristics.getSpeed(), carPosition, toBoundaries);
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

    private CarPosition moveCloser(AccDec accDec, Speed speed, CarPosition oldCarPosition, Boundaries toBoundaries) {
        double newAcc;
        double maxWheelsAngleSpeed = speed.getComfortableSpeed(toBoundaries.getMaxWheelsAngleAbs());

        Boundaries newBoundaries = oldCarPosition.generateNextBoundaries(oldCarPosition.getWheelsAngle());
        double newSpeed = oldCarPosition.generateNextSpeed();

        // Make sure we slow down in time
        double distanceToStop = accDec.getStopDistance(newSpeed);
        double shortestDistance = BoundariesManager.getShortestDistance(newBoundaries, toBoundaries).first;
        if (shortestDistance < distanceToStop) {
            newAcc = accDec.getComfortableDec();
        } else {
            newAcc = accDec.getAcceleration(newSpeed);
        }

        //Log.d("SHIT", "S=" + newSpeed + " A=" + newAcc);

        // Did we hit the place?
        if (newBoundaries.equals(toBoundaries)) {
            if (newSpeed == 0.0 && newAcc == 0.0) {
                return CarPosition.getRestedPosition(newBoundaries);
            } else {
                return CarPosition.getMovingPosition(newBoundaries, newSpeed, newAcc, SimConfig.PATH_RESOLUTION_MS);
            }
        }

        // TODO: Check if a point is on the direction of heading

        Circle[] toCircles = toBoundaries.getMaxTurningCircles();
        // Check if I am on the circle and on the same direction
        Circle turningCircle = newBoundaries.getTurningCircle();
        if (turningCircle != null && (turningCircle.equals(toCircles[0]) || turningCircle.equals(toCircles[1]))) {
            if (newSpeed > maxWheelsAngleSpeed) {
                // We need to slow down
                newAcc = accDec.getComfortableDec();
            }
            return CarPosition.getMovingPosition(newBoundaries, newSpeed, newAcc, SimConfig.PATH_RESOLUTION_MS);
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

        // Check if my direction is intersecting with any of the two circles and reaching the same direction
        LineSegment segment = null;
        if (segments[0] != null && segments[0].getPointA().equals(newBoundaries.getCenterFront())) {
            segment = segments[0];
        } else if (segments[1] != null && segments[1].getPointA().equals(newBoundaries.getCenterFront())) {
            segment = segments[1];
        }

        if (segment != null) {
            newBoundaries.setWheelsAngle(Wheels.STRAIGHT_WHEELS);
            // Make sure I slow down to be max wheels turn when I hit the circle
            double distanceToTargetCircle = segment.getLength();
            double distanceToTargetSpeed = accDec.getDistanceToTargetSpeed(newSpeed, maxWheelsAngleSpeed);
            if (distanceToTargetCircle <= distanceToTargetSpeed) {
                newAcc = accDec.getComfortableDec();
            } else {
                newAcc = accDec.getAcceleration(newSpeed);
            }

            return CarPosition.getMovingPosition(newBoundaries, newSpeed, newAcc, SimConfig.PATH_RESOLUTION_MS);
        }

        if (myMaxCircles[0].getDirection() == toCircles[0].getDirection()) {
            newBoundaries.setWheelsAngle(newBoundaries.getMaxWheelsAngle(myMaxCircles[0].getDirection()));
        } else {
            newBoundaries.setWheelsAngle(newBoundaries.getMaxWheelsAngle(myMaxCircles[1].getDirection()));
        }

        double comfortableSpeed = speed.getComfortableSpeed(newBoundaries.getWheelsAngle());
        if (newSpeed > comfortableSpeed) {
            newAcc = accDec.getComfortableDec();
        } else {
            newAcc = Math.min(accDec.getAcceleration(newSpeed), comfortableSpeed - newSpeed);
        }

        return CarPosition.getMovingPosition(newBoundaries, newSpeed, newAcc, SimConfig.PATH_RESOLUTION_MS);

        // Get the distance between the two max circles (3 options)
        // if the closer one is the same direction - use that
        // if the closer one is the opposite direction but the circles overlap use the circles where the radius don't overlap
        //return oldCarPosition;
    }
}
