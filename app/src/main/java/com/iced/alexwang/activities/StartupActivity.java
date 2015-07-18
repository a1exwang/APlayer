package com.iced.alexwang.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.iced.alexwang.libs.ActivityHelper;
import com.iced.alexwang.player.MusicPlayerHelper;
import com.iced.alexwang.views.search_result.SearchResultView;

public class StartupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        playerHelper = MusicPlayerHelper.getInstance(this);

        layout = (ViewGroup) findViewById(R.id.layoutStartup);
        btnSelectFiles = (Button) findViewById(R.id.btnShowFileSelect);
        btnPlaylist = (Button) findViewById(R.id.btnShowPlaylist);
        btnPlaylists = (Button) findViewById(R.id.btnShowPlaylistList);
        btnPlayer = (Button) findViewById(R.id.btnPlayer);
        btnLyrics = (Button) findViewById(R.id.btnLyrics);
        editSearch = (EditText) findViewById(R.id.editSearch);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        groupSearchBy = (RadioGroup) findViewById(R.id.groupSelectSearchBy);

        btnSelectFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityHelper.startSelectFileActivity(StartupActivity.this, Environment.getExternalStorageDirectory().toString());
            }
        });

        btnPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityHelper.startCurrentPlaylistActivity(StartupActivity.this);
            }
        });

        btnPlaylists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityHelper.startShowPlaylistsActivity(StartupActivity.this);
            }
        });

        btnPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityHelper.startPlayerControlActivity(StartupActivity.this);
            }
        });

        btnLyrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityHelper.showLyricsDialog(StartupActivity.this);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editSearch.getText().toString();
                int checked = groupSearchBy.getCheckedRadioButtonId();
                SearchResultView.SearchType type;

                if (R.id.radioButtonTitle == checked)
                    type = SearchResultView.SearchType.Title;
                else if (R.id.radioButtonArtist == checked)
                    type = SearchResultView.SearchType.Artist;
                else if (R.id.radioButtonPlaylist == checked)
                    type = SearchResultView.SearchType.Playlist;
                else
                    return;

                ActivityHelper.startSearchActivity(StartupActivity.this, type, content);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_startup, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_show_playlist:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    Button btnPlayer, btnPlaylist, btnPlaylists, btnSelectFiles, btnSearch, btnLyrics;
    RadioGroup groupSearchBy;
    EditText editSearch;
    ViewGroup layout;

    MusicPlayerHelper playerHelper;
}
