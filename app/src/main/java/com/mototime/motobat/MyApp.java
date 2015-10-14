package com.mototime.motobat;

import android.app.Application;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.mototime.motobat.maps.general.MyMapManager;
import com.mototime.motobat.maps.google.MyGoogleMapManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyApp extends Application {

    public static final SimpleDateFormat fullTimeFormat = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault());
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    public static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public static final String CLOSE_GROUP_ID = "98656839"; // Приложение
    public static final String OPEN_GROUP_ID = "68397238"; // mototimes

    private final MyApp instance;
    //    private Map<Integer, Point> pointsMap;
    public Points points;
    private MyPreferences prefs = null;
    private Session session = null;
    private MyMapManager map;
    private VK vk;
    private Geocoder geocoder;

    public MyApp() {
        instance = this;
        vk = new VK();
    }

    public MyPreferences getPreferences() {
        if (prefs == null)
            prefs = new MyPreferences(getApplicationContext());
        return prefs;
    }

    public Points getPoints() {
        if(points == null)
            points = new Points();
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
            session = new Session(getApplicationContext(), this);
        return session;
    }

    public VK vk() {
        return vk;
    }

    public String getAddres(Double lat, Double lon) {
        if (geocoder == null) {
            geocoder = new Geocoder(getApplicationContext());
        }
        StringBuilder res = new StringBuilder();
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocation(lat, lon, 1);
            Address addr = list.get(0);


//            for(int i = 0; i < addr.getMaxAddressLineIndex(); i++ ) {
//                if (res.length() > 0)
//                    res.append(" ");
//                res.append(addr.getAddressLine(i));
//            }

            String locality = addr.getLocality();
            if (locality == null)
                locality = addr.getAdminArea();
            if (locality == null && addr.getMaxAddressLineIndex() > 0)
                locality = addr.getAddressLine(0);

            String thoroughfare = addr.getThoroughfare();
            if (thoroughfare == null)
                thoroughfare = addr.getSubAdminArea();

            String featureName = addr.getFeatureName();

            if (locality != null)
                res.append(locality);
            if (thoroughfare != null) {
                if (res.length() > 0)
                    res.append(" ");
                res.append(thoroughfare);
            }
            if (featureName != null)
                if (res.length() > 0)
                    res.append(" ");
            res.append(featureName);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return res.toString();
    }

    public MyMapManager getMap() {
        return map;
    }
}
