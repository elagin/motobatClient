package com.mototime.motobat.content.police;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class PolicePoint {

    public final PointType       type;
    public final PoliceAlignment alignment;
    public final int             id;
    public final Date            created;
    public final String          text;

    private int   ownerID;
    private float lat;
    private float lng;
    private int   karma;

    public PolicePoint(JSONObject data) throws JSONException {
        lat = (float) data.getDouble("lat");
        lng = (float) data.getDouble("lng");
        id = data.getInt("id");
        created = new Date(Long.parseLong(data.getString("created")) * 1000);
        ownerID = data.getInt("owner");
        karma = data.getInt("karma");
        text = data.getString("text");
//TODO добавить на сервере параметр type
        type = PointType.parse(data.getInt("transport"));
        alignment = PoliceAlignment.parse(data.getInt("alignment"));
    }

    public boolean isInvisible() {
        return false;
    }

    public int getKarma() {
        return karma;
    }

    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }

    public String getText() {
        return text;
    }
}