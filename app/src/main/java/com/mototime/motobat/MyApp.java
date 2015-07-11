package com.mototime.motobat;

import android.app.Application;
import android.content.Context;
import android.location.Location;

import com.mototime.motobat.maps.general.MyMapManager;
import com.mototime.motobat.maps.google.MyGoogleMapManager;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MyApp extends Application {

    public static final SimpleDateFormat fullTimeFormat = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault());
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    public static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final MyApp instance;
    //    private Map<Integer, Point> pointsMap;
    public Points points;
    private MyPreferences prefs = null;
    private Session session = null;
    private MyMapManager map;
    private VK vk;

    public MyApp() {
        instance = this;

//        if (pointsMap == null) {
//            pointsMap = new HashMap<>();
//        }
        points = new Points(this);
        vk = new VK();
    }

    public MyPreferences getPreferences() {
        if (prefs == null)
            prefs = new MyPreferences(getApplicationContext());
        return prefs;
    }

    public Points getPoints() {
        return points;
    }

    public void createMap(Context context) {
        map = new MyGoogleMapManager(context);
        Location location = MyLocationManager.getLocation(this);
        map.jumpToPoint(location);
        map.placeUser(context);
    }

    public void updateMap(Context context) {
        map.placePoints(context);
        map.placeUser(context);
    }

//    public void addPoint(Point point) {
//        pointsMap.put(point.getId(), point);
//    }

//    public Map<Integer, Point> getPointsMap() {
//        return pointsMap;
//    }

    public Session getSession() {
        if (session == null)
            session = new Session(getApplicationContext());
        return session;
    }

    public VK vk() {
        return vk;
    }

}
