package com.eightman.autov.Simulation;

import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.StatsInterface;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class DrawingThread extends Thread {
    private static final int UPDATE_FPS = 1;

    SimulationView simulationView;
    List<StatsInterface> statsInterfaces = new LinkedList<>();
    double fps = 0.0;

    private boolean running = false;

    public DrawingThread(SimulationView simulationView) {
        this.simulationView = simulationView;
    }

    public void setRunning(boolean run) {
        this.running = run;
    }

    public void registerStats(StatsInterface statsInterface) {
        if (!statsInterfaces.contains(statsInterface)) {
            statsInterfaces.add(statsInterface);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == UPDATE_FPS) {
                for (StatsInterface statsInterface : statsInterfaces) {
                    statsInterface.onFPSChanged(msg.arg1);
                }
            }
        }
    };

    private void updateFPS(int fps) {
        Message message = handler.obtainMessage();
        message.what = UPDATE_FPS;
        message.arg1 = fps;
        handler.sendMessage(message);
    }

    @Override
    public void run() {
        long lastTime = System.currentTimeMillis();
        while (running) {
            Canvas canvas = simulationView.getHolder().lockCanvas();

            if (canvas != null) {
                synchronized (simulationView.getHolder()) {
                    simulationView.drawWorld(canvas);
                }
                simulationView.getHolder().unlockCanvasAndPost(canvas);
            }

            simulationView.advanceTime();

            long deltaTime = System.currentTimeMillis() - lastTime;
            long delay = SimConfig.DELAY_MS - deltaTime;

            updateFPS((int) (1000.0 / deltaTime));

            if (delay > 0) {
                try {
                    sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            lastTime = System.currentTimeMillis();
        }
    }


}
