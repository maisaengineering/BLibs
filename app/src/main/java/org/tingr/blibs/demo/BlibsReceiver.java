package org.tingr.blibs.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.tingr.blibs.utils.Utils;

public class BlibsReceiver extends BroadcastReceiver {
    private static final String TAG = BlibsReceiver.class.getName();

    public BlibsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.i(TAG, "onReceive..." + intent);
        // read detection state
        String state = intent.getStringExtra(Utils.BEACON.STATE.name());
        if (state != null && (state.equals(Utils.BEACON.FOUND.name()) || state.equals(Utils.BEACON.LOST.name()))) {
            // grab attachment data
            byte[] data = intent.getByteArrayExtra(Utils.BEACON.DATA.name());
            Log.i(TAG, "state..." + state);
            Log.i(TAG, "data..." + new String(data));
        } else {
            Log.w(TAG, "***UN-KNOWN STATE *** ");
        }

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
