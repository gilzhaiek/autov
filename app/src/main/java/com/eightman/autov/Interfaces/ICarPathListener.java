package com.eightman.autov.Interfaces;

import com.eightman.autov.Objects.CarPath;

/**
 * Created by gilzhaiek on 2016-11-13.
 */

public interface ICarPathListener {
    void onPathChanged(CarPath carPath, boolean isCurrentPosition);
}
