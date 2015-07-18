package com.iced.alexwang.models;

import java.util.ArrayList;

public class LoopList<T extends Object> {

    public LoopList(int size) {
        this.size = size;
        data = new Object[size];
        current = 0;
    }

    public int size(){
        return size;
    }

    public void push(T val) {
        data[current] = val;
        current++;
        current %= size;
    }

    public ArrayList<T> toArrayList() {
        ArrayList<T> ret = new ArrayList<>();
        for (int i = 0, pos = current; i < size; ++i) {
            ret.add((T) data[pos]);
            pos++;
            pos %= size;
        }
        return ret;
    }

    int size;
    int current;
    Object[] data;
}
