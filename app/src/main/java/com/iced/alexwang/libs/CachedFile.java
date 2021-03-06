package com.iced.alexwang.libs;

import android.os.Environment;

import com.iced.alexwang.models.callbacks.Marshalable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
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
        if(getAbsolutePath().equals("/"))
            return "/";

        Pattern pattern = Pattern.compile(".*/([^/]*)$");
        Matcher matcher = pattern.matcher(absolutePath);
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    public String getNameWithoutSuffix() {
        if(getAbsolutePath().equals("/"))
            return "/";
        Pattern pattern = Pattern.compile(".*/([^/]*)\\.[^.]*$");
        Matcher matcher = pattern.matcher(absolutePath);
        if (matcher.find())
            return matcher.group(1);
        else {
            Pattern patternUndotted = Pattern.compile(".*/([^/]*)$");
            matcher = patternUndotted.matcher(absolutePath);
            if (matcher.find())
                return matcher.group(1);
            else
                return null;
        }
    }

    public String getSuffix() {
        Matcher matcher = Pattern.compile("^.*\\.([^.]+)$").matcher(getName());
        if (matcher.matches()) {
            return matcher.group(1);
        } else
            return "";
    }

    public Date getLastModifiedDate() {
        File file = new File(getAbsolutePath());
        Date date =  new Date(file.lastModified());
        return date;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }
    public String getRelativePathToSdcard() {
        if (absolutePath.equalsIgnoreCase(Environment.getExternalStorageDirectory().toString()))
            return "SDCard";

        Pattern patternUpperFolderPath = Pattern.compile(Environment.getExternalStorageDirectory().toString().replace(".", "\\.") + "/(.+)$");
        Matcher matcher = patternUpperFolderPath.matcher(absolutePath);
        if (matcher.find()) {
            return "SDCard/" + matcher.group(1);
        } else {
            return "Unknown Path!";
        }
    }


    ArrayList<CachedFile> children = new ArrayList<>();
    String absolutePath;
    boolean isDir;
}
