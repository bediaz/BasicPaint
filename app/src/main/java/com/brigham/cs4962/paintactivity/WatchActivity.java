package com.brigham.cs4962.paintactivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class WatchActivity extends BaseActivity {

    private final String TAG = "Watch Activity";

    private PaintAreaView m_paintArea;
    private SeekBar m_seekBar;
    private int totalNumOfPoints = 0;
    private int totalNumOfPointsToDraw = 0;
    private ArrayList<DrawElement> drawElements;

    private void CalculateTotalNumOfPoints() {
        if(drawElements == null || drawElements.size() <= 0) { return; }
        Log.i(TAG, "CalculateTotalNumOfPoints");

        for(int outerIdx = 0; outerIdx < drawElements.size(); outerIdx++) {
            totalNumOfPoints += drawElements.get(outerIdx).getSize();
        }
    }
    private void DrawPaint() {
        if(totalNumOfPointsToDraw <= 0 || totalNumOfPoints <= 0) { return; }
        Log.i(TAG, "DrawPaint");
        ArrayList<DrawElement> temp = drawElements;
        int pointCount = 0;

        for(int outerIdx = 0; outerIdx < temp.size(); outerIdx++) {
            for(int innerIdx = 0; innerIdx < temp.get(outerIdx).getXPoints().size(); innerIdx++) {
                if(pointCount++ >= totalNumOfPointsToDraw) {
                    temp.get(outerIdx).deletePoints(innerIdx, drawElements.get(outerIdx).getXPoints().size());
                    int numToRemove = drawElements.size();
                    for(int i = outerIdx; i < numToRemove; i++) {
                        temp.remove(outerIdx);
                    }
                    break;
                }
            }
        }

        m_paintArea.setDrawElements(temp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout masterLayout = new LinearLayout(this);
        masterLayout.setOrientation(LinearLayout.VERTICAL);

        m_seekBar = new SeekBar(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0f);

        m_paintArea = new PaintAreaView(this);
        m_paintArea.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 2f));

        params.gravity = Gravity.BOTTOM;

        m_seekBar.setLayoutParams(params);
        m_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO: limit the draw event
                if(!fromUser) { return; }
                totalNumOfPointsToDraw = progress;
                Log.i(TAG, "onProgressChanged, progress=" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i(TAG, "onStartTrackingTouch");
                // TODO: limit the draw event
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO: limit the draw event
                Log.i(TAG, "onStopTrackingTouch");
                DrawPaint();
            }
        });
        masterLayout.addView(m_paintArea);
        masterLayout.addView(m_seekBar);

        masterLayout.setBackgroundColor(Color.LTGRAY);
        setContentView(masterLayout);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");

        // Restore drawElements json
        SharedPreferences settings = getSharedPreferences(PaintView.PREFS, 0);
        String json = settings.getString("drawElements", "");

        // convert json to gson
        Gson gson = new Gson();

        Type listOfDrawElement = new TypeToken<List<DrawElement>>(){}.getType();
        // convert gson to ArrayList<DrawElements>
        drawElements = gson.fromJson(json, listOfDrawElement);

        CalculateTotalNumOfPoints();
        m_seekBar.setMax(totalNumOfPoints);
        //m_paintArea.setDrawElements(drawElements);
    }



    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "onNewIntent");
    }
}
