package com.eightman.autov.Objects;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class CarLocation {
    Double x;
    Double y;
    Double speed;
    Double direction; // 0 is north.  Clock wise 360.0

    Object lock = new Object();

    public CarLocation(CarLocation.Final location) {
        setLocation(location);
    }

    public CarLocation(Double x, Double y, Double speed, Double direction) {
        setLocation(x, y, speed, direction);
    }

    public CarLocation.Final getLocation() {
        synchronized (lock) {
            return new CarLocation.Final(x, y, speed, direction);
        }
    }

    public void setLocation(CarLocation.Final location) {
        setLocation(location.x, location.y, location.speed, location.direction);
    }

    public void setLocation(Double x, Double y, Double speed, Double direction) {
        synchronized (lock) {
            this.x = x;
            this.y = y;
            this.speed = speed;
            this.direction = direction;
        }
    }

    public Double getDistanceFrom(CarLocation otherCar) {
        synchronized (lock) {
            return Math.sqrt(Math.pow(x - otherCar.x, 2) + Math.pow(y - otherCar.y, 2));
        }
    }

    public class Final {
        public final Double x;
        public final Double y;
        public final Double speed;
        public final Double direction; // 0 is north.  Clock wise 360.0

        public Final(Double x, Double y, Double speed, Double direction) {
            this.x = x;
            this.y = y;
            this.speed = speed;
            this.direction = direction;
        }
    }
}
