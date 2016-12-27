package org.tingr.blibs.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by imaginationcoder on 12/22/16.
 */
public class BootCompleteReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = BootCompleteReceiver.class.getName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive...");

        Intent service = new Intent(context, BootCompleteService.class);
        startWakefulService(context, service);
    }
}
