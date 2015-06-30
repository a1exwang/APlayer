/*
* This class represents a text view that can be used as address bar.
*
* */

package com.iced.alexwang.views.select_file;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.iced.alexwang.activities.R;
import com.iced.alexwang.libs.CachedFile;
import com.iced.alexwang.libs.CachedFileSystem;
import com.iced.alexwang.models.callbacks.CurrentDirectoryChangedCallback;

import java.util.ArrayList;

public class PathNavigationView extends TextView {
    public PathNavigationView(Context context, CachedFile current) {
        super(context);
        setOnTouchListener(onTouch);
    }
    public PathNavigationView(Context context, AttributeSet attrSet) {
        super(context, attrSet);
        setOnTouchListener(onTouch);
    }

    public void setCurrentDir(CachedFile current) {
        currentDir = current;
        setText(current.getRelativePathToSdcard());
        if (currentDirChanged != null)
            currentDirChanged.directoryChanged(current);
    }

    public void setCurrentDirectoryChangedCallback(CurrentDirectoryChangedCallback cb) {
        currentDirChanged = cb;
    }

    OnTouchListener onTouch = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            String path = getText().toString();
            float textSize = getTextSize();
            int index = getCharIndexByX(path, getTypeface(), textSize, event.getX());
            // not touching the text
            if (index < 0)
                return true;

            String newPath = getAbsolutePathByCharIndex(path, index);
            setCurrentDir(CachedFileSystem.getInstance().open(newPath));
            return false;
        }
    };

    private String getAbsolutePathByCharIndex(String path, int index) {
        // get all slashes position
        ArrayList<Integer> slashes = new ArrayList<>();
        for (int i = 0; i < path.length(); ++i) {
            if (path.charAt(i) == '/') {
                slashes.add(i);
            }
        }
        slashes.add(path.length());

        // no slashes indicates that we don't need to change directory
        if (slashes.size() == 1)
            return Environment.getExternalStorageDirectory().getAbsolutePath();

        int pos = -1;
        for (int i = 1; i < slashes.size(); ++i) {
            if (slashes.get(i - 1) < index && index < slashes.get(i)){
                pos = i;
                break;
            }
        }

        if (pos == -1) {
            if (index < slashes.get(0))
                pos = 0;
            else // index > slashes.get(n-1)
                pos = slashes.size() - 1;
        }

        String target = path.substring(0, slashes.get(pos));
        return target.replace(getContext().getString(R.string.sdcard_name), Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    private static float[] getXsOfString(String str, Typeface typeface, float textSize) {
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.setTypeface(typeface);

        float[] txtWidths = new float[str.length()];
        paint.getTextWidths(str, txtWidths);

        float[] ret = new float[str.length()];
        ret[0] = 0;
        for (int i = 1; i < str.length(); ++i) {
            ret[i] = ret[i - 1] + txtWidths[i - 1];
        }
        return ret;
    }

    private static int getCharIndexByX(String str, Typeface typeface, float textSize, float x) {
        float[] xs = getXsOfString(str, typeface, textSize);

        for (int i = 1; i < xs.length; ++i) {
            if (xs[i - 1] < x && x <= xs[i])
                return i;
        }
        return -1;
    }

    CurrentDirectoryChangedCallback currentDirChanged;
    CachedFile currentDir;
}
