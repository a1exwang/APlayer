package com.iced.alexwang.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.iced.alexwang.models.Playlist;
import com.iced.alexwang.models.callbacks.PlaylistCallback;
import com.iced.alexwang.player.MusicPlayerHelper;
import com.iced.alexwang.views.playlist.PlaylistView;

public class CurrentPlaylistActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_playlist);

        layoutPlaylist = (RelativeLayout) findViewById(R.id.layoutContainingPlaylist);

        playerHelper = MusicPlayerHelper.getInstance(this);
        playerHelper.getPlaylist(new PlaylistCallback() {
            @Override
            public void run(final Playlist p) {
                playlistView = new PlaylistView(CurrentPlaylistActivity.this);
                playlistView.setPlaylist(p);
                layoutPlaylist.addView(playlistView);
            }
        });

        btnClear = (Button) findViewById(R.id.btnPlaylistClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlistView.clear();
                MusicPlayerHelper.getInstance(CurrentPlaylistActivity.this).setPlaylist(new Playlist());
            }
        });

        btnAddToPlaylist = (Button) findViewById(R.id.btnAddSongToPlaylist);
        btnAddToPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CurrentPlaylistActivity.this, SelectFileActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_current_playlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.playlist_sort_by_artist) {
            playlistView.sortByArtist();
        } else if (id == R.id.playlist_sort_by_title) {
            playlistView.sortByTitle();
        } else if (id == R.id.playlist_sort_by_date) {
            playlistView.sortByDate();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }


    RelativeLayout layoutPlaylist;
    Button btnClear, btnAddToPlaylist;

    MusicPlayerHelper playerHelper;
    PlaylistView playlistView;
}
