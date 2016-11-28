package com.eightman.autov.ai;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Hardware.Boundaries;
import com.eightman.autov.Objects.CarPosition;
import com.eightman.autov.Objects.Collision;
import com.eightman.autov.Objects.MyCar;
import com.eightman.autov.Utils.CollisionUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by gilzhaiek on 2016-11-11.
 */

public class CollisionManager {
    static String TAG = CollisionManager.class.getSimpleName();

    long largestTimeResolution = MyCar.getTimeResolution(); // TODO: Change

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

    private List<CarPosition> updateCarPositions(final List<CarPosition> carPositions, long absTime) {
        final List<CarPosition> retCarPositions = new LinkedList<>();

        for (CarPosition carPosition : carPositions) {
            boolean doInsert = true;
            // MY   - OTHER
            // 1000 - 500 = 500 > 1000 -> insert
            // 1000 - 1500 = -500 > 1000 -> insert
            // 3000 - 500 = 2500 > 1000 -> move if not last
            // 1000 - 3000 = -2000 > wait another 1000
            while (absTime - carPosition.getAbsTime() > largestTimeResolution && doInsert != false) { // need to move forward
                if (!carPosition.isLast()) {
                    carPosition = carPosition.getNext();
                } else {
                    doInsert = false;
                }
            }

            if (doInsert) {
                retCarPositions.add(carPosition);
            }
        }

        return retCarPositions;
    }

    public Collision getFirstCollision(CarPosition myCarPosition) {
        List<CarPosition> otherCarPositions = new LinkedList<>();

        for (MyCar car : cars) {
            if (myCarPosition.getCarUUID().equals(car.getUuid())) {
                continue;
            }

            otherCarPositions.add(car.getCarPosition());
        }

        if(otherCarPositions.isEmpty()) {
            return null;
        }

        do {
            otherCarPositions = updateCarPositions(otherCarPositions, myCarPosition.getAbsTime());
            if(otherCarPositions.isEmpty()) {
                return null;
            }

            Boundaries myCarBoundaries = myCarPosition.getCollisionZone();
            for (int i = 0; i < otherCarPositions.size(); ) {
                CarPosition otherCarPosition = otherCarPositions.get(i);
                if (otherCarPosition.getAbsTime() - myCarPosition.getAbsTime() < largestTimeResolution) {
                    Boundaries otherBoundaries = otherCarPosition.getCollisionZone();
                    if (Math.abs(myCarBoundaries.getCenter().getX() - otherBoundaries.getCenter().getX())
                            <= SimConfig.MAX_COLLISION_VALIDATION) {
                        CollisionUtils.Side side = CollisionUtils.isColliding(myCarBoundaries, otherBoundaries);
                        if (side != CollisionUtils.Side.NONE) {
                            return new Collision(
                                    myCarPosition, myCarPosition.getCarUUID(),
                                    otherCarPosition, otherCarPosition.getCarUUID(),
                                    side);
                        }
                    }
                }

                if (!otherCarPosition.isLast() &&
                        otherCarPosition.getNext().getAbsTime() - myCarPosition.getAbsTime() < largestTimeResolution) {
                    otherCarPositions.set(i, otherCarPosition.getNext());
                } else {
                    i++;
                }
            }

            if(myCarPosition.isLast()) {
                break;
            }
            myCarPosition = myCarPosition.getNext();
        } while(true);

        return null;
    }

}
