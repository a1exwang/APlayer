package com.iced.alexwang.libs;

import android.media.MediaMetadataRetriever;

import com.iced.alexwang.player.MusicPlayerHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MusicMetadataHelper {

    public static MusicMetadataHelper create(String path) {
        try {
            return new MusicMetadataHelper(path);
        } catch(IllegalArgumentException e) {
            return null;
        }
    }

    private MusicMetadataHelper(String path) {
        this.path = path;
        retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);

        patternMusicFileName = Pattern.compile("/([^/-]+)-([^/-]+)\\.[A-Za-z0-9]+$");
        patternFileName = Pattern.compile("/([^/]*)\\.[A-Za-z0-9]+$");
        patternUpperFolderName = Pattern.compile("/([^/]+)/[^/]+$");

        // initialize title and author

        artist = null;
        title = null;
        // First, try file name scheme
        Matcher matcherMusicFileName = patternMusicFileName.matcher(path);
        if(matcherMusicFileName.find()) {
            artist = matcherMusicFileName.group(1);
            title = matcherMusicFileName.group(2);
        }

        // Second, try metadata
        if (artist == null)
            artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        if (title == null)
            title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

        // Third, use file name
        if (title == null) {
            Matcher matcherFileName = patternFileName.matcher(path);
            if(matcherFileName.find()) {
                title = matcherFileName.group(1);
            } else {
                title = "";
            }
        }
        if (artist == null)
            artist = "";

    }

    public String getTitle() {
        return title;
    }
    public String getArtist(){
        return artist;
    }
    public int getBitrate(){
        return Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE));

    }
    public String getAlbum() {
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
    }
    public String getPath() {
        return path;
    }
    public String getPlaylist() {
        Matcher matcher = patternUpperFolderName.matcher(path);
        if (matcher.find())
            return matcher.group(1);
        else
            return "";
    }
    public String getPlaylistPath() {
        Matcher matcher = patternUpperFolderPath.matcher(path);
        if (matcher.find())
            return matcher.group(1);
        else
            return "";
    }

    Pattern patternMusicFileName = Pattern.compile("/([^/-]+)-([^/-]+)\\.[A-Za-z0-9]+$");
    Pattern patternFileName = Pattern.compile("/([^/]*)\\.[A-Za-z0-9]+$");
    Pattern patternUpperFolderName = Pattern.compile("/([^/]+)/[^/]+");
    Pattern patternUpperFolderPath = Pattern.compile("^(.+)/[^/]+$");

    MediaMetadataRetriever retriever;
    String path;

    String title;
    String artist;
}
