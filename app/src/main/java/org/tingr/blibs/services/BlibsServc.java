package org.tingr.blibs.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
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

public class BlibsServc extends IntentService {
    private static final String TAG = BlibsServc.class.getName();
    protected static final String SERVC_NAME = "org.tingr.blibs.service";
    protected static final int PERMISSION_NOTIFICATION_ID = 1001;

    private static final String PERMISSION_RATIONALE_NOTIFY = "%s requires permissions";
    private static final String PERMISSION_RATIONALE_NOTIFY_TXT = "Tap to allow";

    private static final String BLUETOOTH_RATIONALE_NOTIFY = "%s requires Bluetooth";
    private static final String BLUETOOTH_RATIONALE_NOTIFY_TXT = "Tap to turn-on";

    private static String NAME = BlibsServc.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;

    public BlibsServc() {
        super(NAME);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        try {
            handleCommand(intent);
        } catch (Throwable t) {
            // muted
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            handleCommand(intent);
        } catch (Throwable t) {
            // muted
        }
        return START_STICKY;
    }

    public void handleCommand(Intent intent) {
        Log.d(TAG, "handleCommand..." + intent);
        try {
            // MUST BE ON
            if (!isBluetoothAvailable()) {
                notifyAsBluetoothReqd();
            } else if (PermissionsAsk.havePermissions(this)) {
                // clear notification(s)
                NotificationManager nManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                nManager.cancel(PERMISSION_NOTIFICATION_ID);

                // connect to google api
                buildGoogleApiClient();
                return;
            } else {
                notifyAsPermissionsPending();
            }

            // disconnect by default on permission failure
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        } catch (Throwable t) {
            // muted
        }
    }


    private synchronized void buildGoogleApiClient() {
        Log.d(TAG, "buildGoogleApiClient..." + mGoogleApiClient);
        if (mGoogleApiClient == null) {
            Log.d(TAG, "instantiating google api client...");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Nearby.MESSAGES_API, new MessagesOptions.Builder()
                            .setPermissions(NearbyPermissions.BLE).build())
                    .addConnectionCallbacks(new GOOGAPIClient())
                    .build();
            // attempt connect
            mGoogleApiClient.connect();
        } else {
            Log.d(TAG, "reconnecting to google api client..." + mGoogleApiClient);
            mGoogleApiClient.reconnect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy..." + mGoogleApiClient);
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void sub2Background() {
        Log.i(TAG, "sub2Background...");
        /**
         * BACKGROUND BEACON SCAN - SCANS BEACONS <br/>
         * This wont handle if the phone is in unlocked state
         */
        // callback
        SubscribeCallback subCallbakBG = new SubscribeCallback() {
            @Override
            public void onExpired() {
                super.onExpired();
                Log.i(TAG, "on expired...");
                if (mGoogleApiClient != null) {
                    mGoogleApiClient.disconnect();
                }
            }
        };
        Nearby.Messages.subscribe(mGoogleApiClient, getBGPIntent(), subOptions(getApplicationContext(), subCallbakBG))
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            startService(getBGSubServiceIntent());
                        } else {
                            Log.e(TAG, "Operation failed. Error: " +
                                    NearbyMessagesStatusCodes.getStatusCodeString(
                                            status.getStatusCode()));
                        }
                    }
                });
    }


    private PendingIntent getBGPIntent() {
        return PendingIntent.getService(getApplicationContext(), 0,
                getBGSubServiceIntent(), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Intent getBGSubServiceIntent() {
        return new Intent(this, BGBSubService.class);
    }

    private class GOOGAPIClient implements GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            sub2Background();
        }

        @Override
        public void onConnectionSuspended(int i) {
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            mGoogleApiClient.disconnect();
        }
    }

    public static String getAppName(Context context) {
        try {
            return context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
        } catch (Exception e) {

        }

        return "TINGR";
    }

    public static SubscribeOptions subOptions(Context contest, SubscribeCallback subsCallbak) {
        String namespace = Utils.getValForKey(contest, Utils.INIT_NAMESPACE);
        if(namespace == null){
            throw new IllegalStateException(String.format("'%s' is missing from Manifest.", Utils.INIT_NAMESPACE));
        }
        String type = Utils.getValForKey(contest, Utils.INIT_TYPE);
        if(type == null){
            throw new IllegalStateException(String.format("'%s' is missing from Manifest.", Utils.INIT_TYPE));
        }

        // strategy
//        Strategy strategy = new Strategy.Builder().setTtlSeconds(Strategy.TTL_SECONDS_INFINITE).zzlP(2).build();
        return new SubscribeOptions.Builder()
//                .setCallback(subsCallbak)
//                .setStrategy(strategy)
                .setStrategy(Strategy.BLE_ONLY)
                .setFilter(new MessageFilter.Builder()
                        .includeNamespacedType(namespace, type)
                        .build()).build();
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
//                .setAutoCancel(true)
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
//                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentTitle))
                .setOngoing(true)
                .setContentIntent(pIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(PERMISSION_NOTIFICATION_ID, notificationBuilder.build());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    /**
     * Check for Bluetooth.
     *
     * @return True if Bluetooth is available.
     */
    static boolean isBluetoothAvailable() {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return (bluetoothAdapter != null && bluetoothAdapter.isEnabled());
    }
}
