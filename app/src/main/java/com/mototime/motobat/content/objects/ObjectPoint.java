package com.mototime.motobat.content.objects;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rjhdby on 06.05.16.
 */
public class ObjectPoint {
    public final int        id;
    public final ObjectType type;
    public final float      lat;
    public final float      lon;
    public final String     text;

    public ObjectPoint(JSONObject data) throws JSONException {
        lat = (float) data.getDouble("lat");
        lon = (float) data.getDouble("lon");
        type = ObjectType.parse(data.getString("type"));
        text = data.getString("text");
        id = data.getInt("id");
    }

    public LatLng getLatLng() {
        return new LatLng(lat, lon);
    }

    public String getText() {
        return text;
    }
}
