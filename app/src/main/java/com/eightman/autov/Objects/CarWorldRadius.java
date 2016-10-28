package com.eightman.autov.Objects;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by gilzhaiek on 2016-10-27.
 */

public class CarWorldRadius {
    MyCar myCar;

    Map<Float, OtherCar> otherCars = new TreeMap<Float, OtherCar>();

    public CarWorldRadius(MyCar myCar) {
        this.myCar = myCar;
    }
}
