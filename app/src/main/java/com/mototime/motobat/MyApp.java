package com.mototime.motobat;

import android.app.Application;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.mototime.motobat.content.objects.ObjectsPoints;
import com.mototime.motobat.content.police.PolicePoints;
import com.mototime.motobat.maps.general.MyMapManager;
import com.mototime.motobat.maps.google.MyGoogleMapManager;

import java.io.IOException;
import java.util.List;

public class MyApp extends Application {
    public static final String CLOSE_GROUP_ID = "98656839"; // Приложение
    public static final String OPEN_GROUP_ID  = "68397238"; // mototimes

    private PolicePoints  policePoints;
    private ObjectsPoints objectsPoints;
    private MyPreferences prefs;
    private Session       session;
    private MyMapManager  map;
    private Geocoder      geocoder;

    {
        prefs = null;
        session = null;
    }

    public MyApp() {
    }

    public MyPreferences getPreferences() {
        if (prefs == null)
            prefs = new MyPreferences(getApplicationContext());
        return prefs;
    }

    public PolicePoints getPolicePoints() {
        if (policePoints == null)
            policePoints = new PolicePoints();
        return policePoints;
    }

    public ObjectsPoints getObjectsPoints() {
        if (objectsPoints == null)
            objectsPoints = new ObjectsPoints();
        return objectsPoints;
    }

    public void createMap(Context context) {
        map = new MyGoogleMapManager(context);
        Location location = MyLocationManager.getLocation(this);
        map.jumpToPoint(location);
        map.placeUser(context);
    }

    public void updateMap(Context context) {
        map.placePolicePoints(context);
        map.placeUser(context);
    }

    public Session getSession() {
        if (session == null)
            session = new Session(getApplicationContext());
        return session;
    }

    public String getAddres(Double lat, Double lon) {
        if (geocoder == null) {
            geocoder = new Geocoder(getApplicationContext());
        }
        StringBuilder res = new StringBuilder();
        try {
            List<Address> list = geocoder.getFromLocation(lat, lon, 1);
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
