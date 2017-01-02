package com.eightman.autov.Simulation.Objects;

import android.os.AsyncTask;
import android.util.Log;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Managers.BoundariesManager;
import com.eightman.autov.Objects.CarCharacteristics;
import com.eightman.autov.Objects.CarPath;
import com.eightman.autov.Objects.CarPosition;
import com.eightman.autov.Objects.Geom.XY;
import com.eightman.autov.Objects.MyCar;
import com.eightman.autov.Objects.Physical.Wheels;
import com.eightman.autov.Simulation.DataMaker.RandomCirclePathMaker;
import com.eightman.autov.Simulation.Interfaces.IRandomPathMaker;
import com.eightman.autov.Simulation.SimTime;

import java.util.UUID;

/**
 * Created by gilzhaiek on 2016-11-03.
 */

public class CarSimulation extends AbstractSimulation {
    private final static String TAG = CarSimulation.class.getSimpleName();

    boolean stopped = false;
    MyCar myCar;
    static IRandomPathMaker randomPathMaker = new RandomCirclePathMaker();
    GeneratePathTask generatePathTask = null;

    public CarSimulation(final CarCharacteristics carChars, final XY position) {
        final CarPosition carPosition = CarPosition.getRestedPosition(
                BoundariesManager.getBoundariesLookingNorth(position, carChars.getWidth(), carChars.getLength(),
                        Wheels.STRAIGHT_WHEELS, carChars.getWheels().getMaxWheelsAngle()));

        myCar = new MyCar(UUID.randomUUID(), carChars, carPosition);
        carPosition.setAbsTime(SimTime.getInstance().getTime(), false);
        generatePath();
    }

    private synchronized void generatePath() {
        if (generatePathTask == null && !stopped) {
            generatePathTask = new GeneratePathTask();
            generatePathTask.execute(myCar.getCarPath());
            addBusy(this);
        }
    }

    private void moveIfNeeded() {
        if (!myCar.getCarPath().needToMove() || stopped) {
            //Log.d(TAG, "Don't need to move...");
            return;
        }

        CarPath.PositionInfo positionInfo = myCar.getCarPath().moveToNextPosition();
        if (positionInfo == null) {
            return;
        }

        if (SimTime.getInstance().getTime() > (positionInfo.getPosition().getAbsTime() + SimConfig.DELAY_MS * 3)) {
            Log.e(TAG, "Time ERROR: myCar=" + myCar.getCarPosition().toString() +
                    " position=" + positionInfo.getPosition().toString());
            stop();
            return;
        }

        myCar.setCarPosition(positionInfo.getPosition());
    }

    @Override
    public void advanceTime() {
        if (stopped) {
            return;
        } else if (myCar.getCarPath().getSize() <= 1) {
            generatePath();
        } else {
            moveIfNeeded();
        }
    }

    @Override
    public int getTotalCollisions() {
        return myCar.getNumOfAccidents();
    }

    @Override
    public void stop() {
        stopped = true;
    }

    private class GeneratePathTask extends AsyncTask<CarPath, Void, Boolean> {
        protected Boolean doInBackground(CarPath... carPaths) {
            CarPath carPath = carPaths[0];
            try {
                while (!randomPathMaker.generatePath(carPath, myCar.getCarCharacteristics())) {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            carPath.findDistances();

            return true;
        }

        protected void onPostExecute(Boolean success) {
            generatePathTask = null;
            if (success) {
                attemptRemoveCollisions();
                moveIfNeeded();
                removeBusy(CarSimulation.this);
            } else {
                generatePath();
            }
        }
    }

    public MyCar getMyCar() {
        return myCar;
    }

    public void attemptRemoveCollisions() {

    }
}
