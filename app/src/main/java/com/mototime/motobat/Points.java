package com.mototime.motobat;

import com.mototime.motobat.network.AsyncTaskCompleteListener;
import com.mototime.motobat.network.GetPointListRequest;
import com.mototime.motobat.network.RequestErrors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Points {

    private Map<Integer, Point> points;
    private MyApp myApp;

    public Points(/*MyApp myApp*/) {
        //this.myApp = myApp;
        if (points == null) {
            points = new HashMap<>();
        }
        //requestPoints(myApp);
    }

    public void requestPoints(final MyApp myApp) {
        new GetPointListRequest(new AsyncTaskCompleteListener() {
            @Override
            public void onTaskComplete(JSONObject response) throws JSONException {
                if (!RequestErrors.isError(response)) {
                    updatePointsList(response.getJSONArray(RequestErrors.VALID_RESULT));
                    myApp.getMap().placePoints(myApp);
                }
            }
        }, myApp);
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

    /*
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
    */
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
