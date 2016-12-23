package org.tingr.blibs.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Distance;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import org.tingr.blibs.MainActivity;
import org.tingr.blibs.R;
import org.tingr.blibs.Utils;
import org.tingr.blibs.dto.ParentAttachment;
import org.tingr.blibs.utils.GsonHelper;

import java.util.List;

/**
 * Created by imaginationcoder on 12/13/16.
 */
public class BGBSubService extends IntentService {
    private static final String TAG = BGBSubService.class.getName();

    private static String NAME = BGBSubService.class.getSimpleName();
    protected static final int MESSAGES_NOTIFICATION_ID = 108;
    private static final int NUM_MESSAGES_IN_NOTIFICATION = 5;

    public BGBSubService() {
        super(NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        updateNotification();
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.i(TAG, "onHandleIntent...");

            Nearby.Messages.handleIntent(intent, new MessageListener() {
                @Override
                public void onFound(Message message) {
                    Log.i(TAG, "Message = " + message);
//                    Log.i(TAG, "Message string: " + new String(message.getContent()));
                    Log.i(TAG, "Message namespaced/type: " + message.getNamespace() +
                            "/" + message.getType());
//                    Utils.saveFoundMessage(getApplicationContext(), message);
                    updateNotification(message);
                }

                @Override
                public void onLost(Message message) {
                    Log.i(TAG, "lost message = " + message);
//                    Utils.removeLostMessage(getApplicationContext(), message);
                    updateNotification(null);
                }

                @Override
                public void onDistanceChanged(Message message, Distance distance) {
                    Log.i(TAG, "distance Message = " + message);
                    Log.i(TAG, "distance Distance = " + distance);
                }
            });
        }
    }

    private void updateNotification(Message message) {
//        List<String> messages = Utils.getCachedMessages(getApplicationContext());

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(message == null){
            notificationManager.cancel(MESSAGES_NOTIFICATION_ID);
            return;
        }

//        byte[] contentBytes = Base64.decode(message.getContent(), Base64.NO_WRAP);
        String contentStr = new String(message.getContent());
        ParentAttachment parentAttachment = GsonHelper.INSTANCE.fromJson(contentStr, ParentAttachment.class);

        Intent launchIntent = new Intent(getApplicationContext(), MainActivity.class);
        launchIntent.setAction(Intent.ACTION_MAIN);
        launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // String contentTitle = getContentTitle(message != null);
//        String contentText = getContentText(isFound);
        String contentTitle = parentAttachment.getSchool();
        String contentText = parentAttachment.getNotice();

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.star_on)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
//                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentTitle))
                .setOngoing(true)
                .setContentIntent(pi);

        notificationManager.notify(MESSAGES_NOTIFICATION_ID, notificationBuilder.build());
    }

    private String getContentTitle(boolean isFound) {
        if(isFound){
            return getResources().getString(R.string.one_message);
        }else{
            return getResources().getString(R.string.scanning);
        }
    }

    private String getContentText(List<String> messages) {
        String newline = System.getProperty("line.separator");
        if (messages.size() < NUM_MESSAGES_IN_NOTIFICATION) {
            return TextUtils.join(newline, messages);
        }
        return TextUtils.join(newline, messages.subList(0, NUM_MESSAGES_IN_NOTIFICATION)) +
                newline + "&#8230;";
    }
}
