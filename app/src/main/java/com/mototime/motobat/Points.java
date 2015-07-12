package com.mototime.motobat;

import android.content.Context;

import com.mototime.motobat.network.AsyncTaskCompleteListener;
import com.mototime.motobat.network.GetPointListRequest;
import com.mototime.motobat.network.RequestErrors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Points {

    private final Context context;
    private Map<Integer, Point> points;

    public Points(Context context) {
        if (points == null) {
            points = new HashMap<>();
        }
        this.context = context;
        requestPoints(context);
    }

    public void requestPoints(final Context context) {
        new GetPointListRequest(new AsyncTaskCompleteListener() {
            @Override
            public void onTaskComplete(JSONObject response) throws JSONException {
                if (!RequestErrors.isError(response)) {
                    updatePointsList(response.getJSONArray(RequestErrors.VALID_RESULT));
                    ((MyApp) context).getMap().placePoints(context);
                }
            }
        }, context);
    }

    private void updatePointsList(JSONArray json) {
        if (points == null) {
            points = new HashMap<>();
        }
        points.clear();
        for (int i = 0; i < json.length(); i++) {
            try {
                Point point = new Point(json.getJSONObject(i), context);
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
