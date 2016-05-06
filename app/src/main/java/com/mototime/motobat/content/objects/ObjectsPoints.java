package com.mototime.motobat.content.objects;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class ObjectsPoints {

    private Map<Integer, ObjectPoint> points;

    public ObjectsPoints() {
        if (points == null) {
            points = new HashMap<>();
        }
    }

    public int getSize() {
        return points.size();
    }

    public void updatePointsList(JSONArray json) {
        if (points == null) {
            points = new HashMap<>();
        }
        points.clear();
        for (int i = 0; i < json.length(); i++) {
            try {
                ObjectPoint point = new ObjectPoint(json.getJSONObject(i));
                points.put(point.id, point);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public ObjectPoint getPoint(int id) {
        return points.get(id);
    }

    public Map<Integer, ObjectPoint> getMap() {
        return points;
    }
}
