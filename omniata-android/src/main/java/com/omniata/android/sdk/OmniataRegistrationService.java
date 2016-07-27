package com.omniata.android.sdk;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

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
            InstanceID instanceID = InstanceID.getInstance(this);
            String projectNumber = OmniataUtils.gcmSenderId;
            String token = instanceID.getToken(projectNumber,
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            OmniataLog.i(TAG, "GCM Registration Token: " + token);

            if (token != null){
                Omniata.enablePushNotifications(token);

                OmniataUtils.sentTokenToServer = true;
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
