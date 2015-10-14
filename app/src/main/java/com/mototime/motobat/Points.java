package com.mototime.motobat;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class Points {

    private Map<Integer, Point> points;

    public Points() {
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
                Point point = new Point(json.getJSONObject(i));
                if (!point.isError()) {
                    points.put(point.getId(), point);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public Point getPoint(int id) {
        return points.get(id);
    }

    public Map<Integer, Point> getMap() {
        return points;
    }
/*
    public enum Sort {
        FORWARD, BACKWARD
    }
    */
}
