package com.eightman.autov.Simulation;

import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.StatsInterface;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class DrawingThread extends Thread {
    private static final int UPDATE_FPS = 1;

    SimulationView simulationView;
    List<StatsInterface> statsInterfaces = new LinkedList<>();
    Queue<Long> frameTimestamps = new ArrayDeque<>();
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

    private void updateFPS() {
        long timestamp = System.currentTimeMillis();
        frameTimestamps.add(timestamp);

        while (frameTimestamps.peek() < timestamp - 1000) {
            frameTimestamps.poll();
        }

        Message message = handler.obtainMessage();
        message.what = UPDATE_FPS;
        message.arg1 = frameTimestamps.size();
        handler.sendMessage(message);
    }

    @Override
    public void run() {

        while (running) {
            Canvas canvas = simulationView.getHolder().lockCanvas();

            if (canvas != null) {
                synchronized (simulationView.getHolder()) {
                    simulationView.drawWorld(canvas);
                }
                simulationView.getHolder().unlockCanvasAndPost(canvas);
            }

            simulationView.advanceTime();
            simulationView.waitUntilIdle();

            updateFPS();

            try {
                sleep(SimConfig.DELAY_MS);
                SimTime.getInstance().addTime(SimConfig.DELAY_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
