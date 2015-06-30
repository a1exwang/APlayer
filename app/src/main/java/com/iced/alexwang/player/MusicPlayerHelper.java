package com.iced.alexwang.player;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import com.iced.alexwang.activities.R;
import com.iced.alexwang.models.callbacks.ParameterizedRunnable;
import com.iced.alexwang.models.Playlist;
import com.iced.alexwang.models.callbacks.PlaylistCallback;
import com.iced.alexwang.models.callbacks.PositionCallback;
import com.iced.alexwang.models.callbacks.VolumeCallback;

public class MusicPlayerHelper {

    private MusicPlayerHelper(Context c) {
        context = c;
    }
    private static MusicPlayerHelper theInstance;
    public static MusicPlayerHelper getInstance(Context c) {
        if(theInstance == null) {
            theInstance = new MusicPlayerHelper(c);
            return theInstance;
        } else {
            theInstance.context = c;
            return theInstance;
        }
    }

    public void setPlaylist(Playlist playlist) {
        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.putExtra(context.getString(R.string.player_service_operation), context.getString(R.string.player_service_op_set_playlist));
        intent.putExtra(context.getString(R.string.player_service_data_playlist), (Parcelable)playlist);
        context.startService(intent);
    }
    public void play() {
        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.putExtra(context.getString(R.string.player_service_operation), context.getString(R.string.player_service_op_start));
        intent.putExtra(context.getString(R.string.player_service_data_start_index), 0);
        context.startService(intent);
    }
    public void playAt(int index) {
        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.putExtra(context.getString(R.string.player_service_operation), context.getString(R.string.player_service_op_start));
        intent.putExtra(context.getString(R.string.player_service_data_start_index), index);
        context.startService(intent);
    }
    public void toggle() {
        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.putExtra(context.getString(R.string.player_service_operation), context.getString(R.string.player_service_op_toggle));
        context.startService(intent);
    }
    public void stop() {
        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.putExtra(context.getString(R.string.player_service_operation), context.getString(R.string.player_service_op_stop));
        context.startService(intent);
    }
    public void volumeUp() {
        float delta = context.getResources().getFraction(R.fraction.music_player_volume_delta, 1, 1);
        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.putExtra(context.getString(R.string.player_service_operation), context.getString(R.string.player_service_op_modify_volume));
        intent.putExtra(context.getString(R.string.player_service_data_volume), delta);
        context.startService(intent);
    }
    public void volumeDown() {
        float delta = -context.getResources().getFraction(R.fraction.music_player_volume_delta, 1, 1);
        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.putExtra(context.getString(R.string.player_service_operation), context.getString(R.string.player_service_op_modify_volume));
        intent.putExtra(context.getString(R.string.player_service_data_volume), delta);
        context.startService(intent);
    }
    public void setVolume(float vol) {
        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.putExtra(context.getString(R.string.player_service_operation), context.getString(R.string.player_service_op_set_volume));
        intent.putExtra(context.getString(R.string.player_service_data_volume), vol);
        context.startService(intent);
    }
    public void last() {
        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.putExtra(context.getString(R.string.player_service_operation), context.getString(R.string.player_service_op_last));
        context.startService(intent);
    }
    public void next() {
        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.putExtra(context.getString(R.string.player_service_operation), context.getString(R.string.player_service_op_next));
        context.startService(intent);
    }
    public void toggleLoop() {
        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.putExtra(context.getString(R.string.player_service_operation), context.getString(R.string.player_service_op_toggle_loop));
        context.startService(intent);
    }

    public void getVolume(final VolumeCallback callback) {
        PlayerBroadcastReceiver.register(new ParameterizedRunnable() {
            @Override
            public Object run(Object obj) {
                Intent intent = (Intent) ((Object[])obj)[1];
                float vol = intent.getFloatExtra(context.getString(R.string.player_service_data_volume), 0);
                callback.run(vol);
                return null;
            }
        });

        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.putExtra(context.getString(R.string.player_service_operation), context.getString(R.string.player_service_op_get_volume));
        context.startService(intent);
    }
    public void getPosition(final PositionCallback callback) {

        PlayerBroadcastReceiver.register(new ParameterizedRunnable() {
            @Override
            public Object run(Object obj) {
                Intent intent = (Intent) ((Object[])obj)[1];
                int pos = intent.getIntExtra(context.getString(R.string.player_service_data_current_pos), 0);
                callback.run(pos);
                return null;
            }
        });

        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.putExtra(context.getString(R.string.player_service_operation), context.getString(R.string.player_service_op_get_current_pos));
        context.startService(intent);
    }
    public void getPlaylist(final PlaylistCallback callback) {
        PlayerBroadcastReceiver.register(new ParameterizedRunnable() {
            @Override
            public Object run(Object obj) {
                Intent intent = (Intent) ((Object[]) obj)[1];
                Playlist playlist = intent.getParcelableExtra(context.getString(R.string.player_service_data_playlist));
                callback.onPlaylistReceived(playlist);
                return null;
            }
        });

        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.putExtra(context.getString(R.string.player_service_operation), context.getString(R.string.player_service_op_get_playlist));
        context.startService(intent);
    }

    private Context context;
}
