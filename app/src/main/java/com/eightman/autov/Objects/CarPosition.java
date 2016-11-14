package com.eightman.autov.Objects;

import android.support.annotation.NonNull;

import com.eightman.autov.Configurations.Global;
import com.eightman.autov.Hardware.Boundaries;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class CarPosition {
    private final long id;
    private final Boundaries boundaries;
    private final double speed;
    private final long timeToNextPosition;
    private final Boundaries collisionZone;

    private CarPosition next;
    private CarPosition previous;
    private CarPosition last;
    private int linkSize = 1;
    private CarPath parentCarPath;

    // TODO: Add direction - front or back
    // TODO: Add wheels angles

    private CarPosition(Boundaries boundaries) {
        this.id = Global.generateId();
        this.boundaries = boundaries;
        this.speed = 0;
        this.timeToNextPosition = 0;
        this.collisionZone = boundaries;
        this.last = this;
    }

    private CarPosition(
            @NonNull Boundaries boundaries,
            double speed,
            long timeToNextPosition,
            @NonNull Boundaries collisionZone) {
        this.id = Global.generateId();
        this.boundaries = boundaries;
        this.speed = speed;
        this.timeToNextPosition = timeToNextPosition;
        this.collisionZone = collisionZone;
        this.last = this;
    }

    public static CarPosition getRestedPosition(Boundaries boundaries) {
        return new CarPosition(boundaries);
    }

    public static CarPosition getMovingPosition(
            Boundaries boundaries, double speed, long timeOffset, Boundaries collisionZone) {
        return new CarPosition(boundaries, speed, timeOffset, collisionZone);
    }

    public synchronized int getLinkSize() {
        return linkSize;
    }

    private synchronized void setLinkSize(int linkSize) {
        this.linkSize = linkSize;
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

    public synchronized boolean setNext(CarPosition next, boolean mustBeLast) {
        if (next == null) {
            this.last = this;
            linkSize = 1;
        } else {
            if (this.next != null) {
                if (mustBeLast) {
                    return false;
                }
                if (this.next != next) {
                    this.next.setParentCarPath(null);
                    this.next.setPrevious(null);
                }
            }
            linkSize = 1 + next.getLinkSize();
            this.last = next.getLast();
            next.setParentCarPath(this.parentCarPath);
            next.setPrevious(this);
        }
        this.next = next;

        if (this.previous != null) {
            this.previous.setLast(this.last);
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

    public synchronized void setPrevious(CarPosition previous) {
        if (this.previous != null && this.previous != previous) {
            this.previous.setLinkSize(1);
            this.previous.setNext(null, false);
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
}
