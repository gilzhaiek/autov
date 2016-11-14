package com.eightman.autov.Simulation.Objects;

import android.os.AsyncTask;

import com.eightman.autov.Objects.CarCharacteristics;
import com.eightman.autov.Objects.CarPath;
import com.eightman.autov.Objects.CarPosition;
import com.eightman.autov.Objects.MyCar;
import com.eightman.autov.Simulation.DataMaker.RandomSquarePathMaker;
import com.eightman.autov.Utils.TrigUtils;
import com.eightman.autov.Utils.XY;

import java.util.UUID;

/**
 * Created by gilzhaiek on 2016-11-03.
 */

public class CarSimulation extends AbstractSimulation {
    MyCar myCar;
    static RandomSquarePathMaker randomSquarePathMaker = new RandomSquarePathMaker();
    GeneratePathTask generatePathTask = null;

    public CarSimulation(final CarCharacteristics carChars, final XY position) {
        final CarPosition carPosition = CarPosition.getRestedPosition(
                TrigUtils.getBoundariesLookingNorth(
                        position.getX(), position.getY(),
                        carChars.getWidth(), carChars.getLength()));

        myCar = new MyCar(UUID.randomUUID(), carChars, carPosition);
        new GeneratePathTask().execute(myCar.getCarPath());
    }

    private void move() {
        if (!myCar.getCarPath().needToMove()) {
            return;
        }

        CarPath.PositionInfo positionInfo = myCar.getCarPath().moveToNextPosition();
        if (positionInfo == null) {
            return;
        }

        myCar.setCarPosition(positionInfo.getPosition());
        myCar.setTargetSpeed(positionInfo.getAdjustedSpeed());
    }

    @Override
    public void advanceTime() {
        if (myCar.getCarPath().getSize() <= 1) {
            if (generatePathTask == null) {
                generatePathTask = new GeneratePathTask();
                generatePathTask.execute(myCar.getCarPath());
            } // else working on path
        } else {
            if (myCar.getCarPath().needToMove()) {
                move();
            }
        }
    }

    private class GeneratePathTask extends AsyncTask<CarPath, Void, Boolean> {
        protected Boolean doInBackground(CarPath... carPath) {
            return randomSquarePathMaker.generatePath(carPath[0]);
        }

        protected void onPostExecute(Boolean success) {
            if (success) {
                generatePathTask = null;
                if (myCar.getCarPath().needToMove()) {
                    move();
                }
            } else {
                generatePathTask = new GeneratePathTask();
                generatePathTask.execute(myCar.getCarPath());
            }
        }
    }

    public MyCar getMyCar() {
        return myCar;
    }
}
