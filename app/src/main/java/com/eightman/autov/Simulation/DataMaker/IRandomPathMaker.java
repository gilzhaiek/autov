package com.eightman.autov.Simulation.DataMaker;

import com.eightman.autov.Objects.CarPosition;

import java.util.List;

/**
 * Created by gilzhaiek on 2016-10-27.
 */

public interface IRandomPathMaker {
    public List<CarPosition.Final> generatePath(CarPosition.Final carPosition);
}
