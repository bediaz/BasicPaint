package com.brigham.cs4962.basicpaint;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class BaseActivity extends Activity {

    private final String TAG = "Base Activity";
    private Menu menu;
    private boolean play = false; // flase = pause, true = play
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
    }

    public static BaseActivity instance;

    private enum CurrentMenu { Palette, Watch, Create};
    static CurrentMenu m_CurrentMenu = CurrentMenu.Watch;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.paint_menu, menu);
        this.menu = menu;
        Bundle bundle = getIntent().getExtras();

        if(bundle != null) {
            play = bundle.getBoolean("Play", false);
        }

        menu.findItem(R.id.pause_play_menu_button).setIcon(play ? R.drawable.ic_pause_button : R.drawable.ic_play_button);

        return super.onCreateOptionsMenu(menu);
    }

    public void setPlay_PauseButton(boolean play_pause) {

        if(menu == null) {
            return;
        }
        play = !play;
        menu.findItem(R.id.pause_play_menu_button).setIcon(play ? R.drawable.ic_pause_button : R.drawable.ic_play_button);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.setGroupVisible(R.id.group_create_menu, false);
        menu.setGroupVisible(R.id.group_watch_menu, false);
        menu.setGroupVisible(R.id.group_palette_menu, false);

        switch(m_CurrentMenu) {
            case Create: {
                menu.setGroupVisible(R.id.group_create_menu, true);
                return true;
            }
            case Watch:
                menu.setGroupVisible(R.id.group_watch_menu, true);
                return true;
            case Palette:
                menu.setGroupVisible(R.id.group_palette_menu, true);
                return true;
            default:
                return super.onPrepareOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.palette_mode:
            case R.id.create_menu_button: {
                m_CurrentMenu = CurrentMenu.Watch;
                Intent intent = new Intent(this, PaintActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(0, 0);//android.R.anim.slide_in_left | android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            }
            case R.id.delete_menu_button: {
                m_CurrentMenu = CurrentMenu.Watch;
                Intent intent = getIntent();
                finish();
                Bundle bundle = new Bundle();
                bundle.putBoolean("clear", true);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }
            case R.id.palette_menu_button: {
                m_CurrentMenu = CurrentMenu.Palette;
                Intent intent = new Intent(this, PaletteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left | android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            }
            case R.id.watch_menu_button: {
                // switch to create menu
                m_CurrentMenu = CurrentMenu.Create;
                Intent intent = new Intent(this, WatchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left | android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            }
            case R.id.pause_play_menu_button:
                play = !play;
                item.setIcon(play ? R.drawable.ic_pause_button : R.drawable.ic_play_button);
                StartPlayback();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void StartPlayback() {
        {
            Intent intent = getIntent();
            finish();
            Bundle bundle = new Bundle();
            bundle.putBoolean("Play", play);
            intent.putExtras(bundle);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }

}
