package com.iced.alexwang.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iced.alexwang.activities.R;
import com.iced.alexwang.models.Playlist;
import com.iced.alexwang.models.PlaylistList;
import com.iced.alexwang.models.callbacks.PlaylistCallback;
import com.iced.alexwang.player.MusicPlayerHelper;
import com.iced.alexwang.views.decorator.DividerItemDecoration;
import com.iced.alexwang.views.playlist.PlaylistAdapter;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ShowPlaylistsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_playlists);

        btnAdd = (Button) findViewById(R.id.btnPlaylistListAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        layoutPlaylists = (RelativeLayout) findViewById(R.id.layoutPlaylists);
        MusicPlayerHelper.getInstance(this).getPlaylist(new PlaylistCallback() {
            @Override
            public void run(Playlist playlist) {
                if (playlist != null && playlist.size() > 0) {
                    PlaylistList playlists = new PlaylistList();
                    playlists.addPlaylist(playlist);

                    RecyclerView list = new RecyclerView(ShowPlaylistsActivity.this);
                    list.setLayoutManager(new LinearLayoutManager(ShowPlaylistsActivity.this));
                    list.setAdapter(new PlaylistListAdapter(ShowPlaylistsActivity.this, playlists));
                    list.setItemAnimator(new DefaultItemAnimator());
                    list.addItemDecoration(new DividerItemDecoration(ShowPlaylistsActivity.this, DividerItemDecoration.HORIZONTAL_LIST));
                    layoutPlaylists.addView(list);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_playlists, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    static class PlaylistListAdapter extends RecyclerView.Adapter<PlaylistListViewHolder> {

        public PlaylistListAdapter(Context context, PlaylistList playlists) {
            this.context = context;
            this.playlistList = playlists;
        }

        @Override
        public PlaylistListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PlaylistListViewHolder(parent, context);
        }

        @Override
        public void onBindViewHolder(PlaylistListViewHolder holder, int position) {
            Playlist playlist = playlistList.get(position);
            holder.setPlaylistName(playlist.getCurrentSong().getTitle());
            holder.setPlaylistPath(absPathToSdcardPath(playlist.getCurrentSongPath()));
            if (position == playlistList.getCurrent()) {
                holder.setCurrent();
            }
        }

        @Override
        public int getItemCount() {
            return playlistList.size();
        }

        public void addPlaylist(Playlist playlist) {
            playlistList.addPlaylist(playlist);
        }

        public void setCurrentPlaylist(int index) {
            playlistList.setCurrentPlaylist(index);
        }

        public void removePlaylist(int index) {
            playlistList.removePlaylist(index);
        }

        private String absPathToSdcardPath(String absPath) {
            String sdcardDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            Pattern pattern = Pattern.compile(sdcardDir.replace(".", "\\.") + "/(.*)$");
            Matcher matcher = pattern.matcher(absPath);
            if (matcher.find()) {
                String newPath = matcher.group(1);
                return newPath;
            }
            else {
                return absPath;
            }
        }

        /* Private members */
        Context context;
        PlaylistList playlistList = new PlaylistList();
    }

    public static class PlaylistListViewHolder extends RecyclerView.ViewHolder {

        public PlaylistListViewHolder(ViewGroup parent, Context context) {
            super(LayoutInflater.from(context).inflate(R.layout.playlist_list_item, null));
            textPlaylistName = (TextView) itemView.findViewById(R.id.textPlaylistListItemName);
            textPlaylistPath = (TextView) itemView.findViewById(R.id.textPlaylistListItemPath);
        }

        public void setCurrent() {
            textPlaylistName.setTextColor(Color.RED);
        }

        public void setPlaylistName(String name) {
            textPlaylistName.setText(name);
        }

        public void setPlaylistPath(String path) {
            textPlaylistPath.setText(path);
        }

        TextView textPlaylistName, textPlaylistPath;
    }

    RelativeLayout layoutPlaylists;
    Button btnAdd;
}
