package com.iced.alexwang.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

/**
 * Created by alexwang on 15-6-14.
 */
public class Artist implements Parcelable {

    public static class ArtistNameComparator implements Comparator<Artist> {

        public ArtistNameComparator(boolean reverse) {
            this.reverse = reverse;
        }

        @Override
        public int compare(Artist lhs, Artist rhs) {
            return reverse ? rhs.name.compareToIgnoreCase(lhs.name) : lhs.name.compareToIgnoreCase(rhs.name);
        }
        private boolean reverse;
    }

    public static final Parcelable.Creator<Artist> CREATOR = new Parcelable.Creator<Artist>() {
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    private Artist(Parcel parcel) {
        name = parcel.readString();
        type = parcel.readString();
    }
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(type);
    }

    public Artist(String name) {
        this.name = name;
        this.type = "";
    }

    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }

    private String name;
    private String type;
}
