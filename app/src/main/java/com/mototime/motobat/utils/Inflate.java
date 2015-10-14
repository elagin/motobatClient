package com.mototime.motobat.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

class Inflate {
    /**
     * Устанавливаем карту в контейнер.
     */
    @SuppressWarnings("SameParameterValue")
    public static void set(Context context, int parentId, int childId) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup parent = (ViewGroup) ((Activity) context).findViewById(parentId);
        View child = li.inflate(childId, parent, false);
        if (parent.getChildCount() > 0)
            parent.removeAllViews();
        parent.addView(child);
    }
}
