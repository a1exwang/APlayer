package com.iced.alexwang.libs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import com.iced.alexwang.activities.CurrentPlaylistActivity;
import com.iced.alexwang.activities.LyricsActivity;
import com.iced.alexwang.activities.PlayerControlActivity;
import com.iced.alexwang.activities.R;
import com.iced.alexwang.activities.SearchActivity;
import com.iced.alexwang.activities.SelectFileActivity;
import com.iced.alexwang.activities.ShowPlaylistsActivity;
import com.iced.alexwang.views.lyrics.LyricsView;
import com.iced.alexwang.views.search_result.SearchResultView;

import java.security.InvalidParameterException;

public class ActivityHelper {
    public static void startSearchActivity(Context context, SearchResultView.SearchType type, String content) {
        Intent intent = new Intent(context, SearchActivity.class);
        if (type == SearchResultView.SearchType.Artist) {
            intent.putExtra(context.getString(R.string.search_data_item), context.getString(R.string.search_data_item_artist));
        } else if (type == SearchResultView.SearchType.Title) {
            intent.putExtra(context.getString(R.string.search_data_item), context.getString(R.string.search_data_item_title));
        } else if (type == SearchResultView.SearchType.Playlist) {
            intent.putExtra(context.getString(R.string.search_data_item), context.getString(R.string.search_data_item_playlist));
        } else
            throw new InvalidParameterException("Unknown search type");

        intent.putExtra(context.getString(R.string.search_data_content), content);
        context.startActivity(intent);
    }

    public static void startCurrentPlaylistActivity(Context context) {
        Intent intent = new Intent(context, CurrentPlaylistActivity.class);
        context.startActivity(intent);
    }

    public static void startShowPlaylistsActivity(Context context) {
        Intent intent = new Intent(context, ShowPlaylistsActivity.class);
        context.startActivity(intent);
    }

    public static void startLyricsActivity(Context context) {
        Intent intent = new Intent(context, LyricsActivity.class);
        context.startActivity(intent);
    }

    public static void showLyricsDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LyricsView lv = new LyricsView(context);
        lv.init();
        builder.setView(lv);
        builder.show();
    }

    public static void startPlayerControlActivity(Context context) {
        Intent intent = new Intent(context, PlayerControlActivity.class);
        context.startActivity(intent);
    }

    public static void startSelectFileActivity(Context context, String initDir) {
        Intent intent = new Intent(context, SelectFileActivity.class);
        intent.putExtra(context.getString(R.string.select_file_initial_directory), initDir);

        context.startActivity(intent);
    }

}
