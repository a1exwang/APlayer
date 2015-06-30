package com.iced.alexwang.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.iced.alexwang.libs.MarshalHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
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

    // for marshal and load
    public byte[] marshal() {
        try {
            byte[] bytesName = name.getBytes(Charset.forName("UTF-8"));
            byte[] bytesType = type.getBytes(Charset.forName("UTF-8"));

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeInt(bytesName.length);
            oos.write(bytesName);
            oos.writeInt(bytesType.length);
            oos.write(bytesType);
            oos.close();
            byte[] ret = bos.toByteArray();
            bos.close();
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Artist load(byte[] buf, int offset, int size) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(buf, offset, size);
            ObjectInputStream ois = new ObjectInputStream(bis);

            int nameSize = ois.readInt();
            byte[] bytesName = new byte[nameSize];
            ois.readFully(bytesName, 0, nameSize);

            int typeSize = ois.readInt();
            byte[] bytesType = new byte[typeSize];
            ois.readFully(bytesType, 0, typeSize);

            Artist ret = new Artist(new String(bytesName, Charset.forName("UTF-8")));
            ret.type = new String(bytesType, Charset.forName("UTF-8"));
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
