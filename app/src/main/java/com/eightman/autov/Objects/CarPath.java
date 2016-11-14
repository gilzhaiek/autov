package com.eightman.autov.Objects;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.eightman.autov.Interfaces.ICarPathListener;

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

    public CarPath(@NonNull UUID carUUID, @NonNull CarPosition initialPosition) {
        this.carUUID = carUUID;
        currentPosition = initialPosition;
        currentPosition.setParentCarPath(this);
    }

    public synchronized boolean add(CarPosition position) {
        if (position != null) {
            while (!currentPosition.getLast().setNext(position, true)) {
            }
            return true;
        } else {
            return false;
        }
    }

    public synchronized int size() {
        return currentPosition.getLinkSize();
    }

    public synchronized CarPosition getCurrentPosition() {
        return currentPosition;
    }

    public synchronized CarPosition moveToNextPosition() {
        if (currentPosition.getLinkSize() == 1) {
            return null;
        }

        CarPosition oldPosition = currentPosition;
        currentPosition = currentPosition.getNext();
        currentPosition.setPrevious(null);
        oldPosition.makeIsland(false, false);

        return currentPosition;
    }

    public synchronized CarPosition getLastPosition() {
        return currentPosition.getLast();
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
}
