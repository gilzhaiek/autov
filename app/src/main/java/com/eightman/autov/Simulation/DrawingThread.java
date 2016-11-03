package com.eightman.autov.Simulation;

import android.graphics.Canvas;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class DrawingThread extends Thread {
    SimulationView simulationView;
    private boolean running = false;

    public DrawingThread(SimulationView simulationView) {
        this.simulationView = simulationView;
    }

    public void setRunning(boolean run) {
        this.running = run;
    }

    @Override
    public void run() {
        while (running) {
            Canvas canvas = simulationView.getHolder().lockCanvas();

            if (canvas != null) {
                synchronized (simulationView.getHolder()) {
                    simulationView.drawSomething(canvas);
                }
                simulationView.getHolder().unlockCanvasAndPost(canvas);
            }

            try {
                sleep(30);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }


}
