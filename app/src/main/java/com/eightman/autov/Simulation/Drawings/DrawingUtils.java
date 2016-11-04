package com.eightman.autov.Simulation.Drawings;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Hardware.Boundaries;
import com.eightman.autov.Utils.XY;

/**
 * Created by gilzhaiek on 2016-11-03.
 */

public class DrawingUtils {
    protected static Paint getLinePaint(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(SimConfig.STROKE_WIDTH);
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }

    private static void drawLine(Canvas canvas,
                                 final XY fromXY, final XY toXY,
                                 final float canvasWidth, final float canvasHeight,
                                 Paint paint) {
        drawLine(canvas, (float)fromXY.getX(), (float)fromXY.getY(), (float)toXY.getX(), (float)toXY.getY(),
                canvasWidth, canvasHeight, paint);
    }

    private static void drawLine(Canvas canvas,
                                 final float fromX, final float fromY, final float toX, final float toY,
                                 final float canvasWidth, final float canvasHeight,
                                 Paint paint) {
        float moveUnitPerPixel = (float) SimConfig.PIXEL_PER_MOVE_UNIT;
        canvas.drawLine(
                moveUnitPerPixel * (fromX + canvasWidth / 2.0f / moveUnitPerPixel),
                moveUnitPerPixel * (-1 * fromY + canvasHeight / 2.0f / moveUnitPerPixel),
                moveUnitPerPixel * (toX + canvasWidth / 2.0f / moveUnitPerPixel),
                moveUnitPerPixel * (-1 * toY + canvasHeight / 2.0f / moveUnitPerPixel), paint);
    }

    private static void drawBoundaries(Canvas canvas,
                                Boundaries boundaries,
                                final float canvasWidth, final float canvasHeight,
                                Paint paint) {
        drawLine(canvas,
                boundaries.getRightFront(), boundaries.getRightBack(),
                canvasWidth, canvasHeight,
                paint);

        drawLine(canvas,
                boundaries.getRightBack(), boundaries.getLeftBack(),
                canvasWidth, canvasHeight,
                paint);

        drawLine(canvas,
                boundaries.getLeftBack(), boundaries.getLeftFront(),
                canvasWidth, canvasHeight,
                paint);

        drawLine(canvas,
                boundaries.getLeftFront(), boundaries.getRightFront(),
                canvasWidth, canvasHeight,
                paint);
    }

}
