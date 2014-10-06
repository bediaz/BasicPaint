package com.brigham.cs4962.basicpaint;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
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
    private int totalPointCount = 0;
    private int numOfPointsToDraw = 0;
    private List<DrawElement> drawElements;

    private void CalculateTotalNumOfPoints() {
        totalPointCount = 0;
        if(drawElements == null || drawElements.size() <= 0) { return; }
        Log.i(TAG, "CalculateTotalNumOfPoints");

        for(int outerIdx = 0; outerIdx < drawElements.size(); outerIdx++) {
            totalPointCount += drawElements.get(outerIdx).getSize();
        }
    }

    private void DrawPaint() {
        if(numOfPointsToDraw <= 0 || totalPointCount <= 0) { return; }
        Log.i(TAG, "DrawPaint");
        //CalculateTotalNumOfPoints();
        List<DrawElement> temp = new ArrayList<DrawElement>();

        int pointCount = 0;

        for(int outerIdx = 0; outerIdx < drawElements.size() && pointCount < numOfPointsToDraw; outerIdx++) {
            List<Float> x_list = new ArrayList<Float>();
            List<Float> y_list = new ArrayList<Float>();
            temp.add(new DrawElement(x_list, y_list, drawElements.get(outerIdx).getColor()));

            for(int innerIdx = 0; innerIdx < drawElements.get(outerIdx).getXPoints().size(); innerIdx++) {
                float x = drawElements.get(outerIdx).getXPoints().get(innerIdx);
                float y = drawElements.get(outerIdx).getYPoints().get(innerIdx);
                temp.get(outerIdx).addPoint(x, y);

                if (pointCount++ >= numOfPointsToDraw) {
                    break;
                }
            }
            temp.get(outerIdx).createPath();
        }

        m_paintArea.setDrawElements(temp);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DrawPaint();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitializeUI();

    }

    private void InitializeUI() {

        LinearLayout masterLayout = new LinearLayout(this);
        masterLayout.setOrientation(LinearLayout.VERTICAL);

        m_seekBar = new SeekBar(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0f);

        m_paintArea = new PaintAreaView(this);
        m_paintArea.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 2f));
        m_paintArea.setUserDraw(false); // place paintareaview in view-only mode

        params.gravity = Gravity.BOTTOM;

        m_seekBar.setLayoutParams(params);
        m_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                numOfPointsToDraw = progress;
                DrawPaint();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                DrawPaint();
            }

        });
        masterLayout.addView(m_paintArea);
        masterLayout.addView(m_seekBar);

        masterLayout.setBackgroundColor(Color.LTGRAY);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null) {
            play = bundle.getBoolean("Play", false);
        }
        setContentView(masterLayout);
    }
    boolean play = false;

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    ObjectAnimator animation;

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");

        // Restore drawElements json
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String json = settings.getString("drawElements", "");

        // convert json to gson
        Gson gson = new Gson();

        Type listOfDrawElement = new TypeToken<List<DrawElement>>() {
        }.getType();
        // convert gson to ArrayList<DrawElements>
        drawElements = gson.fromJson(json, listOfDrawElement);
        CalculateTotalNumOfPoints();

        int scrubPosition = settings.getInt("scrubPosition", 0);
        m_seekBar.setMax(totalPointCount);
        m_seekBar.setProgress(scrubPosition);


        if (play) {
            // will update the "progress" of m_seekBar until it reaches progress
            animation = ObjectAnimator.ofInt(m_seekBar, "progress", totalPointCount);
            animation.setDuration(5000 - (int)(5000*((float)numOfPointsToDraw/totalPointCount))); // 0.5 second
            animation.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {

                    Intent intent = new Intent(WatchActivity.this, BaseActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("Play", false);
                    intent.putExtras(bundle);

                    startActivity(intent);
                    BaseActivity.instance.StartPlayback();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animation.setInterpolator(new LinearInterpolator());
            animation.start();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //animation.setAutoCancel(true);
        if(animation != null) {
            animation.cancel();
        }
        Log.i(TAG, "onPause");

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("scrubPosition", numOfPointsToDraw);
        editor.commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "onNewIntent");
    }
}
