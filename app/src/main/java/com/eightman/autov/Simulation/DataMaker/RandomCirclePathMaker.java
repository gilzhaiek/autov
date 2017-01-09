package com.eightman.autov.Simulation.DataMaker;

import android.util.Log;
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
        XY center = new XY(1.0, 1.0);//getRandomPoint();
        double randomAngle = 180;//MathUtils.getRandomDouble(0, 360);

        CarPosition startPosition = carPath.getLastPosition();
        CarPosition carPosition = CarPosition.getMovingPosition(
                startPosition.getBoundaries(),
                startPosition.getSpeed(),
                startPosition.getAcceleration(),
                startPosition.getTimeToNextPosition());
        CarPosition firstPosition = null;

        Boundaries toBoundaries = BoundariesManager.getBoundariesRotated(center,
                carCharacteristics.getWidth(), carCharacteristics.getLength(), randomAngle,
                STRAIGHT_WHEELS, carPosition.getBoundaries().getMaxWheelsAngleAbs());
//        toBoundaries = toBoundaries.addOffset(new XY(
//                MathUtils.getRandomDouble(SimConfig.MIN_EDGE_METERS, SimConfig.MAX_EDGE_METERS),
//                MathUtils.getRandomDouble(SimConfig.MIN_EDGE_METERS, SimConfig.MAX_EDGE_METERS)));

        Log.d("SHIT", "TARGET=" + toBoundaries.getCenterFront().toString());

        int i = 0;
        while (!carPosition.getBoundaries().equals(toBoundaries)) {
            carPosition = moveCloser(carCharacteristics.getAccDec(), carCharacteristics.getSpeed(), carPosition, toBoundaries);
            if (firstPosition == null) {
                firstPosition = carPosition;
            }
            Log.d("SHIT", carPosition.toString());
            if (i++ > 50) {
                break;
            }
        }

        carPosition.setNext(CarPosition.getRestedPosition(toBoundaries), true);
        startPosition.setNext(firstPosition, true);

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

    private CarPosition moveCloser(AccDec accDec, Speed speed, CarPosition oldCarPosition, Boundaries toBoundaries) throws Exception {
        long timeToNextPos = SimConfig.PATH_RESOLUTION_MS;
        Circle.Direction targetDirection = Circle.Direction.STRAIGHT;
        double newAcc;
        double maxWheelsAngleSpeed = speed.getComfortableSpeed(toBoundaries.getMaxWheelsAngleAbs());

        Boundaries oldBoundaries = oldCarPosition.getBoundaries();
        Log.d("SHIT", "CF=" + oldBoundaries.getCenterFront().toString() + " WA=" + oldBoundaries.getWheelsAngle());
        Boundaries newBoundaries = oldCarPosition.generateNextBoundaries(oldCarPosition.getWheelsAngle());
        double newSpeed = oldCarPosition.generateNextSpeed();

        // Make sure we slow down in time
        double distanceToStop = accDec.getStopDistance(newSpeed);
        double targetDistance = MathUtils.getDistance(newBoundaries.getCenterFront(), toBoundaries.getCenterFront());
        if (targetDistance < distanceToStop) {
            Log.d("SHIT", "slowing down...");
            newAcc = accDec.getAcceleration(newSpeed, 0.0);
        } else {
            newAcc = accDec.getAcceleration(newSpeed, newSpeed);
        }

        // Did we hit the place?
        if (newBoundaries.equals(toBoundaries)) {
            Log.d("SHIT", "Hitting toBoundaries");
            if (newSpeed == 0.0 && newAcc == 0.0) {
                oldCarPosition.setNext(CarPosition.getRestedPosition(newBoundaries), true);
            } else {
                oldCarPosition.setNext(CarPosition.getMovingPosition(newBoundaries, newSpeed, newAcc, timeToNextPos), true);
            }
            return oldCarPosition.getNext();
        }

        // TODO: Check if a point is on the direction of heading

        Circle[] toCircles = toBoundaries.getMaxTurningCircles();
        // Check if I am on the circle and on the same direction
        Circle turningCircle = newBoundaries.getTurningCircle();
        if (turningCircle != null && (turningCircle.equals(toCircles[0]) || turningCircle.equals(toCircles[1]))) {
            Log.d("SHIT5", "We are on the same circle");

            if (newSpeed > maxWheelsAngleSpeed) {
                // We need to slow down
                newAcc = accDec.getAcceleration(newSpeed, maxWheelsAngleSpeed);
            }
            oldCarPosition.setNext(CarPosition.getMovingPosition(newBoundaries, newSpeed, newAcc, timeToNextPos), true);
            return oldCarPosition.getNext();
        }

        Circle[] myMaxCircles = newBoundaries.getMaxTurningCircles();
        LineSegment segments[] = new LineSegment[4];
        //if (myMaxCircles[0].getDirection() == toCircles[0].getDirection()) {
        segments[0] = TrigUtils.findOuterTangents(myMaxCircles[0], toCircles[0]);
        segments[1] = TrigUtils.findOuterTangents(myMaxCircles[1], toCircles[1]);
//        } else {
//            segments[0] = TrigUtils.findOuterTangents(myMaxCircles[0], toCircles[1]);
//            segments[1] = TrigUtils.findOuterTangents(myMaxCircles[1], toCircles[0]);
//        }

        // TODO: Add Inner Segments to be [2] and [3]

        // Check if my direction is intersecting with any of the two circles and reaching the same direction
        double ratio = 1.0;
        LineSegment segment = null;
        if (segments[0] != null) {
            targetDirection = toCircles[0].getDirection();
            // Check if i'm on the line
            if (segments[0].isBetween(newBoundaries.getCenterFront())) {
                segment = segments[0];
                Log.d("SHIT3", "segment 0.1");
            } else {
                ratio = myMaxCircles[0].getRatio(oldBoundaries.getCenterFront(), newBoundaries.getCenterFront(), segments[0].getPointA());
                if (ratio <= 1.0) {
                    segment = segments[0];
                    Log.d("SHIT3", "segment 0.2");
                }
            }
        }
        if (segments[1] != null && segment == null) {
            // Check if i'm on the line
            if (segments[1].isBetween(newBoundaries.getCenterFront())) {
                segment = segments[1];
                targetDirection = toCircles[1].getDirection();
                Log.d("SHIT3", "segment 1.1");
            } else {
                ratio = myMaxCircles[1].getRatio(oldBoundaries.getCenterFront(), newBoundaries.getCenterFront(), segments[1].getPointA());
                if (ratio <= 1.0) {
                    segment = segments[1];
                    targetDirection = toCircles[1].getDirection();
                    Log.d("SHIT3", "segment 1.2");
                }
            }
        }

        if (segment != null) {
            Log.d("SHIT", "Found Seg=" + segment.toString() + " ratio=" + ratio);
            if (ratio == 1.0) {
                newBoundaries = BoundariesManager.getHeadingBoundaries(
                        segment.getPointA(), segment.getPointB(), Wheels.STRAIGHT_WHEELS, oldBoundaries);
                if (segment.getLength() < 0.01) {
                    newBoundaries = newBoundaries.moveForward(segment.getLength());
                    if (targetDirection != Circle.Direction.STRAIGHT) {
                        newBoundaries.setWheelsAngle(toBoundaries.getMaxWheelsAngle(targetDirection));
                    }
                    // TODO: fix this if needed

                } else if (segment.getLength() < MathUtils.getFactorSec(newSpeed, timeToNextPos)) {
                    Log.d("SHIT7", "Reached PointB");
                    timeToNextPos = (long) ((segment.getLength() / MathUtils.getFactorSec(newSpeed, timeToNextPos)) * (double) timeToNextPos);
                }
            } else {
                CarPosition updatedOldPosition = CarPosition.getMovingPosition(oldBoundaries,
                        oldCarPosition.getSpeed(), oldCarPosition.getAcceleration(), (long) ((double) timeToNextPos * ratio));
                oldCarPosition.replace(updatedOldPosition);
                oldCarPosition = updatedOldPosition;
                newSpeed = oldCarPosition.generateNextSpeed();
                newAcc = accDec.getAcceleration(newSpeed, accDec.getMaxSpeed());
                newBoundaries = BoundariesManager.getHeadingBoundaries(
                        segment.getPointA(), segment.getPointB(), Wheels.STRAIGHT_WHEELS, oldBoundaries);
            }
            Log.d("SHIT", "NC=" + newBoundaries.getCenterFront().toString() + " PA=" + segment.getPointA());
//                segments[0].isBetween(newBoundaries.getCenterFront());
//                myMaxCircles = newBoundaries.getMaxTurningCircles();
//                segments[0] = TrigUtils.findOuterTangents(myMaxCircles[0], toCircles[0]);
//                segments[1] = TrigUtils.findOuterTangents(myMaxCircles[1], toCircles[1]);
//                segments[0].isBetween(newBoundaries.getCenterFront());
//                segments[1].isBetween(newBoundaries.getCenterFront());


            // Make sure I slow down to be max wheels turn when I hit the circle
            double distanceToTargetCircle = segment.getLength();
            double distanceToTargetSpeed = accDec.getDistanceToTargetSpeed(newSpeed, maxWheelsAngleSpeed);
            if (distanceToTargetSpeed > distanceToTargetCircle) {
                if (newSpeed > maxWheelsAngleSpeed) {
                    newAcc = accDec.getAcceleration(newSpeed, maxWheelsAngleSpeed);
                }
            }

            //for (int i = 0; i <= 7; i++) {
            oldCarPosition.setNext(CarPosition.getMovingPosition(newBoundaries, newSpeed, newAcc, timeToNextPos), true);
//                oldCarPosition = oldCarPosition.getNext();
//                newBoundaries = oldCarPosition.generateNextBoundaries(Wheels.STRAIGHT_WHEELS);
//                if (i == 7) {
//                    return oldCarPosition;
//                }
//            }
            return oldCarPosition.getNext();
        }

        if (myMaxCircles[0].getDirection() == toCircles[0].getDirection()) {
            newBoundaries.setWheelsAngle(newBoundaries.getMaxWheelsAngle(myMaxCircles[0].getDirection()));
        } else {
            newBoundaries.setWheelsAngle(newBoundaries.getMaxWheelsAngle(myMaxCircles[1].getDirection()));
        }

        double comfortableSpeed = speed.getComfortableSpeed(newBoundaries.getWheelsAngle());
        newAcc = accDec.getAcceleration(newSpeed, comfortableSpeed);

        oldCarPosition.setNext(CarPosition.getMovingPosition(newBoundaries, newSpeed, newAcc, timeToNextPos), true);
        return oldCarPosition.getNext();

        // Get the distance between the two max circles (3 options)
        // if the closer one is the same direction - use that
        // if the closer one is the opposite direction but the circles overlap use the circles where the radius don't overlap
        //return oldCarPosition;
    }
}
