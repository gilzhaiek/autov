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
        XY center = getRandomPoint();
        double randomAngle = MathUtils.getRandomDouble(0, 360);

        CarPosition carPosition = carPath.getCurrentPosition();

        Boundaries toBoundaries = BoundariesManager.getBoundariesRotated(center,
                carCharacteristics.getWidth(), carCharacteristics.getWidth(), randomAngle,
                STRAIGHT_WHEELS, carPosition.getBoundaries().getMaxWheelsAngleAbs());
        toBoundaries = toBoundaries.addOffset(new XY(
                MathUtils.getRandomDouble(SimConfig.MIN_EDGE_METERS, SimConfig.MAX_EDGE_METERS),
                MathUtils.getRandomDouble(SimConfig.MIN_EDGE_METERS, SimConfig.MAX_EDGE_METERS)));

        Log.d("SHIT", "TARGET=" + toBoundaries.getCenterFront().toString());

        int i = 0;
        while (!carPosition.getBoundaries().equals(toBoundaries)) {
            carPosition = moveCloser(carCharacteristics.getAccDec(), carCharacteristics.getSpeed(), carPosition, toBoundaries);
            if (i++ > 100) {
                break;
            }
        }

        carPosition.setNext(CarPosition.getRestedPosition(toBoundaries), true);

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
        double newAcc;
        double maxWheelsAngleSpeed = speed.getComfortableSpeed(toBoundaries.getMaxWheelsAngleAbs());

        Boundaries oldBoundaries = oldCarPosition.getBoundaries();
        Log.d("SHIT", "CF=" + oldBoundaries.getCenterFront().toString() + " WA=" + oldBoundaries.getWheelsAngle());
        Boundaries newBoundaries = oldCarPosition.generateNextBoundaries(oldCarPosition.getWheelsAngle());
        double newSpeed = oldCarPosition.generateNextSpeed();

        // Make sure we slow down in time
        double distanceToStop = accDec.getStopDistance(newSpeed);
        double shortestDistance = BoundariesManager.getShortestDistance(newBoundaries, toBoundaries).first;
        Log.d("SHIT", "shortestDistance=" + shortestDistance);
        if (shortestDistance < distanceToStop) {
            Log.d("SHIT", "slowing down...");
            newAcc = accDec.getComfortableDec();
        } else {
            newAcc = accDec.getAcceleration(newSpeed);
        }

        // Did we hit the place?
        if (newBoundaries.equals(toBoundaries)) {
            Log.d("SHIT", "Hitting toBoundaries");
            if (newSpeed == 0.0 && newAcc == 0.0) {
                oldCarPosition.setNext(CarPosition.getRestedPosition(newBoundaries), true);
            } else {
                oldCarPosition.setNext(CarPosition.getMovingPosition(newBoundaries, newSpeed, newAcc, SimConfig.PATH_RESOLUTION_MS), true);
            }
            return oldCarPosition.getNext();
        }

        // TODO: Check if a point is on the direction of heading

        Circle[] toCircles = toBoundaries.getMaxTurningCircles();
        // Check if I am on the circle and on the same direction
        Circle turningCircle = newBoundaries.getTurningCircle();
        if (turningCircle != null && (turningCircle.equals(toCircles[0]) || turningCircle.equals(toCircles[1]))) {
            Log.d("SHIT", "We are on the same circle");

            if (newSpeed > maxWheelsAngleSpeed) {
                // We need to slow down
                newAcc = accDec.getComfortableDec();
            }
            oldCarPosition.setNext(CarPosition.getMovingPosition(newBoundaries, newSpeed, newAcc, SimConfig.PATH_RESOLUTION_MS), true);
            return oldCarPosition.getNext();
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
        double ratio = 1.0;
        LineSegment segment = null;
        if (segments[0] != null) {
            // Check if i'm on the line
            if (segments[0].isBetween(newBoundaries.getCenterFront())) {
                segment = segments[0];
            } else {
                ratio = myMaxCircles[0].getRatio(oldBoundaries.getCenterFront(), newBoundaries.getCenterFront(), segments[0].getPointA());
                if (ratio <= 1.0) {
                    segment = segments[0];
                }
            }
        }
        if (segments[1] != null && segment == null) {
            // Check if i'm on the line
            if (segments[1].isBetween(newBoundaries.getCenterFront())) {
                segment = segments[1];
            } else {
                ratio = myMaxCircles[1].getRatio(oldBoundaries.getCenterFront(), newBoundaries.getCenterFront(), segments[1].getPointA());
                if (ratio <= 1.0) {
                    segment = segments[1];
                }
            }
        }

        if (segment != null) {
            Log.d("SHIT", "Found Seg=" + segment.toString());
            if (ratio < 1.0) {
                CarPosition updatedOldPosition = CarPosition.getMovingPosition(oldBoundaries,
                        oldCarPosition.getSpeed(), oldCarPosition.getAcceleration(), (long) ((double) SimConfig.PATH_RESOLUTION_MS * ratio));
                oldCarPosition.replace(updatedOldPosition);
                oldCarPosition = updatedOldPosition;
                newSpeed = oldCarPosition.generateNextSpeed();
                newBoundaries = BoundariesManager.getHeadingBoundaries(
                        segment.getPointA(), segment.getPointB(),
                        oldBoundaries.getWidth(), oldBoundaries.getLength(),
                        Wheels.STRAIGHT_WHEELS, oldBoundaries.getMaxWheelsAngleAbs());
            } else {
                newBoundaries = BoundariesManager.getHeadingBoundaries(
                        newBoundaries.getCenterFront(), segment.getPointB(),
                        oldBoundaries.getWidth(), oldBoundaries.getLength(),
                        Wheels.STRAIGHT_WHEELS, oldBoundaries.getMaxWheelsAngleAbs());
            }


            // Make sure I slow down to be max wheels turn when I hit the circle
            double distanceToTargetCircle = segment.getLength();
            double distanceToTargetSpeed = accDec.getDistanceToTargetSpeed(newSpeed, maxWheelsAngleSpeed);
            if (distanceToTargetCircle <= distanceToTargetSpeed) {
                newAcc = accDec.getComfortableDec();
            } else {
                newAcc = accDec.getAcceleration(newSpeed);
            }

            oldCarPosition.setNext(CarPosition.getMovingPosition(newBoundaries, newSpeed, newAcc, SimConfig.PATH_RESOLUTION_MS), true);
            return oldCarPosition.getNext();
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

        oldCarPosition.setNext(CarPosition.getMovingPosition(newBoundaries, newSpeed, newAcc, SimConfig.PATH_RESOLUTION_MS), true);
        return oldCarPosition.getNext();

        // Get the distance between the two max circles (3 options)
        // if the closer one is the same direction - use that
        // if the closer one is the opposite direction but the circles overlap use the circles where the radius don't overlap
        //return oldCarPosition;
    }
}
