package com.iced.alexwang.views.playlist;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iced.alexwang.activities.MusicDetailsActivity;
import com.iced.alexwang.activities.R;
import com.iced.alexwang.activities.SearchActivity;
import com.iced.alexwang.models.Artist;
import com.iced.alexwang.models.Playlist;
import com.iced.alexwang.models.Song;
import com.iced.alexwang.player.MusicPlayerHelper;

import java.util.Collections;
import java.util.Comparator;

public class PlaylistAdapter extends BaseAdapter {

    public PlaylistAdapter(Context context, Playlist objects) {
        this.playlist = objects;
        this.context = context;
    }

    @Override
    public int getCount() {
        return playlist.size();
    }
    @Override
    public Song getItem(int position) {
        return playlist.get(position);
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // convert song_pic Item to song_pic View
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Song listItem = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.playlist_item, parent, false);
            final ViewGroup layout = (ViewGroup) convertView;
            final TextView textTitle = (TextView) layout.findViewById(R.id.textPlaylistItemTitle);
            final TextView textArtist = (TextView) layout.findViewById(R.id.textPlaylistItemArtist);

            // init the view
            initView(layout, listItem);
            initTitle(textTitle, listItem, position);
            initArtist(textArtist, listItem);
            initCustomView(layout, listItem);

        }
        return convertView;
    }

    void initView(final ViewGroup layout, Song listItem) {
        final TextView textTitle = (TextView) layout.findViewById(R.id.textPlaylistItemTitle);

        // click the whole
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notification noti = new Notification.Builder(getContext())
                        .setContentTitle("APlayer")
                        .setSmallIcon(R.drawable.song_pic)
                        .setContentText(((TextView) layout.findViewById(R.id.textPlaylistItemTitle)).getText())
                        .build();
                NotificationManager mgr = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                mgr.notify(0, noti);
            }
        });
    }

    // click title to see detailed info
    void initTitle(final TextView textTitle, Song listItem, final int position) {
        textTitle.setText(listItem.getTitle());
        textTitle.setTextColor(Color.BLUE);
        textTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MusicDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(getContext().getString(R.string.detailed_data_music_path), textTitle.getText());
                getContext().startActivity(intent);
            }
        });
        textTitle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                String[] dlgItems = {"Play Now", "Remove1", "Info"};

                builder.setItems(dlgItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
//                                Notification noti = new Notification.Builder(getContext())
//                                        .setContentTitle("APlayer")
//                                        .setSmallIcon(R.drawable.song_pic)
//                                        .setContentText(textTitle.getText())
//                                        .build();
//                                NotificationManager mgr = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
//                                mgr.notify(1, noti);

                                MusicPlayerHelper helper = MusicPlayerHelper.getInstance(getContext());
                                helper.addPlaylist(playlist);
                                helper.playAt(position);
                                break;
                            case 1:
                                playlist.remove(position);
                                notifyDataSetChanged();
                                break;
                            case 2:
                                Intent intent = new Intent(getContext(), MusicDetailsActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(getContext().getString(R.string.detailed_data_music_path), textTitle.getText());
                                getContext().startActivity(intent);
                                break;
                            default:
                        }
                        dialog.cancel();
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
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
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = listItem.inflateView(inflater);
        if(v != null)
            layout.addView(v);
    }

    public void sort(Comparator<Song> com) {
        Collections.sort(playlist, com);
    }

    Context getContext() {
        return context;
    }

    Context context;
    Playlist playlist;
}
