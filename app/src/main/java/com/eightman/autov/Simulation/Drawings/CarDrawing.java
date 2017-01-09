package com.eightman.autov.Simulation.Drawings;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Objects.CarPosition;
import com.eightman.autov.Objects.Geom.Boundaries;
import com.eightman.autov.Objects.Geom.Circle;
import com.eightman.autov.Objects.Geom.LineSegment;
import com.eightman.autov.Objects.MyCar;
import com.eightman.autov.Objects.ObjectDistanceInfo;
import com.eightman.autov.Simulation.SimulationView;
import com.eightman.autov.Utils.TrigUtils;
import com.eightman.autov.ai.CollisionManager;

import java.util.List;

/**
 * Created by gilzhaiek on 2016-10-25.
 */

public class CarDrawing extends AbstractDrawing {
    public static final String TAG = CarDrawing.class.getSimpleName();

    MyCar car;
    static Paint frontWheelsPaint;
    static Paint backWheelsPaint;
    Paint carPaint;
    Paint lastPosPaint;
    Paint headingPaint;
    Paint maxCirclePaint;
    Paint turningCirclePaint;
    Paint collisionPaint;
    Paint activeDistancePaint;
    Paint passiveDistancePaint;
    Paint whitePaint;
    Boundaries prevBoundaries;
    CollisionManager collisionManager;

    public CarDrawing(SimulationView simulationView, MyCar car) {
        super(simulationView);
        this.car = car;

        collisionManager = CollisionManager.getInstance();

        if (frontWheelsPaint == null) {
            frontWheelsPaint = DrawingUtils.getLinePaint(Color.rgb(0xFF, 0xFF, 0xFF));
            backWheelsPaint = DrawingUtils.getLinePaint(Color.rgb(0xFF, 0xFF, 0xFF));
        }

        lastPosPaint = DrawingUtils.getLinePaint(car.getCarCharacteristics().getColor());
        carPaint = DrawingUtils.getFillPaint(car.getCarCharacteristics().getColor());
        headingPaint = DrawingUtils.getLinePaint(Color.argb(0x77, 0xA1, 0x00, 0x1E));
        maxCirclePaint = DrawingUtils.getLinePaint(Color.argb(0x44, 0xE3, 0xFC, 0x7E));
        turningCirclePaint = DrawingUtils.getLinePaint(Color.argb(0x99, 0xE3, 0xFC, 0x7E));
        collisionPaint = DrawingUtils.getLinePaint(Color.YELLOW);
        activeDistancePaint = DrawingUtils.getLinePaint(Color.rgb(0xa5, 0x32, 0xdb));
        passiveDistancePaint = DrawingUtils.getLinePaint(Color.CYAN);
        whitePaint = DrawingUtils.getLinePaint(Color.rgb(0xFF, 0xFF, 0xFF));
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

        Circle[] fromCircles = carPosition.getBoundaries().getMaxTurningCircles();
        DrawingUtils.drawCircles(canvas, fromCircles, maxCirclePaint);
        DrawingUtils.drawCircle(canvas, carPosition.getBoundaries().getTurningCircle(), turningCirclePaint);

        List<ObjectDistanceInfo> distanceInfoList = carPosition.getCarDistancesInfo();
        for (ObjectDistanceInfo objectDistanceInfo : distanceInfoList) {
            if (objectDistanceInfo.getSegmentType() == ObjectDistanceInfo.SegmentType.COLLISION_ZONE) {
                DrawingUtils.drawLine(canvas, objectDistanceInfo.getLineSegment(), collisionPaint);
            } else if (objectDistanceInfo.getSegmentType() == ObjectDistanceInfo.SegmentType.DECISION_ZONE_ACTIVE) {
                DrawingUtils.drawLine(canvas, objectDistanceInfo.getLineSegment(), activeDistancePaint);
            } else {
                DrawingUtils.drawLine(canvas, objectDistanceInfo.getLineSegment(), passiveDistancePaint);
            }
        }

        Boundaries boundaries = carPosition.getBoundaries();

        //car.setInAccident(isInCollisions);

        if (SimConfig.DRAW_WHEEL_PATH) {
            if (prevBoundaries != null && !prevBoundaries.equals(boundaries)) {
                DrawingUtils.drawLine(canvas, prevBoundaries.getRightBack(), boundaries.getRightBack(), backWheelsPaint);
                DrawingUtils.drawLine(canvas, prevBoundaries.getLeftBack(), boundaries.getLeftBack(), backWheelsPaint);
                DrawingUtils.drawLine(canvas, prevBoundaries.getRightFront(), boundaries.getRightFront(), frontWheelsPaint);
                DrawingUtils.drawLine(canvas, prevBoundaries.getLeftFront(), boundaries.getLeftFront(), frontWheelsPaint);
            }
            prevBoundaries = boundaries;
        }

        DrawingUtils.drawBoundaries(canvas, boundaries, carPaint);
        Circle[] toCircles = carPosition.getLast().getBoundaries().getMaxTurningCircles();
        DrawingUtils.drawCircles(canvas, toCircles, maxCirclePaint);
        DrawingUtils.drawBoundaries(canvas, carPosition.getLast().getBoundaries(), lastPosPaint);
        LineSegment segments[] = new LineSegment[2];
        //if (myMaxCircles[0].getDirection() == toCircles[0].getDirection()) {
        segments[0] = TrigUtils.findOuterTangents(fromCircles[0], toCircles[0]);
        segments[1] = TrigUtils.findOuterTangents(fromCircles[1], toCircles[1]);
        DrawingUtils.drawLine(canvas, segments[0], whitePaint);
        DrawingUtils.drawLine(canvas, segments[1], whitePaint);
    }
}
