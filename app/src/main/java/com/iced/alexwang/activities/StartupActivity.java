package com.iced.alexwang.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.iced.alexwang.libs.CachedFile;
import com.iced.alexwang.libs.CachedFileSystem;
import com.iced.alexwang.models.Playlist;
import com.iced.alexwang.models.callbacks.ParameterizedRunnable;
import com.iced.alexwang.models.callbacks.PositionCallback;
import com.iced.alexwang.models.callbacks.VolumeCallback;
import com.iced.alexwang.player.MusicPlayerHelper;
import com.iced.alexwang.views.playlist.PlaylistAdapter;
import com.iced.alexwang.views.playlist.PlaylistViewHelper;

import java.io.File;

public class StartupActivity extends Activity {

    Button btnShowDialog, btnAddView, btnStartActivity, btnToast, btnPlayer;
    ViewGroup layout;
    ListView leftDrawer;

    Playlist playlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        playlist = PlaylistViewHelper.initPlaylistItems(this);
        layout = (ViewGroup) findViewById(R.id.layoutStartup);

        leftDrawer = (ListView) findViewById(R.id.left_drawer);
        leftDrawer.setAdapter(new PlaylistAdapter(this, playlist));

        btnShowDialog = (Button) findViewById(R.id.btnShowDialog);
        btnShowDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaylistViewHelper.showPlaylistDialog(StartupActivity.this, playlist);
            }
        });

        btnAddView = (Button) findViewById(R.id.btnAddView);
        btnAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaylistViewHelper helper = new PlaylistViewHelper(layout);
                helper.addPlaylistView(playlist);
            }
        });

        btnStartActivity = (Button) findViewById(R.id.btnStartActivity);
        btnStartActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaylistViewHelper.startPlaylistActivity(getApplicationContext(), playlist);
            }
        });

        btnToast = (Button) findViewById(R.id.btnPlaylistToast);
//        btnToast.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PlaylistViewHelper.showPlaylistToast(StartupActivity.this, playlist);
//            }
//        });
        btnToast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartupActivity.this, SelectFileActivity.class);
                intent.putExtra(getString(R.string.select_file_initial_directory), Environment.getExternalStorageDirectory());

                startActivity(intent);
            }
        });
        btnPlayer = (Button) findViewById(R.id.btnPlayer);
        btnPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StartupActivity.this);

                String[] items = { "Init", "Play", "Stop", "Toggle", "Last", "Next", "VolUp", "VolDown", "ToggleLoop", "GetVol", "GetCur" };

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MusicPlayerHelper helper = MusicPlayerHelper.getInstance(StartupActivity.this);
                        switch(which) {
                            case 0:
                                helper.addPlaylist(playlist);
                                break;
                            case 1:
                                helper.play();
                                break;
                            case 2:
                                helper.stop();
                                break;
                            case 3:
                                helper.toggle();
                                break;
                            case 4:
                                helper.last();
                                break;
                            case 5:
                                helper.next();
                                break;
                            case 6:
                                helper.volumeUp();
                                break;
                            case 7:
                                helper.volumeDown();
                                break;
                            case 8:
                                helper.toggleLoop();
                                break;
                            case 9:
                                helper.getVolume(new VolumeCallback() {
                                    @Override
                                    public void run(float volume) {
                                        Toast.makeText(getApplicationContext(), String.valueOf(volume), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            case 10:
                                helper.getPosition(new PositionCallback() {
                                    @Override
                                    public void run(int position) {
                                        Toast.makeText(getApplicationContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                        }
                    }
                }).show();
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
                PlaylistViewHelper.showPlaylistDialog(this, playlist);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
