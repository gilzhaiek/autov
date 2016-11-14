package com.eightman.autov.Objects;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.eightman.autov.Interfaces.ICarPathListener;
import com.eightman.autov.Utils.MathUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class CarPath {
    private final static int CURRENT_POS = 1;
    private final static int OTHER_POS = 2;

    final UUID carUUID;
    CarPosition currentPosition;
    List<ICarPathListener> carPathListeners = new LinkedList<>();
    long timeNextPosition = 0;

    public CarPath(@NonNull UUID carUUID, @NonNull CarPosition initialPosition) {
        this.carUUID = carUUID;
        currentPosition = initialPosition;
        currentPosition.setParentCarPath(this);
    }

    public synchronized boolean add(CarPosition position) {
        if (position != null) {
            while (!currentPosition.getLast().setNext(position, true)) {
            }

            if (timeNextPosition == 0) {
                timeNextPosition = System.currentTimeMillis() + currentPosition.getTimeToNextPosition();
            }

            return true;
        } else {
            return false;
        }
    }

    public synchronized int getSize() {
        return currentPosition.getLinkSize();
    }

    public synchronized CarPosition getCurrentPosition() {
        return currentPosition;
    }

    public synchronized PositionInfo moveToNextPosition() {
        if (currentPosition.getLinkSize() == 1) {
            timeNextPosition = 0;
            return null;
        }

        CarPosition oldPosition = currentPosition;
        currentPosition = currentPosition.getNext();
        currentPosition.setPrevious(null);
        oldPosition.makeIsland(false, false);

        long timeToNextPosition = currentPosition.getTimeToNextPosition();
        long currentTime = System.currentTimeMillis();

        if(timeNextPosition > currentTime) { // Too Early: 1250 > 1200
            timeNextPosition = timeNextPosition - currentTime + timeToNextPosition;
        } else { // Too Late: 1250 < 1300
            timeNextPosition = currentTime - timeNextPosition + timeToNextPosition;
        }

        double adjustedSpeed = MathUtils.getSpeedToNextPosition(currentPosition, timeToNextPosition);

        return new PositionInfo(currentPosition, timeNextPosition, timeToNextPosition, adjustedSpeed);
    }

    public synchronized CarPosition getLastPosition() {
        return currentPosition.getLast();
    }

    public synchronized long getTimeNextPosition() {
        return timeNextPosition;
    }

    public synchronized boolean needToMove() {
        if (timeNextPosition == 0 && getSize() <= 1) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        boolean needToMove = (timeNextPosition <= currentTime);
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

    Handler handler = new Handler() {
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
        final long timeNextPosition;
        final long timeToNextPosition;
        final double adjustedSpeed;

        public PositionInfo(CarPosition position, long timeNextPosition, long timeToNextPosition, double adjustedSpeed) {
            this.position = position;
            this.timeNextPosition = timeNextPosition;
            this.timeToNextPosition = timeToNextPosition;
            this.adjustedSpeed = adjustedSpeed;
        }

        public CarPosition getPosition() {
            return position;
        }

        public long getTimeNextPosition() {
            return timeNextPosition;
        }

        public long getTimeToNextPosition() {
            return timeToNextPosition;
        }

        public double getAdjustedSpeed() {
            return adjustedSpeed;
        }
    }
}
