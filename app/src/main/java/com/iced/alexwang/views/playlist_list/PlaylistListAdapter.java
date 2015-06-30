package com.iced.alexwang.views.playlist_list;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iced.alexwang.activities.R;
import com.iced.alexwang.models.Playlist;
import com.iced.alexwang.models.PlaylistList;
import com.iced.alexwang.models.callbacks.FlushCallback;
import com.iced.alexwang.models.callbacks.PlayCurrentListCallback;
import com.iced.alexwang.player.MusicPlayerHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by alexwang on 15-6-30.
 */
public class PlaylistListAdapter extends RecyclerView.Adapter<PlaylistListAdapter.PlaylistListViewHolder> {

    static class PlaylistListViewHolder extends RecyclerView.ViewHolder {

        public PlaylistListViewHolder(ViewGroup parent, Context context) {
            super(LayoutInflater.from(context).inflate(R.layout.playlist_list_item, null));
            textPlaylistName = (TextView) itemView.findViewById(R.id.textPlaylistListItemName);
            textPlaylistPath = (TextView) itemView.findViewById(R.id.textPlaylistListItemPath);
            textPlaylistName.setOnClickListener(clickTitle);
        }

        public void setCurrent() {
            textPlaylistName.setTextColor(Color.RED);
        }

        public void setIndex(int index) {
            this.index = index;
        }
        public void setPlaylistName(String name) {
            textPlaylistName.setText(name);
        }

        public void setPlaylistPath(String path) {
            textPlaylistPath.setText(path);
        }

        public void setPlayCurrentListCallback(PlayCurrentListCallback cb) {
            playCurrentCallback = cb;
        }

        View.OnClickListener clickTitle = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrent();
                if (playCurrentCallback != null) {
                    playCurrentCallback.playCurrentList(index);
                }
            }
        };

        int index;
        PlayCurrentListCallback playCurrentCallback;
        TextView textPlaylistName, textPlaylistPath;
    }


    public PlaylistListAdapter(Context context, PlaylistList playlists) {
        this.context = context;
        this.playlistList = playlists;
    }

    @Override
    public PlaylistListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PlaylistListViewHolder(parent, context);
    }

    @Override
    public void onBindViewHolder(PlaylistListViewHolder holder, int position) {
        Playlist playlist = playlistList.get(position);

        holder.setIndex(position);
        holder.setPlaylistName(playlist.getUpperFolderName());
        holder.setPlaylistPath(absPathToSdcardPath(playlist.getUpperFolderPath()));
        if (position == playlistList.getCurrent()) {
            holder.setCurrent();
        }
        holder.setPlayCurrentListCallback(new PlayCurrentListCallback() {
            @Override
            public void playCurrentList(int index) {
                setCurrentPlaylist(index);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }

    public void addPlaylist(Playlist playlist) {
        playlistList.addPlaylist(playlist);
    }

    // setting -1 means that you want the last element.
    public void setCurrentPlaylist(int index) {
        if (index >= 0) {
            playlistList.setCurrentPlaylist(index);
        } else if (index + playlistList.size() > 0) {
            playlistList.setCurrentPlaylist(index + playlistList.size());
        } else
            return;
        if (onFlush != null)
            onFlush.flushChanged();

        Playlist playlist = playlistList.get(index);
        MusicPlayerHelper.getInstance(context).setPlaylist(playlist);
        MusicPlayerHelper.getInstance(context).play();
    }

    public void removePlaylist(int index) {
        playlistList.removePlaylist(index);
    }

    public void setPlaylists(PlaylistList ll) {
        if (ll != null)
            playlistList = ll;
    }
    public PlaylistList getPlaylists() {
        return playlistList;
    }

    public void setFlushCallback(FlushCallback cb) {
        onFlush = cb;
    }

    private String absPathToSdcardPath(String absPath) {
        String sdcardDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        Pattern pattern = Pattern.compile(sdcardDir.replace(".", "\\.") + "/(.*)$");
        Matcher matcher = pattern.matcher(absPath);
        if (matcher.find()) {
            String newPath = matcher.group(1);
            return newPath;
        }
        else {
            return absPath;
        }
    }

    /* Private members */
    Context context;
    PlaylistList playlistList = new PlaylistList();
    FlushCallback onFlush;
}