package org.tingr.blibs.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.tingr.blibs.R;
import org.tingr.blibs.utils.PermissionsAsk;
import org.tingr.blibs.services.Utils;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private static final int PERMISSIONS_REQUEST_CODE = 0;

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
        Log.i(TAG, "On RESUME scheduling task...");

        if (!PermissionsAsk.havePermissions(MainActivity.this)) {
            Log.i(TAG, "need permission. asking now...");

            // call for permission check
            Intent intent = new Intent(getApplicationContext(), PermissionsAsk.class);
            startActivityForResult(intent, PERMISSIONS_REQUEST_CODE);
        }else{
            Log.i(TAG, "scheduling now...");
            Utils.schedulePeriodicTask(MainActivity.this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
//        if (!PermissionsAsk.havePermissions(MainActivity.this)) {
//            // call for permission check
//            Intent intent = new Intent(getApplicationContext(), PermissionsAsk.class);
//            startActivityForResult(intent, PERMISSIONS_REQUEST_CODE);
//        }
    }
}