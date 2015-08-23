package com.laminin.happiness;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;



/**
 * Created by franklin on 15/08/15.
 */
public class FaceView extends View {

    private static final String COLOR_HEX = "#0000FF"; // RRGGBB
    private final Paint mPaint;
    private float xPosition;
    private float yPosition;
    private float faceRadius;
    private static final float strokeWidth = 4;
    private float defaultScale = 0.90f;

    private float eyeRadius = 30; // default value.
    private float eyeYPosition;
    private float leftEyeXPosition;
    private float rightEyeXPosition;


    private static final float FACE_RADIUS_TO_EYE_RADIUS_RATIO = 10;
    private static final float FACE_RADIUS_TO_EYE_OFFSET_RATIO = 3;
    private static final float FACE_RADIUS_TO_EYE_SEPARATION_RATIO = 2.5f;

    private static final float FACE_RADIUS_TO_MOUTH_WIDTH_RATIO = 1;
    private static final float FACE_RADIUS_TO_MOUTH_HEIGHT_RATIO = 2;
    private static final float FACE_RADIUS_TO_MOUTH_X_OFFSET_RATIO = 2;
    private static final float FACE_RADIUS_TO_MOUTH_Y_OFFSET_RATIO = 10;

    private Float mouthWidth;
    private Float mouthHeight = 0.0f;
    private Float mouthMaximumHeight;

    private Float mouthRectLeft;
    private Float mouthRectTop;
    private Float mouthRectRight;
    private Float mouthRectBottom;

    float mLastTouchX = 0;
    float mLastTouchY = 0;
    int mActivePointerId = 0;

    float maximumFaceRadius = 0;
    float minimumFaceRadius = 20;

    float initialDistance = 0;
    float currentDistance = 0;

    int index;
    int action;
    int pointerId;

    public FaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.parseColor(COLOR_HEX));
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPaint(mPaint);

        // drawing outer circle
        // lets setup x cord, y cord, radius
        // x, y position should point to center.
        // radius should be half the width / height

        xPosition = getMeasuredWidth() / 2;
        yPosition = getMeasuredHeight() / 2;


        if (maximumFaceRadius == 0) {
            faceRadius = Math.min(xPosition, yPosition) * defaultScale;
            maximumFaceRadius = faceRadius;
        }

        canvas.drawCircle(xPosition, yPosition, faceRadius, mPaint);

        // lets draw eyes

        // lets set the eye radius:
        eyeRadius = faceRadius / FACE_RADIUS_TO_EYE_RADIUS_RATIO;

        // lets find eye y position
        eyeYPosition = yPosition - (faceRadius / FACE_RADIUS_TO_EYE_OFFSET_RATIO);

        // lets find eye x position
        leftEyeXPosition = xPosition + (faceRadius / FACE_RADIUS_TO_EYE_SEPARATION_RATIO);

        // lets find right eye x position
        rightEyeXPosition = xPosition - (faceRadius / FACE_RADIUS_TO_EYE_SEPARATION_RATIO);
        // left eye
        canvas.drawCircle(leftEyeXPosition, eyeYPosition, eyeRadius, mPaint);

        // right eye
        canvas.drawCircle(rightEyeXPosition, eyeYPosition, eyeRadius, mPaint);

        // lets draw mouth.

        mouthWidth = faceRadius / FACE_RADIUS_TO_MOUTH_WIDTH_RATIO;
        mouthMaximumHeight = faceRadius / FACE_RADIUS_TO_MOUTH_HEIGHT_RATIO;

        if (mouthHeight == 0.0f) { // initial
            mouthHeight = mouthMaximumHeight;
        }

        mouthRectLeft = xPosition - (faceRadius / FACE_RADIUS_TO_MOUTH_X_OFFSET_RATIO);
        mouthRectTop = yPosition + (faceRadius / FACE_RADIUS_TO_MOUTH_Y_OFFSET_RATIO);
        mouthRectRight = mouthRectLeft + mouthWidth;
        mouthRectBottom = mouthRectTop + Math.abs(mouthHeight);

        RectF ovalRect = new RectF(mouthRectLeft, mouthRectTop, mouthRectRight, mouthRectBottom); // left top right bottom


        if (mouthHeight < 0) { // bottom - up - happy
            canvas.drawArc(ovalRect, 0, 180, false, mPaint);
        } else if (mouthHeight > 0) { // up - bottom - sad
            canvas.drawArc(ovalRect, 180, 180, false, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean singleTouch = event.getPointerCount() > 1 ? false : true;

        index = event.getActionIndex();
        action = event.getActionMasked();
        pointerId = event.getPointerId(index);


        final int pointerIndex;
        final float x;
        final float y;


        int secondaryPointerId = 0;
        final int secondaryPointerIndex;
        final float secondaryX;
        final float secondaryY;

        float distance;

        switch (action) {
            case MotionEvent.ACTION_DOWN:

                if (singleTouch) { // for pan/drag event.

                    pointerIndex = MotionEventCompat.getActionIndex(event);
                    x = MotionEventCompat.getX(event, pointerIndex);
                    y = MotionEventCompat.getY(event, pointerIndex);

                    // Remember where we started (for dragging)
                    mLastTouchX = x;
                    mLastTouchY = y;
                    // Save the ID of this pointer (for dragging)
                    mActivePointerId = MotionEventCompat.getPointerId(event, 0);

                } else { // for pinch  event.
                    // mActivePointerId is the primary finger pointer id.
                    pointerIndex = MotionEventCompat.getActionIndex(event);
                    mActivePointerId = MotionEventCompat.getPointerId(event, 0);
                    x = MotionEventCompat.getX(event, pointerIndex);
                    y = MotionEventCompat.getY(event, pointerIndex);

                    secondaryPointerId = MotionEventCompat.getPointerId(event, 1); // is the secondary finger pointer id.
                    secondaryPointerIndex = MotionEventCompat.findPointerIndex(event, secondaryPointerId);
                    secondaryX = MotionEventCompat.getX(event, secondaryPointerIndex);
                    secondaryY = MotionEventCompat.getY(event, secondaryPointerIndex);

                    // distance between primary and secondary fingers
                    // squareroot(square(x2-x1) + square(y2-y1))

                    initialDistance = (float) Math.sqrt(((secondaryX - x) * (secondaryX - x)) + ((secondaryY - y) * (secondaryY - y)));

                }
                break;
            case MotionEvent.ACTION_MOVE:

                if (singleTouch) {

                    // lets implement drag/pan event.
                    // we need to change the height of the mouth and redraw the circle.

                    // Find the index of the active pointer and fetch its position

                    pointerIndex = MotionEventCompat.findPointerIndex(event, mActivePointerId);

                    if (pointerIndex >= 0) { // to avoid pointer index exception.
                        x = MotionEventCompat.getX(event, pointerIndex);
                        y = MotionEventCompat.getY(event, pointerIndex);

                        // Calculate the distance moved
                        final float dx = x - mLastTouchX;
                        final float dy = y - mLastTouchY;

                        // here we need to set the height of the mouth.
                        // lets find y boundary to detect this motion.

//                        if (y > yPosition - faceRadius && y < yPosition + faceRadius) { // change the mouth height only if the event occur inside the face.
//                            mouthHeight = mouthMaximumHeight * (dy / 1000);
//                            invalidate(); // redraw
//                        }
                        mouthHeight = mouthMaximumHeight * (dy / 1000);
                        invalidate(); // redraw

                    }
                } else {
                    // lets find the both the finger index.
                    // pointer index is the primary finger index

                    mActivePointerId = MotionEventCompat.getPointerId(event, 0);
                    pointerIndex = MotionEventCompat.findPointerIndex(event, mActivePointerId);
                    x = MotionEventCompat.getX(event, pointerIndex);
                    y = MotionEventCompat.getY(event, pointerIndex);

                    secondaryPointerId = MotionEventCompat.getPointerId(event, 1);
                    secondaryPointerIndex = MotionEventCompat.findPointerIndex(event, secondaryPointerId);
                    secondaryX = MotionEventCompat.getX(event, secondaryPointerIndex);
                    secondaryY = MotionEventCompat.getY(event, secondaryPointerIndex);


                    if (initialDistance == 0) {
                        initialDistance = (float) Math.sqrt(((secondaryX - x) * (secondaryX - x)) + ((secondaryY - y) * (secondaryY - y)));
                    }
                    distance = (float) Math.sqrt(((secondaryX - x) * (secondaryX - x)) + ((secondaryY - y) * (secondaryY - y)));

                    currentDistance = Math.abs(initialDistance - distance);

                    faceRadius = Math.max(minimumFaceRadius, Math.min(maximumFaceRadius, currentDistance));
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
                initialDistance = 0;
                break;
            case MotionEvent.ACTION_CANCEL:
                initialDistance = 0;
                break;
        }

        return true;

    }
}
