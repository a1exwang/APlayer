package com.iced.alexwang.models.search_result;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

public abstract class SearchResult implements Comparable<SearchResult> {
    public SearchResult(Context context) {
        this.context = context;
    }
    public abstract String getTitle();
    public abstract String getAdditional();

    public int getTitleColor() {
        return Color.BLACK;
    }
    public int getAdditionalColor() {
        return Color.GRAY;
    }
    public float getTitleFontSize() {
        return 20;
    }
    public float getAdditionallFontSize() {
        return 15;
    }

    public View.OnClickListener getOnClickListener() {
        return onClick;
    }
    public void setOnClickListener(View.OnClickListener onClick) {
        this.onClick = onClick;
    }
    public void setOnLongClickListener(View.OnLongClickListener onLongClick) {
        this.onLongClick = onLongClick;
    }

    protected abstract int getTypeWeight();

    @Override
    public int compareTo(SearchResult other) {
        int typeCompare = getTypeWeight() - other.getTypeWeight();
        if (typeCompare != 0)
            return typeCompare;
        return getTitle().compareToIgnoreCase(other.getTitle());
    }

    public Context getContext() {
        return context;
    }

    View.OnClickListener onClick;
    View.OnLongClickListener onLongClick;
    Context context;
}
