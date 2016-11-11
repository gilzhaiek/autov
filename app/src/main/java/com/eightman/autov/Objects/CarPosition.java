package com.eightman.autov.Objects;

import com.eightman.autov.Configurations.Global;
import com.eightman.autov.Hardware.Boundaries;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class CarPosition {
    long id;
    Boundaries boundaries;
    double speed;
    long timeOffset;
    // TODO: Add direction - front or back
    // TODO: Add wheels angles

    Object lock = new Object();

    public CarPosition(CarPosition.Final position) {
        setPosition(position);
    }

    public CarPosition(Boundaries boundaries, double speed, long timeOffset) {
        setPosition(Global.generateId(), boundaries, speed, timeOffset);
    }

    public CarPosition(long id, Boundaries boundaries, double speed, long timeOffset) {
        setPosition(id, boundaries, speed, timeOffset);
    }

    public CarPosition.Final getPosition() {
        synchronized (lock) {
            return new CarPosition.Final(id, boundaries, speed, timeOffset);
        }
    }

    public void setPosition(CarPosition.Final position) {
        setPosition(
                position.getId(),
                position.getBoundaries(),
                position.getSpeed(),
                position.getTimeOffset());
    }

    public void setPosition(long id, Boundaries boundaries, double speed, long timeOffset) {
        synchronized (lock) {
            this.id = id;
            this.boundaries = boundaries;
            this.speed = speed;
            this.timeOffset = timeOffset;
        }
    }

    public class Final {
        final long id;
        final Boundaries boundaries;
        final double speed;
        final long timeOffset;

        public Final(long id, Boundaries boundaries, double speed, long timeOffset) {
            this.id = id;
            this.boundaries = boundaries;
            this.speed = speed;
            this.timeOffset = timeOffset;
        }

        public Boundaries getBoundaries() {
            return boundaries;
        }

        public double getSpeed() {
            return speed;
        }

        public long getTimeOffset() {
            return timeOffset;
        }

        public long getId() {
            return id;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CarPosition.Final)) {
                return false;
            }

            CarPosition.Final other = (CarPosition.Final) obj;

            return other.getBoundaries().equals(boundaries) &&
                    other.getSpeed() == speed &&
                    other.getTimeOffset() == timeOffset;
        }
    }
}
