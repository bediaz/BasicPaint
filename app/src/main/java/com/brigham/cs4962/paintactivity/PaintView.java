package com.brigham.cs4962.paintactivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Brigham on 9/3/2014.
 */
public class PaintView extends View {

    public RectF getContentRect() {
        return m_contentRect;
    }

    private RectF m_contentRect;
    private int m_color = Color.CYAN;
    private float m_radius;
    private boolean m_active;
    private Path m_path;

    private float mLastTouchX, mLastTouchY, mPosX, mPosY;

    // -*~-*~-*~-*~-*~-*~-*~-*~-*~
    public boolean isActive() { return m_active; }

    // -*~-*~-*~-*~-*~-*~-*~-*~-*~
    public void setActive(boolean m_active) {
        this.m_active = m_active;
       // invalidate();
//        if(this.m_active) {
//            this.setColor(Color.GREEN);
//        } else {
//            this.setColor(Color.CYAN);
//        }
    }

    // -*~-*~-*~-*~-*~-*~-*~-*~-*~
    public int getColor() {
        return m_color;
    }

    // -*~-*~-*~-*~-*~-*~-*~-*~-*~
    public void setColor(int m_color) {
        this.m_color = m_color;
        invalidate();
    }

    OnSplotchTouchListener m_onSplotchTouchListener = null;
    OnSplotchReleaseListener m_onSplotchReleaseListener = null;

    // -*~-*~-*~-*~-*~-*~-*~-*~-*~
    public OnSplotchTouchListener getOnSplotchTouchListener() {
        return m_onSplotchTouchListener;
    }
    public OnSplotchReleaseListener getOnSplotchReleaseListener() { return m_onSplotchReleaseListener; }

    // -*~-*~-*~-*~-*~-*~-*~-*~-*~
    public interface OnSplotchTouchListener { public void onSplotchTouched(PaintView v); }
    public interface OnSplotchReleaseListener { public void onSplotchReleased(PaintView v); }

    // -*~-*~-*~-*~-*~-*~-*~-*~-*~
    public void setOnSplotchTouchListener(OnSplotchTouchListener m_onSplotchTouchListener) { this.m_onSplotchTouchListener = m_onSplotchTouchListener; }
    public void setOnSplotchReleaseListener(OnSplotchReleaseListener m_onSplotchReleaseListener) { this.m_onSplotchReleaseListener = m_onSplotchReleaseListener; }
    // -*~-*~-*~-*~-*~-*~-*~-*~-*~


    // -*~-*~-*~-*~-*~-*~-*~-*~-*~


    // -*~-*~-*~-*~-*~-*~-*~-*~-*~






    // -*~-*~-*~-*~-*~-*~-*~-*~-*~
    public PaintView(Context context) {
        super(context);
        setMinimumWidth(100);
        setMinimumHeight(100);
        setPadding(0, 0, 0, 0);
    }

    private float m_posX, m_posY;

    public boolean isOverSplotch(RectF contentRect, float xPos2, float yPos2) {

        int w = getWidth() / 2;
        int h = getHeight() / 2;
        float centerXPos = getX() + w;
        float centerYPos = getY() + h;

        float centerXPos2 = xPos2 + contentRect.centerX();
        float centerYPos2 = yPos2 + contentRect.centerY();

        float distance = (float) Math.sqrt(
                (centerXPos2 - centerXPos) * (centerXPos2 - centerXPos) +
                        (centerYPos2 - centerYPos) * (centerYPos2 - centerYPos));


        if (distance < m_radius) {
//            if (m_onSplotchTouchListener != null) {
//                m_onSplotchTouchListener.onSplotchTouched(this);
//            }

            return true;
        } else {
            Log.i("test", "touch not in circle, color=" + getColor());
            return false;
        }
    }

    @Override
    // -*~-*~-*~-*~-*~-*~-*~-*~-*~
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        final float x = event.getRawX();
        final float y = event.getRawY();

        isOverSplotch(m_contentRect, getX(), getY());

        switch(action) {
            case MotionEvent.ACTION_DOWN: {
                mLastTouchX = x;
                mLastTouchY = y;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                float dx = x - mLastTouchX;
                float dy = y - mLastTouchY;

                mPosX += dx;
                mPosY += dy;

                this.setTranslationX(mPosX);
                this.setTranslationY(mPosY);

                invalidate();
                mLastTouchX = x;
                mLastTouchY = y;
                break;
            }

            case MotionEvent.ACTION_UP: {
                mPosX = 0;
                mPosY = 0;

                float centerXPos = getX() + m_contentRect.centerX();
                float centerYPos = getY() + m_contentRect.centerY();

                float distance = (float) Math.sqrt(
                        (centerXPos - centerXPos) * (centerXPos - centerXPos) +
                                (centerYPos - centerYPos) * (centerYPos - centerYPos));


                if (distance < m_radius) {
                    if (m_onSplotchTouchListener != null) {
                        m_onSplotchTouchListener.onSplotchTouched(this);
                    }
                }

                m_onSplotchReleaseListener.onSplotchReleased(this);
                float left = getLeft();
                float top = getTop();
                this.setX(left);
                this.setY(top);

                invalidate();
                mLastTouchX = left;
                mLastTouchY = top;
                break;
            }
        }

        return super.onTouchEvent(event);
    }

    // -*~-*~-*~-*~-*~-*~-*~-*~-*~
    private void drawBorder(Canvas canvas) {
        Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStyle(Paint.Style.STROKE);

        borderPaint.setStrokeWidth(10f);
        canvas.drawPath(m_path, borderPaint);
        borderPaint.setColor(Color.WHITE);
        borderPaint.setStrokeWidth(5f);
        canvas.drawPath(m_path, borderPaint);
    }

    // -*~-*~-*~-*~-*~-*~-*~-*~-*~
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(m_color);

        // no reason to create new random splotches everytime it invalidates
        if(m_path != null) {
            canvas.drawPath(m_path, linePaint);

            if(isActive()) {
                drawBorder(canvas);
            }

            return;
        }
        m_path = new Path();

        m_contentRect = new RectF();
        m_contentRect.left = getPaddingLeft();
        m_contentRect.top = getPaddingTop();
        m_contentRect.right = getWidth() - getPaddingRight();
        m_contentRect.bottom = getHeight() - getPaddingBottom();


        PointF center = new PointF(m_contentRect.centerX(), m_contentRect.centerY());

        float maxRadius = Math.min(m_contentRect.width() * 0.4f, m_contentRect.height() * 0.4f);
        float minRadius = 0.3f * maxRadius;

        center.x = (float) getWidth() / 2.0f;
        center.y = (float) getHeight() / 2.0f;
        m_radius = minRadius + (maxRadius - minRadius) * 0.5f;

        int pointCount = 75;
        for (int pointIndex = 0; pointIndex < pointCount; pointIndex += 4) {

            m_radius += ((Math.random() - 0.25) * 2.0f) * (maxRadius - m_radius);

            PointF point = new PointF();
            point.x = center.x + m_radius *
                    (float) Math.cos(((double) pointIndex / (double) pointCount) * 2.0f * Math.PI);
            point.y = center.y + m_radius *
                    (float) Math.sin(((double) pointIndex / (double) pointCount) * 2.0f * Math.PI);

            PointF control1 = new PointF();
            float control1Radius = m_radius + (float) (Math.random() - 0.25) * 2.0f * 20.0f;
            control1.x = center.x + control1Radius *
                    (float) Math.cos(((double) pointIndex / (double) pointCount) * 2.0f * Math.PI);
            control1.y = center.y + control1Radius *
                    (float) Math.sin(((double) pointIndex / (double) pointCount) * 2.0f * Math.PI);

            PointF control2 = new PointF();
            float control2Radius = m_radius + (float) (Math.random() - 0.25) * 2.0f * 20.0f;
            control2.x = center.x + control2Radius *
                    (float) Math.cos(((double) pointIndex / (double) pointCount) * 2.0f * Math.PI);
            control2.y = center.y + control2Radius *
                    (float) Math.sin(((double) pointIndex / (double) pointCount) * 2.0f * Math.PI);

            if (pointIndex == 0) {
                m_path.moveTo(point.x, point.y);
            }
            else {
                m_path.cubicTo(control1.x, control1.y, control2.x, control2.y, point.x, point.y);
            }
        }

        canvas.drawPath(m_path, linePaint);
        if(isActive()) {
            drawBorder(canvas);
        }



    }

    @Override
    // -*~-*~-*~-*~-*~-*~-*~-*~-*~
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpec = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpec = MeasureSpec.getSize(heightMeasureSpec);

        int width = getSuggestedMinimumWidth();//.MAX_VALUE;
        int height = getSuggestedMinimumHeight();//.MAX_VALUE;

        if(widthMode == MeasureSpec.AT_MOST) {
            width = widthSpec;
        }
        if(heightMode == MeasureSpec.AT_MOST) {
            height = heightSpec;
        }

        if(widthMode == MeasureSpec.EXACTLY) {
            width = widthSpec;
            height = width;
        }
        if(heightMode == MeasureSpec.EXACTLY) {
            height = heightSpec;
            width = height;
        }

        // TODO: respect padding!
        if(width > height && widthMode != MeasureSpec.EXACTLY) {
            width = height;
        }
        if(height > width && heightMode != MeasureSpec.EXACTLY) {
            height = width;
        }

        width -= (getPaddingLeft() + getPaddingRight());
        height -= (getPaddingTop() + getPaddingBottom());


        setMeasuredDimension(
                resolveSizeAndState(width, widthMeasureSpec, width < getSuggestedMinimumWidth() ? MEASURED_STATE_TOO_SMALL : 0),
                resolveSizeAndState(height, heightMeasureSpec, height < getSuggestedMinimumHeight() ? MEASURED_STATE_TOO_SMALL : 0));
    }
}
