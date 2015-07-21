package com.iced.alexwang.models.search_result;

import android.content.Context;
import android.graphics.Color;

public class TitleSeperatorSearchResult extends SearchResult {

    public TitleSeperatorSearchResult(Context context, int count) {
        super(context);
        this.count = count;
    }

    @Override
    public String getTitle() {
        return "Song";
    }

    @Override
    public String getAdditional() {
        return "(" + String.valueOf(count) + ")";
    }

    @Override
    protected int getTypeWeight() {
        return 19;
    }

    @Override
    public int getTitleColor() {
        return Color.rgb(0x4d, 0xd0, 0xe1);
    }

    @Override
    public float getTitleFontSize() {
        return 36;
    }
    int count;
}
