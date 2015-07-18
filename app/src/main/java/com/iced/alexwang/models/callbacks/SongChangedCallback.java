package com.iced.alexwang.models.callbacks;

import com.iced.alexwang.models.Song;

/**
 * Created by alexwang on 15-7-4.
 */
public interface SongChangedCallback {
    void songChanged(Song newSong);
}
