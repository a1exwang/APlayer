package com.iced.alexwang.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.iced.alexwang.libs.CachedFile;
import com.iced.alexwang.libs.CachedFileSystem;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class SelectFileActivity extends Activity {

    static class SelectFile {
        public boolean selected;
        public CachedFile file;

        public SelectFile(CachedFile f, boolean s) {
            file = f;
            selected = s;
        }
    }


    private ArrayList<SelectFile> fileFilter(CachedFile file) {
        Pattern patternSupportedFiles = Pattern.compile(".*\\." + getString(R.string.select_file_supported_files));
        ArrayList<SelectFile> objs = new ArrayList<>();

        if (!file.isDirectory())
            file = file.getParrent();

        for (CachedFile f : file.getChildren()) {
            if (f.isDirectory() || patternSupportedFiles.matcher(f.getName()).matches())
                objs.add(new SelectFile(f, false));
        }
        return objs;
    }

    class SelectFileAdapter extends ArrayAdapter<SelectFile> {

        public SelectFileAdapter() {
            super(SelectFileActivity.this, R.layout.select_file_item, fileFilter(currentDir));
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.select_file_item, parent, false);
                final ViewGroup layout = (ViewGroup) convertView;

                CachedFile cf = getItem(position).file;
                final TextView tvFile = (TextView) layout.findViewById(R.id.textFileName);
                tvFile.setText(cf.getName());
                tvFile.setTextColor(cf.isDirectory() ? Color.BLUE : Color.BLACK);
                tvFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CachedFile file = getItem(position).file;
                        currentDir = file;
                        flush();
                    }
                });

                final CheckBox checkSelect = (CheckBox) layout.findViewById(R.id.checkSelected);
                checkSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getItem(position).selected = checkSelect.isSelected();
                    }
                });
            }
            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file);

        list = (ListView) findViewById(R.id.listSelectFile);

        Intent intent = getIntent();
        String initDir = intent.getStringExtra(getString(R.string.select_file_initial_directory));
        if (initDir == null || new File(initDir).isFile())
            currentDir = CachedFileSystem.getInstance().open(Environment.getExternalStorageDirectory().getAbsolutePath());
        else
            currentDir = CachedFileSystem.getInstance().open(initDir);

        flush();
    }

    public void flush() {
        list.setAdapter(new SelectFileAdapter());
    }

    CachedFile currentDir;
    ListView list;
}
