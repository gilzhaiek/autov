package com.eightman.autov.Simulation.Drawings;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Hardware.Boundaries;
import com.eightman.autov.Objects.CarPosition;
import com.eightman.autov.Objects.Collision;
import com.eightman.autov.Objects.MyCar;
import com.eightman.autov.Simulation.SimulationView;
import com.eightman.autov.ai.CollisionManager;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class CarDrawing extends AbstractDrawing {
    public static final String TAG = CarDrawing.class.getSimpleName();

    MyCar car;
    static Paint frontWheelsPaint;
    static Paint backWheelsPaint;
    Paint carPaint;
    Paint headingPaint;
    Paint collisionPaint;
    Paint safeZoneClearPaint;
    Paint safeZoneDirtyPaint;
    Boundaries lastBoundaries;
    CollisionManager collisionManager;

    public CarDrawing(SimulationView simulationView, MyCar car) {
        super(simulationView);
        this.car = car;

        collisionManager = CollisionManager.getInstance();

        if (frontWheelsPaint == null) {
            frontWheelsPaint = DrawingUtils.getLinePaint(Color.rgb(0xFF, 0xFF, 0xFF));
            backWheelsPaint = DrawingUtils.getLinePaint(Color.rgb(0xFF, 0xFF, 0xFF));
        }

        carPaint = DrawingUtils.getLinePaint(car.getCarCharacteristics().getColor());
        headingPaint = DrawingUtils.getFillPaint(Color.argb(0x77, 0xA1, 0x00, 0x1E));
        collisionPaint = DrawingUtils.getFillPaint(Color.argb(0x77, 0xFF, 0xFF, 0x00));
        safeZoneClearPaint = DrawingUtils.getFillPaint(Color.argb(0x33, 0xFF, 0xFF, 0xFF));
        safeZoneDirtyPaint = DrawingUtils.getFillPaint(Color.argb(0x77, 0xFF, 0xFF, 0xFF));
    }

    @Override
    void onDraw(Canvas canvas) {
        CarPosition carPosition = car.getCarPosition();
        if (carPosition == null) {
            return;
        }

        /*Log.d(TAG,
                "UUID=" + car.getUuid() +
                        " id=" + carPosition.getId() +
                        " linkSize=" + carPosition.getLinkSize() +
                        " speed=" + carPosition.getSpeed() +
                        " time=" + SimTime.getInstance().getTime() +
                        " timeToNP=" + carPosition.getTimeToNextPosition());*/

        Boundaries boundaries = carPosition.getBoundaries();

        boolean isInCollisions = collisionManager.isInCollision(car);
        boolean isInSafeZone = collisionManager.isInSafeZone(car);

        car.setInAccident(isInCollisions);

        DrawingUtils.fillBoundaries(canvas, carPosition.getSafeZone(),
                isInSafeZone ? safeZoneDirtyPaint : safeZoneClearPaint);

        DrawingUtils.fillBoundaries(canvas, carPosition.getCollisionZone(),
                isInCollisions ? collisionPaint : headingPaint);

        Collision nextCollision = CollisionManager.getInstance().getFirstCollision(carPosition);
        if(nextCollision != null) {
            DrawingUtils.fillBoundaries(
                    canvas,
                    nextCollision.getCollisionPositionActive().getPosition().getBoundaries(),
                    collisionPaint);
        }

        if (SimConfig.DRAW_WHEEL_PATH) {
            if (lastBoundaries != null && !lastBoundaries.equals(boundaries)) {
                DrawingUtils.drawLine(canvas, lastBoundaries.getRightBack(), boundaries.getRightBack(), backWheelsPaint);
                DrawingUtils.drawLine(canvas, lastBoundaries.getLeftBack(), boundaries.getLeftBack(), backWheelsPaint);
                DrawingUtils.drawLine(canvas, lastBoundaries.getRightFront(), boundaries.getRightFront(), frontWheelsPaint);
                DrawingUtils.drawLine(canvas, lastBoundaries.getLeftFront(), boundaries.getLeftFront(), frontWheelsPaint);
            }
            lastBoundaries = boundaries;
        }

        DrawingUtils.drawBoundaries(canvas, boundaries, carPaint);
    }
}
