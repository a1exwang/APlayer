package com.iced.alexwang.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import com.iced.alexwang.libs.CachedFile;
import com.iced.alexwang.libs.CachedFileSystem;
import com.iced.alexwang.views.lyrics.LyricsView;

public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        btnTest = (Button) findViewById(R.id.btnTest);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CachedFile file = CachedFileSystem.getInstance().open(Environment.getExternalStorageDirectory() + "/t.lrc");
                LyricsView lv = new LyricsView(TestActivity.this);
                lv.changeLyrics(file);
            }
        });
    }

    Button btnTest;
}
