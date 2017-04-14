package com.mototime.motobat.maps.google;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mototime.motobat.MyApp;
import com.mototime.motobat.R;
import com.mototime.motobat.activity.MainActivity;
import com.mototime.motobat.content.objects.ObjectPoint;
import com.mototime.motobat.content.police.PolicePoint;
import com.mototime.motobat.maps.general.MyMapManager;
import com.mototime.motobat.utils.MyUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyGoogleMapManager extends MyMapManager {
    private static GoogleMap            map;
    private static Map<String, Integer> points;
    private static Map<String, Integer> objects;
    private static Context              context;
    private final  MyApp                myApp;

    public MyGoogleMapManager(final Context context, final MainActivity.MapReady listener) {
        this.context = context;
        myApp = (MyApp) context.getApplicationContext();
        setName(MyMapManager.GOOGLE);

        //Inflate.set(context, R.id.map_container, R.layout.google_maps_view);
        //Inflate.set(context, R.id.map, R.layout.root);

        android.support.v4.app.FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        //final SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.google_map);
        final SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.google_map);

/* Возможно поможет, хотя и костыль */
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
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
                listener.ready(map);
            }
        });
    }

    private void init() {
        map.clear();
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                //TODO Ёбанейший стыдище
                String text;
                String created;
                String description;
                if (points.containsKey(marker.getId())) {
                    PolicePoint point = myApp.getPolicePoints().getPoint(points.get(marker.getId()));
                    text = point.alignment.text + " " + point.type.text;
                    created = MyUtils.getIntervalFromNowInText(point.created);
                    description = point.text;
                } else {
                    text = myApp.getObjectsPoints().getPoint(objects.get(marker.getId())).type.text;
                    created = "";
                    description = myApp.getObjectsPoints().getPoint(objects.get(marker.getId())).text;
                }
                View view = ((Activity) context).getLayoutInflater().inflate(R.layout.info_window, null);
                ((TextView) view.findViewById(R.id.eventClass)).setText(text);
                ((TextView) view.findViewById(R.id.time)).setText(created);
                ((TextView) view.findViewById(R.id.description)).setText(description);
                return view;
            }
        });
    }

    @Override
    public LatLng getCenter() {
        return map.getCameraPosition().target;
    }

    @Override
    public void goToLatLng(LatLng latLng) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, STANDART_ZOOM));
    }

    @Override
    public void goToUser() {
        Location location = map.getMyLocation();
        if (location == null) {
            map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(MyUtils.LocationToLatLng(location), STANDART_ZOOM));
                    map.setOnMyLocationChangeListener(null);
                }
            });
        } else {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(MyUtils.LocationToLatLng(location), STANDART_ZOOM));
        }
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
    public void placePolicePoints(Context context) {
        if (points == null) {
            points = new HashMap<>();
        }
        init();
        points.clear();

        for (int id : myApp.getPolicePoints().getMap().keySet()) {
            final PolicePoint point = myApp.getPolicePoints().getPoint(id);
            if (point.isInvisible()) continue;
            //String title = point.getAddress();
            StringBuilder title = new StringBuilder()
                    .append(point.type.text)
                    .append(", ")
                    .append(point.alignment.text)
                    .append(", ")
                    .append(MyUtils.getIntervalFromNowInText(point.created))
                    .append(System.getProperty("line.separator"))
                    .append(point.text);

            float alpha;
            int   minutes = (int) (((new Date()).getTime() - point.created.getTime()) / 60000 - point.getKarma());
            alpha = Math.max((float) (1 - 0.003 * Math.max(minutes, 0)), 0.2f);
//            Log.d("POINTS", "minutes: " + String.valueOf(minutes) + " alpha: " + String.valueOf(alpha));
            Marker marker = map.addMarker(new MarkerOptions().position(point.getLatLng()).anchor(0.5f, 1f).title(title.toString())
                                                             .icon(BitmapDescriptorFactory.fromResource(point.type.icon)).alpha(alpha));
            points.put(marker.getId(), id);
        }
    }

    @SuppressWarnings("UnusedParameters")
    @Override
    public void placeObjectsPoints(Context context) {
        //TODO ёбаный стыд
        if (objects == null) {
            objects = new HashMap<>();
        }
        //init();
        objects.clear();

        for (int id : myApp.getObjectsPoints().getMap().keySet()) {
            final ObjectPoint objectPoint = myApp.getObjectsPoints().getPoint(id);
            Marker marker = map.addMarker(new MarkerOptions().position(objectPoint.getLatLng()).anchor(0.5f, 0.5f).title(objectPoint.text)
                                                             .icon(BitmapDescriptorFactory.fromResource(objectPoint.type.icon)).alpha(1));
            objects.put(marker.getId(), id);
        }
    }

    @Override
    public void zoom(int zoom) {
        map.animateCamera(CameraUpdateFactory.zoomTo(zoom));
    }
}
