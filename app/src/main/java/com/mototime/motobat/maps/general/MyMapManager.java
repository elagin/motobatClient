package com.mototime.motobat.maps.general;

import android.content.Context;
import android.location.Location;

/**
 * Created by pavel on 08.07.15.
 */
public abstract class MyMapManager {
    public static final String OSM = "osm";
    public static final String GOOGLE = "google";
    public static final String YANDEX = "yandex";

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
}
