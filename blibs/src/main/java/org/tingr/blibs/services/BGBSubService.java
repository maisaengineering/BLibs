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

/**
 * Created by imaginationcoder on 12/13/16.
 */
public class BGBSubService extends IntentService {
    private static final String TAG = BGBSubService.class.getName();
    private static String KEY_DATA = "DATA";

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
                    // anybody intetrested to know?
                    String receiverAction = Utils.getValForKey(getApplicationContext(), Utils.CALLBACK_FOUND);
                    if (receiverAction != null) {
                        Intent bIntent = new Intent();
                        bIntent.setAction(receiverAction);
                        bIntent.putExtra(KEY_DATA, message.getContent());

                        // broadcast now
                        sendBroadcast(bIntent);
                    } else {
                        Log.i(TAG, " ***receiverAction is null");
                    }
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
                    // anybody intetrested to know?
                    String receiverAction = Utils.getValForKey(getApplicationContext(), Utils.CALLBACK_LOST);
                    if (receiverAction != null) {
                        Intent bIntent = new Intent();
                        bIntent.setAction(receiverAction);
                        bIntent.putExtra(KEY_DATA, message.getContent());

                        // broadcast now
                        sendBroadcast(bIntent);
                    } else {
                        Log.i(TAG, " ***receiverAction is null");
                    }
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

            if (!isRunning) {
                Log.i(TAG, "scheduling for periodic runs.");
                Utils.schedulePeriodicTask(getApplicationContext());
            }
        } catch (Throwable t) {
            // muted
        }
    }
}
