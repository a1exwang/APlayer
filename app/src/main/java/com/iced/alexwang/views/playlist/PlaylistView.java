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
import com.iced.alexwang.views.decorator.DividerItemDecoration;

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
        listAdapter = new PlaylistAdapter(getContext(), playlist);
        listView.setAdapter(listAdapter);
    }

    public void flush() {
        listView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
    }

    private void createListView() {
        listAdapter = new PlaylistAdapter(getContext(), playlist);
        listView = new RecyclerView(getContext());
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setAdapter(listAdapter);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL_LIST));
        addView(listView);
    }

    // view helpers
    public void sortByTitle() {
        listAdapter.sort(new Song.TitleComparator(sortReverse));
        sortReverse = !sortReverse;
        flush();
    }
    public void sortByArtist() {
        listAdapter.sort(new Song.ArtistComparator(sortReverse));
        sortReverse = !sortReverse;
        flush();
    }
    public void sortByDate() {

    }

    public void clear() {
        playlist = new Playlist();
        listAdapter = new PlaylistAdapter(getContext(), playlist);
        flush();
    }

    boolean sortReverse = false;

    RecyclerView listView;
    PlaylistAdapter listAdapter;
    Playlist playlist = new Playlist();
}
