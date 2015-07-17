package com.mototime.motobat.maps.general;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by pavel on 08.07.15.
 */
public abstract class MyMapManager {
    public static final String OSM = "osm";
    public static final String GOOGLE = "google";
    public static final String YANDEX = "yandex";

    public static final int STANDART_ZOOM = 12;

    private String name;

    public abstract void placeUser(Context context);

    public abstract void jumpToPoint(Location location);

    @SuppressWarnings("SameParameterValue")
    public abstract void zoom(int zoom);

    public abstract void placePoints(Context context);

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public abstract LatLng getCenter();

    public abstract void goToLatLng(LatLng latLng);

    public abstract void goToUser();
}
