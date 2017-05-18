package com.APportfolio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by averypozzobon on 2017-05-16.
 */
public final class NotificationServiceStarterReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        MyReceiver.setupAlarm(context);
        Log.e("Service Status", "Started");
    }
}