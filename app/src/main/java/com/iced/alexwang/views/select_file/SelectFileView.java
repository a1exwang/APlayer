/*
* This file provides a reusable view for showing the select music file view
* use attachToViweGroup to add it to your view group.
* You should provide a file selected callback to notify you when the files are submitted
* Another useful method is flushCurrentDirectory, to change current directory that the view shows
*
* */


package com.iced.alexwang.views.select_file;

import android.app.Instrumentation;
import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.iced.alexwang.activities.R;
import com.iced.alexwang.libs.CachedFile;
import com.iced.alexwang.libs.CachedFileSystem;
import com.iced.alexwang.models.Playlist;
import com.iced.alexwang.models.SelectFile;
import com.iced.alexwang.models.Song;
import com.iced.alexwang.models.callbacks.CurrentDirectoryChangedCallback;
import com.iced.alexwang.models.callbacks.FilesSelectedCallback;
import com.iced.alexwang.models.callbacks.FlushCallback;
import com.iced.alexwang.player.MusicPlayerHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;


public class SelectFileView extends RelativeLayout {

    public static SelectFileView attachToViewGroup(Context context, CachedFile currentDir, ViewGroup vg) {
        SelectFileView selectFileView = new SelectFileView(context, currentDir);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_START);
        params.addRule(RelativeLayout.ALIGN_PARENT_END);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        vg.addView(selectFileView, params);
        return selectFileView;
    }

    public SelectFileView(Context context) {
        super(context);
        this.currentDir = CachedFileSystem.getInstance().open(Environment.getExternalStorageDirectory().getAbsolutePath());
        initViews();
    }

    public SelectFileView(Context context, CachedFile currentDir) {
        super(context);
        this.currentDir = currentDir;
        initViews();
    }
    public SelectFileView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public void setOnFilesSelectedListener(FilesSelectedCallback fs) { onFilesSelected = fs; }
    public void setDirectoryChangedCallback(CurrentDirectoryChangedCallback cb) { onDirSelected = cb; }

    public void selectAll() {
        fileListAdapter.selectAll();
    }
    public void unselectAll() {
        fileListAdapter.unselectAll();
    }
    public void selectReverse() {
        fileListAdapter.selectReverse();
    }

    public void flushCurrentDirectory(CachedFile current) {
        fileListAdapter = new SelectFileAdapter(getContext(), current);
        fileListAdapter.setOnFlushCallback(flushCallback);
        fileListAdapter.setOnCurrentDirectoryChangedCallback(directoryChangedCallback);
        fileListView.setAdapter(fileListAdapter);
        fileListAdapter.notifyDataSetChanged();

        textCurrentPath.setCurrentDir(current);
        currentDir = current;
    }
    public void flushChanged() {
        fileListView.setAdapter(fileListAdapter);
        fileListAdapter.notifyDataSetChanged();
    }

    public void sort(Comparator<SelectFile> comparator) {
        fileListAdapter.sort(comparator);
        flushChanged();
    }
    public void sortByFileName() {
        sortReverse = !sortReverse;
        sort(new SelectFile.SelectFileNameComparator(sortReverse));
    }
    public void sortByDate() {
        sortReverse = !sortReverse;
        sort(new SelectFile.SelectFileDateComparator(sortReverse));
    }

    public CachedFile getCurrentDir() {
        return currentDir;
    }

    private void initViews() {
        mainLayout = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.layout_select_file, null);

        // init buttons
        btnPlayAll = (Button) mainLayout.findViewById(R.id.btnSelectFilePlayAll);
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

        btnAddAll = (Button) mainLayout.findViewById(R.id.btnSelectFileAddAll);
        btnAddAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onFilesSelected != null) {
                    ArrayList<CachedFile> files = new ArrayList<>();
                    for (CachedFile f : fileListAdapter.getAllSelectedFiles()) {
                        // check whether it's a valid music file.
                        if (Pattern.compile(getContext().getString(R.string.select_file_supported_files)).matcher(f.getName()).matches()) {
                            files.add(f);
                        }
                    }
                    Collections.sort(files, new Comparator<CachedFile>() {
                                @Override
                                public int compare(CachedFile lhs, CachedFile rhs) {
                                    return lhs.getName().compareToIgnoreCase(rhs.getName());
                                }
                    });
                    onFilesSelected.filesSelected(files);
                }
                fileListAdapter.unselectAll();
            }
        });

        btnSelectHere = (Button) mainLayout.findViewById(R.id.btnSelectFileSelectCurrentDirectory);
        btnSelectHere.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDirSelected != null)
                    onDirSelected.directoryChanged(currentDir);
            }
        });

        btnSelectAll = (Button) mainLayout.findViewById(R.id.btnSelectFileSelectAll);
        btnSelectAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAll();
            }
        });

        btnUnselectAll = (Button) mainLayout.findViewById(R.id.btnSelectFileUnselectAll);
        btnUnselectAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                unselectAll();
            }
        });

        btnReverseSelect = (Button) mainLayout.findViewById(R.id.btnSelectFileReverse);
        btnReverseSelect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectReverse();
            }
        });

        btnSortByDate = (Button) mainLayout.findViewById(R.id.btnSelectFileSortByDate);
        btnSortByDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByDate();
            }
        });

        btnSortByFileName = (Button) mainLayout.findViewById(R.id.btnSelectFileSortByFileName);
        btnSortByFileName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByFileName();
            }
        });

        textCurrentPath = (PathNavigationView) mainLayout.findViewById(R.id.textCurrentPath);
        textCurrentPath.setCurrentDir(currentDir);
        textCurrentPath.setCurrentDirectoryChangedCallback(new CurrentDirectoryChangedCallback() {
            @Override
            public void directoryChanged(CachedFile current) {
               flushFileList(current);
            }
        });
        //textCurrentPath.setText(currentDir.getRelativePathToSdcard());

        // init file list
        viewFilesPlaceHolder = (RelativeLayout) mainLayout.findViewById(R.id.listSelectFile);

        fileListAdapter = new SelectFileAdapter(getContext(), currentDir);

        fileListAdapter.setOnFlushCallback(flushCallback);
        fileListAdapter.setOnCurrentDirectoryChangedCallback(directoryChangedCallback);

        fileListView = new RecyclerView(getContext());
        fileListView.setLayoutManager(new LinearLayoutManager(getContext()));
        fileListView.setAdapter(fileListAdapter);
        //fileListView.setItemAnimator(new DefaultItemAnimator());
        //fileListView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL_LIST));

        viewFilesPlaceHolder.addView(fileListView);

        addView(mainLayout);
    }

    private void flushFileList(CachedFile current) {
        fileListAdapter = new SelectFileAdapter(getContext(), current);
        fileListAdapter.setOnFlushCallback(flushCallback);
        fileListAdapter.setOnCurrentDirectoryChangedCallback(directoryChangedCallback);
        fileListView.setAdapter(fileListAdapter);
        fileListAdapter.notifyDataSetChanged();
    }

    // callbacks

    // Here are magic codes.
    // I can't notify the adapter but have to reset the adapter, or the view won't change!!
    FlushCallback flushCallback = new FlushCallback() {
        @Override
        public void flushChanged() {
            SelectFileView.this.flushChanged();
        }
    };
    CurrentDirectoryChangedCallback directoryChangedCallback = new CurrentDirectoryChangedCallback() {
        @Override
        public void directoryChanged(CachedFile current) {
            currentDir = current;
            flushCurrentDirectory(current);
            textCurrentPath.setCurrentDir(current);
        }
    };

    Button btnPlayAll, btnAddAll, btnSelectAll, btnUnselectAll, btnReverseSelect, btnSelectHere;
    Button btnSortByDate, btnSortByFileName;
    PathNavigationView textCurrentPath;
    RelativeLayout viewFilesPlaceHolder;
    RelativeLayout mainLayout;

    RecyclerView fileListView;
    SelectFileAdapter fileListAdapter;
    boolean sortReverse = true;

    CachedFile currentDir;

    CurrentDirectoryChangedCallback onDirSelected;
    FilesSelectedCallback onFilesSelected;
}
