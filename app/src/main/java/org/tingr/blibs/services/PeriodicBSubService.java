package org.tingr.blibs.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.MessageFilter;
import com.google.android.gms.nearby.messages.MessagesOptions;
import com.google.android.gms.nearby.messages.NearbyMessagesStatusCodes;
import com.google.android.gms.nearby.messages.NearbyPermissions;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;

import org.tingr.blibs.utils.PermissionsAsk;
import org.tingr.blibs.utils.Utils;

public class PeriodicBSubService extends IntentService {
    private static final String TAG = PeriodicBSubService.class.getName();
    protected static final int PERMISSION_NOTIFICATION_ID = 1001;

    private static final String PERMISSION_RATIONALE_NOTIFY = "%s requires permissions";
    private static final String PERMISSION_RATIONALE_NOTIFY_TXT = "Tap to allow";

    private static final String BLUETOOTH_RATIONALE_NOTIFY = "%s requires BLUETOOTH";
    private static final String BLUETOOTH_RATIONALE_NOTIFY_TXT = "Turn-on now";

    private static String NAME = BGBSubService.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;

    public PeriodicBSubService() {
        super(NAME);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "I ran! onHandleIntent");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
//            mGoogleApiClient.blockingConnect(1, TimeUnit.SECONDS);
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "I ran! onCreate");
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // MUST BE ON
        if (!Utils.isBluetoothAvailable()) {
            notifyAsBluetoothReqd();
        } else if (PermissionsAsk.havePermissions(this)) {
            // clear notification(s)
            notificationManager.cancel(PERMISSION_NOTIFICATION_ID);
            notificationManager.cancel(BGBSubService.MESSAGES_NOTIFICATION_ID);

            // connect to google api
            buildGoogleApiClient();
        } else {
            notifyAsPermissionsPending();
        }
    }

    private void notifyAsBluetoothReqd() {
        // go to BLUETOOTH SETTINGS
        Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // as pending
        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String contentTitle = String.format(BLUETOOTH_RATIONALE_NOTIFY, getAppName(this));
        String contentText = BLUETOOTH_RATIONALE_NOTIFY_TXT;
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentTitle))
                .setOngoing(true)
                .setContentIntent(pIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(PERMISSION_NOTIFICATION_ID, notificationBuilder.build());
    }


    private void notifyAsPermissionsPending() {
        // permissions ask intent
        Intent intent = new Intent(getApplicationContext(), PermissionsAsk.class);
        intent.putExtra(PermissionsAsk.SCHEDULE_PERIODIC_SERVICE, false);

        // as pending
        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String contentTitle = String.format(PERMISSION_RATIONALE_NOTIFY, getAppName(this));
        String contentText = PERMISSION_RATIONALE_NOTIFY_TXT;
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentTitle))
                .setOngoing(true)
                .setContentIntent(pIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(PERMISSION_NOTIFICATION_ID, notificationBuilder.build());
    }

    private synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Nearby.MESSAGES_API, new MessagesOptions.Builder()
                            .setPermissions(NearbyPermissions.BLE).build())
                    .addConnectionCallbacks(new GOOGAPIClient())
                    .build();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        Log.d(TAG, "onDestroy..." + mGoogleApiClient);
    }


    private void scanBeacons() {
        /**
         * BACKGROUND BEACON SCAN - SCANS BEACONS <br/>
         * This wont handle if the phone is in unlocked state
         */
        // callback
        SubscribeCallback subCallbakBG = new SubscribeCallback() {
            @Override
            public void onExpired() {
                super.onExpired();
                Log.i(TAG, "background subscription expired...");
            }
        };
        Nearby.Messages.subscribe(mGoogleApiClient, getPendingIntent(), subOptions(subCallbakBG))
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Subscribed successfully.");
                            startService(getBackgroundSubscribeServiceIntent());
                        } else {
                            Log.e(TAG, "Operation failed. Error: " +
                                    NearbyMessagesStatusCodes.getStatusCodeString(
                                            status.getStatusCode()));
                        }
                    }
                });
    }

    private PendingIntent getPendingIntent() {
        return PendingIntent.getService(getApplicationContext(), 0,
                getBackgroundSubscribeServiceIntent(), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Intent getBackgroundSubscribeServiceIntent() {
        return new Intent(this, BGBSubService.class);
    }

    private class GOOGAPIClient implements GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            scanBeacons();
        }

        @Override
        public void onConnectionSuspended(int i) {
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        }
    }

    public static String getAppName(Context context) {
        try {
            return context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
        } catch (Exception e) {

        }

        return "TINGR";
    }

    public static Drawable getAppIcon(Context context) {
        try {
            return context.getPackageManager().getApplicationIcon(context.getPackageName());
        } catch (Exception e) {

        }

        return null;
    }

    public static SubscribeOptions subOptions(SubscribeCallback subsCallbak) {
        // strategy
//        Strategy strategy = new Strategy.Builder().setTtlSeconds(Strategy.TTL_SECONDS_INFINITE).zzlP(2).build();
        return new SubscribeOptions.Builder()
//                .setCallback(subsCallbak)
//                .setStrategy(strategy)
                .setStrategy(Strategy.BLE_ONLY)
                .setFilter(new MessageFilter.Builder()
                        .includeNamespacedType("tingr-152315", "parent")
                        .build()).build();
    }
}
