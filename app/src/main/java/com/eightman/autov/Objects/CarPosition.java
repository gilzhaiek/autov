package com.eightman.autov.Objects;

import com.eightman.autov.Hardware.Boundaries;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class CarPosition {
    Boundaries boundaries;
    Double speed;

    Object lock = new Object();

    public CarPosition(CarPosition.Final position) {
        setPosition(position);
    }

    public CarPosition(Boundaries boundaries, double speed) {
        setPosition(boundaries, speed);
    }

    public CarPosition.Final getPosition() {
        synchronized (lock) {
            return new CarPosition.Final(boundaries, speed);
        }
    }

    public void setPosition(CarPosition.Final position) {
        setPosition(position.boundaries, position.speed);
    }

    public void setPosition(Boundaries boundaries, double speed) {
        synchronized (lock) {
            this.boundaries = boundaries;
            this.speed = speed;
        }
    }

    public class Final {
        final Boundaries boundaries;
        final double speed;

        public Final(Boundaries boundaries, double speed) {
            this.boundaries = boundaries;
            this.speed = speed;
        }

        public Boundaries getBoundaries() {
            return boundaries;
        }

        public double getSpeed() {
            return speed;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CarPosition.Final)) {
                return false;
            }

            CarPosition.Final other = (CarPosition.Final)obj;

            return other.getBoundaries().equals(boundaries) && other.getSpeed() == speed;
        }
    }
}
