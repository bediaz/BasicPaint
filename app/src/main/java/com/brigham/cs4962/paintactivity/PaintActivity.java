package com.brigham.cs4962.paintactivity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.Serializable;
import java.util.ArrayList;

public class PaintActivity extends Activity {

    static final String POINTS_DRAWN = "pointsSaved";
    static final String COLORS_SAVED = "colorsSaved";

    PaintAreaView m_areaView;
    PaletteView m_rootLayout;

    @Override
    // -*~-*~-*~-*~-*~-*~-*~-*~-*~
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout masterLayout = new LinearLayout(this);
        masterLayout.setOrientation(LinearLayout.VERTICAL);

        m_rootLayout = new PaletteView(this);
        m_rootLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 2f));
        m_rootLayout.setOnColorChangedListener(new PaletteView.OnColorChangedListener() {
            @Override
            public void onColorChanged(int newColor) {
                m_areaView.setStrokeColor(newColor);
            }
        });

        m_rootLayout.setOnAddNewColorListener(new PaletteView.OnAddNewColorListener() {
            @Override
            public void onAddNewColor(int newColor) {
                PaintView pv;
                pv = new PaintView(PaintActivity.this);
                pv.setLayoutParams(new LinearLayout.LayoutParams(40, ViewGroup.LayoutParams.WRAP_CONTENT));
                pv.setColor(newColor);
                m_rootLayout.initializeListeners(pv);
                m_rootLayout.addView(pv);

//
//                for (int childIndex = 0; childIndex < m_rootLayout.getChildCount(); childIndex++) {
//                    PaintView child = (PaintView) m_rootLayout.getChildAt(childIndex);
//                    child.setActive(false);
//                }
//
//                if (m_rootLayout.getOnColorChangedListener() != null) {
//                    m_rootLayout.getOnColorChangedListener().onColorChanged(pv.getColor());
//                }
//                pv.setActive(true);
                pv.getOnSplotchTouchListener().onSplotchTouched(pv);
                pv.invalidate();
            }
        });

        for(int splotchIndex = 0; splotchIndex < 10; splotchIndex++) {

            PaintView pv = new PaintView(this);

            if(splotchIndex == 0) {
                pv.setLayoutParams(new LinearLayout.LayoutParams(40, 70,4));
            } else {
                pv.setLayoutParams(new LinearLayout.LayoutParams(40, ViewGroup.LayoutParams.WRAP_CONTENT,4));
            }

            m_rootLayout.addView(pv);
        }

        m_areaView = new PaintAreaView(this);
        m_areaView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 2f));

        masterLayout.addView(m_areaView);
        masterLayout.addView(m_rootLayout);


        // restore settings on rotate
        if(savedInstanceState != null) {
            ArrayList<Float[]> points = (ArrayList<Float[]>) savedInstanceState.getSerializable(POINTS_DRAWN);
            ArrayList<Integer> colors = (ArrayList<Integer>) savedInstanceState.getSerializable(COLORS_SAVED);
            m_areaView.restorePointsAndColors(points, colors);
        }

        setContentView(masterLayout);


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        ArrayList<Float[]> points = m_areaView.getPoints();
        ArrayList<Integer> colors = m_areaView.getStrokeColors();
        outState.putSerializable(POINTS_DRAWN, (Serializable)points);
        outState.putSerializable(COLORS_SAVED, (Serializable)colors);

        super.onSaveInstanceState(outState);

    }
}