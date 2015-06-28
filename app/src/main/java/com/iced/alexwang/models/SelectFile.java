package com.iced.alexwang.models;

import com.iced.alexwang.libs.CachedFile;

// Data structure for SelectFileAdapter
public class SelectFile {
    public boolean checked;
    public CachedFile file;

    public SelectFile(CachedFile f, boolean c) {
        file = f;
        checked = c;
    }
}
