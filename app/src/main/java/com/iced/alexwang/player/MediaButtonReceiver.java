package com.iced.alexwang.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MediaButtonReceiver extends BroadcastReceiver {
    public MediaButtonReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // an Intent broadcast.
        Toast.makeText(context, intent.toString(), Toast.LENGTH_SHORT).show();
    }
}
