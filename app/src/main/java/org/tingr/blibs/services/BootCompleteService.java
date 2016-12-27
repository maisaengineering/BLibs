package org.tingr.blibs.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.tingr.blibs.utils.Utils;

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
        Log.i(TAG, "onHandleIntent...");
        /**
         * PERIODIC SERVICE TO HANDLE BEACON SCANS IN THE BACKGROUND - HANDLES UNLOCKED STATE
         * This wont handle if app is killed by user
         */
        Utils.schedulePeriodicTask(this);

        // mark complete to release wake lock
        BootCompleteReceiver.completeWakefulIntent(intent);
    }
}
