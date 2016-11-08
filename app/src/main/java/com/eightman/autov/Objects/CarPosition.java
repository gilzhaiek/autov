package com.eightman.autov.Objects;

import com.eightman.autov.Hardware.Boundaries;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class CarPosition {
    Boundaries boundaries;
    double speed;
    long timeOffset;


    Object lock = new Object();

    public CarPosition(CarPosition.Final position) {
        setPosition(position);
    }

    public CarPosition(Boundaries boundaries, double speed, long timeOffset) {
        setPosition(boundaries, speed, timeOffset);
    }

    public CarPosition.Final getPosition() {
        synchronized (lock) {
            return new CarPosition.Final(boundaries, speed, timeOffset);
        }
    }

    public void setPosition(CarPosition.Final position) {
        setPosition(position.getBoundaries(), position.getSpeed(), position.getTimeOffset());
    }

    public void setPosition(Boundaries boundaries, double speed, long timeOffset) {
        synchronized (lock) {
            this.boundaries = boundaries;
            this.speed = speed;
            this.timeOffset = timeOffset;
        }
    }

    public class Final {
        final Boundaries boundaries;
        final double speed;
        final long timeOffset;

        public Final(Boundaries boundaries, double speed, long timeOffset) {
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
