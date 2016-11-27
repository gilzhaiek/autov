package com.eightman.autov.Simulation;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by gilzhaiek on 2016-11-21.
 */

public class SimTime {
    private AtomicLong absTime = new AtomicLong(0);

    private static class InternalSingleton {
        private static SimTime singleton = new SimTime();
    }

    public static SimTime getInstance() {
        return SimTime.InternalSingleton.singleton;
    }
    public long addTime(long delta) {
        return absTime.addAndGet(delta);
    }

    public long getTime() {
        return absTime.get();
    }

    public void resetTime() {
        absTime.set(0);
    }

    // TODO
    public void registerForRecurringTime() {
    }

    interface SimTimeCallback {

    }
}
