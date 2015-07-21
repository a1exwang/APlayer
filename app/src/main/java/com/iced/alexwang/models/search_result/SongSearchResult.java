package com.iced.alexwang.models.search_result;

import android.content.Context;
import android.view.View;

import com.iced.alexwang.models.Playlist;
import com.iced.alexwang.models.Song;
import com.iced.alexwang.player.MusicPlayerHelper;
import com.iced.alexwang.views.search_result.OnSongResultClickListener;

public class SongSearchResult extends SearchResult {

    public SongSearchResult(Song song, Context context) {
        super(context);
        this.song = song;
        setOnClickListener(new OnSongResultClickListener() {
            @Override
            public void onClick(View v) {
                Playlist playlist = new Playlist();
                playlist.add(SongSearchResult.this.song);
                MusicPlayerHelper.getInstance(getContext()).setPlaylist(playlist);
                MusicPlayerHelper.getInstance(getContext()).play();
            }
        });
    }

    @Override
    public String getTitle() {
        return song.getTitle();
    }

    @Override
    public String getAdditional() {
        return song.getArtist().getName();
    }

    @Override
    public int getTypeWeight() {
        return 10;
    }

    Song song;
}
