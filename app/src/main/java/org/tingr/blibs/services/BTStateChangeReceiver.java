package org.tingr.blibs.services;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class BTStateChangeReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = BTStateChangeReceiver.class.getName();

    public BTStateChangeReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
            int currState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            if (BluetoothAdapter.STATE_OFF == currState || BluetoothAdapter.STATE_ON == currState) {
                Intent service = new Intent(context, BootCompleteService.class);
                startWakefulService(context, service);
            }
        }
    }
}
