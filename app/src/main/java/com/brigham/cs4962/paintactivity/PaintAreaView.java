package com.brigham.cs4962.paintactivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brigham on 9/21/2014.
 */
public class PaintAreaView extends View implements OnTouchListener {

    private final String TAG = "PaintAreaView";
    Path m_paths;
    Path m_currentPath;
    Paint m_linePaint;

    List<Float> x_Points;
    List<Float> y_Points;

   // int m_strokeColor;

    int m_viewWidth, m_viewHeight;

    public ArrayList<DrawElement> getDrawElements() {
        return drawElements;
    }

    public void setDrawElements(ArrayList<DrawElement> drawElements) {
        if(drawElements != null) { this.drawElements = drawElements; }
    }

    ArrayList<DrawElement> drawElements;

    public PaintAreaView(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
        m_linePaint = defaultPaintStyle();
        m_paths = new Path();
        x_Points = new ArrayList<Float>();
        y_Points = new ArrayList<Float>();

        m_currentPath = new Path();
        drawElements = new ArrayList<DrawElement>();

        setOnTouchListener(this);
    }

    public void setStrokeColor(int newColor) {
        // sanity check since this can get called from other classes
        if(this.m_linePaint == null) {
            this.m_linePaint = defaultPaintStyle();
        }

        this.m_linePaint.setColor(newColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.i(TAG, "onSizeChanged, width=" + w + ",height=" + h);

        // get the width, height
        m_viewWidth = this.getMeasuredWidth();
        m_viewHeight = h;
        invalidate();
    }

    private Paint defaultPaintStyle() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLACK);
        return paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.scale(1.0f, 1.0f);

        for (DrawElement element : drawElements) {
            Path p = element.getPath();
            int c = element.getColor();
            m_linePaint.setColor(c);
            canvas.drawPath(p, m_linePaint);
        }

        // draw current line that hasn't been added to DrawElements yet.
        //m_linePaint.setColor(this.m_strokeColor);
        canvas.drawPath(m_currentPath, m_linePaint);

        canvas.restore();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        int action = event.getActionMasked();
        float x = event.getX();// / m_viewWidth; // scale between 0 < x < 1
        float y = event.getY();// / m_viewHeight; // scale between 0 < y < 1

        String[] actionNames = {
                "ACTION_DOWN",
                "ACTION_UP",
                "ACTION_MOVE",
                "ACTION_CANCEL",
                "ACTION_OUTSIDE"
        };

        switch (action) {
            case MotionEvent.ACTION_UP: {
                drawElements.add(new DrawElement(x_Points, y_Points, this.m_linePaint.getColor()));
                x_Points.clear();
                y_Points.clear();

                Log.i("onTouchEvent", String.format("action=%s",
                        actionNames[action]
                ));
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                m_currentPath = new Path();
                m_currentPath.reset(); // Clear any lines and curves from the path, making it empty.
                m_currentPath.moveTo(x, y);

                x_Points.add(x);
                y_Points.add(y);

                Log.i("onTouchEvent", String.format("action=%s, coord=(%f, %f)",
                                actionNames[action],
                                x,
                                y)
                );
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                // Add a line from the last point to the specified point (x,y).
                m_currentPath.lineTo(x, y);
               // m_currentPointsF.add(new PointF(x, y));
                x_Points.add(x);
                y_Points.add(y);

                Log.i("onTouchEvent", String.format("action=%s, coord=(%f, %f)",
                                actionNames[action],
                                x,
                                y)
                );
                break;
            }
            default: {
                Log.i("ACTION", String.format("actionCode=%d", action));
                return false;
            }
        }

        invalidate();
        return true;// super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpec = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpec = MeasureSpec.getSize(heightMeasureSpec);

        int width = Integer.MAX_VALUE;
        int height = Integer.MAX_VALUE;// getSuggestedMinimumHeight();//.MAX_VALUE;

        if (widthMode == MeasureSpec.AT_MOST) {
            width = widthSpec;
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            height = heightSpec;
        }

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSpec;
            height = width;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSpec;
            width = height;
        }

        // TODO: respect padding!
        if (width > height && widthMode != MeasureSpec.EXACTLY) {
            width = height;
        }
        if (height > width && heightMode != MeasureSpec.EXACTLY) {
            height = width;
        }

        setMeasuredDimension(
                resolveSizeAndState(width, widthMeasureSpec, width < getSuggestedMinimumWidth() ? MEASURED_STATE_TOO_SMALL : 0),
                resolveSizeAndState(height, heightMeasureSpec, height < getSuggestedMinimumHeight() ? MEASURED_STATE_TOO_SMALL : 0));
    }
}

