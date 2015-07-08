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

/**
 * Created by pavel on 07.07.15.
 */
public class MyUtils {
    public static boolean isInteger(String s) {
        return isInteger(s, 10);
    }

    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    public static boolean isInteger(String s, int radix) {
        if (s.isEmpty())
            return false;
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1)
                    return false;
                else
                    continue;
            }
            if (Character.digit(s.charAt(i), radix) < 0)
                return false;
        }
        return true;
    }

    public static LatLng LocationToLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    public static Location LatLngToLocation(LatLng latlng) {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latlng.latitude);
        location.setLongitude(latlng.longitude);
        location.setAccuracy(0);
        return location;
    }

    public static List<String> getPhonesFromText(String in) {
        List<String> out = new ArrayList<>();
        in = in + ".";
        Matcher matcher = Pattern.compile("[7|8][ \\(-]?[\\d]{3}[ \\)-]?[\\d]{3}[ -]?[\\d]{2}[ -]?[\\d]{2}[\\D]").matcher(in);
        while (matcher.find()) {
            out.add("+7" + matcher.group().replaceAll("[^0-9]", "").substring(1));
        }
        return out;
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
            return "Только что";
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
            out.append(" назад");
        } else {
            out.append(String.valueOf(hours)).append("ч ");
            out.append(String.valueOf(minutes)).append("м");
        }
        return out.toString();
    }

    public static String getStringTime(Date date) {
        return getStringTime(date, false);
    }

    public static String getStringTime(Date date, boolean full) {
        String out;
        if (full) {
            out = Const.fullTimeFormat.format(date);
        } else {
            out = Const.timeFormat.format(date);
        }
        return out;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm      = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
