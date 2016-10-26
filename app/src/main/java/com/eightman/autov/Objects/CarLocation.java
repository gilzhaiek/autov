package com.eightman.autov.Objects;

import android.graphics.PointF;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class CarLocation {
    PointF location;

    public CarLocation(PointF location) {
        this.location = location;
    }

    public PointF getLocation() {
        return location;
    }
}
