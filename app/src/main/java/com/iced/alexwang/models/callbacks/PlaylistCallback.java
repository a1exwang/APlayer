package com.iced.alexwang.models.callbacks;

import com.iced.alexwang.models.Playlist;

/**
 * Created by alexwang on 15-6-19.
 */
public interface PlaylistCallback {
    void onPlaylistReceived(Playlist playlist);
}
