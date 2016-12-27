package org.tingr.blibs.services;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Distance;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import org.tingr.blibs.utils.Utils;

/**
 * Created by imaginationcoder on 12/13/16.
 */
public class BGBSubService extends IntentService {
    private static final String TAG = BGBSubService.class.getName();

    public BGBSubService() {
        super(BGBSubService.class.getSimpleName());
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        Log.i(TAG, "onHandleIntent..." + intent);
        if (intent == null) {
            Log.i(TAG, "***onHandleIntent...NULL***");
            return;
        }
        Nearby.Messages.handleIntent(intent, new MessageListener() {
            @Override
            public void onFound(Message message) {
                try {
                    Log.i(TAG, "onFound message = " + message);
                    Log.i(TAG, "namespaced/type..." + message.getNamespace() +
                            "/" + message.getType());
                    Log.i(TAG, "intent..." + intent);

                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(Utils.BROADCAST_KEY_DETECTED);
                    broadcastIntent.putExtra("Message", message);
                    sendBroadcast(intent);
                } catch (Throwable t) {
                    // muted
                } finally {
                    // conditionally start service
                    condStartServc();
                }

            }

            @Override
            public void onLost(Message message) {
                try {
                    Log.i(TAG, "onLost message = " + message);
                } catch (Throwable t) {
                    // muted
                } finally {
                    // conditionally start service
                    condStartServc();
                }
            }

            @Override
            public void onDistanceChanged(Message message, Distance distance) {
                Log.i(TAG, "onDistanceChanged message..." + message);
                Log.i(TAG, "onDistanceChanged distance..." + distance);
            }
        });
    }

    private synchronized void condStartServc() {
        try {
            boolean isRunning = false;
            // grab system services for querying blips service
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo _servc : manager.getRunningServices(Integer.MAX_VALUE)) {
                isRunning |= BlibsServc.class.getName().equals(_servc.service.getClassName());
                if (isRunning) {
                    break;
                }
            }

            Log.i(TAG, "***isRunning = " + isRunning);
            if (!isRunning) {
                Utils.schedulePeriodicTask(getApplicationContext());
            }
        } catch (Throwable t) {
            // muted
        }
    }
}
