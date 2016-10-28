package com.eightman.autov.Simulation.Drawings;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.eightman.autov.Objects.MyCar;
import com.eightman.autov.Simulation.SimulationView;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class CarDrawing extends AbstractDrawing {
    MyCar car;

    public CarDrawing(SimulationView simulationView, MyCar car) {
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
