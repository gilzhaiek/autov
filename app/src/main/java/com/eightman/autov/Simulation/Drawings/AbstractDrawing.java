package com.eightman.autov.Simulation.Drawings;

import android.graphics.Canvas;

import com.eightman.autov.Simulation.SimulationView;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public abstract class AbstractDrawing {
    SimulationView simulationView;

    public AbstractDrawing(SimulationView simulationView) {
        this.simulationView = simulationView;
    }

    protected Canvas lockCanvas() {
        return simulationView.getHolder().lockCanvas();
    }

    protected void unlockCanvasAndPost(Canvas canvas) {
        simulationView.getHolder().unlockCanvasAndPost(canvas);
    }

    public void draw() {
        Canvas canvas = lockCanvas();
        try {
            if (canvas != null) {
                synchronized (simulationView.getHolder()) {
                    onDraw(canvas);
                }
            }
        } finally {
            unlockCanvasAndPost(canvas);
        }
    }

    abstract void onDraw(Canvas canvas);
}
