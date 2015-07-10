package com.mototime.motobat.maps.google;

import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mototime.motobat.MyApp;
import com.mototime.motobat.MyLocationManager;
import com.mototime.motobat.Point;
import com.mototime.motobat.R;
import com.mototime.motobat.maps.general.MyMapManager;
import com.mototime.motobat.utils.Inflate;
import com.mototime.motobat.utils.MyUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pavel on 08.07.15.
 */
public class MyGoogleMapManager extends MyMapManager {
    private static GoogleMap map;
    private static Marker user;
    private static Map<String, Integer> accidents;
    private static String selected;

    private MyApp myApp = null;

    public MyGoogleMapManager(final Context context) {

        myApp = (MyApp) context.getApplicationContext();
        setName(MyMapManager.GOOGLE);
        selected = "";

        Inflate.set(context, R.id.map_container, R.layout.google_maps_view);

        android.support.v4.app.FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        final SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.google_map);

/* Возможно поможет, хотя и костыль */
        for (int i = 0; i < 5; i++) {
            map = mapFragment.getMap();
            if (map == null) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
        init();
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                String id = marker.getId();
//                if (selected.equals(id) && accidents.containsKey(id)) {
//                    AccidentsGeneral.toDetails(context, accidents.get(selected));
//                } else {
                marker.showInfoWindow();
                selected = id;
//                }
                return true;
            }
        });
//        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
//            @Override
//            public void onMapLongClick(LatLng latLng) {
//                String uri    = "geo:" + latLng.latitude + "," + latLng.longitude;
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//                context.startActivity(intent);
//            }
//        });
    }

    @SuppressWarnings("UnusedParameters")
    public void placeUser(Context context) {
        if (user != null) {
            user.remove();
        }

        Location location = MyLocationManager.getLocation(context);
        //if(location != null) {
        user = map.addMarker(new MarkerOptions().position(MyUtils.LocationToLatLng(location)).title("Вы")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.user)));
        //} else {
        //TODO Отобразить сообщение?
        //Toast.makeText(this, Startup.context.getString(R.string.position_not_available), Toast.LENGTH_LONG).show();
        //}
    }

    public void jumpToPoint(Location location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(MyUtils.LocationToLatLng(location), 16));
    }

    @SuppressWarnings("UnusedParameters")
    public void placePoints(Context context) {
        if (accidents == null) {
            accidents = new HashMap<>();
        }
        init();
        accidents.clear();

        for (int id : myApp.getPoints().getMap().keySet()) {
            Point point = myApp.getPoints().getPoint(id);
            if (point.isInvisible()) continue;
            String title = point.getAddress();
//            if (!point.getMedText().equals("")) {
//                title += ", " + point.getMedText();
//            }
            title += ", " + MyUtils.getIntervalFromNowInText(point.getCreated()) + " назад";

            float alpha;
            int age = (int) (((new Date()).getTime() - point.getCreated().getTime()) / 3600000);
            if (age < 2) {
                alpha = 1.0f;
            } else if (age < 6) {
                alpha = 0.5f;
            } else {
                alpha = 0.2f;
            }
            Marker marker = map.addMarker(new MarkerOptions().position(MyUtils.LocationToLatLng(point.getLocation())).title(title)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.point)).alpha(alpha));
            accidents.put(marker.getId(), id);
        }
    }

    private static void init() {
        map.clear();
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
    }

    public void zoom(int zoom) {
        map.animateCamera(CameraUpdateFactory.zoomTo(zoom));
    }
}
