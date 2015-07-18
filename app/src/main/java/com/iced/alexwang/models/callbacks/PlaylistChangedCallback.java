package com.iced.alexwang.models.callbacks;

import com.iced.alexwang.models.Playlist;

public interface PlaylistChangedCallback {
    void changed(Playlist newPlaylist);
}
