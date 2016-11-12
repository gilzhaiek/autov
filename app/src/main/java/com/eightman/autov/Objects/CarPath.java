package com.eightman.autov.Objects;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class CarPath {
    Object lock = new Object();

    final UUID carUUID;
    final List<CarPosition.Final> path = new LinkedList<>();

    public CarPath(UUID carUUID, CarPosition.Final initialPosition) {
        this.carUUID = carUUID;
        path.add(initialPosition);
    }

    public boolean add(List<CarPosition.Final> path, boolean firstPositionIsLast) {
        synchronized (lock) {
            if (firstPositionIsLast && this.path.size() > 0) {
                if (peekLastPosition() != path.get(0)) {
                    return false;
                }
            }
            for (CarPosition.Final position : path) {
                if (firstPositionIsLast) {
                    firstPositionIsLast = false;
                    continue;
                }
                this.path.add(position);
            }

            return true;
        }
    }

    public long getDeltaTime(int fromIndex, int toIndex) {
        long deltaTime = 0;
        while(fromIndex < toIndex) {
            fromIndex++;
            CarPosition.Final nextPosition = getPosition(fromIndex);
            if(nextPosition == null) {
                break;
            }
            deltaTime += nextPosition.getTimeOffset();
        }

        return deltaTime;
    }

    public CarPosition.Final getPosition(int index) {
        synchronized (lock) {
            if (index >= size()) {
                return null;
            }
            return path.get(index);
        }
    }

    public int size() {
        return path.size();
    }

    public CarPosition.Final popFirstPosition() {
        synchronized (lock) {
            if (path.size() > 0) {
                return path.remove(0);
            } else {
                return null;
            }
        }
    }

    public CarPosition.Final peekFirstPosition() {
        synchronized (lock) {
            if (path.size() > 0) {
                return path.get(0);
            } else {
                return null;
            }
        }
    }

    public CarPosition.Final peekLastPosition() {
        synchronized (lock) {
            if (path.size() > 0) {
                return path.get(path.size() - 1);
            } else {
                return null;
            }
        }
    }
}
