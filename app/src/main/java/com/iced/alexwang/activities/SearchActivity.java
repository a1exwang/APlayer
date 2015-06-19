package com.iced.alexwang.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.iced.alexwang.activities.R;

import org.w3c.dom.Text;

import java.lang.reflect.AccessibleObject;

public class SearchActivity extends Activity {

    TextView tvItem, tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        tvItem = (TextView) findViewById(R.id.textSearchItem);
        tvContent = (TextView) findViewById(R.id.textSearchContent);

        Intent intent = getIntent();
        String item = intent.getExtras().getString(getString(R.string.search_data_item));
        String content = intent.getExtras().getString(getString(R.string.search_data_content));
        tvItem.setText(item);
        tvContent.setText(content);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
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
