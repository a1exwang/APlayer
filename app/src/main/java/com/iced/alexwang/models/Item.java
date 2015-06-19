package com.iced.alexwang.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

/**
 * Created by alexwang on 15-6-13.
 */
public class Item implements Parcelable, View.OnClickListener {

    // Parcelable implements
    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    private Item(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Item() {
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(v.getContext(), "ListItem", Toast.LENGTH_SHORT).show();
    }

    // used to make song_pic custom view
    public View inflateView(LayoutInflater inflater){
        return null;
    }
}
