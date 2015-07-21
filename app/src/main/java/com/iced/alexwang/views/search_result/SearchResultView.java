package com.iced.alexwang.views.search_result;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.iced.alexwang.activities.R;
import com.iced.alexwang.libs.CachedFile;
import com.iced.alexwang.libs.CachedFileSystem;
import com.iced.alexwang.models.Artist;
import com.iced.alexwang.models.Song;
import com.iced.alexwang.models.callbacks.ParameterizedRunnable;
import com.iced.alexwang.models.search_result.ArtistSearchResult;
import com.iced.alexwang.models.search_result.ArtistSeperatorSearchResult;
import com.iced.alexwang.models.search_result.SearchResult;
import com.iced.alexwang.models.search_result.SongSearchResult;
import com.iced.alexwang.models.search_result.TitleSeperatorSearchResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

public class SearchResultView extends RelativeLayout {
    public SearchResultView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();

    }
    public enum SearchType {
        Title,
        Artist,
        Playlist,
        All
    }

    private void initViews() {
        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.layout_search_result, this);

        recyclerView = new RecyclerView(getContext());
        adapter = new SearchResultAdapter(getContext(), null);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        layout.addView(recyclerView);
        currentDir = CachedFileSystem.getInstance().open(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music");
    }

    public void search(SearchType type, String content) {

        if (type.equals(SearchType.Artist)) {
            searchArtist(content);
        } else if (type.equals(SearchType.Title)) {
            searchSong(content);
        } else if (type.equals(SearchType.Artist)) {
            searchPlaylist(content);
        } else if (type.equals(SearchType.All)) {
            searchAll(content);
        }
        flush();
    }

    public void searchAllFromResult(final String content) {
        ArrayList<SearchResult> songs = new ArrayList<>();
        ArrayList<SearchResult> artists = new ArrayList<>();

        boolean artist = true;
        for (SearchResult r : results) {
            if (r instanceof ArtistSeperatorSearchResult) {
                artist = true;
            } else if (r instanceof TitleSeperatorSearchResult) {
                artist = false;
            } else if (r.getTitle().toLowerCase().contains(content.toLowerCase())){
                if (artist) {
                    artists.add(r);
                } else {
                    songs.add(r);
                }
            }
        }
        results.clear();
        results.add(new ArtistSeperatorSearchResult(getContext(), artists.size()));
        results.addAll(artists);
        results.add(new TitleSeperatorSearchResult(getContext(), songs.size()));
        results.addAll(songs);
    }

    public void searchAll(final String content) {
        results.clear();
        final LinkedHashMap<com.iced.alexwang.models.Artist, ArrayList<Song>> allArtistResults = new LinkedHashMap<>();
        final ArrayList<SearchResult> songResult = new ArrayList<SearchResult>();
        final ArrayList<SearchResult> artistResult = new ArrayList<SearchResult>();
        CachedFileSystem.dfsCachedSubDirs(currentDir, new ParameterizedRunnable() {
            @Override
            public Object run(Object obj) {
                CachedFile file = (CachedFile) obj;
                Song song = Song.createFromCachedFile(file);
                if (song != null && song.getTitle().toLowerCase().contains(content.toLowerCase())) {
                    songResult.add(new SongSearchResult(song, getContext()));
                }
                if (song != null && song.getArtist().getName().toLowerCase().contains(content.toLowerCase())) {
                    if (!allArtistResults.containsKey(song.getArtist()))
                        allArtistResults.put(song.getArtist(), new ArrayList<Song>());
                    allArtistResults.get(song.getArtist()).add(song);
                }
                return null;
            }
        });

        // dictionary to array list
        ArrayList<ArtistSearchResult> sortedResults = new ArrayList<>();
        for (Object ar : allArtistResults.keySet().toArray()) {
            sortedResults.add(new ArtistSearchResult(getContext(), (Artist) ar, allArtistResults.get(ar)));
        }
        Collections.sort(sortedResults, new ArtistSearchResult.ArtistCountOfSongsComparator(true));


        results.add(new ArtistSeperatorSearchResult(getContext(), sortedResults.size()));
        results.addAll(sortedResults);
        results.add(new TitleSeperatorSearchResult(getContext(), songResult.size()));
        results.addAll(songResult);
    }

    public void searchSong(final String title) {
        results.clear();
        CachedFileSystem.dfsCachedSubDirs(currentDir, new ParameterizedRunnable() {
            @Override
            public Object run(Object obj) {
                CachedFile file = (CachedFile) obj;

                Song song = Song.createFromCachedFile(file);
                if (song != null && song.getTitle().toLowerCase().contains(title.toLowerCase())) {
                    results.add(new SongSearchResult(song, getContext()));
                }
                return null;
            }
        });
    }
    public void searchArtist(final String artist) {
        results.clear();
        final LinkedHashMap<com.iced.alexwang.models.Artist, ArrayList<Song>> allResults = new LinkedHashMap<>();
        CachedFileSystem.dfsCachedSubDirs(currentDir, new ParameterizedRunnable() {
            @Override
            public Object run(Object obj) {
                CachedFile file = (CachedFile) obj;

                Song song = Song.createFromCachedFile(file);
                if (song != null && song.getArtist().getName().toLowerCase().contains(artist.toLowerCase())) {
                    if (!allResults.containsKey(song.getArtist()))
                        allResults.put(song.getArtist(), new ArrayList<Song>());
                    allResults.get(song.getArtist()).add(song);
                }
                return null;
            }
        });

        ArrayList<ArtistSearchResult> unsortedResults = new ArrayList<>();
        for (Object ar : allResults.keySet().toArray()) {
            unsortedResults.add(new ArtistSearchResult(getContext(), (Artist) ar, allResults.get(ar)));
        }
        Collections.sort(unsortedResults, new ArtistSearchResult.ArtistCountOfSongsComparator(true));
        results.addAll(unsortedResults);
    }
    public void searchPlaylist(final String playlist) {

    }

    public void sortByAdditional() {
        adapter.sortByAdditional();
    }
    public void sortByTitle() {
        adapter.sortByTitle();
    }

    public void flush() {
        adapter = new SearchResultAdapter(getContext(), results);
        recyclerView.setAdapter(adapter);
    }

    RecyclerView recyclerView;
    SearchResultAdapter adapter;


    CachedFile currentDir;
    ArrayList<SearchResult> results = new ArrayList<>();
}
