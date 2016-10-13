package com.omniata.android.unityplugin;

import android.text.TextUtils;
import android.widget.Toast;

import com.omniata.android.sdk.OmniataLog;
import com.unity3d.player.UnityPlayer;

public class UnityUtil {
    private static final String TAG = "UnityUtil";

    public static void showToast(final String message) {
        UnityPlayer.currentActivity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(UnityPlayer.currentActivity, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void sendMessage(String unityClass, String method, String message) {
        try {
            if(TextUtils.isEmpty(message)) {
                OmniataLog.i(TAG,"Calling unity method " + unityClass + " with empty message");
                UnityPlayer.UnitySendMessage(unityClass, method, "");
            } else {
                OmniataLog.i(TAG,"Calling unity method " + unityClass + " with message:"+ message);
                UnityPlayer.UnitySendMessage(unityClass, method, message);
            }
        } catch (UnsatisfiedLinkError var3) {
            var3.printStackTrace();
        }
    }
}
