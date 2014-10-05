package com.brigham.cs4962.paintactivity;

import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Arrays;
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

    public int getColor() { return m_Color; }
    public List<Float> getXPoints() { return x_Points; }
    public List<Float> getYPoints() { return y_Points; }
    public int getSize() { return x_Points.size(); }
    public Path getPath() {
        Path path = new Path();
        for(int arrIdx = 0; arrIdx < x_Points.size(); arrIdx++) {
            if(arrIdx == 0) {
                path.moveTo(x_Points.get(arrIdx), y_Points.get(arrIdx));
            } else {
                path.lineTo(x_Points.get(arrIdx), y_Points.get(arrIdx));
            }
        }
        return path;
    }

    public void deletePoints(int start, int end) {
        if(start <= 0 || end > x_Points.size()) { return; }

        for(int i = start; i < end; i++) {
            x_Points.remove(i);
            y_Points.remove(i);
        }


    }

    public DrawElement(List<Float> x_points, List<Float> y_points, int color) {
        this.x_Points = x_points;
        this.y_Points = y_points;
        this.m_Color = color;
    }

    public static int RGBToInt(int r, int g, int b) {
        return Color.rgb(r, g, b);
    }
}
