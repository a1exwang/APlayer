package com.iced.alexwang.libs;

import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.widget.Toast;

import com.iced.alexwang.models.callbacks.Marshalable;
import com.iced.alexwang.models.callbacks.ParameterizedRunnable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class CachedFileSystem implements Marshalable {
    private CachedFileSystem() {
    }
    private static CachedFileSystem fs;


    private static CachedFileSystem load(byte[] data, int off, int size) {
        CachedFileSystem ret = new CachedFileSystem();
        ByteArrayInputStream byteStream = new ByteArrayInputStream(data, off, size);

        try {
            ObjectInputStream stream = new ObjectInputStream(byteStream);

            int childrenSize = stream.readInt();
            ret.cachedFiles = new ArrayList<>();
            for (int i = 0; i < childrenSize; ++i) {
                int childSize = stream.readInt();
                byte[] childBytes = new byte[childSize];
                stream.readFully(childBytes);
                ret.cachedFiles.add(CachedFile.load(childBytes, 0, childSize));
            }

            return ret;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public byte[] marshal() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream stream = new ObjectOutputStream(byteStream);

            stream.writeInt(cachedFiles.size());
            for (CachedFile c : cachedFiles) {
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

    private static byte[] fileToBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[is.available()];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    private void saveToFile(String file) throws IOException {
        File f = new File(file);
        if (f.exists()) {
            f.delete();
        }
        boolean newFile = f.createNewFile();
        if (newFile) {
            FileOutputStream fs = new FileOutputStream(file);
            byte[] b = marshal();
            fs.write(b);
            fs.close();
        }
    }
    private static CachedFileSystem initFromFile(String file) throws IOException, ClassNotFoundException {
        byte[] bytes = fileToBytes(new FileInputStream(file));
        CachedFileSystem ret = load(bytes, 0, bytes.length);
        return ret;
    }

    public static CachedFileSystem getInstance() {
        if(fs == null) {
            try {
                fs = initFromFile(Environment.getExternalStorageDirectory() + "/cache_bin");
                if (fs == null) {
                    fs = new CachedFileSystem();
                }
            } catch (IOException e) {
                fs = new CachedFileSystem();
            } catch (ClassNotFoundException e) {
                fs = new CachedFileSystem();
            }
        }

        return fs;
    }
    public void save() {
        try {
            saveToFile(Environment.getExternalStorageDirectory() + "/cache_bin");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            CachedFile cf = createTree(f);
            cachedFiles.add(cf);
            return cf;
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

    public static void dfsCachedSubDirs(final CachedFile file, ParameterizedRunnable op) {

        Stack<CachedFile> stack = new Stack<>();
        stack.push(file);

        while (!stack.isEmpty()) {
            CachedFile f = stack.pop();
            op.run(f);
            if (f.isDirectory()) {
                for (CachedFile child : f.children)
                    stack.push(child);
            }
        }
    }

    public static void bfsCachedSubDirs(final CachedFile file, ParameterizedRunnable op) {

        LinkedList<CachedFile> queue = new LinkedList<>();
        queue.add(file);

        while (!queue.isEmpty()) {
            CachedFile f = queue.remove();
            op.run(f);
            if (f.isDirectory()) {
                queue.addAll(f.children);
            }
        }
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
