package com.omniata.android.sdk;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by junliu on 7/27/16.
 */
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
        String message = data.getString("message");
        Omniata.trackPushNotification(data);
        sendNotification(message);
    }

    /**
     * Method override to display the message
     * @param message
     */
    public void sendNotification(String message){

    }

}
