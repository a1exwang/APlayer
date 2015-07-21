package com.iced.alexwang.views.main;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iced.alexwang.activities.R;
import com.iced.alexwang.libs.ActivityHelper;

public class LeftDrawerAdapter extends RecyclerView.Adapter<LeftDrawerAdapter.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
            main = (RelativeLayout) itemView;
            tv = (TextView) itemView.findViewById(R.id.textLeftDrawerItem);
            image = (ImageView) itemView.findViewById(R.id.imageViewLeftDrawerItem);
        }

        public TextView tv;
        public ImageView image;
        public RelativeLayout main;
    }

    public LeftDrawerAdapter(Context context, int current) {
        this.context = context;
        this.current = current;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_left_drawer, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == current)
            holder.main.setBackgroundColor(Color.rgb(182, 255, 0));
        switch (position) {
            case 0:
                holder.tv.setText("Player Control");
                holder.tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityHelper.startPlayerControlActivity(context);
                    }
                });
                holder.image.setImageResource(R.drawable.ic_headset_black_18dp);
                break;
            case 1:
                holder.tv.setText("Current Playlist");
                holder.tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityHelper.startCurrentPlaylistActivity(context);
                    }
                });
                holder.image.setImageResource(R.drawable.ic_queue_music_black_18dp);
                break;
            case 2:
                holder.tv.setText("All Playlists");
                holder.tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityHelper.startShowPlaylistsActivity(context);
                    }
                });
                holder.image.setImageResource(R.drawable.ic_library_books_black_18dp);
                break;
            case 3:
                holder.tv.setText("Select Files");
                holder.tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityHelper.startSelectFileActivity(context, Environment.getExternalStorageDirectory().toString());
                    }
                });
                holder.image.setImageResource(R.drawable.ic_playlist_add_black_18dp);
                break;
            case 4:
                holder.tv.setText("Search");
                holder.image.setImageResource(R.drawable.ic_search_18pt_3x);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    Context context;
    int current;
}
