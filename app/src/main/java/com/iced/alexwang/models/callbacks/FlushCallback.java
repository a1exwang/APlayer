package com.iced.alexwang.models.callbacks;

import com.iced.alexwang.libs.CachedFile;

/**
 * Created by alexwang on 15-6-28.
 */
public interface FlushCallback {
    void flush(CachedFile current);
}
