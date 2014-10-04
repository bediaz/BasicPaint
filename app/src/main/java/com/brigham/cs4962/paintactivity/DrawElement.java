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

    private Float[] x_Points;
    private Float[] y_Points;
    private int m_Color;

    public int getColor() { return m_Color; }
    public List<Float> getXPoints() { return Arrays.asList(x_Points); }
    public List<Float> getYPoints() { return Arrays.asList(y_Points); }
    public Path getPath() {
        Path path = new Path();
        for(int arrIdx = 0; arrIdx < x_Points.length; arrIdx++) {
            if(arrIdx == 0) {
                path.moveTo(x_Points[arrIdx], y_Points[arrIdx]);
            } else {
                path.lineTo(x_Points[arrIdx], y_Points[arrIdx]);
            }
        }
        return path;
    }

    public DrawElement(List<Float> x_points, List<Float> y_points, int color) {
        this.x_Points = x_points.toArray(new Float[x_points.size()]);
        this.y_Points = y_points.toArray(new Float[y_points.size()]);
        this.m_Color = color;
    }

    public static int RGBToInt(int r, int g, int b) {
        return Color.rgb(r, g, b);
    }
}
