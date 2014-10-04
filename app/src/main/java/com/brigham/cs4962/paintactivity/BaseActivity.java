package com.brigham.cs4962.paintactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class BaseActivity extends Activity {

    private final String TAG = "Base Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "\tonCreate");
        //setContentView(R.layout.activity_base);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "\tonStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "\tonResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "\tonPause");
    }

    private enum CurrentMenu { Palette, Watch, Create };
    static CurrentMenu m_CurrentMenu = CurrentMenu.Watch;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
//        menu.setGroupVisible(R.id.group_create_menu, false);
//        menu.setGroupVisible(R.id.group_watch_menu, true);
        inflater.inflate(R.menu.paint_menu, menu);
        Log.i(TAG, "\tonCreateOptionsMenu, m_CurrentMenu=" + m_CurrentMenu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        switch(m_CurrentMenu) {
            case Create: {
                Log.i(TAG, "Create Menu Visible");
                menu.setGroupVisible(R.id.group_create_menu, true);
                menu.setGroupVisible(R.id.group_watch_menu, false);
                return true;
            }
            case Watch:
                Log.i(TAG, "Watch Menu Visible");
                menu.setGroupVisible(R.id.group_create_menu, false);
                menu.setGroupVisible(R.id.group_watch_menu, true);
                return true;
            default:
                return super.onPrepareOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "    onOptionsItemSelected");
        switch(item.getItemId()) {
            case R.id.create_menu: {
                Log.i(TAG, "Starting PaintActivity intent");
                m_CurrentMenu = CurrentMenu.Watch;
                invalidateOptionsMenu();
                Intent intent = new Intent(this, PaintActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left | android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            }
            case R.id.palette_menu: {
                Log.i(TAG, "Starting PaletteActivity intent");
                m_CurrentMenu = CurrentMenu.Watch;
                invalidateOptionsMenu();
                Intent intent = new Intent(this, PaletteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left | android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            }
            case R.id.watch_mode: {
                Log.i(TAG, "Starting WatchActivity intent");
                // switch to create menu
                m_CurrentMenu = CurrentMenu.Create;
                invalidateOptionsMenu();
                Intent intent = new Intent(this, WatchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left | android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            }
            default:
                Log.i(TAG, "onOptionsItemSelected Default");
                return super.onOptionsItemSelected(item);
        }
    }

}