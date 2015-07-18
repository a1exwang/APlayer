package com.iced.alexwang.player;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import com.iced.alexwang.activities.R;
import com.iced.alexwang.models.Playlist;
import com.iced.alexwang.models.Song;
import com.iced.alexwang.models.callbacks.CurrentStatusCallback;
import com.iced.alexwang.models.callbacks.ParameterizedRunnable;
import com.iced.alexwang.models.callbacks.PlaylistCallback;
import com.iced.alexwang.models.callbacks.PositionCallback;
import com.iced.alexwang.models.callbacks.SongChangedCallback;
import com.iced.alexwang.models.callbacks.VolumeCallback;

import java.util.ArrayList;

public class MusicPlayerHelper {

    private MusicPlayerHelper(Context c) {
        context = c;

        // get playlist
        PlayerBroadcastReceiver.register(context.getString(R.string.player_service_op_get_playlist), new ParameterizedRunnable() {
            @Override
            public Object run(Object obj) {
                Intent intent = (Intent) ((Object[]) obj)[1];
                Playlist playlist = intent.getParcelableExtra(context.getString(R.string.player_service_data_playlist));
                if (playlistCallback != null) {
                    playlistCallback.onPlaylistReceived(playlist);
                    playlistCallback = null;
                }
                return null;
            }
        });

        // get position
        PlayerBroadcastReceiver.register(context.getString(R.string.player_service_op_get_current_pos), new ParameterizedRunnable() {
            @Override
            public Object run(Object obj) {
                Intent intent = (Intent) ((Object[])obj)[1];
                int pos = intent.getIntExtra(context.getString(R.string.player_service_data_current_pos), 0);
                int total = intent.getIntExtra(context.getString(R.string.player_service_data_total_time), 1);
                if (positionCallback != null) {
                    positionCallback.run(pos, total);
                    positionCallback = null;
                }
                return null;
            }
        });

        // get volume
        PlayerBroadcastReceiver.register(context.getString(R.string.player_service_op_get_volume), new ParameterizedRunnable() {
            @Override
            public Object run(Object obj) {
                Intent intent = (Intent) ((Object[])obj)[1];
                float vol = intent.getFloatExtra(context.getString(R.string.player_service_data_volume), 0);
                if (volumeCallback != null) {
                    volumeCallback.run(vol);
                    volumeCallback = null;
                }
                return null;
            }
        });

        // play next song
        PlayerBroadcastReceiver.register(context.getString(R.string.player_service_op_next), new ParameterizedRunnable() {
            @Override
            public Object run(Object obj) {
                Intent intent = (Intent) ((Object[])obj)[1];
                Song song = intent.getParcelableExtra(context.getString(R.string.player_service_data_song));
                for (SongChangedCallback cb : nextSongCallbacks) {
                    cb.songChanged(song);
                }
                return null;
            }
        });
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
        volumeCallback = callback;
        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.putExtra(context.getString(R.string.player_service_operation), context.getString(R.string.player_service_op_get_volume));
        context.startService(intent);
    }
    public void getPosition(final PositionCallback callback) {
        positionCallback = callback;
        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.putExtra(context.getString(R.string.player_service_operation), context.getString(R.string.player_service_op_get_current_pos));
        context.startService(intent);
    }
    public void getPlaylist(final PlaylistCallback callback) {
        playlistCallback = callback;
        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.putExtra(context.getString(R.string.player_service_operation), context.getString(R.string.player_service_op_get_playlist));
        context.startService(intent);
    }
    public void getCurrentStatus(CurrentStatusCallback callback) {
        currentStatusCallback = callback;
        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.putExtra(context.getString(R.string.player_service_operation), context.getString(R.string.player_service_op_get_player_status));
        context.startService(intent);
    }
    public void addNextSongCallback(SongChangedCallback cb) {
        if (!nextSongCallbacks.contains(cb))
            nextSongCallbacks.add(cb);
    }
    public void removeNextSongCallback(SongChangedCallback cb) {
        if (nextSongCallbacks.contains(cb)) {
            nextSongCallbacks.remove(cb);
        }
    }

    PlaylistCallback playlistCallback;
    PositionCallback positionCallback;
    VolumeCallback volumeCallback;
    CurrentStatusCallback currentStatusCallback;
    ArrayList<SongChangedCallback> nextSongCallbacks = new ArrayList<>();

    private Context context;
}
