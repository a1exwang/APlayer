package com.iced.alexwang.models.search_result;

import android.content.Context;
import android.graphics.Color;


public class ArtistSeperatorSearchResult extends SearchResult {

    public ArtistSeperatorSearchResult(Context context, int count) {
        super(context);
        this.count = count;
    }

    @Override
    public String getTitle() {
        return "Artist";
    }

    @Override
    public String getAdditional() {
        return "(" + String.valueOf(count) + ")";
    }

    @Override
    protected int getTypeWeight() {
        return 9;
    }
    @Override
    public int getTitleColor() {
        return Color.rgb(0x4d, 0xd0, 0xe1);
    }

    @Override
    public float getTitleFontSize() {
        return 36;
    }
    int count = 0;
}
