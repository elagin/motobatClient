package com.mototime.motobat;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mototime.motobat.utils.MyUtils;
import com.mototime.motobat.utils.NewID;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by pavel on 07.07.15.
 */
public class Point {

    private int id;
    private Date created;
    private int ownerID;
    private float lat;
    private float lng;
    private Location location;
    private int karma;

    private boolean error;

    MyApp myApp = null;

    public Point(JSONObject json, Context context ) {
        setError(false);
        myApp = (MyApp) context.getApplicationContext();
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
}