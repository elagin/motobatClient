package com.mototime.motobat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TableRow;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by pavel on 08.07.15.
 */
public class Const {
    public static final SimpleDateFormat fullTimeFormat = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault());
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    public static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    public static final TableRow.LayoutParams trlp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
    public static final ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    public final static int EQUATOR = 20038;

    // Defines a custom Intent action
    public static final String BROADCAST_ACTION = "com.mototime.motobat.BROADCAST";

    // Defines the key for the status "extra" in an Intent
    public static final String EXTENDED_DATA_STATUS = "com.mototime.motobat.STATUS";

    // Defines the key for the log "extra" in an Intent
    public static final String EXTENDED_STATUS_LOG = "com.mototime.motobat.LOG";

    @SuppressWarnings("deprecation")
    public Const() {
    }

    public static float getDP(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static int getHeight(Context context) {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        return display.getHeight();
    }

    public static int getWidth(Context context) {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        return display.getWidth();
    }

    public static int getDefaultBGColor(Context context) {
        TypedArray ta = context.obtainStyledAttributes(new int[]{android.R.attr.colorBackground, android.R.attr.textColorPrimary});
        int color = ta.getIndex(0);
        ta.recycle();
        return color;
    }

    public static int getDefaultColor(Context context) {
        TypedArray ta = context.obtainStyledAttributes(new int[]{android.R.attr.colorBackground, android.R.attr.textColorPrimary});
        int color = ta.getIndex(1);
        ta.recycle();
        return color;
    }

    public static LayoutInflater getLayoutInflater(Context context) {
        Activity act = (Activity) context;
        return (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
}
