package com.iced.alexwang.views.control;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.iced.alexwang.activities.R;
import com.iced.alexwang.models.Playlist;
import com.iced.alexwang.models.Song;
import com.iced.alexwang.models.callbacks.PlaylistCallback;
import com.iced.alexwang.models.callbacks.PositionCallback;
import com.iced.alexwang.player.MusicPlayerHelper;
import com.iced.alexwang.views.lyrics.LyricsView;

import java.util.Timer;
import java.util.TimerTask;

public class PlayerControlView extends RelativeLayout {
    public PlayerControlView(Context context) {
        super(context);
    }
    public PlayerControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init() {
        playerHelper = MusicPlayerHelper.getInstance(getContext());
        layout = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.layout_player_control, this);

        lyricsView = (LyricsView) layout.findViewById(R.id.lyricsViewPlayerControl);
        btnLast = (ImageButton) layout.findViewById(R.id.imageBtnLast);
        btnNext = (ImageButton) layout.findViewById(R.id.imageBtnNext);
        btnPlay = (ImageButton) layout.findViewById(R.id.imageBtnPlay);
        btnStop = (ImageButton) layout.findViewById(R.id.imageBtnStop);
        seekProgress = (SeekBar) layout.findViewById(R.id.seekBarProgress);
        seekVolume = (SeekBar) layout.findViewById(R.id.seekBarVolume);
        textCurrentTime = (TextView) layout.findViewById(R.id.textCurrentTime);
        textTitle = (TextView) layout.findViewById(R.id.textPlayerControlTitle);
        textArtist = (TextView) layout.findViewById(R.id.textPlayerControlArtist);

        lyricsView.init();

        seekProgress.setMax(1000);
        seekProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {

                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        seekVolume.setMax(100);
        seekVolume.setProgress(80);
        seekVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    volume = progress / 100.0f;
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {
                playerHelper.setVolume(volume);
            }
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
            float volume = 0.8f;
        });

        btnLast.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playerHelper.last();
            }
        });
        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playerHelper.next();
            }
        });
        btnPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playerHelper.play();
            }
        });
        btnStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playerHelper.stop();
            }
        });

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                playerHelper.getPosition(new PositionCallback() {
                    @Override
                    public void run(int pos, int total) {
                        int min = pos / (60 * 1000);
                        int sec = (pos - (min * 60 * 1000)) / 1000;
                        int ms = pos - (min * 60 * 1000) - (sec * 1000);
                        String str = String.format("%02d:%02d.%03d", min, sec, ms);
                        textCurrentTime.setText(str);

                        int permicro = (int) (1000.0f * pos / total);
                        seekProgress.setProgress(permicro);
                    }
                });

                playerHelper.getPlaylist(new PlaylistCallback() {
                    @Override
                    public void onPlaylistReceived(Playlist playlist) {
                        if (playlist != null && playlist.size() > 0) {
                            Song song = playlist.getCurrentSong();
                            if (song != null) {
                                textArtist.setText(song.getArtist().getName());
                                textTitle.setText(song.getTitle());
                            }
                        }

                    }
                });

            }
        }, 0, 1000);

    }

    public void release() {
        if (timer != null)
            timer.cancel();
        lyricsView.release();
    }

    // view controls

    RelativeLayout layout;
    LyricsView lyricsView;
    ImageButton btnLast, btnNext, btnPlay, btnStop;
    SeekBar seekProgress, seekVolume;
    TextView textCurrentTime, textTitle, textArtist;

    MusicPlayerHelper playerHelper;
    Timer timer;
}
