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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.iced.alexwang.libs.CachedFile;
import com.iced.alexwang.libs.CachedFileSystem;
import com.iced.alexwang.models.Playlist;
import com.iced.alexwang.models.Song;
import com.iced.alexwang.models.callbacks.FilesSelectedCallback;
import com.iced.alexwang.models.callbacks.PlaylistCallback;
import com.iced.alexwang.player.MusicPlayerHelper;
import com.iced.alexwang.views.select_file.SelectFileAdapter;
import com.iced.alexwang.views.select_file.SelectFileView;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class SelectFileActivity extends Activity {

    /* Event Handlers */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file);

        playerHelper = MusicPlayerHelper.getInstance(this);

        Intent intent = getIntent();
        String initDir = intent.getStringExtra(getString(R.string.select_file_initial_directory));
        if (initDir == null || new File(initDir).isFile())
            currentDir = CachedFileSystem.getInstance().open(Environment.getExternalStorageDirectory().getAbsolutePath());
        else
            currentDir = CachedFileSystem.getInstance().open(initDir);

        mainLayout = (RelativeLayout) findViewById(R.id.layoutSelectFileActivityMain);

        // create file list
        selectFileView = new SelectFileView(this, currentDir);
        selectFileView.setOnFilesSelectedListener(new FilesSelectedCallback() {
            @Override
            public boolean filesSelected(final ArrayList<CachedFile> files) {

                playerHelper.getPlaylist(new PlaylistCallback() {
                    @Override
                    public void run(Playlist playlist) {
                        for (CachedFile f : files) {
                            // check whether it's a valid music file.
                            if (Pattern.compile(getString(R.string.select_file_supported_files)).matcher(f.getName()).matches()) {
                                playlist.add(Song.createFromCachedFile(f));
                            }
                        }
                        playerHelper.setPlaylist(playlist);
                        playerHelper.play();
                    }
                });
                return false;
            }
        });

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_START);
        params.addRule(RelativeLayout.ALIGN_PARENT_END);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mainLayout.addView(selectFileView, params);

        // init button save
        Button btnSave = (Button) findViewById(R.id.btnFileSelectSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CachedFileSystem.getInstance().save();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_select_file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_select_file_sort_by_file_name) {
            sortReverse = !sortReverse;
//            sort(new Comparator<SelectFile>() {
//                @Override
//                public int compare(SelectFile lhs, SelectFile rhs) {
//                    if (lhs.file.getName().equals(".."))
//                        return -1;
//                    else if(rhs.file.getName().equals(".."))
//                        return 1;
//                    else {
//                        return (sortReverse ? -1 : 1) * lhs.file.getName().compareToIgnoreCase(rhs.file.getName());
//                    }
//                }
//            });
        } else if (id == R.id.menu_select_file_sort_by_artist) {

        } else if (id == R.id.menu_select_file_sort_by_date) {

        }

        return super.onOptionsItemSelected(item);
    }

    /* Additional Data Structures */

    // filter files except music files and add a upper dir

    /* Functional Interfaces */
//    public void flush() {
//        SelectFileAdapter adapter = new SelectFileAdapter();
//        list.setAdapter(adapter);
//        adapter.notifyDataSetInvalidated();
//    }
//
//    public void sort(Comparator<SelectFile> comparator) {
//        SelectFileAdapter adapter = (SelectFileAdapter) list.getAdapter();
//        adapter.sort(comparator);
//        list.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
//    }


    /* Private Data */
    CachedFile currentDir;
    RelativeLayout mainLayout;
    SelectFileView selectFileView;

    boolean sortReverse = true;
    
    MusicPlayerHelper playerHelper;
}
