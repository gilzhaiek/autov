package com.eightman.autov.Managers;

import android.util.Pair;

import com.eightman.autov.Objects.CarPosition;
import com.eightman.autov.Objects.MyCar;
import com.eightman.autov.Objects.ObjectDistanceInfo;
import com.eightman.autov.Utils.LineSegment;
import com.eightman.autov.Utils.TrigUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by gilzhaiek on 2016-12-06.
 */

public class DistanceManager {
    static String TAG = DistanceManager.class.getSimpleName();

    long largestTimeResolution = MyCar.getTimeResolution(); // TODO: Change

    List<MyCar> cars = new LinkedList<>();

    private static class InternalSingleton {
        private static DistanceManager singleton = new DistanceManager();
    }

    public static DistanceManager getInstance() {
        return DistanceManager.InternalSingleton.singleton;
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

    public void populateDistances(CarPosition myCarPosition) {
        List<CarPosition> otherCarPositions = new LinkedList<>();

        synchronized (cars) {
            for (MyCar car : cars) {
                if (myCarPosition.getCarUUID().equals(car.getUuid())) {
                    continue;
                }

                otherCarPositions.add(car.getCarPosition());
            }
        }

        if(otherCarPositions.isEmpty()) {
            return;
        }

        do {
            otherCarPositions = updateCarPositions(otherCarPositions, myCarPosition.getAbsTime());
            if(otherCarPositions.isEmpty()) {
                return;
            }

            List<ObjectDistanceInfo> carDistancesInfo = myCarPosition.getCarDistancesInfo();
            carDistancesInfo.clear();
            for (int i = 0; i < otherCarPositions.size(); ) {
                CarPosition otherCarPosition = otherCarPositions.get(i);
                if (otherCarPosition.getAbsTime() - myCarPosition.getAbsTime() < largestTimeResolution) {
                    Pair<Double, LineSegment> o2oLine = TrigUtils.getShortestDistance(
                            myCarPosition.getBoundaries(), otherCarPosition.getBoundaries());
                    carDistancesInfo.add(new ObjectDistanceInfo(
                            o2oLine.first,
                            o2oLine.second,
                            myCarPosition.getCarUUID(),
                            otherCarPosition.getCarUUID()));
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
    }
}
