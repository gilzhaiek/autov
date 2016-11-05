package com.eightman.autov.Simulation.Drawings;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.eightman.autov.Configurations.Global;
import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Hardware.Boundaries;
import com.eightman.autov.Utils.XY;

/**
 * Created by gilzhaiek on 2016-11-03.
 */

public class DrawingUtils {
    public static Paint getLinePaint(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(SimConfig.STROKE_WIDTH);
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }

    public static void drawLine(Canvas canvas,
                                 final XY fromXY, final XY toXY,
                                 Paint paint) {
        drawLine(canvas,
                (float)fromXY.getX(), (float)fromXY.getY(), (float)toXY.getX(), (float)toXY.getY(),
                paint);
    }

    public static void drawLine(Canvas canvas,
                                 final float fromX, final float fromY, final float toX, final float toY,
                                 Paint paint) {
        float moveUnitPerPixel = (float) SimConfig.PIXEL_PER_MOVE_UNIT;
        canvas.drawLine(
                moveUnitPerPixel * (fromX + (float)Global.canvasWidth / 2.0f / moveUnitPerPixel),
                moveUnitPerPixel * (-1 * fromY + (float)Global.canvasHeight / 2.0f / moveUnitPerPixel),
                moveUnitPerPixel * (toX + (float)Global.canvasWidth / 2.0f / moveUnitPerPixel),
                moveUnitPerPixel * (-1 * toY + (float)Global.canvasHeight / 2.0f / moveUnitPerPixel), paint);
    }

    public static void drawBoundaries(Canvas canvas, Boundaries boundaries, Paint paint) {
        drawLine(canvas, boundaries.getRightFront(), boundaries.getRightBack(), paint);
        drawLine(canvas, boundaries.getRightBack(), boundaries.getLeftBack(), paint);
        drawLine(canvas, boundaries.getLeftBack(), boundaries.getLeftFront(), paint);
        drawLine(canvas, boundaries.getLeftFront(), boundaries.getRightFront(), paint);
    }

}
