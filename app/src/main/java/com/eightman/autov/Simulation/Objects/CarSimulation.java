package com.eightman.autov.Simulation.Objects;

import android.os.AsyncTask;
import android.util.Log;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Objects.CarCharacteristics;
import com.eightman.autov.Objects.CarPath;
import com.eightman.autov.Objects.CarPosition;
import com.eightman.autov.Objects.MyCar;
import com.eightman.autov.Simulation.DataMaker.RandomSquarePathMaker;
import com.eightman.autov.Simulation.SimTime;
import com.eightman.autov.Utils.TrigUtils;
import com.eightman.autov.Utils.XY;

import java.util.UUID;

/**
 * Created by gilzhaiek on 2016-11-03.
 */

public class CarSimulation extends AbstractSimulation {
    private final static String TAG = CarSimulation.class.getSimpleName();

    boolean stopped = false;
    MyCar myCar;
    static RandomSquarePathMaker randomSquarePathMaker = new RandomSquarePathMaker();
    GeneratePathTask generatePathTask = null;

    public CarSimulation(final CarCharacteristics carChars, final XY position) {
        final CarPosition carPosition = CarPosition.getRestedPosition(
                TrigUtils.getBoundariesLookingNorth(
                        position.getX(), position.getY(),
                        carChars.getWidth(), carChars.getLength()));

        myCar = new MyCar(UUID.randomUUID(), carChars, carPosition);
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
            return;
        }

        CarPath.PositionInfo positionInfo = myCar.getCarPath().moveToNextPosition();
        if (positionInfo == null) {
            return;
        }

        Log.d(TAG, "Moving from " + myCar.getCarPosition().getAbsTime() +
                " to " + positionInfo.getPosition().getAbsTime() +
                " absTime = " + SimTime.getInstance().getTime());

        if (SimTime.getInstance().getTime() > (positionInfo.getPosition().getAbsTime() + SimConfig.DELAY_MS * 2)) {
            Log.e(TAG, "Time ERROR");
            stop();
        } else {
            myCar.setCarPosition(positionInfo.getPosition());
            myCar.setTargetSpeed(positionInfo.getAdjustedSpeed());
        }
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
                while (!randomSquarePathMaker.generatePath(carPath)) {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            carPath.removeCollisions();

            return true;
        }

        protected void onPostExecute(Boolean success) {
            generatePathTask = null;
            if (success) {
                attemptRemoveCollisions();
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
