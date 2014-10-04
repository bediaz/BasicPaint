package com.brigham.cs4962.paintactivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Brigham on 9/21/2014.
 */
public class PaintAreaView extends View implements OnTouchListener {

    Path m_paths;
    Path m_currentPath;
    Paint m_linePaint;

    List<Float> x_Points;
    List<Float> y_Points;

    int m_strokeColor;

    int m_viewWidth, m_viewHeight;

    //ArrayList<PointF> m_currentPointsF; // stores points before converting to floating point array

    public ArrayList<DrawElement> getDrawElements() {
        return drawElements;
    }

    public void setDrawElements(ArrayList<DrawElement> drawElements) {
        this.drawElements = drawElements;
    }

    //    ArrayList<Float[]> m_savedPointsF; // passes this to bundle for savedInstance
  //  ArrayList<Integer> m_savedColors; // passes this up to bundle for savedInstance
    ArrayList<DrawElement> drawElements;

    public PaintAreaView(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
        m_linePaint = createPaintStyle();
        m_paths = new Path();
        x_Points = new ArrayList<Float>();
        y_Points = new ArrayList<Float>();

        //m_currentPointsF = new ArrayList<PointF>();
//        m_savedPointsF = new ArrayList<Float[]>();
//        m_savedColors = new ArrayList<Integer>();
        m_currentPath = new Path();
        drawElements = new ArrayList<DrawElement>();

        setStrokeColor(Color.BLACK);

        setOnTouchListener(this);
    }

//    public ArrayList<Integer> getStrokeColors() {
//        ArrayList<Integer> colors = new ArrayList<Integer>();
//        for (DrawElement element : drawElements) {
//            colors.add(element.getColor());
//        }
//
//        return colors;
//    }

//    public ArrayList<Float[]> getPoints() {
//        return m_savedPointsF;
//    }

    public void setStrokeColor(int newColor) {
        this.m_strokeColor = newColor;
    }

//    public void restorePointsAndColors(ArrayList<Float[]> savedPoints, ArrayList<Integer> savedColors) {
//        // save the arrays until ready to convert to paths
//        this.m_savedPointsF = savedPoints;
//        this.m_savedColors = savedColors;
//    }

    // converts the ArrayList of floating point arrays into Paths. Each floating point array is a new path
//    private void pathColorArraysToMap() {
//
//        for (int index = 0; index < m_savedPointsF.size(); index++) {
//            Float[] f = m_savedPointsF.get(index);
//            Path path = new Path();
//
//            for (int floatArrayIndex = 0; floatArrayIndex < f.length - 1; floatArrayIndex += 2) {
//                float x = f[floatArrayIndex];
//                x *= m_viewWidth;
//                float y = f[floatArrayIndex + 1];
//                y *= m_viewHeight;
//                if (floatArrayIndex == 0) {
//                    path.moveTo(x, y);
//                } else {
//                    path.lineTo(x, y);
//                }
//            }
//            m_pathAndColorMap.put(path, m_savedColors.get(index));
//        }
//
//        invalidate(); // might be redundant call here
//    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // get the width, height
        m_viewWidth = w;
        m_viewHeight = h;
    }

    private Paint createPaintStyle() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
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
        m_linePaint.setColor(this.m_strokeColor);
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
                drawElements.add(new DrawElement(x_Points, y_Points, this.m_strokeColor));
//                //m_pathAndColorMap.put(m_currentPath, this.m_strokeColor);
//
//                // convert the PointF array to floating point array
//                Float[] f = new Float[m_currentPointsF.size() * 2];
//                int floatArrayIdx = 0;
//                for (PointF point : m_currentPointsF) {
//                    f[floatArrayIdx] = point.x / m_viewWidth; // scale to [0.0 -> 1.0]
//                    f[floatArrayIdx + 1] = point.y / m_viewHeight; // scale to [0.0 -> 1.0]
//                    floatArrayIdx += 2;
//                }

                //m_savedPointsF.add(f);
                x_Points.clear();
                y_Points.clear();
                //m_currentPointsF.clear();

                Log.i("onTouchEvent", String.format("action=%s",
                        actionNames[action]
                ));
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                m_currentPath = new Path();
                m_currentPath.reset(); // Clear any lines and curves from the path, making it empty.
                m_currentPath.moveTo(x, y);
                //m_currentPath.transform()
//                m_currentPointsF.add(new PointF(
//                        x,
//                        y
//                ));
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

