package com.eightman.autov.Simulation.Objects;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by gilzhaiek on 2016-11-04.
 */

public abstract class AbstractSimulation {
    private static final boolean DEBUG = false;
    private static final String TAG = AbstractSimulation.class.getSimpleName();

    private static List<Object> busyObjects = new LinkedList<>();

    public abstract void advanceTime();

    public void addBusy(Object object) {
        synchronized (busyObjects) {
            if (!busyObjects.contains(object)) {
                busyObjects.add(object);
            }
            if (DEBUG) {
                Log.d(TAG, "busyObjects ADDED object, size = " + busyObjects.size());
            }
        }
    }

    public void removeBusy(Object object) {
        synchronized (busyObjects) {
            if (DEBUG) {
                Log.d(TAG, "busyObjects REMOVING object, size = " + busyObjects.size());
            }
            busyObjects.remove(object);

            if (busyObjects.size() == 0) {
                if (DEBUG) {
                    Log.d(TAG, "busyObjects size is 0 NOTIFY ALL");
                }
                busyObjects.notifyAll();
            }
        }
    }

    public static void waitUntilIdle() {
        synchronized (busyObjects) {
            if (busyObjects.size() == 0) {
                return;
            }

            if (DEBUG) {
                Log.d(TAG, "busyObjects WAITING on " + busyObjects.size() + " objects");
            }
            try {
                busyObjects.wait();
                if (DEBUG) {
                    Log.d(TAG, "busyObjects DONE waiting");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
