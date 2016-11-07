package com.omniata.android.fcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.omniata.android.sdk.Omniata;
import com.omniata.android.sdk.OmniataLog;


public class OmniataFCMInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "OmniataFCMInstanceIDService";


    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        OmniataLog.d(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * send token to Omniata
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        try {
            Omniata.enablePushNotifications("om_fcm_enable", token);
        } catch (Exception e){
            OmniataLog.e(TAG, "cannot enable push notifications");
        }

    }
}
