/*
* A view to show the lyrics that the player is currently playing.
* NOTE!! Remember to init the view before show it and release the timer when the view will be destroyed.
* */

package com.iced.alexwang.views.lyrics;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.iced.alexwang.libs.CachedFile;
import com.iced.alexwang.libs.CachedFileSystem;
import com.iced.alexwang.models.Lyrics;
import com.iced.alexwang.models.Playlist;
import com.iced.alexwang.models.Song;
import com.iced.alexwang.models.callbacks.PlaylistCallback;
import com.iced.alexwang.models.callbacks.PositionCallback;
import com.iced.alexwang.models.callbacks.SongChangedCallback;
import com.iced.alexwang.player.MusicPlayerHelper;

import java.util.Timer;
import java.util.TimerTask;

public class LyricsView extends TextView {
    public LyricsView(Context context) {
        super(context);
    }
    public LyricsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init() {
        playerHelper = MusicPlayerHelper.getInstance(getContext());
        playerHelper.getPlaylist(new PlaylistCallback() {
            @Override
            public void onPlaylistReceived(Playlist playlist) {
                if (playlist == null || playlist.size() == 0)
                    return;
                String lrcPath = playlist.getCurrentSong().getLrcPath();
                if (lrcPath == null)
                    return;
                CachedFile lycFile = CachedFileSystem.getInstance().open(lrcPath);
                changeLyrics(lycFile);
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        playerHelper.getPosition(new PositionCallback() {
                            @Override
                            public void run(int pos, int total) {
                                String line = lyrics.getCurrentLyrics(pos);
                                setText(line);
                            }
                        });
                    }
                }, 0, (1000 / timesPerSeconds));
            }
        });

        playerHelper.addNextSongCallback(songChanged);
    }

    public void release() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            playerHelper.removeNextSongCallback(songChanged);
        }
    }

    public void changeLyrics(CachedFile file) {
        lyrics = Lyrics.createFromFile(file);
        if (lyrics != null)
            setText(lyrics.getCurrentLyrics(0));
    }

    SongChangedCallback songChanged = new SongChangedCallback() {
        @Override
        public void songChanged(Song newSong) {
            CachedFile lrcFile = CachedFileSystem.getInstance().open(newSong.getLrcPath());
            if (lrcFile != null)
                changeLyrics(lrcFile);
        }
    };

    MusicPlayerHelper playerHelper;
    int timesPerSeconds = 5;
    Timer timer;
    Lyrics lyrics;
}
