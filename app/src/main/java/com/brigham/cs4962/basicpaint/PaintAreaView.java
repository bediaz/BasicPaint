package com.brigham.cs4962.basicpaint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.DisplayMetrics;
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
	
	public static int getPaintViewWidth() {
		DisplayMetrics metrics = appContext.getResources().getDisplayMetrics();
		m_viewWidth = metrics.widthPixels;
		return metrics.widthPixels;
	}
	
	public static int getPaintViewHeight() {
		DisplayMetrics metrics = appContext.getResources().getDisplayMetrics();
		m_viewHeight = metrics.heightPixels;
		return metrics.heightPixels;
	}
	
	private static Context appContext;
	private static final String TAG = "PaintAreaView";
	
	private boolean allowUserDraw;
	private int m_strokeColor;
	private static int m_viewWidth, m_viewHeight;
	
	private Path m_currentPath;
	private Paint m_linePaint;
	
	private List<Float> m_PointsX, m_PointsY;
	private List<DrawElement> m_drawElements;
	
	public List<DrawElement> getDrawElements() {
		return m_drawElements;
	}
	
	public void setDrawElements(List<DrawElement> temp) {
		if (temp == null || temp.size() == 0) {
			return;
		}
		this.m_drawElements = new ArrayList<DrawElement>(temp);
		invalidate();
	}
	
	public void clearCurrentCurrentPath() {
		if (!m_currentPath.isEmpty()) {
			m_currentPath = new Path();
			m_currentPath.reset(); // Clear any lines and curves from the path, making it empty.
		}
	}
	
	public void setUserDraw(boolean allowUserDraw) {
		this.allowUserDraw = allowUserDraw;
	}
	
	public PaintAreaView(Context context) {
		super(context);
		appContext = context;
		setFocusable(true);
		setFocusableInTouchMode(true);
		m_linePaint = defaultPaintStyle();
		m_PointsX = new ArrayList<Float>();
		m_PointsY = new ArrayList<Float>();
		m_currentPath = new Path();
		m_drawElements = new ArrayList<DrawElement>();
		setOnTouchListener(this);
		setBackgroundColor(Color.WHITE);
	}
	
	public void setStrokeColor(int newColor) {
		// sanity check since this can get called from other classes
		this.m_strokeColor = newColor;
		invalidate();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		Log.i(TAG, "onSizeChanged, width=" + w + ",height=" + h);
		
		// get the width, height
		m_viewWidth = getPaintViewWidth();
		m_viewHeight = getPaintViewHeight();
		invalidate();
	}
	
	private Paint defaultPaintStyle() {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		setStrokeColor(Color.BLACK);
		return paint;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int i = 0;
		for (DrawElement element : m_drawElements) {
			Path p = element.getPath();
			int c = element.getColor();
			m_linePaint.setColor(c);
			
			if (p == null) {
				continue;
			}
			canvas.drawPath(p, m_linePaint);
		}
		
		// draw current line that hasn't been added to DrawElements yet.
		m_linePaint.setColor(this.m_strokeColor);
		canvas.drawPath(m_currentPath, m_linePaint);
	}
	
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if (!allowUserDraw) {
			return true;
		}
		
		int action = event.getActionMasked();
		float x = event.getX();
		float y = event.getY();// / m_viewHeight; // scale between 0 < y < 1
		
		switch (action) {
			case MotionEvent.ACTION_UP: {
				m_drawElements.add(new DrawElement(m_PointsX, m_PointsY, this.m_linePaint.getColor()));
				m_PointsX.clear();
				m_PointsY.clear();
				break;
			}
			case MotionEvent.ACTION_DOWN: {
				m_currentPath = new Path();
				m_currentPath.reset(); // Clear any lines and curves from the path, making it empty.
				m_currentPath.moveTo(x, y);
				m_PointsX.add(x / m_viewWidth);
				m_PointsY.add(y / m_viewHeight);
				return true;
			}
			case MotionEvent.ACTION_MOVE: {
				// Add a line from the last point to the specified point (x,y).
				m_currentPath.lineTo(x, y);
				m_PointsX.add(x / m_viewWidth);
				m_PointsY.add(y / m_viewHeight);
				break;
			}
			default: {
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

