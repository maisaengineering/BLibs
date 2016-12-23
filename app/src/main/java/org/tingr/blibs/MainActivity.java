package org.tingr.blibs;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import org.tingr.blibs.services.PeriodicBSubService;
import org.tingr.blibs.utils.PermissionsAsk;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private static final int PERMISSIONS_REQUEST_CODE = 0;
    private FrameLayout mContainer;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PERMISSIONS_REQUEST_CODE) {
                Utils.schedulePeriodicTask(MainActivity.this);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!PermissionsAsk.havePermissions(MainActivity.this)) {
            // call for permission check
            Intent intent = new Intent(getApplicationContext(), PermissionsAsk.class);
            startActivityForResult(intent, PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        // grab holder
        this.mContainer = (FrameLayout) findViewById(R.id.container);

        // call for permission check
        Intent intent = new Intent(getApplicationContext(), PermissionsAsk.class);
        startActivityForResult(intent, PERMISSIONS_REQUEST_CODE);
    }
}