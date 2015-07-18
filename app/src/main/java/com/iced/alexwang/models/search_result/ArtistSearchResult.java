package com.iced.alexwang.models.search_result;

import android.content.Context;
import android.view.View;

import com.iced.alexwang.models.Artist;
import com.iced.alexwang.models.Playlist;
import com.iced.alexwang.models.Song;
import com.iced.alexwang.player.MusicPlayerHelper;
import com.iced.alexwang.views.search_result.OnArtistResultClickListener;

import java.util.ArrayList;
import java.util.Comparator;

public class ArtistSearchResult extends SearchResult {

    public static class ArtistTitleComparator implements Comparator<ArtistSearchResult> {
        public ArtistTitleComparator(boolean reverse) {
            this.reverse = reverse;
        }
        @Override
        public int compare(ArtistSearchResult lhs, ArtistSearchResult rhs) {
            return (reverse ? -1 : 1) * lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
        }
        boolean reverse;
    }

    public static class ArtistCountOfSongsComparator implements Comparator<ArtistSearchResult> {
        public ArtistCountOfSongsComparator(boolean reverse) {
            this.reverse = reverse;
        }
        @Override
        public int compare(ArtistSearchResult lhs, ArtistSearchResult rhs) {
            int cmpSongSize =  (reverse ? -1 : 1) * (lhs.getSongsSize() - rhs.getSongsSize());
            if (cmpSongSize == 0) {
                int cmpTitle = lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
                return cmpTitle;
            }
            else
                return cmpSongSize;
        }
        boolean reverse;
    }

    public ArtistSearchResult(Context context, Artist artist, ArrayList<Song> songs) {
        super(context);
        this.artist = artist;
        this.songs = songs;

        setOnClickListener(new OnArtistResultClickListener() {
            @Override
            public void onClick(View v) {
                Playlist playlist = new Playlist();
                playlist.addAll(ArtistSearchResult.this.songs);
                MusicPlayerHelper.getInstance(getContext()).setPlaylist(playlist);
                MusicPlayerHelper.getInstance(getContext()).play();
            }
        });
    }

    @Override
    public String getTitle() {
        return artist.getName();
    }

    @Override
    public String getAdditional() {
        return String.valueOf(songs.size());
    }
    public int getSongsSize() {
        return songs.size();
    }

    Artist artist;
    ArrayList<Song> songs;
}
