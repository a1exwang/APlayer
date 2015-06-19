package com.iced.alexwang.player;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.renderscript.RSInvalidStateException;
import android.widget.Toast;

import com.iced.alexwang.activities.R;
import com.iced.alexwang.models.Playlist;

import java.io.FileInputStream;
import java.io.IOException;

public class MusicPlayerService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public MusicPlayerService() {
    }

    @Override
    public void onCreate() {
        player = new MediaPlayer();
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        player.setOnPreparedListener(this);
        player.setVolume(volume, volume);
    }
    public void tryCreate() {
        if (player == null) {
            onCreate();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String op = intent.getExtras().getString(getString(R.string.player_service_operation));
        if (op == null){
            return START_STICKY;
        }
        else if(op.equals(getString(R.string.player_service_op_start))){
            int index = intent.getIntExtra(getString(R.string.player_service_data_start_index), 0);
            onPlay(index);
        }
        else if(op.equals(getString(R.string.player_service_op_stop)))
            onStop();
        else if(op.equals(getString(R.string.player_service_op_last)))
            onLast();
        else if(op.equals(getString(R.string.player_service_op_next)))
            onNext();
        else if(op.equals(getString(R.string.player_service_op_toggle)))
            onToggle();
        else if(op.equals(getString(R.string.player_service_op_seek_to))) {
            int ms = intent.getIntExtra(getString(R.string.player_service_data_seek_to), 0);
            onSeekTo(ms);
        }
        else if(op.equals(getString(R.string.player_service_op_set_volume))) {
            float vol = intent.getFloatExtra(getString(R.string.player_service_data_volume), 0);
            onSetVolume(vol);
        }
        else if(op.equals(getString(R.string.player_service_op_set_playlist)))
            onSetPlaylist((Playlist)intent.getParcelableExtra(getString(R.string.player_service_data_playlist)));
        else if(op.equals(getString(R.string.player_service_op_toggle_loop)))
            onToggleLoop();
        else if(op.equals(getString(R.string.player_service_op_get_current_pos)))
            onGetCurrentPos();
        else if(op.equals(getString(R.string.player_service_op_get_volume)))
            onGetCurrentVolume();

        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        // Tell the user we stopped.
        player.release();
        player = null;
        Toast.makeText(this, "Player Service Stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mp.isLooping()) {
            mp.start();
        } else {
            onNext();
        }

    }
    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        tryCreate();
        mp.reset();
        return true;
    }

    public void onPlay(int index) {
        if(index >= 0){
            try {
                tryCreate();
                player.reset();
                player.setDataSource(playlist.get(index).getPath());
                player.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
    public void onStop() {
        player.stop();
        player.release();
        player = null;
    }
    public void onToggle() {
        tryCreate();
        if(player.isPlaying()) {
            player.pause();
        } else {
            player.start();
        }
    }
    public void onNext() {
        if (playlist.size() > 0) {
            try {
                tryCreate();
                player.reset();
                player.setDataSource(playlist.nextSong().getPath());
                player.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void onLast() {
        if (playlist.size() > 0){
            try {
                tryCreate();
                player.reset();
                player.setDataSource(playlist.lastSong().getPath());
                player.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void onSeekTo(int ms) {
        if (player != null) {
            player.seekTo(ms);
        }
    }
    public void onSetVolume(float vol) {
        if (player != null) {
            volume = vol;
            player.setVolume(vol, vol);
        }

    }
    public void onToggleLoop() {
        if (player != null) {
            if (player.isLooping()) {
                player.setLooping(false);
            } else {
                player.setLooping(true);
            }
        }
    }
    public void onSetPlaylist(Playlist playlist) {
        this.playlist = playlist;
        tryCreate();
    }

    public void onGetCurrentPos() {
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.player_service_data_current_pos), player.getCurrentPosition());
        callBack(R.string.player_service_op_get_current_pos, bundle);
    }
    public void onGetCurrentVolume() {
        Bundle bundle = new Bundle();
        bundle.putFloat(getString(R.string.player_service_data_volume), volume);
        callBack(R.string.player_service_op_get_current_pos, bundle);
    }

    private void callBack(int opResId, Bundle data) {
        Intent intent = new Intent();
        intent.setAction(getString(R.string.player_service_intent_call_back));
        intent.putExtra(getString(R.string.player_service_operation), getString(opResId));
        intent.putExtras(data);
        getApplicationContext().sendBroadcast(intent);
    }

    float volume = 0.5f;
    MediaPlayer player;
    Playlist playlist;
}
