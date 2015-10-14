package com.mototime.motobat;

import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.Date;
import java.util.Random;

public class Point {

    public final static int NORMAL_POLICE = 1;
    public final static int GOOD_POLICE = 2;
    public final static int EVIL_POLICE = 3;

    public final static int GS = 1;
    public final static int RT = 2;
    public final static int CAR = 3;

    private int id;
    private Date created;
    private int ownerID;
    private float lat;
    private float lng;
    private Location location;
    private int karma;
    private boolean error;
    private int alignment;
    private int transport;
    private String text;
    private Random rnd = new Random();

    public Point(JSONObject json) {
        setError(false);
        createPoint(json);
    }

    public int getId() {
        return id;
    }

    public Date getCreated() {
        return created;
    }

    private void createPoint(JSONObject data) {
        //{"owner":244452742,"id":7,"karma":0,"lng":37.631439208984,"created":1436646776,"lat":55.756504058838}]
        try {
            location = new Location(LocationManager.NETWORK_PROVIDER);
            lat = (float) data.getDouble("lat");
            lng = (float) data.getDouble("lng");
            location.setLatitude(lat);
            location.setLongitude(lng);
            location.setAccuracy(0);
            id = data.getInt("id");
            Long date = Long.parseLong(data.getString("created"));
            created = new Date(date * 1000);
            ownerID = data.getInt("owner");
            karma = data.getInt("karma");
            text = data.getString("text");
//TODO прибрать в релизе
            if (data.has("transport"))
                transport = data.getInt("transport");
            else
                transport = rnd.nextInt(2) + 1;
            if (data.has("alignment"))
                alignment = data.getInt("alignment");
            else
                alignment = rnd.nextInt(2) + 1;
//TODO прибрать в релизе
        } catch (Exception e) {
            e.printStackTrace();
            setError(true);
        }
    }

    public boolean isInvisible() {
//        Double dist = getDistanceFromUser();
//        return isHidden() && !Role.isModerator() || (dist != null && ((dist >= prefs.getVisibleDistance() * 1000) || prefs.toHideAccType(type)));
        return false;
    }

    public int getHoursAgo() {
        return (int) ((new Date()).getTime() - created.getTime()) / 3600000;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public int getKarma() {
        return karma;
    }

    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }

    public String getTransportString() {
        switch (transport) {
            case Point.GS:
                return "Гусь";
            case Point.RT:
                return "RT";
            case Point.CAR:
                return "Коробка";
            default:
                return "Не известно";
        }
    }

    public int getTransport() {
        return transport;
    }

    public int getAlignment() {
        return alignment;
    }

    public String getAlignmentString() {
        if (alignment == Point.EVIL_POLICE)
            return "злой";
        if (alignment == Point.NORMAL_POLICE)
            return "нейтральный";
        if (alignment == Point.GOOD_POLICE)
            return "добрый";
        return "не известно";
    }

    public String getText() {
        return text;
    }
}