package com.eightman.autov.Simulation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Hardware.Boundaries;
import com.eightman.autov.Objects.CarPosition;
import com.eightman.autov.Simulation.DataMaker.RandomSquarePathMaker;
import com.eightman.autov.Simulation.Drawings.DrawingUtils;
import com.eightman.autov.Simulation.Objects.CarSimulation;
import com.eightman.autov.Utils.MathUtils;
import com.eightman.autov.Utils.XY;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class SimulationView extends SurfaceView {
    static String TAG = SimulationView.class.getSimpleName();

    SurfaceHolder surfaceHolder;
    DrawingThread drawingThread;
    List<CarSimulation> simulatedCars = new LinkedList<>();

    static double canvasWidth = 100; // 100 pixels
    static double canvasHeight = 100; // 300 pixels

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
                canvasWidth = (double) canvas.getWidth();
                canvasHeight = (double) canvas.getHeight();
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

    public void clearCars() {
        simulatedCars.clear();
    }

    public void addRandomCar() {
        simulatedCars.add(new CarSimulation());
    }

    protected void drawSomething(Canvas canvas) {
        canvas.drawColor(Color.BLACK);

        Double carWidth = SimConfig.MIN_CAR_WIDTH+random.nextDouble()*(SimConfig.MAX_CAR_WIDTH-SimConfig.MIN_CAR_WIDTH);
        Double carLength = SimConfig.MIN_CAR_LENGTH+random.nextDouble()*(SimConfig.MAX_CAR_LENGTH-SimConfig.MIN_CAR_LENGTH);
        Boundaries initialBoundaries = MathUtils.getBoundariesLookingNorth(0.0, 0.0, carWidth, carLength, );
        List<CarPosition> carPositions = randomSquarePathMaker.generatePath(new CarPosition(initialBoundaries, 30));

        Paint rfWheelPaint = getNewPaint(Color.rgb(0xFF, 0, 0));
        Paint rbWheelPaint = getNewPaint(Color.rgb(0, 0, 0xFF));
        Paint lbWheelPaint = getNewPaint(Color.rgb(0, 0, 0xFF));
        Paint lfWheelPaint = getNewPaint(Color.rgb(0xff, 0, 0));
        Paint carPaint = getNewPaint(Color.rgb(0xFF, 0xFF, 0x00)); // Yellow

        if(carPositions.size() > 1) {
            CarPosition.Final startPosition = carPositions.get(0).getPosition();
            drawCar(canvas, startPosition.getBoundaries(), carPaint);

            for (CarPosition carPosition : carPositions) {
                CarPosition.Final stopPosition = carPosition.getPosition();
                if(startPosition.equals(stopPosition)) {
                    continue;
                }

                drawLine(canvas,
                        (float)startPosition.getBoundaries().getRightFront().getX(),
                        (float)startPosition.getBoundaries().getRightFront().getY(),
                        (float)stopPosition.getBoundaries().getRightFront().getX(),
                        (float)stopPosition.getBoundaries().getRightFront().getY(),
                        rfWheelPaint);

                drawLine(canvas,
                        (float)startPosition.getBoundaries().getRightBack().getX(),
                        (float)startPosition.getBoundaries().getRightBack().getY(),
                        (float)stopPosition.getBoundaries().getRightBack().getX(),
                        (float)stopPosition.getBoundaries().getRightBack().getY(),
                        rbWheelPaint);

                drawLine(canvas,
                        (float)startPosition.getBoundaries().getLeftBack().getX(),
                        (float)startPosition.getBoundaries().getLeftBack().getY(),
                        (float)stopPosition.getBoundaries().getLeftBack().getX(),
                        (float)stopPosition.getBoundaries().getLeftBack().getY(),
                        lbWheelPaint);

                drawLine(canvas,
                        (float)startPosition.getBoundaries().getLeftFront().getX(),
                        (float)startPosition.getBoundaries().getLeftFront().getY(),
                        (float)stopPosition.getBoundaries().getLeftFront().getX(),
                        (float)stopPosition.getBoundaries().getLeftFront().getY(),
                        lfWheelPaint);

                drawCar(canvas, stopPosition.getBoundaries(), carPaint);

                startPosition = stopPosition;
            }
        }

    }
}
