package com.omniata.android.sdk;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by junliu on 7/1/16.
 */
public class OmniataMyInstanceIDService extends InstanceIDListenerService {
    private static final String TAG = "OmniataMyInstanceIDLS";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        OmniataLog.i(TAG, "refreshing push token");
        Intent intent = new Intent(this, OmniataRegistrationService.class);
        startService(intent);
    }
}
