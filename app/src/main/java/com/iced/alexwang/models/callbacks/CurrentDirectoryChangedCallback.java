package com.iced.alexwang.models.callbacks;

import com.iced.alexwang.libs.CachedFile;

/**
 * Created by alexwang on 15-6-29.
 */
public interface CurrentDirectoryChangedCallback {
    void directoryChanged(CachedFile current);
}
