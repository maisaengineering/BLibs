package org.tingr.blibs.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import org.tingr.blibs.BuildConfig;
import org.tingr.blibs.R;

public class PermissionsAsk extends AppCompatActivity {
    private static final String TAG = PermissionsAsk.class.getName();
    public static String SCHEDULE_PERIODIC_SERVICE;

    private static final int PERMISSIONS_REQUEST_CODE = 101;
    public static final String OK = "OK";
    public static final String PACKAGE = "package";
    public static final String SETTINGS = "Settings";

    public static final String PERMISSION_RATIONALE = "Location permission is req'd.";
    public static final String PERMISSION_DENIED_EXPLANATION = "While we wait, provide req'd permissions by going to " + SETTINGS;

    private FrameLayout mContainer;

    @Override
    protected void onResume() {
        super.onResume();
        if (havePermissions(PermissionsAsk.this)) {
            Log.i(TAG, "***HAVE ALL PERMISSIONS FOR NOW***");
            returnAsSuccessful();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (havePermissions(PermissionsAsk.this)) {
            returnAsSuccessful();
        }

        setContentView(R.layout.activity_permissions_ask);
        // grab holder
        this.mContainer = (FrameLayout) findViewById(R.id.container);

        // permissions check
        if (!havePermissions(PermissionsAsk.this)) {
            Log.i(TAG, "CREATE::Requesting permissions needed for this app.");
            requestPermissions();
        }
    }

    public static boolean havePermissions(Context aContext) {
        boolean isGranted;

        // ACCESS_FINE_LOCATION
        isGranted = ContextCompat.checkSelfPermission(aContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        // RECEIVE_BOOT_COMPLETED
        if (isGranted) {
            isGranted = ContextCompat.checkSelfPermission(aContext, Manifest.permission.RECEIVE_BOOT_COMPLETED)
                    == PackageManager.PERMISSION_GRANTED;
        }

        // WAKE_LOCK
        if (isGranted) {
            isGranted = ContextCompat.checkSelfPermission(aContext, Manifest.permission.WAKE_LOCK)
                    == PackageManager.PERMISSION_GRANTED;
        }

        // INTERNET
        if (isGranted) {
            isGranted = ContextCompat.checkSelfPermission(aContext, Manifest.permission.INTERNET)
                    == PackageManager.PERMISSION_GRANTED;
        }

        return isGranted;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, permissionsToAsk(), PERMISSIONS_REQUEST_CODE);
    }


    private void showLinkToSettingsSnackbar() {
        if (mContainer == null) {
            return;
        }

        Snackbar.make(mContainer,
                PERMISSION_DENIED_EXPLANATION,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(SETTINGS, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // go to APP SETTINGS
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts(PACKAGE,
                                BuildConfig.APPLICATION_ID, null);
                        intent.setData(uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).show();
    }

    private void showRequestPermissionsSnackbar() {
        if (mContainer == null) {
            return;
        }
        Snackbar.make(mContainer, PERMISSION_RATIONALE,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(OK, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request permission.
                        ActivityCompat.requestPermissions(PermissionsAsk.this,
                                permissionsToAsk(),
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != PERMISSIONS_REQUEST_CODE) {
            return;
        }

        boolean isGranted = true;
        // permission handling
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                isGranted = false;
                if (ActivityCompat.shouldShowRequestPermissionRationale(PermissionsAsk.this, permission)) {
                    showRequestPermissionsSnackbar();
                } else {
                    showLinkToSettingsSnackbar();
                }
            }
        }

        if (isGranted) {
            returnAsSuccessful();
        }
    }

    private void returnAsSuccessful() {
        boolean isSchedulePeriodicTask = getIntent().getBooleanExtra(SCHEDULE_PERIODIC_SERVICE, false);
        if (isSchedulePeriodicTask) {
            Utils.schedulePeriodicTask(PermissionsAsk.this);
        }

        Intent output = new Intent();
        setResult(RESULT_OK, output);
        finish();
    }

    public static String[] permissionsToAsk() {
        String[] permissionsToAsk = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.INTERNET
        };

        return permissionsToAsk;
    }
}
