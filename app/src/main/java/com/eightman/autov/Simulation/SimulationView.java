package com.eightman.autov.Simulation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.eightman.autov.Configurations.Global;
import com.eightman.autov.Managers.DistanceManager;
import com.eightman.autov.Objects.CarCharacteristics;
import com.eightman.autov.Objects.Geom.XY;
import com.eightman.autov.Simulation.Drawings.AbstractDrawing;
import com.eightman.autov.Simulation.Drawings.CarDrawing;
import com.eightman.autov.Simulation.Objects.AbstractSimulation;
import com.eightman.autov.Simulation.Objects.CarSimulation;
import com.eightman.autov.StatsInterface;
import com.eightman.autov.Utils.MathUtils;
import com.eightman.autov.ai.CollisionManager;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class SimulationView extends SurfaceView {
    static String TAG = SimulationView.class.getSimpleName();

    SurfaceHolder surfaceHolder;
    DrawingThread drawingThread;
    List<StatsInterface> statsInterfaces = new LinkedList<>();
    List<AbstractSimulation> simulations = new LinkedList<>();
    List<AbstractDrawing> drawings = new LinkedList<>();
    CollisionManager collisionManager = CollisionManager.getInstance();
    DistanceManager distanceManager = DistanceManager.getInstance();

    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    Object preDrawNotify = new Object();
    Object postDrawNotify = new Object();

    public SimulationView(Context context) {
        super(context);
        init(context);
    }

    public SimulationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SimulationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressLint("NewApi")
    public SimulationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        scaleGestureDetector = new ScaleGestureDetector(context, scaleGestureListener);
        gestureDetector = new GestureDetector(context, gestureListener);

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

    public void registerStats(StatsInterface statsInterface) {
        if (!statsInterfaces.contains(statsInterface)) {
            statsInterfaces.add(statsInterface);
        }

        drawingThread.registerStats(statsInterface);
    }

    public void clearWorld() {
        synchronized (simulations) {
            for (AbstractSimulation simulation : simulations) {
                simulation.stop();
            }
        }

        simulations.clear();
        drawings.clear();
        collisionManager.clear();
        distanceManager.clear();
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

        synchronized (collisionManager) {
            collisionManager.addCar(carSimulation.getMyCar());
        }

        synchronized (distanceManager) {
            distanceManager.addCar(carSimulation.getMyCar());
        }
    }

    public int getNumberOfCars() {
        synchronized (simulations) {
            return simulations.size();
        }
    }

    public int getTotalCollisions() {
        int totalCollisions = 0;
        synchronized (simulations) {
            for (AbstractSimulation simulation : simulations) {
                totalCollisions += simulation.getTotalCollisions();
            }
        }
        return totalCollisions;
    }

    public void advanceTime() {
        synchronized (simulations) {
            for (AbstractSimulation simulation : simulations) {
                simulation.advanceTime();
            }
        }
        AbstractSimulation.waitUntilIdle();
    }

    public void waitForPreDraw() {
        try {
            // TODO: change this to support multiple threads
            synchronized (preDrawNotify) {
                preDrawNotify.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void waitForPostDraw() {
        try {
            synchronized (postDrawNotify) {
                postDrawNotify.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void drawWorld(Canvas canvas) {
        synchronized (preDrawNotify) {
            preDrawNotify.notifyAll();
        }

        canvas.drawColor(Color.BLACK);

        synchronized (drawings) {
            for (AbstractDrawing drawing : drawings) {
                drawing.draw(canvas);
            }
        }

        synchronized (postDrawNotify) {
            postDrawNotify.notifyAll();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean retVal = scaleGestureDetector.onTouchEvent(event);
        retVal = gestureDetector.onTouchEvent(event) || retVal;
        return retVal || super.onTouchEvent(event);
    }

    private final ScaleGestureDetector.OnScaleGestureListener scaleGestureListener
            = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        private float lastSpanX;
        private float lastSpanY;
        private float lastSpan;

        private void setSpan(float x, float y) {
            lastSpanX = x;
            lastSpanY = y;
            lastSpan = (float) MathUtils.getVector(lastSpanX, lastSpanY);
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            setSpan(scaleGestureDetector.getCurrentSpanX(), scaleGestureDetector.getCurrentSpanY());
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float spanX = scaleGestureDetector.getCurrentSpanX();
            float spanY = scaleGestureDetector.getCurrentSpanY();
            float span = (float) MathUtils.getVector(spanX, spanY);
            float newScale = span / lastSpan * (float) Global.pixelPerMoveUnit;
            Global.pixelPerMoveUnit = newScale;
            setSpan(spanX, spanY);
            return true;
        }
    };

    private final GestureDetector.SimpleOnGestureListener gestureListener
            = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Global.offsetX += -distanceX;
            Global.offsetY += -distanceY;
            return true;
        }
    };
}
