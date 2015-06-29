package com.iced.alexwang.models;

import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Playlist extends ArrayList<Song> implements Serializable, Parcelable {
    public Playlist() {
        current = 0;
    }
    public static Playlist createFromM3uFile(String dir){
        return null;
    }

    public String getUpperFolderName() {
        String path = get(current).getPath();
        Pattern pattern = Pattern.compile("/([^/]*)/[^/]+$");
        Matcher matcher = pattern.matcher(path);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "Playlist";
    }
    public String getUpperFolderPath() {
        String path = get(current).getPath();
        Pattern pattern = Pattern.compile("(/[^/]*)/[^/]+$");
        Matcher matcher = pattern.matcher(path);
        if (matcher.find()) {
            return matcher.group(1);
        }
        else
            return Environment.getExternalStorageDirectory().getAbsolutePath();
    }
    public String getCurrentSongPath(){
        return get(current).getPath();
    }
    public Song getCurrentSong() {
        if (current >= size() || current < 0)
            return null;
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

    /* for parcelable */
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

    /* for marshal and load */
    public byte[] marshal() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeInt(current);
            oos.writeInt(size());

            for (int i = 0; i < size(); ++i) {
                Song song = get(i);
                byte[] songBytes = song.marshal();
                oos.writeInt(songBytes.length);
                oos.write(songBytes);
            }
            oos.close();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Playlist load(byte[] buf, int offset, int _size) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(buf, offset, _size);
            ObjectInputStream ois = new ObjectInputStream(bis);

            int current = ois.readInt();

            int size = ois.readInt();

            Playlist playlist = new Playlist();
            for (int i = 0; i < size; ++i) {
                int songBytesSize = ois.readInt();
                byte[] songBytes = new byte[songBytesSize];
                if(-1 == ois.read(songBytes, 0, songBytesSize)) {
                    return null;
                }
                playlist.add(Song.load(songBytes, 0, songBytesSize));
            }
            playlist.current = current;

            return playlist;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    int current;
}
