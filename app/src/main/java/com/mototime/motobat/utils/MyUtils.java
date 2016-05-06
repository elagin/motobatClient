package com.mototime.motobat.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyUtils {

    public static LatLng LocationToLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    public static String getIntervalFromNowInText(Date date) {
        return getIntervalFromNowInText(date, false);
    }

    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    public static String getIntervalFromNowInText(Date date, boolean full) {
        StringBuilder out = new StringBuilder();
        Date now = new Date();
        int minutes = (int) (now.getTime() - date.getTime()) / (60000);
        if (minutes <= 0) {
            return "только что";
        }
        int hours = minutes / 60;
        minutes -= hours * 60;
        int min = minutes % 10;
        if (full) {
            if (hours == 1 || hours == 21) {
                out.append(String.valueOf(hours)).append(" час ");
            } else if (hours == 2 || hours == 3 || hours == 4 || hours == 22 || hours == 23 || hours == 24) {
                out.append(String.valueOf(hours)).append(" часа ");
            } else if (hours > 4 && hours < 21) {
                out.append(String.valueOf(hours)).append(" часов ");
            }
            if (minutes > 10 && minutes < 20) {
                out.append(String.valueOf(minutes)).append(" минут");
            } else if (min == 1) {
                out.append(String.valueOf(minutes)).append(" минуту");
            } else if (min > 1 && min < 5) {
                out.append(String.valueOf(minutes)).append(" минуты");
            } else if (min > 4 || min == 0) {
                out.append(String.valueOf(minutes)).append(" минут");
            }
        } else {
            if (hours > 0)
                out.append(String.valueOf(hours)).append("ч ");
            out.append(String.valueOf(minutes)).append("м");
        }
        out.append(" назад");
        return out.toString();
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
