package com.eightman.autov.Objects;

import android.support.annotation.NonNull;

import com.eightman.autov.Configurations.Global;
import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Objects.Geom.Boundaries;
import com.eightman.autov.Objects.Geom.Circle;
import com.eightman.autov.Utils.MathUtils;
import com.eightman.autov.Utils.TrigUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class CarPosition {
    private final static String TAG = CarPosition.class.getSimpleName();
    private final long id;
    private final Boundaries boundaries;
    private final double speed;
    private final double acceleration;  // m/s^2
    private final double wheelsAngle; // Positive: Facing Right, Negative: Facing Left
    private final long timeToNextPosition;
    private final List<ObjectDistanceInfo> carDistancesInfo;

    private CarPosition next;
    private CarPosition previous;
    private CarPosition last;
    private int linkSize = 1;
    private CarPath parentCarPath;
    private long absTime = 0;
    private Circle turningCircle;

    // TODO: Add direction - front or back

    private CarPosition(Boundaries boundaries) {
        this.id = Global.generateId();
        this.boundaries = boundaries;
        this.speed = 0;
        this.acceleration = 0;
        this.wheelsAngle = 0;
        this.timeToNextPosition = 0;
        this.last = this;
        this.carDistancesInfo = new ArrayList<>();
    }

    private CarPosition(
            @NonNull Boundaries boundaries,
            double speed,
            double acceleration,
            double wheelsAngle,
            long timeToNextPosition) {
        this.id = Global.generateId();
        this.boundaries = boundaries;
        this.speed = speed;
        this.acceleration = acceleration;
        this.wheelsAngle = wheelsAngle;
        this.timeToNextPosition = timeToNextPosition;
        this.last = this;
        this.carDistancesInfo = new ArrayList<>();
    }

    public double generateNextSpeed() {
        return this.speed + MathUtils.getFactorSec(this.acceleration, this.timeToNextPosition);
    }

    public Boundaries generateNextBoundaries() {
        Circle turningCircle = getTurningCircle();
        // move along the turning circle
        boundaries.
    }

    public static CarPosition getRestedPosition(Boundaries boundaries) {
        return new CarPosition(boundaries);
    }

    public static CarPosition getMovingPosition(
            Boundaries boundaries,
            double speed,
            double acceleration,
            double wheelsAngle,
            long timeToNextPosition) {
        return new CarPosition(boundaries, speed, acceleration, wheelsAngle, timeToNextPosition);
    }

    public static CarPosition copy(CarPosition carPosition) {
        return new CarPosition(
                carPosition.getBoundaries(),
                carPosition.getSpeed(),
                carPosition.getAcceleration(),
                carPosition.getWheelsAngle(),
                carPosition.getTimeToNextPosition());
    }

    public synchronized int getLinkSize() {
        return linkSize;
    }

    private synchronized void setLinkSize(int linkSize) {
        if (this.linkSize == linkSize) {
            return;
        }
        this.linkSize = linkSize;
        if (this.previous != null) {
            this.previous.setLinkSize(this.linkSize + 1);
        }
    }

    public synchronized CarPosition getLast() {
        return last;
    }

    private synchronized void setLast(CarPosition last) {
        if (this != last && this.last != last) {
            if (last != null) {
                last.setParentCarPath(this.parentCarPath);
            }
            this.last = last;
        }
    }

    public synchronized boolean setNext(CarPosition next, boolean mustBeLast) throws Exception {
        if (next == null) {
            this.last = this;
            this.next = null;
            linkSize = 1;
        } else {
            if (this == next) {
                throw new Exception("Infinite Linked List");
            }
            if (this.next != null) {
                if (mustBeLast) {
                    return false;
                }
                if (this.next != next) { // Detaching Next
                    this.next.setParentCarPath(null);
                    this.next.setPrevious(null);
                }
            }
            this.next = next;

            linkSize = 1 + this.next.getLinkSize();
            this.last = this.next.getLast();
            this.next.setParentCarPath(this.parentCarPath);
            this.next.setPrevious(this);
        }

        if (this.previous != null) {
            this.previous.setLast(this.last);
            this.previous.setLinkSize(linkSize + 1);
        }

        if (this.parentCarPath != null) {
            this.parentCarPath.onCarPositionChanged(this);
        }

        return true;
    }

    public synchronized void makeIsland(boolean islandNext, boolean islandPrevious) {
        if (next != null && islandNext) {
            next.makeIsland(islandNext, islandPrevious);
        }

        if (previous != null && islandPrevious) {
            previous.makeIsland(islandNext, islandPrevious);
        }

        setParentCarPath(null);
        next = null;
        previous = null;
        last = null;
    }

    public synchronized void setPrevious(CarPosition previous) throws Exception {
        // Making an island as we are replacing the current previous
        if (this.previous != null && this.previous != previous) {
            this.previous.setNext(null, false);
        }

        if (this == previous) {
            throw new Exception("Infinite Linked List");
        }

        this.previous = previous;
        if (this.previous != null) {
            this.previous.setLinkSize(linkSize + 1);
            this.previous.setLast(this.last);
            updateAbsTime(this.previous.getAbsTime() + this.previous.getTimeToNextPosition());
            this.parentCarPath = this.previous.getParentCarPath(); // First element points to parent
        }

        if (this.parentCarPath != null) {
            this.parentCarPath.onCarPositionChanged(this);
        }
    }

    public synchronized UUID getCarUUID() {
        if (parentCarPath != null) {
            return parentCarPath.getCarUUID();
        }

        return null;
    }

    public synchronized CarPath getParentCarPath() {
        return parentCarPath;
    }

    public synchronized void setParentCarPath(CarPath parentCarPath) {
        this.parentCarPath = parentCarPath;
        if (this.next != null) {
            this.next.setParentCarPath(parentCarPath);
        }
    }

    public synchronized CarPosition getNext() {
        return next;
    }

    public synchronized CarPosition getPrevious() {
        return previous;
    }

    public synchronized boolean isLast() {
        return (this.next == null);
    }

    public synchronized boolean isParkingPosition() {
        return (timeToNextPosition == 0);
    }

    public Boundaries getBoundaries() {
        return boundaries;
    }

    public double getSpeed() {
        return speed;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public double getWheelsAngle() {
        return wheelsAngle;
    }

    /**
     * 0 for rested
     *
     * @return
     */
    public long getTimeToNextPosition() {
        return timeToNextPosition;
    }

    public long getId() {
        return id;
    }

    public long getAbsTime() {
        return absTime;
    }

    private void updateAbsTime(long absTime) {
        this.absTime = absTime;
        if (this.next != null) {
            this.next.updateAbsTime(absTime + this.timeToNextPosition);
        }
    }

    public void setAbsTime(long absTime, boolean forceInit) {
        if (this.absTime == 0 || forceInit) {
            updateAbsTime(absTime);
        }
    }

    public double getCollisionDistance() {
        return (speed * timeToNextPosition / 1000.0) * SimConfig.COLLISION_ZONE_ERROR_ADDITION;
    }

    public double getDecisionDistance() {
        return (boundaries.getLength() * SimConfig.SAFE_ZONE_ERROR_ADDITION);
    }

    public Circle getTurningCircle() {
        if (turningCircle == null) {
            if (wheelsAngle > 0) {
                turningCircle = Circle.getCircle(
                        boundaries.getRightSegment(),
                        boundaries.getLeftSegment(),
                        TrigUtils.getRadius(wheelsAngle, boundaries.getLeftSegment().Length()),
                        Circle.Direction.CLOCK_WISE);
            } else if (wheelsAngle < 0) {
                turningCircle = Circle.getCircle(
                        boundaries.getLeftSegment(),
                        boundaries.getRightSegment(),
                        TrigUtils.getRadius(wheelsAngle, boundaries.getRightSegment().Length()),
                        Circle.Direction.COUNTER_CLOCK_WISE);
            } // else null
        }
        return turningCircle;
    }

    public List<ObjectDistanceInfo> getCarDistancesInfo() {
        return carDistancesInfo;
    }

    @Override
    public String toString() {
        return "{id=" + id + "," +
                "next(id)=" + (next == null ? "null" : next.getId()) + "," +
                "prev(id)=" + (previous == null ? "null" : previous.getId()) + "," +
                "speed=" + speed + "," +
                "timeToNextPosition=" + timeToNextPosition + "," +
                "linkSize=" + linkSize + "," +
                "absTime=" + absTime + "," +
                "boundaries=" + boundaries.toString() + "}";
    }
}
