package com.mototime.motobat.content.police;

import com.mototime.motobat.content.police.PolicePoint;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class PolicePoints {

    private Map<Integer, PolicePoint> points;

    public PolicePoints() {
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
                PolicePoint point = new PolicePoint(json.getJSONObject(i));
                points.put(point.id, point);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public PolicePoint getPoint(int id) {
        return points.get(id);
    }

    public Map<Integer, PolicePoint> getMap() {
        return points;
    }
}
