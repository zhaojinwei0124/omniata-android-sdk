package com.omniata.android.sdk;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Omniata {
	
	private static final String TAG       = "Omniata";
	private static final String EVENT_LOG = "events";
	private static final String SDK_VERSION = "android-2.1.12";

	private static Omniata instance;
    private static OmniataChannelEngine channelHandler;


	/**
	 * Initialize the Omniata API with different URL for different Omniata services
	 * @param context current context
	 * @param apiKey api key
	 * @param userID user id
	 * @param org	organization name of the URl, new url will be org.analyzer.omniata.com and org.engager.omniata.com
	 * @throws IllegalArgumentException illegal argument
	 */
	public static void initialize(Context context, String apiKey, String userID, String org) throws IllegalArgumentException{
		synchronized(Omniata.class) {			
			if (instance == null) {
				OmniataLog.i(TAG, "Initializing Omniata API");
				instance = new Omniata(context, apiKey, userID, org);
                channelHandler = new OmniataChannelEngine();
			}
			/*
			 * Since this singleton may persist across application launches
			 * we need to support re-initialization of the SDK
			 */
            instance._initialize(context, apiKey, userID, org);
            trackLoad(null);
        }
	}

	/**
	 * Initialize the Omniata API with some different URL and other extra om_load parameters
	 * @param context current context
	 * @param apiKey api key
	 * @param userID user id
	 * @param org    organization name of the URl, new url will be org.analyzer.omniata.com and org.engager.omniata.com
	 * @param baseUrl base url
	 * @param parameters params
	 * @throws IllegalArgumentException illegal argument
     */
	public static void initialize(Context context, String apiKey, String userID, String org, String baseUrl, JSONObject parameters) throws IllegalArgumentException{
		synchronized(Omniata.class) {
			if (instance == null) {
				OmniataLog.i(TAG, "Initializing Omniata API with more complexity");
				instance = new Omniata(context, apiKey, userID, org);
				channelHandler = new OmniataChannelEngine();
			}
			/*
			 * Since this singleton may persist across application launches
			 * we need to support re-initialization of the SDK
			 */
			instance._initialize(context, apiKey, userID, org);
			if (baseUrl != null){
				OmniataUtils.setURL(org, baseUrl);
			}
			trackLoad(parameters);
		}
	}

	/**
	 * Initilize for unity usage with string parameters.
	 * @param context current context
	 * @param apiKey api key
	 * @param userID userid
	 * @param org org
	 * @param baseUrl base url
	 * @param parameters params
	 * @throws IllegalArgumentException illegal argument
     */
	public static void unityInitialize(Context context, String apiKey, String userID, String org, String baseUrl, String parameters)throws IllegalArgumentException{
		JSONObject myParameters = new JSONObject();
		if (parameters != null) {
			OmniataLog.i("extra om_load parameters:",parameters);
			myParameters =  unityJsonGenerator(parameters);
		} else {
			myParameters = null;
		}
		initialize(context, apiKey, userID, org, baseUrl, myParameters);
	}

	public static void setLogLevel(int priority) {
		OmniataLog.setPriority(priority);
	}
	
	
	/**
	 * Tracks a parameterless event
	 * 
	 * @param eventType event type
	 * @throws IllegalArgumentException if eventType is null or empty
	 * @throws IllegalStateException if SDK not initialized 
	 */
	public static void track(String eventType) throws IllegalArgumentException, IllegalStateException {
		track(eventType, null);
	}

	/**
	 * Tracks an event with parameters
	 * @param eventType evety type
	 * @param parameters extra pramas
	 * @throws IllegalArgumentException	if eventType is null or empty
	 * @throws IllegalStateException	if SDK not initialized  
	 */
	public static void track(String eventType, JSONObject parameters) throws IllegalArgumentException, IllegalStateException {
		synchronized(Omniata.class) {
			assertInitialized();
			instance._track(eventType, parameters);
		}
	}
	
	/**
	 * Track Load with unity version info getting from unity code.
	 * @param para extra params
	 */
	public static void unityTrackLoad(String para){
		trackLoad(unityJsonGenerator(para));
	}
	
	  /**
	  * Track custom event for usage in Unity
	  * @param eventType event type
	  * @param para	extra params
	  */
	 public static void unityTrack(String eventType, String para){
	     instance._track(eventType, unityJsonGenerator(para));
	 }
	 

	 /**
	  * Track revenue for usage in unity
	  * @param total total value
	  * @param currencyCode currency
	  * @param para extra params
	  */
	 public static void unityTrackRevenue(double total, String currencyCode, String para){
		 JSONObject revenuePara = unityJsonGenerator(para);
		 try {
			revenuePara.put("total", total);
			revenuePara.put("currency_code", currencyCode);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Java doesn't use locale-specific formatting, so this is safe
		 instance._track("om_revenue", revenuePara);
	 }

    public static void unityTrackAdvertiserID(String para){
        trackAdvertiserID(unityJsonGenerator(para));
    }

	 /**
	  * String from Unity to JSONobject, string from Unity is URL Encoded.
	  * @param para
	  * @return return json params
	  */
	 private static JSONObject unityJsonGenerator(String para){
		 JSONObject parameters = new JSONObject();
	     String[] paraArray=para.split("\n");
	     String[] paraPair;
	     // Convert string to JsonObject
	     for (int i=0;i<paraArray.length;i++){
	         paraPair = paraArray[i].split("=");
	         try{
	         	parameters.put(URLDecoder.decode(paraPair[0], "UTF-8"), URLDecoder.decode(paraPair[1], "UTF-8"));
	            //parameters.put(paraPair[0], paraPair[1]);
	         } catch(Exception e){
	             // do something
	        	 OmniataLog.e(TAG, e.toString());
	         }
	     }
	     return parameters;
	 }


    /**
     * Get the string of channelMessages
     * @return null or channel message
     * @throws IllegalStateException illegal statement
     */
    @SuppressWarnings("static-access")
    public static String unityGetChannelmessage() throws IllegalStateException{
        JSONArray message = instance.channelHandler.channelMessage;
        if (message != null ){
            return instance.channelHandler.channelMessage.toString();
        }else{
            return null;
        }
    }
	/**
	 * unity_log log method for using in Unity3D 
	 * @param message messgage
	 */
	public static void unity_log(String message){
	   Log.i("Omniata", message);
	}
	 
	private static void assertInitialized() throws IllegalStateException{
		if (instance == null) {
			throw new IllegalStateException("Uninitialized SDK");
		}
	}
	
	/**
	 * Tracks a load event. 
	 * Should be called upon app start.
	 * @throws IllegalStateException if SDK not initialized 
	 */
	public static void trackLoad() throws IllegalStateException{
		trackLoad(null);
	}
	
	/**
	 * Tracks a load event with additional parameters
	 * @param parameters Additional parameters to track with event
	 * @throws IllegalStateException if SDK not initialized
	 */
	public static void trackLoad(JSONObject parameters) throws IllegalStateException {
		if (parameters == null) {
			parameters = new JSONObject();
		}
		track("om_load", OmniataUtils.mergeJSON(getAutomaticParameters(), parameters));
	}

    /**
     * Set the base URL for Event and Channel API.
     * @param url base url
     * @throws IllegalStateException illegal statement exception
     */
    public static void setBaseUrl(String url) throws  IllegalStateException{
        OmniataUtils.setURL(null, url);
    }

    /**
     * Sets the current user id used to track events with enableDeghost tag
     * @param userId user id
	 * @param enableDeghost enable deghost tag
     * @throws IllegalArgumentException if userID is null or empty
     * @throws IllegalStateException if SDK not initialized
     */
    public static void setUserId(String userId, boolean enableDeghost) throws IllegalArgumentException, IllegalStateException {
        synchronized(Omniata.class) {
            assertInitialized();
            OmniataUtils.assertUserIdValid(userId);
            if( enableDeghost == true ){
                instance._track("om_deghost",null);
            }
            instance._setUserId(userId);
        }
    }

	/**
	 * Sets the current user id used to track events
	 * @param userId user id
	 */
	public static void setUserId(String userId)  {
		setUserId(userId,true);
	}

    /**
     * Sets the current API key used to track events with enableDeghost tag
     * @param apiKey api key
     * @param enableDeghost enable deghost tag
     * @throws IllegalArgumentException if apiKey is null or empty
     * @throws IllegalStateException if SDK not initialized
     */
    public static void setApiKey(String apiKey, boolean enableDeghost) throws IllegalArgumentException, IllegalStateException {
        synchronized(Omniata.class) {
            assertInitialized();
            OmniataUtils.assertApiKeyValid(apiKey);
            if (enableDeghost == true){
                instance._track("om_deghost",null);
            }
            instance._setApiKey(apiKey);
        }
    }

	/**
	 * Sets the current API key used to track events
	 * @param apiKey api key
	 */
	public static void setApiKey(String apiKey) {
        setApiKey(apiKey, true);
	}

    /**
     * Sets both the current API key and user id used to track events with enableDeghost tag
     * @param apiKey api key
     * @param userId user id
     * @param enableDeghost enable deghost tag
     * @throws IllegalArgumentException if apiKey and userID are null or empty
     * @throws IllegalStateException if SDK not initialized
     */
    public static void setBothApiKeyUserId(String apiKey, String userId, boolean enableDeghost) throws IllegalArgumentException, IllegalStateException {
        synchronized(Omniata.class) {
            assertInitialized();
            OmniataUtils.assertApiKeyValid(apiKey);
            OmniataUtils.assertUserIdValid(userId);
            if (enableDeghost == true){
                instance._track("om_deghost",null);
            }
            instance._setApiKey(apiKey);
            instance._setUserId(userId);
        }
    }

    /**
     * Sets both the current API key and user id used to track events
     * @param apiKey api key
     * @param userId user id
     */
    public static void setBothApiKeyUserId(String apiKey, String userId) {
        setBothApiKeyUserId(apiKey,userId,true);
    }
	
	/**
	 * Fetches content for this user from a specific channel
	 * 
	 * @param channelId The id of this channel
	 * @throws IllegalStateException if SDK not initialized
	 */
	public static void channel(int channelId) throws IllegalStateException {
        synchronized(Omniata.class) {
			assertInitialized();
			instance._channel(channelId, channelHandler);
		}
	}

    /**
     * Fetches content with customized handler
     * @param channelId channel id
     * @param myChannelHandler channel handler
     * @throws IllegalStateException illegal statement exception
     */
	public static void channel(int channelId, OmniataChannelEngine myChannelHandler) throws IllegalStateException{
		synchronized(Omniata.class) {
			assertInitialized();
			instance._channel(channelId, myChannelHandler);
		}
	}

    /**
     * Get the Json object of channelMessages
     * @return return channel message
     * @throws IllegalStateException illegal statement exception
     */
    @SuppressWarnings("static-access")
    public static JSONArray getChannelmessage() throws IllegalStateException{
        return instance.channelHandler.channelMessage;
    }

	/**
	 * Tracks a revenue event
	 * 
	 * @param total Revenue amount in currency code
	 * @param currencyCode A three letter currency code following ISO-4217 spec.
	 * @throws IllegalStateException if SDK not initialized 
	 */
	public static void trackRevenue(double total, String currencyCode) throws IllegalStateException {
		// TODO: add currency code validation
		trackRevenue(total, currencyCode, null);
	}
	
	/**
	 * Tracks a revenue event
	 * 
	 * @param total Revenue amount in currency code
	 * @param currencyCode A three letter currency code following ISO-4217 spec.
	 * @param additionalParams Additional parameters to be tracked with event
	 * @throws IllegalStateException if SDK not initialized 
	 */
	public static void trackRevenue(double total, String currencyCode, JSONObject additionalParams) throws IllegalStateException {
		JSONObject parameters = new JSONObject();

		try {
			parameters.put("total", total); // Java doesn't use locale-specific formatting, so this is safe
			parameters.put("currency_code", currencyCode);
			
			if (additionalParams != null) {
				Iterator<String> i = (Iterator<String>)additionalParams.keys();
				while(i.hasNext()) {
					String key = (String)i.next();
					Object val = additionalParams.get(key);
					parameters.put(key, val);
				}
			}
			
			track("om_revenue", parameters);
			
		} catch (JSONException e) {
			OmniataLog.e(TAG, e.toString());
		}
	}

    /**
     * Tracks the Advertiser ID and Android ID
     *
     * @throws IllegalStateException illegal statement exception
     */
	public static void trackAdvertiserID() throws IllegalStateException {
        trackAdvertiserID(null);
	}

    /**
     * Track the Advertiser ID and Android ID with parameters
     * 
     * If Google advetiser ID is availabe, there will parameters of
     * om_google_aid="advetiser_id" and om_android_id="device_android_id"
     * inside of the tracking event, otherwise there will be only
     * om_android_id="device_android_id" parameter inside of the event.
     *
     * @param additionalParams extra params
     * @throws IllegalStateException statement exception
     */
    public static void trackAdvertiserID(JSONObject additionalParams) throws IllegalStateException{
        final JSONObject tempParas = additionalParams;
        new Thread(new Runnable() {
                @Override
                public void run() {
                    String android_id = Secure.getString(instance.context.getContentResolver(),
                            Secure.ANDROID_ID);
                    JSONObject parameters = new JSONObject();
                    try {
                        Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(instance.context.getApplicationContext());
                        String AdId = adInfo.getId();
                        parameters.put("om_android_id", android_id);
                        parameters.put("om_google_aid", AdId);
                        if (tempParas != null) {
                            Iterator<String> i = (Iterator<String>)tempParas.keys();
                            while(i.hasNext()) {
                                String key = (String)i.next();
                                Object val = tempParas.get(key);
                                parameters.put(key, val);
                            }
                        }
                        track("om_alias", parameters);
                    } catch (Exception e) {
                        OmniataLog.e(TAG, e.toString());
                        try {
                            parameters.put("om_android_id", android_id);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        track("om_alias", parameters);
                    }
                }
            }).start();
    }

	/**
	 * enable push with push token
	 * @param registrationId project id
     */
	public static void enablePushNotifications(String registrationId) {
		JSONObject params = new JSONObject();
		try {
			params.put("om_registration_id", registrationId);
			track("om_gcm_enable", params);
		} catch (JSONException e) {
			OmniataLog.e(TAG, e.toString());
		}
	}

	/**
	 * Auto get the push registration id and send to Omniata.
	 * @param gcmSenderId, sender ID/ project number of push
     */
	public static void autoEnablePushNotifications(String gcmSenderId){
		OmniataUtils.gcmSenderId = gcmSenderId;
		Intent intent = new Intent(instance.context,OmniataRegistrationService.class);
		instance.context.startService(intent);
	}


	/**
	 * disalbe push notification of this user in omniata.
	 */
	public static void disablePushNotifications() {
		track("om_gcm_disable");
	}

    /**
     * Track the push notification of the push message.
     * @param data push data
     */
    public static void trackPushNotification(Bundle data) {
        JSONObject parameters = new JSONObject();
        try {
            for (String key : data.keySet()) {
                Object value = data.get(key);
                if (key.equals("notification")) {
                    for (String notifKey : ((Bundle) value).keySet()) {
                        Object notifValue = ((Bundle) value).get(notifKey);
                        parameters.put(notifKey, notifValue.toString());
                    }
                }
                else {
                    parameters.put(key, value.toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        track("om_push_received", parameters);
    }

    /**
     * Set the event track interval in millisecond, default interval is 1000 if this method is not called
     * @param milliSec interval in millisecond
     */
	public static void setTrackInterval(int milliSec){
		OmniataEventWorker.setTrackInterval(milliSec);
	}

	protected static JSONObject getAutomaticParameters() {
		JSONObject properties = new JSONObject();
		Locale locale = Locale.getDefault();
        PackageInfo pInfo = null;
        try {
            pInfo = instance.context.getPackageManager().getPackageInfo(instance.context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
		try {
            if (pInfo != null){
                properties.put("om_package_version_name", pInfo.versionName);
                properties.put("om_package_version_code", pInfo.versionCode);
            }
			// Standard automatic parameters
			properties.put("om_sdk_version", SDK_VERSION);
			properties.put("om_os_version", android.os.Build.VERSION.SDK_INT);
			properties.put("om_platform", "android");
			properties.put("om_device", android.os.Build.MODEL);
			
			// Android-specific parameters
			String android_id = Secure.getString(instance.context.getContentResolver(),
					Secure.ANDROID_ID);
			properties.put("om_android_id", android_id);
			properties.put("om_android_serial", android.os.Build.SERIAL);
			properties.put("om_android_device", android.os.Build.DEVICE);
			properties.put("om_android_hardware", android.os.Build.HARDWARE);
		
			if (locale != null) {
				properties.put("om_locale", locale);
			}
		} catch(Throwable e) {
			
		}
		return properties;
	}
	
	protected void _track(String eventType, JSONObject parameters) throws IllegalArgumentException {
		JSONObject event;
		
		OmniataUtils.assertValidEventType(eventType);
		
		try {			
			if (parameters != null) {
				event = new JSONObject(parameters.toString());
			} else {
				event = new JSONObject();
			}
			
			event.put("om_event_type", eventType);
			event.put("api_key", apiKey);
			event.put("uid", userID);
			event.put("om_creation_time", System.currentTimeMillis());
			
			while(true) {
				try {
					eventBuffer.put(event);
					break;
				} catch (InterruptedException e) {
					OmniataLog.e(TAG, e.toString());
				}
			}
		} catch (JSONException e) {
			OmniataLog.e(TAG, e.toString());
		} catch (Exception e){
			OmniataLog.e(TAG, e.toString());
		}
	}
	
	protected void _channel(final int channelId, final OmniataChannelResponseHandler handler) {
		Thread req = new Thread(new Runnable() {
			@Override
			public void run() {
				String uri = OmniataUtils.getChannelAPI(true) + "?api_key=" + apiKey + "&uid=" + userID + "&channel_id=" + channelId + "&require_user=1";
                while (!eventLog.isEmpty()){
                    try {
                        OmniataLog.i(TAG, "Waiting the events in event buffer to empty...");
                        Thread.sleep(500);                 //1000 milliseconds is one second.
                    } catch(InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
                OmniataLog.i(TAG, "Event buffer empty");
                try {
					URL url = new URL(uri);
                    int maximum_retry = 20;
                    while (maximum_retry>0){
                        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                        int httpResponse = connection.getResponseCode();
                        if (httpResponse == 404){
                            OmniataLog.i(TAG,"Omniata channel api returns 404, user doesn't exist, retry the api call");
                            Thread.sleep(500);
                            maximum_retry = maximum_retry - 1;
                            continue;
                        }
                        if (httpResponse >= 200 && httpResponse < 300) {
                            try {
                                String body = OmniataUtils.convertStreamToString(connection.getInputStream());
                                JSONObject jsonObj =  new JSONObject(body);
                                JSONArray content   = jsonObj.getJSONArray("content");
                                handler.onSuccess(channelId, content);
                                OmniataLog.i(TAG, "Omniata channel content: " + content.toString());
                                break;
                            } catch (Exception e) {
                                handler.onError(channelId, e);
                            }

                        } else {
                            handler.onError(channelId, new Exception("Error: Invalid http response code: " + httpResponse));
                        }
                    }
                    if ( maximum_retry == 0){
                        OmniataLog.i(TAG, "Exceed maximum retry of calling channel API, no user with "+ userID);
                    }
				} catch (final Exception e) {
					handler.onError(channelId, e);
				}
				
			}
		});
		
		req.start();
	}
	
	private void _setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}


	private void _setUserId(String userId) {
		this.userID = userId;
	}
	
	
	private Omniata(Context context, String apiKey, String userID, String org) {

	}
	
	
	private void _initialize(Context context, String apiKey, String userID, String org) throws IllegalArgumentException, IllegalStateException {
		OmniataLog.i(TAG, "Initializing Omniata with apiKey: " + apiKey + " and userID: " + userID);
		
		if (context == null) {
			throw new IllegalArgumentException("Context is null");
		}
		
		OmniataUtils.assertApiKeyValid(apiKey);
		OmniataUtils.assertUserIdValid(userID);
		OmniataUtils.setURL(org);

		this.apiKey   	  = apiKey;
		this.userID   	  = userID;
		
		if (this.context == null) {
			this.context = context;
		}
		
		if (eventBuffer == null) {
			eventBuffer = new LinkedBlockingQueue<JSONObject>();
		}
		
		if (eventLog == null) {
			eventLog = new PersistentBlockingQueue<JSONObject>(context, EVENT_LOG, JSONObject.class);
		}
		
		if (eventLogger == null) {
			eventLogger = new OmniataEventLogger(eventBuffer, eventLog);
		}
		
		if (eventWorker == null) {
			eventWorker = new OmniataEventWorker(context, eventLog);
		}
		
		eventLogger.start();
		eventWorker.start();
	}
	
	private Context 							context;
	private String 								apiKey;
	private String 								userID;	
	private BlockingQueue<JSONObject> 			eventBuffer;
	private PersistentBlockingQueue<JSONObject> eventLog;
	private OmniataEventLogger eventLogger;
	private OmniataEventWorker eventWorker;
}
