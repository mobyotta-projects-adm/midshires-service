package com.midshire.midshireservice.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class SignatureView extends View {

    private ArrayList<Path> mPaths = new ArrayList<>();
    private Paint mPaint;
    private Path mCurrentPath;

    public SignatureView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(8f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Path path : mPaths) {
            canvas.drawPath(path, mPaint);
        }
        if (mCurrentPath != null) {
            canvas.drawPath(mCurrentPath, mPaint);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDown(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                break;
        }
        invalidate();
        return true;
    }

    private void touchDown(float x, float y) {
        mCurrentPath = new Path();
        mCurrentPath.moveTo(x, y);
        mPaths.add(mCurrentPath);
    }

    private void touchMove(float x, float y) {
        mCurrentPath.lineTo(x, y);
    }

    private void touchUp() {
        mCurrentPath = null;
    }

    public void clearSignature() {
        mPaths.clear();
        invalidate();
    }

    public Bitmap getSignatureBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        draw(canvas);
        return bitmap;
    }
}

