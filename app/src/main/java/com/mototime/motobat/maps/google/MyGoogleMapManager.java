package com.mototime.motobat.maps.google;

import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mototime.motobat.MyApp;
import com.mototime.motobat.Point;
import com.mototime.motobat.R;
import com.mototime.motobat.maps.general.MyMapManager;
import com.mototime.motobat.utils.Inflate;
import com.mototime.motobat.utils.MyUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MyGoogleMapManager extends MyMapManager {
    private static GoogleMap map;
    private static Map<String, Integer> points;

    private MyApp myApp = null;

    public MyGoogleMapManager(final Context context) {

        myApp = (MyApp) context.getApplicationContext();
        setName(MyMapManager.GOOGLE);

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
                marker.showInfoWindow();
                return true;
            }
        });
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
//                String uri    = "geo:" + latLng.latitude + "," + latLng.longitude;
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//                context.startActivity(intent);
            }
        });
    }

    private static void init() {
        map.clear();
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
    }

    @SuppressWarnings("UnusedParameters")
    @Override
    public void placeUser(Context context) {
        return;
    }

    @Override
    public void jumpToPoint(Location location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(MyUtils.LocationToLatLng(location), 16));
    }

    @SuppressWarnings("UnusedParameters")
    @Override
    public void placePoints(Context context) {
        if (points == null) {
            points = new HashMap<>();
        }
        init();
        points.clear();

        for (int id : myApp.getPoints().getMap().keySet()) {
            final Point point = myApp.getPoints().getPoint(id);
            if (point.isInvisible()) continue;
            //String title = point.getAddress();
            String title = "";
//            if (!point.getMedText().equals("")) {
//                title += ", " + point.getMedText();
//            }
            title += ", " + MyUtils.getIntervalFromNowInText(point.getCreated()) + " назад";

            float alpha;
            int minutes = (int) (((new Date()).getTime() - point.getCreated().getTime()) / 60000 - point.getKarma());
            alpha = Math.max((float) (1 - 0.003 * Math.max(minutes, 0)), 0.2f);
            Marker marker = map.addMarker(new MarkerOptions().position(MyUtils.LocationToLatLng(point.getLocation())).title(title)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.police_stick)).alpha(alpha));
            points.put(marker.getId(), id);
        }
    }

    @Override
    public void zoom(int zoom) {
        map.animateCamera(CameraUpdateFactory.zoomTo(zoom));
    }
}
