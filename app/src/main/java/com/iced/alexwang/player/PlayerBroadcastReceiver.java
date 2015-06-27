package com.iced.alexwang.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iced.alexwang.models.callbacks.ParameterizedRunnable;

public class PlayerBroadcastReceiver extends BroadcastReceiver {
    public PlayerBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (callbackHandler != null) {
            callbackHandler.run(new Object[] { context, intent });
        }
    }

    public static void register(ParameterizedRunnable runnable) {
        callbackHandler = runnable;
    }
    public static void clear() {
        callbackHandler = null;
    }

    static ParameterizedRunnable callbackHandler;
}
