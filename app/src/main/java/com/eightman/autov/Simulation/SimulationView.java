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
import com.eightman.autov.Utils.MathUtils;
import com.eightman.autov.Utils.XY;

import java.util.List;
import java.util.Random;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class SimulationView extends SurfaceView {
    static String TAG = SimulationView.class.getSimpleName();

    RandomSquarePathMaker randomSquarePathMaker = new RandomSquarePathMaker();
    SurfaceHolder surfaceHolder;
    DrawingThread drawingThread;
    Random random = new Random();

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
                canvasWidth = (double)canvas.getWidth();
                canvasHeight = (double)canvas.getHeight();
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
                while(retry) {
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

    protected Paint getNewPaint(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(SimConfig.STROKE_WIDTH);
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }

    private void drawLine(Canvas canvas,
                          float startX, float startY, float stopX, float stopY,
                          Paint paint){
        float moveUnitPerPixel = (float)SimConfig.MOVE_UNIT_PER_PIXEL;
        canvas.drawLine(
                moveUnitPerPixel*(startX+(float)canvasWidth/2.0f/moveUnitPerPixel),
                moveUnitPerPixel*(-1*startY +(float)canvasHeight/2.0f/moveUnitPerPixel),
                moveUnitPerPixel*(stopX+(float)canvasWidth/2.0f/moveUnitPerPixel),
                moveUnitPerPixel*(-1*stopY +(float)canvasHeight/2.0f/moveUnitPerPixel), paint);
    }

    protected void drawSomething(Canvas canvas) {
        canvas.drawColor(Color.BLACK);

        Double carWidth = SimConfig.MIN_CAR_WIDTH+random.nextDouble()*(SimConfig.MAX_CAR_WIDTH-SimConfig.MIN_CAR_WIDTH);
        Double carLength = SimConfig.MIN_CAR_LENGTH+random.nextDouble()*(SimConfig.MAX_CAR_LENGTH-SimConfig.MIN_CAR_LENGTH);
        Boundaries initialBoundaries = MathUtils.getBoundariesLookingNorth(new XY(0.0, 0.0), carWidth, carLength, 0);
        List<CarPosition> carPositions = randomSquarePathMaker.generatePath(new CarPosition(initialBoundaries, 30, 0));

        Paint rfWheelPaint = getNewPaint(Color.rgb(0xff, 0, 0));
        Paint rbWheelPaint = getNewPaint(Color.rgb(0, 0xff, 0));
        Paint lbWheelPaint = getNewPaint(Color.rgb(0, 0, 0xff));
        Paint lfWheelPaint = getNewPaint(Color.rgb(0xff, 0xff, 0));

        if(carPositions.size() > 1) {
            CarPosition.Final startPosition = carPositions.get(0).getPosition();


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

                startPosition = stopPosition;
            }
        }

    }
}
