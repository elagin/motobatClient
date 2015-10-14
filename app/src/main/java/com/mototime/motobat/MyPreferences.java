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

    private final static String savedlng = "savedlng";
    private final static String savedlat = "savedlat";

    private final static String login = "login";
    private final static String password = "password";

    private final static String VK_TOKEN_KEY = "vk_access_token";
    private final static String VK_USER_NAME = "vk_user_name";

    private static String serverURI = "server";

    private static SharedPreferences preferences;
    private static Context context;

    public MyPreferences(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        MyPreferences.context = context;
    }

    public LatLng getSavedLatLng() {
        double lat = (double) preferences.getFloat(savedlat, 55.752295f);
        double lng = (double) preferences.getFloat(savedlng, 37.622735f);
        return new LatLng(lat, lng);
    }

    public void saveLatLng(LatLng latlng) {
        preferences.edit().putFloat(savedlat, (float) latlng.latitude)
                .putFloat(savedlng, (float) latlng.longitude).commit();
    }

    public String getUserID() {
        return preferences.getString(login, "");
    }

    public void setUserID(String value) {
        preferences.edit().putString(login, value).commit();
    }

    public String getPassword() {
        return preferences.getString(password, "");
    }

    public void setPassword(String value) {
        preferences.edit().putString(password, value).commit();
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
        String DefaultURI = "http://forum.moto.msk.ru/mobile_times/mototimes_motobat_json.php";
        String URI = preferences.getString(serverURI, "");
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

    public void setVKUserName(String value) {
        preferences.edit().putString(VK_USER_NAME, value);
    }

    public String getVKUserName() {
        return preferences.getString(VK_USER_NAME, "");
    }
}
