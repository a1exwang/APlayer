package com.iced.alexwang.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.iced.alexwang.libs.CachedFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by alexwang on 15-6-14.
 */

public class Song extends Item implements Parcelable, Serializable {

    // comparators
    public static class TitleComparator implements Comparator<Song> {
        public TitleComparator(boolean reverse){
            this.reverse = reverse;
        }
        @Override
        public int compare(Song lhs, Song rhs) {
            return reverse ? rhs.title.compareToIgnoreCase(lhs.title) : lhs.title.compareToIgnoreCase(rhs.title);
        }
        boolean reverse;
    }
    public static class ArtistComparator implements Comparator<Song> {
        public ArtistComparator(boolean reverse){
            this.reverse = reverse;
        }
        @Override
        public int compare(Song lhs, Song rhs) {
            return new Artist.ArtistNameComparator(reverse).compare(lhs.artist, rhs.artist);
        }
        boolean reverse;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Song) {
            Song other = (Song) obj;
            return other.path.equals(path);
        }
        return false;
    }

    // to marshal and load
    public byte[] marshal() {
        try {
            byte[] bytesTitle = title.getBytes(Charset.forName("UTF-8"));
            byte[] bytesArtist = artist.marshal();
            byte[] bytesPath = path.getBytes(Charset.forName("UTF-8"));
            byte[] bytesLyricsFile = lrcPath.getBytes(Charset.forName("UTF-8"));

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeInt(bytesTitle.length);
            oos.write(bytesTitle);
            oos.writeInt(bytesArtist.length);
            oos.write(bytesArtist);
            oos.writeInt(bytesPath.length);
            oos.write(bytesPath);
            oos.writeInt(bytesLyricsFile.length);
            oos.write(bytesLyricsFile);

            oos.close();
            byte[] ret = bos.toByteArray();
            bos.close();
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Song load(byte[] buf, int offset, int _size) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(buf, offset, _size);
            ObjectInputStream ois = new ObjectInputStream(bis);

            int size = ois.readInt();
            byte[] bytes = new byte[size];
            ois.readFully(bytes, 0, size);
            String title = new String(bytes, Charset.forName("UTF-8"));

            size = ois.readInt();
            bytes = new byte[size];
            ois.readFully(bytes, 0, size);
            Artist artist = Artist.load(bytes, 0, size);

            size = ois.readInt();
            bytes = new byte[size];
            ois.readFully(bytes, 0, size);
            String path = new String(bytes, Charset.forName("UTF-8"));

            size = ois.readInt();
            bytes = new byte[size];
            ois.readFully(bytes, 0, size);
            String lrcPath = new String(bytes, Charset.forName("UTF-8"));

            return new Song(title, artist, path, lrcPath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // for parcel
    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
    private Song(Parcel in) {
        title = in.readString();
        path = in.readString();
        artist = in.readParcelable(Artist.class.getClassLoader());
        lrcPath = in.readString();
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(path);
        dest.writeParcelable(artist, 0);
        dest.writeString(lrcPath);
    }
    @Override
    public int describeContents() {
        return 0;
    }

    // for model search
    public static ArrayList<Song> getAll() {
        return null;
    }
    public static Song find(int id){
        return null;
    }
    public static ArrayList<Song> findByArtist(Artist artist) {
        return null;
    }

    public Song(String title, Artist artist, String path, String lrcPath) {
        this.title = title;
        this.artist = artist;
        this.path = path;
        this.lrcPath = lrcPath;
    }
    public static Song createFromCachedFile(CachedFile file) {
        // Seperate the file name and check suffix to make sure it is a music file
        String suffix = file.getSuffix();
        String name = file.getNameWithoutSuffix();
        String lrcFile = file.getParrent().getAbsolutePath() + '/' + name + ".lrc";

        if (patternSuffix.matcher(suffix).matches()) {
            // Here, it is a music file.

            Matcher matcherArtistAndTitle = patternArtistTitle.matcher(name);
            if (matcherArtistAndTitle.matches()) {
                String title = matcherArtistAndTitle.group(2);
                String artist = matcherArtistAndTitle.group(1);
                return new Song(title, new Artist(artist), file.getAbsolutePath(), lrcFile);
            }
            else
                return new Song(file.getNameWithoutSuffix(), Artist.Unknown, file.getAbsolutePath(), lrcFile);
        } else {
            return null;
        }
    }

    static final Pattern patternSuffix = Pattern.compile("mp3|m4a|aac|wav|flac|ape|wma", Pattern.CASE_INSENSITIVE);
    static final Pattern patternArtistTitle = Pattern.compile("^(.*[^ ]) ?- ?([^ ][^-]*)$");

    public String getTitle() {
        return title;
    }
    public Artist getArtist() {
        return artist;
    }
    public String getPath() {
        return path;
    }
    public String getLrcPath() {
        return lrcPath;
    }

    private String title;
    private Artist artist;
    private String path;
    private String lrcPath;
}
