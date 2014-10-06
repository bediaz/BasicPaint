package com.brigham.cs4962.paintactivity;

import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    public int getColor() { return m_Color; }
    public List<Float> getXPoints() { return x_Points; }
    public List<Float> getYPoints() { return y_Points; }
    public int getSize() { return x_Points.size(); }

    public void addPoint(Float x, Float y) {
        x_Points.add(x);
        y_Points.add(y);
    }

    public void createPath() {
        // insanity check
        path = new Path();

        for(int arrIdx = 0; arrIdx < x_Points.size(); arrIdx++) {
            if(arrIdx == 0) {
                path.moveTo(x_Points.get(arrIdx), y_Points.get(arrIdx));
            } else {
                path.lineTo(x_Points.get(arrIdx), y_Points.get(arrIdx));
            }
        }
    }
    public Path getPath() {
        if(path == null) {
            createPath();
        }
        return path;
    }

    public void deletePoints(int start, int end) {
        if(start <= 0 || end > x_Points.size()) { return; }

        for(int i = start; i < end; i++) {
            x_Points.remove(start);
            y_Points.remove(start);
        }

        createPath();
    }

    public DrawElement(List<Float> x_points, List<Float> y_points, int color) {
        this.x_Points = new ArrayList<Float>(x_points); // create shallow copies
        this.y_Points = new ArrayList<Float>(y_points);
        this.m_Color = color;
        createPath();
    }
//    // copy constructor
//    public DrawElement(DrawElement drawElement) {
//
//        this.x_Points = new ArrayList<Float>();//drawElement.getXPoints().size());
//        this.y_Points = new ArrayList<Float>();//drawElement.getYPoints().size());
//
//        for(int idx = 0; idx < drawElement.getSize(); idx++) {
//            x_Points.add(new Float(drawElement.getXPoints().get(idx)));
//            y_Points.add(new Float(drawElement.getYPoints().get(idx)));
//        }
//
//        this.m_Color = drawElement.getColor();
//        this.createPath();
//    }

    public static int RGBToInt(int r, int g, int b) {
        return Color.rgb(r, g, b);
    }
}
