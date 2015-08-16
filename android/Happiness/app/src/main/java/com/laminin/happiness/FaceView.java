package com.laminin.happiness;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;



/**
 * Created by franklin on 15/08/15.
 */
public class FaceView extends View {

    private static final String COLOR_HEX = "#0000FF"; // RRGGBB
    private final Paint mPaint;
    private float xPosition;
    private float yPosition;
    private float radius;
    private float strokeWidth = 4;
    private float defaultScale = 0.90f;

    private float eyeRadius = 30;
    private float eyeYPosition;
    private float leftEyeXPosition;
    private float rightEyeXPosition;



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
        radius = xPosition < yPosition ? xPosition : yPosition ;
        radius *= defaultScale;
        canvas.drawCircle(xPosition, yPosition, radius, mPaint);

        // lets draw eyes

        // lets find eye y position
        eyeYPosition = (float) (yPosition / 1.2);

        // lets find eye x position
        leftEyeXPosition = xPosition < yPosition ? xPosition / 2 : (float) (xPosition / 1.3);

        // lets find right eye x position
        rightEyeXPosition = xPosition < yPosition ? xPosition + xPosition / 2 : xPosition + xPosition / 4;

        // left eye
        canvas.drawCircle(leftEyeXPosition, eyeYPosition, eyeRadius, mPaint);

        // right eye
        canvas.drawCircle(rightEyeXPosition, eyeYPosition, eyeRadius, mPaint);


        // lets draw mouth.

        RectF oval = new RectF(leftEyeXPosition, yPosition + yPosition / 8, rightEyeXPosition, (float) (yPosition + yPosition / 2.5)); // left top right bottom


//        canvas.drawArc(oval, 200, 140, false, mPaint); // sad face.
        canvas.drawArc(oval, 10, 150, false, mPaint); // happy face.

    }


}
