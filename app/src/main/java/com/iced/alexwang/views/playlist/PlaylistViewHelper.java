package com.iced.alexwang.views.playlist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.iced.alexwang.activities.R;
import com.iced.alexwang.activities.SharedActivity;
import com.iced.alexwang.models.Artist;
import com.iced.alexwang.models.Playlist;
import com.iced.alexwang.models.Song;

public class PlaylistViewHelper {
    public PlaylistViewHelper(ViewGroup vg) {
        viewGroup = vg;
    }

    // 创建一个自定义的Playlist
    public static Playlist initPlaylistItems(final Context context){
        Playlist listItems = new Playlist();

        listItems.add(new Song("You Belong With Me", new Artist("Taylor Swift"), "/storage/sdcard/1.mp3"));
        listItems.add(new Song("21", new Artist("Green"), "/storage/sdcard/2.mp3"));

        return listItems;
    }

    // 创建Playlist View, 使其处于内存中但是没有显示的状态
    public static RelativeLayout createPlaylistView(final Context context, Playlist listItems) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout dlgView = (RelativeLayout) inflater.inflate(R.layout.layout_playlist, null);
        final ListView list = (ListView) dlgView.findViewById(R.id.listPlaylistItems);
        final PlaylistAdapter adapter = new PlaylistAdapter(context, listItems);
        list.setAdapter(adapter);

        final Button btnSort = (Button) dlgView.findViewById(R.id.btnPlaylistSort);

        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                String[] items = { "Title", "Title R", "Artist", "Artist R", "Date", "Date R" };

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                adapter.sort(new Song.TitleComparator(false));
                                break;
                            case 1:
                                adapter.sort(new Song.TitleComparator(true));
                                break;
                            case 2:
                                adapter.sort(new Song.ArtistComparator(false));
                                break;
                            case 3:
                                adapter.sort(new Song.ArtistComparator(true));
                                break;
                            default:
                        }
                        adapter.notifyDataSetChanged();
                    }
                }).show();
            }
        });

        return dlgView;
    }

    // 显示包含Playlist的对话框
    public static void showPlaylistDialog(Context context, Playlist listItems) {
        View playlistView = createPlaylistView(context, listItems);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(playlistView);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // 新建一个包含Playlist的Activity并显示
    public static void startPlaylistActivity(Context context, Playlist listItems) {
        Intent intent = new Intent(context, SharedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(context.getResources().getString(R.string.intent_data_shared_operation), context.getResources().getString(R.string.intent_data_show_playlist));
        intent.putParcelableArrayListExtra(context.getResources().getString(R.string.intent_data_playlist_data),
                listItems);
        context.startActivity(intent);
    }

    public static void showPlaylistToast(Context context, Playlist listItems) {
        Toast toast = new Toast(context);
        toast.setView(createPlaylistView(context, listItems));
        toast.show();
    }

    // 在这个ViewGroup中添加Playlist View
    public void addPlaylistView(Playlist listItems){
        View playlistView = createPlaylistView(viewGroup.getContext(), listItems);
        viewGroup.addView(playlistView);
    }

    private ViewGroup viewGroup;
}
