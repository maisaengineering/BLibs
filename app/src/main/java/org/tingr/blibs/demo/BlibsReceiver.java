package org.tingr.blibs.demo;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.nearby.messages.Message;

import org.tingr.blibs.dto.ParentAttachment;
import org.tingr.blibs.utils.GsonHelper;

public class BlibsReceiver extends BroadcastReceiver {
    private static final String TAG = BlibsReceiver.class.getName();

    public BlibsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.i(TAG, "onReceive...");
        Log.d(TAG, intent.toString());
    }


//    private void updateNotification(Message message) {
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        if (message == null) {
//            notificationManager.cancel(MESSAGES_NOTIFICATION_ID);
//            return;
//        }
//
//        String contentStr = new String(message.getContent());
//        ParentAttachment parentAttachment = GsonHelper.INSTANCE.fromJson(contentStr, ParentAttachment.class);
//
//        Intent launchIntent = new Intent(getApplicationContext(), MainActivity.class);
//        launchIntent.setAction(Intent.ACTION_MAIN);
//        launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
//                launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        // String contentTitle = getContentTitle(message != null);
////        String contentText = getContentText(isFound);
//        String contentTitle = parentAttachment.getSchool();
//        String contentText = parentAttachment.getNotice();
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(android.R.drawable.star_on)
//                .setContentTitle(contentTitle)
//                .setContentText(contentText)
////                .setAutoCancel(true)
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentTitle))
//                .setOngoing(true)
//                .setContentIntent(pi);
//
//        notificationManager.notify(MESSAGES_NOTIFICATION_ID, notificationBuilder.build());
//    }
}
