package com.iced.alexwang.views.playlist;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iced.alexwang.activities.MusicDetailsActivity;
import com.iced.alexwang.activities.R;
import com.iced.alexwang.activities.SearchActivity;
import com.iced.alexwang.models.Playlist;
import com.iced.alexwang.models.Song;
import com.iced.alexwang.models.callbacks.RemoveSongCallback;
import com.iced.alexwang.player.MusicPlayerHelper;

import java.util.Collections;
import java.util.Comparator;

public class PlaylistAdapter extends RecyclerView.Adapter {

    public class PlaylistViewHolder extends RecyclerView.ViewHolder {
        public PlaylistViewHolder(ViewGroup itemView) {
            super(itemView);
            this.itemView = itemView;
        }
        public ViewGroup getView() {
            return itemView;
        }

        private ViewGroup itemView;
    }

    public PlaylistAdapter(Context context, Playlist objects) {
        this.playlist = objects;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        ViewGroup convertView = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.playlist_item, viewGroup, false);
        return new PlaylistViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final Song listItem = getItem(position);

        final ViewGroup layout = ((PlaylistViewHolder)viewHolder).getView();
        final TextView textTitle = (TextView) layout.findViewById(R.id.textPlaylistItemTitle);
        final TextView textArtist = (TextView) layout.findViewById(R.id.textPlaylistItemArtist);
        // init the view
        initView(layout, listItem, position);
        initTitle(textTitle, listItem, position);
        initArtist(textArtist, listItem);
        initCustomView(layout, listItem);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return playlist.size();
    }

    public Song getItem(int position) {
        return playlist.get(position);
    }

    public void sort(Comparator<Song> com) {
        Collections.sort(playlist, com);
    }

    public void add(Song song) {
        playlist.add(song);
    }

    public void add(int index, Song song) {
        playlist.add(index, song);
    }

    public void remove(int index) {
        playlist.remove(index);
    }

    public void setRemoveCallback(RemoveSongCallback c) {
        removeCallback = c;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    void initView(final ViewGroup layout, final Song listItem, final int position) {
        final TextView textTitle = (TextView) layout.findViewById(R.id.textPlaylistItemTitle);
    }

    // click title to see detailed info
    void initTitle(final TextView textTitle, final Song listItem, final int position) {
        textTitle.setText(listItem.getTitle());
        textTitle.setTextColor(Color.BLUE);
        textTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MusicDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(getContext().getString(R.string.detailed_data_music_path), listItem.getPath());
                getContext().startActivity(intent);
            }
        });
        textTitle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                String[] dlgItems = {"Play Now", "Remove", "Info"};

                builder.setItems(dlgItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                MusicPlayerHelper helper = MusicPlayerHelper.getInstance(getContext());
                                helper.setPlaylist(playlist);
                                helper.playAt(position);
                                break;
                            case 1:
                                if (removeCallback != null)
                                    removeCallback.remove(position);
                                playlist.remove(position);
                                break;
                            case 2:
                                Intent intent = new Intent(getContext(), MusicDetailsActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(getContext().getString(R.string.detailed_data_music_path), listItem.getPath());
                                getContext().startActivity(intent);
                                break;
                            default:
                        }
                    }
                });
                builder.show();
                return false;
            }
        });
    }

    // click artist to search for the artist
    void initArtist(final TextView textArtist, Song listItem) {
        textArtist.setText(listItem.getArtist().getName());
        textArtist.setTextColor(Color.BLACK);
        textArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(getContext().getString(R.string.search_data_item), getContext().getString(R.string.search_data_item_artist));
                intent.putExtra(getContext().getString(R.string.search_data_content), textArtist.getText());
                getContext().startActivity(intent);
            }
        });
    }

    // if the song has a custom view, init it.
    void initCustomView(final ViewGroup layout, Song listItem) {
        // show customized information of the song
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = listItem.inflateView(inflater);
        if(v != null)
            layout.addView(v);
    }

    Context getContext() {
        return context;
    }

    Context context;
    Playlist playlist;

    RemoveSongCallback removeCallback;
}
