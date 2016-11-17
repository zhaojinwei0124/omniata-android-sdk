package com.omniata.android.fcm;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.omniata.android.sdk.Omniata;
import com.omniata.android.sdk.OmniataLog;


public class OmniataFCMMessagingService extends FirebaseMessagingService {
    private static final String TAG = "OmniataFCMMessagingService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        OmniataLog.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            OmniataLog.d(TAG, "Message data payload: " + remoteMessage.getData());
            try {
                Omniata.trackPushNotification(remoteMessage.getData());
            } catch (Exception e){
                // track push will not be available if the SDK is not initialize but receive an push message.
                OmniataLog.e(TAG, "Exception of tracking push " + e.toString() );
            }
            sendNotification(remoteMessage.getData().get("message"));
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            OmniataLog.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            try {
                Omniata.trackPushNotification(remoteMessage.getNotification());
            } catch (Exception e){
                // track push will not be available if the SDK is not initialize but receive an push message.
                OmniataLog.e(TAG, "Exception of tracking push " + e.toString() );
            }
            sendNotification(remoteMessage.getNotification().getBody());
        }

    }

    /**
     * Create and show notification containing the received FCM message, needs to be implement by customer themselves
     *
     * @param messageBody FCM message body received.
     */
    public void sendNotification(String messageBody) {

    }
}
