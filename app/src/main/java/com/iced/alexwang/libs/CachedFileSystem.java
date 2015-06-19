package com.iced.alexwang.libs;

import com.iced.alexwang.models.callbacks.ParameterizedRunnable;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class CachedFileSystem {
    private CachedFileSystem() {
    }
    private static CachedFileSystem fs;
    public static CachedFileSystem getInstance() {
        if(fs == null) {
            fs = new CachedFileSystem();
        }
        return fs;
    }
    public CachedFile open(String dir) {
        for(CachedFile file : cachedFiles) {
            if(file.getAbsolutePath().equalsIgnoreCase(dir)) {
                return file;
            }
        }

        // create tree
        File f = new File(dir);
        if(!f.exists())
            return null;
        if(f.isDirectory()) {
            return createTree(f);
        } else {
            return CachedFile.create(f.getAbsolutePath(), false);
        }
    }

    static CachedFile createTree(File dir) {

        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return !(pathname.toString().equals(".") || pathname.toString().equals(".."));
            }
        };

        CachedFile ret = CachedFile.create(dir.getAbsolutePath(), dir.isDirectory());

        if(dir.isDirectory()) {
            for(File file : dir.listFiles(filter)) {
                CachedFile cf = createTree(file);
                ret._addChild(cf);
            }
        }

        return ret;
    }

    public static void bfsSubDirs(final File file, ParameterizedRunnable op) {
        if (!file.exists())
            return;

        LinkedList<File> queue = new LinkedList<>();
        queue.add(file);

        while (!queue.isEmpty()) {
            File f = queue.remove();
            op.run(f);
            if (f.isDirectory()) {
                queue.addAll(Arrays.asList(f.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                    return !(pathname.getName().equals(".") || pathname.getName().equals(".."));
                }})));
            }
        }
    }

    ArrayList<CachedFile> cachedFiles = new ArrayList<>();
}
