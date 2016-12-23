package org.tingr.blibs;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.android.gms.nearby.messages.Message;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.tingr.blibs.services.PeriodicBSubService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * Created by imaginationcoder on 12/14/16.
 */
public final class Utils {

    public static void schedulePeriodicTask(Context aContext){
        /**
         * PERIODIC SERVICE TO HANDLE BEACON SCANS IN THE BACKGROUND - HANDLES UNLOCKED STATE
         * This wont handle if app is killed by user
         */
        Intent periodicIntent = new Intent(aContext, PeriodicBSubService.class);
        PendingIntent pendingPeriodicIntent = PendingIntent.getService(
                aContext,
                101, periodicIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) aContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingPeriodicIntent);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, -1,
                1000, pendingPeriodicIntent);
    }

    /**
     * Check for Bluetooth.
     * @return True if Bluetooth is available.
     */
    public static boolean isBluetoothAvailable() {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return (bluetoothAdapter != null && bluetoothAdapter.isEnabled());
    }
}
