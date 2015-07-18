package com.iced.alexwang.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.iced.alexwang.views.search_result.SearchResultView;

public class SearchActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        resultView = (SearchResultView) findViewById(R.id.viewSearchResult);

        Intent intent = getIntent();
        String item = intent.getExtras().getString(getString(R.string.search_data_item));
        String content = intent.getExtras().getString(getString(R.string.search_data_content));

        if (item.equals(getString(R.string.search_data_item_title))) {
            resultView.search(SearchResultView.SearchType.Title, content);
        } else if (item.equals(getString(R.string.search_data_item_artist))) {
            resultView.search(SearchResultView.SearchType.Artist, content);
        } else if (item.equals(getString(R.string.search_data_item_playlist))) {
            resultView.search(SearchResultView.SearchType.Playlist, content);
        } else
            return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_search_result_sort_by_title) {
            resultView.sortByTitle();
            resultView.flush();
            return true;
        } else if (id == R.id.menu_search_result_sort_by_additional) {
            resultView.sortByAdditional();
            resultView.flush();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    TextView tvItem, tvContent;
    SearchResultView resultView;
}
