/*
* The View contains a playlist and some events to handle player events like play, pause, stop.
* It creates a empty playlist by default.
* */

package com.iced.alexwang.views.playlist;
import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.iced.alexwang.models.Playlist;
import com.iced.alexwang.models.Song;
import com.iced.alexwang.models.callbacks.PlaylistChangedCallback;
import com.iced.alexwang.player.MusicPlayerHelper;
import com.iced.alexwang.views.decorator.DividerItemDecoration;

import java.util.Collections;

public class PlaylistView extends RelativeLayout {
    public PlaylistView(Context context) {
        super(context);
        createListView();
    }
    public PlaylistView(Context context, AttributeSet attr) {
        super(context, attr);
        createListView();
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
        listAdapter = new PlaylistAdapter(getContext(), playlist);
        listAdapter.setChangedCallback(songChanged);
        listView.setAdapter(listAdapter);
    }

    public void flush() {
        listAdapter = new PlaylistAdapter(getContext(), playlist);
        listView.setAdapter(listAdapter);

        playerHelper.setPlaylist(playlist, false);
        //MusicPlayerHelper.getInstance(getContext()).setPlaylist(listAdapter.getPlaylist());
        //MusicPlayerHelper.getInstance(getContext()).play();
    }

    private void createListView() {
        playerHelper = MusicPlayerHelper.getInstance(getContext());
        listAdapter = new PlaylistAdapter(getContext(), playlist);
        listAdapter.setChangedCallback(songChanged);

        listView = new RecyclerView(getContext());
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setAdapter(listAdapter);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL_LIST));
        addView(listView);
    }

    // view helpers
    public void sortByTitle() {
        Collections.sort(playlist, new Song.TitleComparator(sortReverse));
        sortReverse = !sortReverse;
        flush();
    }
    public void sortByArtist() {
        Collections.sort(playlist, new Song.ArtistComparator(sortReverse));
        sortReverse = !sortReverse;
        flush();
    }
    public void sortByDate() {

    }

    public void clear() {
        playlist = new Playlist();
        flush();
    }

    PlaylistChangedCallback songChanged = new PlaylistChangedCallback() {
        @Override
        public void changed(Playlist newPlaylist) {
            playlist = newPlaylist;
            flush();
        }
    };

    boolean sortReverse = false;

    MusicPlayerHelper playerHelper;
    RecyclerView listView;
    PlaylistAdapter listAdapter;
    Playlist playlist = new Playlist();
}
