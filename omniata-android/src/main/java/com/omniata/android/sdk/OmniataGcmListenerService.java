package com.omniata.android.sdk;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;


public class OmniataGcmListenerService  extends GcmListenerService {
    private static final String TAG = "OmniataGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        try {
            Omniata.trackPushNotification(data);
        } catch (Exception e){
            // track push will not be available if the SDK is not initialize but receive an push message.
            OmniataLog.e(TAG, "Exception of tracking push " + e.toString() );
        }
        String message = data.getString("message");
        if (message != null){
            OmniataLog.i(TAG, "onMessageReceived");
            sendNotification(message);
        }
    }

    /**
     * Method override to display the message
     * @param message push notification message
     */
    public void sendNotification(String message){

    }

}
