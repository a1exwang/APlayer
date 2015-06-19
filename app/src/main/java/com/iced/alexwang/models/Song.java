package com.iced.alexwang.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by alexwang on 15-6-14.
 */

public class Song extends Item implements Parcelable, Serializable {

    // comarators
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
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(path);
        dest.writeParcelable(artist, 0);
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

    public Song(String title, Artist artist, String path) {
        this.title = title;
        this.artist = artist;
        this.path = path;
    }

    public String getTitle() {
        return title;
    }
    public Artist getArtist() {
        return artist;
    }
    public String getPath() {
        return path;
    }

    private String title;
    private Artist artist;
    private String path;
}
