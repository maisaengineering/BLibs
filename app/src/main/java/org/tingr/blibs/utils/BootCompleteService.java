package org.tingr.blibs.utils;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.tingr.blibs.Utils;
import org.tingr.blibs.services.PeriodicBSubService;

/**
 * Created by imaginationcoder on 12/22/16.
 */
public class BootCompleteService extends IntentService {
    private static final String TAG = BootCompleteService.class.getName();

    // default constructor
    public BootCompleteService() {
        super(BootCompleteService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        /**
         * PERIODIC SERVICE TO HANDLE BEACON SCANS IN THE BACKGROUND - HANDLES UNLOCKED STATE
         * This wont handle if app is killed by user
         */
        Utils.schedulePeriodicTask(this);

        // mark complete to release wake lock
        BootCompleteReceiver.completeWakefulIntent(intent);
    }
}
