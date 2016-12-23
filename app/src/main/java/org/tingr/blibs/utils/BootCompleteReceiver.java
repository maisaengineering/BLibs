package org.tingr.blibs.utils;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import org.tingr.blibs.services.PeriodicBSubService;

/**
 * Created by imaginationcoder on 12/22/16.
 */
public class BootCompleteReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = BootCompleteReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, BootCompleteService.class);
        startWakefulService(context, service);
    }
}
