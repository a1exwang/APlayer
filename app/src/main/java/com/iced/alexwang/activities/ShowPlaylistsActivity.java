package com.iced.alexwang.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import com.iced.alexwang.views.main.LeftDrawerAdapter;
import com.iced.alexwang.views.main.ViewHelper;
import com.iced.alexwang.views.playlist_list.PlaylistListView;


public class ShowPlaylistsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ViewHelper.getDrawerLayout(this, LayoutInflater.from(this).inflate(R.layout.activity_show_playlists, null), new LeftDrawerAdapter(this, 2)));
        llView = (PlaylistListView) findViewById(R.id.playlistListView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        llView.load();
    }
    @Override
    protected void onStop() {
        super.onStop();
        llView.save();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_playlists, menu);
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

    PlaylistListView llView;
}
