package com.eightman.autov.Simulation.DataMaker;

import android.util.Pair;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Managers.BoundariesManager;
import com.eightman.autov.Objects.CarCharacteristics;
import com.eightman.autov.Objects.CarPath;
import com.eightman.autov.Objects.CarPosition;
import com.eightman.autov.Objects.Geom.Boundaries;
import com.eightman.autov.Objects.Geom.Circle;
import com.eightman.autov.Objects.Geom.XY;
import com.eightman.autov.Objects.Physical.AccDec;
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

        Boundaries toBoundaries = BoundariesManager.getBoundariesRotated(center,
                carCharacteristics.getWidth(), carCharacteristics.getWidth(), randomAngle);

        Circle circles[] = TrigUtils.getMaxTurningCircles(toBoundaries, carCharacteristics.getWheels());

        CarPosition carPosition = carPath.getCurrentPosition();
        //while (!carPosition.getBoundaries().equals(toBoundaries)) {
        for (int i = 0; i < 10; i++) {
            CarPosition newCarPosition = moveCloser(carCharacteristics.getAccDec(), carPosition, toBoundaries, circles);
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

    private CarPosition moveCloser(AccDec accDec, CarPosition oldCarPosition, Boundaries toBoundaries,
                                   Circle[] toCircles) {
        Double acceleration = null;
        Double wheelsAngle = null;
        double speed = 0.0;

        Boundaries newBoundaries = oldCarPosition.generateNextBoundaries();
        double newSpeed = oldCarPosition.generateNextSpeed();

        // Make sure we slow down in time
        double distanceToStop = accDec.getStopDistance(oldCarPosition.getSpeed());
        double shortestDistance = BoundariesManager.getShortestDistance(oldCarPosition.getBoundaries(), toBoundaries).first;
        if (shortestDistance < distanceToStop) {
            acceleration = accDec.getComfortableDec();
        }


        // Check if I am on the circle and on the same direction


        // Check if my direction is intersecting with any of the two circles and reaching the same direction
        // Make sure I slow down to be max wheels turn when I hit the circle

        // Get the distance between the two max circles (3 options)
        // if the closer one is the same direction - use that
        // if the closer one is the opposite direction but the circles overlap use the circles where the radius don't overlap

        if (newBoundaries.equals(toBoundaries) && speed == 0.0 && acceleration == 0.0) {
            return CarPosition.getRestedPosition(newBoundaries);
        } else {
            return CarPosition.getMovingPosition(newBoundaries, speed, acceleration, wheelsAngle, SimConfig.PATH_RESOLUTION_MS);
        }
    }
}
