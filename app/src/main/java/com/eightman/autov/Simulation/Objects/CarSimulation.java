package com.eightman.autov.Simulation.Objects;

import android.os.AsyncTask;

import com.eightman.autov.Objects.CarCharacteristics;
import com.eightman.autov.Objects.CarPosition;
import com.eightman.autov.Objects.MyCar;
import com.eightman.autov.Simulation.DataMaker.RandomSquarePathMaker;
import com.eightman.autov.Utils.TrigUtils;

import java.util.List;

/**
 * Created by gilzhaiek on 2016-11-03.
 */

public class CarSimulation {
    MyCar myCar;
    static RandomSquarePathMaker randomSquarePathMaker = new RandomSquarePathMaker();

    public CarSimulation() {
        final CarCharacteristics carChars = CarCharacteristics.generateRandom();
        final CarPosition carPosition = new CarPosition(
                TrigUtils.getBoundariesLookingNorth(
                        0, 0,
                        carChars.getWidth(), carChars.getLength(),
                        System.currentTimeMillis()),
                0);

        myCar = new MyCar(carChars, carPosition);
        new GeneratePathTask().execute(myCar.getCarPath().getLastPosition());
    }

    private class GeneratePathTask extends AsyncTask<CarPosition.Final, Void, List<CarPosition.Final>> {
        protected List<CarPosition.Final> doInBackground(CarPosition.Final... lastPosition) {
            return randomSquarePathMaker.generatePath(lastPosition[0]);
        }

        protected void onPostExecute(List<CarPosition.Final> carPositions) {
            if(!myCar.addPath(carPositions, true)) {
                new GeneratePathTask().execute(myCar.getCarPath().getLastPosition());
            }
        }
    }
}