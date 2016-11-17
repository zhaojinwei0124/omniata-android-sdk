package com.omniata.android.unityplugin;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.omniata.android.sdk.Omniata;
import com.omniata.android.sdk.OmniataLog;

/**
 * UnityGcmListenerService listens the push notification and send it to notification center
 * and also send it back to unity in case it is needed.
 * supports for both GCM and FCM for displaying push
 */
public class UnityGcmListenerService extends GcmListenerService {
    private static final String TAG = UnityGcmListenerService.class.getSimpleName();
    private static final String RECEIVER_CLASS_NAME = "GCMReceiver";

    private static final String ON_MESSAGE_SENT = "OnMessageSent";
    private static final String ON_MESSAGE_RECEIVED = "OnMessageReceived";
    private static final String OM_SEND_ERROR = "OnSendError";
    private static final String ON_DELETED_MESSAGES = "OnDeletedMessages";

    public UnityGcmListenerService() {
    }

    /**
     * Called when an upstream message has been successfully sent
     * @param msgId message ID
     */
    @Override
    public void onMessageSent(String msgId) {
        Log.v(TAG, "onMessageSent");
        UnityUtil.sendMessage(RECEIVER_CLASS_NAME, ON_MESSAGE_SENT, msgId);
    }

    /**
     * Called when a message is received
     * @param from from
     * @param data push data
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        // current support content type format is: {"data":{"message":"<message>", "title": "<title>"}}

        OmniataLog.i(TAG, "onMessageReceived");
        try {
            Omniata.trackPushNotification(data);
        } catch (Exception e){
            // track push will not be available if the SDK is not initialize but receive an push message.
            OmniataLog.e(TAG, "Exception of tracking push " + e.toString() );
        }

        String message = data.getString("message");
        if (message != null) {
            // send the message to Unity. Unity can get the message as well.
            UnityUtil.sendMessage(RECEIVER_CLASS_NAME, ON_MESSAGE_RECEIVED, message);
            String title = data.getString("title");
            if (title != null) {
                sendNotification(title, message);
            } else {
                sendNotification(null, message);
            }
        }
    }

    /**
     * Send push notification to Android system status bar
     * @param title title
     * @param message message
     */
    public void sendNotification(String title, String message){
        OmniataLog.d(TAG,"getting push message");
        Intent intent = new Intent(this, com.unity3d.player.UnityPlayerNativeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Resources res = this.getResources();

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(res.getIdentifier("app_icon", "drawable", this.getPackageName()))
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    /**
     * Called when there was an error sending an upstream message.
     * @param msgId msg ID
     * @param error error
     */
    @Override
    public void onSendError(String msgId, String error) {
        OmniataLog.i(TAG, "onSendError");
        UnityUtil.sendMessage(RECEIVER_CLASS_NAME, OM_SEND_ERROR, error);
    }

    /**
     * Called when GCM server deletes pending messages due to exceeded storage limits
     */
    @Override
    public void onDeletedMessages(){
        OmniataLog.i(TAG, "onDeletedMessages");
        UnityUtil.sendMessage(RECEIVER_CLASS_NAME, ON_DELETED_MESSAGES,null);
    }
}
