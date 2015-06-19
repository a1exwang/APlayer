package com.iced.alexwang.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import android.widget.Toast;

import com.iced.alexwang.activities.R;
import com.iced.alexwang.models.callbacks.ParameterizedRunnable;
import com.iced.alexwang.models.Playlist;
import com.iced.alexwang.models.callbacks.PositionCallback;
import com.iced.alexwang.models.callbacks.VolumeCallback;

import java.util.ArrayList;

public class MusicPlayerHelper {

    public static MusicPlayerHelper getInstance(Context c) {
        if (theInstance == null) {
            theInstance = new MusicPlayerHelper(c);
            return theInstance;
        } else {
            theInstance.context = c;
            return theInstance;
        }
    }
    private static MusicPlayerHelper theInstance;

    private MusicPlayerHelper(Context c) {
        context = c;
        setupHandlers();
    }

    public void addPlaylist(Playlist playlist) {
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
    }
    public void volumeDown() {
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
        final float[] vol = new float[1];
        callbackHandler = new ParameterizedRunnable() {
            @Override
            public Object run(Object obj) {
                Intent intent = (Intent) ((Object[])obj)[1];
                vol[0] = intent.getFloatExtra(context.getString(R.string.player_service_data_volume), 0);
                callback.run(vol[0]);
                return null;
            }
        };

        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.putExtra(context.getString(R.string.player_service_operation), context.getString(R.string.player_service_op_get_volume));
        context.startService(intent);
    }
    public void getPosition(final PositionCallback callback) {

        final int[] pos = new int[1];
        callbackHandler = new ParameterizedRunnable() {
            @Override
            public Object run(Object obj) {
                Intent intent = (Intent) ((Object[])obj)[1];
                pos[0] = intent.getIntExtra(context.getString(R.string.player_service_data_current_pos), 0);
                callback.run(pos[0]);
                return null;
            }
        };

        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.putExtra(context.getString(R.string.player_service_operation), context.getString(R.string.player_service_op_get_current_pos));
        context.startService(intent);
    }

    private void setupHandlers() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(context.getString(R.string.player_service_intent_call_back));
        context.registerReceiver(callbackReceiver, intentFilter);
    }
    public void destroyHandlers() {
        context.unregisterReceiver(callbackReceiver);
    }

    private BroadcastReceiver callbackReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (callbackHandler != null) {
                callbackHandler.run(new Object[] { context, intent });
            }
        }
    };

    ParameterizedRunnable callbackHandler;
    Context context;
}
