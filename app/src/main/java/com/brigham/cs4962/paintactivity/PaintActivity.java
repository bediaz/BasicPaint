package com.brigham.cs4962.paintactivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PaintActivity extends BaseActivity {


    private final String TAG = "Paint Activity";

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

                pv.getOnSplotchTouchListener().onSplotchTouched(pv);
                pv.invalidate();
            }
        });

        for (int splotchIndex = 0; splotchIndex < 10; splotchIndex++) {

            PaintView pv = new PaintView(this);

            if (splotchIndex == 0) {
                pv.setLayoutParams(new LinearLayout.LayoutParams(40, 70, 4));
            } else {
                pv.setLayoutParams(new LinearLayout.LayoutParams(40, ViewGroup.LayoutParams.WRAP_CONTENT, 4));
            }

            m_rootLayout.addView(pv);
        }

        m_areaView = new PaintAreaView(this);
        m_areaView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 2f));

        masterLayout.addView(m_areaView);
        masterLayout.addView(m_rootLayout);

        //restoreData();

        setContentView(masterLayout);
    }

    /* not needed for this project. calling onResume when program launches is sufficient
    @Override
    protected void onStart() {
        super.onStart();
        restoreData();
        Log.i(TAG, "onStart");
    }
    */

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        restoreData();
        super.onResume();
    }

    private void restoreData() {
        Log.i(TAG, "restoreData");
        try {
            FileInputStream fis = openFileInput(FILE_PATH);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            String json = sb.toString();
            Gson gson = new Gson();

            Type listOfDrawElement = new TypeToken<List<DrawElement>>(){}.getType();
            ArrayList<DrawElement> drawElements = gson.fromJson(json, listOfDrawElement);
            m_areaView.setDrawElements(drawElements);
        } catch(Exception ex) {
        }
    }

    private static final String FILE_PATH = "draw_elements.txt";

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        // SAVE TO JSON
        Gson gson = new Gson();
        StringBuilder str = new StringBuilder();
        str.append(gson.toJson(m_areaView.getDrawElements()));

        // save drawElements json
        SharedPreferences settings = getSharedPreferences(PaintView.PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("drawElements", str.toString());
        editor.commit();

        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(FILE_PATH, Context.MODE_PRIVATE);
            outputStream.write(str.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "onNewIntent");

    }
}