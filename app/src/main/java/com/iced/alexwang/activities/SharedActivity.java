package com.iced.alexwang.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.iced.alexwang.models.Playlist;
import com.iced.alexwang.views.playlist.PlaylistViewHelper;

public class SharedActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        String operation = intent.getStringExtra(getResources().getString(R.string.intent_data_shared_operation));
        if(operation != null && operation.equals(getResources().getString(R.string.intent_data_show_playlist))) {
            Playlist listItems = PlaylistViewHelper.initPlaylistItems(this);

            View v = PlaylistViewHelper.createPlaylistView(this, listItems);
            setContentView(v);
        }
        else {
            setContentView(R.layout.activity_shared);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shared, menu);
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
