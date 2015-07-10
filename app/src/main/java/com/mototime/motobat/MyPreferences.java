package com.mototime.motobat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by pavel on 08.07.15.
 */

@SuppressLint("CommitPrefEdits")
public class MyPreferences {

    private final static String savedlng = "savedlng";
    private final static String savedlat = "savedlat";

    private final static String login = "login";
    private final static String password = "password";

    private static String sTokenKey = "VK_ACCESS_TOKEN";

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

    public String getLogin() {
        return preferences.getString(login, "");
    }

    public void setLogin(String value) {
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

    public String getVkToken() { return preferences.getString(sTokenKey, "");}
}
