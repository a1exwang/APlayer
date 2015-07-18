package com.iced.alexwang.models.search_result;

import android.content.Context;
import android.view.View;

public abstract class SearchResult {
    public SearchResult(Context context) {
        this.context = context;
    }
    public abstract String getTitle();
    public abstract String getAdditional();
    public View.OnClickListener getOnClickListener() {
        return onClick;
    }
    public void setOnClickListener(View.OnClickListener onClick) {
        this.onClick = onClick;
    }
    public void setOnLongClickListener(View.OnLongClickListener onLongClick) {
        this.onLongClick = onLongClick;
    }

    public Context getContext() {
        return context;
    }

    View.OnClickListener onClick;
    View.OnLongClickListener onLongClick;
    Context context;
}
