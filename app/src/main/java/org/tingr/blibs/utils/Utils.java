package org.tingr.blibs.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.tingr.blibs.services.BlibsServc;

/**
 * Created by imaginationcoder on 12/14/16.
 */
public final class Utils {
    private static final String TAG = Utils.class.getName();

    public static final String BROADCAST_KEY_DETECTED = "org.tingr.blibs.DETECTED";
    public static enum BEACON {
        STATE,
        FOUND,
        LOST,
        DATA,
    };

    public static void schedulePeriodicTask(Context aContext) {
        try {
            Log.i(TAG, "schedulePeriodicTask..." + aContext);

            /**
             * PERIODIC SERVICE TO HANDLE BEACON SCANS IN THE BACKGROUND - HANDLES UNLOCKED STATE
             * This wont handle if app is killed by user
             */
            //Intent periodicIntent = new Intent(aContext, BlibsServc.class);
//            Intent intent = new Intent("org.tingr.blibs.service.daemon");
//            Intent intent = new Intent(aContext, BlibsServc.class);
//            PendingIntent pIntent = PendingIntent.getService(
//                    aContext,
//                    101, intent,
//                    PendingIntent.FLAG_CANCEL_CURRENT);

            Intent intent = new Intent(BlibsServc.SERVC_NAME);
            PendingIntent pIntent = PendingIntent.getService(
                    aContext,
                    101, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager am = (AlarmManager) aContext.getSystemService(Context.ALARM_SERVICE);
//            pIntent.cancel();
//            alarmManager.cancel(pIntent);
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, 0,
                    1000, pIntent);
            Log.e(TAG, "...done scheduling");
        } catch (Throwable t) {
            t.printStackTrace();
            Log.e(TAG, t.getMessage());
        }
    }

    /**
     * Check for Bluetooth.
     *
     * @return True if Bluetooth is available.
     */
    public static boolean isBluetoothAvailable() {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return (bluetoothAdapter != null && bluetoothAdapter.isEnabled());
    }
}
