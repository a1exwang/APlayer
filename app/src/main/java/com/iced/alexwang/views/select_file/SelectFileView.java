package com.iced.alexwang.views.select_file;

import android.app.Instrumentation;
import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.iced.alexwang.activities.R;
import com.iced.alexwang.libs.CachedFile;
import com.iced.alexwang.models.Playlist;
import com.iced.alexwang.models.Song;
import com.iced.alexwang.models.callbacks.FilesSelectedCallback;
import com.iced.alexwang.models.callbacks.FlushCallback;
import com.iced.alexwang.player.MusicPlayerHelper;
import com.iced.alexwang.views.decorator.DividerItemDecoration;

import java.util.ArrayList;
import java.util.regex.Pattern;


public class SelectFileView extends RelativeLayout {

    public SelectFileView(Context context, CachedFile currentDir) {
        super(context);
        this.currentDir = currentDir;
        initViews();
    }
    public SelectFileView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public void setOnFilesSelectedListener(FilesSelectedCallback fs) {
        onFilesSelected = fs;
    }

    private void initViews() {
        mainLayout = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.layout_select_file, null);
//
        viewFilesPlaceHolder = (RelativeLayout) mainLayout.findViewById(R.id.listSelectFile);

        fileListAdapter = new SelectFileAdapter(getContext(), currentDir);

        // Here are magic codes.
        // I can't notify the adapter but have to reset the adapter, or the view won't change!!
        final FlushCallback flushCallback = new FlushCallback() {
            @Override
            public void flush(CachedFile current) {
//                SelectFileAdapter newAdapter = new SelectFileAdapter(getContext(), current);
//                newAdapter.setOnFlushCallback(this);
//                viewFilesPlaceHolder.setAdapter(newAdapter);)
                fileListAdapter = new SelectFileAdapter(getContext(), current);
                fileListAdapter.setOnFlushCallback(this);
                fileListView.setAdapter(fileListAdapter);
                fileListAdapter.notifyDataSetChanged();
            }
        };

        fileListAdapter.setOnFlushCallback(flushCallback);

        fileListView = new RecyclerView(getContext());
        fileListView.setLayoutManager(new LinearLayoutManager(getContext()));
        fileListView.setAdapter(fileListAdapter);
        fileListView.setItemAnimator(new DefaultItemAnimator());
        fileListView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL_LIST));

        viewFilesPlaceHolder.addView(fileListView);

        btnPlayAll = (Button) mainLayout.findViewById(R.id.btnFileSelectPlayAll);
        btnPlayAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // when clicking the play all, clear playlists and play these ones
                Playlist playlist = new Playlist();

                for(CachedFile f : fileListAdapter.getAllSelectedFiles()) {
                    // check whether it's a valid music file.
                    if (Pattern.compile(getContext().getString(R.string.select_file_supported_files)).matcher(f.getName()).matches()) {
                        playlist.add(Song.createFromCachedFile(f));
                    }
                }

                MusicPlayerHelper playerHelper = MusicPlayerHelper.getInstance(getContext());
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

        btnAddAll = (Button) mainLayout.findViewById(R.id.btnFileSelectAddAll);
        btnAddAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onFilesSelected != null) {
                    ArrayList<CachedFile> files = new ArrayList<CachedFile>();
                    for (CachedFile f : fileListAdapter.getAllSelectedFiles()) {
                        // check whether it's a valid music file.
                        if (Pattern.compile(getContext().getString(R.string.select_file_supported_files)).matcher(f.getName()).matches()) {
                            files.add(f);
                        }
                    }
                    onFilesSelected.filesSelected(files);
                }
            }
        });

        btnClear = (Button) mainLayout.findViewById(R.id.btnSelectFileClear);
        btnClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        addView(mainLayout);
    }

    Button btnPlayAll, btnAddAll, btnClear;
    RelativeLayout viewFilesPlaceHolder;
    RelativeLayout mainLayout;

    RecyclerView fileListView;
    SelectFileAdapter fileListAdapter;

    CachedFile currentDir;

    FilesSelectedCallback onFilesSelected;
}
