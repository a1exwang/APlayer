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
import android.widget.Toast;

import com.iced.alexwang.libs.CachedFile;
import com.iced.alexwang.libs.CachedFileSystem;
import com.iced.alexwang.models.Playlist;
import com.iced.alexwang.models.Song;
import com.iced.alexwang.models.callbacks.PositionCallback;
import com.iced.alexwang.models.callbacks.VolumeCallback;
import com.iced.alexwang.player.MusicPlayerHelper;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StartupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        playerHelper = MusicPlayerHelper.getInstance(this);

        layout = (ViewGroup) findViewById(R.id.layoutStartup);

        btnSelectFiles = (Button) findViewById(R.id.btnShowFileSelect);
        btnSelectFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartupActivity.this, SelectFileActivity.class);
                intent.putExtra(getString(R.string.select_file_initial_directory), Environment.getExternalStorageDirectory());

                startActivity(intent);
            }
        });

        btnPlaylist = (Button) findViewById(R.id.btnShowPlaylist);
        btnPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartupActivity.this, CurrentPlaylistActivity.class);
                startActivity(intent);
            }
        });

        btnPlaylists = (Button) findViewById(R.id.btnShowPlaylistList);
        btnPlaylists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartupActivity.this, ShowPlaylistsActivity.class);
                startActivity(intent);
            }
        });
//        btnPlaylists.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Playlist playlist = new Playlist();
//                CachedFile file = CachedFileSystem.getInstance().open("/storage/sdcard/Music/History/2009.06.03");
//                ArrayList<CachedFile> files = file.getChildren();
//                for (CachedFile f : files) {
//                    if (Pattern.compile(getString(R.string.select_file_supported_files)).matcher(f.getName()).matches()) {
//                        playlist.add(Song.createFromCachedFile(f));
//                    }
//                }
//
//
//                byte[] b1 = playlist.get(7).marshal();
//                Song s1 = Song.load(b1, 0, b1.length);
//
//
//                byte[] b = playlist.marshal();
//                Playlist p = Playlist.load(b, 0, b.length);
//                StringBuilder sb = new StringBuilder();
//                for (Song s : p) {
//                    sb.append(s.getPath() + "\n");
//                }
//
//                Toast.makeText(StartupActivity.this, sb.toString(), Toast.LENGTH_SHORT).show();
//            }
//        });


        btnPlayer = (Button) findViewById(R.id.btnPlayer);
        btnPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StartupActivity.this);

                String[] items = { "Init", "Play", "Stop", "Toggle", "Last", "Next", "VolUp", "VolDown", "ToggleLoop", "GetVol", "GetCur" };

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which) {
                            case 0:
                                break;
                            case 1:
                                playerHelper.play();
                                break;
                            case 2:
                                playerHelper.stop();
                                break;
                            case 3:
                                playerHelper.toggle();
                                break;
                            case 4:
                                playerHelper.last();
                                break;
                            case 5:
                                playerHelper.next();
                                break;
                            case 6:
                                playerHelper.volumeUp();
                                break;
                            case 7:
                                playerHelper.volumeDown();
                                break;
                            case 8:
                                playerHelper.toggleLoop();
                                break;
                            case 9:
                                playerHelper.getVolume(new VolumeCallback() {
                                    @Override
                                    public void run(float volume) {
                                        Toast.makeText(getApplicationContext(), String.valueOf(volume), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            case 10:
                                playerHelper.getPosition(new PositionCallback() {
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    Button btnPlayer, btnPlaylist, btnPlaylists, btnSelectFiles;
    ViewGroup layout;

    MusicPlayerHelper playerHelper;

}
