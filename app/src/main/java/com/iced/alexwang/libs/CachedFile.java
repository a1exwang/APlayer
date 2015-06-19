package com.iced.alexwang.libs;

import com.iced.alexwang.models.callbacks.ParameterizedRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CachedFile {

    private CachedFile(String filePath, boolean isDir) {
        this.absolutePath = filePath;
        this.isDir = isDir;
    }
    public static CachedFile create(String dir, boolean isDir) {
        return new CachedFile(dir, isDir);
    }

    public void _addChild(CachedFile child) {
        if(child != null)
            children.add(child);
    }

    public boolean isDirectory() {
        return isDir;
    }

    public ArrayList<CachedFile> getChildren() {
        return children;
    }

    public CachedFile getParrent() {
        if (!getName().equals("/")) {
            Pattern pattern = Pattern.compile("^(.*)/[^/]*$");
            Matcher matcher = pattern.matcher(absolutePath);
            if (matcher.matches())
                return CachedFileSystem.getInstance().open(matcher.group(1));
        }
        return this;
    }

    public String getName() {
        Pattern pattern = Pattern.compile(".*/([^/]*)$");
        Matcher matcher = pattern.matcher(absolutePath);
        if (matcher.matches()) {
            if(matcher.group(1).equals("/"))
                return "/";
            else
                return matcher.group(1);
        } else {
            return null;
        }
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    ArrayList<CachedFile> children = new ArrayList<>();
    String absolutePath;
    boolean isDir;
}
