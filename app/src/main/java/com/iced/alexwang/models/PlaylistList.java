package com.iced.alexwang.models;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class PlaylistList extends ArrayList<Playlist> {

    /* for marshal and load */
    public byte[] marshal() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeInt(current);
            oos.writeInt(size());

            for (int i = 0; i < size(); ++i) {
                Playlist playlist = get(i);
                byte[] plBytes = playlist.marshal();
                oos.writeInt(plBytes.length);
                oos.write(plBytes);
            }
            oos.close();
            byte[] ret = bos.toByteArray();
            bos.close();
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static PlaylistList load(byte[] buf, int offset, int _size) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(buf, offset, _size);
            ObjectInputStream ois = new ObjectInputStream(bis);

            int current = ois.readInt();

            int size = ois.readInt();

            PlaylistList list = new PlaylistList();
            for (int i = 0; i < size; ++i) {
                int plBytesSize = ois.readInt();
                byte[] plBytes = new byte[plBytesSize];
                ois.readFully(plBytes, 0, plBytesSize);
                list.add(Playlist.load(plBytes, 0, plBytesSize));
            }
            list.current = current;

            return list;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public PlaylistList() {
        current = -1;
    }

    public void addPlaylist(Playlist playlist) {
        if (playlist != null && playlist.size() > 0)
            add(playlist);
    }
    public void setCurrentPlaylist(int index) {
        if (index >= 0 && index < size())
            current = index;
    }
    public void removePlaylist(int index) {
        if (index >= 0 && index < size()) {
            if (index == current) {
                if (size() == 1)
                    current = -1;
                else if (current != 0)
                    current--;
            } else if (index < current) {
                current--;
            }
            remove(index);
        }
    }
    public Playlist getCurrentPlaylist() {
        if (current >= 0 && current < size())
            return get(current);
        return null;
    }
    public int getCurrent() {
        return current;
    }

    int current;
}
