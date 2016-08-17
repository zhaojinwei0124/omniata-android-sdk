package com.example.omniata;

import android.util.Log;

import com.omniata.android.sdk.OmniataChannelEngine;

import org.json.JSONArray;

/**
 * Created by junliu on 8/17/16.
 */
public class MyHandler extends OmniataChannelEngine {

    private static String TAG = "MyHandler";

    @Override
    public void onSuccess(int channel, JSONArray content) {
        Log.i(TAG,content.toString());
    }


    @Override
    public void onError(int channel, Exception e) {
        Log.i(TAG, "Channel info retrieved failed");
        Log.e(TAG, e.toString());
    }
}
