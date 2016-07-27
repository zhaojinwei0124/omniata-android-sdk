package com.omniata.android.unityplugin;

import android.text.TextUtils;
import android.widget.Toast;

import com.unity3d.player.UnityPlayer;

/**
 * Created by junliu on 7/27/16.
 */
public class UnityUtil {
    private static final String RECEIVER_NAME = "UnityUtil";

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
                UnityPlayer.UnitySendMessage(unityClass, method, "");
            } else {
                UnityPlayer.UnitySendMessage(unityClass, method, message);
            }
        } catch (UnsatisfiedLinkError var3) {
            var3.printStackTrace();
        }
    }
}
