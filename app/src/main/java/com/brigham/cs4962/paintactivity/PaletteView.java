package com.brigham.cs4962.paintactivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Brigham on 9/15/2014.
 */
public class PaletteView extends ViewGroup {

    ArrayList<Integer> m_paletteColors;

    int m_color;

    OnColorChangedListener m_OnColorChangedListener = null;
    OnAddNewColorListener m_OnAddNewColorListener = null;

    public PaletteView(Context context) {
        super(context);
        m_color = Color.rgb(205, 133, 63); // palette color (brown)
        setWillNotDraw(false);

        m_paletteColors = new ArrayList<Integer>(Arrays.asList(
                Color.RED,
                Color.parseColor("#FF7F00"), // orange
                Color.YELLOW,
                Color.GREEN,
                Color.BLUE,
                Color.parseColor("#8B00FF"), // violet
                Color.parseColor("#4B0082"), // indigo
                Color.parseColor("#A52A2A"), // brown
                Color.WHITE,
                Color.BLACK));
    }

    public OnColorChangedListener getOnSplotchTouchListener() {
        return m_OnColorChangedListener;
    }

    public OnColorChangedListener getOnColorChangedListener() {
        return m_OnColorChangedListener;
    }

    // -*~-*~-*~-*~-*~-*~-*~-*~-*~
    public void setOnColorChangedListener(OnColorChangedListener m_onColorChangedListener) {
        this.m_OnColorChangedListener = m_onColorChangedListener;
    }

    public OnAddNewColorListener getOnAddNewColorListener() {
        return m_OnAddNewColorListener;
    }

    // -*~-*~-*~-*~-*~-*~-*~-*~-*~
    public void setOnAddNewColorListener(OnAddNewColorListener m_OnAddNewColorListener) {
        this.m_OnAddNewColorListener = m_OnAddNewColorListener;
    }

    public int blendColor(int c1, int c2) {
        int r1 = Color.red(c1);
        int g1 = Color.green(c1);
        int b1 = Color.blue(c1);

        int r2 = Color.red(c2);
        int g2 = Color.green(c2);
        int b2 = Color.blue(c2);

        int r = (int) ((r1 * 0.40f) + r2 * (1 - 0.40f));
        int g = (int) ((g1 * 0.40f) + g2 * (1 - 0.40f));
        int b = (int) ((b1 * 0.40f) + b2 * (1 - 0.40f));
        int newColor = Color.rgb(r, g, b);
        m_paletteColors.add(newColor);
        return newColor;
    }

    // -*~-*~-*~-*~-*~-*~-*~-*~-*~
    private void setColor(int color) {
        m_color = color;
        invalidate();
    }

    // -*~-*~-*~-*~-*~-*~-*~-*~-*~
    private void addColor(int c1, int c2) {
        int newColor = blendColor(c1, c2);
        if (m_OnAddNewColorListener != null) {
            m_OnAddNewColorListener.onAddNewColor(newColor);
        }
        invalidate();
    }

    private void removeColor() {
        this.setVisibility(GONE);
        invalidate();
    }

    @Override
    // -*~-*~-*~-*~-*~-*~-*~-*~-*~
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // need code here
        int widthSpec = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpec = MeasureSpec.getSize(heightMeasureSpec);
        int width = Math.max(widthSpec, getSuggestedMinimumWidth());
        int height = Math.max(heightSpec, getSuggestedMinimumHeight());

        int childState = 0;

        int childCount = getChildCount();

        for (int childIndex = 0; childIndex < childCount; childIndex++) {
            PaintView child = (PaintView) getChildAt(childIndex);
            child.setColor(m_paletteColors.get(childIndex));

            child.measure(MeasureSpec.AT_MOST | 125, MeasureSpec.AT_MOST | 125);
            childState = combineMeasuredStates(childState, child.getMeasuredState());
        }
        setMeasuredDimension(
                resolveSizeAndState(width, widthMeasureSpec, childState),
                resolveSizeAndState(height, heightMeasureSpec, childState));
    }

    // -*~-*~-*~-*~-*~-*~-*~-*~-*~
    protected void initializeListeners(PaintView child) {
        child.setOnSplotchTouchListener(new PaintView.OnSplotchTouchListener() {
            public void onSplotchTouched(PaintView v) {
                for (int childIndex = 0; childIndex < getChildCount(); childIndex++) {
                    PaintView child = (PaintView) getChildAt(childIndex);
                    child.setActive(false);
                    child.invalidate();
                }
                v.setActive(true);
                if (m_OnColorChangedListener != null) {
                    m_OnColorChangedListener.onColorChanged(((PaintView) v).getColor());
                }
                v.invalidate();
            }
        });

        child.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        child.setOnSplotchReleaseListener(new PaintView.OnSplotchReleaseListener() {
            @Override
            public void onSplotchReleased(PaintView v) {
                int skipIndex = indexOfChild(v);
                int childCount = getChildCount();
                for (int childIndex = 0; childIndex < childCount; childIndex++) {
                    if (childIndex == skipIndex) {
                        continue;
                    }
                    PaintView child = (PaintView) getChildAt(childIndex);
                    // mix paints
                    if (v.isOverSplotch(child.getContentRect(), child.getX(), child.getY())) {
                        Log.i("isOverSplotch", String.format("color %d is over %d!", v.getColor(), child.getColor()));
                        addColor(v.getColor(), child.getColor());
                    } else {
                        Log.i("isOverSplotch", "false!");
                    }
                }
            }
        });
    }

    @Override
    protected void onLayout(boolean b, int i, int i2, int i3, int i4) {

        int childWidthMax = 0;
        int childHeightMax = 0;
        int childrenNotGone = 0;

        int childCount = getChildCount();
        for (int childIndex = 0; childIndex < childCount; childIndex++) {
            View child = getChildAt(childIndex);

            if (child.getVisibility() == GONE) {
                continue;
            }
            childWidthMax = Math.max(childWidthMax, child.getMeasuredWidth());
            childHeightMax = Math.max(childHeightMax, child.getMeasuredHeight());

            childrenNotGone++;
        }

        Rect layoutRect = new Rect();
        layoutRect.left = getPaddingLeft() + childWidthMax / 2;
        layoutRect.top = getPaddingTop() + childHeightMax / 2;
        layoutRect.right = getWidth() - getPaddingRight() - childWidthMax / 2;
        layoutRect.bottom = getHeight() - getPaddingBottom() - childHeightMax / 2;

        int childAngleIndex = 0;

        for (int childIndex = 0; childIndex < getChildCount(); childIndex++) {
            double angle = (double) childAngleIndex / (double) childrenNotGone * 2.0 * Math.PI;
            int childCenterX = (int) (layoutRect.centerX() + (double) layoutRect.width() * 0.50 * Math.cos(angle));
            int childCenterY = (int) (layoutRect.centerY() + (double) layoutRect.height() * 0.50 * Math.sin(angle));

            PaintView child = (PaintView) getChildAt(childIndex);
            Rect childLayout = new Rect();

            if (child.getVisibility() == GONE) {
                childLayout.left = 0;
                childLayout.top = 0;
                childLayout.right = 0;
                childLayout.bottom = 0;
            } else {
                childAngleIndex++;
                childLayout.left = childCenterX - childWidthMax / 2;
                childLayout.top = childCenterY - childHeightMax / 2;
                childLayout.right = childCenterX + childWidthMax / 2;
                childLayout.bottom = childCenterY + childHeightMax / 2;
            }

            initializeListeners(child);

            // layouts needs to be called in onLayout for all the children
            child.layout(childLayout.left, childLayout.top, childLayout.right, childLayout.bottom);//, 0, 50, 50);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        RectF oval = new RectF();
        oval.left = getPaddingLeft();
        oval.top = getPaddingTop();
        oval.right = getWidth() - getPaddingRight();
        oval.bottom = getHeight() - getPaddingBottom();

        Paint ovalPaint = new Paint();
        ovalPaint.setColor(m_color);
        canvas.drawOval(oval, ovalPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public interface OnColorChangedListener {
        public void onColorChanged(int newColor);
    }

    public interface OnAddNewColorListener {
        public void onAddNewColor(int newColor);
    }
}
