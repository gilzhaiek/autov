package com.eightman.autov;

/**
 * Created by gilzhaiek on 2016-11-07.
 */

public interface StatsInterface {
    void onFPSChanged(int fps);

    void onTotalCollisionsChanged(int totalCollisions);

    void onTimeChanged(long time);
}
