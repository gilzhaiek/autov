package com.eightman.autov.Simulation.Drawings;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Pair;

import com.eightman.autov.Configurations.Global;
import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Objects.Geom.Boundaries;
import com.eightman.autov.Objects.Geom.Circle;
import com.eightman.autov.Objects.Geom.LineSegment;
import com.eightman.autov.Objects.Geom.XY;
import com.eightman.autov.Utils.MathUtils;

/**
 * Created by gilzhaiek on 2016-11-03.
 */

public class DrawingUtils {
    public static int randomColor() {
        return Color.argb(255, MathUtils.getRandomInt(256), MathUtils.getRandomInt(256), MathUtils.getRandomInt(256));
    }

    public static Pair<XY, XY> getCarPlayground() {
        double halfWidthMeters = Global.canvasWidth / 2.0 / Global.pixelPerMoveUnit;
        double halfHeightMeters = Global.canvasHeight / 2.0 / Global.pixelPerMoveUnit;
        return new Pair(new XY(-halfWidthMeters, -halfHeightMeters), new XY(halfWidthMeters, halfHeightMeters));
    }

    public static float getScreenPoint(float carPosition, Axis axis) {
        float moveUnitPerPixel = (float) Global.pixelPerMoveUnit;

        if (axis == Axis.xAxis) {
            return moveUnitPerPixel * (carPosition + (float) Global.canvasWidth / 2.0f / moveUnitPerPixel) +
                    (float) Global.offsetX;
        } else {
            return moveUnitPerPixel * (-1 * carPosition + (float) Global.canvasHeight / 2.0f / moveUnitPerPixel) +
                    (float) Global.offsetY;
        }
    }

    public static boolean isInLaunchArea(XY point) {
        return (point.getX() < (SimConfig.EDGE_METERS * 2)) &&
                (point.getX() > (-1 * SimConfig.EDGE_METERS * 2)) &&
                (point.getY() < (SimConfig.EDGE_METERS * 2)) &&
                (point.getY() > (-1 * SimConfig.EDGE_METERS * 2));
    }

    public static boolean enteredLaunchArea(XY from, XY to) {
        if (isInLaunchArea(from)) {
            return false;  // Already in Launch Area
        }

        return isInLaunchArea(to);
    }

    public static boolean inOnScreen(XY point) {
        float x = getScreenPoint((float) point.getX(), Axis.xAxis);
        if (x > Global.canvasWidth || x < 0) {
            return false;
        }

        float y = getScreenPoint((float) point.getY(), Axis.yAxis);
        if (y > Global.canvasHeight || y < 0) {
            return false;
        }

        return true;
    }

    public static Paint getLinePaint(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(SimConfig.STROKE_WIDTH);
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }

    public static Paint getFillPaint(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);

        return paint;
    }

    public static void drawCircles(Canvas canvas, Circle[] circles, Paint paint) {
        for (Circle circle : circles) {
            drawCircle(canvas, circle, paint);
        }
    }

    public static void drawCircle(Canvas canvas, Circle circle, Paint paint) {
        if (circle != null) {
            XY center = circle.getCenter();
            canvas.drawCircle(
                    getScreenPoint((float) center.getX(), Axis.xAxis),
                    getScreenPoint((float) center.getY(), Axis.yAxis),
                    (float) (circle.getRadius() * Global.pixelPerMoveUnit),
                    paint);
        }
    }

    public static void drawLine(Canvas canvas, final LineSegment lineSegment, Paint paint) {
        if (lineSegment != null) {
            drawLine(canvas, lineSegment.getPointA(), lineSegment.getPointB(), paint);
        }
    }

    public static void drawLine(Canvas canvas,
                                final XY fromXY, final XY toXY,
                                Paint paint) {
        drawLine(canvas,
                (float) fromXY.getX(), (float) fromXY.getY(), (float) toXY.getX(), (float) toXY.getY(),
                paint);
    }

    public static void drawLine(Canvas canvas,
                                final float fromX, final float fromY, final float toX, final float toY,
                                Paint paint) {
        canvas.drawLine(
                getScreenPoint(fromX, Axis.xAxis),
                getScreenPoint(fromY, Axis.yAxis),
                getScreenPoint(toX, Axis.xAxis),
                getScreenPoint(toY, Axis.yAxis),
                paint);
    }

    public static void drawBoundaries(Canvas canvas, Boundaries boundaries, Paint paint) {
        if (boundaries == null) {
            return;
        }

        Path path = new Path();
        path.moveTo(getScreenPoint((float) boundaries.getRightFront().getX(), Axis.xAxis),
                getScreenPoint((float) boundaries.getRightFront().getY(), Axis.yAxis));
        path.lineTo(getScreenPoint((float) boundaries.getRightBack().getX(), Axis.xAxis),
                getScreenPoint((float) boundaries.getRightBack().getY(), Axis.yAxis));
        path.lineTo(getScreenPoint((float) boundaries.getLeftBack().getX(), Axis.xAxis),
                getScreenPoint((float) boundaries.getLeftBack().getY(), Axis.yAxis));
        path.lineTo(getScreenPoint((float) boundaries.getLeftFront().getX(), Axis.xAxis),
                getScreenPoint((float) boundaries.getLeftFront().getY(), Axis.yAxis));
        path.lineTo(getScreenPoint((float) boundaries.getRightFront().getX(), Axis.xAxis),
                getScreenPoint((float) boundaries.getRightFront().getY(), Axis.yAxis));

        canvas.drawPath(path, paint);
    }

    public enum Axis {
        yAxis, xAxis
    }
}
