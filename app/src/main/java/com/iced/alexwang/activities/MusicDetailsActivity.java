package com.iced.alexwang.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.iced.alexwang.activities.R;
import com.iced.alexwang.libs.MusicMetadataHelper;

public class MusicDetailsActivity extends Activity {

    TextView textTitle, textArtist, textBitrate, textAlbum, textPlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_details);
        String path = getIntent().getExtras().getString(getString(R.string.detailed_data_music_path));

        textTitle = (TextView) findViewById(R.id.textMusicDetailsTitle);
        textAlbum = (TextView) findViewById(R.id.textMusicDetailsAlbum);
        textArtist = (TextView) findViewById(R.id.textMusicDetailsArtist);
        textBitrate = (TextView) findViewById(R.id.textMusicDetailsBitrate);
        textPlaylist = (TextView) findViewById(R.id.textMusicDetaisPlaylist);

        final MusicMetadataHelper metaHelper = MusicMetadataHelper.create(path);
        if(metaHelper != null) {
            textTitle.setText(metaHelper.getTitle());
            textArtist.setText(metaHelper.getArtist());
            textBitrate.setText(String.valueOf((int)(metaHelper.getBitrate() / 1000.0)) + getString(R.string.kilo_bit_rate_unit));
            textAlbum.setText(metaHelper.getAlbum());

            textPlaylist.setText(metaHelper.getPlaylist());
            textPlaylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String path = metaHelper.getPlaylistPath();
                    if (path.length() != 0) {
                        Intent intent = new Intent(MusicDetailsActivity.this, SelectFileActivity.class);
                        intent.putExtra(getString(R.string.select_file_initial_directory), path);
                        startActivity(intent);
                    }
                }
            });


        } else {
            Toast.makeText(this, getString(R.string.file_not_found_error), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_music_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
