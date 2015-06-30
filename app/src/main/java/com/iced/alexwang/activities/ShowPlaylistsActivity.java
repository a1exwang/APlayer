package com.iced.alexwang.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.iced.alexwang.libs.CachedFile;
import com.iced.alexwang.models.Playlist;
import com.iced.alexwang.models.PlaylistList;
import com.iced.alexwang.models.Song;
import com.iced.alexwang.models.callbacks.FilesSelectedCallback;
import com.iced.alexwang.models.callbacks.PlaylistCallback;
import com.iced.alexwang.player.MusicPlayerHelper;
import com.iced.alexwang.views.decorator.DividerItemDecoration;
import com.iced.alexwang.views.playlist_list.PlaylistListAdapter;
import com.iced.alexwang.views.playlist_list.PlaylistListView;
import com.iced.alexwang.views.select_file.SelectFileView;

import java.util.ArrayList;


public class ShowPlaylistsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_playlists);

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
