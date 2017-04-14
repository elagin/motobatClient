package com.mototime.motobat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.model.LatLng;

import java.net.MalformedURLException;
import java.net.URL;

@SuppressLint("CommitPrefEdits")
public class MyPreferences {
    private final static String savedLng = "savedlng";
    private final static String savedLat = "savedlat";

    private final static String login    = "login";
    private final static String password = "password";

    private final static String VK_TOKEN_KEY = "vk_access_token";
    private final static String VK_USER_NAME = "vk_user_name";

    private static final String serverURI = "server";

    private static SharedPreferences preferences;

    public MyPreferences(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public LatLng getSavedLatLng() {
        double lat = (double) preferences.getFloat(savedLat, 55.752295f);
        double lng = (double) preferences.getFloat(savedLng, 37.622735f);
        return new LatLng(lat, lng);
    }

    public void saveLatLng(LatLng latlng) {
        preferences.edit().putFloat(savedLat, (float) latlng.latitude)
                   .putFloat(savedLng, (float) latlng.longitude).commit();
    }

    public String getUserID() {
        return preferences.getString(login, "");
    }

    public void setUserID(String value) {
        preferences.edit().putString(login, value).commit();
    }

    public void resetAuth() {
        preferences.edit().remove(login).remove(password).commit();
    }

    public String getVkToken() {
        return preferences.getString(VK_TOKEN_KEY, "");
    }

    public void setVkToken(String value) {
        preferences.edit().putString(VK_TOKEN_KEY, value).commit();
    }

    public URL getServerURI() {
        String DefaultURI = "http://motodtp.info/mototimes/request.php";
        String URI        = preferences.getString(serverURI, "");
        if (URI.equals("")) {
            setServerURI(DefaultURI);
            URI = DefaultURI;
        }
        try {
            return new URL(URI);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setServerURI(String URI) {
        preferences.edit().putString(serverURI, URI);
    }
}
