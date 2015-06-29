package com.iced.alexwang.models;

import com.iced.alexwang.libs.CachedFile;

import java.util.Comparator;
import java.util.Date;

// Data structure for SelectFileAdapter
public class SelectFile {

    public static class SelectFileNameComparator implements Comparator<SelectFile> {
        public SelectFileNameComparator(boolean reverse) {
            this.reverse = reverse;
        }

        @Override
        public int compare(SelectFile lhs, SelectFile rhs) {
            if (lhs.file.getName().equalsIgnoreCase("..")) {
                return -1;
            } else if (rhs.file.getName().equalsIgnoreCase("..")) {
                return 1;
            } else {
                return (reverse ? -1 : 1) * lhs.file.getName().compareToIgnoreCase(rhs.file.getName());
            }
        }

        boolean reverse;
    }

    public static class SelectFileDateComparator implements Comparator<SelectFile> {
        public SelectFileDateComparator(boolean reverse) {this.reverse = reverse; }

        @Override
        public int compare(SelectFile lhs, SelectFile rhs) {
            if (lhs.file.getName().equalsIgnoreCase("..")) {
                return -1;
            } else if (rhs.file.getName().equalsIgnoreCase("..")) {
                return 1;
            } else {
                Date left = lhs.file.getLastModifiedDate();
                Date right = rhs.file.getLastModifiedDate();
                return left.before(right) ? -1 : (left.after(right) ? 1 : 0);
            }
        }

        boolean reverse;
    }


    public boolean checked;
    public CachedFile file;

    public SelectFile(CachedFile f, boolean c) {
        file = f;
        checked = c;
    }
}
