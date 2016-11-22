package com.eightman.autov.Simulation.Interfaces;

import com.eightman.autov.Objects.CarPath;

/**
 * Created by gilzhaiek on 2016-10-27.
 */

public interface IRandomPathMaker {
    boolean generatePath(CarPath carPath) throws Exception;
}
