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
import com.eightman.autov.Simulation.Drawings.DrawingUtils;
import com.eightman.autov.Simulation.Interfaces.IRandomPathMaker;
import com.eightman.autov.Utils.MathUtils;

import java.util.Random;

/**
 * Created by gilzhaiek on 2016-12-27.
 */

public class RandomCirclePathMaker implements IRandomPathMaker {
    static Random random = new Random();

    @Override
    public boolean generatePath(CarPath carPath, CarCharacteristics carCharacteristics) throws Exception {
        XY center = getRandomPoint();
        double randomAngle = MathUtils.getRandomDouble(0, 360);

        Boundaries toBoundaries = BoundariesManager.getBoundariesRotated(center,
                carCharacteristics.getWidth(), carCharacteristics.getWidth(), randomAngle);

        Circle[] toCircles = Circle.getCircles(toBoundaries.getLeftSegment(), toBoundaries.getRightSegment(), 0);

        CarPosition carPosition = carPath.getCurrentPosition();
        while (!carPosition.getBoundaries().equals(toBoundaries)) {
            carPosition.setNext(moveCloser(carPosition, toBoundaries, toCircles), true);
            carPosition = carPosition.getNext();
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

    private CarPosition moveCloser(CarPosition carPosition, Boundaries toBoundaries, Circle[] toCircles) {
        double speed = 0.0;
        double acceleration = 0.0;
        double wheelsAngle = 0.0;

        carPosition.getTurningCircles();

        Boundaries newBoundaries;

        if (newBoundaries.equals(toBoundaries) && speed == 0.0 && acceleration == 0.0) {
            return CarPosition.getRestedPosition(newBoundaries);
        } else {
            return carPosition.getMovingPosition(newBoundaries, speed, acceleration, wheelsAngle, SimConfig.PATH_RESOLUTION_MS);
        }
    }
}
