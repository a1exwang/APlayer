package com.iced.alexwang.views.playlist_list;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.iced.alexwang.activities.R;
import com.iced.alexwang.libs.CachedFile;
import com.iced.alexwang.models.Playlist;
import com.iced.alexwang.models.PlaylistList;
import com.iced.alexwang.models.Song;
import com.iced.alexwang.models.callbacks.FilesSelectedCallback;
import com.iced.alexwang.models.callbacks.FlushCallback;
import com.iced.alexwang.models.callbacks.PlaylistCallback;
import com.iced.alexwang.player.MusicPlayerHelper;
import com.iced.alexwang.views.decorator.DividerItemDecoration;
import com.iced.alexwang.views.select_file.SelectFileView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class PlaylistListView extends RelativeLayout {
    public PlaylistListView(Context context, AttributeSet attrSet) {
        super(context, attrSet);
        initViews();
    }

    public void save() {
        try {
            FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + getContext().getString(R.string.playlists_save_file_path));
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            byte[] bytes = listAdapter.getPlaylists().marshal();
            oos.writeInt(bytes.length);
            oos.write(bytes);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void load() {
        String saveFileDir = Environment.getExternalStorageDirectory() + "/" + getContext().getString(R.string.playlists_save_file_path);
        File file = new File(saveFileDir);
        if(file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(saveFileDir);
                ObjectInputStream ois = new ObjectInputStream(fis);

                int size = ois.readInt();
                byte[] bytes = new byte[size];
                ois.readFully(bytes, 0, size);
                PlaylistList ll = PlaylistList.load(bytes, 0, bytes.length);
                if (ll != null)
                    listAdapter.setPlaylists(ll);

                ois.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void flush() {
        //listAdapter.notifyDataSetChanged();
        llView.setAdapter(listAdapter);
    }

    private void initViews() {
        mainLayout = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.layout_playlist_list, this);

        layoutPlaylists = (RelativeLayout) mainLayout.findViewById(R.id.layoutPlaylists);
        PlaylistList playlists = new PlaylistList();

        llView = new RecyclerView(getContext());
        llView.setLayoutManager(new LinearLayoutManager(getContext()));
        listAdapter = new PlaylistListAdapter(getContext(), playlists);
        listAdapter.setFlushCallback(flushCallback);
        llView.setAdapter(listAdapter);
        llView.setItemAnimator(new DefaultItemAnimator());
        llView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL_LIST));
        layoutPlaylists.addView(llView);

        btnAdd = (Button) mainLayout.findViewById(R.id.btnPlaylistListAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                SelectFileView selectFileView = new SelectFileView(getContext());
                selectFileView.setOnFilesSelectedListener(new FilesSelectedCallback() {
                    @Override
                    public boolean filesSelected(ArrayList<CachedFile> files) {
                        Playlist playlist = new Playlist();
                        for (CachedFile f : files) {
                            playlist.add(Song.createFromCachedFile(f));
                        }
                        listAdapter.addPlaylist(playlist);
                        flush();
                        return true;
                    }
                });
                builder.setView(selectFileView);
                builder.show();
            }
        });
    }

    RelativeLayout mainLayout;

    RelativeLayout layoutPlaylists;
    RecyclerView llView;
    PlaylistListAdapter listAdapter;
    FlushCallback flushCallback = new FlushCallback() {
        @Override
        public void flushChanged() {
            llView.setAdapter(listAdapter);
        }
    };

    Button btnAdd;
}
