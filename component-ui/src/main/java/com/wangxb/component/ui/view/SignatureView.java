package com.wangxb.component.ui.view;

import com.wangxb.component.ui.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;

public class SignatureView extends View {
    private final Paint mPaint;
    private final float mPaintHalfWidth;
    private final Path mPath;
    private final RectF mDirtyRect;
    private float mLastTouchX;
    private float mLastTouchY;

    public SignatureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        int paintColor = Color.BLACK;
        float paintWidth = 5f;
        if (attrs != null) {
            TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.SignatureView);
            paintColor = attributes.getColor(R.styleable.SignatureView_paintColor, Color.BLACK);
            paintWidth = attributes.getDimensionPixelSize(R.styleable.SignatureView_paintWidth, 5);
            attributes.recycle();
        }

        mPaintHalfWidth = paintWidth / 2;

        mPath = new Path();
        mDirtyRect = new RectF();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(paintColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(paintWidth);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(x, y);
                mLastTouchX = x;
                mLastTouchY = y;
                return true;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                resetDirtyRect(x, y);
                int historySize = event.getHistorySize();
                for (int i = 0; i < historySize; i++) {
                    float historicalX = event.getHistoricalX(i);
                    float historicalY = event.getHistoricalY(i);
                    expandDirtyRect(historicalX, historicalY);
                    mPath.lineTo(historicalX, historicalY);
                }
                mPath.lineTo(x, y);
                break;
            default:
                return false;
        }

        invalidate((int) (mDirtyRect.left - mPaintHalfWidth),
                (int) (mDirtyRect.top - mPaintHalfWidth),
                (int) (mDirtyRect.right - mPaintHalfWidth),
                (int) (mDirtyRect.bottom - mPaintHalfWidth));
        mLastTouchX = x;
        mLastTouchY = y;
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(mPath, mPaint);
    }

    private void expandDirtyRect(float historicalX, float historicalY) {
        if (historicalX < mDirtyRect.left) {
            mDirtyRect.left = historicalX;
        } else if (historicalX > mDirtyRect.right) {
            mDirtyRect.right = historicalX;
        }

        if (historicalY < mDirtyRect.top) {
            mDirtyRect.top = historicalY;
        } else if (historicalY > mDirtyRect.bottom) {
            mDirtyRect.bottom = historicalY;
        }
    }

    private void resetDirtyRect(float x, float y) {
        mDirtyRect.left = Math.min(mLastTouchX, x);
        mDirtyRect.right = Math.max(mLastTouchX, x);
        mDirtyRect.top = Math.min(mLastTouchY, y);
        mDirtyRect.bottom = Math.max(mLastTouchY, y);
    }

    @SuppressWarnings("unused")
    @MainThread
    public void clearSignature() {
        mPath.reset();
        invalidate();
    }

    @SuppressWarnings("unused")
    @MainThread
    public Bitmap getSignature() {
        setDrawingCacheEnabled(true);
        buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(getDrawingCache());
        setDrawingCacheEnabled(false);
        return bitmap;
    }
}
