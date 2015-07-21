/*
* This Activity is the main control activity which is also the default startup activity.
*
* TODO: LyricsView vanished!
*
*
* */

package com.iced.alexwang.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import com.iced.alexwang.views.control.PlayerControlView;
import com.iced.alexwang.views.main.LeftDrawerAdapter;
import com.iced.alexwang.views.main.ViewHelper;

public class PlayerControlActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ViewHelper.getDrawerLayout(this, LayoutInflater.from(this).inflate(R.layout.activity_player_control, null), new LeftDrawerAdapter(this, 0)));
        controlView = (PlayerControlView) findViewById(R.id.layoutPlayerControlMain);
    }

    @Override
    protected void onStart() {
        super.onStart();
        controlView.init();
    }
    @Override
    protected void onStop() {
        super.onStop();
        controlView.release();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    PlayerControlView controlView;
}
