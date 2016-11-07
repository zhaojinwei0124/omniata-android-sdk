package com.example.omniata;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.omniata.android.sdk.Omniata;

import org.json.JSONArray;


/*
 * created by Jun
 * 16/06/2016
 */


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize omniata service
        Context context = this;
        String apiKey = "2f52f118";
        String userId = "juntest1";

        Omniata.initialize(context, apiKey, userId, "JunOy");
        Omniata.setLogLevel(Log.INFO);

        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);
        Button button4 = (Button) findViewById(R.id.button4);
        Button button5 = (Button) findViewById(R.id.button5);
        Button button6 = (Button) findViewById(R.id.button6);
        Button button7 = (Button) findViewById(R.id.button7);
        Button button8 = (Button) findViewById(R.id.button8);


        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Omniata.trackLoad();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Omniata.trackRevenue(9.99, "USD");
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Omniata.track("testing");
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Omniata.channel(2);
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                JSONArray message = Omniata.getChannelmessage();
                if (message != null){
                    Log.i("channel message: ",Omniata.getChannelmessage().toString());
                }else{
                    Log.i("channel message: ","empty");
                }
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Enable push notification with push token
//                Omniata.enablePushNotifications("RegistrationID");

                // Auto enable push notification by adding the google-services.json file under the project.
                // check detail how to get the google-services.json here:
                // https://developers.google.com/mobile/add?platform=android&cntapi=gcm&cnturl=https:%2F%2Fdevelopers.google.com%2Fcloud-messaging%2Fandroid%2Fclient&cntlbl=Continue%20Adding%20GCM%20Support&%3Fconfigured%3Dtrue
                // R.string.gcm_defaultSenderId is the value of "project_number" in google-services.json file.
                if (checkPlayServices()) {
                    Omniata.autoEnablePushNotifications("fcm", null);
//                    Omniata.autoEnablePushNotifications(getString(R.string.gcm_defaultSenderId));
                }
            }
        });

        button7.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
//                Omniata.trackAdvertiserID();
            Omniata.disablePushNotifications("fcm");
            }
        });

        button8.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                MyHandler myChannelHandler = new MyHandler();
                Omniata.channel(2,myChannelHandler);
            }
        });
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}
