package com.eightman.autov.Simulation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class SimulationView extends SurfaceView {
    private static String TAG = SimulationView.class.getSimpleName();

    private SurfaceHolder surfaceHolder;
    private DrawingThread drawingThread;

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

    protected void drawSomething(Canvas canvas) {
        canvas.drawColor(Color.BLACK);

        Paint paint = new Paint();
        paint.setColor(Color.rgb(0xff, 0xff, 0));
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(100, 100, 200, 200, paint);
    }
}
