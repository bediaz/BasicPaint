package com.brigham.cs4962.basicpaint;

import android.graphics.Color;
import android.graphics.Path;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brigham on 10/4/2014.
 *
 * Consists of a Path, color, and array of PointF values
 * Used to save paint using Gson
 */
public class DrawElement {

    private List<Float> x_Points;
    private List<Float> y_Points;
    private int m_Color;
    private transient Path path; // exclude from Gson
    private transient int width = PaintAreaView.getPaintViewWidth();
    private transient int height =  PaintAreaView.getPaintViewHeight();

    public int getColor() { return m_Color; }
    public List<Float> getXPoints() { return x_Points; }
    public List<Float> getYPoints() { return y_Points; }
    public int getSize() { return x_Points.size(); }

    public float getPointX(int idx) {
        return x_Points.get(idx);
    }
    public float getPointY(int idx) {
        return y_Points.get(idx);
    }
    public void addPoint(Float x, Float y) {
        x_Points.add(x);
        y_Points.add(y);

        x *= PaintAreaView.getPaintViewWidth();
        y *= PaintAreaView.getPaintViewHeight();

        if(path == null || path.isEmpty()) {
            path = new Path();
            path.moveTo(x, y);
        } else {
            path.lineTo(x, y);
        }
    }



    public void createPath() {
        path = new Path();
        for(int arrIdx = 0; arrIdx < x_Points.size(); arrIdx++) {
            float x = x_Points.get(arrIdx) * PaintAreaView.getPaintViewWidth();
            float y = y_Points.get(arrIdx) * PaintAreaView.getPaintViewHeight();

            if(path.isEmpty()) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }
    }

    public Path getPath() {
        if(path == null) {
            createPath();
        }
        return path;
    }

    public DrawElement(List<Float> x_points, List<Float> y_points, int color) {

        this.x_Points = new ArrayList<Float>(x_points);
        this.y_Points = new ArrayList<Float>(y_points);
//        for(int idx = 0; idx < x_points.size(); idx++) {
//            x_Points.add(x_points.get(idx));
//            y_Points.add(y_points.get(idx));
//        }

        this.m_Color = color;
        this.width = PaintAreaView.getPaintViewWidth();
        this.height = PaintAreaView.getPaintViewHeight();
    }

    public static int RGBToInt(int r, int g, int b) {
        return Color.rgb(r, g, b);
    }
}
