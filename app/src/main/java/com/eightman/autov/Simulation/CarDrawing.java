package com.eightman.autov.Simulation;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.eightman.autov.Objects.Car;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class CarDrawing extends AbstractDrawing {
    Car car;

    public CarDrawing(SimulationView simulationView, Car car) {
        super(simulationView);
        this.car = car;
    }

    @Override
    void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.rgb(0xff, 0xff, 0));
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(100, 100, 200, 200, paint);
    }
}
