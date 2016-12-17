package com.eightman.autov.Objects;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import com.eightman.autov.Interfaces.ICarPathListener;
import com.eightman.autov.Managers.DistanceManager;
import com.eightman.autov.Simulation.SimTime;
import com.eightman.autov.Utils.MathUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class CarPath {
    private final static String TAG = CarPath.class.getSimpleName();
    private final static int CURRENT_POS = 1;
    private final static int OTHER_POS = 2;

    final UUID carUUID;
    CarPosition currentPosition;
    List<ICarPathListener> carPathListeners = new LinkedList<>();

    public CarPath(@NonNull UUID carUUID, @NonNull CarPosition initialPosition) {
        this.carUUID = carUUID;
        currentPosition = initialPosition;
        currentPosition.setParentCarPath(this);
    }

    /*public synchronized boolean add(CarPosition position) throws Exception {
        if (position != null) {
            while (!currentPosition.getLast().setNext(position, true)) {
            }

            if (timeNextPosition == 0) {
                timeNextPosition = SimTime.getInstance().getTime() + currentPosition.getTimeToNextPosition();
            }

            return true;
        } else {
            return false;
        }
    }*/

    public synchronized int getSize() {
        return currentPosition.getLinkSize();
    }

    public synchronized CarPosition getCurrentPosition() {
        return currentPosition;
    }

    public synchronized PositionInfo moveToNextPosition() {
        if (currentPosition.getLinkSize() == 1) {
            return null;
        }
        CarPosition oldPosition = currentPosition;

        long timeToNextPosition;
        do {
            currentPosition = currentPosition.getNext();
            timeToNextPosition = currentPosition.getTimeToNextPosition();
        } while (timeToNextPosition == 0 && !currentPosition.isLast());

        try {
            currentPosition.setPrevious(null);
        } catch (Exception e) {
        }
        oldPosition.makeIsland(false, false);

        currentPosition.setAbsTime(SimTime.getInstance().getTime(), false);

        double adjustedSpeed = MathUtils.getSpeedToNextPosition(currentPosition, timeToNextPosition);
        return new PositionInfo(currentPosition, timeToNextPosition, adjustedSpeed);
    }

    public synchronized CarPosition getLastPosition() {
        return currentPosition.getLast();
    }

    public UUID getCarUUID() {
        return carUUID;
    }

    public synchronized void findDistances() {
        DistanceManager.getInstance().populateDistances(getCurrentPosition());
    }

    public synchronized boolean needToMove() {
        if (currentPosition.isParkingPosition()) {
            return !currentPosition.isLast();
        }

        if (currentPosition.isLast()) {
            return false;
        }

        long currentTime = SimTime.getInstance().getTime();
        boolean needToMove = (currentPosition.getNext().getAbsTime() <= currentTime);
//        Log.d(TAG, needToMove +
//                " : absTimeNP = " + currentPosition.getNext().getAbsTime() +
//                " : currentTime = " + currentTime);

        return needToMove;
    }

    public void removeCarPathListener(ICarPathListener carPathListener) {
        synchronized (carPathListener) {
            carPathListeners.remove(carPathListener);
        }
    }

    public void addCarPathListener(ICarPathListener carPathListener) {
        synchronized (carPathListener) {
            if (!carPathListeners.contains(carPathListener)) {
                carPathListeners.add(carPathListener);
            }
        }
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            synchronized (carPathListeners) {
                for (ICarPathListener carPathListener : carPathListeners) {
                    carPathListener.onPathChanged(CarPath.this, (msg.what == CURRENT_POS));
                }
            }
        }
    };

    public void onCarPositionChanged(CarPosition carPosition) {
        handler.sendEmptyMessage(carPosition == currentPosition ? CURRENT_POS : OTHER_POS);
    }

    public class PositionInfo {
        final CarPosition position;
        final long timeToNextPosition;
        final double adjustedSpeed;

        public PositionInfo(CarPosition position, long timeToNextPosition, double adjustedSpeed) {
            this.position = position;
            this.timeToNextPosition = timeToNextPosition;
            this.adjustedSpeed = adjustedSpeed;
        }

        public CarPosition getPosition() {
            return position;
        }

        public long getTimeToNextPosition() {
            return timeToNextPosition;
        }

        public double getAdjustedSpeed() {
            return adjustedSpeed;
        }

        @Override
        public String toString() {
            return "{timeToNextPosition = " + timeToNextPosition + "," +
                    "adjustedSpeed = " + adjustedSpeed + "," +
                    "position = " + position.toString() + "}";
        }
    }
}
