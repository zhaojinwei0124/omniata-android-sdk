package com.omniata.android.sdk;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by junliu on 7/1/16.
 */
public class OmniataRegistrationService extends IntentService {
    private static final String TAG = "OmniataRegService";
    private static final String[] TOPICS = {"global"};

    public OmniataRegistrationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String projectNumber = OmniataUtils.gcmSenderId;
            String token = instanceID.getToken(projectNumber,
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            OmniataLog.i(TAG, "GCM Registration Token: " + token);

            if (token != null){
                Omniata.enablePushNotifications(token);

                // Store a boolean that indicates whether the generated token has been sent.
                OmniataUtils.sentTokenToServer = true;
                // [END register_for_gcm]
            }else{
                OmniataLog.i(TAG, "Failed to get the GCM Registration Token");
            }

        } catch (Exception e) {
            OmniataLog.d(TAG, "Failed to complete token refresh", e);
            OmniataUtils.sentTokenToServer = false;
        }
        // registration has completed
        OmniataUtils.registrationComplete = true;
    }

}
