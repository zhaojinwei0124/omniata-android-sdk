package com.omniata.android.sdk;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Scanner;

/* package */ class OmniataUtils {
	static String BASE_API_URL	   = "omniata.com";
	static String API 			   = "api.omniata.com";
	static String TEST_API 		   = "api-test.omniata.com";
	static String TEST_EVENT_API   = "";
	static String EVENT_API        = "";
	static String CHANNEL_API 	   = "";
	
	
	/**
	 * Set TEST_API by using the org defined by the customer.
	 * @param org
	 */
	/*package*/ static public void setURL(String org){
		EVENT_API = org + "." + "analyzer." + BASE_API_URL;
		CHANNEL_API = org + "." + "engager." + BASE_API_URL;
		
	}

    /**
     * Set the base URL of Event and Channel API.
     * @param org
     * @param url
     */
    static public void setURL(String org, String url){
        EVENT_API = url;
        CHANNEL_API = url;
    }

	/* package */ static String getProtocol(boolean useSSL) {
		return useSSL ? "https://" : "http://";
	}
	
	/* package */ static String getEventAPI(boolean useSSL, boolean debug) {
		if (debug) {
			return getProtocol(false) + TEST_EVENT_API + "/event";  // Test API is http only
		} else {
            if (EVENT_API.contains("http")){
                return EVENT_API + "/event";
            }else{
                return getProtocol(useSSL) + EVENT_API + "/event";
            }
		}
	}

	/* package */ static void assertApiKeyValid(String apiKey) throws IllegalArgumentException{
		if(apiKey == null || apiKey == "") {
			throw new IllegalArgumentException("API key is invalid");
		}
	}
	
	/* package */ static void assertUserIdValid(String userId) throws IllegalArgumentException{
		if(userId == null || userId == "") {
			throw new IllegalArgumentException("API key is invalid");
		}
	}
	
	/* package */ static void assertValidEventType(String eventType) throws IllegalArgumentException{
		if(eventType == null || eventType == "") {
			throw new IllegalArgumentException("Event Type is invalid");
		}
	}
	
	/* package */ static String getChannelAPI(boolean useSSL) {
        if (CHANNEL_API.contains("http")){
            return CHANNEL_API + "/channel";
        }else{
            return getProtocol(useSSL) + CHANNEL_API + "/channel";
        }
	}

	/* package */ static String convertStreamToString(InputStream is) {
		String result;
	    Scanner s = new Scanner(is);
	    s.useDelimiter("\\A");
	    result = s.hasNext() ? s.next() : "";
	    s.close();
	    return result;
	}
	
	/* package */ static boolean isConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		
		return activeNetwork != null && activeNetwork.isConnected();
	}
	
	/* package */ static String jsonToQueryString(JSONObject jsonObj) {
		StringBuilder sb = new StringBuilder();
		
		try {
			
			Iterator<String> i = (Iterator<String>)jsonObj.keys();
			while (i.hasNext()) {
				String key = (String)i.next();
				Object value;
				try {
					value = jsonObj.get(key);
				} catch (JSONException e) {
					value = "";
				}				
				sb.append(URLEncoder.encode(key, "UTF-8"));
				sb.append("=");
				sb.append(URLEncoder.encode(value.toString(), "UTF-8"));
				sb.append("&");
			}
		} catch (UnsupportedEncodingException e) {
			System.out.println(e);
		} catch ( Exception e){
		}

		return sb.substring(0, sb.length() - 1);
	}
	
	/* package */ static JSONObject mergeJSON(JSONObject obj1, JSONObject obj2) {
		JSONObject merged = new JSONObject();
		Iterator<String> keys1 = (Iterator<String>)obj1.keys();
		Iterator<String> keys2 = (Iterator<String>)obj2.keys();
		
		while(keys1.hasNext()) {
			String key = (String)keys1.next();
			try {
				if (!obj2.has(key)) {
					merged.put(key, obj1.get(key));
				}
			} catch(Exception e) {
			}
		}
		
		while(keys2.hasNext()) {
			String key = (String)keys2.next();
			try {
				merged.put(key, obj2.get(key));
			} catch(Exception e) {
			}
		}
		return merged;
	}
}
