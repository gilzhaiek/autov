package com.eightman.autov.Objects;

import com.eightman.autov.Hardware.Boundaries;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class CarPosition {
    Boundaries boundaries;
    Double speed;
    Double direction; // 0 is north.  Clock wise 360.0

    Object lock = new Object();

    public CarPosition(CarPosition.Final position) {
        setPosition(position);
    }

    public CarPosition(Boundaries boundaries, double speed, double direction) {
        setPosition(boundaries, speed, direction);
    }

    public CarPosition.Final getPosition() {
        synchronized (lock) {
            return new CarPosition.Final(boundaries, speed, direction);
        }
    }

    public void setPosition(CarPosition.Final position) {
        setPosition(position.boundaries, position.speed, position.direction);
    }

    public void setPosition(Boundaries boundaries, double speed, double direction) {
        synchronized (lock) {
            this.boundaries = boundaries;
            this.speed = speed;
            this.direction = direction;
        }
    }

    public class Final {
        final Boundaries boundaries;
        final double speed;
        final double direction; // 0 is north.  Clock wise 360.0

        public Final(Boundaries boundaries, double speed, double direction) {
            this.boundaries = boundaries;
            this.speed = speed;
            this.direction = direction;
        }

        public Boundaries getBoundaries() {
            return boundaries;
        }

        public double getSpeed() {
            return speed;
        }

        public double getDirection() {
            return direction;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CarPosition.Final)) {
                return false;
            }

            CarPosition.Final other = (CarPosition.Final)obj;

            return other.getBoundaries().equals(boundaries) &&
                    other.getSpeed() == speed &&
                    other.getDirection() == direction;
        }
    }
}
