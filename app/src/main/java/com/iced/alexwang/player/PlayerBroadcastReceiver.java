package com.iced.alexwang.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iced.alexwang.activities.R;
import com.iced.alexwang.models.callbacks.ParameterizedRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerBroadcastReceiver extends BroadcastReceiver {
    public PlayerBroadcastReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        String opId = intent.getStringExtra(context.getString(R.string.player_service_operation));
        if (opId == null)
            return;
        ArrayList<ParameterizedRunnable> registeredCallbacks = callbackHandlers.get(opId);
        if (registeredCallbacks == null)
            return;
        for (ParameterizedRunnable runnable : registeredCallbacks)
            runnable.run(new Object[] { context, intent });
    }

    public static void register(String opId, ParameterizedRunnable runnable) {
        if (!callbackHandlers.containsKey(opId))
            callbackHandlers.put(opId, new ArrayList<ParameterizedRunnable>());
        callbackHandlers.get(opId).add(runnable);
    }

    public static void clear() {
        callbackHandlers.clear();
    }

    static HashMap<String, ArrayList<ParameterizedRunnable>> callbackHandlers = new HashMap<>();
}
