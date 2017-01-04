package org.tingr.blibs.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by imaginationcoder on 12/14/16.
 */
public final class Utils {
    private static final String TAG = Utils.class.getName();

    /**
     * CLIENT INTEGRATIONS
     */
    static final String INIT_NAMESPACE_TYPE = "org.tingr.blibs.init.namespacetype";

    static final String CALLBACK_FOUND = "org.tingr.blibs.callback.found";
    static final String CALLBACK_LOST = "org.tingr.blibs.callback.lost";

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

    static String getValForKey(Context context, String key) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = appInfo.metaData;
            Object val = bundle == null ? null : bundle.get(key);
            return (String) val;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

   public static String getAppPackageName(Context context) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return (String) appInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }

        return "";
    }
}
