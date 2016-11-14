package com.eightman.autov.ai;

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
        CarPosition carPosition = car.getCarPosition();
        Boundaries carBoundaries = carPosition.getCollisionZone();

        for (int i = 0; i < cars.size(); i++) {
            if (!car.getUuid().equals(cars.get(i).getUuid())) {
                carPosition = cars.get(i).getCarPosition();
                Boundaries otherBoundaries = carPosition.getCollisionZone();
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
