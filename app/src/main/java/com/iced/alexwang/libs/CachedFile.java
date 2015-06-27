package com.iced.alexwang.libs;

import android.os.Parcel;
import android.os.Parcelable;

import com.iced.alexwang.models.Song;
import com.iced.alexwang.models.callbacks.Marshalable;
import com.iced.alexwang.models.callbacks.ParameterizedRunnable;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CachedFile implements Marshalable {

    private CachedFile(String filePath, boolean isDir) {
        this.absolutePath = filePath;
        this.isDir = isDir;
    }

    public static CachedFile load(byte[] data, int off, int size) {
        CachedFile ret = new CachedFile(null, false);
        ByteArrayInputStream byteStream = new ByteArrayInputStream(data, off, size);

        try {
            ObjectInputStream stream = new ObjectInputStream(byteStream);

            ret.isDir = stream.readInt() == 1;

            int strSize = stream.readInt();
            byte[] strBytes = new byte[strSize];
            stream.readFully(strBytes);
            ret.absolutePath = new String(strBytes, "UTF-8");

            int childrenSize = stream.readInt();
            ret.children = new ArrayList<>();
            for (int i = 0; i < childrenSize; ++i) {
                int childSize = stream.readInt();
                byte[] childBytes = new byte[childSize];
                stream.readFully(childBytes);
                ret.children.add(load(childBytes, 0, childSize));
            }

            return ret;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] marshal() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream stream = new ObjectOutputStream(byteStream);

            stream.writeInt(isDir ? 1 : 0);

            byte[] strBytes = absolutePath.getBytes(Charset.forName("UTF-8"));
            stream.writeInt(strBytes.length);
            stream.write(strBytes);

            stream.writeInt(children.size());
            for (CachedFile c : children) {
                byte[] thisBytes = c.marshal();
                stream.writeInt(thisBytes.length);
                stream.write(thisBytes);
            }

            stream.close();
            return byteStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static CachedFile create(String dir, boolean isDir) {
        CachedFile ret = new CachedFile(dir, isDir);
        return ret;
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
