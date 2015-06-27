/*
 * This activity receives an key
 * R.string.select_file_initial_directory => String, indicating the file directory to start from,
 * If this directory is invalid, it will start from root of sdcard.
 * If the sdcard doesn't exist, then crash~
 *
* */


package com.iced.alexwang.activities;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.iced.alexwang.libs.CachedFile;
import com.iced.alexwang.libs.CachedFileSystem;
import com.iced.alexwang.models.Artist;
import com.iced.alexwang.models.Playlist;
import com.iced.alexwang.models.Song;
import com.iced.alexwang.models.callbacks.ParameterizedRunnable;
import com.iced.alexwang.models.callbacks.PlaylistCallback;
import com.iced.alexwang.player.MusicPlayerHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Pattern;

public class SelectFileActivity extends Activity {

    /* Event Handlers */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file);

        playerHelper = MusicPlayerHelper.getInstance(this);

        // init start up file dir
        list = (ListView) findViewById(R.id.listSelectFile);

        Intent intent = getIntent();
        String initDir = intent.getStringExtra(getString(R.string.select_file_initial_directory));
        if (initDir == null || new File(initDir).isFile())
            currentDir = CachedFileSystem.getInstance().open(Environment.getExternalStorageDirectory().getAbsolutePath());
        else
            currentDir = CachedFileSystem.getInstance().open(initDir);

        flush();

        // init views
        btnPlayAll = (Button) findViewById(R.id.btnFileSelectPlayAll);
        btnPlayAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // when clicking the play all, clear playlists and play these ones
                Playlist playlist = new Playlist();

                SelectFileAdapter adapter = (SelectFileAdapter) list.getAdapter();
                for(CachedFile f : adapter.getAllSelectedFiles()) {
                    // check whether it's a valid music file.
                    if (Pattern.compile(getString(R.string.select_file_supported_files)).matcher(f.getName()).matches()) {
                        playlist.add(Song.createFromCachedFile(f));
                    }
                }

                playerHelper.setPlaylist(playlist);
                playerHelper.play();

                new Thread() {
                    @Override
                    public void run() {
                        Instrumentation inst = new Instrumentation();
                        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                    }
                }.start();

            }
        });

        btnAddAll = (Button) findViewById(R.id.btnAddAll);
        btnAddAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SelectFileAdapter adapter = (SelectFileAdapter) list.getAdapter();

                playerHelper.getPlaylist(new PlaylistCallback() {
                    @Override
                    public void run(Playlist playlist) {
                        for (CachedFile f : adapter.getAllSelectedFiles()) {
                            // check whether it's a valid music file.
                            if (Pattern.compile(getString(R.string.select_file_supported_files)).matcher(f.getName()).matches()) {
                                playlist.add(Song.createFromCachedFile(f));
                            }
                        }
                        playerHelper.setPlaylist(playlist);
                        playerHelper.play();
                    }
                });
            }
        });

        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CachedFileSystem.getInstance().save();
            }
        });

        Button btnClear = (Button) findViewById(R.id.btnClearFile);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);

                    byte[] strBytes = "Haha123".getBytes(Charset.forName("UTF-8"));
                    oos.writeInt(strBytes.length);
                    oos.write(strBytes);

                    oos.flush();

                    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
                    ObjectInputStream ois = new ObjectInputStream(bis);

                    int size = ois.readInt();
                    byte[] buffer = new byte[size];
                    ois.read(buffer, 0, size);

                    String str = new String(buffer, "UTF-8");

                    str.toString();


                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            sort(new Comparator<SelectFile>() {
                @Override
                public int compare(SelectFile lhs, SelectFile rhs) {
                    if (lhs.file.getName().equals(".."))
                        return -1;
                    else if(rhs.file.getName().equals(".."))
                        return 1;
                    else {
                        return (sortReverse ? -1 : 1) * lhs.file.getName().compareToIgnoreCase(rhs.file.getName());
                    }
                }
            });
        } else if (id == R.id.menu_select_file_sort_by_artist) {

        } else if (id == R.id.menu_select_file_sort_by_date) {

        }

        return super.onOptionsItemSelected(item);
    }

    /* Additional Data Structures */

    // Data structure for SelectFileAdapter
    static class SelectFile {
        public boolean checked;
        public CachedFile file;

        public SelectFile(CachedFile f, boolean c) {
            file = f;
            checked = c;
        }
    }

    // This adapter is for the ListView in the Activity
    class SelectFileAdapter extends ArrayAdapter<SelectFile> {

        public SelectFileAdapter() {
            super(SelectFileActivity.this, R.layout.select_file_item, fileFilter(currentDir));
        }

        public ArrayList<CachedFile> getAllSelected() {
            ArrayList<CachedFile> ret = new ArrayList<>();
            for (int i = 0; i < getCount(); ++i) {
                if (getItem(i).checked) {
                    ret.add(getItem(i).file);
                }
            }
            return ret;
        }

        public ArrayList<CachedFile> getAllSelectedFiles() {
            final ArrayList<CachedFile> ret = new ArrayList<>();
            for (int i = 0; i < getCount(); ++i) {
                if (getItem(i).checked) {
                    if(getItem(i).file.isDirectory()) {
                        CachedFileSystem.dfsCachedSubDirs(getItem(i).file, new ParameterizedRunnable() {
                            @Override
                            public Object run(Object obj) {
                                CachedFile current = (CachedFile) obj;
                                if(!current.isDirectory()) {
                                    ret.add(current);
                                }
                                return null;
                            }
                        });
                    } else {
                        ret.add(getItem(i).file);
                    }
                }
            }
            return ret;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            // This true cannot be removed or else there will be bugs
            // a non-null convertView is not always valid.
            if (convertView == null || true) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.select_file_item, parent, false);
                final ViewGroup layout = (ViewGroup) convertView;

                final CachedFile cf = getItem(position).file;
                final TextView tvFile = (TextView) layout.findViewById(R.id.textFileName);
                final ImageView imageBack = (ImageView) layout.findViewById(R.id.imgParentDir);

                if(cf.getName().equals("..")) {
                    // this view is 'go to parent'
                    tvFile.setVisibility(View.INVISIBLE);
                    imageBack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!currentDir.getAbsolutePath().equals(Environment.getExternalStorageDirectory().toString())) {
                                currentDir = currentDir.getParrent();
                                flush();
                            }
                        }
                    });
                } else {
                    imageBack.setVisibility(View.INVISIBLE);
                    tvFile.setText(cf.getName());
                    tvFile.setTextColor(cf.isDirectory() ? Color.BLUE : Color.BLACK);
                    tvFile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            // click directory to enter it
                            if (cf.isDirectory())
                                currentDir = cf;
                            else {
                                // click a file to add it to the end of the playlist
                                playerHelper.getPlaylist(new PlaylistCallback() {
                                    @Override
                                    public void run(Playlist playlist) {
                                        playlist.add(new Song("title", new Artist("me"), cf.getAbsolutePath()));
                                        playerHelper.setPlaylist(playlist);
                                        playerHelper.play();
                                        playerHelper.last();
                                    }
                                });
                            }

                            flush();
                        }
                    });
                }

                final CheckBox checkSelect = (CheckBox) layout.findViewById(R.id.checkSelected);
                if(!cf.getName().equals("..")) {
                    checkSelect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getItem(position).checked = checkSelect.isChecked();
                        }
                    });
                } else {
                    checkSelect.setVisibility(View.INVISIBLE);
                }
            }
            return convertView;
        }
    }

    // filter files except music files and add a upper dir
    private ArrayList<SelectFile> fileFilter(CachedFile file) {
        Pattern patternSupportedFiles = Pattern.compile(getString(R.string.select_file_supported_files));
        ArrayList<SelectFile> objs = new ArrayList<>();

        if (!file.isDirectory())
            file = file.getParrent();

        objs.add(new SelectFile(CachedFile.create("/..", true), false));
        for (CachedFile f : file.getChildren()) {
            if (f.isDirectory() || patternSupportedFiles.matcher(f.getName()).matches())
                objs.add(new SelectFile(f, false));
        }
        return objs;
    }

    /* Functional Interfaces */
    public void flush() {
        SelectFileAdapter adapter = new SelectFileAdapter();
        list.setAdapter(adapter);
        adapter.notifyDataSetInvalidated();
    }

    public void sort(Comparator<SelectFile> comparator) {
        SelectFileAdapter adapter = (SelectFileAdapter) list.getAdapter();
        adapter.sort(comparator);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    /* Private Data */
    Button btnPlayAll, btnAddAll;
    ListView list;
    CachedFile currentDir;

    boolean sortReverse = true;
    
    MusicPlayerHelper playerHelper;
}
