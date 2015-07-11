package com.mototime.motobat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.model.LatLng;

import java.net.MalformedURLException;
import java.net.URL;

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

    public String getVkToken() {
        return preferences.getString(sTokenKey, "");
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
}
