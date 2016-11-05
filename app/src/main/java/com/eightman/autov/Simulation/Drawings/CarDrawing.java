package com.eightman.autov.Simulation.Drawings;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Hardware.Boundaries;
import com.eightman.autov.Objects.CarPosition;
import com.eightman.autov.Objects.MyCar;
import com.eightman.autov.Simulation.SimulationView;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class CarDrawing extends AbstractDrawing {
    MyCar car;
    static Paint frontWheelsPaint;
    static Paint backWheelsPaint;
    Paint carPaint;
    Boundaries lastBoundries;

    public CarDrawing(SimulationView simulationView, MyCar car) {
        super(simulationView);
        this.car = car;

        if(frontWheelsPaint == null) {
            frontWheelsPaint = DrawingUtils.getLinePaint(Color.rgb(0xFF, 0xFF, 0xFF));
            backWheelsPaint = DrawingUtils.getLinePaint(Color.rgb(0xFF, 0xFF, 0xFF));
        }

        carPaint = DrawingUtils.getLinePaint(car.getCarCharacteristics().getColor());
    }

    @Override
    void onDraw(Canvas canvas) {
        CarPosition.Final carPosition = car.getCarPath().peekFirstPosition();
        if(carPosition == null) {
            return;
        }

        Boundaries boundaries = carPosition.getBoundaries();

        if(SimConfig.DRAW_WHEEL_PATH) {
            if(lastBoundries != null && !lastBoundries.equals(boundaries)) {
                DrawingUtils.drawLine(canvas, lastBoundries.getRightBack(), boundaries.getRightBack(), backWheelsPaint);
                DrawingUtils.drawLine(canvas, lastBoundries.getLeftBack(), boundaries.getLeftBack(), backWheelsPaint);
                DrawingUtils.drawLine(canvas, lastBoundries.getRightFront(), boundaries.getRightFront(), frontWheelsPaint);
                DrawingUtils.drawLine(canvas, lastBoundries.getLeftFront(), boundaries.getLeftFront(), frontWheelsPaint);
            }
            lastBoundries = boundaries;
        }

        DrawingUtils.drawBoundaries(canvas, boundaries, carPaint);
    }
}
