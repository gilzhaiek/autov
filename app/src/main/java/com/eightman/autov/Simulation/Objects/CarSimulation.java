package com.eightman.autov.Simulation.Objects;

import android.os.AsyncTask;

import com.eightman.autov.Configurations.Constants;
import com.eightman.autov.Objects.CarCharacteristics;
import com.eightman.autov.Objects.CarPosition;
import com.eightman.autov.Objects.MyCar;
import com.eightman.autov.Simulation.DataMaker.RandomSquarePathMaker;
import com.eightman.autov.Utils.TrigUtils;
import com.eightman.autov.Utils.XY;
import com.eightman.autov.ai.CollisionDetector;

import java.util.List;
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
        new GeneratePathTask().execute(myCar.getCarPath().peekLastPosition());
    }

    private void moveToFirstPosition() {
        CarPosition.Final position = myCar.getCarPath().peekFirstPosition();
        if(position != null) {
            myCar.setCarPosition(position);
            if (position.getCollisionZone() == null) {
                CarPosition.Final nextPosition = myCar.getCarPath().getPosition(1);
                position.setCollisionZone(CollisionDetector.getHeadingBoundaries(
                        position,
                        nextPosition != null ? nextPosition.getTimeOffset() : Constants.ONE_SECOND));
            }
        }
    }

    @Override
    public void advanceTime() {
        CarPosition.Final position = myCar.getCarPath().popFirstPosition();
        if (myCar.getCarPath().size() == 0 && generatePathTask == null) {
            generatePathTask = new GeneratePathTask();
            generatePathTask.execute(position);
        } else {
            moveToFirstPosition();
        }
    }

    private class GeneratePathTask extends AsyncTask<CarPosition.Final, Void, List<CarPosition.Final>> {
        protected List<CarPosition.Final> doInBackground(CarPosition.Final... lastPosition) {
            return randomSquarePathMaker.generatePath(lastPosition[0]);
        }

        protected void onPostExecute(List<CarPosition.Final> carPositions) {
            if (!myCar.addPath(carPositions, true)) {
                generatePathTask = new GeneratePathTask();
                generatePathTask.execute(myCar.getCarPath().peekLastPosition());
            } else {
                moveToFirstPosition();
                generatePathTask = null;
            }
        }
    }

    public MyCar getMyCar() {
        return myCar;
    }
}
