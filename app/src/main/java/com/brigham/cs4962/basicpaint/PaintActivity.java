package com.brigham.cs4962.basicpaint;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PaintActivity extends BaseActivity {

    private static final String FILE_PATH = "draw_elements.txt";
    private final String TAG = "Paint Activity";
    private PaintAreaView m_areaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout masterLayout = new LinearLayout(this);
        masterLayout.setOrientation(LinearLayout.VERTICAL);

        m_areaView = new PaintAreaView(this);
        m_areaView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 2f));

        masterLayout.addView(m_areaView);
        setContentView(masterLayout);
    }

    private boolean clearPaintView() {
        File f = new File(FILE_PATH);
        return f.delete();
    }

    @Override
    protected void onResume() {
        restoreData();
        m_areaView.setUserDraw(true);
        super.onResume();
    }

    private void restoreData() {
        try {

            Intent intent = getIntent();
            boolean clear = intent.getBooleanExtra("clear", false);
            if(clear) {
                if(clearPaintView()) {
 //                   return;
                }
            }
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

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            int c = settings.getInt("strokeColor", Color.BLACK);
            m_areaView.setStrokeColor(c);
            Drawable drawable = getApplicationContext().getResources().getDrawable(R.drawable.ic_mode_palette_button);
            drawable.setColorFilter(new PorterDuffColorFilter(c, PorterDuff.Mode.MULTIPLY));
        } catch(Exception ex) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        // SAVE TO JSON
        Gson gson = new Gson();
        StringBuilder str = new StringBuilder();
        str.append(gson.toJson(m_areaView.getDrawElements()));
        m_areaView.clearCurrentCurrentPath();
        // save drawElements json
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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