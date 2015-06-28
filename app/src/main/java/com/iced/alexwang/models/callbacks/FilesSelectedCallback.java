package com.iced.alexwang.models.callbacks;

import com.iced.alexwang.libs.CachedFile;

import java.util.ArrayList;

public interface FilesSelectedCallback {
    // return value indicates whether to simulate a back button click
    boolean filesSelected(ArrayList<CachedFile> files);
}
