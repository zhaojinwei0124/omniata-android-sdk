package com.omniata.android.unityplugin;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.omniata.android.sdk.Omniata;
import com.omniata.android.sdk.OmniataLog;

/**
 * Created by junliu on 7/27/16.
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
     * @param msgId
     */
    @Override
    public void onMessageSent(String msgId) {
        Log.v(TAG, "onMessageSent");
        UnityUtil.sendMessage(RECEIVER_CLASS_NAME, ON_MESSAGE_SENT, msgId);
    }

    /**
     * Called when a message is received.
     * @param from
     * @param data
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        OmniataLog.i(TAG, "onMessageReceived");
        try {
            Omniata.trackPushNotification(data);
        } catch (Exception e){
            // track push will not be available if the SDK is not initialize but receive an push message.
            OmniataLog.e(TAG, "Exception of tracking push " + e.toString() );
        }
        // e.g. content value is something like this {"data":{"message":"<message>"}}
        String message = data.getString("message");
        if (message != null){
            UnityUtil.sendMessage(RECEIVER_CLASS_NAME, ON_MESSAGE_RECEIVED, message);
        }

    }

    /**
     * Called when there was an error sending an upstream message.
     * @param msgId
     * @param error
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
