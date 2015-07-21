/*
 * This activity receives an key
 * R.string.select_file_initial_directory => String, indicating the file directory to start from,
 * If this directory is invalid, it will start from root of sdcard.
 * If the sdcard doesn't exist, then crash~
 *
* */


package com.iced.alexwang.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.iced.alexwang.libs.CachedFile;
import com.iced.alexwang.libs.CachedFileSystem;
import com.iced.alexwang.models.Playlist;
import com.iced.alexwang.models.Song;
import com.iced.alexwang.models.callbacks.CurrentDirectoryChangedCallback;
import com.iced.alexwang.models.callbacks.FilesSelectedCallback;
import com.iced.alexwang.models.callbacks.PlaylistCallback;
import com.iced.alexwang.player.MusicPlayerHelper;
import com.iced.alexwang.views.main.LeftDrawerAdapter;
import com.iced.alexwang.views.main.ViewHelper;
import com.iced.alexwang.views.select_file.SelectFileView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class SelectFileActivity extends Activity {

    /* Event Handlers */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ViewHelper.getDrawerLayout(this, LayoutInflater.from(this).inflate(R.layout.activity_select_file, null), new LeftDrawerAdapter(this, 3)));
        playerHelper = MusicPlayerHelper.getInstance(this);

        Intent intent = getIntent();
        String initDir = intent.getStringExtra(getString(R.string.select_file_initial_directory));
        if (initDir == null || new File(initDir).isFile())
            currentDir = CachedFileSystem.getInstance().open(Environment.getExternalStorageDirectory().getAbsolutePath());
        else
            currentDir = CachedFileSystem.getInstance().open(initDir);

        mainLayout = (RelativeLayout) findViewById(R.id.layoutSelectFileActivityMain);

        // create file list
        selectFileView = SelectFileView.attachToViewGroup(SelectFileActivity.this, currentDir, mainLayout);
        selectFileView.setOnFilesSelectedListener(new FilesSelectedCallback() {
            @Override
            public boolean filesSelected(final ArrayList<CachedFile> files) {
                playerHelper.getPlaylist(new PlaylistCallback() {
                    @Override
                    public void onPlaylistReceived(Playlist playlist) {
                        for (CachedFile f : files) {
                            // check whether it's a valid music file.
                            if (Pattern.compile(getString(R.string.select_file_supported_files)).matcher(f.getName()).matches()) {
                                Song song = Song.createFromCachedFile(f);
                                if (song != null) {
                                    playlist.add(song);
                                }
                            }
                        }
                        playerHelper.setPlaylist(playlist);
                    }
                });
                return false;
            }
        });
        selectFileView.setDirectoryChangedCallback(new CurrentDirectoryChangedCallback() {
            @Override
            public void directoryChanged(CachedFile current) {
                Intent intent = new Intent();
                intent.putExtra(getString(R.string.select_file_current_directory), current.getAbsolutePath());
                setResult(0, intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        load();
        selectFileView.flushCurrentDirectory(currentDir);
    }
    @Override
    protected void onStop() {
        super.onStop();
        save();
    }

    public void save() {
        try {
            FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + getString(R.string.select_file_current_directory_path));
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            String dir = selectFileView.getCurrentDir().getAbsolutePath();
            byte[] dirBytes = dir.getBytes(Charset.forName("UTF-8"));
            oos.writeInt(dirBytes.length);
            oos.write(dirBytes);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void load() {
        String saveFileDir = Environment.getExternalStorageDirectory() + "/" + getString(R.string.select_file_current_directory_path);
        File file = new File(saveFileDir);
        if(file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(saveFileDir);
                ObjectInputStream ois = new ObjectInputStream(fis);

                int size = ois.readInt();
                byte[] dirBytes = new byte[size];
                ois.read(dirBytes, 0, size);
                String dir = new String(dirBytes, Charset.forName("UTF-8"));
                currentDir = CachedFileSystem.getInstance().open(dir);

                ois.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /* Private Data */
    CachedFile currentDir;
    RelativeLayout mainLayout;
    SelectFileView selectFileView;

    MusicPlayerHelper playerHelper;
}
