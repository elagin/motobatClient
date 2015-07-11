package com.mototime.motobat;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pavel on 07.07.15.
 */
public class Points {

    public enum Sort {
        FORWARD, BACKWARD
    }

    private Map<Integer, Point> points;
    public final String error;
    private final Context context;

    public Points(Context context) {
        error = "ok";
        if (points == null) {
            points = new HashMap<>();
        }
        this.context = context;
        //prefs = ((MyApp) context.getApplicationContext()).getPreferences();
    }

    public Integer[] sort(Map<Integer, Point> in, Sort FLAG) {
        List<Integer> list = new ArrayList<>();
        list.addAll(in.keySet());
        Integer[] out = new Integer[list.size()];
        switch (FLAG) {
            case FORWARD:
                list.toArray(out);
                Arrays.sort(out);
                break;
            case BACKWARD:
                list.toArray(out);
                Arrays.sort(out, Collections.reverseOrder());
                break;
            default:
                list.toArray(out);
        }
        return out;
    }

    public Map<Integer, Point> getVisibleAccidents() {
        Map<Integer, Point> out = new HashMap<>();
        for (int i : points.keySet()) {
            Point point = points.get(i);
            if (point.isInvisible()) continue;
            //if (point.getHoursAgo() >= prefs.getHoursAgo()) continue;
            out.put(i, point);
        }
        return out;
    }

    public Point getPoint(int id) {
        return points.get(id);
    }

    private void addPoint(Point point) {
        points.put(point.getId(), point);
    }

    public Map<Integer, Point> getMap() {
        return points;
    }

    public void parseJSON(JSONArray json) throws JSONException {
        if (((JSONObject) json.get(0)).has("isError")) return;
        points.clear();
        for (int i = 0; i < json.length(); i++) {
            JSONObject item = json.getJSONObject(i);
            try {
                Point current = new Point(item, context);
                addPoint(current);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
