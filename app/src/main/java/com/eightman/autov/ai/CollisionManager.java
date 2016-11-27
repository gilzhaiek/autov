package com.eightman.autov.ai;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Hardware.Boundaries;
import com.eightman.autov.Objects.CarPosition;
import com.eightman.autov.Objects.Collision;
import com.eightman.autov.Objects.MyCar;
import com.eightman.autov.Utils.CollisionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

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

    public void clear() {
        synchronized (cars) {
            cars.clear();
        }
    }

    private boolean isInZone(MyCar car, CollisionUtils.Zone zone) {
        CarPosition carPosition = car.getCarPosition();
        Boundaries carBoundaries = (zone == CollisionUtils.Zone.COLLISION_ZONE) ?
                carPosition.getCollisionZone() : carPosition.getSafeZone();

        for (int i = 0; i < cars.size(); i++) {
            if (!car.getUuid().equals(cars.get(i).getUuid())) {
                carPosition = cars.get(i).getCarPosition();
                Boundaries otherBoundaries = (zone == CollisionUtils.Zone.COLLISION_ZONE) ?
                        carPosition.getCollisionZone() : carPosition.getSafeZone();
                if (Math.abs(
                        carBoundaries.getCenter().getX() - otherBoundaries.getCenter().getX())
                        <= SimConfig.MAX_COLLISION_VALIDATION) {
                    if (CollisionUtils.isColliding(carBoundaries, otherBoundaries) != CollisionUtils.Side.NONE) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isInCollision(MyCar car) {
        return isInZone(car, CollisionUtils.Zone.COLLISION_ZONE);
    }

    public boolean isInSafeZone(MyCar car) {
        return isInZone(car, CollisionUtils.Zone.SAFE_ZONE);
    }

    public Collision getFirstCollision(CarPosition carPosition, UUID carUUID) {
        Boundaries carBoundaries = carPosition.getCollisionZone();
        for (int i = 0; i < cars.size(); i++) {
            if (!carUUID.equals(cars.get(i).getUuid())) {
                CarPosition otherCarPosition = cars.get(i).getCarPosition();
                do {
                    Boundaries otherBoundaries = otherCarPosition.getCollisionZone();
                    if (Math.abs(
                            carBoundaries.getCenter().getX() - otherBoundaries.getCenter().getX())
                            <= SimConfig.MAX_COLLISION_VALIDATION) {
                        CollisionUtils.Side side = CollisionUtils.isColliding(carBoundaries, otherBoundaries);
                        if (side != CollisionUtils.Side.NONE) {
                            return new Collision(
                                    carPosition, carUUID,
                                    otherCarPosition, cars.get(i).getUuid(),
                                    side);
                        }
                    }
                    otherCarPosition = otherCarPosition.getNext();
                } while (!otherCarPosition.isLast());
            }
        }

        return null;
    }

    public Collision getFirstCollision(MyCar car) {
        CarPosition carPosition = car.getCarPosition();

        do {
            Collision collision = getFirstCollision(carPosition, car.getUuid());
            if (collision != null) {
                return collision;
            }
            carPosition = carPosition.getNext();
        } while (!carPosition.isLast());

        return null;
    }

}
