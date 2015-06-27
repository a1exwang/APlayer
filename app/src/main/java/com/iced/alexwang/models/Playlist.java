package com.iced.alexwang.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by alexwang on 15-6-14.
 */
public class Playlist extends ArrayList<Song> implements Serializable, Parcelable {
    public Playlist() {
        current = 0;
    }
    public static Playlist createFromM3uFile(String dir){
        return null;
    }

    public String getCurrentSongPath(){
        return get(current).getPath();
    }
    public Song getCurrentSong() {
        return get(current);
    }
    public Song nextSong() {
        if (size() != 0) {
            current++;
            current %= size();
            return get(current);
        }
        return null;
    }
    public Song lastSong() {
        if (size() != 0) {
            current--;
            if(current <= 0)
                current = size() - 1;
            return get(current);
        }
        return null;
    }

    int current;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(size());
        for(int i = 0; i < size(); ++i) {
            dest.writeParcelable(get(i), 0);
        }

        dest.writeInt(current);
    }
    public static final Parcelable.Creator<Playlist> CREATOR = new Parcelable.Creator<Playlist>() {
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

    private Playlist(Parcel in) {
        int size = in.readInt();
        for(int i = 0; i < size; ++i) {
            add((Song)in.readParcelable(Song.class.getClassLoader()));
        }
        current = in.readInt();
    }
}
