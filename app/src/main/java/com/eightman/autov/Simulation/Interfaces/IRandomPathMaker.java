package com.eightman.autov.Simulation.Interfaces;

import com.eightman.autov.Objects.CarCharacteristics;
import com.eightman.autov.Objects.CarPath;

/**
 * Created by gilzhaiek on 2016-10-27.
 */

public interface IRandomPathMaker {
    boolean generatePath(CarPath carPath, CarCharacteristics carCharacteristics) throws Exception;
}
