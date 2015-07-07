package com.mototime.motobat;

import android.app.Application;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by pavel on 07.07.15.
 */
public class MyApp extends Application {

    public static final SimpleDateFormat fullTimeFormat = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault());
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    public static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

//    private Map<Integer, Point> pointsMap;
    public Points points;

    private final MyApp instance;
    public MyApp() {
        instance = this;

//        if (pointsMap == null) {
//            pointsMap = new HashMap<>();
//        }
        points = new Points(this);
    }

//    public void addPoint(Point point) {
//        pointsMap.put(point.getId(), point);
//    }

//    public Map<Integer, Point> getPointsMap() {
//        return pointsMap;
//    }

}
