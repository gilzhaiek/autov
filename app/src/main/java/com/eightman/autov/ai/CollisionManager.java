package com.eightman.autov.ai;

import com.eightman.autov.Configurations.Constants;
import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Hardware.Boundaries;
import com.eightman.autov.Objects.CarPosition;
import com.eightman.autov.Objects.MyCar;
import com.eightman.autov.Utils.TrigUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by gilzhaiek on 2016-11-11.
 */

public class CollisionManager {
    List<MyCar> cars = new LinkedList<>();

    private static class InternalSingleton {
        private static CollisionManager singleton = new CollisionManager();
    }

    public static CollisionManager getInstance() {
        return InternalSingleton.singleton;
    }

    public void addCar(MyCar car) {
        synchronized (cars) {
            cars.add(car);
        }
    }

    public boolean isInCollision(MyCar car) {
        CarPosition.Final carPosition = car.getCarPosition().getPosition();
        Boundaries carBoundaries = carPosition.getCollisionZone();
        if(carBoundaries == null) {
            carBoundaries = CollisionDetector.getHeadingBoundaries(carPosition, Constants.ONE_SECOND);
            carPosition.setCollisionZone(carBoundaries);
        }
        for (int i = 0; i < cars.size(); i++) {
            if (!car.getUuid().equals(cars.get(i).getUuid())) {
                carPosition = cars.get(i).getCarPosition().getPosition();
                Boundaries otherBoundaries = carPosition.getCollisionZone();
                if (otherBoundaries == null) {
                    otherBoundaries = CollisionDetector.getHeadingBoundaries(carPosition, Constants.ONE_SECOND);
                    carPosition.setCollisionZone(otherBoundaries);
                }
                if (Math.abs(
                        carBoundaries.getCenter().getX() - otherBoundaries.getCenter().getX())
                        <= SimConfig.MAX_COLLISION_VALIDATION) {
                    if (TrigUtils.isColliding(carBoundaries, otherBoundaries)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
