package com.eightman.autov.Objects;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class CarPath {
    Object lock = new Object();

    List<CarPosition.Final> path = new LinkedList<>();

    public CarPath(CarPosition.Final initialPosition) {
        path.add(initialPosition);
    }

    public boolean add(List<CarPosition.Final> path, boolean firstPositionIsLast) {
        synchronized (lock) {
            if(firstPositionIsLast && this.path.size() > 0) {
                if(getLastPosition() != path.get(0)) {
                    return false;
                }
            }
            for (CarPosition.Final position : path) {
                if(firstPositionIsLast) {
                    firstPositionIsLast = false;
                    continue;
                }
                this.path.add(position);
            }

            return true;
        }
    }

    public CarPosition.Final pop() {
        synchronized (lock) {
            if (path.size() > 0) {
                return path.remove(0);
            } else {
                return null;
            }
        }
    }

    public CarPosition.Final peek() {
        synchronized (lock) {
            if (path.size() > 0) {
                return path.get(0);
            } else {
                return null;
            }
        }
    }

    public CarPosition.Final getLastPosition() {
        synchronized (lock) {
            if (path.size() > 0) {
                return path.get(path.size() - 1);
            } else {
                return null;
            }
        }
    }
}
