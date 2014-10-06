package com.brigham.cs4962.basicpaint;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ViewGroup;
import android.widget.LinearLayout;


public class PaletteActivity extends BaseActivity {

    PaletteView m_rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_rootLayout = new PaletteView(this);
        m_rootLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 2f));
        m_rootLayout.setOnColorChangedListener(new PaletteView.OnColorChangedListener() {
            @Override
            public void onColorChanged(int newColor) {
                // save drawElements json
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("strokeColor", newColor);
                editor.commit();
            }
        });

        m_rootLayout.setOnAddNewColorListener(new PaletteView.OnAddNewColorListener() {
            @Override
            public void onAddNewColor(int newColor) {
                PaintView pv;
                pv = new PaintView(PaletteActivity.this);
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

        setContentView(m_rootLayout);
    }
}
