package com.eightman.autov.Objects;

import android.support.annotation.NonNull;

import com.eightman.autov.Configurations.Global;
import com.eightman.autov.Hardware.Boundaries;
import com.eightman.autov.Utils.CollisionUtils;

import java.util.UUID;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class CarPosition {
    private final static String TAG = CarPosition.class.getSimpleName();
    private final long id;
    private final Boundaries boundaries;
    private final double speed;
    private final long timeToNextPosition;
    private final Boundaries collisionZone;
    private final Boundaries safeZone;

    private CarPosition next;
    private CarPosition previous;
    private CarPosition last;
    private int linkSize = 1;
    private CarPath parentCarPath;

    // TODO: Add direction - front or back
    // TODO: Add wheels angles

    //
    private CarPosition(Boundaries boundaries) {
        this.id = Global.generateId();
        this.boundaries = boundaries;
        this.speed = 0;
        this.timeToNextPosition = 0;
        this.collisionZone = CollisionUtils.getParkingBoundaries(boundaries);
        this.safeZone = this.collisionZone;
        this.last = this;
    }

    private CarPosition(
            @NonNull Boundaries boundaries,
            double speed,
            long timeToNextPosition,
            @NonNull Boundaries collisionZone,
            @NonNull Boundaries safeZone) {
        this.id = Global.generateId();
        this.boundaries = boundaries;
        this.speed = speed;
        this.timeToNextPosition = timeToNextPosition;
        this.collisionZone = collisionZone;
        this.safeZone = safeZone;
        this.last = this;
    }

    public static CarPosition getRestedPosition(Boundaries boundaries) {
        return new CarPosition(boundaries);
    }

    public static CarPosition getMovingPosition(
            Boundaries boundaries,
            double speed,
            long timeToNextPosition,
            Boundaries collisionZone,
            Boundaries safeZone) {
        return new CarPosition(boundaries, speed, timeToNextPosition, collisionZone, safeZone);
    }

    public static CarPosition copy(CarPosition carPosition) {
        return new CarPosition(
                carPosition.getBoundaries(),
                carPosition.getSpeed(),
                carPosition.getTimeToNextPosition(),
                carPosition.getCollisionZone(),
                carPosition.getSafeZone());
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

    public Boundaries getBoundaries() {
        return boundaries;
    }

    public double getSpeed() {
        return speed;
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

    public Boundaries getCollisionZone() {
        return collisionZone;
    }

    public Boundaries getSafeZone() {
        return safeZone;
    }
}
