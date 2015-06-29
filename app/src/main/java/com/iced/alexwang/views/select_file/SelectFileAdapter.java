package com.iced.alexwang.views.select_file;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.iced.alexwang.activities.R;
import com.iced.alexwang.libs.CachedFile;
import com.iced.alexwang.libs.CachedFileSystem;
import com.iced.alexwang.models.Artist;
import com.iced.alexwang.models.Playlist;
import com.iced.alexwang.models.SelectFile;
import com.iced.alexwang.models.Song;
import com.iced.alexwang.models.callbacks.CurrentDirectoryChangedCallback;
import com.iced.alexwang.models.callbacks.FlushCallback;
import com.iced.alexwang.models.callbacks.ParameterizedRunnable;
import com.iced.alexwang.models.callbacks.PlaylistCallback;
import com.iced.alexwang.player.MusicPlayerHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;
// This adapter is for the ListView in the Activity
public class SelectFileAdapter extends RecyclerView.Adapter {

    private static ArrayList<SelectFile> fileFilter(CachedFile file, Context context) {
        Pattern patternSupportedFiles = Pattern.compile(context.getString(R.string.select_file_supported_files));
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

    public SelectFileAdapter(Context context, CachedFile currentDir) {
        this.context = context;
        this.currentDir = currentDir;
        this.playerHelper = MusicPlayerHelper.getInstance(context);

        this.fileList = fileFilter(currentDir, context);
    }

    public void sort(Comparator<SelectFile> comparator) {
        Collections.sort(fileList, comparator);
    }

    public void selectAll() {
        for (int i = 0; i < fileList.size(); ++i) {
            fileList.get(i).checked = true;
        }
        if (onFlush != null)
            onFlush.flushChanged();
    }
    public void unselectAll() {
        for (int i = 0; i < fileList.size(); ++i) {
            fileList.get(i).checked = false;
        }
        if (onFlush != null)
            onFlush.flushChanged();
    }
    public void selectReverse() {
        for (int i = 0; i < fileList.size(); ++i) {
            fileList.get(i).checked = !fileList.get(i).checked;
        }
        if (onFlush != null)
            onFlush.flushChanged();
    }

    public ArrayList<CachedFile> getAllSelected() {
        ArrayList<CachedFile> ret = new ArrayList<>();
        for (int i = 0; i < fileList.size(); ++i) {
            if (fileList.get(i).checked) {
                ret.add(fileList.get(i).file);
            }
        }
        return ret;
    }

    public ArrayList<CachedFile> getAllSelectedFiles() {
        final ArrayList<CachedFile> ret = new ArrayList<>();
        for (int i = 0; i < fileList.size(); ++i) {
            if (fileList.get(i).checked) {
                if(fileList.get(i).file.isDirectory()) {
                    CachedFileSystem.dfsCachedSubDirs(fileList.get(i).file, new ParameterizedRunnable() {
                        @Override
                        public Object run(Object obj) {
                            CachedFile current = (CachedFile) obj;
                            if (!current.isDirectory()) {
                                ret.add(current);
                            }
                            return null;
                        }
                    });
                } else {
                    ret.add(fileList.get(i).file);
                }
            }
        }
        return ret;
    }

    public void setOnFlushCallback(FlushCallback onFlush) {
        this.onFlush = onFlush;
    }
    public void setOnCurrentDirectoryChangedCallback(CurrentDirectoryChangedCallback onChanged) {
        this.onChanged = onChanged;
    }


    class SelectFileViewHolder extends RecyclerView.ViewHolder {

        public SelectFileViewHolder(View itemView) {
            super(itemView);
        }

        public ViewGroup getView() {
            return (ViewGroup) itemView;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup convertView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.select_file_item, parent, false);
        return new SelectFileViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final CachedFile cf = fileList.get(position).file;
        final TextView tvFile = (TextView) holder.itemView.findViewById(R.id.textFileName);

        if(cf.getName().equals("..")) {
            // this view is 'go to parent'
            tvFile.setText("..");
            tvFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!currentDir.getAbsolutePath().equals(Environment.getExternalStorageDirectory().toString())) {
                        currentDir = currentDir.getParrent();
                        if (onChanged != null)
                            onChanged.directoryChanged(currentDir);
                    }
                }
            });
        } else {
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
                                playlist.add(Song.createFromCachedFile(cf));
                                playerHelper.setPlaylist(playlist);
                                playerHelper.play();
                                playerHelper.last();
                            }
                        });
                    }

                    if (onChanged != null)
                        onChanged.directoryChanged(currentDir);
                }
            });
        }

        final CheckBox checkSelect = (CheckBox) holder.itemView.findViewById(R.id.checkSelected);
        checkSelect.setChecked(fileList.get(position).checked);
        if(!cf.getName().equals("..")) {
            checkSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fileList.get(position).checked = checkSelect.isChecked();
                }
            });
        } else {
            checkSelect.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public long getItemId(int position) { return position; }
    @Override
    public int getItemCount() {
        return fileList.size();
    }

    Context context;
    CachedFile currentDir;
    MusicPlayerHelper playerHelper;
    ArrayList<SelectFile> fileList = new ArrayList<>();

    /* callbacks */
    FlushCallback onFlush;
    CurrentDirectoryChangedCallback onChanged;
}
