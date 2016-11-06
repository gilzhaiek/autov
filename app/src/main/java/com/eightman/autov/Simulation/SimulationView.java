package com.eightman.autov.Simulation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.eightman.autov.Configurations.Global;
import com.eightman.autov.Objects.CarCharacteristics;
import com.eightman.autov.Simulation.Drawings.AbstractDrawing;
import com.eightman.autov.Simulation.Drawings.CarDrawing;
import com.eightman.autov.Simulation.Objects.AbstractSimulation;
import com.eightman.autov.Simulation.Objects.CarSimulation;
import com.eightman.autov.Utils.XY;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class SimulationView extends SurfaceView {
    static String TAG = SimulationView.class.getSimpleName();

    SurfaceHolder surfaceHolder;
    DrawingThread drawingThread;
    List<AbstractSimulation> simulations = new LinkedList<>();
    List<AbstractDrawing> drawings = new LinkedList<>();

    public SimulationView(Context context) {
        super(context);
        init();
    }

    public SimulationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SimulationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SimulationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        drawingThread = new DrawingThread(this);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                drawingThread.setRunning(true);
                drawingThread.start();

                Canvas canvas = holder.lockCanvas();
                Global.canvasWidth = (double) canvas.getWidth();
                Global.canvasHeight = (double) canvas.getHeight();
                canvas.drawColor(Color.BLACK);
                holder.unlockCanvasAndPost(canvas);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                drawingThread.setRunning(false);
                while (retry) {
                    try {
                        drawingThread.join();
                        retry = false;
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Can't join thread", e);
                    }
                }
            }
        });
    }

    public void clearWorld() {
        simulations.clear();
    }

    public void addRandomCar(XY launchPosition) throws Exception {
        CarSimulation carSimulation;
        synchronized (simulations) {
            final CarCharacteristics carChars = CarCharacteristics.generateRandom();
            carSimulation = new CarSimulation(carChars, launchPosition);
            simulations.add(carSimulation);
        }
        synchronized (drawings) {
            drawings.add(new CarDrawing(this, carSimulation.getMyCar()));
        }
    }

    public void advanceTime() {
        synchronized (simulations) {
            for (AbstractSimulation simulation : simulations) {
                simulation.advanceTime();
            }
        }
    }

    public void drawWorld(Canvas canvas) {
        canvas.drawColor(Color.BLACK);

        synchronized (drawings) {
            for (AbstractDrawing drawing : drawings) {
                drawing.draw(canvas);
            }
        }
    }
}
