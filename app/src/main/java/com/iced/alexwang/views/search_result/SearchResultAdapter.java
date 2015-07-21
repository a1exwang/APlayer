package com.iced.alexwang.views.search_result;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iced.alexwang.activities.R;
import com.iced.alexwang.models.search_result.SearchResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);

            textTitle = (TextView) itemView.findViewById(R.id.textSearchResultTitle);
            textAdditional = (TextView) itemView.findViewById(R.id.textSearchResultAdditionalInfo);
        }

        public TextView textTitle;
        public TextView textAdditional;
    }

    public SearchResultAdapter(Context context, ArrayList<SearchResult> results) {
        if (results == null) {
            results = new ArrayList<>();
        }
        this.context = context;
        this.results = results;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_search_result, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SearchResult result = results.get(position);
        holder.textTitle.setText(result.getTitle());
        holder.textTitle.setOnClickListener(result.getOnClickListener());
        holder.textTitle.setTextColor(result.getTitleColor());
        holder.textTitle.setTextSize(result.getTitleFontSize());

        holder.textAdditional.setText(result.getAdditional());
        holder.textAdditional.setTextColor(result.getAdditionalColor());
        holder.textAdditional.setTextSize(result.getAdditionallFontSize());
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public void sortByTitle() {
        reverse = !reverse;
        Collections.sort(results, new Comparator<SearchResult>() {
            @Override
            public int compare(SearchResult lhs, SearchResult rhs) {
                return (reverse ? -1 : 1) * lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
            }
        });
    }
    public void sortByAdditional() {
        reverse = !reverse;
        Collections.sort(results, new Comparator<SearchResult>() {
            @Override
            public int compare(SearchResult lhs, SearchResult rhs) {
                return (reverse ? -1 : 1) * lhs.getAdditional().compareToIgnoreCase(rhs.getAdditional());
            }
        });
    }

    boolean reverse = true;
    ArrayList<SearchResult> results = new ArrayList<>();
    Context context;
}
